package risk.commons.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Klasse für die Art von Missionen, die das Erobern kompletter
 * Kontinente zum Ziel hat. Bekannt sind den Objekten die zu erobernden
 * Kontinente und der Spieler, der die Aufgabe hat.
 * @author Marcel
 * @version 1
 */
public class KontinentMission extends Mission {
	private static final long serialVersionUID = 4843822209143910457L;

	/** Liste von Kontinenten, die zur Erfüllung dieser KontinentMission eingenommen sein müssen */
	private ArrayList<Kontinent> kontinente;

	/** Konstruktor ohne Parameter */
	public KontinentMission() {
		kontinente = new ArrayList<Kontinent>();
	}

	/** Prüfen der Erfüllung der Mission (überschriebene Methode von Mission):
	 * Eine KontinentMission wird geprüft, indem gefragt wird, ob alle Kontinente,
	 * die für diese Mission eingenommen werden müssen, von einem Spieler eingenommen
	 * worden sind. Wenn das für alle Kontinente zutrifft, ist die Mission erfüllt */
	public boolean pruefeSieg() {
		// Kontinente im Attribut durchgehen und "pruefeEinnahme" fragen
		Iterator<Kontinent> iter = kontinente.iterator();
		boolean klappt = false;
		do {
			// Wenn die Liste erreicht worden ist und die Schleife immer noch läuft...
			if(!iter.hasNext())
				// ..., dann passt alles.
				return true;
			Kontinent k = iter.next();
			klappt = k.pruefeEinnahme(s);
		} while(klappt);
		// Ansonsten, wenn sie vorzeitig abgebrochen wird, passt der Sieg noch nicht.
		return false;
	}
	
	/** Füge einen Kontinent zur Liste aller einzunehmenden Kontinente zu */
	public void addKontinent(Kontinent k) {	kontinente.add(k); }
	/** Gib die Liste aller einzunehmenden Kontinente aus */
	public ArrayList<Kontinent> getKontinente() { return kontinente; }
}
