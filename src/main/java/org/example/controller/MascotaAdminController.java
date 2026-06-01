package org.example.controller;

import org.example.model.Cliente;
import org.example.model.Especies;
import org.example.model.Mascotas;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class MascotaAdminController {

    public List<Mascotas> listarTodas() {
        try { return Mascotas.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public List<Especies> listarEspecies() {
        try { return Especies.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public Especies obtenerOCrearEspecie(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        Especies existente = Especies.buscarPorNombreBD(nombre.trim());
        if (existente != null) return existente;
        Especies nueva = new Especies();
        nueva.setNombre(nombre.trim());
        nueva.insertarBD();
        return Especies.buscarPorNombreBD(nombre.trim());
    }

    public List<Cliente> listarClientes() {
        try { return Cliente.consultarTodosBD(); }
        catch (Exception e) { return Collections.emptyList(); }
    }

    public static class NecesitaCaracteristicaException extends Exception {
        public NecesitaCaracteristicaException(String msg) { super(msg); }
    }

    public boolean registrarMascota(String nombre, Especies especie, Cliente cliente,
                                    String fechaNacStr, String sexo, String caracteristica,
                                    JPanel panel) {
        return registrarMascota(nombre, especie, cliente, fechaNacStr, sexo, caracteristica, panel, null);
    }

    public boolean registrarMascota(String nombre, Especies especie, Cliente cliente,
                                    String fechaNacStr, String sexo, String caracteristica,
                                    JPanel panel, Runnable onNecesitaCaracteristica) {
        try {
            if (nombre == null || nombre.trim().isEmpty())
                throw new Exception("El nombre de la mascota es obligatorio.");
            if (especie == null) throw new Exception("Selecciona una especie.");
            if (cliente == null) throw new Exception("Selecciona un dueno.");

            LocalDate fechaNac = null;
            if (fechaNacStr != null && !fechaNacStr.trim().isEmpty()) {
                try { fechaNac = LocalDate.parse(fechaNacStr.trim()); }
                catch (DateTimeParseException e) { throw new Exception("Formato de fecha invalido. Usa: yyyy-MM-dd"); }
            }

            List<Mascotas> todas = Mascotas.consultarTodosBD();
            boolean hayConflicto = false;
            for (Mascotas m : todas) {
                if (m.getNombre().equalsIgnoreCase(nombre.trim()) &&
                    m.getEspecie() != null &&
                    m.getEspecie().getNombre().equalsIgnoreCase(especie.getNombre())) {
                    hayConflicto = true;
                    break;
                }
            }
            String car = (caracteristica != null) ? caracteristica.trim() : "";
            if (hayConflicto) {
                if (car.isEmpty()) {
                    throw new NecesitaCaracteristicaException(
                            "Ya existe una mascota llamada \"" + nombre.trim() + "\" de especie " +
                            especie.getNombre() + ".\nIngresa una caracteristica que la distinga.");
                }
                boolean carDuplicada = false;
                for (Mascotas m : todas) {
                    if (m.getNombre().equalsIgnoreCase(nombre.trim()) &&
                        m.getEspecie() != null &&
                        m.getEspecie().getNombre().equalsIgnoreCase(especie.getNombre()) &&
                        car.equalsIgnoreCase(m.getCaracteristica() != null ? m.getCaracteristica().trim() : "")) {
                        carDuplicada = true;
                        break;
                    }
                }
                if (carDuplicada) {
                    throw new NecesitaCaracteristicaException(
                            "Ya existe una mascota con ese nombre, especie y esa misma caracteristica.\n" +
                            "Ingresa una caracteristica diferente para distinguirla.");
                }
            }

            Mascotas m = new Mascotas();
            m.setNombre(nombre.trim());
            m.setEspecie(especie);
            m.setCliente(cliente);
            m.setFechaNac(fechaNac);
            m.setSexo(sexo);
            m.setCaracteristica(car.isEmpty() ? null : car);
            m.insertarBD();
            JOptionPane.showMessageDialog(panel, "Mascota registrada exitosamente.");
            return true;
        } catch (NecesitaCaracteristicaException e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Campo requerido", JOptionPane.WARNING_MESSAGE);
            if (onNecesitaCaracteristica != null) onNecesitaCaracteristica.run();
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void actualizarMascota(Mascotas mascota, String nuevoNombre,
                                  Especies nuevaEspecie, LocalDate nuevaFecha,
                                  String nuevoSexo, JPanel panel) {
        try {
            if (nuevoNombre == null || nuevoNombre.trim().isEmpty())
                throw new Exception("El nombre no puede estar vacio.");
            mascota.setNombre(nuevoNombre.trim());
            mascota.setEspecie(nuevaEspecie);
            mascota.setFechaNac(nuevaFecha);
            mascota.setSexo(nuevoSexo);
            mascota.actualizarBD();
            JOptionPane.showMessageDialog(panel, "Mascota actualizada correctamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarMascota(Integer id, JPanel panel) {
        try {
            if (id == null) throw new Exception("ID invalido.");
            Mascotas m = new Mascotas();
            m.setId(id);
            m.eliminarBD();
            JOptionPane.showMessageDialog(panel, "Mascota eliminada exitosamente.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
