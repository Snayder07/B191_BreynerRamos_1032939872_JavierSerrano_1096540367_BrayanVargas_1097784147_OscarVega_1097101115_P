package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Citas implements Persistible {

    private Integer    id;
    private Mascotas   mascota;
    private Empleados  empleado;
    private LocalDate  fechaCita;
    private LocalTime  horaCita;
    private EstadoCita estadoCita;
    private String     direccionDomicilio;
    private String     motivo;
    private List<Cita_servicio> servicios = new ArrayList<>();

    public Citas() {}

    public Integer   getId()              { return id; }
    public void      setId(Integer id)    { this.id = id; }

    public Mascotas  getMascota()              { return mascota; }
    public void      setMascota(Mascotas m)    { this.mascota = m; }

    public Empleados getEmpleado()             { return empleado; }
    public void      setEmpleado(Empleados e)  { this.empleado = e; }

    public LocalDate  getFechaCita()           { return fechaCita; }
    public void       setFechaCita(LocalDate f){ this.fechaCita = f; }

    public LocalTime  getHoraCita()            { return horaCita; }
    public void       setHoraCita(LocalTime h) { this.horaCita = h; }

    public EstadoCita getEstadoCita()              { return estadoCita; }
    public void       setEstadoCita(EstadoCita e)  { this.estadoCita = e; }

    public String getDireccionDomicilio()          { return direccionDomicilio; }
    public void   setDireccionDomicilio(String d)  { this.direccionDomicilio = d; }

    public String getMotivo()              { return motivo; }
    public void   setMotivo(String m)      { this.motivo = m; }

    public List<Cita_servicio> getServicios() { return servicios; }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        if (mascota == null || empleado == null) return false;
        ConexionBD bd = new ConexionBD();
        String estado = estadoCita != null ? estadoCita.name() : "PENDIENTE";
        String domStr = (direccionDomicilio != null && !direccionDomicilio.trim().isEmpty())
                ? "'" + esc(direccionDomicilio) + "'" : "NULL";
        String motStr = (motivo != null && !motivo.trim().isEmpty())
                ? "'" + esc(motivo) + "'" : "NULL";
        String sql = "INSERT INTO citas(id_mascota, id_empleado, fecha_cita, hora_cita, estado_cita, direccion_domicilio, motivo) " +
                "VALUES (" + mascota.getId() + ", " + empleado.getId() + ", '" + fechaCita + "', '" + horaCita +
                "', '" + estado + "', " + domStr + ", " + motStr + ")";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean actualizarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "UPDATE citas SET fecha_cita='" + fechaCita + "', hora_cita='" + horaCita +
                "', estado_cita='" + estadoCita.name() + "' WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.actualizarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public static boolean actualizarEstadoBD(Integer id, EstadoCita nuevoEstado) {
        ConexionBD bd = new ConexionBD();
        String sql = "UPDATE citas SET estado_cita='" + nuevoEstado.name() + "' WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.actualizarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean eliminarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "DELETE FROM citas WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.borrarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    // ── Consultas ─────────────────────────────────────────────

    private static final String SQL_BASE =
            "SELECT c.id AS cita_id, c.fecha_cita, c.hora_cita, c.estado_cita, c.direccion_domicilio, c.motivo, " +
            "m.id AS m_id, m.nombre_m, m.sexo, m.caracteristica, m.fecha_nac, " +
            "cl.id AS cl_id, cl.nombre_c, cl.correo_c, cl.telefono_c, cl.direccion_c, cl.contrasena, cl.fecha_registro, " +
            "emp.id AS emp_id, emp.nombre_emp, emp.apellido_emp, emp.correo_emp, emp.cargo, emp.contrasena_emp, " +
            "esp.id AS esp_id, esp.nombre_especie " +
            "FROM citas c " +
            "JOIN mascotas m ON c.id_mascota = m.id " +
            "JOIN clientes cl ON m.id_cliente = cl.id " +
            "JOIN empleados emp ON c.id_empleado = emp.id " +
            "JOIN especies esp ON m.id_especie = esp.id";

    public static List<Citas> consultarTodosBD() {
        List<Citas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " ORDER BY c.fecha_cita DESC, c.hora_cita DESC");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar citas: " + e.getMessage());
        }
        return lista;
    }

    public static List<Citas> consultarPorClienteBD(int clienteId) {
        List<Citas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE cl.id = " + clienteId +
                    " ORDER BY c.fecha_cita DESC, c.hora_cita DESC");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar citas: " + e.getMessage());
        }
        return lista;
    }

    public static List<Citas> consultarPasadasPorClienteBD(int clienteId) {
        List<Citas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE cl.id = " + clienteId +
                    " AND c.fecha_cita < CURRENT_DATE ORDER BY c.fecha_cita DESC");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar citas pasadas: " + e.getMessage());
        }
        return lista;
    }

    public static List<Citas> consultarDeHoyBD() {
        List<Citas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        // Usamos la fecha de Java para evitar diferencias de zona horaria con CURRENT_DATE
        String hoy = java.time.LocalDate.now().toString(); // formato yyyy-MM-dd
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE c.fecha_cita = '" + hoy + "' ORDER BY c.hora_cita ASC");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar citas de hoy: " + e.getMessage());
        }
        return lista;
    }

    public static List<Citas> consultarVacunasBD() {
        List<Citas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE +
                    " WHERE c.estado_cita IN ('PENDIENTE','CONFIRMADA')" +
                    " AND LOWER(c.motivo) LIKE '%vacun%'" +
                    " ORDER BY c.fecha_cita ASC, c.hora_cita ASC");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar citas vacunas: " + e.getMessage());
        }
        return lista;
    }

    public static Citas buscarVacunacionActivaPorMascotaFechaBD(Integer mascotaId, LocalDate fecha) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE +
                    " WHERE m.id = " + mascotaId +
                    " AND c.fecha_cita = '" + fecha + "'" +
                    " AND LOWER(c.motivo) LIKE '%vacun%'" +
                    " AND c.estado_cita NOT IN ('CANCELADA','COMPLETADA')" +
                    " ORDER BY c.id DESC LIMIT 1");
            if (rs.next()) return mapear(rs);
        } catch (Exception e) { /* no encontrada */ }
        return null;
    }

    public static Citas buscarPorIdBD(Integer id) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE c.id = " + id);
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar cita: " + e.getMessage());
        }
        return null;
    }

    // ── Internos ──────────────────────────────────────────────

    private static Citas mapear(ResultSet rs) throws Exception {
        Citas c = new Citas();
        c.setId(rs.getInt("cita_id"));

        java.sql.Date fd = rs.getDate("fecha_cita");
        if (fd != null) c.setFechaCita(fd.toLocalDate());

        java.sql.Time ht = rs.getTime("hora_cita");
        if (ht != null) c.setHoraCita(ht.toLocalTime());

        String est = rs.getString("estado_cita");
        if (est != null) {
            try { c.setEstadoCita(EstadoCita.valueOf(est)); } catch (Exception ignored) {}
        }

        c.setDireccionDomicilio(rs.getString("direccion_domicilio"));
        c.setMotivo(rs.getString("motivo"));

        // Mascota con especie y cliente
        Mascotas m = new Mascotas();
        m.setId(rs.getInt("m_id"));
        m.setNombre(rs.getString("nombre_m"));
        m.setSexo(rs.getString("sexo"));
        m.setCaracteristica(rs.getString("caracteristica"));
        java.sql.Date fn = rs.getDate("fecha_nac");
        if (fn != null) m.setFechaNac(fn.toLocalDate());

        Cliente cl = new Cliente();
        cl.setId(rs.getInt("cl_id"));
        cl.setNombre(rs.getString("nombre_c"));
        cl.setCorreo(rs.getString("correo_c"));
        cl.setTelefono(rs.getString("telefono_c"));
        cl.setDireccion(rs.getString("direccion_c"));
        cl.setContrasena(rs.getString("contrasena"));
        java.sql.Date fr = rs.getDate("fecha_registro");
        if (fr != null) cl.setFechaRegistro(fr.toLocalDate());
        m.setCliente(cl);

        Especies esp = new Especies();
        esp.setId(rs.getInt("esp_id"));
        esp.setNombre(rs.getString("nombre_especie"));
        m.setEspecie(esp);

        c.setMascota(m);

        // Empleado
        Empleados emp = new Empleados();
        emp.setId(rs.getInt("emp_id"));
        emp.setNombre(rs.getString("nombre_emp"));
        emp.setApellido(rs.getString("apellido_emp"));
        emp.setCorreo(rs.getString("correo_emp"));
        emp.setCargo(rs.getString("cargo"));
        emp.setContrasena(rs.getString("contrasena_emp"));
        c.setEmpleado(emp);

        return c;
    }

    private static String esc(String s) {
        return s != null ? s.replace("'", "''") : "";
    }
}
