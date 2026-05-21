package org.example.service;

import org.example.model.Empleados;
import org.example.repository.EmpleadoRepositoryImpl;

import java.util.List;

public class EmpleadoService {

    private final EmpleadoRepositoryImpl repositorio = new EmpleadoRepositoryImpl();

    // ── LOGIN ADMINISTRADOR ───────────────────────────────
    public boolean loginAdmin(String correo, String contrasena) throws Exception {
        if (correo == null || correo.trim().isEmpty())
            throw new Exception("Ingresa el correo de administrador.");
        if (contrasena == null || contrasena.trim().isEmpty())
            throw new Exception("Ingresa la contraseña.");

        Empleados empleado = repositorio.buscarPorCorreo(correo.trim());
        if (empleado == null)
            throw new Exception("No existe un administrador con ese correo.");
        if (!empleado.getContrasena().trim().equals(contrasena.trim()))
            throw new Exception("Contraseña incorrecta.");

        return true;
    }

    // ── BUSCAR POR CORREO ─────────────────────────────────
    public Empleados buscarPorCorreo(String correo) {
        return repositorio.buscarPorCorreo(correo);
    }

    // ── REGISTRAR ADMIN — solo desde panel admin ──────────
    public void registrarAdmin(String nombre, String apellido, String correo,
                               String contrasena, String cargo) throws Exception {
        if (nombre == null || nombre.trim().isEmpty())
            throw new Exception("El nombre es obligatorio.");
        if (correo == null || correo.trim().isEmpty())
            throw new Exception("El correo es obligatorio.");
        if (contrasena == null || contrasena.trim().isEmpty())
            throw new Exception("La contraseña es obligatoria.");

        Empleados existente = repositorio.buscarPorCorreo(correo.trim());
        if (existente != null)
            throw new Exception("Ya existe un administrador con ese correo.");

        Empleados nuevo = new Empleados();
        nuevo.setNombre(nombre.trim());
        nuevo.setApellido(apellido != null ? apellido.trim() : "");
        nuevo.setCorreo(correo.trim().toLowerCase());
        nuevo.setContrasena(contrasena.trim());
        nuevo.setCargo(cargo != null && !cargo.trim().isEmpty() ? cargo.trim() : "Administrador");
        repositorio.guardar(nuevo);
    }

    // ── LISTAR TODOS ──────────────────────────────────────
    public List<Empleados> listarTodos() {
        return repositorio.buscarTodos();
    }
}
