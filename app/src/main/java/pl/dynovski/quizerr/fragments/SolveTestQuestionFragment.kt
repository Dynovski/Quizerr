package pl.dynovski.quizerr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import pl.dynovski.quizerr.activities.SolveTestActivity
import pl.dynovski.quizerr.databinding.FragmentSolveTestQuestionBinding
import pl.dynovski.quizerr.firebaseObjects.Answer
import pl.dynovski.quizerr.firebaseObjects.Question

class SolveTestQuestionFragment(
    private val question: Question,
    private val answers: List<Answer>,
    private val questionId: Int
): Fragment() {

    private lateinit var binding: FragmentSolveTestQuestionBinding

    private lateinit var topTextView: TextView
    private lateinit var questionTextView: TextView

    private lateinit var answer1TextView: TextView
    private lateinit var answer2TextView: TextView
    private lateinit var answer3TextView: TextView
    private lateinit var answer4TextView: TextView

    private lateinit var answer1CheckBox: CheckBox
    private lateinit var answer2CheckBox: CheckBox
    private lateinit var answer3CheckBox: CheckBox
    private lateinit var answer4CheckBox: CheckBox

    private lateinit var finishButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolveTestQuestionBinding.inflate(inflater)

        topTextView = binding.topTextView
        questionTextView = binding.questionTextView

        answer1TextView = binding.answer1
        answer2TextView = binding.answer2
        answer3TextView = binding.answer3
        answer4TextView = binding.answer4

        answer1CheckBox = binding.answerCheckBox1
        answer2CheckBox = binding.answerCheckBox2
        answer3CheckBox = binding.answerCheckBox3
        answer4CheckBox = binding.answerCheckBox4

        finishButton = binding.nextButton

        val solveTestActivity = activity as SolveTestActivity

        questionTextView.text = question.text

        answer1TextView.text = answers[0].text
        answer2TextView.text = answers[1].text
        answer3TextView.text = answers[2].text
        answer4TextView.text = answers[3].text

        solveTestActivity.timerViewModel.timeInMillis.observe(viewLifecycleOwner, Observer {
            val newTime = it ?: return@Observer
            if (newTime / 1000 < 1) {
                topTextView.text = "Time's up!"
            } else {
                topTextView.text = "${newTime / 1000} s"
            }
        })

        if (solveTestActivity.getNumFragments() == questionId + 2) {
            finishButton.visibility = View.VISIBLE
            finishButton.setOnClickListener {
                finishButton.visibility = View.GONE
                solveTestActivity.invalidateTimer()
                solveTestActivity.checkTest()
            }
        }

        return binding.root
    }

    fun areAnswersCorrect(): Boolean {
        return answer1CheckBox.isChecked == answers[0].isCorrect &&
                answer2CheckBox.isChecked == answers[1].isCorrect &&
                answer3CheckBox.isChecked == answers[2].isCorrect &&
                answer4CheckBox.isChecked == answers[3].isCorrect
    }

    fun hideFinishButton() {
        finishButton.visibility = View.GONE
    }
}