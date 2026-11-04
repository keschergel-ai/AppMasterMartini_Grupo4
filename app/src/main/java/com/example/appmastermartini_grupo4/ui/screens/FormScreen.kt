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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.appmastermartini_grupo4.data.local.AppDatabase
import com.example.appmastermartini_grupo4.data.local.ClientEntity
import kotlinx.coroutines.launch

@Composable
fun FormScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Base de datos Room
    val db = remember { AppDatabase.getDatabase(context) }
    val clientDao = db.clientDao()

    // Consulta reactiva de la DB
    val listaClientes by clientDao.getAllClients().collectAsState(initial = emptyList())

    // Estados de UI
    var bitmapResult by remember { mutableStateOf<Bitmap?>(null) }
    var nombre by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Launcher para la Cámara Nativa
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
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

        Spacer(modifier = Modifier.height(12.dp))

        // Campo con validación visual
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                isError = it.isEmpty()
            },
            label = { Text("Nombre del Local / Cliente") },
            isError = isError,
            trailingIcon = {
                if (isError) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        AnimatedVisibility(
            visible = isError,
            enter = slideInVertically() + fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "El nombre es obligatorio para registrar",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Guardar registro en Room DB
        Button(
            onClick = {
                if (nombre.isNotBlank()) {
                    scope.launch {
                        clientDao.insertClient(ClientEntity(nombre = nombre))
                        nombre = ""
                        isError = false
                    }
                } else {
                    isError = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Guardar Cliente en Base de Datos")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recurso Nativo 1: Cámara
        OutlinedButton(
            onClick = { cameraLauncher.launch() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tomar Foto del Negocio (Cámara)")
        }

        bitmapResult?.let { bmp ->
            Spacer(modifier = Modifier.height(6.dp))
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Foto capturada",
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recurso Nativo 2: Marcador Telefónico (Intent)
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

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Divider()

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Clientes Registrados (Room DB Local):",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(listaClientes) { cliente ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "ID: ${cliente.id} - ${cliente.nombre}",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}