package risk.client.ui.gui.panels;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JLayeredPane;

import risk.commons.interfaces.PanelInterface;

/**
 * Diese Klasse definiert den "kleinsten gemeinsamen Nenner" für alle eigenen
 * JPanel- und JLayeredPane-Klassen im RISK-Projekt.
 * @author Yannik
 */
public class DefaultPanel extends JLayeredPane implements PanelInterface, Observer {
	private static final long serialVersionUID = 5536249303312240659L;
	
	/** Konstruktor */
	public DefaultPanel() {	super(); }
	/** Update-Methode aus dem PanelInterface */
	public void updateYourself() { }
	/** Update-Methode aus dem Observer-Interface */
	public void update(Observable o, Object org) {	}
}
