package pl.dynovski.quizerr.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.FragmentCourseDetailsDialogBinding
import pl.dynovski.quizerr.firebaseObjects.Course

class CourseDetailsDialogFragment(val course: Course): DialogFragment() {

    private lateinit var binding: FragmentCourseDetailsDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = FragmentCourseDetailsDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireActivity())

        builder.setView(binding.root)

        binding.courseNameTextView.text = course.name
        binding.courseAuthorTextView.text = course.author
        binding.courseDescriptionTextView.text = course.description

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.main_rounded_background);

        return dialog
    }
}