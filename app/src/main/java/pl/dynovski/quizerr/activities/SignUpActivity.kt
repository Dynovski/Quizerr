package pl.dynovski.quizerr.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.ActivitySignUpBinding
import pl.dynovski.quizerr.extensions.afterTextChanged
import pl.dynovski.quizerr.firebaseObjects.User
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.LoginViewModel

class SignUpActivity: SignActionActivity() {
    private val LOG_TAG = "SIGN_UP"

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var signUpBinding: ActivitySignUpBinding

    // Layout related variables
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signUpButton: Button

    // Firebase instance variables
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore

    private var canRegister = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        signUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(signUpBinding.root)

        // Connecting layout variables with code
        emailEditText = signUpBinding.emailEditText
        passwordEditText = signUpBinding.passwordEditText
        progressBar = signUpBinding.progressBar
        signUpButton = signUpBinding.signUpButton

        // Get Firebase instances
        auth = Firebase.auth
        database = Firebase.firestore

        // Adding actions to clickable elements
        signUpButton.setOnClickListener {
            if (canRegister) {
                hideKeyboard(signUpBinding.root)
                createAccount()
            }
        }

        signUpButton.alpha = 0.7f

        signUpBinding.loginTextView.setOnClickListener {
            finish()
            startActivity(Intent(this, SignInActivity::class.java))
        }

        // Making UI responsive to user input
        loginViewModel.loginFormState.observe(this@SignUpActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            canRegister = loginState.isDataValid
            signUpButton.alpha = if (canRegister) 1.0f else 0.7f

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
                        hideKeyboard(signUpBinding.root)
                        createAccount()
                    }
                }
                false
            }
        }
    }

    private fun createAccount() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        Log.d(LOG_TAG, "createAccount for $email")

        showProgressBar()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    Log.d(LOG_TAG, "User for $email successfully created")

                    val userId = auth.currentUser!!.uid
                    val defaultName = email.substringBefore("@")
                    val newUser = User(defaultName, email, userId)
                    LoggedUser.login(newUser)
                    database.collection("Users").document(userId).set(newUser)
                    finish()
                    startActivity(Intent(this, HomePanelActivity::class.java))
                } else {
                    Log.w(LOG_TAG, "User creation for $email failed", task.exception)
                    if (task.exception is FirebaseAuthUserCollisionException)
                        Toast.makeText(
                            applicationContext, R.string.error_already_registered,
                            Toast.LENGTH_SHORT
                        ).show()
                    else
                        Toast.makeText(
                            applicationContext, R.string.error_sign_up,
                            Toast.LENGTH_SHORT
                        ).show()
                }
                hideProgressBar()
            }
    }
}