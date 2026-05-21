package org.example.service;

import org.example.model.Mascotas;
import org.example.repository.MascotaRepositoryImpl;

import java.util.List;

/**
 * PERSONA 2 — Servicio de Mascotas
 * Lógica para registrar y consultar mascotas
 */
public class MascotaService {

    private final MascotaRepositoryImpl repositorio = new MascotaRepositoryImpl();

    // ─────────────────────────────────────────────────────────
    // REGISTRAR MASCOTA
    // P3 llama a esto cuando el cliente registra su mascota
    // ─────────────────────────────────────────────────────────
    public void registrarMascota(Mascotas mascota) throws Exception {
        if (mascota == null) {
            throw new Exception("Los datos de la mascota no pueden estar vacíos.");
        }
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre de la mascota es obligatorio.");
        }
        repositorio.guardar(mascota);
    }
    public void eliminarMascota(Integer id) throws Exception {
        if (id == null) throw new Exception("ID invalido.");
        repositorio.eliminar(id);
    }
    // ─────────────────────────────────────────────────────────
    // LISTAR TODAS LAS MASCOTAS
    // ─────────────────────────────────────────────────────────
    public List<Mascotas> listarTodas() {
        return repositorio.buscarTodos();
    }

    public List<Mascotas> listarPorCliente(Integer clienteId) {
        return repositorio.buscarPorCliente(clienteId);
    }

    // ─────────────────────────────────────────────────────────
    // BUSCAR POR ID
    // ─────────────────────────────────────────────────────────
    public Mascotas buscarPorId(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("El ID no puede estar vacío.");
        }
        return repositorio.buscarPorId(id);
    }

}
