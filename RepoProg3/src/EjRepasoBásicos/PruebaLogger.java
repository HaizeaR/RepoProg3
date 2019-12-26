package EjRepasoBásicos;



import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;



// LOGGER básico que guarda información de inicio del programa en un fichero del tipo XML 
// cada vez que quiero registrar algo ponelo log.log ( tipo de mensaje , el mensaje que quiero registrar ) 

// El mensaje que quiero registrst puede ser 
// - Inicio de programa
// - Fin de programa 
// - errores 
// - acciones del usuario 8

public class PruebaLogger {
	
	static Logger log; 
  
	public static void main(String[] args) throws SecurityException, IOException {
		log = Logger.getLogger("p-logger"); 
		log.log(Level.INFO, "Inicio del prog: " + (new Date()));
		Handler h = new FileHandler("p-logger.xml", true);
		h.setLevel(Level.INFO);
		log.addHandler(h);
		log.log(Level.FINE, "Fin del programa"); 
		
	
		
		
	
	}

	

}
