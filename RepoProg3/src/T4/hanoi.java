package T4;

public class hanoi {
	
	
	public static void main(String[] args) {
		hanoiRec(10, 'a', 'c' ,'b' );
	}
	
	private static void hanoiRec(int tam, char origen , char destino, char auxiliar ) {
		if(tam==1) {
			// Falta terminar 
			
			System.out.println("Muevo dentro de ");
			
		}else {
		
		
		// caso recursivo 
		hanoiRec(tam-1, origen, auxiliar, destino); 
		System.out.println("Nuevo disco" + tam + " de " + origen + " a " + destino);
		hanoiRec(tam-1, auxiliar, destino, origen); 
		
		}
		
	}

}
