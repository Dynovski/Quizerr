package pl.dynovski.quizerr.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity: SignActionActivity() {
    private val LOG_TAG = "RESET_PASSWORD"

    private lateinit var resetPasswordBinding: ActivityResetPasswordBinding

    // Layout related variables
    private lateinit var emailEditText: EditText

    // Firebase variables
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        resetPasswordBinding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(resetPasswordBinding.root)

        emailEditText = resetPasswordBinding.emailEditText
        progressBar = resetPasswordBinding.progressBar
        auth = Firebase.auth

        resetPasswordBinding.resetPasswordButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val email = emailEditText.text.toString().trim()
        Log.d(LOG_TAG, "reset password for $email")

        showProgressBar()
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task: Task<Void?> ->
                if (task.isSuccessful) {
                    Log.d(LOG_TAG, "Reset password email sent")
                    Toast.makeText(
                        applicationContext, "Reset password email sent",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(RESULT_OK)
                    finish()
                    startActivity(Intent(this, SignInActivity::class.java))
                } else {
                    Log.w(LOG_TAG, "Reset email creation failed", task.exception)
                    Toast.makeText(
                        applicationContext, "Couldn't reset password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                hideProgressBar()
            }
    }
}