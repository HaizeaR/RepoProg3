package ext201702;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

/** Clase de test unitario de la clase FarmaciaGuardia
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class FarmaciaGuardiaTest {

	@Test
	public void testConstructor3Strings() {
		FarmaciaGuardia f = new FarmaciaGuardia( "Bilbao", "00:00-22:00", "(Deusto) Avda. Universidades 24 | 944139000" );
		FarmaciaGuardia f1 = new FarmaciaGuardia( "Bilbao", "00:01-22:00", "Test" );
		FarmaciaGuardia f2 = new FarmaciaGuardia( "Bilbao", "23:59-22:00", "Test" );
		assertTrue( f.getHoraDesde() < f1.getHoraDesde() );
		assertTrue( f.getHoraDesde() < f2.getHoraDesde() );
		assertEquals( f.getLocalidad(), "Bilbao" );
		assertEquals( f.getZona(), "Deusto" );
		assertEquals( f.getDireccion(), "Avda. Universidades 24" );
		assertEquals( f.getTelefono(), "944139000" );
		FarmaciaGuardia f3 = new FarmaciaGuardia( "Bilbao", "00:00-22:00", "Deusto Avda. Universidades 24 944139000" );
		assertEquals( f3.getZona(), "" );
		assertEquals( f3.getTelefono(), "" );
	}
	
	@Test
	public void testCapicua() {
		FarmaciaGuardia f1 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Deusto) Avda. Universidades 24 | 94 4139000" );
		FarmaciaGuardia f2 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Deusto) Avda. Universidades 24 | 94 413 14 49" );
		FarmaciaGuardia f3 = new FarmaciaGuardia( "Bilbao", "09:00-22:00", "(Deusto) Avda. Universidades 24 | 944 132 439" );
		assertTrue( f1.calcCapicua() > 5 );
		assertTrue( f2.calcCapicua() == 0 );
		assertTrue( f3.calcCapicua() == 2 );
	}

	@Test
	public void mapaFarmaciasOrdenado() {
		MapaFarmacias mf = new MapaFarmacias(); 
		
	
		ArrayList<FarmaciaGuardia> fgB = new ArrayList<FarmaciaGuardia>(); 
		ArrayList<FarmaciaGuardia> fgL = new ArrayList<FarmaciaGuardia>(); 
		
		fgB.add(new FarmaciaGuardia("Bilbao", "09:00-22:00", "(Abusu/La Peña-Zamakola)  Zamácola, 57  |  94 4166347")); 
		fgB.add(new FarmaciaGuardia("Bilbao", "09:00-22:00", "(Albia)  Buenos Aires, 11  |  94 4231483" )); 
		fgB.add(new FarmaciaGuardia("Bilbao", "09:00-22:00", "(Albia)  Colón De Larreátegui, 41  |  688988636")); 
		fgB.add(new FarmaciaGuardia( "Bilbao", "09:00-20:00", "(Albia)  Berástegui, 1 (Salida Metro Abando En Berástegui)  |  94 4236143" )); 
		fgL.add(new FarmaciaGuardia("Lekeitio", "09:00-09:00", "(Lekeitio)  Atea, Nº 14  |  946843023")); 

		mf.getMapaFarmacias().put("Bilbao", fgB); 
		mf.getMapaFarmacias().put("Lekeitio", fgL);
		
		
	
		MapaFarmaciasOrdenadas mfo = new MapaFarmaciasOrdenadas(mf);
		
		
		assertEquals(mfo.getMapaOrd().firstKey(), "Bilbao");
		assertEquals(mfo.getMapaOrd().lastKey(), "Lekeitio");
		
//		assertEquals(mfo.getMapaOrd()., actual);
//		assertEquals(mfo[0], actual);
		

		// falt meter las de Lekeitio y Bilbao 

		

		


		
		
		
		
		
	}
	
	
	
}
