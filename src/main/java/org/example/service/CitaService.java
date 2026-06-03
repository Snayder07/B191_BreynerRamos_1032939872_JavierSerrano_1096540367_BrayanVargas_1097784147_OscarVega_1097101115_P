package org.example.service;

import org.example.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CitaService {

    // ── Confirmar una cita y notificar al cliente por correo ──────────────
    public void confirmarCita(Integer id) throws Exception {
        Citas cita = Citas.buscarPorIdBD(id);
        if (cita == null) throw new Exception("No se encontro la cita.");
        Citas.actualizarEstadoBD(id, EstadoCita.CONFIRMADA);
        enviarCorreoEstado(cita, EstadoCita.CONFIRMADA);
    }

    // ── Cancelar una cita y notificar al cliente por correo ───────────────
    public void cancelarCita(Integer id) throws Exception {
        cambiarEstado(id, EstadoCita.CANCELADA);
    }

    // ── Cambiar el estado de una cita y notificar al cliente ──────────────
    public void cambiarEstado(Integer id, EstadoCita nuevoEstado) throws Exception {
        Citas cita = Citas.buscarPorIdBD(id);
        if (cita == null) throw new Exception("No se encontro la cita.");
        Citas.actualizarEstadoBD(id, nuevoEstado);
        enviarCorreoEstado(cita, nuevoEstado);
    }

    // ── Agendar cita desde el panel del cliente ───────────────────────────
    public static final int LIMITE_CUPO = 10;

    public boolean agendarCita(Mascotas mascota, Empleados empleado,
                               String fechaStr, String horaStr,
                               String direccionDomicilio, String motivo) throws Exception {
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

        List<Citas> citasActivas = new ArrayList<>();
        for (Citas c : Citas.consultarTodosBD()) {
            if (c.getEstadoCita() != EstadoCita.CANCELADA
                    && c.getEstadoCita() != EstadoCita.COMPLETADA) {
                citasActivas.add(c);
            }
        }
        boolean hayCupo = citasActivas.size() < LIMITE_CUPO;

        Citas cita = new Citas();
        cita.setMascota(mascota);
        cita.setEmpleado(empleado);
        cita.setFechaCita(fecha);
        cita.setHoraCita(hora);
        cita.setEstadoCita(hayCupo ? EstadoCita.CONFIRMADA : EstadoCita.PENDIENTE);
        if (direccionDomicilio != null && !direccionDomicilio.trim().isEmpty())
            cita.setDireccionDomicilio(direccionDomicilio.trim());
        if (motivo != null && !motivo.trim().isEmpty())
            cita.setMotivo(motivo.trim());
        cita.insertarBD();

        if (hayCupo) {
            String nombreCliente = mascota.getCliente() != null ? mascota.getCliente().getNombre() : "cliente";
            String correoCliente = mascota.getCliente() != null ? mascota.getCliente().getCorreo() : null;
            if (correoCliente != null && !correoCliente.isEmpty()) {
                String cuerpo =
                    "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                    "<h2 style='color:#16a34a;'>Cita Confirmada</h2>" +
                    "<p>Hola <b>" + nombreCliente + "</b>, tu cita en <b>Kampets Veterinaria</b> ha sido confirmada.</p>" +
                    "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                    "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Mascota</td><td style='padding:10px;'>" + mascota.getNombre() + "</td></tr>" +
                    "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Fecha</td><td style='padding:10px;'>" + fecha + "</td></tr>" +
                    "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Hora</td><td style='padding:10px;'>" + hora + "</td></tr>" +
                    "</table></div>";
                CorreoService.enviarCorreoGeneral(correoCliente, nombreCliente, "Confirmacion de cita - Kampets", cuerpo);
            }
        }
        return hayCupo;
    }

    // ── Guardar vacuna con validaciones ───────────────────────────────────
    public void guardarVacuna(Control_vacunas cv) throws Exception {
        if (cv == null)                      throw new Exception("El registro de vacuna no puede estar vacio.");
        if (cv.getMascota() == null)         throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)         throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null) throw new Exception("La fecha de aplicacion es obligatoria.");
        cv.insertarBD();
    }

    public void actualizarVacuna(Control_vacunas cv) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro invalido.");
        if (cv.getMascota() == null)          throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)          throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null)  throw new Exception("La fecha de aplicacion es obligatoria.");
        cv.actualizarBD();
    }

    public void eliminarVacunaConCancelacion(Control_vacunas cv) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro invalido.");

        Citas citaAsociada = null;
        if (cv.getMascota() != null && cv.getFechaAplicacion() != null) {
            citaAsociada = Citas.buscarVacunacionActivaPorMascotaFechaBD(
                    cv.getMascota().getId(), cv.getFechaAplicacion());
        }
        cv.eliminarBD();

        if (citaAsociada != null) {
            Citas.actualizarEstadoBD(citaAsociada.getId(), EstadoCita.CANCELADA);
            String correo = null, nombre = "cliente";
            if (citaAsociada.getMascota() != null && citaAsociada.getMascota().getCliente() != null) {
                correo = citaAsociada.getMascota().getCliente().getCorreo();
                nombre = citaAsociada.getMascota().getCliente().getNombre();
            }
            if (correo != null && !correo.isEmpty()) {
                String mascotaNom = citaAsociada.getMascota() != null ? citaAsociada.getMascota().getNombre() : "su mascota";
                String fechaStr   = citaAsociada.getFechaCita() != null ? citaAsociada.getFechaCita().toString() : "-";
                String horaStr    = citaAsociada.getHoraCita()  != null ? citaAsociada.getHoraCita().toString()  : "-";
                String cuerpo =
                    "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#fff5f5;border-radius:10px;padding:32px;'>" +
                    "<h2 style='color:#dc2626;'>Cita Cancelada</h2>" +
                    "<p>Hola <b>" + nombre + "</b>, tu cita de vacunacion en <b>Kampets Veterinaria</b> ha sido cancelada.</p>" +
                    "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
                    "<tr><td style='padding:10px;background:#fee2e2;font-weight:bold;'>Mascota</td><td style='padding:10px;'>" + mascotaNom + "</td></tr>" +
                    "<tr><td style='padding:10px;background:#fee2e2;font-weight:bold;'>Fecha</td><td style='padding:10px;'>" + fechaStr + "</td></tr>" +
                    "<tr><td style='padding:10px;background:#fee2e2;font-weight:bold;'>Hora</td><td style='padding:10px;'>" + horaStr + "</td></tr>" +
                    "</table><p style='color:#6b7280;font-size:13px;'>Puedes agendar una nueva cita cuando lo desees.</p></div>";
                CorreoService.enviarCorreoGeneral(correo, nombre, "Cita de vacunacion cancelada - Kampets", cuerpo);
            }
        }
    }

    public List<Citas> listarCitasVacunSinRegistro() throws Exception {
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
    }

    // ── Privado: enviar correo según nuevo estado ─────────────────────────
    private void enviarCorreoEstado(Citas cita, EstadoCita nuevoEstado) throws Exception {
        String correoDestino = null, nombreCliente = "cliente";
        String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "su mascota";
        if (cita.getMascota() != null && cita.getMascota().getCliente() != null) {
            correoDestino = cita.getMascota().getCliente().getCorreo();
            nombreCliente = cita.getMascota().getCliente().getNombre();
        }
        if (correoDestino == null || correoDestino.isEmpty()) return;

        String fecha = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "-";
        String hora  = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "-";

        String color, titulo, extra;
        switch (nuevoEstado) {
            case CONFIRMADA: color = "#16a34a"; titulo = "Cita Confirmada"; extra = "Por favor presentate puntualmente."; break;
            case CANCELADA:  color = "#dc2626"; titulo = "Cita Cancelada";  extra = "Si deseas reagendar, puedes contactarnos."; break;
            case COMPLETADA: color = "#2563eb"; titulo = "Cita Completada"; extra = "Gracias por confiar en Kampets Veterinaria!"; break;
            default:         color = "#d97706"; titulo = "Cita Actualizada"; extra = "Si tienes alguna duda, contactanos."; break;
        }
        String cuerpo =
            "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
            "<h2 style='color:" + color + ";'>" + titulo + "</h2>" +
            "<p>Hola <b>" + nombreCliente + "</b>, el estado de tu cita ha sido actualizado a <b>" + nuevoEstado.name() + "</b>.</p>" +
            "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>" +
            "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Mascota</td><td style='padding:10px;'>" + nombreMascota + "</td></tr>" +
            "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Fecha</td><td style='padding:10px;'>" + fecha + "</td></tr>" +
            "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Hora</td><td style='padding:10px;'>" + hora + "</td></tr>" +
            "</table><p style='color:#6b7280;font-size:13px;'>" + extra + "</p></div>";
        CorreoService.enviarCorreoGeneral(correoDestino, nombreCliente, titulo + " - Kampets", cuerpo);
    }
}
