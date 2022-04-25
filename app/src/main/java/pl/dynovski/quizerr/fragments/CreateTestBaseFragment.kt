package pl.dynovski.quizerr.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import pl.dynovski.quizerr.activities.CreateTestActivity
import pl.dynovski.quizerr.databinding.FragmentCreateTestBaseBinding
import pl.dynovski.quizerr.firebaseObjects.Test
import java.lang.Exception
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class CreateTestBaseFragment(var courseName: String) : Fragment() {
    private val LOG_TAG = "CREATE_TEST_BASE"

    private lateinit var binding: FragmentCreateTestBaseBinding

    private lateinit var cancelButton: Button
    private lateinit var createButton: Button
    private lateinit var testNameEditText: EditText
    private lateinit var dueDateEditText: EditText
    private lateinit var numOfQuestionsEditText: EditText
    private lateinit var parentActivity: CreateTestActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateTestBaseBinding.inflate(layoutInflater)

        cancelButton = binding.cancelButton
        createButton = binding.createButton
        testNameEditText = binding.testNameEditText
        dueDateEditText = binding.testDeadlineEditText
        numOfQuestionsEditText = binding.numOfQuestionsEditText

        parentActivity = activity as CreateTestActivity

        createButton.setOnClickListener {
            val testName = testNameEditText.text.toString().trim()
            val numOfQuestions = numOfQuestionsEditText.text.toString().trim()
            val dueDateString = dueDateEditText.text.toString().trim()
            var dueDate: LocalDate? = null
            try {
                dueDate = LocalDate.parse(dueDateString, DateTimeFormatter.ISO_DATE)
            } catch (e: Exception) {
                Log.d(LOG_TAG, "Couldn't format the date\n$e")
            }
            val finalDueDate = dueDate
            if (testName.isBlank()) {
                testNameEditText.error = "Test must have a name"
                testNameEditText.requestFocus()
                return@setOnClickListener
            }
            if (dueDateString.isBlank()) {
                dueDateEditText.error = "Test must have a deadline"
                dueDateEditText.requestFocus()
                return@setOnClickListener
            }
            if (numOfQuestions.isBlank()) {
                numOfQuestionsEditText.error = "Choose number of test questions"
                numOfQuestionsEditText.requestFocus()
                return@setOnClickListener
            }

            if (finalDueDate != null) {
                parentActivity.test = Test(
                    testName,
                    courseName,
                    Integer.parseInt(numOfQuestions),
                    Timestamp(Date.from(finalDueDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                )
            }

            if (parentActivity.data.keys.isEmpty()) {
                parentActivity.addQuestionFragment()
                parentActivity.moveToNextPage()
            }
        }

        return binding.root
    }

}