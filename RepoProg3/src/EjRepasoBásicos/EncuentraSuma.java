package EjRepasoBásicos;

import java.util.ArrayList;


public class EncuentraSuma {

	private static final int MAX_NUMERO = 20;
	private static final int NUM_BUSCADO = 183;

	public static void main(String[] args) {
		int cuantas = encuentraSuma( 1, MAX_NUMERO, NUM_BUSCADO, 0, new ArrayList<Integer>() );
		System.out.println( "Encontradas " + cuantas + " sumas diferentes." );
	}

	private static int encuentraSuma( int numSiguiente, int numMax, int sumaBuscada, int sumaAct, ArrayList<Integer> sumandos ) {
		if (sumaAct>sumaBuscada || numSiguiente>numMax) {
			return 0; // Caso base - se ha pasado
		} if (sumaAct==sumaBuscada) {
			System.out.println( "Encontrada suma " + sumaAct + " con sumandos " + sumandos );
			return 1; // Caso base - encontrada
		} else {
			ArrayList<Integer> sumandos2 = new ArrayList<Integer>( sumandos );
			sumandos2.add( numSiguiente );
			return encuentraSuma( numSiguiente+1, numMax, sumaBuscada, sumaAct, sumandos ) +
					encuentraSuma( numSiguiente+1, numMax, sumaBuscada, sumaAct+numSiguiente, sumandos2 );
		}
	}

}

