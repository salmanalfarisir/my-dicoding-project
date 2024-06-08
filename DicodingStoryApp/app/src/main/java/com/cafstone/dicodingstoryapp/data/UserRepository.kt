package com.cafstone.dicodingstoryapp.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.cafstone.dicodingstoryapp.data.api.ApiService
import com.cafstone.dicodingstoryapp.data.database.StoryDatabase
import com.cafstone.dicodingstoryapp.data.mediator.StoryRemoteMediator
import com.cafstone.dicodingstoryapp.data.pref.UserModel
import com.cafstone.dicodingstoryapp.data.response.DetailStoryResponse
import com.cafstone.dicodingstoryapp.data.response.ListStoryItem
import com.cafstone.dicodingstoryapp.data.pref.UserPreference
import com.cafstone.dicodingstoryapp.data.response.GetAllStoriesResponse
import com.cafstone.dicodingstoryapp.data.response.LoginResponse
import com.cafstone.dicodingstoryapp.data.response.RegisterResponse
import com.cafstone.dicodingstoryapp.utils.wrapEspressoIdlingResource

class UserRepository(
    private val apiService: ApiService,
    private val storyDatabase: StoryDatabase,
    private val userPreference: UserPreference
) {

    @OptIn(ExperimentalPagingApi::class)
    fun getStoriesAll(): LiveData<PagingData<ListStoryItem>> {
        wrapEspressoIdlingResource {
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
                pagingSourceFactory = {
                    storyDatabase.storyDao().getAllStories()
                }
            ).liveData
        }
    }

    suspend fun getStoriesWithLocation(): GetAllStoriesResponse {
        return apiService.getStoriesWithLocation(location = 1)
    }

    suspend fun getStoryDetail(storyId: String): DetailStoryResponse {
        return apiService.getStoryDetail(storyId)
    }

    fun getSession() = userPreference.getSession()

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        if (!response.error!!) {
            response.loginResult?.token?.let { token ->
                userPreference.saveSession(UserModel(email, token, true))
            }
        }
        return response
    }

    suspend fun saveSession(userModel: UserModel) {
        userPreference.saveSession(userModel)
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            storyDatabase: StoryDatabase,
            userPreference: UserPreference
        ) = UserRepository(apiService, storyDatabase, userPreference)
    }
}
