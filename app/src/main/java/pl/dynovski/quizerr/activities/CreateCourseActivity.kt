package pl.dynovski.quizerr.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.databinding.ActivityCreateCourseBinding
import pl.dynovski.quizerr.extensions.afterTextChanged
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createCourseViewModel = ViewModelProvider(this)[CreateCourseViewModel::class.java]
        createCourseBinding = ActivityCreateCourseBinding.inflate(layoutInflater)
        setContentView(createCourseBinding.root)

        courseNameEditText = createCourseBinding.courseNameEditText
        courseDescriptionEditText= createCourseBinding.courseDescriptionEditText
        courseCreateButton = createCourseBinding.createButton
        courseCancelButton = createCourseBinding.cancelButton

        // TODO: edit course zamiast create course jak cos w intencie zostało przekazane

        auth = Firebase.auth

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

        courseCancelButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }

        courseCreateButton.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}