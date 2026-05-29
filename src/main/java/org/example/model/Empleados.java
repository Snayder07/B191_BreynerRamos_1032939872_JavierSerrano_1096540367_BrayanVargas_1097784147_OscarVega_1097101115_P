package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Empleados extends Persona implements Persistible {

    private Integer id;
    private String  apellido;
    private String  cargo;
    private String  contrasena;

    public Empleados() {}

    public Integer getId()           { return id; }
    public void    setId(Integer id) { this.id = id; }

    // getNombre() devuelve "nombre apellido" igual que antes
    @Override
    public String getNombre() {
        String n = super.getNombre();
        if (apellido != null && !apellido.trim().isEmpty())
            return (n != null ? n : "") + " " + apellido;
        return n;
    }

    public String getApellido()              { return apellido; }
    public void   setApellido(String ap)     { this.apellido = ap; }

    public String getCargo()                 { return cargo; }
    public void   setCargo(String cargo)     { this.cargo = cargo; }

    public String getContrasena()            { return contrasena; }
    public void   setContrasena(String c)    { this.contrasena = c; }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "INSERT INTO empleados(nombre_emp, apellido_emp, telefono_emp, correo_emp, cargo, contrasena_emp) " +
                "VALUES ('" + esc(super.getNombre()) + "', '" + esc(apellido) + "', '" + esc(getTelefono()) +
                "', '" + esc(getCorreo()) + "', '" + esc(cargo) + "', '" + esc(contrasena) + "')";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean actualizarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "UPDATE empleados SET nombre_emp='" + esc(super.getNombre()) +
                "', apellido_emp='" + esc(apellido) + "', correo_emp='" + esc(getCorreo()) +
                "', contrasena_emp='" + esc(contrasena) + "' WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.actualizarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public boolean eliminarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "DELETE FROM empleados WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.borrarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public static List<Empleados> consultarTodosBD() {
        List<Empleados> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM empleados");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar empleados: " + e.getMessage());
        }
        return lista;
    }

    public static Empleados buscarPorCorreoBD(String correo) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(
                    "SELECT * FROM empleados WHERE correo_emp = '" + esc(correo) + "'");
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar empleado: " + e.getMessage());
        }
        return null;
    }

    // ── Internos ──────────────────────────────────────────────

    static Empleados mapear(ResultSet rs) throws Exception {
        Empleados e = new Empleados();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre_emp"));
        e.setApellido(rs.getString("apellido_emp"));
        e.setTelefono(rs.getString("telefono_emp"));
        e.setCorreo(rs.getString("correo_emp"));
        e.setCargo(rs.getString("cargo"));
        e.setContrasena(rs.getString("contrasena_emp"));
        return e;
    }

    private static String esc(String s) {
        return s != null ? s.replace("'", "''") : "";
    }
}
