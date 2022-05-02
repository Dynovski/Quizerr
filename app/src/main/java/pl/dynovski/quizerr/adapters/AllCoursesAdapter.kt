package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.SubscriptionCourseItemBinding
import pl.dynovski.quizerr.firebaseObjects.Course

class AllCoursesAdapter: RecyclerView.Adapter<AllCoursesAdapter.ViewHolder>() {

    private var allCourses: Array<Course> = arrayOf()
    private var subscribedCourses: Array<String> = arrayOf()
    private val database = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course: Course = allCourses.getOrNull(position) ?: return


        val courseInSubscriptions = subscribedCourses.contains(course.name)
        holder.bind(course, courseInSubscriptions)

        holder.subscriptionButton.setOnClickListener {
            val button = it as Button
            val currentUser = FirebaseAuth.getInstance().currentUser ?: return@setOnClickListener
            if (courseInSubscriptions) {
                button.setText(R.string.action_join)
                database.collection("Users")
                    .document(currentUser.uid)
                    .collection("SubscribedCourses")
                    .document(course.name).delete()
                database.collection("Courses").document(course.name)
                    .collection("EnrolledStudents")
                    .document(currentUser.uid)
                    .delete()
            } else {
                button.setText(R.string.action_leave)
                database.collection("Users")
                    .document(currentUser.uid)
                    .collection("SubscribedCourses")
                    .document(course.name).set(course)
                database.collection("Courses").document(course.name)
                    .collection("EnrolledStudents")
                    .document(currentUser.uid)
                    .set(hashMapOf<String, Any>())
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.subscriptionButton.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    fun setAllCourses(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
        allCourses = documents.map { it.toObject(Course::class.java)!! }.toTypedArray()
        notifyDataSetChanged()
    }

    fun setSubscribedCourses(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
        subscribedCourses = documents.map { it.toObject(Course::class.java)!!.name }.toTypedArray()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return allCourses.size
    }

    class ViewHolder private constructor(private val binding: SubscriptionCourseItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val courseNameTextView: TextView = binding.courseNameTextView
        val subscriptionButton: Button = binding.subscriptionButton

        fun bind(course: Course, isSubscribed: Boolean) {
            courseNameTextView.text = course.name
            if (isSubscribed)
                subscriptionButton.setText(R.string.action_leave)
            else
                subscriptionButton.setText(R.string.action_join)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = SubscriptionCourseItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}
