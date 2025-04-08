package cat.copernic.amayo.frontend.rutaManagment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Looper
import android.view.MotionEvent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color  // Compose Color
import androidx.compose.ui.graphics.Color.Companion.Unspecified
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import cat.copernic.amayo.frontend.R
import cat.copernic.amayo.frontend.navigation.BottomNavigationBar
import com.google.android.gms.location.*
import org.osmdroid.config.Configuration
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

    // Configuración de OSMDroid (idealmente en la Application)
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))

    // Estados del mapa y de la ubicación
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }

    // Estados para la ruta
    var isRouting by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    val routePoints = remember { mutableStateListOf<GeoPoint>() }
    var polyline by remember { mutableStateOf<Polyline?>(null) }
    var autoCenter by remember { mutableStateOf(true) }

    // Cliente de localización
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // Solicitar permiso de ubicación
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
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

    // Actualizar mapa y polyline en cada cambio de ubicación
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            val currentZoom = mapView?.zoomLevelDouble ?: 15.0
            if (autoCenter) {
                mapView?.controller?.setCenter(loc)
                mapView?.controller?.setZoom(currentZoom)
            }
            // Actualizar marcador "Ubicación actual"
            mapView?.overlays?.removeAll { it is Marker && it.title == "Ubicación actual" }
            mapView?.let { mv -> addMarker(mv, loc, "Ubicación actual", context) }
            // Si se está grabando la ruta y no está en pausa, agregar el punto
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
        bottomBar = { BottomNavigationBar(navController) } // Barra de navegación global
    ) { innerPadding ->
        // Box para apilar el mapa como fondo y controles (overlay) encima
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF9CF3FF)) // Fondo azul opaco, igual que recompensas
                .padding(innerPadding)
        ) {
            // Contenedor con clipToBounds para recortar el mapa al tamaño de pantalla
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clipToBounds()
            ) {
                // Mapa ocupando toda la pantalla, pero escalado un 10% más para precargar tiles extra
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = 1.1f
                            scaleY = 1.1f
                        },
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
            // Overlay inferior para los botones de ruta con fondo azul opaco
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)  // Altura reducida
                    .background(Color(0xFF9CF3FF))  // Azul opaco
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp), // Menos margen horizontal
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Recenter (📍)
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
                        // Botón para iniciar la ruta (Play)
                        IconButton(
                            onClick = {
                                isRouting = true
                                isPaused = false
                                routePoints.clear()
                                polyline = null
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_play),
                                contentDescription = "Iniciar ruta",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                        // Botón Stop (desactivado)
                        IconButton(onClick = { }, enabled = false) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_stop),
                                contentDescription = "Detener ruta",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                    } else {
                        // Botón para pausar / reanudar (Pause/Play)
                        IconButton(onClick = { isPaused = !isPaused }) {
                            Icon(
                                painter = painterResource(
                                    if (isPaused) R.drawable.ic_play else R.drawable.ic_pause
                                ),
                                contentDescription = if (isPaused) "Reanudar" else "Pausar",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                        // Botón para detener la ruta (Stop)
                        IconButton(onClick = {
                            isRouting = false
                            isPaused = false
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_stop),
                                contentDescription = "Detener ruta",
                                modifier = Modifier.size(64.dp),
                                tint = Unspecified
                            )
                        }
                    }
                }
            }
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
