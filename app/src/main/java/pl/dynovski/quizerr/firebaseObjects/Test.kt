package pl.dynovski.quizerr.firebaseObjects

import com.google.firebase.Timestamp
import java.util.*

data class Test(
    var testName: String = "",
    var courseName: String = "",
    var numQuestions: Int = 0,
    var dueDate: Timestamp = Timestamp(Date())
)