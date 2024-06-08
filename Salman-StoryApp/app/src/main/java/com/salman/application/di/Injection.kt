package com.salman.application.di

import android.content.Context
import com.salman.application.data.UserRepository
import com.salman.application.data.api.ApiConfig
import com.salman.application.data.database.StoryDatabase
import com.salman.application.data.pref.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)

        return UserRepository(apiService, storyDatabase, userPreference)
    }
}