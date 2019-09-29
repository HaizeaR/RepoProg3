package Repaso0;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.*;

/** Ejercicio de hilos con ventanas. Programa esta clase para que se cree una ventana
 * que pida un dato de texto al usuario y un botón de confirmar para que se confirme.
 * Haz que al pulsar el botón de confirmación se llame al procesoConfirmar()
 * que simula un proceso de almacenamiento externo que tarda unos segundos.
 * Observa que hasta que ocurre esta confirmación la ventana no responde.
 * 1. Arréglalo para que la ventana no se quede "frita" hasta que se acabe de confirmar.
 * 2. Haz que el botón de "confirmar" no se pueda pulsar dos veces mientras el proceso
 * de confirmación se esté realizando
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class VentanaConfirmacionLenta extends JFrame{

	private JTextField tftext; 
	private  JButton bConfirmar; 

	private static Random r = new Random();
	// Este método simula un proceso que tarda un tiempo en hacerse (entre 5 y 10 segundos)
	private static void procesoConfirmar() {
		try {
			Thread.sleep( 5000 + 1000*r.nextInt(6) );
		} catch (InterruptedException e) {}
	}

	public VentanaConfirmacionLenta() {

		setTitle("VENTANA"); 
		setSize(600, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocation(100, 100);

		tftext = new JTextField(20); 
		bConfirmar = new JButton("Confirmar"); 


		add(tftext, BorderLayout.NORTH);
		add(bConfirmar, BorderLayout.SOUTH);


		bConfirmar.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// solo puedo hacer click una vez 
				bConfirmar.setEnabled(false);
				Thread hilo = new Thread(new Runnable() {

					@Override
					public void run() {
						// cada vez que le doy a aceptar se crea un nuevo hilo
						// necesitamos hacer que solo nos deje hacer click una vez 
						verHilos("Dentro del hilo");
						System.out.println("Empezar");
						procesoConfirmar();
						System.out.println("Acabo");
						// Cuando termine el hilo se activa de nuevo el botón
						bConfirmar.setEnabled(true);
					
					}
				}); 
				
				hilo.setDaemon(true);
				// no saldrá en mensaje de acabado porque lo estamos cortando 
				hilo.start();
				//bConfirmar.setEnable(true); aqui no porque inmediatamente lo activa de nuevo
				
				// bConfirmar = null; 
				// para poder hacer esto 
				// 1- que la variable sea final 
				// 2- crear un atributo de la variable 
			}

		});
	}
	
	// visualiza los hilos de este programa y los saca por consola 
	public static void verHilos(String mensaje){
		
		
		// me saca todas las claves 
		Set<Thread> conjuntoHilos = Thread.getAllStackTraces().keySet();
		System.out.println(mensaje);
		for(Thread t: conjuntoHilos) {
			System.out.println("  " + t.getName() + "  " + t.isDaemon());
		}
		
		
	}
	
	
	

	public static void main(String[] args) {

		VentanaConfirmacionLenta v1 = new VentanaConfirmacionLenta(); 
		verHilos("Antes de ser visible"); 
		v1.setVisible(true);
		verHilos("Despues de ser visible");

	}





}
