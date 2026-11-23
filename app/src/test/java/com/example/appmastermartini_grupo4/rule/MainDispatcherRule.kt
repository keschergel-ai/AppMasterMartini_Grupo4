package com.example.appmastermartini_grupo4.rule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * [MainDispatcherRule]
 *
 * 1. Qué hace: Regla de JUnit 4 que reemplaza el `Dispatchers.Main` de Kotlin Coroutines por un
 *    [UnconfinedTestDispatcher] durante las pruebas unitarias.
 * 2. Conexiones: Utilizado en las clases de prueba de ViewModels para ejecutar corrutinas de manera sincrónica y controlada.
 * 3. Rol en Testing: Configuración global de Dispatcher para evitar excepciones de Main Looper de Android en JUnit.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
