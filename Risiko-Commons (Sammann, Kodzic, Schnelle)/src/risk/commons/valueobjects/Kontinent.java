package risk.commons.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Von dieser Klasse existieren sieben Instanzen im Spiel. Sie
 * repräsentieren Kontinente und beinhalten deshalb festgelegte
 * Länderobjekte und werden in einer bestimmten Farbe gezeichnet.
 * @author Marcel
 * @version 1
 */
public class Kontinent implements Serializable {
	private static final long serialVersionUID = -5221165094596015605L;

	/** Die Konstante für den Dummy-Kontinent (wenn in einer Mission angegeben ist, dass ein beliebiger Kontinent befreit werden soll) */
	public static final int DUMMY = 1337;

	/** Die Länder des Kontinents */
	private ArrayList<Land> laender;
	/** Name des Kontinents */
	private String name;
	
	/** Konstruktor ohne Parameter */
	public Kontinent() { }
	
	/** Diese Methode kann prüfen, ob ein kompletter Kontinent von einer
	 * bestimmten Person erobert worden ist.
	 * @param derSpieler	Spieler-Objekt, das auf Alleinherrschaft geprüft werden soll
	 * @return true, wenn dies zutrifft, false sonst */
	public boolean pruefeEinnahme(Spieler derSpieler) {
		// Mithilfe einer Schleife wird geprüft,
		// ob alle Länder des Kontinents dem
		// übergebenen Spieler-Objekt gehören
		Spieler aktS;
		for(int i=0; i < this.laender.size(); i++) {
			aktS = this.laender.get(i).getSpieler();
			// Bei einer Ungleichheit wird die Schleife beendet
			if (!aktS.equals(derSpieler))
				return false;
		}
		// Wenn die Schleife nicht beendet worden ist, wird true zurückgegeben.
		return true;
	}

	/** Diesem Kontinent seine zugehörigen Länder mitteilen */
	public void setLaender(ArrayList<Land> l) {	this.laender = l; }
	/** Die Länderliste dieses Kontinents zurückgeben */
	public ArrayList<Land> getLaender() { return laender; }
	/** Den Namen dieses Kontinents setzen */
	public void setName(String name) { this.name = name; }
	/** Den Namen dieses Kontinents zurückgeben */
	public String getName() { return name; }

	/** String-Darstellung für println  */
	public String toString() {
		String res = name + ":\n";
		Iterator<Land> iterL = laender.iterator();
		while(iterL.hasNext())
			res += "\t" + iterL.next() + "\n";
		return res;
	}
}
