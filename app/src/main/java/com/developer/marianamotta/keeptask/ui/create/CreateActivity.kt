package com.developer.marianamotta.keeptask.ui.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.developer.marianamotta.keeptask.R
import com.developer.marianamotta.keeptask.MainActivity
import com.developer.marianamotta.keeptask.model.Criptografia
import kotlinx.android.synthetic.main.activity_create.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateActivity : AppCompatActivity() {
    private var image: ByteArray? = null
    private val REQUEST_CAPTURE_IMAGE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        camera.setOnClickListener {
            when {
                checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> getCamera()
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> Toast.makeText(
                    this,
                    "Erro ao iniciar a câmera.",
                    Toast.LENGTH_LONG
                ).show()
                else -> requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAPTURE_IMAGE
                )
            }
        }

        btnSaveNote.setOnClickListener {
            previewCardAnotation()
        }

        btnCloseCreateActivity.setOnClickListener {
            val intent = Intent(this@CreateActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun getCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão concedida.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permissão não concedida.", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                val imageBitmap = data.extras!!["data"] as Bitmap?
                camera.setImageBitmap(imageBitmap)

                val streamOutput = ByteArrayOutputStream()

                imageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, streamOutput)
                val byteArray = streamOutput.toByteArray()

                image = byteArray
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun previewCardAnotation() {
        if (!txtTitle.text.isNullOrEmpty() && !txtDescription.text.isNullOrEmpty()) {
            setVisibility()
            val date = setDate()

            previewTitle.text = txtTitle.text.toString()
            previewDescription.text = txtDescription.text.toString()
            previewImage.setImageBitmap(camera.drawToBitmap())
            previewDate.setText(date)

            val nomeFile =
                "${txtTitle.text.toString().uppercase(Locale.ROOT)}*${date.format(Date())}*"

            Criptografia.cryptoRecordText(
                "$nomeFile.txt",
                this,
                listOf(txtDescription.text.toString())
            )

            Criptografia.cryptoRecordImage(
                "$nomeFile.fig",
                this,
                image!!
            )
        } else {
            Toast.makeText(this, "É preciso preencher todos os campos.", Toast.LENGTH_LONG).show()
        }
    }

    private fun setVisibility() {
        preview.visibility = View.VISIBLE
        txtPreview.visibility = View.VISIBLE
        btnSaveNote.visibility = View.INVISIBLE
        btnCloseCreateActivity.visibility = View.VISIBLE
    }


    private fun setDate(): String {
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)

        return formatter.format(today)
    }
}