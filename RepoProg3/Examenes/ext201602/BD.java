package ext201602;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.rowset.spi.SyncResolver;

public class BD {
	
	
	private static Connection connection;
	private static Statement statement;
	public synchronized static void conexion() {
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:examen.db");
			statement = connection.createStatement();
			try {
				statement.executeUpdate("create table anotaciones (usuario string, coordX integer, coordY integer, contador integer)");
			} catch (SQLException e) {
				if (!e.getMessage().equals("table anotaciones already exists"))  // Este error sí es correcto si la tabla ya existe
					e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public synchronized void Update () {
		
	}
	
	
	/** Actualiza en tabla anotaciones un usuario con anotación existente, modificando su contador
	 * @param usuario	Nombre del usuario
	 * @param x	Coordenada x de la anotación
	 * @param y	Coordenada y de la anotación
	 * @param y	Contador de la anotación
	 */
	public synchronized static void updateAnot( String usuario, int x, int y, int contador ) {
		String sent = "update anotaciones set contador=" + contador + " where usuario='" + usuario + 
				"' AND coordX=" + x + " AND coordY=" + y;
		try {
			statement.executeUpdate(sent);
		} catch (SQLException e) {
			System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
			e.printStackTrace();
		}
	}
	
	
	/** Busca en la tabla anotaciones un usuario y devuelve todas sus anotaciones
	 * @param usuario	Nombre del usuario
	 * @return	Resultset de anotaciones, null si hay cualquier error
	 */
	public synchronized static ResultSet selectAnot( String usuario ) {
		String sent = "select * from anotaciones where usuario='" + usuario + "'";
		try {
			ResultSet rs = statement.executeQuery(sent);
			return rs;
		} catch (SQLException e) {
			System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
			e.printStackTrace();
			return null;
		}
	}
	
	
	
//
//	public synchronized static ResultSet condicion() throws SQLException {
//		
//		statement = connection.createStatement(); 
//		String query = "SELECT usuario, coordX, coordY FROM ANOTACIONES"; 
//		ResultSet rs = statement.executeQuery(query); 
//		while(rs.next()) {
//			String usuario = rs.getString("usuario"); 
//			int cX = rs.getInt("coordX"); 
//			int cY = rs.getInt("coordY"); 
//			
//			
//		}
//		return null;
//		
//		
//	}
	
	
	/** Inserta en tabla anotaciones un nuevo usuario con su anotación
	 * @param usuario	Nombre del usuario
	 * @param x	Coordenada x de la anotación
	 * @param y	Coordenada y de la anotación
	 */
	public synchronized static void insertAnot( String usuario, int x, int y ) {
		String sent = "insert into anotaciones values('" + usuario + "', " + x + "," + y + "," + "1)";
		try {
			statement.executeUpdate(sent);
		} catch (SQLException e) {
			System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
			e.printStackTrace();
		}
	}
	
	
	public synchronized static void finConexion() {
		try {
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
// FIN TAREA 3


