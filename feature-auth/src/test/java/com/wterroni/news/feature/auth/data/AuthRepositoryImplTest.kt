package com.wterroni.news.feature.auth.data

import com.wterroni.news.core.data.datastore.AuthDataStore
import com.wterroni.news.core.data.datastore.SessionDataStore
import com.wterroni.news.core.data.security.HashUtils
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AuthRepositoryImplTest {

    private val authDataStore: AuthDataStore = mockk()
    private val sessionDataStore: SessionDataStore = mockk()
    private val hashUtils: HashUtils = mockk()

    private val repository = AuthRepositoryImpl(authDataStore, sessionDataStore, hashUtils)

    @Test
    fun `login with valid credentials returns true`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val savedEmail = "test@example.com"
        val savedPasswordHash = "hashedPassword"
        val savedSalt = "salt"
        val inputPasswordHash = "hashedPassword"

        coEvery { authDataStore.getUserEmail() } returns flowOf(savedEmail)
        coEvery { authDataStore.getPasswordHash() } returns flowOf(savedPasswordHash)
        coEvery { authDataStore.getSalt() } returns flowOf(savedSalt)
        coEvery { hashUtils.hashPassword(password, savedSalt) } returns inputPasswordHash
        coEvery { sessionDataStore.setLoggedIn(true) } returns Unit

        // When
        val result = repository.login(email, password)

        // Then
        assert(result == true)
        coVerify { authDataStore.getUserEmail() }
        coVerify { authDataStore.getPasswordHash() }
        coVerify { authDataStore.getSalt() }
        coVerify { hashUtils.hashPassword(password, savedSalt) }
        coVerify { sessionDataStore.setLoggedIn(true) }
    }

    @Test
    fun `login with invalid email returns false`() = runTest {
        // Given
        val email = "wrong@example.com"
        val password = "password123"
        val savedEmail = "test@example.com"
        val savedPasswordHash = "hashedPassword"
        val savedSalt = "salt"

        coEvery { authDataStore.getUserEmail() } returns flowOf(savedEmail)
        coEvery { authDataStore.getPasswordHash() } returns flowOf(savedPasswordHash)
        coEvery { authDataStore.getSalt() } returns flowOf(savedSalt)

        // When
        val result = repository.login(email, password)

        // Then
        assert(result == false)
        coVerify { authDataStore.getUserEmail() }
        coVerify { authDataStore.getPasswordHash() }
        coVerify { authDataStore.getSalt() }
        coVerify(exactly = 0) { sessionDataStore.setLoggedIn(match { true }) }
    }

    @Test
    fun `login with invalid password returns false`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        val savedEmail = "test@example.com"
        val savedPasswordHash = "hashedPassword"
        val savedSalt = "salt"
        val inputPasswordHash = "wrongHash"

        coEvery { authDataStore.getUserEmail() } returns flowOf(savedEmail)
        coEvery { authDataStore.getPasswordHash() } returns flowOf(savedPasswordHash)
        coEvery { authDataStore.getSalt() } returns flowOf(savedSalt)
        coEvery { hashUtils.hashPassword(password, savedSalt) } returns inputPasswordHash

        // When
        val result = repository.login(email, password)

        // Then
        assert(result == false)
        coVerify { authDataStore.getUserEmail() }
        coVerify { authDataStore.getPasswordHash() }
        coVerify { authDataStore.getSalt() }
        coVerify { hashUtils.hashPassword(password, savedSalt) }
        coVerify(exactly = 0) { sessionDataStore.setLoggedIn(match { true }) }
    }

    @Test
    fun `logout clears session`() = runTest {
        // Given
        coEvery { sessionDataStore.setLoggedIn(false) } returns Unit

        // When
        repository.logout()

        // Then
        coVerify { sessionDataStore.setLoggedIn(false) }
    }

    @Test
    fun `isLoggedIn returns session flow`() = runTest {
        // Given
        val expectedFlow = flowOf(true)
        coEvery { sessionDataStore.isLoggedInFlow } returns expectedFlow

        // When
        val result = repository.isLoggedIn()

        // Then
        assert(result == expectedFlow)
        coVerify { sessionDataStore.isLoggedInFlow }
    }
}
