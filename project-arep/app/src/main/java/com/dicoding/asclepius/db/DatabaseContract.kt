package com.example.githubuserapp.db

import android.provider.BaseColumns

internal class DatabaseContract {

    internal class NoteColumns : BaseColumns {
        companion object {
            const val TABLE_NAME = "history"
            const val _ID = "_id"
            const val IMAGE = "image"
            const val TITLE = "title"
            const val SCORE = "score"
        }
    }
}