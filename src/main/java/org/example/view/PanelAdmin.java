package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.service.CitaService;
import org.example.model.Citas;
import org.example.model.Control_vacunas;
import org.example.model.EstadoCita;
import org.example.model.Mascotas;
import org.example.model.Vacunas;
import org.example.model.Empleados;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PanelAdmin {
    public JPanel panel;


    private List<Citas> cachedCitasHoy      = null;
    private List<Citas> cachedCitasVacunas  = null;

    private final Color[] CLARO = {
            new Color(240,253,244), new Color(22,101,52),   Color.WHITE,
            new Color(34,120,70),   new Color(220,245,230), Color.WHITE,
            new Color(15,60,30),    new Color(100,130,110), new Color(234,88,12),
            new Color(187,224,200), new Color(15,60,30),    new Color(134,190,155),
            new Color(220,38,38),   new Color(22,163,74),   new Color(210,240,220),
    };
    private Color[] C = CLARO;

    public PanelAdmin() { panel = new JPanel(new BorderLayout()); construir(); }
    public void recargar()         { cachedCitasHoy = null; cachedCitasVacunas = null; construir(); }
    public void invalidarCache()   { cachedCitasHoy = null; cachedCitasVacunas = null; }

    private void construir() {
        panel.removeAll(); C = CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, "panelAdmin", panel), BorderLayout.WEST);

        try { cachedCitasHoy     = Citas.consultarDeHoyBD();   } catch (Exception e) { cachedCitasHoy     = Collections.emptyList(); JOptionPane.showMessageDialog(panel, "Error cargando citas de hoy: " + e.getMessage()); }
        try { cachedCitasVacunas = Citas.consultarVacunasBD(); } catch (Exception e) { cachedCitasVacunas = Collections.emptyList(); }
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JLabel lbl(String t, int sz, int st, Color c) {
        JLabel l = new JLabel(t); l.setFont(new Font("Arial",st,sz + 2)); l.setForeground(c); return l;
    }
    private JButton btn(String t, Color bg, Color fg) {
        JButton b = new JButton(t); b.setFont(new Font("Arial",Font.BOLD,15));
        b.setBackground(bg); b.setForeground(fg); b.setOpaque(true);
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR)); return b;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(C[0]);
        contenido.add(crearTopbar(), BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(crearCentro());
        scroll.setBorder(null); scroll.getViewport().setBackground(C[0]);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        contenido.add(scroll, BorderLayout.CENTER);
        return contenido;
    }

    private JPanel crearTopbar() {
        JPanel tb = new JPanel(new BorderLayout());
        tb.setBackground(C[2]);
        tb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0,0,1,0,C[9]),
                BorderFactory.createEmptyBorder(16,28,16,28)));

        JPanel izq = new JPanel();
        izq.setLayout(new BoxLayout(izq, BoxLayout.Y_AXIS));
        izq.setBackground(C[2]);

        JLabel titulo = new JLabel("Inicio");
        titulo.setFont(new Font("Arial",Font.BOLD,22)); titulo.setForeground(C[6]);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy", new Locale("es", "ES")));
        fechaHoy = Character.toUpperCase(fechaHoy.charAt(0)) + fechaHoy.substring(1);
        JLabel fecha = new JLabel(fechaHoy + " · Kampets Veterinaria");
        fecha.setFont(new Font("Arial",Font.PLAIN,12)); fecha.setForeground(C[11]);
        fecha.setAlignmentX(Component.LEFT_ALIGNMENT);

        izq.add(titulo); izq.add(fecha);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        der.setBackground(C[2]);

        JButton btnActualizar = btn("Actualizar", C[2], C[1]);
        btnActualizar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1), BorderFactory.createEmptyBorder(8,16,8,16)));
        btnActualizar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { recargar(); }
        });

        JButton btnExportar = btn("Exportar PDF", C[2], C[1]);
        btnExportar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1), BorderFactory.createEmptyBorder(8,16,8,16)));
        btnExportar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { exportarReporte(); }
        });

        JButton btnNuevo = btn("Nuevo admin", new Color(22,163,74), Color.WHITE);
        btnNuevo.setBorder(BorderFactory.createEmptyBorder(9,18,9,18));
        btnNuevo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { mostrarFormularioNuevoAdmin(); }
        });

        der.add(btnActualizar); der.add(btnExportar); der.add(btnNuevo);
        tb.add(izq, BorderLayout.WEST); tb.add(der, BorderLayout.EAST);
        return tb;
    }

    private void mostrarFormularioNuevoAdmin() {
        JDialog dlg = new JDialog(Main.frame, "Registrar nuevo administrador", true);
        dlg.setSize(460, 530);
        dlg.setLocationRelativeTo(Main.frame);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(C[2]);

        // ── HEADER ────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C[1]);
        header.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel hText = new JPanel();
        hText.setLayout(new BoxLayout(hText, BoxLayout.Y_AXIS));
        hText.setBackground(C[1]);
        JLabel hTitle = new JLabel("Nuevo administrador");
        hTitle.setFont(new Font("Arial", Font.BOLD, 16));
        hTitle.setForeground(Color.WHITE);
        hTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel hSub = new JLabel("Solo los admins pueden crear cuentas de administrador");
        hSub.setFont(new Font("Arial", Font.PLAIN, 11));
        hSub.setForeground(new Color(180, 220, 195));
        hSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        hText.add(hTitle); hText.add(Box.createVerticalStrut(4)); hText.add(hSub);
        header.add(hText, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ── FORM BODY ─────────────────────────────────────────
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JLabel lNombre = lbl("Nombre", 12, Font.BOLD, C[6]);
        lNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lNombre); form.add(Box.createVerticalStrut(5));
        JTextField tfNombre = new JTextField();
        tfNombre.setFont(new Font("Arial", Font.PLAIN, 13));
        tfNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfNombre.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(6,10,6,10)));
        form.add(tfNombre); form.add(Box.createVerticalStrut(12));

        JLabel lApellido = lbl("Apellido", 12, Font.BOLD, C[6]);
        lApellido.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lApellido); form.add(Box.createVerticalStrut(5));
        JTextField tfApellido = new JTextField();
        tfApellido.setFont(new Font("Arial", Font.PLAIN, 13));
        tfApellido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfApellido.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfApellido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(6,10,6,10)));
        form.add(tfApellido); form.add(Box.createVerticalStrut(12));

        JLabel lCorreo = lbl("Correo electrónico", 12, Font.BOLD, C[6]);
        lCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lCorreo); form.add(Box.createVerticalStrut(5));
        JTextField tfCorreo = new JTextField();
        tfCorreo.setFont(new Font("Arial", Font.PLAIN, 13));
        tfCorreo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfCorreo.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfCorreo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(6,10,6,10)));
        form.add(tfCorreo); form.add(Box.createVerticalStrut(12));

        JLabel lPass = lbl("Contraseña", 12, Font.BOLD, C[6]);
        lPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lPass); form.add(Box.createVerticalStrut(5));
        JPasswordField tfPass = new JPasswordField();
        tfPass.setFont(new Font("Arial", Font.PLAIN, 13));
        tfPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tfPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        tfPass.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(6,10,6,10)));
        form.add(tfPass); form.add(Box.createVerticalStrut(12));

        JLabel lCargo = lbl("Cargo", 12, Font.BOLD, C[6]);
        lCargo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lCargo); form.add(Box.createVerticalStrut(5));
        JComboBox<String> cbCargo = new JComboBox<>(new String[]{
                "Administrador", "Veterinario", "Recepcionista"
        });
        cbCargo.setFont(new Font("Arial", Font.PLAIN, 13));
        cbCargo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbCargo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbCargo.setBackground(C[2]);
        form.add(cbCargo); form.add(Box.createVerticalStrut(20));

        JSeparator sepLine = new JSeparator();
        sepLine.setForeground(C[9]);
        sepLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sepLine.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(sepLine); form.add(Box.createVerticalStrut(14));

        // Botones: ancho completo lado a lado
        JPanel bots = new JPanel(new GridLayout(1, 2, 10, 0));
        bots.setBackground(C[2]);
        bots.setAlignmentX(Component.LEFT_ALIGNMENT);
        bots.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancelar.setBackground(C[4]); btnCancelar.setForeground(C[6]);
        btnCancelar.setOpaque(true); btnCancelar.setBorderPainted(false);
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(8,16,8,16)));
        btnCancelar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { dlg.dispose(); }
        });

        JButton btnGuardar = btn("Registrar admin", new Color(22,163,74), Color.WHITE);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(9,18,9,18));
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre   = tfNombre.getText().trim();
                String apellido = tfApellido.getText().trim();
                String correo   = tfCorreo.getText().trim();
                String pass     = new String(tfPass.getPassword()).trim();
                String cargo    = (String) cbCargo.getSelectedItem();

                if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(dlg,
                            "Todos los campos son obligatorios.",
                            "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try {
                    Empleados emp = new Empleados();
                    emp.setNombre(nombre);
                    emp.setApellido(apellido);
                    emp.setCorreo(correo);
                    emp.setContrasena(pass);
                    emp.setCargo(cargo);
                    emp.insertarBD();
                    JOptionPane.showMessageDialog(dlg,
                            " Administrador registrado exitosamente.\n\nCorreo: " + correo,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dlg,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        bots.add(btnCancelar); bots.add(btnGuardar);
        form.add(bots);

        root.add(form, BorderLayout.CENTER);
        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private JPanel crearCentro() {
        JPanel centro = new JPanel(new BorderLayout(0,20));
        centro.setBackground(C[0]);
        centro.setBorder(BorderFactory.createEmptyBorder(24,28,28,28));
        centro.add(crearFilaStats(), BorderLayout.NORTH);
        JPanel inferior = new JPanel(new BorderLayout(0,20));
        inferior.setBackground(C[0]);
        inferior.add(crearTablaCitas(),   BorderLayout.NORTH);
        inferior.add(crearTablaVacunas(), BorderLayout.CENTER);
        centro.add(inferior, BorderLayout.CENTER);
        return centro;
    }

    private JPanel crearFilaStats() {
        List<Citas> citasHoy      = cachedCitasHoy      != null ? cachedCitasHoy      : Collections.emptyList();
        List<Citas> citasVacunas  = cachedCitasVacunas  != null ? cachedCitasVacunas  : Collections.emptyList();

        long pendientesVac = 0;
        for (Citas c : citasVacunas) {
            if (c.getEstadoCita() == EstadoCita.PENDIENTE || c.getEstadoCita() == EstadoCita.CONFIRMADA)
                pendientesVac++;
        }

        JPanel fila = new JPanel(new GridLayout(1,2,16,0));
        fila.setBackground(C[0]);
        Object[][] stats = {
                {"Citas hoy",               String.valueOf(citasHoy.size()),    "Programadas para hoy",       C[13]},
                {"Vacunaciones pendientes",  String.valueOf(pendientesVac),      "Citas de vacunación activas", C[8]},
        };
        for (Object[] s : stats) {
            JPanel card = new JPanel(new BorderLayout(0,6));
            card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C[9],1),
                    BorderFactory.createEmptyBorder(20,22,20,22)));
            card.add(lbl((String)s[0],12,Font.PLAIN,C[7]),       BorderLayout.NORTH);
            card.add(lbl((String)s[1],32,Font.BOLD,C[6]),        BorderLayout.CENTER);
            card.add(lbl((String)s[2],11,Font.PLAIN,(Color)s[3]),BorderLayout.SOUTH);
            fila.add(card);
        }
        return fila;
    }

    private JPanel crearTablaCitas() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C[2]); header.setBorder(BorderFactory.createEmptyBorder(16,20,14,20));
        JLabel titulo = lbl("Citas de hoy",15,Font.BOLD,C[6]);
        JButton verTodas = btn("Ver todas",C[4],C[1]);
        verTodas.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        verTodas.setFont(new Font("Arial",Font.PLAIN,12));
        verTodas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminCitas"); }
        });
        header.add(titulo,BorderLayout.WEST); header.add(verTodas,BorderLayout.EAST);

        List<Citas> citasHoy = cachedCitasHoy != null ? cachedCitasHoy : Collections.emptyList();

        String[] cols = {"Cliente/Mascota","Hora","Vet","Estado"};
        Object[][] datos;
        if (citasHoy.isEmpty()) {
            datos = new Object[][]{{"Sin citas para hoy","—","—","—"}};
        } else {
            datos = new Object[citasHoy.size()][4];
            for (int i = 0; i < citasHoy.size(); i++) {
                Citas c = citasHoy.get(i);
                String clienteNombre = c.getMascota() != null && c.getMascota().getCliente() != null
                        ? c.getMascota().getCliente().getNombre() : "—";
                String[] partes = clienteNombre.split(" ");
                String clienteCorto = partes.length >= 2
                        ? partes[0] + " " + partes[1].charAt(0) + "."
                        : clienteNombre;
                String mascotaNombre = c.getMascota() != null ? c.getMascota().getNombre() : "—";
                String hora   = c.getHoraCita()   != null ? c.getHoraCita().toString()   : "—";
                String vet    = c.getEmpleado()   != null ? c.getEmpleado().getNombre()  : "—";
                String estado = c.getEstadoCita() != null ? c.getEstadoCita().toString() : "—";
                datos[i] = new Object[]{clienteCorto + " – " + mascotaNombre, hora, vet, estado};
            }
        }

        JTable tabla = construirTabla(cols, datos, 3);
        JScrollPane sp = new JScrollPane(tabla); sp.setBorder(null); sp.getViewport().setBackground(C[2]);
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(C[2]);
        wrapper.add(header,BorderLayout.NORTH); wrapper.add(sp,BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearTablaVacunas() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C[2]); header.setBorder(BorderFactory.createEmptyBorder(16,20,14,20));
        JLabel titulo = lbl("Citas de vacunación", 15, Font.BOLD, C[6]);
        JButton gestionar = btn("Gestionar vacunas", C[4], C[1]);
        gestionar.setBorder(BorderFactory.createEmptyBorder(6,14,6,14));
        gestionar.setFont(new Font("Arial", Font.PLAIN, 12));
        gestionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { Main.cambiarPantalla("adminVacunas"); }
        });
        header.add(titulo, BorderLayout.WEST); header.add(gestionar, BorderLayout.EAST);

        final List<Citas> citasVac = cachedCitasVacunas != null ? cachedCitasVacunas : Collections.emptyList();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("es"));

        String[] cols = {"Mascota", "Dueño", "Fecha", "Hora", "Estado", "Registrar"};
        Object[][] datos;
        if (citasVac.isEmpty()) {
            datos = new Object[][]{{"Sin citas de vacunación", "—", "—", "—", "—", ""}};
        } else {
            datos = new Object[citasVac.size()][6];
            for (int i = 0; i < citasVac.size(); i++) {
                Citas c = citasVac.get(i);
                String mascota = c.getMascota() != null ? c.getMascota().getNombre() : "—";
                String duenio  = c.getMascota() != null && c.getMascota().getCliente() != null
                        ? c.getMascota().getCliente().getNombre() : "—";
                String fecha   = c.getFechaCita() != null ? c.getFechaCita().format(fmt) : "—";
                String hora    = c.getHoraCita()  != null ? c.getHoraCita().toString().substring(0, 5) : "—";
                String estado  = c.getEstadoCita() != null ? c.getEstadoCita().name() : "—";
                datos[i] = new Object[]{mascota, duenio, fecha, hora, estado, "Registrar vacuna"};
            }
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setBackground(C[2]); tabla.setForeground(C[6]);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13)); tabla.setRowHeight(40);
        tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(C[3]); tabla.setFillsViewportHeight(true);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(C[14]); th.setForeground(C[1]);
        th.setFont(new Font("Arial", Font.BOLD, 11)); th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0, 36));

        int[] anchos = {100, 150, 110, 60, 110, 140};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // Estado renderer
        tabla.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, col);
                l.setFont(new Font("Arial", Font.BOLD, 12)); l.setHorizontalAlignment(SwingConstants.CENTER);
                switch (v == null ? "" : v.toString()) {
                    case "CONFIRMADA": l.setForeground(C[13]); break;
                    case "PENDIENTE":  l.setForeground(C[8]);  break;
                    case "CANCELADA":  l.setForeground(C[12]); break;
                    default:           l.setForeground(C[7]);
                }
                l.setBackground(s ? C[3] : (r%2==0 ? C[2] : C[4])); l.setOpaque(true); return l;
            }
        });

        // "Registrar vacuna" button renderer
        tabla.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, col);
                String val = v != null ? v.toString() : "";
                if (!val.isEmpty()) {
                    l.setText("+ Registrar vacuna");
                    l.setFont(new Font("Arial", Font.BOLD, 11));
                    l.setForeground(C[13]);
                    l.setHorizontalAlignment(SwingConstants.CENTER);
                    l.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(C[13], 1),
                            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
                }
                l.setBackground(s ? C[3] : (r%2==0 ? C[2] : C[4])); l.setOpaque(true); return l;
            }
        });

        // Base renderer for text columns
        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                super.getTableCellRendererComponent(t, v, s, f, r, col);
                setForeground(C[6]); setFont(new Font("Arial", Font.PLAIN, 13));
                setBackground(r%2==0 ? C[2] : C[4]);
                if (s) setBackground(C[3]); setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14)); return this;
            }
        };
        for (int i = 0; i < 4; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(base);

        // Click on "Registrar vacuna" column
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tabla.rowAtPoint(e.getPoint());
                int col = tabla.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5 && row < citasVac.size()) {
                    Citas cita = citasVac.get(row);
                    if (cita.getMascota() != null) {
                        abrirFormRegistrarVacuna(cita.getMascota());
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(tabla); sp.setBorder(null); sp.getViewport().setBackground(C[2]);
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(C[2]);
        wrapper.add(header, BorderLayout.NORTH); wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private void abrirFormRegistrarVacuna(Mascotas mascota) {
        Window owner = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog = new JDialog(owner, "Registrar vacuna",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 360);
        dialog.setLocationRelativeTo(panel);
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 6, 7, 6);

        List<Vacunas> vacunas = Vacunas.consultarTodosBD();

        String nombreMascota = mascota.getNombre()
                + (mascota.getCliente() != null ? " (" + mascota.getCliente().getNombre() + ")" : "");
        JLabel lblMascota = lbl(nombreMascota, 13, Font.PLAIN, C[6]);

        JComboBox<Vacunas> cmbVacuna = new JComboBox<>(vacunas.toArray(new Vacunas[0]));
        cmbVacuna.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object v, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, v, idx, sel, focus);
                if (v instanceof Vacunas) setText(((Vacunas) v).getNombre());
                return this;
            }
        });

        JDateChooser dtAplic = new JDateChooser(); dtAplic.setDateFormatString("dd/MM/yyyy");
        JDateChooser dtProx  = new JDateChooser(); dtProx.setDateFormatString("dd/MM/yyyy");

        Object[][] filas = {
            {"Mascota:",          lblMascota},
            {"Vacuna:",           cmbVacuna},
            {"Fecha aplicación:", dtAplic},
            {"Próxima dosis:",    dtProx},
        };
        for (int i = 0; i < filas.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0.3;
            form.add(lbl((String) filas[i][0], 12, Font.PLAIN, C[6]), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            form.add((Component) filas[i][1], gbc);
        }
        gbc.gridx = 1; gbc.gridy = filas.length; gbc.weightx = 0.7;
        form.add(lbl("* Próxima dosis es opcional", 10, Font.PLAIN, C[7]), gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btns.setBackground(C[2]);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancelar.setBackground(C[2]); btnCancelar.setForeground(C[6]);
        btnCancelar.setOpaque(true); btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnCancelar.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { dialog.dispose(); }
        });

        JButton btnGuardar = btn("Guardar", new Color(22, 163, 74), Color.WHITE);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vacunas vacSel = (Vacunas) cmbVacuna.getSelectedItem();
                java.util.Date dAplic = dtAplic.getDate();
                java.util.Date dProx  = dtProx.getDate();

                if (vacSel == null) { JOptionPane.showMessageDialog(dialog, "Seleccione una vacuna."); return; }
                if (dAplic == null) { JOptionPane.showMessageDialog(dialog, "La fecha de aplicación es obligatoria."); return; }

                Control_vacunas cv = new Control_vacunas();
                cv.setMascota(mascota);
                cv.setVacuna(vacSel);
                cv.setFechaAplicacion(dAplic.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                cv.setProximaDosis(dProx != null ? dProx.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null);
                try {
                    new CitaService().guardarVacuna(cv);
                    JOptionPane.showMessageDialog(dialog, "Vacuna registrada correctamente.");
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al guardar: " + ex.getMessage());
                }
            }
        });
        btns.add(btnCancelar); btns.add(btnGuardar);

        gbc.gridx = 0; gbc.gridy = filas.length + 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        form.add(btns, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private JTable construirTabla(String[] cols, Object[][] datos, int colEstado) {
        DefaultTableModel modelo = new DefaultTableModel(datos,cols) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        JTable tabla = new JTable(modelo);
        tabla.setBackground(C[2]); tabla.setForeground(C[6]);
        tabla.setFont(new Font("Arial",Font.PLAIN,13)); tabla.setRowHeight(38);
        tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(C[3]); tabla.setFillsViewportHeight(true);
        JTableHeader th = tabla.getTableHeader();
        th.setBackground(C[14]); th.setForeground(C[1]);
        th.setFont(new Font("Arial",Font.BOLD,11)); th.setReorderingAllowed(false);
        th.setPreferredSize(new Dimension(0,36));
        tabla.getColumnModel().getColumn(colEstado).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int col){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,col);
                l.setFont(new Font("Arial",Font.BOLD,12)); l.setHorizontalAlignment(SwingConstants.CENTER);
                switch(v==null?"":v.toString()){
                    case "Confirmada": l.setForeground(C[13]); break;
                    case "Pendiente":  l.setForeground(C[8]);  break;
                    case "Cancelada":  l.setForeground(C[12]); break;
                    case "Vencida":    l.setForeground(C[12]); break;
                    case "Próxima":    l.setForeground(C[8]);  break;
                    default:           l.setForeground(C[7]);
                }
                l.setBackground(s?C[3]:(r%2==0?C[2]:C[4])); l.setOpaque(true); return l;
            }
        });
        DefaultTableCellRenderer base = new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int col){
                super.getTableCellRendererComponent(t,v,s,f,r,col); setForeground(C[6]);
                setFont(new Font("Arial",Font.PLAIN,13)); setBackground(r%2==0?C[2]:C[4]);
                if(s)setBackground(C[3]); setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0,14,0,14)); return this;
            }
        };
        for(int i=0;i<colEstado;i++) tabla.getColumnModel().getColumn(i).setCellRenderer(base);
        return tabla;
    }

    private void exportarReporte() {
        // Selector de archivo — solo PDF
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar reporte del dia");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF (*.pdf)", "pdf"));
        chooser.setSelectedFile(new File("reporte_kampets_" + LocalDate.now() + ".pdf"));
        if (chooser.showSaveDialog(panel) != JFileChooser.APPROVE_OPTION) return;

        // Asegurar extension .pdf
        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf"))
            archivo = new File(archivo.getAbsolutePath() + ".pdf");

        // Cargar datos
        List<Citas> citasHoy;
        List<Citas> citasVacunas;
        try { citasHoy     = Citas.consultarDeHoyBD();  } catch (Exception e) { citasHoy     = Collections.emptyList(); }
        try { citasVacunas = Citas.consultarVacunasBD();} catch (Exception e) { citasVacunas = Collections.emptyList(); }

        long pendientes = 0;
        for (Citas c : citasVacunas) {
            if (c.getEstadoCita() == EstadoCita.PENDIENTE || c.getEstadoCita() == EstadoCita.CONFIRMADA)
                pendientes++;
        }

        // Generar PDF con PDFBox
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float margin = 50;
            float y = page.getMediaBox().getHeight() - margin;
            DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter fmtHora  = DateTimeFormatter.ofPattern("HH:mm");

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // Titulo
                pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 18, margin, y, "REPORTE DEL DIA - KAMPETS VETERINARIA");
                y -= 22;
                pdfTexto(cs, PDType1Font.HELVETICA, 10, margin, y, "Fecha: " + LocalDate.now().format(fmtFecha));
                y -= 28;
                pdfLinea(cs, margin, y, page.getMediaBox().getWidth() - margin);
                y -= 18;

                // Estadisticas
                pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 13, margin, y, "ESTADISTICAS");
                y -= 18;
                pdfTexto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Citas programadas hoy:       " + citasHoy.size());
                y -= 15;
                pdfTexto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Vacunaciones pendientes:     " + pendientes);
                y -= 24;
                pdfLinea(cs, margin, y, page.getMediaBox().getWidth() - margin);
                y -= 18;

                // Citas de hoy
                pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 13, margin, y, "CITAS DE HOY");
                y -= 18;
                if (citasHoy.isEmpty()) {
                    pdfTexto(cs, PDType1Font.HELVETICA, 10, margin, y, "  No hay citas programadas para hoy.");
                    y -= 15;
                } else {
                    // Encabezados
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin,       y, "Hora");
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 60,  y, "Mascota");
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 200, y, "Veterinario");
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 340, y, "Estado");
                    y -= 14;
                    for (Citas c : citasHoy) {
                        String hora    = c.getHoraCita()   != null ? c.getHoraCita().format(fmtHora)    : "-";
                        String mascota = c.getMascota()    != null ? c.getMascota().getNombre()           : "-";
                        String vet     = c.getEmpleado()   != null ? c.getEmpleado().getNombre()          : "-";
                        String estado  = c.getEstadoCita() != null ? c.getEstadoCita().name()             : "-";
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin,       y, hora);
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin + 60,  y, truncar(mascota, 18));
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin + 200, y, truncar(vet,     18));
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin + 340, y, estado);
                        y -= 14;
                    }
                }
                y -= 10;
                pdfLinea(cs, margin, y, page.getMediaBox().getWidth() - margin);
                y -= 18;

                // Vacunaciones pendientes
                pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 13, margin, y, "VACUNACIONES PENDIENTES");
                y -= 18;
                if (citasVacunas.isEmpty()) {
                    pdfTexto(cs, PDType1Font.HELVETICA, 10, margin, y, "  No hay vacunaciones pendientes.");
                } else {
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin,       y, "Fecha");
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 100, y, "Mascota");
                    pdfTexto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 280, y, "Estado");
                    y -= 14;
                    for (Citas c : citasVacunas) {
                        if (c.getEstadoCita() != EstadoCita.PENDIENTE && c.getEstadoCita() != EstadoCita.CONFIRMADA) continue;
                        String fecha   = c.getFechaCita()  != null ? c.getFechaCita().format(fmtFecha) : "-";
                        String mascota = c.getMascota()    != null ? c.getMascota().getNombre()          : "-";
                        String estado  = c.getEstadoCita() != null ? c.getEstadoCita().name()            : "-";
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin,       y, fecha);
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin + 100, y, truncar(mascota, 22));
                        pdfTexto(cs, PDType1Font.HELVETICA, 10, margin + 280, y, estado);
                        y -= 14;
                    }
                }

                // Pie de pagina
                pdfLinea(cs, margin, 45, page.getMediaBox().getWidth() - margin);
                pdfTexto(cs, PDType1Font.HELVETICA, 8, margin, 32, "Generado por Kampets Veterinaria - Sistema de gestion");
            }

            doc.save(archivo);
            JOptionPane.showMessageDialog(panel,
                    "Reporte PDF exportado:\n" + archivo.getAbsolutePath(),
                    "Listo", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel,
                    "Error al generar PDF: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Helpers para generar PDF ──────────────────────────────────────────
    private void pdfTexto(PDPageContentStream cs, PDType1Font fuente, float tam,
                          float x, float y, String texto) throws IOException {
        cs.beginText();
        cs.setFont(fuente, tam);
        cs.newLineAtOffset(x, y);
        cs.showText(limpiarTexto(texto));
        cs.endText();
    }

    private void pdfLinea(PDPageContentStream cs, float x1, float y, float x2) throws IOException {
        cs.setStrokingColor(0.7f, 0.7f, 0.7f);
        cs.moveTo(x1, y); cs.lineTo(x2, y); cs.stroke();
        cs.setStrokingColor(0f, 0f, 0f);
    }

    private String truncar(String s, int max) {
        if (s == null) return "-";
        return s.length() <= max ? s : s.substring(0, max - 1) + ".";
    }

    private String limpiarTexto(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char ch : s.toCharArray()) {
            int code = (int) ch;
            if      (code == 0x2014 || code == 0x2013) sb.append('-');
            else if (code == 0x201C || code == 0x201D) sb.append('"');
            else if (code == 0x2018 || code == 0x2019) sb.append('\'');
            else if (code > 255)                        sb.append('?');
            else                                        sb.append(ch);
        }
        return sb.toString();
    }
}
