package org.example.service;

import org.example.model.Citas;
import org.example.repository.CitaRepositoryImpl;

import java.util.List;

public class CitaService {

    private final CitaRepositoryImpl repositorio = new CitaRepositoryImpl();

    // ─────────────────────────────────────────────────────────
    // GUARDAR CITA
    // P3 llama a este método cuando el cliente agenda una cita
    // ─────────────────────────────────────────────────────────
    public void guardarCita(Citas cita) throws Exception {
        if (cita == null) {
            throw new Exception("La cita no puede estar vacía.");
        }
        repositorio.guardar(cita);
    }


    public List<Citas> listarTodas() {
        return repositorio.buscarTodos();
    }

    public List<Citas> listarPorCliente(Integer clienteId) {
        return repositorio.buscarPorCliente(clienteId);
    }

    public List<Citas> listarPasadasPorCliente(Integer clienteId) {
        return repositorio.buscarPasadasPorCliente(clienteId);
    }

    public List<Citas> listarDeHoy() {
        return repositorio.buscarDeHoy();
    }

    public List<Citas> listarCitasVacunas() {
        return repositorio.buscarCitasVacunas();
    }

    // ─────────────────────────────────────────────────────────
    // BUSCAR CITA POR ID
    // ─────────────────────────────────────────────────────────
    public Citas buscarPorId(Integer id) throws Exception {
        if (id == null) {
            throw new Exception("El ID de cita no puede estar vacío.");
        }
        return repositorio.buscarPorId(id);
    }

    // ─────────────────────────────────────────────────────────
    // CANCELAR CITA  (cambia estado a CANCELADA, no elimina)
    // ─────────────────────────────────────────────────────────
    public void cancelarCita(Integer idCita) throws Exception {
        Citas cita = repositorio.buscarPorId(idCita);
        if (cita == null) {
            throw new Exception("No se encontró la cita con ID: " + idCita);
        }
        repositorio.actualizarEstado(idCita, org.example.model.EstadoCita.CANCELADA);
    }

    // ─────────────────────────────────────────────────────────
    // CAMBIAR ESTADO DE CITA (admin)
    // ─────────────────────────────────────────────────────────
    public void cambiarEstado(Integer idCita, org.example.model.EstadoCita nuevoEstado) throws Exception {
        Citas cita = repositorio.buscarPorId(idCita);
        if (cita == null) {
            throw new Exception("No se encontró la cita con ID: " + idCita);
        }
        repositorio.actualizarEstado(idCita, nuevoEstado);
    }
}
