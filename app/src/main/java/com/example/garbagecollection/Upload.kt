package com.example.garbagecollection

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class Upload : AppCompatActivity() {

    private lateinit var Home_btn: ImageButton
    private lateinit var Upload_btn: ImageButton
    private lateinit var History_btn : ImageButton
    private lateinit var Click : Button
    private lateinit var Upload : Button
    private lateinit var viewImage:ImageView
    var db:DbHandler= DbHandler(this)
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val IMAGE_REQUEST=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }


        Home_btn=findViewById(R.id.Home_btn)
        Home_btn.setOnClickListener {
            val intent =  Intent(this,Home::class.java)
            startActivity(intent)
            finish()
        }

        Upload_btn=findViewById(R.id.Upload_btn)
        Upload_btn.setOnClickListener {
            val intent =  Intent(this,Upload::class.java)
            startActivity(intent)
            finish()
        }

        History_btn=findViewById(R.id.History_btn)
        History_btn.setOnClickListener {
            val intent =  Intent(this,History::class.java)
            startActivity(intent)
            finish()
        }

        Click=findViewById(R.id.Click_Photo)

        Click.isEnabled=false

        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),111)
        }
        else{
            Click.isEnabled=true
        }

        Click.setOnClickListener {
            getpermission()
            val intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager)!=null){
                startActivityForResult(intent,IMAGE_REQUEST)

            }
        }

        viewImage=findViewById(R.id.view_Image)
        Upload=findViewById(R.id.upload)
        db.getrewards()

        Upload.setOnClickListener {
            db.addpoints()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION),100)
            return
        }

        fetchLocation()

    }


    override fun onActivityResult(requestCode:Int , resultCode:Int,data:Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==IMAGE_REQUEST && resultCode== Activity.RESULT_OK){

            val extras= data?.extras
            val bitmap=extras?.get("data") as Bitmap

            viewImage.setImageBitmap(bitmap)

            val cid=db.getuserid()
            db.insertimage(cid,bitmap.toString())
            Log.d("BITMAP","BITMAP:    $bitmap" )
        }

    }


    private fun getpermission() {
        val permissionlist= mutableListOf<String>()
        if(checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) permissionlist.add(android.Manifest.permission.CAMERA)
        if(checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) permissionlist.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) permissionlist.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

//                    Toast.makeText(this,"Latitude: $latitude, Longitude: $longitude",Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }




}
