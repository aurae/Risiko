package risk.commons.valueobjects;

import java.io.Serializable;

/**
 * Objekte dieser Klasse dienen als Hülle für einen potentiellen Angriff.
 * Gespeichert werden in einem Angriff-Objekt Angreifer und Verteidiger.
 * Das war's aber eigentlich auch schon.
 * @author Marcel
 */
public class Angriff implements Serializable {
	private static final long serialVersionUID = -6143646728184426324L;

	/** Angreifendes Land */
	private Land von;
	/** Angegriffenes Land */
	private Land nach;
	/** Zahl der angreifenden Armeen */
	private int angrStr;
	/** Zahl der verteidigenden Armeen */
	private int vertStr;
	
	/** Konstruktor, der nur zwei Land-Referenzen übermittelt bekommt
	 * @param v			Angreifendes Land
	 * @param n			Angegriffenes Land */
	public Angriff(Land v, Land n) {
		von = v;
		nach = n;
	}
	
	/** Konstruktor, der zwei Landreferenzen und direkte Zahlen der Armeen bekommt
	 * @param v			Angreifendes Land
	 * @param n			Angegriffenes Land
	 * @param angrStr	Zahl der Angreifer
	 * @param vertStr	Zahl der Verteidiger */
	public Angriff(Land v, Land n, int angrStr, int vertStr) {
		this(v,n);
		this.setAngrStr(angrStr);
		this.setVertStr(vertStr);
	}
	
	/** Rückgabe des angreifenden Landes */
	public Land getVon() { return von; }
	/** Rückgabe des angegriffenen Landes */
	public Land getNach()  { return nach; }
	/** Rückgabe des Angreifers */
	public Spieler getAngreifer() { return von.getSpieler(); }
	/** Rückgabe des Verteidigers (nach erfolgreichem Angriff ist dies auch der Angreifer, vorsicht!) */
	public Spieler getVerteidiger() { return nach.getSpieler(); }
	/** Rückgabe der Angreiferzahl */
	public int getAngrStr() { return angrStr; }
	/** Setzen der Angreiferzahl */
	public void setAngrStr(int angrStr) { this.angrStr = angrStr; }
	/** Rückgabe der Verteidigerzahl */
	public int getVertStr() { return vertStr; }
	/** Setzen der Verteidigerzahl */
	public void setVertStr(int vertStr) { this.vertStr = vertStr; }
	
	/** Darstellen eines Angriff-Objekts über println-Zugriffe*/
	public String toString() {
		return von.getName()+"\t(Stärke: "+von.getStaerke()+", Spieler: "+getAngreifer().getName()+")\t-->\t" + 
		nach.getName()+"\t(Stärke: "+nach.getStaerke()+", Spieler: "+getVerteidiger().getName()+")";
	}
}
