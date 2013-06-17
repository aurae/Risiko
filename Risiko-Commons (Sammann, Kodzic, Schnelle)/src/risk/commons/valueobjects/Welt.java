package risk.commons.valueobjects;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;

import risk.commons.valueobjects.Karte;
import risk.commons.collections.BidiMap;

/**
 * Das Welt-Objekt beinhaltet die gesamte Spielumgebung, d.h.
 * alle Kontinente und teilnehmende Spieler.
 * Hinzu kommen die Kenntnisse über die Farbkonstanten des Spiels.
 * Sie liefert jedoch nur die Umgebung, sie kann sie nicht selbst verändern
 * (dafür sind die Verwaltungs-Klassen im domain-Paket zuständig).
 * @author Marcel
 */
public class Welt implements Serializable {
	private static final long serialVersionUID = -9211144788909486943L;

	/** Konstante für die Spielerfarben: ROT */
	public static final Integer RED = 0xCC2222;
	/** Konstante für die Spielerfarben: SCHWARZ */
	public static final Integer BLACK = 0x333333;
	/** Konstante für die Spielerfarben: BLAU */
	public static final Integer BLUE = 0x2222CC;
	/** Konstante für die Spielerfarben: GELB */
	public static final Integer YELLOW = 0xDDDD22;
	/** Konstante für die Spielerfarben: GRÜN */
	public static final Integer GREEN = 0x22CC22;
	/** Konstante für die Spielerfarben: ROSA */
	public static final Integer PINK = 0xDD22DD;
	
	/** Alle Kontinent-Objekte */
	private ArrayList<Kontinent> kontinente;		
	/** Alle Spieler */
	private Spieler[] spieler;						
	/** Verfügbare Spielerfarben */
	private Vector<Integer> farben;					
	/** Farbenhash Int<->String */
	private BidiMap farbHash;						
	/** Nachziehbare Karten */
	private Vector<Karte> kartenstapel;				
	/** Abgelegte Karten */
	private Vector<Karte> ablagestapel;				
	/** Selbstreferenz zur statischen Abfrage */
	private static Welt welt;						

	/** Konstruktor ohne Parameter */
	public Welt() {
		// Vector-Objekte erzeugen
		kartenstapel = new Vector<Karte>();
		ablagestapel = new Vector<Karte>();
		spieler = new Spieler[6];
		Integer[] tempF = {RED,BLACK,YELLOW,GREEN,PINK,BLUE};
		farben = new Vector<Integer>(Arrays.asList(tempF));
		// HashMap erstellen (int-Farben -> String-Farben
		farbHash = new BidiMap();
		farbHash.put(RED,"rot"); farbHash.put(BLACK,"schwarz");
		farbHash.put(YELLOW,"gelb"); farbHash.put(GREEN,"grün");
		farbHash.put(PINK,"pink"); farbHash.put(BLUE,"blau");
		Welt.welt = this;
	}

	/** Kopieren der Eigenschaften des Welt-Objektes in ein neues, das dann zurückgegeben wird */
	public Welt copyWelt() {
		Welt n = new Welt();
		n.setProperties(this);
		return n;
	}

	/** Gib' die HashMap mit der int-String-Zuordnung der Farben zurück */
	public BidiMap getHash() { return farbHash; }
	
	/** Setze die Liste der Kontinente in dieser Welt auf die übergebene Liste */
	public void setKontinente(ArrayList<Kontinent> k) {	kontinente = k; }
	/** Gib' die Kontinente dieser Welt in einer Liste aus */
	public ArrayList<Kontinent> getKontinente() { return kontinente; }
	
	/** Gib' das Spieler-Array aus */
	public Spieler[] getSpieler() { return spieler; }
	/** Setze das Spieler-Array neu */
	public void setSpieler(Spieler[] s) { spieler = s; }
	
	/** Gib' den Kartenstapel zurück. Dort sind alle ziehbaren Karten drin */
	public Vector<Karte> getKartenstapel() { return kartenstapel; }
	/** Gib' den Ablagestapel zurück. Dort sind alle gezogenen Karten drin */
	public Vector<Karte> getAblagestapel() { return ablagestapel; }
	/** Füge eine Karte zum Ablagestapel hinzu */
	public void addAblagestapel(Karte k) { ablagestapel.add(k); }
	
	/** Gib' alle int-Farben in einem Vector zurück */
	public Vector<Integer> getFarben() { return farben; }
	
	/** Gib' die Referenz auf diese Welt zurück */
	public static Welt getWelt() { return welt;	}
	
	/** Adaptieren der Eigenschaften aus der übergebenen Welt-Umgebung
	 * (wird benutzt von der Lade-Methode)
	 * @param other	Welt-Objekt, dessen Eigenschaften kopiert werden */
	public void setProperties(Welt other) {
		this.kontinente = other.kontinente;
		this.ablagestapel = other.ablagestapel;
		this.kartenstapel = other.kartenstapel;
		this.spieler = other.spieler;
		Welt.welt = this;
	}
	
	/** Identifizeren eines Landes auf der Weltkarte über seine FarbMap.
	 * Wird von der GUI benutzt, um zu ermitteln, wo auf der Karte ein Mausklick passiert ist.
	 * @param farbe	int-Farbe an Maus-Position
	 * @return	Land mit der passenden FarbMap, oder null, wenn es keines gibt. */
	public Land getLandByFarbMap(int farbe) {
		ArrayList<Kontinent> alleK = this.getKontinente();
		Iterator<Kontinent> iterK = alleK.iterator();
		while (iterK.hasNext()) {
			Kontinent k = iterK.next();
			Iterator<Land> iterL = k.getLaender().iterator();
			while (iterL.hasNext()) {
				Land l = iterL.next();
				if (l.getFarbMap() == farbe)
					return l;
			}
		}
		return null;
	}
}
