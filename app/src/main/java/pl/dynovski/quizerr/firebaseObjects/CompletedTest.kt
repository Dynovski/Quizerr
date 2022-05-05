package pl.dynovski.quizerr.firebaseObjects

import com.google.firebase.Timestamp
import java.util.*

data class CompletedTest(
    var name: String = "",
    var courseName: String = "",
    var maxScore: Int = 0,
    var score: Int = 0,
    var finishDate: Timestamp = Timestamp(Date())
)
