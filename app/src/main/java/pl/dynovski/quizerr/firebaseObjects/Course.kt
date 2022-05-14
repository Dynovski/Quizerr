package pl.dynovski.quizerr.firebaseObjects

import java.io.Serializable

data class Course(
    val name: String = "",
    val author: String = "",
    val description: String = "",
    val userId: String = "",
    val enrolledUsersIds: List<String> = listOf()
): Serializable