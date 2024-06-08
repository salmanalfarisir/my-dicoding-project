package com.dicoding.asclepius.db

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Blob

@Parcelize
data class User (val _ID: Int,
                 val IMAGE: ByteArray,
                 val TITLE: String,
                 val SCORE: String) : Parcelable