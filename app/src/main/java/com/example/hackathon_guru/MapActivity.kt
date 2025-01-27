package com.example.hackathon_guru

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var placesClient: PlacesClient
    private lateinit var placeAdapter: PlaceAdapter
    private lateinit var recyclerView: RecyclerView
    private var scrapFolders: ArrayList<String>? = null
    private val placeList = mutableListOf<AutocompletePrediction>()
    private val markers = mutableListOf<Marker>()  // 마커 목록을 유지합니다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Scrap 폴더 목록 받기
        scrapFolders = intent.getStringArrayListExtra("scrapFolders")

        // BottomNavigationView 설정
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.navigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_map // map 선택

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_group -> {
                    startActivity(Intent(this, GroupListMain::class.java))
                    true
                }
                R.id.navigation_map -> {
                    true
                }
                R.id.navigation_scrap -> {
                    startActivity(Intent(this, MyScrapActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // MyScrapActivity에서 저장한 폴더 정보를 SharedPreferences에서 불러오기
        val sharedPreferences = getSharedPreferences("MyScrapPrefs", MODE_PRIVATE)
        val folderNames = sharedPreferences.getString("folders", "") ?: ""
        val folders = folderNames.split(",").filter { it.isNotEmpty() }
        scrapFolders = ArrayList(folders)  // Update scrapFolders with the loaded folders

        // recyclerView 초기화
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // PlaceAdapter 생성 시 scrapButton 클릭 리스너 추가
        placeAdapter = PlaceAdapter(convertToPlaceDataList(placeList), showScrapButton = true) { place ->
            // scrapButton 클릭 시 동작
            handleScrapButtonClick(place)
        }
        recyclerView.adapter = placeAdapter

        // Places 초기화
        Places.initialize(applicationContext, getString(R.string.MAPS_API_KEY))
        placesClient = Places.createClient(this)

        // 지도 생성
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
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

    // AutocompletePrediction 리스트를 PlaceData 리스트로 변환하는 함수
    private fun convertToPlaceDataList(predictions: List<AutocompletePrediction>): List<PlaceData> {
        return predictions.map {
            PlaceData(it.placeId, it.getPrimaryText(null).toString(), it.getSecondaryText(null).toString())
        }
    }

    // 장소 정보를 스크랩할 수 있는 다이얼로그 표시
    private fun showScrapDialog(place: AutocompletePrediction) {
        val dialog = MyScrapChooseFolderDialog(scrapFolders ?: listOf()) { selectedFolder ->
            savePlaceToFolder(place, selectedFolder)
        }
        dialog.show(supportFragmentManager, "ChooseFolderDialog")
    }

    private fun handleScrapButtonClick(place: PlaceData) {
        // Place 정보를 스크랩할 수 있는 다이얼로그 표시
        val prediction = placeList.find { it.placeId == place.id }
        if (prediction != null) {
            showScrapDialog(prediction)
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
            LatLng(cameraPosition.latitude - 0.1, cameraPosition.longitude - 0.1),
            LatLng(cameraPosition.latitude + 0.1, cameraPosition.longitude + 0.1)
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
            placeAdapter = PlaceAdapter(convertToPlaceDataList(placeList), showScrapButton = true) { place ->
                handleScrapButtonClick(place)
            }
            recyclerView.adapter = placeAdapter
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

                    // 카메라를 첫 번째 결과의 위치로 이동
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
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

    // 장소 정보를 폴더에 저장하는 함수
    private fun savePlaceToFolder(place: AutocompletePrediction, folderName: String) {
        val placeId = place.placeId
        val placeName = place.getPrimaryText(null).toString()
        val placeAddress = place.getSecondaryText(null).toString()

        val sharedPreferences = getSharedPreferences("MyScrapFolderPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val savedPlaces = sharedPreferences.getStringSet(folderName, mutableSetOf()) ?: mutableSetOf()
        savedPlaces.add("$placeId|$placeName|$placeAddress")
        editor.putStringSet(folderName, savedPlaces)
        editor.apply()

        Log.d("MapActivity", "Saved place $placeName to folder $folderName")
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
