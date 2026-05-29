package org.example.model;

import org.example.util.ConexionBD;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Productos implements Persistible {

    private Integer    id;
    private byte[]     foto;
    private String     nombre;
    private String     tipo;
    private String     marca;
    private BigDecimal precio;
    private Integer    stock;

    public Productos() {}

    public Integer    getId()              { return id; }
    public void       setId(Integer id)    { this.id = id; }

    public byte[] getFoto()                { return foto; }
    public void   setFoto(byte[] foto)     { this.foto = foto; }

    public String  getNombre()             { return nombre; }
    public void    setNombre(String n)     { this.nombre = n; }

    public String  getTipo()               { return tipo; }
    public void    setTipo(String t)       { this.tipo = t; }

    public String  getMarca()              { return marca; }
    public void    setMarca(String m)      { this.marca = m; }

    public BigDecimal getPrecio()              { return precio; }
    public void       setPrecio(BigDecimal p)  { this.precio = p; }

    public Integer getStock()              { return stock; }
    public void    setStock(Integer s)     { this.stock = s; }

    // ── CRUD ──────────────────────────────────────────────────
    // Se usa PreparedStatement para manejar el campo BYTEA (foto)

    public boolean insertarBD() {
        ConexionBD bd = new ConexionBD();
        try {
            String sql = "INSERT INTO productos(nombre_pro, tipo_pro, marca_pro, precio_pro, stock_pro, foto_pro) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = bd.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.setString(3, marca);
            ps.setBigDecimal(4, precio);
            ps.setInt(5, stock != null ? stock : 0);
            if (foto != null) ps.setBytes(6, foto);
            else ps.setNull(6, java.sql.Types.BINARY);
            ps.execute();
            bd.cerrarConexion();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al guardar producto: " + e.getMessage());
            bd.cerrarConexion();
            return false;
        }
    }

    public boolean actualizarBD() {
        ConexionBD bd = new ConexionBD();
        try {
            String sql = "UPDATE productos SET nombre_pro=?, tipo_pro=?, marca_pro=?, precio_pro=?, stock_pro=?, foto_pro=? WHERE id=?";
            PreparedStatement ps = bd.getConnection().prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ps.setString(3, marca);
            ps.setBigDecimal(4, precio);
            ps.setInt(5, stock != null ? stock : 0);
            if (foto != null) ps.setBytes(6, foto);
            else ps.setNull(6, java.sql.Types.BINARY);
            ps.setInt(7, id);
            ps.executeUpdate();
            bd.cerrarConexion();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar producto: " + e.getMessage());
            bd.cerrarConexion();
            return false;
        }
    }

    public boolean eliminarBD() {
        ConexionBD bd = new ConexionBD();
        String sql = "DELETE FROM productos WHERE id=" + id;
        if (bd.setAutoCommitBD(false)) {
            if (bd.borrarBD(sql)) { bd.commitBD(); bd.cerrarConexion(); return true; }
            bd.rollbackBD();
        }
        bd.cerrarConexion();
        return false;
    }

    public static List<Productos> consultarTodosBD() {
        List<Productos> lista = new ArrayList<>();
        ConexionBD bd = new ConexionBD();
        try {
            ResultSet rs = bd.consultarBD("SELECT * FROM productos ORDER BY nombre_pro");
            while (rs.next()) {
                Productos p = new Productos();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre_pro"));
                p.setTipo(rs.getString("tipo_pro"));
                p.setMarca(rs.getString("marca_pro"));
                p.setPrecio(rs.getBigDecimal("precio_pro"));
                p.setStock(rs.getInt("stock_pro"));
                p.setFoto(rs.getBytes("foto_pro"));
                lista.add(p);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al consultar productos: " + e.getMessage());
        }
        return lista;
    }
}
