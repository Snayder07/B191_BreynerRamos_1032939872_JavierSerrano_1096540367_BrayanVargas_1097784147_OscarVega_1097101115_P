package org.example.controller;

import org.example.model.Control_vacunas;
import org.example.model.Mascotas;
import org.example.model.Vacunas;
import org.example.service.ControlVacunaService;

import java.util.Collections;
import java.util.List;

public class VacunaAdminController {

    private final ControlVacunaService service = new ControlVacunaService();

    public List<Control_vacunas> listarTodas() {
        try { return service.listarTodas(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Vacunas> listarVacunas() {
        try { return service.listarVacunas(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Mascotas> listarMascotas() {
        try { return service.listarMascotas(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void guardar(Control_vacunas cv) throws Exception {
        service.guardar(cv);
    }

    public void actualizar(Control_vacunas cv) throws Exception {
        service.actualizar(cv);
    }

    public void eliminar(Integer id) throws Exception {
        service.eliminar(id);
    }

    public String calcularEstado(Control_vacunas cv) {
        return service.calcularEstado(cv.getProximaDosis());
    }
}
