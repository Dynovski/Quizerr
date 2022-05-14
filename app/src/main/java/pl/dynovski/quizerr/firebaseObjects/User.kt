package pl.dynovski.quizerr.firebaseObjects

data class User(
    var name: String = "",
    var email: String = "",
    var userId: String = "",
    var numCreatedCourses: Int = 0,
    var numCreatedTests: Int = 0,
    var subscribedCoursesIds: List<String> = listOf(),
    var completedTestsIds: List<String> = listOf(),
)

