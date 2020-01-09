package ext201602;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

import ext201602.captura.CapturaPantalla;

import java.awt.*;
import java.awt.event.*;
import java.awt.color.*;
import java.awt.image.*;

/**
 * Programa servidor que acepta peticiones de clientes para recibir capturas de pantalla.
 * Cuando un cliente se conecta, se inicia un hilo para gestionar un diálogo con el
 * cliente en el cual el cliente solicita una captura, y el servidor se la entrega.
 */
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
    }
    
    /** Visualiza mensaje
     * @param mens	Mensaje a visualizar, en consola y en la ventana si existe
     */
    private void mensaje( String mens ) {
    	System.out.println( mens );
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

        private BufferedImage antCaptura = null;
        /** Método de servicio. Envía mensaje acknowledge 
         * lee mensajes con la información de captura y luego envía la pantalla capturada
         */
        public void run() {
        	String usuario = "";
            try {
            	listaClientesActivos.add( this );  // Añade la conexión a la lista de clientes activos
            	// Streams para recibir caracteres y no solo bytes, y para enviar objetos
                ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream() ); 
                ObjectInputStream in = new ObjectInputStream( socket.getInputStream() );
                // Envía mensaje de comunicación establecida
                out.writeObject( "ACK" );
                // Recibe nombre de usuario del cliente
                usuario = (String) in.readObject();
                mensaje( "Cliente #" + numCliente + " conectado con el usuario " + usuario );
                out.writeObject( "ACK" );
                // Toma mensajes del cliente, línea a línea. Cada línea es la información de la pantalla
                // que debe ser capturada, con el formato numPant,xIni,yIni,anch,alt  (todo enteros válidos)
                // Si el mensaje es nulo o "END" se acaba el proceso.
    	        while (funcionando) {
                    String input = (String) in.readObject();
                    if (input == null || input.equals("END")) {
                        out.writeObject( "ACK" );
                        break;
                    } else if ("CAPTURA".equals(input)){  // Se pide captura de pantalla
                        input = (String) in.readObject(); // Leer configuración de captura
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
                    	BufferedImage captura = null;
                    	if (anch==-1 || alt==-1)
                    		captura = CapturaPantalla.capturaPantalla(numP);
                    	else
                    		captura = CapturaPantalla.capturaPantalla(numP,iniX,iniY,anch,alt);

                		if (sonImgsIguales(captura,antCaptura)) {  // No se envía la pantalla si es la misma
                    		out.writeObject( null );
                		} else {  // Se envía si hay cambios
                    		out.writeObject( new ImageIcon( captura ) );
                            mensaje( "Enviada CAPTURA a cliente #" + numCliente + ": " + input );
                		}
                		antCaptura = captura;
                        out.reset();  // Evita que se almacenen todas las referencias en memoria (eventualmente heap overflow)
                    } else if ("PREGUNTA".equals(input)){  // Se pide pregunta (texto)
                    	input = (String) in.readObject();
                    	pregunta( usuario, input );
                    } else if ("ANOTACION".equals(input)){  // Se pide anotación (x,y)
                    	int coordX = (Integer) in.readObject();
                    	int coordY = (Integer) in.readObject();
                    	anotacion( usuario, coordX, coordY );
                    } else if ("HAY_ALGO?".equals(input)){  // el cliente pide al servidor si tiene algo para él
                    	// TAREA 5
                    	if (respuestasPendientes.isEmpty())
                    		out.writeObject( null );  // de momento no hay nada
                    	else {
                    		do {  // Manda preguntas y respuestas pendientes
	                    		out.writeObject( "RESPUESTA" );
	                    		out.writeObject( respuestasPendientes.getFirst() );
	                    		respuestasPendientes.removeFirst();
	                    		out.writeObject( respuestasPendientes.getFirst() );
	                    		respuestasPendientes.removeFirst();
                    		} while (!respuestasPendientes.isEmpty());
                    	}
                    }
                }
            } catch (IOException e) {
            	e.printStackTrace( System.out );
            	mensaje( "Error en el cliente #" + numCliente + ": " + e );
            } catch (Exception e) {  // Recepción o cast del in.readObject()
            	e.printStackTrace( System.out );
            	mensaje( "Error en recepción del cliente #" + numCliente + ": " + e );
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                	mensaje( "Comunicación con cliente " + numCliente + " no ha podido cerrarse correctamente." );
                }
                mensaje( "Conexión con cliente " + numCliente + " cerrada." );
            }
        	listaClientesActivos.remove( this );  // Quita a la conexión de la lista de clientes activos
        	// TAREA 2
	        if (psSalida!=null) psSalida.close();  // Cierra el fichero de la tarea 2 si estaba abierto
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
    		cabeceraTabla = new Vector<String>();
    		cabeceraTabla.add( "Usuario" ); cabeceraTabla.add( "Pregunta" ); cabeceraTabla.add( "Respuesta" );
    		datosTabla = new Vector<Vector<String>>();
    		modeloTabla = new MiModeloTabla(datosTabla,cabeceraTabla);
    		tabla = new MiTabla(modeloTabla);
    		getContentPane().add( new JScrollPane(tabla), BorderLayout.CENTER );
    		modeloTabla.addTableModelListener( new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					if (e.getColumn()==2 && e.getFirstRow()>=0 && e.getFirstRow()<datosTabla.size()) {
						// Si se cambia un valor correcto en la columna 2 -la editable- es una respuesta que hay que enviar al cliente
						respuestasPendientes.add( datosTabla.get(e.getFirstRow()).get(1) );  // Pregunta
						respuestasPendientes.add( datosTabla.get(e.getFirstRow()).get(2) );  // Respuesta
					}
				}
			});
    	}
    }
    
    // Procesa una pregunta lanzada por el cliente
    private void pregunta( String nomUsuario, String textoPregunta ) {
    	System.out.println( "Pregunta del usuario " + nomUsuario + ": " + textoPregunta );
    	// TAREA 2
    	sacaAFichero( nomUsuario, textoPregunta );
    	// TAREA 5
    	anyadeATabla( nomUsuario, textoPregunta );
    }
    
    	// TAREA 2
    	private PrintStream psSalida = null;
		private SimpleDateFormat f = new SimpleDateFormat( "dd/MM/yy HH:mm:ss.SSS" );
    	private void sacaAFichero( String nomUsuario, String textoPregunta ) {
    		if (nomUsuario!=null && !nomUsuario.equals("")) {
				try {
	    			if (psSalida==null) // Si el fichero no estaba abierto lo abre (primera escritura de esta sesión)
	    				psSalida = new PrintStream( new FileOutputStream( nomUsuario + ".txt", true ) );  // Append = true
	    			psSalida.println( f.format(new Date()) + "\t" + textoPregunta );
	    			// Si se quieren sacar centésimas en lugar de milésimas el formateador no vale... para
	    			// seguir fielmente el enunciado con las centésimas habría que hacer la conversión msgs a centésimas a mano:
	    			// SimpleDateFormat f = new SimpleDateFormat( "dd/MM/yy HH:mm:ss." );
	    			// Date ahora = new Date();
	    			// long milesimas = ahora.getTime() % 1000; 
	    			// psSalida.println( f.format(ahora) + String.format("%02d", Math.round(milesimas/10.0)) + "\t" + textoPregunta );
				} catch (IOException e) {
				}
    		}
    	}
    	
    // Procesa una anotacion lanzada por el cliente
    private void anotacion( String nomUsuario, int x, int y ) {
    	System.out.println( "Anotación del usuario " + nomUsuario + ": " + x + "," + y );
    	// TAREA 3
    	anyadeABD( nomUsuario, x, y );
    }
		// TAREA 3
		private void anyadeABD( String nomUsuario, int x, int y ) {
			ResultSet anotacionesPrevias = BD.selectAnot( nomUsuario );
			boolean habiaCercana = false;
			try {
				while (anotacionesPrevias.next()) {
					int xPrevia = anotacionesPrevias.getInt( "coordX" );
					int yPrevia = anotacionesPrevias.getInt( "coordY" );
					int contPrevio = anotacionesPrevias.getInt( "contador" );
					if (x>=xPrevia-5 && x<=xPrevia+5 && y>=yPrevia-5 && y<=yPrevia+5) {  // Es cercano!
						habiaCercana = true;
						BD.updateAnot( nomUsuario, xPrevia, yPrevia, contPrevio+1 );
						break;  // Sale del while (solo coge la primera anotación cercana) 
					}
				}
				if (!habiaCercana) {
					BD.insertAnot( nomUsuario, x, y );
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
    	// TAREA 5
    	MiTabla tabla;
    	MiModeloTabla modeloTabla;
    	Vector<Vector<String>> datosTabla;
    	Vector<String> cabeceraTabla;
    	private void anyadeATabla( String nomUsuario, String textoPregunta ) {
    		Vector<String> fila = new Vector<String>();
    		fila.add( nomUsuario ); fila.add( textoPregunta ); fila.add( "" );
    		System.out.println( datosTabla );
    		modeloTabla.addRow( fila );
    		System.out.println( " " + datosTabla );
    	}
    	private class MiTabla extends JTable {
			private static final long serialVersionUID = 1L;
			public MiTabla( DefaultTableModel modelo ) { super(modelo); }
    	}
    	private class MiModeloTabla extends DefaultTableModel {
			private static final long serialVersionUID = 1L;
			public MiModeloTabla( Vector<Vector<String>> datos, Vector<String> cabs ) { super( datos, cabs ); }
			@Override
			public boolean isCellEditable(int row, int column) {  // Solo es editable la respuesta
				if (column==2) return true; else return false;
			}
			
    	}
    	// Parte de comunicación de respuestas con el cliente
    	LinkedList<String> respuestasPendientes = new LinkedList<String>();

    
    	// Compara dos imágenes píxel a píxel
		private static boolean sonImgsIguales( BufferedImage i1, BufferedImage i2 ) {
			boolean sonIguales = false;
			if (i1!=null && i2!=null) {
				int[] act = getPixels(i1);
				int[] ant = getPixels(i2);
				if (act!=null && ant!=null && act.length==ant.length) {
					sonIguales = true;
					for( int pix=0; pix<act.length; pix++) {
						if (act[pix]!=ant[pix]) { sonIguales = false; break; }
					}
				}
			}
			return sonIguales;
		}
		// Devuelve un array de píxels de una imagen
	    private static int[] getPixels(BufferedImage img) {
	        final int width = img.getWidth();
	        final int height = img.getHeight();
	        int[] pixelData = new int[width * height];
	        final Image pixelImg; 
	        if (img.getColorModel().getColorSpace() == ColorSpace.getInstance(ColorSpace.CS_sRGB)) {
	            pixelImg = img;
	        } else {
	            pixelImg = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(img, null);
	        }
	        final PixelGrabber pg = new PixelGrabber(pixelImg, 0, 0, width, height, pixelData, 0, width);
	        try {
	            if(!pg.grabPixels()) {
	                throw new RuntimeException();
	            }
	        } catch (final InterruptedException ie) {
	            return null;
	        }
	        return pixelData;
	    }
    
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

