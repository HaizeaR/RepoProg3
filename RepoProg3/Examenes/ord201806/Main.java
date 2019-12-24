package ord201806;

import javax.swing.JFrame;

import ord201806.iu.VentanaProcesoLogs;

/** Clase principal - ejecuci�n examen ordinaria 201806
 * Ver fichero leeme.txt
 * @author andoni.eguiluz @ ingenieria @ deusto.es
 */
public class Main {

	/** M�todo principal de la clase para ejecuci�n
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);  // Para que el JFrame se pueda hacer transparente (al cerrar)
		VentanaProcesoLogs v = new VentanaProcesoLogs();
		v.setVisible( true );
	}

}
