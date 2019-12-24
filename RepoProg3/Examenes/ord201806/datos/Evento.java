package ord201806.datos;

import java.io.Serializable;

/** Clase abstracta para gesti�n de eventos en logs
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public abstract class Evento implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Detecta un posible evento en una l�nea le�da de log
	 * @param evento	Evento previo de l�neas anteriores de log (null si no hay evento previo)
	 * @param linea	L�nea a chequear
	 * @param numLinea	N�mero de esa l�nea en el fichero
	 * @param fichero	Fichero de log
	 * @return	Nuevo objeto evento si esa l�nea lo tiene, mismo evento modificado si sigue, null si no lo tiene
	 */
	public static Evento detectaEventoEnLinea( Evento evento, String linea, int numLinea, FicheroLog fichero ) {
		Evento ret = null;
		if (evento!=null && evento.esMultiLinea()) {  // Hay evento previo y es multil�nea
			// Chequear si ese evento sigue
			if (evento.sigueEvento( linea )) {
				evento.addLinea( linea );
				return evento;
			}
		}
		// Detecci�n de todos los tipos de evento posibles...
		if (Excepcion.inicioDeEvento( linea )) {
			ret = new Excepcion( linea, fichero, numLinea );
		} else if (Saved.inicioDeEvento( linea )) {
			ret = new Saved( linea, fichero, numLinea );
		}
		// T1
		else if (Error.inicioDeEvento( linea )) {
			ret = new Error( linea, fichero, numLinea );
		}
		return ret;
	}

	// Atributos de objeto
	
	private String texto;  // Texto origen del evento en el log
	private int numLinea; // L�nea de origen del evento en el log
	private FicheroLog fichero; // Fichero log de origen del evento
	private int numLineas; // N�mero de l�neas del evento en el fichero
	
	/** Constructor general de evento
	 * @param texto	Texto origen del evento en el log
	 * @param fichero	Fichero de log de ese evento
	 * @param linea	L�nea del fichero donde se encuentra
	 */
	public Evento( String texto, FicheroLog fichero, int linea ) {
		this.texto = texto;
		this.fichero = fichero;
		this.numLinea = linea;
		this.numLineas = 1;
	}
	
	/** Devuelve la l�nea de evento en el fichero de log
	 * @return	N�mero de l�nea
	 */
	public int getLinea() {
		return numLinea;
	}
	
	/** Devuelve el n�mero de l�neas de evento en el fichero de log
	 * @return	N�mero de l�neas
	 */
	public int getNumLineas() {
		return numLineas;
	}

	/** Devuelve el fichero de log donde se encuentra el evento
	 * @return	Fichero de log
	 */
	public FicheroLog getFichero() {
		return fichero;
	}

	/** Devuelve el texto origen del evento en el log
	 * @return	texto origen del evento
	 */
	public String getTexto() {
		return texto;
	}

	/** Modifica el texto origen del evento en el log
	 * @param texto	Nuevo texto
	 */
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	/** Indica si el tipo de evento es o no de m�ltiples l�neas de log
	 * @return	true si el evento tiene m�ltiples l�neas, false si solo tiene una
	 */
	public abstract boolean esMultiLinea();
	
	/** Indica si el evento multil�nea sigue con la l�nea indicada
	 * @param linea	Nueva l�nea del evento
	 * @return	true si esa l�nea es parte del mismo evento, false en caso contrario
	 */
	public abstract boolean sigueEvento( String linea );
	
	/** A�ade l�nea al texto del log
	 * @param texto	Nueva l�nea a a�adir
	 */
	public void addLinea(String texto) {
		this.texto += ("\n" + texto);
		numLineas++;
	}

	@Override
	public String toString() {
		if (numLineas==1)
			return fichero + ":" + numLinea;
		else
			return fichero + ":" + numLinea + "(" + numLineas + ")";
	}
}
