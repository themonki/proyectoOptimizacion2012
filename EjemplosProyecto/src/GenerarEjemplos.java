import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerarEjemplos {

	int ciudades;
	int grilla;
	int[] posiciones;
	Random c;
	Random p;

	File ejemplo;

	int cities[] = { 5, 10, 15, 20 };
	int grids[] = { 5, 10, 15, 20 };

	public GenerarEjemplos() throws IOException {
		c = new Random(456789);
		p = new Random(125609);

		for (int i = 0; i < grids.length; i++) {
			grilla = grids[i];
			for (int e = 0; e < 5; e++) {
				ciudades = (int) (Math.floor(c.nextDouble() * (grilla - 1)) + 2);
				posiciones = new int[ciudades * 2];
				int aux = 0;
				for (int j = 0; j < ciudades*2; j++) {
					posiciones[aux] = (int) (Math.floor(p.nextDouble()* (grilla - 1)));
					aux++;
				}
				
				String name= "ejemplo_g"+grilla+/*"_c"+ciudades+*/"-"+e+".txt";
			    BufferedWriter bw = new BufferedWriter(new FileWriter(name));
			    bw.write(""+grilla + "\n");
			    bw.write(""+ciudades);
			    aux=0;
			    
			    for(int idc=1; idc<=ciudades; idc++){
			    	bw.write("\n"+idc + " " + posiciones[aux++] + " " + posiciones[aux]);
			    	aux++;
			    }
			    
			    bw.close();
			}
		}
	}

	public static void main(String[] args) {
		try {
			GenerarEjemplos g = new GenerarEjemplos();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
