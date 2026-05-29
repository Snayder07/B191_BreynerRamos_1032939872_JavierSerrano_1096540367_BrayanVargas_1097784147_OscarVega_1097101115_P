package org.example.service;

import org.example.model.Cliente;
import java.util.List;

public class ClienteService {

    public void registrar(String nombre, String apellido, String correo,
                          String telefono, String contrasena,
                          String confirmar) throws Exception {

        if (nombre == null || nombre.trim().isEmpty())
            throw new Exception("El nombre es obligatorio.");
        if (correo == null || correo.trim().isEmpty())
            throw new Exception("El correo es obligatorio.");
        if (contrasena == null || contrasena.trim().isEmpty())
            throw new Exception("La contrasena es obligatoria.");
        if (contrasena.length() < 8)
            throw new Exception("La contrasena debe tener al menos 8 caracteres.");
        if (!contrasena.equals(confirmar))
            throw new Exception("Las contrasenas no coinciden.");
        if (buscarPorCorreo(correo) != null)
            throw new Exception("Ya existe una cuenta con ese correo.");

        Cliente cliente = new Cliente();
        cliente.setNombre((nombre.trim() + " " + apellido.trim()).trim());
        cliente.setCorreo(correo.trim().toLowerCase());
        cliente.setTelefono(telefono != null ? telefono.trim() : "");
        cliente.setDireccion("");
        cliente.setContrasena(contrasena);
        cliente.insertarBD();
    }

    public Cliente loginCliente(String correo, String contrasena) throws Exception {
        if (correo == null || correo.trim().isEmpty())
            throw new Exception("Ingresa tu correo.");
        if (contrasena == null || contrasena.trim().isEmpty())
            throw new Exception("Ingresa tu contrasena.");

        Cliente c = buscarPorCorreo(correo.trim().toLowerCase());
        if (c == null)
            throw new Exception("El correo no esta registrado.");
        if (!c.getContrasena().equals(contrasena))
            throw new Exception("Contrasena incorrecta.");
        return c;
    }

    public Cliente buscarPorCorreo(String correo) {
        return Cliente.buscarPorCorreoBD(correo);
    }

    public List<Cliente> listarTodos() {
        return Cliente.consultarTodosBD();
    }
}
