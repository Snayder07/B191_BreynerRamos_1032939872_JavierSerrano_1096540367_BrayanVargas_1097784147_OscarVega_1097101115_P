package org.example.view;

import org.example.controller.InventarioController;
import org.example.model.Productos;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;

public class PanelAlimentos {
    public JPanel panel;
    private boolean temaOscuro = false;

    private final Color[] CLARO = {
            new Color(240, 246, 252), new Color(26, 74, 122), Color.WHITE,
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

    public PanelAlimentos() { panel = new JPanel(new BorderLayout()); construir(); }

    public void setTema(boolean oscuro) { if (oscuro != temaOscuro) { temaOscuro = oscuro; construir(); } }
    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll(); C = temaOscuro ? OSCURO : CLARO;
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
        if (borde) b.setBorder(BorderFactory.createLineBorder(fg,1)); else b.setBorderPainted(false);
        return b;
    }

    private JPanel crearSidebar() {
        JPanel sb = new JPanel();
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBackground(C[1]); sb.setPreferredSize(new Dimension(240,0));
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
        String[] mp = {"Inicio","Mis mascotas","Mis citas","Historial"};
        for (int i = 0; i < mp.length; i++) {
            JButton b = btn(mp[i], C[1], C[5], false);
            b.setFont(new Font("Arial", Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            if (i == 0) b.addActionListener(e -> Main.cambiarPantalla("panelCliente"));
            if (i == 1) b.addActionListener(e -> Main.cambiarPantalla("misMascotas"));
            if (i == 2) b.addActionListener(e -> Main.cambiarPantalla("misCitas"));
            if (i == 3) b.addActionListener(e -> Main.cambiarPantalla("historial"));
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalStrut(12));
        agregarSeccion(sb, "SERVICIOS");
        String[] ms = {"Alimentos","Vacunas"};
        for (int i = 0; i < ms.length; i++) {
            JButton b = btn(ms[i], i == 0 ? C[2] : C[1], i == 0 ? C[1] : C[5], false);
            b.setFont(new Font("Arial", i == 0 ? Font.BOLD : Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            if (i == 1) b.addActionListener(e -> Main.cambiarPantalla("vacunas"));
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalGlue());

        JButton cerrar = btn("Cerrar sesion", C[1], C[12], true);
        cerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        cerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        cerrar.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(panel,"Deseas cerrar sesion?","Cerrar sesion",
                    JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                Main.frame.setSize(420,520); Main.frame.setLocationRelativeTo(null);
                Main.cambiarPantalla("login");
            }
        });
        sb.add(cerrar); sb.add(Box.createVerticalStrut(8));

        String nombreCliente = Main.clienteActual != null ? Main.clienteActual.getNombre() : "Cliente";
        String[] partesA = nombreCliente.split(" ");
        String inicialesA = partesA.length >= 2 ?
                String.valueOf(partesA[0].charAt(0)) + String.valueOf(partesA[1].charAt(0)) :
                String.valueOf(nombreCliente.charAt(0));
        JPanel up = new JPanel(new BorderLayout(8,0));
        up.setBackground(C[10]); up.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        up.setMaximumSize(new Dimension(Integer.MAX_VALUE,66)); up.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel av = lbl(inicialesA,13,Font.BOLD,C[1]); av.setBackground(C[5]); av.setOpaque(true);
        av.setPreferredSize(new Dimension(40,40)); av.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel ui = new JPanel(new GridLayout(2,1)); ui.setBackground(C[10]);
        ui.add(lbl(nombreCliente,12,Font.BOLD,C[5]));
        ui.add(lbl("Cliente",10,Font.PLAIN,C[11]));
        up.add(av,BorderLayout.WEST); up.add(ui,BorderLayout.CENTER);
        sb.add(up);
        return sb;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(C[0]);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(C[2]);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,C[9]),
                BorderFactory.createEmptyBorder(16,24,16,24)));
        JPanel topLeft = new JPanel(new GridLayout(2,1)); topLeft.setBackground(C[2]);
        topLeft.add(lbl("Alimentos para mascotas", 20, Font.BOLD, C[6]));
        topLeft.add(lbl("Productos recomendados para tus mascotas", 12, Font.PLAIN, C[7]));

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setBackground(C[2]);
        topbar.add(topLeft, BorderLayout.WEST);
        topbar.add(topRight, BorderLayout.EAST);
        contenido.add(topbar, BorderLayout.NORTH);

        // Cuerpo
        JPanel cuerpo = new JPanel(new BorderLayout(0,20));
        cuerpo.setBackground(C[0]);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(24,28,28,28));

        // Cargar productos desde la base de datos
        InventarioController ctrl = new InventarioController();
        List<Productos> lista = ctrl.listarTodos();

        // Paleta de colores para tipos de producto
        Color[] paleta = {
            new Color(59, 130, 246),   // azul
            new Color(168, 85, 247),   // violeta
            new Color(34, 197, 94),    // verde
            new Color(20, 184, 166),   // teal
            new Color(251, 146, 60),   // naranja
            new Color(239, 68, 68),    // rojo
        };

        int totalProductos = lista.size();
        if (totalProductos == 0) {
            JLabel sinProd = new JLabel("No hay productos disponibles.", SwingConstants.CENTER);
            sinProd.setFont(new Font("Arial", Font.PLAIN, 14));
            sinProd.setForeground(C[7]);
            cuerpo.add(sinProd, BorderLayout.CENTER);
            contenido.add(cuerpo, BorderLayout.CENTER);
            return contenido;
        }
        int cols = 3;
        int rows = (totalProductos + cols - 1) / cols;

        JPanel grid = new JPanel(new GridLayout(rows, cols, 16, 16));
        grid.setBackground(C[0]);

        for (int idx = 0; idx < totalProductos; idx++) {
            Productos p  = lista.get(idx);
            Color    color = paleta[idx % paleta.length];

            // ── TARJETA ───────────────────────────────────────
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(C[9], 1),
                            BorderFactory.createEmptyBorder(16, 16, 16, 16))));

            // ── Datos ────────────────────────────────────────
            String cat      = p.getTipo() != null && !p.getTipo().isEmpty() ? p.getTipo().toUpperCase() : "PRODUCTO";
            String nombre   = p.getNombre() != null ? p.getNombre() : "Sin nombre";
            String marca    = p.getMarca()  != null && !p.getMarca().isEmpty() ? p.getMarca() : "";
            int    stockVal = p.getStock()  != null ? p.getStock() : 0;
            BigDecimal precioVal = p.getPrecio() != null ? p.getPrecio() : BigDecimal.ZERO;
            String precioStr = "$" + String.format("%,.0f", precioVal.doubleValue());

            // ── BADGE ─────────────────────────────────────────
            JLabel badge = new JLabel(cat);
            badge.setFont(new Font("Arial", Font.BOLD, 10));
            badge.setForeground(color);
            badge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            JPanel topWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            topWrap.setBackground(C[2]);
            topWrap.add(badge);
            card.add(topWrap, BorderLayout.NORTH);

            // ── CENTRO: info + foto ───────────────────────────
            JPanel centro = new JPanel(new BorderLayout(0, 8));
            centro.setBackground(C[2]);

            // Texto (nombre, marca)
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBackground(C[2]);
            JLabel lblNombre = lbl(nombre, 13, Font.BOLD, C[6]);
            lblNombre.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
            info.add(lblNombre);
            if (!marca.isEmpty()) {
                JLabel lblMarca = lbl(marca, 11, Font.PLAIN, C[7]);
                info.add(lblMarca);
            }
            centro.add(info, BorderLayout.NORTH);

            // Foto centrada
            final BufferedImage[] imgHolder = {null};
            if (p.getFoto() != null && p.getFoto().length > 0) {
                try { imgHolder[0] = ImageIO.read(new ByteArrayInputStream(p.getFoto())); }
                catch (Exception ignored) {}
            }
            final Color bg4 = C[4];
            JPanel fotoPanel = new JPanel(new BorderLayout()) {
                @Override protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (imgHolder[0] != null) {
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
                        int pw = getWidth(), ph2 = getHeight();
                        int iw = imgHolder[0].getWidth(), ih = imgHolder[0].getHeight();
                        double scale = Math.min((double) pw / iw, (double) ph2 / ih);
                        int dw = (int)(iw * scale), dh = (int)(ih * scale);
                        g2.drawImage(imgHolder[0], (pw-dw)/2, (ph2-dh)/2, dw, dh, this);
                    }
                }
            };
            fotoPanel.setBackground(bg4);
            fotoPanel.setPreferredSize(new Dimension(0, 120));
            centro.add(fotoPanel, BorderLayout.CENTER);

            card.add(centro, BorderLayout.CENTER);

            // ── FOOTER: precio | stock · disponible ───────────
            JPanel footer = new JPanel(new BorderLayout());
            footer.setBackground(C[2]);
            footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, C[9]));

            footer.add(lbl(precioStr, 14, Font.BOLD, C[1]), BorderLayout.WEST);

            JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            rightInfo.setBackground(C[2]);
            JLabel lblStk = lbl(stockVal + " uds", 10, Font.PLAIN, C[7]);
            JLabel lblDisp = new JLabel(stockVal > 0 ? "● Disponible" : "● Agotado");
            lblDisp.setFont(new Font("Arial", Font.BOLD, 10));
            lblDisp.setForeground(stockVal > 0 ? new Color(34, 197, 94) : new Color(239, 68, 68));
            rightInfo.add(lblStk);
            rightInfo.add(lblDisp);
            footer.add(rightInfo, BorderLayout.EAST);

            card.add(footer, BorderLayout.SOUTH);
            grid.add(card);
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null); scroll.getViewport().setBackground(C[0]);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        cuerpo.add(scroll, BorderLayout.CENTER);
        contenido.add(cuerpo, BorderLayout.CENTER);
        return contenido;
    }

    private void agregarSeccion(JPanel p, String t) {
        JLabel l = lbl(t, 10, Font.PLAIN, C[11]);
        l.setBorder(BorderFactory.createEmptyBorder(8,0,4,0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE,24)); p.add(l);
    }
    private void agregarSep(JPanel p) {
        JSeparator s = new JSeparator(); s.setForeground(C[3]);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Box.createVerticalStrut(6)); p.add(s); p.add(Box.createVerticalStrut(6));
    }
}