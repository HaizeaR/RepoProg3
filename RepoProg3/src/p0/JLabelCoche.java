package p0;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;


import javax.swing.*;

public class JLabelCoche extends JLabel {
	private static final long serialVersionUID = 1L; 
	
	int ancho = 100 ; 
	int alto = 100; 
	String coche; // noombre del gráfico "coche.png"

	

	public JLabelCoche() {
		
		try {
			setIcon( new ImageIcon( JLabelCoche.class.getResource("coche.png").toURI().toURL()));
		} catch (Exception e) {
			System.out.println("ERROR");
			e.printStackTrace();
		}
		setBounds( 0, 0, alto, ancho );
		
	}
	
	
	private double giro = Math.PI/2;
	
	public void setGirar( double gradosGiro ) {

		giro = gradosGiro/180*Math.PI;
		giro = -giro;  
		giro = giro + Math.PI/2; 
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		Image img =  ((ImageIcon)getIcon()).getImage();
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null)); 
		Graphics2D g2 = (Graphics2D) g;
		setPreferredSize(size);
		setSize(140, 100);
		g2.rotate(giro, 50, 50);
		g2.drawImage( img, 0, 0, 100, 100, null );
		
	}
	
	


}
