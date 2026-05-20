package org.example.repository;

import org.example.model.Citas;
import java.util.List;

public interface CitaRepository {
    void guardar(Citas cita);
    Citas buscarPorId(Integer id);
    List<Citas> buscarTodos();
    List<Citas> buscarPorCliente(Integer clienteId);
    List<Citas> buscarPasadasPorCliente(Integer clienteId);
    List<Citas> buscarDeHoy();
    void eliminar(Integer id);
    List<Citas> buscarCitasVacunas();
}