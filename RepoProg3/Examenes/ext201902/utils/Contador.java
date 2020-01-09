package ext201902.utils;

/** Clase que representa un contador (entero modificable que puede inicializarse e irse incrementando)
 * (Nótese que Integer no vale para hacer este trabajo porque es inmutable)
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Contador {
	
	private int cont = 0;  // Contador entero
	
	/** Inicializa un contador a cero
	 */
	public Contador() {
	}
	/** Inicializa un contador con el valor indicado
	 * @param valor	Valor de inicio del contador
	 */
	public Contador( int valor ) {
		cont = valor;
	}
	/** Devuelve el valor del contador
	 * @return	Valor actual del contador
	 */
	public int get() { return cont; }
	/** Incrementa el contador
	 */

	/** Incrementa el contador en una unidad
	 */
	public void inc() { cont++; }
	
	/** Incrementa el contador
	 * @param inc	Número de unidades de incremento de contador
	 */
	public void inc( int inc ) { cont += inc; }
	
	@Override
	public String toString() { return "" + cont; }
	
}
