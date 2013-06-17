package risk.client;

import risk.client.ui.gui.RiskClientGUI;

/**
 * Die ausf�hrende Klasse auf Clientseite. Diese Klasse beinhaltet die main()-Methode,
 * �ber die ein Client gestartet werden kann und dann weitere Abl�ufe einleitet.
 * @author Marcel
 */
public class Client {
	/** main(). Als Parameter kann ein anderer Host angegeben werden, dies ist aber optional (Standard: localhost) */
	public static void main(String[] args) {
		// GUI-Objekt erzeugen...
		RiskClientGUI ui = new RiskClientGUI();
		// ...und laufen lassen!
		ui.run(args);
	}
}