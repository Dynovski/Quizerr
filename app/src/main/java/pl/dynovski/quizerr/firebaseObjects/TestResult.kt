package pl.dynovski.quizerr.firebaseObjects

import com.google.firebase.Timestamp
import java.util.*

data class TestResult(
    var testName: String = "",
    var maxScore: Int = 0,
    var score: Int = 0,
    var finishedDate: Timestamp = Timestamp(Date()),
    var testId: String = "",
    var courseId: String = "",
    var userId: String = ""
)
