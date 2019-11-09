package T3.ejemplo6;


import java.awt.BorderLayout;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;

import com.sun.org.apache.bcel.internal.generic.Select;

public class Main extends JFrame {
	


	JTextArea taTexto; 
	JButton bVerProducto, bCompra, bAnular; 
	JPanel pBotonera; 
	private static Connection conn;
	private static Statement stmt; 
	
	
	
	private Main() {
		
		setSize(600,400);
		setLocation(300, 200);

		setTitle("MiniAmazon");
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		
		pBotonera = new JPanel(); 
		getContentPane().add(pBotonera, BorderLayout.SOUTH); 
		
		bVerProducto = new JButton("Ver Producto"); 
		bCompra = new JButton("Compra");
		bAnular = new JButton("Anular");
		
		pBotonera.add(bVerProducto);
		pBotonera.add(bCompra);
		pBotonera.add(bAnular);
		
		
		taTexto = new JTextArea(); 
		taTexto.setEditable(false); // Esto hace que no se pueda escribir 
		getContentPane().add(taTexto, BorderLayout.CENTER);
		
		
		bVerProducto.addActionListener((ActionListener) -> {clickVerProductos();});
		bCompra.addActionListener((ActionListener)->{clickCompra();});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				cargarBD("miniAmazon.db");
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				cerrarBD(conn,stmt); 
			}
		});
	}
	
	private static void clickCompra() {
		
		
	}
	
	private static void clickVerProductos() {
		
	}
	

	private static void cargarBD(String nombFich)   {

		System.out.println( "Conexión abierta" );
		
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:"+ nombFich );
			
			
		}catch( ClassNotFoundException | SQLException e ) {
			System.out.println("Error");
			e.printStackTrace();
		}

	}
	
	private static void cerrarBD(Connection conn, Statement stmt) {

		try {
			if (stmt!=null) stmt.close();
			if (conn!=null) conn.close();
			System.out.println("Cierre de base de datos");
		}catch(SQLException e) {
			e.printStackTrace();
			
		}
	}
	
	
	
	public static ArrayList<Producto> getProductos(){

		try { 
			ArrayList<Producto> ret = new ArrayList<Producto>(); 
			Statement statement = conn.createStatement(); 
			
			return ret;
			} catch (SQLException e) { 
				// TODO catch block 
				e.printStackTrace(); 
				return null;
				}

	}

	
	public static void main(String[] args) throws ClassNotFoundException {
		Main v1 = new Main(); 
		v1.setVisible(true);
		
		
	}

}
