package com.cafstone.dicodingstoryapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cafstone.dicodingstoryapp.R
import com.cafstone.dicodingstoryapp.data.pref.UserModel
import com.cafstone.dicodingstoryapp.databinding.ActivityLoginBinding
import com.cafstone.dicodingstoryapp.utils.wrapEspressoIdlingResource
import com.cafstone.dicodingstoryapp.view.ViewModelFactory
import com.cafstone.dicodingstoryapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        showLoading(false)
        binding.emailEditText.setErrorTextView(binding.emailEditTextLayout)
        binding.passwordEditText.setErrorTextView(binding.passwordEditTextLayout)
        setupAction()
        playAnimation()

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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if ((binding.emailEditText.text.toString().isNotEmpty() && binding.emailEditTextLayout.error == null) &&
                (binding.passwordEditText.text.toString().isNotEmpty() && binding.passwordEditTextLayout.error == null))
            {
                viewModel.login(email, password)
            }else {
                Toast.makeText(this,"Mohon Isi Form Dengan Benar",Toast.LENGTH_SHORT).show()
            }

            viewModel.isLoading.observe(this) { isLoading ->
                showLoading(isLoading)
            }

            viewModel.login.observe(this) { response ->
                wrapEspressoIdlingResource {
                    if (!response.error!!) {
                        val token = response.loginResult?.token
                        if (token != null) {
                            val userModel = UserModel(email, token, true)
                            viewModel.saveSession(userModel)

                            Toast.makeText(this,R.string.login_success,Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                    }
                }
            }

            viewModel.error.observe(this){
                if (!it.equals(""))
                {
                    AlertDialog.Builder(this).apply {
                        setTitle(R.string.login_failed)
                        setMessage(it)
                        setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                            dialog.dismiss()
                        }
                        create()
                        show()
                    }
                    viewModel.seterror()
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(100)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(100)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(100)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                login
            )
            startDelay = 100
        }.start()
    }
}