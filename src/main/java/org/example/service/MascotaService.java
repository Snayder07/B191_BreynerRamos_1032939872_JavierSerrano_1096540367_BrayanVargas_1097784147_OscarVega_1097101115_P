package org.example.service;

import org.example.model.Especies;
import org.example.model.Cliente;
import org.example.model.Mascotas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MascotaService {

    // ── Atributos propios ─────────────────────────────────────────────────
    private boolean crearEspecieAutomaticamente;  // si se crea la especie si no existe
    private int     maxLongitudNombre;            // longitud maxima del nombre de la mascota

    public MascotaService() {
        this.crearEspecieAutomaticamente = true;
        this.maxLongitudNombre           = 100;
    }

    public boolean isCrearEspecieAutomaticamente()                   { return crearEspecieAutomaticamente; }
    public void    setCrearEspecieAutomaticamente(boolean valor)     { this.crearEspecieAutomaticamente = valor; }

    public int  getMaxLongitudNombre()                               { return maxLongitudNombre; }
    public void setMaxLongitudNombre(int max)                        { this.maxLongitudNombre = max; }

    // ── Excepcion propia del servicio ─────────────────────────────────────
    public static class NecesitaCaracteristicaException extends Exception {
        public NecesitaCaracteristicaException(String msg) { super(msg); }
    }

    public void registrarMascota(String nombre, Especies especie, Cliente cliente,
                                 String fechaNacStr, String sexo, String caracteristica) throws Exception {
        if (nombre == null || nombre.trim().isEmpty())
            throw new Exception("El nombre de la mascota es obligatorio.");
        if (nombre.trim().length() > maxLongitudNombre)
            throw new Exception("El nombre no puede superar " + maxLongitudNombre + " caracteres.");
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
            for (Mascotas m : todas) {
                if (m.getNombre().equalsIgnoreCase(nombre.trim()) &&
                    m.getEspecie() != null &&
                    m.getEspecie().getNombre().equalsIgnoreCase(especie.getNombre()) &&
                    car.equalsIgnoreCase(m.getCaracteristica() != null ? m.getCaracteristica().trim() : "")) {
                    throw new NecesitaCaracteristicaException(
                            "Ya existe una mascota con ese nombre, especie y esa misma caracteristica.\n" +
                            "Ingresa una caracteristica diferente para distinguirla.");
                }
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
    }

    public void actualizarMascota(Mascotas mascota, String nuevoNombre,
                                  Especies nuevaEspecie, LocalDate nuevaFecha, String nuevoSexo) throws Exception {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty())
            throw new Exception("El nombre no puede estar vacio.");
        mascota.setNombre(nuevoNombre.trim());
        mascota.setEspecie(nuevaEspecie);
        mascota.setFechaNac(nuevaFecha);
        mascota.setSexo(nuevoSexo);
        mascota.actualizarBD();
    }

    public Especies obtenerOCrearEspecie(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) return null;
        Especies existente = Especies.buscarPorNombreBD(nombre.trim());
        if (existente != null) return existente;
        if (!crearEspecieAutomaticamente) return null;
        Especies nueva = new Especies();
        nueva.setNombre(nombre.trim());
        nueva.insertarBD();
        return Especies.buscarPorNombreBD(nombre.trim());
    }
}
