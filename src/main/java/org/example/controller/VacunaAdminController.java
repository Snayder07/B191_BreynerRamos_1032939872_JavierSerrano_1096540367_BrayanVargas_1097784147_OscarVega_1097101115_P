package org.example.controller;

import org.example.model.Control_vacunas;
import org.example.model.Mascotas;
import org.example.model.Vacunas;

import java.util.Collections;
import java.util.List;

public class VacunaAdminController {

    public List<Control_vacunas> listarTodas() {
        try { return Control_vacunas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Vacunas> listarVacunas() {
        try { return Vacunas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Mascotas> listarMascotas() {
        try { return Mascotas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void guardar(Control_vacunas cv) throws Exception {
        if (cv == null)                     throw new Exception("El registro de vacuna no puede estar vacio.");
        if (cv.getMascota() == null)        throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)        throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null) throw new Exception("La fecha de aplicacion es obligatoria.");
        cv.insertarBD();
    }

    public void actualizar(Control_vacunas cv) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro invalido.");
        if (cv.getMascota() == null)          throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)          throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null)  throw new Exception("La fecha de aplicacion es obligatoria.");
        cv.actualizarBD();
    }

    public void eliminar(Integer id) throws Exception {
        if (id == null) throw new Exception("ID de registro invalido.");
        Control_vacunas cv = new Control_vacunas();
        cv.setId(id);
        cv.eliminarBD();
    }
}
