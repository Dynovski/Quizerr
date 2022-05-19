package pl.dynovski.quizerr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import pl.dynovski.quizerr.activities.SolveTestActivity
import pl.dynovski.quizerr.databinding.FragmentSolveTestSummaryBinding
import pl.dynovski.quizerr.firebaseObjects.TestResult

class TestResultFragment(private val testResult: TestResult): Fragment() {

    private lateinit var binding: FragmentSolveTestSummaryBinding

    private lateinit var testNameTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var finishButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSolveTestSummaryBinding.inflate(inflater)

        testNameTextView = binding.testNameTextView
        scoreTextView = binding.testScoreTextView
        finishButton = binding.closeButton

        testNameTextView.text = testResult.testName
        scoreTextView.text = "${testResult.score} / ${testResult.maxScore}"

        finishButton.setOnClickListener {
            (activity as SolveTestActivity).finish()
        }

        return binding.root
    }
}