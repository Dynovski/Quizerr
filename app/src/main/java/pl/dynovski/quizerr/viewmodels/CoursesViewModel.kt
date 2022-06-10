package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData
import pl.dynovski.quizerr.singletons.LoggedUser

class CoursesViewModel: ViewModel() {

    private val database = Firebase.firestore
    private val currentUser = LoggedUser.get()
    private val coursesRef = database.collection("Courses")

    val allCourses = FirebaseQueryLiveData(coursesRef)
    val currentUserCourses = FirebaseQueryLiveData(
        coursesRef.whereEqualTo("userId", currentUser.userId)
    )
}