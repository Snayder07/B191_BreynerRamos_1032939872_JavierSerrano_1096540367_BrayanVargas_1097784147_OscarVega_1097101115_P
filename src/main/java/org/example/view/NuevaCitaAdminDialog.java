package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.model.*;
import org.example.service.CorreoService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Dialog para que el admin registre una cita manualmente,
 * incluyendo datos del dueno y mascota para personas
 * que no usan la app.
 */
public class NuevaCitaAdminDialog extends JDialog {

    private static final Color VERDE    = new Color(22, 101, 52);
    private static final Color VERDE_LT = new Color(240, 253, 244);
    private static final Color GRIS     = new Color(100, 116, 139);
    private static final Color BORDE    = new Color(187, 224, 200);

    private boolean guardado = false;

    public NuevaCitaAdminDialog(Window owner) {
        super(owner, "Nueva Cita", ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        construir();
        pack();
        setLocationRelativeTo(owner);
    }

    public boolean fueGuardado() { return guardado; }

    private void construir() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(VERDE_LT);
        root.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        // ── Titulo ────────────────────────────────────────
        JLabel titulo = new JLabel("Registrar nueva cita");
        titulo.setFont(new Font("Arial", Font.BOLD, 17));
        titulo.setForeground(VERDE);
        titulo.setAlignmentX(LEFT_ALIGNMENT);
        root.add(titulo);

        root.add(Box.createVerticalStrut(4));
        JLabel sub = new JLabel("<html><font color='#64748b'>Selecciona el cliente y su mascota para agendar la cita.</font></html>");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setAlignmentX(LEFT_ALIGNMENT);
        root.add(sub);
        root.add(Box.createVerticalStrut(20));

        // ── Seccion: Cliente y mascota ────────────────────
        root.add(seccion("Cliente y mascota"));
        root.add(Box.createVerticalStrut(8));

        // Combo de clientes
        List<Cliente> clientesList = Cliente.consultarTodosBD();
        JComboBox<Object> cbCliente = new JComboBox<>();
        cbCliente.addItem("-- Selecciona un cliente --");
        for (Cliente cl : clientesList) cbCliente.addItem(cl);
        cbCliente.setFont(new Font("Arial", Font.PLAIN, 13));
        cbCliente.setBackground(Color.WHITE);
        cbCliente.setAlignmentX(LEFT_ALIGNMENT);
        cbCliente.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbCliente.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Cliente) setText(((Cliente)v).getNombre());
                return this;
            }
        });
        root.add(filaCombo("Cliente *", cbCliente));
        root.add(Box.createVerticalStrut(8));

        // Combo de mascotas filtrado por cliente (recarga desde BD cada vez)
        JComboBox<Object> cbMascota = new JComboBox<>();
        cbMascota.addItem("-- Selecciona primero un cliente --");
        cbMascota.setEnabled(false);
        cbMascota.setFont(new Font("Arial", Font.PLAIN, 13));
        cbMascota.setBackground(Color.WHITE);
        cbMascota.setAlignmentX(LEFT_ALIGNMENT);
        cbMascota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbMascota.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Mascotas) setText(((Mascotas)v).getEtiqueta());
                return this;
            }
        });
        root.add(filaCombo("Mascota *", cbMascota));
        root.add(Box.createVerticalStrut(18));

        // Filtrar mascotas consultando BD cada vez que cambia el cliente
        cbCliente.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                cbMascota.removeAllItems();
                Object sel = cbCliente.getSelectedItem();
                if (sel instanceof Cliente) {
                    Cliente cl = (Cliente) sel;
                    List<Mascotas> frescas = Mascotas.consultarTodosBD();
                    boolean hayMascotas = false;
                    for (Mascotas m : frescas) {
                        if (m.getCliente() != null && m.getCliente().getId().equals(cl.getId())) {
                            cbMascota.addItem(m);
                            hayMascotas = true;
                        }
                    }
                    if (!hayMascotas) cbMascota.addItem("-- Este cliente no tiene mascotas registradas --");
                    cbMascota.setEnabled(hayMascotas);
                } else {
                    cbMascota.addItem("-- Selecciona primero un cliente --");
                    cbMascota.setEnabled(false);
                }
            }
        });

        // ── Seccion: Datos de la cita ─────────────────────
        root.add(seccion("Datos de la cita"));
        root.add(Box.createVerticalStrut(8));

        List<Servicio> serviciosList = Servicio.consultarTodosBD();
        JComboBox<Object> cbMotivo = new JComboBox<>();
        cbMotivo.addItem("-- Selecciona un servicio --");
        for (Servicio s : serviciosList) cbMotivo.addItem(s);
        cbMotivo.setFont(new Font("Arial", Font.PLAIN, 13));
        cbMotivo.setBackground(Color.WHITE);
        cbMotivo.setAlignmentX(LEFT_ALIGNMENT);
        cbMotivo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbMotivo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof Servicio) setText(((Servicio) v).getNombre());
                return this;
            }
        });
        root.add(filaCombo("Servicio *", cbMotivo));
        root.add(Box.createVerticalStrut(8));

        // Fecha con JDateChooser (igual que el panel de cliente)
        JPanel filaFechaHora = new JPanel(new GridLayout(1, 2, 16, 0));
        filaFechaHora.setBackground(VERDE_LT);
        filaFechaHora.setAlignmentX(LEFT_ALIGNMENT);
        filaFechaHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel colFecha = new JPanel();
        colFecha.setLayout(new BoxLayout(colFecha, BoxLayout.Y_AXIS));
        colFecha.setBackground(VERDE_LT);
        colFecha.add(label("Fecha *"));
        colFecha.add(Box.createVerticalStrut(3));
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 13));
        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new Date());
        dateChooser.setMinSelectableDate(new Date());
        dateChooser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        dateChooser.setAlignmentX(LEFT_ALIGNMENT);
        dateChooser.addPropertyChangeListener("date", new java.beans.PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                Date sel = dateChooser.getDate();
                if (sel != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(sel);
                    if (cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.SUNDAY) {
                        JOptionPane.showMessageDialog(NuevaCitaAdminDialog.this,
                                "Los domingos no están disponibles para citas.\nSelecciona un día de lunes a sábado.",
                                "Día no disponible", JOptionPane.WARNING_MESSAGE);
                        dateChooser.setDate(null);
                    }
                }
            }
        });
        colFecha.add(dateChooser);

        // Hora con JComboBox (igual que el panel de cliente)
        JPanel colHora = new JPanel();
        colHora.setLayout(new BoxLayout(colHora, BoxLayout.Y_AXIS));
        colHora.setBackground(VERDE_LT);
        colHora.add(label("Hora *"));
        colHora.add(Box.createVerticalStrut(3));
        java.util.List<String> listaHoras = new java.util.ArrayList<>();
        listaHoras.add("Selecciona hora...");
        for (int h = 7; h < 20; h++) {
            for (int m = 0; m < 60; m += 30) {
                listaHoras.add(String.format("%02d:%02d", h, m));
            }
        }
        JComboBox<String> cbHora = new JComboBox<>(listaHoras.toArray(new String[0]));
        cbHora.setFont(new Font("Arial", Font.PLAIN, 13));
        cbHora.setBackground(Color.WHITE);
        cbHora.setSelectedItem("09:00");
        cbHora.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbHora.setAlignmentX(LEFT_ALIGNMENT);
        colHora.add(cbHora);

        filaFechaHora.add(colFecha);
        filaFechaHora.add(colHora);
        root.add(filaFechaHora);
        root.add(Box.createVerticalStrut(8));

        // Tipo de atencion
        root.add(label("Tipo de atencion *"));
        root.add(Box.createVerticalStrut(3));
        JRadioButton rbPresencial = new JRadioButton("Presencial", true);
        JRadioButton rbDomicilio  = new JRadioButton("Domicilio");
        rbPresencial.setBackground(VERDE_LT);
        rbDomicilio.setBackground(VERDE_LT);
        rbPresencial.setForeground(GRIS);
        rbDomicilio.setForeground(GRIS);
        rbPresencial.setFont(new Font("Arial", Font.PLAIN, 13));
        rbDomicilio.setFont(new Font("Arial", Font.PLAIN, 13));
        ButtonGroup bgTipo = new ButtonGroup();
        bgTipo.add(rbPresencial); bgTipo.add(rbDomicilio);

        JPanel tipoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        tipoPanel.setBackground(VERDE_LT);
        tipoPanel.setAlignmentX(LEFT_ALIGNMENT);
        tipoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tipoPanel.add(rbPresencial);
        tipoPanel.add(rbDomicilio);
        root.add(tipoPanel);
        root.add(Box.createVerticalStrut(8));

        JTextField tfDireccion = campo("");
        tfDireccion.setEnabled(false);
        tfDireccion.setBackground(new Color(220, 220, 220));
        root.add(fila("Direccion (si es domicilio)", tfDireccion));

        rbDomicilio.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tfDireccion.setEnabled(true);
                tfDireccion.setBackground(Color.WHITE);
            }
        });
        rbPresencial.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                tfDireccion.setEnabled(false);
                tfDireccion.setBackground(new Color(220, 220, 220));
                tfDireccion.setText("");
            }
        });

        root.add(Box.createVerticalStrut(24));

        // ── Botones ───────────────────────────────────────
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.setBackground(VERDE_LT);
        botones.setAlignmentX(LEFT_ALIGNMENT);
        botones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JButton btnCancelar = btnSecundario("Cancelar");
        JButton btnGuardar  = btnPrincipal("Guardar cita");
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        root.add(botones);

        // ── Logica ────────────────────────────────────────
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                NuevaCitaAdminDialog.this.dispose();
            }
        });

        // Cargar empleados para asignar (se usa el admin logueado o el primero disponible)
        List<Empleados> empleadosList = Empleados.consultarTodosBD();

        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
            try {
                // Validar cliente y mascota seleccionados
                if (!(cbCliente.getSelectedItem() instanceof Cliente))
                    throw new Exception("Selecciona un cliente.");
                if (!(cbMascota.getSelectedItem() instanceof Mascotas))
                    throw new Exception("Selecciona una mascota del cliente.");

                Cliente cliente      = (Cliente) cbCliente.getSelectedItem();
                Mascotas mascota     = (Mascotas) cbMascota.getSelectedItem();
                String nombreMascota = mascota.getEtiqueta();

                String horaStr      = (String) cbHora.getSelectedItem();
                boolean esDomicilio = rbDomicilio.isSelected();
                String direccion    = tfDireccion.getText().trim();
                Servicio servicioSel = (cbMotivo.getSelectedItem() instanceof Servicio)
                        ? (Servicio) cbMotivo.getSelectedItem() : null;
                String motivo = servicioSel != null ? servicioSel.getNombre() : null;

                if (motivo == null)   throw new Exception("Selecciona el servicio / motivo de la cita.");
                if (dateChooser.getDate() == null) throw new Exception("Selecciona la fecha de la cita.");
                if (horaStr == null || horaStr.startsWith("Selecciona")) throw new Exception("Selecciona la hora de la cita.");
                if (esDomicilio && direccion.isEmpty()) throw new Exception("Ingresa la direccion del domicilio.");

                LocalDate fecha = dateChooser.getDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime hora  = LocalTime.parse(horaStr);

                // Crear cita — veterinario = admin logueado o primero disponible
                Empleados empleado = Main.empleadoActual;
                if (empleado == null && !empleadosList.isEmpty())
                    empleado = empleadosList.get(0);
                if (empleado == null)
                    throw new Exception("No hay empleados registrados. Registra un empleado primero.");

                long citasActivas = 0;
                for (Citas c : Citas.consultarTodosBD()) {
                    if (c.getEstadoCita() != EstadoCita.CANCELADA
                            && c.getEstadoCita() != EstadoCita.COMPLETADA) citasActivas++;
                }
                boolean hayCupo = citasActivas < 10;
                EstadoCita estadoFinal = hayCupo ? EstadoCita.CONFIRMADA : EstadoCita.PENDIENTE;

                Citas cita = new Citas();
                cita.setMascota(mascota);
                cita.setEmpleado(empleado);
                cita.setFechaCita(fecha);
                cita.setHoraCita(hora);
                cita.setEstadoCita(estadoFinal);
                cita.setMotivo(motivo);
                if (esDomicilio) cita.setDireccionDomicilio(direccion);
                cita.insertarBD();

                // Correo si hay cupo y el cliente tiene correo
                if (hayCupo && cliente.getCorreo() != null && !cliente.getCorreo().isEmpty()) {
                    String cuerpoCorreo =
                            "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                            "<h2 style='color:#16a34a;'>Cita Confirmada</h2>" +
                            "<p>Hola <b>" + cliente.getNombre() + "</b>, tu cita en <b>Kampets Veterinaria</b> fue confirmada.</p>" +
                            "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>" +
                            "<tr><td style='padding:8px 12px;background:#dcfce7;font-weight:bold;'>Mascota</td><td style='padding:8px 12px;'>" + nombreMascota + "</td></tr>" +
                            "<tr><td style='padding:8px 12px;background:#dcfce7;font-weight:bold;'>Fecha</td><td style='padding:8px 12px;'>" + fecha + "</td></tr>" +
                            "<tr><td style='padding:8px 12px;background:#dcfce7;font-weight:bold;'>Hora</td><td style='padding:8px 12px;'>" + hora + "</td></tr>" +
                            "</table></div>";
                    final String cf = cliente.getCorreo(), nf = cliente.getNombre(), bf = cuerpoCorreo;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                CorreoService.enviarCorreoGeneral(cf, nf, "Confirmacion de cita - Kampets", bf);
                            } catch (Exception mailEx) {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        JOptionPane.showMessageDialog(null,
                                                "Cita guardada, pero el correo no se pudo enviar:\n" + mailEx.getMessage(),
                                                "Aviso de correo", JOptionPane.WARNING_MESSAGE);
                                    }
                                });
                            }
                        }
                    }).start();
                }

                guardado = true;
                String estadoMsg = hayCupo ? "Confirmada" : "En lista de espera (sin cupo)";
                JOptionPane.showMessageDialog(NuevaCitaAdminDialog.this,
                        "Cita registrada.\nCliente: " + cliente.getNombre() +
                        "\nMascota: " + nombreMascota +
                        "\nFecha:   " + fecha + "  Hora: " + hora +
                        "\nEstado:  " + estadoMsg,
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
                NuevaCitaAdminDialog.this.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(NuevaCitaAdminDialog.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            }
        });

        setContentPane(root);
    }

    // ── Helpers de UI ─────────────────────────────────────

    private JLabel seccion(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(VERDE);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        return l;
    }

    private JPanel fila(String etiqueta, JTextField campo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(VERDE_LT);
        p.setAlignmentX(LEFT_ALIGNMENT);
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        campo.setAlignmentX(LEFT_ALIGNMENT);
        p.add(label(etiqueta));
        p.add(Box.createVerticalStrut(3));
        p.add(campo);
        return p;
    }

    private JPanel filaCombo(String etiqueta, JComboBox<?> combo) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(VERDE_LT);
        p.setAlignmentX(LEFT_ALIGNMENT);
        combo.setAlignmentX(LEFT_ALIGNMENT);
        p.add(label(etiqueta));
        p.add(Box.createVerticalStrut(3));
        p.add(combo);
        return p;
    }

    private JLabel label(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        l.setForeground(GRIS);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField campo(String valor) {
        JTextField tf = new JTextField(valor);
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        tf.setAlignmentX(LEFT_ALIGNMENT);
        return tf;
    }

    private JButton btnPrincipal(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setBackground(VERDE); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false);
        b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton btnSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setBackground(Color.WHITE); b.setForeground(VERDE);
        b.setOpaque(true); b.setBorder(BorderFactory.createLineBorder(VERDE, 1));
        b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
