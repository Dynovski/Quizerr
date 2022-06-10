package pl.dynovski.quizerr.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FieldValue
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
import pl.dynovski.quizerr.singletons.LoggedUser

class CreateTestActivity : FragmentActivity() {
    private val TAG = "CREATE_TEST"

    private lateinit var binding: ActivityCreateTestBinding

    // Firebase variables
    private lateinit var database: FirebaseFirestore

    // Layout related variables
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter

    // Variables holding data to save
    var data: MutableMap<Int, Pair<Question, List<Answer>>> = mutableMapOf()
    lateinit var test: Test

    // Variable to manage arrayList operations
    private var questionCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.firestore

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        adapter = ViewPagerAdapter(this)
        val courseId = intent.extras?.getString(MyCoursesActivity.COURSE_ID_KEY) ?: return
        adapter.addFragment(CreateTestBaseFragment(courseId), "Test details")

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
        executeBatchedWrite()
        finish()
    }

    fun moveToNextPage() {
        viewPager.currentItem += 1
    }

    fun moveToPreviousPage() {
        viewPager.currentItem -= 1
    }

    fun numFragments(): Int {
        return adapter.numFragments()
    }

    fun setDataItem(key: Int, question: Question, answers: List<Answer>) {
        data[key] = Pair(question, answers)
    }

    private fun executeBatchedWrite() {
        val batch = database.batch()

        val newTest = database.collection("Tests").document()
        batch[newTest] = test

        val questionsRef = database.collection("Questions")
        val answersRef = database.collection("Answers")

        for (item in data) {
            val newQuestion = questionsRef.document()
            val questionId = newQuestion.id
            val question = item.value.first
            question.testId = newTest.id
            batch[newQuestion] = question
            for (answer in item.value.second) {
                val newAnswer = answersRef.document()
                answer.questionId = questionId
                batch[newAnswer] = answer
            }
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Successfully added new test",
                    Toast.LENGTH_SHORT
                ).show()
                database.collection("Users")
                    .document(LoggedUser.get().userId)
                    .update("numCreatedTests", FieldValue.increment(1))
                    .addOnSuccessListener {
                        Log.d(TAG, "Incremented numCreatedTests")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Could not increment numCreatedTests\n$it")
                    }
            }
            .addOnFailureListener { e: Exception ->
                Toast.makeText(this, "Couldn't add new test", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Adding new test failed\n$e")
            }
    }
}