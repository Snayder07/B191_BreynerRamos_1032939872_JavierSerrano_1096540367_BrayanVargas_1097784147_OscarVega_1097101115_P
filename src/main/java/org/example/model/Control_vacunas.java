package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Control_vacunas implements Persistible {

    private Integer   id;
    private Mascotas  mascota;
    private Vacunas   vacuna;
    private LocalDate fechaAplicacion;
    private LocalDate proximaDosis;

    public Control_vacunas() {}

    public Integer  getId()              { return id; }
    public void     setId(Integer id)    { this.id = id; }

    public Mascotas getMascota()              { return mascota; }
    public void     setMascota(Mascotas m)    { this.mascota = m; }

    public Vacunas  getVacuna()               { return vacuna; }
    public void     setVacuna(Vacunas v)      { this.vacuna = v; }

    public LocalDate getFechaAplicacion()              { return fechaAplicacion; }
    public void      setFechaAplicacion(LocalDate f)   { this.fechaAplicacion = f; }

    public LocalDate getProximaDosis()              { return proximaDosis; }
    public void      setProximaDosis(LocalDate p)   { this.proximaDosis = p; }

    public String getEstado() {
        LocalDate hoy = LocalDate.now();
        if (fechaAplicacion != null && fechaAplicacion.isAfter(hoy)) return "Pendiente";
        if (proximaDosis == null) return "Al dia";
        if (proximaDosis.isBefore(hoy)) return "Vencida";
        if (!proximaDosis.isAfter(hoy.plusDays(30))) return "Por vencer";
        return "Al dia";
    }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        if (mascota == null || vacuna == null || fechaAplicacion == null) return false;
        ConexionBD bd = new ConexionBD();
        String proxStr = proximaDosis != null ? "'" + proximaDosis + "'" : "NULL";
        String sql = "INSERT INTO control_vacunas(id_mascota, id_vacuna, fecha_aplicacion, proxima_dosis) " +
                "VALUES (" + mascota.getId() + ", " + vacuna.getId() + ", '" + fechaAplicacion + "', " + proxStr + ")";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean actualizarBD() {
        if (vacuna == null || fechaAplicacion == null) return false;
        ConexionBD bd = new ConexionBD();
        String proxStr = proximaDosis != null ? "'" + proximaDosis + "'" : "NULL";
        String sql = "UPDATE control_vacunas SET id_vacuna=" + vacuna.getId() +
                ", fecha_aplicacion='" + fechaAplicacion + "', proxima_dosis=" + proxStr +
                " WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.actualizarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean eliminarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "DELETE FROM control_vacunas WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.borrarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    private static final String SQL_BASE =
            "SELECT cv.id AS cv_id, cv.fecha_aplicacion, cv.proxima_dosis, " +
            "m.id AS m_id, m.nombre_m, m.sexo, m.caracteristica, m.fecha_nac, " +
            "cl.id AS cl_id, cl.nombre_c, cl.correo_c, cl.telefono_c, cl.direccion_c, cl.contrasena, cl.fecha_registro, " +
            "esp.id AS esp_id, esp.nombre_especie, " +
            "v.id AS v_id, v.nombre_vacuna, v.descripcion " +
            "FROM control_vacunas cv " +
            "JOIN mascotas m ON cv.id_mascota = m.id " +
            "JOIN clientes cl ON m.id_cliente = cl.id " +
            "JOIN especies esp ON m.id_especie = esp.id " +
            "JOIN vacunas v ON cv.id_vacuna = v.id";

    public static List<Control_vacunas> consultarTodosBD() {
        List<Control_vacunas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " ORDER BY m.nombre_m, cv.fecha_aplicacion");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar control vacunas: " + e.getMessage());
        }
        return lista;
    }

    public static List<Control_vacunas> consultarPorClienteBD(int clienteId) {
        List<Control_vacunas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE cl.id = " + clienteId +
                    " ORDER BY m.nombre_m, cv.fecha_aplicacion");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar control vacunas: " + e.getMessage());
        }
        return lista;
    }

    // ── Internos ──────────────────────────────────────────────

    private static Control_vacunas mapear(ResultSet rs) throws Exception {
        Control_vacunas cv = new Control_vacunas();
        cv.setId(rs.getInt("cv_id"));
        java.sql.Date fa = rs.getDate("fecha_aplicacion");
        if (fa != null) cv.setFechaAplicacion(fa.toLocalDate());
        java.sql.Date pd = rs.getDate("proxima_dosis");
        if (pd != null) cv.setProximaDosis(pd.toLocalDate());

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

        cv.setMascota(m);

        Vacunas v = new Vacunas();
        v.setId(rs.getInt("v_id"));
        v.setNombre(rs.getString("nombre_vacuna"));
        v.setDescripcion(rs.getString("descripcion"));
        cv.setVacuna(v);

        return cv;
    }
}
