package org.example.view;

import org.example.service.CitaService;
import org.example.model.Cita_servicio;
import org.example.model.Citas;
import org.example.model.Control_vacunas;
import org.example.model.Vacunas;
import org.example.model.EstadoCita;
import org.example.model.Vacunas;

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
    private String filtroActual = "Todas";
    private List<Citas> cachedTodas = null;


    private final Color[] CLARO = {
            new Color(240,253,244), new Color(22,101,52),   Color.WHITE,
            new Color(34,120,70),   new Color(220,245,230), Color.WHITE,
            new Color(15,60,30),    new Color(100,130,110), new Color(234,88,12),
            new Color(187,224,200), new Color(15,60,30),    new Color(134,190,155),
            new Color(220,38,38),   new Color(22,163,74),   new Color(210,240,220),
    };
    private Color[] C = CLARO;

    public PanelAdminCitas() { panel = new JPanel(new BorderLayout()); construir(); }
    public void recargar() { cachedTodas = null; construir(); }

    private void construir() {
        panel.removeAll(); C = CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, "adminCitas", panel), BorderLayout.WEST);

        try { cachedTodas = Citas.consultarTodosBD(); }
        catch (Exception e) { cachedTodas = Collections.emptyList(); }
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
        btnNueva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                NuevaCitaAdminDialog dlg = new NuevaCitaAdminDialog(SwingUtilities.getWindowAncestor(c));
                dlg.setVisible(true);
                if (dlg.fueGuardado()) {
                    cachedTodas = null;
                    construir();
                    Main.recargarPanelAdmin();
                }
            }
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
        if (estadoKey == null || cachedTodas == null) return 0;
        int count = 0;
        for (Citas cita : cachedTodas) {
            if (cita.getEstadoCita() != null && cita.getEstadoCita().name().equals(estadoKey)) count++;
        }
        return count;
    }

    // ── Tabla con filtro + acciones por fila ──────────────────
    private JPanel crearTabla(String filtro, JPanel bodyRef) {
        List<Citas> todasCitas = cachedTodas != null ? new ArrayList<>(cachedTodas) : new ArrayList<>();
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

        // Columnas de la tabla
        String[] cols = {"Mascota", "Dueno", "Servicio", "Direccion", "Fecha", "Hora", "Estado"};
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
            String motivoText = "-";
            if (cita.getMotivo() != null && !cita.getMotivo().isEmpty()) {
                motivoText = cita.getMotivo();
            } else if (cita.getServicios() != null && !cita.getServicios().isEmpty()) {
                StringBuilder sbMotivo = new StringBuilder();
                for (Cita_servicio cs : cita.getServicios()) {
                    if (cs.getServicio() != null) {
                        if (sbMotivo.length() > 0) sbMotivo.append(", ");
                        sbMotivo.append(cs.getServicio().getNombre());
                    }
                }
                motivoText = sbMotivo.toString();
                if (motivoText.isEmpty()) motivoText = "-";
            }
            datos[i][2] = motivoText;
            datos[i][3] = direccion;
            datos[i][4] = cita.getFechaCita() != null ? cita.getFechaCita().toString() : "-";
            datos[i][5] = cita.getHoraCita()  != null ? cita.getHoraCita().toString()  : "-";
            datos[i][6] = cita.getEstadoCita()!= null ? cita.getEstadoCita().name()    : "-";
        }

        final List<Citas> citasRef = citasFiltradas;

        DefaultTableModel modelo = new DefaultTableModel(datos, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable tabla = new JTable(modelo);
        tabla.setBackground(C[2]); tabla.setForeground(C[6]);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(44);
        tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(C[3]); tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(C[14]); th.setForeground(C[1]);
        th.setFont(new Font("Arial", Font.BOLD, 11));
        th.setReorderingAllowed(false); th.setPreferredSize(new Dimension(0, 36));

        // Renderer columna Estado (colores segun estado)
        tabla.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
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

        // Renderer base para las demas columnas
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
        for (int i = 0; i < 6; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(base);

        int[] anchos = {110, 130, 160, 170, 90, 65, 120};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // ── Botones de accion (fuera de la tabla, actuan sobre la fila seleccionada) ──
        final JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 12));
        btnConfirmar.setBackground(new Color(22, 163, 74)); btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setOpaque(true); btnConfirmar.setBorderPainted(false);
        btnConfirmar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnConfirmar.setEnabled(false);

        final JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        btnCancelar.setBackground(new Color(220, 38, 38)); btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setOpaque(true); btnCancelar.setBorderPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btnCancelar.setEnabled(false);

        // Habilitar o deshabilitar botones segun el estado de la fila seleccionada
        tabla.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                int row = tabla.getSelectedRow();
                if (row < 0 || row >= citasRef.size()) {
                    btnConfirmar.setEnabled(false);
                    btnCancelar.setEnabled(false);
                    return;
                }
                String estado = citasRef.get(row).getEstadoCita() != null
                        ? citasRef.get(row).getEstadoCita().name() : "";
                btnConfirmar.setEnabled(estado.equals("PENDIENTE") || estado.equals("EN_ESPERA"));
                btnCancelar.setEnabled(!estado.equals("CANCELADA") && !estado.equals("COMPLETADA"));
            }
        });

        btnConfirmar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tabla.getSelectedRow();
                if (row < 0 || row >= citasRef.size()) return;
                Citas cita = citasRef.get(row);
                String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "esta mascota";
                int resp = JOptionPane.showConfirmDialog(panel,
                        "Confirmar la cita de " + nombreMascota + "?",
                        "Confirmar cita", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    try {
                        new CitaService().confirmarCita(cita.getId());
                        JOptionPane.showMessageDialog(panel, "Cita confirmada correctamente.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cachedTodas = null; construir(); Main.recargarPanelAdmin();
                    // Si era una cita de vacunacion, preguntar si registrar la vacuna
                    boolean esVacunacion = cita.getMotivo() != null && cita.getMotivo().toLowerCase().contains("vacun");
                    if (!esVacunacion && cita.getServicios() != null) {
                        for (Cita_servicio cs : cita.getServicios()) {
                            if (cs.getServicio() != null && cs.getServicio().getNombre().toLowerCase().contains("vacun")) {
                                esVacunacion = true; break;
                            }
                        }
                    }
                    if (esVacunacion && cita.getMascota() != null) abrirDialogoRegistrarVacuna(cita);
                }
            }
        });

        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tabla.getSelectedRow();
                if (row < 0 || row >= citasRef.size()) return;
                Citas cita = citasRef.get(row);
                String nombreMascota = cita.getMascota() != null ? cita.getMascota().getNombre() : "esta mascota";
                int resp = JOptionPane.showConfirmDialog(panel,
                        "Cancelar la cita de " + nombreMascota + "?",
                        "Cancelar cita", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (resp == JOptionPane.YES_OPTION) {
                    try {
                        new CitaService().cancelarCita(cita.getId());
                        JOptionPane.showMessageDialog(panel, "Cita cancelada correctamente.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    cachedTodas = null; construir(); Main.recargarPanelAdmin();
                }
            }
        });

        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        botonesPanel.setBackground(C[2]);
        botonesPanel.add(btnConfirmar);
        botonesPanel.add(btnCancelar);

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
            wrapper.add(botonesPanel, BorderLayout.SOUTH);
        }

        return wrapper;
    }

    // ════════════════════════════════════════════════════════
    //  DIALOGO REGISTRO DE VACUNA desde confirmación de cita
    // ════════════════════════════════════════════════════════
    private void abrirDialogoRegistrarVacuna(Citas cita) {
        java.util.List<Vacunas> vacunas = Vacunas.consultarTodosBD();
        if (vacunas.isEmpty()) return;

        // Verificar si el cliente ya especifico una vacuna al agendar
        Control_vacunas preRegistro = null;
        if (cita.getMascota() != null && cita.getFechaCita() != null) {
            for (Control_vacunas cv : Control_vacunas.consultarTodosBD()) {
                if (cv.getMascota() != null
                        && cv.getMascota().getId().equals(cita.getMascota().getId())
                        && cita.getFechaCita().equals(cv.getFechaAplicacion())) {
                    preRegistro = cv;
                    break;
                }
            }
        }
        if (preRegistro != null) {
            // Ya existe — informar al admin que el cliente ya especifico la vacuna
            int resp = JOptionPane.showConfirmDialog(panel,
                    "<html>El cliente ya indicó la vacuna: <b>" + preRegistro.getVacuna().getNombre() + "</b><br>" +
                    "¿Deseas cambiarla o dejar la que el cliente eligio?</html>",
                    "Vacuna preseleccionada por el cliente",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (resp != JOptionPane.YES_OPTION) return; // dejar la que el cliente eligio
        }

        Window owner = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog = new JDialog(owner,
                "Registrar vacuna para " + cita.getMascota().getNombre(),
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(480, 330);
        dialog.setLocationRelativeTo(panel);
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 6, 8, 6);

        String nombreMascota = cita.getMascota().getNombre()
                + (cita.getMascota().getCliente() != null
                   ? " (" + cita.getMascota().getCliente().getNombre() + ")" : "");

        JComboBox<Vacunas> cmbVacuna = new JComboBox<>(vacunas.toArray(new Vacunas[0]));
        cmbVacuna.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object v, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, v, idx, sel, focus);
                if (v instanceof Vacunas) setText(((Vacunas) v).getNombre());
                return this;
            }
        });
        // Pre-seleccionar si el cliente eligio una vacuna
        final Control_vacunas preReg = preRegistro;
        if (preReg != null) {
            for (int i = 0; i < vacunas.size(); i++) {
                if (vacunas.get(i).getId().equals(preReg.getVacuna().getId())) {
                    cmbVacuna.setSelectedIndex(i); break;
                }
            }
        }

        // Banner informativo si el cliente eligio vacuna
        int filaBase = 0;
        if (preReg != null) {
            gbc.gridx = 0; gbc.gridy = filaBase; gbc.gridwidth = 2; gbc.weightx = 1.0;
            JLabel lClienteEligio = new JLabel("<html><b style='color:#15803d'>El cliente indicó: " +
                    preReg.getVacuna().getNombre() + "</b> — puedes cambiarlo abajo.</html>");
            lClienteEligio.setFont(new Font("Arial", Font.PLAIN, 11));
            lClienteEligio.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(22, 163, 74), 1),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            form.add(lClienteEligio, gbc);
            gbc.gridwidth = 1;
            filaBase = 1;
        }

        Object[][] filas = {
            {"Mascota:",    new JLabel(nombreMascota)},
            {"Tipo de vacuna:", cmbVacuna},
        };
        for (int i = 0; i < filas.length; i++) {
            gbc.gridx = 0; gbc.gridy = filaBase + i; gbc.weightx = 0.3;
            JLabel lbl = new JLabel((String) filas[i][0]);
            lbl.setFont(new Font("Arial", Font.BOLD, 12)); lbl.setForeground(C[6]);
            form.add(lbl, gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            Component comp = (Component) filas[i][1];
            comp.setFont(new Font("Arial", Font.PLAIN, 13));
            form.add(comp, gbc);
        }

        gbc.gridx = 0; gbc.gridy = filaBase + 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        JLabel info = new JLabel("<html><i>La vacuna quedará pendiente hasta que el cliente la reciba.</i></html>");
        info.setFont(new Font("Arial", Font.PLAIN, 11)); info.setForeground(C[7]);
        form.add(info, gbc);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btns.setBackground(C[2]);
        JButton btnOmitir = new JButton("Omitir");
        btnOmitir.setFont(new Font("Arial", Font.PLAIN, 13)); btnOmitir.setBackground(C[2]);
        btnOmitir.setForeground(C[6]); btnOmitir.setOpaque(true);
        btnOmitir.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1), BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        btnOmitir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) { dialog.dispose(); }
        });

        JButton btnGuardar = new JButton("Registrar vacuna");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(22, 163, 74)); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setOpaque(true); btnGuardar.setBorderPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                Vacunas vacSel = (Vacunas) cmbVacuna.getSelectedItem();
                if (vacSel == null) { JOptionPane.showMessageDialog(dialog, "Selecciona una vacuna."); return; }
                Control_vacunas cv = new Control_vacunas();
                cv.setMascota(cita.getMascota());
                cv.setVacuna(vacSel);
                cv.setFechaAplicacion(cita.getFechaCita());
                cv.setProximaDosis(null);
                try {
                    new CitaService().guardarVacuna(cv);
                    JOptionPane.showMessageDialog(dialog,
                            "Vacuna registrada. Aparecerá como Pendiente hasta que el cliente sea atendido.",
                            "Vacuna registrada", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error al registrar: " + ex.getMessage());
                }
            }
        });
        btns.add(btnOmitir); btns.add(btnGuardar);

        gbc.gridy = 3;
        form.add(btns, gbc);
        dialog.add(form);
        dialog.setVisible(true);
    }

}
