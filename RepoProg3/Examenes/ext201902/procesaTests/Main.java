package ext201902.procesaTests;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import ext201902.procesaTests.datos.*;
import ext201902.procesaTests.iu.*;
import ext201902.utils.*;
	
	
/** Clase principal de revisión de tests de pensamiento computacional
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Main {

	public static void main( String[] s ) {
		principalPC();
	}
	
		/** Método principal de carga de datos de los tests y generación del emparejamiento entre pre y post
		 */
		@SuppressWarnings("unchecked")
		private static void principalPC() {
			try {
				// T3 BD ya inicializada (no hace falta cambiar nada aquí)
				Datos.conn = BD.initBD( "testsPC.bd" );
				Datos.stat = BD.usarCrearTablasBD( Datos.conn );
				Datos.ventana = new VentanaGeneral();
				Datos.ventana.setTitle( "Gestión de test de pensamiento computacional" );
				Datos.ventana.setVisible( true );
				Datos.listaPres = new ArrayList<>();
				int numErrores = Tabla.processCSV( Main.class.getResource( "testsPre.csv" ), Datos.listaPres, TestPC.class );
				Datos.listaPosts = new ArrayList<>();
				numErrores += Tabla.processCSV( Main.class.getResource( "testsPost.csv" ), Datos.listaPosts, TestPC.class );
				if (numErrores > 0) {
					System.err.println( "Hay " + numErrores + " errores de carga en las urls intentadas." );
				}
				if (Datos.listaPosts.size()==0) {
					System.err.println( "No se ha podido cargar ningún valor de tests pre" );
				} else {
					if (Datos.listaPosts.size()==0) {
						System.err.println( "No se ha podido cargar ningún valor de tests post" );
					} else {
						corregirClavesTemporales( Datos.listaPres, Datos.listaPosts );  // Corregir claves para que la marca temporal de cada test sea clave única
						reducirCodigosCentros( Datos.listaPres, Datos.listaPosts );  // Recodificar los centros 
						Datos.mapaPrePost = new HashMap<>();   // Crear y cargar el mapa de todos los tests
						for (TestPC test : Datos.listaPres) Datos.mapaPrePost.put( test.getMarcaTemporal(), test );
						for (TestPC test : Datos.listaPosts) Datos.mapaPrePost.put( test.getMarcaTemporal(), test );
						// Cargar datos en ventanas
						Datos.tablaPCPre = newVentanaDatosPC( Datos.ventana, Datos.listaPres, "Pre", 0, 0 );
						Datos.tablaPCPost = newVentanaDatosPC( Datos.ventana, Datos.listaPosts, "Post", 20, 20 );
						// Calcular enlaces entre pres y posts
						calculaEnlaces();
						// Botón de ordenar por resultados
						Datos.ventana.addMenuAccion( "Ordenar por resultados", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								ordenarPorResultados();
							}
						});
						// Botón de detectar copias
						Datos.ventana.addMenuAccion( "Detectar copias", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								detectarCopias();
							}
						});
						// Botón de combinaciones
						Datos.ventana.addMenuAccion( "Combinaciones de copias", new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								ArrayList<TestPC> aCombinar = new ArrayList<>();
								aCombinar.add( buscaTest( Datos.listaPres, "dq2j" ) );
								aCombinar.add( buscaTest( Datos.listaPres, "g910" ) );
								aCombinar.add( buscaTest( Datos.listaPres, "grjy" ) );
								aCombinar.add( buscaTest( Datos.listaPres, "dkw" ) );
								aCombinar.add( buscaTest( Datos.listaPres, "eyk0" ) );
								aCombinar.add( buscaTest( Datos.listaPres, "d2z2" ) );
								combinacionesCopias( aCombinar );
							}
							private TestPC buscaTest( ArrayList<TestPC> lista, String codEst ) {
								for (TestPC test : lista) if (test.getCodEstud().equals( codEst )) return test;
								return null;
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
				
			// -------------------------------------------------------------------
			// Métodos de tests de PC
			// -------------------------------------------------------------------

			// Método de recodificación de nombres de centro con similitudes de string (por ejemplo para que nombres como Zunzunegui, zununegui, zunzunegi... se reduzcan a un solo ZUNZUNEGUI)

				private static boolean VER_INFO_CAMBIOS = true; // Ver en consola los cambios de nombres que se hacen
				private static double SIM_MINIMA = 0.59;  // Similitud mínima para entender que dos nombres de centro son el mismo
			@SuppressWarnings("unchecked")
			/** Reduce los códigos de centros suponiendo que hay errores ortográficos que los hacen muchos diferentes cuando realmente son unos pocos
			 * @param listas	Listas tests que incluyen nombres de centros. Se modifican sustituyendo esos nombres por nombres similares, respetando los que más veces aparecen
			 */
			public static void reducirCodigosCentros( ArrayList<TestPC>... listas ) {
				// 1.- Metemos en un arraylist todos los centros
				ArrayList<String> todos = new ArrayList<>();
				for (ArrayList<TestPC> lista : listas) {
					for (TestPC test : lista) {
						todos.add( test.getNomCentro() );
					}
				}
				// 2.- Los reducimos con una rutina externa (reducirStringsSimilares)
				reducirStringsSimilares( todos );
				// 3.- Los sustituimos por los que había en el mismo orden
				int centro = 0;
				for (ArrayList<TestPC> lista : listas) {
					for (TestPC test : lista) {
						test.setNomCentro( todos.get(centro) );
						centro++;
					}
				}
			}
			
			/** Sustituye una lista de strings por la misma lista con los textos sustituidos por los que más veces ocurren dentro de un rango de similitud 
			 * @param lista	Lista de strings que se recibe y se devuelve modificada
			 */
			public static void reducirStringsSimilares( ArrayList<String> lista ) {
				// Fase 1 - calculamos cuántos strings distintos hay y contamos cada uno de ellos
				HashMap<String,Contador> mapaStrings = new HashMap<>(); // Mapa de nombres de centro - contador de ocurrencias
				for (String string : lista) {
					string = string.toUpperCase().trim();  // Simplificamos el nombre a mayúsculas y quitando espacios
					Contador cont = mapaStrings.get( string );
					if (cont==null) {
						Contador contIni = new Contador(1);
						mapaStrings.put( string, contIni );
					} else {
						cont.inc();
					}
				}
				// Fase 2 - Creamos una lista con los strings y sus contadores, y la ordenamos descendentemente por conteo
				ArrayList<CentroContador> listaOrd = new ArrayList<>();
				for (String string : mapaStrings.keySet()) {
					Contador cont = mapaStrings.get( string );
					listaOrd.add( new CentroContador( string, cont ) );
				}
				listaOrd.sort( new Comparator<CentroContador>() {
					@Override
					public int compare(CentroContador o1, CentroContador o2) {
						int comp = o2.cont.get() - o1.cont.get();
						if (comp==0) comp = o1.centro.compareTo( o2.centro );
						return comp;
					}
				});
				System.out.println( listaOrd );
				// Fase 3 - Recorremos la lista buscando las mejores similitudes y las marcamos (por encima de un umbral de similitud de strings)
				int i=0;
				while (i<listaOrd.size()) {
					CentroContador cc = listaOrd.get(i);
					CentroContador mejorSimilitud = null;
					double mayorSimilitud = 0.0;
					int posiMejor = -1;
					for (int j=i+1; j<listaOrd.size(); j++) {
						CentroContador cc2 = listaOrd.get(j);
						double similitud = ParecidoStrings.similitud( cc.centro, cc2.centro );
						if (similitud > SIM_MINIMA && similitud > mayorSimilitud) {
							mayorSimilitud = similitud;
							mejorSimilitud = cc2;
							posiMejor = j;
						}
						if (cc.lSust.size()>0) {
							for (CentroContador ccS : cc.lSust) {
								double simil2 = ParecidoStrings.similitud( ccS.centro, cc2.centro );
								if (simil2 > SIM_MINIMA && simil2 > mayorSimilitud) {
									mayorSimilitud = simil2;
									mejorSimilitud = cc2;
									posiMejor = j;
								}
							}
						}
					}
					if (mejorSimilitud!=null) {  // Se ha encontrado una buena similitud: se sustituye y se intercambia en la lista
						cc.lSust.add( mejorSimilitud );
						listaOrd.remove( posiMejor );
						// System.out.println( "Mejor similitud entre " + cc.centro + " y " + mejorSimilitud.centro + " = " + mayorSimilitud );
					} else {
						i++;
					}
				}
				// Si se quieren visualizar las sustituciones
				for (CentroContador cc : listaOrd) {
					System.out.println( "Centro principal: " + cc );
					if (cc.lSust.size()>0) {
						for (CentroContador cc2 : cc.lSust) {
							System.out.println( "  Sustituido al principal: " + cc2 );
						}
					}
				}
				// Fase 4 - Creamos un mapa de sustituciones
				HashMap<String,String> mapaCambios = new HashMap<>();
				for (CentroContador cc : listaOrd) {
					if (VER_INFO_CAMBIOS) System.out.println( "Centro principal: " + cc );
					if (cc.lSust.size()>0) {
						for (CentroContador cc2 : cc.lSust) {
							if (VER_INFO_CAMBIOS) System.out.println( "  Se sustituye por ese principal: " + cc2 );
							mapaCambios.put( cc2.centro, cc.centro );
						}
					}
				}
				// Fase 5 - Cambiamos todos los elementos en la lista original según las sustituciones definidas
				for (int posi=0; posi<lista.size(); posi++) {
					String string = lista.get(posi).toUpperCase().trim();  // Quitamos espacios y ponemos mayúsculas
					String cambio = mapaCambios.get( string );
					if (cambio!=null) {
						lista.set( posi, cambio );  // Si hay sustitución se cambia
					} else {
						lista.set( posi,  string );  // Si no se mantiene, pero con mayúsculas y sin espacios
					}
				}
			}
				// Clase temporal usada para ordenar los nombres de centro
				private static class CentroContador {
					String centro;
					Contador cont;
					ArrayList<CentroContador> lSust = new ArrayList<>(); // Centros que se pueden sustituir por este (suficiente similitud)
					CentroContador( String centro, Contador cont ) { this.centro = centro; this.cont = cont; }
					@Override
					public String toString() { return centro + " {" + cont + " veces}"; }
				}
		
			// Corrige claves de tiempo (dd/mm/aaaa hh:mm:ss) si hay repetidos para que funcionen como claves únicas
			@SuppressWarnings("unchecked")
			private static void corregirClavesTemporales( ArrayList<TestPC>... listas ) {
				HashSet<String> claves = new HashSet<>();
				for (ArrayList<TestPC> lista : listas) {
					for (TestPC test : lista) {
						if (claves.contains(test.getMarcaTemporal())) {  // Ya existe la marca temporal
							String nuevaMarca = test.getMarcaTemporal();
							int rep=0;
							do {
								rep++;
								nuevaMarca = test.getMarcaTemporal() + "$" + rep;
							} while (claves.contains(nuevaMarca));
							test.setMarcaTemporal( nuevaMarca );
						}
						claves.add( test.getMarcaTemporal() );
					}
				}
				claves.clear();
			}
			
			// Calcula enlaces de pres y post (una vez cargadas las listas de pres y posts)
			// Crea el mapa y la lista de enlaces de tests principal partiendo de la base de datos y las tablas de tests ya cargadas, y saca la ventana correspondiente
			@SuppressWarnings("serial")
			private static void calculaEnlaces() {
				// Vaciamos las estructuras
				Datos.mapaEnlaces = new HashMap<>();
				HashSet<String> enlacesYaEnBD = new HashSet<>();  // Estructura temporal para saber qué tests ya están enlazados
				int ultimoNumEnl = 0; // Info del último código (número) de enlace de cada centro
				// 1.- Cargamos lo ya gestionado en la base de datos
				// T3 - Cargar los enlaces desde la base de datos al mapa de enlaces y añadir marcas temporales al mapa de enlaces en BD para no meterlos después dos veces (ver líneas (*))
				ArrayList<EnlacePrePost> enlacesBD = BD.enlacePrePostSelect( Datos.stat, Datos.mapaPrePost );
				for (EnlacePrePost enlace : enlacesBD) {
					Datos.mapaEnlaces.put( enlace.getNumEnlace(), enlace );
					if (enlace.getNumEnlace()>ultimoNumEnl) ultimoNumEnl = enlace.getNumEnlace();
					if (enlace.getTestPre()!=null) enlacesYaEnBD.add( enlace.getTestPre().getMarcaTemporal() );
					if (enlace.getTestPost()!=null) enlacesYaEnBD.add( enlace.getTestPost().getMarcaTemporal() );
				}
				
				// 2.- Cargamos todos los demás datos de las tablas de tests por si hay datos que no estén en base de datos
				// Metemos en las tablas solo si no está ya
				for (TestPC test : Datos.listaPres) {
					if (!enlacesYaEnBD.contains( test.getMarcaTemporal() )) {  // (*) Si ya se ha cargado este enlace de BD no se incorpora 
						ultimoNumEnl++;
						EnlacePrePost nuevoEnlace = new EnlacePrePost( ultimoNumEnl, test.getNomCentro() );
						nuevoEnlace.setTestPre( test );
						Datos.mapaEnlaces.put( nuevoEnlace.getNumEnlace(), nuevoEnlace );
					}
				}
				// Lo mismo con los posts
				for (TestPC test : Datos.listaPosts) {
					if (!enlacesYaEnBD.contains( test.getMarcaTemporal() )) {  // (*) Si ya se ha cargado este enlace de BD no se incorpora
						ultimoNumEnl++;
						EnlacePrePost nuevoEnlace = new EnlacePrePost( ultimoNumEnl, test.getNomCentro() );
						nuevoEnlace.setTestPost( test );
						Datos.mapaEnlaces.put( nuevoEnlace.getNumEnlace(), nuevoEnlace );
					}
				}

				// 3.- Creamos la lista de todos los enlaces y la ordenamos por código de estudiante
				Datos.listaEnlaces = new ArrayList<>();
				for (EnlacePrePost enl : Datos.mapaEnlaces.values()) Datos.listaEnlaces.add( enl );
				Datos.listaEnlaces.sort( new Comparator<EnlacePrePost>() {
					@Override
					public int compare(EnlacePrePost o1, EnlacePrePost o2) {
						if (o1.getCodigoEst()==null) return -1;  // Si no tienen código de estudiante entonces al principio  (no debería ocurrir)
						return o1.getCodigoEst().trim().toUpperCase().compareTo(o2.getCodigoEst().trim().toUpperCase());
					}
				});

				// 4.- Juntar los códigos comunes consecutivos
				for (int linea = 1; linea<Datos.listaEnlaces.size(); linea++) {
					EnlacePrePost enlace1 = Datos.listaEnlaces.get( linea-1 );
					EnlacePrePost enlace2 = Datos.listaEnlaces.get( linea );
					String codEnl1 = enlace1.getCodigoEst();
					String codEnl2 = enlace2.getCodigoEst();
					if (codigosSimilares( codEnl1, codEnl2 ) ) {  // Son similares: ver si hay hueco para combinarlos
						if (enlace1.getTestPre()==null && enlace2.getTestPost()==null) {   // Caso 1: el primero tiene solo post y el segundo solo pre -> se mueve el pre al primer enlace
							enlace1.setTestPre( enlace2.getTestPre() );
							enlace2.setTestPre( null );
						} else if (enlace1.getTestPost()==null && enlace2.getTestPre()==null) {   // Caso 2: el primero tiene solo pre y el segundo solo post -> se mueve el post al primer enlace
							enlace1.setTestPost( enlace2.getTestPost() );
							enlace2.setTestPost( null );
						}
					}
				}
				
				// 5.- Quitar los enlaces vacíos
				for (int linea=Datos.listaEnlaces.size()-1; linea>=0; linea--) {
					EnlacePrePost epp = Datos.listaEnlaces.get( linea );
					if (epp.getCodigoEst()==null) Datos.listaEnlaces.remove( linea );
				}
				
				// 6.- Gestionar ventana de enlaces de test - asociarla a la tabla de datos
				Datos.tablaEnlaces = Tabla.linkTablaToList( Datos.listaEnlaces );
				VentanaDatos vd = new VentanaDatos( Datos.ventana, "Pre-post (" + Datos.tablaEnlaces.size() + ")" );
				vd.setTabla( Datos.tablaEnlaces ); 
				Datos.ventana.addVentanaInterna( vd, "Pre-post" );
				vd.setLocation( 40, 40 );
				vd.addBoton( "-> clipboard", new Main.CopyToClipboard( Datos.tablaEnlaces, vd ) );
				vd.addBoton( "guardar BD", new Main.GuardarEnlBD( Datos.listaEnlaces, vd ) );
				Datos.tablaEnlaces.calcTypes();
				
				// 7.- Formato visual de tabla de enlaces
				int[] anchuras = { 100, 300, 200, 200, 120, 300, 150, 200, 200, 120, 300, 150 };
				for (int numCol=0; numCol<vd.getJTable().getColumnCount(); numCol++) {
					TableColumn tc = vd.getJTable().getColumnModel().getColumn( numCol );
					tc.setPreferredWidth( anchuras[ numCol ] );
				}
				vd.getJTable().setDefaultRenderer( String.class, new DefaultTableCellRenderer() {
					private boolean[] centrado = { true, false, false, false, true, false, true, false, false, true, false, true }; // Columnas centradas y no centradas
					@Override
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
						Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						if (centrado[column])
							((JLabel)comp).setHorizontalAlignment( JLabel.CENTER );
						else
							((JLabel)comp).setHorizontalAlignment( JLabel.LEFT );
						// T5 Renderer para parte 1 de la tarea 5
						((JLabel)comp).setBackground( Color.WHITE );
						EnlacePrePost enl = Datos.listaEnlaces.get( row );
						if ((column==5 || column==10) && enl.getTestPre()!=null && enl.getTestPost()!=null) {
							if (!enl.getTestPre().getNomCentro().equals(enl.getTestPost().getNomCentro())) {
								((JLabel)comp).setBackground( Color.RED );
							}
						}
						return comp;
					}
				});
				
				// 8.- Eventos específicos de tabla de enlaces
				// T5 Gestión de teclado para parte 2 de la tarea 5
				vd.getJTable().addKeyListener( new KeyAdapter() {
					boolean ctrlPulsado = false;
					int filaCortada = -1;
					int colCortada = -1;
					@Override
					public void keyPressed(KeyEvent e) {
						if (e.getKeyCode()==KeyEvent.VK_CONTROL) ctrlPulsado = true;
						else if (e.getKeyCode()==KeyEvent.VK_X && ctrlPulsado) {  // Ctrl+X
							int colSel = vd.getJTable().getSelectedColumn();
							int filaSel = vd.getJTable().getSelectedRow();
							if (colSel==2 || colSel==7) {
								filaCortada = filaSel;
								colCortada = colSel;
							}
						} else if (e.getKeyCode()==KeyEvent.VK_V && ctrlPulsado) {  // Ctrl+V
							if (filaCortada>=0 && colCortada>=0) {
								int colSel = vd.getJTable().getSelectedColumn();
								int filaSel = vd.getJTable().getSelectedRow();
								if (filaSel!=filaCortada && colSel==colCortada) {
									EnlacePrePost pOrigen = Datos.listaEnlaces.get( filaCortada );
									EnlacePrePost pDestino = Datos.listaEnlaces.get( filaSel );
									if (colSel==2) {  // Pre
										if (pDestino.getTestPre()==null && pOrigen.getTestPre()!=null) {  // Test en origen y nada en destino: se mueve
											pDestino.setTestPre( pOrigen.getTestPre() );
											pOrigen.setTestPre( null );
											cambioEnEnlaces( filaSel, filaCortada, 2, 6 );
											vd.setMensaje( "Movimiento de test pre con código " + pDestino.getTestPre().getCodEstud() );
										}
									} else if (colSel==7) {  // Pos
										if (pDestino.getTestPost()==null && pOrigen.getTestPost()!=null) {  // Test en origen y nada en destino: se mueve
											pDestino.setTestPost( pOrigen.getTestPost() );
											pOrigen.setTestPost( null );
											cambioEnEnlaces( filaSel, filaCortada, 7, 11 );
											vd.setMensaje( "Movimiento de test post con código " + pDestino.getTestPost().getCodEstud() );
										}
									}
								}
								filaCortada = -1;
								colCortada = -1;
							}
						}
					}
					@Override
					public void keyReleased(KeyEvent e) {
						if (e.getKeyCode()==KeyEvent.VK_CONTROL) ctrlPulsado = false;
					}
				});
				vd.getJTable().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
				vd.getJTable().addMouseListener( new MouseAdapter() {
					private Point pulsacion;
					@Override
					public void mousePressed(MouseEvent e) {
						pulsacion = e.getPoint();
					}
					@Override
					public void mouseReleased(MouseEvent e) {
						int filaP = vd.getJTable().rowAtPoint( pulsacion );
						int colP = vd.getJTable().columnAtPoint( pulsacion );
						int filaS = vd.getJTable().rowAtPoint( e.getPoint() );
						int colS = vd.getJTable().columnAtPoint( e.getPoint() );
						if (filaP>=0 && filaS>=0 && colP==colS && (colP==2 || colP==7) && filaP!=filaS) {  // Columna de código de pre o de post, distintas filas: pasar de una a otra si se puede
							EnlacePrePost pDestino = Datos.listaEnlaces.get( filaS );
							EnlacePrePost pOrigen = Datos.listaEnlaces.get( filaP );
							if (colP==2) {  // Pre
								if (pDestino.getTestPre()==null && pOrigen.getTestPre()!=null) {  // Test en origen y nada en destino: se mueve
									pDestino.setTestPre( pOrigen.getTestPre() );
									pOrigen.setTestPre( null );
									cambioEnEnlaces( filaP, filaS, 2, 6 );
									vd.setMensaje( "Movimiento de test pre con código " + pDestino.getTestPre().getCodEstud() );
								}
							} else if (colP==7) {  // Pos
								if (pDestino.getTestPost()==null && pOrigen.getTestPost()!=null) {  // Test en origen y nada en destino: se mueve
									pDestino.setTestPost( pOrigen.getTestPost() );
									pOrigen.setTestPost( null );
									cambioEnEnlaces( filaP, filaS, 7, 11 );
									vd.setMensaje( "Movimiento de test post con código " + pDestino.getTestPost().getCodEstud() );
								}
							}
						}
						// T5 Gestión del click derecho (parte 3)
						if (e.isPopupTrigger()) {
							filaPulsada = vd.getJTable().rowAtPoint( e.getPoint() );
							if (filaPulsada>=0) popup.show( vd.getJTable(), e.getX(), e.getY() );
						}
					}
				});
				vd.setVisible( true ); 
			}

				// T5 Añade el código que necesites aquí para la parte 3 de la tarea
				// (lo puedes hacer de otras maneras pero se recomienda hacer el popup mostrando con show(...) este componente ya definido)
				private static int filaPulsada = -1;
				private static JPopupMenu popup = null;
				static {
					popup = new JPopupMenu();
					JMenuItem mi = new JMenuItem( "Ver pre" ); 
					mi.addActionListener( new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							verPre();
						}
					} );
					popup.add( mi );
					mi = new JMenuItem( "Ver post" ); 
					mi.addActionListener( new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							verPost();
						}
					} );
					popup.add( mi );
				}
				private static void verPre() {
					// T5
					verTest( Datos.listaEnlaces.get(filaPulsada).getTestPre(), "pre" );
				}
				private static void verPost() {
					// T5
					verTest( Datos.listaEnlaces.get(filaPulsada).getTestPost(), "post" );
				}
				// T5
				private static void verTest( TestPC test, String tipo ) {
					String mens = "Test no existente";
					if (test!=null) {
						mens = test.getMarcaTemporal() + " - " + test.getCodEstud() + "\n";
						mens += (test.getNomCentro() + " - " + test.getCurso() + "\n");
						mens += ("Respuestas: " + test.getRespTest() + "\n");
						mens += ("Evaluación: " + test.getPuntuacion() + " (" + test.getEvalTest() + ")\n");
					}
					JOptionPane.showMessageDialog( Datos.ventana, mens, "Información de test " + tipo, JOptionPane.INFORMATION_MESSAGE );
				}

				// Se cambian las filas enl1 y enl2, desde las columnas col1 hasta la col2 (inclusive)
				private static void cambioEnEnlaces( int enl1, int enl2, int col1, int col2 ) {
					Datos.tablaEnlaces.cambioEnTabla( enl1, col1, enl1, col2 );
					Datos.tablaEnlaces.cambioEnTabla( enl2, col1, enl2, col2 );
					// T3 - Marcar los enlaces cambiados para que deban ser guardados en base de datos
					Datos.listaEnlaces.get( enl1 ).setGuardado( false );
					Datos.listaEnlaces.get( enl2 ).setGuardado( false );
				}
		
				private static boolean codigosSimilares( String cod1, String cod2 ) {
					if (cod1==null || cod2==null) return false;
					cod1 = cod1.trim(); cod2 = cod2.trim();
					if (cod1.toUpperCase().equals( cod2.toUpperCase() )) return true;
					String c1 = cod1.toUpperCase().replaceAll("0", "");
					String c2 = cod2.toUpperCase().replaceAll("0", "");
					if (c1.equals(c2)) return true;
					return false;
				}
					
			
			/** Crea una nueva tabla de datos, asociada a una nueva ventana interna de visualización de la tabla, sobre la lista de referencia indicada
			 * Asocia a la ventana un botón que lanza el proceso de copia
			 * @param vg	Ventana principal
			 * @param lista	Lista de datos de referencia para la nueva tabla
			 * @param codTabla	Código de texto que asociar a la tabla
			 * @param posX	Posición x de la ventana en la que localizar la nueva subventana
			 * @param posY	Posición y de la ventana en la que localizar la nueva subventana 
			 * @return	tabla creada
			 */
			private static Tabla newVentanaDatosPC( VentanaGeneral vg, ArrayList<? extends CargableDesdeCSV> lista, String codTabla, int posX, int posY ) {
				try {
					Tabla ret = Tabla.linkTablaToList( lista );
					String tit = codTabla + " (" + lista.size() + ")";
					VentanaDatos vd = new VentanaDatos( vg, tit ); 
					vd.setTabla( ret ); 
					vg.addVentanaInterna( vd, codTabla );
					vd.setLocation( posX, posY );
					vd.addBoton( "-> clipboard", new CopyToClipboard( ret, vd ) );
					vd.setVisible( true ); 
					try { Thread.sleep( 100 ); } catch (InterruptedException e) {}
					return ret;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}

			/** Clase interna cuyo método run, al ser ejecutado, copia el contenido de la tabla asociada al portapapeles
			 */
			public static class CopyToClipboard implements Runnable {
				private Tabla tabla;
				private VentanaDatos vd;
				/** Crea el objeto de copia al portapapeles (la copia se hará al llamar a su método run())
				 * @param tabla	Tabla de datos a la que asociar el objeto
				 * @param vd	Ventana de visualización asociada a la tabla
				 */
				public CopyToClipboard( Tabla tabla, VentanaDatos vd ) {
					this.tabla = tabla;
					this.vd = vd;
				}
				@Override
				public void run() {
					String texto = "";
					for (int col=0; col<tabla.getWidth(); col++) texto = texto + tabla.getHeader( col ) + "\t";
					for (int fila=0; fila<tabla.size(); fila++) {
						texto += "\n";
						for (int col=0; col<tabla.getWidth(); col++) {
							if (tabla.getType(col) == Double.class) {
								texto = texto + tabla.get( fila, col ).replaceAll("\\.",",") + "\t";
							} else {
								texto = texto + tabla.get( fila, col ) + "\t";
							}
						}
					}
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new StringSelection(texto),null);				
					vd.setMensaje( "Contenido de tabla copiado al portapapeles." );
				}
			}
			
			/** Clase interna cuyo método run, al ser ejecutado, guarda la lista de enlaces en base de datos
			 */
			public static class GuardarEnlBD implements Runnable {
				private ArrayList<EnlacePrePost> lista;
				private VentanaDatos vd;
				public GuardarEnlBD( ArrayList<EnlacePrePost> lista, VentanaDatos vd ) {
					this.lista = lista;
					this.vd = vd;
				}
				@Override
				public void run() {
					// T3 - Guardar los enlaces en BD si no están ya guardados (borrar, insertar, modificar)
					for (EnlacePrePost enl : lista) { // Recorrer lsita de enlaces 
						// si enlace no está guardado 
						if (!enl.isGuardado()) {  // Solo se considera si no está guardado ya en BD
							boolean ok = false;
							
							// comprobar si NO tiene pre y post 
							if (enl.getTestPre()==null && enl.getTestPost()==null) {  // Si no tiene enlaces borrarlo de la bd (si existe)
								// en el caso de no tener borrar 
								ok = BD.enlacePrePostDelete( Datos.stat, enl.getNumEnlace() );
							
							// Si tiene enlace PRE-POST 

							} else {  // Si tiene enlaces insertarlo o actualizarlo

							
								if (BD.enlacePrePostSelect( Datos.stat, enl.getNumEnlace(), Datos.mapaPrePost )==null) {  // Si no existe
									// insertarlo si no estaba
									ok = BD.enlacePrePostInsert( Datos.stat, enl );
								} else {
									// modificarlo si ya estaba 
									ok = BD.enlacePrePostUpdate( Datos.stat, enl );
								}
							}
							// guardar 
							if (ok) enl.setGuardado( true );
						}
					}
					vd.setMensaje( "Contenido de tabla guardado en base de datos." );
				}
			}
			
		private static void ordenarPorResultados() {
			// T2
			// T2 1. Preparamos la clase EnlacePrePost para que ordene por resultado (diferencia de puntuaciones)
			// T2 2. Cargamos el mapa de edades ordenada cada edad por resultado pre-post
			TreeMap<Integer,TreeSet<EnlacePrePost>> mapaEnlaces = new TreeMap<>();
			int cont = 0;
			for (EnlacePrePost enl : Datos.listaEnlaces) {
				if (enl.getTestPre()!=null && enl.getTestPost()!=null) {
					int edad = enl.getTestPre().getEdad();
					TreeSet<EnlacePrePost> arbol = mapaEnlaces.get( edad );
					if (arbol==null) {
						arbol = new TreeSet<>();
						mapaEnlaces.put( edad, arbol );
					}
					arbol.add( enl );
					cont++;
				}
			}
			System.out.println( cont );
			// T2 3. Recorremos el mapa para sacar a consola y cargar una nueva tabla en una ventana adicional
			ArrayList<EnlacePrePost> listaPrePost = new ArrayList<>();
			for (TreeSet<EnlacePrePost> set : mapaEnlaces.values()) {
				for (EnlacePrePost enl : set) {
					System.out.println( enl );
					listaPrePost.add( enl );
				}
				System.out.println();
			}
			Tabla tablaOrdenPrePost = Tabla.linkTablaToList( listaPrePost );
			VentanaDatos vd = new VentanaDatos( Datos.ventana, "Orden pre-post (" + listaPrePost.size() + ")" );
			vd.setTabla( tablaOrdenPrePost ); 
			Datos.ventana.addVentanaInterna( vd, "Orden pre-post" );
			vd.setLocation( 60, 60 );
			vd.setVisible( true );
		}
		
		// T4 Opción 1 - Detecta copias en pres
		private static void detectarCopias() {
			try {
				int numPersonas = Integer.parseInt( JOptionPane.showInputDialog( Datos.ventana, "Número de personas que crees que han copiado:", "6" ) );
				int pregInicial = Integer.parseInt( JOptionPane.showInputDialog( Datos.ventana, "Desde qué pregunta buscar la copia:", "1" ) );
				int pregFinal = Integer.parseInt( JOptionPane.showInputDialog( Datos.ventana, "Hasta qué pregunta buscar la copia:", "13" ) );
				ArrayList<TestPC> posiblesCopias = new ArrayList<>();
				detectarCopiasRec( Datos.listaPres, 0, "", posiblesCopias, numPersonas, pregInicial, pregFinal );
				for (TestPC test : posiblesCopias) {
					System.out.println( test.getCodEstud() + "\tRespuestas " + pregInicial + " a " + pregFinal + ":\t" + test.getRespTest().substring( pregInicial-1, pregFinal ) + "\tTodas las respuestas:\t" + test.getRespTest() );
				}
			} catch (Exception e) {}
		}
		
			/** Método recursivo para detectar y devolver una lista de tests que pueden ser copias (tienen las mismas respuestas)
			 * @param listaTests	Lista de tests a comprobar (NO CAMBIA EN LAS LLAMADAS RECURSIVAS)
			 * @param posDesde	Siguiente posición de comprobación en esa lista
			 * @param resps	Cadena de respuestas que coincide en las posibles copias (ejemplo: "BCDDCDABDCCDB" como respuestas 1 a 13 de "BCDDCDABDCCDBCDCCACABDACBDACCDDB" que serían todas, las 32)
			 * @param posiblesCopias	Lista de tests que se van encontrando con posibles copias (se devuelve finalmente con las copias encontradas o vacía si no se encuentran)
			 * @param numPMinimo	Número mínimo de personas cuyas respuestas deben coincidir para que se consideren posibles copias (NO CAMBIA EN LAS LLAMADAS RECURSIVAS)
			 * @param pregIni	Número de pregunta inicial a comprobar (de 1 a 32). (NO CAMBIA EN LAS LLAMADAS RECURSIVAS)
			 * @param pregFin	Número de pregunta final a comprobar (de 1 a 32). Debe ser mayor que pregIni (NO CAMBIA EN LAS LLAMADAS RECURSIVAS)
			 */
			private static void detectarCopiasRec( ArrayList<TestPC> listaTests, int posDesde, String resps, ArrayList<TestPC> posiblesCopias, int numPMinimo, int pregIni, int pregFin ) {
				if (numPMinimo==posiblesCopias.size()) return; // Caso base: se han encontrado personas suficientes
				if (posDesde >= listaTests.size()) { posiblesCopias.clear(); return; } // Caso base: se llega al final y no se ha encontrado
				String misResps = listaTests.get( posDesde ).getRespTest().substring( pregIni-1, pregFin );
				// System.out.println( "DCR " + posDesde + " - " + misResps + " -- " + posiblesCopias.size() );
				if (posiblesCopias.isEmpty()) {  // No hay todavía ninguna posible copia detectada: empezamos a probar con la persona actual
					posiblesCopias.add( listaTests.get( posDesde ) );
					detectarCopiasRec( listaTests, posDesde+1, misResps, posiblesCopias, numPMinimo, pregIni, pregFin );
					if (posiblesCopias.size()==numPMinimo) return; // Se han encontrado personas suficientes
					posiblesCopias.clear(); // No se han encontrado: probar si la copia está más adelante
					detectarCopiasRec( listaTests, posDesde+1, "", posiblesCopias, numPMinimo, pregIni, pregFin ); 
				} else {
					if (misResps.equals( resps )) {  // Mis respuestas son las mismas que las que se están valorando: incrementar la cuenta
						posiblesCopias.add( listaTests.get(posDesde) );
					}
					detectarCopiasRec( listaTests, posDesde+1, resps, posiblesCopias, numPMinimo, pregIni, pregFin );  // En cualquier caso, se devuelve recursivamente
				}
			}

		// T4 Opción 2 - Genera todas las combinaciones posibles de los tests recibidos
		private static void combinacionesCopias( ArrayList<TestPC> aCombinar ) {
			combinacionesCopiasRec( aCombinar, aCombinar.size(), new LinkedList<>() );
		}
		
			// T4 - posible forma de la rutina recursiva (puedes cambiarlo si prefieres hacerlo con otro formato de parámetros)
			/** Método recursivo para generar combinaciones sin repetición de una lista de *n* tests
			 * @param opciones	Opciones con las que se generan las combinaciones
			 * @param faltan	Opciones que faltan en la combinación actual
			 * @param combActual	Combinación actual
			 */
			private static void combinacionesCopiasRec( ArrayList<TestPC> opciones, int faltan, LinkedList<TestPC> combActual ) {
				if (faltan==0) {  // Caso base: se ha llegado a una combinación
					for (TestPC test : combActual ) System.out.print( test.getCodEstud() + " " );
					System.out.println();
				} else {
					for (TestPC test : opciones) {
						if (!combActual.contains( test )) {
							combActual.add( test );
							combinacionesCopiasRec( opciones, faltan-1, combActual );
							combActual.removeLast();
						}
					}
				}
			}

			
		
}