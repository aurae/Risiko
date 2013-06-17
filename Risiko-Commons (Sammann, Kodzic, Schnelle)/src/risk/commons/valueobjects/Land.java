package risk.commons.valueobjects;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import risk.commons.components.ArmeenButton;

/**
 * In dieser Klasse wird ein Land auf der Weltkarte des Spielbrettes
 * definiert. Jedes Land ist einem Kontinenten zugeordnet, und f�r
 * jedes der 42 L�nder existiert ein Objekt. Ein Land kennt au�erdem
 * den Spieler, der es kontrolliert, und die Armeenst�rke auf ihm.
 * Die Grundinformationen werden aus einer externen Datei ausgelesen.
 * @author Marcel
 * @version 1
 */
public class Land implements Serializable {
	private static final long serialVersionUID = 6042071268597509668L;
	
	/** Der Name als String */
	private String kuerzel;			
	/** Die angrenzenden L�nder-Objekte */
	private ArrayList<Land> nachbaren;
	/** Die angrenzenden L�nder (String-Form) */
	private ArrayList<String> strnachb;
	
	/** GUI-Komponente f�r dieses Land */
	private ArmeenButton button;
	/** Position des Buttons auf der Weltkarte (lt. XML) */
	private Point buttonPos;
	/** Symbol, das auf der zugeh�rigen Land-Karte ist */
	private int symbol;
	/** Farb-Codierung (wichtig f�r die Identifizierung durch die Maus) */
	private int farbMap;
	/** Der Spieler, der es besitzt */
	private Spieler spieler;
	/** Die Armeenst�rke */
	private int staerke;
	/** Ist true, wenn Einheiten in diesem Zug auf dieses Land bewegt worden sind */
	private boolean bewegt;	
	
	/** Konstruktor
	 * @param name K�rzel f�r Darstellung */
	public Land(String name) {
		this.kuerzel = name;
		this.bewegt = false;
		this.staerke = 1;
		this.buttonPos = new Point(0,0);
		this.strnachb = new ArrayList<String>();
		this.nachbaren = new ArrayList<Land>();
	}
	
	/** Besitzer dieses Landes setzen */
	public boolean setSpieler(Spieler s) {
		if(s.equals(this.spieler))
			return false;
		this.spieler = s;
		return true;
	}
	/** Besitzer dieses Landes zur�ckgeben */
	public Spieler getSpieler() { return spieler; }
	
	/** Addiert n auf die Armeenst�rke */
	public void plusStaerke(int n) { this.staerke += n; }
	/** Subtrahiert n von der Armeenst�rke (Aufrufer muss pr�fen, ob das geht) */
	public void minusStaerke(int n) { this.staerke -= n; }
	/** Gibt die St�rke der Armeen auf diesem Land zur�ck */
	public int getStaerke() { return this.staerke; }
	/** Setzt die St�rke der Armeen auf diesem Land */
	public void setStaerke(int n) { this.staerke = n; }

	/** Gibt an, ob das Land in dem aktiven Zug benutzt worden ist */
	public boolean getBewegt() { return this.bewegt; }
	/** Setzt den Zustand dieses Landes auf den �bergebenen boolean */
	public void setBewegt(boolean b) { this.bewegt = b; }

	/** F�ge ein Land zur Liste der Nachbaren f�r dieses Land hinzu */
	public void addNachbar(Land n) { nachbaren.add(n); }
	/** Gib' die Liste der Nachbarl�nder dieses Landes zur�ck */
	public ArrayList<Land> getNachbaren() {	return this.nachbaren; }
	/** F�ge einen L�ndernamen zu der entsprechenden String-Liste hinzu */
	public void addStrNachb(String s) {	this.strnachb.add(s); }
	/** Gib' die String-Liste aller Nachbarl�nder aus */
	public ArrayList<String> getStrNachb() { return strnachb; }

	/** Setze den FarbMap-Wert f�r dieses Land auf die �bergebene int-Farbe */
	public void setFarbMap(int z) { this.farbMap = z; }
	/** Gib' den FarbMap-Wert f�r dieses Land zur�ck */
	public int getFarbMap() { return farbMap; }
	/** Gib' den Namen dieses Landes aus */
	public String getName() { return kuerzel; }

	/** Setze das Symbol f�r dieses Land auf die �bergebene Konstante */
	public void setSymbol(int s) { symbol = s; }
	/** Gib' das Symbol dieses Landes zur�ck */
	public int getSymbol() { return symbol; }

	/** Lege einen ArmeenButton f�r dieses Land fest */
	public void setButton(ArmeenButton a) { button = a; }
	/** Gib' die Referenz auf den ArmeenButton f�r dieses Land zur�ck */
	public ArmeenButton getButton() { return button; }
	
	/** Setze die Position des ArmeenButtons f�r dieses Land auf die �bergebenen Koordinaten */
	public void setButtonPos(Point p) { buttonPos = p; }
	/** Gib' die Position des ArmeenButtons f�r dieses Land zur�ck */
	public Point getButtonPos() { return buttonPos; }
	
	/** String-Darstellung f�r println */
	public String toString() { return kuerzel; }
	
	/** Pr�fung auf Gleichheit zweier Land-Objekte: Sie sind genau dann gleich, wenn sie den gleichen Namen haben */
	public boolean equals(Object obj) {
		Land objLand;
		if (obj instanceof Land) {
			objLand = (Land) obj;
		} else if (obj instanceof String) {
			return this.kuerzel.equals(obj);
		} else return false;
		return this.kuerzel.equals(objLand.getName());
	}
	
	/** HashCode-Berechnung f�r ein Land-Objekt */
	public int hashCode() {
		int result = 17;
		result = 37 * result + kuerzel.hashCode();
		return result;
	}
	
	/** Pr�fen, ob zwei L�nder Nachbarn sind
	 * @return true, wenn's so ist, ansonsten false. Verbl�ffend! */
	public static boolean sindBenachbart(Land l1, Land l2) {
		// Wenn eines der beiden L�nder null ist, sofort false zur�ckgeben
		if (l1 == null || l2 == null) return false;
		// Zwei Landobjekte definieren, abh�ngig davon, wer weniger Nachbarl�nder hat
		Land land = (l1.getNachbaren().size() < l2.getNachbaren().size()) ? l1 : l2;
		Land other =(l1.getNachbaren().size() < l2.getNachbaren().size()) ? l2 : l1;
		// Das Land mit der kleineren Nachbar-Liste wird durchiteriert
		Iterator<Land> iter = land.getNachbaren().iterator();
		while (iter.hasNext()) {
			// Wenn das andere Land mit einem in der Liste �bereinstimmt, true zur�ckgeben
			if (other.equals(iter.next()))
				return true;
		}
		// Wenn nichts gefunden wurde, also die Schleife nicht abgebrochen wurde, false zur�ckgeben.
		return false;	
	}
	
	/** Kopiert die Eigenschaften vom ersten �bergebenen Land in das zweite
	 * @param von	Land, von dem kopiert wird
	 * @param nach	Land, in das kopiert wird */
	public static void copy(Land von, Land nach) {
		nach.setBewegt(von.getBewegt());
		nach.setSpieler(von.getSpieler());
		nach.setStaerke(von.getStaerke());
	}
}
