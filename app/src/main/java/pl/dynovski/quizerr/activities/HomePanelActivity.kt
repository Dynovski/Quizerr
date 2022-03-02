package pl.dynovski.quizerr.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import pl.dynovski.quizerr.databinding.ActivityHomePanelBinding

class HomePanelActivity: AppCompatActivity() {
    private val LOG_TAG = "HOME_PANEL"

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        homePanelBinding = ActivityHomePanelBinding.inflate(layoutInflater)
        setContentView(homePanelBinding.root)

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
            Log.d(LOG_TAG, "Selected 'All courses'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        createCourseCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'Create course'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        myCoursesCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'My courses'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        myTestsCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'My tests'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        createTestCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'Create test'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        myAccountCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'My account'")
            startActivity(Intent(this, AccountDetailsActivity::class.java))
        }

        activeTestsCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'Active tests'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        resultsCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'Results'")
            startActivity(Intent(this, SignInActivity::class.java))
        }

        signOutCardView.setOnClickListener {
            Log.d(LOG_TAG, "Selected 'Sign out'")
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, SignInActivity::class.java)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
            )
            startActivity(intent)
            finish()
        }
    }
}