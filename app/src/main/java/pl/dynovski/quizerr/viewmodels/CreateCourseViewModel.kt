package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.data.NewCourseState

class CreateCourseViewModel: ViewModel() {

    private val _newCourseForm = MutableLiveData<NewCourseState>()
    val newCourseFormState: LiveData<NewCourseState> = _newCourseForm

    fun dataChanged(courseName: String, courseDescription: String) {
        if (!isNameEntered(courseName))
            _newCourseForm.value = NewCourseState(
                nameError = R.string.error_new_course_no_name
            )
        else if (!isDescriptionEntered(courseDescription))
            _newCourseForm.value = NewCourseState(
                descriptionError = R.string.error_new_course_no_description
            )
        else
            _newCourseForm.value = NewCourseState(isDataValid = true)
    }

    private fun isNameEntered(courseName: String): Boolean {
        return courseName.isNotBlank()
    }

    private fun isDescriptionEntered(courseDescription: String): Boolean {
        return courseDescription.isNotBlank()
    }
}