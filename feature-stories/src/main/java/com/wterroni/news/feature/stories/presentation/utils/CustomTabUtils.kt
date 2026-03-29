package com.wterroni.news.feature.stories.presentation.utils

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun openCustomTab(context: Context, url: String) {
    if (url.isBlank()) return
    
    try {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        
        customTabsIntent.launchUrl(context, url.toUri())
    } catch (e: Exception) {
        // Silently fail - don't crash the app if Custom Tabs fails
    }
}
