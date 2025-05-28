package cat.copernic.amayo.frontend.sistemaManagment.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cat.copernic.amayo.frontend.rutaManagment.data.remote.RutaApi
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

private const val MIN_MARKER_ZOOM = 13.0  // zoom mínimo para mostrar marcadores

/**
 * Crea un BitmapDrawable con la forma de un pin:
 * un círculo (diámetro = d) y un triángulo apuntando hacia abajo.
 * El pin se rellena con el color dado.
 */
private fun createPinMarker(ctx: android.content.Context, diameterPx: Int, fillColor: Int): BitmapDrawable {
    val tailHeight = diameterPx / 2
    val width = diameterPx
    val height = diameterPx + tailHeight
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)

    val paintFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = fillColor
    }
    val paintStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = diameterPx * 0.05f
        color = AndroidColor.WHITE
    }

    // Círculo
    val radius = diameterPx / 2f
    canvas.drawCircle(radius, radius, radius, paintFill)
    canvas.drawCircle(radius, radius, radius, paintStroke)

    // Triángulo (cola)
    val path = android.graphics.Path().apply {
        moveTo(radius / 2, diameterPx.toFloat())
        lineTo(radius, (diameterPx + tailHeight).toFloat())
        lineTo(radius + radius / 2, diameterPx.toFloat())
        close()
    }
    canvas.drawPath(path, paintFill)
    canvas.drawPath(path, paintStroke)

    return BitmapDrawable(ctx.resources, bmp)
}

@Composable
fun rememberOsmMapView(): MapView {
    val context = LocalContext.current
    return remember {
        Configuration.getInstance().load(
            context,
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        )
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            setUseDataConnection(true)
            zoomController.setVisibility(
                org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER
            )
        }
    }
}

@Composable
fun RouteDetailsDialog(
    ruta: RutaApi.RutaDto,
    onDismiss: () -> Unit
) {
    val mapView = rememberOsmMapView()
    val ctx = LocalContext.current

    // Icono de pin rojo de 16 dp
    val diameterDp = 16.dp
    val density = LocalContext.current.resources.displayMetrics.density
    val diameterPx = (diameterDp.value * density).toInt()
    val pinIcon = remember {
        createPinMarker(ctx, diameterPx, AndroidColor.RED)
    }

    DisposableEffect(mapView) {
        mapView.onResume()
        onDispose { mapView.onPause() }
    }

    var showMarkers by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 8.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        title = {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        ruta.nom,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        ruta.descripcio,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Desat el: ${ruta.fechaCreacion}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                IconButton(onClick = { showMarkers = !showMarkers }) {
                    Icon(
                        imageVector = if (showMarkers) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showMarkers) "Ocultar punts" else "Mostrar punts"
                    )
                }
            }
        },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { mapView },
                        update = { mv ->
                            mv.overlays.clear()
                            val pts = ruta.posicions.map { GeoPoint(it.latitud, it.longitud) }

                            if (pts.size > 1) {
                                // Múltiples puntos: zoom al bounding box
                                val lats = pts.map { it.latitude }
                                val lons = pts.map { it.longitude }
                                val north = lats.maxOrNull()?.coerceIn(-85.0511, 85.0511) ?: pts.first().latitude
                                val south = lats.minOrNull()?.coerceIn(-85.0511, 85.0511) ?: pts.first().latitude
                                val east = lons.maxOrNull()?.coerceIn(-180.0, 180.0) ?: pts.first().longitude
                                val west = lons.minOrNull()?.coerceIn(-180.0, 180.0) ?: pts.first().longitude

                                runCatching {
                                    mv.zoomToBoundingBox(BoundingBox(north, east, south, west), false, 48)
                                }.onFailure {
                                    mv.controller.setCenter(pts.first())
                                    mv.controller.setZoom(14.0)
                                }

                                // Dibujar ruta
                                Polyline().apply {
                                    setPoints(pts)
                                    outlinePaint.color = AndroidColor.BLUE
                                    outlinePaint.strokeWidth = 6f
                                }.also { mv.overlays.add(it) }
                            } else if (pts.size == 1) {
                                // Solo un punto: centrado y zoom fijo
                                mv.controller.setCenter(pts.first())
                                mv.controller.setZoom(14.0)
                            }

                            // Marcadores opcionales si el usuario lo pidió
                            if (showMarkers && mv.zoomLevelDouble >= MIN_MARKER_ZOOM) {
                                pts.forEach { gp ->
                                    Marker(mv).apply {
                                        position = gp
                                        icon = pinIcon
                                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                        setOnMarkerClickListener { _, _ ->
                                            Toast.makeText(
                                                ctx,
                                                "Lat: ${"%.5f".format(gp.latitude)}, Lon: ${"%.5f".format(gp.longitude)}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            true
                                        }
                                    }.also { mv.overlays.add(it) }
                                }
                            }

                            mv.invalidate()
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard("Distancia", "%.2f km".format(ruta.km), Modifier.weight(1f))
                    MetricCard("Temps", formatSecToTime(ruta.temps), Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricCard("Vel. mitja", "%.2f km/h".format(ruta.velMitja), Modifier.weight(1f))
                    MetricCard("Pausa", formatSecToTime(ruta.tempsParat), Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("TANCAR", fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

private fun formatSecToTime(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return "%d:%02d:%02d".format(h, m, s)
}
