package ord201806.datos;

/** Evento de tipo error en logs
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public class Error extends Evento {
	private static final long serialVersionUID = 1L;

	/** Detecta si una l�nea de texto de log inicia un evento del tipo correspondiente a esta clase
	 * @param linea	L�nea a chequear
	 * @return	true si esta l�nea inicia un evento de este tipo
	 */
	public static boolean inicioDeEvento( String linea ) {
		return (linea.contains( "ERROR:") );
	}
	
	/** Crea un evento de tipo excepci�n
	 * @param linea	L�nea inicial del evento
	 * @param fichero	Fichero de log del evento
	 * @param numLinea	L�nea donde aparece ese evento en el fichero
	 */
	public Error( String linea, FicheroLog fichero, int numLinea ) {
		super( linea, fichero, numLinea );
	}

	@Override
	public boolean esMultiLinea() {
		return false;
	}
	
	@Override
	public boolean sigueEvento(String linea) {
		return false;
	}

	@Override
	public String toString() {
		return super.toString() + " - Error";
	}
}
