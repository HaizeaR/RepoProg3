package T1;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestUtilsString {
	// los ejecuta en el orden before test after 
	
	
	// En este caso Before y After no son necesarios 
//	@Before
//	public void setUp() throws Exception{
//		
//	}
//
//	@After
//	public void tearDown() throws Exception{
//		
//	}

	
	@Test
	public void testQuitarTabsYSaltosLinea() {
		
		String prueba = "Hola\nEsto es un string con tres líneas\ny\tvarios\ttabuladores.";
		String prueba2 = "Hola#Esto es un string con tres líneas#y|varios|tabuladores.";

		// lo que espero y lo que quiero comprobar 
		assertEquals(prueba2, UtilsString.quitarTabsYSaltosLinea(prueba));
		
	}
	
		
	// Más pruebas variadas de quitarTabsYSaltosLinea
	@Test
	public void testQuitarTabsYSaltosLinea2() {
		assertEquals( "", UtilsString.quitarTabsYSaltosLinea("") ); // String vacío
		assertEquals( "|", UtilsString.quitarTabsYSaltosLinea("\t") ); // Strings solo con \t y \n
		assertEquals( "#", UtilsString.quitarTabsYSaltosLinea("\n") );
		assertEquals( "#|||#", UtilsString.quitarTabsYSaltosLinea("\n\t\t\t\n") );
		assertEquals( "sin nada", UtilsString.quitarTabsYSaltosLinea("sin nada") ); // String sin \t o \n
	}
	
	
	// Prueba de null de quitarTabsYSaltosLinea
	// Comprobar lo que pasa cuando el usuario mete null a este método
	
	@Test
	public void testQuitarTabsYSaltosLineaNull() {
		assertNull( UtilsString.quitarTabsYSaltosLinea(null) );
	}

	// Pruebas básicas de wrapString
	@Test
	public void testWrapString() {
		assertEquals( "And...", UtilsString.wrapString( "Andoni", 3) );
		assertEquals( "Andoni", UtilsString.wrapString( "Andoni", 6) );
		assertEquals( "Andoni", UtilsString.wrapString( "Andoni", 8) );
		assertEquals( "", UtilsString.wrapString( "", 8) );
		assertEquals( "", UtilsString.wrapString( "", 0) );
		assertEquals( "...", UtilsString.wrapString( "Andoni", 0) );
	}
	
	// Pruebas de wrapString con valores extremos
	@Test
	public void testWrapString2() {
		assertEquals( null, UtilsString.wrapString( null, 3) );
		assertEquals( null, UtilsString.wrapString( null, -1) );
	}
		
	// Pruebas de excepción de wrapString (método 1)
	@Test
	public void testWrapStringExc() {
		try {
			UtilsString.wrapString( "Andoni", -5 );
			fail();
		} catch (IndexOutOfBoundsException e) {
			// Ok
		}
	}

	// Pruebas de excepción de wrapString (método 2)
	// Otra manera de probar excepción para wrapString (alternativa - hace lo mismo que la anterior, de otra manera)
	@Test(expected=IndexOutOfBoundsException.class)
	public void testWrapStringExc2() {
		UtilsString.wrapString( "Andoni", -5 );
	}
	
	
	
	
	
	

}
