package int201811;


import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.junit.Test;

// T1

public class TablaTest {


	String[][]  tabla1 = {
			{"1","1","1","1"},
			{"2","2","2","2"}, 
			{"4","4","4","4"},
			{"6","6","6","6"}
			
	};
	
	
	
	
	
	
	// numero de filas y columnas es el mismo 
	@Test
	public void mismoTamaño() throws ConnectException, MalformedURLException, UnknownHostException, FileNotFoundException, IOException, URISyntaxException {
		Tabla t1 =  Tabla.processCSV(TablaTest.class.getResource("testTabla1.csv").toURI().toURL());
		assertEquals(tabla1.length, t1.size() );
		assertEquals(tabla1.length, t1.getWidth());
		
		
		
		
	}
	
	
	
	@Test
	public void mismoTamaño2() throws ConnectException, MalformedURLException, UnknownHostException, FileNotFoundException, IOException, URISyntaxException {
		Tabla t2 =  Tabla.processCSV(TablaTest.class.getResource("testTabla2.csv").toURI().toURL());
		
		assertEquals(tabla1.length, t2.size() );
		// expected:<4> but was:<6>
		
		
		assertEquals(tabla1.length, t2.getWidth());
	}
	
}	

