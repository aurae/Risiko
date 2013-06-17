package risk.server.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import risk.commons.exceptions.IncompatibleWeltException;
import risk.commons.exceptions.KeinLandException;
import risk.commons.exceptions.SpielBeendetException;
import risk.commons.exceptions.SpielerExistiertBereitsException;
import risk.commons.exceptions.SpielerNichtBesitzerException;
import risk.commons.exceptions.SpielerzahlException;
import risk.commons.interfaces.ClientMethods;
import risk.commons.interfaces.ServerMethods;
import risk.commons.valueobjects.Angriff;
import risk.commons.valueobjects.ChatMessage;
import risk.commons.valueobjects.Kontinent;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import de.root1.simon.exceptions.SimonRemoteException;

/**
 * Diese Klasse wird von der CUI/GUI angesprochen, wenn es
 * um die Verarbeitung der Benutzereingaben geht.
 * Sie bietet Methoden an, die wiederum neue Objekte dazu auffordern,
 * etwas zu tun.
 * @author Marcel
 */
public class SpielVerwaltung implements ServerMethods, Serializable {
	private static final long serialVersionUID = -3243942646488814231L;

	/** Statische Selbstreferenz, damit andere Klassen über einen getter auf die SpielVerwaltung zugreifen können */
	private static SpielVerwaltung instance;
	
	/** Welt-Referenz */
	private Welt w;							
	
	/** Referenz auf LandVerwaltung (Zuteiler von Ländern) */
	private LandVerwaltung landMgr;			
	/** Referenz auf MissionsVerwaltung (Zuteiler von Missionen) */
	private MissionsVerwaltung missionMgr;	
	/** Referenz auf SpielerVerwaltung */
	private SpielerVerwaltung spielerMgr;	
	/** Referenz auf AngriffsVerwaltung */
	private AngriffsVerwaltung angriffMgr;
	/** Referenz auf KartenVerwaltung */
	private KartenVerwaltung kartenMgr;		
	
	/** Liste der Länder, die bewegt werden dürfen (für die Armeen-Bewegen-Phase) */
	private ArrayList<Land> zulaessigeLaender;
	
	/** Instanz der Klasse zurückgeben */
	public static SpielVerwaltung getInstance() { return SpielVerwaltung.instance; }
	
	/** Konstruktor ohne Parameter */
	public SpielVerwaltung() {
		SpielVerwaltung.instance = this;
		this.w = new Welt();
		this.landMgr = new LandVerwaltung(w);
		this.missionMgr = new MissionsVerwaltung(w);
		this.spielerMgr = new SpielerVerwaltung(w);
		this.kartenMgr = new KartenVerwaltung(w);
		this.angriffMgr = new AngriffsVerwaltung(kartenMgr);
	}
	
	/** Angriff auf ein Land
	 * @param a Angriffsstärke
	 * @param v Verteidigungsstärke
	 * @throws SpielBeendetException wenn der Angriff das Spiel beendet (also ein Spieler den letzten Gegenspieler eliminiert)
	 * @return Result-Objekt mit Infos über den Ausgang des Angriffs */
	public Result angriffAufLand(int[] a, int[] v) throws SpielBeendetException { return angriffMgr.angriffAufLand(a,v); }
	
	/** Angriff-Anfrage senden
	 * @return Zahl der möglichen Angriffe, die gestartet werden können */
	public int angriffInit(Spieler s) { return angriffMgr.init(s); }
	
	
	/** Neuen Angriff setzen lassen
	 * @param n Index
	 * @return Angriffs-Objekt */
	public Angriff determineNewAngriff(int n) { return angriffMgr.newAngriff(n); }
	
	/** Neuen Angriff setzen. */
	public void setAngriff(Angriff a) { angriffMgr.setAngriff(a); }
	
	/** Entfernen eines übergebenen Spieler-Objektes aus dem Pool der registrierten Spieler
	 * @param s	Das zu löschende Spieler-Objekt */
	public void disposeSpieler(Spieler s) { SpielerVerwaltung.disposeSpieler(s); }
	
	/** Shortcut-Methode zum Prüfen, ob nur noch ein Spieler "im Rennen" ist
	 * (nützlich zum Testen auf "Spiel beendet?")
	 * @return	true, wenn die Bedingung erfüllt ist, false sonst */
	public boolean einSpielerUebrig() { return SpielerVerwaltung.countSpieler() == 1; }
	
	/** Rückgabe des Spielers, der aktuell am Zug ist */
	public Spieler getAkteur() { return SpielerVerwaltung.getAkteur(); }
	
	/** Anzahl der möglichen Ziele zurückgeben */
	public int getAnzahlZiele() { return angriffMgr.getAnzahlZiele(); }
	
	/** Spielermanager zurückgeben
	 * @return	SpielerManager-Objekt */
	public SpielerVerwaltung getSpielerMgr() { return spielerMgr; }
	
	/** Welt zurückgeben
	 * @return	Welt-Objekt */
	public Welt getWelt() { return w; }
	
	/** Berechnung der neuen Armeen für den aktuellen Spieler
	 * @return	Anzahl der ihm zustehenden neuen Armeen */
	public int getNewArmeen() { return spielerMgr.calcNeueArmeen(SpielerVerwaltung.getAkteur()); }
	
	/** Ersten Spieler im Spieler-Array erfragen - wird zur Bestimmung eines Sieges
	 * durch K.O. benutzt
	 * @return Spieler an niedrigstmöglichem Index im Spieler-Array */
	public Spieler grabFirstSpieler() { return SpielerVerwaltung.grabFirstSpieler(); }
	
	/** Methode zum Prüfen, ob ein Spieler kein Land mehr besitzt und deshalb verloren hat
	 * @param s Spieler, der überprüft wird
	 * @return true, wenn er tatsächlich kein Land mehr besitzt, false sonst
	 * @throws SpielBeendetException wenn die Partie auf diese Art und Weise beendet wird */
	public boolean hasLost(Spieler s) {
		if (spielerMgr.hatVerloren(s)) {
			return true;
		} else return false;
	}
	
	/** Durchführen des Kartentauschs
	 * @return	Result-Objekt mit Informationen zu den neu verfügbaren Armeen und den getauschten Karten */
	public Result kartentausch() { return kartenMgr.tausch(); }
	
	/** Kartentausch-Anfrage senden
	 * @return true, wenn Karten getauscht werden können; false sonst */
	public boolean kartentauschInit() { return kartenMgr.init(); }
	
	/** Methode zum Laden des Spielstands */
	public void load(File file) throws FileNotFoundException, ClassNotFoundException, IOException, IncompatibleWeltException {
		if (file == null) throw new FileNotFoundException();
		// Zuerst Abschneiden der Dateiendung, falls sie vorhanden ist
		String name = file.getName();
		StringTokenizer token = new StringTokenizer(name, ".");
		name = token.nextToken();
		
		// InputStream anwerfen
		ObjectInputStream ois = null;
			ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
			// Zuerst den aktuellen Index lesen
			int n = ois.read();

			// Jetzt das Welt-Objekt lesen
			Welt loadedWorld = (Welt) ois.readObject();
			
			// Eigenschaften des eingelesenen Welt-Objektes in die bestehende Welt einbindne
			w.setProperties(loadedWorld);
			
			// Der nächste Spieler wird bestimmt (derjenige, der zuvor das Spiel gespeichert hatte)
			SpielerVerwaltung.setAkteur(n-1);
			spielerMgr.naechsterSpieler();
			
			ois.close();
	}
	
	/** Methode zum Nachziehen von a Armeen im Kontext des aktuellen Angriffs */
	public void nachziehen(int a) { angriffMgr.nachziehen(a); }
	
	/** Festsetzen des nächsten Spielers */
	public void nextSpieler() {
		this.resetStats();
		spielerMgr.naechsterSpieler();
	}
	
	/** Methode zur Ausgabe aller Spieler
	 * (Durchreichen an SpielerVerwaltung) */
	public Result printSpieler() throws SpielerzahlException { return spielerMgr.listSpieler(); }
	
	/** Methode zum Registrieren eines neuen Spielers mit Namen und Farbe */
	public void register(Spieler name, String farbe) throws SpielerExistiertBereitsException,
	SpielerzahlException, IOException { spielerMgr.addSpieler(name,farbe); }
	
	/** Stats zurücksetzen. Diese Methode wird am Ende des Zuges aufgerufen und setzt alle Land-Referenzen
	 * zurück auf "Nicht bewegt" und den "alten" Akteur auf "nicht siegreich" */
	public void resetStats() {
		// Der "War Erfolgreich"-Status des aktiven Spielers wird zurückgesetzt
		// (so wird sichergestellt, dass er bei seinem nächsten Zug wieder eine Karte bekommen kann).
		SpielerVerwaltung.getAkteur().setSiegreich(false);
		
		// Iterator für die Länder initialisieren und nach doppeltem Iterator-Prinzip alle Länder auf
		// setBewegt = false setzen
		Iterator<Kontinent> iterK = w.getKontinente().iterator();
		Iterator<Land> iterL;
		Kontinent aktK;
		while(iterK.hasNext()) {
			aktK = iterK.next();
			iterL = aktK.getLaender().iterator();
			while(iterL.hasNext()) {
				Land l = iterL.next();
				l.setBewegt(false);
			}
		}
	}
	
	/** Diese Methode speichert ab, wobei sie Objektserialisierung benutzt,
	 * um dieses zu tun.
	 * @throws FileNotFoundException	wenn die Datei nicht gefunden werden kann */
	public void save(File file) throws FileNotFoundException, IOException {
		if (file == null) throw new FileNotFoundException();
		// Zuerst Abschneiden der Dateiendung, falls sie vorhanden ist
		String name = file.getName();
		StringTokenizer token = new StringTokenizer(name, ".");
		name = token.nextToken();
		
		// Gespeichert werden muss:
		// - Das Spieler-Array der Welt
		// - Welcher Spieler gerade an der Reihe ist
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(file.getParent() + "\\" + name + ".risksav"));
			// 1: Der Index des aktuellen Spielers
			int n = SpielerVerwaltung.getIndex();
			oos.write(n);
			
			oos.writeObject(w);
			
			oos.close();
		} catch(NotSerializableException e) {
			System.out.println("Not serializable exception? "+e.getMessage());
		}
	}
	/** Sucht einen Spieler mit dem übergebenen String als Namen */
	public Spieler searchSpieler(String s) { return SpielerVerwaltung.searchSpieler(s); }
	
	/** Methode zum Re-Initialisieren einiger Daten nach Spielende (Kartenstapel neu mischen,
	 * Spieler aus dem Array entfernen etc. */
	public void spielumgebungKonstruieren() {
		// Neues Spieler-Array bauen
		@SuppressWarnings("unused")
		Spieler[] allesp = w.getSpieler();
		allesp = new Spieler[6];
	}
	
	/** Methode zum Initiieren einer Partie Risiko
	 * @throws SpielerzahlException, wenn weniger als 3 Spieler registriert sind */
	public void startePartie() throws SpielerzahlException {
		// Wenn die Spielerzahl ok ist (Frage an SpielerVerwaltung)...
		if (spielerMgr.spielerzahlOK()) {
			// Auslesen und Organisieren der Spielbrett-Struktur mit Ländern
			// und Besitz der Spieler zu Partiebeginn. Der LandVerwalter
			// teilt dem SpielerVerwalter auch direkt die Nummer des Spielers mit,
			// der den ersten Zug tun soll
			landMgr.organizeWelt();
			// Ausgabe der eingelesenen XML-Datei, gegliedert in Kontinente
			//landMgr.gibAlleAus();
			// Auslesen und Organisieren der Missionen (auch: Zuteilung der Missionen an Spieler)
			missionMgr.organizeMissionen();
			// Vorbereitungen abgeschlossen
		} else {
			// Zu wenig sind registriert
			throw new SpielerzahlException(SpielerzahlException.ZUWENIG);
		}
	}
	
	/** Methode zum Erhöhen einer Armeenzahl auf einem Land.
	 * @param index Index des Landes in der Liste der Länder im Besitz des aktuellen Spielers, dessen Armeen verstärkt werden sollen
	 * @return true, wenn alles geklappt hat, false bspw wenn das Land-Objekt an diesem Index nicht existiert */
	public boolean verstaerkeArmeen(int index) {
		if (index >= SpielerVerwaltung.getAkteur().getBesitz().size())
			return false;
		SpielerVerwaltung.getAkteur().getBesitz().get(index).plusStaerke(1);
		return true;
	}
	
	/** Methode zum Erhöhen der Armeenzahl auf einem Land.
	 * @param l	Land, das verstärkt werden soll */
	public void verstaerkeArmeen(Land l) throws SpielerNichtBesitzerException {
		// Aktuellen Spieler ziehen (wird zum Vergleich benötigt)
		Spieler akteur = SpielerVerwaltung.getAkteur();
		// Besitzer des Landes ziehen
		Spieler besitzer = l.getSpieler();
		// Prüfen auf Gleichheit
		if (akteur.equals(besitzer)) {
			// Es stimmt. In diesem Fall Verstärkung durchführen
			try {
				l.plusStaerke(1);
				LandVerwaltung.searchLand(l.getName()).plusStaerke(1);
			} catch (KeinLandException e) {
				// Nichts zu tun. Das Land existiert, wenn drauf geklickt werden kann.
				System.out.println("DAS LAND GIBTS NICHT=?!=!=!=!=!=!=!=!?!?!?!?!");
			}
		} else throw new SpielerNichtBesitzerException(besitzer, l);
	}
	
	/** Würfeln lassen
	 * @param wzahl Anzahl der Würfel, mit denen gewürfelt wird
	 * @return Array mit Ergebnissen oder [0] */
	public int[] wuerfeln(int wzahl) {
		int[] ergebnisse = new int[wzahl];
		for(int i=0; i<wzahl;i++) {
			ergebnisse[i] = (int)Math.floor(Math.random()*6)+1;
		}
		return ergebnisse;
	}
	
	/** Gibt die maximale Anzahl an "Würfeln" (also kämpfenden Armeen) an */
	public int wuerfelzahl(String n) { return angriffMgr.wuerfelzahl(n); }

	/** Identifizeren eines Landes auf der Weltkarte über seine FarbMap.
	 * Wird von der GUI benutzt, um zu ermitteln, wo auf der Karte ein Mausklick passiert ist.
	 * @param farbe	int-Farbe an Maus-Position
	 * @return	Land mit der passenden FarbMap, oder null, wenn es keines gibt. */
	public Land getLandByFarbMap(int farbe) {
		ArrayList<Kontinent> alleK = w.getKontinente();
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
	
	/** Gibt die Länder ungeordnet in einem Vector zurück */
	public Vector<Land> getAlleLaender() { return LandVerwaltung.getAlleLaender(); }
	
	/** Berechnen der möglichen Bewegungen für den übergebenen Spieler */
	public int armeenbewegenInit(Spieler s) {
		zulaessigeLaender = new ArrayList<Land>();
		Spieler sp = SpielerVerwaltung.searchSpieler(s.getName());
		Iterator<Land> iterL = sp.getBesitz().iterator();
		int moeglicheLaender = 0;
		while(iterL.hasNext()) {
			Land land = iterL.next();
			// Frage, ob dieses Land in diesem Zug bewegt wurde.
			// Wenn nicht, in die Liste zulässiger Länder eintragen
			if (!land.getBewegt()) {
				// Wenn das Land mehr als 1 Armee hat, wird der strcounter hochgezählt.
				if (land.getStaerke() > 1)
					moeglicheLaender++;
				zulaessigeLaender.add(land);
			}
		}
		return moeglicheLaender;
	}
	
	/** Durchführen einer Bewegung von Land source zu Land dest mit der Armeenzahl zahl */
	public void armeenbewegen(Land source, Land dest, int zahl) {
		try {
			LandVerwaltung.searchLand(source.getName()).minusStaerke(zahl);
			LandVerwaltung.searchLand(dest.getName()).plusStaerke(zahl);
			// Bewegt-Status setzen
			LandVerwaltung.searchLand(source.getName()).setBewegt(true);
			LandVerwaltung.searchLand(dest.getName()).setBewegt(true);
		} catch (KeinLandException e) {	}
	}
	
	
	// Ungenutzte Methoden, die aber vom Interface verlangt werden
	/** Leere Implementierung */
	public void login(Spieler user, ClientMethods client) throws SimonRemoteException, IOException, 
	SpielerExistiertBereitsException, SpielerzahlException {	}
	/** Leere Implementierung */
	public void unregister(Spieler c) { }
	/** Leere Implementierung */
	public int getLoggedInSpieler() { return 0; }
	/** Leere Implementierung */
	public void startGame() { }
	/** Leere Implementierung */
	public void register(Spieler s) { }
	/** Leere Implementierung */
	public void informServer(Spieler user) { }
	/** Leere Implementierung */
	public void attack(Angriff a) { }
	/** Leere Implementierung */
	public void defend(int d) { }
	/** Leere Implementierung */
	public void sendChatMessage(ChatMessage nachricht) { }
	/** Leere Implementierung */
	public void forceUpdate(Spieler s, int mode) { }
	/** Leere Implementierung */
	public boolean pruefeMission(Spieler s) { return false; }
	/** Leere Implementierung */
	public Spieler pruefeAlleMissionen() { return null; }
	/** Leere Implementierung */
	public boolean isSpielGestartet() {	return false; }
}
