package io.kaendagger.bamquiz

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.kaendagger.bamquiz.data.QuizApiClient
import io.kaendagger.bamquiz.data.model.Problem
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates

class QuizActivity : AppCompatActivity(), CoroutineScope, View.OnClickListener {

    private var score = 0
    private var qIdx = 0
    private val userMarkedOptions = mutableListOf<String>()
    private var currCorrectOptions = emptyList<String>()
    private var questions = emptyList<Problem>()


    private var btnAState: Boolean by Delegates.observable(false) { _, _, newValue ->
        applyBtnState(newValue, btnA, "1")
    }

    private var btnBState: Boolean by Delegates.observable(false) { _, _, newValue ->
        applyBtnState(newValue, btnB, "2")
    }

    private var btnCState: Boolean by Delegates.observable(false) { _, _, newValue ->
        applyBtnState(newValue, btnC, "3")
    }

    private var btnDState: Boolean by Delegates.observable(false) { _, _, newValue ->
        applyBtnState(newValue, btnD, "4")
    }

    private fun applyBtnState(state: Boolean, btn: Button, value: String) {
        if (state) {
            btn.apply {
                setBackgroundColor(resources.getColor(R.color.colorAccent))
                setTextColor(Color.WHITE)
            }
            userMarkedOptions.add(value)
        } else {
            btn.apply {
                setBackgroundColor(Color.WHITE)
                setTextColor(resources.getColor(R.color.colorPrimary))
            }
            if (value in userMarkedOptions) {
                userMarkedOptions.remove(value)
            }
        }

        Log.d("PUI","""
            user $userMarkedOptions
            correct $currCorrectOptions
        """.trimIndent())
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnA -> {
                btnAState = !btnAState
                btnNextQ.isVisible = true
            }
            R.id.btnB -> {
                btnBState= !btnBState
                btnNextQ.isVisible = true

            }
            R.id.btnC -> {
                btnCState= !btnCState
                btnNextQ.isVisible = true
            }
            R.id.btnD -> {
                btnDState = !btnDState
                btnNextQ.isVisible = true
            }
            R.id.btnNextQ -> {
                if (checkCorrectness()) {
                    score++
                }
                userMarkedOptions.clear()
                resetBtns()
                qIdx++
                if (qIdx < questions.size) {
                    val problem = questions[qIdx]
                    displayThisProblem(problem)
                } else {
                    val intent = Intent(this,ScoreActivity::class.java).apply {
                        putExtra("score",score)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
                Log.d("PUI","score $score")
            }
        }
    }

    private fun resetBtns() {
        btnAState = false
        btnBState = false
        btnCState = false
        btnDState = false
        btnNextQ.isVisible = false
    }

    private fun checkCorrectness(): Boolean {

        if (currCorrectOptions.size != userMarkedOptions.size){
            return false
        }

        currCorrectOptions.forEach {
            if (it !in userMarkedOptions)
                return false
        }
        return true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private val DEF_URL = "http://nk521.pythonanywhere.com"
    private val IMAGE_BASE_URL =
        "http://nk521.pythonanywhere.com/download/%252Fhome%252Fnk521%252FQuiz_api%252Fimg%252F"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        val serverUrl = intent.getStringExtra("ServerURL") ?: DEF_URL
        val quizService = QuizApiClient(serverUrl).quizeService

        val btnArray = arrayOf(btnA, btnB, btnC, btnD, btnNextQ)
        for (btn in btnArray) {
            btn.setOnClickListener(this)
        }

        quesLoad.isVisible = true
        quesContainer.isVisible = false
        launch(Dispatchers.IO) {
            val response = quizService.getQuestions()
            if (response.isSuccessful) {
                questions = response.body() ?: emptyList()
                val problem = questions[qIdx]
                displayThisProblem(problem)
            } else {
                Log.d("PUI", "${response.errorBody()}")
            }
        }
    }

    private fun displayThisProblem(problem: Problem) {
        launch {
            quesLoad.isVisible = false
            quesContainer.isVisible = true
            tvQuestion.text = problem.ques
            btnA.text = problem.option_a
            btnB.text = problem.option_b
            btnC.text = problem.option_c
            btnD.text = problem.option_d
            currCorrectOptions = problem.correct.split(",")
            if (problem.ques_img != null) {
                imLoad.isVisible = true
                imContainer.isVisible = true
                Picasso.get().load("${IMAGE_BASE_URL}${problem.ques_img}")
                    .into(ivQues, object : Callback {
                        override fun onSuccess() {
                            imLoad.isVisible = false
                        }

                        override fun onError(e: Exception?) {
                        }
                    })
            } else {
                imLoad.isVisible = false
                imContainer.isVisible = false
                imContainer.invalidate()
                imContainer.requestLayout()
            }
        }
    }


    override fun onDestroy() {
        coroutineContext.cancelChildren()
        super.onDestroy()
    }
}
