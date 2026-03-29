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
class SignUpUseCaseTest {

    private val repository: AuthRepository = mockk()
    private val signUpUseCase = SignUpUseCase(repository)

    @Test
    fun `sign up calls repository correctly`() = runTest {
        // Given
        val name = "John Doe"
        val email = "john@example.com"
        val password = "password123"
        coEvery { repository.signUp(name, email, password) } returns Unit

        // When
        signUpUseCase(name, email, password)

        // Then
        coVerify { repository.signUp(name, email, password) }
    }

    @Test
    fun `sign up with empty name calls repository`() = runTest {
        // Given
        val name = ""
        val email = "test@example.com"
        val password = "password123"
        coEvery { repository.signUp(name, email, password) } returns Unit

        // When
        signUpUseCase(name, email, password)

        // Then
        coVerify { repository.signUp(name, email, password) }
    }

    @Test
    fun `sign up with invalid email calls repository`() = runTest {
        // Given
        val name = "Jane Doe"
        val email = "invalid-email"
        val password = "password123"
        coEvery { repository.signUp(name, email, password) } returns Unit

        // When
        signUpUseCase(name, email, password)

        // Then
        coVerify { repository.signUp(name, email, password) }
    }
}
