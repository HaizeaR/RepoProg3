package p4;

public class invertir {
	
	public static void main(String[] args) {
		invertirFrase(); 
		invertirPalabra();
		
	}

	
	private static void invertirFrase() {
		String palabra = "Esto" ;
		String frase = "Esto es una frase a invertir";
		
		reverse(palabra);
		// Se tiene que ver -->  otsE
		reverse(frase);
		// Se tiene que ver --> ritrevni a esarf anu se otsE
		
	}
	
	private static void invertirPalabra() {
		
		String frase2 = "Esto es una frase a invertir"; 
		int i =0; 
		imprimirReves(frase2, i);
		// SOL --> invertir a frase una es esto 
		// tenemos que separarlo por /t, /n,
		
	}
	

	private static String reverse (String palabra) {
	
			 if (palabra.length() == 1) {
				// System.out.println(palabra);
				 return palabra; 
			 }
			   
			 else {
				 palabra = reverse(palabra.substring(1))+palabra.charAt(0) ;
				// System.out.println(palabra);
				 return palabra;
			 }
			   
			
		}
		
	public static void imprimirReves(String frase2, int num) {
		// TODO
		
	}
	
	
	
	
}

		
		
	
	

