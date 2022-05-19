package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData
import pl.dynovski.quizerr.singletons.LoggedUser

class TestsViewModel: ViewModel() {

    private val database = Firebase.firestore
    private val testsRef = database.collection("Tests")

    val allTests = FirebaseQueryLiveData(testsRef)

    fun ongoingTests(courseIds: List<String>): FirebaseQueryLiveData {
        return FirebaseQueryLiveData(
            database.collection("Tests")
                .whereIn("CourseId", courseIds)
                .whereGreaterThan("dueDate", Timestamp(Date()))
        )
    }

    val currentUserTests = FirebaseQueryLiveData(
        testsRef.whereEqualTo("userId", LoggedUser.get().userId)
    )
}