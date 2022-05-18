package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.activities.ChooseCourseActivity
import pl.dynovski.quizerr.databinding.CourseItemBinding
import pl.dynovski.quizerr.firebaseObjects.Course

class ChooseCourseAdapter: RecyclerView.Adapter<ChooseCourseAdapter.ViewHolder>() {

    private var myCourses: Array<Course> = arrayOf()
    private var myCoursesIds: Array<String> = arrayOf()
    private lateinit var parent: ChooseCourseActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent.context as ChooseCourseActivity
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course: Course = myCourses.getOrNull(position) ?: return

        holder.bind(course)

        holder.itemView.setOnClickListener {
            parent.startTestCreationFor(myCoursesIds[position])
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnClickListener(null)
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

    class ViewHolder private constructor(binding: CourseItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val courseNameTextView: TextView = binding.courseNameTextView

        fun bind(course: Course) {
            courseNameTextView.text = course.name
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = CourseItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}