
package Interfaz;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

/**
 *
 * @author Edgar Andrés Moncada
 */
public class VentanaPrincipal extends JFrame {
    
    Manejador manejador;
    
    public VentanaPrincipal() throws HeadlessException {
        
        super();
        init();
        propiedadesJFrame();
        
    }
    
    public void init(){
    
    }
    
    public void propiedadesJFrame(){
        this.setTitle("Proyecto");        
        this.setSize(400, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        
    }
    
    public void agregarEventos(){
        
    }
    
    
    private class Manejador implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent ae) {
            
            if(ae.getSource()==""){
            
            }else if(ae.getSource()=="."){
            }
            
        }
    
    
    }
    
    
}
