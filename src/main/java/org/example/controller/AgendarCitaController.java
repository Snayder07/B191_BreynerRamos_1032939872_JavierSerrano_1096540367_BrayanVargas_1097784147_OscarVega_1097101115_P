package org.example.controller;

import org.example.model.Citas;
import org.example.model.Empleados;
import org.example.model.EstadoCita;
import org.example.model.Mascotas;
import org.example.service.CitaService;
import org.example.service.CorreoService;
import org.example.service.EmpleadoService;
import org.example.service.MascotaService;
import org.example.model.Servicio;
import org.example.repository.EspecieRepositoryImpl;
import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class AgendarCitaController {

    /** Máximo de citas activas antes de pasar nuevas a PENDIENTE */
    private static final int LIMITE_CUPO = 10;

    private final CitaService     citaService     = new CitaService();
    private final MascotaService  mascotaService  = new MascotaService();
    private final EmpleadoService empleadoService = new EmpleadoService();


    public List<Mascotas> listarMascotas() {
        try {
            return mascotaService.listarTodas();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    public List<Servicio> listarServicios() {
        try {
            jakarta.persistence.EntityManager em = org.example.util.JPAUtil.getEntityManager();
            List<Servicio> lista = em.createQuery("SELECT s FROM Servicio s", Servicio.class).getResultList();
            em.close();
            return lista;
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }

    public List<Empleados> listarVeterinarios() {
        try {
            return empleadoService.listarTodos();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // ── Guardar cita en BD ────────────────────────────────
    public boolean guardarCita(Mascotas mascota, Empleados empleado,
                               String fechaStr, String horaStr,
                               String direccionDomicilio, String motivo, JPanel panel) {
        try {
            if (mascota  == null) throw new Exception("Selecciona una mascota.");
            if (empleado == null) throw new Exception("Selecciona un veterinario.");
            if (fechaStr == null || fechaStr.trim().isEmpty())
                throw new Exception("Ingresa la fecha (yyyy-MM-dd).");
            if (horaStr  == null || horaStr.trim().isEmpty())
                throw new Exception("Selecciona una hora.");

            LocalDate fecha;
            LocalTime hora;
            try {
                fecha = LocalDate.parse(fechaStr.trim());
            } catch (DateTimeParseException e) {
                throw new Exception("Formato de fecha inválido. Usa: yyyy-MM-dd");
            }
            try {
                hora = LocalTime.parse(horaStr.trim());
            } catch (DateTimeParseException e) {
                throw new Exception("Formato de hora inválido. Usa: HH:mm");
            }

            // ── Regla de cupo: si hay 10+ citas activas → PENDIENTE ──
            List<Citas> citasActivas = citaService.listarTodas().stream()
                    .filter(c -> c.getEstadoCita() != EstadoCita.CANCELADA
                            && c.getEstadoCita() != EstadoCita.COMPLETADA)
                    .collect(java.util.stream.Collectors.toList());
            boolean hayCupo = citasActivas.size() < LIMITE_CUPO;
            EstadoCita estadoNuevo = hayCupo ? EstadoCita.CONFIRMADA : EstadoCita.PENDIENTE;

            Citas cita = new Citas();
            cita.setMascota(mascota);
            cita.setEmpleado(empleado);
            cita.setFechaCita(fecha);
            cita.setHoraCita(hora);
            cita.setEstadoCita(estadoNuevo);
            if (direccionDomicilio != null && !direccionDomicilio.trim().isEmpty())
                cita.setDireccionDomicilio(direccionDomicilio.trim());
            if (motivo != null && !motivo.trim().isEmpty())
                cita.setMotivo(motivo.trim());

            citaService.guardarCita(cita);

            if (hayCupo) {
                // Hay cupo → confirmada, enviar correo en hilo separado
                String nombreCliente = mascota.getCliente() != null ? mascota.getCliente().getNombre() : "cliente";
                String correoCliente = mascota.getCliente() != null ? mascota.getCliente().getCorreo() : null;
                String cuerpo =
                        "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                                "<h2 style='color:#16a34a;'>✅ Cita Confirmada</h2>" +
                                "<p style='color:#374151;font-size:15px;'>Hola <b>" + nombreCliente + "</b>, tu cita en <b>Kampets Veterinaria</b> ha sido <b>confirmada</b>.</p>" +
                                "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                                "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Mascota</td><td style='padding:10px 14px;background:#f0fdf4;'>" + mascota.getNombre() + "</td></tr>" +
                                "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Fecha</td><td style='padding:10px 14px;background:#f0fdf4;'>" + fecha + "</td></tr>" +
                                "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Hora</td><td style='padding:10px 14px;background:#f0fdf4;'>" + hora + "</td></tr>" +
                                "</table>" +
                                "<p style='color:#6b7280;font-size:13px;'>Por favor preséntate puntualmente.</p>" +
                                "<p style='color:#16a34a;font-weight:bold;'>¡Hasta pronto! 🐾</p></div>";

                String msg = "¡Cita agendada y confirmada!\n\n"
                        + "Mascota:     " + mascota.getNombre()  + "\n"
                        + "Veterinario: " + empleado.getNombre() + "\n"
                        + "Fecha:       " + fecha                + "\n"
                        + "Hora:        " + hora;
                if (direccionDomicilio != null && !direccionDomicilio.trim().isEmpty())
                    msg += "\nDomicilio:   " + direccionDomicilio.trim();
                JOptionPane.showMessageDialog(panel, msg, "Cita confirmada", JOptionPane.INFORMATION_MESSAGE);

                // Enviar correo en hilo separado para no bloquear la UI
                if (correoCliente != null && !correoCliente.isEmpty()) {
                    final String correoFinal  = correoCliente;
                    final String nombreFinal  = nombreCliente;
                    final String cuerpoFinal  = cuerpo;
                    new Thread(() -> {
                        try {
                            CorreoService.enviarCorreoGeneral(correoFinal, nombreFinal, "Confirmación de cita - Kampets", cuerpoFinal);
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(panel,
                                            "Cita guardada, pero el correo no se pudo enviar:\n" + ex.getMessage(),
                                            "Aviso de correo", JOptionPane.WARNING_MESSAGE));
                        }
                    }).start();
                }
            } else {
                // Sin cupo → queda en espera, el admin la confirmará
                String msg = "Tu cita fue registrada en lista de espera.\n\n"
                        + "En este momento tenemos el horario lleno.\n"
                        + "Un administrador la confirmará pronto y recibirás un correo.\n\n"
                        + "Mascota: " + mascota.getNombre() + "\n"
                        + "Fecha:   " + fecha + "\n"
                        + "Hora:    " + hora;
                JOptionPane.showMessageDialog(panel, msg, "En lista de espera", JOptionPane.INFORMATION_MESSAGE);
            }
            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel,
                    e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}