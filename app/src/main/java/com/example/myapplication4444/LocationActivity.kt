package com.example.myapplication4444

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication4444.databinding.ActivityLocationBinding
import com.example.myapplication4444.model.Place
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouter
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.network.NetworkError

class LocationActivity : BaseActivity(), LocationListener {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var mapView: MapView
    private lateinit var map: Map
    private lateinit var locationManager: LocationManager
    private var userLocation: Point? = null
    private var selectedPlaces = listOf<Place>()

    private val inputListener = object : InputListener {
        override fun onMapTap(map: Map, point: Point) = Unit
        override fun onMapLongTap(map: Map, point: Point) {
            routePoints = routePoints + point
        }
    }

    private val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
        override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
            routes = drivingRoutes
        }
        override fun onDrivingRoutesError(error: Error) {
            when (error) {
                is NetworkError -> showToast("Routes request error due network issues")
                else -> showToast("Routes request unknown error")
            }
        }
    }

    private var routePoints = emptyList<Point>()
        set(value) {
            field = value
            onRoutePointsUpdated()
        }
    private var routes = emptyList<DrivingRoute>()
        set(value) {
            field = value
            onRoutesUpdated()
        }

    private lateinit var drivingRouter: DrivingRouter
    private var drivingSession: DrivingSession? = null
    private lateinit var placemarksCollection: MapObjectCollection
    private lateinit var routesCollection: MapObjectCollection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.initialize(this)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapview
        map = mapView.mapWindow.map
        map.addInputListener(inputListener)

        val mapkitVersionTextView = binding.mapkitVersion.findViewById<TextView>(R.id.mapkit_version_value)
        mapkitVersionTextView.text = MapKitFactory.getInstance().version

        placemarksCollection = map.mapObjects.addCollection()
        routesCollection = map.mapObjects.addCollection()

        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)

        binding.buttonClearRoute.setOnClickListener {
            routePoints = emptyList()
        }

        // Set up bottom navigation
        setupBottomNavigation(binding.bottomNavigation, R.id.nav_location)

        // Handle back navigation
        binding.back.setOnClickListener {
            onBackPressed()
        }

        // Get selected places from Intent
        selectedPlaces = intent.getParcelableArrayListExtra<Place>("selected_places") ?: listOf()

        initializeLocationManager()

        binding.scanIcon.setOnClickListener {
            val intent = Intent(this, QRCodeScannerActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeLocationManager() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        userLocation = Point(location.latitude, location.longitude)
        map.move(
            CameraPosition(userLocation!!, 14.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 1.5f),
            null
        )
        placemarksCollection.clear()
        placemarksCollection.addPlacemark(userLocation!!, ImageProvider.fromResource(this, R.drawable.arrow))

        if (selectedPlaces.isNotEmpty()) {
            buildRouteFromUserLocation()
        }
    }

    private fun buildRouteFromUserLocation() {
        val sortedPlaces = selectedPlaces.sortedBy { place ->
            val results = FloatArray(1)
            Location.distanceBetween(userLocation!!.latitude, userLocation!!.longitude, place.latitude, place.longitude, results)
            results[0]
        }

        routePoints = listOf(userLocation!!) + sortedPlaces.map { Point(it.latitude, it.longitude) }
        onRoutePointsUpdated()
    }

    private fun onRoutePointsUpdated() {
        placemarksCollection.clear()

        if (routePoints.isEmpty()) {
            drivingSession?.cancel()
            routes = emptyList()
            return
        }

        val imageProvider = ImageProvider.fromResource(this, R.drawable.location_pin)
        routePoints.forEach {
            placemarksCollection.addPlacemark(it, imageProvider)
        }

        if (routePoints.size < 2) return

        val requestPoints = buildList {
            add(RequestPoint(routePoints.first(), RequestPointType.WAYPOINT, null, null))
            addAll(
                routePoints.subList(1, routePoints.size - 1)
                    .map { RequestPoint(it, RequestPointType.VIAPOINT, null, null) })
            add(RequestPoint(routePoints.last(), RequestPointType.WAYPOINT, null, null))
        }

        val drivingOptions = DrivingOptions()
        val vehicleOptions = VehicleOptions()

        drivingSession = drivingRouter.requestRoutes(
            requestPoints,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener,
        )
    }

    private fun onRoutesUpdated() {
        routesCollection.clear()
        if (routes.isEmpty()) return

        routes.forEachIndexed { index, route ->
            routesCollection.addPolyline(route.geometry).apply {
                if (index == 0) styleMainRoute() else styleAlternativeRoute()
            }
        }
    }

    private fun PolylineMapObject.styleMainRoute() {
        zIndex = 10f
        setStrokeColor(ContextCompat.getColor(this@LocationActivity, R.color.gray))
        strokeWidth = 5f
        outlineColor = ContextCompat.getColor(this@LocationActivity, R.color.black)
        outlineWidth = 3f
    }

    private fun PolylineMapObject.styleAlternativeRoute() {
        zIndex = 5f
        setStrokeColor(ContextCompat.getColor(this@LocationActivity, R.color.light_blue))
        strokeWidth = 4f
        outlineColor = ContextCompat.getColor(this@LocationActivity, R.color.black)
        outlineWidth = 2f
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                initializeLocationManager()
            } else {
                showToast("Permission denied")
            }
        }
    }

    override fun onProviderEnabled(provider: String) {
        // Do something when the provider is enabled
    }

    override fun onProviderDisabled(provider: String) {
        // Do something when the provider is disabled
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Do something when the status changes
    }

    override fun onResume() {
        super.onResume()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onPause() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onPause()
    }

    companion object {
        private const val locationPermissionCode = 1
    }
}
