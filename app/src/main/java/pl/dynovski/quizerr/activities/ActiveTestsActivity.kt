package pl.dynovski.quizerr.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.ActiveTestsAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.firebaseObjects.Test
import pl.dynovski.quizerr.firebaseObjects.User
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.CoursesViewModel
import pl.dynovski.quizerr.viewmodels.TestsViewModel
import pl.dynovski.quizerr.viewmodels.UsersViewModel
import java.util.*

class ActiveTestsActivity: AppCompatActivity() {

    private val TAG = "ACTIVE_TESTS"

    private lateinit var adapter: ActiveTestsAdapter
    private lateinit var testsViewModel: TestsViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var headerTextView: TextView
    private lateinit var binding: ActivityRecyclerViewBinding
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.firestore
        testsViewModel = ViewModelProvider(this)[TestsViewModel::class.java]

        database.collection("Users")
            .document(LoggedUser.get().userId)
            .get()
            .addOnSuccessListener { userDocument ->
                val user = userDocument.toObject(User::class.java) ?: return@addOnSuccessListener
                testsViewModel.ongoingTests(user.subscribedCoursesIds).observe(this) { querySnapshot ->
                    val activeTestsDocuments = querySnapshot.documents.filter { !user.completedTestsIds.contains(it.id) }
                    val activeTests = activeTestsDocuments.map { it.toObject(Test::class.java)!! }.toTypedArray()
                    adapter.setActiveTests(activeTests)
                }
            }

        adapter = ActiveTestsAdapter()
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_active_tests)

    }
}