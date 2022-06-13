package pl.dynovski.quizerr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import pl.dynovski.quizerr.activities.SolveTestActivity
import pl.dynovski.quizerr.databinding.FragmentSolveTestBaseBinding
import pl.dynovski.quizerr.firebaseObjects.Test
import java.text.SimpleDateFormat

class SolveTestBaseFragment(
    private val testName: String,
    private val testDueDate: Timestamp,
    private val numQuestions: Int
): Fragment() {

    private lateinit var binding: FragmentSolveTestBaseBinding

    private lateinit var cancelButton: Button
    private lateinit var beginButton: Button
    private lateinit var testNameTextView: TextView
    private lateinit var dueDateTextView: TextView
    private lateinit var numOfQuestionsTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolveTestBaseBinding.inflate(inflater)

        cancelButton = binding.cancelButton
        beginButton = binding.beginButton
        testNameTextView = binding.testNameTextView
        dueDateTextView = binding.testDeadlineTextView
        numOfQuestionsTextView = binding.testNumQuestionsTextView

        testNameTextView.text = testName
        dueDateTextView.text = SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(testDueDate.toDate())
        numOfQuestionsTextView.text = numQuestions.toString()

        val solveTestActivity = activity as SolveTestActivity

        cancelButton.setOnClickListener {
            solveTestActivity.finish()
        }

        beginButton.setOnClickListener {
            solveTestActivity.addQuestionFragments()
            solveTestActivity.moveToNextPage()
            beginButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
        }

        return binding.root
    }
}