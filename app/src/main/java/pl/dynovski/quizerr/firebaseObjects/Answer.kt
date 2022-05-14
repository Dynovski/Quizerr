package pl.dynovski.quizerr.firebaseObjects

data class Answer(
    var text: String = "",
    var isCorrect: Boolean = false,
    var questionId: String = ""
)