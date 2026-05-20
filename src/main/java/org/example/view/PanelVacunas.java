package org.example.view;

import org.example.model.Control_vacunas;
import org.example.model.Mascotas;
import org.example.service.ControlVacunaService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PanelVacunas {
    public JPanel panel;
    private boolean temaOscuro = false;
    private Mascotas mascotaSeleccionada = null;
    private List<Control_vacunas> cachedVacunas = null;
    private final ControlVacunaService vacunaService = new ControlVacunaService();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color[] CLARO = {
            new Color(240, 246, 252), new Color(26, 74, 122),  Color.WHITE,
            new Color(42, 90, 138),   new Color(230, 240, 250), Color.WHITE,
            new Color(26, 58, 90),    new Color(138, 170, 200), new Color(224, 112, 32),
            new Color(208, 228, 244), new Color(15, 53, 96),    new Color(122, 175, 212),
            new Color(168, 200, 232), new Color(168, 212, 245),
    };
    private final Color[] OSCURO = {
            new Color(18, 24, 38),  new Color(13, 18, 30),  new Color(26, 34, 52),
            new Color(37, 55, 90),  new Color(32, 42, 64),  Color.WHITE,
            new Color(226, 232, 240), new Color(100, 116, 139), new Color(251, 146, 60),
            new Color(30, 41, 59),  new Color(9, 14, 24),   new Color(122, 175, 212),
            new Color(80, 120, 170), new Color(100, 160, 210),
    };
    private Color[] C = CLARO;

    // Color rojo para cerrar sesión (independiente del tema)
    private static final Color ROJO = new Color(220, 38, 38);

    public PanelVacunas() { panel = new JPanel(new BorderLayout()); construir(); }

    public void setTema(boolean oscuro) {
        if (oscuro != temaOscuro) { temaOscuro = oscuro; cachedVacunas = null; construir(); }
    }
    public void recargar() { cachedVacunas = null; mascotaSeleccionada = null; construir(); }

    private void construir() {
        panel.removeAll(); C = temaOscuro ? OSCURO : CLARO;
        panel.setBackground(C[0]);
        panel.add(crearSidebar(), BorderLayout.WEST);

        if (cachedVacunas != null) {
            panel.add(crearContenido(cachedVacunas), BorderLayout.CENTER);
            panel.revalidate(); panel.repaint();
            return;
        }

        JPanel cargando = crearPanelCargando();
        panel.add(cargando, BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();

        new SwingWorker<List<Control_vacunas>, Void>() {
            @Override
            protected List<Control_vacunas> doInBackground() {
                if (Main.clienteActual == null) return Collections.emptyList();
                try { return vacunaService.listarPorCliente(Main.clienteActual.getId()); }
                catch (Exception e) { return Collections.emptyList(); }
            }
            @Override
            protected void done() {
                try   { cachedVacunas = get(); }
                catch (InterruptedException | ExecutionException e) { cachedVacunas = Collections.emptyList(); }
                if (mascotaSeleccionada == null && !cachedVacunas.isEmpty()) {
                    mascotaSeleccionada = cachedVacunas.get(0).getMascota();
                }
                panel.remove(cargando);
                panel.add(crearContenido(cachedVacunas), BorderLayout.CENTER);
                panel.revalidate(); panel.repaint();
            }
        }.execute();
    }

    private JPanel crearPanelCargando() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(C[0]);
        JLabel l = new JLabel("Cargando vacunas...", SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.PLAIN, 16));
        l.setForeground(C[7]);
        p.add(l, BorderLayout.CENTER);
        return p;
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
        String[] mp = {"Inicio", "Mis mascotas", "Mis citas", "Historial"};
        for (int i = 0; i < mp.length; i++) {
            JButton b = btn(mp[i], C[1], C[5], false);
            b.setFont(new Font("Arial", Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            if (i == 0) b.addActionListener(e -> Main.cambiarPantalla("panelCliente"));
            if (i == 1) b.addActionListener(e -> Main.cambiarPantalla("misMascotas"));
            if (i == 2) b.addActionListener(e -> Main.cambiarPantalla("misCitas"));
            if (i == 3) b.addActionListener(e -> Main.cambiarPantalla("historial"));
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalStrut(12));
        agregarSeccion(sb, "SERVICIOS");
        String[] ms = {"Alimentos", "Vacunas"};
        for (int i = 0; i < ms.length; i++) {
            JButton b = btn(ms[i], i == 1 ? C[2] : C[1], i == 1 ? C[1] : C[5], false);
            b.setFont(new Font("Arial", i == 1 ? Font.BOLD : Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            if (i == 0) b.addActionListener(e -> Main.cambiarPantalla("alimentos"));
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalGlue());

        JButton cerrar = btn("Cerrar sesion", C[1], ROJO, true);
        cerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cerrar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(panel, "¿Deseas cerrar sesion?", "Cerrar sesion",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                Main.frame.setSize(420, 520); Main.frame.setLocationRelativeTo(null);
                Main.cambiarPantalla("login");
            }
        });
        sb.add(cerrar); sb.add(Box.createVerticalStrut(8));

        String nombreCliente = Main.clienteActual != null ? Main.clienteActual.getNombre() : "Cliente";
        String[] partes = nombreCliente.split(" ");
        String iniciales = partes.length >= 2
                ? String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0))
                : String.valueOf(nombreCliente.charAt(0));
        JPanel up = new JPanel(new BorderLayout(8, 0));
        up.setBackground(C[10]); up.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        up.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66)); up.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel av = lbl(iniciales, 13, Font.BOLD, C[1]); av.setBackground(C[5]); av.setOpaque(true);
        av.setPreferredSize(new Dimension(40, 40)); av.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel ui = new JPanel(new GridLayout(2, 1)); ui.setBackground(C[10]);
        ui.add(lbl(nombreCliente, 12, Font.BOLD, C[5]));
        ui.add(lbl("Cliente", 10, Font.PLAIN, C[11]));
        up.add(av, BorderLayout.WEST); up.add(ui, BorderLayout.CENTER);
        sb.add(up);
        return sb;
    }

    private JPanel crearContenido(List<Control_vacunas> todos) {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(C[0]);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(C[2]);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C[9]),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JPanel topLeft = new JPanel(new GridLayout(2, 1)); topLeft.setBackground(C[2]);
        topLeft.add(lbl("Vacunas de mis mascotas", 20, Font.BOLD, C[6]));
        topLeft.add(lbl("Estado y registro de vacunacion", 12, Font.PLAIN, C[7]));
        topbar.add(topLeft, BorderLayout.WEST);
        contenido.add(topbar, BorderLayout.NORTH);

        // Cuerpo
        JPanel cuerpo = new JPanel(new BorderLayout(0, 20));
        cuerpo.setBackground(C[0]);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // Mascotas únicas
        List<Mascotas> mascotasUnicas = new ArrayList<>();
        for (Control_vacunas cv : todos) {
            Mascotas m = cv.getMascota();
            boolean yaEsta = false;
            for (Mascotas mu : mascotasUnicas) {
                if (mu.getId().equals(m.getId())) { yaEsta = true; break; }
            }
            if (!yaEsta) mascotasUnicas.add(m);
        }

        if (mascotaSeleccionada == null && !mascotasUnicas.isEmpty())
            mascotaSeleccionada = mascotasUnicas.get(0);

        // Selector de mascota
        if (!mascotasUnicas.isEmpty()) {
            JPanel selectorWrap = new JPanel(new BorderLayout(0, 8));
            selectorWrap.setBackground(C[0]);
            selectorWrap.add(lbl("Selecciona tu mascota:", 12, Font.PLAIN, C[7]), BorderLayout.NORTH);
            JPanel selector = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            selector.setBackground(C[0]);

            for (Mascotas m : mascotasUnicas) {
                boolean sel = mascotaSeleccionada != null && m.getId().equals(mascotaSeleccionada.getId());
                JPanel mc = new JPanel(new GridLayout(2, 1, 0, 4));
                mc.setBackground(sel ? C[1] : C[2]);
                mc.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(sel ? C[1] : C[9], 2),
                        BorderFactory.createEmptyBorder(12, 20, 12, 20)));
                mc.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
                JLabel nmM = lbl(m.getNombre(), 12, Font.BOLD, sel ? C[5] : C[6]);
                nmM.setHorizontalAlignment(SwingConstants.CENTER);
                JLabel tipM = lbl(m.getEspecie() != null ? m.getEspecie().getNombre() : "", 10, Font.PLAIN, sel ? C[13] : C[7]);
                tipM.setHorizontalAlignment(SwingConstants.CENTER);
                mc.add(nmM); mc.add(tipM);
                mc.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        mascotaSeleccionada = m; construir();
                    }
                });
                selector.add(mc);
            }
            selectorWrap.add(selector, BorderLayout.CENTER);
            cuerpo.add(selectorWrap, BorderLayout.NORTH);
        }

        // Lista de vacunas de la mascota seleccionada
        JPanel vacunasPanel = new JPanel(new BorderLayout(0, 10));
        vacunasPanel.setBackground(C[0]);

        if (mascotaSeleccionada != null) {
            JLabel tituloV = lbl("Vacunas de " + mascotaSeleccionada.getNombre(), 14, Font.BOLD, C[6]);
            tituloV.setBorder(BorderFactory.createEmptyBorder(4, 0, 8, 0));
            vacunasPanel.add(tituloV, BorderLayout.NORTH);
        }

        List<Control_vacunas> vacunasMascota = new ArrayList<>();
        for (Control_vacunas cv : todos) {
            if (mascotaSeleccionada != null && cv.getMascota().getId().equals(mascotaSeleccionada.getId()))
                vacunasMascota.add(cv);
        }

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(C[0]);

        if (vacunasMascota.isEmpty()) {
            JLabel sinVac = lbl(
                mascotaSeleccionada != null
                    ? "No hay vacunas registradas para " + mascotaSeleccionada.getNombre() + "."
                    : "No hay vacunas registradas.",
                13, Font.PLAIN, C[7]);
            sinVac.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(30));
            lista.add(sinVac);
        } else {
            List<Control_vacunas> necesitanAtencion = new ArrayList<>();
            for (Control_vacunas cv : vacunasMascota) {
                String est = cv.getEstado();
                if ("Vencida".equals(est) || "Próxima".equals(est)) necesitanAtencion.add(cv);
            }
            if (!necesitanAtencion.isEmpty()) {
                lista.add(crearBannerVacunas(necesitanAtencion));
                lista.add(Box.createVerticalStrut(14));
            }

            for (Control_vacunas cv : vacunasMascota) {
                String nombreVac  = cv.getVacuna() != null ? cv.getVacuna().getNombre() : "—";
                String fechaAplic = cv.getFechaAplicacion() != null ? cv.getFechaAplicacion().format(FMT) : "—";
                String proxDosis  = cv.getProximaDosis()    != null ? cv.getProximaDosis().format(FMT)    : "Sin próxima dosis";
                String estado     = cv.getEstado();

                Color estadoColor;
                switch (estado) {
                    case "Vencida": estadoColor = new Color(220, 38, 38);  break;
                    case "Próxima": estadoColor = new Color(234, 88, 12);  break;
                    default:        estadoColor = new Color(22, 163, 74);  break;
                }

                JPanel card = new JPanel(new BorderLayout(12, 0));
                card.setBackground(C[2]);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, estadoColor),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(C[9], 1),
                                BorderFactory.createEmptyBorder(14, 18, 14, 18))));

                JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
                info.setBackground(C[2]);
                info.add(lbl(nombreVac, 13, Font.BOLD, C[6]));
                info.add(lbl("Aplicada: " + fechaAplic + "   ·   Próxima dosis: " + proxDosis, 11, Font.PLAIN, C[7]));

                JLabel badge = lbl(estado, 11, Font.BOLD, estadoColor);
                badge.setHorizontalAlignment(SwingConstants.RIGHT);

                card.add(info, BorderLayout.CENTER);
                card.add(badge, BorderLayout.EAST);
                lista.add(card);
                lista.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null); scroll.getViewport().setBackground(C[0]);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        vacunasPanel.add(scroll, BorderLayout.CENTER);
        cuerpo.add(vacunasPanel, BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearBannerVacunas(List<Control_vacunas> pendientes) {
        Color bgBanner  = temaOscuro ? new Color(50, 30, 10)   : new Color(255, 247, 237);
        Color borde     = new Color(234, 88, 12);
        Color tituloCol = temaOscuro ? new Color(251, 146, 60) : new Color(154, 52, 18);

        JPanel banner = new JPanel(new BorderLayout(0, 6));
        banner.setBackground(bgBanner);
        banner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, borde),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borde, 1),
                        BorderFactory.createEmptyBorder(14, 18, 14, 18))));
        banner.setAlignmentX(Component.LEFT_ALIGNMENT);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 999));

        JLabel titulo = new JLabel("⚠ Vacunas que requieren atención");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setForeground(tituloCol);

        JPanel listaVac = new JPanel();
        listaVac.setLayout(new BoxLayout(listaVac, BoxLayout.Y_AXIS));
        listaVac.setBackground(bgBanner);
        listaVac.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        for (Control_vacunas cv : pendientes) {
            String nombre = cv.getVacuna() != null ? cv.getVacuna().getNombre() : "Vacuna";
            String estado = cv.getEstado();
            String prox   = cv.getProximaDosis() != null
                    ? "  —  Próxima dosis: " + cv.getProximaDosis().format(FMT) : "";
            Color c = "Vencida".equals(estado) ? new Color(220, 38, 38) : new Color(234, 88, 12);
            JLabel l = new JLabel("• " + nombre + "  [" + estado + "]" + prox);
            l.setFont(new Font("Arial", Font.PLAIN, 12));
            l.setForeground(c);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaVac.add(l);
            listaVac.add(Box.createVerticalStrut(3));
        }

        JButton btnAgendar = new JButton("Agendar cita de vacunación");
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 12));
        btnAgendar.setBackground(borde);
        btnAgendar.setForeground(Color.WHITE);
        btnAgendar.setOpaque(true); btnAgendar.setBorderPainted(false);
        btnAgendar.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        btnAgendar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnAgendar.addActionListener(e -> Main.cambiarPantalla("agendarCita"));
        btnAgendar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        botones.setBackground(bgBanner);
        botones.add(btnAgendar);

        banner.add(titulo,   BorderLayout.NORTH);
        banner.add(listaVac, BorderLayout.CENTER);
        banner.add(botones,  BorderLayout.SOUTH);
        return banner;
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
