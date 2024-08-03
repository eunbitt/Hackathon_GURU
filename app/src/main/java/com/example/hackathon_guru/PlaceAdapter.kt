package com.example.hackathon_guru

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction

class PlaceAdapter(
    private val places: List<AutocompletePrediction>,
    private val onScrapButtonClick: (AutocompletePrediction) -> Unit
) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeName: TextView = itemView.findViewById(R.id.place_name)
        val placeAddress: TextView = itemView.findViewById(R.id.place_address)
        val scrapButton: ImageView = itemView.findViewById(R.id.scrapButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.placeName.text = place.getPrimaryText(null).toString()
        holder.placeAddress.text = place.getSecondaryText(null).toString()

        // scrapButton 클릭 리스너 설정
        holder.scrapButton.setOnClickListener {
            onScrapButtonClick(place)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }
}
