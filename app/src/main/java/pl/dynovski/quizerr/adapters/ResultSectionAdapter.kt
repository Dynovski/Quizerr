package pl.dynovski.quizerr.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.dynovski.quizerr.databinding.ResultSectionItemBinding
import pl.dynovski.quizerr.helpers.ResultSection

class ResultSectionAdapter: RecyclerView.Adapter<ResultSectionAdapter.ViewHolder>() {

    private var resultSections: Array<ResultSection> = arrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val resultSection: ResultSection = resultSections.getOrNull(position) ?: return
        holder.bind(resultSection)
    }

    override fun getItemCount(): Int {
        return resultSections.size
    }

    fun setSections(sections: Array<ResultSection>) {
        resultSections = sections
        notifyDataSetChanged()
    }

    fun updateSection(section: ResultSection, position: Int) {
        resultSections[position] = section
        notifyItemChanged(position)
    }

    class ViewHolder private constructor(binding: ResultSectionItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        private val courseNameTextView: TextView = binding.courseNameTextView
        private val childRecyclerView: RecyclerView = binding.childRecyclerView

        fun bind(section: ResultSection) {
            courseNameTextView.text = section.name
            val childAdapter = ResultsAdapter()
            childAdapter.setResults(section.items)
            childRecyclerView.adapter = childAdapter
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ResultSectionItemBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}