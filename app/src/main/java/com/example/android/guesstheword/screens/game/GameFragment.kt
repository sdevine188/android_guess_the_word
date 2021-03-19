/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.guesstheword.screens.game

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.GameFragmentBinding
import java.util.EnumSet.of

/**
 * Fragment where the game is played
 */
class GameFragment : Fragment() {

    // create a var called GameViewModel, which is of the class viewModel
    // note we link this var to the GameViewModel code below using ViewModelProvider
    private lateinit var viewModel: GameViewModel

    private lateinit var binding: GameFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.game_fragment,
                container,
                false
        )

        // link the var GameViewModel to the actual GameViewModel code
        Log.i("GameFragment", "called ViewModelProvider")
        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        // pass the GameViewModel (saved as viewModel) into the binding.gameViewModel data slot
        // so that the GameViewModel values can directly update
        // game_fragment.xml, instead of being passed via GameFragment.kt like a middle man
        // this lets me get rid of some onClickListeners (see below)
        binding.gameViewModel = viewModel

        // note that bindings will always refer to the layout.xml id's using camelcase
        // even though (if) the id's actually specified in the layout.xml are snake case
        // it's not clear whether you get compiler errors if you deviate from this pattern
        // of snake case in layout .xml and camel case in binding
        // but it seems to be that the bindings insists in someway on camelcase, so go with it
        // https://stackoverflow.com/questions/1832290/android-id-naming-convention-lower-case-with-underscore-vs-camel-case

        // originally, these onClick listeners called viewModel's onCorrect/onSkip functions
        // which updated the score value, which then triggered the viewModel.score observer below to
        // updated the binding.scoreText.text
        // but then the tutorial said it was better to directly add the viewModel to the binding
        // and place the onclick listeners as a lambda function right in the game_fragment.xml layout
        // note though that the onClick listener in the game_fragment.xml layout only calls
        // onCorrect()/onSkip() which then update the viewModel.score
        // but the viewModel.score observer code below is still necessary to update the
        // binding.scoreText.text
//        binding.correctButton.setOnClickListener {
//            viewModel.onCorrect()
////            updateWordText()
//        }
//        binding.skipButton.setOnClickListener {
//            viewModel.onSkip()
////            updateWordText()
//        }

        // update score using livedata, including a special anonymous lambda function
        // that is of the Observer type (part of lifecycle package i think)
        viewModel.score.observe(this, Observer {
            // newScore is the updated score livedata integer that the observer receives
            // and the "->" arrow serves to pipe it into the following code
            newScore -> binding.scoreText.text = newScore.toString()
        })

        // update word using livedata, including a special anonymous lambda function
        // that is of the Observer type
        viewModel.word.observe(this, Observer {
            // newWord is the updated word livedata string that the observer receives
            // and the "->" arrow serves to pipe it into the following code
            newWord -> binding.wordText.text = newWord
        })

        //  observe eventGameFinsh livedata
        viewModel.eventGameFinish.observe(this, Observer {
            // newWord is the updated word livedata string that the observer receives
            // and the "->" arrow serves to pipe it into the following code
            hasFinished -> if (hasFinished) {
                gameFinished()
            viewModel.onGameFinishComplete()
        }
        })

        // observe currentTime to display remaining time left
        // note this code got commented out after tutorial recommended connecting
        // currentTime from viewModel directly to game_fragment.xml
        // so GameFragment.kt no longer had to play middleman passing it along
        // but then i found that passing gameViewModel timer values direct to xml caused timer to disappear
        // so i just rolled back and left it like this, which is fine
        viewModel.currentTime.observe(this, Observer { newTime ->
            binding.timerText.text = DateUtils.formatElapsedTime(newTime)

        })

//        updateWordText()
        return binding.root

    }

    /**
     * Called when the game is finished
     */
    private fun gameFinished() {
        // note the "viewModel.score.value ?: 0" code below is really just getting
        // the score livedata integer value, but since the MutableData integer is nullable
        // kotlin throws an error if you just try to take its value
        // and so instead you need to run a null check. the "?:" elvis operator is this null check
        // and it means "give me the score.value i asked, unless the value is null then return 0"
        val action = GameFragmentDirections.actionGameToScore(viewModel.score.value ?: 0)
        findNavController(this).navigate(action)
        Toast.makeText(this.activity, "Game has finished", Toast.LENGTH_SHORT).show()
    }

    /** Methods for updating the UI **/
// before switching to LiveData we used this function to update the wordText
//    private fun updateWordText() {
//        binding.wordText.text = viewModel.word
//
//    }
}
