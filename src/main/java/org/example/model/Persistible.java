package org.example.model;

public interface Persistible {
    boolean insertarBD();
    boolean actualizarBD();
    boolean eliminarBD();
}
