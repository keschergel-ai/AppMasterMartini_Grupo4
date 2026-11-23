package com.example.martinibackend.service;

import com.example.martinibackend.model.ClienteEntity;
import com.example.martinibackend.repository.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * [ClienteService]
 *
 * 1. Qué hace: Contiene la logica de negocio CRUD para gestionar clientes gastronomicos.
 *    Implementa [CommandLineRunner] para precargar automaticamente 2 locales de prueba
 *    (pasteleria / cafeteria) si la base de datos se encuentra vacia al iniciar.
 * 2. Conexiones: Utiliza [ClienteRepository] y es consumido por [ClienteController].
 * 3. Rol: Capa de Servicios de Negocio (Service Layer).
 */
@Service
public class ClienteService implements CommandLineRunner {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Precarga automatica de datos de prueba si la tabla 'clientes' esta vacia.
     */
    @Override
    public void run(String... args) throws Exception {
        if (clienteRepository.count() == 0) {
            ClienteEntity local1 = new ClienteEntity(
                "Pasteleria Dulce Martini",
                "+56 9 1234 5678",
                "contacto@dulcemartini.cl",
                "Av. Providencia 1234, Santiago",
                -33.4250,
                -70.6120,
                ""
            );

            ClienteEntity local2 = new ClienteEntity(
                "Panaderia y Cafeteria Artesanal",
                "+56 9 8765 4321",
                "ventas@panaderiaartesanal.cl",
                "Calle Barrio Italia 567, Santiago",
                -33.4450,
                -70.6280,
                ""
            );

            clienteRepository.save(local1);
            clienteRepository.save(local2);
            System.out.println(">>> [INIT] Base de datos vacia detectada: Precargados 2 locales de pasteleria/cafeteria de ejemplo.");
        }
    }

    /**
     * Obtiene la lista completa de clientes.
     */
    public List<ClienteEntity> listarTodos() {
        return clienteRepository.findAll();
    }

    /**
     * Busca un cliente por su ID unico.
     */
    public Optional<ClienteEntity> obtenerPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Guarda un nuevo cliente en la base de datos.
     */
    public ClienteEntity guardar(ClienteEntity cliente) {
        // Aseguramos que el ID sea autogenerado si viene nulo o 0
        if (cliente.getId() != null && cliente.getId() == 0) {
            cliente.setId(null);
        }
        return clienteRepository.save(cliente);
    }

    /**
     * Actualiza un cliente existente por su ID.
     */
    public Optional<ClienteEntity> actualizar(Long id, ClienteEntity clienteNuevo) {
        return clienteRepository.findById(id).map(clienteExistente -> {
            clienteExistente.setNombre(clienteNuevo.getNombre());
            clienteExistente.setTelefono(clienteNuevo.getTelefono());
            clienteExistente.setCorreo(clienteNuevo.getCorreo());
            clienteExistente.setDireccion(clienteNuevo.getDireccion());
            if (clienteNuevo.getLatitud() != null) {
                clienteExistente.setLatitud(clienteNuevo.getLatitud());
            }
            if (clienteNuevo.getLongitud() != null) {
                clienteExistente.setLongitud(clienteNuevo.getLongitud());
            }
            if (clienteNuevo.getFotoUri() != null) {
                clienteExistente.setFotoUri(clienteNuevo.getFotoUri());
            }
            return clienteRepository.save(clienteExistente);
        });
    }

    /**
     * Elimina un cliente por su ID.
     */
    public boolean eliminar(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
