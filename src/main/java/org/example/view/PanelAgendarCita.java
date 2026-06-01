package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.AgendarCitaController;
import org.example.controller.VacunaAdminController;
import org.example.model.Control_vacunas;
import org.example.model.Empleados;
import org.example.model.Mascotas;
import org.example.model.Servicio;
import org.example.model.Vacunas;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class PanelAgendarCita {
    public JPanel panel;

    private final AgendarCitaController ctrl = new AgendarCitaController();

    private final Color[] CLARO = {
            new Color(240, 246, 252), new Color(26,  74,  122), Color.WHITE,
            new Color(42,  90,  138), new Color(230, 240, 250), Color.WHITE,
            new Color(26,  58,   90), new Color(138, 170, 200), new Color(224, 112,  32),
            new Color(208, 228, 244), new Color(15,  53,   96), new Color(122, 175, 212),
            new Color(168, 200, 232), new Color(168, 212, 245),
    };
    private Color[] C = CLARO;

    public PanelAgendarCita() {
        panel = new JPanel(new BorderLayout());
        construir();
    }

    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll();
        C = CLARO;
        panel.setBackground(C[0]);
        panel.add(crearSidebar(),   BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    private JLabel lbl(String t, int sz, int st, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial", st, sz + 2)); l.setForeground(c); return l;
    }
    private JButton btn(String t, Color bg, Color fg, boolean borde) {
        JButton b = new JButton(t); b.setFont(new Font("Arial", Font.PLAIN, 15));
        b.setBackground(bg); b.setForeground(fg); b.setOpaque(true);
        b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        if (borde) b.setBorder(BorderFactory.createLineBorder(fg, 1));
        else b.setBorderPainted(false);
        return b;
    }

    // ── Sidebar ───────────────────────────────────────────
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
            final int idx = i;
            JButton b = btn(mp[i], C[1], C[5], false);
            b.setFont(new Font("Arial", Font.PLAIN, 13));
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (idx == 0) Main.cambiarPantalla("panelCliente");
                    if (idx == 1) Main.cambiarPantalla("misMascotas");
                    if (idx == 2) Main.cambiarPantalla("misCitas");
                    if (idx == 3) Main.cambiarPantalla("historial");
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

    // ── Contenido ─────────────────────────────────────────
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(C[0]);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(C[2]);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C[9]),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));
        JPanel topLeft = new JPanel(new GridLayout(2, 1)); topLeft.setBackground(C[2]);
        topLeft.add(lbl("Agendar cita", 20, Font.BOLD, C[6]));
        topLeft.add(lbl("Completa los datos para reservar tu cita", 12, Font.PLAIN, C[7]));

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setBackground(C[2]);
        JButton btnVolver = btn("← Volver", C[0], C[1], true);
        btnVolver.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("panelCliente"); }
        });
        topRight.add(btnVolver);
        topbar.add(topLeft, BorderLayout.WEST); topbar.add(topRight, BorderLayout.EAST);
        contenido.add(topbar, BorderLayout.NORTH);

        // Formulario
        JPanel outer = new JPanel(new GridBagLayout()); outer.setBackground(C[0]);
        JPanel form = new JPanel(); form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1),
                BorderFactory.createEmptyBorder(32, 36, 32, 36)));

        JLabel tituloForm = lbl("Nueva cita", 18, Font.BOLD, C[6]);
        tituloForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subForm = lbl("Selecciona tu mascota, servicio, fecha y hora", 12, Font.PLAIN, C[7]);
        subForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(tituloForm); form.add(Box.createVerticalStrut(4)); form.add(subForm);
        form.add(Box.createVerticalStrut(24));

        // ── Mascota — solo las del cliente logueado ───────
        agregarCampoLabel(form, "Mascota");
        JComboBox<Mascotas> cbMascota = new JComboBox<>();
        cbMascota.setFont(new Font("Arial", Font.PLAIN, 13));
        cbMascota.setBackground(C[2]); cbMascota.setForeground(C[6]);
        cbMascota.setBorder(BorderFactory.createLineBorder(C[9], 1));
        cbMascota.addItem(null);
        List<Mascotas> todasMascotas = ctrl.listarMascotas();
        for (Mascotas m : todasMascotas) {
            // Solo mostrar mascotas del cliente logueado
            if (Main.clienteActual != null && m.getCliente() != null &&
                    m.getCliente().getId().equals(Main.clienteActual.getId())) {
                cbMascota.addItem(m);
            }
        }
        cbMascota.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Mascotas) setText(((Mascotas) value).getEtiqueta());
                else setText("Selecciona una mascota...");
                return this;
            }
        });
        cbMascota.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbMascota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        form.add(cbMascota); form.add(Box.createVerticalStrut(16));

        // ── Servicio — desde BD ───────────────────────────
        agregarCampoLabel(form, "Tipo de servicio");
        JComboBox<Servicio> cbServicio = new JComboBox<>();
        cbServicio.setFont(new Font("Arial", Font.PLAIN, 13));
        cbServicio.setBackground(C[2]); cbServicio.setForeground(C[6]);
        cbServicio.setBorder(BorderFactory.createLineBorder(C[9], 1));
        cbServicio.addItem(null);
        List<Servicio> servicios = ctrl.listarServicios();
        for (Servicio s : servicios) cbServicio.addItem(s);
        cbServicio.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Servicio) {
                    setText(((Servicio) value).getNombre());
                } else setText("Selecciona un servicio...");
                return this;
            }
        });
        cbServicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbServicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        form.add(cbServicio); form.add(Box.createVerticalStrut(16));

        // ── Panel vacuna (aparece solo si el servicio es vacunacion) ──
        List<Vacunas> listaVacunas = new VacunaAdminController().listarVacunas();
        JPanel panelVacuna = new JPanel();
        panelVacuna.setLayout(new BoxLayout(panelVacuna, BoxLayout.Y_AXIS));
        panelVacuna.setBackground(C[2]);
        panelVacuna.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelVacuna.setVisible(false);

        JLabel lblVacuna = lbl("¿Sabes qué vacuna necesita tu mascota? (opcional)", 12, Font.BOLD, C[6]);
        lblVacuna.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subVacuna = lbl("Si no sabes, el veterinario la asignara al atenderte.", 11, Font.PLAIN, C[7]);
        subVacuna.setAlignmentX(Component.LEFT_ALIGNMENT);
        subVacuna.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JComboBox<Object> cbVacuna = new JComboBox<>();
        cbVacuna.addItem("El veterinario decide");
        for (Vacunas v : listaVacunas) cbVacuna.addItem(v);
        cbVacuna.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vacunas) setText(((Vacunas) value).getNombre());
                return this;
            }
        });
        cbVacuna.setFont(new Font("Arial", Font.PLAIN, 13));
        cbVacuna.setBackground(C[2]); cbVacuna.setForeground(C[6]);
        cbVacuna.setBorder(BorderFactory.createLineBorder(C[9], 1));
        cbVacuna.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbVacuna.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

        panelVacuna.add(lblVacuna);
        panelVacuna.add(Box.createVerticalStrut(4));
        panelVacuna.add(subVacuna);
        panelVacuna.add(cbVacuna);
        form.add(panelVacuna); form.add(Box.createVerticalStrut(8));

        // Mostrar/ocultar segun servicio seleccionado
        cbServicio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object sel = cbServicio.getSelectedItem();
                boolean esVacunacion = sel instanceof Servicio &&
                        ((Servicio) sel).getNombre().toLowerCase().contains("vacun");
                panelVacuna.setVisible(esVacunacion);
                if (!esVacunacion) cbVacuna.setSelectedIndex(0);
                form.revalidate(); form.repaint();
            }
        });

        // ── Fecha con JDateChooser y Hora ─────────────────
        JPanel filaFechaHora = new JPanel(new GridLayout(1, 2, 16, 0));
        filaFechaHora.setBackground(C[2]); filaFechaHora.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaFechaHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel colFecha = new JPanel(); colFecha.setLayout(new BoxLayout(colFecha, BoxLayout.Y_AXIS));
        colFecha.setBackground(C[2]);
        agregarCampoLabel(colFecha, "Fecha");
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 13));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setBorder(BorderFactory.createLineBorder(C[9], 1));
        dateChooser.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        colFecha.add(dateChooser);

        // Bloquear domingos: avisar inmediatamente al seleccionar
        dateChooser.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                java.util.Date selDate = dateChooser.getDate();
                if (selDate != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(selDate);
                    if (cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY) {
                        JOptionPane.showMessageDialog(panel,
                                "Los domingos no están disponibles para citas.\nPor favor selecciona otro día (lunes a sábado).",
                                "Día no disponible", JOptionPane.WARNING_MESSAGE);
                        dateChooser.setDate(null);
                    }
                }
            }
        });

        JPanel colHora = new JPanel(); colHora.setLayout(new BoxLayout(colHora, BoxLayout.Y_AXIS));
        colHora.setBackground(C[2]);
        agregarCampoLabel(colHora, "Hora");
        java.util.List<String> listaHorasAg = new java.util.ArrayList<>();
        listaHorasAg.add("Selecciona hora...");
        for (int h = 7; h < 20; h++) {
            for (int m = 0; m < 60; m += 30) {
                listaHorasAg.add(String.format("%02d:%02d", h, m));
            }
        }
        String[] horas = listaHorasAg.toArray(new String[0]);
        JComboBox<String> cbHora = new JComboBox<>(horas);
        cbHora.setFont(new Font("Arial", Font.PLAIN, 13));
        cbHora.setBackground(C[2]); cbHora.setForeground(C[6]);
        cbHora.setBorder(BorderFactory.createLineBorder(C[9], 1));
        cbHora.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        colHora.add(cbHora);

        filaFechaHora.add(colFecha); filaFechaHora.add(colHora);
        form.add(filaFechaHora); form.add(Box.createVerticalStrut(20));

        // ── Servicio a domicilio ──────────────────────────
        JPanel domicilioTogglePanel = new JPanel(new BorderLayout(10, 0));
        domicilioTogglePanel.setBackground(C[4]);
        domicilioTogglePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        domicilioTogglePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        domicilioTogglePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel domTexto = new JPanel(new GridLayout(2, 1, 0, 2));
        domTexto.setBackground(C[4]);
        domTexto.add(lbl("Traer / Llevar a domicilio", 12, Font.BOLD, C[6]));
        domTexto.add(lbl("El veterinario se desplaza a tu direccion", 10, Font.PLAIN, C[7]));

        JToggleButton togDomicilio = new JToggleButton("No");
        togDomicilio.setFont(new Font("Arial", Font.BOLD, 12));
        togDomicilio.setBackground(C[9]); togDomicilio.setForeground(C[6]);
        togDomicilio.setFocusPainted(false); togDomicilio.setOpaque(true);
        togDomicilio.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        togDomicilio.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));

        domicilioTogglePanel.add(domTexto, BorderLayout.CENTER);
        domicilioTogglePanel.add(togDomicilio, BorderLayout.EAST);
        form.add(domicilioTogglePanel); form.add(Box.createVerticalStrut(6));

        // Campos de dirección (ocultos por defecto — se muestran sin panel wrapper)
        JLabel lblDireccion = lbl("Direccion de domicilio", 12, Font.BOLD, C[6]);
        lblDireccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDireccion.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        lblDireccion.setVisible(false);

        JTextField tfDireccion = new JTextField();
        tfDireccion.setFont(new Font("Arial", Font.PLAIN, 13));
        tfDireccion.setPreferredSize(new Dimension(480, 36));
        tfDireccion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        tfDireccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tfDireccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfDireccion.setVisible(false);

        form.add(lblDireccion);
        form.add(tfDireccion);
        form.add(Box.createVerticalStrut(8));

        togDomicilio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean activo = togDomicilio.isSelected();
                togDomicilio.setText(activo ? "Si" : "No");
                togDomicilio.setBackground(activo ? C[1] : C[9]);
                togDomicilio.setForeground(activo ? C[5] : C[6]);
                lblDireccion.setVisible(activo);
                tfDireccion.setVisible(activo);
                form.revalidate(); form.repaint();
            }
        });

        // ── Botones ───────────────────────────────────────
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        botones.setBackground(C[2]); botones.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCancelar = btn("Cancelar", C[4], C[1], false);
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(10, 22, 10, 22)));
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("panelCliente"); }
        });

        JButton btnConfirmar = btn("Confirmar cita", C[1], C[5], false);
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 13));
        btnConfirmar.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        btnConfirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (cbMascota.getSelectedItem()  == null ||
                        cbServicio.getSelectedItem() == null ||
                        cbHora.getSelectedIndex()    == 0    ||
                        dateChooser.getDate()        == null) {
                    JOptionPane.showMessageDialog(panel,
                            "Por favor completa todos los campos.",
                            "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String fechaStr = new SimpleDateFormat("yyyy-MM-dd").format(dateChooser.getDate());
                LocalDate fechaSel = LocalDate.parse(fechaStr);
                if (fechaSel.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(panel,
                            "La fecha no puede ser en el pasado.",
                            "Fecha invalida", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (fechaSel.getDayOfWeek() == java.time.DayOfWeek.SUNDAY) {
                    JOptionPane.showMessageDialog(panel,
                            "Los domingos no están disponibles para citas.\nPor favor selecciona un día entre lunes y sábado.",
                            "Día no disponible", JOptionPane.WARNING_MESSAGE);
                    dateChooser.setDate(null);
                    return;
                }
                Mascotas mascota = (Mascotas) cbMascota.getSelectedItem();
                String hora = (String) cbHora.getSelectedItem();

                // El veterinario se asigna automáticamente (primer disponible)
                List<Empleados> vets = ctrl.listarVeterinarios();
                if (vets.isEmpty()) {
                    JOptionPane.showMessageDialog(panel,
                            "No hay veterinarios disponibles.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Empleados empleado = vets.get(0);

                String domicilio = togDomicilio.isSelected() ? tfDireccion.getText().trim() : null;
                Servicio servicioSel = (cbServicio.getSelectedItem() instanceof Servicio)
                        ? (Servicio) cbServicio.getSelectedItem() : null;
                String motivoStr = servicioSel != null ? servicioSel.getNombre() : null;
                boolean ok = ctrl.guardarCita(mascota, empleado, fechaStr, hora, domicilio, motivoStr, panel);
                if (ok) {
                    // Si el cliente especifico una vacuna, registrarla como pendiente
                    if (cbVacuna.getSelectedItem() instanceof Vacunas && panelVacuna.isVisible()) {
                        try {
                            Control_vacunas cv = new Control_vacunas();
                            cv.setMascota(mascota);
                            cv.setVacuna((Vacunas) cbVacuna.getSelectedItem());
                            cv.setFechaAplicacion(LocalDate.parse(fechaStr));
                            cv.insertarBD();
                        } catch (Exception ex) {
                            // No bloquear si falla el registro de vacuna
                        }
                    }
                    Main.cambiarPantalla("misCitas");
                }
            }
        });

        botones.add(btnCancelar); botones.add(btnConfirmar);
        form.add(botones);

        form.setPreferredSize(new Dimension(600, form.getPreferredSize().height));
        form.setMaximumSize(new Dimension(700, Integer.MAX_VALUE));
        GridBagConstraints gbcOuter = new GridBagConstraints();
        gbcOuter.fill = GridBagConstraints.BOTH;
        gbcOuter.weightx = 1; gbcOuter.weighty = 1;
        gbcOuter.insets = new Insets(24, 40, 24, 40);
        outer.add(form, gbcOuter);

        JScrollPane scrollOuter = new JScrollPane(outer);
        scrollOuter.setBorder(null); scrollOuter.getViewport().setBackground(C[0]);
        scrollOuter.getVerticalScrollBar().setUnitIncrement(12);
        contenido.add(scrollOuter, BorderLayout.CENTER);
        return contenido;
    }

    private void agregarCampoLabel(JPanel p, String texto) {
        JLabel l = lbl(texto, 12, Font.BOLD, C[6]);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        p.add(l);
    }
    private void agregarSep(JPanel p) {
        JSeparator s = new JSeparator(); s.setForeground(C[3]);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); s.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(Box.createVerticalStrut(6)); p.add(s); p.add(Box.createVerticalStrut(6));
    }
    private void agregarSeccion(JPanel p, String t) {
        JLabel l = lbl(t, 10, Font.PLAIN, C[11]);
        l.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        p.add(l);
    }
}