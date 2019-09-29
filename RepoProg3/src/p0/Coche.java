package p0;

public class Coche {
	
	
	private double miVelocidad; // Velocidad en pixels/segundo
	protected double miDireccionActual; // Dirección en la que estoy mirando en grados (de 0 a 360) 
	protected double posX; // Posición en X (horizontal)
	protected double posY; // Posición en Y (vertical)
	private String piloto; // Nombre de piloto

//	public Coche(double miVelocidad, double miDireccionActual,double posX, double posY, String piloto) {
//		
//		this.miVelocidad = miVelocidad; 
//		this.miDireccionActual = miDireccionActual; 
//		this.posX = posX;
//		this.posY = posY; 
//		this.piloto = piloto; 
//	}
 
	// inicializa a 0 
	public Coche() {
		miVelocidad = 0;
		miDireccionActual = 0;
		posX = 300;
		posY = 300;
	}
	
	
	
	// GETTERS AND SETTERS 
	public double getMiVelocidad() {
		return miVelocidad;
	}

	public void setMiVelocidad(double miVelocidad) {
		this.miVelocidad = miVelocidad;
	}

	public double getMiDireccionActual() {
		return miDireccionActual;
	}

	public void setMiDireccionActual(double miDireccionActual) {
		this.miDireccionActual = miDireccionActual;
	}

	public double getPosY() {
		return posY;
	}

	public void setPosY(double posY) {
		this.posY = posY;
	}

	public String getPiloto() {
		return piloto;
	}

	public void setPiloto(String piloto) {
		this.piloto = piloto;
	}

	public double getPosX() {
		return posX;
	}

	public void setPosX(double posX) {
		this.posX = posX;
	}

	public void setPosicion(double posX,double posY) {
		setPosX(posX);
		setPosY(posY);
	}
	

	@Override
	public String toString() {
		return "Coche [miVelocidad=" + miVelocidad + ", miDireccionActual=" + miDireccionActual + ", posX=" + posX
				+ ", posY=" + posY + ", piloto=" + piloto + "]";
	}


	/** Cambia la velocidad actual del coche
	 * @param aceleracion Incremento de la velocidad en pixels/segundo
	 */
	public void acelera( double aceleracion ) {
		
		miVelocidad=miVelocidad+aceleracion; 
		
		
		
	}
	
	/** Cambia la dirección actual del coche
	 * @param giro Angulo de giro a sumar o restar de la dirección actual, en grados (-180 a +180)
	 */
	public void gira( double giro ) {
		
	 setMiDireccionActual( miDireccionActual + giro); 
		
		
	}
	
	/** Cambia la posición del coche dependiendo de su velocidad y dirección
	 * @param tiempoDeMovimiento Tiempo transcurrido, en segundos
	 */
	public void mueve( double tiempoDeMovimiento ) {
		
		setPosX( posX + miVelocidad  * Math.cos(miDireccionActual/180.0*Math.PI * tiempoDeMovimiento  ));
		setPosY( posY + miVelocidad *  -Math.sin(miDireccionActual/180.0*Math.PI * tiempoDeMovimiento  ));
		// el negativo es porque en pantalla la Y crece hacia abajo y no hacia arriba
		
		
		
	}





	
	
	
}
