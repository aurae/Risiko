package risk.client.ui.gui.comp;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Diese Klasse realisiert den "kleinsten gemeinsamen Nenner" aller Dialog-Klassen
 * des RISK-Projektes. Alle Erweiterungen von RiskDialog m�ssen die Methoden
 * getInput() und getButtons() sinnvoll �berschreiben.
 * @author Marcel
 *
 */
public abstract class RiskDialog extends JDialog {
	private static final long serialVersionUID = 8391795051815126700L;
	
	/** Inhalts-Panel */
	protected final JPanel contentPanel = new JPanel();
	
	/** Konstruktor mit Frame ohne Gr��enangabe. Es wird eine Standardgr��e f�r
	 * das Dialogfenster ausgew�hlt, wenn beim Konstruktor keine Wunschgr��e
	 * �bergeben worden ist.
	 * @param frame	�bergeordneter JFrame */
	public RiskDialog(JFrame frame) { this(frame, new Dimension(450,293)); }
	
	/** Konstruktor mit Superframe und Wunschgr��e
	 * @param frame	�bergeordneter JFrame
	 * @param size	Wunschgr��e */
	public RiskDialog(JFrame frame, Dimension size) {
		// Modalit�t und unver�nderliche Gr��e definieren
		setResizable(false);
			
		// Dialog positionieren: Dies soll genau in der Mitte des Eltern-Panels (also des RiskClientGUI-Frames) geschehen.
		// Dazu: Gr��e des Frame holen
		Dimension d = frame.getSize();
		// Danach Positionierung (gewisserma�en "Offset") holen
		Point pos = frame.getLocationOnScreen();
		// Gr��en f�r das Objekt setzen
		contentPanel.setMinimumSize(size);
		contentPanel.setPreferredSize(size);
		contentPanel.setMaximumSize(size);
		// Positionierung: Offset + (Frame-Dimension/2) - (Dialog-Dimension/2)
		setBounds(pos.x + (d.width / 2) - (size.width / 2), pos.y + (d.height / 2) - (size.height / 2), size.width, size.height);
	}
	
	/** R�ckgabe der beiden Buttons in einem JButton-Array. Stelle 0: OK, Stelle 1: CANCEL */
	public JButton[] getButtons() {	return null; }
	/** Warten-Methode, die den Dialog blockiert, solange keine Antwort erfolgt ist */
	public void wartenLassen() { }
	/** R�ckgabe des Wertes, der in das Textfeld eingetragen worden oder �ber den Slider eingestellt ist */
	public int getInput() { return -1; }
	
	
}
