package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.MascotaAdminController;
import org.example.model.Cliente;
import org.example.model.Especies;
import org.example.model.Mascotas;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class PanelMisMascotas {
    public JPanel panel;
    private boolean temaOscuro = false;

    private final MascotaAdminController ctrl = new MascotaAdminController();

    private final Color[] CLARO = {
            new Color(240,246,252), new Color(26,74,122),   Color.WHITE,
            new Color(42,90,138),   new Color(230,240,250), Color.WHITE,
            new Color(26,58,90),    new Color(138,170,200), new Color(224,112,32),
            new Color(208,228,244), new Color(15,53,96),    new Color(122,175,212),
            new Color(168,200,232), new Color(168,212,245),
    };
    private final Color[] OSCURO = {
            new Color(18,24,38),   new Color(13,18,30),   new Color(26,34,52),
            new Color(37,55,90),   new Color(32,42,64),   Color.WHITE,
            new Color(226,232,240),new Color(100,116,139),new Color(251,146,60),
            new Color(30,41,59),   new Color(9,14,24),    new Color(122,175,212),
            new Color(80,120,170), new Color(100,160,210),
    };
    private Color[] C = CLARO;

    public PanelMisMascotas() {
        panel = new JPanel(new BorderLayout());
        construir();
    }

    public void setTema(boolean oscuro) {
        if (oscuro != temaOscuro) { temaOscuro = oscuro; construir(); }
    }

    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll();
        C = temaOscuro ? OSCURO : CLARO;
        panel.setBackground(C[0]);
        panel.add(crearSidebar(),   BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JLabel lbl(String t, int sz, int st, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial",st,sz + 2)); l.setForeground(c); return l;
    }
    private JButton btn(String t, Color bg, Color fg, boolean borde) {
        JButton b = new JButton(t); b.setFont(new Font("Arial",Font.PLAIN,15));
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
        // Mis mascotas resaltado (idx==1)
        String[] mp = {"Inicio", "Mis mascotas", "Mis citas", "Historial"};
        for (int i = 0; i < mp.length; i++) {
            final int idx = i;
            JButton b = btn(mp[i], i==1?C[2]:C[1], i==1?C[1]:C[5], false);
            b.setFont(new Font("Arial", i==1?Font.BOLD:Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (idx==0) Main.cambiarPantalla("panelCliente");
                    if (idx==2) Main.cambiarPantalla("misCitas");
                    if (idx==3) Main.cambiarPantalla("historial");
                }
            });
            sb.add(b); sb.add(Box.createVerticalStrut(3));
        }
        sb.add(Box.createVerticalStrut(12));

        agregarSeccion(sb, "SERVICIOS");
        String[] ms = {"Alimentos","Vacunas"};
        for (String item : ms) {
            JButton b = btn(item, C[1], C[5], false);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
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
        cerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        cerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JOptionPane.showConfirmDialog(panel,"Deseas cerrar sesion?",
                        "Cerrar sesion",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION) {
                    Main.clienteActual = null;
                    Main.frame.setSize(420,520);
                    Main.frame.setLocationRelativeTo(null);
                    Main.cambiarPantalla("login");
                }
            }
        });
        sb.add(cerrar); sb.add(Box.createVerticalStrut(8));

        String nombreCliente = Main.clienteActual != null ? Main.clienteActual.getNombre() : "Cliente";
        String[] partes = nombreCliente.split(" ");
        String iniciales = partes.length >= 2 ?
                String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0)) :
                String.valueOf(nombreCliente.charAt(0));

        JPanel up = new JPanel(new BorderLayout(8,0));
        up.setBackground(C[10]); up.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        up.setMaximumSize(new Dimension(Integer.MAX_VALUE,66));
        up.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel av = lbl(iniciales,13,Font.BOLD,C[1]); av.setBackground(C[5]); av.setOpaque(true);
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

        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(C[2]);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,C[9]),
                BorderFactory.createEmptyBorder(16,24,16,24)));
        JPanel topLeft = new JPanel(new GridLayout(2,1)); topLeft.setBackground(C[2]);
        topLeft.add(lbl("Mis mascotas",20,Font.BOLD,C[6]));
        topLeft.add(lbl("Administra tus mascotas registradas",12,Font.PLAIN,C[7]));

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        topRight.setBackground(C[2]);
        JButton btnAgregar = btn("+ Agregar mascota", C[1], C[5], false);
        btnAgregar.setFont(new Font("Arial",Font.BOLD,13));
        btnAgregar.setBorder(BorderFactory.createEmptyBorder(9,18,9,18));
        btnAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { mostrarFormularioAgregar(); }
        });

        topRight.add(btnAgregar);
        topbar.add(topLeft,BorderLayout.WEST); topbar.add(topRight,BorderLayout.EAST);
        contenido.add(topbar,BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(0,20));
        cuerpo.setBackground(C[0]);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(24,28,28,28));

        List<Mascotas> todas = ctrl.listarTodas();
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(C[0]);

        boolean hayMascotas = false;
        for (Mascotas m : todas) {
            if (Main.clienteActual != null &&
                    m.getCliente() != null &&
                    m.getCliente().getId().equals(Main.clienteActual.getId())) {

                hayMascotas = true;
                JPanel card = new JPanel(new BorderLayout(12,0));
                card.setBackground(C[2]);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE,80));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0,4,0,0,C[1]),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(C[9],1),
                                BorderFactory.createEmptyBorder(14,18,14,18))));

                String especie  = m.getEspecie()  != null ? m.getEspecie().getNombre() : "—";
                String fechaNac = m.getFechaNac() != null ? m.getFechaNac().toString()  : "—";
                String sexo     = m.getSexo()     != null ? m.getSexo()                : "—";

                JPanel izq = new JPanel(new GridLayout(2,1,0,4));
                izq.setBackground(C[2]);
                izq.add(lbl(m.getNombre(),15,Font.BOLD,C[6]));
                izq.add(lbl(especie + "  ·  Nacimiento: " + fechaNac + "  ·  Sexo: " + sexo,
                        12,Font.PLAIN,C[7]));

                // Botones Editar / Eliminar
                final Integer idMascota = m.getId();
                final Mascotas mascotaRef = m;
                JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
                der.setBackground(C[2]);

                JButton btnEditar = new JButton(" Editar");
                btnEditar.setFont(new Font("Arial", Font.PLAIN, 11));
                btnEditar.setBackground(C[1]);
                btnEditar.setForeground(Color.WHITE);
                btnEditar.setOpaque(true); btnEditar.setBorderPainted(false); btnEditar.setFocusPainted(false);
                btnEditar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
                btnEditar.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        mostrarFormularioEditar(mascotaRef);
                    }
                });

                JButton btnEliminar = new JButton("Eliminar");
                btnEliminar.setFont(new Font("Arial", Font.PLAIN, 11));
                btnEliminar.setBackground(new Color(220,38,38));
                btnEliminar.setForeground(Color.WHITE);
                btnEliminar.setOpaque(true);
                btnEliminar.setBorderPainted(false);
                btnEliminar.setFocusPainted(false);
                btnEliminar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
                btnEliminar.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int confirm = JOptionPane.showConfirmDialog(panel,
                                "¿Eliminar esta mascota? Esta acción no se puede deshacer.",
                                "Confirmar", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            ctrl.eliminarMascota(idMascota, panel);
                            construir();
                        }
                    }
                });
                der.add(btnEditar);
                der.add(btnEliminar);

                card.add(izq,BorderLayout.CENTER);
                card.add(der,BorderLayout.EAST);
                lista.add(card);
                lista.add(Box.createVerticalStrut(10));
            }
        }

        if (!hayMascotas) {
            JPanel sinMascotas = new JPanel(new GridBagLayout());
            sinMascotas.setBackground(C[0]);
            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner,BoxLayout.Y_AXIS));
            inner.setBackground(C[0]);
            JLabel msg = lbl("No tienes mascotas registradas",16,Font.PLAIN,C[7]);
            msg.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel sub = lbl("Haz clic en '+ Agregar mascota' para comenzar",12,Font.PLAIN,C[11]);
            sub.setAlignmentX(Component.CENTER_ALIGNMENT);
            inner.add(msg); inner.add(Box.createVerticalStrut(8)); inner.add(sub);
            sinMascotas.add(inner);
            cuerpo.add(sinMascotas,BorderLayout.CENTER);
        } else {
            JScrollPane scroll = new JScrollPane(lista);
            scroll.setBorder(null); scroll.getViewport().setBackground(C[0]);
            scroll.getVerticalScrollBar().setUnitIncrement(12);
            cuerpo.add(scroll,BorderLayout.CENTER);
        }

        contenido.add(cuerpo,BorderLayout.CENTER);
        return contenido;
    }

    private void mostrarFormularioEditar(Mascotas mascota) {
        JDialog dlg = new JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(panel),
                "Editar mascota", true);
        dlg.setSize(420, 420);
        dlg.setLocationRelativeTo(panel);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(24,28,24,28));

        // Nombre
        form.add(lbl("Nombre de la mascota", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JTextField tfNombre = new JTextField(mascota.getNombre());
        tfNombre.setFont(new Font("Arial",Font.PLAIN,13));
        tfNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        tfNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        form.add(tfNombre); form.add(Box.createVerticalStrut(14));

        // Especie
        form.add(lbl("Especie", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JComboBox<Especies> cbEspecie = new JComboBox<>();
        cbEspecie.setFont(new Font("Arial",Font.PLAIN,13));
        cbEspecie.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        List<Especies> especies = ctrl.listarEspecies();
        for (Especies esp : especies) {
            cbEspecie.addItem(esp);
            if (mascota.getEspecie() != null && esp.getId().equals(mascota.getEspecie().getId()))
                cbEspecie.setSelectedItem(esp);
        }
        cbEspecie.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if (value instanceof Especies) setText(((Especies)value).getNombre());
                return this;
            }
        });
        form.add(cbEspecie); form.add(Box.createVerticalStrut(14));

        // Fecha de nacimiento
        form.add(lbl("Fecha de nacimiento", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        com.toedter.calendar.JDateChooser dateChooser = new com.toedter.calendar.JDateChooser();
        dateChooser.setFont(new Font("Arial",Font.PLAIN,13));
        dateChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setBorder(BorderFactory.createLineBorder(C[9],1));
        if (mascota.getFechaNac() != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(mascota.getFechaNac().getYear(), mascota.getFechaNac().getMonthValue()-1, mascota.getFechaNac().getDayOfMonth());
            dateChooser.setDate(cal.getTime());
        }
        form.add(dateChooser); form.add(Box.createVerticalStrut(14));

        // Sexo
        form.add(lbl("Sexo", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JComboBox<String> cbSexo = new JComboBox<>(new String[]{"Macho","Hembra"});
        cbSexo.setFont(new Font("Arial",Font.PLAIN,13));
        cbSexo.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        if (mascota.getSexo() != null) cbSexo.setSelectedItem(mascota.getSexo());
        form.add(cbSexo); form.add(Box.createVerticalStrut(20));

        // Botones
        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        bots.setBackground(C[2]);
        JButton btnCancel = btn("Cancelar",C[4],C[1],true);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1), BorderFactory.createEmptyBorder(8,16,8,16)));
        btnCancel.addActionListener(e -> dlg.dispose());
        JButton btnGuardar = btn("Guardar cambios",C[1],C[5],false);
        btnGuardar.setFont(new Font("Arial",Font.BOLD,13));
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        btnGuardar.addActionListener(e -> {
            String nuevoNombre = tfNombre.getText().trim();
            Especies nuevaEspecie = (Especies) cbEspecie.getSelectedItem();
            String nuevoSexo = (String) cbSexo.getSelectedItem();
            java.time.LocalDate nuevaFecha = null;
            if (dateChooser.getDate() != null) {
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(dateChooser.getDate());
                nuevaFecha = java.time.LocalDate.of(cal.get(java.util.Calendar.YEAR),
                        cal.get(java.util.Calendar.MONTH)+1, cal.get(java.util.Calendar.DAY_OF_MONTH));
            }
            ctrl.actualizarMascota(mascota, nuevoNombre, nuevaEspecie, nuevaFecha, nuevoSexo, panel);
            dlg.dispose();
            construir();
        });
        bots.add(btnCancel); bots.add(btnGuardar);
        bots.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(bots);

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(null);
        dlg.setContentPane(scroll);
        dlg.setVisible(true);
    }

    private void mostrarFormularioAgregar() {
        if (Main.clienteActual == null) {
            JOptionPane.showMessageDialog(panel,
                    "Debes iniciar sesion para agregar mascotas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(panel),
                "Agregar mascota", true);
        dlg.setSize(420, 400);
        dlg.setLocationRelativeTo(panel);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(24,28,24,28));

        // Nombre
        form.add(lbl("Nombre de la mascota", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JTextField tfNombre = new JTextField();
        tfNombre.setFont(new Font("Arial",Font.PLAIN,13));
        tfNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        tfNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        form.add(tfNombre); form.add(Box.createVerticalStrut(14));

        // Especie — cargada desde BD + opción "Otro..."
        form.add(lbl("Especie", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JComboBox<Object> cbEspecie = new JComboBox<>();
        cbEspecie.setFont(new Font("Arial",Font.PLAIN,13));
        cbEspecie.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        List<Especies> especies = ctrl.listarEspecies();
        for (Especies esp : especies) cbEspecie.addItem(esp);
        cbEspecie.addItem("Otro...");   // sentinel al final
        cbEspecie.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                if (value instanceof Especies) setText(((Especies)value).getNombre());
                // "Otro..." String se muestra tal cual
                return this;
            }
        });
        form.add(cbEspecie); form.add(Box.createVerticalStrut(6));

        // Campo para escribir la especie/raza cuando se elige "Otro..."
        JLabel lOtra = lbl("Cual es la especie/raza?", 12, Font.BOLD, C[6]);
        lOtra.setAlignmentX(Component.LEFT_ALIGNMENT);
        lOtra.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        lOtra.setVisible(false);

        JTextField tfOtraEspecie = new JTextField();
        tfOtraEspecie.setFont(new Font("Arial", Font.PLAIN, 13));
        tfOtraEspecie.setPreferredSize(new Dimension(340, 36));
        tfOtraEspecie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        tfOtraEspecie.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tfOtraEspecie.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfOtraEspecie.setVisible(false);

        form.add(lOtra);
        form.add(tfOtraEspecie);
        form.add(Box.createVerticalStrut(8));

        cbEspecie.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean esOtro = "Otro...".equals(cbEspecie.getSelectedItem());
                lOtra.setVisible(esOtro);
                tfOtraEspecie.setVisible(esOtro);
                dlg.pack();
                dlg.setLocationRelativeTo(panel);
            }
        });

        // Fecha con JDateChooser
        form.add(lbl("Fecha de nacimiento", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(new Font("Arial",Font.PLAIN,13));
        dateChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setBorder(BorderFactory.createLineBorder(C[9],1));
        form.add(dateChooser); form.add(Box.createVerticalStrut(14));

        // Sexo
        form.add(lbl("Sexo", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JComboBox<String> cbSexo = new JComboBox<>(new String[]{"Macho","Hembra"});
        cbSexo.setFont(new Font("Arial",Font.PLAIN,13));
        cbSexo.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        form.add(cbSexo); form.add(Box.createVerticalStrut(14));

        // Característica diferenciadora
        form.add(lbl("Característica (si hay duplicado)", 12, Font.BOLD, C[6]));
        form.add(Box.createVerticalStrut(6));
        JTextField tfCaracteristica = new JTextField();
        tfCaracteristica.setFont(new Font("Arial",Font.PLAIN,13));
        tfCaracteristica.setMaximumSize(new Dimension(Integer.MAX_VALUE,46));
        tfCaracteristica.setToolTipText("Ej: pelaje dorado, collar azul — solo si hay otra mascota con igual nombre y especie");
        final Color bordeNormalCar = C[9];
        final Color bordeErrorCar  = new Color(220,38,38);
        tfCaracteristica.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bordeNormalCar,1),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        JLabel lblCarHint = lbl("<html><i>Solo necesaria si ya existe otra mascota con el mismo nombre y especie.</i></html>", 10, Font.PLAIN, C[7]);
        lblCarHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lblCarError = lbl("", 11, Font.BOLD, new Color(220,38,38));
        lblCarError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblCarError.setVisible(false);
        // Limpiar error al escribir
        tfCaracteristica.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void limpiar() {
                tfCaracteristica.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bordeNormalCar,1), BorderFactory.createEmptyBorder(6,10,6,10)));
                lblCarError.setVisible(false);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
        });
        form.add(tfCaracteristica); form.add(Box.createVerticalStrut(4));
        form.add(lblCarHint); form.add(Box.createVerticalStrut(2));
        form.add(lblCarError); form.add(Box.createVerticalStrut(14));

        // Botones
        JPanel bots = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        bots.setBackground(C[2]);

        JButton btnCancel = btn("Cancelar",C[4],C[1],true);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1),
                BorderFactory.createEmptyBorder(8,16,8,16)));
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dlg.dispose(); }
        });

        JButton btnGuardar = btn("Guardar",C[1],C[5],false);
        btnGuardar.setFont(new Font("Arial",Font.BOLD,13));
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8,16,8,16));
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre   = tfNombre.getText().trim();
                String sexo     = (String) cbSexo.getSelectedItem();
                Cliente cliente = Main.clienteActual;

                // Resolver especie
                Especies especie;
                if ("Otro...".equals(cbEspecie.getSelectedItem())) {
                    String nombreOtra = tfOtraEspecie.getText().trim();
                    if (nombreOtra.isEmpty()) {
                        JOptionPane.showMessageDialog(dlg,
                                "Escribe el nombre de la especie/raza.",
                                "Campo vacío", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    especie = ctrl.obtenerOCrearEspecie(nombreOtra);
                    if (especie == null) {
                        JOptionPane.showMessageDialog(dlg,
                                "No se pudo guardar la especie. Intenta de nuevo.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } else {
                    especie = (Especies) cbEspecie.getSelectedItem();
                }

                // Obtener fecha del calendario
                String fecha = "";
                if (dateChooser.getDate() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    fecha = sdf.format(dateChooser.getDate());
                }

                boolean ok = ctrl.registrarMascota(nombre, especie, cliente, fecha, sexo,
                        tfCaracteristica.getText(), panel,
                        () -> {
                            tfCaracteristica.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(bordeErrorCar, 2),
                                    BorderFactory.createEmptyBorder(6,10,6,10)));
                            lblCarError.setText("⚠ Obligatorio para distinguir esta mascota");
                            lblCarError.setVisible(true);
                            tfCaracteristica.requestFocusInWindow();
                            form.revalidate();
                        });
                if (ok) {
                    dlg.dispose();
                    construir();
                }
            }
        });

        bots.add(btnCancel); bots.add(btnGuardar);
        bots.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(bots);

        dlg.add(form);
        dlg.setVisible(true);
    }

    private void agregarSep(JPanel p) {
        JSeparator s = new JSeparator(); s.setForeground(C[3]);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Box.createVerticalStrut(6)); p.add(s); p.add(Box.createVerticalStrut(6));
    }
    private void agregarSeccion(JPanel p, String t) {
        JLabel l = lbl(t,10,Font.PLAIN,C[11]);
        l.setBorder(BorderFactory.createEmptyBorder(8,0,4,0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE,24)); p.add(l);
    }
}