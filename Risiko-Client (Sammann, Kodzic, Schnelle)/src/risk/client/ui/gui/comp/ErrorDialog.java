package risk.client.ui.gui.comp;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Instanzen dieser Klasse werden dann vom MainPanel generiert, wenn im Programmablauf
 * ein Fehler auftritt. Die Konstruktoren dieser Klasse nehmen benutzerdefinierte
 * Titel und Nachrichten an
 * @author Yannik
 */
public class ErrorDialog {

	/** Konstruktor mit JDialog als Superframe
	 * @param f		Super-JDialog
	 * @param s		Text für die Nachrichtenbox
	 * @param title	Titel */
	public ErrorDialog(JDialog f, String s, String title) {
		JOptionPane.showMessageDialog(f,s,title,
				JOptionPane.ERROR_MESSAGE,new ImageIcon(".\\images\\error.png"));
	}
	
	/** Konstruktor mit JFrame als Superframe
	 * @param f		Super-JFrame
	 * @param s		Text für die Nachrichtenbox
	 * @param title	Titel */
	public ErrorDialog(JFrame f, String s, String title) {
		JOptionPane.showMessageDialog(f,s,title,
				JOptionPane.ERROR_MESSAGE,new ImageIcon(".\\images\\error.png"));
	}
}
