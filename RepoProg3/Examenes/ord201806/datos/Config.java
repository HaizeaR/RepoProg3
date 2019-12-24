package ord201806.datos;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import ord201806.iu.VentanaProcesoLogs;

/** Clase de configuraci�n para gesti�n de fichero de logs
 * basado en la clase Properties de java.util
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public class Config {
	
	// Variables de configuraci�n que se guardan
	public static final String PROP_CARPETA = "ULT-CARPETA";
	public static final String PROP_EXT = "EXT";
	public static final String PROP_X_VENT = "X-VENT";
	public static final String PROP_Y_VENT  = "Y-VENT";
	public static final String PROP_ANCHO_VENT  = "ANCHO-VENT";
	public static final String PROP_ALTO_VENT  = "ALTO-VENT";
	public static final String PROP_TEXTO_BUSQ  = "TEXTO-BUSQ";

	// Valores iniciales de las variables de configuraci�n
	public static final String VAL_CARPETA = "c:\\";
	public static final String VAL_EXT = "txt";
	public static final String VAL_X_VENT = "0";
	public static final String VAL_Y_VENT = "0";
	public static final String VAL_ANCHO_VENT = "1000";
	public static final String VAL_ALTO_VENT = "600";
	public static final String VAL_TEXTO_BUSQ = "#$#";

	private static Properties properties = new Properties();  // Objeto principal de properties de configuraci�n
	static {
		carga();  // Carga en la inicializaci�n las properties desde fichero por defecto
	}

	/** Devuelve una variable de configuraci�n
	 * @param nombreProp	Clave de la variable
	 * @return	Valor de la variable (null si no est� definida)
	 */
	public static String getProperty( String nombreProp ) {
		return properties.getProperty( nombreProp );
	}
	
	/** Modifica una variable de configuraci�n
	 * @param nombreProp	Clave de la variable
	 * @param valorProp	Valor de la variable
	 */
	public static void setProperty( String nombreProp, String valorProp ) {
		properties.setProperty( nombreProp, valorProp );
	}
	
	/** Devuelve el objeto de propiedades principal de la configuraci�n
	 * @return	Objeto properties inicializado
	 */
	public static Properties getProperties() {
		return properties;
	}

	
	/** Carga las variables de configuraci�n desde el fichero de configuraci�n por defecto
	 * Si no se encuentran las variables de configuraci�n est�ndar
	 * (correspondientes a los atributos String p�blicos de esta clase)
	 * se inicializan con valores por defecto:
	 * PROP_CARPETA - Carpeta de disco de carga = "c:\\"
	 * PROP_EXT - Extensi�n de ficheros a cargar = "txt"
	 * PROP_X_VENT - Coordenada x de la ventana principal = "0"
	 * PROP_Y_VENT - Coordenada y de la ventana principal = "0"
	 * PROP_ANCHO_VENT - Ancho de la ventana principal = "1000"
	 * PROP_ALTO_VENT - Alto de la ventana principal = "600"
	 * PROP_TEXTO_BUSQ - Listado de historial de textos buscados = "#$#"
	 */
	public static void carga() {
		properties = new Properties();
		try {
			properties.loadFromXML( new FileInputStream( "procesologs.ini" ));
		} catch (Exception e) {}
		if (!properties.containsKey( PROP_CARPETA )) properties.setProperty( PROP_CARPETA, VAL_CARPETA );
		if (!properties.containsKey( PROP_EXT )) properties.setProperty( PROP_EXT, VAL_EXT );
		if (!properties.containsKey( PROP_X_VENT )) properties.setProperty( PROP_X_VENT, VAL_X_VENT );
		if (!properties.containsKey( PROP_Y_VENT )) properties.setProperty( PROP_Y_VENT, VAL_Y_VENT );
		if (!properties.containsKey( PROP_ANCHO_VENT )) properties.setProperty( PROP_ANCHO_VENT, VAL_ANCHO_VENT );
		if (!properties.containsKey( PROP_ALTO_VENT )) properties.setProperty( PROP_ALTO_VENT, VAL_ALTO_VENT );
		if (!properties.containsKey( PROP_TEXTO_BUSQ )) properties.setProperty( PROP_TEXTO_BUSQ, VAL_TEXTO_BUSQ );
	}
	
	/** Guarda las variables de configuraci�n en el fichero de configuraci�n por defecto
	 * modificando previamente las variables de configuraci�n de la ventana de acuerdo
	 * a sus valores actuales en la ventana principal
	 * @param v	Ventana principal de la que tomar los valores de posici�n y tama�o
	 */
	public static void guardar( VentanaProcesoLogs v ) {
		properties.setProperty( PROP_X_VENT, v.getLocation().x + "" );
		properties.setProperty( PROP_Y_VENT, v.getLocation().y + "" );
		properties.setProperty( PROP_ANCHO_VENT, v.getWidth() + "" );
		properties.setProperty( PROP_ALTO_VENT, v.getHeight() + "" );
		try {
			properties.storeToXML( new FileOutputStream( "procesologs.ini" ), "ProcesoLogs.java" );
		} catch (Exception e) {
			JOptionPane.showMessageDialog( null, "No se ha podido guardar la configuraci�n", "�Atenci�n!", JOptionPane.ERROR_MESSAGE );
		}
	}
	
	
}
