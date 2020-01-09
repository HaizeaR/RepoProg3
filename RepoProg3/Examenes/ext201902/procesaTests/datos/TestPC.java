package ext201902.procesaTests.datos;

import java.util.ArrayList;

/** Clase para gestionar la información respuesta a un test de Pensamiento Computacional (PC) de 32 preguntas y 3 preguntas de control
 * @author andoni.eguiluz @ ingenieria.deusto.es
 */
public class TestPC implements CargableDesdeCSV{

	private String marcaTemporal;  // Marca temporal del test (formato dd/mm/aaaa hh:mm:ss)
	private String codEstud;       // Código único de estudiante
	private String sexo;           // Sexo de estudiante
	private int edad;              // Edad de estudiante
	private String nomCentro;      // Nombre del centro educativo
	private String localidad;      // Localidad del centro educativo
	private String curso;          // Nivel educativo actual del estudiante
	private String respControl;    // Respuestas a las 3 preguntas de control. String de tres posiciones con un carácter con cada respuesta: ' ' si no se ha contestado, o 'A', 'B', 'C', 'D' para la respuesta dada
	private String evalControl;    // Respuestas a las 3 preguntas de control. String de tres posiciones con un carácter para cada pregunta: ' ' si no se ha contestado, '+' si es correcta, '-' si es incorrecta
	private String respTest;       // Respuestas a las 32 preguntas de test. String de 32 posiciones con un carácter con cada respuesta: ' ' si no se ha contestado, o 'A', 'B', 'C', 'D' para la respuesta dada
	private String evalTest;       // Respuestas a las 32 preguntas de test. String de 32 posiciones con un carácter para cada pregunta: ' ' si no se ha contestado, '+' si es correcta, '-' si es incorrecta
	private int autoevalTest;      // Autopercepción (de 0 a 10) del resultado en el test
	private int autoPercepcionTec; // Autopercepción de capacidad para la informática y los ordenadores (de 0 a 10)
	
	/** Constructor por defecto
	 */
	public TestPC() {
		evalControl = "";
		evalTest = "";
	}
	
	/** Constructor con todos los datos del test
	 * @param marcaTemporal	Marca temporal del test (formato "dd/mm/aaaa hh:mm:ss")
	 * @param codEstud	Código único de estudiante
	 * @param sexo	Sexo de estudiante
	 * @param edad	Edad de estudiante
	 * @param nomCentro	Nombre del centro educativo
	 * @param localidad	Localidad del centro educativo
	 * @param curso	Nivel educativo actual del estudiante
	 * @param respControl	Respuestas a las 3 preguntas de control. String de tres posiciones con un carácter con cada respuesta: ' ' si no se ha contestado, o 'A', 'B', 'C', 'D' para la respuesta dada
	 * @param respTest	Respuestas a las 32 preguntas de test. String de 32 posiciones con un carácter con cada respuesta: ' ' si no se ha contestado, o 'A', 'B', 'C', 'D' para la respuesta dada
	 * @param autoevalTest	Autoevaluación del test (de 0 a 10)
	 * @param autoPercepcionTest	Autopercepción de tecnología (de 0 a 10)
	 */
	public TestPC(String marcaTemporal, String codEstud, String sexo, int edad, String nomCentro, String localidad, String curso, String respControl, String respTest, int autoevalTest, int autoPercepcionTec ) {
		super();
		this.marcaTemporal = marcaTemporal;
		this.codEstud = codEstud;
		this.sexo = sexo;
		this.edad = edad;
		this.nomCentro = nomCentro;
		this.localidad = localidad;
		this.curso = curso;
		this.respControl = respControl;
		this.respTest = respTest;
		calc(); // Calcula la evaluación del test (evalControl y evalTest) 
		this.autoevalTest = autoevalTest;
		this.autoPercepcionTec = autoPercepcionTec;
	}

	// ====================================================================================
	// Métodos de los interfaces implementados (ConvertibleEnTabla, CargableDesdeCSV)
	// ====================================================================================
	
	// Representación en tabla del test con las siguientes columnas:
	// Marca temporal | Código estud | sexo | edad | nomCentro | localidad | curso | evalControl | numControlCorrectas | evalTest | numRespCorrectas | numRespIncorrectas | puntuación | autoevalTest | autopercTec

	@Override
	public int getNumColumnas() {
		return 15;
	}

	@Override
	public String getValorColumna(int col) {
		switch (col) {
			case 0: {
				return marcaTemporal;
			}
			case 1: {
				return codEstud;
			}
			case 2: {
				return sexo;
			}
			case 3: {
				return (edad==-1)?"":"" + edad;
			}
			case 4: {
				return nomCentro;
			}
			case 5: {
				return localidad; 
			}
			case 6: {
				return curso;
			}
			case 7: {
				return evalControl;
			}
			case 8: {
				return "" + getNumControlCorrectas();
			}
			case 9: {
				return evalTest;
			}
			case 10: {
				return "" + getNumTestCorrectas();
			}
			case 11: {
				return "" + getNumTestIncorrectas();
			}
			case 12: {
				return "" + getPuntuacion();
			}
			case 13: {
				return (autoevalTest==-1)?"":"" + autoevalTest;
			}
			case 14: {
				return (autoPercepcionTec==-1)?"":"" + autoPercepcionTec;
			}
		}
		return null;
	}

	@Override
	public void setValorColumna(int col, String valor) {
		switch (col) {
			case 0: {
				marcaTemporal = valor;
				break;
			}
			case 1: {
				codEstud = valor;
				break;
			}
			case 2: {
				sexo = valor;
				break;
			}
			case 3: {
				try {
					if (valor.isEmpty()) edad = -1;
					else edad = Integer.parseInt( valor );
				} catch (NumberFormatException e) { e.printStackTrace(); }
				break;
			}
			case 4: {
				nomCentro = valor;
				break;
			}
			case 5: {
				localidad = valor;
				break;
			}
			case 6: {
				curso = valor;
				break;
			}
			case 7: {
				evalControl = valor;
				calc();  // Recalcula la evaluación
				break;
			}
			case 9: {
				evalTest = valor;
				calc();  // Recalcula la evaluación
				break;
			}
			case 13: {
				try {
					if (valor.isEmpty()) autoevalTest = -1;
					else autoevalTest = Integer.parseInt( valor );
				} catch (NumberFormatException e) { e.printStackTrace(); }
				break;
			}
			case 14: {
				try {
					if (valor.isEmpty()) autoPercepcionTec = -1;
					else autoPercepcionTec = Integer.parseInt( valor );
				} catch (NumberFormatException e) { e.printStackTrace(); }
				break;
			}
			 // Resto de campos (8, 10, 11, 12) no son campos modificables (son calculados) así que no se hace nada con el set
		}
	}

		private static String[] nomsCols = { "Fecha", "CodEst", "Sexo", "Edad", "Centro", "Localidad", "Curso", "evControl", "BienControl", "evTest", "BienTest", "MalTest", "Puntuación", "AutoevalTest", "AutopercTec" };
	@Override
	public String getNombreColumna(int col) {
		if (col<0 || col>14) return "";
		return nomsCols[col];
	}

		private static String[] cabsCast = { "marca temporal", "código de estudiante", "sexo", "edad", "nombre de la escuela", "población", "curso", "", "", "", "", "", "", "de 0 a 10, ¿cómo consideras que te ha salido el test?", "de 0 a 10, ¿cómo consideras que se te dan los ordenadores y la informática?" };
		private static String[] cabsEus  = { "marca temporal", "ikasle kodea", "sexua", "adina", "ikastetxearen izena", "herria", "maila", "", "", "", "", "", "", "0tik 10era, nola uste duzu atera zaizula testa?", "0tik 10era, zure ustez nola moldatzen zara ordenagailuekin eta informatikan?" };
	@Override
	public boolean cargaDesdeCSV(ArrayList<String> cabeceras, ArrayList<String> valores) {
		edad = -1; // Inicializa a valor -1 los campos numéricos
		autoevalTest = -1;
		autoPercepcionTec = -1;
		evalControl = ""; // Inicializa a vacíos los campos calculados
		evalTest = "";
		char[] respsControl = new char[3]; for (int i=0; i<3; i++) respsControl[i] = ' '; // Inicializa las posibles respuestas
		char[] respsTest = new char[32]; for (int i=0; i<32; i++) respsTest[i] = ' '; // Inicializa las posibles respuestas
		for (int i=0; i<valores.size(); i++) {
			if (cabeceras.size()>i) {  // Si no hay cabecera no se procesa el valor (no se sabe qué es)
				String cab = cabeceras.get(i).toLowerCase();
				String valor = valores.get(i);
				boolean encontrada = false;
				if (!cab.isEmpty()) {
					for (int cabPosible=0; cabPosible<cabsCast.length; cabPosible++) {
						if (!cabsCast[cabPosible].isEmpty() && cab.startsWith( cabsCast[cabPosible] )) {  // Cabecera encontrada
							encontrada = true;
							setValorColumna( cabPosible, valor );
						}
					}
					if (!encontrada) {
						for (int cabPosible=0; cabPosible<cabsEus.length; cabPosible++) {
							if (!cabsEus[cabPosible].isEmpty() && cab.startsWith( cabsEus[cabPosible] )) {  // Cabecera encontrada
								encontrada = true;
								setValorColumna( cabPosible, valor );
							}
						}
					}
					if (!encontrada) {
						if (cab.contains( "iii." )) {  // pregunta III de control
							encontrada = true;
							respsControl[2] = (valor.isEmpty()?' ':valor.charAt(0));
						} else if (cab.contains( "ii." )) {  // pregunta II de control
							encontrada = true;
							respsControl[1] = (valor.isEmpty()?' ':valor.charAt(0));
						} else if (cab.contains( "i." )) {  // pregunta I de control
							encontrada = true;
							respsControl[0] = (valor.isEmpty()?' ':valor.charAt(0));
						} else if (cab.contains( "28b." )) {  // pregunta 28b de test
							encontrada = true;
							respsTest[31] = (valor.isEmpty()?' ':valor.charAt(0));
						} else if (cab.contains( "20b." )) {  // pregunta 20b de test
							encontrada = true;
							respsTest[30] = (valor.isEmpty()?' ':valor.charAt(0));
						} else if (cab.contains( "10b." )) {  // pregunta 10b de test
							encontrada = true;
							respsTest[29] = (valor.isEmpty()?' ':valor.charAt(0));
						} else if (cab.contains( "4b." )) {  // pregunta 4b de test
							encontrada = true;
							respsTest[28] = (valor.isEmpty()?' ':valor.charAt(0));
						} else {  // Comprobar si es alguna de las preguntas 1 a 28
							for (int numPreg=28; numPreg>0; numPreg--) {
								if (cab.contains( "" + numPreg )) {
									encontrada = true;
									respsTest[numPreg-1] = (valor.isEmpty()?' ':valor.charAt(0));
									break; // Sale del for
								}
							}
						}
					}
				}
				if (!encontrada && !cab.isEmpty()) {
					if (cab.startsWith( "irakurri eta ulertu ditut jarraibideak" ) ) ;  // Esta columna se desprecia
					else System.err.println( "Cabecera errónea " + cab + " para valor " + valor );
				}
			} else {
				System.err.println( "Cabecera no existente para valor " + valores.get(i) );
			}
		}
		respControl = "";  // Crea el campo de respuestas de control
		for (int i=0; i<respsControl.length; i++) respControl += respsControl[i];
		respTest = "";  // Crea el campo de respuestas de test
		for (int i=0; i<respsTest.length; i++) respTest += respsTest[i];
		calc(); // Calcula los campos calculados de evaluación partiendo de las respuestas
		return true; // Devuelve correctamente la carga
	}
	
	// ====================================================================================
	// Métodos de los valores calculados
	// ====================================================================================
	
	private static String respCorrectasControl = "BCA";  // Respuestas correctas de las 3 preguntas de control
	private static String respCorrectasTest = "BCDDCDABDCCABADDBAACABACBBACCDDD";  // Respuestas correctas de las 32 preguntas de test (1-28,4b,10b,20b,28b)
	
	// Método privado para calcular la evaluación de las preguntas de control y las del test
	private void calc() {
		evalControl = "";
		for (int resp=0; resp<respCorrectasControl.length(); resp++) {
			if (respControl.length() <= resp || respControl.charAt(resp)==' ') {
				evalControl += " ";  // No contestado
			} else if (respControl.charAt(resp) == respCorrectasControl.charAt(resp)) {
				evalControl += "+";  // Contestado correctamente
			} else {
				evalControl += "-";  // Contestado incorrectamente
			}
		}
		evalTest = "";
		for (int resp=0; resp<respCorrectasTest.length(); resp++) {
			if (respTest.length() <= resp || respTest.charAt(resp)==' ') {
				evalTest += " ";  // No contestado
			} else if (respTest.charAt(resp) == respCorrectasTest.charAt(resp)) {
				evalTest += "+";  // Contestado correctamente
			} else {
				evalTest += "-";  // Contestado incorrectamente
			}
		}
	}
	
	/** Devuelve las preguntas de test que se han contestado correctamente
	 * @return	Número de respuestas correctas
	 */
	public int getNumTestCorrectas() {
		int num = 0;
		for (int resp=0; resp<evalTest.length(); resp++) {
			if (evalTest.charAt(resp)=='+') num++;
		}
		return num;
	}

	/** Devuelve las preguntas de test que se han contestado incorrectamente
	 * @return	Número de respuestas incorrectas
	 */
	public int getNumTestIncorrectas() {
		int num = 0;
		for (int resp=0; resp<evalTest.length(); resp++) {
			if (evalTest.charAt(resp)=='-') num++;
		}
		return num;
	}
	
	/** Devuelve la puntuación del test
	 * @return	Puntuación = número de preguntas correctas * 1 - número de respuestas incorrectas * 1
	 */
	public int getPuntuacion() {
		return getNumTestCorrectas() - getNumTestIncorrectas();
	}

	/** Devuelve las preguntas de control que se han contestado correctamente
	 * @return	Número de respuestas correctas
	 */
	public int getNumControlCorrectas() {
		int num = 0;
		for (int resp=0; resp<evalControl.length(); resp++) {
			if (evalControl.charAt(resp)=='+') num++;
		}
		return num;
	}

	// ====================================================================================
	// Métodos get y set de atributos
	// ====================================================================================
	
	public String getMarcaTemporal() {
		return marcaTemporal;
	}

	public void setMarcaTemporal(String marcaTemporal) {
		this.marcaTemporal = marcaTemporal;
	}

	public String getCodEstud() {
		return codEstud;
	}

	public void setCodEstud(String codEstud) {
		this.codEstud = codEstud;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public int getEdad() {
		return edad;
	}

	public void setEdad(int edad) {
		this.edad = edad;
	}

	public String getNomCentro() {
		return nomCentro;
	}

	public void setNomCentro(String nomCentro) {
		this.nomCentro = nomCentro;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCurso() {
		return curso;
	}

	public void setCurso(String curso) {
		this.curso = curso;
	}

	public String getRespControl() {
		return respControl;
	}

	public void setRespControl(String respControl) {
		this.respControl = respControl;
		calc();
	}

	public String getEvalControl() {
		return evalControl;
	}

	public String getRespTest() {
		return respTest;
	}

	public void setRespTest(String respTest) {
		this.respTest = respTest;
		calc();
	}

	public String getEvalTest() {
		return evalTest;
	}

	public int getAutoevalTest() {
		return autoevalTest;
	}

	public void setAutoevalTest(int autoevalTest) {
		this.autoevalTest = autoevalTest;
	}

	public int getAutoPercepcionTec() {
		return autoPercepcionTec;
	}

	public void setAutoPercepcionTec(int autoPercepcionTec) {
		this.autoPercepcionTec = autoPercepcionTec;
	}	

	@Override
	public String toString() {
		return codEstud + "(" + edad + ") -> " + evalTest + " " + getPuntuacion();
	}
	
}
