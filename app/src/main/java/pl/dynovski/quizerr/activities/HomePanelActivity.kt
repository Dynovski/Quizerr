package pl.dynovski.quizerr.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.ActivityHomePanelBinding
import pl.dynovski.quizerr.firebaseObjects.Course
import pl.dynovski.quizerr.singletons.LoggedUser

class HomePanelActivity: AppCompatActivity() {
    private val TAG = "HOME_PANEL"

    private lateinit var homePanelBinding: ActivityHomePanelBinding

    // Layout related variables
    private lateinit var allCoursesCardView: CardView
    private lateinit var createCourseCardView: CardView
    private lateinit var myCoursesCardView: CardView
    private lateinit var myTestsCardView: CardView
    private lateinit var createTestCardView: CardView
    private lateinit var myAccountCardView: CardView
    private lateinit var activeTestsCardView: CardView
    private lateinit var resultsCardView: CardView
    private lateinit var signOutCardView: CardView

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homePanelBinding = ActivityHomePanelBinding.inflate(layoutInflater)
        setContentView(homePanelBinding.root)

        auth = Firebase.auth
        database = Firebase.firestore

        allCoursesCardView = homePanelBinding.allCoursesCardView
        createCourseCardView = homePanelBinding.createCourseCardView
        myCoursesCardView = homePanelBinding.myCoursesCardView
        myTestsCardView = homePanelBinding.myTestsCardView
        createTestCardView = homePanelBinding.createTestCardView
        myAccountCardView = homePanelBinding.myAccountCardView
        activeTestsCardView = homePanelBinding.activeTestsCardView
        resultsCardView = homePanelBinding.resultsCardView
        signOutCardView = homePanelBinding.signOutCardView

        allCoursesCardView.setOnClickListener {
            Log.d(TAG, "Selected 'All courses'")
            startActivity(Intent(this, CoursesActivity::class.java))
        }

        val createCourseLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                val course = it.data!!.extras!!.get(MyCoursesActivity.COURSE_KEY) as Course
                database.collection("Courses")
                    .document()
                    .set(course)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            R.string.create_course_success,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            this,
                            R.string.create_course_failed,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            else if (it.resultCode == RESULT_CANCELED)
                Toast.makeText(
                    this, R.string.create_course_canceled,
                    Toast.LENGTH_SHORT
                ).show()
        }

        createCourseCardView.setOnClickListener {
            Log.d(TAG, "Selected 'Create course'")
            createCourseLauncher.launch(Intent(this, CreateCourseActivity::class.java))
        }

        myCoursesCardView.setOnClickListener {
            Log.d(TAG, "Selected 'My courses'")
            startActivity(Intent(this, MyCoursesActivity::class.java))
        }

        myTestsCardView.setOnClickListener {
            Log.d(TAG, "Selected 'My tests'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        createTestCardView.setOnClickListener {
            Log.d(TAG, "Selected 'Create test'")
            startActivity(Intent(this, CreateTestActivity::class.java))
        }

        myAccountCardView.setOnClickListener {
            Log.d(TAG, "Selected 'My account'")
            startActivity(Intent(this, AccountDetailsActivity::class.java))
        }

        activeTestsCardView.setOnClickListener {
            Log.d(TAG, "Selected 'Active tests'")
            startActivity(Intent(this, ActiveTestsActivity::class.java))
        }

        resultsCardView.setOnClickListener {
            Log.d(TAG, "Selected 'Results'")
            startActivity(Intent(this, ResultsActivity::class.java))
        }

        signOutCardView.setOnClickListener {
            Log.d(TAG, "Selected 'Sign out'")
            auth.signOut()
            LoggedUser.logout()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Signed out on HomePanelActivity destroy")
        auth.signOut()
    }
}