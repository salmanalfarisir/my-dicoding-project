package com.cafstone.dicodingstoryapp.view.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.cafstone.dicodingstoryapp.R
import com.cafstone.dicodingstoryapp.databinding.ActivityDetailBinding
import com.cafstone.dicodingstoryapp.view.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_story)

        val data = intent.getStringExtra(EXTRA_DETAIL)
        if (data != null){
            viewModel.getStoryDetail(data)
            viewModel.isLoading.observe(this) { isLoading ->
                showLoading(isLoading)
            }

            viewModel.story.observe(this) { storyResponse ->
                if (!storyResponse.error!!) {
                    storyResponse.story?.let {story->
                        binding.titleTextView.text = story.name
                        binding.messageTextView.text = story.description
                        binding.imgItemPhoto.load(story.photoUrl)
                        val tanggalStr = story.createdAt

                        val apiFormat =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        apiFormat.timeZone = TimeZone.getTimeZone("UTC")
                        val tanggalObj: Date? = tanggalStr?.let { apiFormat.parse(it) }

                        val targetFormat =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        val tanggalFormatBaru = tanggalObj?.let { targetFormat.format(it) }
                        val text = "Create at : $tanggalFormatBaru"
                        binding.createtext.text = text
                    }
                } else {
                    binding.titleTextView.text = getString(R.string.error_loading_story)
                    binding.messageTextView.text = storyResponse.message ?: getString(R.string.unknown_error)
                }
            }

            viewModel.error.observe(this) { error ->
                binding.titleTextView.text = getString(R.string.error)
                binding.messageTextView.text = error
            }
        }else{
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}