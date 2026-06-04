package org.example.view;

import org.example.service.CitaService;
import org.example.model.Citas;
import org.example.model.Cliente;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PanelCliente {
    public JPanel panel;

    private List<Citas> cachedCitas = null;

    private final Color[] CLARO = {
            new Color(240, 246, 252), new Color(26,  74,  122), Color.WHITE,
            new Color(42,  90,  138), new Color(230, 240, 250), Color.WHITE,
            new Color(26,  58,   90), new Color(138, 170, 200), new Color(224, 112,  32),
            new Color(208, 228, 244), new Color(15,  53,   96), new Color(122, 175, 212),
            new Color(168, 200, 232), new Color(168, 212, 245),
    };
    private Color[] C = CLARO;

    public PanelCliente() {
        panel = new JPanel(new BorderLayout());
        construir();
    }

    public void recargar() { cachedCitas = null; construir(); }

    private void construir() {
        panel.removeAll();
        C = CLARO;
        panel.setBackground(C[0]);
        panel.add(crearSidebar(), BorderLayout.WEST);

        try {
            if (Main.clienteActual != null)
                cachedCitas = Citas.consultarPorClienteBD(Main.clienteActual.getId());
            else
                cachedCitas = Collections.emptyList();
        } catch (Exception e) {
            cachedCitas = Collections.emptyList();
        }
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JButton crearBoton(String texto, Color fondo, Color textColor, boolean borde) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.PLAIN, 15));
        btn.setBackground(fondo); btn.setForeground(textColor);
        btn.setOpaque(true); btn.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        if (borde) btn.setBorder(BorderFactory.createLineBorder(textColor, 1));
        else btn.setBorderPainted(false);
        return btn;
    }

    private JLabel crearLabel(String texto, int size, int style, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", style, size + 2));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(C[1]);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // ── Logo ─────────────────────────────────────────
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    URL imgUrl = PanelCliente.class.getClassLoader().getResource("logo_cliente.png");
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
        sidebar.add(logoPanel);

        JSeparator sep = new JSeparator();
        sep.setForeground(C[3]); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(sep); sidebar.add(Box.createVerticalStrut(12));

        JLabel secPrincipal = crearLabel("PRINCIPAL", 10, Font.PLAIN, C[11]);
        secPrincipal.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(secPrincipal); sidebar.add(Box.createVerticalStrut(5));

        // Inicio — resaltado
        JButton btnInicio = crearBoton("Inicio", C[2], C[1], false);
        btnInicio.setFont(new Font("Arial", Font.BOLD, 13));
        btnInicio.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnInicio.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnInicio.setHorizontalAlignment(SwingConstants.LEFT);
        btnInicio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("panelCliente"); }
        });
        sidebar.add(btnInicio); sidebar.add(Box.createVerticalStrut(3));

        // Mis mascotas
        JButton btnMisMascotas = crearBoton("Mis mascotas", C[1], C[5], false);
        btnMisMascotas.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMisMascotas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnMisMascotas.setHorizontalAlignment(SwingConstants.LEFT);
        btnMisMascotas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("misMascotas"); }
        });
        sidebar.add(btnMisMascotas); sidebar.add(Box.createVerticalStrut(3));

        // Mis citas
        JButton btnMisCitas = crearBoton("Mis citas", C[1], C[5], false);
        btnMisCitas.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnMisCitas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnMisCitas.setHorizontalAlignment(SwingConstants.LEFT);
        btnMisCitas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("misCitas"); }
        });
        sidebar.add(btnMisCitas); sidebar.add(Box.createVerticalStrut(3));

        // Historial
        JButton btnHistorial = crearBoton("Historial", C[1], C[5], false);
        btnHistorial.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnHistorial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnHistorial.setHorizontalAlignment(SwingConstants.LEFT);
        btnHistorial.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("historial"); }
        });
        sidebar.add(btnHistorial); sidebar.add(Box.createVerticalStrut(12));

        JLabel secServicios = crearLabel("SERVICIOS", 10, Font.PLAIN, C[11]);
        secServicios.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(secServicios); sidebar.add(Box.createVerticalStrut(5));

        JButton btnAlimentos = crearBoton("Alimentos", C[1], C[5], false);
        btnAlimentos.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAlimentos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnAlimentos.setHorizontalAlignment(SwingConstants.LEFT);
        btnAlimentos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("alimentos"); }
        });
        sidebar.add(btnAlimentos); sidebar.add(Box.createVerticalStrut(3));

        JButton btnVacunas = crearBoton("Vacunas", C[1], C[5], false);
        btnVacunas.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnVacunas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnVacunas.setHorizontalAlignment(SwingConstants.LEFT);
        btnVacunas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("vacunas"); }
        });
        sidebar.add(btnVacunas); sidebar.add(Box.createVerticalStrut(3));
        sidebar.add(Box.createVerticalGlue());

        JButton btnCerrar = crearBoton("Cerrar sesion", C[1], C[12], true);
        btnCerrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCerrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnCerrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int conf = JOptionPane.showConfirmDialog(panel,
                        "Deseas cerrar sesion?", "Cerrar sesion", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    Main.clienteActual = null;
                    Main.frame.setExtendedState(JFrame.NORMAL);
                    Main.frame.setSize(420, 520);
                    Main.frame.setLocationRelativeTo(null);
                    Main.cambiarPantalla("login");
                }
            }
        });
        sidebar.add(btnCerrar); sidebar.add(Box.createVerticalStrut(8));

        String nombreCliente = Main.clienteActual != null ? Main.clienteActual.getNombre() : "Cliente";
        String[] partes = nombreCliente.split(" ");
        String iniciales = partes.length >= 2 ?
                String.valueOf(partes[0].charAt(0)) + String.valueOf(partes[1].charAt(0)) : "C";

        JPanel userPanel = new JPanel(new BorderLayout(8, 0));
        userPanel.setBackground(C[10]);
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        userPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel avatarLbl = crearLabel(iniciales, 13, Font.BOLD, C[1]);
        avatarLbl.setBackground(C[5]); avatarLbl.setOpaque(true);
        avatarLbl.setPreferredSize(new Dimension(40, 40));
        avatarLbl.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel userInfo = new JPanel(new GridLayout(2, 1));
        userInfo.setBackground(C[10]);
        userInfo.add(crearLabel(nombreCliente, 12, Font.BOLD, C[5]));
        userInfo.add(crearLabel("Cliente", 10, Font.PLAIN, C[11]));
        userPanel.add(avatarLbl, BorderLayout.WEST);
        userPanel.add(userInfo,  BorderLayout.CENTER);
        sidebar.add(userPanel);
        return sidebar;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(C[0]);

        // Topbar
        JPanel topbar = new JPanel(new BorderLayout());
        topbar.setBackground(C[2]);
        topbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C[9]),
                BorderFactory.createEmptyBorder(16, 24, 16, 24)));

        JPanel topLeft = new JPanel(new GridLayout(2, 1));
        topLeft.setBackground(C[2]);
        String nombreCliente = Main.clienteActual != null ? Main.clienteActual.getNombre() : "Cliente";
        String fechaHoy = LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", new Locale("es")));
        fechaHoy = Character.toUpperCase(fechaHoy.charAt(0)) + fechaHoy.substring(1);
        topLeft.add(crearLabel("Bienvenido, " + nombreCliente.split(" ")[0], 20, Font.PLAIN, C[6]));
        topLeft.add(crearLabel(fechaHoy, 12, Font.PLAIN, C[7]));

        JPanel topRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topRight.setBackground(C[2]);

        // ── Botón Mis mascotas ────────────────────────────
        JButton btnMascotas = crearBoton("Mis mascotas", C[4], C[1], true);
        btnMascotas.setFont(new Font("Arial", Font.PLAIN, 13));
        btnMascotas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnMascotas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("misMascotas"); }
        });

        // ── Botón Agendar cita ────────────────────────────
        JButton btnAgendar = crearBoton("+ Agendar cita", C[1], C[5], false);
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 13));
        btnAgendar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnAgendar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("agendarCita"); }
        });

        JButton btnEditar = crearBoton("Editar perfil", C[4], C[1], true);
        btnEditar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnEditar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnEditar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { mostrarFormEditarPerfil(); }
        });

        topRight.add(btnEditar); topRight.add(btnMascotas); topRight.add(btnAgendar);
        topbar.add(topLeft, BorderLayout.WEST); topbar.add(topRight, BorderLayout.EAST);
        contenido.add(topbar, BorderLayout.NORTH);

        // Centro
        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBackground(C[0]);
        centro.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        // Próxima cita
        List<Citas> citas = cachedCitas != null ? cachedCitas : Collections.emptyList();
        JPanel proximaCard = new JPanel(new BorderLayout());
        proximaCard.setBackground(C[1]);
        proximaCard.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel proximaIzq = new JPanel(new GridLayout(3, 1));
        proximaIzq.setBackground(C[1]);

        if (!citas.isEmpty()) {
            Citas primera = citas.get(0);
            String mascota = primera.getMascota()  != null ? primera.getMascota().getNombre()  : "—";
            String vet     = primera.getEmpleado() != null ? primera.getEmpleado().getNombre() : "—";
            String fecha   = primera.getFechaCita() != null ? primera.getFechaCita().toString() : "—";
            String hora    = primera.getHoraCita()  != null ? primera.getHoraCita().toString()  : "—";
            proximaIzq.add(crearLabel("PROXIMA CITA", 10, Font.PLAIN, C[13]));
            proximaIzq.add(crearLabel(mascota, 17, Font.BOLD, C[5]));
            proximaIzq.add(crearLabel(vet, 12, Font.PLAIN, C[12]));
            JPanel proximaDer = new JPanel(new GridLayout(2, 1));
            proximaDer.setBackground(C[1]);
            JLabel lFecha = crearLabel(fecha, 12, Font.BOLD, C[5]);
            lFecha.setHorizontalAlignment(SwingConstants.RIGHT);
            JLabel lHora = crearLabel(hora, 12, Font.PLAIN, C[13]);
            lHora.setHorizontalAlignment(SwingConstants.RIGHT);
            proximaDer.add(lFecha); proximaDer.add(lHora);
            proximaCard.add(proximaIzq, BorderLayout.CENTER);
            proximaCard.add(proximaDer, BorderLayout.EAST);
        } else {
            proximaIzq.add(crearLabel("PROXIMA CITA", 10, Font.PLAIN, C[13]));
            proximaIzq.add(crearLabel("No tienes citas programadas", 15, Font.BOLD, C[5]));
            proximaIzq.add(crearLabel("Agenda tu primera cita", 12, Font.PLAIN, C[12]));
            proximaCard.add(proximaIzq, BorderLayout.CENTER);
        }

        // Lista citas
        JPanel citasPanel = new JPanel(new BorderLayout(0, 10));
        citasPanel.setBackground(C[0]);
        JPanel citasHeader = new JPanel(new BorderLayout());
        citasHeader.setBackground(C[0]);
        citasHeader.add(crearLabel("MIS CITAS", 11, Font.PLAIN, C[7]), BorderLayout.WEST);
        JButton verTodo = crearBoton("Ver todas", C[0], C[1], false);
        verTodo.setFont(new Font("Arial", Font.BOLD, 12));
        verTodo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("misCitas"); }
        });
        citasHeader.add(verTodo, BorderLayout.EAST);
        citasPanel.add(citasHeader, BorderLayout.NORTH);

        int max = Math.min(citas.size(), 3);
        JPanel listaCitas = new JPanel();
        listaCitas.setLayout(new BoxLayout(listaCitas, BoxLayout.Y_AXIS));
        listaCitas.setBackground(C[0]);
        listaCitas.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        if (citas.isEmpty()) {
            JLabel sinCitas = crearLabel("No tienes citas registradas aún.", 13, Font.PLAIN, C[7]);
            sinCitas.setHorizontalAlignment(SwingConstants.CENTER);
            sinCitas.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaCitas.add(sinCitas);
        } else {
            for (int i = 0; i < max; i++) {
                Citas c = citas.get(i);
                String mascota = c.getMascota()  != null ? c.getMascota().getNombre()  : "—";
                String vet     = c.getEmpleado() != null ? c.getEmpleado().getNombre() : "—";
                String fecha   = c.getFechaCita() != null ? c.getFechaCita().toString() : "—";
                String estado  = c.getEstadoCita() != null ? c.getEstadoCita().toString() : "—";

                Color colorEstado = C[1];
                if (c.getEstadoCita() != null) {
                    switch (c.getEstadoCita()) {
                        case CONFIRMADA: colorEstado = new Color(22, 163, 74); break;
                        case PENDIENTE:  colorEstado = new Color(234, 88, 12); break;
                        case CANCELADA:  colorEstado = new Color(220, 38, 38); break;
                        default: colorEstado = C[1];
                    }
                }

                JPanel card = new JPanel(new BorderLayout(10, 0));
                card.setBackground(C[2]);
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
                card.setAlignmentX(Component.LEFT_ALIGNMENT);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, colorEstado),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(C[9], 1),
                                BorderFactory.createEmptyBorder(14, 16, 14, 16))));

                JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
                info.setBackground(C[2]);
                info.add(crearLabel(mascota, 14, Font.BOLD, C[6]));
                info.add(crearLabel(fecha + "  ·  " + vet, 12, Font.PLAIN, C[7]));

                JPanel derecha = new JPanel(new GridLayout(2, 1, 0, 6));
                derecha.setBackground(C[2]);
                JLabel badge = crearLabel(estado, 11, Font.BOLD, colorEstado);
                badge.setHorizontalAlignment(SwingConstants.RIGHT);
                final Integer idCita = c.getId();
                JLabel cancelar = crearLabel("Cancelar cita", 11, Font.PLAIN,
                        new Color(176, 200, 224));
                cancelar.setHorizontalAlignment(SwingConstants.RIGHT);
                cancelar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
                cancelar.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        int conf = JOptionPane.showConfirmDialog(panel,
                                "¿Cancelar esta cita?", "Confirmar", JOptionPane.YES_NO_OPTION);
                        if (conf == JOptionPane.YES_OPTION) {
                            try {
                                new CitaService().cancelarCita(idCita);
                                JOptionPane.showMessageDialog(panel, "Cita cancelada correctamente.");
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(panel, "Error al cancelar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            }
                            cachedCitas = null;
                            construir();
                        }
                    }
                });
                derecha.add(badge); derecha.add(cancelar);
                card.add(info, BorderLayout.CENTER); card.add(derecha, BorderLayout.EAST);
                listaCitas.add(card);
                if (i < max - 1) listaCitas.add(Box.createVerticalStrut(8));
            }
        }

        citasPanel.add(listaCitas, BorderLayout.CENTER);
        centro.add(proximaCard, BorderLayout.NORTH);
        centro.add(citasPanel,  BorderLayout.CENTER);
        JScrollPane outerScroll = new JScrollPane(centro);
        outerScroll.setBorder(null); outerScroll.getViewport().setBackground(C[0]);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contenido.add(outerScroll, BorderLayout.CENTER);
        return contenido;
    }

    // ── Formulario editar perfil del cliente ─────────────────
    private void mostrarFormEditarPerfil() {
        Cliente cli = Main.clienteActual;
        if (cli == null) return;

        JDialog dlg = new JDialog(Main.frame, "Editar perfil", true);
        dlg.setResizable(false);
        dlg.setLocationRelativeTo(panel);

        Color azul   = C[1];
        Color fondo  = new Color(240, 246, 252);
        Color gris   = new Color(100, 116, 139);
        Color borde  = new Color(208, 228, 244);

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(fondo);
        root.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel tit = new JLabel("Editar informacion personal");
        tit.setFont(new Font("Arial", Font.BOLD, 16));
        tit.setForeground(azul);
        tit.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(tit);
        root.add(Box.createVerticalStrut(6));

        JLabel sub = new JLabel("Correo: " + (cli.getCorreo() != null ? cli.getCorreo() : "—"));
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setForeground(gris);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(sub);
        root.add(Box.createVerticalStrut(18));

        // Campos editables
        JTextField tfNombre    = campo("Nombre completo *", cli.getNombre()   != null ? cli.getNombre()   : "", root, gris, borde);
        JTextField tfTelefono  = campo("Telefono",          cli.getTelefono() != null ? cli.getTelefono() : "", root, gris, borde);
        JTextField tfDireccion = campo("Direccion",         cli.getDireccion()!= null ? cli.getDireccion(): "", root, gris, borde);

        // Separador para contraseña
        root.add(Box.createVerticalStrut(14));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(borde);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(sep);
        root.add(Box.createVerticalStrut(12));

        JLabel lblPass = new JLabel("Cambiar contrasena (opcional)");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        lblPass.setForeground(azul);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(lblPass);
        root.add(Box.createVerticalStrut(8));

        JPasswordField tfPassActual = passField("Contrasena actual", root, gris, borde);
        JPasswordField tfPassNueva  = passField("Nueva contrasena",  root, gris, borde);
        JPasswordField tfPassConf   = passField("Confirmar nueva contrasena", root, gris, borde);

        root.add(Box.createVerticalStrut(20));

        // Botones
        JPanel bots = new JPanel(new GridLayout(1, 2, 12, 0));
        bots.setBackground(fondo);
        bots.setAlignmentX(Component.LEFT_ALIGNMENT);
        bots.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancelar.setBackground(Color.WHITE); btnCancelar.setForeground(azul);
        btnCancelar.setBorder(BorderFactory.createLineBorder(azul, 1));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dlg.dispose(); }
        });

        JButton btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setBackground(azul); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true); btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            String nombre = tfNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "El nombre es obligatorio.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validar cambio de contrasena si llenaron el campo
            String passActual = new String(tfPassActual.getPassword()).trim();
            String passNueva  = new String(tfPassNueva.getPassword()).trim();
            String passConf   = new String(tfPassConf.getPassword()).trim();

            if (!passNueva.isEmpty() || !passActual.isEmpty()) {
                if (!passActual.equals(cli.getContrasena())) {
                    JOptionPane.showMessageDialog(dlg, "La contrasena actual es incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (passNueva.length() < 6) {
                    JOptionPane.showMessageDialog(dlg, "La nueva contrasena debe tener al menos 6 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!passNueva.equals(passConf)) {
                    JOptionPane.showMessageDialog(dlg, "Las contrasenas nuevas no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                cli.setContrasena(passNueva);
            }

            cli.setNombre(nombre);
            cli.setTelefono(tfTelefono.getText().trim().isEmpty() ? null : tfTelefono.getText().trim());
            cli.setDireccion(tfDireccion.getText().trim().isEmpty() ? null : tfDireccion.getText().trim());

            try {
                cli.actualizarBD();
                Main.clienteActual = cli;
                JOptionPane.showMessageDialog(dlg, "Perfil actualizado correctamente.");
                dlg.dispose();
                construir();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
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

    private JTextField campo(String label, String valor, JPanel root, Color gris, Color borde) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(gris);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JTextField tf = new JTextField(valor);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(lbl);
        root.add(Box.createVerticalStrut(3));
        root.add(tf);
        root.add(Box.createVerticalStrut(10));
        return tf;
    }

    private JPasswordField passField(String label, JPanel root, Color gris, Color borde) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(gris);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Arial", Font.PLAIN, 13));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        pf.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(lbl);
        root.add(Box.createVerticalStrut(3));
        root.add(pf);
        root.add(Box.createVerticalStrut(10));
        return pf;
    }
}