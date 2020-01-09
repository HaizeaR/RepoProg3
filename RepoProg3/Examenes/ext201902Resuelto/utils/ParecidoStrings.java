package ext201902Resuelto.utils;

/** Calcula distancia entre dos strings en base al método de Levenshtein
 */
public class ParecidoStrings {

	  /** Calcula la similitud de dos strings como un número entre 0 y 1 (0=ninguna similitud, 1=strings idénticos)
	   */
	public static double similitud(String s1, String s2) {
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) { // Se pone siempre s1 como el más corto y s2 como el más largo
			longer = s2; shorter = s1;
		}
		int longerLength = longer.length();
		if (longerLength == 0) { return 1.0; } // Ambos strings son vacíos
		return (longerLength - distanciaLevenshtein(longer, shorter)) / (double) longerLength;
	}

	  // Implementación de la distancia Levenshtein
	  // Ver http://rosettacode.org/wiki/Levenshtein_distance#Java
	  private static int distanciaLevenshtein(String s1, String s2) {
		  s1 = s1.toLowerCase();
		  s2 = s2.toLowerCase();
		  int[] costs = new int[s2.length() + 1];
		  for (int i = 0; i <= s1.length(); i++) {
			  int lastValue = i;
			  for (int j = 0; j <= s2.length(); j++) {
				  if (i == 0)
					  costs[j] = j;
				  else {
					  if (j > 0) {
						  int newValue = costs[j - 1];
						  if (s1.charAt(i - 1) != s2.charAt(j - 1))
							  newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						  costs[j - 1] = lastValue;
						  lastValue = newValue;
					  }
				  }
			  }
			  if (i > 0)
				  costs[s2.length()] = lastValue;
		  }
		  return costs[s2.length()];
	  }

	  
	  
}