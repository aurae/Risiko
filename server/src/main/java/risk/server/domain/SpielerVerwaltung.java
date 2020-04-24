package risk.server.domain;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import risk.commons.exceptions.SpielBeendetException;
import risk.commons.exceptions.SpielerExistiertBereitsException;
import risk.commons.exceptions.SpielerzahlException;
import risk.commons.valueobjects.Kontinent;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;

/**
 * Diese Klasse verwaltet Maßnahmen, die die Spieler der Partie betreffen.
 * Dazu gehört unter anderem das Anmelden und Ausgeben von Spielern.
 * Sie wird benutzt von der SpielVerwaltung.
 * @author Marcel
 */
public class SpielerVerwaltung implements Serializable {
	private static final long serialVersionUID = -6163537392355613549L;

	/** Welt-Referenz */
	private static Welt w;
	/** Der Spieler, der aktuell "an der Reihe" ist (bzw. sein Index im Spieler-Array) */
	private static int aktuellerSpieler;

	/** Konstruktor mit Welt-Referenz */
	public SpielerVerwaltung(Welt w) { SpielerVerwaltung.w = w; }
	
	/** Methode zum Einfügen eines Spieler-Objekts in den Vector
	 * der Klasse Welt.
	 * @throws SpielerExistiertBereitsException Wenn dieser Name bereits vorhanden ist
	 * @throws SpielerzahlException Wenn bereits 6 Spieler im Vector sind
	 * @param name Name des einzutragenden Spielers */
	public void addSpieler(Spieler name, String farbe) throws SpielerExistiertBereitsException, SpielerzahlException, IOException {		
		// Spielen bereits 6 Leute mit, also sind die Farben aufgebraucht?
		if(w.getFarben().size() == 0) {
			throw new SpielerzahlException(SpielerzahlException.ZUVIEL);
		}
		// Kein Name wurde eingegeben
		if(name.equals(""))
			throw new IOException("");
		
		// Ist der Spieler bereits vorhanden?
		for(int i = 0; i < w.getSpieler().length; i++) {
			Spieler andererName = w.getSpieler()[i];
			if (andererName != null) {
				if (name.getName().equalsIgnoreCase(andererName.getName()))
					throw new SpielerExistiertBereitsException(name.getName());
			}
		}
		// Ansonsten, wenn alles passt, zum Spieler-Array hinzufügen
		@SuppressWarnings({"rawtypes"})
		Vector<Comparable> f = new Vector<Comparable>(2);
		// Int-Farbe zur String-Farbe holen
		int intfarbe = w.getHash().get(farbe);
		
		f.add(intfarbe);
		f.add(farbe);
		Spieler neuerSpieler = name;
		neuerSpieler.setFarben(f);
		
		// Farbe aus den Maps löschen
		Object[] fbla = w.getFarben().toArray();
		for (int i = 0; i < w.getFarben().size(); i++) {
			if (intfarbe == (Integer) (fbla[i])) {
				w.getFarben().remove(i);
				break;
			}
		}
		w.getHash().remove(farbe);
		
		for(int i = 0; i < w.getSpieler().length; i++) {
			if (w.getSpieler()[i] == null) {
				w.getSpieler()[i] = neuerSpieler;
				break;
			}
		}
	}
	
	/** Diese Methode löscht einen Spieler aus dem Spieler-Array, wenn er existiert, und setzt an die Stelle
	 * der Referenz null
	 * @param s Der zu löschende Spieler */
	public void removeSpieler(Spieler s) {
		Spieler[] allesp = w.getSpieler();
		for(int i = 0; i < allesp.length; i++) {
			if (allesp[i] != null && allesp[i].equals(s)) {
				allesp[i] = null;
				break;
			}
		}
	}
	
	/** Methode zum Prüfen, ob ein Spieler kein Land mehr besitzt und deshalb verloren hat
	 * @param s Spieler, der überprüft wird
	 * @return true, wenn er tatsächlich kein Land mehr besitzt, false sonst
	 * @throws SpielBeendetException wenn die Partie auf diese Art und Weise beendet wird */
	public boolean hatVerloren(Spieler s) {
		if (s.getBesitz().size() == 0) {
			removeSpieler(s);
			return true;
		} else return false;
	}
	
	/** Methode zur Ausgabe aller Spieler-Objekte im Vector des Welt-Objekts
	 * @throws SpielerzahlException Wenn der Spieler-Vector leer ist */
	public Result listSpieler() throws SpielerzahlException {
		// Wenn keine Spieler in der Liste stehen, Exception werfen
		if (w.getSpieler()[0] == null)
			throw new SpielerzahlException(SpielerzahlException.ZUWENIG);
		// Ansonsten alle Spieler-Objekte im Vector ansprechen
		// und ausgeben lassen
		Result res = new Result();
		for(int i = 0; i < w.getSpieler().length; i++) {
			if (w.getSpieler()[i] != null)
				res.push(w.getSpieler()[i]);
		}
		return res;
	}

	/** Diese Methode sucht einen Spieler mit der übergebenen Farbe und gibt ihn zurück
	 * @param farbe Gesuchter Spieler
	 * @return Spieler-Referenz oder null */
	public static Spieler searchSpieler(int farbe) {
		for (int i = 0; i < w.getSpieler().length; i++) {
			Spieler sp = w.getSpieler()[i];
			if (sp != null && sp.getFarbe() == farbe)
				return sp;
		}
		return null;
	}
	
	/** Diese Methode sucht einen Spieler mit dem übergebenen Namen und gibt ihn zurück
	 * @param s Gesuchter Spieler
	 * @return Spieler-Referenz oder null */
	public static Spieler searchSpieler(String s) {
		for (int i = 0; i < w.getSpieler().length; i++) {
			Spieler sp = w.getSpieler()[i];
			if (sp.getName().equals(s))
				return sp;
		}
		return null;
	}
	
	/** Diese Methode löscht einen Spieler aus der Liste der Spieler im Array und lässt alle anderen Spieler "aufrücken"
	 * @param s	Spieler-Referenz, die gelöscht werden soll */
	public static void disposeSpieler(Spieler s) {
		// Farbe der Welt zurückgeben
		int farbe = s.getFarbe();
		String farbtext = s.getFarbtext();
		w.getFarben().add(farbe);
		w.getHash().put(farbe, farbtext);
		for(int i = 0; i < w.getSpieler().length; i++) {
			if (s.equals(w.getSpieler()[i])) {
				w.getSpieler()[i] = null;
				for (int j = i + 1; j < w.getSpieler().length; j++) {
					w.getSpieler()[j-1] = w.getSpieler()[j];
				}
			}
		}
		w.getSpieler()[5] = null;
	}
	
	/** Diese Methode berechnet, wie viele neue Armeen ein Spieler bekommt.
	 * Sie wird von der SpielVerwaltung zu Beginn eines Zuges aufgerufen.
	 * @param s der Spieler, dessen Anspruch berechnet wird */
	public int calcNeueArmeen(Spieler s) {
		// Zuerst: Ausrechnen, wie viele Grund-Armeen er bekommt.
		// Dies errechnet sich über Anzahl-der-Länder / 3, abgerundet
		// auf die nächste ganze Zahl.
		int neueLaender = (int) Math.floor(s.getBesitz().size() / 3);
		// Wenn ein Spieler weniger als 9 Länder besitzt, also auch
		// weniger als 3 neue Armeen bekäme, bekommt er trotzdem 3.
		// Dies ist das Minimum an neuen Armeen.
		if (neueLaender < 3) neueLaender = 3;
		// Als nächstes: Prüfen, ob der Spieler einen kompletten Kontinent
		// eingenommen hat.
		// Dazu: Iterator auf die Kontinent-Liste der Welt setzen und durchlaufen.
		Iterator<Kontinent> iterK = w.getKontinente().iterator();
		while (iterK.hasNext()) {
			// Jeden Kontinent fragen wir nun, ob das erste Land "in ihm"
			// zu dem Spieler gehört, der gerade dran ist.
			// Dazu: Lange if-Abfrage!
			// (Kontinent -> Länderliste -> Land Nr. 1 -> Besitzender == s?)
			Kontinent aktK = iterK.next();
			if (aktK.getLaender().get(0).getSpieler().equals(s)) {
				// Wenn der Spieler tatsächlich das erste Land besitzen sollte,
				// hat er dann auch die anderen? -> Wir fragen aktK
				// mit der Methode pruefeEinnahme, ob alle seine Länder
				// von der gleichen Person besetzt werden. Sollte diese
				// Prüfung true ergeben, geht's weiter.
				if (aktK.pruefeEinnahme(s)) {
					// Jetzt holen wir uns den Namen des Kontinentes.
					// gemäß des Namens werden neue Armeen verteilt
					// (siehe Risiko-Anleitung)
					String name = aktK.getName();
					//
					if (name.equals("Australien") || name.equals("Südamerika"))
						neueLaender += 2;
					else if (name.equals("Afrika"))
						neueLaender += 3;
					else if (name.equals("Europa") || name.equals("Nordamerika"))
						neueLaender += 5;
					else if (name.equals("Asien"))
						neueLaender += 7;
				}
			}
		}
		// Die Anzahl der gefundenen neuen Armeen, die dem Spieler zustehen, wird zurückgegeben.
		return neueLaender;
	}

	/** Setzt den Index des Spielers, der an der Reihe ist, auf den übergebenen Wert */
	public static void setAkteur(int n) { aktuellerSpieler = n; }
	
	/** Gibt den Spieler, der an der Reihe ist, zurück */
	public static Spieler getAkteur() { return w.getSpieler()[aktuellerSpieler]; }
	
	/** Setzt die Welt-Referenz auf das übergebene Welt-Objekt */
	public static void setWelt(Welt w) { SpielerVerwaltung.w = w; }
	
	/** Methode, die den Index des nächsten Spielers im Spieler-Array ermittelt. */
	public void naechsterSpieler() {
		// Hochsetzen des Spieler-Indexes innerhalb einer Schleife. Es muss das nächste existente
		// Objekt im Spieler-Array gesucht werden
		Spieler[] allesp = w.getSpieler();

		boolean klappt = false;
		do {
			aktuellerSpieler++;
			// Wenn der Index die Anzahl der Spieler übersteigt, zurück auf 0 setzen
			if (aktuellerSpieler > countSpieler()
					|| aktuellerSpieler == 6)
				setAkteur(0);
			
			if (allesp[aktuellerSpieler] != null)
				klappt = true;
		} while (!klappt);
	}

	/** Methode zum Starten der Partie
	 * Bedingung: Spielerzahl > 2
	 * @return true, wenn Bedingung erfüllt ist, false sonst */
	public boolean spielerzahlOK() {
		// Einer allein kann nicht spielen, kein registrierter schon gar nicht
		// d.h. true geben, wenn mindestens drei Spieler registriert sind
		int spielerzahl = countSpieler();
		if (spielerzahl >= 3)
			return true;
		else return false;
	}
	
	/** Methode zum Zählen der Spieler, die noch dabei sind
	 * @return Anzahl der Spieler */
	public static int countSpieler() {
		Spieler[] diesp = w.getSpieler();
		int zahl = 0;
		for(int i = 0; i < diesp.length; i++) {
			if (diesp[i] != null) {
				zahl++;
			}
		}
		return zahl;
	}
	
	/** Gibt den ersten Spieler im Array zurück. Wird zur Siegerermittlung benutzt */
	public static Spieler grabFirstSpieler() {
		Spieler[] allesp = w.getSpieler();
		for (int i = 0; i < allesp.length; i++) {
			if (allesp[i] != null)
				return allesp[i];
		}
		return null;
	}
	
	/** Gibt die Position des Spielers im Array zurück, der gerade an der Reihe ist */
	public static int getIndex() { return aktuellerSpieler; }	
}
