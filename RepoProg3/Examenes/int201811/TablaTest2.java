package int201811;

import static org.junit.Assert.*;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

// ESTA SIN TERMINAR PERO NO DA ERROR DE MOMENTO 

public class TablaTest2 {

	private String[][] datosTest1 = {
			{ "1", "1", "1", "1" },
			{ "2", "2", "2", "2" },
			{ "4", "4", "4", "4" },
			{ "6", "6", "6", "6" },
	};
	
	private Tabla tabla1; 
	private Tabla tabla2; 
	
	@Before
	public void SetUp() {
		tabla1 = new Tabla(); 
		tabla2 = new Tabla();
		
		
	}
	
	
	@Test
	public void test() throws ConnectException, MalformedURLException, UnknownHostException, FileNotFoundException, IOException, URISyntaxException {
		tabla1 = Tabla.processCSV(TablaTest.class.getResource( "testTabla1.csv" ).toURI().toURL() );
		// No hace falta pero si sacamos a consola vemos como es igual
		//System.out.println(tabla1);
		
		tabla2 = Tabla.processCSV(TablaTest.class.getResource( "testTabla2.csv" ).toURI().toURL());
		
		
		assertEquals(tabla1.size(), datosTest1.length);
		assertEquals(tabla1.getWidth(), datosTest1[0].length);
	
	
	}

}
