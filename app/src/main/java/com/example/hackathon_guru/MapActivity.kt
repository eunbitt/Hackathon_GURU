package com.example.hackathon_guru

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.location.Location
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.TrackingManager


class MapActivity : AppCompatActivity() {
    // onRequestPermissionsResult에서 권한 요청 결과를 받기 위한 request code
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    // 요청할 위치 권한 목록
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var startPosition: LatLng? = null // 카카오 지도의 시작 위치를 저장하는 변수입니다.
    private lateinit var progressBar: ProgressBar // 로딩 중을 표시하는 ProgressBar를 참조하는 변수입니다.
    private lateinit var mapView: MapView // 카카오 지도 뷰를 참조하는 변수입니다.
    private lateinit var centerLabel: Label // 현재 위치를 나타내는 라벨을 저장하는 변수입니다.

    // 위치 업데이트가 요청된 적 있는지 여부를 저장합니다.
    private var requestingLocationUpdates = false // 위치 업데이트 요청 여부를 나타내는 변수입니다.
    private lateinit var locationRequest: LocationRequest // 위치 업데이트 요청을 위한 LocationRequest 객체입니다.
    private lateinit var locationCallback: LocationCallback // 위치 업데이트를 처리하는 LocationCallback 객체입니다.

    private val readyCallback = object : KakaoMapReadyCallback() {
        // 인증 후 API가 정상적으로 실행될 때 호출됨
        override fun onMapReady(kakaoMap: KakaoMap) {
            progressBar.visibility = View.GONE // 지도 로딩 완료 후 ProgressBar 숨기기

            // 현재 위치를 나타낼 label를 그리기 위해 kakaomap 인스턴스에서 LabelLayer를 가져옵니다.
            kakaoMap.labelManager?.layer?.let { layer ->
                // LabelLayer에 라벨을 추가합니다. 카카오 지도 API 공식 문서에 지도에서 사용하는 이미지는 drawable-nodpi/ 에 넣는 것을 권장합니다.
                centerLabel = layer.addLabel(
                    LabelOptions.from("centerLabel", startPosition!!)
                        .setStyles(
                            LabelStyle.from(R.drawable.map_pin).setAnchorPoint(0.5f, 0.5f)
                        )
                        .setRank(1)
                )

                // 라벨의 위치가 변하더라도 항상 화면 중앙에 위치할 수 있도록 trackingManager를 통해 tracking을 시작합니다.
                val trackingManager: TrackingManager? = kakaoMap.trackingManager
                if (trackingManager != null) {
                    trackingManager.startTracking(centerLabel)
                }
            }

            // 위치 업데이트 메서드 입니다. 5번 항목 참조
            startLocationUpdates()
        }

        // 카카오 지도의 초기 위치를 반환하는 메서드입니다.
        override fun getPosition(): LatLng {
            return startPosition!!
        }

        // 카카오 지도의 초기 줌레벨을 설정하는 메서드 입니다.
        override fun getZoomLevel(): Int {
            return 17
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE // 지도 로딩 중에 ProgressBar를 표시

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                // 업데이트된 위치로 라벨을 이동시킵니다.
                for (location in locationResult.locations) {
                    centerLabel.moveTo(LatLng.from(location.latitude, location.longitude))
                }
            }
        }

        // 이미 앱에 위치 권한이 있는지 확인합니다.
        if (ContextCompat.checkSelfPermission(
                this,
                locationPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                locationPermissions[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getStartLocation()
        } else {
            // 위치 권한이 없는 경우, 권한 요청 다이얼로그를 띄웁니다.
            ActivityCompat.requestPermissions(
                this,
                locationPermissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            override fun onMapError(error: Exception) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, readyCallback)
    }

    // 액티비티가 onPause 되었다가 onResume될때, requestingLocationUpdates의 값이 true면 위치 업데이트가 다시 시작되도록 합니다.
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    // 액티비티가 onPause 상태가 될때, 위치업데이트가 중단시켜서 불필요한 배터리 및 리소스 낭비를 막습니다.
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getStartLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    // 구해진 현재 위치를 카카오 지도의 시작 위치를 설정할 때 사용하기 위해 LatLng 객체로 변환합니다.
                    startPosition = LatLng.from(location.latitude, location.longitude)
                    // 카카오 지도를 시작합니다.
                    mapView.start(readyCallback)
                }
            }
    }

    // 위치 업데이트 요청을 시작하는 메서드입니다.
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        requestingLocationUpdates = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    // 사용자가 권한 요청 다이얼로그에 응답하면 이 메소드가 실행됩니다.
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray)
    { super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 위치 권한을 허가했을 경우입니다. 여기서 원하는 작업을 진행하면 됩니다.
                getStartLocation()
            } else {
                // 위치 권한이 거부되었을 경우, 다이얼로그를 띄워서 사용자에게 앱을 종료할지, 권한 설정 화면으로 이동할지 선택하게 합니다.
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setMessage("위치 권한 거부시 앱을 사용할 수 없습니다.")
            .setPositiveButton("권한 설정하러 가기") { _: DialogInterface, _: Int ->
                // 권한 설정하러 가기 버튼 클릭시 해당 앱의 상세 설정 화면으로 이동합니다.
                try {
                    val intent =
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:$packageName"))
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                } finally {
                    finish()
                }
            }
            .setNegativeButton("앱 종료하기") { _: DialogInterface, _: Int -> finish() }
            // 앱 종료하기 버튼 클릭시 앱을 종료합니다.
            .setCancelable(false)
            .show()
    }

    // 액티비티가 파괴될 때 호출되는 메서드입니다.
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}