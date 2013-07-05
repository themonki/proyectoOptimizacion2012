/**
 * Button.java
 * 
 * Clase que representa un boton con caracteristicas especiales es 
 * decir ya tiene un estilo definido y ciertos efectos que lo hacen
 * mas amigable con una interfaz.
 *
 * JAVA version "1.6.0"
 * 
 * 
 * Autor:   Luis Felipe Vargas
 * Version:   4.0
 */
package Utilidades;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.JButton;

/**
 * Esta clase es usada para todos los botones en las GUI
 * 
 * @author Luis Felipe Vargas
 * 
 */
public class Button extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     *
     */
	private Color color1 = new Color(230, 0, 0);
	/**
     * 
     * 
     */
	private Color color2 = new Color(100, 100, 100);
	/**
     * 
     */
	private Color color3 = new Color(150, 150, 150);

	/**
	 * Contructor de la clase Button
	 * 
	 * @param c
	 *            - String con el nombre para el Boton
	 */
	public Button(String c) {
		Font fontTitulo = new Font("dejavu sans", Font.BOLD, 13);
		setText(c);
		setOpaque(false);
		setContentAreaFilled(false);
		setForeground(Color.white);
		setFont(fontTitulo);
		setFocusPainted(false);
		setBorderPainted(false);
	}

	/*
	 * Metodo sobreescrito de la clase JButton para cambiar el estilo del boton
	 */
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		Color c1, c2, c3;
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		ButtonModel m = getModel();

		Paint oldPaint = g2.getPaint();
		

		if (m.isArmed()) {
			c2 = color1.darker();
			c1 = color2.darker();
			c3 = color3;
		} else {
			c1 = color1.darker();
			c2 = color2.darker();
			c3 = color3.brighter();
		}
		if (!m.isEnabled()) {
			c2 = color1.brighter();
			c1 = color2.brighter();

		}
		if (m.isRollover()) {

			c2 = color1.brighter();
			c1 = color2.brighter();
			c3 = color3.brighter();
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0,
				getWidth(), getHeight() - 1, 20, 20);
		g2.clip(r2d);

		g2.setPaint(new GradientPaint(0.0f, 0.0f, c1, 0.0f, getHeight(), c2));

		g2.fillRect(0, 0, getWidth(), getHeight());

		g2.setStroke(new BasicStroke(4f));
		g2.setPaint(new GradientPaint(0.0f, 0.0f, c3, 0.0f, getHeight(), c3));
		g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 18, 18);

		g2.setPaint(oldPaint);
		super.paintComponent(g);
	}

	/**
	 * @return el color1
	 */
	public Color getColor1() {
		return color1;
	}

	/**
	 * @param color1
	 *            - Color a modificar
	 */
	public void setColor1(Color color1) {
		this.color1 = color1;
	}

	/**
	 * @return color2
	 */
	public Color getColor2() {
		return color2;
	}

	/**
	 * @param color2
	 *            - Color a modificar
	 */
	public void setColor2(Color color2) {
		this.color2 = color2;
	}

	/**
	 * @return color3
	 */
	public Color getColor3() {
		return color3;
	}

	/**
	 * @param color3
	 *            - Color a modificar
	 */
	public void setColor3(Color color3) {
		this.color3 = color3;
	}

}