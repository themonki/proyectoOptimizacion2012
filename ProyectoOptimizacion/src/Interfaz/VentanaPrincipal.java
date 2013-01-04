package Interfaz;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;

import Lectura.Lector;
import Solver.Solver_basuro_ultimate;
import Utilidades.Button;
import Utilidades.Estilos;
import java.awt.Color;

import java.awt.Insets;
import java.text.DecimalFormat;


import lpsolve.*;

/**
 *
 * @author Edgar Andrés Moncada
 * @
 */
public class VentanaPrincipal extends JFrame {

    Manejador manejador;
    CanvasGrid canvasGrid;
    JLabel value_coordenadaBasurero, value_coordenadaCiudadCercana, value_funcionObjetivo;
    JMenuItem menuFile_Open;
    String dirArchivo;
    JScrollPane jsp;
    Button btEjecutar;
    JPanel informacion;
    Lector lector;

    public VentanaPrincipal() throws HeadlessException {

        super();
        init();
        initBarMenu();
        agregarEventos();
        propiedadesJFrame();

    }

    public void init() {


        //Instancias Locales
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        JPanel panelDatos = new JPanel(new GridBagLayout());
        informacion = new JPanel();
        informacion.setLayout(new GridLayout(6, 1, 6, 6));


        JLabel info = new JLabel("::Informacion::");
        JLabel label_coordenadaBasurero = new JLabel(" Coordenadas del Basurero:");
        JLabel label_cordenadaCiudadCercana = new JLabel(" Coordenadas Ciudad mas Cercana:");

        //JLabel label_distanciaCiudadCercana = new JLabel(" Distancia a la ciudad mas cercana:");       
        JLabel label_funcionObjetivo = new JLabel(" Valor Funcion Objetivo::");

       

        info.setBackground(Color.gray);
        info.setFont(Utilidades.Estilos.fontSubrayados);
        label_coordenadaBasurero.setFont(Utilidades.Estilos.fontLabels);
        //label_distanciaCiudadCercana.setFont(Utilidades.Estilos.fontLabels);

        label_funcionObjetivo.setFont(Utilidades.Estilos.fontLabels);
        label_cordenadaCiudadCercana.setFont(Utilidades.Estilos.fontLabels);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 30;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);

        //Instanciando Atributos
        this.canvasGrid = new CanvasGrid();
        this.jsp = new JScrollPane();

        value_coordenadaBasurero = new JLabel("");

        value_coordenadaCiudadCercana = new JLabel("");
        value_funcionObjetivo = new JLabel("");




        this.btEjecutar = new Button("Ejecutar");
        btEjecutar.setEnabled(false);

        //Modificadores
        this.jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        //adicionando elementos
        int movery = 0;

        panelDatos.add(info, gbc);
        movery++;
        gbc.gridy = movery;
        panelDatos.add(informacion, gbc);


        movery++;
        gbc.gridy = movery;

        panelDatos.add(this.btEjecutar, gbc);
        gbc.ipady = 20;
        /*
         movery++;
         gbc.gridy=movery;
         panelDatos.add(this.labelDump,gbc);
         movery++;
         gbc.gridy=movery;
         panelDatos.add(etiquetaCiudadCercana, gbc);
         movery++;
         gbc.gridy=movery;
         panelDatos.add(this.labelDistancia,gbc);
         * */
        informacion.add(label_coordenadaBasurero);
        informacion.add(this.value_coordenadaBasurero);

        informacion.add(label_cordenadaCiudadCercana);
        informacion.add(this.value_coordenadaCiudadCercana);

        //informacion.add(label_distanciaCiudadCercana);
        //informacion.add(this.value_DistanciaCiudadCercana);


        informacion.add(label_funcionObjetivo);
        informacion.add(this.value_funcionObjetivo);



        informacion.setBackground(Color.WHITE);


        jsp.setViewportView(canvasGrid);
        jsp.setPreferredSize(new Dimension(550, 550));

        container.add(panelDatos, BorderLayout.EAST);
        container.add(jsp, BorderLayout.CENTER);


        //this.pack();

    }

    public void initBarMenu() {
        JMenuBar jmb = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuHelp = new JMenu("Help");

        menuFile_Open = new JMenuItem("Open");
        JMenuItem menuHelp_About = new JMenuItem("About");

        menuFile_Open.setAccelerator(KeyStroke.getKeyStroke("control O"));

        menuFile.add(menuFile_Open);
        menuHelp.add(menuHelp_About);
        jmb.add(menuFile);
        jmb.add(menuHelp);
        this.setJMenuBar(jmb);

    }

    public void propiedadesJFrame() {
        this.setTitle("Proyecto");
        this.setSize(1000, 700);
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void agregarEventos() {
        this.manejador = new Manejador();
        this.menuFile_Open.addActionListener(this.manejador);
        this.btEjecutar.addActionListener(this.manejador);
    }

    /*
     
     */
    private class Manejador implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == menuFile_Open) {
                JFileChooser manager = new JFileChooser(System.getProperty("user.dir"));
                manager.setDialogTitle("Seleccionar Archivo");
                manager.setFileSelectionMode(JFileChooser.FILES_ONLY);
                manager.setApproveButtonText("Cargar");
                int returnVal = manager.showSaveDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {//si selecciona guardar
                    File file = manager.getSelectedFile();
                    dirArchivo = file.getAbsolutePath();
                    //cargar el canvas!!

                    lector = new Lector();
                    lector.leer(dirArchivo);//Devolver la estructura de datos para graficar y/o ejecutar solucion
                    //canvasGrid.pruebaGrid();// luego quitar esto
                    canvasGrid.inicializar(lector.getNumCiudades(), lector.getTamRegion(), lector.getPosCiudades());
                    canvasGrid.setActivarDump(false, 0.0, 0.0);
                    jsp.setViewportView(canvasGrid);
                    btEjecutar.setEnabled(true);
                    


                }
            } else if (e.getSource() == btEjecutar) {
                llamarSolver();


            }

        }
    }

    public void llamarSolver() {


        try {

            int numCities = lector.getNumCiudades();
            int sizeGrid = lector.getTamRegion();
            int posCities[] = lector.getPosCiudades();

            Solver_basuro_ultimate solv = new Solver_basuro_ultimate();
            solv.initConstant(numCities, sizeGrid, posCities);
            solv.init();


            canvasGrid.setActivarDump(true, solv.getPosXb(), solv.getPosYb());

            DecimalFormat decimal = new DecimalFormat("0.000");


            this.value_coordenadaBasurero.setText("X=" + decimal.format(solv.getPosXb()) + " Y=" + decimal.format(solv.getPosYb()));

            this.value_coordenadaCiudadCercana.setText("X=" + decimal.format(solv.getPosXc()) + " Y=" + decimal.format(solv.getPosYc()));
            this.value_funcionObjetivo.setText("" + decimal.format(solv.getFuncionObjetivo()));



            canvasGrid.updateUI();
            jsp.updateUI();

            btEjecutar.setEnabled(false);
            lector = null;// limpiar lector para un proximo llamaddo 

        } catch (LpSolveException e) {
            e.printStackTrace();
        }



    }
}
