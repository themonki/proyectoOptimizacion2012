
package Interfaz;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

/**
 *
 * @author Edgar Andrés Moncada
 * @
 */
public class VentanaPrincipal extends JFrame {
    
    Manejador manejador;
    
    public VentanaPrincipal() throws HeadlessException {
        
        super();
        init();
        propiedadesJFrame();
        
    }
    
    public void init(){
    	Container con = this.getContentPane();
    	CanvasGrid c = new CanvasGrid();
    	con.add(c);
    }
    
    public void propiedadesJFrame(){
        this.setTitle("Proyecto");        
        this.setSize(1000, 600);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
    }
    
    public void agregarEventos(){
        manejador = new Manejador();
    }
    
    /*
     
    */
    private class Manejador implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(e.getSource()==""){
            
            }else if(e.getSource()=="."){
            }
            
        }
    
    
    }
    
    
}
