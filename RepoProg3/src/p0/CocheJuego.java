package p0;


public class CocheJuego extends Coche {
	
	private JLabelCoche coche; 
	

	
	public CocheJuego() {
		coche = new JLabelCoche();
	}


// constructor usando Super
	
//	public CocheJuego(double miVelocidad, double miDireccionActual, double posX, double posY, String piloto,
//			JLabelCoche coche) {
//		super(miVelocidad, miDireccionActual, posX, posY, piloto);
//		this.coche = coche;
//	}

	public JLabelCoche getGrafico() {
		return coche;
	}
	
	
	// modificar estos tres métodos para que se mueva el JLabel a la vez que el coche !!
	
	@Override
	public void setPosX(double posX) {
		super.setPosX(posX);
		// RECUERDA , funciona con enteros y posX/posY son DOUBLE
		coche.setLocation((int)posX, (int)posY);
		coche.repaint();
	}
	
	@Override 
	public void setPosY(double posY) {
		super.setPosY(posY);
		coche.setLocation((int)posX, (int)posY);
		coche.repaint();
		
	}
	
	@Override 
	public void setMiDireccionActual(double miDireccionActual) {
		super.setMiDireccionActual(miDireccionActual);
		coche.setGirar(miDireccionActual); 
		coche.repaint();
	
	}


}
