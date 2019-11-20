package T4;

public class busquedaBinaria {
	
	public static void main(String[] args) {
		
		busquedaBinaria();

	}
	
	
	private static void busquedaBinaria() {
		int[] v = { 1, 2, 10, 11, 15, 17, 21, 43, 57, 83, 84, 85, 86, 87, 89, 110};
		int donde = busq(v , 87, 0 , v.length-1); 
		System.out.println(donde);
	}

	
	
	// calculamos la mitad 
	// comaparamo el valor
	//  a) son iguales - caso base : se devuelve 
	//
	//	b) menor - buscar recursivamente en la mitad SUPERIOR 
	//			como ya sabemos que ese num no es ( mitad + 1 de limite inf) 
	//  c) mayor - buscar recursivamente en la mitad INFERIOR 
	// 
	private static int busq(int[] v, int valorBuscado, int ini, int fin) {

		// caso base 
		if (ini >  fin) {
			return 1 ; 
			
		}else {
			// caso recursivo 
			int mitad = (ini + fin)/2; // int devuelve la mitad del entero ( centro lig.a la izquierda) 
			if ( v[mitad] == valorBuscado) {
				return mitad; 

			}else if (v[mitad]< valorBuscado) {
				return  busq(v, valorBuscado, mitad + 1, fin);
				
			}else {
				return busq(v, valorBuscado, ini, mitad - 1);

			}

		}
	}

}
