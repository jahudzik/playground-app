package com.jahu.playground.chooseuser

import com.jahu.playground.dao.User
import com.nhaarman.mockito_kotlin.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ChooseUserPresenterTest {

    private lateinit var presenter: ChooseUserPresenter

    @Mock
    private lateinit var viewMock: ChooseUserContract.View

    @Mock
    private lateinit var getUsersUseCaseMock: GetUsersUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        presenter = ChooseUserPresenter(viewMock, getUsersUseCaseMock)
    }

    @Test
    fun initView_noUsers() {
        whenever(getUsersUseCaseMock.execute()).thenReturn(emptySet())

        presenter.initView()

        verify(getUsersUseCaseMock).execute()
        verify(viewMock).showNoUsersMessage()
    }

    @Test
    fun initView_someUsers() {
        val user1 = User("user1", "Amy", "Dolores")
        val user2 = User("user2", "Eva", "Black")
        val user3 = User("user3", "Mike", "Smith")
        val usersSet = setOf(user3, user1, user2)
        val expectedUsersList = listOf(user1, user2, user3)
        whenever(getUsersUseCaseMock.execute()).thenReturn(usersSet)

        presenter.initView()

        verify(getUsersUseCaseMock).execute()
        verify(viewMock).showUsersList(eq(expectedUsersList))
    }

    @Test
    fun onUserChosen_expected() {
        presenter.onUserChosen(mock())

        verify(viewMock).navigateToApp()
    }

    @Test
    fun onAddUserButtonClicked_expected() {
        presenter.onAddUserButtonClicked()

        verify(viewMock).navigateToAddUserScreen()
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(viewMock)
        verifyNoMoreInteractions(getUsersUseCaseMock)
    }

}