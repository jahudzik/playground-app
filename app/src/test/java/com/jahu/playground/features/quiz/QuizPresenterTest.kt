package com.jahu.playground.features.quiz

import com.jahu.playground.features.quiz.random.RandomSequenceGenerator
import com.jahu.playground.trivia.TriviaQuestion
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

private const val QUESTION = "Question"

class QuizPresenterTest {

    private lateinit var presenter: QuizPresenter

    @Mock private lateinit var view: QuizContract.View

    @Mock private lateinit var sequenceGenerator: RandomSequenceGenerator

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun showNextQuestion_shuffleAnswers0123() {
        initWithSingleQuestion(0, 1, 2, 3)

        presenter.resumeView()

        verifyExpectedAnswers(listOf("yes", "no_1", "no_2", "no_3"))
    }

    @Test
    fun showNextQuestion_shuffleAnswers3210() {
        initWithSingleQuestion(3, 2, 1, 0)

        presenter.resumeView()

        verifyExpectedAnswers(listOf("no_3", "no_2", "no_1", "yes"))
    }

    @Test
    fun showNextQuestion_shuffleAnswers2301() {
        initWithSingleQuestion(2, 3, 0, 1)

        presenter.resumeView()

        verifyExpectedAnswers(listOf("no_2", "no_3", "yes", "no_1"))
    }

    @Test
    fun showNextQuestion_shuffleAnswers1023() {
        initWithSingleQuestion(1, 0, 2, 3)

        presenter.resumeView()

        verifyExpectedAnswers(listOf("no_1", "yes", "no_2", "no_3"))
    }

    @Test
    fun onAnswerChosen_allAnswersIncorrect() {
        val questionsCount = 3
        initQuestionsWithCorrectAnswersFirst(questionsCount)

        presenter.resumeView()
        presenter.onAnswerChosen(3)     // incorrect
        presenter.onAnswerChosen(3)     // incorrect
        presenter.onAnswerChosen(3)     // incorrect

        verifyCorrectAnswersCount(0, questionsCount)
    }


    @Test
    fun onAnswerChosen_someAnswersCorrect() {
        val questionsCount = 6
        initQuestionsWithCorrectAnswersFirst(questionsCount)

        presenter.resumeView()
        presenter.onAnswerChosen(1)     // incorrect
        presenter.onAnswerChosen(0)     // correct
        presenter.onAnswerChosen(2)     // incorrect
        presenter.onAnswerChosen(0)     // correct
        presenter.onAnswerChosen(3)     // incorrect
        presenter.onAnswerChosen(3)     // incorrect

        verifyCorrectAnswersCount(2, questionsCount)
    }

    @Test
    fun onAnswerChosen_allAnswersCorrect() {
        val questionsCount = 4
        initQuestionsWithCorrectAnswersFirst(questionsCount)

        presenter.resumeView()
        presenter.onAnswerChosen(0)     // correct
        presenter.onAnswerChosen(0)     // correct
        presenter.onAnswerChosen(0)     // correct
        presenter.onAnswerChosen(0)     // correct

        verifyCorrectAnswersCount(4, questionsCount)
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(view)
        verifyNoMoreInteractions(sequenceGenerator)
    }

    private fun initWithSingleQuestion(vararg answersSequence: Int) {
        val triviaQuestion = buildTriviaQuestion(QUESTION, "yes", arrayOf("no_1", "no_2", "no_3"))
        whenever(sequenceGenerator.generate(any())).thenReturn(answersSequence.asList())
        presenter = QuizPresenter(view, listOf(triviaQuestion), sequenceGenerator)
    }

    private fun initQuestionsWithCorrectAnswersFirst(questionsCount: Int) {
        val questions = buildTriviaQuestionMocksList(questionsCount)
        whenever(sequenceGenerator.generate(any())).thenReturn(listOf(0, 1, 2, 3))
        presenter = QuizPresenter(view, questions, sequenceGenerator)
    }

    private fun buildTriviaQuestion(question: String,
                                    correctAnswer: String,
                                    incorrectAnswers: Array<String>): TriviaQuestion {
        return TriviaQuestion(
                "9",
                "multiple",
                "easy",
                question,
                correctAnswer,
                incorrectAnswers
        )
    }

    private fun buildTriviaQuestionMocksList(size: Int): List<TriviaQuestion> {
        val list = mutableListOf<TriviaQuestion>()
        for (i in 1..size) {
            list.add(buildTriviaQuestionMock())
        }
        return list
    }

    private fun buildTriviaQuestionMock(): TriviaQuestion {
        val triviaQuestion = mock<TriviaQuestion>()
        whenever(triviaQuestion.question).thenReturn("some question")
        whenever(triviaQuestion.correctAnswer).thenReturn("yes")
        whenever(triviaQuestion.incorrectAnswers).thenReturn(arrayOf("no_1", "no_2", "no_3"))
        return triviaQuestion
    }

    private fun verifyExpectedAnswers(expectedAnswers: List<String>) {
        verify(sequenceGenerator).generate(4)
        verify(view).showQuestion(QUESTION, expectedAnswers)
    }

    private fun verifyCorrectAnswersCount(expectedCorrectAnswersCount: Int, expectedQuestionsCount: Int) {
        verify(sequenceGenerator, times(expectedQuestionsCount)).generate(4)
        verify(view, times(expectedQuestionsCount)).showQuestion(any(), any())
        verify(view).showSummary(expectedCorrectAnswersCount, expectedQuestionsCount)
    }

}

