package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.databinding.ResultItemBinding
import pl.dynovski.quizerr.firebaseObjects.CompletedTest

class ResultsAdapter: RecyclerView.Adapter<ResultsAdapter.ViewHolder>() {

    private var completedTests: Array<CompletedTest> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test: CompletedTest = completedTests.getOrNull(position) ?: return
        holder.bind(test)
    }

    fun setCompletedTests(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
        completedTests = documents.map { it.toObject(CompletedTest::class.java)!! }.toTypedArray()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return completedTests.size
    }

    class ViewHolder private constructor(binding: ResultItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val courseNameTextView: TextView = binding.resultCourseNameTextView
        private val testNameTextView: TextView = binding.resultTestNameTextView
        private val scoreTextView: TextView = binding.resultScoreTextView

        fun bind(test: CompletedTest) {
            courseNameTextView.text = test.courseName
            testNameTextView.text = test.name
            scoreTextView.text = "${test.score}/${test.maxScore}"
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