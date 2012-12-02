package Lectura;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Edgar Andrés Moncada
 */
public class Lector {

    FileReader fr;
    BufferedReader bf;

    public Lector() {
        
    }

    public void leer(String archivo) {
        try {
            fr = new FileReader(archivo);
            bf = new BufferedReader(fr);

            int tamRegion = Integer.parseInt(bf.readLine());
            int numCiudades = Integer.parseInt(bf.readLine());
            
            //INFORMATIVO
            println(tamRegion);
            println(numCiudades);       
            /**/
            
            for (int i = 0; i < numCiudades; i++) {
                String line = bf.readLine();
                String valores[] = line.split(" ");
                
                int idCiudad = Integer.parseInt(valores[0]);
                int posx = Integer.parseInt(valores[1]);
                int posy = Integer.parseInt(valores[2]);
                
                //INFORMATIVO
                for (int j = 0; j < valores.length; j++) {
                    print(valores[j] + " ");
                }
                println("");
                /**/
            }
        
        
        } catch (FileNotFoundException ex) {
            System.out.println("no cargo");
        } catch (IOException ex) {
            Logger.getLogger(Lector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //comentar si no se quiere imprimir
    private void print(Object arg){
        //System.out.print(arg);
    }
    private void println(Object arg){
        print(arg + "\n");
    }

    public static void main(String [] args) {
        Lector l = new Lector();
        l.leer("src/Lectura/inputexample");
    }
}
