package pl.dynovski.quizerr.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.ResultsAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.viewmodels.CompletedTestsViewModel

class ResultsActivity: AppCompatActivity() {

    private lateinit var binding: ActivityRecyclerViewBinding

    private lateinit var adapter: ResultsAdapter
    private lateinit var completedTestsViewModel: CompletedTestsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ResultsAdapter()
        val recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        val headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_results)

        completedTestsViewModel = ViewModelProvider(this)[CompletedTestsViewModel::class.java]
        completedTestsViewModel.completedTests.observe(this) { queryDocumentSnapshots ->
            adapter.setCompletedTests(queryDocumentSnapshots)
        }
    }
}