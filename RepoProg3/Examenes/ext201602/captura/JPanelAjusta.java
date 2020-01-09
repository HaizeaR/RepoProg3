package ext201602.captura;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

/** JPanel para gr�fico, que ajusta su gr�fico al tama�o del panel 
 * @author Andoni Egu�luz Mor�n
 * Facultad de Ingenier�a - Universidad de Deusto
 */
@SuppressWarnings("serial")
public class JPanelAjusta extends JPanel {
	private ImageIcon i;
	
	/** Construye un nuevo panel partiendo de un gr�fico de fondo
	 * @param i	Gr�fico a incluir
	 */
	public JPanelAjusta( ImageIcon i ) {
		super();
		setLayout( null );
		this.i = i;
	}
	
	/** Cambia el gr�fico del panel
	 * @param icon	Nuevo gr�fico
	 */
	public void setImageIcon(ImageIcon icon) {
		i = icon;
		// setIcon(i);
		repaint();
	};
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (i!=null) {
			Graphics2D g2 = (Graphics2D) g;
			g2.drawImage( i.getImage(), 0, 0, getWidth(), getHeight(), null );
		}
	}
}
