package com.example.lacartedelaliberation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.lacartedelaliberation.databinding.ActivityMapsBinding
import androidx.core.app.ActivityCompat

import android.content.pm.PackageManager
import android.location.LocationManager
//import android.location.LocationRequest
import android.os.Build
import android.util.Log
import android.util.Log.DEBUG
import androidx.annotation.RequiresApi

import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import java.lang.Exception
import android.content.Intent
import android.text.Editable
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.lacartedelaliberation.R.id.TextCaptureOrFree
import com.google.android.gms.maps.model.BitmapDescriptorFactory


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var myPosition: LatLng = LatLng(-14.19782, -62.96149)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        try {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // in onCreate() initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            locationRequest = LocationRequest()
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            maxWaitTime = 100
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return

                    if (locationResult.locations.isNotEmpty()) {
                        // get latest location
                        val location =
                            locationResult.lastLocation
                        // use your location object
                        // get latitude , longitude and other info from this
                        Log.d("STATE", location.toString())
                        val me = LatLng(location.latitude, location.longitude)
                        myPosition = me
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(me))
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), 17.0f
                            )
                        )
                        mMap.addMarker(MarkerOptions()
                            .position(me)
                            .title("Me")
                        )
                    }
                }

            }
        }

        val free = findViewById<ImageButton>(R.id.free)
        val prison = findViewById<ImageButton>(R.id.prison)
        val pop_up = findViewById<LinearLayout>(R.id.pop_up)
        val textTitle = findViewById<TextView>(R.id.TextCaptureOrFree)
        val latitude = findViewById<EditText>(R.id.latitude)
        val longitude = findViewById<EditText>(R.id.longitude)
        val create = findViewById<Button>(R.id.create)

        free.setOnClickListener { v ->
            latitude.hint = myPosition.latitude.toString()
            longitude.hint = myPosition.longitude.toString()
            if (pop_up.visibility == View.VISIBLE && textTitle.text == "Merci d'indiquer le point de capture") {
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                textTitle.text = "Merci d'indiquer le point de libération"
                pop_up.startAnimation(animation)
            } else if(pop_up.visibility == View.VISIBLE) {
                pop_up.visibility = View.INVISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
                pop_up.startAnimation(animation)
            } else {
                pop_up.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                textTitle.text = "Merci d'indiquer le point de libération"
                pop_up.startAnimation(animation)
            }
        }

        prison.setOnClickListener { v ->
            latitude.hint = myPosition.latitude.toString()
            longitude.hint = myPosition.longitude.toString()
            if (pop_up.visibility == View.VISIBLE && textTitle.text == "Merci d'indiquer le point de libération") {
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
                textTitle.text = "Merci d'indiquer le point de capture"
                pop_up.startAnimation(animation)
            } else if (pop_up.visibility == View.VISIBLE) {
                pop_up.visibility = View.INVISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
                pop_up.startAnimation(animation)
            } else {
                pop_up.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
                textTitle.text = "Merci d'indiquer le point de capture"
                pop_up.startAnimation(animation)
            }
        }

        create.setOnClickListener { v ->
            var lat = latitude.text.toString()
            var lon = longitude.text.toString()
            if (lat == "") lat = latitude.hint.toString()
            if (lon == "") lon = longitude.hint.toString()
            if (textTitle.text == "Merci d'indiquer le point de libération") {
                //TO DO: add to liberation data
            } else {
                //TO DO: add to prison data
            }
            pop_up.visibility = View.INVISIBLE
            val animation = AnimationUtils.loadAnimation(this, R.anim.exit_top)
            pop_up.startAnimation(animation)

        }

        startLocationUpdates()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    //start location updates
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null /* Looper */
        )
    }
}