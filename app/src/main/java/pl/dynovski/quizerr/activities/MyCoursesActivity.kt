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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.MyCoursesAdapter
import pl.dynovski.quizerr.databinding.ActivityRecyclerViewBinding
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.CoursesViewModel

class MyCoursesActivity: AppCompatActivity(), PopupMenu.OnMenuItemClickListener {

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
        coursesViewModel.currentUserCourses.observe(this) { queryDocumentSnapshots ->
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

                val editedCourse = data.extras!!.get(COURSE_KEY) as Course
                val selectedCourseId = data.getStringExtra(COURSE_ID_KEY)!!
                updateCourse(selectedCourseId, editedCourse)
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
                val editCourseIntent = Intent(this, CreateCourseActivity::class.java)
                editCourseIntent.putExtra(COURSE_KEY, adapter.getSelectedCourse())
                editCourseIntent.putExtra(COURSE_ID_KEY, adapter.getSelectedCourseId())
                editCourseLauncher.launch(editCourseIntent)
                return true
            }
            R.id.delete_course_item -> {
                val selectedCourseId = adapter.getSelectedCourseId()
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.question_delete_course)
                builder.setPositiveButton(R.string.action_yes) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    deleteTestsRelatedTo(selectedCourseId)
                    deleteCourse(selectedCourseId)
                }
                builder.setNegativeButton(R.string.action_no) { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                builder.create().show()
                return true
            }
            R.id.add_test_item -> {
                val testIntent = Intent(this, CreateTestActivity::class.java)
                testIntent.putExtra(COURSE_ID_KEY, adapter.getSelectedCourseId())
                startActivity(testIntent)
                return true
            }
            else -> { return true }
        }
    }

    private fun deleteCourse(courseId: String) {
        database.collection("Courses")
            .document(courseId)
            .get()
            .addOnSuccessListener {
                val enrolledUsersIds = it.get("enrolledUsersIds") as? List<String> ?: return@addOnSuccessListener
                for (userId in enrolledUsersIds) {
                    database.collection("Users")
                        .document(userId)
                        .update("subscribedCoursesIds", FieldValue.arrayRemove(courseId))
                        .addOnSuccessListener {
                            Log.d(TAG, "Deleted $courseId from subscribed courses of $userId")
                        }
                }
                database.collection("Courses")
                    .document(courseId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG,"Successfully deleted course from courses")
                        database.collection("Users")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Couldn't delete course from courses\n$it")
                    }
            }
    }

    private fun updateCourse(courseId: String, editedCourse: Course) {
        database.collection("Courses")
            .document(courseId)
            .set(editedCourse)
            .addOnSuccessListener {
                Log.d(TAG, "Updated course data for $courseId")
                Toast.makeText(this, R.string.edit_course_success, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to update course data for $courseId")
                Toast.makeText(this, R.string.edit_course_failed, Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteTestsRelatedTo(courseId: String) {
        database.collection("Tests")
            .whereEqualTo("courseId", courseId)
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for(document in documents) {
                    deleteQuestionsRelatedTo(document.id)
                    document.reference.delete()
                }
            }
    }

    private fun deleteQuestionsRelatedTo(testId: String) {
        database.collection("Questions")
            .whereEqualTo("testId", testId)
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for(document in documents) {
                    deleteAnswersRelatedTo(document.id)
                    document.reference.delete()
                }
            }
    }

    private fun deleteAnswersRelatedTo(questionId: String) {
        database.collection("Answers")
            .whereEqualTo("question", questionId)
            .get()
            .addOnSuccessListener {
                val documents = it.documents
                for(document in documents) {
                    document.reference.delete()
                }
            }
    }

    companion object {
        private const val TAG = "MY_COURSES"
        const val COURSE_KEY = "OLD_COURSE"
        const val COURSE_ID_KEY = "COURSE_ID"
    }
}