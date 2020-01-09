package ext201902Resuelto.procesaTests.datos;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

/** Clase de gestión de base de datos del examen 201811
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class BD {
	
	private static boolean LOGGING = true;

	/** Inicializa una BD SQLITE y devuelve una conexión con ella
	 * @param nombreBD	Nombre de fichero de la base de datos
	 * @return	Conexión con la base de datos indicada. Si hay algún error, se devuelve null
	 */
	public static Connection initBD( String nombreBD ) {
		try {
		    Class.forName("org.sqlite.JDBC");
		    Connection con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD );
			log( Level.INFO, "Conectada base de datos " + nombreBD, null );
		    return con;
		} catch (ClassNotFoundException | SQLException e) {
			log( Level.SEVERE, "Error en conexión de base de datos " + nombreBD, e );
			return null;
		}
	}
	
	/** Crea las tablas de la base de datos. Si ya existen, las deja tal cual. Devuelve un statement para trabajar con esa base de datos
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se crea correctamente, null si hay cualquier error
	 */
	public static Statement usarCrearTablasBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			try {
				statement.executeUpdate("create table enlacePrePost " +
					"(numEnlace integer" +               // Código de enlace (número único)
					", centro text" +                    // Nombre de centro
					", codTestPre text" +                // Código de test pre (null si no está definido)
					", codTestPost text" +               // Código de test post (null si no está definido)
					");");
			} catch (SQLException e) {} // Tabla ya existe. Nada que hacer
			return statement;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en creación de base de datos", e );
			return null;
		}
	}
	
	/** Reinicia en blanco las tablas de la base de datos. 
	 * UTILIZAR ESTE MËTODO CON PRECAUCIÓN. Borra todos los datos que hubiera ya en las tablas
	 * @param con	Conexión ya creada y abierta a la base de datos
	 * @return	sentencia de trabajo si se borra correctamente, null si hay cualquier error
	 */
	public static Statement reiniciarBD( Connection con ) {
		try {
			Statement statement = con.createStatement();
			statement.setQueryTimeout(30);  // poner timeout 30 msg
			statement.executeUpdate("drop table if exists enlacePrePost");
			log( Level.INFO, "Reiniciada base de datos", null );
			return usarCrearTablasBD( con );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en reinicio de base de datos", e );
			return null;
		}
	}
	
	/** Cierra la base de datos abierta
	 * @param con	Conexión abierta de la BD
	 * @param st	Sentencia abierta de la BD
	 */
	public static void cerrarBD( Connection con, Statement st ) {
		try {
			if (st!=null) st.close();
			if (con!=null) con.close();
			log( Level.INFO, "Cierre de base de datos", null );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en cierre de base de datos", e );
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	//                      Operaciones sobre tablas                   //
	/////////////////////////////////////////////////////////////////////
	
	/////////////////////////////////////////////////////////////////////
	//                  Operaciones de enlacePrePost                   //
	/////////////////////////////////////////////////////////////////////
	
//	Formato de tabla de BD: 
//	 "create table enlacePrePost " +
//			"(numEnlace integer" +               // CLAVE: Código de enlace (número único)
//			", centro text" +                    // Nombre de centro
//			", codTestPre text" +                // Código de test pre (null si no está definido)
//			", codTestPost text" +               // Código de test post (null si no está definido)
	
	/** Añade un cambio de edición en enlace a la tabla abierta de BD, usando la sentencia INSERT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param enlace	Enlace de test a añadir en la base de datos
	 * @return	true si la inserción es correcta, false en caso contrario
	 */
	public static boolean enlacePrePostInsert( Statement st, EnlacePrePost enlace ) {
		String sentSQL = "";
		try {
			sentSQL = "insert into enlacePrePost (numEnlace, centro, codTestPre, codTestPost) values(" +
					"" + enlace.getNumEnlace() + ", " +   
					"'" + secu(enlace.getCentro()) + "', " +
					(enlace.getTestPre()==null ? "NULL" : "'" + secu( enlace.getTestPre().getMarcaTemporal() ) + "'" ) + ", " +
					(enlace.getTestPost()==null ? "NULL" : "'" + secu( enlace.getTestPost().getMarcaTemporal() ) + "'" ) +
					");";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD añadida " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que añadir 1 - error si no
				log( Level.SEVERE, "Error en insert de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}

	/** Modifica un cambio de edición en enlace a la tabla abierta de BD, usando la sentencia UPDATE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param enlace	Enlace de test a modificar en la base de datos (debe existir su número de enlace)
	 * @return	true si la modificación es correcta, false en caso contrario
	 */
	public static boolean enlacePrePostUpdate( Statement st, EnlacePrePost enlace ) {
		String sentSQL = "";
		try {
			sentSQL = "update enlacePrePost set" +
					" centro='" + secu( enlace.getCentro() ) + "'," +
					" codTestPre=" + (enlace.getTestPre()==null ? "NULL" : "'" + secu( enlace.getTestPre().getMarcaTemporal() ) + "'" ) + "," +
					" codTestPost=" + (enlace.getTestPost()==null ? "NULL" : "'" + secu( enlace.getTestPost().getMarcaTemporal() ) + "'" ) +
					" where numEnlace=" + enlace.getNumEnlace() + ";";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD modificada " + val + " fila\t" + sentSQL, null );
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				log( Level.SEVERE, "Error en update de BD\t" + sentSQL, null );
				return false;  
			}
			return true;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			return false;
		}
	}

	/** Realiza una consulta a la tabla abierta de enlace de la BD, usando la sentencia SELECT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente)
	 * @param numEnlace	Número de enlace que se quiere buscar
	 * @param mapaTests	Mapa de tests del que se toman los enlaces a test PC
	 * @return	Enlace con ese número en la base de datos. Si no existe, null
	 */
	public static EnlacePrePost enlacePrePostSelect( Statement st, int numEnlace, HashMap<String,TestPC> mapaTests ) {
		String sentSQL = "";
		try {
			sentSQL = "select * from enlacePrePost where numEnlace=" + numEnlace;
			EnlacePrePost epp = null;
			ResultSet rs = st.executeQuery( sentSQL );
			if (rs.next()) {
				int numEnl = rs.getInt( "numEnlace" );
				String codC = rs.getString( "centro" );
				epp = new EnlacePrePost( numEnl, codC );
				String codPre = rs.getString( "codTestPre" );
				epp.setTestPre( mapaTests.get( codPre ) );
				String codPost = rs.getString( "codTestPost" );
				epp.setTestPost( mapaTests.get( codPost ) );
				epp.setGuardado( true ); // Se carga de bd luego está guardado
			}
			rs.close();
			log( Level.INFO, "BD\t" + sentSQL, null );
			return epp;
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return null;
		}
	}

	/** Realiza una consulta a la tabla abierta de enlace de la BD, usando la sentencia SELECT de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente)
	 * @param mapaTests	Mapa de tests del que se toman los enlaces a test PC.
	 * @return	Lista de enlaces de test en la base de datos (si un test no existe en el mapa se define null en el enlace correspondiente)
	 */
	public static ArrayList<EnlacePrePost> enlacePrePostSelect( Statement st, HashMap<String,TestPC> mapaTests ) {
		ArrayList<EnlacePrePost> ret = new ArrayList<>();
		String sentSQL = "";
		try {
			sentSQL = "select * from enlacePrePost order by centro,numEnlace;";
			ResultSet rs = st.executeQuery( sentSQL );
			while (rs.next()) {
				int numEnl = rs.getInt( "numEnlace" );
				String codC = rs.getString( "centro" );
				EnlacePrePost epp = new EnlacePrePost( numEnl, codC );
				String codPre = rs.getString( "codTestPre" );
				TestPC testPre = mapaTests.get( codPre );
				String codPost = rs.getString( "codTestPost" );
				TestPC testPost = mapaTests.get( codPost );
				epp.setTestPre( testPre );
				epp.setTestPost( testPost );
				epp.setGuardado( true ); // Se carga de bd luego está guardado
				ret.add( epp );
			}
			rs.close();
			log( Level.INFO, "BD\t" + sentSQL, null );
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			ret.clear();
		}
		return ret;
	}

	/** Borrar un enlace de test de la tabla abierta de BD, usando la sentencia DELETE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @param numEnlace	Enlace a borrar
	 * @return	true si el borrado es correcto, false en caso contrario
	 */
	public static boolean enlacePrePostDelete( Statement st, int numEnlace ) {
		String sentSQL = "";
		try {
			sentSQL = "delete from enlacePrePost where numEnlace=" + numEnlace + ";";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD borrada " + val + " fila\t" + sentSQL, null );
			return (val==1);
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return false;
		}
	}

	/** Borrar todos los enlaces de test de la tabla abierta de BD, usando la sentencia DELETE de SQL
	 * @param st	Sentencia ya abierta de Base de Datos (con la estructura de tabla correspondiente al centro)
	 * @return	true si se borran filas (más de ninguna), false en caso contrario
	 */
	public static boolean enlacePrePostDeleteAll( Statement st ) {
		String sentSQL = "";
		try {
			sentSQL = "delete from enlacePrePost;";
			int val = st.executeUpdate( sentSQL );
			log( Level.INFO, "BD borrada " + val + " fila\t" + sentSQL, null );
			return (val>0);
		} catch (SQLException e) {
			log( Level.SEVERE, "Error en BD\t" + sentSQL, e );
			e.printStackTrace();
			return false;
		}
	}

	

	/////////////////////////////////////////////////////////////////////
	//                      Métodos privados                           //
	/////////////////////////////////////////////////////////////////////

	// Devuelve el string "securizado" para volcarlo en SQL
	private static String secu( String string ) {
		return string.replaceAll( "'",  "''" );
	}
	

	/////////////////////////////////////////////////////////////////////
	//                      Logging                                    //
	/////////////////////////////////////////////////////////////////////
	
	private static Logger logger = null;
	
	// Método local para loggear
	private static void log( Level level, String msg, Throwable excepcion ) {
		if (!LOGGING) return;
		if (logger==null) {  // Logger por defecto local:
			logger = Logger.getLogger( BD.class.getName() );  // Nombre del logger - el de la clase
			logger.setLevel( Level.ALL );  // Loguea todos los niveles
		}
		if (excepcion==null)
			logger.log( level, msg );
		else
			logger.log( level, msg, excepcion );
	}
	
}
