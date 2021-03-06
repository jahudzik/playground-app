package com.jahu.playground.features.gamesetup

import com.jahu.playground.data.DataSource
import com.jahu.playground.data.SharedPreferencesManager
import com.jahu.playground.data.entities.User
import com.jahu.playground.mvp.BasePresenter
import com.jahu.playground.trivia.TriviaQuestion
import com.jahu.playground.usecases.games.GetNewQuestionsUseCase

class GameSetupPresenter(
        private val view: GameSetupContract.View,
        private val sharedPreferencesManager: SharedPreferencesManager,
        private val dataSource: DataSource,
        private val getNewQuestionsUseCase: GetNewQuestionsUseCase
) : GameSetupContract.Presenter, BasePresenter<GameSetupContract.View>() {

    override fun resumeView() {
        val user = getActualUser()
        view.showUserName(user.firstName)
    }

    override fun onPlayButtonClicked() {
        view.disablePlayButton()
        view.showLoading()
        getNewQuestionsUseCase.execute(object : GetNewQuestionsUseCase.ResultListener {
            override fun onSuccess(questions: Array<TriviaQuestion>) {
                view.hideLoading()
                view.showNewGameScreen(questions)
                view.enablePlayButton()
            }

            override fun onFailure() {
                view.hideLoading()
                view.showQuestionsRequestError()
                view.enablePlayButton()
            }
        })
    }

    private fun getActualUser(): User {
        val userId = sharedPreferencesManager.getActualUserId()
        if (userId == -1L) {
            throw IllegalStateException("No information about current user (user id)")
        }
        return dataSource.getUserById(userId) ?:
                throw IllegalStateException("No information about current user (user data)")
    }

}
