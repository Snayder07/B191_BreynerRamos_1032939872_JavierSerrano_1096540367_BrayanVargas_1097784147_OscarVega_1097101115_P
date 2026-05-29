package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Servicio {

    private Integer    id;
    private String     nombre;
    private String     descripcion;
    private BigDecimal precio;
    private Integer    duracionMin;

    public Servicio() {}

    public Integer    getId()              { return id; }
    public void       setId(Integer id)    { this.id = id; }

    public String  getNombre()             { return nombre; }
    public void    setNombre(String n)     { this.nombre = n; }

    public String  getDescripcion()        { return descripcion; }
    public void    setDescripcion(String d){ this.descripcion = d; }

    public BigDecimal getPrecio()          { return precio; }
    public void       setPrecio(BigDecimal p) { this.precio = p; }

    public Integer getDuracionMin()            { return duracionMin; }
    public void    setDuracionMin(Integer min) { this.duracionMin = min; }

    // ── Consultas ─────────────────────────────────────────────

    public static List<Servicio> consultarTodosBD() {
        List<Servicio> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM servicios ORDER BY nombre_se");
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar servicios: " + e.getMessage());
        }
        return lista;
    }

    // ── Internos ──────────────────────────────────────────────

    static Servicio mapear(ResultSet rs) throws Exception {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id"));
        s.setNombre(rs.getString("nombre_se"));
        s.setDescripcion(rs.getString("descripcion_se"));
        s.setPrecio(rs.getBigDecimal("precio_se"));
        s.setDuracionMin(rs.getInt("duracion_min"));
        return s;
    }
}
