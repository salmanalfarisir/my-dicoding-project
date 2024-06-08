package com.salman.application.view.login

import android.content.Intent
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
import com.salman.application.data.pref.UserModel
import com.salman.application.databinding.ActivityLoginBinding
import com.salman.application.utils.wrapEspressoIdlingResource
import com.salman.application.view.ViewModelFactory
import com.salman.application.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupView()
        setupAction()

        val emailEditText = binding.edLoginEmail
        val emailInputLayout = binding.emailEditTextLayout
        val passwordEditText = binding.edLoginPassword
        val passwordInputLayout = binding.passwordEditTextLayout

        emailInputLayout.setEditText(emailEditText)
        passwordInputLayout.setEditText(passwordEditText)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            viewModel.login(email, password)

            viewModel.isLoading.observe(this) { isLoading ->
                showLoading(isLoading)
            }

            viewModel.loginResult.observe(this) { response ->
                wrapEspressoIdlingResource {
                    if (!response.error!!) {
                        val token = response.loginResult?.token
                        if (token != null) {
                            val userModel = UserModel(email, token, true)
                            viewModel.saveSession(userModel)

                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.yeah))
                                setMessage(getString(R.string.success))
                                setPositiveButton(getString(R.string.lanjut)) { _, _ ->
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    } else {
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.error))
                            setMessage(getString(R.string.login_failed))
                            setPositiveButton(getString(R.string.ok)) { _, _ -> }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}