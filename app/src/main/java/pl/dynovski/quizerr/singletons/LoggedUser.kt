package pl.dynovski.quizerr.singletons

import pl.dynovski.quizerr.firebaseObjects.User

object LoggedUser {

    private var user: User? = null

    fun get(): User {
        return user!!
    }

    fun login(user: User) {
        this.user = this.user ?: user
    }

    fun logout() {
        user = null
    }
}