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

    // ViewModel y contexto
    val context = LocalContext.current
    val app     = context.applicationContext as Application
    val rutaVM: RutaViewModel = viewModel(
        factory    = ViewModelProvider.AndroidViewModelFactory(app),
        modelClass = RutaViewModel::class.java
    )

    // Estados observados
    val isRouting     by remember { derivedStateOf { rutaVM.isRouting } }
    val routeSegments by remember { derivedStateOf { rutaVM.routeSegments } }
    val pending       by remember { derivedStateOf { rutaVM.pendingStats } }

    // Estados de UI
    var mapView            by remember { mutableStateOf<MapView?>(null) }
    var userLocation       by remember { mutableStateOf<GeoPoint?>(null) }
    var autoCenter         by remember { mutableStateOf(true) }
    var showLoadingText    by remember { mutableStateOf(true) }
    var showDiscardConfirm by remember { mutableStateOf(false) }

    // Inicializamos ubicación
    val fused = LocationServices.getFusedLocationProviderClient(context)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) startLocationUpdates(fused) { userLocation = it }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            startLocationUpdates(fused) { userLocation = it }
        }
    }

    // Cada vez que llega una nueva posición
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            if (showLoadingText) showLoadingText = false

            mapView?.let { map ->
                // Centro automático
                if (autoCenter) {
                    map.controller.setCenter(loc)
                    map.controller.setZoom(map.zoomLevelDouble)
                }
                // Actualizar marcador actual
                removeCurrentLocationMarker(map)
                addMarker(map, loc, "Ubicación actual", context)
                // Si está grabando, añadimos punto y redibujamos ruta
                if (isRouting) {
                    rutaVM.addPoint(loc)
                    clearPolylines(map)
                    routeSegments.forEach { segment ->
                        if (segment.size > 1) {
                            Polyline().apply {
                                setPoints(segment)
                                outlinePaint.color = AndroidColor.BLUE
                                outlinePaint.strokeWidth = 5f
                            }.also { map.overlayManager.add(0, it) }
                        }
                    }
                }
                map.invalidate()
            }
        }
    }

    // Animación “Cargando mapa…”
    val alphaAnim = rememberInfiniteTransition().animateFloat(
        initialValue  = 0.5f,
        targetValue   = 1f,
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

            // Barra de controles en la parte inferior
            Box(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                BottomControls(
                    isRouting  = isRouting,
                    onStart    = { rutaVM.startRoute("Mi ruta", "Salida en bici", userLocation) },
                    onStop     = { rutaVM.requestStop() },
                    onRecenter = {
                        userLocation?.let {
                            autoCenter = true
                            mapView?.controller?.setCenter(it)
                        }
                    }
                )
            }

            // Overlay de carga
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

            // Diálogo de resumen de ruta
            pending?.let { stats ->
                RouteSummaryDialog(
                    stats     = stats,
                    initName  = rutaVM.nomRuta,
                    initDesc  = rutaVM.descRuta,
                    onSave    = { n, d ->
                        rutaVM.saveRoute(n, d)
                        mapView?.let { clearPolylines(it) }
                    },
                    onDiscard = {
                        showDiscardConfirm = true
                    }
                )
            }

            // Confirmación antes de descartar
            if (showDiscardConfirm) {
                AlertDialog(
                    onDismissRequest = { showDiscardConfirm = false },
                    title   = { Text("Confirmar descarte") },
                    text    = { Text("¿Estás seguro de que quieres descartar la ruta?") },
                    confirmButton = {
                        TextButton(onClick = {
                            rutaVM.discardRoute()
                            mapView?.let { clearPolylines(it) }
                            showDiscardConfirm = false
                        }) {
                            Text("Sí")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDiscardConfirm = false }) {
                            Text("No")
                        }
                    }
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    fused: FusedLocationProviderClient,
    onLocationChanged: (GeoPoint) -> Unit
) {
    val req = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 1_000L
    ).apply {
        setMinUpdateIntervalMillis(2_000L)
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

private fun clearPolylines(map: MapView) {
    val it = map.overlayManager.iterator()
    while (it.hasNext()) {
        if (it.next() is Polyline) it.remove()
    }
    map.invalidate()
}

private fun removeCurrentLocationMarker(map: MapView) {
    val it = map.overlayManager.iterator()
    while (it.hasNext()) {
        val o = it.next()
        if (o is Marker && o.title == "Ubicación actual") it.remove()
    }
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
        icon     = ContextCompat.getDrawable(context, R.drawable.ic_blue_dot)
        this.title = title
        map.overlayManager.add(this)
    }
}

@Composable
private fun BottomControls(
    isRouting: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onRecenter: () -> Unit
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color(0xFF9CF3FF)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onRecenter) {
                Text("📍", fontSize = 30.sp, fontWeight = FontWeight.Bold)
            }
            if (!isRouting) {
                IconButton(onClick = onStart) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = "Iniciar ruta",
                        modifier = Modifier.size(64.dp),
                        tint = Unspecified
                    )
                }
                IconButton(onClick = {}, enabled = false) {
                    Icon(
                        painter = painterResource(R.drawable.ic_stop),
                        contentDescription = "Detener ruta",
                        modifier = Modifier.size(64.dp),
                        tint = Unspecified
                    )
                }
            } else {
                IconButton(onClick = onStop) {
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
}
