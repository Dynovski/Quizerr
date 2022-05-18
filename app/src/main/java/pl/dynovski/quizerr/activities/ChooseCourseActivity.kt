package pl.dynovski.quizerr.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.ChooseCourseAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.viewmodels.CoursesViewModel

class ChooseCourseActivity: AppCompatActivity() {

    private lateinit var coursesViewModel: CoursesViewModel
    private lateinit var adapter: ChooseCourseAdapter
    private lateinit var binding: ActivityRecyclerViewBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var headerTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)

        setContentView(binding.root)

        adapter = ChooseCourseAdapter()
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_choose_course)

        coursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
        coursesViewModel.currentUserCourses.observe(this) { queryDocumentSnapshots ->
            adapter.setCourses(queryDocumentSnapshots)
            if (queryDocumentSnapshots.isEmpty())
                Toast.makeText(
                    this,
                    R.string.toast_no_courses_available,
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    fun startTestCreationFor(courseId: String) {
        intent = Intent(this, CreateTestActivity::class.java)
        intent.putExtra("courseId", courseId)
        startActivity(intent)
    }
}