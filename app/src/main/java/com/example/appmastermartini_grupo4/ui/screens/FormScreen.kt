package com.example.appmastermartini_grupo4.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.appmastermartini_grupo4.R
import com.example.appmastermartini_grupo4.ui.viewmodel.ClientViewModel

/**
 * [FormScreen]
 *
 * 1. Qué hace: Formulario para crear/editar clientes gastronómicos con validación estricta en tiempo real de los 4 campos obligatorios, cámara nativa, botón de muestra de vitrina y GPS con LocationManager.
 * 2. Conexiones: Conecta con [ClientViewModel] para procesar y guardar la entidad.
 * 3. Rol en MVVM: Vista (UI Layer) para Entrada y Modificación de Datos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    viewModel: ClientViewModel,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var bitmapResult by remember { mutableStateOf<Bitmap?>(null) }
    var locationStatus by remember { mutableStateOf("GPS no obtenido") }

    // Launcher para Cámara Nativa (cumplimiento de hardware)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        if (bitmap != null) {
            bitmapResult = bitmap
            viewModel.setFotoUri("captured_bitmap_${System.currentTimeMillis()}")
        }
    }

    // Launcher para Permiso de Cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        }
    }

    // Launcher para Permiso de Ubicación
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            obtainGpsLocation(context, viewModel) { status ->
                locationStatus = status
            }
        } else {
            viewModel.setLocation(-33.4489, -70.6693)
            locationStatus = "Permiso denegado. Coordenadas por defecto (Santiago: Lat -33.4489, Lon -70.6693)"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Cliente Gastronómico") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Campo 1: Nombre con Validación Estricta (*)
            OutlinedTextField(
                value = viewModel.nombreInput,
                onValueChange = { viewModel.onNombreChanged(it) },
                label = { Text("Nombre del Local *") },
                isError = viewModel.nombreError != null,
                supportingText = {
                    viewModel.nombreError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.nombreError != null) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo 2: Teléfono con Validación Estricta (*)
            OutlinedTextField(
                value = viewModel.telefonoInput,
                onValueChange = { viewModel.onTelefonoChanged(it) },
                label = { Text("Teléfono de Contacto *") },
                isError = viewModel.telefonoError != null,
                supportingText = {
                    viewModel.telefonoError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.telefonoError != null) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo 3: Correo con Validación Estricta (*)
            OutlinedTextField(
                value = viewModel.correoInput,
                onValueChange = { viewModel.onCorreoChanged(it) },
                label = { Text("Correo Electrónico *") },
                isError = viewModel.correoError != null,
                supportingText = {
                    viewModel.correoError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.correoError != null) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo 4: Dirección con Validación Estricta (*)
            OutlinedTextField(
                value = viewModel.direccionInput,
                onValueChange = { viewModel.onDireccionChanged(it) },
                label = { Text("Dirección del Local *") },
                isError = viewModel.direccionError != null,
                supportingText = {
                    viewModel.direccionError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                trailingIcon = {
                    if (viewModel.direccionError != null) {
                        Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Recurso Nativo 1: Cámara Nativa
            OutlinedButton(
                onClick = {
                    val hasCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    if (hasCamera) {
                        cameraLauncher.launch()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tomar Foto del Local (Cámara)")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Secundario: Cargar Foto de Vitrina (Muestra)
            OutlinedButton(
                onClick = {
                    val sampleBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_pasteleria_vitrina)
                    bitmapResult = sampleBitmap
                    viewModel.setFotoUri("vitrina_sample")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cargar Foto de Vitrina (Muestra)")
            }

            bitmapResult?.let { bmp ->
                Spacer(modifier = Modifier.height(10.dp))
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Vista previa de la foto",
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } ?: run {
                if (viewModel.fotoUriInput == "vitrina_sample") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_pasteleria_vitrina),
                        contentDescription = "Muestra de Vitrina",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón Recurso Nativo 2: GPS LocationManager
            OutlinedButton(
                onClick = {
                    val hasLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    if (hasLocation) {
                        obtainGpsLocation(context, viewModel) { status ->
                            locationStatus = status
                        }
                    } else {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Obtener Ubicación GPS Actual")
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = locationStatus,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Guardar Cliente
            Button(
                onClick = {
                    viewModel.saveClient {
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cliente (Room DB + Microservicio)")
            }
        }
    }
}

/**
 * Función auxiliar para obtener la ubicación GPS mediante [LocationManager].
 * Si el GPS del emulador devuelve null o no está disponible, asigna automáticamente coordenadas válidas por defecto (Santiago, Chile) sin lanzar errores.
 */
@Suppress("DEPRECATION")
private fun obtainGpsLocation(context: Context, viewModel: ClientViewModel, onStatus: (String) -> Unit) {
    val defaultLat = -33.4489
    val defaultLon = -70.6693

    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = try { locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (_: Exception) { false }
        val isNetworkEnabled = try { locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { false }

        if (!isGpsEnabled && !isNetworkEnabled) {
            viewModel.setLocation(defaultLat, defaultLon)
            onStatus("GPS y Red deshabilitados. Ubicación por defecto (Santiago: Lat $defaultLat, Lon $defaultLon)")
            return
        }

        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            viewModel.setLocation(defaultLat, defaultLon)
            onStatus("Sin permiso. Ubicación por defecto (Santiago: Lat $defaultLat, Lon $defaultLon)")
            return
        }

        val locationListener = LocationListener { location ->
            viewModel.setLocation(location.latitude, location.longitude)
            onStatus("GPS OK: Lat ${location.latitude}, Lon ${location.longitude}")
        }

        if (isGpsEnabled) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, locationListener)
            } catch (_: Exception) {
            }
        } else {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 1f, locationListener)
            } catch (_: Exception) {
            }
        }

        val lastLocation = try {
            (if (isGpsEnabled) locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) else null)
                ?: (if (isNetworkEnabled) locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) else null)
                ?: locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        } catch (_: Exception) {
            null
        }

        if (lastLocation != null) {
            viewModel.setLocation(lastLocation.latitude, lastLocation.longitude)
            onStatus("Ubicación obtenida: Lat ${lastLocation.latitude}, Lon ${lastLocation.longitude}")
        } else {
            // GPS devolvió null -> Asigna automáticamente coordenadas por defecto (Santiago, Chile) sin lanzar errores
            viewModel.setLocation(defaultLat, defaultLon)
            onStatus("GPS devolvió null. Ubicación por defecto (Santiago: Lat $defaultLat, Lon $defaultLon)")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        viewModel.setLocation(defaultLat, defaultLon)
        onStatus("Error GPS: ${e.localizedMessage}. Ubicación por defecto (Santiago: Lat $defaultLat, Lon $defaultLon)")
    }
}
