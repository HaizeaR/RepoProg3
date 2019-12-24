package ord201806.datos;

import java.io.File;

public class FicheroLog extends File {
	private static final long serialVersionUID = 1L;  // C�digo de versi�n para posible serializado
	
	private String codTablet;  // C�digo de tablet del fichero de log
	
	/** Construye un objeto que representa un fichero de log. Sin c�digo de tablet por defecto
	 * @param path	Path de ese fichero
	 */
	public FicheroLog(String path) {
		super(path);
		codTablet = "";
	}

	/** Construye un objeto que representa un fichero de log
	 * @param f	Fichero
	 * @param tablet	C�digo de tablet de ese fichero
	 */
	public FicheroLog(File f, String tablet) {
		super( f.getAbsolutePath() );
		codTablet = tablet;
	}

	/** Devuelve c�digo de tablet
	 * @return	c�digo de tablet del fichero de log, "" si no est� definido
	 */
	public String getCodTablet() {
		return codTablet;
	}

	/** Modifica el c�digo de tablet del fichero de log
	 * @param codTablet	Nuevo c�digo
	 */
	public void setCodTablet(String codTablet) {
		this.codTablet = codTablet;
	}

	@Override
	public String toString() {
		return codTablet + " - " + getName();
	}
}
