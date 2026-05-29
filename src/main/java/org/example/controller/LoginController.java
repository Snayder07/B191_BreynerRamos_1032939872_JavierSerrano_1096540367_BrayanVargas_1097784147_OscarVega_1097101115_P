package org.example.controller;

import org.example.view.Main;
import org.example.model.Cliente;
import org.example.model.Empleados;

import javax.swing.*;

public class LoginController {

    public Cliente loginCliente(String correo, String contrasena, JPanel panel) {
        try {
            if (correo == null || correo.trim().isEmpty())
                throw new Exception("Ingresa tu correo.");
            if (contrasena == null || contrasena.trim().isEmpty())
                throw new Exception("Ingresa tu contrasena.");

            Cliente c = Cliente.buscarPorCorreoBD(correo.trim().toLowerCase());
            if (c == null)
                throw new Exception("El correo no esta registrado.");
            if (!c.getContrasena().equals(contrasena))
                throw new Exception("Contrasena incorrecta.");

            Main.clienteActual  = c;
            Main.empleadoActual = null;
            Main.cambiarPantalla("panelCliente");
            return c;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error de acceso", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void loginAdmin(String correo, String contrasena, JPanel panel) {
        try {
            if (correo == null || correo.trim().isEmpty())
                throw new Exception("Ingresa el correo de administrador.");
            if (contrasena == null || contrasena.trim().isEmpty())
                throw new Exception("Ingresa la contrasena.");

            Empleados emp = Empleados.buscarPorCorreoBD(correo.trim());
            if (emp == null)
                emp = Empleados.buscarPorCorreoBD(correo.trim().toLowerCase());
            if (emp == null)
                throw new Exception("No existe un administrador con ese correo.");
            if (!emp.getContrasena().trim().equals(contrasena.trim()))
                throw new Exception("Contrasena incorrecta.");

            Main.clienteActual  = null;
            Main.empleadoActual = emp;
            Main.cambiarPantalla("panelAdmin");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error de acceso", JOptionPane.ERROR_MESSAGE);
        }
    }
}
