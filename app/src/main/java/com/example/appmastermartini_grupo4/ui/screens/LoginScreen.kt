package com.example.appmastermartini_grupo4.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.appmastermartini_grupo4.R

/**
 * [LoginScreen]
 *
 * 1. Qué hace: Pantalla de inicio de sesión con validación de credenciales, conmutador de visibilidad de contraseña y botón de autocompletado rápido para agilizar la defensa oral.
 * 2. Conexiones: Valida la entrada y navega a [HomeScreen] mediante `onNavigateToHome`.
 * 3. Rol en MVVM: Vista (UI Layer) de Autenticación de Usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit
) {
    val scrollState = rememberScrollState()

    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validateAndLogin() {
        emailError = LoginValidator.validateEmail(emailInput)
        passwordError = LoginValidator.validatePassword(passwordInput)

        if (LoginValidator.isValid(emailInput, passwordInput)) {
            onNavigateToHome()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Martini Kevin - Iniciar Sesión") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ícono de marca
            Image(
                painter = painterResource(id = R.drawable.ic_pasteleria_vitrina),
                contentDescription = "Logo Pastelería",
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Acceso a la Plataforma",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Correo Institucional
            OutlinedTextField(
                value = emailInput,
                onValueChange = {
                    emailInput = it
                    if (emailError != null) emailError = null
                },
                label = { Text("Correo Institucional *") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                isError = emailError != null,
                supportingText = {
                    emailError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Contraseña
            OutlinedTextField(
                value = passwordInput,
                onValueChange = {
                    passwordInput = it
                    if (passwordError != null) passwordError = null
                },
                label = { Text("Contraseña *") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (passwordVisible) "Ocultar clave" else "Mostrar clave"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Autocompletado Demo
            OutlinedButton(
                onClick = {
                    emailInput = "kevin@martini.cl"
                    passwordInput = "Martini123!"
                    emailError = null
                    passwordError = null
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Autocompletar Demo (kevin@martini.cl)")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botón Iniciar Sesión
            Button(
                onClick = { validateAndLogin() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

/**
 * [LoginValidator]
 *
 * 1. Qué hace: Helper reutilizable para validar credenciales de correo y contraseña en Login.
 * 2. Conexiones: Utilizado por [LoginScreen] y evaluado en las pruebas unitarias.
 * 3. Rol en MVVM: Lógica de validación de autenticación de usuario.
 */
object LoginValidator {
    fun validateEmail(email: String): String? {
        return if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            "Ingrese un correo institucional válido (ej: kevin@martini.cl)"
        } else {
            null
        }
    }

    fun validatePassword(password: String): String? {
        return if (password.isBlank() || password.length < 4) {
            "La contraseña debe tener al menos 4 caracteres"
        } else {
            null
        }
    }

    fun isValid(email: String, password: String): Boolean {
        return validateEmail(email) == null && validatePassword(password) == null
    }
}
