package pl.dynovski.quizerr.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.AllCoursesAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.viewmodels.CoursesViewModel

class CoursesActivity: AppCompatActivity() {

    private lateinit var coursesViewModel: CoursesViewModel
    private lateinit var adapter: AllCoursesAdapter
    private lateinit var binding: ActivityRecyclerViewBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var headerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AllCoursesAdapter()
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_all_courses)

        coursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
        coursesViewModel.allCourses.observe(this) { queryDocumentSnapshots ->
            adapter.setAllCourses(queryDocumentSnapshots)
        }
        coursesViewModel.subscribedCourses.observe(this) { queryDocumentSnapshots ->
            adapter.setSubscribedCourses(queryDocumentSnapshots)
        }
    }
}