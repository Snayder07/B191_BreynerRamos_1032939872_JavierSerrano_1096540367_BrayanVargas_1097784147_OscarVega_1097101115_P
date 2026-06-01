package org.example.controller;

import org.example.model.Citas;
import org.example.model.Control_vacunas;
import org.example.model.EstadoCita;
import org.example.model.Mascotas;
import org.example.model.Vacunas;
import org.example.service.CorreoService;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
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

    public void eliminarConCancelacion(Control_vacunas cv, JPanel panel) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro invalido.");

        // 1. Buscar la cita de vacunacion asociada (misma mascota + misma fecha)
        Citas citaAsociada = null;
        if (cv.getMascota() != null && cv.getFechaAplicacion() != null) {
            citaAsociada = Citas.buscarVacunacionActivaPorMascotaFechaBD(
                    cv.getMascota().getId(), cv.getFechaAplicacion());
        }

        // 2. Eliminar el registro de vacuna
        cv.eliminarBD();

        // 3. Si habia cita → cancelarla
        if (citaAsociada != null) {
            Citas.actualizarEstadoBD(citaAsociada.getId(), EstadoCita.CANCELADA);

            // 4. Enviar correo al cliente
            String correo = null, nombre = "cliente";
            if (citaAsociada.getMascota() != null && citaAsociada.getMascota().getCliente() != null) {
                correo = citaAsociada.getMascota().getCliente().getCorreo();
                nombre = citaAsociada.getMascota().getCliente().getNombre();
            }
            if (correo != null && !correo.isEmpty()) {
                final String cf = correo, nf = nombre;
                final String mascotaNom = citaAsociada.getMascota() != null
                        ? citaAsociada.getMascota().getNombre() : "su mascota";
                final String fechaStr = citaAsociada.getFechaCita() != null
                        ? citaAsociada.getFechaCita().toString() : "-";
                final String horaStr = citaAsociada.getHoraCita() != null
                        ? citaAsociada.getHoraCita().toString() : "-";
                final String cuerpo =
                        "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#fff5f5;border-radius:10px;padding:32px;'>" +
                        "<h2 style='color:#dc2626;'>Cita Cancelada</h2>" +
                        "<p>Hola <b>" + nf + "</b>, lamentamos informarte que tu cita de vacunacion en <b>Kampets Veterinaria</b> ha sido cancelada.</p>" +
                        "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                        "<tr><td style='padding:10px;background:#fee2e2;font-weight:bold;'>Mascota</td><td style='padding:10px;'>" + mascotaNom + "</td></tr>" +
                        "<tr><td style='padding:10px;background:#fee2e2;font-weight:bold;'>Fecha</td><td style='padding:10px;'>" + fechaStr + "</td></tr>" +
                        "<tr><td style='padding:10px;background:#fee2e2;font-weight:bold;'>Hora</td><td style='padding:10px;'>" + horaStr + "</td></tr>" +
                        "</table>" +
                        "<p style='color:#6b7280;font-size:13px;'>Puedes agendar una nueva cita cuando lo desees. Disculpa los inconvenientes.</p></div>";
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CorreoService.enviarCorreoGeneral(cf, nf, "Cita de vacunacion cancelada - Kampets", cuerpo);
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    javax.swing.JOptionPane.showMessageDialog(panel,
                                            "Registro eliminado, cita cancelada, pero el correo no se pudo enviar:\n" + ex.getMessage(),
                                            "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    public List<Citas> listarCitasVacunSinRegistro() {
        try {
            List<Citas> citasVacun = Citas.consultarVacunasBD();
            List<Control_vacunas> registros = Control_vacunas.consultarTodosBD();
            List<Citas> sinRegistro = new ArrayList<>();
            for (Citas c : citasVacun) {
                boolean tieneRegistro = false;
                for (Control_vacunas cv : registros) {
                    if (cv.getMascota() != null && c.getMascota() != null
                            && cv.getMascota().getId().equals(c.getMascota().getId())
                            && cv.getFechaAplicacion() != null
                            && cv.getFechaAplicacion().equals(c.getFechaCita())) {
                        tieneRegistro = true;
                        break;
                    }
                }
                if (!tieneRegistro) sinRegistro.add(c);
            }
            return sinRegistro;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
