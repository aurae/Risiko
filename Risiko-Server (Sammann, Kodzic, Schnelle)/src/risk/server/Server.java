package risk.server;

import risk.server.net.ServerEngineNetwork;

/**
 * Die ausf�hrende Klasse auf Serverseite. Diese Klasse beinhaltet die main()-Methode,
 * �ber die ein Server gestartet werden kann und dann weitere Abl�ufe einleitet.
 * @author Marcel
 */
public class Server {
	/** main(). Als Parameter kann ein anderer Host angegeben werden, dies ist aber optional (Standard: localhost) */
	public static void main(String[] args) {
		try {
			// Neue Serverimplementierung starten!
			new ServerEngineNetwork();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}