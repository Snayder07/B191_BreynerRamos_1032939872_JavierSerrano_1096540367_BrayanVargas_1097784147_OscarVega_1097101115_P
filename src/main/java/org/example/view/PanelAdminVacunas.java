package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.controller.VacunaAdminController;
import org.example.model.Control_vacunas;
import org.example.model.Mascotas;
import org.example.model.Vacunas;
import org.example.service.ControlVacunaService;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PanelAdminVacunas {
    public JPanel panel;
    private boolean temaOscuro = false;
    private JTable tabla;
    private List<Control_vacunas> lista = null;

    private final VacunaAdminController ctrl = new VacunaAdminController();
    private final ControlVacunaService  svc  = new ControlVacunaService();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color[] CLARO = {
            new Color(240,253,244), new Color(22,101,52),  Color.WHITE,          new Color(34,120,70),
            new Color(220,245,230), Color.WHITE,            new Color(15,60,30),  new Color(100,130,110),
            new Color(234,88,12),   new Color(187,224,200), new Color(15,60,30),  new Color(134,190,155),
            new Color(220,38,38),   new Color(22,163,74),   new Color(210,240,220),
    };
    private final Color[] OSCURO = {
            new Color(18,24,38),   new Color(13,18,30),   new Color(26,34,52),  new Color(37,55,90),
            new Color(32,42,64),   Color.WHITE,            new Color(226,232,240), new Color(148,163,184),
            new Color(251,146,60), new Color(30,41,59),   new Color(9,14,24),   new Color(122,175,212),
            new Color(239,68,68),  new Color(34,197,94),  new Color(15,23,42),
    };
    private Color[] C = CLARO;

    public PanelAdminVacunas() { panel = new JPanel(new BorderLayout()); construir(); }
    public void setTema(boolean o) { if (o != temaOscuro) { temaOscuro = o; lista = null; construir(); } }
    public void recargar()        { lista = null; construir(); }

    private void construir() {
        panel.removeAll(); C = temaOscuro ? OSCURO : CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, temaOscuro, "adminVacunas", panel), BorderLayout.WEST);

        if (lista != null) {
            panel.add(crearContenido(), BorderLayout.CENTER);
            panel.revalidate(); panel.repaint();
            return;
        }

        JPanel cargando = crearPanelCargando();
        panel.add(cargando, BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();

        new SwingWorker<List<Control_vacunas>, Void>() {
            @Override protected List<Control_vacunas> doInBackground() { return ctrl.listarTodas(); }
            @Override protected void done() {
                try   { lista = get(); }
                catch (InterruptedException | ExecutionException e) { lista = Collections.emptyList(); }
                panel.remove(cargando);
                panel.add(crearContenido(), BorderLayout.CENTER);
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

    private JButton btnPrimario(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Arial", Font.BOLD, 13)); b.setBackground(C[1]); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setFocusPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    private JButton btnDanger(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Arial", Font.BOLD, 13)); b.setBackground(C[12]); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setFocusPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return b;
    }

    private JPanel crearContenido() {
        long vencidas = lista.stream().filter(cv -> "Vencida".equals(svc.calcularEstado(cv.getProximaDosis()))).count();
        long proximas = lista.stream().filter(cv -> "Próxima".equals(svc.calcularEstado(cv.getProximaDosis()))).count();
        long alDia    = lista.stream().filter(cv -> "Al día" .equals(svc.calcularEstado(cv.getProximaDosis()))).count();

        JPanel c = new JPanel(new BorderLayout()); c.setBackground(C[0]);

        // ── Topbar ────────────────────────────────────────────────────
        JPanel tb = new JPanel(new BorderLayout()); tb.setBackground(C[2]);
        tb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, C[9]),
                BorderFactory.createEmptyBorder(16, 28, 16, 28)));
        JPanel tl = new JPanel(new GridLayout(2, 1)); tl.setBackground(C[2]);
        tl.add(lbl("Vacunas", 22, Font.BOLD, C[6]));
        tl.add(lbl("Control y seguimiento del plan de vacunación", 12, Font.PLAIN, C[7]));
        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); tr.setBackground(C[2]);
        JButton btnNueva = btnPrimario("+ Nueva vacuna");
        btnNueva.addActionListener(e -> abrirFormulario(null));
        tr.add(btnNueva);
        tb.add(tl, BorderLayout.WEST); tb.add(tr, BorderLayout.EAST);
        c.add(tb, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 20));
        body.setBackground(C[0]); body.setBorder(BorderFactory.createEmptyBorder(24, 28, 28, 28));

        // ── Stats ─────────────────────────────────────────────────────
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0)); stats.setBackground(C[0]);
        Object[][] st = {
                {"Vencidas",           String.valueOf(vencidas), C[12]},
                {"Próximas (30 días)", String.valueOf(proximas), C[8]},
                {"Al día",             String.valueOf(alDia),    C[13]},
        };
        for (Object[] s : st) {
            JPanel card = new JPanel(new BorderLayout(0, 4)); card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(C[9], 1),
                    BorderFactory.createEmptyBorder(18, 20, 18, 20)));
            card.add(lbl((String) s[0], 11, Font.PLAIN, C[7]), BorderLayout.NORTH);
            card.add(lbl((String) s[1], 28, Font.BOLD, (Color) s[2]), BorderLayout.CENTER);
            stats.add(card);
        }
        body.add(stats, BorderLayout.NORTH);

        // ── Tabla ─────────────────────────────────────────────────────
        String[] cols = {"Mascota", "Dueño", "Vacuna", "Fecha aplicación", "Próxima dosis", "Estado"};
        Object[][] datos = new Object[lista.size()][6];
        for (int i = 0; i < lista.size(); i++) {
            Control_vacunas cv = lista.get(i);
            datos[i] = new Object[]{
                cv.getMascota() != null ? cv.getMascota().getNombre() : "—",
                (cv.getMascota() != null && cv.getMascota().getCliente() != null)
                    ? cv.getMascota().getCliente().getNombre() : "—",
                cv.getVacuna() != null ? cv.getVacuna().getNombre() : "—",
                cv.getFechaAplicacion() != null ? cv.getFechaAplicacion().format(FMT) : "—",
                cv.getProximaDosis()    != null ? cv.getProximaDosis().format(FMT)    : "—",
                svc.calcularEstado(cv.getProximaDosis())
            };
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, cols) {
            public boolean isCellEditable(int r, int cc) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setBackground(C[2]); tabla.setForeground(C[6]);
        tabla.setFont(new Font("Arial", Font.PLAIN, 13)); tabla.setRowHeight(40);
        tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(C[3]); tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tabla.getTableHeader();
        th.setBackground(C[14]); th.setForeground(temaOscuro ? C[7] : C[1]);
        th.setFont(new Font("Arial", Font.BOLD, 11));
        th.setReorderingAllowed(false); th.setPreferredSize(new Dimension(0, 36));

        int[] anchos = {120, 140, 140, 130, 130, 100};
        for (int i = 0; i < anchos.length; i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, col);
                l.setFont(new Font("Arial", Font.BOLD, 12)); l.setHorizontalAlignment(SwingConstants.CENTER);
                switch (v != null ? v.toString() : "") {
                    case "Vencida": l.setForeground(C[12]); break;
                    case "Próxima": l.setForeground(C[8]);  break;
                    default:        l.setForeground(C[13]);
                }
                l.setBackground(s ? C[3] : (r % 2 == 0 ? C[2] : C[4])); l.setOpaque(true); return l;
            }
        });
        DefaultTableCellRenderer base = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int col) {
                super.getTableCellRendererComponent(t, v, s, f, r, col);
                setForeground(C[6]); setBackground(r % 2 == 0 ? C[2] : C[4]);
                if (s) setBackground(C[3]);
                setFont(new Font("Arial", Font.PLAIN, 13)); setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 14)); return this;
            }
        };
        for (int i = 0; i < 5; i++) tabla.getColumnModel().getColumn(i).setCellRenderer(base);

        // Doble clic → editar
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tabla.getSelectedRow() >= 0)
                    abrirFormulario(lista.get(tabla.getSelectedRow()));
            }
        });

        // ── Botones de acción ─────────────────────────────────────────
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        acciones.setBackground(C[2]);
        JButton btnEditar   = btnPrimario("Editar");
        JButton btnEliminar = btnDanger("Eliminar");
        btnEditar.setEnabled(false); btnEliminar.setEnabled(false);

        tabla.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                boolean sel = tabla.getSelectedRow() >= 0;
                btnEditar.setEnabled(sel); btnEliminar.setEnabled(sel);
            }
        });
        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) abrirFormulario(lista.get(row));
        });
        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) return;
            Control_vacunas cv = lista.get(row);
            String nom = cv.getMascota() != null ? cv.getMascota().getNombre() : "esta mascota";
            int conf = JOptionPane.showConfirmDialog(panel,
                    "¿Eliminar el registro de vacuna de " + nom + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try { ctrl.eliminar(cv.getId()); recargar(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage()); }
            }
        });
        acciones.add(btnEditar); acciones.add(btnEliminar);

        JScrollPane sp = new JScrollPane(tabla);
        sp.setBorder(null); sp.getViewport().setBackground(C[2]);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(C[2]);
        wrapper.add(sp, BorderLayout.CENTER);
        wrapper.add(acciones, BorderLayout.SOUTH);
        body.add(wrapper, BorderLayout.CENTER);

        JScrollPane outerScroll = new JScrollPane(body);
        outerScroll.setBorder(null); outerScroll.getViewport().setBackground(C[0]);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(outerScroll, BorderLayout.CENTER);
        return c;
    }

    private void abrirFormulario(Control_vacunas existing) {
        boolean editando = existing != null;
        Window owner = SwingUtilities.getWindowAncestor(panel);
        JDialog dialog = new JDialog(owner,
                editando ? "Editar registro de vacuna" : "Registrar vacuna",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 380);
        dialog.setLocationRelativeTo(panel);
        dialog.setResizable(false);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(7, 6, 7, 6);

        // Cargar catálogos
        List<Mascotas> mascotas = ctrl.listarMascotas();
        List<Vacunas>  vacunas  = ctrl.listarVacunas();

        JComboBox<Mascotas> cmbMascota = new JComboBox<>(mascotas.toArray(new Mascotas[0]));
        cmbMascota.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object v, int idx, boolean sel, boolean focus) {
                super.getListCellRendererComponent(list, v, idx, sel, focus);
                if (v instanceof Mascotas) setText(((Mascotas) v).getEtiqueta());
                return this;
            }
        });

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

        // Pre-poblar si es edición
        if (editando) {
            for (Mascotas m : mascotas) {
                if (m.getId().equals(existing.getMascota().getId())) { cmbMascota.setSelectedItem(m); break; }
            }
            for (Vacunas v : vacunas) {
                if (v.getId().equals(existing.getVacuna().getId())) { cmbVacuna.setSelectedItem(v); break; }
            }
            if (existing.getFechaAplicacion() != null)
                dtAplic.setDate(java.sql.Date.valueOf(existing.getFechaAplicacion()));
            if (existing.getProximaDosis() != null)
                dtProx.setDate(java.sql.Date.valueOf(existing.getProximaDosis()));
        }

        // Layout del formulario
        Object[][] filas = {
            {"Mascota:",          cmbMascota},
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

        // Botones
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        btns.setBackground(C[2]);
        JButton btnCancelar = new JButton("Cancelar");
        estilizarTema(btnCancelar);
        JButton btnGuardar = btnPrimario("Guardar");

        btnCancelar.addActionListener(e -> dialog.dispose());
        btnGuardar.addActionListener(e -> {
            Mascotas mascSel = (Mascotas) cmbMascota.getSelectedItem();
            Vacunas  vacSel  = (Vacunas)  cmbVacuna.getSelectedItem();
            java.util.Date dAplic = dtAplic.getDate();
            java.util.Date dProx  = dtProx.getDate();

            if (mascSel == null) { JOptionPane.showMessageDialog(dialog, "Seleccione una mascota."); return; }
            if (vacSel  == null) { JOptionPane.showMessageDialog(dialog, "Seleccione una vacuna.");  return; }
            if (dAplic  == null) { JOptionPane.showMessageDialog(dialog, "La fecha de aplicación es obligatoria."); return; }

            Control_vacunas cv = editando ? existing : new Control_vacunas();
            cv.setMascota(mascSel);
            cv.setVacuna(vacSel);
            cv.setFechaAplicacion(dAplic.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            cv.setProximaDosis(dProx != null ? dProx.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null);

            try {
                if (editando) ctrl.actualizar(cv);
                else          ctrl.guardar(cv);
                dialog.dispose();
                recargar();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al guardar: " + ex.getMessage());
            }
        });
        btns.add(btnCancelar); btns.add(btnGuardar);

        gbc.gridx = 0; gbc.gridy = filas.length + 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        form.add(btns, gbc);

        dialog.add(form);
        dialog.setVisible(true);
    }

    private void estilizarTema(JButton b) {
        b.setFont(new Font("Arial", Font.PLAIN, 13)); b.setBackground(C[2]); b.setForeground(C[6]);
        b.setOpaque(true); b.setFocusPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9], 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)));
    }
}
