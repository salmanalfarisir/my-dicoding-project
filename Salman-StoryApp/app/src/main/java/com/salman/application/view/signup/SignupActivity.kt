package com.salman.application.view.signup

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.salman.application.R
import com.salman.application.databinding.ActivitySignupBinding
import com.salman.application.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.emailEditTextLayout.setEditText(binding.edRegisterEmail)
        binding.passwordEditTextLayout.setEditText(binding.edRegisterPassword)

        setupView()
        setupAction()

        signupViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        signupViewModel.registrationStatus.observe(this) { status ->
            when (status) {
                is SignupViewModel.RegistrationStatus.Loading -> {
                }

                is SignupViewModel.RegistrationStatus.Success -> {
                    showDialog(status.message)
                }

                is SignupViewModel.RegistrationStatus.Error -> {
                    showDialog(status.message)
                }
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            signupViewModel.register(name, email, password)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(getString(R.string.lanjut)) { _, _ ->
                finish()
            }
            create()
            show()
        }
    }
}