package org.example.view;

import org.example.model.Cliente;
import org.example.model.Empleados;
import org.example.service.ConfigService;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {
    public static JFrame frame      = new JFrame("Kampets");
    public static JPanel contenedor = new JPanel(new CardLayout());

    // ── Sesión activa ─────────────────────────────────────
    public static Cliente   clienteActual   = null;
    public static Empleados empleadoActual  = null;

    // ── Instancias de cada panel ──────────────────────────
    private static PanelCliente         panelCliente         = new PanelCliente();
    private static PanelAdmin           panelAdmin           = new PanelAdmin();
    private static PanelAdminCitas      panelAdminCitas      = new PanelAdminCitas();
    private static PanelAdminMascotas   panelAdminMascotas   = new PanelAdminMascotas();
    private static PanelAdminVacunas    panelAdminVacunas    = new PanelAdminVacunas();
    private static PanelAdminInventario panelAdminInventario = new PanelAdminInventario();
    private static PanelAdminReportes   panelAdminReportes   = new PanelAdminReportes();
    private static PanelCalendario      panelCalendario      = new PanelCalendario();
    private static PanelMisCitas        panelMisCitas        = new PanelMisCitas();
    private static PanelHistorial       panelHistorial       = new PanelHistorial();
    private static PanelAlimentos       panelAlimentos       = new PanelAlimentos();
    private static PanelVacunas         panelVacunas         = new PanelVacunas();
    private static PanelAgendarCita     panelAgendarCita     = new PanelAgendarCita();
    private static PanelMisMascotas     panelMisMascotas     = new PanelMisMascotas();

    public static void  main(String[] args) {
        contenedor.add(new Interfaz_Grafica_Kampets().panel, "login");
        contenedor.add(new CrearCuenta().panel,              "crearCuenta");
        contenedor.add(panelCliente.panel,                   "panelCliente");
        contenedor.add(panelAdmin.panel,                     "panelAdmin");
        contenedor.add(panelAdminCitas.panel,                "adminCitas");
        contenedor.add(panelAdminMascotas.panel,             "adminMascotas");
        contenedor.add(panelAdminVacunas.panel,              "adminVacunas");
        contenedor.add(panelAdminInventario.panel,           "adminInventario");
        contenedor.add(panelAdminReportes.panel,             "adminReportes");
        contenedor.add(panelCalendario.panel,                "adminCalendario");
        contenedor.add(panelMisCitas.panel,                  "misCitas");
        contenedor.add(panelHistorial.panel,                 "historial");
        contenedor.add(panelAlimentos.panel,                 "alimentos");
        contenedor.add(panelVacunas.panel,                   "vacunas");
        contenedor.add(panelAgendarCita.panel,               "agendarCita");
        contenedor.add(panelMisMascotas.panel,               "misMascotas");

        frame.setContentPane(contenedor);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null);
        cursorNormal = crearCursorGarra();
        cursorHover  = crearCursorGarraHover();
        frame.setCursor(cursorNormal);
        frame.setVisible(true);

        cambiarPantalla("login");
    }

    public static void expandirVentana() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public static void cambiarPantalla(String nombre) {
        CardLayout cl = (CardLayout) contenedor.getLayout();
        cl.show(contenedor, nombre);

        switch (nombre) {
            case "login":
                frame.setExtendedState(JFrame.NORMAL);
                frame.setSize(420, 520);
                frame.setLocationRelativeTo(null);
                break;
            case "crearCuenta":
                frame.setExtendedState(JFrame.NORMAL);
                frame.setSize(420, 580);
                frame.setLocationRelativeTo(null);
                break;
            case "panelCliente":   panelCliente.recargar();         expandirVentana(); break;
            case "panelAdmin":
                panelAdmin.recargar(); expandirVentana();
                // Si el correo no está configurado, avisar al admin la primera vez
                if (!ConfigService.isCorreoConfigurado()) {
                    SwingUtilities.invokeLater(() -> {
                        int op = JOptionPane.showConfirmDialog(frame,
                            "Para que funcione la recuperacion de contraseña\n" +
                            "necesitas configurar el correo de Kampets.\n\n" +
                            "¿Quieres configurarlo ahora?",
                            "Configurar correo", JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
                        if (op == JOptionPane.YES_OPTION) {
                            new ConfigurarCorreoDialog(frame).setVisible(true);
                        }
                    });
                }
                break;
            case "adminCitas":     panelAdminCitas.recargar();      expandirVentana(); break;
            case "adminMascotas":  panelAdminMascotas.recargar();   expandirVentana(); break;
            case "adminVacunas":   panelAdminVacunas.recargar();    expandirVentana(); break;
            case "adminInventario":panelAdminInventario.recargar(); expandirVentana(); break;
            case "adminReportes":  panelAdminReportes.recargar();   expandirVentana(); break;
            case "adminCalendario":panelCalendario.recargar();      expandirVentana(); break;
            case "misCitas":       panelMisCitas.recargar();        expandirVentana(); break;
            case "misMascotas":    panelMisMascotas.recargar();     expandirVentana(); break;
            case "historial":      panelHistorial.recargar();       expandirVentana(); break;
            case "alimentos":      panelAlimentos.recargar();       expandirVentana(); break;
            case "vacunas":        panelVacunas.recargar();         expandirVentana(); break;
            case "agendarCita":    panelAgendarCita.recargar();     expandirVentana(); break;
            default:               expandirVentana(); break;
        }
    }

    // ── Cursores de garra (inicializados al arrancar) ─────
    public static Cursor cursorNormal;
    public static Cursor cursorHover;

    // ── Cursor de garra de perrito ────────────────────────
    public static Cursor crearCursorGarra() {
        // Usamos 32x32 pero dibujamos pequeño (como cursor normal ~18px)
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);

        Color piel   = new Color(205, 148, 75);
        Color almoha = new Color(175,  90, 45);
        Color borde  = new Color( 90,  45, 10);

        // Palma principal
        g.setColor(piel);
        g.fillOval(5, 9, 14, 13);
        g.setColor(almoha);
        g.fillOval(8, 13, 8, 7);

        // 3 deditos superiores
        g.setColor(piel);
        g.fillOval(4,  4, 6, 7);
        g.fillOval(10, 2, 6, 7);
        g.fillOval(16, 4, 6, 7);
        g.setColor(almoha);
        g.fillOval(5,  6, 4, 4);
        g.fillOval(11, 4, 4, 4);
        g.fillOval(17, 6, 4, 4);

        // Pulgar lateral
        g.setColor(piel);
        g.fillOval(17, 11, 6, 5);
        g.setColor(almoha);
        g.fillOval(18, 12, 3, 3);

        // Contorno
        g.setColor(borde);
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(5, 9, 14, 13);
        g.drawOval(4,  4, 6, 7);
        g.drawOval(10, 2, 6, 7);
        g.drawOval(16, 4, 6, 7);
        g.drawOval(17, 11, 6, 5);

        g.dispose();
        return Toolkit.getDefaultToolkit().createCustomCursor(
                img, new Point(13, 2), "garra");
    }

    // ── Cursor hover: garra extendida apuntando ───────────
    public static Cursor crearCursorGarraHover() {
        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);

        Color piel   = new Color(205, 148, 75);
        Color almoha = new Color(175,  90, 45);
        Color borde  = new Color( 90,  45, 10);

        // Palma rotada (mas abajo, como apuntando hacia arriba)
        g.setColor(piel);
        g.fillOval(7, 14, 14, 13);
        g.setColor(almoha);
        g.fillOval(10, 18, 8, 7);

        // Dedo indice extendido hacia arriba (mas largo)
        g.setColor(piel);
        g.fillOval(10, 2, 7, 13);
        g.setColor(almoha);
        g.fillOval(12, 4, 4, 5);

        // Dedo izquierdo doblado (mas corto)
        g.setColor(piel);
        g.fillOval(4, 9, 6, 7);
        g.setColor(almoha);
        g.fillOval(5, 11, 3, 3);

        // Dedo derecho doblado (mas corto)
        g.setColor(piel);
        g.fillOval(18, 9, 6, 7);
        g.setColor(almoha);
        g.fillOval(19, 11, 3, 3);

        // Pulgar lateral
        g.setColor(piel);
        g.fillOval(19, 15, 5, 5);
        g.setColor(almoha);
        g.fillOval(20, 16, 3, 3);

        // Contorno
        g.setColor(borde);
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawOval(7, 14, 14, 13);
        g.drawOval(10, 2, 7, 13);
        g.drawOval(4,  9, 6, 7);
        g.drawOval(18, 9, 6, 7);
        g.drawOval(19, 15, 5, 5);

        g.dispose();
        // Hotspot en la punta del dedo extendido
        return Toolkit.getDefaultToolkit().createCustomCursor(
                img, new Point(13, 2), "garraHover");
    }

    public static void aplicarTemaGlobal(boolean oscuro) {
        panelCliente.setTema(oscuro);
        panelAdminCitas.setTema(oscuro);
        panelAdminMascotas.setTema(oscuro);
        panelAdminVacunas.setTema(oscuro);
        panelAdminInventario.setTema(oscuro);
        panelAdminReportes.setTema(oscuro);
        panelMisCitas.setTema(oscuro);
        panelHistorial.setTema(oscuro);
        panelAlimentos.setTema(oscuro);
        panelVacunas.setTema(oscuro);
        panelAgendarCita.setTema(oscuro);
        panelMisMascotas.setTema(oscuro);
    }
}
