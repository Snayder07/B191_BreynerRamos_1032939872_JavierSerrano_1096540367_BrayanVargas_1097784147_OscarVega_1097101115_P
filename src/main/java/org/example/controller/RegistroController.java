package org.example.controller;

import org.example.view.Main;
import org.example.service.ClienteService;

import javax.swing.*;

public class RegistroController {

    private final ClienteService clienteService = new ClienteService();

    public void registrar(String nombre, String apellido, String correo,
                          String telefono, String contrasena,
                          String confirmar, JPanel panel) {
        try {
            clienteService.registrar(nombre, apellido, correo, telefono, contrasena, confirmar);
            JOptionPane.showMessageDialog(panel,
                    "Cuenta creada exitosamente. Ya puedes iniciar sesion.",
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            Main.cambiarPantalla("login");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(panel, e.getMessage(), "Error al registrar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
