package pl.dynovski.quizerr.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import pl.dynovski.quizerr.firebaseObjects.FirebaseQueryLiveData

class CoursesViewModel: ViewModel() {

    private val COURSES_REFERENCE = FirebaseFirestore.getInstance().collection("Courses")
    private val SUBSCRIBED_COURSES_REFERENCE = FirebaseFirestore.getInstance().collection("Users")
        .document(FirebaseAuth.getInstance().currentUser!!.uid)
        .collection("SubscribedCourses")

    val allCourses = FirebaseQueryLiveData(COURSES_REFERENCE)
    val subscribedCourses = FirebaseQueryLiveData(SUBSCRIBED_COURSES_REFERENCE)
}