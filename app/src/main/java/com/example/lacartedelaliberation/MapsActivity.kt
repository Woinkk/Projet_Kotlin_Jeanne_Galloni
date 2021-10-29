package com.example.lacartedelaliberation

import android.Manifest
import android.R.attr
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
import com.example.lacartedelaliberation.BuildConfig.DEBUG
import com.google.android.gms.location.*
import java.lang.Exception
import android.util.Xml

import org.xmlpull.v1.XmlSerializer
import android.R.attr.data
import android.widget.Toast

import android.os.Environment
import android.R.attr.data
import java.lang.Boolean
import android.R.attr.data
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory


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
            maxWaitTime= 100
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return

                    if (locationResult.locations.isNotEmpty()) {
                        // get latest location
                        val location =
                            locationResult.lastLocation
                        // use your location object
                        // get latitude , longitude and other info from this
                        //Log.d("STATE", location.toString())
                        val me = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(me))
                        mMap.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    location.latitude,
                                    location.longitude
                                ), 10.0f
                            )
                        )
                        mMap.addMarker(MarkerOptions().position(me).title("Me"))
                    }
                }

            }
        }

        startLocationUpdates()

        /*try {
            var checkFile = File("markers.xml")
            var fileExists = checkFile.exists()
            if (!fileExists) {
                val fos: FileOutputStream = openFileOutput("markers.xml", MODE_APPEND)

                val serializer = Xml.newSerializer()
                serializer.setOutput(fos, "UTF-8")
                serializer.startDocument(null, Boolean.valueOf(true))
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

                serializer.endDocument()
                serializer.flush()

                fos.close()
            }

            val serializer = Xml.newSerializer()
            serializer.setOutput(fos, "UTF-8")
            serializer.startDocument(null, Boolean.valueOf(true))
                serializer.startTag(null, "marker")
                serializer.text("lat,lng")
                serializer.endTag(null, "marker")

            var fis: FileInputStream? = null
            var isr: InputStreamReader? = null

            fis = this.openFileInput("test9.xml")
            isr = InputStreamReader(fis)

            val inputBuffer = CharArray(fis.available())
            isr.read(inputBuffer)

            val data = String(inputBuffer)
            Log.d("STATE", data)
            isr.close()
            fis.close()


            /*
            * Converting the String data to XML format so
            * that the DOM parser understands it as an XML input.
            */
            val file: InputStream = ByteArrayInputStream(data.toByteArray())

            val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val db: DocumentBuilder = dbf.newDocumentBuilder()

            var items: NodeList? = null
            val dom: Document = db.parse(file)

            // Normalize the document
            dom.documentElement.normalize()

            items = dom.getElementsByTagName("marker")
            val arr = ArrayList<String>()

            for (i in 0 until items.length) {
                val item: Node = items.item(i)
                arr.add(item.textContent)
            }
            Log.d("STATE", arr.toString());
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
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

    fun addMarkers() {

    }
}