package pl.dynovski.quizerr.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.databinding.ActivityCreateCourseBinding
import pl.dynovski.quizerr.extensions.afterTextChanged
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.CreateCourseViewModel

class CreateCourseActivity : AppCompatActivity() {

    private lateinit var createCourseBinding: ActivityCreateCourseBinding
    private lateinit var createCourseViewModel: CreateCourseViewModel

    // Layout related variables
    private lateinit var courseNameEditText: EditText
    private lateinit var courseDescriptionEditText: EditText
    private lateinit var courseCreateButton: Button
    private lateinit var courseCancelButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createCourseViewModel = ViewModelProvider(this)[CreateCourseViewModel::class.java]
        createCourseBinding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(createCourseBinding.root)

        courseNameEditText = createCourseBinding.courseNameEditText
        courseDescriptionEditText= createCourseBinding.courseDescriptionEditText
        courseCreateButton = createCourseBinding.createButton
        courseCancelButton = createCourseBinding.cancelButton

        auth = Firebase.auth
        database = Firebase.firestore

        val course = intent.extras?.get(MyCoursesActivity.COURSE_KEY) as? Course

        createCourseViewModel.newCourseFormState.observe(this, Observer {
            val newCourseState = it ?: return@Observer

            courseCreateButton.isEnabled = newCourseState.isDataValid

            if (newCourseState.nameError != null) {
                courseNameEditText.error = getString(newCourseState.nameError)
            }
            if (newCourseState.descriptionError != null) {
                courseDescriptionEditText.error = getString(newCourseState.descriptionError)
            }
        })

        courseNameEditText.afterTextChanged {
            createCourseViewModel.dataChanged(
                courseNameEditText.text.toString(),
                courseDescriptionEditText.text.toString()
            )
        }

        courseDescriptionEditText.afterTextChanged {
            createCourseViewModel.dataChanged(
                courseNameEditText.text.toString(),
                courseDescriptionEditText.text.toString()
            )
        }

        if (course != null) {
            courseNameEditText.setText(course.name)
            courseDescriptionEditText.setText(course.description)
        }

        courseCancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        courseCreateButton.setOnClickListener {
            val newCourse: Course
            if (course != null) {
                newCourse = course
                newCourse.name = courseNameEditText.text.toString()
                newCourse.description = courseDescriptionEditText.text.toString()

            } else {
                newCourse = Course(
                    courseNameEditText.text.toString(),
                    LoggedUser.get().name,
                    courseDescriptionEditText.text.toString(),
                    LoggedUser.get().userId
                )
            }
            val data = Intent()
            data.putExtra(MyCoursesActivity.COURSE_KEY, newCourse)
            data.putExtra(
                MyCoursesActivity.COURSE_ID_KEY,
                intent.getStringExtra(MyCoursesActivity.COURSE_ID_KEY)
            )
            setResult(RESULT_OK, data)
            finish()
        }
    }
}