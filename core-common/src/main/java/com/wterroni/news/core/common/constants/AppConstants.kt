package com.wterroni.news.core.common.constants

object AppConstants {
    
    // Splash
    const val SPLASH_DELAY_MS = 1000L
    
    // API
    const val STORIES_BATCH_SIZE = 20
    const val STORIES_LOAD_MORE_LIMIT = 50
    
    // Security
    const val SALT_LENGTH = 16
    
    // Navigation
    const val DESTINATION_STORIES = "stories"
    const val DESTINATION_LOGIN = "login"
    
    // Strings
    const val CHARS_FOR_SALT = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
}
