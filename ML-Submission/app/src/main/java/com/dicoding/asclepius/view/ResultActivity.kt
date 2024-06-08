package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.dicoding.asclepius.R
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.text.NumberFormat

@Suppress("DEPRECATION")
class ResultActivity : AppCompatActivity() {

    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        supportActionBar?.apply {
            title = "Result"
            setDisplayHomeAsUpEnabled(true)
        }

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let { it ->
            Log.d("Image URI", "showImage: $it")
            findViewById<ImageView>(R.id.result_image).setImageURI(it)
            imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        runOnUiThread {
                            Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        runOnUiThread {
                            results?.let { it ->
                                if (it.isNotEmpty() && it[0].categories.isNotEmpty()) {
                                    println(it)
                                    val sortedCategories =
                                        it[0].categories.sortedByDescending { it?.score }
                                    val displayResult =
                                        sortedCategories[0].label + " " + NumberFormat.getPercentInstance()
                                            .format(sortedCategories[0].score).trim()
                                    findViewById<TextView>(R.id.result_text).text = displayResult
                                } else {
                                    findViewById<TextView>(R.id.result_text).text = ""
                                }
                            }
                        }
                    }
                }
            )
            imageClassifierHelper.classifyStaticImage(it)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
//        const val EXTRA_RESULT = "extra_result"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}

