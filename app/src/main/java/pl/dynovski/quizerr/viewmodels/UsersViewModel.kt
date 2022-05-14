package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData
import pl.dynovski.quizerr.singletons.LoggedUser

class UsersViewModel: ViewModel() {

    private val database = Firebase.firestore
    private val currentUser = LoggedUser.get()
    private val usersRef = database.collection("Users")

    fun getUser(userId: String = currentUser.userId): FirebaseQueryLiveData {
        return FirebaseQueryLiveData(usersRef.whereEqualTo("userId", userId))
    }
}