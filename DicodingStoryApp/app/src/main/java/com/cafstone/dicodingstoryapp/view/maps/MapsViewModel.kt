package com.cafstone.dicodingstoryapp.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cafstone.dicodingstoryapp.data.UserRepository
import com.cafstone.dicodingstoryapp.data.response.GetAllStoriesResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _stories = MutableLiveData<GetAllStoriesResponse>()
    val stories: LiveData<GetAllStoriesResponse> = _stories

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            val response = userRepository.getStoriesWithLocation()
            _stories.postValue(response)
        }
    }
}