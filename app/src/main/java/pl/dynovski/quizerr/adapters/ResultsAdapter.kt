package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.databinding.ResultItemBinding
import pl.dynovski.quizerr.firebaseObjects.TestResult

class ResultsAdapter(): RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    private var testResults: Array<TestResult> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val testResult: TestResult = testResults.getOrNull(position) ?: return
        holder.bind(testResult)
    }

    fun setResults(results: Array<TestResult>) {
        testResults = results
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return testResults.size
    }

    class ViewHolder private constructor(binding: ResultItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val testNameTextView: TextView = binding.resultTestNameTextView
        private val scoreTextView: TextView = binding.resultScoreTextView

        fun bind(testResult: TestResult) {
            testNameTextView.text = testResult.testName
            scoreTextView.text = "${testResult.score}/${testResult.maxScore}"
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ResultItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}