package com.cafstone.dicodingstoryapp.view.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.cafstone.dicodingstoryapp.data.UserRepository
import com.cafstone.dicodingstoryapp.data.response.ErrorResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignupViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _regist = MutableLiveData<RegistrationStatus>()
    val regist: LiveData<RegistrationStatus>
        get() = _regist

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    sealed class RegistrationStatus {
        data object Loading : RegistrationStatus()
        data class Success(val message: String) : RegistrationStatus()
        data class Error(val message: String) : RegistrationStatus()
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            _regist.value = RegistrationStatus.Loading
            try {
                val response = userRepository.register(name, email, password)
                if (response.error == false) {
                    _regist.value = RegistrationStatus.Success(response.message ?: "User Created!")
                } else {
                    _regist.value = RegistrationStatus.Error(response.message ?: "Registration failed")
                }
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                val errorMessage = errorBody.message
                _regist.value = errorMessage?.let { RegistrationStatus.Error(it) }
            } catch (e: Exception) {
                _regist.value = RegistrationStatus.Error(e.message ?: "Something went wrong during registration")
            } finally {
                _isLoading.value = false
            }
        }
    }
}