package ord201512;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import ord201512.captura.JLabelAjusta;

/** Cliente de captura de pantalla basado en Swing.
 * Lanza conexiones con usuario e IPs definidos en constantes globales
 * y recibir capturas de pantallas hasta que se cierre,
 * indicando la velocidad de refresco en n�mero de gr�ficos por segundo.
 */
public class VelocidadCapturaCliente {

    private static final String NOM_USUARIO = "guest"; // Usuario identificado para el test de velocidad
    private static final String IP = "127.0.0.1";      // IP para el test de velocidad
    private ObjectInputStream in;
    private PrintWriter out;
    private VentanaCliente v;
    private JTextArea taMensajes;
    private JLabelAjusta laPantalla;
    private Socket socket;
    private boolean funcionando = false;
    private int numCapturas = 0;
    private ImageIcon ultimaCaptura = null;  // Ultima pantalla capturada

    /** Construye el cliente, muestra el GUI
     */
    public VelocidadCapturaCliente() {
        try {
			SwingUtilities.invokeAndWait( new Runnable() {
				@Override
				public void run() {
			        v = new VentanaCliente( "Cliente de transmisi�n de captura de pantalla" );
			        v.setVisible(true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /** L�gica de conexi�n: se conecta al servidor y realiza la comunicaci�n con el mismo. 
     * Recoge pantallas y las actualiza en la ventana, y se queda haciendo eso en el n�mero de capturas indicadas
     * o hasta que la ventana se cierra por parte del usuario.
     * @param numMaxCapturas	N�mero de capturas que se hacen
     * @param ip	IP del servidor
     * @param usuario	Nombre del usuario que se conecta
     * @param numPant	N�mero de la pantalla que se quiere capturar
     * @param xIni	Coordenada x de la esquina superior izquierda
     * @param yIni	Coordenada y de la esquina superior izquierda
     * @param anchura	Anchura del rect�ngulo a capturar (-1 si se quiere capturar la pantalla completa)
     * @param altura	Altura del rect�ngulo a capturar (-1 si se quiere capturar la pantalla completa)
     * @return	Milisegundos empleados en la comunicaci�n de las capturas, -1 si hay error
     * @throws IOException	Error producido si surge cualquier problema en la conexi�n
     */
    public long lanzaConexionTest( int numMaxCapturas, String ip, String usuario, int numPant, int xIni, int yIni, int anchura, int altura ) throws IOException {
    	long tiempo;
        if (!ip.equals("")) {
			try {
	        	funcionando = true;
		        // Realiza la conexi�n e inicializa los flujos de comunicaci�n
		        socket = new Socket(ip, 9898);
		        in = new ObjectInputStream( socket.getInputStream() );
		        out = new PrintWriter(socket.getOutputStream(), true);
		        // Consume el Ack (confirmaci�n = acknowledge) del servidor
		        Object respuesta = in.readObject();
		        if (!"ACK".equals(respuesta)) throw new IOException( "Conexi�n err�nea: respuesta del servidor inesperada: " + respuesta );
		        // Env�a el usuario
		        out.println( usuario );
		        // Espera el Ack
		        respuesta = in.readObject();
		        if (!"ACK".equals(respuesta)) throw new IOException( "Conexi�n err�nea: " + respuesta );
		        tiempo = System.currentTimeMillis(); 
		        while (funcionando && numMaxCapturas>0) {
			        out.println( numPant + "," + xIni + "," + yIni + "," + anchura + "," + altura );
			        respuesta = in.readObject();
			        if (respuesta instanceof ImageIcon) {  // Le�da captura
			        	ultimaCaptura = (ImageIcon)respuesta;
			        	laPantalla.setImageIcon( ultimaCaptura );
			        	numCapturas++;
			        } else {
						throw new IOException( "Conexi�n err�nea: lectura de elemento incorrecto desde el servidor: " + respuesta );
			        }
			        numMaxCapturas--;
		        }
		        tiempo = System.currentTimeMillis() - tiempo;
		        out.println( "END" );
		        respuesta = in.readObject();
		        if (!"ACK".equals(respuesta)) throw new IOException( "Conexi�n err�nea: respuesta del servidor inesperada: " + respuesta );
		        socket.close();
		        return tiempo;
			} catch (ClassNotFoundException e) {  // Error en lectura de objeto
				throw new IOException( "Conexi�n err�nea: lectura de elemento incorrecto desde el servidor" );
			}
        }
        return -1; // Error
    }
    
    /** Devuelve el n�mero de capturas de pantalla realizadas por el cliente hasta este momento
     * @return
     */
    public int getCapturas() {
    	return numCapturas;
    }

    /** Clase de ventana principal del cliente
     */
    @SuppressWarnings("serial")
	private class VentanaCliente extends JFrame {
    	public VentanaCliente( String titulo ) {
    		setTitle( titulo );
	        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
	        setSize( 1000, 750 );
	        // Componentes
	        laPantalla = new JLabelAjusta( null );
	        taMensajes = new JTextArea(8, 60);
	        taMensajes.setEditable(false);
	        getContentPane().add( laPantalla, BorderLayout.CENTER );
	        getContentPane().add( new JScrollPane(taMensajes), BorderLayout.SOUTH );
	        // Escuchadores
    		addWindowListener( new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					VelocidadCapturaCliente.this.fin(true);
				}
			} );
    	}
    }


    
    /** Cierra el cliente
     */
    public void fin(boolean cerrarVentana) {
    	if (funcionando)
    		funcionando = false;
    	else {
			try {
		    	if (socket!=null) socket.close();
			} catch (IOException e) { }
    	}
		try { Thread.sleep(2000); } catch (InterruptedException e1) {}
    	if (cerrarVentana && v!=null) v.dispose();
    }
    
    /** Visualiza mensaje
     * @param mens	Mensaje a visualizar, en consola y en la ventana si existe
     */
    private void mensaje( String mens ) {
    	System.out.println( mens );
    	if (v!=null) {
    		taMensajes.append( mens + "\n" );
    		if (taMensajes.getLineCount()>200) {  // Elimina l�neas si hay demasiadas
    			try {
					taMensajes.select( taMensajes.getLineStartOffset(0), taMensajes.getLineStartOffset(100) );
					taMensajes.replaceSelection( "" );
				} catch (BadLocationException e) {}
    		}
    		taMensajes.select( taMensajes.getText().length(), taMensajes.getText().length() );  // Se pone al final del textarea
    	}
    }
    
   // CAMBIAMOS ESTE MËTODO Y LO HACEMOS RECURSIVO 
//    private static void testVelocidad() {
//		long tiempo = 0;
//		int anchuraTest = 0;
//		int alturaTest = 0;
//        vcc = new VelocidadCapturaCliente();
//        try {
//    		// Prueba de 100 capturas de 1000x750
//        	anchuraTest = 1000; alturaTest = 750;
//        	tiempo = vcc.lanzaConexionTest( 100, IP, NOM_USUARIO, 0, 0, 0, anchuraTest, alturaTest );
//    		vcc.fin(false);  // Acaba pero no cierra a�n la ventana 
//    		vcc.mensaje( "Velocidad de transferencia de " + anchuraTest + "x" + alturaTest + " = " + 100.0 / tiempo * 1000 + " gr�ficos/segundo." );
//    		// Fin prueba 100 capturas
//    		
//    		// Prueba de 100 capturas de 10x10
//        	anchuraTest = 10; alturaTest = 10;
//        	tiempo = vcc.lanzaConexionTest( 100, IP, NOM_USUARIO, 0, 0, 0, anchuraTest, alturaTest );
//    		vcc.mensaje( "Velocidad de transferencia de " + anchuraTest + "x" + alturaTest + " = " + 100.0 / tiempo * 1000 + " gr�ficos/segundo." );
//    		try { Thread.sleep(3000); } catch (InterruptedException e1) {}
//    		vcc.fin(true);  // Acaba cerrando ya la ventana 
//    		// Fin prueba 100 capturas
//    		
//		} catch (IOException e) {
//			vcc.mensaje( "ERROR: " + e.getMessage() );
//			vcc.mensaje( "Cerrando por error en conexi�n con servidor..." );
//			try { Thread.sleep(3000); } catch (InterruptedException e1) {}
//		}
//    }
//    


   
 // TAREA 4
	private static void testVelocidad( double pantsPorSegObjetivo, int numLlams, int anch1, int alt1, int anch2, int alt2 ) throws IOException {
		
		int anchMedia = (anch1+anch2)/2;
		int altMedia = (alt1+alt2)/2;
		// Prueba de 100 capturas 
		
       	long tiempo = vcc.lanzaConexionTest( 100, IP, NOM_USUARIO, 0, 0, 0, anchMedia, altMedia );
       	
       	double velocidad = 100.0 / tiempo * 1000;
       	
		vcc.fin(false);  // Acaba pero no cierra aún la ventana 
		vcc.mensaje( "Velocidad de transferencia de " + anchMedia + "x" + altMedia + " = " +  velocidad + " gráficos/segundo." );
		
		if (numLlams==0) {  // Caso base
		} else {
			// 25 pqntalas por segundo 
			if (velocidad < pantsPorSegObjetivo)
				testVelocidad( pantsPorSegObjetivo, numLlams-1, anchMedia, altMedia, anch2, alt2);
			else
				testVelocidad( pantsPorSegObjetivo, numLlams-1, anch1, alt1, anchMedia, altMedia);
		}
	}
	private static void testVelocidad() {
    vcc = new VelocidadCapturaCliente();
	// Prueba de 100 capturas recursiva
    try {
    	
    	// num_llamadas 15 
    	// caso base que llegue a 0 
    	// caso recursivo de 15 a 0 
    	
    	testVelocidad( 25, 15, 1000, 750, 10, 10 );
	} catch (IOException e) {
		vcc.mensaje( "ERROR: " + e.getMessage() );
		vcc.mensaje( "Cerrando por error en conexión con servidor..." );
		try { Thread.sleep(3000); } catch (InterruptedException e1) {}
	}
	try { Thread.sleep(3000); } catch (InterruptedException e1) {}
	vcc.fin(true);  // Acaba cerrando ya la ventana 
}
// FIN TAREA 4
    
    
    
    
    
    
    
    
    
    	private static VelocidadCapturaCliente vcc = null;
    /**
     * Ejecuta la aplicaci�n cliente y testea la velocidad
     */
    public static void main(String[] args) {
        testVelocidad();
    }
    
}