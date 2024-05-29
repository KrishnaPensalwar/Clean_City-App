package com.example.garbagecollection

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class Home : AppCompatActivity() {

private lateinit var Home_btn: ImageButton
private lateinit var Upload_btn: ImageButton
private lateinit var History_btn : ImageButton
private lateinit var name:TextView
private lateinit var rewards:TextView
private lateinit var fusedLocationClient: FusedLocationProviderClient
private lateinit var mapView: MapView
private lateinit var myLocationOverlay: MyLocationNewOverlay
private lateinit var database:DatabaseReference
private lateinit var loginclass:login


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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



//        val db:DbHandler = DbHandler(this)
//        val noteList:ArrayList<User> = db.getallusers()

//        for (i in noteList.indices) {
//            Log.d("USER_INFO", "Id: ${noteList[i].id} ,Name: ${noteList[i].nama} , Mobile :${noteList[i].mobile}")
//        }


        val username=name.text.toString()
        rewards=findViewById(R.id.rewards)
//        val showname=db.getusername()
//        val showrewards=db.getrewards()



        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this)

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        mapView = findViewById(R.id.mapview)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mapView)
        myLocationOverlay.enableMyLocation()
        mapView.overlays.add(myLocationOverlay)


        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),100)
        } else {
            mapView.controller.setCenter(myLocationOverlay.myLocation)
            fetchLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Handle permission results here
        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mapView.controller.setCenter(myLocationOverlay.myLocation)
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

//                    Toast.makeText(this,"Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()

                    mapView.controller.setCenter(myLocationOverlay.myLocation)
                    val puneLocation = GeoPoint(latitude, longitude)
                    val marker = Marker(mapView)
                    marker.position = puneLocation
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)

                } else {
                    Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }


}