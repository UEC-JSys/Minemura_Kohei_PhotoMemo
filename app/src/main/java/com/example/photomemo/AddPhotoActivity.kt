package com.example.photomemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModel
import android.widget.Toast

class AddPhotoActivity : AppCompatActivity() {
    private val pickPhotoRequestCode = 4
    // これであってるのか？ /////
    private lateinit var imageUri: Uri
    private lateinit var viewModel: AddPhotoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        val openButton: Button = findViewById(R.id.addPhotoOpenButton)
        openButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                setType("image/jpeg")
            }
            startActivityForResult(intent, pickPhotoRequestCode)
        }

        val saveButton: Button = findViewById(R.id.addPhotoSaveButton)
        saveButton.setOnClickListener {
            val editText = findViewById<EditText>(R.id.addPhotoEditText)
            val replyIntent = Intent()
            if (imageUri == null || TextUtils.isEmpty(editText.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }else{
                val photo = Photo(imageUri.toString(), editText.text.toString())
                viewModel.insert(photo)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if( requestCode == pickPhotoRequestCode && resultCode == Activity.RESULT_OK ) {
            data?.data?.let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R)
                    contentResolver.takePersistableUriPermission(
                        it, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                val imageView = findViewById<ImageView>(R.id.addPhotoImageView)
                imageView.setImageURI(it)
                imageUri = it

                Toast.makeText(
                    applicationContext,
                    "setting ImageURI.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}