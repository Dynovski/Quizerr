package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData

class TestResultsViewModel: ViewModel() {

    private val testResultsRef = Firebase.firestore.collection("TestResults")

    fun getTestResults(userId: String): FirebaseQueryLiveData {
        return FirebaseQueryLiveData(testResultsRef.whereEqualTo("userId", userId))
    }
}