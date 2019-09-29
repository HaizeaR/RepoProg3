package Repaso0;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;

/** Ejercicio de hilos  con ventanas. Esta clase carga el texto del Quijote en un área de texto,
 * y permite navegar por el área con la scrollbar y con botones de página arriba y página abajo.
 * 1. Modificarlo para que al pulsar los botones el scroll se haga con una animación 
 * a lo largo de un segundo, en lugar de en forma inmediata.
 * 2. Prueba a pulsar muy rápido varias páginas abajo. ¿Cómo lo arreglarías para que el scroll
 * en ese caso funcione bien y vaya bajando una página tras otra pero las baje *completas*?
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class VentanaQuijoteCLASE extends JFrame {

	private JTextArea taTexto;
	private JScrollPane spTexto;
	
	public VentanaQuijoteCLASE() {
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setTitle( "Don Quijote de la Mancha" );
		setSize( 800, 600 );
		setLocationRelativeTo( null );  // Pone la ventana relativa a la pantalla
		taTexto = new JTextArea();
		spTexto = new JScrollPane( taTexto );
		add( spTexto, BorderLayout.CENTER );
		JPanel pBotonera = new JPanel();
		JButton bPagArriba = new JButton( "^" );
		JButton bPagAbajo = new JButton( "v" );
		pBotonera.add( bPagArriba );
		pBotonera.add( bPagAbajo );
		add( pBotonera, BorderLayout.SOUTH );
		bPagArriba.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				muevePagina( -(spTexto.getHeight()-20) );
			}
		});
		bPagAbajo.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				muevePagina( (spTexto.getHeight()-20) );
			}
		});
	}
	
	
	
	// no hay solo una manera de hacerlo 
	// puede ser nombres y yo llamo así a mis hilos
	// pero tambien puede ser de objetos y meto directamente los hilos
	
	private ArrayList<Thread> hilosActivos = new ArrayList<>(); 
	
	private void muevePagina( int pixelsVertical ) {
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				// TODO Cambiar este comportamiento de acuerdo a los comentarios de la cabecera de clase
				JScrollBar bVertical = spTexto.getVerticalScrollBar();
				System.out.println( "Moviendo texto de " + bVertical.getValue() + " a " + (bVertical.getValue()+pixelsVertical) );

				//bVertical.setValue( bVertical.getValue() + pixelsVertical );

				// como empiezo en 0 solo <
				// si empiezo en 1 sería <= 

				if (pixelsVertical>0) {
					for(int i=0; i < pixelsVertical; i++) {

						bVertical.setValue( bVertical.getValue() + 1 );
						try {Thread.sleep(10);} catch  (InterruptedException e) {

						}
					}

				}else {
					for(int i=0; i < -pixelsVertical; i++) {
						bVertical.setValue( bVertical.getValue() - 1 );
						try {Thread.sleep(10);} catch  (InterruptedException e) {

						}
					}
				}
			}

		});
		t.start();

	}
	
	private void cargaQuijote() {
		try {
			Scanner scanner = new Scanner( VentanaQuijoteCLASE.class.getResourceAsStream( "DonQuijote.txt" ), "UTF-8" );
			while (scanner.hasNextLine()) {
				String linea = scanner.nextLine();
				taTexto.append( linea + "\n" );
			}
			scanner.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog( this, "No se ha podido cargar el texto" );
		}
	}

	public static void main(String[] args) {
		VentanaQuijoteCLASE v = new VentanaQuijoteCLASE();
		v.setVisible( true );
		v.cargaQuijote();
	}

}