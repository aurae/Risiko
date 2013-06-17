package risk.commons.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Von dieser Klasse existieren sieben Instanzen im Spiel. Sie
 * repr�sentieren Kontinente und beinhalten deshalb festgelegte
 * L�nderobjekte und werden in einer bestimmten Farbe gezeichnet.
 * @author Marcel
 * @version 1
 */
public class Kontinent implements Serializable {
	private static final long serialVersionUID = -5221165094596015605L;

	/** Die Konstante f�r den Dummy-Kontinent (wenn in einer Mission angegeben ist, dass ein beliebiger Kontinent befreit werden soll) */
	public static final int DUMMY = 1337;

	/** Die L�nder des Kontinents */
	private ArrayList<Land> laender;
	/** Name des Kontinents */
	private String name;
	
	/** Konstruktor ohne Parameter */
	public Kontinent() { }
	
	/** Diese Methode kann pr�fen, ob ein kompletter Kontinent von einer
	 * bestimmten Person erobert worden ist.
	 * @param derSpieler	Spieler-Objekt, das auf Alleinherrschaft gepr�ft werden soll
	 * @return true, wenn dies zutrifft, false sonst */
	public boolean pruefeEinnahme(Spieler derSpieler) {
		// Mithilfe einer Schleife wird gepr�ft,
		// ob alle L�nder des Kontinents dem
		// �bergebenen Spieler-Objekt geh�ren
		Spieler aktS;
		for(int i=0; i < this.laender.size(); i++) {
			aktS = this.laender.get(i).getSpieler();
			// Bei einer Ungleichheit wird die Schleife beendet
			if (!aktS.equals(derSpieler))
				return false;
		}
		// Wenn die Schleife nicht beendet worden ist, wird true zur�ckgegeben.
		return true;
	}

	/** Diesem Kontinent seine zugeh�rigen L�nder mitteilen */
	public void setLaender(ArrayList<Land> l) {	this.laender = l; }
	/** Die L�nderliste dieses Kontinents zur�ckgeben */
	public ArrayList<Land> getLaender() { return laender; }
	/** Den Namen dieses Kontinents setzen */
	public void setName(String name) { this.name = name; }
	/** Den Namen dieses Kontinents zur�ckgeben */
	public String getName() { return name; }

	/** String-Darstellung f�r println  */
	public String toString() {
		String res = name + ":\n";
		Iterator<Land> iterL = laender.iterator();
		while(iterL.hasNext())
			res += "\t" + iterL.next() + "\n";
		return res;
	}
}
