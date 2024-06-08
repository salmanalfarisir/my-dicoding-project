package com.salman.application.view.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.salman.application.data.UserRepository
import com.salman.application.data.api.ApiConfig
import com.salman.application.data.api.response.FileUploadResponse
import com.salman.application.data.api.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostStoryViewModel(repository: UserRepository) : ViewModel() {
    private val _response = MutableLiveData<FileUploadResponse>()
    val response: LiveData<FileUploadResponse> = _response

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val story: LiveData<PagingData<ListStoryItem>> =
        repository.getStoriesAll().cachedIn(viewModelScope)

    suspend fun uploadStoryImage(
        token: String,
        multipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: Double?,
        lon: Double?
    ): FileUploadResponse {
        _isLoading.value = true
        return withContext(Dispatchers.IO) {
            try {
                val apiService = ApiConfig.getApiService(token)
                val response = apiService.uploadImage(multipartBody, description, lat, lon)
                _response.postValue(response)
                _isLoading.postValue(false)
                response
            } catch (e: Exception) {
                throw e
            }
        }
    }
}