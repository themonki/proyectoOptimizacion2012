
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

/**
 *
 * @author Edgar Andrés Moncada
 * @
 */
public class VentanaPrincipal extends JFrame {
    
    Manejador manejador;
    CanvasGrid canvasGrid;
    JLabel labelDump, labelDistancia;
    JMenuItem menuFile_Open;
    String dirArchivo;
    JScrollPane jsp;
    JButton btEjecutar;
    
    public VentanaPrincipal() throws HeadlessException {
        
        super();
        init();
        initBarMenu();
        agregarEventos();
        propiedadesJFrame();
        
    }
    
    public void init(){
    	
    	
    	//Instancias Locales
    	Container container = this.getContentPane();
    	container.setLayout(new BorderLayout());
    	JPanel panelDatos = new JPanel(new GridBagLayout());
    	
    	
    	
    	JLabel etiquetaBasurero = new JLabel("Coordenadas del Basurero:");
    	JLabel etiquetaCiudadCercana = new JLabel("Distancia a la ciudad mas cercana:");
    	
    	
    	GridBagConstraints gbc = new GridBagConstraints();
    	gbc.gridwidth=30;
    	gbc.gridx=0;
    	gbc.gridy=0;
    	
    	//Instanciando Atributos
    	this.canvasGrid = new CanvasGrid();
    	this.jsp = new JScrollPane();
    	this.labelDump = new JLabel("( 0 , 0 )");
    	this.labelDistancia = new JLabel(" 0 ");
    	this.btEjecutar = new JButton("Ejecutar");
    	
    	
    	//Modificadores
    	this.jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    	this.jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
    	
    	
    	//adicionando elementos
    	int movery=0;
    	panelDatos.add(this.btEjecutar,gbc);
    	gbc.ipady=20;
    	
    	movery++;
    	gbc.gridy=movery;
    	panelDatos.add(etiquetaBasurero,gbc);    	
    	movery++;
    	gbc.gridy=movery;
    	panelDatos.add(this.labelDump,gbc);
    	movery++;
    	gbc.gridy=movery;
    	panelDatos.add(etiquetaCiudadCercana, gbc);
    	movery++;
    	gbc.gridy=movery;
    	panelDatos.add(this.labelDistancia,gbc);

    	
    	jsp.setViewportView(canvasGrid);
    	jsp.setPreferredSize(new Dimension(550,550));
    	
    	container.add(panelDatos, BorderLayout.EAST);
    	container.add(jsp, BorderLayout.CENTER);
    	    	
    	
    	//this.pack();
    	
    }
    
    public void initBarMenu(){
    	JMenuBar jmb = new JMenuBar();
    	JMenu menuFile =new JMenu("File");
    	JMenu menuHelp =new JMenu("Help");
    	
    	menuFile_Open = new JMenuItem("Open");
    	JMenuItem menuHelp_About = new JMenuItem("About");
    	
    	menuFile_Open.setAccelerator(KeyStroke.getKeyStroke("control O"));
    	
    	menuFile.add(menuFile_Open);
    	menuHelp.add(menuHelp_About);
    	jmb.add(menuFile);
    	jmb.add(menuHelp);
    	this.setJMenuBar(jmb);
    	
    }
    
    public void propiedadesJFrame(){
        this.setTitle("Proyecto");        
        this.setSize(1000, 700);
        //this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }
    
    public void agregarEventos(){
        this.manejador = new Manejador();
        this.menuFile_Open.addActionListener(this.manejador);
        this.btEjecutar.addActionListener(this.manejador);
    }
    
    /*
     
    */
    private class Manejador implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(e.getSource()==menuFile_Open){
            	JFileChooser manager = new JFileChooser(System.getProperty("user.dir"));
				manager.setDialogTitle("Seleccionar Archivo");
				manager.setFileSelectionMode(JFileChooser.FILES_ONLY);
				manager.setApproveButtonText("Cargar");
				int returnVal = manager.showSaveDialog(new JFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {//si selecciona guardar
					File file = manager.getSelectedFile();
					dirArchivo=  file.getAbsolutePath();
					//cargar el canvas!!
					
					Lector lector = new Lector();
					lector.leer(dirArchivo);//Devolver la estructura de datos para graficar y/o ejecutar solucion
					canvasGrid.pruebaGrid();// luego quitar esto
                                        canvasGrid.inicializar(lector.getNumCiudades(), lector.getTamRegion(), lector.getPosCiudades());
					jsp.setViewportView(canvasGrid);
				}            
            }else if(e.getSource()==btEjecutar){
            	/*
            	canvasGrid.setActivarDump(true);
            	 
            	canvasGrid.setDump();
            	
            	
            	
            	*/
            	double coordenadaDumpX = 1, coordenadaDumpY=2, distancia=3;
            	labelDump.setText("( " +coordenadaDumpX + " , "+coordenadaDumpY +" )");
            	labelDistancia.setText(""+distancia);
            
            }
            
        }
    
    
    }
    
    
}
