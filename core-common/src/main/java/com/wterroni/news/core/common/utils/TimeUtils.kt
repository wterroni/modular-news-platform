package com.wterroni.news.core.common.utils

fun Long.toRelativeTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    
    return when {
        diff < 60_000 -> "just now"
        diff < 3_600_000 -> "${diff / 60_000} minutes ago"
        diff < 86_400_000 -> "${diff / 3_600_000} hours ago"
        diff < 604_800_000 -> "${diff / 86_400_000} days ago"
        else -> {
            val days = diff / 86_400_000
            if (days < 30) "$days days ago"
            else if (days < 365) "${days / 30} months ago"
            else "${days / 365} years ago"
        }
    }
}
