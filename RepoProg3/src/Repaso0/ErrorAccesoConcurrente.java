package Repaso0;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

/** Programa un par de hilos según la especificación y comprueba que hay problemas.
 * Resuelve esos problemas con una estructura synchronized
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class ErrorAccesoConcurrente {

	private static long CONPAUSA = 50; // msgs de pausa en los hilos
	
	private static JTextArea taSalida = new JTextArea();
	// TODO
	// Probar con esta estructura y ver que hay problemas:
	
	
	// se protege contra la modificación concurrente 
	// pero es más lento que el arraylist 
	// bloquea lo que esta modificando y así se asegura no tener problemas 
	
	private static Vector<Long> listaNums = new Vector<>();
	// TODO Sustituirla por una estructura synchronized
	
	public static void main(String[] args) {
		// Ventana de salida
		JFrame f = new JFrame();
		f.setSize( 1000, 800 );
		// f.setLocation( 2000, 0 );
		taSalida.setEditable( false );
		f.add( new JScrollPane( taSalida ) );
		f.setVisible( true );
		println( "Test" ); // Mensaje de prueba
		// Empezamos con la lista [0]
		listaNums.add( 0L ); 
		
		// TODO Programa un hilo que solo va añadiendo números incrementales a la lista por el final
		// Haz que el hilo visualice en la ventana lo que va haciendo y espere un poquito en cada iteración:
		
	
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				long numero = 0; 
				while ( true) {
					numero++; 
					listaNums.add(numero);
					println("añadidos" + listaNums.toString()); 
					if (CONPAUSA>0) try {Thread.sleep(CONPAUSA);} catch (InterruptedException ex) {}
				}
					
			}
		}); 
		t.start();
		
		
		
		// TODO Programa otro hilo que solo va quitando números por el principio
		// Haz que el hilo visualice en la ventana lo que va haciendo y espere un poquito en cada iteración:
	

		Thread t2 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					if (!listaNums.isEmpty()) {
					long primero = listaNums.remove(0);
					 println( "Borrado: " + primero );
					}
					 if (CONPAUSA>0) try { Thread.sleep(CONPAUSA); } catch (InterruptedException ex) {}
				}
			}
		}); 
		t2.start(); 
		
		
		
		
		
		
		// A partir de ahora se tiene que ir viendo en pantalla una lista donde se añaden números por el final 
		// y se quitan por el principio... salvo que haya algún problema de concurrencia y uno de los dos 
		// hilos deje de hacer bien su trabajo
	}

	// Método auxiliar para sacar información en la ventana
	// Lo hacemos syncrhonized para que no haya interferencia entre los hilos a la hora de visualizar
	// probar que si se quita el synchronized hay problemas)
	private static synchronized void println( String mens ) {
		taSalida.append( mens + "\n" );
		taSalida.setSelectionStart( taSalida.getText().length() );
		taSalida.setSelectionEnd( taSalida.getText().length() );
		if (taSalida.getText().length()>100000) {  // Para que no se llene la textarea vamos quitando de vez en cuando
			taSalida.replaceRange( "", 0, 50000 );
		}
		// Aunque en este caso no va a haber problema, sería más correcto hacer esto para respetar a Swing (que no es Thread-safe):
		// try {
		// 	SwingUtilities.invokeAndWait( new Runnable() {
		// 		@Override
		// 		public void run() {
		// 			taSalida.append( mens + "\n" );
		// 			taSalida.setSelectionStart( taSalida.getText().length() );
		// 			taSalida.setSelectionEnd( taSalida.getText().length() );
		// 			if (taSalida.getText().length()>100000) {  // Para que no se llene la textarea vamos quitando de vez en cuando
		// 				taSalida.replaceRange( "", 0, 50000 );
		// 			}
		// 		}
		// 	});
		// } catch (Exception e) {
		// 	e.printStackTrace();
		// }
	}

}
