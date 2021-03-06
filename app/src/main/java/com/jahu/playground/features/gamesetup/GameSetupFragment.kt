package com.jahu.playground.features.gamesetup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jahu.playground.R
import com.jahu.playground.features.game.GameActivity
import com.jahu.playground.mvp.MvpFragment
import com.jahu.playground.trivia.TriviaQuestion
import kotlinx.android.synthetic.main.fragment_game_setup.*

class GameSetupFragment : MvpFragment<GameSetupContract.Presenter>(), GameSetupContract.View {

    companion object {
        fun newInstance() = GameSetupFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAppComponent()
                .plus(GameSetupModule(this))
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_game_setup, container, false)
    }

    override fun onResume() {
        super.onResume()
        playButton.setOnClickListener { presenter.onPlayButtonClicked() }
    }

    override fun showUserName(userName: String) {
        welcomeMessageTextView.text = getString(R.string.welcome_message, userName)
    }

    override fun showQuestionsRequestError() {
        Toast.makeText(activity, "Failed to fetch the questions", Toast.LENGTH_SHORT).show()
    }

    override fun showLoading() {
        questionsLoader.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        questionsLoader.visibility = View.GONE
    }

    override fun enablePlayButton() {
        playButton.isEnabled = true
    }

    override fun disablePlayButton() {
        playButton.isEnabled = false
    }

    override fun showNewGameScreen(questions: Array<TriviaQuestion>) {
        val intent = Intent(activity, GameActivity::class.java)
        intent.putExtra(GameActivity.BUNDLE_QUESTIONS_KEY, questions)
        startActivity(intent)
    }

}
