package com.salman.application.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.salman.application.R
import com.salman.application.databinding.ActivityMainBinding
import com.salman.application.view.ViewModelFactory
import com.salman.application.view.maps.MapsActivity
import com.salman.application.view.post.AddStoryActivity
import com.salman.application.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var setAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        supportActionBar?.title = "Story"
        supportActionBar?.apply {
            show()
            setDisplayHomeAsUpEnabled(false)
        }

        binding.rvListStory.layoutManager = LinearLayoutManager(this)

        setAdapter = StoryAdapter()
        binding.rvListStory.adapter = setAdapter.withLoadStateFooter(footer = LoadingStateAdapter {
            setAdapter.retry()
        })

        viewModel.isStory.observe(this) { pagingData ->
            setAdapter.submitData(lifecycle, pagingData)
        }

        setAdapter.addLoadStateListener { state ->
            if (state.refresh is LoadState.Loading) {
                showLoading(true)
            } else {
                showLoading(false)
            }
        }
    }

    private fun setupAction() {
        binding.fabIcon.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
        binding.fabLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.map_action -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
