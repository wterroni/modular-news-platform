package com.wterroni.news.feature.stories.data.model

import com.wterroni.news.feature.stories.domain.model.Story

fun StoryDto.toDomain(): Story {
    return Story(
        id = id,
        author = by,
        title = title,
        score = score,
        time = time,
        commentCount = descendants,
        url = url
    )
}
