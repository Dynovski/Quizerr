package pl.dynovski.quizerr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.activities.CreateTestActivity
import pl.dynovski.quizerr.databinding.FragmentCreateTestQuestionBinding
import pl.dynovski.quizerr.firebaseObjects.Answer
import pl.dynovski.quizerr.firebaseObjects.Question

class CreateTestQuestionFragment(private val questionId: Int): Fragment() {

    private lateinit var binding: FragmentCreateTestQuestionBinding

    private lateinit var questionEditText: EditText

    private lateinit var answer1EditText: EditText
    private lateinit var answer2EditText: EditText
    private lateinit var answer3EditText: EditText
    private lateinit var answer4EditText: EditText

    private lateinit var answer1CheckBox: CheckBox
    private lateinit var answer2CheckBox: CheckBox
    private lateinit var answer3CheckBox: CheckBox
    private lateinit var answer4CheckBox: CheckBox

    private lateinit var nextButton: Button

    private lateinit var parentActivity: CreateTestActivity
    // TODO: data from activity already added
    private var wasDataPassedToParent: Boolean = false

    // TODO: viewmodele dla każdego fragmentu, dla textchange ustawianie pola w klasie zdefiniowanej
    // TODO: na szczycie fragmentu, klasa umieszczana w data, wtedy powinno aktualizować bez wciskania buttona
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateTestQuestionBinding.inflate(layoutInflater)

        questionEditText = binding.questionEditText

        answer1EditText = binding.answerEditText1
        answer2EditText = binding.answerEditText2
        answer3EditText = binding.answerEditText3
        answer4EditText = binding.answerEditText4

        answer1CheckBox = binding.answerCheckBox1
        answer2CheckBox = binding.answerCheckBox2
        answer3CheckBox = binding.answerCheckBox3
        answer4CheckBox = binding.answerCheckBox4

        nextButton = binding.createButton

        parentActivity = activity as CreateTestActivity

        // If this fragment is final one
        if (parentActivity.test.numQuestions == questionId + 1)
            nextButton.setText(R.string.action_create_test)
        else
            nextButton.setText(R.string.action_create_test_next)

        nextButton.setOnClickListener {
            if (questionEditText.text.isBlank()) {
                questionEditText.error = "Question cannot be empty"
                questionEditText.requestFocus()
                return@setOnClickListener
            }

            val answer1 = answer1EditText.text.toString().trim()
            val answer2 = answer2EditText.text.toString().trim()
            val answer3 = answer3EditText.text.toString().trim()
            val answer4 = answer4EditText.text.toString().trim()

            val correct1 = answer1CheckBox.isChecked
            val correct2 = answer2CheckBox.isChecked
            val correct3 = answer3CheckBox.isChecked
            val correct4 = answer4CheckBox.isChecked

            if (arrayOf(answer1, answer2, answer3, answer4).any { it.isBlank() }) {
                Toast.makeText(parentActivity, "Answers cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val question = Question(questionEditText.text.toString().trim())
            val answers = listOf(
                Answer(answer1, correct1),
                Answer(answer2, correct2),
                Answer(answer3, correct3),
                Answer(answer4, correct4)
            )

            parentActivity.setDataItem(questionId, question, answers)

            // If final fragment save test to database
            if (parentActivity.test.numQuestions == questionId + 1)
                parentActivity.saveTest()
            else {
                // Add next fragment when in last
                if (parentActivity.data.keys.count() == questionId + 1) {
                    parentActivity.addQuestionFragment()
                    parentActivity.moveToNextPage()
                    nextButton.setText(R.string.action_edit)
                } else { // Otherwise edit already created fragment
                    parentActivity.moveToNextPage()
                    Toast.makeText(parentActivity, "Question $questionId edited", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return binding.root
    }
}