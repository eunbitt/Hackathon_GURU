package com.example.hackathon_guru

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.codelabs.buildyourfirstmap.place.Place
import com.google.codelabs.buildyourfirstmap.place.PlacesReader

// Google Maps API, Places API 사용을 위한 코드
class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var recyclerView: RecyclerView
    private lateinit var placesClient: PlacesClient
    private lateinit var mMap: GoogleMap

    // places 속성 추가
    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    private val placeList = mutableListOf<AutocompletePrediction>()
    private lateinit var placeAdapter: PlaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        placeAdapter = PlaceAdapter(placeList)
        recyclerView.adapter = placeAdapter

        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // 처음에는 숨기기
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Places.initialize(applicationContext, getString(R.string.MAPS_API_KEY))
        placesClient = Places.createClient(this)

        val searchBar = findViewById<SearchView>(R.id.searchBar)
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchPlaces(it)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(object : OnMapReadyCallback {
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

                // 초기 오버레이 숨기기
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })
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

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            // 검색 결과를 bottomSheet에 표시하도록 합니다.
            placeList.clear()
            placeList.addAll(predictions)
            placeAdapter.notifyDataSetChanged()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }.addOnFailureListener { exception ->
            // 오류 처리
            if (exception is ApiException) {
                Log.e("Place", "Place not found: ${exception.statusCode}")
            }
        }
    }

    // 마커 추가하기
    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
            )
        }
    }
}
