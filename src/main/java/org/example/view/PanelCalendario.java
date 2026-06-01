package org.example.view;

import org.example.model.Citas;
import org.example.model.EstadoCita;
import org.example.service.CorreoService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

/**
 * PanelCalendario v4.0
 * - Clic en tarjeta → popup de edición inline (fecha + hora)
 * - Al guardar, la tarjeta se reposiciona en el calendario automáticamente
 * - La tarjeta muestra: fecha, hora, mascota, cliente, prioridad
 * - Columnas alineadas dinámicamente
 * - Sin domingo (Lun–Sáb)
 * - Colores por prioridad
 */
public class PanelCalendario {

    public JPanel panel;

    private List<Citas> cachedTodas = null;
    private LocalDate semanaInicio = LocalDate.now().with(DayOfWeek.MONDAY);

    private static final int HORA_INICIO = 7;
    private static final int HORA_FIN    = 20;
    private static final int ALTO_HORA   = 80;
    private static final int ANCHO_HORAS = 68;
    private static final int DIAS        = 6;
    private static final int ALTO_BLOQUE = ALTO_HORA - 8;
    private static final int SNAP_MIN    = 15;

    private int anchoDia = 160;

    private JPanel      panelBloques;
    private GrillaFondo grilla;
    private JPanel      cabeceraRef;
    private JLabel      lblSemana;

    private Citas  citaArrastrada   = null;
    private JPanel bloqueArrastrado = null;
    private Point  offsetArrastre   = new Point();

    private final Color[] CLARO = {
            new Color(244,249,246), new Color(18,84,48),   Color.WHITE,
            new Color(20,125,72),   new Color(233,246,239), Color.WHITE,
            new Color(25,45,35),    new Color(95,120,108),  new Color(230,80,10),
            new Color(190,224,206), new Color(8,48,26),     new Color(135,195,160),
            new Color(215,35,35),   new Color(20,160,70),   new Color(208,240,220),
    };
    private Color[] C = CLARO;

    private static final Color[] COL_BLOQUE = {
            new Color(20,125,72),  new Color(12,112,140), new Color(118,52,230),
            new Color(210,112,5),  new Color(182,15,55),  new Color(4,144,100),
            new Color(54,124,240), new Color(160,78,240), new Color(228,80,8),
    };
    private static final Color COL_ALTA   = new Color(200,30,30);
    private static final Color COL_MEDIA  = new Color(210,120,5);
    private static final Color COL_BAJA   = new Color(20,125,72);
    private static final Color COL_VACUNA = new Color(109,40,217);

    private List<CitaPos> citasPos = new ArrayList<>();

    private static class CitaPos {
        Citas cita; Color color; int diaIdx, colIdx, totalCols;
        CitaPos(Citas c, Color col, int d, int ci, int tc) {
            cita=c; color=col; diaIdx=d; colIdx=ci; totalCols=tc;
        }
    }

    public PanelCalendario() {
        panel = new JPanel(new BorderLayout());
        construir();
    }

    public void recargar() { cachedTodas = null; construir(); }

    // ════════════════════════════════════════════════════════
    //  CONSTRUCCIÓN
    // ════════════════════════════════════════════════════════
    private void construir() {
        panel.removeAll();
        C = CLARO;
        panel.setBackground(C[0]);
        panel.add(SidebarAdmin.crear(C, "adminCalendario", panel), BorderLayout.WEST);

        if (cachedTodas != null) {
            panel.add(crearContenido(), BorderLayout.CENTER);
            panel.revalidate(); panel.repaint();
            return;
        }

        JPanel cargando = new JPanel(new BorderLayout());
        cargando.setBackground(C[0]);
        JLabel lCargando = new JLabel("Cargando calendario...", SwingConstants.CENTER);
        lCargando.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lCargando.setForeground(C[7]);
        cargando.add(lCargando, BorderLayout.CENTER);
        panel.add(cargando, BorderLayout.CENTER);
        panel.revalidate(); panel.repaint();

        new SwingWorker<List<Citas>, Void>() {
            @Override protected List<Citas> doInBackground() {
                try { return Citas.consultarTodosBD(); }
                catch (Exception e) { return Collections.emptyList(); }
            }
            @Override protected void done() {
                try { cachedTodas = get(); }
                catch (Exception e) { cachedTodas = Collections.emptyList(); }
                panel.remove(cargando);
                panel.add(crearContenido(), BorderLayout.CENTER);
                panel.revalidate(); panel.repaint();
            }
        }.execute();
    }

    private JPanel crearContenido() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(C[0]);
        c.add(crearTopbar(),     BorderLayout.NORTH);
        c.add(crearCalendario(), BorderLayout.CENTER);
        return c;
    }

    // ── Topbar ────────────────────────────────────────────────
    private JPanel crearTopbar() {
        JPanel tb = new JPanel(new BorderLayout());
        tb.setBackground(C[1]);
        tb.setBorder(BorderFactory.createEmptyBorder(13,26,13,26));
        tb.setPreferredSize(new Dimension(0,64));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        izq.setOpaque(false);
        JLabel titulo = new JLabel("Calendario de Citas");
        titulo.setFont(new Font("Segoe UI",Font.BOLD,22));
        titulo.setForeground(Color.WHITE);
        JLabel sub = new JLabel("Lunes a Sábado  •  Clic en tarjeta para editar");
        sub.setFont(new Font("Segoe UI",Font.PLAIN,12));
        sub.setForeground(new Color(165,215,190));
        izq.add(titulo); izq.add(sub);
        tb.add(izq, BorderLayout.WEST);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT,12,0));
        leyenda.setOpaque(false);
        leyenda.add(badge("ALTA",   COL_ALTA));
        leyenda.add(badge("MEDIA",  COL_MEDIA));
        leyenda.add(badge("BAJA",   COL_BAJA));
        leyenda.add(badge("VACUNA", COL_VACUNA));

        JPanel nav = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0));
        nav.setOpaque(false);
        lblSemana = new JLabel();
        lblSemana.setFont(new Font("Segoe UI",Font.PLAIN,13));
        lblSemana.setForeground(new Color(175,225,200));
        actualizarLblSemana();

        JButton btnAnt = navBtn("< Anterior");
        JButton btnHoy = navBtn("Hoy");
        JButton btnSig = navBtn("Siguiente >");
        JButton btnIr  = navBtn("📅 Ir a fecha...");
        btnHoy.setFont(new Font("Segoe UI",Font.BOLD,12));
        btnIr.setBackground(new Color(12,80,48));
        btnAnt.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { semanaInicio=semanaInicio.minusWeeks(1); construir(); } });
        btnHoy.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { semanaInicio=LocalDate.now().with(DayOfWeek.MONDAY); construir(); } });
        btnSig.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { semanaInicio=semanaInicio.plusWeeks(1); construir(); } });
        btnIr.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { abrirSelectorFecha(); } });
        nav.add(lblSemana); nav.add(btnAnt); nav.add(btnHoy); nav.add(btnSig); nav.add(btnIr);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
        der.setOpaque(false);
        der.add(leyenda); der.add(nav);
        tb.add(der, BorderLayout.EAST);
        return tb;
    }

    private JLabel badge(String t, Color c) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI",Font.BOLD,11));
        l.setForeground(c);
        return l;
    }

    private JButton navBtn(String t) {
        JButton b = new JButton(t);
        b.setFont(new Font("Segoe UI",Font.PLAIN,12));
        b.setBackground(new Color(32,105,65));
        b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(7,15,7,15));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            final Color o=b.getBackground();
            @Override public void mouseEntered(MouseEvent e){b.setBackground(o.brighter());}
            @Override public void mouseExited(MouseEvent e){b.setBackground(o);}
        });
        return b;
    }

    private void actualizarLblSemana() {
        if (lblSemana==null) return;
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d MMM", new Locale("es","CO"));
        lblSemana.setText(semanaInicio.format(f)+" – "+semanaInicio.plusDays(DIAS-1).format(f));
    }

    // ════════════════════════════════════════════════════════
    //  CALENDARIO
    // ════════════════════════════════════════════════════════
    private JPanel crearCalendario() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(C[0]);

        // Cabecera pintada para alinear perfectamente con la grilla
        JPanel cabecera = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                int aDia = calcAnchoDia(getWidth());
                g2.setColor(C[4]); g2.fillRect(0,0,getWidth(),getHeight());
                // Celda HORA
                g2.setFont(new Font("Segoe UI",Font.BOLD,10)); g2.setColor(C[7]);
                FontMetrics fm=g2.getFontMetrics();
                g2.drawString("HORA",(ANCHO_HORAS-fm.stringWidth("HORA"))/2, getHeight()/2+5);
                // Días
                String[] noms={"LUN","MAR","MIÉ","JUE","VIE","SÁB"};
                for (int d=0;d<DIAS;d++) {
                    LocalDate dia=semanaInicio.plusDays(d);
                    boolean hoy=dia.equals(LocalDate.now());
                    int xd=ANCHO_HORAS+d*aDia;
                    g2.setColor(C[4]); g2.fillRect(xd,0,aDia,getHeight());
                    g2.setColor(C[9]); g2.drawLine(xd,0,xd,getHeight());
                    g2.setFont(new Font("Segoe UI",Font.BOLD,11));
                    g2.setColor(C[7]);
                    fm=g2.getFontMetrics();
                    g2.drawString(noms[d], xd+(aDia-fm.stringWidth(noms[d]))/2, 18);
                    String num=String.valueOf(dia.getDayOfMonth());
                    g2.setFont(new Font("Segoe UI",Font.BOLD,20)); fm=g2.getFontMetrics();
                    // Hoy: subrayado verde en vez de fondo completo
                    if (hoy) {
                        g2.setColor(C[3]);
                        g2.setStroke(new BasicStroke(3f));
                        g2.drawLine(xd+8, getHeight()-3, xd+aDia-8, getHeight()-3);
                        g2.setStroke(new BasicStroke(1f));
                        g2.setColor(C[3]);
                    } else {
                        g2.setColor(C[6]);
                    }
                    g2.drawString(num,xd+(aDia-fm.stringWidth(num))/2,44);
                }
                g2.setColor(C[3]); g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        cabecera.setPreferredSize(new Dimension(0,56));
        cabecera.setBackground(C[4]);
        cabeceraRef = cabecera;

        int altoTotal=(HORA_FIN-HORA_INICIO)*ALTO_HORA;

        JLayeredPane capas = new JLayeredPane(){
            @Override public Dimension getPreferredSize(){
                int w=getParent()!=null?getParent().getWidth():ANCHO_HORAS+DIAS*160;
                return new Dimension(w,altoTotal);
            }
        };

        grilla = new GrillaFondo();
        grilla.setBounds(0,0,ANCHO_HORAS+DIAS*anchoDia,altoTotal);
        capas.add(grilla, JLayeredPane.DEFAULT_LAYER);

        panelBloques = new JPanel(null);
        panelBloques.setOpaque(false);
        panelBloques.setBounds(0,0,ANCHO_HORAS+DIAS*anchoDia,altoTotal);
        capas.add(panelBloques, JLayeredPane.PALETTE_LAYER);

        capas.addComponentListener(new ComponentAdapter(){
            @Override public void componentResized(ComponentEvent e){
                int w=capas.getWidth();
                if (w<ANCHO_HORAS+DIAS*60) return;
                anchoDia=calcAnchoDia(w);
                grilla.setBounds(0,0,w,altoTotal);
                panelBloques.setBounds(0,0,w,altoTotal);
                grilla.repaint();
                reposicionarBloques();
                // Sincronizar cabecera con viewport (ancho exacto)
                if (cabeceraRef!=null) {
                    cabeceraRef.setPreferredSize(new Dimension(w,56));
                    cabeceraRef.revalidate();
                    cabeceraRef.repaint();
                }
            }
        });

        cargarCitas();

        JScrollPane scroll = new JScrollPane(capas);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(C[0]);
        scroll.getVerticalScrollBar().setUnitIncrement(ALTO_HORA/2);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // Cabecera como columnHeader del scroll -> alineacion perfecta con la grilla
        scroll.setColumnHeaderView(cabecera);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                scroll.getVerticalScrollBar().setValue((8-HORA_INICIO)*ALTO_HORA);
            }
        });

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private int calcAnchoDia(int anchoTotal) {
        int disp=anchoTotal-ANCHO_HORAS;
        return (disp<DIAS*60) ? 160 : disp/DIAS;
    }

    // ── Grilla ────────────────────────────────────────────────
    private class GrillaFondo extends JPanel {
        GrillaFondo() { setBackground(Color.WHITE); setLayout(null); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int aDia=calcAnchoDia(getWidth()), totalH=HORA_FIN-HORA_INICIO, w=getWidth(), h=getHeight();

            // Fondos columnas
            for (int d=0;d<DIAS;d++){
                int xd=ANCHO_HORAS+d*aDia;
                g2.setColor(d==5?new Color(243,251,246):d%2==0?new Color(249,252,250):Color.WHITE);
                g2.fillRect(xd,0,aDia,h);
            }
            // Sin resalte de columna del día actual (se ve en la cabecera)
            // Líneas y etiquetas
            for (int hr=0;hr<=totalH;hr++){
                int y=hr*ALTO_HORA;
                if (hr<totalH){
                    g2.setColor(new Color(235,245,240)); g2.fillRect(0,y,ANCHO_HORAS-1,ALTO_HORA);
                }
                g2.setColor(new Color(165,208,187,180)); g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(0,y,w,y);
                if (hr<totalH){
                    g2.setFont(new Font("Segoe UI",Font.BOLD,11)); g2.setColor(C[6]);
                    String hs=String.format("%02d:00",HORA_INICIO+hr);
                    FontMetrics fm=g2.getFontMetrics();
                    g2.drawString(hs,ANCHO_HORAS-fm.stringWidth(hs)-5,y+15);
                    // Media hora
                    int yM=y+ALTO_HORA/2;
                    g2.setColor(new Color(165,208,187,100));
                    float[] dash={4f,4f};
                    g2.setStroke(new BasicStroke(1f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10f,dash,0f));
                    g2.drawLine(ANCHO_HORAS,yM,w,yM); g2.setStroke(new BasicStroke(1f));
                    g2.setColor(new Color(228,242,236)); g2.fillRect(0,yM,ANCHO_HORAS-1,ALTO_HORA-ALTO_HORA/2);
                    g2.setFont(new Font("Segoe UI",Font.PLAIN,10)); g2.setColor(new Color(90,130,110));
                    String ms=String.format("%02d:30",HORA_INICIO+hr);
                    fm=g2.getFontMetrics();
                    g2.drawString(ms,ANCHO_HORAS-fm.stringWidth(ms)-5,yM+13);
                }
            }
            // Líneas verticales
            g2.setColor(new Color(165,208,187)); g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(ANCHO_HORAS-1,0,ANCHO_HORAS-1,h);
            g2.setStroke(new BasicStroke(1f));
            for (int d=0;d<=DIAS;d++){
                g2.setColor(new Color(165,208,187,130));
                g2.drawLine(ANCHO_HORAS+d*aDia,0,ANCHO_HORAS+d*aDia,h);
            }
            // (línea de hora actual eliminada)
        }
    }

    // ════════════════════════════════════════════════════════
    //  CARGA DE CITAS
    // ════════════════════════════════════════════════════════
    private void cargarCitas() {
        panelBloques.removeAll(); citasPos.clear();
        List<Citas> todas = cachedTodas != null ? cachedTodas : Collections.emptyList();
        LocalDate fin=semanaInicio.plusDays(DIAS-1);
        List<Citas> semana=new ArrayList<>();
        for (Citas c:todas){
            if (c.getFechaCita()==null||c.getHoraCita()==null) continue;
            if (c.getEstadoCita()==EstadoCita.CANCELADA) continue;
            if (c.getFechaCita().isBefore(semanaInicio)||c.getFechaCita().isAfter(fin)) continue;
            if (c.getFechaCita().getDayOfWeek()==DayOfWeek.SUNDAY) continue;
            semana.add(c);
        }
        Map<LocalDate,List<Citas>> porDia=new LinkedHashMap<>();
        for (Citas c : semana) {
            LocalDate key = c.getFechaCita();
            if (!porDia.containsKey(key)) porDia.put(key, new ArrayList<>());
            porDia.get(key).add(c);
        }

        int ci=0;
        for (Map.Entry<LocalDate,List<Citas>> entry:porDia.entrySet()){
            LocalDate fecha=entry.getKey();
            List<Citas> dia=entry.getValue();
            dia.sort(Comparator.comparing(Citas::getHoraCita));
            int diaIdx=(int)(fecha.toEpochDay()-semanaInicio.toEpochDay());
            if (diaIdx<0||diaIdx>=DIAS) continue;
            int n=dia.size();
            int[] col=new int[n], maxC=new int[n];
            Arrays.fill(col,0); Arrays.fill(maxC,1);
            boolean[] proc=new boolean[n];
            for (int i=0;i<n;i++){
                if (proc[i]) continue;
                List<Integer> g=new ArrayList<>(); g.add(i); proc[i]=true;
                boolean exp=true;
                while(exp){ exp=false;
                    for(int j=i+1;j<n;j++){ if(proc[j]) continue;
                        for(int k:g){ LocalTime kI=dia.get(k).getHoraCita(),kF=kI.plusMinutes(55),jI=dia.get(j).getHoraCita(),jF=jI.plusMinutes(55);
                            if(jI.isBefore(kF)&&jF.isAfter(kI)){g.add(j);proc[j]=true;exp=true;break;}
                        }
                    }
                }
                int tg=g.size(); for(int k=0;k<tg;k++){col[g.get(k)]=k;maxC[g.get(k)]=tg;}
            }
            for(int i=0;i<n;i++){
                Citas cita=dia.get(i);
                Color color = esVacuna(cita)
                        ? COL_VACUNA
                        : colorPorPrioridad(cita, COL_BLOQUE[ci%COL_BLOQUE.length]);
                ci++;
                citasPos.add(new CitaPos(cita,color,diaIdx,col[i],maxC[i]));
                crearYAgregarBloque(cita,color,diaIdx,col[i],maxC[i]);
            }
        }
        panelBloques.revalidate(); panelBloques.repaint();
    }

    private void crearYAgregarBloque(Citas cita, Color color, int diaIdx, int colIdx, int totalCols) {
        boolean comp=totalCols>2;
        int minOff=(cita.getHoraCita().getHour()-HORA_INICIO)*60+cita.getHoraCita().getMinute();
        int y=minOff*ALTO_HORA/60+3;
        int margen=3, dispA=anchoDia-2*margen, anchoB=dispA/totalCols;
        int x=ANCHO_HORAS+diaIdx*anchoDia+margen+colIdx*anchoB;
        JPanel bloque=crearBloque(cita,color,comp);
        bloque.setBounds(x,y,anchoB-1,ALTO_BLOQUE);
        panelBloques.add(bloque);
        habilitarInteracciones(bloque,cita);
    }

    private void reposicionarBloques() {
        panelBloques.removeAll();
        for(CitaPos cp:citasPos) crearYAgregarBloque(cp.cita,cp.color,cp.diaIdx,cp.colIdx,cp.totalCols);
        panelBloques.revalidate(); panelBloques.repaint();
    }

    // ════════════════════════════════════════════════════════
    //  TARJETA (BLOQUE)
    // ════════════════════════════════════════════════════════
    private JPanel crearBloque(Citas cita, Color color, boolean comp) {
        DateTimeFormatter fh = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter fd = DateTimeFormatter.ofPattern("EEE d MMM", new Locale("es","CO"));

        String hora        = cita.getHoraCita().format(fh);
        String fecha       = cita.getFechaCita().format(fd);
        String mascota     = cita.getMascota()!=null ? cita.getMascota().getNombre() : "?";
        String cliente     = (cita.getMascota()!=null && cita.getMascota().getCliente()!=null)
                             ? cita.getMascota().getCliente().getNombre() : "?";
        String telefono    = (cita.getMascota()!=null && cita.getMascota().getCliente()!=null
                             && cita.getMascota().getCliente().getTelefono()!=null)
                             ? cita.getMascota().getCliente().getTelefono() : "";
        String direccion   = (cita.getDireccionDomicilio()!=null && !cita.getDireccionDomicilio().isEmpty())
                             ? cita.getDireccionDomicilio() : null;
        String prio        = getPrioridadTexto(cita);
        boolean vacuna     = esVacuna(cita);
        boolean esDomicilio = direccion != null;

        // Prefijo de icono para el nombre
        String iconPrefijo = esDomicilio ? "🏠 " : (vacuna ? "💉 " : "");

        JPanel b = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();

                // Sombra
                g2.setColor(new Color(0,0,0,30));
                g2.fillRoundRect(2,3,w-3,h-3,10,10);
                // Fondo principal
                g2.setColor(color);
                g2.fillRoundRect(0,0,w-2,h-2,10,10);
                // Barra lateral izquierda
                g2.setColor(new Color(0,0,0,40));
                g2.fillRoundRect(0,0,5,h-2,4,4);

                // ── Zona de texto con margen ─────────────────
                int px = 10, py = 7;
                int maxW = w - px - 4;

                // Fila 1: hora (derecha) + mascota (izquierda) — ambas en negrita
                Font fBold   = new Font("Arial", Font.BOLD,   comp ? 9 : 11);
                Font fNormal = new Font("Arial", Font.PLAIN,  comp ? 8 : 10);
                Font fSmall  = new Font("Arial", Font.PLAIN,  comp ? 7 :  9);
                Font fItalic = new Font("Arial", Font.ITALIC, 8);

                g2.setFont(fBold);
                FontMetrics fmBold = g2.getFontMetrics();
                int lineH = fmBold.getHeight();

                // Hora — alineada a la derecha
                g2.setColor(new Color(220,255,235));
                int horaW = fmBold.stringWidth(hora);
                g2.drawString(hora, w - 2 - horaW - 4, py + fmBold.getAscent());

                // Mascota — truncada si no cabe
                g2.setColor(Color.WHITE);
                String masLabel = iconPrefijo + mascota;
                int masMaxW = maxW - horaW - 6;
                g2.drawString(truncar(masLabel, fmBold, masMaxW), px, py + fmBold.getAscent());

                int y = py + lineH + 2;

                if (!comp) {
                    // Fila 2: fecha + cliente
                    g2.setFont(fSmall);
                    FontMetrics fmS = g2.getFontMetrics();
                    g2.setColor(new Color(210,248,228));
                    g2.drawString(truncar(fecha, fmS, maxW), px, y + fmS.getAscent());
                    y += fmS.getHeight() + 1;

                    g2.setFont(fNormal);
                    FontMetrics fmN = g2.getFontMetrics();
                    g2.setColor(new Color(210,248,228));
                    g2.drawString(truncar(cliente, fmN, maxW), px, y + fmN.getAscent());
                    y += fmN.getHeight() + 1;

                    // Fila 3: info extra
                    if (esDomicilio) {
                        g2.setFont(fSmall);
                        FontMetrics fmS2 = g2.getFontMetrics();
                        g2.setColor(new Color(255,240,140));
                        g2.drawString(truncar("Domicilio: " + direccion, fmS2, maxW), px, y + fmS2.getAscent());
                        y += fmS2.getHeight() + 1;
                        if (!telefono.isEmpty()) {
                            g2.setColor(new Color(160,255,200));
                            g2.drawString(truncar("Tel: " + telefono, fmS2, maxW), px, y + fmS2.getAscent());
                            y += fmS2.getHeight() + 1;
                        }
                    } else if (vacuna) {
                        g2.setFont(fSmall);
                        FontMetrics fmS2 = g2.getFontMetrics();
                        g2.setColor(new Color(220,180,255));
                        g2.drawString("Vacunacion", px, y + fmS2.getAscent());
                        y += fmS2.getHeight() + 1;
                    } else if (prio != null) {
                        g2.setFont(fSmall);
                        FontMetrics fmS2 = g2.getFontMetrics();
                        g2.setColor(new Color(255,255,180));
                        g2.drawString(truncar("! " + prio, fmS2, maxW), px, y + fmS2.getAscent());
                        y += fmS2.getHeight() + 1;
                    }

                    // Pie: "Clic para editar"
                    if (y + 12 < h - 2) {
                        g2.setFont(fItalic);
                        FontMetrics fmI = g2.getFontMetrics();
                        g2.setColor(new Color(180,230,200,180));
                        g2.drawString("Clic para editar", px, h - 6);
                    }
                }
                g2.dispose();
            }

            // Trunca texto con "..." si supera el ancho maximo
            private String truncar(String txt, FontMetrics fm, int maxW) {
                if (fm.stringWidth(txt) <= maxW) return txt;
                String puntos = "...";
                int pW = fm.stringWidth(puntos);
                StringBuilder sb = new StringBuilder();
                for (char c : txt.toCharArray()) {
                    if (fm.stringWidth(sb.toString()) + fm.charWidth(c) + pW > maxW) break;
                    sb.append(c);
                }
                return sb + puntos;
            }
        };
        b.setOpaque(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Tooltip enriquecido
        String tipoBadge = vacuna
                ? "<br><b style='color:#c084fc'>💉 Cita de vacunacion</b>"
                : (prio!=null?"<br>Prioridad: <b>"+prio+"</b>":"");
        String tipoDomicilio = esDomicilio
                ? "<br><b style='color:#facc15'>🏠 Domicilio: "+direccion+"</b>"
                  +(telefono.isEmpty() ? "" : "<br><b style='color:#4ade80'>📞 Tel: "+telefono+"</b>")
                : "";
        b.setToolTipText("<html><b>"+mascota+"</b><br>"
                +"Fecha: "+fecha+"<br>"
                +"Hora: <b>"+hora+"</b><br>"
                +"Cliente: "+cliente
                +tipoDomicilio
                +tipoBadge
                +"<br><i style='color:#aaa'>Clic para editar  |  Arrastra para mover</i>"
                +"</html>");
        return b;
    }

    // ════════════════════════════════════════════════════════
    //  INTERACCIONES: CLIC (editar) + ARRASTRE (mover)
    // ════════════════════════════════════════════════════════
    private void habilitarInteracciones(JPanel bloque, Citas cita) {
        // Detectar si fue clic o arrastre
        final boolean[] arrastrado = {false};

        bloque.addMouseListener(new MouseAdapter(){
            @Override public void mousePressed(MouseEvent e){
                arrastrado[0]=false;
                citaArrastrada=cita; bloqueArrastrado=bloque;
                offsetArrastre=e.getPoint();
                panelBloques.setComponentZOrder(bloque,0);
            }
            @Override public void mouseReleased(MouseEvent e){
                if (arrastrado[0]) {
                    // Fue arrastre → lógica de mover
                    if (citaArrastrada==null) return;
                    int sx=snapX(bloque.getX()), sy=snapY(bloque.getY());
                    bloque.setLocation(sx,sy); panelBloques.repaint();
                    int dI=Math.max(0,Math.min(DIAS-1,(sx-ANCHO_HORAS)/anchoDia));
                    int mn=Math.max(0,Math.min((HORA_FIN-HORA_INICIO)*60-SNAP_MIN,sy*60/ALTO_HORA));
                    LocalDate nF=semanaInicio.plusDays(dI);
                    LocalTime nH=LocalTime.of(HORA_INICIO+mn/60,(mn%60/SNAP_MIN)*SNAP_MIN);
                    if (nF.getDayOfWeek()==DayOfWeek.SUNDAY){
                        JOptionPane.showMessageDialog(panel,"No se agenda citas los domingos.",
                                "Día no disponible",JOptionPane.WARNING_MESSAGE);
                        construir(); citaArrastrada=null; bloqueArrastrado=null; return;
                    }
                    LocalDate aF=citaArrastrada.getFechaCita(); LocalTime aH=citaArrastrada.getHoraCita();
                    if (!nF.equals(aF)||!nH.equals(aH)) confirmarCambio(citaArrastrada,nF,nH,aF,aH);
                    else construir();
                } else {
                    // Fue clic simple → abrir editor
                    abrirEditorCita(cita);
                }
                citaArrastrada=null; bloqueArrastrado=null;
            }
        });

        bloque.addMouseMotionListener(new MouseMotionAdapter(){
            @Override public void mouseDragged(MouseEvent e){
                arrastrado[0]=true;
                if (bloqueArrastrado==null) return;
                Point p=SwingUtilities.convertPoint(bloque,e.getPoint(),panelBloques);
                bloqueArrastrado.setLocation(snapX(p.x-offsetArrastre.x),snapY(p.y-offsetArrastre.y));
                panelBloques.repaint();
            }
        });
    }

    // ════════════════════════════════════════════════════════
    //  EDITOR INLINE DE CITA (popup)
    // ════════════════════════════════════════════════════════
    private void abrirEditorCita(Citas cita) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(panel), "Editar cita",
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.getRootPane().setBorder(BorderFactory.createLineBorder(C[3],2));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        dlg.setContentPane(root);

        // ── Header del popup ─────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C[1]);
        header.setBorder(BorderFactory.createEmptyBorder(12,18,12,12));

        String mascota   = cita.getMascota()!=null?cita.getMascota().getNombre():"?";
        String cliente   = (cita.getMascota()!=null&&cita.getMascota().getCliente()!=null)
                ?cita.getMascota().getCliente().getNombre():"?";
        String telPopup  = (cita.getMascota()!=null&&cita.getMascota().getCliente()!=null
                &&cita.getMascota().getCliente().getTelefono()!=null)
                ?cita.getMascota().getCliente().getTelefono():"";
        String dirPopup  = (cita.getDireccionDomicilio()!=null&&!cita.getDireccionDomicilio().isEmpty())
                ?cita.getDireccionDomicilio():null;

        JLabel lTitulo=new JLabel("Editar cita — "+mascota);
        lTitulo.setFont(new Font("Segoe UI",Font.BOLD,15));
        lTitulo.setForeground(Color.WHITE);
        header.add(lTitulo, BorderLayout.WEST);

        JButton btnX=new JButton("  X  ");
        btnX.setFont(new Font("Segoe UI",Font.BOLD,13));
        btnX.setForeground(Color.WHITE);
        btnX.setBackground(new Color(180,30,30));
        btnX.setOpaque(true); btnX.setBorderPainted(false); btnX.setFocusPainted(false);
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.setBorder(BorderFactory.createEmptyBorder(4,10,4,10));
        btnX.addMouseListener(new MouseAdapter(){
            @Override public void mouseEntered(MouseEvent e){btnX.setBackground(new Color(210,40,40));}
            @Override public void mouseExited(MouseEvent e){btnX.setBackground(new Color(180,30,30));}
        });
        btnX.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { dlg.dispose(); } });
        header.add(btnX, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Cuerpo del popup ─────────────────────────────
        JPanel body=new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(18,20,10,20));
        GridBagConstraints gc=new GridBagConstraints();
        gc.insets=new Insets(6,4,6,4); gc.anchor=GridBagConstraints.WEST;

        // Info no editable (mascota + cliente)
        gc.gridx=0; gc.gridy=0; gc.gridwidth=2;
        JPanel infoBanner=new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        infoBanner.setBackground(new Color(240,250,244));
        infoBanner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1),
                BorderFactory.createEmptyBorder(6,10,6,10)));
        JLabel lInfoM=new JLabel(mascota); lInfoM.setFont(new Font("Segoe UI",Font.BOLD,12)); lInfoM.setForeground(C[3]);
        JLabel lInfoC=new JLabel(cliente);  lInfoC.setFont(new Font("Segoe UI",Font.PLAIN,12)); lInfoC.setForeground(C[6]);
        infoBanner.add(lInfoM); infoBanner.add(lInfoC);
        body.add(infoBanner,gc);

        // ── Banner de domicilio (solo si aplica) ────────────
        if (dirPopup != null) {
            gc.gridx=0; gc.gridy=1; gc.gridwidth=2;
            JPanel domBanner = new JPanel();
            domBanner.setLayout(new BoxLayout(domBanner, BoxLayout.Y_AXIS));
            domBanner.setBackground(new Color(255,251,220));
            domBanner.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(234,179,8),1),
                    BorderFactory.createEmptyBorder(6,10,6,10)));
            JLabel lDomTit = new JLabel("🏠  Cita a DOMICILIO");
            lDomTit.setFont(new Font("Segoe UI",Font.BOLD,12));
            lDomTit.setForeground(new Color(120,70,0));
            JLabel lDomDir = new JLabel("📍  "+dirPopup);
            lDomDir.setFont(new Font("Segoe UI",Font.PLAIN,11));
            lDomDir.setForeground(new Color(80,50,0));
            domBanner.add(lDomTit);
            domBanner.add(Box.createVerticalStrut(3));
            domBanner.add(lDomDir);
            if (!telPopup.isEmpty()) {
                JLabel lDomTel = new JLabel("📞  "+telPopup+"  — llamar antes de salir");
                lDomTel.setFont(new Font("Segoe UI",Font.BOLD,11));
                lDomTel.setForeground(new Color(14,100,50));
                domBanner.add(Box.createVerticalStrut(3));
                domBanner.add(lDomTel);
            }
            body.add(domBanner,gc);
            gc.gridy=2;
        } else {
            gc.gridy=1;
        }

        gc.gridwidth=1;

        // ── Campo FECHA ── JComboBox con días Lun–Sáb de las próximas 8 semanas
        int filaFecha = gc.gridy;
        gc.gridx=0; gc.gridy=filaFecha;
        JLabel lFLbl=new JLabel("Fecha:");
        lFLbl.setFont(new Font("Segoe UI",Font.BOLD,12)); lFLbl.setForeground(C[6]);
        body.add(lFLbl,gc);

        gc.gridx=1;
        DateTimeFormatter fdCombo=DateTimeFormatter.ofPattern("EEE dd/MM/yyyy", new Locale("es","CO"));
        List<LocalDate> fechasDisp=new ArrayList<>();
        LocalDate cursor2=LocalDate.now().with(DayOfWeek.MONDAY);
        for (int w=0;w<12;w++){
            for (int d=0;d<DIAS;d++){
                LocalDate fd2=cursor2.plusDays(d);
                if (!fd2.isBefore(LocalDate.now().minusDays(1))) fechasDisp.add(fd2);
            }
            cursor2=cursor2.plusWeeks(1);
        }
        String[] fechaOpts = new String[fechasDisp.size()];
        for (int i = 0; i < fechasDisp.size(); i++) fechaOpts[i] = fechasDisp.get(i).format(fdCombo);
        JComboBox<String> cbFecha=new JComboBox<>(fechaOpts);
        cbFecha.setFont(new Font("Segoe UI",Font.PLAIN,12));
        cbFecha.setPreferredSize(new Dimension(175,32));
        // Seleccionar la fecha actual de la cita
        String fechaCitaStr=cita.getFechaCita().format(fdCombo);
        for (int i=0;i<fechaOpts.length;i++) if(fechaOpts[i].equals(fechaCitaStr)){cbFecha.setSelectedIndex(i);break;}
        estilizarCombo(cbFecha);
        body.add(cbFecha,gc);

        // ── Campo HORA ── JComboBox con intervalos de 15 min
        gc.gridx=0; gc.gridy=filaFecha+1;
        JLabel lHLbl=new JLabel("Hora:");
        lHLbl.setFont(new Font("Segoe UI",Font.BOLD,12)); lHLbl.setForeground(C[6]);
        body.add(lHLbl,gc);

        gc.gridx=1;
        List<String> horas=new ArrayList<>();
        for (int h=HORA_INICIO;h<HORA_FIN;h++)
            for (int m=0;m<60;m+=SNAP_MIN)
                horas.add(String.format("%02d:%02d",h,m));
        JComboBox<String> cbHora=new JComboBox<>(horas.toArray(new String[0]));
        cbHora.setFont(new Font("Segoe UI",Font.PLAIN,12));
        cbHora.setPreferredSize(new Dimension(95,32));
        String horaActual=cita.getHoraCita().format(DateTimeFormatter.ofPattern("HH:mm"));
        cbHora.setSelectedItem(horas.contains(horaActual)?horaActual:horas.get(0));
        estilizarCombo(cbHora);
        body.add(cbHora,gc);

        // ── Aviso domingo ─────────────────────────────────
        gc.gridx=0; gc.gridy=filaFecha+2; gc.gridwidth=2;
        JLabel lAviso=new JLabel(" ");
        lAviso.setFont(new Font("Segoe UI",Font.ITALIC,10));
        lAviso.setForeground(new Color(180,30,30));
        body.add(lAviso,gc);

        root.add(body, BorderLayout.CENTER);

        // ── Botones ───────────────────────────────────────
        JPanel footer=new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        footer.setBackground(new Color(248,252,250));
        footer.setBorder(BorderFactory.createMatteBorder(1,0,0,0,C[9]));

        JButton btnCancelar=footerBtn("Cancelar",C[9],C[6]);
        JButton btnGuardar =footerBtn("Guardar",C[3],Color.WHITE);
        btnGuardar.setFont(new Font("Segoe UI",Font.BOLD,12));

        btnCancelar.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { dlg.dispose(); } });

        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fechaSelStr=(String)cbFecha.getSelectedItem();
                LocalDate nuevaFecha=LocalDate.parse(fechaSelStr, fdCombo);

                if (nuevaFecha.getDayOfWeek()==DayOfWeek.SUNDAY){
                    lAviso.setText("No se permiten citas los domingos.");
                    return;
                }
                String horaStr=(String)cbHora.getSelectedItem();
                LocalTime nuevaHora=LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));

                LocalDate aF=cita.getFechaCita(); LocalTime aH=cita.getHoraCita();

                if (nuevaFecha.equals(aF)&&nuevaHora.equals(aH)){
                    dlg.dispose(); return;
                }

                dlg.dispose();
                guardarCambioDesdeEditor(cita,nuevaFecha,nuevaHora,aF,aH);
            }
        });

        footer.add(btnCancelar); footer.add(btnGuardar);
        root.add(footer, BorderLayout.SOUTH);

        dlg.pack();
        dlg.setMinimumSize(new Dimension(340,0));
        // Centrar respecto al panel
        Point loc=panel.getLocationOnScreen();
        dlg.setLocation(loc.x+panel.getWidth()/2-dlg.getWidth()/2,
                loc.y+panel.getHeight()/2-dlg.getHeight()/2);
        dlg.setVisible(true);
    }

    private void estilizarCombo(JComboBox<?> cb){
        cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(C[9],1),
                BorderFactory.createEmptyBorder(2,4,2,4)));
        cb.setBackground(Color.WHITE);
        cb.setFocusable(false);
    }

    private JButton footerBtn(String txt, Color bg, Color fg){
        JButton b=new JButton(txt);
        b.setFont(new Font("Segoe UI",Font.PLAIN,12));
        b.setBackground(bg); b.setForeground(fg);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8,18,8,18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter(){
            final Color o=b.getBackground();
            @Override public void mouseEntered(MouseEvent e){b.setBackground(o.brighter());}
            @Override public void mouseExited(MouseEvent e){b.setBackground(o);}
        });
        return b;
    }

    /** Guarda el cambio desde el editor, navega a la semana correcta y reposiciona */
    private void guardarCambioDesdeEditor(Citas cita, LocalDate nF, LocalTime nH,
                                          LocalDate aF, LocalTime aH) {
        DateTimeFormatter ff=DateTimeFormatter.ofPattern("EEEE d 'de' MMMM",new Locale("es","CO"));
        DateTimeFormatter fh=DateTimeFormatter.ofPattern("HH:mm");
        String msg="<html><b>¿Confirmar cambio?</b><br><br>"
                +"Mascota: <b>"+cita.getMascota().getNombre()+"</b><br>"
                +"Antes: "+aF.format(ff)+" a las "+aH.format(fh)+"<br>"
                +"Nueva: <b>"+nF.format(ff)+" a las "+nH.format(fh)+"</b><br><br>"
                +"<i>Se notificará al cliente por correo.</i></html>";
        int r=JOptionPane.showConfirmDialog(panel,msg,"Confirmar cambio",
                JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
        if (r==JOptionPane.YES_OPTION){
            try {
                cita.setFechaCita(nF); cita.setHoraCita(nH);
                cita.actualizarBD();
                enviarNotificacion(cita,aF,aH,nF,nH);
                // Navegar automáticamente a la semana donde quedó la cita
                semanaInicio = nF.with(DayOfWeek.MONDAY);
                construir();
                JOptionPane.showMessageDialog(panel,
                        "Cita actualizada y reubicada en el calendario.\nCliente notificado por correo.",
                        "Éxito",JOptionPane.INFORMATION_MESSAGE);
            } catch(Exception ex){
                JOptionPane.showMessageDialog(panel,"Error: "+ex.getMessage(),
                        "Error",JOptionPane.ERROR_MESSAGE);
                construir();
            }
        }
        // Si cancela, no hace nada
    }

    // ════════════════════════════════════════════════════════
    //  SNAP + ARRASTRE (mover)
    // ════════════════════════════════════════════════════════
    private int snapX(int raw){
        int d=(int)Math.floor((double)(raw+anchoDia/2-ANCHO_HORAS)/anchoDia);
        return ANCHO_HORAS+Math.max(0,Math.min(DIAS-1,d))*anchoDia+3;
    }
    private int snapY(int raw){
        int maxM=(HORA_FIN-HORA_INICIO)*60-SNAP_MIN;
        int m=(int)Math.round((double)(raw-3)*60/ALTO_HORA/SNAP_MIN)*SNAP_MIN;
        return Math.max(0,Math.min(maxM,m))*ALTO_HORA/60+3;
    }

    private void confirmarCambio(Citas cita,LocalDate nF,LocalTime nH,LocalDate aF,LocalTime aH){
        guardarCambioDesdeEditor(cita,nF,nH,aF,aH);
    }

    // ════════════════════════════════════════════════════════
    //  UTILIDADES
    // ════════════════════════════════════════════════════════
    private Color colorPorPrioridad(Citas cita, Color fallback){
        try {
            String p=(String)cita.getClass().getMethod("getPrioridad").invoke(cita);
            if (p==null) return fallback;
            switch(p.trim().toUpperCase()){
                case "ALTA":  return COL_ALTA;
                case "MEDIA": return COL_MEDIA;
                case "BAJA":  return COL_BAJA;
                default: return fallback;
            }
        } catch(Exception ex){ return fallback; }
    }

    private String getPrioridadTexto(Citas cita){
        try {
            String p=(String)cita.getClass().getMethod("getPrioridad").invoke(cita);
            return (p!=null&&!p.isEmpty())?p.toUpperCase():null;
        } catch(Exception ex){ return null; }
    }

    private void enviarNotificacion(Citas cita,LocalDate aF,LocalTime aH,LocalDate nF,LocalTime nH){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String correo=cita.getMascota().getCliente().getCorreo();
                    String nombre=cita.getMascota().getCliente().getNombre();
                    String mascota=cita.getMascota().getNombre();
                    DateTimeFormatter ff=DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'de' yyyy",new Locale("es","CO"));
                    DateTimeFormatter fh=DateTimeFormatter.ofPattern("HH:mm");
                    CorreoService.enviarCorreoGeneral(correo,nombre,"Tu cita en Kampets fue reprogramada",
                            cuerpoCorreo(nombre,mascota,aF.format(ff),aH.format(fh),nF.format(ff),nH.format(fh)));
                } catch(Exception ex){ System.err.println("Correo err: "+ex.getMessage()); }
            }
        }).start();
    }

    private String cuerpoCorreo(String nom,String mas,String fA,String hA,String fN,String hN){
        return "<!DOCTYPE html><html><body style='font-family:Segoe UI,Arial,sans-serif;background:#f0f8f4;margin:0;padding:20px'>"
                +"<div style='max-width:520px;margin:auto;background:#fff;border-radius:12px;padding:32px;box-shadow:0 2px 8px rgba(0,0,0,.08)'>"
                +"<h2 style='color:#14804a;margin-top:0'>🐾 Kampets Veterinaria</h2>"
                +"<p style='color:#444'>Hola <strong>"+nom+"</strong>,</p>"
                +"<p style='color:#444'>Tu cita para <strong>"+mas+"</strong> fue reprogramada:</p>"
                +"<table style='width:100%;border-collapse:collapse;margin:20px 0'>"
                +"<tr><td style='padding:12px;background:#fef2f2;color:#991b1b;border-radius:6px'>❌ <b>Antes:</b> "+fA+" — "+hA+"</td></tr>"
                +"<tr><td style='padding:4px'></td></tr>"
                +"<tr><td style='padding:12px;background:#ecfdf5;color:#065f46;border-radius:6px'>✅ <b>Ahora:</b> "+fN+" — "+hN+"</td></tr>"
                +"</table><p style='color:#777;font-size:13px'>¿Preguntas? Contáctanos.</p>"
                +"<hr style='border:none;border-top:1px solid #e0e0e0;margin:20px 0'/>"
                +"<p style='color:#aaa;font-size:11px;text-align:center'>© Kampets Veterinaria</p>"
                +"</div></body></html>";
    }

    // ════════════════════════════════════════════════════════
    //  SELECTOR RÁPIDO DE FECHA (Ir a fecha...)
    // ════════════════════════════════════════════════════════
    private void abrirSelectorFecha() {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(panel),
                "Ir a fecha", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setUndecorated(true);
        dlg.getRootPane().setBorder(BorderFactory.createLineBorder(C[3], 2));

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(Color.WHITE);

        // ── Header ────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C[1]);
        header.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 12));
        JLabel lTit = new JLabel("Ir a fecha");
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lTit.setForeground(Color.WHITE);
        JButton btnX = new JButton("  X  ");
        btnX.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnX.setForeground(Color.WHITE);
        btnX.setBackground(new Color(180, 30, 30));
        btnX.setOpaque(true); btnX.setBorderPainted(false); btnX.setFocusPainted(false);
        btnX.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { dlg.dispose(); } });
        header.add(lTit, BorderLayout.WEST);
        header.add(btnX, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Selector mes/año ──────────────────────────────
        final LocalDate[] seleccionado = {semanaInicio};
        final JPanel[] refGrid = {null};

        // Estado navegable del mini-calendario
        final int[] navYear  = {semanaInicio.getYear()};
        final int[] navMonth = {semanaInicio.getMonthValue()};

        JPanel body = new JPanel(new BorderLayout(0, 8));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        // Fila de mes/año con flechas
        JPanel navMes = new JPanel(new BorderLayout(6, 0));
        navMes.setBackground(Color.WHITE);

        JButton bAntMes = miniNavBtn("<");
        JButton bSigMes = miniNavBtn(">");
        JLabel lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMesAnio.setForeground(C[6]);

        navMes.add(bAntMes, BorderLayout.WEST);
        navMes.add(lblMesAnio, BorderLayout.CENTER);
        navMes.add(bSigMes, BorderLayout.EAST);
        body.add(navMes, BorderLayout.NORTH);

        // Panel contenedor del grid de días
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        body.add(gridWrapper, BorderLayout.CENTER);

        // Función para construir el grid de días
        Runnable[] construirGrid = {null};
        construirGrid[0] = new Runnable() {
            @Override
            public void run() {
            gridWrapper.removeAll();

            int anio = navYear[0], mes = navMonth[0];
            DateTimeFormatter fMes = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es","CO"));
            LocalDate primeroDia = LocalDate.of(anio, mes, 1);
            String mesAnioStr = primeroDia.format(fMes);
            lblMesAnio.setText(Character.toUpperCase(mesAnioStr.charAt(0)) + mesAnioStr.substring(1));

            JPanel grid = new JPanel(new GridLayout(0, 7, 3, 3));
            grid.setBackground(Color.WHITE);

            // Cabecera días
            String[] dias = {"Lu","Ma","Mi","Ju","Vi","Sá","Do"};
            for (String d : dias) {
                JLabel lh = new JLabel(d, SwingConstants.CENTER);
                lh.setFont(new Font("Segoe UI", Font.BOLD, 11));
                lh.setForeground(C[7]);
                grid.add(lh);
            }

            // Espacios vacíos antes del primer día
            int inicioDia = primeroDia.getDayOfWeek().getValue() - 1; // 0=Lun
            for (int i = 0; i < inicioDia; i++) grid.add(new JLabel());

            // Días del mes
            int diasMes = primeroDia.lengthOfMonth();
            for (int d = 1; d <= diasMes; d++) {
                LocalDate fecha = LocalDate.of(anio, mes, d);
                boolean esDom  = fecha.getDayOfWeek() == DayOfWeek.SUNDAY;
                boolean esHoy2 = fecha.equals(LocalDate.now());
                boolean esSelec = fecha.with(DayOfWeek.MONDAY).equals(seleccionado[0].with(DayOfWeek.MONDAY));

                JButton btn = new JButton(String.valueOf(d));
                btn.setFont(new Font("Segoe UI", esHoy2 ? Font.BOLD : Font.PLAIN, 12));
                btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
                btn.setCursor(esDom ? Cursor.getDefaultCursor() : new Cursor(Cursor.HAND_CURSOR));
                btn.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

                if (esDom) {
                    btn.setBackground(new Color(250, 245, 245));
                    btn.setForeground(new Color(200, 150, 150));
                    btn.setEnabled(false);
                } else if (esSelec) {
                    btn.setBackground(C[3]);
                    btn.setForeground(Color.WHITE);
                } else if (esHoy2) {
                    btn.setBackground(new Color(220, 245, 230));
                    btn.setForeground(C[1]);
                } else {
                    btn.setBackground(Color.WHITE);
                    btn.setForeground(C[6]);
                }

                final LocalDate fechaFinal = fecha;
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ev) {
                        seleccionado[0] = fechaFinal.with(DayOfWeek.MONDAY);
                        semanaInicio = seleccionado[0];
                        dlg.dispose();
                        construir();
                    }
                });

                // Hover
                if (!esDom) {
                    Color bgNormal = btn.getBackground();
                    btn.addMouseListener(new MouseAdapter() {
                        @Override public void mouseEntered(MouseEvent e) {
                            if (!esSelec) btn.setBackground(new Color(200, 235, 215));
                        }
                        @Override public void mouseExited(MouseEvent e) {
                            btn.setBackground(bgNormal);
                        }
                    });
                }
                grid.add(btn);
            }

            refGrid[0] = grid;
            gridWrapper.add(grid, BorderLayout.CENTER);
            gridWrapper.revalidate();
            gridWrapper.repaint();
            dlg.pack();
            }
        };

        // Lógica de navegación de mes
        bAntMes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navMonth[0]--;
                if (navMonth[0] < 1) { navMonth[0] = 12; navYear[0]--; }
                construirGrid[0].run();
            }
        });
        bSigMes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navMonth[0]++;
                if (navMonth[0] > 12) { navMonth[0] = 1; navYear[0]++; }
                construirGrid[0].run();
            }
        });

        // Nota informativa
        JLabel nota = new JLabel("<html><i style='color:#888'>Selecciona cualquier día — el calendario<br>saltará a esa semana.</i></html>",
                SwingConstants.CENTER);
        nota.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        nota.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        body.add(nota, BorderLayout.SOUTH);

        root.add(body, BorderLayout.CENTER);
        dlg.setContentPane(root);

        // Construir grid inicial
        construirGrid[0].run();

        dlg.pack();
        dlg.setMinimumSize(new Dimension(260, 0));
        Point loc = panel.getLocationOnScreen();
        dlg.setLocation(loc.x + panel.getWidth() / 2 - dlg.getWidth() / 2,
                loc.y + panel.getHeight() / 2 - dlg.getHeight() / 2);
        dlg.setVisible(true);
    }

    private JButton miniNavBtn(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(C[4]); b.setForeground(C[6]);
        b.setOpaque(true); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            final Color o = b.getBackground();
            @Override public void mouseEntered(MouseEvent e) { b.setBackground(o.brighter()); }
            @Override public void mouseExited(MouseEvent e)  { b.setBackground(o); }
        });
        return b;
    }

    private boolean esHoy(LocalDate f){ return f.equals(LocalDate.now()); }

    private boolean esVacuna(Citas c) {
        return c.getMotivo() != null && c.getMotivo().toLowerCase().contains("vacun");
    }
}
