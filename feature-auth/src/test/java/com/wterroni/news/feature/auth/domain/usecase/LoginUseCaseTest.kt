package com.wterroni.news.feature.auth.domain.usecase

import com.wterroni.news.feature.auth.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LoginUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val loginUseCase = LoginUseCase(repository)

    @Test
    fun `login valid credentials returns true`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        coEvery { repository.login(email, password) } returns true

        // When
        val result = loginUseCase(email, password)

        // Then
        assert(result == true)
        coVerify { repository.login(email, password) }
    }

    @Test
    fun `login invalid credentials returns false`() = runTest {
        // Given
        val email = "invalid@example.com"
        val password = "wrongpassword"
        coEvery { repository.login(email, password) } returns false

        // When
        val result = loginUseCase(email, password)

        // Then
        assert(result == false)
        coVerify { repository.login(email, password) }
    }
}
