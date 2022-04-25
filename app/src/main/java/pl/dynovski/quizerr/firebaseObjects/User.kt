package pl.dynovski.quizerr.firebaseObjects

data class User(
    var privilegeLevel: Int = 0,
    var name: String = "",
    var userId: String = "",
    var email: String = ""
)

