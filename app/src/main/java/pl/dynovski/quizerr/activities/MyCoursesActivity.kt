package pl.dynovski.quizerr.activities
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.MyCoursesAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.CoursesViewModel

class MyCoursesActivity : AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

    private lateinit var coursesViewModel: CoursesViewModel
    private lateinit var adapter: MyCoursesAdapter
    private lateinit var binding: ActivityRecyclerViewBinding

    private lateinit var recyclerView: RecyclerView
    private lateinit var headerTextView: TextView
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        database = FirebaseFirestore.getInstance()

        setContentView(binding.root)

        adapter = MyCoursesAdapter()
        recyclerView = binding.recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        headerTextView = binding.headerTextView
        headerTextView.setText(R.string.label_my_courses)

        coursesViewModel = ViewModelProvider(this)[CoursesViewModel::class.java]
        coursesViewModel.myCourses.observe(this) { queryDocumentSnapshots ->
            adapter.setCourses(queryDocumentSnapshots)
        }
    }

    fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.my_courses_popup)
        popup.show()
    }

    private val editCourseLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        when (it.resultCode) {
            RESULT_OK -> {
                val data = it.data ?: return@registerForActivityResult

                val oldCourseName = data.getStringExtra(COURSE_OLD_NAME_KEY)!!
                val newCourseName = data.getStringExtra(COURSE_NEW_NAME_KEY)!!
                val courseDescription = data.getStringExtra(COURSE_DESCRIPTION_KEY)!!

                if (oldCourseName!= newCourseName) {
                    database.collection("Courses")
                        .document(oldCourseName)
                        .collection("EnrolledStudents")
                        .get()
                        .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                            val enrolledStudents = queryDocumentSnapshots.documents
                            val studentsEnrolledToCourse: MutableList<String> = mutableListOf()
                            val editedCourse = Course(
                                newCourseName,
                                LoggedUser.get()?.name ?: "",
                                courseDescription
                            )
                            for (enrolledStudent in enrolledStudents) {
                                val userId: String = enrolledStudent.id
                                studentsEnrolledToCourse.add(userId)
                                deleteCourseFromSubscribedCourses(userId, oldCourseName)
                                setSubscribedCourse(userId, newCourseName, editedCourse)
                                updateCourseNameInCompletedTests(
                                    userId,
                                    oldCourseName,
                                    newCourseName
                                )
                                // deleting document from enrolled students collection
                                enrolledStudent.reference.delete()
                            }
                            deleteCourse(oldCourseName)
                            recreateCourse(newCourseName, editedCourse, studentsEnrolledToCourse)
                            updateCourseNameInTests(oldCourseName, newCourseName)
                            Log.d(TAG,"Updated course information for name changed")
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "Couldn't retrieve all users from course\n$it")
                        }
                } else {
                    val editedCourse = Course(
                        newCourseName,
                        LoggedUser.get()?.name ?: "",
                        courseDescription
                    )
                    recreateCourse(newCourseName, editedCourse, listOf())
                    database.collection("Courses")
                        .document(oldCourseName)
                        .collection("EnrolledStudents")
                        .get()
                        .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                            val enrolledStudents = queryDocumentSnapshots.documents
                            for (enrolledStudent in enrolledStudents) {
                                val userId: String = enrolledStudent.id
                                setSubscribedCourse(userId, newCourseName, editedCourse)
                            }
                            Log.d(TAG, "Updated course information for name unchanged")
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "Couldn't retrieve all users from course\n$it")
                        }
                }
                Toast.makeText(this, R.string.edit_course_success, Toast.LENGTH_SHORT).show()
            }
            RESULT_CANCELED ->
                Toast.makeText(this, R.string.edit_course_cancel, Toast.LENGTH_SHORT).show()
            else ->
                Toast.makeText(this, R.string.edit_course_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_course_item -> {
                val selectedCourse: Course = adapter.getSelectedCourse()
                val editCourseIntent = Intent(this, CreateCourseActivity::class.java)
                editCourseIntent.putExtra(COURSE_NAME_KEY, selectedCourse.name)
                editCourseIntent.putExtra(COURSE_DESCRIPTION_KEY, selectedCourse.description)
                editCourseLauncher.launch(editCourseIntent)
                return true
            }
            R.id.delete_course_item -> {
                val selectedCourse = adapter.getSelectedCourse()
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Do you want to delete this course?")
                builder.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    database.collection("Courses")
                        .document(selectedCourse.name)
                        .collection("EnrolledStudents")
                        .get()
                        .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                            val enrolledStudents = queryDocumentSnapshots.documents
                            for (student in enrolledStudents) {
                                val studentId = student.id
                                database.collection("Users")
                                    .document(studentId)
                                    .collection("SubscribedCourses")
                                    .document(selectedCourse.name)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(TAG, "Successfully deleted course from subscribed courses")
                                    }
                                    .addOnFailureListener {
                                        Log.d(TAG, "Couldn't delete course from subscribed courses\n$it")
                                    }
                                student.reference.delete()
                            }
                            database.collection("Courses")
                                .document(selectedCourse.name)
                                .delete()
                                .addOnSuccessListener {
                                    Log.d(TAG, "Successfully deleted course from courses")
                                }
                                .addOnFailureListener {
                                    Log.d(TAG, "Couldn't delete course from courses\n$it")
                                }
                        }
                }
                builder.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                builder.create().show()
                return true
            }
            R.id.add_test_item -> {
                val testIntent = Intent(this, CreateTestActivity::class.java)
                testIntent.putExtra("courseName", adapter.getSelectedCourse().name)
                startActivity(testIntent)
                return true
            }
            else -> { return true }
        }
    }

    private fun deleteCourseFromSubscribedCourses(userId: String, courseName: String) {
        database.collection("Users")
            .document(userId)
            .collection("SubscribedCourses")
            .document(courseName)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG,"Successfully deleted course content from subscribed courses")
            }
            .addOnFailureListener {
                Log.d(TAG, "Couldn't delete course content from subscribed courses\n$it")
            }
    }

    private fun updateCourseNameInCompletedTests(
        userId: String,
        oldCourseName: String,
        newCourseName: String
    ) {
        database.collection("Users")
            .document(userId)
            .collection("CompletedTests")
            .whereEqualTo("courseName", oldCourseName)
            .get()
            .addOnSuccessListener {
                val completedTests = it.documents
                for (completedTest in completedTests)
                    completedTest.reference
                        .update("courseName", newCourseName)
                        .addOnSuccessListener {
                            Log.d(TAG, "Successfully updated course in completed tests")
                        }
                        .addOnFailureListener {
                            Log.d(TAG, "Couldn't update course in completed tests\n$it")
                        }
            }
            .addOnFailureListener {
                Log.d(TAG,"Couldn't get courses from completed tests\n$it")
            }
    }

    private fun deleteCourse(courseName: String) {
        database.collection("Courses")
            .document(courseName)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG,"Successfully deleted course from courses")
            }
            .addOnFailureListener {
                Log.d(TAG, "Couldn't delete course from courses\n$it")
            }
    }

    private fun updateCourseNameInTests(oldCourseName: String, newCourseName: String) {
        database.collection("Tests")
            .whereEqualTo("courseName", oldCourseName)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots: QuerySnapshot ->
                val tests = queryDocumentSnapshots.documents
                for (test in tests)
                    test.reference
                        .update("courseName", newCourseName)
                        .addOnSuccessListener {
                            Log.d(TAG,"Successfully updated courseName in tests")
                        }
                        .addOnFailureListener {
                            Log.d(TAG,"Couldn't update courseName in tests\n$it")
                        }
            }
            .addOnFailureListener {
                Log.d(TAG, "Couldn't get tests with given courseName\n$it")
            }
    }

    private fun recreateCourse(courseName: String, course: Course, enrolledStudents: List<String>) {
        database.collection("Courses")
            .document(courseName)
            .set(course)
            .addOnSuccessListener {
                // If name changed (therefore document) rewrite enrolled students
                if (enrolledStudents.isNotEmpty()) {
                    for (student in enrolledStudents)
                        FirebaseFirestore.getInstance()
                            .collection("Courses")
                            .document(courseName)
                            .collection("EnrolledStudents")
                            .document(student)
                            .set(hashMapOf<String, Any>())
                }
                Log.d(TAG, "Recreated course with new data")
            }
            .addOnFailureListener {
                Log.d(TAG, "Couldn't set course in courses\n$it")
            }
    }

    private fun setSubscribedCourse(userId: String, courseName: String, course: Course) {
        database.collection("Users")
            .document(userId)
            .collection("SubscribedCourses")
            .document(courseName)
            .set(course)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully set course in subscribed courses")
            }
            .addOnFailureListener {
                Log.d(TAG, "Couldn't set course in subscribed courses\n$it")
            }
    }

    companion object {
        private const val TAG = "TEACHER_COURSES"
        private const val COURSE_NAME_KEY = "COURSE_NAME"
        private const val COURSE_OLD_NAME_KEY = "OLD_COURSE_NAME"
        private const val COURSE_NEW_NAME_KEY = "NEW_COURSE_NAME"
        private const val COURSE_DESCRIPTION_KEY = "COURSE_DESCRIPTION"
    }
}