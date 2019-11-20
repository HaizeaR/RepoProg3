package T4;




public class fibonacci{
	
	// fin(n) = fib(n-1) + fib(n-2) 
	// si n = 1 fib(1) = 1 
	// este no es un buen ejemplo de recursividad  
	
	private static int fibonacci(int n) {

		if (n == 1 ) {
			return 1 ; 
		}else if (n == 2){
			return 1 ; 
		}else {

			return fibonacci(n-1) + fibonacci(n-2) ; 
		}

	}
	
	public static void main(String[] args) {
		
		System.out.println(fibonacci(3));
	}
	
}
