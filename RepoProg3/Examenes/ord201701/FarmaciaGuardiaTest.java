package ord201701;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/** Clase de test unitario de la clase FarmaciaGuardia
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class FarmaciaGuardiaTest {
	FarmaciaGuardia f; 
	FarmaciaGuardia f1;
	FarmaciaGuardia f2;
	FarmaciaGuardia f3;
	@Before
	public void init() {
		 f = new FarmaciaGuardia( "Bilbao", "00:00-22:00", "(Deusto) Avda. Universidades 24 | 944139000" );
		 f1 = new FarmaciaGuardia( "Bilbao", "00:01-22:00", "Test" );
		 f2 = new FarmaciaGuardia( "Bilbao", "23:59-22:00", "Test" );
	
		 // FRACCIÓN CON DATOS MAL 
			 f3 = new FarmaciaGuardia( "Bilbao", "00:00-22:00", "Deusto Avda. Universidades 24 944139000" );

		 
		 
	}
	
	

	@Test
	public void testConstructor3Strings() {
		// TAREA 1
				
	assertEquals(f.getDireccion(), "Avda. Universidades 24");	
	assertEquals(f.getLocalidad(), "Bilbao");		
	assertEquals(f.getTelefono(), "944139000");		
	assertEquals(f.getZona(), "Deusto");		
	
	
	long horaf = f.getHoraDesde(); 
	long horaf1 = f1.getHoraDesde(); 
	long horaf2 = f2.getHoraDesde(); 
	
	assertTrue(horaf < horaf1);
	assertTrue(horaf < horaf2);
	
	// CREAR UNA FRACCIÖN CONDATOS MAL
	assertEquals(f3.getTelefono(), ""); 
	assertEquals(f3.getZona(), "");
	
	
	
		
		
		// TODO
	}
	
	@Test
	public void testCapicua() {
		// TAREA 7
		// TODO
	}

}
