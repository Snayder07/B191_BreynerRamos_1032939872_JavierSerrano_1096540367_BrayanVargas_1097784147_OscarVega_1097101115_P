package org.example.service;

import jakarta.persistence.EntityManager;
import org.example.model.Control_vacunas;
import org.example.model.Mascotas;
import org.example.model.Vacunas;
import org.example.repository.ControlVacunaRepositoryImpl;
import org.example.util.JPAUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class ControlVacunaService {

    private final ControlVacunaRepositoryImpl repositorio = new ControlVacunaRepositoryImpl();

    public List<Control_vacunas> listarTodas() {
        return repositorio.buscarTodos();
    }

    public List<Control_vacunas> listarPorCliente(Integer clienteId) {
        return repositorio.buscarPorCliente(clienteId);
    }

    public List<Vacunas> listarVacunas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT v FROM Vacunas v ORDER BY v.nombre", Vacunas.class)
                     .getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally { em.close(); }
    }

    public List<Mascotas> listarMascotas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery(
                "SELECT m FROM Mascotas m JOIN FETCH m.especie JOIN FETCH m.cliente ORDER BY m.nombre",
                Mascotas.class
            ).getResultList();
        } catch (Exception e) {
            return Collections.emptyList();
        } finally { em.close(); }
    }

    public void guardar(Control_vacunas cv) throws Exception {
        if (cv == null)                     throw new Exception("El registro de vacuna no puede estar vacío.");
        if (cv.getMascota() == null)        throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)        throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null) throw new Exception("La fecha de aplicación es obligatoria.");
        repositorio.guardar(cv);
    }

    public void actualizar(Control_vacunas cv) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro inválido.");
        if (cv.getMascota() == null)          throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)          throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null)  throw new Exception("La fecha de aplicación es obligatoria.");
        repositorio.actualizar(cv);
    }

    public void eliminar(Integer id) throws Exception {
        if (id == null) throw new Exception("ID de registro inválido.");
        repositorio.eliminar(id);
    }

    public String calcularEstado(LocalDate proximaDosis) {
        if (proximaDosis == null) return "Al día";
        LocalDate hoy = LocalDate.now();
        if (proximaDosis.isBefore(hoy))              return "Vencida";
        if (proximaDosis.isBefore(hoy.plusDays(30))) return "Próxima";
        return "Al día";
    }
}
