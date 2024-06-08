package com.salman.application.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.salman.application.data.UserRepository
import com.salman.application.data.api.response.DetailStoryResponse
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isStory = MutableLiveData<DetailStoryResponse>()
    private val _isError = MutableLiveData<String>()
    private val _isLoading = MutableLiveData<Boolean>()

    val isStory: LiveData<DetailStoryResponse> = _isStory
    val isError: LiveData<String> = _isError
    val isLoading: LiveData<Boolean> = _isLoading

    fun getDetailStory(story: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val getResponse = repository.getStoryDetail(story)
                _isStory.value = getResponse
            } catch (e: Exception) {
                _isError.value = DetailStoryResponse(error = true, message = e.message).toString()
            } finally {
                _isLoading.value = false
            }
        }
    }
}