package risk.commons.valueobjects;

import java.io.Serializable;

/**
 * Ein Karten-Objekt entspricht einer Handkarte aus dem Brettspiel.
 * Sie enthält Informationen wie einen Titel, ein Symbol und ein Land,
 * zu dem sie gehört. Die Karten-Informationen werden aus einer
 * Datenbank ausgelesen.
 * @author Marcel
 * @version 1
 */
public class Karte implements Serializable {
	private static final long serialVersionUID = -6680213567225696770L;
	
	// Konstanten
	public static final int REITER = 2;
	public static final int SOLDAT = 3;
	public static final int KANONE = 5;
	public static final int JOKER = 10;
	
	public static final int RSK = REITER*SOLDAT*KANONE;
	public static final int RRR = REITER*REITER*REITER;
	public static final int KKK = KANONE*KANONE*KANONE;
	public static final int SSS = SOLDAT*SOLDAT*SOLDAT;
	public static final int RSJ = REITER*SOLDAT*JOKER;
	public static final int RJK = REITER*JOKER*KANONE;
	public static final int JSK = JOKER*SOLDAT*KANONE;
	public static final int JJK = JOKER*JOKER*KANONE;
	public static final int JSJ = JOKER*SOLDAT*JOKER;
	public static final int RJJ = REITER*JOKER*JOKER;
	
	
	// Attribute
	/** Titel / Name */
	private String titel;
	/** Symbol (Zugriff auf Konstanten) */
	private int symbol;
	
	/** Konstruktor, der ein Land-Objekt bekommt und daraus Titel und Symbol generiert
	 * @param l	Land-Objekt, dessen Name und Symbol gespeichert werden */
	public Karte(Land l) {
		this.titel = l.getName();
		this.symbol = l.getSymbol();
		//this.land = l;
	}
	/** Konstruktor mit String-Parameter, welcher für die beiden JOKER-Karten benutzt wird */
	public Karte(String s) {
		this.titel = s;
		this.symbol = JOKER;
	}

	/** Rückgabe des Symbols auf der Karte */
	public int getSymbol() { return symbol; }
	/** Rückgabe des Titels der Karte */
	public String getTitel() { return titel; }
	
	/** String-Darstellung des Karten-Objekts */
	public String toString() {
		String s = symbol == REITER ? "REITER" : (symbol == SOLDAT ? "SOLDAT" : (symbol == KANONE ? "KANONE" : "JOKER"));
		return (s + ": " + titel);
	}
}
