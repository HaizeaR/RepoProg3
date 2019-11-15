package T4;

public class aLaLuna {

	
	public static void main(String[] args) {
		aLaLuna(); 
		
	}
	
	private static void aLaLuna(){
		// grosor de folio , distancia a la luna, numero de dobleces
		
		calcDobleces(0.00015, 384400000.0, 0 ); // en m 
	
	}
	
	private static void calcDobleces(double grosor, double distancia, int numDobleces) {
		if (grosor >= distancia) {
			System.out.println(numDobleces);
		}else {
			calcDobleces(grosor * 2, distancia, numDobleces + 1 );
		}
	}
	
	
	
	
}
