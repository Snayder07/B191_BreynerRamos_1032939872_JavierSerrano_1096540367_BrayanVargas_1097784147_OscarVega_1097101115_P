package org.example.view;

import org.example.service.ConfigService;
import org.example.service.CorreoService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ConfigurarCorreoDialog extends JDialog {

    private static final Color VERDE    = new Color(22, 101, 52);
    private static final Color VERDE_LT = new Color(240, 253, 244);
    private static final Color AMARILLO = new Color(254, 243, 199);
    private static final Color AMARILLO_B = new Color(217, 119, 6);
    private static final Color GRIS     = new Color(100, 116, 139);
    private static final Color BORDE    = new Color(187, 224, 200);

    // Proveedor seleccionado: "brevo" o "gmail"
    private String proveedorActual = "brevo";

    public ConfigurarCorreoDialog(Window owner) {
        super(owner, "Configurar correo", ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construir();
        pack();
        setLocationRelativeTo(owner);
    }

    private void construir() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(VERDE_LT);
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // ── Titulo ────────────────────────────────────────
        JLabel titulo = new JLabel("Configurar correo de Kampets");
        titulo.setFont(new Font("Arial", Font.BOLD, 17));
        titulo.setForeground(VERDE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        root.add(titulo);
        root.add(Box.createVerticalStrut(4));

        JLabel sub = new JLabel("<html><font color='#64748b'>Elige como quieres enviar los correos de recuperacion.</font></html>");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setAlignmentX(LEFT_ALIGNMENT);
        root.add(sub);
        root.add(Box.createVerticalStrut(16));

        // ── Selector de proveedor ─────────────────────────
        JPanel tabs = new JPanel(new GridLayout(1, 2, 6, 0));
        tabs.setBackground(VERDE_LT);
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tabs.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnBrevo = tabBtn("Brevo (recomendado)", true);
        JButton btnGmail = tabBtn("Gmail", false);
        tabs.add(btnBrevo);
        tabs.add(btnGmail);
        root.add(tabs);
        root.add(Box.createVerticalStrut(14));

        // ── Aviso importante (CLAVE SMTP) ─────────────────
        JPanel avisoPanel = panelAviso(
            "<html><b>IMPORTANTE:</b> En el campo contrasena debes poner la<br>" +
            "<b>CLAVE SMTP</b>, NO tu contrasena de cuenta.<br>" +
            "En Brevo: SMTP &amp; API &rarr; Generar clave SMTP<br>" +
            "En Gmail: Contrasenas de aplicacion &rarr; clave de 16 letras</html>"
        );
        avisoPanel.setAlignmentX(LEFT_ALIGNMENT);
        root.add(avisoPanel);
        root.add(Box.createVerticalStrut(10));

        // ── Panel de instrucciones (cambia segun proveedor) ──
        JPanel[] instrPanel = { panelInstrBrevo(), panelInstrGmail() };
        JPanel instrWrapper = new JPanel(new CardLayout());
        instrWrapper.setBackground(VERDE_LT);
        instrWrapper.setAlignmentX(LEFT_ALIGNMENT);
        instrWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        instrWrapper.add(instrPanel[0], "brevo");
        instrWrapper.add(instrPanel[1], "gmail");
        root.add(instrWrapper);
        root.add(Box.createVerticalStrut(14));

        // ── Campo: Login SMTP (solo visible en Brevo) ─────
        JLabel lblLogin = label("Tu email de cuenta Brevo (login SMTP)");
        JTextField tfLogin = campo(ConfigService.get("smtp.login", ConfigService.getEmailRemitente()));
        tfLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JPanel loginWrapper = new JPanel();
        loginWrapper.setLayout(new BoxLayout(loginWrapper, BoxLayout.Y_AXIS));
        loginWrapper.setBackground(VERDE_LT);
        loginWrapper.setAlignmentX(LEFT_ALIGNMENT);
        loginWrapper.add(lblLogin);
        loginWrapper.add(Box.createVerticalStrut(4));
        loginWrapper.add(tfLogin);
        loginWrapper.add(Box.createVerticalStrut(12));
        root.add(loginWrapper);

        // ── Campo: Remitente FROM ─────────────────────────
        root.add(label("Correo remitente (aparece como FROM en los mensajes)"));
        root.add(Box.createVerticalStrut(4));
        JTextField tfEmail = campo(ConfigService.getEmailRemitente());
        tfEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        root.add(tfEmail);
        root.add(Box.createVerticalStrut(12));

        // ── Campo: Clave SMTP ─────────────────────────────
        JLabel lblPass = label("CLAVE SMTP (no es tu contrasena de cuenta)");
        lblPass.setForeground(new Color(180, 50, 0));
        root.add(lblPass);
        root.add(Box.createVerticalStrut(4));

        JPanel passPanel = new JPanel(new BorderLayout(0, 0));
        passPanel.setBackground(VERDE_LT);
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passPanel.setAlignmentX(LEFT_ALIGNMENT);
        JPasswordField tfPass = new JPasswordField(ConfigService.getEmailPassword());
        tfPass.setFont(new Font("Arial", Font.PLAIN, 13));
        tfPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        JButton btnOjo = new JButton("ver");
        btnOjo.setFont(new Font("Arial", Font.PLAIN, 11));
        btnOjo.setPreferredSize(new Dimension(44, 36));
        btnOjo.setBackground(Color.WHITE);
        btnOjo.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        btnOjo.setFocusPainted(false);
        btnOjo.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnOjo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfPass.setEchoChar(tfPass.getEchoChar() == 0 ? '*' : (char) 0);
            }
        });
        passPanel.add(tfPass, BorderLayout.CENTER);
        passPanel.add(btnOjo, BorderLayout.EAST);
        root.add(passPanel);
        root.add(Box.createVerticalStrut(20));

        // ── Botones ───────────────────────────────────────
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(VERDE_LT);
        botones.setAlignmentX(LEFT_ALIGNMENT);
        botones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnProbar  = btnSecundario("Probar conexion");
        JButton btnGuardar = btnPrincipal("Guardar");
        botones.add(btnProbar);
        botones.add(btnGuardar);
        root.add(botones);

        // ── Logica tabs ───────────────────────────────────
        CardLayout cl = (CardLayout) instrWrapper.getLayout();
        btnBrevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proveedorActual = "brevo";
                btnBrevo.setBackground(VERDE); btnBrevo.setForeground(Color.WHITE);
                btnGmail.setBackground(Color.WHITE); btnGmail.setForeground(VERDE);
                cl.show(instrWrapper, "brevo");
                loginWrapper.setVisible(true);
                lblLogin.setText("Tu email de cuenta Brevo (login SMTP)");
                pack();
            }
        });
        btnGmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                proveedorActual = "gmail";
                btnGmail.setBackground(VERDE); btnGmail.setForeground(Color.WHITE);
                btnBrevo.setBackground(Color.WHITE); btnBrevo.setForeground(VERDE);
                cl.show(instrWrapper, "gmail");
                loginWrapper.setVisible(false);
                pack();
            }
        });

        // ── Detectar proveedor guardado ───────────────────
        String hostGuardado = ConfigService.get("smtp.host", "brevo");
        if (hostGuardado.contains("gmail")) {
            btnGmail.doClick();
        }

        // ── Logica guardar ────────────────────────────────
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = tfLogin.getText().trim();
                String email = tfEmail.getText().trim();
                String pass  = new String(tfPass.getPassword()).trim();

                if (email.isEmpty() || !email.contains("@")) { error("Ingresa un correo remitente valido."); return; }
                if (pass.isEmpty()) { error("Ingresa la CLAVE SMTP (no tu contrasena de cuenta)."); return; }

                String host, smtpLogin;
                if (proveedorActual.equals("brevo")) {
                    host = "smtp-relay.brevo.com";
                    smtpLogin = login.isEmpty() ? email : login;
                    if (!smtpLogin.contains("@")) { error("Ingresa el email de tu cuenta Brevo como login SMTP."); return; }
                } else {
                    host = "smtp.gmail.com";
                    smtpLogin = email;
                }

                ConfigService.setEmailCredenciales(email, smtpLogin, pass, host, "587");
                JOptionPane.showMessageDialog(ConfigurarCorreoDialog.this,
                    "Configuracion guardada.\nAhora puedes usar la recuperacion de contrasena.",
                    "Listo", JOptionPane.INFORMATION_MESSAGE);
                ConfigurarCorreoDialog.this.dispose();
            }
        });

        // ── Logica probar ─────────────────────────────────
        btnProbar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String login = tfLogin.getText().trim();
                String email = tfEmail.getText().trim();
                String pass  = new String(tfPass.getPassword()).trim();

                if (email.isEmpty() || pass.isEmpty()) { error("Completa los campos primero."); return; }

                String host, smtpLogin;
                if (proveedorActual.equals("brevo")) {
                    host = "smtp-relay.brevo.com";
                    smtpLogin = login.isEmpty() ? email : login;
                } else {
                    host = "smtp.gmail.com";
                    smtpLogin = email;
                }

                ConfigService.setEmailCredenciales(email, smtpLogin, pass, host, "587");

                btnProbar.setEnabled(false); btnProbar.setText("Probando...");
                final String emailFinal = email;
                final String smtpLoginFinal = smtpLogin;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CorreoService.enviarCorreoGeneral(emailFinal, "Admin",
                                "Prueba de conexion - Kampets",
                                "<h2 style='color:#16653c'>Kampets Veterinaria</h2>" +
                                "<p>La configuracion de correo funciona correctamente.</p>" +
                                "<p>Si recibes este mensaje, el sistema esta listo.</p>");
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnProbar.setEnabled(true); btnProbar.setText("Probar conexion");
                                    JOptionPane.showMessageDialog(ConfigurarCorreoDialog.this,
                                        "Correo enviado correctamente.\n" +
                                        "Revisa tu bandeja de entrada (y la carpeta de spam).",
                                        "Conexion exitosa", JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnProbar.setEnabled(true); btnProbar.setText("Probar conexion");
                                    error(ex.getMessage());
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        setContentPane(root);
    }

    // ── Panel de aviso amarillo ───────────────────────────
    private JPanel panelAviso(String html) {
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(120, 53, 15));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AMARILLO);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(253, 211, 77), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        p.add(lbl);
        return p;
    }

    // ── Paneles de instrucciones ──────────────────────────

    private JPanel panelInstrBrevo() {
        return instrPanel(
            "<html><b>Pasos para Brevo (gratis, 300 correos/dia):</b><br>" +
            "1. Entra a <u>brevo.com</u> → menu izquierdo → <b>Remitentes</b><br>" +
            "2. Agrega y <b>verifica</b> tu email (llega un correo de confirmacion)<br>" +
            "3. Ve a <b>SMTP &amp; API</b> → <b>Generar nueva clave SMTP</b> y copiala<br>" +
            "4. Pon ese email abajo y la clave SMTP como contrasena</html>"
        );
    }

    private JPanel panelInstrGmail() {
        return instrPanel(
            "<html><b>Pasos para Gmail:</b><br>" +
            "1. Activa verificacion en 2 pasos en <u>myaccount.google.com</u><br>" +
            "2. Busca <b>Contrasenas de aplicacion</b> y genera una<br>" +
            "3. Copia la clave de 16 letras que te da Google</html>"
        );
    }

    private JPanel instrPanel(String html) {
        JLabel lbl = new JLabel(html);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        lbl.setForeground(new Color(55, 65, 81));
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        return p;
    }

    // ── Helpers UI ────────────────────────────────────────

    private JButton tabBtn(String texto, boolean activo) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBackground(activo ? VERDE : Color.WHITE);
        b.setForeground(activo ? Color.WHITE : VERDE);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(VERDE, 1));
        b.setFocusPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton btnPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(VERDE); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton btnSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setBackground(Color.WHITE); b.setForeground(VERDE);
        b.setOpaque(true); b.setBorder(BorderFactory.createLineBorder(VERDE, 1));
        b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(GRIS);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField campo(String valor) {
        JTextField tf = new JTextField(valor);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        return tf;
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
