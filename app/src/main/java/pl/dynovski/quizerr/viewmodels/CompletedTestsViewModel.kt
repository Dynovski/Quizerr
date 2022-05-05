package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData

class CompletedTestsViewModel: ViewModel() {

    private val COMPLETED_TESTS_REFERENCE = Firebase.firestore.collection("Users")
        .document(Firebase.auth.currentUser!!.uid)
        .collection("CompletedTests")

    val completedTests = FirebaseQueryLiveData(COMPLETED_TESTS_REFERENCE)
}