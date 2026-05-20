package org.example.view;

import org.example.controller.RegistroController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CrearCuenta {
    public JPanel panel;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JTextField correoField;
    private JTextField telefonoField;
    private JPasswordField passwordField;
    private JPasswordField confirmarPasswordField;
    private JButton registrarButton;
    private JButton volverButton;

    // ── Conectado al RegistroController ──────────────────
    private final RegistroController registroController = new RegistroController();

    public CrearCuenta() {

        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 244));
        panel.setPreferredSize(new Dimension(420, 560));

        // Título
        JLabel titulo = new JLabel("Crear cuenta");
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
        titulo.setForeground(new Color(29, 158, 117));
        titulo.setBounds(40, 20, 340, 40);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo);

        JLabel subtitulo = new JLabel("Completa tus datos para registrarte");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(150, 150, 150));
        subtitulo.setBounds(40, 58, 340, 20);
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subtitulo);

        JSeparator sep = new JSeparator();
        sep.setBounds(40, 85, 340, 2);
        sep.setForeground(new Color(29, 158, 117));
        panel.add(sep);

        // Nombre
        JLabel labelNombre = new JLabel("Nombre");
        labelNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        labelNombre.setForeground(new Color(80, 80, 80));
        labelNombre.setBounds(40, 100, 160, 20);
        panel.add(labelNombre);

        nombreField = new JTextField();
        nombreField.setFont(new Font("Arial", Font.PLAIN, 13));
        nombreField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        nombreField.setBounds(40, 122, 160, 35);
        panel.add(nombreField);

        // Apellido
        JLabel labelApellido = new JLabel("Apellido");
        labelApellido.setFont(new Font("Arial", Font.PLAIN, 12));
        labelApellido.setForeground(new Color(80, 80, 80));
        labelApellido.setBounds(220, 100, 160, 20);
        panel.add(labelApellido);

        apellidoField = new JTextField();
        apellidoField.setFont(new Font("Arial", Font.PLAIN, 13));
        apellidoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        apellidoField.setBounds(220, 122, 160, 35);
        panel.add(apellidoField);

        // Correo
        JLabel labelCorreo = new JLabel("Correo electrónico");
        labelCorreo.setFont(new Font("Arial", Font.PLAIN, 12));
        labelCorreo.setForeground(new Color(80, 80, 80));
        labelCorreo.setBounds(40, 170, 200, 20);
        panel.add(labelCorreo);

        correoField = new JTextField();
        correoField.setFont(new Font("Arial", Font.PLAIN, 13));
        correoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        correoField.setBounds(40, 192, 340, 35);
        panel.add(correoField);

        // Teléfono
        JLabel labelTelefono = new JLabel("Teléfono");
        labelTelefono.setFont(new Font("Arial", Font.PLAIN, 12));
        labelTelefono.setForeground(new Color(80, 80, 80));
        labelTelefono.setBounds(40, 240, 200, 20);
        panel.add(labelTelefono);

        telefonoField = new JTextField();
        telefonoField.setFont(new Font("Arial", Font.PLAIN, 13));
        telefonoField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        telefonoField.setBounds(40, 262, 340, 35);
        panel.add(telefonoField);

        // Contraseña
        JLabel labelPass = new JLabel("Contraseña");
        labelPass.setFont(new Font("Arial", Font.PLAIN, 12));
        labelPass.setForeground(new Color(80, 80, 80));
        labelPass.setBounds(40, 310, 160, 20);
        panel.add(labelPass);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        passwordField.setOpaque(false);

        JButton btnOjoPass = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean vis = Boolean.TRUE.equals(getClientProperty("visible"));
                g2.setColor(vis ? new Color(29,158,117) : new Color(130,130,130));
                int cx=getWidth()/2, cy=getHeight()/2;
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(cx-7, cy-4, 14, 9);
                g2.fillOval(cx-2, cy-2, 5, 5);
                if (!vis) {
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx-8, cy+5, cx+8, cy-5);
                }
                g2.dispose();
            }
        };
        btnOjoPass.putClientProperty("visible", Boolean.FALSE);
        btnOjoPass.setText("");
        btnOjoPass.setOpaque(false); btnOjoPass.setContentAreaFilled(false); btnOjoPass.setBorderPainted(false); btnOjoPass.setFocusPainted(false);
        btnOjoPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOjoPass.addActionListener(e -> {
            boolean vis = Boolean.TRUE.equals(btnOjoPass.getClientProperty("visible"));
            if (!vis) { passwordField.setEchoChar((char)0); btnOjoPass.putClientProperty("visible", Boolean.TRUE); }
            else { passwordField.setEchoChar('\u2022'); btnOjoPass.putClientProperty("visible", Boolean.FALSE); }
            btnOjoPass.repaint();
        });

        JPanel wrapPass = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),4,4);
                g2.setColor(new Color(180,220,200)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,4,4);
                g2.dispose();
            }
        };
        wrapPass.setOpaque(false);
        wrapPass.add(passwordField, BorderLayout.CENTER);
        wrapPass.add(btnOjoPass, BorderLayout.EAST);
        btnOjoPass.setPreferredSize(new Dimension(32, 35));
        wrapPass.setBounds(40, 332, 160, 35);
        panel.add(wrapPass);

        // Confirmar contraseña
        JLabel labelConfirmar = new JLabel("Confirmar contraseña");
        labelConfirmar.setFont(new Font("Arial", Font.PLAIN, 12));
        labelConfirmar.setForeground(new Color(80, 80, 80));
        labelConfirmar.setBounds(220, 310, 160, 20);
        panel.add(labelConfirmar);

        confirmarPasswordField = new JPasswordField();
        confirmarPasswordField.setFont(new Font("Arial", Font.PLAIN, 13));
        confirmarPasswordField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        confirmarPasswordField.setOpaque(false);

        JButton btnOjoConfirmar = new JButton() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean vis = Boolean.TRUE.equals(getClientProperty("visible"));
                g2.setColor(vis ? new Color(29,158,117) : new Color(130,130,130));
                int cx=getWidth()/2, cy=getHeight()/2;
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawOval(cx-7, cy-4, 14, 9);
                g2.fillOval(cx-2, cy-2, 5, 5);
                if (!vis) {
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx-8, cy+5, cx+8, cy-5);
                }
                g2.dispose();
            }
        };
        btnOjoConfirmar.putClientProperty("visible", Boolean.FALSE);
        btnOjoConfirmar.setText("");
        btnOjoConfirmar.setOpaque(false); btnOjoConfirmar.setContentAreaFilled(false); btnOjoConfirmar.setBorderPainted(false); btnOjoConfirmar.setFocusPainted(false);
        btnOjoConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOjoConfirmar.addActionListener(e -> {
            boolean vis = Boolean.TRUE.equals(btnOjoConfirmar.getClientProperty("visible"));
            if (!vis) { confirmarPasswordField.setEchoChar((char)0); btnOjoConfirmar.putClientProperty("visible", Boolean.TRUE); }
            else { confirmarPasswordField.setEchoChar('\u2022'); btnOjoConfirmar.putClientProperty("visible", Boolean.FALSE); }
            btnOjoConfirmar.repaint();
        });

        JPanel wrapConfirmar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE); g2.fillRoundRect(0,0,getWidth(),getHeight(),4,4);
                g2.setColor(new Color(180,220,200)); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,4,4);
                g2.dispose();
            }
        };
        wrapConfirmar.setOpaque(false);
        wrapConfirmar.add(confirmarPasswordField, BorderLayout.CENTER);
        wrapConfirmar.add(btnOjoConfirmar, BorderLayout.EAST);
        btnOjoConfirmar.setPreferredSize(new Dimension(32, 35));
        wrapConfirmar.setBounds(220, 332, 160, 35);
        panel.add(wrapConfirmar);

        // Botón Crear cuenta
        registrarButton = new JButton("Crear cuenta");
        registrarButton.setFont(new Font("Arial", Font.BOLD, 14));
        registrarButton.setBackground(new Color(29, 158, 117));
        registrarButton.setForeground(Color.WHITE);
        registrarButton.setOpaque(true);
        registrarButton.setBorderPainted(false);
        registrarButton.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        registrarButton.setBounds(40, 395, 340, 40);
        panel.add(registrarButton);

        // Botón Volver
        volverButton = new JButton("Volver al login");
        volverButton.setFont(new Font("Arial", Font.PLAIN, 13));
        volverButton.setBackground(new Color(240, 248, 244));
        volverButton.setForeground(new Color(29, 158, 117));
        volverButton.setOpaque(true);
        volverButton.setBorder(BorderFactory.createLineBorder(new Color(29, 158, 117), 1));
        volverButton.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        volverButton.setBounds(40, 445, 340, 38);
        panel.add(volverButton);

        // ── Acciones ──────────────────────────────────────
        registrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre    = nombreField.getText().trim();
                String apellido  = apellidoField.getText().trim();
                String correo    = correoField.getText().trim();
                String telefono  = telefonoField.getText().trim();
                String pass      = new String(passwordField.getPassword());
                String confirmar = new String(confirmarPasswordField.getPassword());

                if (pass.length() < 8) {
                    JOptionPane.showMessageDialog(panel,
                            "La contraseña debe tener al menos 8 caracteres.",
                            "Contraseña muy corta", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // ← Llama al RegistroController que guarda en BD
                registroController.registrar(
                        nombre, apellido, correo, telefono, pass, confirmar, panel);
            }
        });

        volverButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.cambiarPantalla("login");
            }
        });
    }
}
