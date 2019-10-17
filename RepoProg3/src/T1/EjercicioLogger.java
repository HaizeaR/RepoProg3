package T1;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/** Ejercicio de logger: sacar a fichero XML el registro de lo que hace el programa, al menos estas cosas:
 * - Cuando se lanza la ventana
 * - Cuando se pulsa el botón
 * - Cuando se edita el textfield
 * - Cada carpeta que se visualiza el número de ficheros
 * - Observa que hay un posible error si la carpeta no existe. Atrapa esta excepción y añádela al log indicando un mensaje al usuario
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class EjercicioLogger {
	
	// TODO Añadir logger y registrar lo indicado en la cabecera (y más si se quiere)
	
	//static PrintStream log; 
	
	static Logger log = Logger.getLogger("prueba-logger");;


	private static JTextField tfEntrada = new JTextField( 60 );
	private static JLabel lMensaje = new JLabel( " " );
	
	
	
	public static void main(String[] args) {
		
		
		//log.setLevel(Level.ALL);

//		try {
//			h  = new FileHandler("prueba-logger.xml" ,true);
//		} catch (Exception e) {
//			log.log( Level.SEVERE, e.toString(), e );
//		
//		log.addHandler(h);
//		}
//		
		
		try {
		
			Handler h = new FileHandler("prueba-logger.xml" ,true); 
	
			log.setLevel( Level.FINEST );
			log.addHandler( h );  // Saca todos los errores a out
			h.setLevel( Level.FINEST );
		
			
		}catch (Exception e){ }
		
		
		
		// Ventana de ejemplo para el ejercicio
		final JFrame f = new JFrame( "Ventana rápida para ejercicio logger" );  // Ventana a visualizar
		f.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		f.setSize( 800, 150 );
		f.setLocationRelativeTo( null );
		JPanel pSuperior = new JPanel();
		JPanel pInferior = new JPanel();
		JButton bBuscar = new JButton( "Buscar" );
		pSuperior.add( new JLabel("Indica carpeta") ); 
		pSuperior.add( tfEntrada );
		pSuperior.add( bBuscar );
		pInferior.add( lMensaje );
		f.add( pSuperior, BorderLayout.CENTER );
		f.add( pInferior, BorderLayout.SOUTH );
		tfEntrada.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sacaFicheros();
				
				log.log(Level.FINE, "Editando textflied"); 
				
				
			}
		});
		bBuscar.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Saca un diálogo de búsqueda de fichero con JFileChooser
				JFileChooser f = new JFileChooser();
				f.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				int cod = f.showOpenDialog( f );
				if (cod==JFileChooser.APPROVE_OPTION) {
					File dir = f.getSelectedFile();
					tfEntrada.setText( dir.getAbsolutePath() );
					sacaFicheros();
				}
			}
		});
		f.setVisible( true );
		log.log(Level.FINE, "Ventana inciada"); 
		}
 
	
	// Saca el número de ficheros de la carpeta indicada
	private static  void sacaFicheros() {
		try {
		File f = new File( tfEntrada.getText() );
		File[] listDir = f.listFiles();
		lMensaje.setText( "Ficheros+directorios en la carpeta: " + listDir.length );
		
		log.log(Level.INFO , "Carpeta buscada" + f.getAbsolutePath()); 
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "Carpeta Erronea introduce otra");
			e.printStackTrace(); 
			log.log(Level.SEVERE, "Error en sacarfichero" , e );
			
			
		}
		
	}

}
