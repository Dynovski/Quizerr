package pl.dynovski.quizerr.firebaseObjects

// Conversion class for users from database to kotlin code and the other way
data class User(
    var privilegeLevel: Int = 0,
    var name: String = "",
    var userId: String = "",
    var email: String = ""
)

