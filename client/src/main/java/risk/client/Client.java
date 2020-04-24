package risk.client;

import risk.client.ui.gui.RiskClientGUI;

import java.io.IOException;

/**
 * Die ausführende Klasse auf Clientseite. Diese Klasse beinhaltet die main()-Methode,
 * über die ein Client gestartet werden kann und dann weitere Abläufe einleitet.
 * @author Marcel
 */
public class Client {
	/** main(). Als Parameter kann ein anderer Host angegeben werden, dies ist aber optional (Standard: localhost) */
	public static void main(String[] args) throws IOException {
		// GUI-Objekt erzeugen...
		RiskClientGUI ui = new RiskClientGUI();
		// ...und laufen lassen!
		ui.run(args);
	}
}
