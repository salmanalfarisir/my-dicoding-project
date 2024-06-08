package com.mobile.storyapp

import com.cafstone.dicodingstoryapp.data.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 1..100) {
            val story = ListStoryItem(
                i.toString(),
                "name $i",
                "description $i",
                "photoUrl $i",
            )
            items.add(story)
        }
        return items
    }
}