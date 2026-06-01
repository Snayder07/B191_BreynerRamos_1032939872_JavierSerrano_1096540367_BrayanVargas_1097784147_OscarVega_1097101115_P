package org.example.view;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.example.controller.CitaAdminController;
import org.example.controller.InventarioController;
import org.example.controller.MascotaAdminController;
import org.example.controller.VacunaAdminController;
import org.example.model.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelAdminReportes {
    public JPanel panel;

    private final CitaAdminController    citaCtrl    = new CitaAdminController();
    private final VacunaAdminController  vacunaCtrl  = new VacunaAdminController();
    private final InventarioController   invCtrl     = new InventarioController();
    private final MascotaAdminController mascotaCtrl = new MascotaAdminController();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Color[] CLARO = {
            new Color(240,253,244),new Color(22,101,52),Color.WHITE,new Color(34,120,70),
            new Color(220,245,230),Color.WHITE,new Color(15,60,30),new Color(100,130,110),
            new Color(234,88,12),new Color(187,224,200),new Color(15,60,30),new Color(134,190,155),
            new Color(220,38,38),new Color(22,163,74),new Color(210,240,220),
    };
    private Color[] C = CLARO;

    public PanelAdminReportes() { panel = new JPanel(new BorderLayout()); construir(); }
    public void recargar() { construir(); }

    private void construir() {
        panel.removeAll(); C = CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, "adminReportes", panel), BorderLayout.WEST);
        panel.add(crearContenido(), BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();
    }

    private JLabel lbl(String t,int sz,int st,Color c){JLabel l=new JLabel(t);l.setFont(new Font("Arial",st,sz+2));l.setForeground(c);return l;}

    private JPanel crearContenido() {
        // ── Cargar datos de BD ────────────────────────────────────────
        List<Citas>          citas    = citaCtrl.listarTodas();
        List<Control_vacunas> vacunas = vacunaCtrl.listarTodas();
        List<Cliente>         clientes = Cliente.consultarTodosBD();
        long completadas = 0;
        for (Citas ci : citas) { if (EstadoCita.COMPLETADA.equals(ci.getEstadoCita())) completadas++; }
        long canceladas = 0;
        for (Citas ci : citas) { if (EstadoCita.CANCELADA.equals(ci.getEstadoCita())) canceladas++; }

        JPanel c = new JPanel(new BorderLayout()); c.setBackground(C[0]);

        // ── Topbar ────────────────────────────────────────────────────
        JPanel tb = new JPanel(new BorderLayout()); tb.setBackground(C[2]);
        tb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,0,C[9]),BorderFactory.createEmptyBorder(16,28,16,28)));
        JPanel tl = new JPanel(new GridLayout(2,1)); tl.setBackground(C[2]);
        tl.add(lbl("Reportes",22,Font.BOLD,C[6]));
        tl.add(lbl("Genera y descarga reportes del sistema en PDF",12,Font.PLAIN,C[7]));
        JPanel tr = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); tr.setBackground(C[2]);
        tb.add(tl,BorderLayout.WEST); tb.add(tr,BorderLayout.EAST); c.add(tb,BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0,24)); body.setBackground(C[0]); body.setBorder(BorderFactory.createEmptyBorder(24,28,28,28));

        // ── Stats ─────────────────────────────────────────────────────
        JPanel stats = new JPanel(new GridLayout(1,4,16,0)); stats.setBackground(C[0]);
        Object[][] st = {
                {"Citas completadas",  String.valueOf(completadas),    C[13]},
                {"Total clientes",     String.valueOf(clientes.size()), C[1]},
                {"Vacunas registradas",String.valueOf(vacunas.size()),  C[1]},
                {"Cancelaciones",      String.valueOf(canceladas),      C[12]},
        };
        for (Object[] s : st) {
            JPanel card = new JPanel(new BorderLayout(0,4)); card.setBackground(C[2]);
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(18,20,18,20)));
            card.add(lbl((String)s[0],11,Font.PLAIN,C[7]),BorderLayout.NORTH);
            card.add(lbl((String)s[1],28,Font.BOLD,(Color)s[2]),BorderLayout.CENTER);
            stats.add(card);
        }
        body.add(stats,BorderLayout.NORTH);

        // ── Tarjetas de reportes ──────────────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2,3,16,16)); grid.setBackground(C[0]);

        grid.add(crearTarjetaReporte("📋","Reporte de citas",     "Todas las citas con estados y detalles",        "reporte_citas"));
        grid.add(crearTarjetaReporte("💉","Reporte de vacunas",   "Estado del plan de vacunacion de las mascotas", "reporte_vacunas"));
        grid.add(crearTarjetaReporte("🐾","Reporte de mascotas",  "Listado completo de mascotas registradas",      "reporte_mascotas"));
        grid.add(crearTarjetaReporte("📦","Reporte de inventario","Stock actual de medicamentos y productos",      "reporte_inventario"));
        grid.add(crearTarjetaReporte("👤","Reporte de usuarios",  "Clientes registrados y actividad reciente",     "reporte_usuarios"));
        grid.add(crearTarjetaReporte("📊","Reporte general",      "Resumen ejecutivo completo del sistema",        "reporte_general"));

        JScrollPane scroll = new JScrollPane(grid); scroll.setBorder(null); scroll.getViewport().setBackground(C[0]); scroll.getVerticalScrollBar().setUnitIncrement(16);
        body.add(scroll,BorderLayout.CENTER);
        JScrollPane outerScroll = new JScrollPane(body);
        outerScroll.setBorder(null); outerScroll.getViewport().setBackground(C[0]);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.add(outerScroll,BorderLayout.CENTER); return c;
    }

    private JPanel crearTarjetaReporte(String icono, String nombre, String descripcion, String tipo) {
        JPanel card = new JPanel(new BorderLayout(0,10)); card.setBackground(C[2]);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(22,22,18,22)));

        JPanel top = new JPanel(new BorderLayout(12,0)); top.setBackground(C[2]);
        JLabel ico = new JLabel(icono); ico.setFont(new Font("Segoe UI Emoji",Font.PLAIN,28)); ico.setPreferredSize(new Dimension(44,44));
        JPanel textos = new JPanel(new GridLayout(2,1,0,4)); textos.setBackground(C[2]);
        textos.add(lbl(nombre,14,Font.BOLD,C[6]));
        textos.add(lbl(descripcion,11,Font.PLAIN,C[7]));
        top.add(ico,BorderLayout.WEST); top.add(textos,BorderLayout.CENTER);

        JButton descBtn = new JButton("Descargar PDF");
        descBtn.setFont(new Font("Arial",Font.BOLD,12));
        descBtn.setBackground(C[1]); descBtn.setForeground(C[5]);
        descBtn.setOpaque(true); descBtn.setBorderPainted(false);
        descBtn.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        descBtn.setBorder(BorderFactory.createEmptyBorder(9,16,9,16));
        descBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { descargarReportePDF(nombre, tipo); }
        });

        card.add(top,BorderLayout.CENTER);
        card.add(descBtn,BorderLayout.SOUTH);
        return card;
    }

    // ─────────────────────────────────────────────────────────────────────
    //  GENERACION DE PDF
    // ─────────────────────────────────────────────────────────────────────

    private void descargarReportePDF(String nombre, String tipo) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Guardar " + nombre);
        chooser.setSelectedFile(new File(tipo + ".pdf"));
        if (chooser.showSaveDialog(panel) != JFileChooser.APPROVE_OPTION) return;

        File archivo = chooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf"))
            archivo = new File(archivo.getAbsolutePath() + ".pdf");

        try {
            switch (tipo) {
                case "reporte_citas":      generarReporteCitas(archivo);      break;
                case "reporte_vacunas":    generarReporteVacunas(archivo);    break;
                case "reporte_mascotas":   generarReporteMascotas(archivo);   break;
                case "reporte_inventario": generarReporteInventario(archivo); break;
                case "reporte_usuarios":   generarReporteUsuarios(archivo);   break;
                default:                   generarReporteGeneral(archivo);    break;
            }
            JOptionPane.showMessageDialog(panel,
                    "PDF exportado:\n" + archivo.getAbsolutePath(), "Listo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Error al generar PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Reporte de citas ─────────────────────────────────────────────────
    private void generarReporteCitas(File archivo) throws IOException {
        List<Citas> citas = citaCtrl.listarTodas();
        String[] cols = {"Mascota","Dueno","Veterinario","Fecha","Hora","Estado"};
        float[]  anchos = {101, 101, 101, 76, 50, 76};  // suma=505 = ancho util A4

        Object[][] filas = new Object[citas.size()][];
        for (int i = 0; i < citas.size(); i++) {
            Citas ci = citas.get(i);
            String masc = ci.getMascota() != null ? ci.getMascota().getNombre() : "—";
            String dueno = (ci.getMascota() != null && ci.getMascota().getCliente() != null)
                    ? ci.getMascota().getCliente().getNombre() : "—";
            String vet  = ci.getEmpleado() != null ? ci.getEmpleado().getNombre() : "—";
            String fecha = ci.getFechaCita() != null ? ci.getFechaCita().format(FMT) : "—";
            String hora  = ci.getHoraCita() != null ? ci.getHoraCita().toString() : "—";
            String estado = ci.getEstadoCita() != null ? ci.getEstadoCita().name() : "—";
            filas[i] = new Object[]{masc, dueno, vet, fecha, hora, estado};
        }
        generarPDF(archivo, "REPORTE DE CITAS", cols, anchos, filas);
    }

    // ── Reporte de vacunas ───────────────────────────────────────────────
    private void generarReporteVacunas(File archivo) throws IOException {
        List<Control_vacunas> lista = vacunaCtrl.listarTodas();
        String[] cols = {"Mascota","Dueno","Vacuna","Fecha aplic.","Prox. fecha","Estado"};
        float[]  anchos = {91, 101, 101, 76, 76, 61};  // suma=506 = ancho util A4

        Object[][] filas = new Object[lista.size()][];
        for (int i = 0; i < lista.size(); i++) {
            Control_vacunas cv = lista.get(i);
            String masc  = cv.getMascota() != null ? cv.getMascota().getNombre() : "—";
            String dueno = (cv.getMascota() != null && cv.getMascota().getCliente() != null)
                    ? cv.getMascota().getCliente().getNombre() : "—";
            String vac   = cv.getVacuna() != null ? cv.getVacuna().getNombre() : "—";
            String fa    = cv.getFechaAplicacion() != null ? cv.getFechaAplicacion().format(FMT) : "—";
            String fp    = cv.getProximaDosis()     != null ? cv.getProximaDosis().format(FMT)  : "—";
            String estado = cv.getEstado();
            filas[i] = new Object[]{masc, dueno, vac, fa, fp, estado};
        }
        generarPDF(archivo, "REPORTE DE VACUNAS", cols, anchos, filas);
    }

    // ── Reporte de mascotas ──────────────────────────────────────────────
    private void generarReporteMascotas(File archivo) throws IOException {
        List<Mascotas> lista = mascotaCtrl.listarTodas();
        String[] cols = {"Nombre","Especie","Caracteristica","Dueno","Fecha nac.","Sexo"};
        float[]  anchos = {87, 70, 116, 104, 75, 52};  // suma=504 = ancho util A4

        Object[][] filas = new Object[lista.size()][];
        for (int i = 0; i < lista.size(); i++) {
            Mascotas m = lista.get(i);
            String nom    = m.getNombre() != null ? m.getNombre() : "—";
            String esp    = m.getEspecie() != null ? m.getEspecie().getNombre() : "—";
            String car    = (m.getCaracteristica() != null && !m.getCaracteristica().isBlank())
                    ? m.getCaracteristica() : "—";
            String dueno  = m.getCliente() != null ? m.getCliente().getNombre() : "—";
            String fnac   = m.getFechaNac() != null ? m.getFechaNac().format(FMT) : "—";
            String sexo   = m.getSexo() != null ? m.getSexo() : "—";
            filas[i] = new Object[]{nom, esp, car, dueno, fnac, sexo};
        }
        generarPDF(archivo, "REPORTE DE MASCOTAS", cols, anchos, filas);
    }

    // ── Reporte de inventario ────────────────────────────────────────────
    private void generarReporteInventario(File archivo) throws IOException {
        List<Productos> lista = invCtrl.listarTodos();
        String[] cols = {"Producto","Tipo","Marca","Precio","Stock","Estado"};
        float[]  anchos = {158, 76, 76, 76, 50, 69};  // suma=505 = ancho util A4

        Object[][] filas = new Object[lista.size()][];
        for (int i = 0; i < lista.size(); i++) {
            Productos p = lista.get(i);
            String nombre = p.getNombre() != null ? p.getNombre() : "—";
            String tipo   = p.getTipo()   != null ? p.getTipo()   : "—";
            String marca  = p.getMarca()  != null ? p.getMarca()  : "—";
            String precio = p.getPrecio() != null ? "$" + p.getPrecio().toPlainString() : "—";
            String stock  = p.getStock()  != null ? String.valueOf(p.getStock()) : "0";
            String estado = (p.getStock() == null || p.getStock() == 0) ? "Sin stock"
                    : (p.getStock() < 10) ? "Stock bajo" : "OK";
            filas[i] = new Object[]{nombre, tipo, marca, precio, stock, estado};
        }
        generarPDF(archivo, "REPORTE DE INVENTARIO", cols, anchos, filas);
    }

    // ── Reporte de usuarios ──────────────────────────────────────────────
    private void generarReporteUsuarios(File archivo) throws IOException {
        List<Cliente> lista = Cliente.consultarTodosBD();
        String[] cols = {"Nombre","Correo","Telefono","Fecha registro"};
        float[]  anchos = {135, 168, 101, 101};  // suma=505 = ancho util A4

        Object[][] filas = new Object[lista.size()][];
        for (int i = 0; i < lista.size(); i++) {
            Cliente cl = lista.get(i);
            String nom  = cl.getNombre() != null ? cl.getNombre() : "—";
            String cor  = cl.getCorreo() != null ? cl.getCorreo() : "—";
            String tel  = cl.getTelefono() != null ? cl.getTelefono() : "—";
            String freg = cl.getFechaRegistro() != null ? cl.getFechaRegistro().format(FMT) : "—";
            filas[i] = new Object[]{nom, cor, tel, freg};
        }
        generarPDF(archivo, "REPORTE DE USUARIOS / CLIENTES", cols, anchos, filas);
    }

    // ── Reporte general ──────────────────────────────────────────────────
    private void generarReporteGeneral(File archivo) throws IOException {
        List<Citas>           citas    = citaCtrl.listarTodas();
        List<Control_vacunas> vacunas  = vacunaCtrl.listarTodas();
        List<Cliente>         clientes = Cliente.consultarTodosBD();
        List<Mascotas>        mascotas = mascotaCtrl.listarTodas();
        List<Productos>       prods    = invCtrl.listarTodos();
        long completadas = 0;
        for (Citas ci : citas) { if (EstadoCita.COMPLETADA.equals(ci.getEstadoCita())) completadas++; }
        long canceladas = 0;
        for (Citas ci : citas) { if (EstadoCita.CANCELADA.equals(ci.getEstadoCita())) canceladas++; }
        long vencidas = 0;
        for (Control_vacunas cv : vacunas) { if ("Vencida".equals(cv.getEstado())) vencidas++; }
        long stockBajo = 0;
        for (Productos p : prods) { if (p.getStock() != null && p.getStock() < 10) stockBajo++; }

        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float margin = 50; float pageH = page.getMediaBox().getHeight();
            float y = pageH - margin;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // Título
                texto(cs, PDType1Font.HELVETICA_BOLD, 18, margin, y, "REPORTE GENERAL — KAMPETS VETERINARIA");
                y -= 22;
                texto(cs, PDType1Font.HELVETICA, 10, margin, y, "Fecha: " + LocalDate.now().format(FMT));
                y -= 30;

                linea(cs, margin, y, page.getMediaBox().getWidth() - margin);
                y -= 18;

                texto(cs, PDType1Font.HELVETICA_BOLD, 13, margin, y, "RESUMEN EJECUTIVO");
                y -= 20;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Total citas registradas:   " + citas.size());    y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Citas completadas:         " + completadas);     y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Citas canceladas:          " + canceladas);      y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Total clientes:            " + clientes.size()); y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Total mascotas:            " + mascotas.size()); y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Vacunas registradas:       " + vacunas.size());  y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Vacunas vencidas:          " + vencidas);        y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Productos en inventario:   " + prods.size());    y -= 16;
                texto(cs, PDType1Font.HELVETICA, 11, margin, y, "  Productos con stock bajo:  " + stockBajo);       y -= 24;

                linea(cs, margin, y, page.getMediaBox().getWidth() - margin);
                y -= 18;

                texto(cs, PDType1Font.HELVETICA_BOLD, 13, margin, y, "ULTIMAS 5 CITAS");
                y -= 18;
                // Encabezados — posiciones X ajustadas al ancho util (505pt)
                texto(cs, PDType1Font.HELVETICA_BOLD, 10, margin,       y, "Mascota");
                texto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 120, y, "Veterinario");
                texto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 240, y, "Fecha");
                texto(cs, PDType1Font.HELVETICA_BOLD, 10, margin + 330, y, "Estado");
                y -= 16;
                int maxCitas = Math.min(5, citas.size());
                for (int i = 0; i < maxCitas; i++) {
                    Citas ci = citas.get(citas.size() - 1 - i);
                    String masc   = ci.getMascota() != null ? ci.getMascota().getNombre() : "-";
                    String vet    = ci.getEmpleado() != null ? ci.getEmpleado().getNombre() : "-";
                    String fecha  = ci.getFechaCita() != null ? ci.getFechaCita().format(FMT) : "-";
                    String estado = ci.getEstadoCita() != null ? ci.getEstadoCita().name() : "-";
                    texto(cs, PDType1Font.HELVETICA, 10, margin,       y, truncar(masc,   115));
                    texto(cs, PDType1Font.HELVETICA, 10, margin + 120, y, truncar(vet,    115));
                    texto(cs, PDType1Font.HELVETICA, 10, margin + 240, y, truncar(fecha,   85));
                    texto(cs, PDType1Font.HELVETICA, 10, margin + 330, y, truncar(estado, 175));
                    y -= 15;
                }
                y -= 10;
                linea(cs, margin, y, page.getMediaBox().getWidth() - margin);
                y -= 16;
                texto(cs, PDType1Font.HELVETICA, 9, margin, y, "Generado por Kampets — Sistema de gestion veterinaria");
            }
            doc.save(archivo);
        }
    }

    private void generarPDF(File archivo, String titulo, String[] cols,
                            float[] anchos, Object[][] filas) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float margin = 45;
            float pageH  = page.getMediaBox().getHeight();
            float pageW  = page.getMediaBox().getWidth();
            float y      = pageH - margin;
            float rowH   = 15f;
            int pageNum  = 1;

            // Posiciones X absolutas de cada columna (con 4pt de padding interno)
            float PAD = 4f;
            float[] colX = new float[cols.length];
            colX[0] = margin + PAD;
            for (int i = 1; i < cols.length; i++) colX[i] = colX[i - 1] + anchos[i - 1];

            PDPageContentStream cs = new PDPageContentStream(doc, page);

            // ── Encabezado ──────────────────────────────────────────
            texto(cs, PDType1Font.HELVETICA_BOLD, 16, margin, y, titulo); y -= 20;
            texto(cs, PDType1Font.HELVETICA, 10, margin, y,
                    "Fecha de generacion: " + LocalDate.now().format(FMT)
                            + "     Total registros: " + filas.length);
            y -= 12;
            linea(cs, margin, y, pageW - margin); y -= 16;

            // ── Fondo de encabezados de columnas ────────────────────
            rellenarFila(cs, margin, y - 2, pageW - margin, rowH + 4, 0.88f);
            for (int j = 0; j < cols.length; j++)
                texto(cs, PDType1Font.HELVETICA_BOLD, 9, colX[j], y, truncar(cols[j], anchos[j] - PAD*2));
            y -= rowH;
            linea(cs, margin, y, pageW - margin); y -= 4;

            // ── Filas de datos ───────────────────────────────────────
            boolean sombreado = false;
            for (Object[] fila : filas) {
                if (y < 60) {
                    piePagina(cs, margin, pageNum);
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = pageH - margin;
                    pageNum++;
                    rellenarFila(cs, margin, y - 2, pageW - margin, rowH + 4, 0.88f);
                    for (int j = 0; j < cols.length; j++)
                        texto(cs, PDType1Font.HELVETICA_BOLD, 9, colX[j], y, truncar(cols[j], anchos[j] - PAD*2));
                    y -= rowH;
                    linea(cs, margin, y, pageW - margin); y -= 4;
                    sombreado = false;
                }
                // Filas alternas con fondo muy sutil
                if (sombreado) rellenarFila(cs, margin, y - 3, pageW - margin, rowH, 0.96f);
                sombreado = !sombreado;

                for (int j = 0; j < Math.min(fila.length, cols.length); j++) {
                    String cel = fila[j] != null ? limpiarTexto(fila[j].toString()) : "-";
                    texto(cs, PDType1Font.HELVETICA, 9, colX[j], y, truncar(cel, anchos[j] - PAD*2));
                }
                y -= rowH;
            }

            piePagina(cs, margin, pageNum);
            cs.close();
            doc.save(archivo);
        }
    }

    /** Rellena un rectángulo con un gris claro (para encabezado y filas alternas) */
    private void rellenarFila(PDPageContentStream cs, float x, float y,
                              float x2, float h, float gris) throws IOException {
        cs.setNonStrokingColor(gris, gris, gris);
        cs.addRect(x, y, x2 - x, h);
        cs.fill();
        cs.setNonStrokingColor(0f, 0f, 0f);
    }

    /** Escribe texto en coordenadas X, Y absolutas de la página */
    private void texto(PDPageContentStream cs, PDType1Font fuente, float tam,
                       float x, float y, String texto) throws IOException {
        cs.beginText();
        cs.setFont(fuente, tam);
        cs.newLineAtOffset(x, y);
        cs.showText(limpiarTexto(texto));
        cs.endText();
    }

    /** Dibuja una línea horizontal de separación */
    private void linea(PDPageContentStream cs, float x1, float y, float x2) throws IOException {
        cs.setStrokingColor(0.7f, 0.7f, 0.7f);
        cs.moveTo(x1, y); cs.lineTo(x2, y); cs.stroke();
        cs.setStrokingColor(0f, 0f, 0f);
    }

    /** Pie de página con número */
    private void piePagina(PDPageContentStream cs, float margin, int num) throws IOException {
        linea(cs, margin, 45, 550);
        texto(cs, PDType1Font.HELVETICA, 8, margin, 32,
                "Pag. " + num + " — Generado por Kampets Sistema de Gestion Veterinaria");
    }

    private String truncar(String s, float anchoPts) {
        if (s == null || s.isEmpty()) return "-";
        int maxChars = Math.max(1, (int)(anchoPts / 5.2f));
        if (s.length() <= maxChars) return s;
        return s.substring(0, maxChars - 1) + ".";
    }
    private String limpiarTexto(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length());
        for (char ch : s.toCharArray()) {
            int code = (int) ch;
            if      (code == 0x2014 || code == 0x2013) sb.append('-');   // em dash / en dash
            else if (code == 0x201C || code == 0x201D) sb.append('"');   // comillas dobles curvas
            else if (code == 0x2018 || code == 0x2019) sb.append('\'');  // comillas simples curvas
            else if (code > 255)                        sb.append('?');   // otros fuera de Latin-1
            else                                        sb.append(ch);
        }
        return sb.toString();
    }

    private void estilizarTema(JButton b){
        b.setFont(new Font("Arial",Font.PLAIN,13)); b.setBackground(C[2]); b.setForeground(C[6]);
        b.setOpaque(true); b.setFocusPainted(false); b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(C[9],1),BorderFactory.createEmptyBorder(7,14,7,14)));
    }
}
