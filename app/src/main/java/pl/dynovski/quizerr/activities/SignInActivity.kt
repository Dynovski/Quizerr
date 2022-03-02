package pl.dynovski.quizerr.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.ActivitySignInBinding
import pl.dynovski.quizerr.extensions.afterTextChanged
import pl.dynovski.quizerr.viewmodels.LoginViewModel

class SignInActivity: SignActionActivity() {
    private val LOG_TAG = "SIGN_IN"

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var signInBinding: ActivitySignInBinding

    // Layout related variables
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signInButton: Button

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)

        // Connecting layout variables with code
        emailEditText = signInBinding.emailEditText
        passwordEditText = signInBinding.passwordEditText
        progressBar = signInBinding.progressBar
        signInButton = signInBinding.signInButton

        // Get Firebase instances
        auth = Firebase.auth
        database = Firebase.firestore

        // Adding actions to clickable elements
        signInButton.setOnClickListener {
            hideKeyboard(signInBinding.root)
            signIn()
        }
        signInBinding.registerTextView.setOnClickListener {
            finish()
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Making UI responsive to user input
        loginViewModel.loginFormState.observe(this@SignInActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            signInButton.isEnabled = loginState.isDataValid

            if (loginState.emailError != null) {
                emailEditText.error = getString(loginState.emailError)
            }
            if (loginState.passwordError != null) {
                passwordEditText.error = getString(loginState.passwordError)
            }
        })

        emailEditText.afterTextChanged {
            loginViewModel.loginDataChanged(
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        passwordEditText.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    emailEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        hideKeyboard(signInBinding.root)
                        signIn()
                    }
                }
                false
            }
        }

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK)
                Toast.makeText(
                    this, R.string.error_forgot_password_email_pending,
                    Toast.LENGTH_SHORT
                ).show();
            else if (it.resultCode == RESULT_CANCELED)
                Toast.makeText(
                    this, R.string.error_forgot_password_cancelled,
                    Toast.LENGTH_SHORT
                ).show();
        }

        signInBinding.forgotPasswordTextView.setOnClickListener {
            resultLauncher.launch(Intent(this, ResetPasswordActivity::class.java))
        }
    }

    private fun signIn() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        Log.d(LOG_TAG, "login of $email")

        showProgressBar()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Log.d(LOG_TAG, "$email successfully logged in")
                    startActivity(Intent(this, HomePanelActivity::class.java))
                    finish()
                } else {
                    Log.d(LOG_TAG, "$email login failed", task.exception)
                    Toast.makeText(
                        applicationContext, R.string.error_sign_in,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressBar()
            }
    }
}