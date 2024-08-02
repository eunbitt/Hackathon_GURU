package com.example.hackathon_guru

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PinConfig
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var recyclerView: RecyclerView
    private val placeList = mutableListOf<AutocompletePrediction>()
    private val markers = mutableListOf<Marker>()  // 마커 목록을 유지합니다.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // recyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        placeAdapter = PlaceAdapter(placeList)
        recyclerView.adapter = placeAdapter

        // Places 초기화
        Places.initialize(applicationContext, getString(R.string.MAPS_API_KEY))
        placesClient = Places.createClient(this)

        // 지도 생성
        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val searchView = findViewById<SearchView>(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    clearMarkers()  // 기존 마커 제거
                    searchPlaces(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    clearMarkers()  // 기존 마커 제거
                    recyclerView.visibility = View.GONE
                }
                return false
            }
        })

        searchView.setOnCloseListener {
            clearMarkers()  // 기존 마커 제거
            recyclerView.visibility = View.GONE
            false
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // 초기 카메라 위치 서울 설정
        val seoul = LatLng(37.5665, 126.9780)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 12f))

        // 카메라 위치 대한민국 내로 제한
        val southKoreaBounds = LatLngBounds(
            LatLng(33.0, 124.0), // Southwest corner
            LatLng(39.0, 132.0)  // Northeast corner
        )
        mMap.setLatLngBoundsForCameraTarget(southKoreaBounds)
    }

    private fun searchPlaces(query: String) {
        val cameraPosition = mMap.cameraPosition.target
        val bias = RectangularBounds.newInstance(
            LatLng(cameraPosition.latitude - 0.05, cameraPosition.longitude - 0.05),
            LatLng(cameraPosition.latitude + 0.05, cameraPosition.longitude + 0.05)
        )
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setLocationBias(bias)  // 현재 카메라 위치로 검색을 제한합니다.
            .build()

        // 검색 결과 생성
        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            placeList.clear()
            placeList.addAll(predictions)
            placeAdapter.notifyDataSetChanged()
            recyclerView.visibility = View.VISIBLE

            // 각 장소의 LatLng를 가져와서 마커 추가
            predictions.forEach { prediction ->
                val placeId = prediction.placeId
                fetchPlaceAndAddMarker(placeId)
            }
        }.addOnFailureListener { exception ->
            // 오류 처리
            if (exception is ApiException) {
                Log.e("Place", "Place not found: ${exception.statusCode}")
            }
        }
    }

    // 장소의 LatLng를 가져와서 마커 추가
    private fun fetchPlaceAndAddMarker(placeId: String) {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { response ->
            val place = response.place
            val latLng = place.latLng

            if (latLng != null) {
                val advancedMarkerOptions = AdvancedMarkerOptions()
                    .position(latLng)
                    .title(place.name)
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(R.drawable.map_pin)))
                    .collisionBehavior(AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL)

                val marker = mMap.addMarker(advancedMarkerOptions)
                if (marker != null) {
                    markers.add(marker)
                    Log.d("Marker", "Added marker at ${latLng.latitude}, ${latLng.longitude}")
                }
            }
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e("Place", "Place not found: ${exception.statusCode}")
            }
        }
    }

    // 기존 마커 제거 함수
    private fun clearMarkers() {
        for (marker in markers) {
            marker.remove()
        }
        markers.clear()
    }

    // 벡터 drawable을 비트맵으로 변환하는 함수
    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(this, drawableId)!!
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}