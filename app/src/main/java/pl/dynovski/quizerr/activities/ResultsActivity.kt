package pl.dynovski.quizerr.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.ResultSectionAdapter
import pl.dynovski.quizerr.adapters.ResultsAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.firebaseObjects.TestResult
import pl.dynovski.quizerr.helpers.ResultSection
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.TestResultsViewModel

class ResultsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerViewBinding
    private lateinit var database: FirebaseFirestore

    private lateinit var adapter: ResultSectionAdapter
    private lateinit var testResultsViewModel: TestResultsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.firestore

        adapter = ResultSectionAdapter()
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_results)

        testResultsViewModel = ViewModelProvider(this)[TestResultsViewModel::class.java]
        testResultsViewModel.getTestResults(LoggedUser.get().userId).observe(this) {
            val results = it.documents.map { it.toObject(TestResult::class.java)!! }
            val resultsByCourseId = results.groupBy { it.courseId }

            database.collection("Courses")
                .whereIn(FieldPath.documentId(), resultsByCourseId.keys.toList())
                .get()
                .addOnSuccessListener {
                    val documents = it.documents
                    val sections: MutableList<ResultSection> = mutableListOf()
                    for (document in documents) {
                        val documentId = document.id
                        val courseName = document.getString("name")!!
                        val section = ResultSection(
                            courseName,
                            resultsByCourseId[documentId]!!.toTypedArray()
                        )
                        sections.add(section)
                    }
                    adapter.setSections(sections.toTypedArray())
                }
        }
    }
}