package T1;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.Test;

import com.sun.deploy.uitoolkit.impl.fx.Utils;

public class TestUtilsString {
	// los ejecuta en el orden before test after 
	
	@Before
	public void setUp() throws Exception{
		
	}

	@After
	public void tearDown() throws Exception{
		
	}
	
	
	
	@Test
	public void testQuitarTabsYSaltosLinea() {
		String prueba = "Hola\nEsto es un string con tres líneas\ny\tvarios\ttabuladores.";
		String prueba2 = "Hola#Esto es un string con tres líneas#y|varios|tabuladores.";
		
		
		
		// FORMA NORMAL DE HACERLO
		
		
		// lo que espero y lo que quiero comprobar 
		assertEquals(prueba2, prueba);
		
		
		// OTRA FORMA
	
//		if (prueba2.equals(UtilsString.quitarTabsYSaltosLinea(prueba))) {
//			//System.out.println( "OK" );
//			assertTrue(true); 
//			
//		} else {
//			//System.out.println( "FAIL" );
//			fail();
//		}
		
	
		
		
	
	}
	
	
	
	
	

}
