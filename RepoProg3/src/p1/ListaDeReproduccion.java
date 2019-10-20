package p1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.sun.istack.internal.logging.Logger;


/** Clase para crear instancias como listas de reproducción,
 * que permite almacenar listas de ficheros con posición de índice
 * (al estilo de un array / arraylist)
 * con marcas de error en los ficheros y con métodos para cambiar la posición
 * de los elementos en la lista, borrar elementos y añadir nuevos.
 */
public class ListaDeReproduccion implements ListModel<String> {
	
	ArrayList<File> ficherosLista;     // ficheros de la lista de reproducción
	int ficheroEnCurso = -1;           // Fichero seleccionado (-1 si no hay ninguno seleccionado)
	private static final boolean ANYADIR_A_FIC_LOG = true; // poner true para no sobreescribir
	
	private static Logger logger = Logger.getLogger( ListaDeReproduccion.class.getName(), ListaDeReproduccion.class);

	static{

		try {
			 logger = Logger.getLogger("ListaDeReptroduccion", null);
			
			Handler h = new FileHandler("ListaRepLogger.xml", true); 
			logger.setLevel(Level.FINEST);
			h.setLevel(Level.FINEST);
			// NO entiendo porque no funciona el addHandler pero como no 
			// me deja  no podmeos guardar los datos en el fichero 
			//logger.addHandler(h);
			


	} catch (SecurityException | IOException e) {
		logger.log( Level.SEVERE, "No se ha podido crear fichero de log en clase ListaDeReproduccion" );
	}
	logger.log( Level.INFO, "" );
	logger.log( Level.INFO, DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.LONG ).format( new Date() ) );
	
	}

	
	// Crear constructor 

	public ListaDeReproduccion() {
		ficherosLista = new ArrayList<File>();
	}

	//que intercambia las dos posiciones (no hace nada si
	// cualquiera de las posiciones es errónea).
	public void intercambia( int posi1, int posi2 ) {
		//falta perte de error 
		int pos2 = 0 ; 
		int pos1 = 0; 
		
	if (posi1 >= 0 && posi1 < ficherosLista.size()) {
		posi1 = pos1; 
	}else if(posi2>=0 && posi2 < ficherosLista.size()){
		 posi2 = pos2;
	}
	 posi1 = pos2; 
	 posi2 = pos1; 
	}
	
	public  int size() {
		return ficherosLista.size();
	}
	
	public void addFile(File f) {
		ficherosLista.add(f); 
	}
	
	 public void removeFic( int posi ) {
		 if (ficherosLista.size() != 0) {
			 ficherosLista.remove(posi);
		 }
	 }
	 public void clear(){
		 ficherosLista.clear();
	 }

	/** Devuelve uno de los ficheros de la lista
	 * @param posi	Posición del fichero en la lista (de 0 a size()-1)
	 * @return	Devuelve el fichero en esa posición
	 * @throws IndexOutOfBoundsException	Si el índice no es válido
	 */
	public File getFic( int posi ) throws IndexOutOfBoundsException {
		return ficherosLista.get( posi );
	}	



	/** Añade a la lista de reproducción todos los ficheros que haya en la 
	 * carpeta indicada, que cumplan el filtro indicado.
	 * Si hay cualquier error, la lista de reproducción queda solo con los ficheros
	 * que hayan podido ser cargados de forma correcta.
	 * @param carpetaFicheros	Path de la carpeta donde buscar los ficheros
	 * @param filtroFicheros	Filtro del formato que tienen que tener los nombres de
	 * 							los ficheros para ser cargados.
	 * 							String con cualquier letra o dígito. Si tiene un asterisco
	 * 							hace referencia a cualquier conjunto de letras o dígitos.
	 * 							Por ejemplo p*.* hace referencia a cualquier fichero de nombre
	 * 							que empiece por p y tenga cualquier extensión.
	 * @return	Número de ficheros que han sido añadidos a la lista
	 */
	public int add(String carpetaFicheros, String filtroFicheros) {
		// TODO: Codificar este método de acuerdo a la práctica (pasos 3 y sucesivos)
		// PASO 3 y 3B
		// Añadimos los logs en este método 
		
		int ficsAnyadidos = 0;
		
		if (carpetaFicheros!=null) {
			logger.log( Level.INFO, "Añadiendo ficheros con filtro " + filtroFicheros );
			try {
				
				filtroFicheros = filtroFicheros.replaceAll( "\\.", "\\\\." );  // Pone el símbolo de la expresión regular \. donde figure un .
				filtroFicheros = filtroFicheros.replaceAll( "\\*", ".*" ); 
				
				logger.log(Level.INFO, "Despues de filtro" + filtroFicheros);
				
				Pattern pFics = Pattern.compile( filtroFicheros, Pattern.CASE_INSENSITIVE );
				File fInic = new File(carpetaFicheros); 
				if (fInic.isDirectory()) {
					for( File f : fInic.listFiles() ) {
						logger.log( Level.FINE, "Procesando fichero " + f.getName() );
						if ( pFics.matcher(f.getName()).matches() ) {
							ficsAnyadidos++;
							logger.log( Level.INFO, "Añadido vídeo a lista de reproducción: " + f.getName() );
							addFile( f );
						}
					}
				}
			} catch (PatternSyntaxException e) {
				logger.log( Level.SEVERE, "Error en patrón de expresión regular ", e );
			}
		}
		logger.log( Level.INFO, "ficheros añadidos: " + ficsAnyadidos );
		return ficsAnyadidos;
	}


	
	
	//
	// Métodos de selección
	//
	
	/** Seleciona el primer fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAPrimero() {
		ficheroEnCurso = 0;  // Inicia
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}
	
	/** Seleciona el último fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAUltimo() {
		ficheroEnCurso = ficherosLista.size()-1;  // Inicia al final
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el anterior fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irAAnterior() {
		if (ficheroEnCurso>=0) ficheroEnCurso--;
		if (ficheroEnCurso==-1) {  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}

	/** Seleciona el siguiente fichero de la lista de reproducción
	 * @return	true si la selección es correcta, false si hay error y no se puede seleccionar
	 */
	public boolean irASiguiente() {
		ficheroEnCurso++;
		if (ficheroEnCurso>=ficherosLista.size()) {
			ficheroEnCurso = -1;  // Si no se encuentra, no hay selección
			return false;  // Y devuelve error
		}
		return true;
	}
	
	private static Random genAleat = new Random();
	/** Selecciona un fichero aleatorio de la lista de reproducción.
	 * @return true si la selección es correcta, false si hay error y no se puede seleccionar */
	public boolean irARandom() {
			if (ficherosLista.size()== 0) {
				ficheroEnCurso = -1; 
				return false; 
			}
			for(int i=0; i<500; i++) {
				ficheroEnCurso = genAleat.nextInt(ficherosLista.size()); 
				return true;
			}

		return false;

	}
	         

	/** Devuelve el fichero seleccionado de la lista
	 * @return	Posición del fichero seleccionado en la lista de reproducción (0 a n-1), -1 si no lo hay
	 */
	public int getFicSeleccionado() {
		return ficheroEnCurso;
	}

	//
	// Métodos de DefaultListModel
	//
	
	@Override
	public int getSize() {
		return ficherosLista.size();
	}

	@Override
	public String getElementAt(int index) {
		return ficherosLista.get(index).getName();
	}

		// Escuchadores de datos de la lista
		ArrayList<ListDataListener> misEscuchadores = new ArrayList<>();
	@Override
	public void addListDataListener(ListDataListener l) {
		misEscuchadores.add( l );
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		misEscuchadores.remove( l );
	}
	
	// Llamar a este método cuando se añada un elemento a la lista
	// (Utilizado para avisar a los escuchadores de cambio de datos de la lista)
	private void avisarAnyadido( int posi ) {
		for (ListDataListener ldl : misEscuchadores) {
			ldl.intervalAdded( new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, posi, posi ));
		}
	}
}
