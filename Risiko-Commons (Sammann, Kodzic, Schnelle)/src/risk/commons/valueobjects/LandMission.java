package risk.commons.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Klasse f�r die Art von Missionen, die das Einnehmen von beliebigen
 * L�ndern zum Ziel hat. Zudem Verweis auf den Spieler, der diese
 * Mission hat.
 * @author Marcel
 * @version 1
 */
public class LandMission extends Mission {
	private static final long serialVersionUID = 8452463312413268324L;

	/** Ben�tigte Zahl an L�ndern zum Sieg */
	private int zielzahl;	
	/** Mindestst�rke auf allen Feldern */
	private int minStaerke;	
	
	/** �berschriebene Siegpr�fungsmethode der Mission-Klasse. Eine LandMission ist dann erf�llt, wenn der Besitz des Spielers,
	 * der diese Mission bekommen hat, mindestens der Zielzahl entspricht, und zudem auf jedem dieser L�nder die ben�tigte
	 * Mindestzahl von Armeen steht. */
	public boolean pruefeSieg() {
		// Spielerbesitz einholen
		ArrayList<Land> besitz = s.getBesitz();
		// Z�hler einf�hren
		int anzahlErfuellt = 0;
		// �ber den Besitz iterieren und mitz�hlen, wie viele L�nder die St�rke-Anforderung erf�llen
		Iterator<Land> iter = besitz.iterator();
		while(iter.hasNext()) {
			Land l = iter.next();
			if (l.getStaerke() >= minStaerke)
				anzahlErfuellt++;
		}
		// Hinterher Abfrage, ob die Zielzahl erreicht worden ist
		if (anzahlErfuellt >= zielzahl)
			return true;
		else return false;
	}

	/** Lege die Zielzahl an L�ndern f�r diese Mission fest */
	public void setAnzahl(int n) { zielzahl = n; }
	/** Gebe die Anzahl L�nder aus, die f�r diese Mission besetzt sein m�ssen */
	public int getAnzahl() { return zielzahl; }

	/** Lege die Mindestst�rke f�r jedes einzunehmende Land fest */
	public void setStaerke(int n) { minStaerke = n; }
	/** Gebe die Mindestst�rke f�r jedes einzunehmende Land aus */
	public int getStaerke() { return minStaerke; }
}
