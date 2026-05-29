package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona implements Persistible {

    private Integer   id;
    private String    direccion;
    private String    contrasena;
    private LocalDate fechaRegistro;

    public Cliente() {}

    public Integer   getId()           { return id; }
    public void      setId(Integer id) { this.id = id; }

    public String getDireccion()             { return direccion; }
    public void   setDireccion(String d)     { this.direccion = d; }

    public String getContrasena()            { return contrasena; }
    public void   setContrasena(String c)    { this.contrasena = c; }

    public LocalDate getFechaRegistro()              { return fechaRegistro; }
    public void      setFechaRegistro(LocalDate f)   { this.fechaRegistro = f; }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "INSERT INTO clientes(nombre_c, correo_c, telefono_c, direccion_c, contrasena, fecha_registro) " +
                "VALUES ('" + esc(getNombre()) + "', '" + esc(getCorreo()) + "', '" + esc(getTelefono()) + "', '" +
                esc(direccion != null ? direccion : "") + "', '" + esc(contrasena) + "', '" + LocalDate.now() + "')";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean actualizarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "UPDATE clientes SET nombre_c='" + esc(getNombre()) + "', correo_c='" + esc(getCorreo()) +
                "', telefono_c='" + esc(getTelefono()) + "', contrasena='" + esc(contrasena) +
                "' WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.actualizarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean eliminarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "DELETE FROM clientes WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.borrarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public static List<Cliente> consultarTodosBD() {
        List<Cliente> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM clientes");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar clientes: " + e.getMessage());
        }
        return lista;
    }

    public static Cliente buscarPorCorreoBD(String correo) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(
                    "SELECT * FROM clientes WHERE LOWER(correo_c) = LOWER('" + esc(correo) + "')");
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    public static Cliente buscarPorIdBD(int id) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM clientes WHERE id=" + id);
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar cliente: " + e.getMessage());
        }
        return null;
    }

    // ── Internos ──────────────────────────────────────────────

    static Cliente mapear(ResultSet rs) throws Exception {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre_c"));
        c.setCorreo(rs.getString("correo_c"));
        c.setTelefono(rs.getString("telefono_c"));
        c.setDireccion(rs.getString("direccion_c"));
        c.setContrasena(rs.getString("contrasena"));
        java.sql.Date f = rs.getDate("fecha_registro");
        if (f != null) c.setFechaRegistro(f.toLocalDate());
        return c;
    }

    static String esc(String s) {
        return s != null ? s.replace("'", "''") : "";
    }
}
