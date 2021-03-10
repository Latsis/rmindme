package com.latsis.rmindme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.latsis.rmindme.db.AppDatabase
import java.util.*

const val GEOFENCE_RADIUS = 500
const val GEOFENCE_ID = "REMINDER_GEOFENCE_ID"
const val GEOFENCE_EXPIRATION = 10 * 24 * 60 * 60 * 1000 // 10 days
const val GEOFENCE_DWELL_DELAY =  10 * 1000 // 10 secs // 2 minutes
const val GEOFENCE_LOCATION_REQUEST_CODE = 12345
const val CAMERA_ZOOM_LEVEL = 13f
const val LOCATION_REQUEST_CODE = 123

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val TAG = MapActivity::class.java.simpleName
    private val REQUEST_LOCATION_PERMISSION = 1
    private var bundleCoordinatesX: Double = 65.08238
    private var bundleCoordinatesY: Double = 25.44262

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        title = "Reminder location"
        val bundle :Bundle ?=intent.extras
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (bundle?.getString("latitude")!= null && bundle.getString("latitude")!!.trim().isNotEmpty()) {
            bundleCoordinatesX = bundle.getString("latitude")!!.toDouble()
            bundleCoordinatesY = bundle.getString("longitude")!!.toDouble()
        } else {
            bundleCoordinatesX = 65.08238
            bundleCoordinatesY = 25.44262
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val reminderPoi = LatLng(bundleCoordinatesX, bundleCoordinatesY)
        //val oulu = LatLng(65.08238, 25.44262)
        val zoomLevel = 15f
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(reminderPoi, zoomLevel))
        map.addMarker(MarkerOptions().position(reminderPoi))
        map.addCircle(
            CircleOptions()
                .center(reminderPoi)
                .strokeColor(Color.argb(50,70,70,70))
                .fillColor(Color.argb(70,150, 150, 150))
                .radius(GEOFENCE_RADIUS.toDouble())
        )

        setMapLongClick(map)
        setPoiClick(map)
        setMapStyle(map)
        enableMyLocation()
    }


    // Allow users to add markers on map with long click
    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener {
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Lng: %2$.5f",
                it.latitude,
                it.longitude
            )
            map.addMarker(
                MarkerOptions()
                    .position(it)
                    .title("Dropped pin")
                    .snippet(snippet)
            )
            map.addCircle(
                CircleOptions()
                    .center(it)
                    .strokeColor(Color.argb(50,70,70,70))
                    .fillColor(Color.argb(70,150, 150, 150))
                    .radius(GEOFENCE_RADIUS.toDouble())
            )

            val backwardsIntent = Intent()
            backwardsIntent.putExtra("latitude", it.latitude.toString())
            backwardsIntent.putExtra("longitude", it.longitude.toString())
            setResult(RESULT_OK, backwardsIntent)

            //return to reminderItemActivity
            finish()
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener {
            val poiMarker = map.addMarker(MarkerOptions()
                .position(it.latLng)
                .title(it.name)
            )
            poiMarker.showInfoWindow()
        }
    }

    private fun setMapStyle(map: GoogleMap) {
        try {
            val success = map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_LOCATION_PERMISSION) {
            if(grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                enableMyLocation()
            }
        }
    }
}