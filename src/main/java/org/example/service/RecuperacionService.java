package org.example.service;

import org.example.model.Cliente;
import org.example.model.Empleados;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RecuperacionService {

    // ── Atributos propios ─────────────────────────────────────────────────
    private final Map<String, String>  codigosPendientes;  // correo → codigo enviado
    private final Map<String, Boolean> esEmpleado;         // correo → true si es empleado
    private int longitudCodigo;                            // cantidad de digitos del codigo
    private int minimoCaracteresContrasena;                // minimo de caracteres para la nueva contrasena

    public RecuperacionService() {
        this.codigosPendientes         = new HashMap<>();
        this.esEmpleado                = new HashMap<>();
        this.longitudCodigo            = 6;
        this.minimoCaracteresContrasena = 6;
    }

    public int getLongitudCodigo()                        { return longitudCodigo; }
    public void setLongitudCodigo(int longitud)           { this.longitudCodigo = longitud; }

    public int getMinimoCaracteresContrasena()            { return minimoCaracteresContrasena; }
    public void setMinimoCaracteresContrasena(int minimo) { this.minimoCaracteresContrasena = minimo; }

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

        int maxValor = (int) Math.pow(10, longitudCodigo) - 1;
        String formato = "%0" + longitudCodigo + "d";
        String codigo = String.format(formato, new Random().nextInt(maxValor));
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
        if (nueva.length() < minimoCaracteresContrasena)
            throw new Exception("La contrasena debe tener al menos " + minimoCaracteresContrasena + " caracteres.");

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
