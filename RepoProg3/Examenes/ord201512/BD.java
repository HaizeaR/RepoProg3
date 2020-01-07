package ord201512;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;



public class BD {



		private static Connection connection;
		private static Statement statement;
		public synchronized static void conexion() {
			try {
				connection = DriverManager.getConnection("jdbc:sqlite:examen.db");
				statement = connection.createStatement();
				try {
					statement.executeUpdate("create table transmision (usuario string, numCapturas integer)");
				} catch (SQLException e) {
					if (!e.getMessage().equals("table transmision already exists"))  // Este error sí es correcto si la tabla ya existe
						e.printStackTrace();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		/** Inserta en tabla transmisión un nuevo usuario con su contador de capturas
		 * @param usuario	Nombre del usuario
		 * @param numCapturas	Número de capturas
		 */
		public synchronized static void insertTrans( String usuario, long numCapturas ) {
			String sent = "insert into transmision values('" + usuario + "', " + numCapturas + ")";
			try {
				statement.executeUpdate(sent);
			} catch (SQLException e) {
				System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
				e.printStackTrace();
			}
		}
		/** Actualiza en tabla transmisión un usuario con su contador de capturas
		 * @param usuario	Nombre del usuario
		 * @param numCapturas	Número de capturas
		 */
		public synchronized static void updateTrans( String usuario, long numCapturas ) {
			String sent = "update transmision set numCapturas=" + numCapturas + " where usuario='" + usuario +"'";
			try {
				statement.executeUpdate(sent);
			} catch (SQLException e) {
				System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
				e.printStackTrace();
			}
		}
		
		/** Busca en la tabla transmisión un usuario y devuelve su contador
		 * @param usuario	Nombre del usuario
		 * @return	Número de capturas, -1 si no se encuentra en la tabla
		 */
		public synchronized static long selectTrans( String usuario ) {
			String sent = "select * from transmision where usuario='" + usuario + "'";
			try {
				ResultSet rs = statement.executeQuery(sent);
				int numCapturas = -1;  // Si no hay fila para el usuario
				if (rs.next()) numCapturas = rs.getInt("numCapturas");
				rs.close();
				return numCapturas;
			} catch (SQLException e) {
				System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
				e.printStackTrace();
				return -1;
			}
		}
		
		// Versiones mejores (como se ve en ejecución al salvar a BD se pierde mucho tiempo en el mismo hilo)
		// Si se hace en un hilo diferente hay mejora en el rendimiento
		
		/** Inserta en tabla transmisión un nuevo usuario con su contador de capturas
		 * @param usuario	Nombre del usuario
		 * @param numCapturas	Número de capturas
		 */
		public synchronized static void insertTransHilo( String usuario, long numCapturas ) {
			final String sent = "insert into transmision values('" + usuario + "', " + numCapturas + ")";
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						statement.executeUpdate(sent);
					} catch (SQLException e) {
						System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
						e.printStackTrace();
					}
				}
			};
			(new Thread(r)).start();
		}
		/** Actualiza en tabla transmisión un usuario con su contador de capturas
		 * @param usuario	Nombre del usuario
		 * @param numCapturas	Número de capturas
		 */
		public synchronized static void updateTransHilo( String usuario, long numCapturas ) {
			final String sent = "update transmision set numCapturas=" + numCapturas + " where usuario='" + usuario +"'";
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						statement.executeUpdate(sent);
					} catch (SQLException e) {
						System.out.println( "ERROR EN SENTENCIA SQL: " + sent);
						e.printStackTrace();
					}
				}
			};
			(new Thread(r)).start();
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