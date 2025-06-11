package com.example.babymonitorapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SavedLocationsAdapter(
    private val locations: List<LocationData>,
    private val onClick: (LocationData) -> Unit
) : RecyclerView.Adapter<SavedLocationsAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameText: TextView = view.findViewById(R.id.textLocationName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.nameText.text = location.name
        holder.itemView.setOnClickListener { onClick(location) }
    }

    override fun getItemCount(): Int = locations.size
}
