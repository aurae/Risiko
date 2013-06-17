package risk.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import risk.commons.exceptions.KeinLandException;
import risk.commons.exceptions.SpielBeendetException;
import risk.commons.valueobjects.Angriff;
import risk.commons.valueobjects.Karte;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;

/**
 * Diese Klasse ist für die Handhabung eines Angriffes zuständig. Alles, was
 * mit dem Angreifen eines Landes von einem anderen zu tun hat, ist hier implementiert
 * @author Marcel
 */
public class AngriffsVerwaltung implements Serializable {
	private static final long serialVersionUID = 4828068081858660835L;

	// Attribute
	/** Referenz auf KartenVerwaltung */
	private KartenVerwaltung kv;
	
	// Attribute für Kampfberechnung
	/** Liste an Angriffsobjekten, die für den aktuellen Zug denkbar sind */
	private ArrayList<Angriff> ziele;
	/** Verlustzahlen des Angreifers nach ausgeführtem Angriff */
	private int verlust_angreifer = 0;
	/** Verlustzahlen des Verteidigers nach ausgeführtem Angriff */
	private int verlust_verteidiger = 0;
	/** Der aktuell ausgewählte Angriff */
	private Angriff derAngriff;

	/** Konstruktor mit KartenVerwaltung als Parameter (beide Verwalter arbeiten eng zusammen) */
	public AngriffsVerwaltung(KartenVerwaltung kv) { this.kv = kv; }
	
	/** Diese private Methode wird zum Berechnen einer Kampfsituation benutzt. Übergeben werden
	 * die Würfelergebnisse beider Spieler in Form von ArrayLists.
	 * @param a1 Würfelergebnisse von Spieler 1
	 * @param a2 Würfelergebnisse von Spieler 2
	 * @return 0, wenn die Berechnung noch nicht abgeschlossen ist, -1 wenn dies zutrifft. */
	private int kampfCalc(ArrayList<Integer> a1, ArrayList<Integer> a2) {
		// Abbruchbedingung: a1.length = 0 oder a2.length = 0 (eine Seite hat keine Armeen mehr zum Kämpfen)
		if (a1.size() == 0 || a2.size() == 0) return -1;
		// Höchste Zahl ermitteln
		// Dazu: Zwei Iteratoren
		Iterator<Integer> iter1 = a1.iterator();
		Iterator<Integer> iter2 = a2.iterator();
		// Maximalzahlen und Positionen der Maximalzahlen in den Listen ermitteln
		int max1 = 0;
		int index1 = 0;
		int max2 = 0;
		int index2 = 0;
		// Lokaler Zähler
		int i = 0;
		while(iter1.hasNext()) {
			// Zahl vom Angreifer ansehen...
			Integer z = iter1.next();
			// ...und wenn sie größer ist als das aktuelle Maximum...
			if (z > max1)  {
				// ...festhalten.
				max1 = z;
				index1 = i;
			}
			i++;
		}
		// Lokalen Zähler zurücksetzen und das gleiche analog für den Verteidiger
		i = 0;
		while(iter2.hasNext()) {
			Integer z = iter2.next();
			if (z > max2)  {
				max2 = z;
				index2 = i;
			}
			i++;
		}
		// Vergleich
		if (max1 > max2) {
			// "Linke Seite" größer -> Verteidiger macht Verlust
			verlust_verteidiger++;
		} else {
			// Ansonsten Gleichstand oder "rechte Seite" größer -> Angreifer macht Verlust
			verlust_angreifer++;
		}
		// Löschen der aktuellen Zahlen
		a1.remove(index1);
		a2.remove(index2);
		return 0;
	}
	
	/** Diese Methode leitet die Angriffe ein, die ein Spieler auf feindliche Länder tätigen kann.
	 * Dargestellt wird, wo der aktuelle Spieler angreifen kann.
	 * Die Liste der Ziele wird gespeichert (Attribut der Klasse)
	 * @return Zahl der möglichen Angriffe */
	public int init(Spieler s) {
		// Angriffsphase wird eingeleitet.
		// Zuerst aktuellen Spieler holen
		int erg = 0;
		// Seinen Besitz auffrischen
		LandVerwaltung.updateBesitz(s);
		// Besitz des Spielers in einer temporären Land-ArrayList verpacken, danach die Nachbaren rausziehen.
		ArrayList<Land> besitz = s.getBesitz();
		// Die Ziele werden in einem Attribut dieser Klasse gespeichert, damit auch die Angriffs-Methode darauf zugreifen kann
		ziele = new ArrayList<Angriff>();
		// Per Iterator über den Besitz gehen und dieses Land nach seinen Nachbaren fragen.
		Iterator<Land> iterL = besitz.iterator();

		while(iterL.hasNext()) {
			Land land = iterL.next();
			// Wenn auf diesem aktuellen Land nur 1 Armee steht... überspringen!
			// (1 Armee muss immer auf dem Feld stehen)
			// D.h. Bedingung, damit die Nachbaren überhaupt geprüft werden: Land-Stärke größer 1!
			if (land.getStaerke() > 1) {
				// Neuer Iterator für die Nachbarländer
				Iterator<Land> iterN = land.getNachbaren().iterator();
				while(iterN.hasNext()) {
					Land nachbar = iterN.next();
					// Kriterium, damit ein Nachbarland in die Ziele-Liste aufgenommen wird:
					// Es gehört nicht dem aktuellen Spieler
					if (!nachbar.getSpieler().equals(s)) {
						// Neues Angriffs-Objekt erstellen
						Angriff ang = new Angriff(land, nachbar);
						erg++;
						// Ausgabe auf der Konsole
						// Hinzufügen des Angriffs zur Ziele-Liste
						ziele.add(ang);
					}
				}
			}
		}
		return erg;
	}
	
	
	/** Neuen Angriff aus der Liste möglicher Ziele auswählen */
	public Angriff newAngriff(int n) {
		derAngriff = ziele.get(n);
		return derAngriff;
	}
	
	/** Neuen Angriff setzen */
	public void setAngriff(Angriff a) { derAngriff = a; }
	
	/** Diese Methode übernimmt die Abhandlung einer Kampfsituation.
	 * Ermittelt werden Angreifer und Verteidiger... diese müssen dann würfeln, um die Machtverhältnisse zu regeln etc.
	 * @param angr int[]-Array mit den Würfelergebnissen des Angreifers
	 * @param vert int[]-Array mit den Würfelergebnissen des Verteidigers
	 * @return Result-Objekt  [0]: BOOLEAN:Sieg oder nicht?	 [1]: INT: Zahl der Armeen, die übrig blieben [2]: KARTE: Karte, die der Spieler erhält (eventuell)
	 * @throws SpielBeendetException wenn ein Spieler einen anderen Spieler mit seinem Angriff besiegt und er der letzte verbleibende ist */
	public Result angriffAufLand(int[] angr, int[] vert) throws SpielBeendetException {
		// Komponenten des aktuellen Angriffs (Attribut) ziehen (Angreifer, Verteidiger)
		Land landAngr = derAngriff.getVon();
		Land landVert = derAngriff.getNach();
		
		// Die Armeen aus dem Angreiferland werden erstmal vom Brett genommen.
		landAngr.minusStaerke(angr.length);
		
		// Listen für die Würfelergebnisse erstellen
		ArrayList<Integer> angreiferzahlen = new ArrayList<Integer>();
		ArrayList<Integer> verteidigerzahlen = new ArrayList<Integer>();
		
		// ERGEBNISARRAYS NOCH IN DIE LISTEN EINTRAGEN
		for(int i=0;i<angr.length;i++) {
			angreiferzahlen.add(angr[i]);
		}
		for(int i=0;i<vert.length;i++) {
			verteidigerzahlen.add(vert[i]);
		}
		
		// Jetzt in einer Schleife berechnen lassen. Dazu die private Methode kampfCalc verwenden
		// (diese gibt so lange 0 zurück, bis die Berechnung fertig ist. Danach gibt sie -1 zurück)
		int result = 0;
		// Zurücksetzen der Verlust-Attribute
		verlust_verteidiger = 0; verlust_angreifer = 0;
		do {
			result = kampfCalc(angreiferzahlen, verteidigerzahlen);
		} while (result != -1);
		// In den Attributen verlustAngreifer und verlustVerteidiger steht nun drin, wie viel Verlust gemacht wurde
		// Als nächstes wird den Armeen im Verteidigerland der entstandene Schaden zugefügt.
		landVert.minusStaerke(verlust_verteidiger);
		// Nun prüfen wir anhand der neuen Stärke, ob der Angreifer das Land erobert hat oder nicht. Wenn
		// keine Armee mehr im Verteidigerland ist, ziehen die übrigen Einheiten des Angreifers dort ein.
		int uebrigeAngreifer = angr.length - verlust_angreifer;
		
		// Rückgabe-Objekt
		// [0]: BOOLEAN:	Sieg oder nicht?
		// [1]: INT:		Zahl der Armeen, die übrig blieben
		// [2]: KARTE:		Karte, die der Spieler erhält (eventuell)
		Result resultObj = new Result();
		
		// Angreifer war erfolgreich
		if (landVert.getStaerke() == 0) {
			resultObj.push(true);
			resultObj.push(uebrigeAngreifer);
			// Besitzverhältnisse ändern
			// Angreifer bekommt das Land...
			Spieler angreifer = derAngriff.getAngreifer();
			Spieler verteidiger = derAngriff.getVerteidiger();

			landVert.setSpieler(angreifer);
			angreifer.getBesitz().add(landVert);
			Welt.getWelt().getLandByFarbMap(landVert.getFarbMap()).setSpieler(angreifer);
			
			// Verteidiger verliert es...
			verteidiger.getBesitz().remove(landVert);
			
			// Armeen einziehen lassen
			landVert.plusStaerke(uebrigeAngreifer);

			// Nun wird das eingenommene Land "versiegelt", sodass bei der Armeenverteilung keine Armee
			// mehr in dieses Land oder von diesem Land bewegt werden kann
			landVert.setBewegt(true);
			// Der Spieler muss einmalig eine Handkarte bekommen, wenn er den Kampf gewonnen hat
			if (!derAngriff.getAngreifer().getSiegreich()) {
				Karte neu = kv.karteZiehen();
				if (neu != null) {
					derAngriff.getAngreifer().addKarte(neu);
					derAngriff.getAngreifer().setSiegreich(true);
					resultObj.push(neu);
				}
			}
		} else {
			resultObj.push(false);
			resultObj.push(uebrigeAngreifer);
			// Ansonsten hat der Verteidiger noch Armeen übrig. In diesem Fall ziehen die übrigen
			// Truppen zurück ins Heimatland.
			landAngr.plusStaerke(uebrigeAngreifer);
		}
		
		try {
			// Die veränderten Land-Werte müssen über die Copy-Funktion noch in die "Welt-Datenbank" eingetragen werden,
			// sonst bekommen die Clients keine Änderungen mit
			Land.copy(landAngr, LandVerwaltung.searchLand(landAngr.getName()));
			Land.copy(landVert, LandVerwaltung.searchLand(landVert.getName()));
		} catch (KeinLandException e) {	}	
		return resultObj;
	}
	
	/** Nachziehen von a Armeen ins eingenommene Land */
	public void nachziehen(int a) {
		// Eingegebene Armeen bei Bedarf nachziehen
		derAngriff.getNach().plusStaerke(a);
		derAngriff.getVon().minusStaerke(a);
	}
	
	/** Anzahl möglicher Würfel (= Armeen) berechnen lassen
	 * @param a "Angreifer" oder "Verteidiger"
	 * @return Anzahl der möglichen nutzbaren Würfel, oder -1 wenn der String nicht passt */
	public int wuerfelzahl(String a) {
		int maxStaerke = 0;
		
		if (a.equals("Angreifer")) {
			maxStaerke = derAngriff.getVon().getStaerke();
			if (maxStaerke > 3)
				maxStaerke = 3;
			else
				maxStaerke--;
		}
		else if (a.equals("Verteidiger")) {
			maxStaerke = derAngriff.getNach().getStaerke();
			if(maxStaerke > 2) maxStaerke = 2;
		}
		else return -1;
		return maxStaerke;
	}

	/** Zurückgeben der Anzahl möglicher Angriffe */
	public int getAnzahlZiele() { return ziele.size(); }
	
	/** Zurückgeben des ausgewählten Angriffs */
	public Angriff getAngriff() { return derAngriff; }	
}
