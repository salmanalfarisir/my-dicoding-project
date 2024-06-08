package com.cafstone.dicodingstoryapp.di

import android.content.Context
import com.cafstone.dicodingstoryapp.data.UserRepository
import com.cafstone.dicodingstoryapp.data.api.ApiConfig
import com.cafstone.dicodingstoryapp.data.database.StoryDatabase
import com.cafstone.dicodingstoryapp.data.pref.UserPreference
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context)
        val token = runBlocking { userPreference.getToken() }
        val apiService = ApiConfig.getApiService(token)
        val storyDatabase = StoryDatabase.getDatabase(context)

        return UserRepository(apiService, storyDatabase, userPreference)
    }
}