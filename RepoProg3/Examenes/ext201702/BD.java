package ext201702;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JOptionPane;


import int201811.CentroEd;

public class BD {
	
	
	static Connection con;
	static Statement st; 
	
	/**
	 * Inicializa una BD SQLITE y devuelve una conexiÃ³n con ella
	 * @param nombreBD Nombre de fichero de la base de datos
	 * @return ConexiÃ³n con la base de datos indicada. Si hay algÃºn error, se
	 *         devuelve null
	 */
	public static Connection initBD(String nombreBD) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD);
			return con;
		} catch (ClassNotFoundException | SQLException e) {
			return null;
		}
	}
	
	
	
	
	public static boolean abrirConexion(String nombreBD) {

		try {

			System.out.println("ConexiÃ³n abierta");
			Class.forName("org.sqlite.JDBC"); // Carga la clase de BD para sqlite
			Connection con = DriverManager.getConnection("jdbc:sqlite:" + nombreBD);

			// No lo pide el ejercicio, pero si se quiere crear la base de datos si no
			// existe desde el propio programa habrÃ­a que hacer esto:
			// creaciÃ³n bd

			Statement statement = con.createStatement();
			String sent = "CREATE TABLE IF NOT EXISTS analitica (codigo String PRIMARY KEY, contador int);";
			// Codigo es Listado Capicua o Clicl
			System.out.println(sent);
			statement.executeUpdate(sent);

		
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean insertValores(String codigo, int contador) {

		try (Statement statement = con.createStatement()) {
			String sent;

			sent = "insert into analitica values(" + "'" + (codigo) + "', " + "'" + contador + "' " + ")";

			System.out.println(sent);
			int insertados = statement.executeUpdate(sent);

			if (insertados != 1)
				return false;
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	
	
	public static boolean tableUpdate( String codigo, int contador) {
		String sentSQL = "";
		try {
			sentSQL = "update analitica set" +
					" codigo=" + codigo + ", " +
					" contador=" + contador  + "'";
			int val = st.executeUpdate( sentSQL );
			
			if (val!=1) {  // Se tiene que modificar 1 - error si no
				
				return false;  
			}
			return true;
		} catch (SQLException e) {
		
			e.printStackTrace();
			return false;
		}
	}
	

	
	public static String selectCodigo() {
	
			
		
				String SQL = ""; 
				String codigo; 
				try {
					Statement stat = con.createStatement();
					SQL = "select codigo from analisis"; 

					ResultSet rs = stat.executeQuery( SQL );
				
					while(rs.next()) {
						 codigo = rs.getString("codigo"); 
					}
			
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return selectCodigo(); 
			};


			public static int selectContador(String codigo) {
				
				
				String SQL = ""; 
				int contador = 0; 
				try {
					Statement stat = con.createStatement();
					SQL = "select contador from analisis where codigo = " + codigo; 

					ResultSet rs = stat.executeQuery( SQL );
					
					while(rs.next()) {
						 contador = rs.getInt("contador"); 
					}
			
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return contador; 
			};

	
	

	/**
	 * Cierra la conexiÃ³n abierta de base de datos ({@link #abrirConexion(String)})
	 */
	public static void cerrarConexion() {
		try {
			con.close();
			System.out.println("ConexiÃ³n cerrada");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
	
}
