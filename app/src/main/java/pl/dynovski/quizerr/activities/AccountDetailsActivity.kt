package pl.dynovski.quizerr.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.databinding.ActivityAccountDetailsBinding
import pl.dynovski.quizerr.firebaseObjects.User
import pl.dynovski.quizerr.singletons.LoggedUser

class AccountDetailsActivity: AppCompatActivity() {

    private val TAG = "ACCOUNT_DETAILS"

    private lateinit var accountDetailsBinding: ActivityAccountDetailsBinding

    // Layout related variables
    private lateinit var accountNameTextView: TextView
    private lateinit var accountEmailTextView: TextView
    private lateinit var createdCoursesTextView: TextView
    private lateinit var createdTestsTextView: TextView
    private lateinit var deleteAccountButton: Button

    private lateinit var database: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountDetailsBinding = ActivityAccountDetailsBinding.inflate(layoutInflater)
        setContentView(accountDetailsBinding.root)

        database = Firebase.firestore
        auth = Firebase.auth

        accountNameTextView = accountDetailsBinding.accountNameTextView
        accountEmailTextView = accountDetailsBinding.accountEmailTextView
        createdCoursesTextView = accountDetailsBinding.createdCoursesTextView
        createdTestsTextView = accountDetailsBinding.createdTestsTextView
        deleteAccountButton = accountDetailsBinding.deleteButton

        database.collection("Users")
            .document(LoggedUser.get().userId)
            .get()
            .addOnSuccessListener {
                Log.d(TAG, "Loaded user account details")
                val user = it.toObject(User::class.java)!!
                accountNameTextView.text = user.name
                accountEmailTextView.text = user.email
                createdTestsTextView.text = user.numCreatedTests.toString()
                createdCoursesTextView.text = user.numCreatedCourses.toString()
            }
            .addOnFailureListener {
                Log.d(TAG, "Could not load user account details\n$it")
            }

        deleteAccountButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.question_delete_account)
            builder.setPositiveButton(R.string.action_yes) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                deleteAccount(LoggedUser.get().userId)
            }
            builder.setNegativeButton(R.string.action_no) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            builder.create().show()
        }
    }

    private fun deleteAccount(userId: String) {
        auth.currentUser!!.delete()
            .addOnSuccessListener {
                database.collection("Users")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            R.string.action_delete_success,
                            Toast.LENGTH_SHORT
                        ).show()
                        database.collection("Courses")
                            .whereArrayContains("enrolledUsersIds", userId)
                            .get()
                            .addOnSuccessListener {
                                for (document in it.documents) {
                                    database.collection("Courses")
                                        .document(document.id)
                                        .update("enrolledUsersIds", FieldValue.arrayRemove(userId))
                                        .addOnSuccessListener {
                                            Log.d(TAG, "Deleted $userId from enrolled users of ${document.id}")
                                        }
                                }
                            }
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
                    .addOnFailureListener {
                        Log.d(TAG, "Could not delete user from firestore\n$it")
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "Could not delete user from auth\n$it")
                Toast.makeText(
                    this,
                    R.string.action_delete_fail,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}