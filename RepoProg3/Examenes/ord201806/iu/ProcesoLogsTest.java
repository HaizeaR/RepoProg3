package ord201806.iu;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class ProcesoLogsTest {
	VentanaProcesoLogs v1;
	
	
	
	@Test
	public void init() {
		v1 = new VentanaProcesoLogs(); 

		v1.setVisible(true);

		
		try {	Thread.sleep(1000);  } catch (InterruptedException e) {	e.printStackTrace();}

			v1.tfCarpeta.setText("data/examen/ord201806");
			v1.buscarEnCarpeta();

			try {	Thread.sleep(1000);  } catch (InterruptedException e) {	e.printStackTrace();
		}
			
		assertEquals(v1.lFicheros.size(), 3);
		
		
	}
	
	
	
//	@Test
//	public void testComprobarFicheros() {
//// Esta mal
//		assertEquals(v1.lFicheros.getSize(), 3); 
//		// tendriamos que usar size pero size no me funciona 
//	
//	}

	
	
	
}
