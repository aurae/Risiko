package risk.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import risk.commons.exceptions.KeinLandException;
import risk.commons.valueobjects.Karte;
import risk.commons.valueobjects.Kontinent;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import risk.server.persistence.ReadLandXML;

/**
 * Diese Klasse wird beim Spielstart von der SpielVerwaltung aufgerufen.
 * Sie hat die Aufgabe, den angemeldeten Spielern ihre Länder zu
 * Beginn der Partie zuzuordnen und die Hoheitsrechte eines Landes
 * später im Spiel zu regeln. Sie kennt das Welt-Objekt und kann
 * damit alles einsehen, was das Welt-Objekt freigegeben hat.
 * @author Marcel
 */
public class LandVerwaltung implements Serializable {
	private static final long serialVersionUID = -5534258022860474345L;
	
	// Attribute
	/** Referenz auf Welt-Objekt */
	private static Welt w;					
	/** Liste aller Länder ohne Kontinentunterteilung */
	private static Vector<Land> alleLaender;		
	/** Liste aller Kontinente */
	private static ArrayList<Kontinent> listeKon;

	/** Konstruktor
	 * @param w Die Welt, deren Länder verwaltet werden */
	public LandVerwaltung(Welt w) {	LandVerwaltung.w = w; }
	
	/** Diese Methode wird nur zum Spielstart aufgerufen. Sie lässt
	 * die Liste der Länder auslesen, umverteilen und abspeichern.
	 * Danach teilt sie jedem Land einen Spieler zu. */
	public void organizeWelt() {
		// SCHRITT 1: Länderliste aus der XML-Datei lesen
			// Handler erzeugen
			ReadLandXML handler = new ReadLandXML();
			// XML-Datei auslesen lassen und Kontinentliste abspeichern
			listeKon = handler.read("xml/laender.xml");
			// Liste dem Welt-Objekt übergeben
			w.setKontinente(listeKon);
		// SCHRITT 2: Den Ländern ihre Nachbaren gemäß XML-Datei zuteilen
			// Aus String-Namen werden echte Objektreferenzen
			organizeNachbaren();
		// SCHRITT 3: Besitzverteilung
			// Den teilnehmenden Spielern werden die verfügbaren Länder zugeteilt;
			// dies übernimmt die private Methode landVerteilung().
			// Die statische Methode setAkteur der Klasse SpielerVerwaltung
			// bekommt den Startspieler-Index.
			SpielerVerwaltung.setAkteur(landVerteilung());		
	}
	
	/** Diese Methode arbeitet für die übergeordnete Methode organizeWelt.
	 * Sie ist dafür verantwortlich, dass die zuvor gesetzten String-ArrayListen
	 * aller Länder ausgelesen werden und tatsächliche Objekt-Referenzen in
	 * die einzelnen Länder gespeichert werden
	 * @param k Die Kontinent-ArrayList, deren Elemente bearbeitet werden */
	private void organizeNachbaren() {
		// Kontinent-Iterator und aktuelles Element
		Iterator<Kontinent> iterK = listeKon.iterator();
		Kontinent aktK;
		// Land-Iterator und aktuelles Element
		Iterator<Land> iterL;
		Land aktL;
		// StrNachbar-Iterator und Objekte
		Iterator<String> iterS;
		ArrayList<String> stringliste;
		String aktS;
		// Durch alle Kontinente durchgehen (analog zur For-Schleife)
		while (iterK.hasNext()) {
			// Aktuelles Element speichern
			aktK = iterK.next();
			// Als nächstes zweiten Iterator setzen auf die Länder-Objekte dieses Kontinents
			iterL = aktK.getLaender().iterator();
			// Durch alle Länder durchgehen (analog zur For-Schleife)
			while (iterL.hasNext()) {
				// Aktuelles Element speichern
				aktL = iterL.next();
				// String-Nachbar-Liste herausziehen und speichern
				stringliste = aktL.getStrNachb();
				// Dritten Iterator setzen auf die Objekte dieser String-Liste
				iterS = stringliste.iterator();
				// Durch alle Elemente der String-Liste durchgehen (analog zur For-Schleife)
				while (iterS.hasNext()) {
					// Aktuelles Element setzen
					aktS = iterS.next();
					// Dieser String wird nun mit der Hilfsmethode sucheLand gesucht und direkt
					// in das aktuelle Land-Objekt gespeichert.
					// Dabei muss die Exception gefangen werden, wenn sie geworfen wird.
					try {
						aktL.addNachbar(searchLand(aktS));
					} catch (KeinLandException e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}	
	}
	
	/** Diese Methode verteilt zu Spielbeginn die verfügbaren Länder an die Spieler.
	 * Sie wird von der übergeordneten Methode organizeWelt benutzt.
	 * @return Der Index des Spielers, der den ersten Zug tätigt */
	private int landVerteilung() {
		// Zuerst: Alle Länder der gesamten Karte in eine ArrayList verfrachten
		// Dazu: Initialisierung...
		ArrayList<Land> alleLaender = new ArrayList<Land>();
		// ...und Speichern aller Länder mit einem Iterator,
		// der alle Elemente der übergebenen Kontinent-ArrayList durchgeht
		// und dann (über einen zweiten Iterator) alle Länder dieser Kontinente
		// der ArrayList alleLaender hinzufügt.
		Iterator<Kontinent> iterK = listeKon.iterator();
		Iterator<Land> iterL;
		while(iterK.hasNext()) {
			iterL = iterK.next().getLaender().iterator();
			while(iterL.hasNext()) {
				alleLaender.add(iterL.next());
			}
		}
		// Initialisierung des zweiten Teils der Methode:
		// Zufallszahl, aktueller Spieler, aktuelles Land, Index
		int zufall;
		Spieler derSpieler;
		Land dasLand;
		int index = 0;
		// Nun wird die Schleife gestartet, die aufhört, wenn alleLaender keine
		// Elemente mehr beinhaltet.	
		while (alleLaender.size() > 0) {
			// Jetzt wird ein Zufalls-Index aus der size()-Angabe berechnet.
			zufall = (int) ( Math.random() * alleLaender.size() );
			// Das Spieler-Objekt an Stelle index bekommt das Land an Stelle zufall
			derSpieler = w.getSpieler()[index];
			dasLand = alleLaender.get(zufall);
			//
			derSpieler.getBesitz().add(dasLand);
			dasLand.setSpieler(derSpieler);
			// Vor dem Löschen des Landes wird eine Karte erstellt, die
			// dem Welt-Objekt zugesteckt wird. So wird nebenbei
			// nach und nach der "Nachziehstapel" aufgebaut.
			Karte neueKarte = new Karte(dasLand);
			w.getKartenstapel().add(neueKarte);
			// Zuletzt wird das gerade vergebene Land aus der Komplett-Liste gelöscht
			alleLaender.remove(zufall);
			// Index wird hochgezählt. Wenn er über die Anzahl an Spielern kommt,
			// wird er zurück gesetzt.
			index++;
			if( index >= SpielerVerwaltung.countSpieler())
				index = 0;
		}
		// Am Schluss werden dem Kartenstapel noch zwei Joker-Karten zugefügt.
		w.getKartenstapel().add(new Karte("Joker"));
		w.getKartenstapel().add(new Karte("Joker"));
		return index;
	}
	
	/** Diese Methode kann einen Kontinent im Welt-Objekt suchen
	 * @param s Der Name des gesuchten Kontinents
	 * @return Referenz auf den Kontinent
	 * @throws KeinLandException wenn kein Kontinent mit diesem Namen existiert */
	public static Kontinent searchKontinent(String s) throws KeinLandException  {
		Iterator<Kontinent> iter = w.getKontinente().iterator();
		while(iter.hasNext()) {
			Kontinent k = iter.next();
			if (k.getName().equals(s))
				return k;
		}
		// Sonst Fehler
		throw new KeinLandException("Es existiert kein Kontinent namens "+s+"!");
	}
	
	/** Diese Methode kann ein Land im Welt-Objekt suchen
	 * @param s Der Name des gesuchten Landes
	 * @return Referenz auf das Land (oder null, wenn es keinen gibt)
	 * @throws KeinLandException wenn kein Land mit diesem Namen existiert */
	public static Land searchLand(String s) throws KeinLandException {
		Vector<Land> laender = getAlleLaender();
		for (Land l : laender) {
			if (l.getName().equals(s))
				return l;
		}
		throw new KeinLandException(s);
	}
	
	/** Methode, die alle Kontinent- und Land-Infos ausgibt */
	public void gibAlleAus() {
		if (w.getKontinente() != null) {
			Iterator<Kontinent> iter = w.getKontinente().iterator();
			while(iter.hasNext())
				System.out.println(iter.next().toString());
		}
	}
	
	/** Diese Methode setzt die Hoheitsrechte auf einem Land so um,
	 * dass das Land ab sofort dem übergebenen Spieler gehört
	 * @param l Land
	 * @param s Spieler */
	public void setBesitz(Land l, Spieler s) { l.setSpieler(s); }
	
	/** Setzt die Welt-Referenz dieses LandVerwaltungs-Objekts um */
	public void setWelt(Welt w) { LandVerwaltung.w = w;	}

	/** Gib' alle Länder in einer unsortierten Liste aus */
	public static Vector<Land> getAlleLaender() {
		if (alleLaender == null) {
			alleLaender = new Vector<Land>();
			// Kontinent-Liste aktualisieren
			listeKon = w.getKontinente();
			// Alle Kontinente durchgehen und wahllos einsortieren
			Iterator<Kontinent> iterK = listeKon.iterator();
			while (iterK.hasNext()) {
				Kontinent k = iterK.next();
				Iterator<Land> iterL = k.getLaender().iterator();
				while(iterL.hasNext()) {
					alleLaender.add(iterL.next());
				}
			}
		}
		return alleLaender;
	}

	/** Aktualisiere den Besitz eines übergebenen Spieler-Objektes */
	public static void updateBesitz(Spieler s) {
		Iterator<Land> iter = s.getBesitz().iterator();
		ArrayList<Land> neu = new ArrayList<Land>();
		while(iter.hasNext()) {
			neu.add(w.getLandByFarbMap(iter.next().getFarbMap()));
		}
		s.setBesitz(neu);
	}
}
