package p4;

public class longAHexa {


	public static void main(String[] args) {
		longAHexa();
	}

	private static void longAHexa() {
		long l = 900;
		long l2 = 1235; 
		System.out.println(cambio(l));
		System.out.println("\t numero en DEC " + l);
	
		System.out.println(cambio(l2));
		System.out.println("\t numero en DEC " + l2);


	}

	private static String cambio (Long l ) {


		int resto = (int) (l % 16); 

		String resultado = ""; 
		if (l == 0) {
			return Integer.toString(0); 
		}else {
			if (resto < 10) {
				resultado = resto + resultado; 

			}else if(resto == 10 ) {
				resultado = "A" + resultado; 

			}else if (resto == 11) {
				resultado = "B" + resultado; 

			}
			else if (resto == 12) {
				resultado = "C" + resultado; 
			}
			else if (resto == 13) {
				resultado = "D" + resultado; 
			}
			else if (resto == 14) {
				resultado = "E" + resultado; 
			}else if (resto == 15) {
				resultado = "F" + resultado;
			}else {

				int r = (int) (l/16);
				resultado = Integer.toString(r) + resultado;
					//System.out.println(invertir.reverse(resultado));

			}
			
			
			return cambio(l/16) + resultado;
		}
		
	}


}
