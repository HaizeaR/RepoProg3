package ext201902.procesaTests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ext201902.procesaTests.datos.Tabla;
import ext201902.procesaTests.datos.TestPC;
import ext201902.utils.ParecidoStrings;

public class TestReducirStringsSimilares {

	@Test
	public void test1() {
		
		ArrayList<String> lista = new ArrayList<String>(); 
		lista.add("lápiz"); 
		lista.add("lápiz"); 
		lista.add("lapiz"); 
		lista.add("lápiz"); 
		lista.add("lápis"); 
		lista.add("comodo"); 
		lista.add("cómodo");
		lista.add("cómodo");
		System.out.println(lista);
		
		
		
		Main.reducirStringsSimilares(lista);
		assertFalse( lista.contains( "LAPIZ" ) );
		assertFalse( lista.contains( "LAPIS" ) );
		assertFalse( lista.contains( "COMODO" ) );
	
		
		// T1 parte 1
	}
	
	@Test
	public void test2() {
		// T1 parte 2
		ArrayList<TestPC> listaTests = new ArrayList<>();
		try {
			Tabla.processCSV( Main.class.getResource( "testsPre.csv" ), listaTests, TestPC.class );
			Tabla.processCSV( Main.class.getResource( "testsPost.csv" ), listaTests, TestPC.class );
			
			
			ArrayList<String> listaNombres = new ArrayList<>();
			
			for (TestPC test : listaTests) {
				listaNombres.add( test.getNomCentro() );
			}
				
			
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
		// inicializa el array para luego leerlo
		
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
		
		for (String partido : new String[] { "PP", "PSOE", "PODEMOS", "CIUDADANOS" }) 
			
			assertTrue( diferentes.contains( partido ) );
	}		

}
