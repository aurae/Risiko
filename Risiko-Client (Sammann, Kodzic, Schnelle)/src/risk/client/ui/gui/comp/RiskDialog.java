package risk.client.ui.gui.comp;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Diese Klasse realisiert den "kleinsten gemeinsamen Nenner" aller Dialog-Klassen
 * des RISK-Projektes. Alle Erweiterungen von RiskDialog müssen die Methoden
 * getInput() und getButtons() sinnvoll überschreiben.
 * @author Marcel
 *
 */
public abstract class RiskDialog extends JDialog {
	private static final long serialVersionUID = 8391795051815126700L;
	
	/** Inhalts-Panel */
	protected final JPanel contentPanel = new JPanel();
	
	/** Konstruktor mit Frame ohne Größenangabe. Es wird eine Standardgröße für
	 * das Dialogfenster ausgewählt, wenn beim Konstruktor keine Wunschgröße
	 * übergeben worden ist.
	 * @param frame	Übergeordneter JFrame */
	public RiskDialog(JFrame frame) { this(frame, new Dimension(450,293)); }
	
	/** Konstruktor mit Superframe und Wunschgröße
	 * @param frame	Übergeordneter JFrame
	 * @param size	Wunschgröße */
	public RiskDialog(JFrame frame, Dimension size) {
		// Modalität und unveränderliche Größe definieren
		setResizable(false);
			
		// Dialog positionieren: Dies soll genau in der Mitte des Eltern-Panels (also des RiskClientGUI-Frames) geschehen.
		// Dazu: Größe des Frame holen
		Dimension d = frame.getSize();
		// Danach Positionierung (gewissermaßen "Offset") holen
		Point pos = frame.getLocationOnScreen();
		// Größen für das Objekt setzen
		contentPanel.setMinimumSize(size);
		contentPanel.setPreferredSize(size);
		contentPanel.setMaximumSize(size);
		// Positionierung: Offset + (Frame-Dimension/2) - (Dialog-Dimension/2)
		setBounds(pos.x + (d.width / 2) - (size.width / 2), pos.y + (d.height / 2) - (size.height / 2), size.width, size.height);
	}
	
	/** Rückgabe der beiden Buttons in einem JButton-Array. Stelle 0: OK, Stelle 1: CANCEL */
	public JButton[] getButtons() {	return null; }
	/** Warten-Methode, die den Dialog blockiert, solange keine Antwort erfolgt ist */
	public void wartenLassen() { }
	/** Rückgabe des Wertes, der in das Textfeld eingetragen worden oder über den Slider eingestellt ist */
	public int getInput() { return -1; }
	
	
}
