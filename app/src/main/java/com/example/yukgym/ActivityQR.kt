package com.example.yukgym

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yukgym.databinding.ActivityQrBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class ActivityQR : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityQrBinding

    companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

        private const val TAG = "MAIN_TAG"
    }

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>

    private var imaqeUri: Uri? = null

    private var barcodeScanner0ption: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cameraBtn.setOnClickListener(this)
        binding.galleryBtn.setOnClickListener(this)
        binding.scanBtn.setOnClickListener(this)

        cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        barcodeScanner0ption = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        barcodeScanner = BarcodeScanning.getClient(barcodeScanner0ption!!)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cameraBtn -> {
                if (checkCameraPermissions()) {
                    pickImageCamera()
                } else {
                    requestCameraPermission()
                }
            }
            R.id.galleryBtn -> {
                if (checkStoragePermissions()) {
                    pickImageGallery()
                } else {
                    requestStoragePermission()
                }
            }
            R.id.scanBtn -> {
                if (imaqeUri == null) {
                    showToast("Pick image first")
                } else {
                    detectResultFromImage()
                }
            }
        }
    }

    private fun checkCameraPermissions(): Boolean {
        val resultcamera = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        val resultstorage = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

        return resultcamera && resultstorage
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }

    private fun checkStoragePermissions(): Boolean {
        val result = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        pickImageCamera()
                    } else {
                        showToast("Camera dan storage permission are required")
                    }
                }
            }

            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    if (storageAccepted) {
                        pickImageGallery()
                    } else
                    {
                        showToast("Storage are required…")
                    }
                }
            }

        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Foto Sample")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Deskripsi Foto Sample")

        imaqeUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imaqeUri)

        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $imaqeUri")

            binding.imageTv.setImageURI (imaqeUri)
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }

    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
        if (result.resultCode == RESULT_OK){
            val data = result.data

            imaqeUri = data!!.data
            Log.d(TAG, "galleryActivityResultLauncher: imageUri: $imaqeUri")

            binding.imageTv.setImageURI(imaqeUri)

        }else {
            showToast("Dibatalkan ............")
        }
    }

    private fun detectResultFromImage() {
        try {
            val inputImage = InputImage.fromFilePath(this, imaqeUri!!)

            val barcodeResult = barcodeScanner?.process(inputImage)
                ?.addOnSuccessListener { barcodes ->
                    extractbarcodeQrCodeInfo(barcodes)
                }
                ?.addOnFailureListener { e ->
                    Log.d(TAG, "detectResultFromImage: ", e)
                    showToast("failed Scanning due to ${e.message}")
                }
        } catch (e: Exception) {
            Log.d(TAG, "detectResultFromImage: ", e)
            showToast("Failed Due to ${e.message}")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun extractbarcodeQrCodeInfo(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val bound = barcode.boundingBox
            val corners = barcode.cornerPoints
            val rawValue = barcode.rawValue
            Log.d(TAG,"extractbarcodeQrCodeInfo: rawValue: $rawValue")
            val valueType = barcode.valueType
            when(valueType) {
                Barcode.TYPE_WIFI -> {
                    val typeWifi = barcode.wifi
                    val ssid = "${typeWifi?.ssid}"
                    val password = "${typeWifi?.password}"
                    var encryptionType = "${typeWifi?.encryptionType}"
                    if (encryptionType == "1"){
                        encryptionType = "OPEN"
                    } else if (encryptionType == "2"){
                        encryptionType = "WPA"
                    } else if (encryptionType == "3"){
                        encryptionType = "WEP"
                    }
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_WIFI")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: ssid: $ssid")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: password: $password")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: encryptionType:$encryptionType")
                    binding.resultView.text =
                        "TYPE_WIFI \n ssid: $ssid \npassword: $password \nencryptionType: $encryptionType" + "\n\nrawValue : $rawValue"
                }
                Barcode.TYPE_URL -> {
                    val typeUrl = barcode.url
                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_URL")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: title: $title")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: url: $url")
                    binding.resultView.text =
                        "TYPE_URL \ntitle: $title \nurl: $url \n\nrawValue: $rawValue"
                }
                Barcode.TYPE_EMAIL -> {
                    val typeEmail = barcode.email
                    val address = "${typeEmail?.address}"
                    val body = "${typeEmail?.body}"
                    val subject = "${typeEmail?.subject}"
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_EMAIL")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: address: $address")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: body: $body")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: subject: $subject")
                    binding.resultView.text = "TYPE_EMAIL \ntitle: $address \nurl: $body \nsubject: $subject \n\nrawValue : $rawValue"
                }
                Barcode.TYPE_CONTACT_INFO -> {
                    val typeContact = barcode.contactInfo
                    val title = "${typeContact?.title}"
                    val organisasi = "${typeContact?.organization}"
                    val name = "${typeContact?.name}"
                    val phone = "${typeContact?.phones}"
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_CONTACT_INFO")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: Title: $title")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: Organization: $organisasi")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: nama: $name")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: Phone: $phone")
                    binding.resultView.text = "TYPE_CONTACT_INFO " +
                            "\ntitle: $title " +
                            "\nnama: $name " +
                            "\norganization: $organisasi " +
                            "\nPhone : $phone" + "\n\nrawValue : $rawValue"
                }
                else -> {
                    binding.resultView.text = "rawValue: $rawValue"
                }
            }
        }
    }


}