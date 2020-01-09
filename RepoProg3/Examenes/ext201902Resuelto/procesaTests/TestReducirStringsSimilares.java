package ext201902Resuelto.procesaTests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ext201902Resuelto.procesaTests.datos.Tabla;
import ext201902Resuelto.procesaTests.datos.TestPC;
import ext201902Resuelto.utils.ParecidoStrings;

public class TestReducirStringsSimilares {

	@Test
	public void test1() {
		// T1 parte 1
		ArrayList<String> listaNombres = new ArrayList<>(
				Arrays.asList( new String[] { "lápiz", "lápiz", "lápiz", "lapiz", "lapis", "cómodo", "comodo", "cómodo" } )
		);
		Main.reducirStringsSimilares( listaNombres );
		assertFalse( listaNombres.contains( "LAPIZ" ) );
		assertFalse( listaNombres.contains( "LAPIS" ) );
		assertFalse( listaNombres.contains( "COMODO" ) );
		System.out.println( listaNombres );
	}
	
	@Test
	public void test2() {
		// T1 parte 2
		ArrayList<TestPC> listaTests = new ArrayList<>();
		try {
			Tabla.processCSV( Main.class.getResource( "testsPre.csv" ), listaTests, TestPC.class );
			Tabla.processCSV( Main.class.getResource( "testsPost.csv" ), listaTests, TestPC.class );
			ArrayList<String> listaNombres = new ArrayList<>();
			for (TestPC test : listaTests) listaNombres.add( test.getNomCentro() );
			Main.reducirStringsSimilares( listaNombres );  // Recodificar los centros
			for (int i=0; i<listaNombres.size(); i++) {  // Comparar los originales con los codificados
				String nombreOriginal = listaTests.get(i).getNomCentro();
				String nomFinal = listaNombres.get(i);
				double sim = ParecidoStrings.similitud( nombreOriginal, nomFinal );
				// System.out.println( nombreOriginal + "\t" + nomFinal + "\t" + sim );
				assertTrue( sim > 0.3 );
			}
		} catch (Exception e) {
			fail( "No debería haber habido excepción" );
		}
	}
	
	@Test
	public void test3() {
		// T1 parte 3
		ArrayList<String> listaUnis = new ArrayList<>();
		Scanner sc = new Scanner( TestReducirStringsSimilares.class.getResourceAsStream("pruebaPartidos.txt"), "UTF-8" );
		while (sc.hasNextLine()) {
			listaUnis.add( sc.nextLine() );
		}
		sc.close();
		Main.reducirStringsSimilares( listaUnis );
		HashSet<String> diferentes = new HashSet<>();
		for (String uni : listaUnis ) diferentes.add( uni );
		assertEquals( 4, diferentes.size() );
		for (String partido : new String[] { "PP", "PSOE", "PODEMOS", "CIUDADANOS" }) assertTrue( diferentes.contains( partido ) );
	}	

}
