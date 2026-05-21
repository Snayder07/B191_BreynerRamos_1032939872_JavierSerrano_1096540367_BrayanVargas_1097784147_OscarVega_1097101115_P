package org.example.view;

import org.example.controller.InventarioController;
import org.example.model.Productos;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

public class PanelAdminInventario {
    public JPanel panel;
    private boolean temaOscuro = false;

    private final InventarioController ctrl = new InventarioController();

    private final Color[] CLARO = {
            new Color(240,253,244),new Color(22,101,52),Color.WHITE,new Color(34,120,70),
            new Color(220,245,230),Color.WHITE,new Color(15,60,30),new Color(100,130,110),
            new Color(234,88,12),new Color(187,224,200),new Color(15,60,30),new Color(134,190,155),
            new Color(220,38,38),new Color(22,163,74),new Color(210,240,220),
    };
    private final Color[] OSCURO = {
            new Color(18,24,38),new Color(13,18,30),new Color(26,34,52),new Color(37,55,90),
            new Color(32,42,64),Color.WHITE,new Color(226,232,240),new Color(148,163,184),
            new Color(251,146,60),new Color(30,41,59),new Color(9,14,24),new Color(122,175,212),
            new Color(239,68,68),new Color(34,197,94),new Color(15,23,42),
    };
    private Color[] C = CLARO;

    public PanelAdminInventario() { panel = new JPanel(new BorderLayout()); construir(); }
    public void setTema(boolean o) { if(o!=temaOscuro){temaOscuro=o;construir();} }
    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll(); C = temaOscuro ? OSCURO : CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, temaOscuro, "adminInventario", panel), BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JLabel lbl(String t,int sz,int st,Color c){JLabel l=new JLabel(t);l.setFont(new Font("Arial",st,sz+2));l.setForeground(c);return l;}

    private JPanel crearContenido() {
        List<Productos> lista = ctrl.listarTodos();
        long total     = lista.size();
        long stockBajo = lista.stream().filter(p -> p.getStock() != null && p.getStock() < 10).count();
        long tipos     = lista.stream().map(Productos::getTipo).filter(Objects::nonNull).distinct().count();
        long sinStock  = lista.stream().filter(p -> p.getStock() != null && p.getStock() == 0).count();

        JPanel c = new JPanel(new BorderLayout()); c.setBackground(C[0]);

        // ── Topbar ────────────────────────────────────────
        JPanel tb = new JPanel(new BorderLayout()); tb.setBackground(C[2]);
        tb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,C[9]),
                BorderFactory.createEmptyBorder(16,28,16,28)));
        JPanel tl = new JPanel(new GridLayout(2,1)); tl.setBackground(C[2]);
        tl.add(lbl("Inventario",22,Font.BOLD,C[6]));
        tl.add(lbl("Medicamentos, vacunas y productos disponibles",12,Font.PLAIN,C[7]));
        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); tr.setBackground(C[2]);
        JButton btnAgregar = new JButton("+ Agregar producto");
        btnAgregar.setFont(new Font("Arial",Font.BOLD,13));
        btnAgregar.setBackground(new Color(22,163,74)); btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setOpaque(true); btnAgregar.setBorderPainted(false);
        btnAgregar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.setBorder(BorderFactory.createEmptyBorder(9,18,9,18));
        btnAgregar.addActionListener(e -> mostrarFormAgregar());
        tr.add(btnAgregar);
        tb.add(tl,BorderLayout.WEST); tb.add(tr,BorderLayout.EAST);
        c.add(tb,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0,20));
        body.setBackground(C[0]);
        body.setBorder(BorderFactory.createEmptyBorder(24,28,28,28));

        // ── Stats ─────────────────────────────────────────
        JPanel stats = new JPanel(new GridLayout(1,4,16,0)); stats.setBackground(C[0]);
        Object[][] st = {
                {"Total productos",  String.valueOf(total),     C[1]},
                {"Stock bajo (<10)", String.valueOf(stockBajo), C[12]},
                {"Sin stock",        String.valueOf(sinStock),  C[8]},
                {"Categorias",       String.valueOf(tipos),     C[1]},
        };
        for (Object[] s : st) {
            JPanel card = new JPanel(new BorderLayout(0,4)); card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C[9],1),
                    BorderFactory.createEmptyBorder(16,20,16,20)));
            card.add(lbl((String)s[0],11,Font.PLAIN,C[7]),BorderLayout.NORTH);
            card.add(lbl((String)s[1],28,Font.BOLD,(Color)s[2]),BorderLayout.CENTER);
            stats.add(card);
        }
        body.add(stats,BorderLayout.NORTH);

        // ── Cuadrícula de cajas (diseño) ─────────────────────
        Color[] paleta = {
            new Color(59, 130, 246),  // azul
            new Color(168, 85, 247),  // violeta
            new Color(34, 197, 94),   // verde
            new Color(20, 184, 166),  // teal
            new Color(251, 146, 60),  // naranja
            new Color(239, 68, 68),   // rojo
        };
        int totalCards = Math.max(lista.size(), 6);
        int gridCols = 3;
        int gridRows = (totalCards + gridCols - 1) / gridCols;
        JPanel grid = new JPanel(new GridLayout(gridRows, gridCols, 16, 16));
        grid.setBackground(C[0]);
        for (int idx = 0; idx < totalCards; idx++) {
            Color color = paleta[idx % paleta.length];
            JPanel card = new JPanel(new BorderLayout(0, 0));
            card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(C[9], 1),
                            BorderFactory.createEmptyBorder(14, 14, 14, 14))));
            if (idx < lista.size()) {
                // botones editar/eliminar en el card real
                Productos p = lista.get(idx);
                JPanel footer = new JPanel(new GridLayout(1, 2, 8, 0));
                footer.setBackground(C[2]);
                footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
                JButton btnEdit = new JButton("Editar");
                btnEdit.setFont(new Font("Arial", Font.BOLD, 11));
                btnEdit.setBackground(new Color(37, 99, 235)); btnEdit.setForeground(Color.WHITE);
                btnEdit.setOpaque(true); btnEdit.setBorderPainted(false);
                btnEdit.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
                btnEdit.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
                btnEdit.addActionListener(e -> mostrarFormEditar(p));
                JButton btnDel = new JButton("Eliminar");
                btnDel.setFont(new Font("Arial", Font.BOLD, 11));
                btnDel.setBackground(new Color(220, 38, 38)); btnDel.setForeground(Color.WHITE);
                btnDel.setOpaque(true); btnDel.setBorderPainted(false);
                btnDel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
                btnDel.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
                btnDel.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(panel,
                            "¿Eliminar \"" + p.getNombre() + "\" del inventario?",
                            "Confirmar eliminacion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) { ctrl.eliminarProducto(p.getId(), panel); construir(); }
                });
                footer.add(btnEdit); footer.add(btnDel);
                card.add(footer, BorderLayout.SOUTH);
            }
            JPanel placeholder = new JPanel(new BorderLayout());
            placeholder.setBackground(C[4]);
            placeholder.setPreferredSize(new Dimension(0, 130));
            card.add(placeholder, BorderLayout.CENTER);
            grid.add(card);
        }

        JScrollPane gridScroll = new JScrollPane(grid);
        gridScroll.setBorder(null);
        gridScroll.getViewport().setBackground(C[0]);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        body.add(gridScroll, BorderLayout.CENTER);

        JScrollPane outerScroll = new JScrollPane(body);
        outerScroll.setBorder(null); outerScroll.getViewport().setBackground(C[0]);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(outerScroll, BorderLayout.CENTER);
        return c;
    }

    // ── Formulario agregar producto ───────────────────────
    private void mostrarFormAgregar() {
        JDialog dlg = new JDialog();
        dlg.setTitle("Agregar producto");
        dlg.setModal(true);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(panel);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(240,253,244));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        Color verde  = new Color(22,101,52);
        Color gris   = new Color(100,116,139);
        Color borde  = new Color(187,224,200);

        // Titulo
        JLabel tit = new JLabel("Nuevo producto");
        tit.setFont(new Font("Arial",Font.BOLD,16)); tit.setForeground(verde);
        tit.setAlignmentX(Component.LEFT_ALIGNMENT); root.add(tit);
        root.add(Box.createVerticalStrut(16));

        // Campos
        JTextField tfNombre = tf(); JTextField tfTipo   = tf();
        JTextField tfMarca  = tf(); JTextField tfPrecio = tf();
        JTextField tfStock  = tf();

        root.add(campo(root, "Nombre *",   tfNombre, gris, borde));
        root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Tipo",       tfTipo,   gris, borde));
        root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Marca",      tfMarca,  gris, borde));
        root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Precio *",   tfPrecio, gris, borde));
        root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Stock *",    tfStock,  gris, borde));
        root.add(Box.createVerticalStrut(14));

        // ── Selector de foto ──────────────────────────────
        JLabel lblFoto = new JLabel("Foto del producto (opcional)");
        lblFoto.setFont(new Font("Arial",Font.PLAIN,12));
        lblFoto.setForeground(gris); lblFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(lblFoto); root.add(Box.createVerticalStrut(6));

        JPanel fotoPanel = new JPanel(new BorderLayout(10,0));
        fotoPanel.setBackground(new Color(240,253,244));
        fotoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fotoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel previewFoto = new JLabel("Sin foto");
        previewFoto.setPreferredSize(new Dimension(70, 70));
        previewFoto.setHorizontalAlignment(SwingConstants.CENTER);
        previewFoto.setFont(new Font("Arial",Font.PLAIN,10));
        previewFoto.setForeground(gris);
        previewFoto.setOpaque(true);
        previewFoto.setBackground(Color.WHITE);
        previewFoto.setBorder(BorderFactory.createLineBorder(borde,1));

        final byte[][] fotoBytes = {null};

        JButton btnFoto = new JButton("Seleccionar imagen");
        btnFoto.setFont(new Font("Arial",Font.PLAIN,12));
        btnFoto.setBackground(Color.WHITE); btnFoto.setForeground(verde);
        btnFoto.setBorder(BorderFactory.createLineBorder(verde,1));
        btnFoto.setFocusPainted(false);
        btnFoto.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Seleccionar foto del producto");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagenes (jpg, png, gif)", "jpg","jpeg","png","gif","bmp"));
            if (fc.showOpenDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    fotoBytes[0] = Files.readAllBytes(f.toPath());
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) {
                        Image scaled = img.getScaledInstance(66, 66, Image.SCALE_SMOOTH);
                        previewFoto.setIcon(new ImageIcon(scaled));
                        previewFoto.setText("");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg, "No se pudo cargar la imagen.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel fotoDer = new JPanel();
        fotoDer.setLayout(new BoxLayout(fotoDer, BoxLayout.Y_AXIS));
        fotoDer.setBackground(new Color(240,253,244));
        fotoDer.add(btnFoto);
        fotoDer.add(Box.createVerticalStrut(6));
        JLabel hint = new JLabel("JPG, PNG, GIF (max 5MB)");
        hint.setFont(new Font("Arial",Font.PLAIN,10)); hint.setForeground(gris);
        fotoDer.add(hint);

        fotoPanel.add(previewFoto, BorderLayout.WEST);
        fotoPanel.add(fotoDer, BorderLayout.CENTER);
        root.add(fotoPanel);
        root.add(Box.createVerticalStrut(20));

        // ── Botones ───────────────────────────────────────
        JPanel bots = new JPanel(new GridLayout(1,2,10,0));
        bots.setBackground(new Color(240,253,244));
        bots.setAlignmentX(Component.LEFT_ALIGNMENT);
        bots.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial",Font.PLAIN,13));
        btnCancelar.setBackground(Color.WHITE); btnCancelar.setForeground(verde);
        btnCancelar.setBorder(BorderFactory.createLineBorder(verde,1));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setFont(new Font("Arial",Font.BOLD,13));
        btnGuardar.setBackground(verde); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true); btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> {
            boolean ok = ctrl.agregarProducto(
                    tfNombre.getText(), tfTipo.getText(),
                    tfMarca.getText(), tfPrecio.getText(),
                    tfStock.getText(), fotoBytes[0], panel);
            if (ok) { dlg.dispose(); recargar(); }
        });

        bots.add(btnCancelar); bots.add(btnGuardar);
        root.add(bots);

        dlg.add(root);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(380, dlg.getHeight()));
        dlg.setLocationRelativeTo(panel);
        dlg.setVisible(true);
    }

    private JPanel campo(JPanel root, String label, JTextField tf, Color gris, Color borde) {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(240,253,244)); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial",Font.PLAIN,12)); l.setForeground(gris);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(3)); p.add(tf);
        return p;
    }

    private JTextField tf() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial",Font.PLAIN,13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187,224,200),1),
                BorderFactory.createEmptyBorder(4,10,4,10)));
        return tf;
    }

    // ── Formulario editar producto ────────────────────────
    private void mostrarFormEditar(Productos prod) {
        JDialog dlg = new JDialog();
        dlg.setTitle("Editar producto");
        dlg.setModal(true);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(panel);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(new Color(240,253,244));
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        Color verde = new Color(22,101,52);
        Color gris  = new Color(100,116,139);
        Color borde = new Color(187,224,200);

        JLabel tit = new JLabel("Editar: " + prod.getNombre());
        tit.setFont(new Font("Arial",Font.BOLD,16)); tit.setForeground(verde);
        tit.setAlignmentX(Component.LEFT_ALIGNMENT); root.add(tit);
        root.add(Box.createVerticalStrut(16));

        JTextField tfNombre = tf(); tfNombre.setText(prod.getNombre() != null ? prod.getNombre() : "");
        JTextField tfTipo   = tf(); tfTipo.setText(prod.getTipo()    != null ? prod.getTipo()    : "");
        JTextField tfMarca  = tf(); tfMarca.setText(prod.getMarca()  != null ? prod.getMarca()   : "");
        JTextField tfPrecio = tf(); tfPrecio.setText(prod.getPrecio()!= null ? prod.getPrecio().toPlainString() : "");
        JTextField tfStock  = tf(); tfStock.setText(prod.getStock()  != null ? String.valueOf(prod.getStock()) : "");

        root.add(campo(root, "Nombre *", tfNombre, gris, borde)); root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Tipo",     tfTipo,   gris, borde)); root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Marca",    tfMarca,  gris, borde)); root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Precio *", tfPrecio, gris, borde)); root.add(Box.createVerticalStrut(8));
        root.add(campo(root, "Stock *",  tfStock,  gris, borde)); root.add(Box.createVerticalStrut(14));

        // Foto
        JLabel lblFoto = new JLabel("Foto del producto (dejar vacío para conservar la actual)");
        lblFoto.setFont(new Font("Arial",Font.PLAIN,12));
        lblFoto.setForeground(gris); lblFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(lblFoto); root.add(Box.createVerticalStrut(6));

        JPanel fotoPanel = new JPanel(new BorderLayout(10,0));
        fotoPanel.setBackground(new Color(240,253,244));
        fotoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fotoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel previewFoto = new JLabel();
        previewFoto.setPreferredSize(new Dimension(70,70));
        previewFoto.setHorizontalAlignment(SwingConstants.CENTER);
        previewFoto.setOpaque(true);
        previewFoto.setBackground(Color.WHITE);
        previewFoto.setBorder(BorderFactory.createLineBorder(borde,1));

        // Mostrar foto actual si existe
        final byte[][] fotoBytes = {prod.getFoto()};
        if (prod.getFoto() != null && prod.getFoto().length > 0) {
            try {
                BufferedImage img = ImageIO.read(new ByteArrayInputStream(prod.getFoto()));
                if (img != null) {
                    previewFoto.setIcon(new ImageIcon(img.getScaledInstance(66,66,Image.SCALE_SMOOTH)));
                }
            } catch (Exception ignored) {}
        } else {
            previewFoto.setText("Sin foto");
            previewFoto.setFont(new Font("Arial",Font.PLAIN,10));
            previewFoto.setForeground(gris);
        }

        JButton btnFoto = new JButton("Cambiar imagen");
        btnFoto.setFont(new Font("Arial",Font.PLAIN,12));
        btnFoto.setBackground(Color.WHITE); btnFoto.setForeground(verde);
        btnFoto.setBorder(BorderFactory.createLineBorder(verde,1));
        btnFoto.setFocusPainted(false);
        btnFoto.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Seleccionar foto");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagenes (jpg, png, gif)", "jpg","jpeg","png","gif","bmp"));
            if (fc.showOpenDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                try {
                    fotoBytes[0] = Files.readAllBytes(fc.getSelectedFile().toPath());
                    BufferedImage img = ImageIO.read(fc.getSelectedFile());
                    if (img != null) {
                        previewFoto.setIcon(new ImageIcon(img.getScaledInstance(66,66,Image.SCALE_SMOOTH)));
                        previewFoto.setText("");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg,"No se pudo cargar la imagen.","Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel fotoDer = new JPanel();
        fotoDer.setLayout(new BoxLayout(fotoDer, BoxLayout.Y_AXIS));
        fotoDer.setBackground(new Color(240,253,244));
        fotoDer.add(btnFoto);
        fotoDer.add(Box.createVerticalStrut(4));
        JLabel hint = new JLabel("JPG, PNG, GIF — conserva la actual si no cambias");
        hint.setFont(new Font("Arial",Font.PLAIN,10)); hint.setForeground(gris);
        fotoDer.add(hint);

        fotoPanel.add(previewFoto, BorderLayout.WEST);
        fotoPanel.add(fotoDer,     BorderLayout.CENTER);
        root.add(fotoPanel);
        root.add(Box.createVerticalStrut(20));

        JPanel bots = new JPanel(new GridLayout(1,2,10,0));
        bots.setBackground(new Color(240,253,244));
        bots.setAlignmentX(Component.LEFT_ALIGNMENT);
        bots.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial",Font.PLAIN,13));
        btnCancelar.setBackground(Color.WHITE); btnCancelar.setForeground(verde);
        btnCancelar.setBorder(BorderFactory.createLineBorder(verde,1));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setFont(new Font("Arial",Font.BOLD,13));
        btnGuardar.setBackground(verde); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true); btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = tfNombre.getText().trim();
                if (nombre.isEmpty()) { JOptionPane.showMessageDialog(dlg,"El nombre es obligatorio.","Error",JOptionPane.ERROR_MESSAGE); return; }
                prod.setNombre(nombre);
                prod.setTipo(tfTipo.getText().trim().isEmpty()  ? null : tfTipo.getText().trim());
                prod.setMarca(tfMarca.getText().trim().isEmpty() ? null : tfMarca.getText().trim());
                prod.setPrecio(new java.math.BigDecimal(tfPrecio.getText().trim()));
                prod.setStock(Integer.parseInt(tfStock.getText().trim()));
                prod.setFoto(fotoBytes[0]);
                boolean ok = ctrl.actualizarProducto(prod, panel);
                if (ok) { dlg.dispose(); recargar(); }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg,"Precio y stock deben ser números válidos.","Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        bots.add(btnCancelar); bots.add(btnGuardar);
        root.add(bots);

        dlg.add(root);
        dlg.pack();
        dlg.setMinimumSize(new Dimension(400, dlg.getHeight()));
        dlg.setLocationRelativeTo(panel);
        dlg.setVisible(true);
    }

    private void estilizarTema(JButton b){
        b.setFont(new Font("Arial",Font.PLAIN,13)); b.setBackground(C[2]); b.setForeground(C[6]);
        b.setOpaque(true); b.setFocusPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(7,14,7,14)));
    }
}
