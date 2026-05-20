package org.example.controller;

import org.example.model.Citas;
import org.example.model.Empleados;
import org.example.model.EstadoCita;
import org.example.model.Mascotas;
import org.example.service.CitaService;
import org.example.service.CorreoService;
import org.example.service.EmpleadoService;
import org.example.service.MascotaService;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class CitaAdminController {

    private final CitaService     citaService     = new CitaService();
    private final MascotaService  mascotaService  = new MascotaService();
    private final EmpleadoService empleadoService = new EmpleadoService();

    public List<Citas> listarTodas() {
        try {
            return citaService.listarTodas();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Citas> listarPorCliente(Integer clienteId) {
        try {
            return citaService.listarPorCliente(clienteId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Citas> listarPasadasPorCliente(Integer clienteId) {
        try {
            return citaService.listarPasadasPorCliente(clienteId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Citas> listarDeHoy() {
        try {
            return citaService.listarDeHoy();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Citas> listarCitasVacunas() {
        try {
            return citaService.listarCitasVacunas();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public void cancelarCita(Integer id, JPanel panel) {
        cambiarEstado(id, EstadoCita.CANCELADA, panel);
    }

    /**
     * Confirma una cita: cambia estado a CONFIRMADA y envia correo al cliente.
     * Si el correo falla, igual confirma la cita y avisa al admin.
     */
    public void confirmarCita(Integer id, JPanel panel) {
        try {
            Citas cita = citaService.buscarPorId(id);
            if (cita == null) throw new Exception("No se encontró la cita.");

            citaService.cambiarEstado(id, EstadoCita.CONFIRMADA);

            // Intentar enviar correo de confirmacion
            String correoDestino = null;
            String nombreCliente = "cliente";
            String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "su mascota";
            if (cita.getMascota() != null && cita.getMascota().getCliente() != null) {
                correoDestino = cita.getMascota().getCliente().getCorreo();
                nombreCliente = cita.getMascota().getCliente().getNombre();
            }
            String fecha = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "—";
            String hora  = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "—";

            String cuerpo =
                    "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                            "<h2 style='color:#16a34a;margin-bottom:6px;'>\u2705 Cita Confirmada</h2>" +
                            "<p style='color:#374151;font-size:15px;'>Hola <b>" + nombreCliente + "</b>, tu cita en <b>Kampets Veterinaria</b> ha sido <b>confirmada</b>.</p>" +
                            "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Mascota</td><td style='padding:10px 14px;background:#f0fdf4;'>" + nombreMascota + "</td></tr>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Fecha</td><td style='padding:10px 14px;background:#f0fdf4;'>" + fecha + "</td></tr>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Hora</td><td style='padding:10px 14px;background:#f0fdf4;'>" + hora + "</td></tr>" +
                            "</table>" +
                            "<p style='color:#6b7280;font-size:13px;'>Por favor presentate puntualmente. Si necesitas cancelar, contactanos con anticipacion.</p>" +
                            "<p style='color:#16a34a;font-weight:bold;margin-top:24px;'>Hasta pronto!</p>" +
                            "</div>";

            if (correoDestino != null && !correoDestino.isEmpty()) {
                final String correoFinal  = correoDestino;
                final String nombreFinal  = nombreCliente;
                final String cuerpoFinal  = cuerpo;
                JOptionPane.showMessageDialog(panel,
                        "Cita confirmada. Enviando correo a " + correoFinal + "...",
                        "Cita confirmada", JOptionPane.INFORMATION_MESSAGE);
                new Thread(() -> {
                    try {
                        CorreoService.enviarCorreoGeneral(correoFinal, nombreFinal, "Confirmación de cita - Kampets", cuerpoFinal);
                    } catch (Exception mailEx) {
                        SwingUtilities.invokeLater(() ->
                                JOptionPane.showMessageDialog(panel,
                                        "Cita confirmada, pero el correo no se pudo enviar:\n" + mailEx.getMessage(),
                                        "Aviso de correo", JOptionPane.WARNING_MESSAGE));
                    }
                }).start();
            } else {
                JOptionPane.showMessageDialog(panel,
                        "Cita confirmada. El cliente no tiene correo registrado.",
                        "Cita confirmada", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Cambia el estado de una cita y envía correo al cliente notificando el cambio.
     */
    public void cambiarEstado(Integer id, EstadoCita nuevoEstado, JPanel panel) {
        try {
            Citas cita = citaService.buscarPorId(id);
            if (cita == null) throw new Exception("No se encontró la cita.");

            citaService.cambiarEstado(id, nuevoEstado);

            // Datos del cliente
            String correoDestino = null;
            String nombreCliente = "cliente";
            String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "su mascota";
            if (cita.getMascota() != null && cita.getMascota().getCliente() != null) {
                correoDestino = cita.getMascota().getCliente().getCorreo();
                nombreCliente = cita.getMascota().getCliente().getNombre();
            }
            String fecha = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "—";
            String hora  = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "—";

            if (correoDestino == null || correoDestino.isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Estado actualizado. El cliente no tiene correo registrado.",
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Definir asunto, color y mensaje según el nuevo estado
            String asunto, encabezado, colorTitulo, emoji, mensajeExtra;
            switch (nuevoEstado) {
                case CONFIRMADA:
                    asunto       = "Cita confirmada - Kampets";
                    encabezado   = "Cita Confirmada";
                    colorTitulo  = "#16a34a";
                    emoji        = "✅";
                    mensajeExtra = "Por favor preséntate puntualmente. Si necesitas cancelar, contáctanos con anticipación.";
                    break;
                case CANCELADA:
                    asunto       = "Cita cancelada - Kampets";
                    encabezado   = "Cita Cancelada";
                    colorTitulo  = "#dc2626";
                    emoji        = "❌";
                    mensajeExtra = "Si deseas reagendar, puedes hacerlo desde la aplicación o contactándonos directamente.";
                    break;
                case COMPLETADA:
                    asunto       = "Cita completada - Kampets";
                    encabezado   = "Cita Completada";
                    colorTitulo  = "#2563eb";
                    emoji        = "🎉";
                    mensajeExtra = "Gracias por confiar en Kampets Veterinaria. ¡Esperamos ver a tu mascota pronto!";
                    break;
                default:
                    asunto       = "Actualización de cita - Kampets";
                    encabezado   = "Cita Actualizada";
                    colorTitulo  = "#d97706";
                    emoji        = "📋";
                    mensajeExtra = "Si tienes alguna duda, no dudes en contactarnos.";
            }

            String cuerpo =
                    "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                            "<h2 style='color:" + colorTitulo + ";margin-bottom:6px;'>" + emoji + " " + encabezado + "</h2>" +
                            "<p style='color:#374151;font-size:15px;'>Hola <b>" + nombreCliente + "</b>, el estado de tu cita en <b>Kampets Veterinaria</b> ha sido actualizado a <b>" + nuevoEstado.name() + "</b>.</p>" +
                            "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Mascota</td><td style='padding:10px 14px;background:#f0fdf4;'>" + nombreMascota + "</td></tr>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Fecha</td><td style='padding:10px 14px;background:#f0fdf4;'>" + fecha + "</td></tr>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Hora</td><td style='padding:10px 14px;background:#f0fdf4;'>" + hora + "</td></tr>" +
                            "<tr><td style='padding:10px 14px;background:#dcfce7;color:#15803d;font-weight:bold;'>Estado</td><td style='padding:10px 14px;background:#f0fdf4;font-weight:bold;color:" + colorTitulo + ";'>" + nuevoEstado.name() + "</td></tr>" +
                            "</table>" +
                            "<p style='color:#6b7280;font-size:13px;'>" + mensajeExtra + "</p>" +
                            "<p style='color:#16a34a;font-weight:bold;margin-top:24px;'>¡Hasta pronto! 🐾</p>" +
                            "</div>";

            try {
                CorreoService.enviarCorreoGeneral(correoDestino, nombreCliente, asunto, cuerpo);
                JOptionPane.showMessageDialog(panel,
                        "Estado actualizado y correo enviado a " + correoDestino + ".",
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception mailEx) {
                JOptionPane.showMessageDialog(panel,
                        "Estado actualizado, pero no se pudo enviar el correo.\n" + mailEx.getMessage(),
                        "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Mascotas> listarMascotas() {
        try {
            return mascotaService.listarTodas();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Empleados> listarEmpleados() {
        try {
            return empleadoService.listarTodos();
        } catch (Exception e) {
            return Collections.emptyList();
        }
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
            catch (DateTimeParseException e) { throw new Exception("Formato de fecha inválido. Usa: yyyy-MM-dd"); }
            try { hora = LocalTime.parse(horaStr.trim()); }
            catch (DateTimeParseException e) { throw new Exception("Formato de hora inválido. Usa: HH:mm"); }

            // Convertir String a EstadoCita
            EstadoCita estado;
            try {
                estado = EstadoCita.valueOf(estadoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                estado = EstadoCita.CONFIRMADA;
            }

            Citas cita = new Citas();
            cita.setMascota(mascota);
            cita.setEmpleado(empleado);
            cita.setFechaCita(fecha);
            cita.setHoraCita(hora);
            cita.setEstadoCita(estado);
            citaService.guardarCita(cita);
            JOptionPane.showMessageDialog(panel, "Cita guardada exitosamente.");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
