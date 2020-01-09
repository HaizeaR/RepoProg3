package ext201602;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.BadLocationException;

import ext201602.captura.*;

/** Cliente de captura de pantalla basado en Swing.
 * Tiene una ventana principal para introducir el usuario, lanzar la conexi�n
 * y recibir las pantallas hasta que se cierre.
 */
public class ClienteCaptura {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private VentanaCliente v;
    private JTextField tfUsuario, tfIP, tfNumPant, tfXIni, tfYIni, tfAnchura, tfAltura;
    private JTextArea taMensajes;
    private JButton bLanzar;
    private JButton bPregunta;
    private JToggleButton tbAnotacion;
    private JPanelAjusta laPantalla;
    private JPanel pSuperior;
    private Socket socket;
    private boolean funcionando = false;
    private int numCapturas = 0;
    private ImageIcon ultimaCaptura = null;  // Ultima pantalla capturada
    private String nomUsuario = ""; // Usuario identificado en el cliente
    private LinkedList<Object> colaMensajesAServidor = new LinkedList<Object>();  // Cola de mensajes pendientes a enviar al servidor
    private ArrayList<Point> lAnotaciones = new ArrayList<Point>();

    /** Construye el cliente, muestra el GUI
     */
    public ClienteCaptura() {
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
        bLanzar.requestFocusInWindow();
    }

    /** L�gica de conexi�n: se conecta al servidor y realiza la comunicaci�n con el mismo. 
     * Recoge pantallas y las actualiza en la ventana, y se queda haciendo eso hasta que se acaba la comunicaci�n,
     * o hasta que la ventana se cierra por parte del usuario.
     * @param ip	IP del servidor
     * @param usuario	Nombre del usuario que se conecta
     * @param numPant	N�mero de la pantalla que se quiere capturar
     * @param xIni	Coordenada x de la esquina superior izquierda
     * @param yIni	Coordenada y de la esquina superior izquierda
     * @param anchura	Anchura del rect�ngulo a capturar (-1 si se quiere capturar la pantalla completa)
     * @param altura	Altura del rect�ngulo a capturar (-1 si se quiere capturar la pantalla completa)
     * @throws IOException	Error producido si surge cualquier problema en la conexi�n
     */
    public void lanzaConexion( String ip, String usuario, int numPant, int xIni, int yIni, int anchura, int altura ) throws IOException {
        // Pide la direcci�n del servidor
        if (!ip.equals("")) {
			try {
	        	funcionando = true;
		        // Realiza la conexi�n e inicializa los flujos de comunicaci�n
		        socket = new Socket(ip, 9898);
		        in = new ObjectInputStream( socket.getInputStream() );
		        out = new ObjectOutputStream( socket.getOutputStream() );
		        // Consume el Ack (confirmaci�n = acknowledge) del servidor
		        Object respuesta = in.readObject();
		        if (!"ACK".equals(respuesta)) throw new IOException( "Conexi�n err�nea: respuesta del servidor inesperada: " + respuesta );
		        // Env�a el usuario
		        nomUsuario = usuario;
		        out.writeObject( usuario );
		        // Espera el Ack
		        respuesta = in.readObject();
		        if (!"ACK".equals(respuesta)) throw new IOException( "Conexi�n err�nea: " + respuesta );
		        while (funcionando) {
		        	out.writeObject( "HAY_ALGO?" );  // Comando de petici�n de si hay algo pendiente por parte del servidor
			        out.reset(); // Limpia la memoria interna de escritura (los objetos se escriben todos independientes)
			        respuesta = in.readObject();  // Respuesta del servidor
			        if (respuesta!=null) {
			        	// De momento no se hace nada
			        	System.out.println( respuesta );
			        }
		        	if (colaMensajesAServidor.isEmpty()) {  // Si no hay mensajes pendientes se pide captura de pantalla
			        	out.writeObject( "CAPTURA" ); // Comando de captura
				        out.writeObject( numPant + "," + xIni + "," + yIni + "," + anchura + "," + altura );
				        out.reset(); // Limpia la memoria interna de escritura (los objetos se escriben todos independientes)
				        respuesta = in.readObject();
				        if (respuesta==null) {  // Captura le�da, pantalla es igual a la anterior
				        	// Nada que hacer
				        } else if (respuesta instanceof ImageIcon) {  // Le�da captura
				        	ultimaCaptura = (ImageIcon)respuesta;
				        	laPantalla.setImageIcon( ultimaCaptura );
				        	numCapturas++;
				        } else {
							throw new IOException( "Conexi�n err�nea: lectura de elemento incorrecto desde el servidor: " + respuesta );
				        }
		        	} else {  // Si hay mensajes pendientes se env�an al servidor
		        		Object mens = colaMensajesAServidor.removeFirst();  // Quita el mensaje de la cola
		        		if (mens instanceof String) {  // Si es string es una pregunta
				        	out.writeObject( "PREGUNTA" ); // Comando de pregunta
					        out.writeObject( mens );  // Env�a la pregunta
					        out.reset(); // Limpia la memoria interna de escritura (los objetos se escriben todos independientes)
		        		} else if (mens instanceof Integer) {  // Si es integer es una coordenada
				        	out.writeObject( "ANOTACION" ); // Comando de anotaci�n (de coordenada de captura de pantalla)
					        out.writeObject( mens );  // Env�a x
					        mens = colaMensajesAServidor.removeFirst();
					        out.writeObject( mens );  // Env�a y
					        out.reset(); // Limpia la memoria interna de escritura (los objetos se escriben todos independientes)
		        		}
		        	}
		        }
		        out.writeObject( "END" );
		        respuesta = in.readObject();
		        if (!"ACK".equals(respuesta)) throw new IOException( "Conexi�n err�nea: respuesta del servidor inesperada: " + respuesta );
		        out.close();
		        socket.close();
			} catch (ClassNotFoundException e) {  // Error en lectura de objeto
				throw new IOException( "Conexi�n err�nea: lectura de elemento incorrecto desde el servidor" );
			} catch (Exception e) {  // Cualquier otro error
				throw new IOException( "Error en cliente: " + e.getClass().getName() + " - " + e.getMessage()  );
			}
        }
    }
    
    /** Guarda la �ltima pantalla capturada, si la hay, a fichero, en formato jpg
     * @param fic	Fichero en el que guardar la pantalla (preferiblemente con extensi�n .jpg)
     */
    public void guardaCapturaAFichero( File fic ) {
    	Image img = ultimaCaptura.getImage();
    	BufferedImage bi = new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
    	Graphics2D g2 = bi.createGraphics();
    	g2.drawImage(img, 0, 0, null);
    	g2.dispose();
    	try {
			ImageIO.write(bi, "jpg", fic );
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	        tfUsuario = new JTextField("guest",15); tfIP = new JTextField("127.0.0.1",25);
	        tfNumPant = new JTextField("0",3); tfXIni = new JTextField("0",3); tfYIni = new JTextField("0",3);
	        tfAnchura = new JTextField("-1",5); tfAltura = new JTextField("-1",5);
	        laPantalla = new JPanelAjusta( null );
	        bLanzar = new JButton( "Lanza captura!" );
	        bPregunta = new JButton( new ImageIcon( ClienteCaptura.class.getResource("img/smallQuestion.jpg") ) );
	        	bPregunta.setPressedIcon( new ImageIcon( ClienteCaptura.class.getResource("img/smallQuestion-click.jpg") ) );
	        	bPregunta.setBorder( null );
	        tbAnotacion = new JToggleButton( new ImageIcon( ClienteCaptura.class.getResource("img/smallCheck.jpg") ) );
        		tbAnotacion.setSelectedIcon( new ImageIcon( ClienteCaptura.class.getResource("img/smallCheck-click.jpg") ) );
	        	tbAnotacion.setBorder( null );
	        taMensajes = new JTextArea(8, 60);
	        taMensajes.setEditable(false);
	        pSuperior = new JPanel();
	        JPanel pSuperior1 = new JPanel();
	        JPanel pSuperior2 = new JPanel();
	        JPanel pSuperior3 = new JPanel();
	        JPanel pDerecho = new JPanel();
	        pSuperior1.add( new JLabel("Introduce direcci�n IP del servidor (127.0.0.1 misma m�quina):" ));
	        pSuperior1.add( tfIP );
	        pSuperior2.add( new JLabel( "Introduce usuario:" ) );
	        pSuperior2.add( tfUsuario );
	        pSuperior2.add( bLanzar );
	        pSuperior3.add( new JLabel( "N� pantalla" ) );
	        pSuperior3.add( tfNumPant );
	        pSuperior3.add( new JLabel( " - X:" ) );
	        pSuperior3.add( tfXIni );
	        pSuperior3.add( new JLabel( "Y:" ) );
	        pSuperior3.add( tfYIni );
	        pSuperior3.add( new JLabel( "Ancho:" ) );
	        pSuperior3.add( tfAnchura );
	        pSuperior3.add( new JLabel( "Alto" ) );
	        pSuperior3.add( tfAltura );
	        pDerecho.setLayout( new BoxLayout( pDerecho, BoxLayout.Y_AXIS ));
	        pDerecho.add( bPregunta ); pDerecho.add( tbAnotacion );
	        pSuperior.setLayout( new BoxLayout( pSuperior, BoxLayout.Y_AXIS ));
	        pSuperior.add( pSuperior1); pSuperior.add( pSuperior2 ); pSuperior.add( pSuperior3 );
	        getContentPane().add( pSuperior, BorderLayout.NORTH );
	        getContentPane().add( laPantalla, BorderLayout.CENTER );
	        getContentPane().add( new JScrollPane(taMensajes), BorderLayout.SOUTH );
	        getContentPane().add( pDerecho, BorderLayout.EAST );
	        // Escuchadores
    		addWindowListener( new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					ClienteCaptura.this.fin();
				}
			} );
    		bLanzar.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					bLanzar.setEnabled( false );
					pSuperior.setVisible( false );
					Thread t = new Thread() {
						@Override
						public void run() {
					        try {
					        	int nP = 0; int x = 0; int y = 0; int anc = -1; int alt = -1;
					        	try {
					        		nP = Integer.parseInt(tfNumPant.getText());
					        		x = Integer.parseInt(tfXIni.getText());
					        		y = Integer.parseInt(tfYIni.getText());
					        		anc = Integer.parseInt(tfAnchura.getText());
					        		alt = Integer.parseInt(tfAltura.getText());
					        	} catch (NumberFormatException e) {}
								lanzaConexion( tfIP.getText(), tfUsuario.getText(), nP, x, y, anc, alt );
							} catch (IOException e) {
								mensaje( "ERROR: " + e.getMessage() );
								mensaje( "Cerrando por error en conexi�n con servidor..." );
								try { Thread.sleep(3000); } catch (InterruptedException e1) {}
								fin();
							}
						}
					};
					t.start();
				}
			});
    		bPregunta.addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String pidePregunta = JOptionPane.showInputDialog( VentanaCliente.this, "Introduce texto de pregunta:",
							"Env�o de pregunta al servidor", JOptionPane.QUESTION_MESSAGE );
					if (pidePregunta!=null && !pidePregunta.equals(""))
						colaMensajesAServidor.addLast( pidePregunta );
				}
			});
    		laPantalla.addMouseListener( new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (tbAnotacion.isSelected() && ultimaCaptura!=null) {  // Calcula las coordenadas relativas y las transmite
						int xRelativo = (int) ( e.getX() * 1.0 / laPantalla.getWidth() * ultimaCaptura.getIconWidth() );
						int yRelativo = (int) ( e.getY() * 1.0 / laPantalla.getHeight() * ultimaCaptura.getIconHeight() );
						colaMensajesAServidor.addLast( xRelativo );
						colaMensajesAServidor.addLast( yRelativo );
						lAnotaciones.add( new Point( xRelativo, yRelativo ));
						sacaClicksUnRato( xRelativo, yRelativo );
					}
				}
			});
    	}
    }
    
    	private Point temporal = new Point(0,0);  // Usado para recordar temporalmente los puntos
    	// Saca en pantalla las anotaciones temporalmente
    	private void sacaClicksUnRato(int x, int y) {
			temporal = new Point(x,y);
			(new Thread() {
				@Override
				public void run() {
					Point temp2 = (Point)temporal.clone();
					mostrarAnotacion( temp2, true );
					try {
						Thread.sleep(1200);  // Saca 1200 msgs la marca
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mostrarAnotacion( temp2, false );
				}
			}).start();
    	}
    
    // Muestra u oculta una anotaci�n visual en el punto de la pantalla de captura indicado
    // mostrar = true la muestra, mostrar = false la quita
    private void mostrarAnotacion(Point p, boolean mostrar) {
    	if (ultimaCaptura!=null) {
			int xRelativo = (int) ( p.x * 1.0 * laPantalla.getWidth() / ultimaCaptura.getIconWidth() );
			int yRelativo = (int) ( p.y * 1.0 * laPantalla.getHeight() / ultimaCaptura.getIconHeight() );
			if (mostrar) {  // Poner una nueva marca
				JLabelAjusta marca = new JLabelAjusta( new ImageIcon( ClienteCaptura.class.getResource( "img/mark.png" ) ) );
				marca.setBounds( xRelativo-10, yRelativo-10, 20, 20 );
				laPantalla.add( marca );
			} else {  // Quitar marca que ya estuviera
				for (Component c : laPantalla.getComponents()) {
					if (c instanceof JLabelAjusta) {
						JLabelAjusta marca = (JLabelAjusta) c;
						if (marca.getX()==xRelativo-10 && marca.getY()==yRelativo-10) {
							laPantalla.remove( marca );
							break;
						}
					}
				}
			}
			laPantalla.repaint();
    	}
    }

    /** Cierra el cliente
     */
    public void fin() {
    	if (funcionando)
    		funcionando = false;
    	else {
			try {
		    	if (socket!=null) socket.close();
			} catch (IOException e) { }
    	}
		try { Thread.sleep(2000); } catch (InterruptedException e1) {}
    	if (v!=null) v.dispose();
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

    /**
     * Ejecuta la aplicaci�n cliente
     */
    public static void main(String[] args) {
        new ClienteCaptura();
    }
    
}