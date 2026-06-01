package org.example.controller;

import org.example.model.*;
import org.example.service.CorreoService;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class AgendarCitaController {

    private static final int LIMITE_CUPO = 10;

    public List<Mascotas> listarMascotas() {
        try { return Mascotas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Servicio> listarServicios() {
        try { return Servicio.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Empleados> listarVeterinarios() {
        try { return Empleados.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

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
            try { fecha = LocalDate.parse(fechaStr.trim()); }
            catch (DateTimeParseException e) { throw new Exception("Formato de fecha invalido. Usa: yyyy-MM-dd"); }
            try { hora = LocalTime.parse(horaStr.trim()); }
            catch (DateTimeParseException e) { throw new Exception("Formato de hora invalido. Usa: HH:mm"); }

            List<Citas> citasActivas = new java.util.ArrayList<>();
            for (Citas c : Citas.consultarTodosBD()) {
                if (c.getEstadoCita() != EstadoCita.CANCELADA
                        && c.getEstadoCita() != EstadoCita.COMPLETADA) {
                    citasActivas.add(c);
                }
            }
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
            cita.insertarBD();

            if (hayCupo) {
                String nombreCliente = mascota.getCliente() != null ? mascota.getCliente().getNombre() : "cliente";
                String correoCliente = mascota.getCliente() != null ? mascota.getCliente().getCorreo() : null;
                String cuerpo =
                        "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                        "<h2 style='color:#16a34a;'>Cita Confirmada</h2>" +
                        "<p>Hola <b>" + nombreCliente + "</b>, tu cita en <b>Kampets Veterinaria</b> ha sido confirmada.</p>" +
                        "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                        "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Mascota</td><td style='padding:10px;'>" + mascota.getNombre() + "</td></tr>" +
                        "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Fecha</td><td style='padding:10px;'>" + fecha + "</td></tr>" +
                        "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Hora</td><td style='padding:10px;'>" + hora + "</td></tr>" +
                        "</table></div>";

                String msg = "Cita agendada y confirmada!\n\nMascota: " + mascota.getNombre() +
                        "\nVeterinario: " + empleado.getNombre() +
                        "\nFecha: " + fecha + "\nHora: " + hora;
                if (direccionDomicilio != null && !direccionDomicilio.trim().isEmpty())
                    msg += "\nDomicilio: " + direccionDomicilio.trim();
                JOptionPane.showMessageDialog(panel, msg, "Cita confirmada", JOptionPane.INFORMATION_MESSAGE);

                if (correoCliente != null && !correoCliente.isEmpty()) {
                    final String cf = correoCliente, nf = nombreCliente, bf = cuerpo;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CorreoService.enviarCorreoGeneral(cf, nf, "Confirmacion de cita - Kampets", bf);
                            } catch (Exception ex) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        JOptionPane.showMessageDialog(panel,
                                                "Cita guardada, pero el correo no se pudo enviar:\n" + ex.getMessage(),
                                                "Aviso de correo", JOptionPane.WARNING_MESSAGE);
                                    }
                                });
                            }
                        }
                    }).start();
                }
            } else {
                String msg = "Tu cita fue registrada en lista de espera.\n\n" +
                        "En este momento tenemos el horario lleno.\n" +
                        "Un administrador la confirmara pronto.\n\n" +
                        "Mascota: " + mascota.getNombre() + "\nFecha: " + fecha + "\nHora: " + hora;
                JOptionPane.showMessageDialog(panel, msg, "En lista de espera", JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
