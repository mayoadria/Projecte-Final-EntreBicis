package cat.copernic.amayo.frontend.rutaManagment.ui

import android.Manifest
import android.app.Application
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.R
import cat.copernic.amayo.frontend.navigation.BottomNavigationBar
import cat.copernic.amayo.frontend.rutaManagment.viewmodels.RutaViewModel
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import android.graphics.Color as AndroidColor

@SuppressLint("MissingPermission", "ClickableViewAccessibility")
@Composable
fun RutaScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val rutaVM: RutaViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory(application),
        modelClass = RutaViewModel::class.java
    )

    // Observamos los estados del ViewModel
    val isRouting by remember { derivedStateOf { rutaVM.isRouting } }
    val isPaused  by remember { derivedStateOf { rutaVM.isPaused  } }
    val routeSegments by remember { derivedStateOf { rutaVM.routeSegments } }

    // Estados de la UI
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var autoCenter by remember { mutableStateOf(true) }
    var showLoadingText by remember { mutableStateOf(true) }

    // Cliente de localización y permiso
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startLocationUpdates(fusedLocationClient) { userLocation = it }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates(fusedLocationClient) { userLocation = it }
        }
    }

    // Cada vez que cambia la ubicación
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            if (showLoadingText) showLoadingText = false
            if (autoCenter) {
                mapView?.controller?.setCenter(loc)
                mapView?.controller?.setZoom(mapView?.zoomLevelDouble ?: 15.0)
            }
            mapView?.overlays?.removeAll { it is Marker && it.title == "Ubicación actual" }
            mapView?.let { addMarker(it, loc, "Ubicación actual", context) }

            // Si está grabando y no en pausa, persistimos y dibujamos
            if (isRouting && !isPaused) {
                rutaVM.addPoint(loc)
                mapView?.overlays?.removeAll { it is Polyline }
                routeSegments.forEach { segment ->
                    if (segment.size > 1) {
                        Polyline().apply {
                            setPoints(segment)
                            outlinePaint.color = AndroidColor.BLUE
                            outlinePaint.strokeWidth = 5f
                        }.also { mapView?.overlays?.add(0, it) }
                    }
                }
            }
            mapView?.invalidate()
        }
    }

    // Animación de “Cargando mapa…”
    val alphaAnim = rememberInfiniteTransition().animateFloat(
        initialValue = 0.5f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(tween(500), RepeatMode.Reverse)
    )

    Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0xFF9CF3FF))
                .padding(padding)
        ) {
            // Mapa
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
                    .graphicsLayer { scaleX = 1.1f; scaleY = 1.1f },
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                        setOnTouchListener { _, ev ->
                            if (ev.action == MotionEvent.ACTION_DOWN) autoCenter = false
                            false
                        }
                    }.also { mapView = it }
                }
            )

            // Controles Play / Pause / Stop
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(Color(0xFF9CF3FF))
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Recentrar
                    IconButton(onClick = {
                        userLocation?.let {
                            autoCenter = true
                            mapView?.controller?.setCenter(it)
                            mapView?.controller?.setZoom(mapView?.zoomLevelDouble ?: 15.0)
                        }
                    }) {
                        Text("📍", fontSize = 30.sp, fontWeight = FontWeight.Bold)
                    }

                    if (!isRouting) {
                        // ▶️ Play
                        IconButton(onClick = {
                            rutaVM.startRoute("Mi ruta", "Salida en bici", userLocation)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_play),
                                contentDescription = "Iniciar ruta",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                        // ■ Stop (deshabilitado)
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                painter = painterResource(R.drawable.ic_stop),
                                contentDescription = "Detener ruta",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                    } else {
                        // ⏸️ / ▶️ Pause / Resume
                        IconButton(onClick = { rutaVM.togglePause() }) {
                            Icon(
                                painter = painterResource(
                                    if (isPaused) R.drawable.ic_play else R.drawable.ic_pause
                                ),
                                contentDescription = if (isPaused) "Reanudar" else "Pausar",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                        // ■ Stop
                        IconButton(onClick = { rutaVM.stopRoute() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_stop),
                                contentDescription = "Detener ruta",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                    }
                }
            }

            // Texto “Cargando mapa…”
            if (showLoadingText) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0x99000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Cargando mapa…",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.alpha(alphaAnim.value)
                    )
                }
            }
        }
    }
}

// Funciones auxiliares
@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    fused: FusedLocationProviderClient,
    onLocationChanged: (GeoPoint) -> Unit
) {
    val req = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1000L
    ).apply {
        setMinUpdateIntervalMillis(2000L)
        setMinUpdateDistanceMeters(5f)
        setWaitForAccurateLocation(false)
    }.build()
    val cb = object : LocationCallback() {
        override fun onLocationResult(res: LocationResult) {
            res.lastLocation?.let { loc ->
                onLocationChanged(GeoPoint(loc.latitude, loc.longitude))
            }
        }
    }
    fused.requestLocationUpdates(req, cb, Looper.getMainLooper())
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
