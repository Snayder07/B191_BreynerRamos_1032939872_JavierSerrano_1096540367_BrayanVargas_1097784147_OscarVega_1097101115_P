package org.example.util;

import javax.swing.JOptionPane;
import java.sql.*;

public class ConexionBD {

    private Connection connection;
    private Statement  stmtConsulta;   // solo para mantener vivo el ResultSet activo

    private static final String URL  = "jdbc:postgresql://aws-1-us-east-2.pooler.supabase.com:6543/postgres?sslmode=require&prepareThreshold=0";
    private static final String USER = "postgres.swsxsodiwaoeqtkiqfox";
    private static final String PASS = "BVOVBRDS071207";

    public ConexionBD() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("[BD] Conexion establecida correctamente.");
        } catch (Exception e) {
            System.out.println("[BD] ERROR al conectar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error de conexion: " + e.getMessage());
        }
    }

    public Connection getConnection() { return connection; }

    public boolean insertarBD(String sql) {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            System.out.println("[BD] Registro insertado correctamente.");
            return true;
        } catch (Exception e) {
            System.out.println("[BD] ERROR al insertar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al insertar: " + e.getMessage());
            return false;
        }
    }

    public ResultSet consultarBD(String sql) {
        try {
            if (stmtConsulta != null && !stmtConsulta.isClosed()) stmtConsulta.close();
            stmtConsulta = connection.createStatement();
            ResultSet rs = stmtConsulta.executeQuery(sql);
            System.out.println("[BD] Consulta ejecutada correctamente.");
            return rs;
        } catch (Exception e) {
            System.out.println("[BD] ERROR al consultar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al consultar: " + e.getMessage());
            return null;
        }
    }

    public boolean actualizarBD(String sql) {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("[BD] Registro actualizado correctamente.");
            return true;
        } catch (Exception e) {
            System.out.println("[BD] ERROR al actualizar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    public boolean borrarBD(String sql) {
        try (Statement st = connection.createStatement()) {
            st.execute(sql);
            System.out.println("[BD] Registro eliminado correctamente.");
            return true;
        } catch (Exception e) {
            System.out.println("[BD] ERROR al eliminar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al eliminar: " + e.getMessage());
            return false;
        }
    }

    public boolean setAutoCommitBD(boolean val) {
        try {
            connection.setAutoCommit(val);
            System.out.println("[BD] AutoCommit configurado en: " + val);
            return true;
        } catch (Exception e) {
            System.out.println("[BD] ERROR autocommit: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error autocommit: " + e.getMessage());
            return false;
        }
    }

    public boolean commitBD() {
        try {
            connection.commit();
            System.out.println("[BD] Commit realizado.");
            return true;
        } catch (Exception e) {
            System.out.println("[BD] ERROR commit: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error commit: " + e.getMessage());
            return false;
        }
    }

    public boolean rollbackBD() {
        try {
            connection.rollback();
            System.out.println("[BD] Rollback realizado.");
            return true;
        } catch (Exception e) {
            System.out.println("[BD] ERROR rollback: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error rollback: " + e.getMessage());
            return false;
        }
    }

    public void cerrarConexion() {
        try {
            if (stmtConsulta != null && !stmtConsulta.isClosed()) stmtConsulta.close();
            if (connection   != null && !connection.isClosed())   connection.close();
            System.out.println("[BD] Conexion cerrada.");
        } catch (Exception e) {
            System.out.println("[BD] ERROR al cerrar: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error al cerrar: " + e.getMessage());
        }
    }
}
