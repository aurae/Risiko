package risk.commons.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Klasse f�r die Art von Missionen, die das Erobern kompletter
 * Kontinente zum Ziel hat. Bekannt sind den Objekten die zu erobernden
 * Kontinente und der Spieler, der die Aufgabe hat.
 * @author Marcel
 * @version 1
 */
public class KontinentMission extends Mission {
	private static final long serialVersionUID = 4843822209143910457L;

	/** Liste von Kontinenten, die zur Erf�llung dieser KontinentMission eingenommen sein m�ssen */
	private ArrayList<Kontinent> kontinente;

	/** Konstruktor ohne Parameter */
	public KontinentMission() {
		kontinente = new ArrayList<Kontinent>();
	}

	/** Pr�fen der Erf�llung der Mission (�berschriebene Methode von Mission):
	 * Eine KontinentMission wird gepr�ft, indem gefragt wird, ob alle Kontinente,
	 * die f�r diese Mission eingenommen werden m�ssen, von einem Spieler eingenommen
	 * worden sind. Wenn das f�r alle Kontinente zutrifft, ist die Mission erf�llt */
	public boolean pruefeSieg() {
		// Kontinente im Attribut durchgehen und "pruefeEinnahme" fragen
		Iterator<Kontinent> iter = kontinente.iterator();
		boolean klappt = false;
		do {
			// Wenn die Liste erreicht worden ist und die Schleife immer noch l�uft...
			if(!iter.hasNext())
				// ..., dann passt alles.
				return true;
			Kontinent k = iter.next();
			klappt = k.pruefeEinnahme(s);
		} while(klappt);
		// Ansonsten, wenn sie vorzeitig abgebrochen wird, passt der Sieg noch nicht.
		return false;
	}
	
	/** F�ge einen Kontinent zur Liste aller einzunehmenden Kontinente zu */
	public void addKontinent(Kontinent k) {	kontinente.add(k); }
	/** Gib die Liste aller einzunehmenden Kontinente aus */
	public ArrayList<Kontinent> getKontinente() { return kontinente; }
}
