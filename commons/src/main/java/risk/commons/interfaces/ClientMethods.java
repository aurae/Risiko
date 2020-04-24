package risk.commons.interfaces;

import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;


/**
 * Dieses Interface definiert die Schnittstelle von Server zu Client. Alle Methoden, die
 * der Server auf einem Client aufrufen kann, sind hier definiert. Die am häufigsten genutzte
 * Methode ist die Callback-Methode aktualisiere()
 * @author Marcel
 */
public interface ClientMethods {
	
	/** Callback für Update auf der Client-GUI mit Parametern, was sich getan hat */
	public void aktualisiere(Spieler s, Result param);
	
	/** Spielleiter-Status festsetzen */
	public void setGameMaster(boolean b);
	
	/** Spieler zum Client-Objekt ermitteln */
	public Spieler getSpieler();
	
	/** Ist der Client angemeldet? */
	public boolean isLoggedIn();
	
	/** Verbindung zum Server trennen */
	public void disconnect();
}
