package org.example.service;

import org.example.model.Cliente;
import org.example.model.Empleados;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RecuperacionService {

    // Mapa temporal: correo → codigo (valido durante la sesion)
    private final Map<String, String>  codigosPendientes = new HashMap<>();
    private final Map<String, Boolean> esEmpleado        = new HashMap<>();

    public void enviarCodigoRecuperacion(String correo) throws Exception {
        if (correo == null || correo.trim().isEmpty())
            throw new Exception("Ingresa tu correo electronico.");

        String correoNorm = correo.trim().toLowerCase();
        String nombre;

        Cliente cliente = Cliente.buscarPorCorreoBD(correoNorm);
        if (cliente != null) {
            nombre = cliente.getNombre();
            esEmpleado.put(correoNorm, false);
        } else {
            Empleados empleado = Empleados.buscarPorCorreoBD(correo.trim());
            if (empleado == null)
                empleado = Empleados.buscarPorCorreoBD(correoNorm);
            if (empleado == null)
                throw new Exception("No existe ninguna cuenta con ese correo.");
            nombre = empleado.getNombre();
            esEmpleado.put(correoNorm, true);
        }

        String codigo = String.format("%06d", new Random().nextInt(999999));
        codigosPendientes.put(correoNorm, codigo);
        CorreoService.enviarCodigoRecuperacion(correo.trim(), nombre, codigo);
    }

    public boolean verificarCodigo(String correo, String codigoIngresado) {
        String esperado = codigosPendientes.get(correo.trim().toLowerCase());
        return esperado != null && esperado.equals(codigoIngresado.trim());
    }

    public void cambiarContrasena(String correo, String nueva, String confirmar) throws Exception {
        if (nueva == null || nueva.trim().isEmpty())
            throw new Exception("La nueva contrasena no puede estar vacia.");
        if (!nueva.equals(confirmar))
            throw new Exception("Las contrasenas no coinciden.");
        if (nueva.length() < 6)
            throw new Exception("La contrasena debe tener al menos 6 caracteres.");

        String correoNorm = correo.trim().toLowerCase();
        Boolean esEmp = esEmpleado.get(correoNorm);

        if (Boolean.TRUE.equals(esEmp)) {
            Empleados emp = Empleados.buscarPorCorreoBD(correo.trim());
            if (emp == null) emp = Empleados.buscarPorCorreoBD(correoNorm);
            if (emp == null) throw new Exception("Administrador no encontrado.");
            emp.setContrasena(nueva);
            emp.actualizarBD();
        } else {
            Cliente c = Cliente.buscarPorCorreoBD(correoNorm);
            if (c == null) throw new Exception("Cliente no encontrado.");
            c.setContrasena(nueva);
            c.actualizarBD();
        }

        codigosPendientes.remove(correoNorm);
        esEmpleado.remove(correoNorm);
    }
}
