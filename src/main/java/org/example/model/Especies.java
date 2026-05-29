package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Especies {

    private Integer id;
    private String  nombre;

    public Especies() {}

    public Integer getId()              { return id; }
    public void    setId(Integer id)    { this.id = id; }

    public String getNombre()           { return nombre; }
    public void   setNombre(String n)   { this.nombre = n; }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "INSERT INTO especies(nombre_especie) VALUES ('" + esc(nombre) + "')";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public static List<Especies> consultarTodosBD() {
        List<Especies> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM especies ORDER BY nombre_especie");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar especies: " + e.getMessage());
        }
        return lista;
    }

    public static Especies buscarPorNombreBD(String nombre) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD(
                    "SELECT * FROM especies WHERE LOWER(nombre_especie) = LOWER('" + esc(nombre) + "') LIMIT 1");
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar especie: " + e.getMessage());
        }
        return null;
    }

    public static Especies buscarPorIdBD(int id) {
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM especies WHERE id=" + id);
            if (rs.next()) return mapear(rs);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al buscar especie: " + e.getMessage());
        }
        return null;
    }

    // ── Internos ──────────────────────────────────────────────

    static Especies mapear(ResultSet rs) throws Exception {
        Especies e = new Especies();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre_especie"));
        return e;
    }

    private static String esc(String s) {
        return s != null ? s.replace("'", "''") : "";
    }
}
