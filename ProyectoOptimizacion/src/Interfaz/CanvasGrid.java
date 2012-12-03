/**
 * 
 */
package Interfaz;

import java.awt.Color;
import java.awt.Graphics;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.lang.Double;
import java.math.BigDecimal;

import javax.swing.JComponent;

/**
 * @author "Edgar Andrés Moncada"
 *
 */
public class CanvasGrid extends JComponent{

	/**
	 * el tamaño de la grilla (valor n de la entrada)
	 * 
	 */
	private int sizeGrid;
	
	/**
	 * el tamaño de cada celda de la grilla
	 * 
	 */
	private int sizeRects;
	
	
	/**
	 * El número de columnas que se deben pintar
	 */
	private int numColumns;
	
	/**
	 * El número de filas que se deben pintar
	 */
	private int numRows;
	
	/**
	 * La posición x donde empieza a dibujarse el grid; 
	 */
	private int initX;
	/**
	 * La posición y donde empieza a dibujarse el grid;
	 */
	private int initY;
	
	
	/**
	 * Tamaño del circulo que representa la ciudad o el basureo
	 * Debe de ser par para que quede bien centrado
	 */
	private int sizeCircle;
	
	/**
	 * Color de la ciudad
	 */
	private Color colorCity;
	
	
	/**
	 *	Color del basurero 
	 */
	private Color colorDump;
	
	
	
	/**
	 * 
	 */
	public CanvasGrid() {
		// TODO Auto-generated constructor stub
		this.sizeGrid=10-1;
		this.sizeRects=50;
		this.numColumns=this.numRows=sizeRects*sizeGrid;
		this.initX=10;
		this.initY=10;
		this.sizeCircle=10;
		this.colorCity=Color.GREEN;
		this.colorDump=Color.RED;
		
	}
	
	@Override
	public void paint(Graphics g){
		 
		paintGrid(g);
		paintCity(g, 0, 3);
		paintCity(g, 1, 3);
		paintCity(g, 2, 3);
		paintCity(g, 5, 3);
		paintDump(g, 2, 4);
		///paintElementPoint(g, 0, 2.5);
		
		//System.out.println((this.sizeRects)*getDecimal(2.5));
		
	}
	
	public void paintGrid(Graphics g){
		//Color tmp = g.getColor();
		
		for(int i = this.initX; i < this.numColumns; i+=this.sizeRects)
			for(int j = this.initY; j < this.numRows; j+=this.sizeRects){
				g.drawRect(i, j, this.sizeRects, this.sizeRects);
				//g.fillOval(i-(this.sizeCircle/2), j-(sizeCircle/2), this.sizeCircle, this.sizeCircle);
			}
		
		//g.setColor(tmp);
	}
	
	
	/**
	 * Función que gráfica un circulo y lo centra en uno de los espacios de la grilla
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintElementPoint(Graphics g, double posX, double posY){
		/*int posXDec = (int) ((this.sizeRects)*getDecimal(posX));
		int posYDec = (int) ((this.sizeRects)*getDecimal(posY));
		int x = this.initX+posX*this.sizeRects-(this.sizeCircle/2);
		int y = this.initY+posY*this.sizeRects-(sizeCircle/2);
		int correctionY = this.initY+this.numRows - y;	
		g.fillOval(x,correctionY, this.sizeCircle, this.sizeCircle);*/
	}
	
	
	/**
	 * Función que gráfica un circulo y lo centra en uno de las intersecciones de la grilla
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintElementPointIntersection(Graphics g, int posX, int posY){
		int x = this.initX+posX*this.sizeRects-(this.sizeCircle/2);
		int y = this.initY+posY*this.sizeRects-(sizeCircle/2);
		int correctionY = this.initY+this.numRows - y;		
		g.fillOval(x,correctionY, this.sizeCircle, this.sizeCircle);
	}
	
	/**
	 * Función que gráfica una ciudad en una intersección de la grilla
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintCity(Graphics g, int posX, int posY){
		Color tmp = g.getColor();
		g.setColor(this.colorCity);
		paintElementPointIntersection(g, posX, posY);
		g.setColor(tmp);
	}
	
	/**
	 * Función que gráfica el basurero en una intersección de la grilla
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintDump(Graphics g, int posX, int posY){
		Color tmp = g.getColor();
		g.setColor(this.colorDump);
		paintElementPointIntersection(g, posX, posY);
		g.setColor(tmp);
		
	}
	
	
	/** Devuelve la parte decimal de un double como un entero
	 * No se puede mandar un valor decimal muy grande 
	 * @param d double
	 * @return retorna la parte decimal del double d
	 */
	public double getDecimal(double d){
		/*String cade = Double.toString(d);
		int i = cade.indexOf('.');
		return Integer.parseInt(cade.substring(i+1, i+2));*/
		//Por problemas de presicion
		int num = (int)d;
		BigDecimal bd = new BigDecimal(Double.toString(d));
		double resta = Double.parseDouble(bd.subtract(new BigDecimal(num)).toString());
				
		return resta;
		
		
	}
	

}
