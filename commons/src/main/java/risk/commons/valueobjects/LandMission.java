package risk.commons.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Klasse für die Art von Missionen, die das Einnehmen von beliebigen
 * Ländern zum Ziel hat. Zudem Verweis auf den Spieler, der diese
 * Mission hat.
 * @author Marcel
 * @version 1
 */
public class LandMission extends Mission {
	private static final long serialVersionUID = 8452463312413268324L;

	/** Benötigte Zahl an Ländern zum Sieg */
	private int zielzahl;	
	/** Mindeststärke auf allen Feldern */
	private int minStaerke;	
	
	/** Überschriebene Siegprüfungsmethode der Mission-Klasse. Eine LandMission ist dann erfüllt, wenn der Besitz des Spielers,
	 * der diese Mission bekommen hat, mindestens der Zielzahl entspricht, und zudem auf jedem dieser Länder die benötigte
	 * Mindestzahl von Armeen steht. */
	public boolean pruefeSieg() {
		// Spielerbesitz einholen
		ArrayList<Land> besitz = s.getBesitz();
		// Zähler einführen
		int anzahlErfuellt = 0;
		// Über den Besitz iterieren und mitzählen, wie viele Länder die Stärke-Anforderung erfüllen
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

	/** Lege die Zielzahl an Ländern für diese Mission fest */
	public void setAnzahl(int n) { zielzahl = n; }
	/** Gebe die Anzahl Länder aus, die für diese Mission besetzt sein müssen */
	public int getAnzahl() { return zielzahl; }

	/** Lege die Mindeststärke für jedes einzunehmende Land fest */
	public void setStaerke(int n) { minStaerke = n; }
	/** Gebe die Mindeststärke für jedes einzunehmende Land aus */
	public int getStaerke() { return minStaerke; }
}
