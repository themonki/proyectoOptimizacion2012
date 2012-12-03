
package Interfaz;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

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
    	CanvasGrid c = new CanvasGrid();
    	
    	add(c);
    }
    
    public void propiedadesJFrame(){
        this.setTitle("Proyecto");        
        this.setSize(800, 600);
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
