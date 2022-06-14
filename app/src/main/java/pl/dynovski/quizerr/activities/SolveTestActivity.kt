package pl.dynovski.quizerr.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.dynovski.quizerr.R
import pl.dynovski.quizerr.adapters.ViewPagerAdapter
import pl.dynovski.quizerr.databinding.ActivityTestViewPagerBinding
import pl.dynovski.quizerr.firebaseObjects.Answer
import pl.dynovski.quizerr.firebaseObjects.Question
import pl.dynovski.quizerr.firebaseObjects.TestResult
import pl.dynovski.quizerr.fragments.SolveTestBaseFragment
import pl.dynovski.quizerr.fragments.SolveTestQuestionFragment
import pl.dynovski.quizerr.fragments.TestResultFragment
import pl.dynovski.quizerr.singletons.LoggedUser
import pl.dynovski.quizerr.viewmodels.TimerViewModel
import java.util.*

class SolveTestActivity: FragmentActivity() {
    private val TAG = "SOLVE_TEST"

    private lateinit var binding: ActivityTestViewPagerBinding
    // Firebase variables
    private lateinit var database: FirebaseFirestore

    // Layout related variables
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var adapter: ViewPagerAdapter

    private lateinit var data: MutableMap<Int, Pair<Question, List<Answer>>>
    private lateinit var testName: String
    private lateinit var testId: String
    private lateinit var courseId: String
    private lateinit var dueDate: Timestamp
    private var numOfQuestions: Int = 0

    lateinit var timerViewModel: TimerViewModel
    private lateinit var testTimer: CountDownTimer

    private lateinit var newTestResult: DocumentReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timerViewModel = ViewModelProvider(this)[TimerViewModel::class.java]
        database = Firebase.firestore

        testName = intent.getStringExtra(TEST_NAME_KEY)!!
        numOfQuestions = intent.getIntExtra(TEST_QUESTIONS_KEY, 0)
        dueDate = intent.getParcelableExtra<Timestamp>(TEST_DATE_KEY) as Timestamp
        testId = intent.getStringExtra(TEST_ID_KEY)!!
        courseId = intent.getStringExtra(TEST_COURSE_ID_KEY)!!

        getTestData()

        binding = ActivityTestViewPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = Firebase.firestore

        tabLayout = binding.tabLayout
        viewPager = binding.viewPager
        adapter = ViewPagerAdapter(this)
        adapter.addFragment(SolveTestBaseFragment(testName, dueDate, numOfQuestions), "Test information")
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

        testTimer = object: CountDownTimer(40000L * numOfQuestions, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerViewModel.timeChanged(millisUntilFinished)
            }

            override fun onFinish() {
                val lastQuestionFragment = adapter.getItem(adapter.itemCount - 1) as SolveTestQuestionFragment
                lastQuestionFragment.hideFinishButton()
                checkTest()
            }
        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.question_finish_test)
        builder.setPositiveButton(R.string.action_yes) { dialog: DialogInterface, _: Int ->
            super.onBackPressed()
        }
        builder.setNegativeButton(R.string.action_no) { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        testTimer.cancel()
    }

    private fun getTestData() {
        database.collection("Questions")
            .whereEqualTo("testId", testId)
            .get()
            .addOnSuccessListener {
                val questionData = it.documents.map { it.id to it.toObject(Question::class.java)!! }
                val storage: MutableMap<Int, Pair<Question, List<Answer>>> = mutableMapOf()
                database.collection("Answers")
                    .whereIn("questionId", questionData.map { it.first })
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

    private fun addResultFragment(testResult: TestResult) {
        adapter.addFragment(TestResultFragment(testResult), "Results")
        adapter.notifyDataSetChanged()
    }

    fun setInitialResult() {
        val result = TestResult(
            testName,
            numOfQuestions,
            0,
            Timestamp(Date()),
            testId,
            courseId,
            LoggedUser.get().userId
        )

        newTestResult = database.collection("TestResults").document()

        newTestResult
            .set(result)
            .addOnSuccessListener {
                Log.d(TAG, "Added initial test result")
                database.collection("Users")
                    .document(LoggedUser.get().userId)
                    .update("completedTestsIds", FieldValue.arrayUnion(testId))
                    .addOnSuccessListener {
                        Log.d(TAG, "Added test id to completedTestsIds")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Could not add test id to completedTestsIds\n$it")
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to add initial test result")
            }
    }

    fun checkTest() {
        var score = 0
        for (i in 1 until adapter.itemCount) {
            if ((adapter.getItem(i) as SolveTestQuestionFragment).areAnswersCorrect())
                score += 1
        }
        val result = TestResult(
            testName,
            numOfQuestions,
            score,
            Timestamp(Date()),
            testId,
            courseId,
            LoggedUser.get().userId
        )
        database.collection("TestResults")
            .document(newTestResult.id)
            .set(result)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully added completed test")
                addResultFragment(result)
                moveToLastPage()
                database.collection("Users")
                    .document(LoggedUser.get().userId)
                    .update("completedTestsIds", FieldValue.arrayUnion(testId))
                    .addOnSuccessListener {
                        Log.d(TAG, "Added test id to completedTestsIds")
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "Could not add test id to completedTestsIds\n$it")
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to save completed test")
            }
    }

    fun addQuestionFragments() {
        for (i in 0 until data.size) {
            val question = data[i]!!.first
            val answers = data[i]!!.second
            adapter.addFragment(SolveTestQuestionFragment(question, answers, i), "Question ${i + 1}")
        }
        adapter.notifyDataSetChanged()
    }

    fun getNumQuestions(): Int {
        return data.size
    }

    fun getNumFragments(): Int {
        return adapter.numFragments()
    }

    fun startTimer() {
        testTimer.start()
    }

    fun moveToNextPage() {
        viewPager.currentItem += 1
    }

    fun moveToLastPage() {
        viewPager.currentItem = adapter.itemCount - 1
    }

    fun invalidateTimer() {
        testTimer.cancel()
    }

    companion object {
        const val TEST_NAME_KEY = "TEST_NAME_KEY"
        const val TEST_QUESTIONS_KEY = "TEST_QUESTIONS_KEY"
        const val TEST_DATE_KEY = "TEST_DATE_KEY"
        const val TEST_ID_KEY = "TEST_ID_KEY"
        const val TEST_USER_ID_KEY = "TEST_USER_ID_KEY"
        const val TEST_COURSE_ID_KEY = "TEST_COURSE_ID_KEY"
    }
}