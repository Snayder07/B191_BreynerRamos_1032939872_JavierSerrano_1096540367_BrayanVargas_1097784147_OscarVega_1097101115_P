package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Cita_servicio {

    private Integer    id;
    private Citas      cita;
    private Servicio   servicio;
    private BigDecimal precioCobrado;

    public Cita_servicio() {}

    public Integer   getId()              { return id; }
    public void      setId(Integer id)    { this.id = id; }

    public Citas     getCita()            { return cita; }
    public void      setCita(Citas c)     { this.cita = c; }

    public Servicio  getServicio()            { return servicio; }
    public void      setServicio(Servicio s)  { this.servicio = s; }

    public BigDecimal getPrecioCobrado()          { return precioCobrado; }
    public void       setPrecioCobrado(BigDecimal p){ this.precioCobrado = p; }

    // ── CRUD ──────────────────────────────────────────────────

    public boolean insertarBD() {
        if (cita == null || servicio == null) return false;
        ConexionBD bd = new ConexionBD();
        String precioStr = precioCobrado != null ? precioCobrado.toString() : "NULL";
        String sql = "INSERT INTO cita_servicio(id_cita, id_servicio, precio_cobrado) " +
                "VALUES (" + cita.getId() + ", " + servicio.getId() + ", " + precioStr + ")";
        if (bd.setAutoCommitBD(false)) {
            if (bd.insertarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public static List<Cita_servicio> consultarPorCitaBD(int citaId) {
        List<Cita_servicio> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            String sql = "SELECT cs.id, cs.id_cita, cs.precio_cobrado, " +
                    "s.id AS s_id, s.nombre_se, s.descripcion_se, s.precio_se, s.duracion_min " +
                    "FROM cita_servicio cs JOIN servicios s ON cs.id_servicio = s.id " +
                    "WHERE cs.id_cita = " + citaId;
            ResultSet rs = bd.consultarBD(sql);
            while (rs.next()) {
                Cita_servicio cs = new Cita_servicio();
                cs.setId(rs.getInt("id"));
                cs.setPrecioCobrado(rs.getBigDecimal("precio_cobrado"));
                Servicio srv = new Servicio();
                srv.setId(rs.getInt("s_id"));
                srv.setNombre(rs.getString("nombre_se"));
                srv.setDescripcion(rs.getString("descripcion_se"));
                srv.setPrecio(rs.getBigDecimal("precio_se"));
                srv.setDuracionMin(rs.getInt("duracion_min"));
                cs.setServicio(srv);
                lista.add(cs);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar servicios de cita: " + e.getMessage());
        }
        return lista;
    }
}
