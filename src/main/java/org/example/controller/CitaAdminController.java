package org.example.controller;

import org.example.model.*;
import org.example.service.CorreoService;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class CitaAdminController {

    public List<Citas> listarTodas() {
        try { return Citas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Citas> listarPorCliente(Integer clienteId) {
        try { return Citas.consultarPorClienteBD(clienteId); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Citas> listarPasadasPorCliente(Integer clienteId) {
        try { return Citas.consultarPasadasPorClienteBD(clienteId); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Citas> listarDeHoy() {
        try { return Citas.consultarDeHoyBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Citas> listarCitasVacunas() {
        try { return Citas.consultarVacunasBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public void cancelarCita(Integer id, JPanel panel) {
        cambiarEstado(id, EstadoCita.CANCELADA, panel);
    }

    public void confirmarCita(Integer id, JPanel panel) {
        try {
            Citas cita = Citas.buscarPorIdBD(id);
            if (cita == null) throw new Exception("No se encontro la cita.");
            Citas.actualizarEstadoBD(id, EstadoCita.CONFIRMADA);

            String correoDestino = null, nombreCliente = "cliente";
            String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "su mascota";
            if (cita.getMascota() != null && cita.getMascota().getCliente() != null) {
                correoDestino = cita.getMascota().getCliente().getCorreo();
                nombreCliente = cita.getMascota().getCliente().getNombre();
            }
            String fecha = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "-";
            String hora  = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "-";
            String cuerpo = buildCorreo(nombreCliente, nombreMascota, fecha, hora, "CONFIRMADA",
                    "#16a34a", "Cita Confirmada", "Por favor presentate puntualmente.");

            if (correoDestino != null && !correoDestino.isEmpty()) {
                final String cf = correoDestino, nf = nombreCliente, bf = cuerpo;
                JOptionPane.showMessageDialog(panel, "Cita confirmada. Enviando correo a " + cf + "...",
                        "Cita confirmada", JOptionPane.INFORMATION_MESSAGE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try { CorreoService.enviarCorreoGeneral(cf, nf, "Confirmacion de cita - Kampets", bf); }
                        catch (Exception ex) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    JOptionPane.showMessageDialog(panel,
                                            "Cita confirmada, pero el correo no se pudo enviar:\n" + ex.getMessage(),
                                            "Aviso de correo", JOptionPane.WARNING_MESSAGE);
                                }
                            });
                        }
                    }
                }).start();
            } else {
                JOptionPane.showMessageDialog(panel, "Cita confirmada. El cliente no tiene correo registrado.",
                        "Cita confirmada", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cambiarEstado(Integer id, EstadoCita nuevoEstado, JPanel panel) {
        try {
            Citas cita = Citas.buscarPorIdBD(id);
            if (cita == null) throw new Exception("No se encontro la cita.");
            Citas.actualizarEstadoBD(id, nuevoEstado);

            String correoDestino = null, nombreCliente = "cliente";
            String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "su mascota";
            if (cita.getMascota() != null && cita.getMascota().getCliente() != null) {
                correoDestino = cita.getMascota().getCliente().getCorreo();
                nombreCliente = cita.getMascota().getCliente().getNombre();
            }
            if (correoDestino == null || correoDestino.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Estado actualizado. El cliente no tiene correo registrado.",
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String fecha = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "-";
            String hora  = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "-";

            String color, titulo, extra;
            switch (nuevoEstado) {
                case CONFIRMADA: color = "#16a34a"; titulo = "Cita Confirmada"; extra = "Por favor presentate puntualmente."; break;
                case CANCELADA:  color = "#dc2626"; titulo = "Cita Cancelada";  extra = "Si deseas reagendar, puedes contactarnos."; break;
                case COMPLETADA: color = "#2563eb"; titulo = "Cita Completada"; extra = "Gracias por confiar en Kampets Veterinaria!"; break;
                default:         color = "#d97706"; titulo = "Cita Actualizada"; extra = "Si tienes alguna duda, contactanos."; break;
            }
            String cuerpo = buildCorreo(nombreCliente, nombreMascota, fecha, hora,
                    nuevoEstado.name(), color, titulo, extra);

            try {
                CorreoService.enviarCorreoGeneral(correoDestino, nombreCliente, titulo + " - Kampets", cuerpo);
                JOptionPane.showMessageDialog(panel, "Estado actualizado y correo enviado a " + correoDestino + ".",
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Estado actualizado, pero no se pudo enviar el correo.\n" + ex.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Mascotas> listarMascotas() {
        try { return Mascotas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Empleados> listarEmpleados() {
        try { return Empleados.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean guardarCita(Mascotas mascota, Empleados empleado,
                               String fechaStr, String horaStr, String estadoStr, JPanel panel) {
        try {
            if (mascota  == null) throw new Exception("Selecciona una mascota.");
            if (empleado == null) throw new Exception("Selecciona un veterinario.");
            if (fechaStr == null || fechaStr.trim().isEmpty())
                throw new Exception("Ingresa la fecha (yyyy-MM-dd).");
            if (horaStr  == null || horaStr.trim().isEmpty())
                throw new Exception("Ingresa la hora (HH:mm).");

            LocalDate fecha;
            LocalTime hora;
            try { fecha = LocalDate.parse(fechaStr.trim()); }
            catch (DateTimeParseException e) { throw new Exception("Formato de fecha invalido. Usa: yyyy-MM-dd"); }
            try { hora  = LocalTime.parse(horaStr.trim()); }
            catch (DateTimeParseException e) { throw new Exception("Formato de hora invalido. Usa: HH:mm"); }

            EstadoCita estado;
            try { estado = EstadoCita.valueOf(estadoStr.toUpperCase()); }
            catch (Exception e) { estado = EstadoCita.CONFIRMADA; }

            Citas cita = new Citas();
            cita.setMascota(mascota);
            cita.setEmpleado(empleado);
            cita.setFechaCita(fecha);
            cita.setHoraCita(hora);
            cita.setEstadoCita(estado);
            cita.insertarBD();
            JOptionPane.showMessageDialog(panel, "Cita guardada exitosamente.");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static String buildCorreo(String cliente, String mascota, String fecha, String hora,
                                      String estado, String color, String titulo, String extra) {
        return "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                "<h2 style='color:" + color + ";'>" + titulo + "</h2>" +
                "<p>Hola <b>" + cliente + "</b>, el estado de tu cita en <b>Kampets Veterinaria</b> ha sido actualizado a <b>" + estado + "</b>.</p>" +
                "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Mascota</td><td style='padding:10px;'>" + mascota + "</td></tr>" +
                "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Fecha</td><td style='padding:10px;'>" + fecha + "</td></tr>" +
                "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Hora</td><td style='padding:10px;'>" + hora + "</td></tr>" +
                "</table><p style='color:#6b7280;font-size:13px;'>" + extra + "</p></div>";
    }
}
