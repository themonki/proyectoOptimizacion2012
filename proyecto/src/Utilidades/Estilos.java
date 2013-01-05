/**
 * Estilos.java
 * 
 * Clase que representa un estilos como tipos de letras y colores
 * que pueden ser usados por botones,etiquetas atributos  y campos 
 * de texto para darle una mejor  apariencia a la interfaz.
 *
 * JAVA version "1.6.0"
 * 
 * 
 * Autor:    Luis Felipe Vargas
 * Version:   4.0
 */

package Utilidades;

import java.awt.Color;
import java.awt.Font;

public class Estilos {

	/**
	 * Constructor por defecto, no inicializa nada
	 * 
	 */
	public Estilos() {
	}

	// --------------------------Fuentes---------------------------------------------
	/**
	 * Variable que contiene las fuentes para los titulos
	 */
	public static Font fontTitulo = new Font("Book Antiqua", Font.BOLD
			+ Font.ITALIC, 25);
	/**
	 * Variable que contiene las fuentes para las etiquetas
	 */
	public static Font fontLabels = new Font("Book Antiqua", Font.BOLD
			+ Font.ITALIC, 12);
	/**
	 * Variable que contiene las fuentes para los subtitulos
	 */
	public static Font fontSubtitulos = new Font("Book Antiqua", Font.BOLD, 15);
	/**
	 * Variable que contiene las fuentes para los subrayados
	 */
	public static Font fontSubrayados = new Font("Book Antiqua", Font.BOLD, 15);

	// -------------------------------Color letras----------------------------
	/**
	 * Variable que contiene el color para los titulo
	 */
	public static Color colorTitulo = new Color(72, 23, 23);
	/**
	 * Variable que contiene el color para los subtitulos
	 */
	public static Color colorSubtitulo = new Color(160, 51, 51);
	/**
	 * Variable que contiene el color para las etiquetas
	 */
	public static Color colorLabels = new Color(160, 51, 51);
	/**
	 * Variable que contiene el color para las etiquetas2
	 */
	public static Color colorLabels2 = Color.DARK_GRAY;

	// ------------------------------Color Border -----------------------------
	/**
	 * Variable que contiene el color para los border
	 */
	public static Color colorBorder = new Color(34, 0, 11);
	/**
	 * Variable que contiene el color para los lightBorder
	 */
	public static Color colorLightBorder = new Color(167, 153, 157);
	
	//------------------------------Color panel de menu -------------------------
	
	/**
	 * Variable que contiene el color para los panels de menu 
	 */
	public static Color colorFondoPanel = new Color(205,74,74);
}
