package org.example.service;

import org.example.model.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CitaService {

    // ── Atributos propios ─────────────────────────────────────────────────
    private int limiteCupo;   // maximo de citas activas permitidas al mismo tiempo

    public CitaService() {
        this.limiteCupo = 10;
    }

    public int getLimiteCupo()           { return limiteCupo; }
    public void setLimiteCupo(int limite){ this.limiteCupo = limite; }

    // ── Confirmar cita y notificar al cliente ─────────────────────────────
    public void confirmarCita(Integer id) throws Exception {
        Citas cita = Citas.buscarPorIdBD(id);
        if (cita == null) throw new Exception("No se encontro la cita.");
        Citas.actualizarEstadoBD(id, EstadoCita.CONFIRMADA);
        enviarCorreoEstado(cita, EstadoCita.CONFIRMADA);
    }

    // ── Cancelar cita y notificar al cliente ──────────────────────────────
    public void cancelarCita(Integer id) throws Exception {
        Citas cita = Citas.buscarPorIdBD(id);
        if (cita == null) throw new Exception("No se encontro la cita.");
        Citas.actualizarEstadoBD(id, EstadoCita.CANCELADA);
        enviarCorreoEstado(cita, EstadoCita.CANCELADA);
    }

    // ── Agendar cita desde el panel del cliente ───────────────────────────
    public boolean agendarCita(Mascotas mascota, Empleados empleado,
                               String fechaStr, String horaStr,
                               String direccionDomicilio, String motivo) throws Exception {
        if (mascota  == null) throw new Exception("Selecciona una mascota.");
        if (empleado == null) throw new Exception("Selecciona un veterinario.");
        if (fechaStr == null || fechaStr.trim().isEmpty()) throw new Exception("Ingresa la fecha (yyyy-MM-dd).");
        if (horaStr  == null || horaStr.trim().isEmpty())  throw new Exception("Selecciona una hora.");

        LocalDate fecha;
        LocalTime hora;
        try { fecha = LocalDate.parse(fechaStr.trim()); }
        catch (DateTimeParseException e) { throw new Exception("Formato de fecha invalido. Usa: yyyy-MM-dd"); }
        try { hora  = LocalTime.parse(horaStr.trim()); }
        catch (DateTimeParseException e) { throw new Exception("Formato de hora invalido. Usa: HH:mm"); }

        // Contar citas activas para saber si hay cupo
        List<Citas> citasActivas = new ArrayList<>();
        for (Citas c : Citas.consultarTodosBD()) {
            if (c.getEstadoCita() != EstadoCita.CANCELADA && c.getEstadoCita() != EstadoCita.COMPLETADA)
                citasActivas.add(c);
        }
        boolean hayCupo = citasActivas.size() < limiteCupo;

        // Guardar la cita en la base de datos
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

        // Si hay cupo, enviar correo de confirmacion
        if (hayCupo && mascota.getCliente() != null) {
            String nombreCliente = mascota.getCliente().getNombre();
            String correoCliente = mascota.getCliente().getCorreo();
            if (correoCliente != null && !correoCliente.isEmpty()) {
                String cuerpo = construirCorreo(
                        nombreCliente, mascota.getNombre(),
                        fecha.toString(), hora.toString(),
                        "Cita Confirmada", "#16a34a",
                        "Tu cita ha sido confirmada. Por favor presentate puntualmente.");
                CorreoService.enviarCorreoGeneral(correoCliente, nombreCliente, "Confirmacion de cita - Kampets", cuerpo);
            }
        }
        return hayCupo;
    }

    // ── Validar y guardar registro de vacuna ──────────────────────────────
    public void guardarVacuna(Control_vacunas cv) throws Exception {
        if (cv == null)                      throw new Exception("El registro de vacuna no puede estar vacio.");
        if (cv.getMascota() == null)         throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)         throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null) throw new Exception("La fecha de aplicacion es obligatoria.");
        cv.insertarBD();
    }

    // ── Validar y actualizar registro de vacuna ───────────────────────────
    public void actualizarVacuna(Control_vacunas cv) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro invalido.");
        if (cv.getMascota() == null)          throw new Exception("Debe seleccionar una mascota.");
        if (cv.getVacuna()  == null)          throw new Exception("Debe seleccionar una vacuna.");
        if (cv.getFechaAplicacion() == null)  throw new Exception("La fecha de aplicacion es obligatoria.");
        cv.actualizarBD();
    }

    // ── Eliminar vacuna y cancelar la cita asociada si existe ─────────────
    public void eliminarVacunaConCancelacion(Control_vacunas cv) throws Exception {
        if (cv == null || cv.getId() == null) throw new Exception("Registro invalido.");

        // Buscar si hay una cita de vacunacion asociada
        Citas citaAsociada = null;
        if (cv.getMascota() != null && cv.getFechaAplicacion() != null) {
            citaAsociada = Citas.buscarVacunacionActivaPorMascotaFechaBD(
                    cv.getMascota().getId(), cv.getFechaAplicacion());
        }

        cv.eliminarBD();

        // Si habia cita, cancelarla y notificar al cliente
        if (citaAsociada != null) {
            Citas.actualizarEstadoBD(citaAsociada.getId(), EstadoCita.CANCELADA);
            enviarCorreoEstado(citaAsociada, EstadoCita.CANCELADA);
        }
    }

    // ── Obtener citas de vacunacion sin vacuna asignada ───────────────────
    public List<Citas> listarCitasVacunSinRegistro() throws Exception {
        List<Citas> citasVacun   = Citas.consultarVacunasBD();
        List<Control_vacunas> registros = Control_vacunas.consultarTodosBD();
        List<Citas> sinRegistro  = new ArrayList<>();

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

    // ─────────────────────────────────────────────────────────────────────
    //  METODOS PRIVADOS
    // ─────────────────────────────────────────────────────────────────────

    // Determina el asunto y mensaje segun el estado y envia el correo
    private void enviarCorreoEstado(Citas cita, EstadoCita nuevoEstado) throws Exception {
        if (cita.getMascota() == null || cita.getMascota().getCliente() == null) return;

        String correo  = cita.getMascota().getCliente().getCorreo();
        String cliente = cita.getMascota().getCliente().getNombre();
        if (correo == null || correo.isEmpty()) return;

        String mascota = cita.getMascota().getNombre();
        String fecha   = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "-";
        String hora    = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "-";

        String titulo, colorTitulo, mensaje;
        switch (nuevoEstado) {
            case CONFIRMADA:
                titulo      = "Cita Confirmada";
                colorTitulo = "#16a34a";
                mensaje     = "Tu cita ha sido confirmada. Por favor presentate puntualmente.";
                break;
            case CANCELADA:
                titulo      = "Cita Cancelada";
                colorTitulo = "#dc2626";
                mensaje     = "Tu cita fue cancelada. Puedes reagendar cuando lo desees.";
                break;
            case COMPLETADA:
                titulo      = "Cita Completada";
                colorTitulo = "#2563eb";
                mensaje     = "Gracias por confiar en Kampets Veterinaria.";
                break;
            default:
                titulo      = "Cita Actualizada";
                colorTitulo = "#d97706";
                mensaje     = "El estado de tu cita fue actualizado.";
        }

        String cuerpo = construirCorreo(cliente, mascota, fecha, hora, titulo, colorTitulo, mensaje);
        CorreoService.enviarCorreoGeneral(correo, cliente, titulo + " - Kampets", cuerpo);
    }

    // Construye el cuerpo HTML del correo con los datos de la cita
    private String construirCorreo(String cliente, String mascota,
                                   String fecha, String hora,
                                   String titulo, String colorTitulo, String mensaje) {
        return "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;"
             + "background:#f0fdf4;border-radius:10px;padding:32px;'>"
             + "<h2 style='color:" + colorTitulo + ";'>" + titulo + "</h2>"
             + "<p>Hola <b>" + cliente + "</b>. " + mensaje + "</p>"
             + "<table style='width:100%;border-collapse:collapse;margin:20px 0;'>"
             + "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Mascota</td>"
             +     "<td style='padding:10px;'>" + mascota + "</td></tr>"
             + "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Fecha</td>"
             +     "<td style='padding:10px;'>" + fecha + "</td></tr>"
             + "<tr><td style='padding:10px;background:#dcfce7;font-weight:bold;'>Hora</td>"
             +     "<td style='padding:10px;'>" + hora  + "</td></tr>"
             + "</table></div>";
    }
}
