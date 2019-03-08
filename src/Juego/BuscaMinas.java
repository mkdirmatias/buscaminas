/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Juego;

import Clases.Coordenada;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Base64;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.UIManager;

/**
 *
 * @author unkndown
 */
public class BuscaMinas extends javax.swing.JFrame {

    // Variables para usar dentro del juego
    private JToggleButton tablero[][];
    private boolean esMina[][], flags[][], actual[][], estadoPartida = false, cargado = false, estadoCronometro = true;
    private int x = 0, y = 0;
    public int porcentajeMinas, columnas = 2, filas, minasT = 0;
    private String tiempo = "19";
    public String ruta = "";
    private int segundos,minutos;

    // Creamos una ventana de configuracion
    Configuracion conf = new Configuracion();

    /**
     * Creates new form BuscaMinas
     */
    public BuscaMinas() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

    /*
    * Iniciar Cronometro
    */
    private void iniciarCronometro() {
        Thread hilo = new Thread() {
            @Override
            public void run() {
                while(estadoCronometro){
                        try {
                            sleep(1000);
                            segundos++;
                            if(segundos >=60)
                            {
                                segundos=0;
                                minutos++;
                            }
                            
                            segundosLabel.setText(Integer.toString(segundos));
                            minutosLabel.setText(Integer.toString(minutos));
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                }
            }
        };
        hilo.start();
    }
    

    /**
    * Cargar una partida desde archivo txt
    */
    public void cargar() throws IOException, Exception {
        BufferedReader partida = new BufferedReader(new FileReader(ruta));
        String datos = partida.readLine().trim();
        partida.close();
        byte[] decodedBytes = Base64.getDecoder().decode(datos.getBytes());
        String[] informacion = new String(decodedBytes).split("\n");
        
        // actualizamos datos del cronometro
        segundos=Integer.parseInt(informacion[4]);
        minutos=Integer.parseInt(informacion[5]);
        segundosLabel.setText(Integer.toString(segundos));
        minutosLabel.setText(Integer.toString(minutos));
        iniciarCronometro();

        // filas obtenidas desde el txt
        int filas_ob = Integer.parseInt(informacion[2]);
        int cols_ob = Integer.parseInt(informacion[6]);
        columnas = cols_ob;
        filas = filas_ob;

        // Dimensiones tablero
        this.setSize((filas * 25) + 20, (columnas * 25) + 115);
        campo.setLayout(new GridLayout(0, columnas));
        tablero = new JToggleButton[filas][columnas];
        esMina = new boolean[filas][columnas];
        flags = new boolean[filas][columnas];
        actual = new boolean[filas][columnas];
        cargado = true;

        // pre-tablero
        int tab_car[][] = new int[filas_ob][filas_ob];

        // minas
        minasT = Integer.parseInt(informacion[1]);

        // filas
        String[] filas_txt = informacion[3].split(";");

        for (int i = 0; i < filas; i++) {
            String[] numeros = filas_txt[i].split("  ");

            for (int j = 0; j < columnas; j++) {
                tab_car[i][j] = Integer.parseInt(numeros[j]);
            }
        }

        // cargar botones en el tablero
        for (y = 0; y < filas; y++) {
            for (x = 0; x < columnas; x++) {
                JToggleButton boton = new JToggleButton();
                boton.setActionCommand(x + "," + y);
                boton.addActionListener((java.awt.event.ActionEvent e) -> {
                    Coordenada posicion;
                    JToggleButton pulsado = (JToggleButton) e.getSource();
                    if (pulsado.isSelected()) {
                        if (!estadoPartida) {
                            posicion = obtienePosicion(e.getActionCommand());
                            presionarCampo(posicion.getx(), posicion.gety());
                        } else {
                            pulsado.setSelected(false);
                        }

                    } else {
                        pulsado.setSelected(true);
                    }
                });
                boton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Coordenada posicion;
                        JToggleButton pulsado = (JToggleButton) e.getSource();
                        posicion = obtienePosicion(pulsado.getActionCommand());
                        int boton = e.getButton();
                        pulsado.setIcon(null);

                        if (boton == 3) {
                            if (!estadoPartida) {
                                if (!tablero[posicion.gety()][posicion.getx()].isSelected()) {
                                    if (flags[posicion.gety()][posicion.getx()] == true) {
                                        pulsado.setIcon(null);
                                        flags[posicion.gety()][posicion.getx()] = false;
                                    } else {
                                        flags[posicion.gety()][posicion.getx()] = true;
                                        pulsado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/flag.png")));
                                    }
                                }
                                comprobarJuego();
                            }
                        }
                    }
                });
                // agregamos el boton al tablero
                tablero[y][x] = boton;
                tablero[y][x].setMinimumSize(new Dimension(10, 10));
                tablero[y][x].setMaximumSize(new Dimension(10, 10));
                tablero[y][x].setPreferredSize(new Dimension(10, 10));
                campo.add(tablero[y][x]);
                esMina[y][x] = false;
            }
        }

        // recorrer y actualizar los botones segun su estado
        for (int i = 0; i < filas; i++) {
            String[] numeros = filas_txt[i].split("  ");

            for (int j = 0; j < columnas; j++) {
                tab_car[i][j] = Integer.parseInt(numeros[j]);

                switch (tab_car[i][j]) {
                    case 1:
                        esMina[i][j] = true;
                        flags[i][j] = true;
                        tablero[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/flag.png")));
                        break;
                    case 2:
                        esMina[i][j] = true;
                        flags[i][j] = false;
                        break;
                    case 3:
                        esMina[i][j] = false;
                        flags[i][j] = true;
                        tablero[i][j].setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/flag.png")));
                        break;
                    case 4:
                        esMina[i][j] = false;
                        flags[i][j] = false;
                        break;
                }
            }
        }

        // recorrer y presionar los botones ya seleccionados anteriormente
        for (int i = 0; i < filas; i++) {
            String[] numeros = filas_txt[i].split("  ");

            for (int j = 0; j < columnas; j++) {
                tab_car[i][j] = Integer.parseInt(numeros[j]);

                switch (tab_car[i][j]) {
                    case 5:
                        tablero[i][j].setSelected(true);
                        presionarCampo(j, i);
                        break;
                }
            }
        }
        // total minas
        minasTotal.setText(informacion[1]);
    }

    /**
    * Crear una nueva partida
    */
    public void inicio() {
        // Dimensiones tablero
        this.setSize((filas * 25) + 20, (columnas * 25) + 115);
        campo.setLayout(new GridLayout(0, columnas));
        tablero = new JToggleButton[filas][columnas];
        esMina = new boolean[filas][columnas];
        flags = new boolean[filas][columnas];
        actual = new boolean[filas][columnas];
        // Crear y mostrar
        construyeTablero();
        minarCampo();
        iniciarCronometro();
        minasTotal.setText(Integer.toString(minasT));
    }

    /*
    * Volver a jugar, muestra un mensaje de si quiere volver a comenzar
    */
    private void finJuego(String mensaje) {
        // Mensaje de confirmacion para volver a jugar
        int volverJugar = JOptionPane.showConfirmDialog(null, mensaje);
        this.estadoPartida = true;

        // Evaluar respuesta
        switch (volverJugar) {
            case 0:
                // volver a jugar
                this.setVisible(false);
                conf.setVisible(true);
                this.estadoPartida = false;
                break;
            case 1:
                // Evaluar cierre de juego
                int cerrar = JOptionPane.showConfirmDialog(null, "Seguro de cerrar el juego?");
                if (cerrar == 0) {
                    System.exit(1);
                } else {
                    finJuego(mensaje);
                }
                break;
            case 2:
                // En caso que presione cancelar
                finJuego(mensaje);
                break;
        }
    }

    /*
    * Mostrar las minas
     */
    private void mostrarMinas() {
        for (int x = 0; x < columnas; x++) {
            for (int y = 0; y < filas; y++) {
                if (esMina[y][x]) {
                    if (actual[y][x] == false) {
                        tablero[y][x].setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/mina.png")));
                    } else {
                        tablero[y][x].setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/minaActual.png")));
                    }
                }
            }
        }

    }

    /*
    * Colocar las minas
     */
    private void minarCampo() {
        int aleatorio;
        boolean mina;

        for (int x = 0; x < columnas; x++) {
            for (int y = 0; y < filas; y++) {
                // definimos el estado de la bandera
                flags[y][x] = false;

                // definimos la mina que pondrá fin al juego
                actual[y][x] = false;

                // colocamos las minas
                aleatorio = (int) (Math.random() * (100));
                aleatorio++;
                if (aleatorio <= porcentajeMinas) {
                    minasT++;
                    mina = true;
                    esMina[y][x] = true;
                }
            }
        }

    }

    
    /*
    * Ir comprobando el estado del juego
    */
    private void comprobarJuego() {
        int libres = 0;
        for (int x = 0; x < filas; x++) {
            for (int y = 0; y < columnas; y++) {
                if (tablero[x][y].isSelected()) {
                    libres++;
                }
            }
        }

        if (((filas * columnas) - minasT) == libres) {
            finJuego("Felicidades has ganado la partida, quieres volver a jugar otra?");
        }
    }


    /*
    * Click sobre un botón, se usa cuando se hace click sobre un botón
     */
    private void presionarCampo(int x, int y) {
        // verificar si es mina
        if (esMina[y][x]) {
            actual[y][x] = true;
            mostrarMinas();
            finJuego("Has perdido, has tocado una mina, quieres volver a jugar otra?");
        } else {
            // si no es mina, muestra las minas que tiene alrededor
            minasVecinas(x, y);
        }
        // volvemos a comprobar el estado del juego
        comprobarJuego();
    }

    /*
    * Comprobar el numero de minas alrededor
     */
    private void minasVecinas(int x, int y) {
        int cuentaminas = 0;
        boolean estaizquierda;
        boolean estaderecha;
        boolean estaarriba;
        boolean estaabajo;

        if (x > 0) {
            estaizquierda = false;
        } else {
            estaizquierda = true;
        }

        if (x < columnas - 1) {
            estaderecha = false;

        } else {
            estaderecha = true;
        }
        if (y > 0) {
            estaarriba = false;
        } else {
            estaarriba = true;
        }
        if (y < filas - 1) {
            estaabajo = false;
        } else {
            estaabajo = true;
        }
        if (!estaizquierda && !estaarriba) {
            if (esMina[y - 1][x - 1]) {
                cuentaminas++;
            }
        }

        if (!estaarriba) {
            if (esMina[y - 1][x]) {
                cuentaminas++;
            }
        }
        if (!estaderecha && !estaarriba) {
            if (esMina[y - 1][x + 1]) {
                cuentaminas++;
            }
        }
        if (!estaderecha) {
            if (esMina[y][x + 1]) {
                cuentaminas++;
            }
        }
        if (!estaderecha & !estaabajo) {
            if (esMina[y + 1][x + 1]) {
                cuentaminas++;
            }
        }

        if (!estaabajo) {
            if (esMina[y + 1][x]) {
                cuentaminas++;
            }
        }
        if (!estaabajo && !estaizquierda) {
            if (esMina[y + 1][x - 1]) {
                cuentaminas++;
            }
        }
        if (!estaizquierda) {
            if (esMina[y][x - 1]) {
                cuentaminas++;
            }
        }
        if (cuentaminas == 0) {

            if (!estaizquierda && !estaarriba) {
                if (!esMina[y - 1][x - 1]) {
                    if (!tablero[y - 1][x - 1].isSelected()) {
                        tablero[y - 1][x - 1].setSelected(true);
                        minasVecinas(x - 1, y - 1);
                    }
                }
            }

            if (!estaarriba) {
                if (!esMina[y - 1][x]) {
                    if (!tablero[y - 1][x].isSelected()) {
                        tablero[y - 1][x].setSelected(true);
                        minasVecinas(x, y - 1);
                    }
                }
            }
            if (!estaderecha && !estaarriba) {
                if (!esMina[y - 1][x + 1]) {
                    if (!tablero[y - 1][x + 1].isSelected()) {
                        tablero[y - 1][x + 1].setSelected(true);
                        minasVecinas(x + 1, y - 1);
                    }
                }
            }
            if (!estaderecha) {
                if (!esMina[y][x + 1]) {
                    if (!tablero[y][x + 1].isSelected()) {
                        tablero[y][x + 1].setSelected(true);
                        minasVecinas(x + 1, y);
                    }
                }
            }
            if (!estaderecha & !estaabajo) {
                if (!esMina[y + 1][x + 1]) {
                    if (!tablero[y + 1][x + 1].isSelected()) {
                        tablero[y + 1][x + 1].setSelected(true);
                        minasVecinas(x + 1, y + 1);
                    }
                }
            }
            if (!estaabajo) {
                if (!esMina[y + 1][x]) {
                    if (!tablero[y + 1][x].isSelected()) {
                        tablero[y + 1][x].setSelected(true);
                        minasVecinas(x, y + 1);
                    }
                }
            }
            if (!estaabajo && !estaizquierda) {
                if (!esMina[y + 1][x - 1]) {
                    if (!tablero[y + 1][x - 1].isSelected()) {
                        tablero[y + 1][x - 1].setSelected(true);
                        minasVecinas(x - 1, y + 1);
                    }

                }
            }
            if (!estaizquierda) {
                if (!esMina[y][x - 1]) {
                    if (!tablero[y][x - 1].isSelected()) {
                        tablero[y][x - 1].setSelected(true);
                        minasVecinas(x - 1, y);
                    }
                }
            }
        } else {
            tablero[y][x].setText(String.valueOf(cuentaminas));
        }
    }

    /*
    * Obtener la posicion del boton seleccionado
     */
    private Coordenada obtienePosicion(String cadena) {
        Coordenada posicionleida = new Coordenada();
        String[] coordenadas;
        coordenadas = cadena.split(",");
        posicionleida.setx(Integer.parseInt(coordenadas[0]));
        posicionleida.sety(Integer.parseInt(coordenadas[1]));

        return posicionleida;
    }

    /*
    * Construir el tablero, si es un juego nuevo
     */
    private void construyeTablero() {

        for (y = 0; y < filas; y++) {
            for (x = 0; x < columnas; x++) {

                // creamos un boton nuevo
                JToggleButton boton = new JToggleButton();
                boton.setActionCommand(x + "," + y);
                // añadimos un listener para verificar si ha sido presionado
                boton.addActionListener((java.awt.event.ActionEvent e) -> {
                    Coordenada posicion;

                    JToggleButton pulsado = (JToggleButton) e.getSource();
                    // si fue presionado
                    if (pulsado.isSelected()) {
                        // verificamos el estado del juego
                        if (!estadoPartida) {
                            // si no se ha ganado aún, obtenemos la posicion
                            posicion = obtienePosicion(e.getActionCommand());
                            // y lo presionamos
                            presionarCampo(posicion.getx(), posicion.gety());
                        } else {
                            // si terminó la partida, lo dejamos como false
                            pulsado.setSelected(false);
                        }

                    } else {
                        // si fue presionado, dejamos el estado en true
                        pulsado.setSelected(true);
                    }
                });
                // añadimos un MouseListener
                boton.addMouseListener(new MouseAdapter() {
                    @Override
                    // Para verificar si se está haciendo click para colocar una bandera
                    public void mouseClicked(MouseEvent e) {
                        // obtenemos la posicion y el estado
                        Coordenada posicion;
                        JToggleButton pulsado = (JToggleButton) e.getSource();
                        posicion = obtienePosicion(pulsado.getActionCommand());
                        int boton = e.getButton();
                        // quitamos la bandera en caso que se hizo click normal (click izquierdo)
                        pulsado.setIcon(null);

                        // verificar si se está haciendo click derecho
                        if (boton == 3) {
                            // verificamos la partida
                            if (!estadoPartida) {
                                // verificamos si no está presioando
                                if (!tablero[posicion.gety()][posicion.getx()].isSelected()) {
                                    // verificamos si es una bandera
                                    if (flags[posicion.gety()][posicion.getx()] == true) {
                                        // si ya fue presionado, quitamos el icono y lo dejamos false
                                        pulsado.setIcon(null);
                                        flags[posicion.gety()][posicion.getx()] = false;
                                    } else {
                                        // si se está presionando colocamos la imagen de la bandera
                                        flags[posicion.gety()][posicion.getx()] = true;
                                        pulsado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/flag.png")));
                                    }
                                }
                                // volvemos a comprobar el juego
                                comprobarJuego();
                            }
                        }
                    }
                });
                // metemos el boton al tablero
                tablero[y][x] = boton;
                tablero[y][x].setMinimumSize(new Dimension(10, 10));
                tablero[y][x].setMaximumSize(new Dimension(10, 10));
                tablero[y][x].setPreferredSize(new Dimension(10, 10));
                campo.add(tablero[y][x]);
                esMina[y][x] = false;
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        informacion = new javax.swing.JPanel();
        minasTotal = new javax.swing.JTextField();
        reload = new javax.swing.JLabel();
        minutosLabel = new javax.swing.JTextField();
        segundosLabel = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        controlarCronometro = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        campo = new javax.swing.JPanel();
        menu = new javax.swing.JMenuBar();
        selectMenu = new javax.swing.JMenu();
        cambiarNivel = new javax.swing.JMenuItem();
        saveNivel = new javax.swing.JMenuItem();
        instrucciones = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        informacion.setBackground(new java.awt.Color(51, 51, 51));
        informacion.setForeground(new java.awt.Color(255, 255, 255));

        minasTotal.setEditable(false);
        minasTotal.setBackground(new java.awt.Color(51, 51, 51));
        minasTotal.setForeground(new java.awt.Color(255, 255, 255));
        minasTotal.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        minasTotal.setText("0");
        minasTotal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Minas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(255, 255, 255))); // NOI18N

        reload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Assets/reload.png"))); // NOI18N
        reload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                reloadMouseClicked(evt);
            }
        });

        minutosLabel.setBackground(new java.awt.Color(51, 51, 51));
        minutosLabel.setForeground(new java.awt.Color(255, 255, 255));
        minutosLabel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        minutosLabel.setText("0");
        minutosLabel.setBorder(null);

        segundosLabel.setBackground(new java.awt.Color(51, 51, 51));
        segundosLabel.setForeground(new java.awt.Color(255, 255, 255));
        segundosLabel.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        segundosLabel.setText("0");
        segundosLabel.setBorder(null);

        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText(":");

        controlarCronometro.setText("||");
        controlarCronometro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controlarCronometroActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout informacionLayout = new javax.swing.GroupLayout(informacion);
        informacion.setLayout(informacionLayout);
        informacionLayout.setHorizontalGroup(
            informacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informacionLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(minutosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(segundosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(controlarCronometro, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(reload)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(minasTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        informacionLayout.setVerticalGroup(
            informacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(informacionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(informacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minasTotal, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(reload, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minutosLabel, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, informacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(segundosLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(controlarCronometro)))
                .addContainerGap())
        );

        jScrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51), 3));

        campo.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jScrollPane1.setViewportView(campo);

        selectMenu.setText("Menú");

        cambiarNivel.setText("Cambiar Nivel");
        cambiarNivel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cambiarNivelActionPerformed(evt);
            }
        });
        selectMenu.add(cambiarNivel);

        saveNivel.setText("Guardar Nivel");
        saveNivel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveNivelActionPerformed(evt);
            }
        });
        selectMenu.add(saveNivel);

        menu.add(selectMenu);

        instrucciones.setText("Instrucciones");
        menu.add(instrucciones);

        setJMenuBar(menu);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(informacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(informacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
    * Cambiar nivel desde el menú
     */
    private void cambiarNivelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cambiarNivelActionPerformed
        // mostramos mensaje de advertencia
        int change = JOptionPane.showConfirmDialog(null, "Seguro de cambiar nivel?");

        // si selecciona que si
        if (change == 0) {
            // volvemos a la ventana de configuracion
            this.setVisible(false);
            conf.setVisible(true);
            this.estadoPartida = false;
        }
    }//GEN-LAST:event_cambiarNivelActionPerformed

    /*
    * Reiniciar el nivel
     */
    private void reloadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reloadMouseClicked
        // mensaje de advertencia
        int reset = JOptionPane.showConfirmDialog(null, "Seguro de cambiar tablero?");
        // si selecciona que sí
        if (reset == 0) {
            // verificamos si se ha cargado la partida
            if (cargado) {
                // si se cargó la partida, volvemos al estado inicial del guardado
                campo.removeAll();
                campo.updateUI();
                try {
                    cargar();
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            } else {
                // si el juego es nuevo, cramos un tablero nuevo
                campo.removeAll();
                campo.updateUI();
                inicio();
            }
        }
    }//GEN-LAST:event_reloadMouseClicked

    /*
    * Guardar la partida
     */
    private void saveNivelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveNivelActionPerformed
        int save = JOptionPane.showConfirmDialog(null, "¿Guardar Partida?");

        if (save == 0) {
            javax.swing.JFileChooser jF1 = new javax.swing.JFileChooser();
            String ruta = "";
            try {
                if (jF1.showSaveDialog(null) == jF1.APPROVE_OPTION) {
                    ruta = jF1.getSelectedFile().getAbsolutePath();
                    // dato
                    String linea = "";
                    // Guardar txt
                    PrintStream partidaTxt = new PrintStream(ruta + ".txt");
                    // la primera linea será el tiempo
                    linea += tiempo + "\n";
                    // La segunda las minas
                    linea += minasT + "\n";
                    // la tercera las filas
                    linea += filas + "\n";

                    for (int x = 0; x < filas; x++) {
                        String fila = " ";
                        for (int y = 0; y < columnas; y++) {
                            // verificar si está seleccionado
                            if (!tablero[x][y].isSelected()) {
                                // no está seleccionado
                                // verificar si es mina
                                if (esMina[x][y]) {
                                    // es mina
                                    // verificar si es bandera
                                    if (flags[x][y]) {
                                        // es bandera
                                        fila += " 1 ";
                                    } else {
                                        // no es bandera
                                        fila += " 2 ";
                                    }
                                } else {
                                    // no es mina
                                    // verificar si es bandera
                                    if (flags[x][y]) {
                                        // es bandera
                                        fila += " 3 ";
                                    } else {
                                        // no es bandera
                                        fila += " 4 ";
                                    }
                                }
                            } else {
                                // está seleccionado
                                fila += " 5 ";
                            }
                        }
                        linea += fila.trim() + ";";

                    }
                    
                    // la penultima linea serán los segundos
                    linea += "\n"+segundos + "\n";
                    // la ultima serán los minutos
                    linea += minutos + "\n";
                    
                    // la ultima serán los columnas
                    linea += columnas + "\n";
                    
                    byte[] cifrado = Base64.getEncoder().encode(linea.trim().getBytes());
                    partidaTxt.println(new String(cifrado));
                    //partidaTxt.println(linea);
                    // Mensaje de guardado
                    JOptionPane.showMessageDialog(null, "Partida Guardada");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_saveNivelActionPerformed

    private void controlarCronometroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_controlarCronometroActionPerformed
        if(controlarCronometro.isSelected())
        {
            estadoCronometro=false;
            controlarCronometro.setText(">");
        }
        else
        {
            estadoCronometro=true;
            controlarCronometro.setText("||");
            iniciarCronometro();
        }
    }//GEN-LAST:event_controlarCronometroActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws IOException, Exception {
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
        java.awt.EventQueue.invokeLater(() -> {
            new BuscaMinas().setVisible(true);
        });

        BuscaMinas b = new BuscaMinas();
        b.cargar();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cambiarNivel;
    private javax.swing.JPanel campo;
    private javax.swing.JToggleButton controlarCronometro;
    private javax.swing.JPanel informacion;
    private javax.swing.JMenu instrucciones;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuBar menu;
    private javax.swing.JTextField minasTotal;
    private javax.swing.JTextField minutosLabel;
    private javax.swing.JLabel reload;
    private javax.swing.JMenuItem saveNivel;
    private javax.swing.JTextField segundosLabel;
    private javax.swing.JMenu selectMenu;
    // End of variables declaration//GEN-END:variables
}
