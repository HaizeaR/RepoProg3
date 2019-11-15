package T4;

public class ProdConSuma {

	
	
	public static void main(String[] args) {
		prodConSuma();
	}



	public static void prodConSuma() {
		System.out.println(producto(6,5));


	}

	/**Calula el producto de m y n utilizando solo sumas 
	 * 
	 * CR 		Definición m*n = m*(n-1) 			producto(m,n) = producto(m,n-1)
	 * 
	 * CB 		 0 si n==0
	 * 
	 * @param m
	 * @param n
	 * 
	 * @return 0 if n == 0 o el producto de m y n 
	 * 
	 */
	private static int producto(int m , int n ) {

		if(n==0) {
			return 0; 
		}else {
			return m + producto (m, n-1); 
		}

	}

}
