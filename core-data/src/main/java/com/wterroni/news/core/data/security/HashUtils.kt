package com.wterroni.news.core.data.security

import com.wterroni.news.core.common.constants.AppConstants
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import kotlin.random.Random

object HashUtils {
    
    fun generateSalt(): String {
        return (1..AppConstants.SALT_LENGTH)
            .map { AppConstants.CHARS_FOR_SALT[Random.nextInt(AppConstants.CHARS_FOR_SALT.length)] }
            .joinToString("")
    }
    
    fun hashPassword(password: String, salt: String): String {
        val combined = password + salt
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(combined.toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
