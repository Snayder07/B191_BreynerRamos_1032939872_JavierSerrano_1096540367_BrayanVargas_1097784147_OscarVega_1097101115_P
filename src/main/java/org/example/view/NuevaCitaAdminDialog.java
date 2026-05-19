package org.example.view;

import com.toedter.calendar.JDateChooser;
import org.example.model.*;
import org.example.repository.*;
import org.example.service.CitaService;
import org.example.service.CorreoService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
        JLabel sub = new JLabel("<html><font color='#64748b'>Completa los datos del dueno y la mascota.</font></html>");
        sub.setFont(new Font("Arial", Font.PLAIN, 12));
        sub.setAlignmentX(LEFT_ALIGNMENT);
        root.add(sub);
        root.add(Box.createVerticalStrut(20));

        // ── Seccion: Datos del dueno ──────────────────────
        root.add(seccion("Datos del dueno"));
        root.add(Box.createVerticalStrut(8));

        JTextField tfNombreDueno = campo("");
        JTextField tfCelular     = campo("");
        JTextField tfCorreo      = campo("");

        root.add(fila("Nombre del dueno *", tfNombreDueno));
        root.add(Box.createVerticalStrut(8));
        root.add(fila("Celular", tfCelular));
        root.add(Box.createVerticalStrut(8));
        root.add(fila("Correo (opcional)", tfCorreo));
        root.add(Box.createVerticalStrut(18));

        // ── Seccion: Datos de la mascota ──────────────────
        root.add(seccion("Datos de la mascota"));
        root.add(Box.createVerticalStrut(8));

        // Combo: elegir mascota existente
        List<Mascotas> mascotasList = new MascotaRepositoryImpl().buscarTodos();
        JComboBox<Object> cbMascota = new JComboBox<>();
        cbMascota.addItem("-- Nueva mascota (no registrada) --");
        for (Mascotas m : mascotasList) cbMascota.addItem(m);
        cbMascota.setFont(new Font("Arial", Font.PLAIN, 13));
        cbMascota.setBackground(Color.WHITE);
        cbMascota.setAlignmentX(LEFT_ALIGNMENT);
        cbMascota.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cbMascota.setRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList<?> l,Object v,int i,boolean s,boolean f){
                super.getListCellRendererComponent(l,v,i,s,f);
                if (v instanceof Mascotas) setText(((Mascotas)v).getEtiqueta());
                return this;
            }
        });
        root.add(filaCombo("Seleccionar mascota registrada", cbMascota));
        root.add(Box.createVerticalStrut(6));

        JLabel lblOSep = new JLabel("— o ingresa datos de mascota nueva —");
        lblOSep.setFont(new Font("Arial", Font.ITALIC, 11));
        lblOSep.setForeground(GRIS);
        lblOSep.setAlignmentX(LEFT_ALIGNMENT);
        root.add(lblOSep);
        root.add(Box.createVerticalStrut(6));

        JTextField tfNombreMascota = campo("");

        // Cargar especies desde BD
        List<Especies> especiesList = new EspecieRepositoryImpl().buscarTodos();
        String[] nombresEspecies = especiesList.stream()
                .map(Especies::getNombre)
                .toArray(String[]::new);
        if (nombresEspecies.length == 0) nombresEspecies = new String[]{"Perro", "Gato", "Otro"};

        JComboBox<String> cbEspecie = new JComboBox<>(nombresEspecies);
        cbEspecie.setFont(new Font("Arial", Font.PLAIN, 13));
        cbEspecie.setBackground(Color.WHITE);
        cbEspecie.setAlignmentX(LEFT_ALIGNMENT);
        cbEspecie.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JTextField tfCaracteristica = campo("");
        tfCaracteristica.setToolTipText("Solo requerida si ya existe otra mascota con el mismo nombre y especie");
        // Mensaje de error inline para duplicados
        JLabel lblCarError = new JLabel("");
        lblCarError.setFont(new Font("Arial", Font.BOLD, 11));
        lblCarError.setForeground(new Color(220, 38, 38));
        lblCarError.setAlignmentX(LEFT_ALIGNMENT);
        lblCarError.setVisible(false);
        // Limpiar error al escribir
        tfCaracteristica.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            void limpiar() {
                tfCaracteristica.setBorder(BorderFactory.createLineBorder(BORDE, 1));
                lblCarError.setVisible(false);
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { limpiar(); }
        });

        // Panel de nueva mascota (se oculta si elige una existente)
        JPanel panelNueva = new JPanel();
        panelNueva.setLayout(new BoxLayout(panelNueva, BoxLayout.Y_AXIS));
        panelNueva.setBackground(VERDE_LT);
        panelNueva.setAlignmentX(LEFT_ALIGNMENT);
        panelNueva.add(fila("Nombre de la mascota *", tfNombreMascota));
        panelNueva.add(Box.createVerticalStrut(8));
        panelNueva.add(filaCombo("Especie *", cbEspecie));
        panelNueva.add(Box.createVerticalStrut(8));
        panelNueva.add(fila("Característica diferenciadora (si hay duplicado)", tfCaracteristica));
        panelNueva.add(lblCarError);
        root.add(panelNueva);

        // Mostrar/ocultar panel de nueva mascota según selección del combo
        cbMascota.addActionListener(ev -> {
            boolean esNueva = !(cbMascota.getSelectedItem() instanceof Mascotas);
            panelNueva.setVisible(esNueva);
        });

        root.add(Box.createVerticalStrut(18));

        // ── Seccion: Datos de la cita ─────────────────────
        root.add(seccion("Datos de la cita"));
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
        colFecha.add(dateChooser);

        // Hora con JComboBox (igual que el panel de cliente)
        JPanel colHora = new JPanel();
        colHora.setLayout(new BoxLayout(colHora, BoxLayout.Y_AXIS));
        colHora.setBackground(VERDE_LT);
        colHora.add(label("Hora *"));
        colHora.add(Box.createVerticalStrut(3));
        String[] horas = {
                "Selecciona hora...",
                "08:00","08:30","09:00","09:30",
                "10:00","10:30","11:00","11:30",
                "14:00","14:30","15:00","15:30",
                "16:00","16:30"
        };
        JComboBox<String> cbHora = new JComboBox<>(horas);
        cbHora.setFont(new Font("Arial", Font.PLAIN, 13));
        cbHora.setBackground(Color.WHITE);
        cbHora.setSelectedIndex(3); // 09:00 por defecto
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

        rbDomicilio.addActionListener(e -> {
            tfDireccion.setEnabled(true);
            tfDireccion.setBackground(Color.WHITE);
        });
        rbPresencial.addActionListener(e -> {
            tfDireccion.setEnabled(false);
            tfDireccion.setBackground(new Color(220, 220, 220));
            tfDireccion.setText("");
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
        btnCancelar.addActionListener(e -> dispose());

        // Cargar empleados para asignar (se usa el admin logueado o el primero disponible)
        List<Empleados> empleadosList = new EmpleadoRepositoryImpl().buscarTodos();

        btnGuardar.addActionListener(e -> {
            try {
                String nombreDueno   = tfNombreDueno.getText().trim();
                String celular       = tfCelular.getText().trim();
                String correo        = tfCorreo.getText().trim();
                String horaStr       = (String) cbHora.getSelectedItem();
                boolean esDomicilio  = rbDomicilio.isSelected();
                String direccion     = tfDireccion.getText().trim();

                if (nombreDueno.isEmpty())   throw new Exception("El nombre del dueno es obligatorio.");
                if (dateChooser.getDate() == null) throw new Exception("Selecciona la fecha de la cita.");
                if (horaStr == null || horaStr.startsWith("Selecciona")) throw new Exception("Selecciona la hora de la cita.");
                if (esDomicilio && direccion.isEmpty()) throw new Exception("Ingresa la direccion del domicilio.");

                LocalDate fecha = dateChooser.getDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime hora  = LocalTime.parse(horaStr);

                // 1) Crear cliente
                Cliente cliente = new Cliente();
                cliente.setNombre(nombreDueno);
                cliente.setTelefono(celular.isEmpty() ? null : celular);
                cliente.setCorreo(correo.isEmpty() ? null : correo.toLowerCase());
                cliente.setContrasena("kamp" + UUID.randomUUID().toString().substring(0, 6));
                cliente.setFechaRegistro(LocalDate.now());
                new ClienteRepositoryImpl().guardar(cliente);

                // 2) Mascota: existente o nueva
                Mascotas mascota;
                String nombreMascota;
                if (cbMascota.getSelectedItem() instanceof Mascotas) {
                    mascota = (Mascotas) cbMascota.getSelectedItem();
                    nombreMascota = mascota.getEtiqueta();
                } else {
                    String nmMascota = tfNombreMascota.getText().trim();
                    if (nmMascota.isEmpty()) throw new Exception("El nombre de la mascota es obligatorio.");
                    int especieIdx = cbEspecie.getSelectedIndex();
                    Especies especie = (especieIdx >= 0 && especieIdx < especiesList.size())
                            ? especiesList.get(especieIdx) : null;
                    if (especie == null) throw new Exception("No hay especies registradas. Registra una especie primero.");

                    // Verificar duplicados
                    List<Mascotas> todasMascotas = new MascotaRepositoryImpl().buscarTodos();
                    boolean hayConflicto = todasMascotas.stream().anyMatch(m ->
                            m.getNombre().equalsIgnoreCase(nmMascota) &&
                                    m.getEspecie() != null &&
                                    m.getEspecie().getNombre().equalsIgnoreCase(especie.getNombre()));
                    String car = tfCaracteristica.getText().trim();
                    if (hayConflicto) {
                        if (car.isEmpty()) {
                            // Resaltar campo en rojo y enfocar
                            tfCaracteristica.setBorder(BorderFactory.createLineBorder(new Color(220,38,38), 2));
                            lblCarError.setText("⚠ Obligatorio: ya existe \"" + nmMascota + "\" de especie " + especie.getNombre());
                            lblCarError.setVisible(true);
                            tfCaracteristica.requestFocusInWindow();
                            pack();
                            throw new Exception(
                                    "Ya existe una mascota llamada \"" + nmMascota + "\" de especie " + especie.getNombre() + ".\n" +
                                            "Ingresa una característica diferenciadora (ej: collar rojo, pelaje negro).");
                        }
                        boolean carDup = todasMascotas.stream().anyMatch(m ->
                                m.getNombre().equalsIgnoreCase(nmMascota) &&
                                        m.getEspecie() != null &&
                                        m.getEspecie().getNombre().equalsIgnoreCase(especie.getNombre()) &&
                                        car.equalsIgnoreCase(m.getCaracteristica() != null ? m.getCaracteristica().trim() : ""));
                        if (carDup) {
                            tfCaracteristica.setBorder(BorderFactory.createLineBorder(new Color(220,38,38), 2));
                            lblCarError.setText("⚠ Esa característica ya existe, usa otra diferente");
                            lblCarError.setVisible(true);
                            tfCaracteristica.requestFocusInWindow();
                            pack();
                            throw new Exception("Ya existe una mascota con ese nombre, especie y característica. Elige otra diferenciadora.");
                        }
                    }

                    mascota = new Mascotas();
                    mascota.setNombre(nmMascota);
                    mascota.setCliente(cliente);
                    mascota.setEspecie(especie);
                    mascota.setCaracteristica(car.isEmpty() ? null : car);
                    new MascotaRepositoryImpl().guardar(mascota);
                    nombreMascota = mascota.getEtiqueta();
                }

                // 3) Crear cita — veterinario = admin logueado o primero disponible
                Empleados empleado = Main.empleadoActual;
                if (empleado == null && !empleadosList.isEmpty())
                    empleado = empleadosList.get(0);
                if (empleado == null)
                    throw new Exception("No hay empleados registrados. Registra un empleado primero.");

                // ── Regla de cupo (mismo criterio que el cliente) ──
                CitaService citaSvc = new CitaService();
                long citasActivas = citaSvc.listarTodas().stream()
                        .filter(c -> c.getEstadoCita() != EstadoCita.CANCELADA
                                && c.getEstadoCita() != EstadoCita.COMPLETADA)
                        .count();
                boolean hayCupo = citasActivas < 10;
                EstadoCita estadoFinal = hayCupo ? EstadoCita.CONFIRMADA : EstadoCita.PENDIENTE;

                Citas cita = new Citas();
                cita.setMascota(mascota);
                cita.setEmpleado(empleado);
                cita.setFechaCita(fecha);
                cita.setHoraCita(hora);
                cita.setEstadoCita(estadoFinal);
                if (esDomicilio) cita.setDireccionDomicilio(direccion);
                citaSvc.guardarCita(cita);

                // Si hay cupo → enviar correo de confirmación al cliente
                if (hayCupo && cliente.getCorreo() != null && !cliente.getCorreo().isEmpty()) {
                    String cuerpoCorreo =
                            "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;background:#f0fdf4;border-radius:10px;padding:32px;'>" +
                                    "<h2 style='color:#16a34a;'>✅ Cita Confirmada</h2>" +
                                    "<p>Hola <b>" + cliente.getNombre() + "</b>, tu cita en <b>Kampets Veterinaria</b> fue confirmada.</p>" +
                                    "<table style='width:100%;border-collapse:collapse;margin:16px 0;'>" +
                                    "<tr><td style='padding:8px 12px;background:#dcfce7;color:#15803d;font-weight:bold;'>Mascota</td><td style='padding:8px 12px;'>" + nombreMascota + "</td></tr>" +
                                    "<tr><td style='padding:8px 12px;background:#dcfce7;color:#15803d;font-weight:bold;'>Fecha</td><td style='padding:8px 12px;'>" + fecha + "</td></tr>" +
                                    "<tr><td style='padding:8px 12px;background:#dcfce7;color:#15803d;font-weight:bold;'>Hora</td><td style='padding:8px 12px;'>" + hora + "</td></tr>" +
                                    "</table>" +
                                    "<p style='color:#6b7280;font-size:13px;'>Por favor preséntate puntualmente. 🐾</p></div>";
                    final String correoFinal = cliente.getCorreo();
                    final String nombreFinal = cliente.getNombre();
                    final String cuerpoFinal = cuerpoCorreo;
                    new Thread(() -> {
                        try {
                            CorreoService.enviarCorreoGeneral(correoFinal, nombreFinal, "Confirmación de cita - Kampets", cuerpoFinal);
                        } catch (Exception mailEx) {
                            System.err.println("[CORREO ERROR] " + mailEx.getMessage());
                            SwingUtilities.invokeLater(() ->
                                    JOptionPane.showMessageDialog(null,
                                            "Cita guardada, pero el correo no se pudo enviar:\n" + mailEx.getMessage(),
                                            "Aviso de correo", JOptionPane.WARNING_MESSAGE));
                        }
                    }).start();
                }

                guardado = true;
                String estadoMsg = hayCupo ? "Confirmada" : "En lista de espera (sin cupo disponible)";
                JOptionPane.showMessageDialog(this,
                        "Cita registrada.\n" +
                                "Dueño:  " + nombreDueno + "\n" +
                                "Mascota: " + nombreMascota + "\n" +
                                "Fecha:   " + fecha + "  Hora: " + hora + "\n" +
                                "Estado:  " + estadoMsg,
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
