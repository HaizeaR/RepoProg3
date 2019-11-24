package p4;

public class invertir {
	
	public static void main(String[] args) {
		System.out.println("Invertir cada letra de la frase: \n");
		invertirFrase(); 
		System.out.println("\nInvertir las palabras: \n");
		invertirPalabra();
		
	}

	
	private static void invertirFrase() {
		String palabra = "Esto" ;
		String frase = "Esto es una frase a invertir";
		System.out.println(invertirF(palabra));
	
		// Se tiene que ver -->  otsE
		System.out.println(invertirF(frase));
		// Se tiene que ver --> ritrevni a esarf anu se otsE
		
	}
	
	private static void invertirPalabra() {
		
		String frase2 = "Esto es una frase a invertir"; 
		
		System.out.println(invertirP(frase2));
		
		// SOL --> invertir a frase una es esto 
		// tenemos que separarlo por /t, /n,
		
	}
	

	private static String invertirF (String palabra) {
	
			 if (palabra.length() == 1) {
				// System.out.println(palabra);
				 return palabra; 
			 }
			   
			 else {
				 palabra = invertirF(palabra.substring(1))+palabra.charAt(0) ;
				// System.out.println(palabra);
				 return palabra;
			 }
			   
			
		}
		
	public static String invertirP(String frase2) {
		String[] palabras_sueltas = frase2.split(" ");
		if(palabras_sueltas.length == 1) {
			return frase2;
		}else {
			return invertirP(frase2.split(" ",2)[1]) + " " + frase2.split(" ",2)[0];
		}
	}

}
	
	
	
	


		
		
	
	

