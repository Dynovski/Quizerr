package pl.dynovski.quizerr.activities

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.adapters.ViewPagerAdapter
import pl.dynovski.quizerr.databinding.ActivityCreateTestBinding
import pl.dynovski.quizerr.firebaseObjects.Answer
import pl.dynovski.quizerr.firebaseObjects.Question
import pl.dynovski.quizerr.firebaseObjects.Test
import pl.dynovski.quizerr.firebaseObjects.TestResult
import pl.dynovski.quizerr.fragments.SolveTestBaseFragment
import pl.dynovski.quizerr.fragments.SolveTestQuestionFragment
import pl.dynovski.quizerr.fragments.TestResultFragment
import java.util.*

class SolveTestActivity: FragmentActivity() {
    private val TAG = "SOLVE_TEST"

    private lateinit var binding: ActivityCreateTestBinding
    // Firebase variables
    private lateinit var database: FirebaseFirestore

    // Layout related variables
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter

    lateinit var data: MutableMap<Int, Pair<Question, List<Answer>>>
    lateinit var test: Test
    lateinit var testId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        test = intent.extras!!.get(TEST_KEY) as Test
        testId = intent.getStringExtra(TEST_ID_KEY)!!

        getTestData()

        binding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.firestore

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        adapter = ViewPagerAdapter(this)
        adapter.addFragment(SolveTestBaseFragment(test), "Test information")
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }
    }

    private fun getTestData() {
        database.collection("Questions")
            .whereEqualTo("test_id", testId)
            .get()
            .addOnSuccessListener {
                val questionData = it.documents.map { it.id to it.toObject(Question::class.java)!! }
                val storage: MutableMap<Int, Pair<Question, List<Answer>>> = mutableMapOf()
                database.collection("Answers")
                    .whereIn("question_id", questionData.map { it.first })
                    .get()
                    .addOnSuccessListener {
                        val answers = it.documents.map { it.toObject(Answer::class.java)!! }
                        val answersByQuestionId = answers.groupBy { it.questionId }
                        for ((index, question) in questionData.withIndex()) {
                            storage[index] = Pair(
                                question.second,
                                answersByQuestionId[question.first]!!
                            )
                        }
                        data = storage
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Failed to load questions")
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to load test")
            }
    }

    fun checkTest() {
        var score = 0
        for (i in 0 until adapter.itemCount) {
            if ((adapter.getItem(i) as SolveTestQuestionFragment).areAnswersCorrect())
                score += 1
        }
        val result = TestResult(
            test.name,
            test.numQuestions,
            score,
            Timestamp(Date()),
            testId,
            test.courseId,
            test.userId
        )
        database.collection("TestResults")
            .document()
            .set(result)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully added completed test")
                addResultFragment(result)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to save completed test")
            }
    }

    fun addQuestionFragments() {
        adapter.removeAllFragments()
        for (i in 0 until data.size) {
            val question = data[i]!!.first
            val answers = data[i]!!.second
            adapter.addFragment(SolveTestQuestionFragment(question, answers), "Question ${i + 1}")
        }
        adapter.notifyItemRangeInserted(1, data.size)
    }

    private fun addResultFragment(testResult: TestResult) {
        adapter.removeAllFragments()
        adapter.addFragment(TestResultFragment(testResult), "TestResult")
        adapter.notifyItemInserted(0)
    }

    fun moveToNextPage() {
        viewPager.currentItem += 1
    }

    fun moveToPreviousPage() {
        viewPager.currentItem -= 1
    }

    companion object {
        const val TEST_KEY = "TEST_KEY"
        const val TEST_ID_KEY = "TEST_ID_KEY"
    }
}