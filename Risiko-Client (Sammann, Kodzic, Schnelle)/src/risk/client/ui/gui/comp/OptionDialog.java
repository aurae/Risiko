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
	
	/** Ausgewählte Option des Dialogs gemäß JOptionPane-Konstanten */
	public int input;		
	
	/** Konstruktor mit JDialog-Superframe
	 * @param f		übergeordneter JDialog
	 * @param s		Text für die Nachrichtenbox
	 * @param title	Titel */
	public OptionDialog(JDialog f, String s, String title) {
		input = JOptionPane.showConfirmDialog(f,s,title,JOptionPane.YES_NO_OPTION);
	}
	
	/** Konstruktor mit JFrame-Superframe
	 * @param f		übergeordneter JDialog
	 * @param s		Text für die Nachrichtenbox
	 * @param title	Titel */
	public OptionDialog(JFrame f, String s, String title) {
		input = JOptionPane.showConfirmDialog(f,s,title,JOptionPane.YES_NO_OPTION);
	}
}
