package com.dicoding.asclepius.view

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.example.githubuserapp.db.DatabaseContract
import com.example.githubuserapp.db.NoteHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.NumberFormat

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Result"
            setDisplayHomeAsUpEnabled(true)
        }

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
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
                                    binding.resultText.text = displayResult

                                    if (intent.getStringExtra(EXTRA_RESULT) == null) {
                                        val noteHelper = NoteHelper.getInstance(this@ResultActivity)
                                        noteHelper.open()
                                        val inputstream = contentResolver.openInputStream(imageUri)
                                        val bitmap = BitmapFactory.decodeStream(inputstream)
                                        val imagecontent = getBytes(bitmap)
                                        val values = ContentValues()
                                        values.put(DatabaseContract.NoteColumns.IMAGE, imagecontent)
                                        values.put(
                                            DatabaseContract.NoteColumns.TITLE,
                                            sortedCategories[0].label
                                        )
                                        val score = NumberFormat.getPercentInstance()
                                            .format(sortedCategories[0].score).trim()
                                        values.put(DatabaseContract.NoteColumns.SCORE, score)
                                        val result = noteHelper.insert(values)
                                    }
                                } else {
                                    binding.resultText.text = ""
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
        const val EXTRA_RESULT = "extra_result"
    }

    fun getBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
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