package com.example.appmastermartini_grupo4.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmastermartini_grupo4.R
import com.example.appmastermartini_grupo4.ui.viewmodel.ClientViewModel

/**
 * [DetailScreen]
 *
 * 1. Qué hace: Muestra la vista detallada de un cliente gastronómico, con su foto de vitrina o personalizada, teléfono, correo, dirección, GPS, llamada con Intent [Intent.ACTION_DIAL] y opción de eliminación con [AlertDialog].
 * 2. Conexiones: Obtiene los datos de [ClientViewModel].
 * 3. Rol en MVVM: Vista (UI Layer) de Detalle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    clientId: Int,
    viewModel: ClientViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val context = LocalContext.current
    val clients by viewModel.clients.collectAsState()
    val client = clients.find { it.id == clientId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Está seguro de eliminar este cliente gastronómico del sistema?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteClient(clientId) {
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Cliente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Menu, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (client == null) {
                Text(
                    text = "Cliente no encontrado",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto del Local / Vitrina de Pastelería
                    Image(
                        painter = painterResource(id = R.drawable.ic_pasteleria_vitrina),
                        contentDescription = "Foto del Local / Vitrina",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = client.nombre,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider()
                            Text(
                                text = "Teléfono: ${client.telefono.ifEmpty { "No registrado" }}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Correo: ${client.correo.ifEmpty { "No registrado" }}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Dirección: ${client.direccion.ifEmpty { "No registrada" }}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Ubicación GPS: Lat ${client.latitud}, Lon ${client.longitud}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Botón de Llamada Telefónica (Intent ACTION_DIAL)
                    Button(
                        onClick = {
                            if (client.telefono.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${client.telefono}")
                                }
                                context.startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Llamar al Local (${client.telefono})")
                    }
                }
            }
        }
    }
}
