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

package com.example.android.guesstheword.screens.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.guesstheword.R
import com.example.android.guesstheword.databinding.ScoreFragmentBinding
import androidx.lifecycle.Observer
import com.example.android.guesstheword.screens.game.GameViewModel

/**
 * Fragment where the final score is shown, after the game is over
 */
class ScoreFragment : Fragment() {

    // create vars called ScoreViewModel and ScoreViewModelFactory,
    // which are of the class viewModel and viewModelFactory, respectively
    // note we link these vars to the respective ScoreViewModel and ScoreViewModelFactory code below
    // the instructors admit the factory isn't 100% necessary, but it's benefit is giving you a value immediately
    // as opposed to just using a get() like in the regular ViewModelProvider like in GameFragment
    // although both methods are used in onCreateView,
    // it sounds like using the factory has a slight loading speed advantage
    private lateinit var viewModel: ScoreViewModel
    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class.
        val binding: ScoreFragmentBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.score_fragment,
                container,
                false
        )

        // Get args using by navArgs property delegate
        val scoreFragmentArgs by navArgs<ScoreFragmentArgs>()
        binding.scoreText.text = scoreFragmentArgs.score.toString()

        // link fragment to ScoreViewModelFactory code by calling ScoreViewModelFactory() function
        // this linking for factories is similar to the linking for regular viewModels w/ GetViewModelProvider
        // like we used in GameFragment.kt
        viewModelFactory = ScoreViewModelFactory(scoreFragmentArgs.score)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ScoreViewModel::class.java)

        // pass the GameViewModel (saved as viewModel) into the binding.gameViewModel data slot
        // so that the GameViewModel values can directly update
        // game_fragment.xml, instead of being passed via GameFragment.kt like a middle man
        // this lets me get rid of some onClickListeners (see below)
        binding.scoreViewModel = viewModel

        // Add observer for score
        viewModel.score.observe(this, Observer { newScore ->
            binding.scoreText.text = newScore.toString()
        })

        // originally, this onClick listeners called viewModel's onPlayAgain function
        // which updated the eventPlayAgain value to true,
        // which then triggered the viewModel.eventPlayAgain observer below to
        // restart the game and navigate etc
        // but then the tutorial said it was better to directly add the viewModel to the binding
        // and place the onclick listeners as a lambda function right in the score_fragment.xml layout
        // note though that the onClick listener in the score_fragment.xml layout only calls
        // onPlayAgain() which then update the viewModel.eventPlayAgain
        // but the viewModel.eventPlayAgain observer code below is still necessary to
        // restart the game and navigate etc
//        binding.playAgainButton.setOnClickListener { viewModel.onPlayAgain() }
        //        binding.playAgainButton.setOnClickListener { onPlayAgain() }


        // Navigates back to title when button is pressed
        viewModel.eventPlayAgain.observe(this, Observer { playAgain ->
            if (playAgain) {
                findNavController().navigate(ScoreFragmentDirections.actionRestart())
                viewModel.onPlayAgainComplete()
            }
        })

        return binding.root
    }

    private fun onPlayAgain() {
        findNavController().navigate(ScoreFragmentDirections.actionRestart())
    }
}
