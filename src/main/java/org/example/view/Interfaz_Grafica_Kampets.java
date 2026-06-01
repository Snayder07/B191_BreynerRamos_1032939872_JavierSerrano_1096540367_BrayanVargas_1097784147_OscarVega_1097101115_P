package org.example.view;

import org.example.controller.LoginController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Interfaz_Grafica_Kampets {
    public JPanel panel;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton entrarButton;
    private JButton entrarAdminButton;
    private JButton crearCuentaButton;

    // ── Conectado al LoginController ──────────────────────
    private final LoginController loginController = new LoginController();

    public Interfaz_Grafica_Kampets() {

        // ── Panel principal ──────────────────────────────
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(240, 248, 244));
        panel.setPreferredSize(new Dimension(420, 480)); // aumentado para el enlace nuevo

        // ── Título ───────────────────────────────────────
        JLabel titulo = new JLabel("Kampets");
        titulo.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titulo.setForeground(new Color(29, 158, 117));
        titulo.setBounds(130, 25, 200, 50);
        panel.add(titulo);

        JLabel subtitulo = new JLabel("Veterinaria");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(120, 120, 120));
        subtitulo.setBounds(175, 70, 120, 20);
        panel.add(subtitulo);

        // ── Separador ────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setBounds(40, 100, 340, 2);
        sep.setForeground(new Color(29, 158, 117));
        panel.add(sep);

        // ── Label Correo ─────────────────────────────────
        JLabel labelCorreo = new JLabel("Correo electrónico");
        labelCorreo.setFont(new Font("Arial", Font.PLAIN, 12));
        labelCorreo.setForeground(new Color(80, 80, 80));
        labelCorreo.setBounds(40, 115, 180, 20);
        panel.add(labelCorreo);

        // ── Campo Correo ─────────────────────────────────
        textField1 = new JTextField();
        textField1.setFont(new Font("Arial", Font.PLAIN, 13));
        textField1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        textField1.setBounds(40, 138, 340, 35);
        panel.add(textField1);

        // ── Label Contraseña ─────────────────────────────
        JLabel labelPass = new JLabel("Contraseña");
        labelPass.setFont(new Font("Arial", Font.PLAIN, 12));
        labelPass.setForeground(new Color(80, 80, 80));
        labelPass.setBounds(40, 185, 180, 20);
        panel.add(labelPass);

        // ── Campo Contraseña + ojo ────────────────────────
        passwordField1 = new JPasswordField();
        passwordField1.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField1.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        passwordField1.setOpaque(false);

        JButton btnOjo1 = new JButton("\uD83D\uDC41") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean vis = (Boolean)getClientProperty("visible") == Boolean.TRUE;
                g2.setColor(vis ? new Color(29,158,117) : new Color(130,130,130));
                int cx=getWidth()/2, cy=getHeight()/2;
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                // Contorno del ojo
                g2.drawOval(cx-7, cy-4, 14, 9);
                // Pupila
                g2.fillOval(cx-2, cy-2, 5, 5);
                // Tachado si está oculto
                if (!vis) {
                    g2.setColor(new Color(130,130,130));
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx-8, cy+5, cx+8, cy-5);
                }
                g2.dispose();
            }
        };
        btnOjo1.putClientProperty("visible", Boolean.FALSE);
        btnOjo1.setText("");
        btnOjo1.setOpaque(false); btnOjo1.setContentAreaFilled(false); btnOjo1.setBorderPainted(false); btnOjo1.setFocusPainted(false);
        btnOjo1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOjo1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean vis = (Boolean) btnOjo1.getClientProperty("visible") == Boolean.TRUE;
                if (!vis) {
                    passwordField1.setEchoChar((char) 0);
                    btnOjo1.putClientProperty("visible", Boolean.TRUE);
                } else {
                    passwordField1.setEchoChar('\u2022');
                    btnOjo1.putClientProperty("visible", Boolean.FALSE);
                }
                btnOjo1.repaint();
            }
        });

        // Panel contenedor: campo + ojo lado a lado
        JPanel wrapPass1 = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.setColor(new Color(180, 220, 200));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 4, 4);
                g2.dispose();
            }
        };
        wrapPass1.setOpaque(false);
        wrapPass1.add(passwordField1, BorderLayout.CENTER);
        wrapPass1.add(btnOjo1, BorderLayout.EAST);
        btnOjo1.setPreferredSize(new Dimension(32, 35));
        wrapPass1.setBounds(40, 208, 340, 35);
        panel.add(wrapPass1);

        // ── Enlace "¿Olvidaste tu contraseña?" ───────────
        JLabel linkOlvide = new JLabel("¿Olvidaste tu contraseña?");
        linkOlvide.setFont(new Font("Arial", Font.PLAIN, 12));
        linkOlvide.setForeground(new Color(29, 158, 117));
        linkOlvide.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        linkOlvide.setBounds(40, 250, 220, 20);
        // Subrayado al pasar el mouse
        linkOlvide.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                linkOlvide.setText("<html><u>¿Olvidaste tu contraseña?</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                linkOlvide.setText("¿Olvidaste tu contraseña?");
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Abre el diálogo de recuperación
                Window ventana = SwingUtilities.getWindowAncestor(panel);
                RecuperacionContrasenaDialog dialogo = new RecuperacionContrasenaDialog(ventana);
                dialogo.setVisible(true);
            }
        });
        panel.add(linkOlvide);

        // ── Botón Entrar ─────────────────────────────────
        entrarButton = new JButton("Entrar");
        entrarButton.setFont(new Font("Arial", Font.BOLD, 14));
        entrarButton.setBackground(new Color(29, 158, 117));
        entrarButton.setForeground(Color.WHITE);
        entrarButton.setOpaque(true);
        entrarButton.setBorderPainted(false);
        entrarButton.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        entrarButton.setBounds(40, 280, 340, 40);
        panel.add(entrarButton);

        // ── Botón Entrar como Administrador ──────────────
        entrarAdminButton = new JButton("Entrar como administrador");
        entrarAdminButton.setFont(new Font("Arial", Font.PLAIN, 13));
        entrarAdminButton.setBackground(new Color(15, 110, 86));
        entrarAdminButton.setForeground(Color.WHITE);
        entrarAdminButton.setOpaque(true);
        entrarAdminButton.setBorderPainted(false);
        entrarAdminButton.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        entrarAdminButton.setBounds(40, 330, 340, 38);
        panel.add(entrarAdminButton);

        // ── Texto primera vez ─────────────────────────────
        JLabel labelSep = new JLabel("¿Es tu primera vez aquí?");
        labelSep.setFont(new Font("Arial", Font.PLAIN, 12));
        labelSep.setForeground(new Color(150, 150, 150));
        labelSep.setBounds(40, 378, 340, 20);
        labelSep.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(labelSep);

        // ── Botón Crear cuenta ────────────────────────────
        crearCuentaButton = new JButton("Crear cuenta");
        crearCuentaButton.setFont(new Font("Arial", Font.PLAIN, 13));
        crearCuentaButton.setBackground(new Color(240, 248, 244));
        crearCuentaButton.setForeground(new Color(29, 158, 117));
        crearCuentaButton.setOpaque(true);
        crearCuentaButton.setBorder(BorderFactory.createLineBorder(new Color(29, 158, 117), 1));
        crearCuentaButton.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        crearCuentaButton.setBounds(40, 402, 340, 38);
        panel.add(crearCuentaButton);

        // ── Acciones de los botones ───────────────────────

        // Botón Entrar cliente — consulta la BD
        entrarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String correo = textField1.getText().trim();
                String clave  = new String(passwordField1.getPassword());
                if (correo.isEmpty() || clave.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "Por favor ingresa correo y contraseña.",
                            "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (clave.length() < 8) {
                    JOptionPane.showMessageDialog(panel,
                            "La contraseña debe tener al menos 8 caracteres.",
                            "Contraseña muy corta", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                loginController.loginCliente(correo, clave, panel);
            }
        });

        // Botón Entrar admin — consulta la BD
        entrarAdminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String correo = textField1.getText().trim();
                String clave  = new String(passwordField1.getPassword());
                if (correo.isEmpty() || clave.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "Por favor ingresa correo y contraseña.",
                            "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (clave.length() < 8) {
                    JOptionPane.showMessageDialog(panel,
                            "La contraseña debe tener al menos 8 caracteres.",
                            "Contraseña muy corta", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                loginController.loginAdmin(correo, clave, panel);
            }
        });

        // Botón Crear cuenta
        crearCuentaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Main.cambiarPantalla("crearCuenta");
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Kampets - Login");
        frame.setContentPane(new Interfaz_Grafica_Kampets().panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
