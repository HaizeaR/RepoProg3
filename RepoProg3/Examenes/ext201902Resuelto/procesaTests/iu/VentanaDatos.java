package ext201902Resuelto.procesaTests.iu;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import ext201902Resuelto.procesaTests.datos.Tabla;

/** Clase de ventana para muestra de datos
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
@SuppressWarnings("serial")
public class VentanaDatos extends JInternalFrame {
	
	private JTable tDatos;    // JTable de datos de la ventana
	private Tabla tablaDatos; // Tabla de datos de la ventana
	private JScrollPane spDatos; // Scrollpane de la jtable
	private JLabel lMensaje;  // Label de mensaje
	private JPanel pBotonera; // Panel de botones
	private VentanaGeneral ventMadre;  // Ventana madre

	private EventoEnCelda dobleClick;
	private EventoEnCelda enter;
	
	/** Añade un botón a la ventana
	 * @param texto	Texto del botón
	 * @param runnable	Objeto runnable con código a ejecutar (run()) cuando el botón se pulse
	 */
	public void addBoton( String texto, Runnable runnable ) {
		JButton b = new JButton( texto );
		pBotonera.add( b );
		b.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				runnable.run();
			}
		});
	}
	
	public JTable getJTable() { return tDatos; }
	
	/** Crea una nueva ventana
	 */
	public VentanaDatos( VentanaGeneral ventMadre, String titulo ) {
	    super( titulo, true, true, true, true ); //  resizable, closable, maximizable, iconifiable
	    this.ventMadre = ventMadre;
		// Configuración general
		setTitle( titulo );
		setSize( 800, 600 ); // Tamaño por defecto
		setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE );
		// Creación de componentes y contenedores
		pBotonera = new JPanel();
		tDatos = new JTable();
		lMensaje = new JLabel( " " );
		// Asignación de componentes
		spDatos = new JScrollPane( tDatos );
		getContentPane().add( spDatos, BorderLayout.CENTER );
		getContentPane().add( pBotonera, BorderLayout.SOUTH );
		getContentPane().add( lMensaje, BorderLayout.NORTH );
		// Eventos
		tDatos.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()>=2) {
					int fila = tDatos.rowAtPoint( e.getPoint() );
					int columna = tDatos.columnAtPoint( e.getPoint() );
					if (dobleClick!=null) dobleClick.evento( fila, columna );
				}
			}
		});
		tDatos.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int fila = tDatos.rowAtPoint( e.getPoint() );
				int columna = tDatos.columnAtPoint( e.getPoint() );
				if (fila>=0 && columna>=0) {
					Object valor = tDatos.getValueAt( fila, columna );
					if (valor!=null && ventMadre!=null) ventMadre.setMensaje( valor.toString() );
				}
			}
		});
		tDatos.getTableHeader().addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int columna = tDatos.columnAtPoint( e.getPoint() );
				if (columna>=0) {
					Object valor = tDatos.getTableHeader().getColumnModel().getColumn(columna).getHeaderValue().toString();
					if (valor!=null && ventMadre!=null) ventMadre.setMensaje( valor.toString() );
				}
			}
		});
		tDatos.addKeyListener( new KeyAdapter() {
			boolean ctrlPulsado = false;
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (enter!=null && tDatos!=null) {
						enter.evento( tDatos.getSelectedRow(), tDatos.getSelectedColumn() );
						e.consume();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_CONTROL ) {
					ctrlPulsado = true;
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_CONTROL ) {
					ctrlPulsado = false;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN ) {
					if (ctrlPulsado) {
						// Buscar siguiente centro vacío
						int colCodCentro = getColumnWithHeader( "CodCentro", false );
						if (colCodCentro==-1) colCodCentro = getColumnWithHeader( "COD", true );
						if (colCodCentro!=-1) {
							int row = tDatos.getSelectedRow();
							if (row>=0) {
								row++;
								while (row<tDatos.getModel().getRowCount()) {
									if (tDatos.getValueAt( row, colCodCentro ).toString().isEmpty()) {
										tDatos.getSelectionModel().setSelectionInterval( row, row );
										Rectangle rect = tDatos.getCellRect( row, colCodCentro, true );
										// Point pt = spDatos.getViewport().getViewPosition();
										rect.setLocation(rect.x, rect.y+25);  // Y un poquito más abajo
										tDatos.scrollRectToVisible( rect );
										return;
									}
									row++;
								}
							}
						}
					}
				}
			}
		} );
		// Cierre
		// setLocationRelativeTo( null );  // Centra la ventana en el escritorio  (solo se puede con JFrame)
		/* Renderer posible
		tDatos.setDefaultRenderer( Object.class, new DefaultTableCellRenderer() {
			private JProgressBar pb = new JProgressBar( 0, 500 );
			private JLabel lVacia = new JLabel( "" );
			private JLabel lError = new JLabel( "ERROR" );
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				if (column==2 || column==3) {
					try {
						if (value.toString().isEmpty()) return lVacia;
						double val = Double.parseDouble( value.toString() ); 
						pb.setValue( (int) val*100 );
						return pb;
					} catch (Exception e) {}
					return new JLabel( "Error" );
				} else {
					Component comp = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
					if (value instanceof String) {
						String string = (String) value;
						comp.setBackground( Color.white );
						comp.setForeground( Color.black );
						((JLabel)comp).setHorizontalAlignment( JLabel.LEFT );
						if ("0".equals(string)) {
							comp.setForeground( Color.LIGHT_GRAY ); 
							((JLabel)comp).setHorizontalAlignment( JLabel.CENTER );
						}
						if (column==1 || column>=4 && column<=9) {
							((JLabel)comp).setHorizontalAlignment( JLabel.CENTER );
						}
					}
					return comp;
				}
			}
		} );
		tDatos.addMouseMotionListener( new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int fila = tDatos.rowAtPoint( e.getPoint() );
				int columna = tDatos.columnAtPoint( e.getPoint() );
				if (columna==2 || columna==3) {
					if (tDatos.getModel().getValueAt( fila, columna ).toString().isEmpty()) {
						lMensaje.setText( " " );
					} else {
						lMensaje.setText( "Valor de satisfacción: " + tDatos.getModel().getValueAt( fila, columna )  );
					}
				} else {
					lMensaje.setText( " " );
				}
			}
		});
		tDatos.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount()>=2) { // Doble click
					int fila = tDatos.rowAtPoint( e.getPoint() );
					int columna = tDatos.columnAtPoint( e.getPoint() );
					if (columna==0) {
						String centro = tDatos.getModel().getValueAt( fila, columna ).toString();
						CentroEd centroEd = Datos.centros.get( centro );
						JOptionPane.showMessageDialog( VentanaDatos.this, "Centro " + centro + "\nAbreviaturas: " + centroEd.getlAbrevs(), "Información de centro", JOptionPane.INFORMATION_MESSAGE );
						centroEd.getlAbrevs();
					} else {
						lMensaje.setText( " " );
					}
				}
			}
		});
		*/
	}
	
	public void setMensaje( String mens ) {
		if (mens==null || mens.isEmpty()) mens = " ";
		lMensaje.setText( mens );
	}
	
	/** Asigna una tabla de datos a la JTable principal de la ventana
	 * @param tabla	Tabla de datos a visualizar
	 */
	public void setTabla( Tabla tabla ) {
		tablaDatos = tabla;
		tDatos.setModel( tabla.getTableModel() );
	}
	
	/** Devuelve la tabla de datos asignada a la ventana
	 * @return	tabla de datos asignada, null si no la hay
	 */
	public Tabla getTabla() {
		return tablaDatos;
	}
		
	/** Oculta las columnas indicadas en la visual
	 * @param colD	columna inicial (0 a n-1)
	 * @param colH	columna final (0 a n-1)
	 */
	public void ocultaColumnas( final int colD, final int colH ) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				for (int i=colH; i>=colD; i--)
					tDatos.removeColumn(tDatos.getColumnModel().getColumn( i ));
			}
		};
		if (SwingUtilities.isEventDispatchThread()) r.run(); else SwingUtilities.invokeLater( r );
	}
	
	public void setDobleClickCelda( EventoEnCelda evento ) {
		dobleClick = evento;
	}
	
	public void setEnterCelda( EventoEnCelda evento ) {
		enter = evento;
	}
	
	public int getColumnWithHeader( String nomCol, boolean nomExacto ) {
		TableColumnModel cols = tDatos.getTableHeader().getColumnModel();
		for (int col = 0; col<cols.getColumnCount(); col++) {
			String nom = cols.getColumn(col).getHeaderValue() + "";
			if (nomExacto && nom.equals( nomCol )) return col;
			if (!nomExacto && nom.toUpperCase().startsWith( nomCol.toUpperCase() ) ) return col;
		}
		return -1;
	}
	
	public interface EventoEnCelda {
		public void evento( int fila, int columna );
	}

	public void setRendererSiVacio( final int col, final Color back ) {
		tDatos.setDefaultRenderer( Object.class, new DefaultTableCellRenderer() {
			private int miCol = col;
			private Color miBack = back;
			private Color miBack2; 
			{	int r = back.getRed() + 160; int g = back.getGreen() + 160; int b = back.getBlue() + 160;
				if (r>255) r = 255; if (g>255) g = 255; if (b>255) b = 255;
				miBack2 = new Color( r, g, b );
			}
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );
				comp.setBackground( Color.white );
				if (column==miCol) {
					if (value.toString().isEmpty()) {
						if (isSelected)
							comp.setBackground( miBack2 );
						else
							comp.setBackground( miBack );
					}
				}
				return comp;
			}
		} );
	}
	
}
