package com.example.lacartedelaliberation

//import android.location.LocationRequest

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lacartedelaliberation.databinding.ActivityMapsBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.*
import java.lang.Boolean
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    // declare a global variable FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    // globally declare LocationRequest
    private lateinit var locationRequest: LocationRequest

    // globally declare LocationCallback
    private lateinit var locationCallback: LocationCallback

    var fileName: String = "markers.xml"

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var myPosition = LatLng(-14.19782, -62.96149)

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

        //Create the markers.xml file if the file doesn't exists
        try {
            val checkFile = baseContext.getFileStreamPath(fileName)
            val fileExists = checkFile.exists()
            if (!fileExists) {

                val fos: FileOutputStream = openFileOutput(fileName, MODE_APPEND)

                val serializer = Xml.newSerializer()
                serializer.setOutput(fos, "UTF-8")
                serializer.startDocument(null, Boolean.valueOf(true))
                serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)

                serializer.startTag(null, "root")
                serializer.startTag(null, "marker")
                serializer.text("test")
                serializer.endTag(null, "marker")

                serializer.endDocument()
                serializer.flush()

                fos.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        LocationRequest.create().apply {
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
                        mMap.addMarker(
                            MarkerOptions()
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
            } else if (pop_up.visibility == View.VISIBLE) {
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
                addMarkers(lat, lon, "free")
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat.toDouble(), lon.toDouble()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.free_nain_small))
                )
                readSavedMarkers()
            } else {
                addMarkers(lat, lon, "prison")
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat.toDouble(), lon.toDouble()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.prison_nain_small))
                )
                readSavedMarkers()
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

        //get all saved markers and place them on the maps
        val savedMarkers: MutableList<String> = readSavedMarkers()

        for (i in 1 until savedMarkers.size) {
            val values = savedMarkers[i].split(",")
            val lat = values[0]
            val lng = values[1]
            val type = values[2]

            if (type != "free") {
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.prison_nain_small))
                )
            } else {
                mMap.addMarker(
                    MarkerOptions().position(LatLng(lat.toDouble(), lng.toDouble()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.free_nain_small))
                )
            }

        }
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
            null
        )
    }

    private fun addMarkers(latitude: String, longitude: String, type: String) {
        try {
            val filePath: String = filesDir.path.toString() + "/" + fileName
            val f = File(filePath)
            val documentBuilderFactory = DocumentBuilderFactory.newInstance()
            val documentBuilder = documentBuilderFactory.newDocumentBuilder()
            val document = documentBuilder.parse(f)
            val root: Node = document.firstChild

            val marker = document.createElement("marker")
            marker.appendChild(document.createTextNode("$latitude,$longitude,$type"))

            root.appendChild(marker)

            val source = DOMSource(document)

            val transformerFactory: TransformerFactory = TransformerFactory.newInstance()
            val transformer: Transformer = transformerFactory.newTransformer()
            val result = StreamResult(f)
            transformer.transform(source, result)
        } catch (e: Exception) {
            e.printStackTrace();
        }
    }

    private fun readSavedMarkers(): MutableList<String> {
        val arr: MutableList<String> = mutableListOf()
        try {
            val fis: FileInputStream = this.openFileInput(fileName)
            val isr = InputStreamReader(fis)

            val inputBuffer = CharArray(fis.available())
            isr.read(inputBuffer)

            val data = String(inputBuffer)
            isr.close()
            fis.close()

            val file: InputStream = ByteArrayInputStream(data.toByteArray())

            val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
            val db: DocumentBuilder = dbf.newDocumentBuilder()

            val items: NodeList?
            val dom: Document = db.parse(file)

            // Normalize the document
            dom.documentElement.normalize()

            items = dom.getElementsByTagName("marker")

            for (i in 0 until items.length) {
                val item: Node = items.item(i)
                arr.add(item.textContent)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return arr;
    }
}