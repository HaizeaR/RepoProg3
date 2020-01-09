package ext201902.procesaTests.datos;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import ext201902.procesaTests.iu.VentanaGeneral;

/** Clase para datos globales únicos y accesibles en la gestión de tests
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Datos {
	
	public static VentanaGeneral ventana;                     // Ventana general de aplicación
	
	public static ArrayList<TestPC> listaPres;                // Lista de tests pre cargados
	public static ArrayList<TestPC> listaPosts;               // Lista de tests post cargados
	public static Tabla tablaPCPre;                           // Tabla de datos de lista de tests pre
	public static Tabla tablaPCPost;                          // Tabla de datos de lista de tests pre

	public static HashMap<String,TestPC> mapaPrePost;         // Mapa de todos los tests pre y post cargados
	
	public static HashMap<Integer,EnlacePrePost> mapaEnlaces; // Mapa de enlaces pre-post cargados
	public static ArrayList<EnlacePrePost> listaEnlaces;      // Lista de enlaces pre-post cargados
	public static Tabla tablaEnlaces;                         // Tabla de datos de lista de tests pre

	public static Connection conn;                                   // Conexión a Base de Datos
	public static Statement stat;                                    // Sentencia abierta de base de datos
	
}
