package risk.commons.valueobjects;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import risk.commons.components.ArmeenButton;

/**
 * In dieser Klasse wird ein Land auf der Weltkarte des Spielbrettes
 * definiert. Jedes Land ist einem Kontinenten zugeordnet, und für
 * jedes der 42 Länder existiert ein Objekt. Ein Land kennt außerdem
 * den Spieler, der es kontrolliert, und die Armeenstärke auf ihm.
 * Die Grundinformationen werden aus einer externen Datei ausgelesen.
 * @author Marcel
 * @version 1
 */
public class Land implements Serializable {
	private static final long serialVersionUID = 6042071268597509668L;
	
	/** Der Name als String */
	private String kuerzel;			
	/** Die angrenzenden Länder-Objekte */
	private ArrayList<Land> nachbaren;
	/** Die angrenzenden Länder (String-Form) */
	private ArrayList<String> strnachb;
	
	/** GUI-Komponente für dieses Land */
	private ArmeenButton button;
	/** Position des Buttons auf der Weltkarte (lt. XML) */
	private Point buttonPos;
	/** Symbol, das auf der zugehörigen Land-Karte ist */
	private int symbol;
	/** Farb-Codierung (wichtig für die Identifizierung durch die Maus) */
	private int farbMap;
	/** Der Spieler, der es besitzt */
	private Spieler spieler;
	/** Die Armeenstärke */
	private int staerke;
	/** Ist true, wenn Einheiten in diesem Zug auf dieses Land bewegt worden sind */
	private boolean bewegt;	
	
	/** Konstruktor
	 * @param name Kürzel für Darstellung */
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
	/** Besitzer dieses Landes zurückgeben */
	public Spieler getSpieler() { return spieler; }
	
	/** Addiert n auf die Armeenstärke */
	public void plusStaerke(int n) { this.staerke += n; }
	/** Subtrahiert n von der Armeenstärke (Aufrufer muss prüfen, ob das geht) */
	public void minusStaerke(int n) { this.staerke -= n; }
	/** Gibt die Stärke der Armeen auf diesem Land zurück */
	public int getStaerke() { return this.staerke; }
	/** Setzt die Stärke der Armeen auf diesem Land */
	public void setStaerke(int n) { this.staerke = n; }

	/** Gibt an, ob das Land in dem aktiven Zug benutzt worden ist */
	public boolean getBewegt() { return this.bewegt; }
	/** Setzt den Zustand dieses Landes auf den übergebenen boolean */
	public void setBewegt(boolean b) { this.bewegt = b; }

	/** Füge ein Land zur Liste der Nachbaren für dieses Land hinzu */
	public void addNachbar(Land n) { nachbaren.add(n); }
	/** Gib' die Liste der Nachbarländer dieses Landes zurück */
	public ArrayList<Land> getNachbaren() {	return this.nachbaren; }
	/** Füge einen Ländernamen zu der entsprechenden String-Liste hinzu */
	public void addStrNachb(String s) {	this.strnachb.add(s); }
	/** Gib' die String-Liste aller Nachbarländer aus */
	public ArrayList<String> getStrNachb() { return strnachb; }

	/** Setze den FarbMap-Wert für dieses Land auf die übergebene int-Farbe */
	public void setFarbMap(int z) { this.farbMap = z; }
	/** Gib' den FarbMap-Wert für dieses Land zurück */
	public int getFarbMap() { return farbMap; }
	/** Gib' den Namen dieses Landes aus */
	public String getName() { return kuerzel; }

	/** Setze das Symbol für dieses Land auf die übergebene Konstante */
	public void setSymbol(int s) { symbol = s; }
	/** Gib' das Symbol dieses Landes zurück */
	public int getSymbol() { return symbol; }

	/** Lege einen ArmeenButton für dieses Land fest */
	public void setButton(ArmeenButton a) { button = a; }
	/** Gib' die Referenz auf den ArmeenButton für dieses Land zurück */
	public ArmeenButton getButton() { return button; }
	
	/** Setze die Position des ArmeenButtons für dieses Land auf die übergebenen Koordinaten */
	public void setButtonPos(Point p) { buttonPos = p; }
	/** Gib' die Position des ArmeenButtons für dieses Land zurück */
	public Point getButtonPos() { return buttonPos; }
	
	/** String-Darstellung für println */
	public String toString() { return kuerzel; }
	
	/** Prüfung auf Gleichheit zweier Land-Objekte: Sie sind genau dann gleich, wenn sie den gleichen Namen haben */
	public boolean equals(Object obj) {
		Land objLand;
		if (obj instanceof Land) {
			objLand = (Land) obj;
		} else if (obj instanceof String) {
			return this.kuerzel.equals(obj);
		} else return false;
		return this.kuerzel.equals(objLand.getName());
	}
	
	/** HashCode-Berechnung für ein Land-Objekt */
	public int hashCode() {
		int result = 17;
		result = 37 * result + kuerzel.hashCode();
		return result;
	}
	
	/** Prüfen, ob zwei Länder Nachbarn sind
	 * @return true, wenn's so ist, ansonsten false. Verblüffend! */
	public static boolean sindBenachbart(Land l1, Land l2) {
		// Wenn eines der beiden Länder null ist, sofort false zurückgeben
		if (l1 == null || l2 == null) return false;
		// Zwei Landobjekte definieren, abhängig davon, wer weniger Nachbarländer hat
		Land land = (l1.getNachbaren().size() < l2.getNachbaren().size()) ? l1 : l2;
		Land other =(l1.getNachbaren().size() < l2.getNachbaren().size()) ? l2 : l1;
		// Das Land mit der kleineren Nachbar-Liste wird durchiteriert
		Iterator<Land> iter = land.getNachbaren().iterator();
		while (iter.hasNext()) {
			// Wenn das andere Land mit einem in der Liste übereinstimmt, true zurückgeben
			if (other.equals(iter.next()))
				return true;
		}
		// Wenn nichts gefunden wurde, also die Schleife nicht abgebrochen wurde, false zurückgeben.
		return false;	
	}
	
	/** Kopiert die Eigenschaften vom ersten übergebenen Land in das zweite
	 * @param von	Land, von dem kopiert wird
	 * @param nach	Land, in das kopiert wird */
	public static void copy(Land von, Land nach) {
		nach.setBewegt(von.getBewegt());
		nach.setSpieler(von.getSpieler());
		nach.setStaerke(von.getStaerke());
	}
}
