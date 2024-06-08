package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import java.io.File

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private const val REQUEST_CODE_PICK_IMAGE = 102
    }
    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }
        binding.HistoryButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            showImage(uri)
            val x = 16.toFloat()
            val y = 9.toFloat()
            val options = UCrop.Options().apply {
                // Atur format kompresi gambar menjadi JPEG
                setCompressionFormat(Bitmap.CompressFormat.JPEG)

                // Atur kualitas kompresi gambar menjadi 80
                setCompressionQuality(80)

                // Aktifkan semua jenis gestur untuk crop
                setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL)

                // Atur ukuran maksimum bitmap yang didecode dari URI sumber
                // Nilai default adalah 0, yang berarti ukuran maksimum tidak dibatasi
                // Misalnya, atur ukuran maksimum menjadi 1000x1000 pixel

                // Aktifkan tampilan frame pemangkasan dan panduan pemangkasan
                setShowCropFrame(true)
                setShowCropGrid(true)

                // Atur warna dan lebar frame pemangkasan
                setCropFrameColor(Color.RED)
                setCropFrameStrokeWidth(2)

                // Atur warna dan jumlah panduan pemangkasan
                setCropGridColor(Color.BLUE)
                setCropGridRowCount(2)
                setCropGridColumnCount(2)

                // Atur area pemangkasan menjadi oval (lingkaran)
                setCircleDimmedLayer(false)
                // Atur warna toolbar
                setToolbarColor(Color.WHITE)

                // Atur warna status bar
                setStatusBarColor(Color.DKGRAY)

            }
            UCrop.of(uri, Uri.fromFile(File(cacheDir, "cropped")))
                .withOptions(options)
                .withMaxResultSize(1000, 1000)
                .start(this);
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }
    private fun showImage(uri : Uri?) {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        if (uri != null)
        {
            binding.previewImageView.setImageURI(uri)
        }
    }

    private fun analyzeImage() {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        currentImageUri?.let {
            moveToResult()
        } ?: run {
            showToast("Gambar Belum Dimasukkan")
        }
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            currentImageUri = UCrop.getOutput(data!!);
            showImage(currentImageUri)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            showToast("Eroor")
        }
    }

}