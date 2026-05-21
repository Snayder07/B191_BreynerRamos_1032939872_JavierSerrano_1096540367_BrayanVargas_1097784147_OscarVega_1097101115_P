package org.example.view;

import org.example.controller.MascotaAdminController;
import org.example.model.Cliente;
import org.example.model.Especies;
import org.example.model.Mascotas;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class PanelAdminMascotas {
    public JPanel panel;
    private boolean temaOscuro = false;
    private final MascotaAdminController ctrl = new MascotaAdminController();

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

    public PanelAdminMascotas() { panel = new JPanel(new BorderLayout()); construir(); }
    public void setTema(boolean o) { if(o!=temaOscuro){temaOscuro=o;construir();} }
    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll(); C = temaOscuro ? OSCURO : CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, temaOscuro, "adminMascotas", panel), BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JLabel lbl(String t,int sz,int st,Color c){JLabel l=new JLabel(t);l.setFont(new Font("Arial",st,sz+2));l.setForeground(c);return l;}

    private JPanel crearContenido() {
        JPanel c = new JPanel(new BorderLayout()); c.setBackground(C[0]);

        JPanel tb = new JPanel(new BorderLayout()); tb.setBackground(C[2]);
        tb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,C[9]),BorderFactory.createEmptyBorder(16,28,16,28)));
        JPanel tl = new JPanel(new GridLayout(2,1)); tl.setBackground(C[2]);
        tl.add(lbl("Mascotas registradas",22,Font.BOLD,C[6]));
        tl.add(lbl("Gestión de mascotas en el sistema",12,Font.PLAIN,C[7]));
        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); tr.setBackground(C[2]);
        JButton btnNueva = new JButton("+ Registrar mascota");
        btnNueva.setFont(new Font("Arial",Font.BOLD,13)); btnNueva.setBackground(new Color(22,163,74));
        btnNueva.setForeground(Color.WHITE); btnNueva.setOpaque(true); btnNueva.setBorderPainted(false);
        btnNueva.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        btnNueva.setBorder(BorderFactory.createEmptyBorder(9,18,9,18));
        btnNueva.addActionListener(e -> abrirDialogoRegistro());
        tr.add(btnNueva);
        tb.add(tl,BorderLayout.WEST); tb.add(tr,BorderLayout.EAST); c.add(tb,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0,16)); body.setBackground(C[0]); body.setBorder(BorderFactory.createEmptyBorder(24,28,28,28));

        List<Mascotas> todas = ctrl.listarTodas();
        long total  = todas.size();
        long perros = todas.stream().filter(m -> m.getEspecie()!=null && m.getEspecie().getNombre().equalsIgnoreCase("Perro")).count();
        long gatos  = todas.stream().filter(m -> m.getEspecie()!=null && m.getEspecie().getNombre().equalsIgnoreCase("Gato")).count();
        long otros  = total - perros - gatos;

        JPanel stats = new JPanel(new GridLayout(1,4,16,0)); stats.setBackground(C[0]);
        Object[][] st = {{"Total mascotas",String.valueOf(total)},{"Perros",String.valueOf(perros)},{"Gatos",String.valueOf(gatos)},{"Otros",String.valueOf(otros)}};
        for (Object[] s : st) {
            JPanel card = new JPanel(new BorderLayout(0,4)); card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(16,20,16,20)));
            card.add(lbl((String)s[0],11,Font.PLAIN,C[7]),BorderLayout.NORTH);
            card.add(lbl((String)s[1],28,Font.BOLD,C[6]),BorderLayout.CENTER);
            stats.add(card);
        }
        body.add(stats,BorderLayout.NORTH);

        String[] cols = {"Nombre","Especie","Característica","Dueño","Fecha nac.","Sexo"};
        Object[][] datos = new Object[todas.size()][6];
        for (int i = 0; i < todas.size(); i++) {
            Mascotas m = todas.get(i);
            datos[i] = new Object[]{
                    m.getNombre(),
                    m.getEspecie()  != null ? m.getEspecie().getNombre()  : "—",
                    (m.getCaracteristica() != null && !m.getCaracteristica().isBlank()) ? m.getCaracteristica() : "—",
                    m.getCliente()  != null ? m.getCliente().getNombre()  : "—",
                    m.getFechaNac() != null ? m.getFechaNac().toString()  : "—",
                    m.getSexo()     != null ? m.getSexo()                : "—"
            };
        }

        DefaultTableModel modelo = new DefaultTableModel(datos,cols){public boolean isCellEditable(int r,int cc){return false;}};
        JTable tabla = new JTable(modelo);
        tabla.setBackground(C[2]); tabla.setForeground(C[6]);
        tabla.setFont(new Font("Arial",Font.PLAIN,13)); tabla.setRowHeight(40);
        tabla.setShowGrid(false); tabla.setIntercellSpacing(new Dimension(0,0));
        tabla.setSelectionBackground(C[3]); tabla.setFillsViewportHeight(true);
        JTableHeader th = tabla.getTableHeader(); th.setBackground(C[14]); th.setForeground(temaOscuro?C[7]:C[1]);
        th.setFont(new Font("Arial",Font.BOLD,11)); th.setReorderingAllowed(false); th.setPreferredSize(new Dimension(0,36));

        DefaultTableCellRenderer base = new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int col){
                super.getTableCellRendererComponent(t,v,s,f,r,col); setForeground(C[6]);
                setBackground(r%2==0?C[2]:C[4]); if(s)setBackground(C[3]);
                setFont(new Font("Arial",Font.PLAIN,13)); setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0,14,0,14)); return this;
            }
        };
        for(int i=0;i<cols.length;i++) tabla.getColumnModel().getColumn(i).setCellRenderer(base);
        int[] anchos = {120, 100, 160, 160, 100, 70};
        for(int i=0;i<anchos.length;i++) tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        JScrollPane sp = new JScrollPane(tabla); sp.setBorder(null); sp.getViewport().setBackground(C[2]); sp.getVerticalScrollBar().setUnitIncrement(16);
        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setBackground(C[2]); wrapper.add(sp,BorderLayout.CENTER);
        body.add(wrapper,BorderLayout.CENTER);
        JScrollPane outerScroll = new JScrollPane(body);
        outerScroll.setBorder(null); outerScroll.getViewport().setBackground(C[0]);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(outerScroll,BorderLayout.CENTER); return c;
    }

    private void abrirDialogoRegistro() {
        JDialog dlg = new JDialog((Frame) SwingUtilities.getWindowAncestor(panel), "Registrar mascota", true);
        dlg.setSize(480, 580);
        dlg.setLocationRelativeTo(panel);
        dlg.setResizable(false);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(C[2]);
        form.setBorder(BorderFactory.createEmptyBorder(28,32,28,32));

        form.add(campLbl("Nombre de la mascota *"));
        JTextField tfNombre = campo(); form.add(tfNombre); form.add(Box.createVerticalStrut(14));

        form.add(campLbl("Especie *"));
        List<Especies> especies = ctrl.listarEspecies();
        JComboBox<Especies> cbEspecie = new JComboBox<>();
        cbEspecie.addItem(null);
        for (Especies esp : especies) cbEspecie.addItem(esp);
        cbEspecie.setRenderer(new DefaultListCellRenderer(){
            public java.awt.Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
                super.getListCellRendererComponent(l,v,i,s,f);
                setText(v instanceof Especies ? ((Especies)v).getNombre() : "Selecciona una especie...");
                setBackground(s?C[3]:C[2]); setForeground(C[6]); return this;
            }
        });
        estilizarCombo(cbEspecie); form.add(cbEspecie); form.add(Box.createVerticalStrut(14));

        form.add(campLbl("Dueño *"));
        List<Cliente> clientes = ctrl.listarClientes();
        JComboBox<Cliente> cbCliente = new JComboBox<>();
        cbCliente.addItem(null);
        for (Cliente cl : clientes) cbCliente.addItem(cl);
        cbCliente.setRenderer(new DefaultListCellRenderer(){
            public java.awt.Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
                super.getListCellRendererComponent(l,v,i,s,f);
                setText(v instanceof Cliente ? ((Cliente)v).getNombre() : "Selecciona un dueño...");
                setBackground(s?C[3]:C[2]); setForeground(C[6]); return this;
            }
        });
        estilizarCombo(cbCliente); form.add(cbCliente); form.add(Box.createVerticalStrut(14));

        form.add(campLbl("Fecha de nacimiento (yyyy-MM-dd)"));
        JTextField tfFecha = campo(); tfFecha.setToolTipText("Ejemplo: 2021-03-15");
        form.add(tfFecha); form.add(Box.createVerticalStrut(14));

        form.add(campLbl("Sexo"));
        JComboBox<String> cbSexo = new JComboBox<>(new String[]{"","Macho","Hembra"});
        estilizarCombo(cbSexo); form.add(cbSexo); form.add(Box.createVerticalStrut(14));

        form.add(campLbl("Característica diferenciadora (opcional)"));
        JTextField tfCar = campo(); tfCar.setToolTipText("Ej: pelaje dorado, collar azul, mancha en la oreja...");
        form.add(tfCar);
        JLabel hint = new JLabel("<html><i>Solo requerida si otra mascota tiene el mismo nombre y especie.</i></html>");
        hint.setFont(new Font("Arial",Font.PLAIN,11)); hint.setForeground(C[7]);
        hint.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        // Mensaje de error inline para duplicados
        JLabel lblError = new JLabel("");
        lblError.setFont(new Font("Arial",Font.BOLD,11)); lblError.setForeground(new Color(220,38,38));
        lblError.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        lblError.setVisible(false);
        // Al escribir, limpiar el resaltado de error
        Color bordeNormal = C[9];
        Color bordeError  = new Color(220,38,38);
        tfCar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void limpiar() {
                tfCar.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(bordeNormal,1), BorderFactory.createEmptyBorder(8,10,8,10)));
                lblError.setVisible(false);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
        });
        form.add(hint); form.add(Box.createVerticalStrut(2));
        form.add(lblError); form.add(Box.createVerticalStrut(18));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0));
        btnRow.setBackground(C[2]); btnRow.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        JButton btnCancel = new JButton("Cancelar");
        btnCancel.setBackground(C[0]); btnCancel.setForeground(C[6]);
        btnCancel.setFont(new Font("Arial",Font.PLAIN,13)); btnCancel.setOpaque(true);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(8,16,8,16)));
        btnCancel.addActionListener(e -> dlg.dispose());

        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.setBackground(new Color(22,163,74)); btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial",Font.BOLD,13)); btnGuardar.setOpaque(true); btnGuardar.setBorderPainted(false);
        btnGuardar.setBorder(BorderFactory.createEmptyBorder(8,18,8,18));
        btnGuardar.addActionListener(e -> {
            Especies esp = cbEspecie.getSelectedItem() instanceof Especies ? (Especies)cbEspecie.getSelectedItem() : null;
            Cliente cl   = cbCliente.getSelectedItem() instanceof Cliente  ? (Cliente)cbCliente.getSelectedItem()  : null;
            String sexo  = (String)cbSexo.getSelectedItem();
            boolean ok = ctrl.registrarMascota(
                    tfNombre.getText(), esp, cl,
                    tfFecha.getText(), (sexo==null||sexo.isBlank())?null:sexo,
                    tfCar.getText(), form,
                    () -> {
                        // Resaltar campo en rojo y enfocar
                        tfCar.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(bordeError, 2),
                                BorderFactory.createEmptyBorder(8,10,8,10)));
                        lblError.setText("⚠ Este campo es obligatorio para distinguir la mascota");
                        lblError.setVisible(true);
                        tfCar.requestFocusInWindow();
                        form.revalidate();
                    });
            if (ok) {
                dlg.dispose();
                recargar();
                int resp = JOptionPane.showConfirmDialog(panel,
                    "Mascota registrada.\n¿Deseas ir a registrar las vacunas de " + tfNombre.getText().trim() + " ahora?",
                    "Registrar vacunas", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resp == JOptionPane.YES_OPTION) {
                    Main.cambiarPantalla("adminVacunas");
                }
            }
        });
        btnRow.add(btnCancel); btnRow.add(btnGuardar);
        form.add(btnRow);

        JScrollPane scroll = new JScrollPane(form); scroll.setBorder(null); scroll.getViewport().setBackground(C[2]);
        dlg.add(scroll);
        dlg.setVisible(true);
    }

    private JLabel campLbl(String t){JLabel l=new JLabel(t);l.setFont(new Font("Arial",Font.BOLD,12));l.setForeground(C[6]);l.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);return l;}
    private JTextField campo(){JTextField tf=new JTextField();tf.setFont(new Font("Arial",Font.PLAIN,13));tf.setBackground(C[0]);tf.setForeground(C[6]);tf.setCaretColor(C[6]);tf.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(8,10,8,10)));tf.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));tf.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);return tf;}
    private void estilizarCombo(JComboBox<?> cb){cb.setFont(new Font("Arial",Font.PLAIN,13));cb.setBackground(C[0]);cb.setForeground(C[6]);cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));cb.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);}
}
