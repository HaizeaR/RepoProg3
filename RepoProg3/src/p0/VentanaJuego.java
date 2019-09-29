package p0;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



import javax.swing.*;


public class VentanaJuego extends JFrame {
	private static final long serialVersionUID = 1L;

	JButton bAcelerar, bFrenar, bGirarIzquierda, bGirarDerecha;
	JPanel pBlanco; 
	CocheJuego coche; 
	MiRunnable miHilo = null; 

	

	public VentanaJuego() {
		
		setTitle("JUEGO"); 
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocation(100, 100);
		
		pBlanco = new JPanel();
		pBlanco.setBackground(Color.WHITE);
		pBlanco.setLayout(null);
		
		JPanel botonera = new JPanel(); 
		
		bAcelerar = new JButton("Acelerar"); 
		bFrenar = new JButton("Frenar"); 
		bGirarDerecha = new JButton("Gira Der."); 
		bGirarIzquierda = new JButton("Gira Izq."); 
		
		
		botonera.add(bAcelerar); 
		botonera.add(bFrenar); 
		botonera.add(bGirarDerecha); 
		botonera.add(bGirarIzquierda); 
		
		setLayout(new BorderLayout());
		add(pBlanco, BorderLayout.CENTER);
		add(botonera, BorderLayout.SOUTH);
		
		
		bAcelerar.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			 // velocidad del coche incrememnta 5 pixeles 
				coche.acelera(+5);
				System.out.println("Nueva velocidad : " + coche.getMiVelocidad());
				
			}
		});
		
		bFrenar.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {
				 // velocidad del coche decrementa 5 pixeles 
				coche.acelera(-5);
				System.out.println("Nueva velocidad : " + coche.getMiVelocidad());
			
			}
		});
		
		bGirarDerecha.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// giro a la derecha NEGATIVO
				coche.gira(-10);
				System.out.println("Nueva dirección : " + coche.getMiDireccionActual());
			}
		});
		
		bGirarIzquierda.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// giro a la izq POSITIVO
				coche.gira(+10);
				System.out.println("Nueva dirección : " + coche.getMiDireccionActual());
				
			}
		});	
		
		
	
	}
	

	
	public void crearCoche(int posX, int posY) {
		coche = new CocheJuego(); 
		coche.setPosicion(posX, posY);
		pBlanco.add(coche.getGrafico());
		
	}

	
	
	
	public static void main(String[] args) {
		
		
		//Coche c1 = new Coche(0.0, 0.0, 150, 100, "H");
		//JLabelCoche l1 = new JLabelCoche();
		//CocheJuego coche = new CocheJuego(); 

		
		VentanaJuego v1 = new VentanaJuego(); 
		
		v1.crearCoche(100,100);
		v1.setVisible(true);

		
		
		v1.miHilo = v1.new MiRunnable(); 
		Thread nuevoHilo = new Thread(v1.miHilo); 
	
		nuevoHilo.start();

	}
	

	class MiRunnable implements Runnable{

		@Override
		public void run() {
			coche.mueve(0.040);
		

			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}
}

