package com.example.githubuserapp.db

import android.database.Cursor
import com.dicoding.asclepius.db.User

object MappingHelper {

    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<User> {
        val notesList = ArrayList<User>()
        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val image = getBlob(getColumnIndexOrThrow(DatabaseContract.NoteColumns.IMAGE))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val score = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.SCORE))
                notesList.add(User(id,image, title,score))
            }
        }
        return notesList
    }
}