package T1;

/** Ejercicio 1.8.a  planteado (sin codificar ni probar)
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class Fraccion {
	
	/** Método de prueba de la clase fracción
	 * @param args	No utilizado
	 */
	public static void main(String[] args) {
		Fraccion f = new Fraccion( 2, 6 ); // 2/6 = 1/3
		System.out.println( f );
		Fraccion f2 = new Fraccion( 1, -5 ); // 1/-5 = -1/5
		System.out.println( f2 );
		System.out.println( suma(f,f2) ); // 1/3 + (-1/5) = 2/15
		System.out.println( resta(f,f2) ); // 1/3 - (-1/5) = 8/15
		System.out.println( multiplica(f,f2) ); // 1/3 * (-1/5) = -1/15
		System.out.println( divide(f,f2) ); // 1/3 / (-1/5) = -5/3
	}
	
	private int num; // Numerador
	private int den; // Denominador
	
	/** Crea y simplifica (si procede) una fracción
	 * @param num	Numerador
	 * @param den	Denominador
	 * @throws ArithmeticException Funcion incorrecta si denom de f2 es 0 
	 */
	public Fraccion( int num, int den ) {
		
		int num_mcd = mcd(num, den); 
		if (den < 0) {
		den = -den;
		num = -num; 
	}
		this.num = num/num_mcd; 
		this.den = den/num_mcd; 
		
		
	}
	
	
	/** Devuelve el numerador de una fracción
	 * @return	Valor del numerador (si hay signo lo tiene el numerador)
	 */
	public int getNum() { return num; }
	
	/** Devuelve el denominador de una fracción
	 * @return	Valor del denominador (positivo)
	 */
	public int getDen() { return den; }
	
	
	
	
	
	/** Suma dos fracciones
	 * @param f1	Fracción 1
	 * @param f2	Fracción 2
	 * @return	Fracción resultado de la suma de las fracciones 1 y 2
	 */
	
	// Podriamos diseñar el método como f1.sumar(f2) que modifique f1 pero NO cree una nueva 
	
	public static Fraccion suma( Fraccion f1, Fraccion f2 ) {
		// TODO
	
	
		int denom = f1.getDen() * f2.getDen(); 
		int numer = (f1.getNum()*f2.getDen()) + (f2.getNum()*f1.getNum()); 
		
		Fraccion suma = new Fraccion(numer, denom);
		return suma;
	}
	
	/** Resta dos fracciones
	 * @param f1	Fracción 1
	 * @param f2	Fracción 2
	 * @return	Fracción resultado de la resta f1 - f2
	 */
	public static Fraccion resta( Fraccion f1, Fraccion f2 ) {
		
		int denom = f1.getDen() * f2.getDen(); 
		int numer = (f1.getNum()*f2.getDen()) - (f2.getNum()*f1.getNum()); 
		
		Fraccion resta = new Fraccion(numer, denom);
		return resta;
	
	}
	
	
	
	
	/** Multiplica dos fracciones
	 * @param f1	Fracción 1
	 * @param f2	Fracción 2
	 * @return	Fracción resultado de la resta f1 - f2
	 */
	public static Fraccion multiplica( Fraccion f1, Fraccion f2 ) {
		// TODO
		int num_multi = f1.getNum() * f2.getNum();
		int den_multi = f1.getDen() * f2.getDen(); 
		Fraccion multi = new Fraccion(num_multi, den_multi);

		return multi;
	}
	
	/** Divide dos fracciones
	 * @param f1	Fracción 1
	 * @param f2	Fracción 2
	 * @return	Fracción resultado de la resta f1 - f2
	 * @throws ArithmeticException si denom de f2 es 0 
	 */
	public static Fraccion divide( Fraccion f1, Fraccion f2 ) {
		
		int num_div = f1.getNum() * f2.getDen(); 
		int den_div = f1.getDen() * f2.getNum(); 
		Fraccion f_div = new Fraccion(num_div,den_div);
		
	
		return f_div;
	}
	
	@Override
	public String toString() {
		return num + "/" + den; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Fraccion){
			Fraccion temp = (Fraccion)obj;
			return num == temp.num && den==temp.den; 
			
		}else {
			return false; 
			
		}
		
	}
	
	// Utilidad: Devuelve el máximo común divisor de 2 números positivos
	static int mcd( int num1, int num2 ) {
		int divisor = num1<num2 ? num1 : num2;  // El número mayor que hay que probar para calcular el MCD es el más pequeño. De ahí hacia abajo
		int mcd = 1;
		while (divisor > 1) {
			if (num1%divisor == 0 && num2%divisor==0) {  // Es divisor común: se añade y se reducen los números
				mcd *= divisor;
				num1 /= divisor;
				num2 /= divisor;
			}
			divisor--;
		}
		return mcd;
	}
	
	
	
}
