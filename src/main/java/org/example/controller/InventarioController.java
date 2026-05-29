package org.example.controller;

import org.example.model.Productos;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class InventarioController {

    public List<Productos> listarTodos() {
        try { return Productos.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public boolean agregarProducto(String nombre, String tipo, String marca,
                                   String precio, String stock, byte[] foto, JPanel panel) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                throw new Exception("El nombre del producto es obligatorio.");
            if (precio == null || precio.trim().isEmpty())
                throw new Exception("El precio es obligatorio.");
            if (stock == null || stock.trim().isEmpty())
                throw new Exception("El stock es obligatorio.");

            Productos p = new Productos();
            p.setNombre(nombre.trim());
            p.setTipo(tipo  != null ? tipo.trim()  : null);
            p.setMarca(marca != null ? marca.trim() : null);
            try { p.setPrecio(new BigDecimal(precio.trim())); }
            catch (NumberFormatException e) { throw new Exception("El precio debe ser un numero valido (ej: 9.99)."); }
            try { p.setStock(Integer.parseInt(stock.trim())); }
            catch (NumberFormatException e) { throw new Exception("El stock debe ser un numero entero."); }
            if (foto != null) p.setFoto(foto);

            p.insertarBD();
            JOptionPane.showMessageDialog(panel, "Producto agregado exitosamente.");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void eliminarProducto(Integer id, JPanel panel) {
        try {
            if (id == null) throw new Exception("ID de producto invalido.");
            Productos p = new Productos();
            p.setId(id);
            p.eliminarBD();
            JOptionPane.showMessageDialog(panel, "Producto eliminado exitosamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean actualizarProducto(Productos producto, JPanel panel) {
        try {
            if (producto == null) throw new Exception("El producto no puede ser nulo.");
            producto.actualizarBD();
            JOptionPane.showMessageDialog(panel, "Producto actualizado exitosamente.");
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
