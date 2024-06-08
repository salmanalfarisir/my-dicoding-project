package com.dicoding.asclepius.view

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.db.User
import com.example.githubuserapp.db.DatabaseContract
import com.example.githubuserapp.db.MappingHelper
import com.example.githubuserapp.db.NoteHelper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.NumberFormat

class QuoteAdapter(private var listHero: ArrayList<User>) : RecyclerView.Adapter<QuoteAdapter.ListViewHolder>() {


private lateinit var context : Context
private var adapter = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_review, parent, false)
        context = parent.context
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (_id,image,title,score) = listHero[position]
        val bitmap = BitmapFactory.decodeByteArray(image,0,image.size)
        holder.imgPhoto.setImageBitmap(bitmap)
        holder.tvName.text = title
        holder.tvDescription.text = score
        holder.itemView.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("History List")
            alertDialogBuilder.setMessage("Ingin Melihat/Menghapus History?")

            alertDialogBuilder.setPositiveButton("Lihat History") { dialog, _ ->
                val imageuri = byteArrayToImageUri(context,image)
                val moveWithObjectIntent = Intent(holder.itemView.context, ResultActivity::class.java)
                moveWithObjectIntent.putExtra(ResultActivity.EXTRA_IMAGE_URI, imageuri.toString())
                moveWithObjectIntent.putExtra(ResultActivity.EXTRA_RESULT,"Database")
                holder.itemView.context.startActivity(moveWithObjectIntent)
                dialog.dismiss()
            }

            alertDialogBuilder.setNeutralButton("Hapus History"){dialog, _ ->
                val noteHelper = NoteHelper.getInstance(context)
                noteHelper.open()
                val delete = noteHelper.deleteById(_id.toString())
                if (delete > 0)
                {
                    listHero.removeAt(position)
                    notifyDataSetChanged()
                }
                dialog.dismiss()
            }

            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }
    override fun getItemCount(): Int = listHero.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val tvName: TextView = itemView.findViewById(R.id.tv_item_name)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)
    }

    fun byteArrayToImageUri(context: Context, byteArray: ByteArray): Uri? {
        var uri: Uri? = null
        try {
            // Simpan array byte ke dalam file sementara
            val file = File(context.cacheDir, "temp_image.jpg")
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(byteArray)
            fileOutputStream.flush()
            fileOutputStream.close()

            // Dapatkan URI dari file sementara
            uri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }


}