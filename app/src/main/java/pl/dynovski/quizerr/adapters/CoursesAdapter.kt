package pl.dynovski.quizerr.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.SubscriptionCourseItemBinding
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.firebaseObjects.User
import pl.dynovski.quizerr.singletons.LoggedUser

class CoursesAdapter: RecyclerView.Adapter<CoursesAdapter.ViewHolder>() {

    private var allCourses: Array<Course> = arrayOf()
    private var allCoursesIds: Array<String> = arrayOf()
    private var subscribedCoursesIds: Array<String> = arrayOf()
    private val database = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val course: Course = allCourses.getOrNull(position) ?: return
        val courseId: String = allCoursesIds[position]

        val courseInSubscriptions = subscribedCoursesIds.contains(courseId)
        holder.bind(course, courseInSubscriptions)

        holder.subscriptionButton.setOnClickListener {
            val button = it as Button
            if (courseInSubscriptions) {
                button.setText(R.string.action_join)
                removeFromSubscribed(courseId)
                removeUserFromEnrolledUsers(courseId)
            } else {
                button.setText(R.string.action_leave)
                addToSubscribed(courseId)
                addUserToEnrolledUsers(courseId)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.subscriptionButton.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    override fun getItemCount(): Int {
        return allCourses.size
    }

    fun setAllCourses(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
        allCoursesIds = documents.map { it.id }.toTypedArray()
        allCourses = documents.map { it.toObject(Course::class.java)!! }.toTypedArray()
        notifyDataSetChanged()
    }

    fun setSubscribedCourses(snapshot: QuerySnapshot) {
        val user = snapshot.documents[0].toObject(User::class.java)!!
        subscribedCoursesIds = user.subscribedCoursesIds.toTypedArray()
        notifyDataSetChanged()
    }

    private fun addToSubscribed(courseId: String) {
        database.collection("Users")
            .document(LoggedUser.get().userId)
            .update("subscribedCoursesIds", FieldValue.arrayUnion(courseId))
            .addOnSuccessListener {
                LoggedUser.get().subscribedCoursesIds
            }
    }

    private fun addUserToEnrolledUsers(courseId: String) {
        database.collection("Courses")
            .document(courseId)
            .update("enrolledUsersIds", FieldValue.arrayUnion(LoggedUser.get().userId))
    }

    private fun removeFromSubscribed(courseId: String) {
        database.collection("Users")
            .document(LoggedUser.get().userId)
            .update("subscribedCoursesIds", FieldValue.arrayRemove(courseId))
    }

    private fun removeUserFromEnrolledUsers(courseId: String) {
        database.collection("Courses")
            .document(courseId)
            .update("enrolledUsersIds", FieldValue.arrayRemove(LoggedUser.get().userId))
    }


    class ViewHolder private constructor(binding: SubscriptionCourseItemBinding):
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
