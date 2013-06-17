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
 * Diese Klasse ist f�r die Handhabung eines Angriffes zust�ndig. Alles, was
 * mit dem Angreifen eines Landes von einem anderen zu tun hat, ist hier implementiert
 * @author Marcel
 */
public class AngriffsVerwaltung implements Serializable {
	private static final long serialVersionUID = 4828068081858660835L;

	// Attribute
	/** Referenz auf KartenVerwaltung */
	private KartenVerwaltung kv;
	
	// Attribute f�r Kampfberechnung
	/** Liste an Angriffsobjekten, die f�r den aktuellen Zug denkbar sind */
	private ArrayList<Angriff> ziele;
	/** Verlustzahlen des Angreifers nach ausgef�hrtem Angriff */
	private int verlust_angreifer = 0;
	/** Verlustzahlen des Verteidigers nach ausgef�hrtem Angriff */
	private int verlust_verteidiger = 0;
	/** Der aktuell ausgew�hlte Angriff */
	private Angriff derAngriff;

	/** Konstruktor mit KartenVerwaltung als Parameter (beide Verwalter arbeiten eng zusammen) */
	public AngriffsVerwaltung(KartenVerwaltung kv) { this.kv = kv; }
	
	/** Diese private Methode wird zum Berechnen einer Kampfsituation benutzt. �bergeben werden
	 * die W�rfelergebnisse beider Spieler in Form von ArrayLists.
	 * @param a1 W�rfelergebnisse von Spieler 1
	 * @param a2 W�rfelergebnisse von Spieler 2
	 * @return 0, wenn die Berechnung noch nicht abgeschlossen ist, -1 wenn dies zutrifft. */
	private int kampfCalc(ArrayList<Integer> a1, ArrayList<Integer> a2) {
		// Abbruchbedingung: a1.length = 0 oder a2.length = 0 (eine Seite hat keine Armeen mehr zum K�mpfen)
		if (a1.size() == 0 || a2.size() == 0) return -1;
		// H�chste Zahl ermitteln
		// Dazu: Zwei Iteratoren
		Iterator<Integer> iter1 = a1.iterator();
		Iterator<Integer> iter2 = a2.iterator();
		// Maximalzahlen und Positionen der Maximalzahlen in den Listen ermitteln
		int max1 = 0;
		int index1 = 0;
		int max2 = 0;
		int index2 = 0;
		// Lokaler Z�hler
		int i = 0;
		while(iter1.hasNext()) {
			// Zahl vom Angreifer ansehen...
			Integer z = iter1.next();
			// ...und wenn sie gr��er ist als das aktuelle Maximum...
			if (z > max1)  {
				// ...festhalten.
				max1 = z;
				index1 = i;
			}
			i++;
		}
		// Lokalen Z�hler zur�cksetzen und das gleiche analog f�r den Verteidiger
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
			// "Linke Seite" gr��er -> Verteidiger macht Verlust
			verlust_verteidiger++;
		} else {
			// Ansonsten Gleichstand oder "rechte Seite" gr��er -> Angreifer macht Verlust
			verlust_angreifer++;
		}
		// L�schen der aktuellen Zahlen
		a1.remove(index1);
		a2.remove(index2);
		return 0;
	}
	
	/** Diese Methode leitet die Angriffe ein, die ein Spieler auf feindliche L�nder t�tigen kann.
	 * Dargestellt wird, wo der aktuelle Spieler angreifen kann.
	 * Die Liste der Ziele wird gespeichert (Attribut der Klasse)
	 * @return Zahl der m�glichen Angriffe */
	public int init(Spieler s) {
		// Angriffsphase wird eingeleitet.
		// Zuerst aktuellen Spieler holen
		int erg = 0;
		// Seinen Besitz auffrischen
		LandVerwaltung.updateBesitz(s);
		// Besitz des Spielers in einer tempor�ren Land-ArrayList verpacken, danach die Nachbaren rausziehen.
		ArrayList<Land> besitz = s.getBesitz();
		// Die Ziele werden in einem Attribut dieser Klasse gespeichert, damit auch die Angriffs-Methode darauf zugreifen kann
		ziele = new ArrayList<Angriff>();
		// Per Iterator �ber den Besitz gehen und dieses Land nach seinen Nachbaren fragen.
		Iterator<Land> iterL = besitz.iterator();

		while(iterL.hasNext()) {
			Land land = iterL.next();
			// Wenn auf diesem aktuellen Land nur 1 Armee steht... �berspringen!
			// (1 Armee muss immer auf dem Feld stehen)
			// D.h. Bedingung, damit die Nachbaren �berhaupt gepr�ft werden: Land-St�rke gr��er 1!
			if (land.getStaerke() > 1) {
				// Neuer Iterator f�r die Nachbarl�nder
				Iterator<Land> iterN = land.getNachbaren().iterator();
				while(iterN.hasNext()) {
					Land nachbar = iterN.next();
					// Kriterium, damit ein Nachbarland in die Ziele-Liste aufgenommen wird:
					// Es geh�rt nicht dem aktuellen Spieler
					if (!nachbar.getSpieler().equals(s)) {
						// Neues Angriffs-Objekt erstellen
						Angriff ang = new Angriff(land, nachbar);
						erg++;
						// Ausgabe auf der Konsole
						// Hinzuf�gen des Angriffs zur Ziele-Liste
						ziele.add(ang);
					}
				}
			}
		}
		return erg;
	}
	
	
	/** Neuen Angriff aus der Liste m�glicher Ziele ausw�hlen */
	public Angriff newAngriff(int n) {
		derAngriff = ziele.get(n);
		return derAngriff;
	}
	
	/** Neuen Angriff setzen */
	public void setAngriff(Angriff a) { derAngriff = a; }
	
	/** Diese Methode �bernimmt die Abhandlung einer Kampfsituation.
	 * Ermittelt werden Angreifer und Verteidiger... diese m�ssen dann w�rfeln, um die Machtverh�ltnisse zu regeln etc.
	 * @param angr int[]-Array mit den W�rfelergebnissen des Angreifers
	 * @param vert int[]-Array mit den W�rfelergebnissen des Verteidigers
	 * @return Result-Objekt  [0]: BOOLEAN:Sieg oder nicht?	 [1]: INT: Zahl der Armeen, die �brig blieben [2]: KARTE: Karte, die der Spieler erh�lt (eventuell)
	 * @throws SpielBeendetException wenn ein Spieler einen anderen Spieler mit seinem Angriff besiegt und er der letzte verbleibende ist */
	public Result angriffAufLand(int[] angr, int[] vert) throws SpielBeendetException {
		// Komponenten des aktuellen Angriffs (Attribut) ziehen (Angreifer, Verteidiger)
		Land landAngr = derAngriff.getVon();
		Land landVert = derAngriff.getNach();
		
		// Die Armeen aus dem Angreiferland werden erstmal vom Brett genommen.
		landAngr.minusStaerke(angr.length);
		
		// Listen f�r die W�rfelergebnisse erstellen
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
		// (diese gibt so lange 0 zur�ck, bis die Berechnung fertig ist. Danach gibt sie -1 zur�ck)
		int result = 0;
		// Zur�cksetzen der Verlust-Attribute
		verlust_verteidiger = 0; verlust_angreifer = 0;
		do {
			result = kampfCalc(angreiferzahlen, verteidigerzahlen);
		} while (result != -1);
		// In den Attributen verlustAngreifer und verlustVerteidiger steht nun drin, wie viel Verlust gemacht wurde
		// Als n�chstes wird den Armeen im Verteidigerland der entstandene Schaden zugef�gt.
		landVert.minusStaerke(verlust_verteidiger);
		// Nun pr�fen wir anhand der neuen St�rke, ob der Angreifer das Land erobert hat oder nicht. Wenn
		// keine Armee mehr im Verteidigerland ist, ziehen die �brigen Einheiten des Angreifers dort ein.
		int uebrigeAngreifer = angr.length - verlust_angreifer;
		
		// R�ckgabe-Objekt
		// [0]: BOOLEAN:	Sieg oder nicht?
		// [1]: INT:		Zahl der Armeen, die �brig blieben
		// [2]: KARTE:		Karte, die der Spieler erh�lt (eventuell)
		Result resultObj = new Result();
		
		// Angreifer war erfolgreich
		if (landVert.getStaerke() == 0) {
			resultObj.push(true);
			resultObj.push(uebrigeAngreifer);
			// Besitzverh�ltnisse �ndern
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
			// Ansonsten hat der Verteidiger noch Armeen �brig. In diesem Fall ziehen die �brigen
			// Truppen zur�ck ins Heimatland.
			landAngr.plusStaerke(uebrigeAngreifer);
		}
		
		try {
			// Die ver�nderten Land-Werte m�ssen �ber die Copy-Funktion noch in die "Welt-Datenbank" eingetragen werden,
			// sonst bekommen die Clients keine �nderungen mit
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
	
	/** Anzahl m�glicher W�rfel (= Armeen) berechnen lassen
	 * @param a "Angreifer" oder "Verteidiger"
	 * @return Anzahl der m�glichen nutzbaren W�rfel, oder -1 wenn der String nicht passt */
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

	/** Zur�ckgeben der Anzahl m�glicher Angriffe */
	public int getAnzahlZiele() { return ziele.size(); }
	
	/** Zur�ckgeben des ausgew�hlten Angriffs */
	public Angriff getAngriff() { return derAngriff; }	
}
