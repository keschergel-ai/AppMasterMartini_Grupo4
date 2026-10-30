package com.example.appmastermartini_grupo4.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun FormScreen() {
    // 1. CONTEXTO: Necesario para lanzar Intents del sistema (como abrir el marcador telefónico)
    val context = LocalContext.current

    // 2. ESTADOS: Guardan los datos de la interfaz para que Compose reaccione a los cambios
    var bitmapResult by remember { mutableStateOf<Bitmap?>(null) } // Guarda la foto capturada
    var nombre by remember { mutableStateOf("") }                  // Guarda el texto del cliente
    var isError by remember { mutableStateOf(false) }               // Activa o desactiva la alerta de error

    // 3. RECURSO NATIVO 1 (CÁMARA): Launcher nativo para abrir la cámara sin pedir permisos complejos
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Cuando la foto es capturada, la guardamos en el estado
        bitmapResult = bitmap
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registro de Cliente Gastronómico",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- FORMULARIO CON VALIDACIÓN VISUAL ---
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                // Regla de validación simple: Si el campo queda vacío, marca error
                isError = it.isEmpty()
            },
            label = { Text("Nombre del Local / Cliente") },
            isError = isError,
            trailingIcon = {
                // Si hay error, mostramos un ícono nativo de advertencia dentro del campo
                if (isError) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // --- ANIMACIÓN EN COMPOSE ---
        // 'AnimatedVisibility' hace que el mensaje de error aparezca suavemente y no de golpe
        AnimatedVisibility(
            visible = isError,
            enter = slideInVertically() + fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "El nombre es obligatorio",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- RECURSO NATIVO 1: BOTÓN DE CÁMARA ---
        Button(
            onClick = { cameraLauncher.launch() }, // Dispara el recurso nativo de la cámara
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tomar Foto del Negocio (Cámara)")
        }

        // Si se tomó una foto, la pintamos en la pantalla
        bitmapResult?.let { bmp ->
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // --- RECURSO NATIVO 2: INTENT IMPLÍCITO (LLAMADA) ---
        // Abre la aplicación de teléfono predeterminada del celular
        OutlinedButton(
            onClick = {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:+56912345678")
                }
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Phone, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Llamar a Ejecutivo Master Martini")
        }
    }
}