package ord201512.captura;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class CapturaPantalla {
	
	/** Captura y devuelve la pantalla
	 * @param numPantalla	N�mero de pantalla a capturar, 0 para la principal y 1-n para pantallas subsiguientes en escritorio extendido. -1 para escritorio completo
	 * @return	Imagen de la pantalla capturada, la total de escritorio si se indica -1, la de la pantalla 0 si la pantalla indicada no existe. null si hay alg�n problema de captura
	 */
	public static BufferedImage capturaPantalla( int numPantalla ) {
		return capturaPantalla( numPantalla, -1, -1, -1, -1 );
	}
	
	/** Captura y devuelve el rect�ngulo gr�fico indicado de una pantalla
	 * @param numPantalla	N�mero de pantalla a capturar, 0 para la principal y (1-n) para pantallas subsiguientes en escritorio extendido. -1 para escritorio completo
	 * @param xDesde	Coordenada x de p�xel inicial de captura (relativo a la pantalla indicada) [toda la pantalla si se indica -1]
	 * @param yDesde	Coordenada y de p�xel inicial de captura (relativo a la pantalla indicada)
	 * @param anchura	anchura de rect�ngulo de captura (en pixels)
	 * @param altura	altura de rect�ngulo de captura (en pixels)
	 * @return	Imagen del recorte de pantalla capturado. null si hay alg�n problema de captura
	 */
	public static BufferedImage capturaPantalla( int numPantalla, int xDesde, int yDesde, int anchura, int altura ) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();
		Rectangle pantallaConcreta = null;
		Rectangle rectCaptura = new Rectangle();
		if (numPantalla<-1 || numPantalla>=screens.length) numPantalla = 0;
		for (int sc=0; sc<screens.length; sc++) {
			GraphicsDevice pantalla = screens[sc];
		    Rectangle rectPantalla = pantalla.getDefaultConfiguration().getBounds();  // L�mites supuestos
		    // Correcci�n de tama�o real de la pantalla
		    rectPantalla.width = pantalla.getDefaultConfiguration().getDevice().getDisplayMode().getWidth();  
		    rectPantalla.height = pantalla.getDefaultConfiguration().getDevice().getDisplayMode().getHeight();
		    // Correcci�n de coordenada real de la pantalla  (pegada a las anteriores)
		    if (rectPantalla.x!=0 || rectPantalla.y!=0) {  // Excepto para la pantalla principal (0,0)
		    	if (rectPantalla.x > rectCaptura.x+rectCaptura.width)
		    		rectPantalla.x -= (rectPantalla.x - rectCaptura.x-rectCaptura.width);
		    	if (rectPantalla.y > rectCaptura.y+rectCaptura.height)
		    		rectPantalla.y -= (rectPantalla.y - rectCaptura.y-rectCaptura.height);
		    }
		    // Memorizar pantalla (si esta es la que nos han pedido)
			if (sc==numPantalla) { pantallaConcreta = rectPantalla; break; }
		    // C�lculo del rect�ngulo englobador
			rectCaptura.x = Math.min(rectCaptura.x, rectPantalla.x);
			rectCaptura.y = Math.min(rectCaptura.y, rectPantalla.y);
		    rectCaptura.width = Math.max(rectCaptura.width,rectPantalla.x+rectPantalla.width);
		    rectCaptura.height = Math.max(rectCaptura.height,rectPantalla.y+rectPantalla.height);
			// System.out.println( screen + " " + screenBounds + " | " + allScreenBounds);
		}
		if (pantallaConcreta==null) {
			if (rectCaptura.x < 0) rectCaptura.width = rectCaptura.width-rectCaptura.x;
			if (rectCaptura.y < 0) rectCaptura.height = rectCaptura.height-rectCaptura.y;
		} else rectCaptura = pantallaConcreta;
		if (xDesde!=-1) {
			rectCaptura.x += xDesde; rectCaptura.y += yDesde;
			rectCaptura.width = anchura; rectCaptura.height = altura;
		}
		BufferedImage screenShot = null;
		try {
			Robot robot;
			robot = new Robot();
			screenShot = robot.createScreenCapture(rectCaptura);
			ultimaAnchura = rectCaptura.width; ultimaAltura = rectCaptura.height;
		} catch (AWTException e) { e.printStackTrace(); }
		return screenShot;
	}
	
		private static int ultimaAnchura = 0;
		private static int ultimaAltura = 0;
		
	/** Devuelve la anchura de la �ltima pantalla capturada
	 * @return	Anchura en p�xels
	 */
	public static int ultimaAnchuraCapturada() { return ultimaAnchura; }
	/** Devuelve la altura de la �ltima pantalla capturada
	 * @return	Altura en p�xels
	 */
	public static int ultimaAlturaCapturada() { return ultimaAltura; }

		private static boolean finCaptura = false;
	/** Prueba de captura. Captura la pantalla 2 (si no existe coge la 1) y la visualiza repetidamente en una ventana
	 * hasta que la ventana se cierra
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame v = new JFrame();
		v.setSize(1000,800);
		v.setLocation(0,0);
		v.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		v.setLocationRelativeTo(null);
		v.addWindowListener( new WindowAdapter() { @Override public void windowClosing(WindowEvent e) { finCaptura = true; }  });  // Acaba la captura al cerrar la ventana
		ImageIcon ii = new ImageIcon( capturaPantalla(1) );
		JLabelAjusta l = new JLabelAjusta(ii);
		v.getContentPane().add( l );
		v.setVisible( true );
		long tiempoActual = System.currentTimeMillis();
		int numCapturas = 0;
		while (!finCaptura) {
			l.setImageIcon( new ImageIcon( capturaPantalla(1) ) );
			numCapturas++;
			if (numCapturas % 50 == 0) {  // Cada 50 capturas muestra el tiempo de refresco aproximado
				double refresco = (System.currentTimeMillis() - tiempoActual) * 1.0 / numCapturas;
				System.out.println( "Tiempo de refresco: " + refresco + " msgs. / captura." );
			}
		}
		// Al final del programa muestra el tiempo de refresco aproximado
		double refresco = (System.currentTimeMillis() - tiempoActual) * 1.0 / numCapturas;
		System.out.println( "Tiempo de refresco: " + refresco + " msgs. / captura." );
	}

}
