package risk.server.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import risk.commons.valueobjects.Karte;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;

/**
 * Diese Klasse wird von der SpielVerwaltung angesprochen, wenn es um die Verarbeitung
 * von Aufgaben geht, die die Karten in den Händen der Spieler betreffen. Die
 * KartenVerwaltung kann einen Kartentausch durchführen.
 * @author Marcel, Yannik
 */
public class KartenVerwaltung implements Serializable {
	private static final long serialVersionUID = -5461221020215207218L;

	// Attribute
	/** Referenz auf Welt-Objekt */
	private Welt w;
	
	/** Statischer Zähler für Tauschvorgänge, die durchgeführt worden sind (davon abhängig: Die Anzahl neuer Armeen nach Eintauschen) */
	private static int anzahlTauschvorgaenge = 0;
	
	/** Zähler für die Anzahl KANONE-Karten */
	private int anzahlKANONE;
	/** Zähler für die Anzahl SOLDAT-Karten */
	private int anzahlSOLDAT;
	/** Zähler für die Anzahl REITER-Karten */
	private int anzahlREITER;
	/** Liste der Karten auf der Hand des aktuellen Spielers */
	private ArrayList<Karte> karten;

	/** Konstruktor mit Welt-Referenz als Parameter */
	public KartenVerwaltung(Welt w) { this.w = w; }
	
	/** Diese Methode wird aufgerufen, wenn eine Karte an einen Spieler
	 * verteilt wird. Sie prüft, ob der Kartenstapel leer ist, und wenn ja,
	 * "mischt sie ihn neu", indem sie der Kartenstapel-ArrayList alle Elemente
	 * aus der Ablagestapel-ArrayList zuteilt. */
	private void kartenMischen() {
		Vector<Karte> kartenstapel = w.getKartenstapel();
		Vector<Karte> ablagestapel = w.getAblagestapel();
		
		// Nur, wenn der Kartenstapel leer ist (size() == 0)...
		if (kartenstapel.size() == 0) {
			// Neue ArrayList für Kartenstapel machen und per Copy-Constructor
			// alles aus der Ablagestapel-ArrayList rüberkopieren
			kartenstapel = new Vector<Karte>(ablagestapel);
			// Ablagestapel-Liste löschen
			ablagestapel.clear();
		}
	}
	/** Diese Methode wird von der SpielVerwaltung aufgerufen, wenn ein Spieler eine Karte erhält.
	 * Sie kann analog zum Vorgang des "Eine Karte ziehens" verstanden werden.
	 * @return Eine zufällig gezogene Karte aus den vorhandenen Karten im Kartenstapel */
	public Karte karteZiehen() {
		Vector<Karte> kartenstapel = w.getKartenstapel();
		// Zuerst prüfen, ob der Stapel leer ist, also neu gemischt werden müsste
		kartenMischen();
		// Danach per Random-Variable zufällig eine Karte im Stapel auswählen
		int karte = (int) Math.floor( Math.random()*kartenstapel.size() );
		Karte k = null;
		try {
			k = kartenstapel.get(karte);
		} catch(ArrayIndexOutOfBoundsException e) {
			return null;
		}
		// Die Karte aus dem Kartenstapel entfernen
		kartenstapel.remove(karte);

		// Und die gezogene Karte zurückgeben
		return k;
	}	
	
	/** Methode zum Abrufen der Handkarten eines Spielers. Diese Methode bietet an, Karten einzutauschen, wenn's geht */
	public boolean init() {
		// Setzen der Counter für die Symbole auf den Karten in der Hand des aktuellen Spielers
		anzahlKANONE = 0;
		anzahlSOLDAT = 0;
		anzahlREITER = 0;
		// Verpacken des aktuellen Spielers in ein Temp-Objekt (leichtere Handhabung)
		Spieler s = SpielerVerwaltung.getAkteur();
		
		// Handkarten des Spielers fetchen und in eine ArrayList verpacken
		karten = s.getKarten();
		// Die Karten-Liste wird nun durchgegangen und jede Karte erstmal ausgegeben. (Natürlich nur wenn da Karten sind)
		if (karten.size() != 0) {
			Iterator<Karte> iter = karten.iterator();
			while (iter.hasNext()) {
				Karte k = iter.next();
				// Es wird außerdem direkt überprüft, welches Symbol auf der aktuellen Karte ist. Der entsprechende
				// Counter wird dabei hochgezählt.
				if (k.getSymbol() == Karte.SOLDAT) anzahlSOLDAT++;
				else if (k.getSymbol() == Karte.REITER) anzahlREITER++;
				else if (k.getSymbol() == Karte.KANONE) anzahlKANONE++;
				else if (k.getSymbol() == Karte.JOKER) {
				}
			}
			// Prüfen, ob eine Tausch-Bedingung erfüllt ist
			// 0. Bedingung: Hat der Spieler 5 Karten auf der Hand? Dann MUSS er tauschen.
			if (karten.size() == 5) return true;
			// 1. Bedingung: Überhaupt mehr als 3 Karten haben
			if (karten.size() >= 3) {
				// 2. Bedingung (a): Von jedem Typ mindestens eine
				// 2. Bedingung (b): Von einem Typ mindestens drei
				if ((anzahlKANONE >= 1 && anzahlSOLDAT >= 1 && anzahlREITER >= 1)
						|| (anzahlKANONE >= 3) || (anzahlSOLDAT >= 3) || (anzahlREITER >= 3)) {
					return true;
				}
			}
		}
		// Ansonsten false
		return false;
	}

	/** Methode zum Eintauschen von Karten. Wird über SpielVerwaltung von der UI aufgerufen,
	 * wenn die Bedingung in der init()-Methode erfüllt worden ist. Sie benutzt einen
	 * Algorithmus, der automatisch drei Karten entfernt, die die Kriterien erfüllen
	 * @return Weiterleiten des Result-Objektes aus der tauscheEin()-Methode */
	public Result tausch() {
		// Produktvariable zur Ermittlung des "Eintausch-Codes"
		int prod = 0;
		// Größe des übergebenen Kartenhaufens wird (der Übersicht halber) in eine Variable verpackt
		int size = karten.size();
		// Erzeugen einer Ergebnis-Liste zum Halten verschiedener gültiger Kombinationen
		// (damit am Ende der geringwertigste ausgewählt wird)
		ArrayList<Ergebnis> ergebnisse = new ArrayList<Ergebnis>();
		// Dreifach verschachtelte for-Schleifen, die alle Möglichkeiten der Kartenkombinationen durchgeht
		// und per switch/case abfragt, ob eine gültige Kombination vorliegt. Dann werden die Karten eingetauscht
		// und der Spieler wird benachrichtigt und kann neue Einheiten verteilen (abhängig vom static-Wert
		// anzahlTauschvorgaenge)
		for (int i = 0; i < size - 2; i++) {
			// Erster Wert
			int k1 = karten.get(i).getSymbol();
			for(int j = i+1; j < size - 1; j++) {
				// Zweiter Wert
				int k2 = karten.get(j).getSymbol();
				for(int k = j+1; k < size; k++) {
					// Dritter Wert
					int k3 = karten.get(k).getSymbol();
					// Produkt bilden
					prod = k1 * k2 * k3;
					// Wenn das eine gültige Kombination ist (Hilfsmethode)...
					if (gueltigeKombi(prod)) {
						// ...wird sie zur Ergebnisliste hinzugefügt.
						ergebnisse.add(new Ergebnis(i,j,k));
					}
				}
			}
		}
		// Ergebnisliste auswerten
		Ergebnis e;
		// 1. Wenn nur ein Element drin ist, ist das auch die Lösung.
		if (ergebnisse.size() == 1) {
			e = ergebnisse.get(0);
		} else {
			// Ansonsten sucht die private Methode sucheGuenstigstesErgebnis das beste raus
			e = sucheGuenstigstesErgebnis(ergebnisse);
		}
		// Am Schluss Eintauschen von e
		Result s = tauscheEin(e);
		return s;
	}
	
	/** Hilfsmethode zur Bestimmung, ob ein gefundenes Karten-Produkt einem gültigen Karten-Kombi-Wert
	 * gleicht (dazu: Konstanten aus Klasse "Karte" verwenden)
	 * @param prod Produkt der aktuellen Kartenkombination
	 * @return true wenn prod eine gültige Kombination ist, false sonst */
	private boolean gueltigeKombi(int prod) {
		int[] gueltigeErgebnisse = {Karte.RSK,Karte.RRR,Karte.SSS,Karte.KKK,Karte.RSJ,Karte.RJK,Karte.JSK,Karte.JJK,Karte.JSJ,Karte.RJJ};
		for (int i=0; i<gueltigeErgebnisse.length;i++) {
			if (prod == gueltigeErgebnisse[i])
				return true;
		}
		return false;
	}
	
	/** Methode zum Ermitteln des günstigsten Ergebnisses (= des Ergebnisses mit den am häufigsten vorkommenden
	 * Karten). Diese Methode zieht eine Kombination aus "einfachen" Karten (ohne Joker) solchen mit Joker vor
	 * @param erg Liste von Ergebnissen, die mit den Spieler-Karten gebildet werden können
	 * @return Ergebnis-Objekt mit den Index-Nummern der "günstigsten" Kartenkombination */
	private Ergebnis sucheGuenstigstesErgebnis(ArrayList<Ergebnis> erg) {
		// Ergebnis
		int groessteKlasse = 0;
		// Rausgezogener Index für die größte Klasse
		int groessterIndex = 0;
		for (int i = 0; i < erg.size(); i++) {
			Ergebnis aktuellesErgebnis = erg.get(i);
			// Ist die Klasse des aktuellen Indexes größer als das bisher Gemerkte?
			int aktuelleKlasse = aktuellesErgebnis.getKlasse();
			if (aktuelleKlasse > groessteKlasse) {
				// Wenn ja, umspeichern und Verweis auf dieses Ergebnis merken
				groessteKlasse = aktuelleKlasse;
				groessterIndex = i;
			}
		}
		// Rückgabe des "wahrscheinlichsten" Ergebnisses (mit den kleinstmöglichen Karten-Werten)
		return erg.get(groessterIndex);
	}
	
	/** Diese Methode ist dafür zuständig, das übergebene Ergebnis einzutauschen, dem Spieler
	 * Armeen bereit zu stellen und die Karten aus der Hand des Spielers entfernen. Zusätzlich
	 * wird der "Anzahl-Tausche"-Counter hochgezählt. (Ja, es heißt wirklich "Tausche", ohne Witz.)
	 * Rückgabe-Objektinhalt:
	 * [0]	int		neue Armeen
	 * [1]	Karte	Karte 1, die getauscht wurde
	 * [2]	Karte	Karte 2, die getauscht wurde
	 * [3]	Karte	Karte 3, die getauscht wurde
	 * @param e Ergebnis zum Tauschen
	 * @return Result-Objekt (Inhalt s.o.) */
	private Result tauscheEin(Ergebnis e) {
		// Result-Objekt
		Result ergebnis = new Result();
		
		// Elemente aus dem Ergebnis ziehen
		int[] k = e.getElemente();
		int k1 = k[0];
		int k2 = k[1];
		int k3 = k[2];
		Karte karte1 = karten.get(k1);
		Karte karte2 = karten.get(k2);
		Karte karte3 = karten.get(k3);
		
		// Karten dem Ablagestapel hinzufügen
		w.addAblagestapel(karte1);
		w.addAblagestapel(karte2);
		w.addAblagestapel(karte3);
		
		// Eintauschen (also Entfernen aus der Hand des Spielers)
		karten.remove(k1);
		karten.remove(k2 - 1);	// Diese Differenzen kommen zustande, weil die ArrayList die Element-Indexe
		karten.remove(k3 - 2);	// "einrückt", sodass nach dem Löschen des ersten Indexes alle weiteren angepasst werden
		
		// Neue Armeen ausrechnen
		Function func = new Function();
		int neu = func.f(anzahlTauschvorgaenge);
		
		// Result-Objekt füllen
		ergebnis.push(neu);
		ergebnis.push(karte1);
		ergebnis.push(karte2);
		ergebnis.push(karte3);
		
		// Hochzählen vom Counter
		anzahlTauschvorgaenge++;
		
		return ergebnis;
	}
	
	
	
	/** 
	 * Hilfsklasse: Function
	 * Diese Klasse berechnet die neuen Armeen für einen Spieler mithilfe einer mathematischen Formel.
	 * @version 1
	 * @author Marcel, Yannik
	*/
	private class Function {
		
		/** Mathematische Funktion zum Berechnen der dem Spieler zustehenden Armeen durch den Kartentausch
		 * @param 	x	Abzisse (hier: Anzahl der Tauschvorgänge)
		 * @return	Anzahl der Armeen */
		public int f(int x) {
			double erg;
			// Für 0 < x < 6 wird die mathematische Formel benutzt
			if (x < 6) {
				// Diese Formel wurde über ein lineares Gleichungssystem mit acht Unbekannten ermittelt und beschreibt
				// die Zuordnung von "Anzahl Tauschvorgänge" - "zustehende Armeen"
				double teil1 = ((double)-1/(double)360)	* Math.pow(x,6);
				double teil2 = ((double)1/(double)20)	* Math.pow(x,5);
				double teil3 = ((double)-23/(double)72)	* Math.pow(x,4);
				double teil4 = ((double)11/(double)12)	* Math.pow(x,3);
				double teil5 = ((double)-53/(double)45)	* Math.pow(x,2);
				double teil6 = ((double)38/(double)15)	* x;

				erg = Math.floor(teil1 + teil2 + teil3 + teil4 + teil5 + teil6 + 4);
			} else {
				// Ab dem 6. Tauschdurchgang erhöht sich die Armeenzahl lediglich um 5 pro Vorgang. Dies wird hier beschrieben
				erg = 15 + (5*(anzahlTauschvorgaenge-5));
			}
			return (int) erg;
		}
	}
	
	
	
	/** 
	 * Hilfsklasse: Ergebnis
	 * Klasse zum Aufnehmen eines gültigen Ergebnisses aus der Kartentausch-Berechnung
	 * @author Marcel, Yannik
	 * @version 1
	 */
	private class Ergebnis {
		
		// Attribute
		private int k1;		// Index 1
		private int k2;		// Index 2
		private int k3;		// Index 3
		private int klasse;	// "Klasse" des Ergebnisses
		
		/** Konstruktor
		 * @param k1	Index 1
		 * @param k2	Index 2
		 * @param k3	Index 3 */
		public Ergebnis(int k1,int k2, int k3) {
			this.k1 = k1;
			this.k2 = k2;
			this.k3 = k3;
			// Produkt in Klassen einteilen
			int prod = k1*k2*k3;
			
			switch(prod) {
			case (Karte.RSK):
				klasse = 4;
				break;
			case (Karte.RRR):
				klasse = 3;
				break;
			case (Karte.SSS):
				klasse = 3;
				break;
			case (Karte.KKK):
				klasse = 3;
				break;
			case (Karte.RSJ):
				klasse = 2;
				break;
			case (Karte.RJK):
				klasse = 2;
				break;
			case (Karte.JSK):
				klasse = 2;
				break;
			case (Karte.JJK):
				klasse = 1;
				break;
			case (Karte.JSJ):
				klasse = 1;
				break;
			case (Karte.RJJ):
				klasse = 1;
				break;
			}
		}
		
		/** Gib' die "Klasse" des Ergebnisses zurück (gibt an, wie wertvoll die beteiligten Karten sind) */
		public int getKlasse() {
			return klasse;
		}
		
		/** Gib' die Elemente des Ergebnisses in einem int-Array zurück */
		public int[] getElemente() {
			int[] a = {k1,k2,k3};
			return a;
		}
	}
}
