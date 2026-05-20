package org.example.repository;

import org.example.model.Control_vacunas;
import java.util.List;

public interface ControlVacunaRepository {
    void guardar(Control_vacunas cv);
    Control_vacunas buscarPorId(Integer id);
    List<Control_vacunas> buscarTodos();
    List<Control_vacunas> buscarPorCliente(Integer clienteId);
    void actualizar(Control_vacunas cv);
    void eliminar(Integer id);
}