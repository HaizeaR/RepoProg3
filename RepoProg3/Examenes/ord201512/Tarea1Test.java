package ord201512;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import ord201512.captura.CapturaPantalla;

public class Tarea1Test {

	@Test
	public void test() {
		// crea e inicia el servidor
		ServidorCaptura sc1 = new ServidorCaptura(); 
		sc1.inicio();
		// no hace falta visualizarlo 
		// en caso de querer 
		//sc1.activaVentana();
		
		// espera 500 mili
		
		try { Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		
		// crea dos clientes de captura
		
		ClienteCaptura cc1 = new ClienteCaptura(); 
		
		Thread t = new Thread() {
		public void run() {
			// dentro de hilo
			try {
				cc1.lanzaConexion("127.0.0.1", cc1.toString(), 0, 100, 100, 300, 300);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		};
		t.start();
		
		ClienteCaptura cc2 = new ClienteCaptura(); 
		
		Thread t1 = new Thread() {
			public void run() {
				// dentro de hilo
				try {
					cc2.lanzaConexion("127.0.0.1", cc2.toString(), 0, 100, 100, 300, 300);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			};
			t1.start();
			
			
		// espera 3 segundos	
		try {Thread.sleep(3000);} catch (InterruptedException e) {e.printStackTrace();}
	
		cc1.fin();
		cc2.fin();
		sc1.fin();
		
		


		

		assertTrue(cc1.getCapturas()> 10);
		assertTrue(cc2.getCapturas()> 10);
		
		
	}

}
