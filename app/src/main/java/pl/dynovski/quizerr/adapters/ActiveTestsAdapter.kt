package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.SubscriptionCourseItemBinding
import pl.dynovski.quizerr.firebaseObjects.Test

class ActiveTestsAdapter: RecyclerView.Adapter<ActiveTestsAdapter.ViewHolder>() {

    private var activeTests: Array<Test> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test: Test = activeTests.getOrNull(position) ?: return
        holder.bind(test)

//        holder.beginButton.setOnClickListener {
//            val solveTestIntent = Intent(it.context, SolveTestActivity::class.java)
//            solveTestIntent.putExtra("testName", test.testName)
//            solveTestIntent.putExtra("numOfQuestions", test.numQuestions)
//            solveTestIntent.putExtra("deadline", test.dueDate)
//            solveTestIntent.putExtra("courseName", test.courseName)
//            solveTestIntent.putExtra("courseName", test.author)
//            it.context.startActivity(solveTestIntent)
//        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.beginButton.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    fun setActiveTests(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
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
            testNameTextView.text = test.testName
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