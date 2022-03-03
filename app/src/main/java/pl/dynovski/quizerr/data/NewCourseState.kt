package pl.dynovski.quizerr.data

data class NewCourseState(
    val nameError: Int? = null,
    val descriptionError: Int? = null,
    val isDataValid: Boolean = false
)