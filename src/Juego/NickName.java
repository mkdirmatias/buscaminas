/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;

/**
 *
 * @author unkndown
 */
public class NickName extends javax.swing.JFrame {

    /**
     * Creates new form NickName
     */
    public NickName() {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setIconImage(new ImageIcon(getClass().getResource("../Assets/mina.png")).getImage());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nickName = new javax.swing.JTextField();
        startGame = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        Fondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getIconImage());
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        nickName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nickName.setText("Nick Name");
        nickName.setBorder(null);
        getContentPane().add(nickName, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 220, 150, 30));

        startGame.setBackground(new java.awt.Color(255, 255, 255));
        startGame.setText("Iniciar");
        startGame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                startGameMouseClicked(evt);
            }
        });
        getContentPane().add(startGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 280, -1, -1));

        jLabel1.setText("UnknDown ®");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 300, -1, 20));

        Fondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/FondoInicio.png"))); // NOI18N
        getContentPane().add(Fondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startGameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_startGameMouseClicked

        // Verificar que el nickname no sea vacio y enviar a la pantalla de confiuracion
        String nick = nickName.getText().toUpperCase();
        if (!nick.isEmpty() && nick.equals("NICK NAME") ==  false) {
            // no es vacio ni Nick Name
            Configuracion conf = new Configuracion();
            conf.nickNameJugador.setText(nick);

            // Mostramos la pantalla de configuracion
            conf.setVisible(true);
            this.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(null, "Ingresa un Nick Name");
        }
    }//GEN-LAST:event_startGameMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NickName().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Fondo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField nickName;
    private javax.swing.JButton startGame;
    // End of variables declaration//GEN-END:variables
}
