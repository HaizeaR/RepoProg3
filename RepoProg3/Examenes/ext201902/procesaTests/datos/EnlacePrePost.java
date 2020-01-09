package ext201902.procesaTests.datos;

/** Clase para gestionar enlaces de los tests personales
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class EnlacePrePost implements ConvertibleEnTabla, Comparable<EnlacePrePost> {  // T2 interfaz Comparable
	
	private int numEnlace;                  // Código único de enlace
	
	private String centro;                  // Centro educativo

	private TestPC testPre;                 // Info de tests pre (null si no existe)
	private TestPC testPost;                // Info de tests post (null si no existe)
	
	// Info de modificación
	private boolean guardado;               // true si está almacenado en base de datos
	

	/** Crea un nuevo enlace de test
	 * @param numEnlace	Número de enlace (código único)
	 * @param centro	Centro educativo (no null)
	 */
	public EnlacePrePost( int numEnlace, String centro ) {
		this.numEnlace = numEnlace;
		this.centro = centro;
		guardado = false;
	}
	
	public int getNumEnlace() {
		return numEnlace;
	}

	public void setNumEnlace(int numEnlace) {
		this.numEnlace = numEnlace;
	}
	
	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	/** Devuelve el código de estudiante del enlace 
	 * @return	Código del test pre si existe, si no el del post, y si no null
	 */
	public String getCodigoEst() {
		if (testPre!=null) return testPre.getCodEstud();
		else if (testPost!=null) return testPost.getCodEstud();
		else return null;
	}
	
	public TestPC getTestPre() {
		return testPre;
	}

	public void setTestPre( TestPC testPre ) {
		this.testPre = testPre;
	}

	public TestPC getTestPost() {
		return testPost;
	}

	public void setTestPost( TestPC testPost ) {
		this.testPost = testPost;
	}

	public boolean isGuardado() {
		return guardado;
	}

	public void setGuardado(boolean guardado) {
		this.guardado = guardado;
	}

	@Override
	public String toString() {
		return centro + "\t" + numEnlace + "\t" + testPre + "\t" + testPost;
	}
		

	// =========================================================================
	// Métodos de interfaz ConvertibleEnTabla
	
	@Override
	public int getNumColumnas() {
		return 12;
	}
	
	@Override
	public String getValorColumna( int col ) {
		switch (col) {
			case 0: {
				return numEnlace + "";
			}
			case 1: {
				return centro;
			}
			case 2: {
				return testPre==null?"":testPre.getCodEstud();
			}
			case 3: {
				return testPre==null?"":testPre.getSexo();
			}
			case 4: {
				return testPre==null?"":testPre.getEdad()+"";
			}
			case 5: {
				return testPre==null?"":testPre.getNomCentro();
			}
			case 6: {
				return testPre==null?"":""+testPre.getPuntuacion();
			}
			case 7: {
				return testPost==null?"":testPost.getCodEstud();
			}
			case 8: {
				return testPost==null?"":testPost.getSexo();
			}
			case 9: {
				return testPost==null?"":testPost.getEdad()+"";
			}
			case 10: {
				return testPost==null?"":testPost.getNomCentro();
			}
			case 11: {
				return testPost==null?"":""+testPost.getPuntuacion();
			}
			default: {
				return "";
			}
		}
	}

		private static String[] cabs = { "Num", "Centro", "Pre:Cod", "Pre:Sexo", "Pre:Edad", "Pre:Centro", "Pre:Punt", "Post:Cod", "Post:Sexo", "Post:Edad", "Post:Centro", "Post:Punt" };
	@Override
	public String getNombreColumna( int col ) {
		return cabs[col];
	}
	
	@Override
	public void setValorColumna(int col, String valor) {
		// No hace nada (no se puede cambiar interactivamente)
	}
	
	// T2
	@Override
	public int compareTo(EnlacePrePost o) {
		if (getTestPre()==null || getTestPost()==null || o.getTestPre()==null || o.getTestPost()==null) return 0;  // No interesa la comparación si no hay pre o no hay post
		int mejora1 = getTestPost().getPuntuacion() - getTestPre().getPuntuacion();
		int mejora2 = o.getTestPost().getPuntuacion() - o.getTestPre().getPuntuacion();
		int comp = mejora2-mejora1;  // 1.- Ordena por mejora
		if (comp==0) {  // 2.- Después ordena por número de aciertos en el post
			comp = o.getTestPost().getNumTestCorrectas() - getTestPost().getNumTestCorrectas();
			if (comp==0) {  // 3.- Después ordena por código
				comp = getCodigoEst().compareTo( o.getCodigoEst() );
			}
		}
		return comp;
	}
	
}
