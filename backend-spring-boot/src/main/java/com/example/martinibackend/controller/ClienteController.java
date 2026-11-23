package com.example.martinibackend.controller;

import com.example.martinibackend.model.ClienteEntity;
import com.example.martinibackend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [ClienteController]
 *
 * 1. Qué hace: Controlador REST que expone los endpoints HTTP consumidos por [MicroserviceApiService] en la app Android.
 *    Ruta Base: /api/clientes
 *    Endpoints:
 *      - GET    /api/clientes      -> Obtener todos los clientes (200 OK)
 *      - GET    /api/clientes/{id} -> Obtener un cliente por ID (200 OK / 404 Not Found)
 *      - POST   /api/clientes      -> Crear un nuevo cliente (201 Created)
 *      - PUT    /api/clientes/{id} -> Actualizar cliente existente (200 OK / 404 Not Found)
 *      - DELETE /api/clientes/{id} -> Eliminar cliente por ID (200 OK / 404 Not Found)
 * 2. Conexiones: Invocado por la app Android via Retrofit (o clientes web/Postman) y utiliza [ClienteService].
 * 3. Rol: Capa de Presentacion REST / API Controller.
 */
@RestController
@RequestMapping("/api/clientes")
@CrossOrigin(origins = "*") // Permite peticiones CORS desde cualquier origen (Android, Frontend Web, Postman)
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * GET /api/clientes
     * Devuelve la lista completa de clientes registrados.
     */
    @GetMapping
    public ResponseEntity<List<ClienteEntity>> getClients() {
        List<ClienteEntity> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    /**
     * GET /api/clientes/{id}
     * Devuelve los datos de un cliente segun su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> getClientById(@PathVariable Long id) {
        return clienteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/clientes
     * Crea y persiste un nuevo cliente en el sistema.
     */
    @PostMapping
    public ResponseEntity<ClienteEntity> createClient(@Valid @RequestBody ClienteEntity client) {
        ClienteEntity creado = clienteService.guardar(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * PUT /api/clientes/{id}
     * Actualiza la informacion de un cliente existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteEntity> updateClient(@PathVariable Long id, @Valid @RequestBody ClienteEntity client) {
        return clienteService.actualizar(id, client)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/clientes/{id}
     * Elimina a un cliente del sistema por su ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        boolean eliminado = clienteService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
