package com.salman.application.view.detail

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.salman.application.R
import com.salman.application.data.api.response.Story
import com.salman.application.databinding.ActivityDetailStoryBinding
import com.salman.application.view.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupView() {
        val storyId = intent.getStringExtra("storyId")
        val storyPhotoUrl = intent.getStringExtra("storyPhotoUrl")

        storyPhotoUrl?.let {
            binding.ivDetailPhoto.load(it)
        }

        storyId?.let {
            viewModel.getDetailStory(it)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.isStory.observe(this) { storyResponse ->
            if (storyResponse.error == false) {
                storyResponse.story?.let { setupAction(it) }
            } else {
                binding.tvDetailName.text = getString(R.string.error_loading_story)
                binding.tvDetailDescription.text =
                    storyResponse.message ?: getString(R.string.unknown_error)
            }
        }

        viewModel.isError.observe(this) { error ->
            binding.tvDetailName.text = getString(R.string.error)
            binding.tvDetailDescription.text = error
        }

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupAction(story: Story) {
        supportActionBar?.title = "Story ${story.name}"
        binding.tvDetailName.text = story.name
        binding.tvDetailDescription.text = story.description
        binding.ivDetailPhoto.load(story.photoUrl)
        binding.divider
        val createdDate = story.createdAt
        val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        apiFormat.timeZone = TimeZone.getTimeZone("UTC")
        val dateFormat: Date? = createdDate?.let { apiFormat.parse(it) }

        val targetFormat =
            SimpleDateFormat("dd/MM/yyyy - HH.mm", Locale.getDefault())
        val newDateFormat = dateFormat?.let { targetFormat.format(it) }
        val showDate = "Posted on: $newDateFormat"
        binding.createdAt.text = showDate

        binding.ivDetailPhoto.transitionName = "photo"
        binding.tvDetailName.transitionName = "name"
        binding.tvDetailDescription.transitionName = "description"
        binding.createdAt.transitionName = "createdAt"
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {

        val divider = ObjectAnimator.ofFloat(binding.divider, View.ALPHA, 1f).setDuration(100)
        val together = AnimatorSet().apply {
            playTogether(divider)
        }

        AnimatorSet().apply {
            playSequentially(divider, together)
            start()
        }
    }
}