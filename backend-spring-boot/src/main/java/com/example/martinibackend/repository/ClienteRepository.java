package com.example.martinibackend.repository;

import com.example.martinibackend.model.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * [ClienteRepository]
 *
 * 1. Qué hace: Proporciona metodos CRUD automaticos (findAll, findById, save, deleteById)
 *    para la entidad [ClienteEntity] mediante Spring Data JPA.
 * 2. Conexiones: Inyectado en [ClienteService].
 * 3. Rol: Capa de Acceso a Datos (Data Access Object / Repository).
 */
@Repository
public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
}
