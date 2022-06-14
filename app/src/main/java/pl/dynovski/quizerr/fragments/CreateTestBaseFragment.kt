package pl.dynovski.quizerr.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import pl.dynovski.quizerr.activities.CreateTestActivity
import pl.dynovski.quizerr.databinding.FragmentCreateTestBaseBinding
import pl.dynovski.quizerr.firebaseObjects.Test
import pl.dynovski.quizerr.singletons.LoggedUser
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*

class CreateTestBaseFragment(private var courseId: String): Fragment() {
    private val TAG = "CREATE_TEST_BASE"

    private lateinit var binding: FragmentCreateTestBaseBinding

    private lateinit var cancelButton: Button
    private lateinit var createButton: Button
    private lateinit var testNameEditText: EditText
    private lateinit var dueDateTextView: TextView
    private lateinit var numOfQuestionsEditText: EditText
    private lateinit var parentActivity: CreateTestActivity

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCreateTestBaseBinding.inflate(layoutInflater)

        cancelButton = binding.cancelButton
        createButton = binding.createButton
        testNameEditText = binding.testNameEditText
        dueDateTextView = binding.testDeadlineTextView
        numOfQuestionsEditText = binding.numOfQuestionsEditText

        parentActivity = activity as CreateTestActivity

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                updateDueDate()
            }

        val datePickerDialog = DatePickerDialog(
            this.requireActivity(),
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = Date().time + 86400000L
        dueDateTextView.setOnClickListener {
            datePickerDialog.show()
        }

        createButton.setOnClickListener {
            val name = testNameEditText.text.toString().trim()
            val numOfQuestions = numOfQuestionsEditText.text.toString().trim()
            val dueDate: Date? = calendar.time

            if (name.isBlank()) {
                testNameEditText.error = "Test must have a name"
                testNameEditText.requestFocus()
                return@setOnClickListener
            }
            if (calendar.time < Date()) {
                dueDateTextView.error = "Test must have a deadline"
                dueDateTextView.requestFocus()
                return@setOnClickListener
            }
            if (numOfQuestions.isBlank()) {
                numOfQuestionsEditText.error = "Choose number of test questions"
                numOfQuestionsEditText.requestFocus()
                return@setOnClickListener
            }
            if (dueDate != null) {
                parentActivity.test = Test(
                    name,
                    Integer.parseInt(numOfQuestions),
                    Timestamp(dueDate),
                    LoggedUser.get().userId,
                    courseId
                )
            }
            createButton.visibility = View.GONE
            testNameEditText.isEnabled = false
            dueDateTextView.isEnabled = false
            numOfQuestionsEditText.isEnabled = false

            parentActivity.addQuestionFragment()
            parentActivity.moveToNextPage()
        }

        cancelButton.setOnClickListener {
            parentActivity.finish()
        }

        return binding.root
    }

    private fun updateDueDate() {
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        dueDateTextView.text = sdf.format(calendar.time)
    }
}