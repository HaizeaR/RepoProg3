package ord201701;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class MapaFarmaciasOrdenadas {
	// TAREA 6 - Definir atributo, codificar constructor y m�todo consultor de atributo
	// TODO
	// hash Map no tiene orden NO USAMOS HASH MAP 
	//HashMap<String, ArrayList<FarmaciaGuardia>> mapa = new HashMap<String, ArrayList<FarmaciaGuardia>>(); 
	
	private TreeMap<String, TreeSet<FarmaciaGuardia>> mapaO; 
	
	
	// CREA EL MAPA A PARTIR DEL MAPA  DE LA CLASE MAPA FARMACIAS 
	




	public MapaFarmaciasOrdenadas(TreeMap<String, TreeSet<FarmaciaGuardia>> mapaO) {
		
		this.mapaO = mapaO;
	}
	/** Crea un mapa de farmacias ordenadas por localidad, horario, zona y direcci�n partiendo de un mapa desordenado
	 * @param mapa	Mapa de farmacias por poblaci�n, desordenado
	 */
	public MapaFarmaciasOrdenadas( MapaFarmacias mapa ) {
		mapaO = new TreeMap<>();
		for (String loc : mapa.getMapaFarmacias().keySet()) {
			ArrayList<FarmaciaGuardia> l = mapa.getMapaFarmacias().get( loc );
			TreeSet<FarmaciaGuardia> tree = new TreeSet<FarmaciaGuardia>();
			for (FarmaciaGuardia f : l) {
				tree.add( f );
			}
			mapaO.put( loc, tree );
		}
	}
	
	public TreeMap<String, TreeSet<FarmaciaGuardia>> getMapaO() {
		return mapaO;
	}
	
}
