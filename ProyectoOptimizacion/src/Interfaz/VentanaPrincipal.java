
package Interfaz;

import java.awt.HeadlessException;
import javax.swing.JFrame;

/**
 *
 * @author Edgar Andrés Moncada
 */
public class VentanaPrincipal extends JFrame {

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
    
    
    
}
