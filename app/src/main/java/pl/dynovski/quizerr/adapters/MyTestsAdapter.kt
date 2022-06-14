package pl.dynovski.quizerr.adapters

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.QuerySnapshot
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.activities.MyTestsActivity
import pl.dynovski.quizerr.databinding.MyTestItemBinding
import pl.dynovski.quizerr.firebaseObjects.Test

class MyTestsAdapter: RecyclerView.Adapter<MyTestsAdapter.ViewHolder>() {

    private var myTests: Array<Test> = arrayOf()
    private var myTestsIds: Array<String> = arrayOf()
    private lateinit var parent: MyTestsActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        this.parent = parent.context as MyTestsActivity
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val test: Test = myTests.getOrNull(position) ?: return

        holder.bind(test)

        holder.editButton.setOnClickListener {
            val builder = AlertDialog.Builder(parent)
            builder.setMessage(R.string.question_delete_test)
            builder.setPositiveButton(R.string.action_yes) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                parent.deleteTest(myTestsIds[position])
            }
            builder.setNegativeButton(R.string.action_no) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.itemView.setOnClickListener(null)
        super.onViewRecycled(holder)
    }

    fun setTests(snapshot: QuerySnapshot) {
        val documents = snapshot.documents
        myTestsIds = documents.map { it.id }.toTypedArray()
        myTests = documents.map { it.toObject(Test::class.java)!! }.toTypedArray()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myTests.size
    }

    class ViewHolder private constructor(binding: MyTestItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val courseNameTextView: TextView = binding.testNameTextView
        val editButton: Button = binding.editButton

        fun bind(test: Test) {
            courseNameTextView.text = test.name
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = MyTestItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}