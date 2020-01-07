package ord201512.captura;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/** JLabel para gr�fico, que ajusta su gr�fico al tama�o del label 
 * @author Andoni Egu�luz Mor�n
 * Facultad de Ingenier�a - Universidad de Deusto
 */
@SuppressWarnings("serial")
public class JLabelAjusta extends JLabel {
	private ImageIcon i;
	
	/** Construye un nuevo label partiendo de un gr�fico
	 * @param i	Gr�fico a incluir
	 */
	public JLabelAjusta( ImageIcon i ) {
		super( i );
		this.i = i;
	}
	
	/** Cambia el gr�fico del label
	 * @param icon	Nuevo gr�fico
	 */
	public void setImageIcon(ImageIcon icon) {
		i = icon;
		// setIcon(i);
		repaint();
	};
	
	@Override
	protected void paintComponent(Graphics g) {
		if (i!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage( i.getImage(), 0, 0, getWidth(), getHeight(), null );
		}
	}
}
