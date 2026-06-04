package org.example.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

/**
 * Servicio para enviar correos electronicos.
 * Soporta Brevo SMTP y Gmail SMTP.
 * Credenciales configuradas desde la app: Sidebar Admin -> Configurar correo
 */
public class CorreoService {

    // ── Atributos propios ─────────────────────────────────────────────────
    private String nombreEmisor;  // nombre que aparece en el campo "De:" del correo
    private int    timeoutMs;     // tiempo maximo de espera para conectar al servidor SMTP

    public CorreoService() {
        this.nombreEmisor = "Kampets Veterinaria";
        this.timeoutMs    = 15000;
    }

    public String getNombreEmisor()            { return nombreEmisor; }
    public void   setNombreEmisor(String name) { this.nombreEmisor = name; }

    public int  getTimeoutMs()           { return timeoutMs; }
    public void setTimeoutMs(int timeout){ this.timeoutMs = timeout; }

    // ── Metodos estaticos (usan los atributos de la instancia por defecto) ─
    private static final CorreoService INSTANCIA = new CorreoService();

    /** Correo que aparece como FROM en el mensaje */
    private static String getRemitente() { return ConfigService.getEmailRemitente(); }

    /** Login de autenticacion SMTP (puede diferir del remitente en Brevo) */
    private static String getSmtpLogin()  { return ConfigService.getSmtpLogin();      }

    /** Clave SMTP / contrasena de aplicacion */
    private static String getPassword()   { return ConfigService.getEmailPassword();  }

    /**
     * Envia el codigo de recuperacion al correo del cliente.
     */
    public static void enviarCodigoRecuperacion(String destinatario,
                                                String nombreCliente,
                                                String codigo) throws Exception {
        String cuerpo = construirCuerpoRecuperacion(nombreCliente, codigo);
        enviar(destinatario, "Codigo de recuperacion - Kampets", cuerpo);
    }

    /**
     * Envia un correo general con asunto y cuerpo HTML personalizados.
     */
    public static void enviarCorreoGeneral(String destinatario,
                                           String nombreCliente,
                                           String asunto,
                                           String cuerpoHtml) throws Exception {
        enviar(destinatario, asunto, cuerpoHtml);
    }

    // ── Metodo interno de envio ───────────────────────────
    private static void enviar(String destinatario, String asunto, String cuerpoHtml) throws Exception {
        if (!ConfigService.isCorreoConfigurado()) {
            throw new Exception(
                    "El correo no esta configurado.\n" +
                            "Ve a: Sidebar Admin -> Configurar correo\n" +
                            "e ingresa tus credenciales SMTP."
            );
        }

        final String remitente  = getRemitente();   // FROM visible
        final String smtpLogin  = getSmtpLogin();   // login de autenticacion
        final String password   = getPassword();    // clave SMTP

        String host = ConfigService.get("smtp.host", "smtp-relay.brevo.com");
        String port = ConfigService.get("smtp.port", "587");

        Properties props = new Properties();
        props.put("mail.smtp.auth",               "true");
        props.put("mail.smtp.starttls.enable",    "true");
        props.put("mail.smtp.starttls.required",  "true");
        props.put("mail.smtp.host",               host);
        props.put("mail.smtp.port",               port);
        props.put("mail.smtp.ssl.trust",          host);
        props.put("mail.smtp.ssl.protocols",      "TLSv1.2");
        props.put("mail.smtp.connectiontimeout",  String.valueOf(INSTANCIA.timeoutMs));
        props.put("mail.smtp.timeout",            String.valueOf(INSTANCIA.timeoutMs));
        props.put("mail.smtp.writetimeout",       String.valueOf(INSTANCIA.timeoutMs));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpLogin, password);
            }
        });
        session.setDebug(true); // Muestra el log SMTP en consola de IntelliJ

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente, INSTANCIA.nombreEmisor, "UTF-8"));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setContent(cuerpoHtml, "text/html; charset=UTF-8");
            Transport.send(mensaje);
        } catch (AuthenticationFailedException ex) {
            String host2 = ConfigService.get("smtp.host", "smtp-relay.brevo.com");
            boolean esBrevo = host2.contains("brevo");
            if (esBrevo) {
                throw new Exception(
                        "Credenciales de Brevo incorrectas.\n\n" +
                                "Verifica en brevo.com:\n" +
                                "  1. SMTP & API -> Generar una clave SMTP\n" +
                                "  2. Copia esa clave (empieza con letras y numeros)\n" +
                                "  3. NO uses tu contrasena de cuenta, usa la CLAVE SMTP\n" +
                                "  4. El login SMTP es tu email de cuenta Brevo\n\n" +
                                "Ve a: Sidebar Admin -> Configurar correo"
                );
            } else {
                throw new Exception(
                        "Credenciales de Gmail incorrectas.\n\n" +
                                "Verifica que:\n" +
                                "  1. Activaste verificacion en 2 pasos\n" +
                                "  2. Generaste una Contrasena de Aplicacion\n" +
                                "  3. Usas esa clave de 16 letras (no tu clave normal)\n\n" +
                                "Ve a: Sidebar Admin -> Configurar correo"
                );
            }
        } catch (MessagingException ex) {
            throw new Exception("Error al enviar correo: " + ex.getMessage());
        }
    }

    /** Plantilla HTML del correo de recuperacion de contrasena */
    private static String construirCuerpoRecuperacion(String nombre, String codigo) {
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;" +
                "background:#f0f8f4;margin:0;padding:20px'>" +
                "<div style='max-width:480px;margin:auto;background:#fff;" +
                "border-radius:12px;padding:32px;box-shadow:0 2px 8px rgba(0,0,0,0.08)'>" +
                "<h2 style='color:#1d9e75;margin-top:0'>Kampets Veterinaria</h2>" +
                "<p style='color:#444'>Hola <strong>" + nombre + "</strong>,</p>" +
                "<p style='color:#444'>Recibimos una solicitud para recuperar tu contrasena. " +
                "Usa el siguiente codigo de verificacion:</p>" +
                "<div style='text-align:center;margin:28px 0'>" +
                "<span style='font-size:36px;font-weight:bold;letter-spacing:10px;" +
                "color:#1d9e75;background:#e8f7f1;padding:16px 28px;border-radius:8px'>" +
                codigo + "</span></div>" +
                "<p style='color:#777;font-size:13px'>Este codigo es valido solo para " +
                "esta sesion. Si no solicitaste este cambio, ignora este correo.</p>" +
                "<hr style='border:none;border-top:1px solid #e0e0e0;margin:20px 0'/>" +
                "<p style='color:#aaa;font-size:11px;text-align:center'>" +
                "Kampets Veterinaria</p></div></body></html>";
    }
}
