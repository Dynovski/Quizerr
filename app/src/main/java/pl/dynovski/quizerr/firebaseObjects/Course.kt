package pl.dynovski.quizerr.firebaseObjects

import java.io.Serializable

data class Course(
    var name: String = "",
    val author: String = "",
    var description: String = "",
    val userId: String = "",
    val enrolledUsersIds: List<String> = listOf()
): Serializable