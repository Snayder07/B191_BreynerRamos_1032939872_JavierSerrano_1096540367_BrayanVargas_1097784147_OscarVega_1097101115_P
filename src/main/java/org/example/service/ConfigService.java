package org.example.service;

import java.io.*;
import java.util.Properties;

public class ConfigService {

    // ── Atributos propios ─────────────────────────────────────────────────
    private final File   archivoConfig;   // ruta del archivo donde se guardan las credenciales
    private final String nombreAplicacion; // nombre que aparece en el archivo de configuracion

    public ConfigService() {
        this.archivoConfig    = new File(System.getProperty("user.home"), ".kampets/config.properties");
        this.nombreAplicacion = "Kampets Veterinaria";
    }

    public File   getArchivoConfig()    { return archivoConfig; }
    public String getNombreAplicacion() { return nombreAplicacion; }

    // ── Acceso estatico (usa la instancia por defecto) ────────────────────
    private static final ConfigService INSTANCIA = new ConfigService();

    private static File CONFIG_FILE = INSTANCIA.archivoConfig;

    // ── Lectura ──────────────────────────────────────────────────────────

    public static String get(String clave, String valorPorDefecto) {
        return cargar().getProperty(clave, valorPorDefecto);
    }

    /** Correo que aparece como remitente en los mensajes (FROM) */
    public static String getEmailRemitente() {
        return get("email.remitente", "");
    }

    /** Login SMTP (puede ser igual al remitente o diferente segun el proveedor) */
    public static String getSmtpLogin() {
        String login = get("smtp.login", "");
        // Compatibilidad: si no hay smtp.login, usar email.remitente
        return login.isEmpty() ? getEmailRemitente() : login;
    }

    public static String getEmailPassword() {
        return get("email.password", "");
    }

    /** Devuelve true si ya hay credenciales reales configuradas */
    public static boolean isCorreoConfigurado() {
        String email = getEmailRemitente();
        String pass  = getEmailPassword();
        return !email.isEmpty() && !pass.isEmpty()
                && !pass.contains("xxxx");
    }

    // ── Escritura ─────────────────────────────────────────────────────────

    public static void setEmailCredenciales(String emailRemitente, String smtpLogin,
                                             String password, String host, String port) {
        Properties props = cargar();
        props.setProperty("email.remitente", emailRemitente.trim());
        props.setProperty("smtp.login",      smtpLogin.trim());
        props.setProperty("email.password",  password.trim());
        props.setProperty("smtp.host",       host.trim());
        props.setProperty("smtp.port",       port.trim());
        guardar(props);
    }

    /** Sobrecarga para compatibilidad cuando login == remitente */
    public static void setEmailCredenciales(String email, String password, String host, String port) {
        setEmailCredenciales(email, email, password, host, port);
    }

    // ── Internos ──────────────────────────────────────────────────────────

    private static Properties cargar() {
        Properties props = new Properties();
        if (CONFIG_FILE.exists()) {
            try (InputStream is = new FileInputStream(CONFIG_FILE)) {
                props.load(is);
            } catch (IOException ignored) {}
        }
        return props;
    }

    private static void guardar(Properties props) {
        try {
            CONFIG_FILE.getParentFile().mkdirs();
            try (OutputStream os = new FileOutputStream(CONFIG_FILE)) {
                props.store(os, "Kampets - Configuracion interna");
            }
        } catch (IOException ignored) {}
    }
}
