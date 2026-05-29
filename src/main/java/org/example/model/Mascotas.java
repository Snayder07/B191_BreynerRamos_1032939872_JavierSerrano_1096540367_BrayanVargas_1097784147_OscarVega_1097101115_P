package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Mascotas implements Persistible {

    private Integer   id;
    private Cliente   cliente;
    private Especies  especie;
    private String    nombre;
    private LocalDate fechaNac;
    private String    sexo;
    private String    caracteristica;

    public Mascotas() {}

    public Integer  getId()              { return id; }
    public void     setId(Integer id)    { this.id = id; }

    public Cliente  getCliente()              { return cliente; }
    public void     setCliente(Cliente c)     { this.cliente = c; }

    public Especies getEspecie()              { return especie; }
    public void     setEspecie(Especies e)    { this.especie = e; }

    public String   getNombre()               { return nombre; }
    public void     setNombre(String n)       { this.nombre = n; }

    public LocalDate getFechaNac()            { return fechaNac; }
    public void      setFechaNac(LocalDate f) { this.fechaNac = f; }

    public String getSexo()                   { return sexo; }
    public void   setSexo(String s)           { this.sexo = s; }

    public String getCaracteristica()             { return caracteristica; }
    public void   setCaracteristica(String c)     { this.caracteristica = c; }

    public String getEtiqueta() {
        String base = nombre != null ? nombre : "?";
        String esp  = (especie != null) ? especie.getNombre() : null;
        String car  = (caracteristica != null && !caracteristica.trim().isEmpty()) ? caracteristica.trim() : null;
        if (esp != null && car != null) return base + " (" + esp + " — " + car + ")";
        if (esp != null)                return base + " (" + esp + ")";
        if (car != null)                return base + " [" + car + "]";
        return base;
    }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        if (cliente == null || especie == null) return false;
        ConexionBD bd = new ConexionBD();
        String fechaStr = fechaNac != null ? "'" + fechaNac + "'" : "NULL";
        String carStr   = caracteristica != null ? "'" + esc(caracteristica) + "'" : "NULL";
        String sql = "INSERT INTO mascotas(id_cliente, id_especie, nombre_m, fecha_nac, sexo, caracteristica) " +
                "VALUES (" + cliente.getId() + ", " + especie.getId() + ", '" + esc(nombre) + "', " +
                fechaStr + ", '" + esc(sexo) + "', " + carStr + ")";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean actualizarBD() {
        if (especie == null) return false;
        ConexionBD bd = new ConexionBD();
        String fechaStr = fechaNac != null ? "'" + fechaNac + "'" : "NULL";
        String carStr   = caracteristica != null ? "'" + esc(caracteristica) + "'" : "NULL";
        String sql = "UPDATE mascotas SET nombre_m='" + esc(nombre) + "', id_especie=" + especie.getId() +
                ", fecha_nac=" + fechaStr + ", sexo='" + esc(sexo) + "', caracteristica=" + carStr +
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
        String sql = "DELETE FROM mascotas WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.borrarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    private static final String SQL_BASE =
            "SELECT m.id AS m_id, m.nombre_m, m.sexo, m.caracteristica, m.fecha_nac, " +
            "cl.id AS cl_id, cl.nombre_c, cl.correo_c, cl.telefono_c, cl.direccion_c, cl.contrasena, cl.fecha_registro, " +
            "esp.id AS esp_id, esp.nombre_especie " +
            "FROM mascotas m " +
            "JOIN clientes cl ON m.id_cliente = cl.id " +
            "JOIN especies esp ON m.id_especie = esp.id";

    public static List<Mascotas> consultarTodosBD() {
        List<Mascotas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " ORDER BY m.nombre_m");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar mascotas: " + e.getMessage());
        }
        return lista;
    }

    public static List<Mascotas> consultarPorClienteBD(int clienteId) {
        List<Mascotas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE cl.id = " + clienteId + " ORDER BY m.nombre_m");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar mascotas: " + e.getMessage());
        }
        return lista;
    }

    public static Mascotas buscarPorIdBD(int id) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(SQL_BASE + " WHERE m.id = " + id);
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar mascota: " + e.getMessage());
        }
        return null;
    }

    // ── Internos ──────────────────────────────────────────────

    static Mascotas mapear(ResultSet rs) throws Exception {
        Mascotas m = new Mascotas();
        m.setId(rs.getInt("m_id"));
        m.setNombre(rs.getString("nombre_m"));
        m.setSexo(rs.getString("sexo"));
        m.setCaracteristica(rs.getString("caracteristica"));
        java.sql.Date fn = rs.getDate("fecha_nac");
        if (fn != null) m.setFechaNac(fn.toLocalDate());

        Cliente c = new Cliente();
        c.setId(rs.getInt("cl_id"));
        c.setNombre(rs.getString("nombre_c"));
        c.setCorreo(rs.getString("correo_c"));
        c.setTelefono(rs.getString("telefono_c"));
        c.setDireccion(rs.getString("direccion_c"));
        c.setContrasena(rs.getString("contrasena"));
        java.sql.Date fr = rs.getDate("fecha_registro");
        if (fr != null) c.setFechaRegistro(fr.toLocalDate());
        m.setCliente(c);

        Especies esp = new Especies();
        esp.setId(rs.getInt("esp_id"));
        esp.setNombre(rs.getString("nombre_especie"));
        m.setEspecie(esp);

        return m;
    }

    private static String esc(String s) {
        return s != null ? s.replace("'", "''") : "";
    }
}
