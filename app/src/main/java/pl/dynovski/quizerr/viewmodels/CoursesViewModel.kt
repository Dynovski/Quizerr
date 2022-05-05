package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData

class CoursesViewModel: ViewModel() {

    private val currentUser = Firebase.auth.currentUser
    private val COURSES_REFERENCE = FirebaseFirestore.getInstance().collection("Courses")
    private val SUBSCRIBED_COURSES_REFERENCE = FirebaseFirestore.getInstance().collection("Users")
        .document(currentUser!!.uid)
        .collection("SubscribedCourses")

    val allCourses = FirebaseQueryLiveData(COURSES_REFERENCE)
    val subscribedCourses = FirebaseQueryLiveData(SUBSCRIBED_COURSES_REFERENCE)
    val myCourses = FirebaseQueryLiveData(COURSES_REFERENCE.whereEqualTo("author", currentUser?.uid))
}