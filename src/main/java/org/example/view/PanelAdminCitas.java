package org.example.view;

import org.example.controller.CitaAdminController;
import org.example.model.Citas;
import org.example.model.EstadoCita;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PanelAdminCitas {
    public JPanel panel;
    private boolean temaOscuro = false;
    private String filtroActual = "Todas";

    private final CitaAdminController ctrl = new CitaAdminController();

    private final Color[] CLARO = {
            new Color(240,253,244), new Color(22,101,52),   Color.WHITE,
            new Color(34,120,70),   new Color(220,245,230), Color.WHITE,
            new Color(15,60,30),    new Color(100,130,110), new Color(234,88,12),
            new Color(187,224,200), new Color(15,60,30),    new Color(134,190,155),
            new Color(220,38,38),   new Color(22,163,74),   new Color(210,240,220),
    };
    private final Color[] OSCURO = {
            new Color(18,24,38),  new Color(13,18,30),   new Color(26,34,52),
            new Color(37,55,90),  new Color(32,42,64),   Color.WHITE,
            new Color(226,232,240), new Color(148,163,184), new Color(251,146,60),
            new Color(30,41,59),  new Color(9,14,24),    new Color(122,175,212),
            new Color(239,68,68), new Color(34,197,94),  new Color(15,23,42),
    };
    private Color[] C = CLARO;

    public PanelAdminCitas() { panel = new JPanel(new BorderLayout()); construir(); }
    public void setTema(boolean o) { if (o != temaOscuro) { temaOscuro = o; construir(); } }
    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll(); C = temaOscuro ? OSCURO : CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, temaOscuro, "adminCitas", panel), BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JLabel lbl(String t, int sz, int st, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", st, sz + 2));
        l.setForeground(c);
        return l;
    }

    private JPanel crearContenido() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(C[0]);

        // ── Topbar ────────────────────────────────────────────
        JPanel tb = new JPanel(new BorderLayout());
        tb.setBackground(C[2]);
        tb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C[9]),
                BorderFactory.createEmptyBorder(16, 28, 16, 28)));

        JPanel tl = new JPanel(new GridLayout(2, 1));
        tl.setBackground(C[2]);
        tl.add(lbl("Gestión de Citas", 22, Font.BOLD, C[6]));
        tl.add(lbl("Confirma, gestiona y da seguimiento a las citas", 12, Font.PLAIN, C[7]));

        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tr.setBackground(C[2]);
        JButton btnNueva = new JButton("+ Nueva cita");
        btnNueva.setFont(new Font("Arial", Font.BOLD, 13));
        btnNueva.setBackground(new Color(22, 163, 74));
        btnNueva.setForeground(Color.WHITE);
        btnNueva.setOpaque(true); btnNueva.setBorderPainted(false);
        btnNueva.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnNueva.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnNueva.addActionListener(ev -> {
            NuevaCitaAdminDialog dlg = new NuevaCitaAdminDialog(SwingUtilities.getWindowAncestor(c));
            dlg.setVisible(true);
            if (dlg.fueGuardado()) construir();
        });
        tr.add(btnNueva);
        tb.add(tl, BorderLayout.WEST);
        tb.add(tr, BorderLayout.EAST);
        c.add(tb, BorderLayout.NORTH);

        // ── Body ──────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(0, 16));
        body.setBackground(C[0]);
        body.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // ── Filtros con badges de conteo ───────────────────────
        String[] fnombres = {"Todas", "Pendientes", "Confirmadas", "Canceladas", "Completadas"};
        String[] festados = {null,    "PENDIENTE",  "CONFIRMADA",  "CANCELADA",  "COMPLETADA"};
        Color[]  fColores = {C[1], new Color(180,90,10), new Color(22,163,74), new Color(200,30,30), new Color(80,110,90)};

        JButton[] botonesF = new JButton[fnombres.length];
        JPanel[] tablaHolder = new JPanel[1];
        tablaHolder[0] = crearTabla(filtroActual, body);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setBackground(C[0]);

        for (int i = 0; i < fnombres.length; i++) {
            final String etiqueta = fnombres[i];
            final String estadoKey = festados[i];
            final Color colorActivo = fColores[i];
            final int idx = i;

            int count = contarEstado(estadoKey);
            String textoBoton = etiqueta + (count > 0 && estadoKey != null ? "  " + count : "");

            JButton f = new JButton(textoBoton);
            f.setFont(new Font("Arial", Font.PLAIN, 12));
            boolean esActivo = filtroActual.equals(etiqueta);
            f.setBackground(esActivo ? colorActivo : C[2]);
            f.setForeground(esActivo ? Color.WHITE : C[7]);
            f.setOpaque(true); f.setFocusPainted(false);
            f.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
            f.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(esActivo ? colorActivo : C[9], 1),
                    BorderFactory.createEmptyBorder(6, 14, 6, 14)));
            botonesF[idx] = f;

            f.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    filtroActual = etiqueta;
                    for (int j = 0; j < botonesF.length; j++) {
                        boolean activo = fnombres[j].equals(filtroActual);
                        botonesF[j].setBackground(activo ? fColores[j] : C[2]);
                        botonesF[j].setForeground(activo ? Color.WHITE : C[7]);
                        botonesF[j].setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(activo ? fColores[j] : C[9], 1),
                                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
                    }
                    body.remove(tablaHolder[0]);
                    tablaHolder[0] = crearTabla(filtroActual, body);
                    body.add(tablaHolder[0], BorderLayout.CENTER);
                    body.revalidate(); body.repaint();
                }
            });
            filtros.add(f);
        }

        body.add(filtros, BorderLayout.NORTH);
        body.add(tablaHolder[0], BorderLayout.CENTER);

        JScrollPane outerScroll = new JScrollPane(body);
        outerScroll.setBorder(null);
        outerScroll.getViewport().setBackground(C[0]);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(outerScroll, BorderLayout.CENTER);
        return c;
    }

    // ── Contar citas por estado para el badge ─────────────────
    private int contarEstado(String estadoKey) {
        if (estadoKey == null) return 0;
        List<Citas> todas = ctrl.listarTodas();
        int count = 0;
        for (Citas cita : todas) {
            if (cita.getEstadoCita() != null && cita.getEstadoCita().name().equals(estadoKey)) count++;
        }
        return count;
    }

    // ── Tabla con filtro + acciones por fila ──────────────────
    private JPanel crearTabla(String filtro, JPanel bodyRef) {
        List<Citas> todasCitas = ctrl.listarTodas();
        Collections.reverse(todasCitas);

        List<Citas> citasFiltradas = new ArrayList<>();
        for (Citas cita : todasCitas) {
            if (filtro.equals("Todas")) {
                citasFiltradas.add(cita);
            } else {
                EstadoCita estadoBuscado = null;
                switch (filtro) {
                    case "Confirmadas":  estadoBuscado = EstadoCita.CONFIRMADA;  break;
                    case "Pendientes":   estadoBuscado = EstadoCita.PENDIENTE;   break;
                    case "Canceladas":   estadoBuscado = EstadoCita.CANCELADA;   break;
                    case "Completadas":  estadoBuscado = EstadoCita.COMPLETADA;  break;
                }
                if (estadoBuscado != null && cita.getEstadoCita() == estadoBuscado) {
                    citasFiltradas.add(cita);
                }
            }
        }

        // Columnas — la ultima es "Accion" (no viene de BD)
        String[] cols = {"Mascota", "Dueno", "Direccion", "Fecha", "Hora", "Estado", "Accion"};
        Object[][] datos = new Object[citasFiltradas.size()][7];
        for (int i = 0; i < citasFiltradas.size(); i++) {
            Citas cita = citasFiltradas.get(i);
            datos[i][0] = cita.getMascota() != null ? cita.getMascota().getNombre() : "-";
            String duenio = "-";
            if (cita.getMascota() != null && cita.getMascota().getCliente() != null)
                duenio = cita.getMascota().getCliente().getNombre();
            String direccion = (cita.getDireccionDomicilio() != null && !cita.getDireccionDomicilio().isEmpty())
                    ? "Domicilio: " + cita.getDireccionDomicilio() : "Presencial";
            datos[i][1] = duenio;
            datos[i][2] = direccion;
            datos[i][3] = cita.getFechaCita() != null ? cita.getFechaCita().toString()  : "-";
            datos[i][4] = cita.getHoraCita()  != null ? cita.getHoraCita().toString()   : "-";
            datos[i][5] = cita.getEstadoCita()!= null ? cita.getEstadoCita().name()     : "-";
            datos[i][6] = cita.getId();
        }

        final List<Citas> citasRef = citasFiltradas;

        DefaultTableModel modelo = new DefaultTableModel(datos, cols) {
            public boolean isCellEditable(int r, int cc) { return cc == 6; }
            public Class<?> getColumnClass(int c) { return c == 6 ? Object.class : String.class; }
        };

        JTable tabla = new JTable(modelo);
        tabla.setBackground(C[2]); tabla.setForeground(C[6]);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(44);
        tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(C[3]); tabla.setFillsViewportHeight(true);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(C[14]); th.setForeground(temaOscuro ? C[7] : C[1]);
        th.setFont(new Font("Arial", Font.BOLD, 11));
        th.setReorderingAllowed(false); th.setPreferredSize(new Dimension(0, 36));

        // ── Renderer columna Estado ───────────────────────────
        tabla.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, col);
                l.setFont(new Font("Arial", Font.BOLD, 12));
                l.setHorizontalAlignment(SwingConstants.CENTER);
                String estado = v == null ? "" : v.toString();
                switch (estado) {
                    case "CONFIRMADA": l.setForeground(C[13]); l.setText("Confirmada");  break;
                    case "PENDIENTE":  l.setForeground(C[8]);  l.setText("Pendiente");   break;
                    case "CANCELADA":  l.setForeground(C[12]); l.setText("Cancelada");   break;
                    case "EN_ESPERA":  l.setForeground(C[8]);  l.setText("En espera");   break;
                    case "COMPLETADA": l.setForeground(C[13]); l.setText("Completada");  break;
                    default:           l.setForeground(C[7]);
                }
                l.setBackground(s ? C[3] : (r % 2 == 0 ? C[2] : C[4]));
                l.setOpaque(true);
                return l;
            }
        });

        // ── Renderer base para demas columnas ─────────────────
        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                super.getTableCellRendererComponent(t, v, s, f, r, col);
                setForeground(C[6]);
                setBackground(r % 2 == 0 ? C[2] : C[4]);
                if (s) setBackground(C[3]);
                setFont(new Font("Arial", Font.PLAIN, 13));
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14));
                return this;
            }
        };
        for (int i = 0; i < 5; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(base);

        // ── Renderer + Editor de la columna Accion ────────────
        tabla.getColumnModel().getColumn(6).setCellRenderer(new AccionRenderer());
        tabla.getColumnModel().getColumn(6).setCellEditor(new AccionEditor(tabla, citasRef, bodyRef));

        // Anchos de columna
        int[] anchos = {110, 140, 200, 100, 70, 120, 130};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(null); sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getViewport().setBackground(C[2]);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C[2]);

        if (citasFiltradas.isEmpty()) {
            JPanel sinCitas = new JPanel(new GridBagLayout());
            sinCitas.setBackground(C[2]);
            JLabel msg = new JLabel("No hay citas con estado \"" + filtro + "\"");
            msg.setFont(new Font("Arial", Font.PLAIN, 14));
            msg.setForeground(C[7]);
            sinCitas.add(msg);
            wrapper.add(sinCitas, BorderLayout.CENTER);
        } else {
            wrapper.add(sp, BorderLayout.CENTER);
        }

        return wrapper;
    }

    // ════════════════════════════════════════════════════════
    //  RENDERER columna Accion — muestra boton visual
    // ════════════════════════════════════════════════════════
    private class AccionRenderer extends JPanel implements TableCellRenderer {
        private final JButton btnConfirmar = new JButton("Confirmar");
        private final JLabel  lblYaConfirmada = new JLabel("Ya confirmada");
        private final JLabel  lblCancelada    = new JLabel("-");

        AccionRenderer() {
            setLayout(new GridBagLayout());
            setOpaque(true);
            btnConfirmar.setFont(new Font("Arial", Font.BOLD, 11));
            btnConfirmar.setBackground(new Color(22, 163, 74));
            btnConfirmar.setForeground(Color.WHITE);
            btnConfirmar.setOpaque(true); btnConfirmar.setBorderPainted(false);
            btnConfirmar.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            lblYaConfirmada.setFont(new Font("Arial", Font.PLAIN, 11));
            lblYaConfirmada.setForeground(new Color(22, 163, 74));
            lblCancelada.setFont(new Font("Arial", Font.PLAIN, 11));
            lblCancelada.setForeground(new Color(150, 150, 150));
        }

        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
            removeAll();
            setBackground(r % 2 == 0 ? C[2] : C[4]);
            if (s) setBackground(C[3]);
            String estado = t.getValueAt(r, 5) != null ? t.getValueAt(r, 5).toString() : "";
            switch (estado) {
                case "CONFIRMADA": add(lblYaConfirmada); break;
                case "CANCELADA":  add(lblCancelada);    break;
                case "COMPLETADA": add(lblCancelada);    break;
                default:           add(btnConfirmar);    break;
            }
            return this;
        }
    }

    // ════════════════════════════════════════════════════════
    //  EDITOR columna Accion — ejecuta la accion al hacer clic
    // ════════════════════════════════════════════════════════
    private class AccionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel cellPanel = new JPanel(new GridBagLayout());
        private final JButton btnConfirmar = new JButton("Confirmar");
        private int filaActual = -1;
        private final JTable tabla;
        private final List<Citas> citas;
        private final JPanel bodyRef;

        AccionEditor(JTable tabla, List<Citas> citas, JPanel bodyRef) {
            this.tabla = tabla; this.citas = citas; this.bodyRef = bodyRef;
            cellPanel.setOpaque(true);
            btnConfirmar.setFont(new Font("Arial", Font.BOLD, 11));
            btnConfirmar.setBackground(new Color(22, 163, 74));
            btnConfirmar.setForeground(Color.WHITE);
            btnConfirmar.setOpaque(true); btnConfirmar.setBorderPainted(false);
            btnConfirmar.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
            btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cellPanel.add(btnConfirmar);

            btnConfirmar.addActionListener(e -> {
                fireEditingStopped();
                if (filaActual < 0 || filaActual >= citas.size()) return;
                Citas cita = citas.get(filaActual);
                if (cita.getId() == null) return;

                // Bloquear si ya está confirmada, completada o cancelada
                EstadoCita estadoActual = cita.getEstadoCita();
                if (estadoActual == EstadoCita.CONFIRMADA) {
                    JOptionPane.showMessageDialog(panel,
                            "Esta cita ya está confirmada.", "Sin cambios", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (estadoActual == EstadoCita.COMPLETADA || estadoActual == EstadoCita.CANCELADA) {
                    JOptionPane.showMessageDialog(panel,
                            "No se puede confirmar una cita " + estadoActual.name().toLowerCase() + ".",
                            "Sin cambios", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int respuesta = JOptionPane.showConfirmDialog(panel,
                        "Confirmar la cita de " +
                                (cita.getMascota() != null ? cita.getMascota().getNombre() : "esta mascota") +
                                " el " + cita.getFechaCita() + " a las " + cita.getHoraCita() + "?\n" +
                                "Se enviara un correo de confirmacion al cliente.",
                        "Confirmar cita", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (respuesta == JOptionPane.YES_OPTION) {
                    ctrl.confirmarCita(cita.getId(), panel);
                    construir();
                }
            });
        }

        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int col) {
            filaActual = r;
            cellPanel.setBackground(C[3]);
            return cellPanel;
        }

        public Object getCellEditorValue() { return null; }
    }
}
