package ord201901.editorSprites;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAnimacion {

	
	VentanaEdicionSprites v1 ; 
	
	
	@Before
	
	public void setUp() {
		// crea ventana
		v1  = new VentanaEdicionSprites();
		
		// Añadir fichero gráfico 
		
		//v1.lSprites.add(comp)
		
		// visualiza ventana
	
		//v1.tfVelocidad.set(100);
		//v1.tfVelocidad.setCaret(100);

	}
	
	
	@After
	public void tearDown() {
		v1.dispose();
		
	}
	

	@Test
	public void test() {
		
		File f1 = new File( "src/examen/ord201901/editorSprites/img/Attack__000.png" );
		v1.getController().anyadirSpriteASecuencia( f1 );
		
		v1.setVisible(true); 
		// asigna valores
		v1.tfVelocidad.setText("100");
		v1.tfAngulo.setText("25");
		// simula un click en el botón
		v1.bSecuenciaAnim.doClick();
		
		boolean cambia = true; 
		boolean dentro = true; 
		
		while (cambia == true) {
			int x_inicial = v1.getController().lAnim.getX(); 
			int y_inicial = v1.getController().lAnim.getY(); 
			
			
			// son dos valores constantes 
			
			int x_w_panel = v1.pArena.getWidth(); 
			int x_h_panel = v1.pArena.getHeight(); 
					
			
			try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }

			int x_final = v1.getController().lAnim.getX(); 
			int y_final = v1.getController().lAnim.getY(); 
			
			
			long tiempo = System.currentTimeMillis();
			if (x_inicial - x_final == 0 && y_inicial - y_final == 0) {
				// this will mean that it hasnt move
				assertTrue(x_inicial == x_final); 
				assertTrue(y_inicial == y_final); 
				assertTrue( x_final >= v1.pArena.getWidth() || y_final >= v1.pArena.getHeight()); 

				
				cambia = false; 
				
			}
			
				
			//assertFalse(x_inicial == x_final); 
			
			//assertTrue(x_final = x_w_panel || y_final == x_h_panel);
			//assertTrue( y_final == x_h_panel); 
			
			assertTrue(System.currentTimeMillis() - tiempo < 10000 );
			
			
		}
		
	
		
			
		}
		
		
		
	}


