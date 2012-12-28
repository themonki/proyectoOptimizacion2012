/**
 * felipe
 */
package Interfaz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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
	 * Usado para determinar cual es la coordenada en Y mas hacia abajo en el grid y poder invertir el orden
	 */
	private int maxPointY;
	
	private boolean activarPrueba=false;
	
	
	/**
	 *	Permite gráficar el basurero en el método paintRegion si esta true, por defecto false 
	 */
	private boolean activarDump = false;
	
	/**
	 * 
	 */
	public CanvasGrid() {
		// TODO Auto-generated constructor stub
		this.sizeGrid=0;// se resta 1 siempre
		this.sizeRects=0;		
		this.initX=0;
		this.initY=0;
		this.maxPointY=0;
		this.sizeCircle=0;
		
		this.numColumns=this.numRows=sizeRects*this.sizeGrid;
		this.colorCity=Color.GREEN;
		this.colorDump=Color.RED;
		
		Dimension d =new Dimension((this.numColumns+this.sizeCircle*2 + this.initX*2),(this.numRows+this.sizeCircle*2)+this.initY*2);
		this.setPreferredSize(d);
		
	}
	
	public CanvasGrid(int sizeGrid,int sizeRects,int initX, int initY,int sizeCircle) {//falta agregar el tipo de dato inicial sin el basurero
		
		//valores para pintar la forma el Grid
		this.sizeRects=sizeRects;		
		this.initX=initX;
		this.initY=initY;
		this.sizeCircle=sizeCircle;
		
		//valores para el grid y pintar los elementos
		this.sizeGrid=sizeGrid;// se resta 1 siempre
		this.maxPointY=0; //
		
		
		this.numColumns=this.numRows=sizeRects*this.sizeGrid;
		this.colorCity=Color.GREEN;
		this.colorDump=Color.RED;
		
		Dimension d =new Dimension((this.numColumns+this.sizeCircle*2 + this.initX*2),(this.numRows+this.sizeCircle*2)+this.initY*2);
		this.setPreferredSize(d);
		
	}
	
	
	
	/**
	 * Método que limpia el canvas
	 * @param g
	 */
	public void clearGrid(Graphics g){
		g.clearRect(0, 0, this.numColumns+this.sizeCircle*2, this.numRows+this.sizeCircle*2);		
	}
	
	@Override
	public void paint(Graphics g){
		clearGrid(g);
		//SOLO PARA PRUEBAS
		if(this.activarPrueba){
			paintGrid(g);
			paintCity(g, 0, 3);
			paintCity(g, 1, 3);
			paintCity(g, 2, 3);
			paintCity(g, 8, 9);
			paintDump(g, 9, 9);
			paintDump(g, 3, 2.1234);
			paintDump(g, 3, 2.5);			
		}
		
		//LLAMAR AL METODO QUE VA A PINTAR LA REGION DADA LA ESTRUCTURA DE DATOS
		if(this.sizeGrid!=0)paintRegion(g,this.sizeGrid);
	}
	
	
	
	/**
	 * Función que pintara una región dada una estructura de datos, con las ciudades y (o sin) el basurero
	 * @param g
	 */
	public void paintRegion(Graphics g, int sizeGrid){//AGREGAR LA ESTRUCTURA DE DATOS 
		//this.activarPrueba=false; // DESCOMENTAR CUANDO SE TENGA LA ESTRUCTURA DE DATOS
		clearGrid(g);
		int size = sizeGrid;//cantidad de ciudades a mostrar		
		paintGrid(g);
		int posx = 1;
		int posy = 2;//
		for(int i=0;i<size;i++){					
			paintCity(g, posx, posy);
		}
		if(this.activarDump){// si se quiere mostrar el basurero
			paintDump(g, posx+1, posy+1);
		}
		
		Dimension d =new Dimension((this.numColumns+this.sizeCircle*2 + this.initX*2),(this.numRows+this.sizeCircle*2)+this.initY*2);
		this.setPreferredSize(d);
	}
	
	/**
	 * Pinta la cuadricula que representa la grilla de la región
	 * @param g donde se va a pintar la grilla
	 */
	public void paintGrid(Graphics g){
		Color tmp = g.getColor();
		//valores para indicar los números de la grid
		int finX = 0, finY = 0, calibrarPosXHorizontal = 25, calibrarPosYHorizontal=5, calibrarPosXVertical = 5, calibrarPosYVertical=10  ;
		
		for(int i = 0; i < this.numColumns; i+=this.sizeRects)
			for(int j = 0; j < this.numRows; j+=this.sizeRects){
				
				//dibujar coordenadas izquierda primero y luego superior
				g.drawString(""+ (this.sizeGrid -(int)(i/this.sizeRects)), this.initX-calibrarPosXHorizontal , i+this.initY+calibrarPosYHorizontal);
				g.drawString(""+ (int)(j/this.sizeRects), j+this.initY-calibrarPosXVertical, this.initY-calibrarPosYVertical);
				//*******************************************
				g.drawRect(i+this.initX, j+this.initY, this.sizeRects, this.sizeRects);
				this.maxPointY=j+this.initY+this.sizeRects;
				finX=i+this.sizeRects;
				finY=j+this.sizeRects;
			}
		//dibujar coordenadas finales, izquierda primero y luego superior
		g.drawString(""+ (this.sizeGrid -(int)(finX/this.sizeRects)), this.initX-calibrarPosXHorizontal , finX+this.initY+calibrarPosYHorizontal);
		g.drawString(""+ (int)(finY/this.sizeRects), finY+this.initY-calibrarPosXVertical, this.initY-calibrarPosYVertical);
		//**************************
		g.setColor(tmp);
	}
	
	
	/**
	 * Función que gráfica un circulo y lo centra en uno de los espacios de la grilla
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintElementPoint(Graphics g, double posX, double posY){
		int posXDec = (int) ((this.sizeRects)*getDecimal(posX));
		int posYDec = (int) ((this.sizeRects)*getDecimal(posY));
		int x = posXDec+this.initX+(int)posX*this.sizeRects-(int)(this.sizeCircle/2);
		//int y = posYDec+this.initY+(int)posY*this.sizeRects-(int)(this.sizeCircle/2);			
		int correctionY = this.maxPointY - posYDec - (int)posY *this.sizeRects - (int)(this.sizeCircle/2);		
		g.fillOval(x,correctionY, this.sizeCircle, this.sizeCircle);
	}
	
	/**
	 * Función que gráfica una ciudad en una punto de la grilla (no necesariamente la interseccion)
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintCity(Graphics g, double posX, double posY){
		Color tmp = g.getColor();
		g.setColor(this.colorCity);
		paintElementPoint(g, posX, posY);
		g.setColor(tmp);
	}
	
	/**
	 * Función que gráfica el basurero en un punto de la grilla (no necesariamente la interseccion)
	 * @param g El gráfico donde se va a pintar
	 * @param posX La posición en X donde se quiere centrar
	 * @param posY La posición en Y donde se quiere centrar
	 */
	public void paintDump(Graphics g, double posX, double posY){
		Color tmp = g.getColor();
		g.setColor(this.colorDump);
		paintElementPoint(g, posX, posY);
		g.setColor(tmp);
		
	}
	
	
	/** Devuelve la parte decimal de un double como un double
	 * No se puede mandar un valor decimal muy grande 
	 * @param d double
	 * @return retorna la parte decimal del double d
	 */
	public double getDecimal(double d){		
		int num = (int)d;
		BigDecimal bd = new BigDecimal(Double.toString(d));
		double resta = Double.parseDouble(bd.subtract(new BigDecimal(num)).toString());
				
		return resta;
		
		
	}
	
	
	//para prueba del canvas
	public void pruebaGrid(){
		this.activarPrueba=true;
		this.sizeGrid=40-1;// se resta 1 siempre
		this.sizeRects=50;
		this.numColumns=this.numRows=sizeRects*this.sizeGrid;
		this.initX=30;
		this.initY=30;
		this.maxPointY=0;
		this.sizeCircle=12;
		this.colorCity=Color.GREEN;
		this.colorDump=Color.RED;
		Dimension d =new Dimension((this.numColumns+this.sizeCircle*2 + this.initX*2),(this.numRows+this.sizeCircle*2)+this.initY*2);
		this.setPreferredSize(d);
		repaint();
		
	}
	public void desactivarPruebaGrid(){
		this.activarPrueba=false;
		this.sizeGrid=0;// se resta 1 siempre
		this.sizeRects=0;		
		this.initX=0;
		this.initY=0;
		this.maxPointY=0;
		this.sizeCircle=0;
		
		this.numColumns=this.numRows=sizeRects*this.sizeGrid;
		this.setPreferredSize(new Dimension(0,0));
	}

	/**
	 * @return the activarBasurero
	 */
	public boolean isActivarDump() {
		return activarDump;
	}

	/**
	 * @param activarBasurero the activarBasurero to set
	 */
	public void setActivarDump(boolean activarBasurero) {
		this.activarDump = activarBasurero;
	}

}
