package com.example.martinibackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * [ClienteEntity]
 *
 * 1. Qué hace: Mapea la tabla "clientes" en la base de datos relacional (H2 via JPA/Hibernate).
 *    Los nombres de sus atributos coinciden exactamente con los campos JSON consumidos por la app Android
 *    (id, nombre, telefono, correo, direccion, latitud, longitud, fotoUri).
 * 2. Conexiones: Utilizada por ClienteRepository, ClienteService y ClienteController.
 * 3. Rol: Entidad de Modelo / Persistencia JPA.
 */
@Entity
@Table(name = "clientes")
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String telefono;
    private String correo;
    private String direccion;
    private Double latitud = 0.0;
    private Double longitud = 0.0;
    private String fotoUri = "";

    // Constructor sin argumentos (Requerido por JPA/Hibernate)
    public ClienteEntity() {
    }

    // Constructor con todos los campos
    public ClienteEntity(Long id, String nombre, String telefono, String correo, String direccion, Double latitud, Double longitud, String fotoUri) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.latitud = latitud != null ? latitud : 0.0;
        this.longitud = longitud != null ? longitud : 0.0;
        this.fotoUri = fotoUri != null ? fotoUri : "";
    }

    // Constructor conveniente sin ID (para creacion manual)
    public ClienteEntity(String nombre, String telefono, String correo, String direccion, Double latitud, Double longitud, String fotoUri) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.latitud = latitud != null ? latitud : 0.0;
        this.longitud = longitud != null ? longitud : 0.0;
        this.fotoUri = fotoUri != null ? fotoUri : "";
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }
}
