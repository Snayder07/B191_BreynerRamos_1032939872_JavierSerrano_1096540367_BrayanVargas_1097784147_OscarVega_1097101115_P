package org.example.view;

import org.example.controller.CitaAdminController;
import org.example.model.Citas;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

public class PanelHistorial {
    private final CitaAdminController ctrl = new CitaAdminController();
    public JPanel panel;

    private final Color[] CLARO = {
            new Color(240, 246, 252), new Color(26, 74, 122), Color.WHITE,
            new Color(42, 90, 138),   new Color(230, 240, 250), Color.WHITE,
            new Color(26, 58, 90),    new Color(138, 170, 200), new Color(224, 112, 32),
            new Color(208, 228, 244), new Color(15, 53, 96),    new Color(122, 175, 212),
            new Color(168, 200, 232), new Color(168, 212, 245),
    };
    private Color[] C = CLARO;

    public PanelHistorial() { panel = new JPanel(new BorderLayout()); construir(); }

    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll(); C = CLARO;
        panel.setBackground(C[0]);
        panel.add(crearSidebar(), BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JLabel lbl(String t, int sz, int st, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial", st, sz + 2)); l.setForeground(c); return l;
    }
    private JButton btn(String t, Color bg, Color fg, boolean borde) {
        JButton b = new JButton(t); b.setFont(new Font("Arial", Font.PLAIN, 15));
        b.setBackground(bg); b.setForeground(fg); b.setOpaque(true);
        b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        if (borde) b.setBorder(BorderFactory.createLineBorder(fg, 1)); else b.setBorderPainted(false);
        return b;
    }

    private JPanel crearSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(C[1]); sb.setPreferredSize(new Dimension(240, 0));
        sb.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    java.net.URL imgUrl = getClass().getClassLoader().getResource("logo_cliente.png");
                    if (imgUrl != null) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.drawImage(new ImageIcon(imgUrl).getImage(), 0, 0, getWidth(), getHeight(), this);
                        g2.dispose();
                    }
                } catch (Exception ignored) {}
            }
        };
        logoPanel.setBackground(C[1]);
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        logoPanel.setMinimumSize(new Dimension(240, 140));
        logoPanel.setPreferredSize(new Dimension(240, 140));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sb.add(logoPanel);
        agregarSep(sb);

        agregarSeccion(sb, "PRINCIPAL");
        // Historial resaltado (idx==3)
        String[] mp = {"Inicio", "Mis mascotas", "Mis citas", "Historial"};
        for (int i = 0; i < mp.length; i++) {
            final int idx = i;
            JButton b = btn(mp[i], i == 3 ? C[2] : C[1], i == 3 ? C[1] : C[5], false);
            b.setFont(new Font("Arial", i == 3 ? Font.BOLD : Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (idx == 0) Main.cambiarPantalla("panelCliente");
                    if (idx == 1) Main.cambiarPantalla("misMascotas");
                    if (idx == 2) Main.cambiarPantalla("misCitas");
                }
            });
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalStrut(12));

        agregarSeccion(sb, "SERVICIOS");
        String[] ms = {"Alimentos", "Vacunas"};
        for (String item : ms) {
            JButton b = btn(item, C[1], C[5], false);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            if (item.equals("Alimentos")) b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("alimentos"); }
            });
            if (item.equals("Vacunas")) b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("vacunas"); }
            });
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalGlue());

        JButton cerrar = btn("Cerrar sesion", C[1], C[12], true);
        cerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(panel, "Deseas cerrar sesion?",
                        "Cerrar sesion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    Main.clienteActual = null;
                    Main.frame.setSize(420, 520);
                    Main.frame.setLocationRelativeTo(null);
                    Main.cambiarPantalla("login");
                }
            }
        });
        sb.add(cerrar); sb.add(Box.createVerticalStrut(8));

        String nombreCliente = Main.clienteActual != null ? Main.clienteActual.getNombre() : "Cliente";
        String[] partes = nombreCliente.split(" ");
        String iniciales = partes.length >= 2 ?
                String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0)) : "C";

        JPanel up = new JPanel(new BorderLayout(8, 0));
        up.setBackground(C[10]); up.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        up.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        up.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel av = lbl(iniciales, 13, Font.BOLD, C[1]); av.setBackground(C[5]); av.setOpaque(true);
        av.setPreferredSize(new Dimension(40, 40)); av.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel ui = new JPanel(new GridLayout(2, 1)); ui.setBackground(C[10]);
        ui.add(lbl(nombreCliente, 12, Font.BOLD, C[5]));
        ui.add(lbl("Cliente", 10, Font.PLAIN, C[11]));
        up.add(av, BorderLayout.WEST); up.add(ui, BorderLayout.CENTER);
        sb.add(up);
        return sb;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(C[0]);

        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(C[2]);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C[9]),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JPanel topLeft = new JPanel(new GridLayout(2, 1)); topLeft.setBackground(C[2]);
        topLeft.add(lbl("Historial médico", 20, Font.BOLD, C[6]));
        topLeft.add(lbl("Registro de citas y tratamientos anteriores", 12, Font.PLAIN, C[7]));

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setBackground(C[2]);
        topbar.add(topLeft, BorderLayout.WEST);
        topbar.add(topRight, BorderLayout.EAST);
        contenido.add(topbar, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0, 20));
        cuerpo.setBackground(C[0]);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Cargar citas pasadas del cliente
        List<Citas> pasadas = Main.clienteActual != null
                ? ctrl.listarPasadasPorCliente(Main.clienteActual.getId())
                : Collections.emptyList();

        // Stats reales
        String totalStr = String.valueOf(pasadas.size());
        String ultimaStr = pasadas.isEmpty() ? "—"
                : pasadas.get(0).getFechaCita().toString();

        JPanel stats = new JPanel(new GridLayout(1, 2, 16, 0));
        stats.setBackground(C[0]);
        String[][] st = {{"Total de citas pasadas", totalStr}, {"Ultima visita", ultimaStr}};
        for (String[] s : st) {
            JPanel card = new JPanel(new BorderLayout(0, 4));
            card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C[9], 1),
                    BorderFactory.createEmptyBorder(18, 20, 18, 20)));
            card.add(lbl(s[0], 11, Font.PLAIN, C[7]), BorderLayout.NORTH);
            card.add(lbl(s[1], 26, Font.BOLD, C[6]), BorderLayout.CENTER);
            stats.add(card);
        }
        cuerpo.add(stats, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(C[0]);

        if (pasadas.isEmpty()) {
            JLabel sinH = lbl("No tienes citas anteriores registradas.", 13, Font.PLAIN, C[7]);
            sinH.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(30));
            lista.add(sinH);
        } else {
            for (Citas c : pasadas) {
                String mascota = c.getMascota()  != null ? c.getMascota().getNombre()  : "—";
                String vet     = c.getEmpleado() != null ? c.getEmpleado().getNombre() : "—";
                String fecha   = c.getFechaCita() != null ? c.getFechaCita().toString() : "—";
                String hora    = c.getHoraCita()  != null ? c.getHoraCita().toString()  : "—";
                String estado  = c.getEstadoCita() != null ? c.getEstadoCita().toString() : "—";

                Color colorEstado;
                if (c.getEstadoCita() != null) {
                    switch (c.getEstadoCita()) {
                        case COMPLETADA: colorEstado = new Color(22, 163, 74);  break;
                        case CANCELADA:  colorEstado = new Color(220, 38, 38);  break;
                        default:         colorEstado = new Color(100,116,139);  break;
                    }
                } else { colorEstado = new Color(100, 116, 139); }

                JPanel card = new JPanel(new BorderLayout(12, 0));
                card.setBackground(C[2]);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, colorEstado),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(C[9], 1),
                                BorderFactory.createEmptyBorder(14, 18, 14, 18))));

                JPanel izq = new JPanel(new GridLayout(2, 1, 0, 4));
                izq.setBackground(C[2]);
                izq.add(lbl(mascota, 13, Font.BOLD, C[6]));
                izq.add(lbl(fecha + "  ·  " + hora + "  ·  " + vet, 11, Font.PLAIN, C[7]));

                JLabel badge = lbl(estado, 11, Font.BOLD, colorEstado);
                badge.setHorizontalAlignment(SwingConstants.RIGHT);

                card.add(izq, BorderLayout.CENTER);
                card.add(badge, BorderLayout.EAST);
                lista.add(card);
                lista.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null); scroll.getViewport().setBackground(C[0]);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        cuerpo.add(scroll, BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);
        return contenido;
    }

    private void agregarSeccion(JPanel p, String t) {
        JLabel l = lbl(t, 10, Font.PLAIN, C[11]);
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24)); p.add(l);
    }
    private void agregarSep(JPanel p) {
        JSeparator s = new JSeparator(); s.setForeground(C[3]);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Box.createVerticalStrut(6)); p.add(s); p.add(Box.createVerticalStrut(6));
    }
}