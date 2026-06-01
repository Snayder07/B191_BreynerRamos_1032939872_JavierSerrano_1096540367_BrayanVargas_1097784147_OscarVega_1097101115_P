package org.example.view;

import org.example.service.RecuperacionService;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de recuperación de contraseña en 3 pasos:
 *   Paso 1 → ingresa tu correo
 *   Paso 2 → ingresa el código recibido
 *   Paso 3 → ingresa la nueva contraseña
 */
public class RecuperacionContrasenaDialog extends JDialog {

    private final RecuperacionService recuperacionService = new RecuperacionService();

    // Guardamos el correo entre pasos
    private String correoActual = "";

    // Panel con CardLayout para los 3 pasos
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);

    // ── Colores Kampets ──────────────────────────────────
    private static final Color VERDE      = new Color(29, 158, 117);
    private static final Color VERDE_DARK = new Color(15, 110, 86);
    private static final Color BG         = new Color(240, 248, 244);
    private static final Color TEXTO      = new Color(80, 80, 80);

    public RecuperacionContrasenaDialog(Window owner) {
        super(owner, "Recuperar contraseña", ModalityType.APPLICATION_MODAL);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cardPanel.setBackground(BG);
        cardPanel.add(crearPaso1(), "paso1");
        cardPanel.add(crearPaso2(), "paso2");
        cardPanel.add(crearPaso3(), "paso3");

        setContentPane(cardPanel);
        pack();
        setLocationRelativeTo(owner);
    }

    // ══════════════════════════════════════════════════════
    //  PASO 1 — Ingresa correo
    // ══════════════════════════════════════════════════════
    private JPanel crearPaso1() {
        JPanel p = panelBase();

        agregarTitulo(p, "¿Olvidaste tu contraseña?");
        agregarSubtitulo(p, "Ingresa tu correo y te enviaremos un código de verificación.", 70);

        JTextField campoCorreo = campoTexto();
        campoCorreo.setBounds(40, 120, 340, 36);
        campoCorreo.setToolTipText("Tu correo registrado");
        JLabel lblCorreo = label("Correo electrónico", 40, 100);
        p.add(lblCorreo);
        p.add(campoCorreo);

        JButton btnEnviar = boton("Enviar código", VERDE);
        btnEnviar.setBounds(40, 180, 340, 40);
        p.add(btnEnviar);

        JButton btnCancelar = botonSecundario("Cancelar");
        btnCancelar.setBounds(40, 228, 340, 32);
        p.add(btnCancelar);

        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String correo = campoCorreo.getText().trim();
                if (correo.isEmpty()) {
                    mostrarError(p, "Por favor ingresa tu correo.");
                    return;
                }
                btnEnviar.setEnabled(false);
                btnEnviar.setText("Enviando...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            recuperacionService.enviarCodigoRecuperacion(correo);
                            correoActual = correo.toLowerCase();
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnEnviar.setEnabled(true);
                                    btnEnviar.setText("Enviar código");
                                    cardLayout.show(cardPanel, "paso2");
                                    pack();
                                }
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnEnviar.setEnabled(true);
                                    btnEnviar.setText("Enviar código");
                                    mostrarError(p, ex.getMessage());
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                RecuperacionContrasenaDialog.this.dispose();
            }
        });

        return p;
    }

    // ══════════════════════════════════════════════════════
    //  PASO 2 — Verificar código
    // ══════════════════════════════════════════════════════
    private JPanel crearPaso2() {
        JPanel p = panelBase();

        agregarTitulo(p, "Verifica tu código");
        agregarSubtitulo(p, "Ingresa el código de 6 dígitos que enviamos a tu correo.", 70);

        JTextField campoCodigo = campoTexto();
        campoCodigo.setFont(new Font("Arial", Font.BOLD, 22));
        campoCodigo.setHorizontalAlignment(SwingConstants.CENTER);
        campoCodigo.setBounds(100, 120, 220, 44);
        JLabel lblCodigo = label("Código de verificación", 40, 100);
        p.add(lblCodigo);
        p.add(campoCodigo);

        JButton btnVerificar = boton("Verificar", VERDE);
        btnVerificar.setBounds(40, 190, 340, 40);
        p.add(btnVerificar);

        JButton btnReenviar = botonSecundario("Reenviar código");
        btnReenviar.setBounds(40, 238, 160, 30);
        p.add(btnReenviar);

        JButton btnAtras = botonSecundario("← Atrás");
        btnAtras.setBounds(220, 238, 160, 30);
        p.add(btnAtras);

        btnVerificar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String cod = campoCodigo.getText().trim();
                if (cod.isEmpty()) {
                    mostrarError(p, "Ingresa el código recibido.");
                    return;
                }
                if (recuperacionService.verificarCodigo(correoActual, cod)) {
                    cardLayout.show(cardPanel, "paso3");
                    pack();
                } else {
                    mostrarError(p, "Código incorrecto. Revisa tu correo e intenta de nuevo.");
                }
            }
        });

        btnReenviar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btnReenviar.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            recuperacionService.enviarCodigoRecuperacion(correoActual);
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnReenviar.setEnabled(true);
                                    mostrarInfo(p, "Código reenviado. Revisa tu correo.");
                                }
                            });
                        } catch (Exception ex) {
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    btnReenviar.setEnabled(true);
                                    mostrarError(p, ex.getMessage());
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        btnAtras.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                campoCodigo.setText("");
                cardLayout.show(cardPanel, "paso1");
                pack();
            }
        });

        return p;
    }

    // ══════════════════════════════════════════════════════
    //  PASO 3 — Nueva contraseña
    // ══════════════════════════════════════════════════════
    private JPanel crearPaso3() {
        JPanel p = panelBase();

        agregarTitulo(p, "Nueva contraseña");
        agregarSubtitulo(p, "Crea una contraseña segura (mínimo 6 caracteres).", 70);

        JPasswordField campoNueva = campoPassword();
        campoNueva.setBounds(40, 120, 340, 36);
        JLabel lblNueva = label("Nueva contraseña", 40, 100);

        JPasswordField campoConfirmar = campoPassword();
        campoConfirmar.setBounds(40, 200, 340, 36);
        JLabel lblConfirmar = label("Confirmar contraseña", 40, 180);

        p.add(lblNueva);
        p.add(campoNueva);
        p.add(lblConfirmar);
        p.add(campoConfirmar);

        // ── Ojo para nueva ───────────────────────────
        JButton btnOjo_nueva = new JButton() {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new java.awt.Color(29,158,117) : new java.awt.Color(150,150,150));
                int cx=getWidth()/2, cy=getHeight()/2;
                g2.setStroke(new java.awt.BasicStroke(1.6f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.drawOval(cx-6, cy-4, 12, 8);
                g2.fillOval(cx-2, cy-2, 5, 5);
                if ((Boolean)getClientProperty("oculto") == Boolean.FALSE) {
                    g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx-8, cy+5, cx+8, cy-5);
                }
                g2.dispose();
            }
        };
        btnOjo_nueva.putClientProperty("oculto", Boolean.TRUE);
        btnOjo_nueva.setOpaque(false); btnOjo_nueva.setContentAreaFilled(false); btnOjo_nueva.setBorderPainted(false);
        btnOjo_nueva.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOjo_nueva.setBounds(352, 124, 28, 28);
        btnOjo_nueva.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                boolean oculto = (Boolean) btnOjo_nueva.getClientProperty("oculto");
                if (oculto) {
                    campoNueva.setEchoChar((char)0);
                    btnOjo_nueva.putClientProperty("oculto", Boolean.FALSE);
                } else {
                    campoNueva.setEchoChar('\u2022');
                    btnOjo_nueva.putClientProperty("oculto", Boolean.TRUE);
                }
                btnOjo_nueva.repaint();
            }
        });
        p.add(btnOjo_nueva);

        // ── Ojo para confirmar ───────────────────────────
        JButton btnOjo_confirmar = new JButton() {
            @Override protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new java.awt.Color(29,158,117) : new java.awt.Color(150,150,150));
                int cx=getWidth()/2, cy=getHeight()/2;
                g2.setStroke(new java.awt.BasicStroke(1.6f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                g2.drawOval(cx-6, cy-4, 12, 8);
                g2.fillOval(cx-2, cy-2, 5, 5);
                if ((Boolean)getClientProperty("oculto") == Boolean.FALSE) {
                    g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
                    g2.drawLine(cx-8, cy+5, cx+8, cy-5);
                }
                g2.dispose();
            }
        };
        btnOjo_confirmar.putClientProperty("oculto", Boolean.TRUE);
        btnOjo_confirmar.setOpaque(false); btnOjo_confirmar.setContentAreaFilled(false); btnOjo_confirmar.setBorderPainted(false);
        btnOjo_confirmar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnOjo_confirmar.setBounds(352, 204, 28, 28);
        btnOjo_confirmar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent ev) {
                boolean oculto = (Boolean) btnOjo_confirmar.getClientProperty("oculto");
                if (oculto) {
                    campoConfirmar.setEchoChar((char)0);
                    btnOjo_confirmar.putClientProperty("oculto", Boolean.FALSE);
                } else {
                    campoConfirmar.setEchoChar('\u2022');
                    btnOjo_confirmar.putClientProperty("oculto", Boolean.TRUE);
                }
                btnOjo_confirmar.repaint();
            }
        });
        p.add(btnOjo_confirmar);

        JButton btnCambiar = boton("Cambiar contraseña", VERDE);
        btnCambiar.setBounds(40, 255, 340, 40);
        p.add(btnCambiar);

        btnCambiar.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String nueva    = new String(campoNueva.getPassword());
                String confirma = new String(campoConfirmar.getPassword());
                try {
                    recuperacionService.cambiarContrasena(correoActual, nueva, confirma);
                    JOptionPane.showMessageDialog(RecuperacionContrasenaDialog.this,
                            "¡Contraseña actualizada exitosamente!\nYa puedes iniciar sesión con tu nueva contraseña.",
                            "Listo", JOptionPane.INFORMATION_MESSAGE);
                    RecuperacionContrasenaDialog.this.dispose();
                } catch (Exception ex) {
                    mostrarError(p, ex.getMessage());
                }
            }
        });

        return p;
    }

    // ══════════════════════════════════════════════════════
    //  Helpers de UI
    // ══════════════════════════════════════════════════════
    private JPanel panelBase() {
        JPanel p = new JPanel(null);
        p.setBackground(BG);
        p.setPreferredSize(new Dimension(420, 300));
        return p;
    }

    private void agregarTitulo(JPanel p, String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        lbl.setForeground(VERDE);
        lbl.setBounds(40, 20, 340, 30);
        p.add(lbl);
    }

    private void agregarSubtitulo(JPanel p, String texto, int y) {
        JLabel lbl = new JLabel("<html>" + texto + "</html>");
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(new Color(120, 120, 120));
        lbl.setBounds(40, y - 20, 340, 40);
        p.add(lbl);
    }

    private JLabel label(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        lbl.setForeground(TEXTO);
        lbl.setBounds(x, y, 200, 20);
        return lbl;
    }

    private JTextField campoTexto() {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private JPasswordField campoPassword() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Arial", Font.PLAIN, 13));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 220, 200), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return pf;
    }

    private JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton botonSecundario(String texto) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Arial", Font.PLAIN, 12));
        b.setBackground(BG);
        b.setForeground(VERDE);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(VERDE, 1));
        b.setCursor(Main.cursorHover != null ? Main.cursorHover : new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void mostrarError(JPanel p, String msg) {
        JOptionPane.showMessageDialog(p, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarInfo(JPanel p, String msg) {
        JOptionPane.showMessageDialog(p, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
