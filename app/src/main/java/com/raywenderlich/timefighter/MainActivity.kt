package com.raywenderlich.timefighter

import android.content.IntentSender
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal lateinit var tapMeButton: Button
    internal lateinit var GameScoreView: TextView
    internal lateinit var timeLeftTextView: TextView



    internal var score = 0
    internal var gameStarted = false

    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 1000
    internal val countDownInterval: Long = 100000
    internal var timeleftOnTimer: Long = 1000

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY ="SCORE_KEY"
        private const val TIME_LEFT_KEY ="TIME_LEFT_KEY"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate called. Score is: $score")

        tapMeButton = findViewById(R.id.tapMeButton)
        GameScoreView = findViewById(R.id.GameScoreView)
        timeLeftTextView = findViewById(R.id.timeLeftTextView)

        tapMeButton.setOnClickListener { view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
            GameScoreView.text = getString(R.string.yourScore, score)

        }
        if(savedInstanceState != null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeleftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        } else {
            resetGame()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
         super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.actionAbout) {
            showInfo()
        }
        return true
    }
        private fun showInfo(){
            val dialogMessage = getString(R.string.aboutMessage)

            val builder = AlertDialog.Builder(this)
            builder.setMessage(dialogMessage)
            builder.create().show()
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeleftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving Score: $score & Time Left: $timeleftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun resetGame(){
        //Reset Score
        score = 0

        GameScoreView.text = getString(R.string.yourScore, score)
        val initialTimeLeft = initialCountDown / 10000
        timeLeftTextView.text =getString(R.string.timeLeft, initialTimeLeft)

        countDownTimer = object : CountDownTimer(initialCountDown, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                timeleftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 10000
                timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }
        gameStarted = false
    }

private fun restoreGame() {
    GameScoreView.text = getString(R.string.yourScore, score )
    val restoredTime = timeleftOnTimer / 10000
    timeLeftTextView.text = getString(R.string.timeLeft, restoredTime)

    countDownTimer = object : CountDownTimer(timeleftOnTimer, countDownInterval) {
        override fun onTick(millisUnitilFinished: Long) {
            timeleftOnTimer = millisUnitilFinished
            val timeLeft = millisUnitilFinished / 10000
            timeLeftTextView.text = getString(R.string.timeLeft, timeLeft)
        }

        override fun onFinish() {
            endGame()
        }
    }
    countDownTimer.start()
    gameStarted = true
}

    private fun incrementScore() {
        if(!gameStarted){
            startGame()
        }

        score += 1
        val newScore = getString(R.string.yourScore, score)
        GameScoreView.text = newScore

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        GameScoreView.startAnimation(blinkAnimation)
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }
    private fun endGame() {
        Toast.makeText(this, getString(R.string.gameOverMessage), Toast.LENGTH_LONG).show()
        resetGame()
    }

}