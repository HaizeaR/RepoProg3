package ord201806.iu;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ord201806.datos.*;

/** Ventana principal de proceso de logs - examen ordinaria 201806
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
@SuppressWarnings("serial")
public class VentanaProcesoLogs extends JFrame {

	// Atributos de componentes principales de ventana
	private JLabel lMensaje; // l�nea de mensajes de la ventana
	private JTextArea taVer; // �rea de texto de salida de informaci�n
	public JTextField tfCarpeta; // Cuadro de texto de carpeta a explorar
	private JTextField tfExtensiones; // Cuadro de texto de extensiones de ficheros a buscar
	private JComboBox<String> cbTextoABuscar; // Combo de texto a buscar con el historial de textos buscados
	public JList<FicheroLog> lFicheros; // Lista de ficheros de log encontrados
	private JList<Evento> lEventos; // Lista de eventos de log encontrados
	private JButton bCarpeta; // Bot�n de b�squeda de carpeta
	private JButton bBuscarTexto; // Bot�n de b�squeda en logs
	private JButton bBuscarEventos; // Bot�n de b�squeda de eventos en logs
	private JPanel p2; // Panel de botonera de b�squeda
	
	// Atributos de datos
	private DefaultComboBoxModel<String> mTextoABuscar; // Modelo de datos para el combo
	private DefaultListModel<FicheroLog> mFicheros; // Modelo de datos para la lista de ficheros
	private DefaultListModel<Evento> mEventos; // Modelo de datos para la lista de eventos
	
	/** Constructor de ventana
	 */
	public VentanaProcesoLogs() {
		// Inicializaci�n general
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setTitle( "Gesti�n de logs     (Examen Prog III - junio 2018)");
		setSize( 1000, 600 );
		// Creaci�n de componentes y contenedores
		taVer = new JTextArea();
		tfCarpeta = new JTextField( "", 20 );
		tfExtensiones = new JTextField( "", 10 );
		tfExtensiones.setToolTipText( "Si son varias, separarlas con comas");
		mTextoABuscar = new DefaultComboBoxModel<>();
		cbTextoABuscar = new JComboBox<String>( mTextoABuscar );
		lMensaje = new JLabel( "" );
		mFicheros = new DefaultListModel<FicheroLog>();
		lFicheros = new JList<FicheroLog>( mFicheros );
		mEventos = new DefaultListModel<Evento>();
		lEventos = new JList<Evento>( mEventos );
		lEventos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		JScrollPane spFicheros = new JScrollPane( lFicheros ); 
			spFicheros.setPreferredSize( new Dimension( 150, 100 ) );  // Define tama�os de la lista para el ajuste del layout
		JScrollPane spEventos = new JScrollPane( lEventos ); 
			spEventos.setPreferredSize( new Dimension( 300, 100 ) );  // Define tama�os de la lista para el ajuste del layout
		JPanel p1 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		p2 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		JPanel pCent = new JPanel( new BorderLayout() );
		JPanel pSup = new JPanel();
		JPanel pInf = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		bCarpeta = new JButton( "Buscar logs en carpeta" );
		bBuscarTexto = new JButton( "Buscar texto" );
		bBuscarEventos = new JButton( "Buscar eventos" );
		// Layouts y asignaci�n a contenedores
		pSup.setLayout( new BoxLayout( pSup, BoxLayout.Y_AXIS ));
		pSup.add( p1 ); pSup.add( p2 );
		p1.add( new JLabel( "Exts. de ficheros" ) ); p1.add( tfExtensiones );
		p1.add( new JLabel( "Carpeta:" ) ); p1.add( tfCarpeta ); p1.add( bCarpeta );
		p2.add( new JLabel( "Texto a buscar:" ) ); p2.add( cbTextoABuscar ); p2.add( bBuscarTexto );  p2.add( bBuscarEventos );
		pCent.add( spEventos, BorderLayout.WEST );
		pCent.add( new JScrollPane( taVer ), BorderLayout.CENTER );
		pInf.add( lMensaje );
		getContentPane().add( pSup, BorderLayout.NORTH );
		getContentPane().add( spFicheros, BorderLayout.WEST );
		getContentPane().add( pCent, BorderLayout.CENTER );
		getContentPane().add( pInf, BorderLayout.SOUTH );
		cbTextoABuscar.setEditable( true );
		// Eventos
		tfExtensiones.addKeyListener( new KeyAdapter() {  // No permite espacios en las extensiones
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == ' ') e.consume();
			}
		});
		tfCarpeta.addFocusListener( new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Config.setProperty( Config.PROP_CARPETA, tfCarpeta.getText() ); // Cambia la carpeta de b�squeda en configuraci�n
			}
		});
		bCarpeta.addActionListener( new ActionListener() {  // Acci�n de bot�n carpeta
			@Override
			public void actionPerformed(ActionEvent e) {
				// Crea y preparar un filechooser para elegir una carpeta (desde la ya definida en el cuadro de texto tfCarpeta)
				JFileChooser fChooser = new JFileChooser();
				fChooser.setCurrentDirectory( new File( tfCarpeta.getText() ) );
				fChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
				fChooser.setDialogTitle( "Elige carpeta en la que buscar" );
				if (fChooser.showOpenDialog( VentanaProcesoLogs.this )==JFileChooser.APPROVE_OPTION) { // Lanza el di�logo y comprueba si se ha pulsado "abrir"
					tfCarpeta.setText( fChooser.getSelectedFile().getAbsolutePath() );
					Config.setProperty( Config.PROP_CARPETA, tfCarpeta.getText() ); // Cambia la carpeta de b�squeda en configuraci�n
					buscarEnCarpeta();
				}
			}
		});
		bBuscarTexto.addActionListener( new ActionListener() {  // Acci�n de bot�n buscar texto
			@Override
			public void actionPerformed(ActionEvent e) {
				String texto = (String) cbTextoABuscar.getSelectedItem();
				if (texto==null || texto.isEmpty()) return;
				// A�ade el texto a la property de configuraci�n (si no estaba)
				if (!Config.getProperty( Config.PROP_TEXTO_BUSQ ).contains( "#$#" + texto + "#$#" )) {  
					Config.setProperty( Config.PROP_TEXTO_BUSQ, Config.getProperty( Config.PROP_TEXTO_BUSQ ) + texto + "#$#" );
				}
				// Cambia la extensi�n buscada en la property
				Config.setProperty( Config.PROP_EXT, tfExtensiones.getText().trim() );
				buscarTexto( texto );
			}
		});
		bBuscarEventos.addActionListener( new ActionListener() {  // Acci�n de bot�n buscar eventos
			@Override
			public void actionPerformed(ActionEvent e) {
				buscarEventos();
			}
		});
		lFicheros.addListSelectionListener( new ListSelectionListener() {  // Selecci�n de lista de fichero
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (lFicheros.getSelectedValue()==null) {
						taVer.setText( "" );
					} else {
						sacaFicAVentana( lFicheros.getSelectedValue() );
					}
				}
			}
		});
		lEventos.addListSelectionListener( new ListSelectionListener() {  // Selecci�n de lista de evento
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (lEventos.getSelectedValue()==null) {
						taVer.setText( "" );
					} else {
						taVer.setText( lEventos.getSelectedValue().getTexto() + "\n" );
					}
				}
			}
		});
		addWindowListener( new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				cargarConfig();
				if (!tfCarpeta.getText().isEmpty() && !tfCarpeta.getText().equals( "c:\\" )) {
					// Si la carpeta no tiene el valor vac�o o por defecto se lanza la b�squeda al iniciar (sin pulsar el bot�n expl�citamente)
					buscarEnCarpeta();
					// Carga los eventos de fichero
					ObjectInputStream ois = null;
					try {
						ois = new ObjectInputStream( new FileInputStream( "eventos.dat ") );
						while (true) {  // Se corta cuando haya error de lectura
							Evento evento = (Evento) ois.readObject();
							mEventos.addElement( evento );
						}
					} catch (Exception ex) {
						try {
							ois.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			@Override
			public void windowClosing(WindowEvent e) {
				sigueHiloBusqLog = false;  // Acaba los hilos si estuvieran activados
				sigueHiloEventosLog = false;
				sigueHiloBusqFicheros = false;
				Config.guardar( VentanaProcesoLogs.this ); // Guarda la configuraci�n
				// Guarda los eventos en fichero
				try {
					ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream( "eventos.dat ") );
					for (int i=0; i<mEventos.size(); i++) {
						Evento evento = mEventos.elementAt( i );
						oos.writeObject( evento );
					}
					oos.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// Cierra de forma progresiva
				(new Thread() {
					@Override
					public void run() {
						while (getHeight()>40) {
							setSize( getWidth()-2, getHeight()-1 );
							if (getOpacity()>=0.002f) setOpacity( getOpacity()-0.002f );
							try { Thread.sleep(5); } catch (InterruptedException e) {}
						}
						dispose();
					}
				}).start();
			}
		});
	}
	
	// Gesti�n de b�squeda de ficheros en carpeta

	private boolean sigueHiloBusqFicheros; // Variable l�gica para acabar el hilo de b�squeda de ficheros si procede
	
	public void buscarEnCarpeta() {
		bCarpeta.setEnabled( false );  // Desactiva el bot�n de carpeta hasta que se acabe la b�squeda
		sigueHiloBusqFicheros = true;  // Marca el inicio del funcionamiento del hilo de b�squeda
		mFicheros.clear(); // Borra los ficheros de la lista para recalcularlos
		(new Thread() {
			@Override
			public void run() {
				lMensaje.setText( "Buscando ficheros " + tfExtensiones.getText() + " en carpeta...\n" );
				buscarRec( new File( tfCarpeta.getText() ), "" );
				lMensaje.setText( "Fin de b�squeda." );
				bCarpeta.setEnabled( true ); // Reactiva el bot�n de carpeta
			}
		}).start();
	}
		// B�squeda recursiva en carpetas (el tema de la recursividad se aplica por funcionalidad para este examen,
		// pero no se considera en la asignatura, ni se preguntar� por esto en las tareas)
		private void buscarRec( File dir, String tablet ) {
			if (dir.isDirectory() && dir.listFiles()!=null) {
				lMensaje.setText( "Buscando en " + dir.getAbsolutePath() );
				for (File f : dir.listFiles()) {
					if (!sigueHiloBusqFicheros) return; // Corta el hilo si procede
					if (f.isDirectory()) {  // Es un directorio, busca dentro de esa carpeta recursivamente
						String posibleTablet = tablet;
						if ( f.getName().toUpperCase().startsWith( "T" ) && f.getName().length()==4 ) {
							posibleTablet = f.getName(); // La carpeta marca una tablet de log (formato "Txxx")
						}
						buscarRec( f, posibleTablet );
					} else {  // Es un fichero
						// Mira a ver si la extensi�n del fichero coincide con alguna de las extensiones buscadas
						String extsPosibles = tfExtensiones.getText().toUpperCase().trim();
						if (!extsPosibles.isEmpty()) extsPosibles = "," + extsPosibles  + ",";
						String ext = "";
						int i = f.getName().lastIndexOf('.');
						if (i > 0) ext = f.getName().substring(i+1).toUpperCase();
						if (ext.isEmpty() || extsPosibles.isEmpty() || extsPosibles.contains( "," + ext + "," )) {  // Extensi�n v�lida o no definida - y en ese caso se coge
							anyadirFicheroALista( f, tablet );
						}
					}
				}
			}
		}

			// Atributos para los radio buttons de las tablets
			private HashSet<String> tablets = new HashSet<String>();
			private ButtonGroup bGroup = new ButtonGroup();
		private void anyadirFicheroALista( File f, String tablet ) {
			final FicheroLog fl = new FicheroLog( f, tablet );
			SwingUtilities.invokeLater( new Runnable() {  // Modifica de forma segura un elemento de swing desde un hilo diferente
				@Override
				public void run() {
					mFicheros.addElement( fl );  // A�ade fichero a la lista 
					String tablet = fl.getCodTablet();
					if (!tablets.contains( tablet )) {  // Si la tablet no estaba a�adir el radio button
						tablets.add( tablet );
						JRadioButton rb = new JRadioButton( tablet );
						p2.add( rb );
						p2.revalidate();
						bGroup.add( rb );
						rb.addActionListener( new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								String miTablet = ((JRadioButton)e.getSource()).getText();  // Una manera de saber qu� tablet es ese bot�n - se puede hacer de otros modos con el atributo tablet
								int i=0;
								while (i<mEventos.size()) {
									if (!mEventos.getElementAt(i).getFichero().getCodTablet().equals(miTablet))
										mEventos.removeElementAt(i);
									else
										i++;
								}
							}
						});
					}
				}
			});
		}

	// Gesti�n de b�squeda de texto en ficheros log ya identificados 
		
	private boolean sigueHiloBusqLog; // Variable l�gica para acabar el hilo de b�squeda en logs si procede
	
	private void buscarTexto( final String texto ) {
		bBuscarTexto.setEnabled( false );  // Desactiva el bot�n de b�squeda hasta que se acabe la b�squeda
		sigueHiloBusqLog = true;  // Marca el inicio del funcionamiento del hilo de b�squeda
		(new Thread() {
			@Override
			public void run() {
				taVer.setText( "" );
				for (int i=0; i<mFicheros.size(); i++) {
					if (!sigueHiloBusqLog) return; // Corta el hilo si procede
					FicheroLog fl = mFicheros.getElementAt(i);
					taVer.append( "Buscando texto " + texto + " en fichero " + fl + "\n" );
					buscarTextoEnFic( fl, texto.toUpperCase() );
				}
				lMensaje.setText( "Fin de b�squeda de texto." );
				bBuscarTexto.setEnabled( true ); // Reactiva el bot�n de b�squeda
			}
		}).start();
	}

		// Busca el texto indicado en el fichero de texto f
		private void buscarTextoEnFic( File f, String texto ) {
			try {
				Scanner scanner = new Scanner( f );
				boolean encontrado = false;
				int numLinea = 0;
				while (scanner.hasNextLine()) {
					String linea = scanner.nextLine();
					numLinea++;
					if (linea.toUpperCase().contains( texto )) {
						if (!encontrado) {
							encontrado = true;
						}
						taVer.append( "    L�nea " + numLinea + ": " + linea + "\n" );
					}
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog( null, "Error en lectura de fichero " + f.getAbsolutePath(), "�Atenci�n!", JOptionPane.ERROR_MESSAGE );
			}
		}

	// Gesti�n de b�squeda de eventos en ficheros logs
		
	private boolean sigueHiloEventosLog; // Variable l�gica para acabar el hilo de b�squeda de eventos si procede
	
	private void buscarEventos() {
		bBuscarEventos.setEnabled( false );  // Desactiva el bot�n de b�squeda hasta que se acabe la b�squeda
		sigueHiloEventosLog = true;  // Marca el inicio del funcionamiento del hilo de b�squeda
		mEventos.clear(); // Borra los eventos para volver a empezar a buscarlos
		(new Thread() {
			@Override
			public void run() {
				for (int i=0; i<mFicheros.size(); i++) {
					if (!sigueHiloEventosLog) return; // Corta el hilo si procede
					FicheroLog fl = mFicheros.getElementAt(i);
					lMensaje.setText( "Buscando eventos en fichero " + fl + "\n" );
					buscarEventosEnFic( fl );
				}
				lMensaje.setText( "Fin de b�squeda de eventos." );
				bBuscarEventos.setEnabled( true ); // Reactiva el bot�n de b�squeda
			}
		}).start();
	}
	
		private void buscarEventosEnFic( FicheroLog fl ) {
			try {
				Scanner scanner = new Scanner( fl );
				int numLinea = 0;
				Evento eventoAnt = null;
				while (scanner.hasNextLine()) {
					String linea = scanner.nextLine();
					numLinea++;
					final Evento evento = Evento.detectaEventoEnLinea( eventoAnt, linea, numLinea, fl );
					if (evento!=null && evento!=eventoAnt) {
						SwingUtilities.invokeLater( new Runnable() {  // Modifica de forma segura un elemento de swing desde un hilo diferente
							@Override
							public void run() {
								mEventos.addElement( evento );
							}
						});
					}
					eventoAnt = evento;
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog( null, "Error en lectura de fichero " + fl.getAbsolutePath(), "�Atenci�n!", JOptionPane.ERROR_MESSAGE );
			}
		}

	// Saca el texto del fichero a la ventana
	private void sacaFicAVentana( FicheroLog fl ) {
		try {
			taVer.setText( "" );
			Scanner scanner = new Scanner( fl );
			while (scanner.hasNextLine()) {
				String linea = scanner.nextLine();
				taVer.append( linea + "\n" );
			}
			taVer.setSelectionStart(0); taVer.setSelectionEnd(0);  // Marca el principio para que la textarea se posicione al inicio en la ventana
			scanner.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog( null, "Error en lectura de fichero " + fl.getAbsolutePath(), "�Atenci�n!", JOptionPane.ERROR_MESSAGE );
		}
	}

	
	// RECURSIVIDAD 
	// CB 
	//
	//if sig evento >= num eventos en lista 
	
	//CR
	// Recorra 
	
	
	
	
	private void recEventos(DefaultListModel<Evento> mEventos, ArrayList<String> lEventos, ArrayList<Integer> lLineas, int sigEvento) {
		
		
		if(sigEvento >= mEventos.size()) return; 
		Evento evento = mEventos.getElementAt(sigEvento); 
		
		if (lEventos.isEmpty() || evento.getLinea()<lLineas.get(lLineas.size()-1)) {
			// Si la lista de eventos está vacia o el fichero no está escrito aún 
			lEventos.clear(); 
			lLineas.clear();
			 
		}
		// añade el evento a la lista
		lEventos.add(evento.getClass().getSimpleName()); 
		// Añade linea a lista
		lLineas.add(evento.getLinea()); 
		
		// Reducir la lista si hay más de 20 lineas de distancia
		
		while((lLineas.get(lLineas.size()-1) - lLineas.get(0)) > 20) {
			// si la posición final - la inicial es > 20 quita elementos de la lista 
			// para reducir borramos los elementos inicales 
			
			lEventos.remove(0); 
			lLineas.remove(0); 
		}
		
		recEventos(mEventos, lEventos,lLineas, sigEvento+1);
		// Falta meterlo en el MAPA 
		// 
		

		
		
		
		
			
			
		}

	
	
		
	
	
	
	
	
		
	// Otros
		
	// Carga configuraci�n de la ventana
	private void cargarConfig() {
		StringTokenizer st = new StringTokenizer( Config.getProperty( Config.PROP_TEXTO_BUSQ ), "#$#" );
		mTextoABuscar.removeAllElements();
		while (st.hasMoreTokens()) {
			mTextoABuscar.addElement( st.nextToken() );
		}
		tfCarpeta.setText( Config.getProperty( Config.PROP_CARPETA ) );
		tfExtensiones.setText( Config.getProperty( Config.PROP_EXT ) );
		try {
			setLocation( Integer.parseInt(Config.getProperty( Config.PROP_X_VENT )), Integer.parseInt(Config.getProperty( Config.PROP_Y_VENT )) );
			setSize( Integer.parseInt(Config.getProperty( Config.PROP_ANCHO_VENT )), Integer.parseInt(Config.getProperty( Config.PROP_ALTO_VENT )) );
		} catch (Exception e) {}
	}
	
}
