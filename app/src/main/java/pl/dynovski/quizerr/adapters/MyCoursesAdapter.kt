package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.activities.MyCoursesActivity
import pl.dynovski.quizerr.databinding.MyCourseItemBinding
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.fragments.CourseDetailsDialogFragment

class MyCoursesAdapter: RecyclerView.Adapter<MyCoursesAdapter.ViewHolder>() {

    private var myCourses: Array<Course> = arrayOf()
    private var myCoursesIds: Array<String> = arrayOf()
    private lateinit var parent: MyCoursesActivity
    private lateinit var selectedCourse: Course
    private lateinit var selectedCourseId: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent.context as MyCoursesActivity
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course: Course = myCourses.getOrNull(position) ?: return

        val numEnrolled: String = course.enrolledUsersIds.size.toString()

        holder.bind(course, numEnrolled)

        holder.itemView.setOnClickListener {
            val newFragment = CourseDetailsDialogFragment(course)
            newFragment.show(
                parent.supportFragmentManager,
                course.name + "DetailDialog"
            )
        }

        holder.itemView.setOnLongClickListener {
            selectedCourse = course
            selectedCourseId = myCoursesIds[position]
            parent.showPopupMenu(it)
            return@setOnLongClickListener true
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnLongClickListener(null)
        super.onViewRecycled(holder)
    }

    fun setCourses(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
        myCoursesIds = documents.map { it.id }.toTypedArray()
        myCourses = documents.map { it.toObject(Course::class.java)!! }.toTypedArray()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myCourses.size
    }

    fun getSelectedCourse(): Course {
        return selectedCourse
    }

    fun getSelectedCourseId(): String {
        return selectedCourseId
    }

    class ViewHolder private constructor(binding: MyCourseItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val courseNameTextView: TextView = binding.courseNameTextView
        private val enrolledTextView: TextView = binding.enrolledTextView

        fun bind(course: Course, enrolled: String) {
            courseNameTextView.text = course.name
            enrolledTextView.text = enrolled
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = MyCourseItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
