package ord201801;

import java.awt.Color;


/** Clase con m�todos est�ticos para implementar algunas de las tareas del examen
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Tareas {
	
	// TAREA 4 - Atributos necesarios para la tarea 4
	public static void tarea4() {
		ventana = EdicionZonasGPS.getVentana();  // Atributo de acceso a la ventana
		// TAREA 4 - C�digo de conexi�n y de actualizaci�n de base de datos
		// 1.- Conexi�n de base de datos (que se conecte solo la primera vez)
		// 2.- Actualizaci�n de �rboles de zona seleccionada
		ventana.lMensaje.setText( "Finalizado proceso de BD." );
	}
	
	public static void finAplicacion() {
		// TAREA 4 - Cierre al final de la aplicaci�n
	}
	
	
	// Atributos y m�todos de la tarea 5
	private static EdicionZonasGPS ventana;
	private static PuntoGPS[] centroide;
	public static void tarea5() {
		// 1.- Reajusta la ventana para que se vean las zonas completas y para que se vean los �rboles
		ventana = EdicionZonasGPS.getVentana();
		if (!ventana.cbArboles.isSelected()) {
			ventana.cbArboles.doClick();  // Activa la selecci�n de �rboles para que se vean
		}
		ventana.calcMoverACentroZonas();
		ventana.calculaMapa();
		// 2.- Plantea 3 centroides (en lugar de aleatorios -que es lo habitual- se ponen de forma arbitraria para que se pueda probar el algoritmo siempre con los mismos valores
		centroide = new PuntoGPS[] { new PuntoGPS( 43.319, -2.964 ), new PuntoGPS( 43.304, -2.979 ), new PuntoGPS( 43.298, -2.958 ) };
		// 3.- Llama al algoritmo propio de la tarea 5 para que esos centroides se vayan recalculando
		(new Thread() { public void run() { calculoCentroides( 0 ); ventana.lMensaje.setText( "Finalizado algoritmo de centroides." ); }}).start();
	}
	// TAREA 5 - Codificar este m�todo de forma recursiva completando los pasos sin codificar
	private static void calculoCentroides( int numPasoRecursivo ) {
		ventana.lMensaje.setText( "Inicio de paso " + numPasoRecursivo );
		Color[] color = { Color.RED, Color.BLUE, Color.GREEN };  // Array de colores asociados a cada centroide
		// 1.- Colorear los puntos dependiendo del centroide m�s cercano:
		//   centroide1 - Rojo, centroide2 - Azul, centroide3 - Verde
		// 2.- Redibujar la pantalla con los �rboles recoloreados
		ventana.calculaMapa();
		// 3.- Dibujar centroides actuales
		for (int i=0; i<3; i++) {
			ventana.dibujaCirculo( centroide[i].getLongitud(), centroide[i].getLatitud(), 8, color[i], EdicionZonasGPS.stroke4, true );
			ventana.dibujaCruz( centroide[i].getLongitud(), centroide[i].getLatitud(), 30, Color.BLACK, EdicionZonasGPS.stroke2m, true );
		}
		// 4.- Esperar un par de segundos
		try { Thread.sleep( 2000 ); } catch (Exception e) {}
		// 5.- Calcular medias de cada uno de los grupos (colores de �rboles)
		// 6.- Recalcular los centroides con las medias
		// 7.- Completar recursividad...
	}
}
