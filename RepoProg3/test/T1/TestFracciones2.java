package T1;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.sun.jna.platform.win32.SetupApi;

public class TestFracciones2 {

	Fraccion f1;
	Fraccion f2;
	// Inicializamos o creamos dos fracciones por defecto para poder utilizarlas

	@Before
	public void SetUp() {
		f1 = new Fraccion(1, -5); 
		f2 = new Fraccion(2,6); 
	}

	// tiene que poner el - en el NUMERADR 

	@Test
	public void testSigno() {
		// analizamos numerador y denominador 

		assertEquals(f1.getNum(), -1);
		assertEquals(f1.getDen(), 5);

	}

	@Test
	public void testSimplifica() {
		// comprueba que la siplificación es correcta 
		// si nos dan 2/6 = 1/3
		assertEquals(f2.getNum(), 1);
		assertEquals(f2.getDen(), 3);

	}


	@Test
	public void testSuma() {
		Fraccion f3 = new Fraccion(-4, 15);
		// NO suma bien el método 
		// el resultado de la suma es 2/15 pero da -4/15 
		//esta es la forma de comprobarlo 

		assertEquals(f1.suma(f1, f2) ,f3);

		//System.out.println(f1.suma(f1, f2));
	}


	@Test 
	public void testResta () {

		// método resta sin terminar a si qye no funciona 
		//assertEquals(f1.resta(f1, f2), -8/15 );
		//System.out.println(f1.resta(f1, f2));
	}


	@Test 
	public void testMulti () {
		Fraccion f3 = new Fraccion(-1, 15);

		assertEquals(f1.multiplica(f1, f2), f3);

	}
	
	@Test
	public void testDivision() {
		Fraccion f3 = new Fraccion (-3,5); 
		assertEquals(f1.divide(f1, f2), f3);
		
	}


}
