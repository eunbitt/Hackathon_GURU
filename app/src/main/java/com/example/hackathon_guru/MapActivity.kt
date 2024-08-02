package com.example.hackathon_guru

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var placeAdapter: PlaceAdapter
    private val placeList = mutableListOf<AutocompletePrediction>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Places 초기화
        Places.initialize(applicationContext, getString(R.string.MAPS_API_KEY))
        placesClient = Places.createClient(this)

        // 지도 생성
        val mapFragment = supportFragmentManager.findFragmentById(
            R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 지도 오버레이 생성
        val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        placeAdapter = PlaceAdapter(placeList)

        // RecyclerView 설정
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placeAdapter
        }

        // 필터 설정 - 삭제 염두
        findViewById<Button>(R.id.restaurantBtn).setOnClickListener {
            searchPlaces("음식점")
        }
        findViewById<Button>(R.id.cafeBtn).setOnClickListener {
            searchPlaces("카페")
        }
        findViewById<Button>(R.id.lodgingBtn).setOnClickListener {
            searchPlaces("숙소")
        }
        findViewById<Button>(R.id.spotBtn).setOnClickListener {
            searchPlaces("가볼만한곳")
        }

        val searchView = findViewById<SearchView>(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            // 검색 결과를 bottomSheet에 표시하도록 합니다.
            placeList.clear()
            placeList.addAll(predictions)
            placeAdapter.notifyDataSetChanged()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }.addOnFailureListener { exception ->
            // 오류 처리
        }
    }
}