package ord201512;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import ord201512.captura.CapturaPantalla;
import java.awt.*;
import java.awt.event.*;

/**
 * Programa servidor que acepta peticiones de clientes para recibir capturas de pantalla.
 * Cuando un cliente se conecta, se inicia un hilo para gestionar un diálogo con el
 * cliente en el cual el cliente solicita una captura, y el servidor se la entrega.
 */
@SuppressWarnings("unchecked")
public class ServidorCaptura {

	private VentanaCaptura v = null;
    private int numCliente = 0;  // Número correlativo de cliente conectado
    private ServerSocket servidor = null;  // servidor de sockets
    private boolean funcionando = false;  // Lógica de funcionamiento del servidor
    private ArrayList<Conexion> listaClientesActivos = new ArrayList<Conexion>();  // Lista de clientes activos

    /** Crea un servidor de captura
     */
    public ServidorCaptura() {
    }

    /** Activa la ventana visual del servidor
     */
    public void activaVentana() {
    	v = new VentanaCaptura();
    	v.setVisible( true );
    }
    
    /** Inicia el servidor en un hilo independiente. A partir de este punto el servidor 
     * se queda activo en ese hilo, escuchando a todos los clientes que se conecten,
     * hasta que se cierre con {@link #fin()} 
     */
    public void inicio() {
    	(new Thread() {
    		@Override
    		public void run() {
    	        try {
    	        	funcionando = true;
    	        	servidor = new ServerSocket(9898);
    	        	while (funcionando) {
    	                Conexion con = new Conexion( servidor.accept(), numCliente++ );
    	                con.start();
    	                listaClientesActivos.add( con );
    	            }
    	        } catch (Exception e) {
    	        } finally {
    	            try {
    	            	if (servidor!=null) servidor.close();
    				} catch (IOException e) {}
    	        }
    		}
    	}).start();
    	// TAREA 3
		BD.conexion();
    }
    
    /** Cierra el servidor
     */
    public void fin() {
    	funcionando = false;
    	if (servidor!=null)
	     	try {
				servidor.close();  // Cierra el servidor abierto
			} catch (IOException e) {}  
    	// TAREA 3
		BD.finConexion();
		// TAREA 5
		guardaEstadisticas();
    }
    
    /** Visualiza mensaje
     * @param mens	Mensaje a visualizar, en consola y en la ventana si existe
     */
    private void mensaje( String mens ) {
    	// System.out.println( mens );
    	if (v!=null) {
    		taMensajes.append( mens + "\n" );
    		if (taMensajes.getLineCount()>200) {  // Elimina líneas si hay demasiadas
    			try {
					taMensajes.select( taMensajes.getLineStartOffset(0), taMensajes.getLineStartOffset(100) );
					taMensajes.replaceSelection( "" );
				} catch (BadLocationException e) {}
    		}
    		taMensajes.select( taMensajes.getText().length(), taMensajes.getText().length() );
    	}
    }

    /** Hilo para gestionar peticiones de cliente de capturas de pantalla
     */
    private class Conexion extends Thread {
        private Socket socket;
        private int numCliente;

        public Conexion(Socket socket, int numCliente) {
            this.socket = socket;
            this.numCliente = numCliente;
            mensaje( "Nueva conexión con cliente #" + numCliente + " en puerto " + socket );
        }

        /** Método de servicio. Envía mensaje acknowledge 
         * lee mensajes con la información de captura y luego envía la pantalla capturada
         */
        public void run() {
        	String usuario = "";
            try {
            	listaClientesActivos.add( this );  // Añade la conexión a la lista de clientes activos
            	// Streams para recibir caracteres y no solo bytes, y para enviar objetos
                BufferedReader in = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
                ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream() ); 
                // Envía mensaje de comunicación establecida
                out.writeObject( "ACK" );
                // Recibe nombre de usuario del cliente
                usuario = in.readLine();
                mensaje( "Cliente #" + numCliente + " conectado con el usuario " + usuario );
                out.writeObject( "ACK" );
                // TAREA 5
                	procesaNuevaConexión( usuario );
                // Toma mensajes del cliente, línea a línea. Cada línea es la información de la pantalla
                // que debe ser capturada, con el formato numPant,xIni,yIni,anch,alt  (todo enteros válidos)
                // Si el mensaje es nulo o "END" se acaba el proceso.
    	        while (funcionando) {
                    String input = in.readLine();
                    mensaje( "Comando recibido de cliente #" + numCliente + ": " + input );
                    if (input == null || input.equals("END")) {
                        out.writeObject( "ACK" );
                        break;
                    } else {
                    	int numP = 0; int iniX = 0; int iniY = 0; int anch = -1; int alt = -1;
                    	try {
                    		StringTokenizer st = new StringTokenizer( input, "," );
                    		numP = Integer.parseInt( st.nextToken() );
                    		iniX = Integer.parseInt( st.nextToken() );
                    		iniY = Integer.parseInt( st.nextToken() );
                    		anch = Integer.parseInt( st.nextToken() );
                    		alt = Integer.parseInt( st.nextToken() );
                    	} catch (Exception e) { }
                    	// Realizar captura y enviársela al cliente
                    	if (anch==-1 || alt==-1)
                    		out.writeObject( new ImageIcon( CapturaPantalla.capturaPantalla(0) ) );
                    	else
                    		out.writeObject( new ImageIcon( CapturaPantalla.capturaPantalla(numP,iniX,iniY,anch,alt) ) );
                    	// TAREA 3 - Actualizar contadores bd
                    	long cont = BD.selectTrans( usuario );
                    	if (cont==-1) {  // no hay contador - insertar nuevo usuario
                    		BD.insertTrans( usuario, 1 );
                    	} else {
                    		BD.updateTrans( usuario, cont+1 );
                    	}
                    	// FIN TAREA 3
                        // TAREA 5
                    	if (anch==-1 || alt==-1)
                    		procesaNuevaCaptura( usuario, CapturaPantalla.ultimaAnchuraCapturada(), CapturaPantalla.ultimaAlturaCapturada() );
                    	else
                    		procesaNuevaCaptura( usuario, anch, alt );
                        // FIN TAREA 5
                        mensaje( "  Enviada imagen a cliente #" + numCliente );
                        out.reset();  // Evita que se almacenen todas las referencias en memoria (eventualmente heap overflow)
                    }
                }
            } catch (IOException e) {
            	e.printStackTrace( System.out );
            	mensaje( "Error en el cliente #" + numCliente + ": " + e );
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                	mensaje( "Comunicación con cliente " + numCliente + " no ha podido cerrarse correctamente." );
                }
                mensaje( "Conexión con cliente " + numCliente + " cerrada." );
            }
        	listaClientesActivos.remove( this );  // Quita a la conexión de la lista de clientes activos
            // TAREA 5
        		procesaNuevaDesconexión( usuario );
        }
    }
    
    

    private JTextArea taMensajes = null;
    /** Clase de ventana visual del servidor
     */
    @SuppressWarnings("serial")
	private class VentanaCaptura extends JFrame {
    	public VentanaCaptura() {
    		setTitle( "Captura de pantalla - Servidor" );
    		setSize( 600, 400 );
    		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
    		// Componentes
    		taMensajes = new JTextArea( 8, 10 );
	        taMensajes.setEditable(false);
    		getContentPane().add( new JScrollPane(taMensajes), BorderLayout.SOUTH );
    		// Escuchadores
    		addWindowListener( new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					ServidorCaptura.this.fin();
				}
			} );
    		// TAREA 5
    		getContentPane().add( new JScrollPane( tEstadisticas ), BorderLayout.CENTER );
    	}
    }
    
    // TAREA 5
    
    ArrayList<EstadisticaUsuario> lEstadisticas = new ArrayList<EstadisticaUsuario>();
    private TablaEstadisticasModel tem = new TablaEstadisticasModel();
    private JTable tEstadisticas = new JTable( tem );
    
    {  // Inicialización cargando de fichero
    	File fEstadisticas = new File( "estadisticasUsuarios.txt" );
    	if (fEstadisticas.exists()) {
    		try {
	    		ObjectInputStream ois = new ObjectInputStream( new FileInputStream( fEstadisticas ));
	    		lEstadisticas = (ArrayList<EstadisticaUsuario>) ois.readObject();
	    		ois.close();
    		} catch (Exception e) {
    			lEstadisticas = new ArrayList<EstadisticaUsuario>();
    		}
    	}
    }
		private static void addEnOrden( ArrayList<EstadisticaUsuario> l, EstadisticaUsuario eu, int desde, int hasta ) {
			if (desde>=hasta) {  // Desde>hasta contempla el caso de lista vacía inicial
				if (desde>hasta || eu.compareTo( l.get(desde) )<0 )
					l.add( desde, eu );
				else
					l.add( desde+1, eu );
			} else {
				int mitad = (desde + hasta) / 2;
				if (eu.compareTo(l.get(mitad))<=0) {
					addEnOrden(l, eu, desde, mitad);
				} else {
					addEnOrden(l, eu, mitad+1, hasta);
				}
			}
		}
	private static void addEnOrden( ArrayList<EstadisticaUsuario> l, EstadisticaUsuario eu ) {
		addEnOrden( l, eu, 0, l.size()-1 );
	}
		private static int busquedaBinaria( ArrayList<EstadisticaUsuario> l, String nombre, int desde, int hasta ) {
			if (desde>=hasta) {
				if (desde>hasta) return -1;
				if (l.get(desde).usuario.equals(nombre))
					return desde; // Caso base (encontrado)
				else
					return -1; // Caso base (no encontrado)
			} else {
				int mitad = (desde + hasta) / 2;
				if (nombre.compareTo(l.get(mitad).usuario)<=0) {
					return busquedaBinaria(l, nombre, desde, mitad);
				} else {
					return busquedaBinaria(l, nombre, mitad+1, hasta);
				}
			}
		}
	private static int busquedaBinaria( ArrayList<EstadisticaUsuario> l, String nombre ) {
		return busquedaBinaria( l, nombre, 0, l.size()-1 );
	}
    private static class EstadisticaUsuario implements Comparable<EstadisticaUsuario>, Serializable {
		private static final long serialVersionUID = 1L;  // Serializable
		String usuario;
    	boolean activo;
    	int numConexiones;
    	int numCapturas;
    	long bytesTransmitidos;
		public EstadisticaUsuario(String usuario, boolean activo, int numConexiones, int numCapturas, long bytesTransmitidos) {
			this.usuario = usuario;
			this.activo = activo;
			this.numConexiones = numConexiones;
			this.numCapturas = numCapturas;
			this.bytesTransmitidos = bytesTransmitidos;
		}
		@Override
		public int compareTo(EstadisticaUsuario o) {
			if (usuario==null) return "".compareTo(o.usuario);
			return usuario.compareTo(o.usuario);
		}
		@Override
		public String toString() {
			return usuario + (activo?" ACTIVO ":" INACTIVO ") + " - " + numConexiones + " conexiones | " + numCapturas + " capturas | " + bytesTransmitidos + " bytes transmitidos."; 
		}
    }
    @SuppressWarnings("serial")
	private class TablaEstadisticasModel extends DefaultTableModel {
    	@Override
        public int getColumnCount() {
            return 5;
        }
    	@Override
        public int getRowCount() {
    		if (lEstadisticas==null) return 0;
            return lEstadisticas.size();
        }
    	@Override
        public String getColumnName(int col) {
    		switch (col) {
    			case 0: return "Usuario"; 	
    			case 1: return "¿Activo?"; 	
    			case 2: return "Nº Conex"; 	
    			case 3: return "Nº Capturas"; 	
    			case 4: return "Bytes trans."; 	
    		}
    		return "";
        }
    	@Override
        public Class<?> getColumnClass(int c) {
    		switch (c) {
				case 1: return Boolean.class;
				case 2: return Integer.class;
				case 3: return Integer.class;
				case 4: return Long.class;
			}
    		return String.class;
        }
    	@Override
    	public boolean isCellEditable(int row, int column) {
    		return false;
    	}
    	@Override
    	public Object getValueAt(int row, int column) {
    		if (row<0 || row > lEstadisticas.size()) return null;
    		switch (column) {
    			case 0: return lEstadisticas.get(row).usuario; 
    			case 1: return lEstadisticas.get(row).activo; 
    			case 2: return lEstadisticas.get(row).numConexiones; 
    			case 3: return lEstadisticas.get(row).numCapturas; 
    			case 4: return lEstadisticas.get(row).bytesTransmitidos; 
    		}
    		return super.getValueAt(row, column);
    	}
    	@Override
    	public void setValueAt(Object aValue, int row, int column) {
    		if (row<0 || row > lEstadisticas.size()) return;
    		switch (column) {
    			case 0: lEstadisticas.get(row).usuario = (String) aValue; break; 
    			case 1: lEstadisticas.get(row).activo = (Boolean) aValue; break; 
    			case 2: lEstadisticas.get(row).numConexiones = (Integer) aValue; break; 
    			case 3: lEstadisticas.get(row).numCapturas = (Integer) aValue; break; 
    			case 4: lEstadisticas.get(row).bytesTransmitidos = (Long) aValue; break; 
    		}
    	}
    }
    // Métodos internos principales (llamados desde el método de gestión del servidor)
    // Procesa una nueva captura de pantalla de un usuario y una resolución
    private void procesaNuevaCaptura( String usuario, int anchura, int altura ) {
    	int posi = busquedaBinaria( lEstadisticas, usuario );
    	if (posi==-1) return;  // No se pueden sumar estadísticas si no existe el usuario
    	lEstadisticas.get(posi).numCapturas++;
    	lEstadisticas.get(posi).bytesTransmitidos = lEstadisticas.get(posi).bytesTransmitidos + anchura*altura*3;
    	tem.fireTableRowsUpdated( posi, posi );
    }
    // Procesa una nueva conexión de un usuario
    private void procesaNuevaConexión( String usuario ) {
    	int posi = busquedaBinaria( lEstadisticas, usuario );
    	if (posi==-1) {  // Usuario nuevo
    		addEnOrden( lEstadisticas, new EstadisticaUsuario( usuario , true, 1, 0, 0 ));
        	tem.fireTableRowsUpdated( 0, posi );  // Puede haber habido reorden
        	posi = busquedaBinaria( lEstadisticas, usuario );
    	}
    	lEstadisticas.get(posi).activo = true;
    	lEstadisticas.get(posi).numConexiones++;
    	tem.fireTableRowsInserted( posi, posi );
    }
    // Procesa una nueva desconexión de un usuario
    private void procesaNuevaDesconexión( String usuario ) {
    	int posi = busquedaBinaria( lEstadisticas, usuario );
    	if (posi==-1) return;  // No se pueden sumar estadísticas si no existe el usuario
    	lEstadisticas.get(posi).activo = false;
    	tem.fireTableRowsUpdated( posi, posi );
    }
    // Guarda la lista al final
    private void guardaEstadisticas() {
		try {
	    	ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( new File( "estadisticasUsuarios.txt" ) ) );
	    	oos.writeObject( lEstadisticas );
    		oos.close();
		} catch (Exception e) {
		}
    }
    
    // FIN TAREA 5
    
    /** Ejecuta el servidor, que se ejecuta hasta que se cierra la ventana.
     * El servidor escucha el puerto de comunicaciones 9898
     * y espera a clientes que piden conexión. 
     */
    public static void main(String[] args) {
    	ServidorCaptura s = new ServidorCaptura();
        s.inicio();
        s.activaVentana();
        try { Thread.sleep(10); } catch (InterruptedException e) {}
        s.mensaje( "Servidor de captura funcionando." );
    }
    
    
}

