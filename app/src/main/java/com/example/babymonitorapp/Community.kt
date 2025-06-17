package com.example.babymonitorapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class Community : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var searchButton: Button
    private lateinit var locationInput: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedLocationsAdapter
    private var bottomNav: BottomNavigationView? = null
    private val savedLocations = mutableListOf<LocationData>()

    private val PREFS_NAME = "saved_locations"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        // Initialize views
        searchButton = findViewById(R.id.buttonSearch)
        locationInput = findViewById(R.id.editTextLocation)
        recyclerView = findViewById(R.id.recyclerViewLocations)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        adapter = SavedLocationsAdapter(savedLocations) { locationData ->
            moveToLocation(locationData.latLng, locationData.name)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        loadSavedLocations()

        searchButton.setOnClickListener {
            val locationName = locationInput.text.toString()
            if (locationName.isNotEmpty()) {
                searchLocation(locationName)
            }
        }

        bottomNav?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, MainActivity3::class.java))
                    true
                }
                R.id.baby -> {
                    startActivity(Intent(this, YoutubeActivity::class.java))
                    true
                }
                R.id.community -> {
                    startActivity(Intent(this, Community::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsView::class.java))
                    true
                }
                else -> false
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(locationName, 1)
        if (addresses != null && addresses.isNotEmpty()) {
            val address = addresses[0]
            val latLng = LatLng(address.latitude, address.longitude)
            map.addMarker(MarkerOptions().position(latLng).title(locationName))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            saveLocation(locationName, latLng)
        } else {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLocation(name: String, latLng: LatLng) {
        savedLocations.add(LocationData(name, latLng))
        adapter.notifyDataSetChanged()

        // Save to SharedPreferences
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val set = prefs.getStringSet("locations", mutableSetOf())!!.toMutableSet()
        set.add("${name}::${latLng.latitude},${latLng.longitude}")
        editor.putStringSet("locations", set)
        editor.apply()
    }

    private fun loadSavedLocations() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val set = prefs.getStringSet("locations", emptySet()) ?: emptySet()

        for (entry in set) {
            val parts = entry.split("::")
            if (parts.size == 2) {
                val name = parts[0]
                val coords = parts[1].split(",")
                val lat = coords[0].toDouble()
                val lng = coords[1].toDouble()
                savedLocations.add(LocationData(name, LatLng(lat, lng)))
            }
        }
        adapter.notifyDataSetChanged()
    }

    private fun moveToLocation(latLng: LatLng, name: String) {
        map.addMarker(MarkerOptions().position(latLng).title(name))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}
