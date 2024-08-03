package com.example.hackathon_guru

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackathon_guru.databinding.ItemScrapedPlaceBinding
import com.example.hackathon_guru.databinding.PlaceItemBinding

data class PlaceData(val id: String, val name: String, val address: String)

class PlaceAdapter(
    private val places: List<PlaceData>,
    private val showScrapButton: Boolean = true,
    private val onScrapButtonClick: ((PlaceData) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolderWithButton(private val binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceData) {
            binding.placeName.text = place.name
            binding.placeAddress.text = place.address
            binding.scrapButton.setOnClickListener {
                onScrapButtonClick?.invoke(place)
            }
        }
    }

    inner class ViewHolderWithoutButton(private val binding: ItemScrapedPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceData) {
            binding.placeName.text = place.name
            binding.placeAddress.text = place.address
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (showScrapButton) {
            val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolderWithButton(binding)
        } else {
            val binding = ItemScrapedPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolderWithoutButton(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val place = places[position]
        if (holder is ViewHolderWithButton) {
            holder.bind(place)
        } else if (holder is ViewHolderWithoutButton) {
            holder.bind(place)
        }
    }

    override fun getItemCount(): Int {
        return places.size
    }
}
