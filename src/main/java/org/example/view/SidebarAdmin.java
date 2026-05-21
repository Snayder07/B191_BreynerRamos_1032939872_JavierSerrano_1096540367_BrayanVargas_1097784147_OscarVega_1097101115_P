package org.example.view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class SidebarAdmin {

    public static JPanel crear(Color[] C, boolean temaOscuro, String pantallaActual, JPanel panelRef) {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(C[1]);
        sb.setPreferredSize(new Dimension(240, 0));

        // ── Logo ─────────────────────────────────────────
        JPanel logoPanel = new JPanel(new GridBagLayout());
        logoPanel.setBackground(C[1]);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        logoPanel.setMinimumSize(new Dimension(240, 140));
        logoPanel.setPreferredSize(new Dimension(240, 140));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoLabel;
        try {
            URL imgUrl = SidebarAdmin.class.getClassLoader().getResource("logo.png");
            if (imgUrl != null) {
                ImageIcon rawIcon = new ImageIcon(imgUrl);
                Image rawImg = rawIcon.getImage();
                int targetW = 240;
                int targetH = 140;
                int origW = rawIcon.getIconWidth();
                int origH = rawIcon.getIconHeight();
                if (origW > 0 && origH > 0) {
                    double ratio = Math.min((double) targetW / origW, (double) targetH / origH);
                    targetW = (int)(origW * ratio);
                    targetH = (int)(origH * ratio);
                }
                BufferedImage scaled = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = scaled.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(rawImg, 0, 0, targetW, targetH, null);
                g2.dispose();
                logoLabel = new JLabel(new ImageIcon(scaled));
            } else {
                logoLabel = new JLabel("Kampets");
                logoLabel.setFont(new Font("Arial", Font.BOLD, 22));
                logoLabel.setForeground(Color.WHITE);
            }
        } catch (Exception ex) {
            logoLabel = new JLabel("Kampets");
            logoLabel.setFont(new Font("Arial", Font.BOLD, 22));
            logoLabel.setForeground(Color.WHITE);
        }
        logoPanel.add(logoLabel);
        sb.add(logoPanel);
        agregarSep(sb, C);

        // ── GENERAL ──────────────────────────────────────
        agregarSeccion(sb, "GENERAL", C);

        JButton btnInicio = crearItem("🏠", "Inicio", pantallaActual.equals("panelAdmin"), C);
        btnInicio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("panelAdmin"); }
        });
        sb.add(btnInicio); sb.add(Box.createVerticalStrut(4));

        JButton btnCitas = crearItem("📋", "Todas las citas", pantallaActual.equals("adminCitas"), C);
        btnCitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminCitas"); }
        });
        sb.add(btnCitas); sb.add(Box.createVerticalStrut(4));

        JButton btnMascotas = crearItem("🐾", "Mascotas registradas", pantallaActual.equals("adminMascotas"), C);
        btnMascotas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminMascotas"); }
        });
        sb.add(btnMascotas); sb.add(Box.createVerticalStrut(4));

        JButton btnCalendario = crearItem("📅", "Calendario", pantallaActual.equals("adminCalendario"), C);
        btnCalendario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminCalendario"); }
        });
        sb.add(btnCalendario); sb.add(Box.createVerticalStrut(4));

        agregarSep(sb, C);

        // ── CLÍNICA ───────────────────────────────────────
        agregarSeccion(sb, "CLÍNICA", C);

        JButton btnVacunas = crearItem("💉", "Vacunas pendientes", pantallaActual.equals("adminVacunas"), C);
        btnVacunas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminVacunas"); }
        });
        sb.add(btnVacunas); sb.add(Box.createVerticalStrut(4));

        JButton btnInventario = crearItem("📦", "Inventario", pantallaActual.equals("adminInventario"), C);
        btnInventario.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminInventario"); }
        });
        sb.add(btnInventario); sb.add(Box.createVerticalStrut(4));

        JButton btnReportes = crearItem("📊", "Reportes", pantallaActual.equals("adminReportes"), C);
        btnReportes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminReportes"); }
        });
        sb.add(btnReportes); sb.add(Box.createVerticalStrut(4));

        agregarSep(sb, C);
        sb.add(Box.createVerticalGlue());

        // ── Cerrar sesión ─────────────────────────────────
        JButton cerrar = new JButton("Cerrar sesión");
        cerrar.setFont(new Font("Arial", Font.PLAIN, 15));
        cerrar.setBackground(new Color(127, 29, 29));
        cerrar.setForeground(new Color(252, 165, 165));
        cerrar.setOpaque(true); cerrar.setBorderPainted(false); cerrar.setFocusPainted(false);
        cerrar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        cerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        cerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cerrar.setHorizontalAlignment(SwingConstants.LEFT);
        cerrar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        cerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int r = JOptionPane.showConfirmDialog(panelRef, "¿Deseas cerrar sesión?",
                        "Cerrar sesión", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    Main.empleadoActual = null;
                    Main.clienteActual  = null;
                    Main.frame.setExtendedState(JFrame.NORMAL);
                    Main.frame.setSize(420, 520);
                    Main.frame.setLocationRelativeTo(null);
                    Main.cambiarPantalla("login");
                }
            }
        });
        sb.add(cerrar); sb.add(Box.createVerticalStrut(8));

        // ── Usuario — nombre real del admin logueado ──────
        String nombreAdmin = Main.empleadoActual != null ?
                Main.empleadoActual.getNombre() : "Administrador";
        String cargoAdmin  = Main.empleadoActual != null ?
                Main.empleadoActual.getCargo()   : "Admin · Kampets";

        // Iniciales del nombre
        String[] partes = nombreAdmin.split(" ");
        String iniciales = partes.length >= 2 ?
                String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0)) :
                String.valueOf(nombreAdmin.charAt(0));

        JPanel up = new JPanel(new BorderLayout(10, 0));
        up.setBackground(C[10]);
        up.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        up.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        up.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel av = new JLabel(iniciales);
        av.setFont(new Font("Arial", Font.BOLD, 15));
        av.setForeground(C[1]); av.setBackground(Color.WHITE); av.setOpaque(true);
        av.setPreferredSize(new Dimension(40, 40));
        av.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel ui = new JPanel(new GridLayout(2, 1));
        ui.setBackground(C[10]);
        JLabel uName = new JLabel(nombreAdmin);
        uName.setFont(new Font("Arial", Font.BOLD, 14)); uName.setForeground(Color.WHITE);
        JLabel uRole = new JLabel(cargoAdmin + " · Kampets");
        uRole.setFont(new Font("Arial", Font.PLAIN, 12)); uRole.setForeground(C[11]);
        ui.add(uName); ui.add(uRole);
        up.add(av, BorderLayout.WEST); up.add(ui, BorderLayout.CENTER);
        sb.add(up);

        return sb;
    }

    private static JButton crearItem(String emoji, String texto, boolean activo, Color[] C) {
        // Panel interno: emoji + texto
        JPanel contenido = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        contenido.setOpaque(false);

        JLabel iconLbl = new JLabel(emoji + "  ");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 17));
        iconLbl.setForeground(activo ? Color.WHITE : C[7]);

        JLabel txtLbl = new JLabel(texto);
        txtLbl.setFont(new Font("Arial", activo ? Font.BOLD : Font.PLAIN, 15));
        txtLbl.setForeground(activo ? Color.WHITE : C[7]);

        contenido.add(iconLbl);
        contenido.add(txtLbl);

        Color bgActivo = C[3]; // verde medio del tema
        JButton b = new JButton();
        b.setLayout(new BorderLayout());
        b.add(contenido, BorderLayout.CENTER);
        b.setBackground(activo ? bgActivo : C[1]);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 12));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!activo) { b.setBackground(bgActivo); iconLbl.setForeground(Color.WHITE); txtLbl.setForeground(Color.WHITE); }
            }
            public void mouseExited(MouseEvent e) {
                if (!activo) { b.setBackground(C[1]); iconLbl.setForeground(C[7]); txtLbl.setForeground(C[7]); }
            }
        });
        return b;
    }

    private static void agregarSeccion(JPanel p, String t, Color[] C) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 11)); l.setForeground(C[11]);
        l.setBorder(BorderFactory.createEmptyBorder(14, 20, 6, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        p.add(l);
    }

    private static void agregarSep(JPanel p, Color[] C) {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(255, 255, 255, 30));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Box.createVerticalStrut(6)); p.add(s); p.add(Box.createVerticalStrut(6));
    }
}
