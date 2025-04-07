package cat.copernic.amayo.frontend.rutaManagment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import android.graphics.Color as AndroidColor
import android.os.Looper
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import cat.copernic.amayo.frontend.R
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@SuppressLint("MissingPermission", "ClickableViewAccessibility")
@Composable
fun RutaScreen() {
    val context = LocalContext.current

    // OSMDroid config
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    // Estados
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }

    var isRouting by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    val routePoints = remember { mutableStateListOf<GeoPoint>() }
    var polyline by remember { mutableStateOf<Polyline?>(null) }
    var autoCenter by remember { mutableStateOf(true) }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Pedir permiso
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startLocationUpdates(fusedLocationClient) { newLocation ->
                userLocation = newLocation
            }
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates(fusedLocationClient) { newLocation ->
                userLocation = newLocation
            }
        }
    }

    // Al cambiar userLocation
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            val currentZoom = mapView?.zoomLevelDouble ?: 15.0

            if (autoCenter) {
                mapView?.controller?.setCenter(loc)
                mapView?.controller?.setZoom(currentZoom)
            }

            // Actualizar marcador de "Ubicación actual"
            mapView?.overlays?.removeAll { it is Marker && it.title == "Ubicación actual" }
            mapView?.let { mv ->
                addMarker(mv, loc, "Ubicación actual", context)
            }

            // Añadir puntos a la polyline
            if (isRouting && !isPaused) {
                routePoints.add(loc)
                if (polyline == null) {
                    polyline = Polyline().apply {
                        setPoints(routePoints)
                        outlinePaint.color = AndroidColor.BLUE
                        outlinePaint.strokeWidth = 5f
                    }
                    mapView?.overlays?.add(polyline)
                } else {
                    polyline?.setPoints(routePoints)
                }
            }
            mapView?.invalidate()
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(72.dp),
                containerColor = ComposeColor.White,
                contentColor = ComposeColor.Black,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                // Botón recenter (📍) - se deja igual
                IconButton(
                    onClick = {
                        userLocation?.let {
                            autoCenter = true
                            mapView?.controller?.setCenter(it)
                            mapView?.controller?.setZoom(mapView?.zoomLevelDouble ?: 15.0)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("📍", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }

                if (!isRouting) {
                    // Botón para iniciar la ruta (Play)
                    IconButton(
                        onClick = {
                            isRouting = true
                            isPaused = false
                            routePoints.clear()
                            polyline = null
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Iniciar ruta",
                            modifier = Modifier.size(48.dp), // Aumentado a 48.dp
                            tint = Color.Unspecified
                        )
                    }
                    // Botón de detener desactivado (Stop)
                    IconButton(
                        onClick = { /* sin acción */ },
                        modifier = Modifier.weight(1f),
                        enabled = false
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stop),
                            contentDescription = "Detener ruta",
                            modifier = Modifier.size(48.dp), // Aumentado a 48.dp
                            tint = Color.Unspecified
                        )
                    }
                } else {
                    // Botón para pausar/reanudar (Pause/Play)
                    IconButton(
                        onClick = { isPaused = !isPaused },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(
                                if (isPaused) R.drawable.ic_play else R.drawable.ic_pause
                            ),
                            contentDescription = if (isPaused) "Reanudar" else "Pausar",
                            modifier = Modifier.size(48.dp), // Aumentado a 48.dp
                            tint = Color.Unspecified
                        )
                    }
                    // Botón para detener la ruta (Stop)
                    IconButton(
                        onClick = {
                            isRouting = false
                            isPaused = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_stop),
                            contentDescription = "Detener ruta",
                            modifier = Modifier.size(48.dp), // Aumentado a 48.dp
                            tint = Color.Unspecified
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        setOnTouchListener { _, event ->
                            if (event.action == MotionEvent.ACTION_DOWN) {
                                autoCenter = false
                            }
                            false
                        }
                    }.also { mapView = it }
                }
            )
        }
    }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationChanged: (GeoPoint) -> Unit
) {
    val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        3000L
    ).apply {
        setMinUpdateIntervalMillis(2000L)
        setMinUpdateDistanceMeters(5f)
        setWaitForAccurateLocation(false)
    }.build()

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { loc ->
                onLocationChanged(GeoPoint(loc.latitude, loc.longitude))
            }
        }
    }

    fusedLocationClient.requestLocationUpdates(
        locationRequest,
        locationCallback,
        Looper.getMainLooper()
    )
}

private fun addMarker(
    map: MapView,
    point: GeoPoint,
    title: String,
    context: android.content.Context
) {
    Marker(map).apply {
        position = point
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        icon = ContextCompat.getDrawable(context, R.drawable.ic_blue_dot)
        this.title = title
        map.overlays.add(this)
    }
}
