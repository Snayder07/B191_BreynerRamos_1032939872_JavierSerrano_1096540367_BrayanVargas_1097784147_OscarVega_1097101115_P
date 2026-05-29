package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Vacunas {

    private Integer id;
    private String  nombre;
    private String  descripcion;

    public Vacunas() {}

    public Integer getId()              { return id; }
    public void    setId(Integer id)    { this.id = id; }

    public String getNombre()           { return nombre; }
    public void   setNombre(String n)   { this.nombre = n; }

    public String getDescripcion()          { return descripcion; }
    public void   setDescripcion(String d)  { this.descripcion = d; }

    // ── Consultas ─────────────────────────────────────────────

    public static List<Vacunas> consultarTodosBD() {
        List<Vacunas> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM vacunas ORDER BY nombre_vacuna");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar vacunas: " + e.getMessage());
        }
        return lista;
    }

    // ── Internos ──────────────────────────────────────────────

    static Vacunas mapear(ResultSet rs) throws Exception {
        Vacunas v = new Vacunas();
        v.setId(rs.getInt("id"));
        v.setNombre(rs.getString("nombre_vacuna"));
        v.setDescripcion(rs.getString("descripcion"));
        return v;
    }
}
