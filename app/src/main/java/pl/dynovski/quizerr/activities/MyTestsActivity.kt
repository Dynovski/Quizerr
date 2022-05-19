package pl.dynovski.quizerr.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.MyTestsAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.viewmodels.TestsViewModel

class MyTestsActivity: AppCompatActivity() {

    private lateinit var testsViewModel: TestsViewModel
    private lateinit var adapter: MyTestsAdapter
    private lateinit var binding: ActivityRecyclerViewBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var headerTextView: TextView
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        database = FirebaseFirestore.getInstance()

        setContentView(binding.root)

        adapter = MyTestsAdapter()
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_my_tests)

        testsViewModel = ViewModelProvider(this)[TestsViewModel::class.java]
        testsViewModel.currentUserTests.observe(this) { queryDocumentSnapshots ->
            adapter.setTests(queryDocumentSnapshots)
        }
    }
}