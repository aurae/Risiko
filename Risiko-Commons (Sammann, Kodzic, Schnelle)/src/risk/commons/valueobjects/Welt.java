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
 * Hinzu kommen die Kenntnisse �ber die Farbkonstanten des Spiels.
 * Sie liefert jedoch nur die Umgebung, sie kann sie nicht selbst ver�ndern
 * (daf�r sind die Verwaltungs-Klassen im domain-Paket zust�ndig).
 * @author Marcel
 */
public class Welt implements Serializable {
	private static final long serialVersionUID = -9211144788909486943L;

	/** Konstante f�r die Spielerfarben: ROT */
	public static final Integer RED = 0xCC2222;
	/** Konstante f�r die Spielerfarben: SCHWARZ */
	public static final Integer BLACK = 0x333333;
	/** Konstante f�r die Spielerfarben: BLAU */
	public static final Integer BLUE = 0x2222CC;
	/** Konstante f�r die Spielerfarben: GELB */
	public static final Integer YELLOW = 0xDDDD22;
	/** Konstante f�r die Spielerfarben: GR�N */
	public static final Integer GREEN = 0x22CC22;
	/** Konstante f�r die Spielerfarben: ROSA */
	public static final Integer PINK = 0xDD22DD;
	
	/** Alle Kontinent-Objekte */
	private ArrayList<Kontinent> kontinente;		
	/** Alle Spieler */
	private Spieler[] spieler;						
	/** Verf�gbare Spielerfarben */
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
		farbHash.put(YELLOW,"gelb"); farbHash.put(GREEN,"gr�n");
		farbHash.put(PINK,"pink"); farbHash.put(BLUE,"blau");
		Welt.welt = this;
	}

	/** Kopieren der Eigenschaften des Welt-Objektes in ein neues, das dann zur�ckgegeben wird */
	public Welt copyWelt() {
		Welt n = new Welt();
		n.setProperties(this);
		return n;
	}

	/** Gib' die HashMap mit der int-String-Zuordnung der Farben zur�ck */
	public BidiMap getHash() { return farbHash; }
	
	/** Setze die Liste der Kontinente in dieser Welt auf die �bergebene Liste */
	public void setKontinente(ArrayList<Kontinent> k) {	kontinente = k; }
	/** Gib' die Kontinente dieser Welt in einer Liste aus */
	public ArrayList<Kontinent> getKontinente() { return kontinente; }
	
	/** Gib' das Spieler-Array aus */
	public Spieler[] getSpieler() { return spieler; }
	/** Setze das Spieler-Array neu */
	public void setSpieler(Spieler[] s) { spieler = s; }
	
	/** Gib' den Kartenstapel zur�ck. Dort sind alle ziehbaren Karten drin */
	public Vector<Karte> getKartenstapel() { return kartenstapel; }
	/** Gib' den Ablagestapel zur�ck. Dort sind alle gezogenen Karten drin */
	public Vector<Karte> getAblagestapel() { return ablagestapel; }
	/** F�ge eine Karte zum Ablagestapel hinzu */
	public void addAblagestapel(Karte k) { ablagestapel.add(k); }
	
	/** Gib' alle int-Farben in einem Vector zur�ck */
	public Vector<Integer> getFarben() { return farben; }
	
	/** Gib' die Referenz auf diese Welt zur�ck */
	public static Welt getWelt() { return welt;	}
	
	/** Adaptieren der Eigenschaften aus der �bergebenen Welt-Umgebung
	 * (wird benutzt von der Lade-Methode)
	 * @param other	Welt-Objekt, dessen Eigenschaften kopiert werden */
	public void setProperties(Welt other) {
		this.kontinente = other.kontinente;
		this.ablagestapel = other.ablagestapel;
		this.kartenstapel = other.kartenstapel;
		this.spieler = other.spieler;
		Welt.welt = this;
	}
	
	/** Identifizeren eines Landes auf der Weltkarte �ber seine FarbMap.
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
