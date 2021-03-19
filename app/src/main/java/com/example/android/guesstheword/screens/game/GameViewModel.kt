package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel


class GameViewModel : ViewModel() {

    // this companion object contains values to help run the timer
    // note sure why the "companion object" was needed, since previously we've created values like these??
    companion object {
        // These represent different important times
        // This is when the game is over
        const val DONE = 0L
        // This is the number of milliseconds in a second
        const val ONE_SECOND = 1000L
        // This is the total time of the game
        const val COUNTDOWN_TIME = 10000L
    }

    // create timer
    private val timer: CountDownTimer

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime
    val currentTimeString = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }



    // The current word
    // note that at first, the score was just a normal var, before converting it to LiveData
//    var word = ""

    // note that LiveData objects need to be val, not var
//    val word = MutableLiveData<String>()

    // also note that originally we just used the "val word = MutableLiveData ... " code below
    // but then the lesson recommended "encapsulating" LiveData so that the
    // LiveData that's externally exposed to the UI controller (GameFragment.kt in this case)
    // was not Mutable, since allowing for LiveData to just be changed anywhere can introduce bugs
    // it's not strictly necessary, but the safety fix was to make a MutableLiveData for use
    // in GameViewModel.kt, but a non-mutable "LiveData" exposed to the UI controller
    private val _word = MutableLiveData<String>()
    val word : LiveData<String>
        get() = _word

    // The current score
    // note that at first, the score was just a normal var, before converting it to LiveData
//    var score = 0//

    // note that LiveData objects need to be val, not var
    //    val score = MutableLiveData<Int>()

    // also note that originally we just used the "val score = MutableLiveData ... " code below
    // but then the lesson recommended "encapsulating" LiveData so that the
    // LiveData that's externally exposed to the UI controller (GameFragment.kt in this case)
    // was not Mutable, since allowing for LiveData to just be changed anywhere can introduce bugs
    // it's not strictly necessary, but the safety fix was to make a MutableLiveData for use
    // in GameViewModel.kt, but a non-mutable "LiveData" exposed to the UI controller
    // so, the exposed non-private score LiveData will serve up the internal-only _score
    // and since _score is private to GameViewModel, it can't be changed by GameFragment
    private val _score = MutableLiveData<Int>()
    val score : LiveData<Int>
        get() = _score


    // create event livedata for game being finished
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish : LiveData<Boolean>
        get() = _eventGameFinish


    // The list of words - the front of the list is the next word to guess
    private lateinit var wordList: MutableList<String>

    init {
        Log.i("GameViewModel", "GameViewModel created")
        resetList()
        nextWord()
//        score.value = 0
        _score.value = 0
        _eventGameFinish.value = false

        // create countdown timer
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = (millisUntilFinished / ONE_SECOND)
            }

            override fun onFinish() {
                _currentTime.value = DONE
                _eventGameFinish.value = true
            }
        }

        // start timer
        timer.start()
    }

    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
            "queen",
            "hospital",
            "basketball",
            "cat",
            "change",
            "snail",
            "soup",
            "calendar",
            "sad",
            "desk",
            "guitar",
            "home",
            "railway",
            "zebra",
            "jelly",
            "car",
            "crow",
            "trade",
            "bag",
            "roll",
            "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     */
    private fun nextWord() {
        //Select and remove a word from the list
        if (wordList.isEmpty()) {
            resetList()
        }
        _word.value = wordList.removeAt(0)


        // this was the original code, before adding the timer to determine when game is finished
        //Select and remove a word from the list
//        if (wordList.isEmpty()) {
//            //gameFinished()
//            _eventGameFinish.value = true
//        } else {
////            word = wordList.removeAt(0)
////            word.value = wordList.removeAt(0)
//            _word.value = wordList.removeAt(0)
//        }
    }

    /** Methods for buttons presses **/
    fun onSkip() {

        // before updating to use LiveData, score was just a basic var
//        score = score -1

        // note that i want to just run code below to minus 1, but it errors bc score can
        // be null, which means it's supposedly safer to run with null check (see below)
        // score.value = score.value - 1

        // update score with null check instead
//        score.value = (score.value)?.minus(1)
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
//        score = score - 1
        //score.value = score.value + 1
//        score.value = (score.value)?.plus(1)
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    // this just makes sure the timer is canceled when the app is cleared, to avoid memory leaks
    override fun onCleared() {
        super.onCleared()
        Log.i("GameViewModel", "GameViewModel destroyed")
        timer.cancel()
    }
}