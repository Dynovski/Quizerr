package pl.dynovski.quizerr.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.adapters.ViewPagerAdapter
import pl.dynovski.quizerr.databinding.ActivityCreateTestBinding
import pl.dynovski.quizerr.firebaseObjects.Answer
import pl.dynovski.quizerr.firebaseObjects.Question
import pl.dynovski.quizerr.firebaseObjects.Test
import pl.dynovski.quizerr.fragments.CreateTestBaseFragment
import pl.dynovski.quizerr.fragments.CreateTestQuestionFragment

class CreateTestActivity : FragmentActivity() {
    private val TAG = "CREATE_TEST"

    private lateinit var createTestBinding: ActivityCreateTestBinding

    // Firebase variables
    private lateinit var database: FirebaseFirestore
    private lateinit var testsRef: CollectionReference

    // Layout related variables
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter

    // Variables holding data to save
    var data: MutableMap<Question, List<Answer>> = mutableMapOf()
    lateinit var test: Test

    // Variable to manage arrayList operations
    private var questionCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createTestBinding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(createTestBinding.root)

        database = Firebase.firestore

        tabLayout = createTestBinding.tabLayout
        viewPager = createTestBinding.viewPager
        adapter = ViewPagerAdapter(this)
        val courseName = intent.extras?.getString("courseName") ?: ""
        adapter.addFragment(CreateTestBaseFragment(courseName), "Test details")

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()
    }

    fun addQuestionFragment() {
        adapter.addFragment(CreateTestQuestionFragment(questionCount++), "Question $questionCount")
        adapter.notifyItemInserted(questionCount + 1)
    }

    fun saveTest() {
        executeBatchedWrite(applicationContext)
        finish()
    }

    fun moveToNextPage() {
        viewPager.currentItem += 1
    }

    fun moveToPreviousPage() {
        viewPager.currentItem -= 1
    }

    fun addDataItem(key: Question, value: List<Answer>) {
        data[key] = value
    }

    private fun executeBatchedWrite(context: Context) {
        val batch = database.batch()

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "Successfully added new test",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(context, "Couldn't add new test", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Adding new test failed\n$e")
            }
    }
}