package risk.client.ui.gui.comp;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Diese Klasse realisiert auf einfache Weise das Anzeigen eines Dialogfensters
 * mit Ja/Nein-Auswahl.
 * @author Yannik
 *
 */
public class OptionDialog {
	
	/** Ausgew�hlte Option des Dialogs gem�� JOptionPane-Konstanten */
	public int input;		
	
	/** Konstruktor mit JDialog-Superframe
	 * @param f		�bergeordneter JDialog
	 * @param s		Text f�r die Nachrichtenbox
	 * @param title	Titel */
	public OptionDialog(JDialog f, String s, String title) {
		input = JOptionPane.showConfirmDialog(f,s,title,JOptionPane.YES_NO_OPTION);
	}
	
	/** Konstruktor mit JFrame-Superframe
	 * @param f		�bergeordneter JDialog
	 * @param s		Text f�r die Nachrichtenbox
	 * @param title	Titel */
	public OptionDialog(JFrame f, String s, String title) {
		input = JOptionPane.showConfirmDialog(f,s,title,JOptionPane.YES_NO_OPTION);
	}
}
