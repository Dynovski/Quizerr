package pl.dynovski.quizerr.activities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.databinding.ActivityAccountDetailsBinding

class AccountDetailsActivity : AppCompatActivity() {

    private lateinit var accountDetailsBinding: ActivityAccountDetailsBinding

    // Layout related variables
    private lateinit var accountNameTextView: TextView
    private lateinit var accountEmailTextView: TextView

    // Firebase related variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountDetailsBinding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(accountDetailsBinding.root)

        auth = Firebase.auth

        accountNameTextView = accountDetailsBinding.accountNameTextView
        accountEmailTextView = accountDetailsBinding.accountEmailTextView

        accountNameTextView.text = auth.currentUser?.displayName
        accountEmailTextView.text = auth.currentUser?.email
    }
}