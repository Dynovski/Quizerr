package pl.dynovski.quizerr.firebaseObjects

import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*

data class Test(
    var name: String = "",
    var numQuestions: Int = 0,
    var dueDate: Timestamp = Timestamp(Date()),
    var userId: String = "",
    var courseId: String = "",
)