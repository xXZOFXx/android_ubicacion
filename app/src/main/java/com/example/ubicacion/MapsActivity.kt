package com.example.ubicacion

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets



class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var lastLocation: Location

    private val client = OkHttpClient()


    companion object{
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onMarkerClick(p0: Marker?) = false





        private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        run("https://api.github.com/users/Evin1-/repos")




    }


    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) = println(response.body?.string())
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if (id == R.id.add_action){
            Toast.makeText(this, "item Add Clicked", Toast.LENGTH_SHORT).show()
            return true
        }else{
            Toast.makeText(this,"Seeting clicked",Toast.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
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
        map  = googleMap

        //Add a marker in Sydney and move the camera
        //val sydney = LatLng(-34.0, 151.0)
        //map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
       // map.moveCamera(CameraUpdateFactory.newLatLng(sydney))



        map.setOnMarkerClickListener(this)
        map.uiSettings.isZoomControlsEnabled = true

        setUpMap()

    }


    private fun placeMarker(location: LatLng){

    val markerOption = MarkerOptions().position(location)

      map.addMarker(markerOption.title("location"))




    }


















    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

              ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
                return
            }

            map.isMyLocationEnabled = true

                fusedLocationClient.lastLocation.addOnSuccessListener(this) {location ->

                  if(location != null) {

                      lastLocation = location
                      val  currentLatLong = LatLng(location.latitude, location.longitude)
                      placeMarker(currentLatLong)
                      map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 10f))

                  }

                }

    }


    fun pushToChat(message: String) {

        val serverURL: String = "http://18.217.147.215:5000/api/eventos/v1/crearevento"
        val url = URL(serverURL)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.connectTimeout = 300000
        connection.connectTimeout = 300000
        connection.doOutput = true

        val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)

        connection.setRequestProperty("charset", "utf-8")
        connection.setRequestProperty("Content-lenght", postData.size.toString())
        connection.setRequestProperty("Content-Type", "application/json")

        try {
            val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()
        } catch (exception: Exception) {

        }

        if (connection.responseCode != HttpURLConnection.HTTP_OK && connection.responseCode != HttpURLConnection.HTTP_CREATED) {
            try {
                val inputStream: DataInputStream = DataInputStream(connection.inputStream)
                val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                val output: String = reader.readLine()

                println("There was error while connecting the chat $output")
                System.exit(0)

            } catch (exception: Exception) {
                throw Exception("Exception while push the notification  $exception.message")
            }
        }

    }








}
