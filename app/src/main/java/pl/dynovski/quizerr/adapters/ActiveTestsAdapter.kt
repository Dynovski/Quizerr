package pl.dynovski.quizerr.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.activities.ActiveTestsActivity
import pl.dynovski.quizerr.activities.SolveTestActivity
import pl.dynovski.quizerr.databinding.SubscriptionCourseItemBinding
import pl.dynovski.quizerr.firebaseObjects.Test

class ActiveTestsAdapter: RecyclerView.Adapter<ActiveTestsAdapter.ViewHolder>() {

    private var activeTests: Array<Test> = arrayOf()
    private var activeTestsIds: Array<String> = arrayOf()
    private lateinit var parent: ActiveTestsActivity


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent.context as ActiveTestsActivity
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test: Test = activeTests.getOrNull(position) ?: return
        holder.bind(test)

        holder.beginButton.setOnClickListener {
            val solveTestIntent = Intent(parent, SolveTestActivity::class.java)
            solveTestIntent.putExtra(SolveTestActivity.TEST_NAME_KEY, test.name)
            solveTestIntent.putExtra(SolveTestActivity.TEST_QUESTIONS_KEY, test.numQuestions)
            solveTestIntent.putExtra(SolveTestActivity.TEST_DATE_KEY, test.dueDate)
            solveTestIntent.putExtra(SolveTestActivity.TEST_ID_KEY, activeTestsIds[position])
            solveTestIntent.putExtra(SolveTestActivity.TEST_COURSE_ID_KEY, test.courseId)
            solveTestIntent.putExtra(SolveTestActivity.TEST_USER_ID_KEY, test.userId)
            parent.startActivity(solveTestIntent)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.beginButton.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    fun setActiveTests(documents: List<DocumentSnapshot>) {
        activeTestsIds = documents.map { it.id }.toTypedArray()
        activeTests = documents.map { it.toObject(Test::class.java)!! }.toTypedArray()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return activeTests.size
    }

    class ViewHolder private constructor(binding: SubscriptionCourseItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val testNameTextView: TextView = binding.courseNameTextView
        val beginButton: Button = binding.subscriptionButton

        fun bind(test: Test) {
            testNameTextView.text = test.name
            beginButton.setText(R.string.action_begin)
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = SubscriptionCourseItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}