package T4;

public class Factorial {

	
	public static void main(String[] args) {
		factorial();
		factorial2();
	}
	
	
	private static void factorial() {
		System.out.println(fact(5));
	
		
	}
	private static void factorial2() {
		fact2(0,1,6);
	}
	
	private static long fact(int n) {
		// Caso base ( para que termine alguna vez) 
		if ( n==0) {
			return 1; 
		}else { // caso recursivo ( calcula el factorial ) 
			
			return n * fact(n-1); 
		}
		
	}
	
	private static void fact2(int nInicial, long valorInicial, int nFinal) {
		if(nInicial == nFinal) {
			System.out.println(valorInicial);
		}else {
			fact2(nInicial+1, valorInicial*(nInicial+1) , nFinal);
		}

	}
	

}
