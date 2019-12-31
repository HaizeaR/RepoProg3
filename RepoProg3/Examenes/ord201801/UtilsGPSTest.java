package ord201801;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Test;

import ord201801.item.Arbol;

public class UtilsGPSTest {

	@Test
	public void gpsDentroDePoligonoTest() {
		// TAREA 1
		
			int size = GrupoZonas.arbolesErandio.size(); 
			
			for ( int i = 0; i < size; i++) {
				Arbol a = GrupoZonas.arbolesErandio.get(i); 
				a.getPunto(); 
				
				
				
				Zona z = GrupoZonas.jardinesErandio.getZona("'"+ i +"'");
				
				
				
				z.getPuntosGPS();
				
				for (ArrayList<PuntoGPS> p : z.getPuntosGPS()) {
					 PuntoGPS punto = p.get(i);
					assertEquals(a.getPunto(), punto );
				}
				
					
				
				
		
				
			}
			
		
		
		
		
		assertEquals(GrupoZonas.arbolesErandio, GrupoZonas.jardinesErandio);
		
		
		
	}
	
	
	private boolean puntoEstaEnAlgunaZona(PuntoGPS punto) {
		
		// Las zonas tienes el método iterator incluido en ellas 
		
		Iterator<Zona> itZona = GrupoZonas.jardinesErandio.getIteradorZonas(); 
		while ( itZona.hasNext()) {
			Zona zona = itZona.next(); 
			for (ArrayList<PuntoGPS> subzona: zona.getPuntosGPS()  ) {
				if(UtilsGPS.gpsDentroDePoligono(punto, subzona)) {
					return true; 
				}		
			}
		}
		
		return false;
		
	}

}
