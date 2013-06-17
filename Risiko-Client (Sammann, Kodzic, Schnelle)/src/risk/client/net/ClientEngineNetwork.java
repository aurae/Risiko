package risk.client.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import risk.client.ui.gui.RiskClientGUI;
import risk.client.ui.gui.comp.ErrorDialog;
import risk.client.ui.gui.panels.LoginPanel;
import risk.commons.exceptions.IncompatibleWeltException;
import risk.commons.exceptions.SpielerExistiertBereitsException;
import risk.commons.exceptions.SpielerNichtBesitzerException;
import risk.commons.exceptions.SpielerzahlException;
import risk.commons.interfaces.ClientMethods;
import risk.commons.interfaces.ServerMethods;
import risk.commons.valueobjects.Angriff;
import risk.commons.valueobjects.ChatMessage;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import de.root1.simon.Lookup;
import de.root1.simon.Simon;
import de.root1.simon.SimonUnreferenced;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.EstablishConnectionFailed;
import de.root1.simon.exceptions.LookupFailedException;
import de.root1.simon.exceptions.SimonRemoteException;

/**
 * Diese Klasse ist die clientseitige Schnittstelle der Netzwerkimplementierung. Sie leitet Anfragen der GUI-Schicht an die
 * Spiellogik an ein Objekt weiter, das via SIMON die Server-Verbindung herstellt.
 * @author Marcel
 */
@SimonRemote(value = {ServerMethods.class, ClientMethods.class})
public class ClientEngineNetwork extends Observable implements ServerMethods, ClientMethods, Serializable, SimonUnreferenced {
	private static final long serialVersionUID = 721275475611245976L;
	
	/** Instanz der Klasse zur statischen Rückgabe bei Anfragen durch andere Klassen */
	private static ClientEngineNetwork instance = null;
	/** Server-Schnittstelle */
	private ServerMethods server = null;
	/** Lookup für die Verbindung zum Server */
	private Lookup nameLookup;
	
	/** Konstruktor (ist privat, damit der statische Charakter des Serverzugriffs gewahrt ist) */
    private ClientEngineNetwork() throws EstablishConnectionFailed, SimonRemoteException, IOException, LookupFailedException {
    	// Im Konstruktor wird die Verbindung zum Server hergestellt; dazu werden Host, Name und Port des Dienstes explizit aufgerufen
    		String host = RiskClientGUI.getInstance().getHost().isEmpty() ? "127.0.0.1" : RiskClientGUI.getInstance().getHost();
			String name = "risk";
			int port = 33333;
		// Der Lookup sichert den Serverstatus
			nameLookup = Simon.createNameLookup(host, port);
	        server = (ServerMethods) nameLookup.lookup(name);
	    // Aber wenn das Spiel schon läuft, geht das Einloggen nicht mehr. Dann schmeißen wir den Client gleich wieder raus
	        if (isSpielGestartet()) {
	        	new ErrorDialog(RiskClientGUI.getInstance(), 
        				"Das Spiel ist bereits gestartet. Bitte warte, \nbis es beendet ist, und versuche es dann noch einmal.",
        				"Verbindungsproblem");
        		RiskClientGUI.getInstance().setVisible(false);
        		nameLookup.release(server);
        		System.exit(-1);
	        }
    }

    /** Getter-Methode, mit der alle GUI-Klassen auf Domain-Methoden zugreifen können
     * @return	Die Instanz dieser Klasse, die Serveranfragen regelt */
    public static ClientEngineNetwork getEngine() {
    		// Beim Erstzugriff neue Instanz erzeugen
            if (ClientEngineNetwork.instance == null) {
            	try {
            		ClientEngineNetwork.instance = new ClientEngineNetwork();
            	} catch (EstablishConnectionFailed e) {
            		// Verbindungsfehler
            		new ErrorDialog(RiskClientGUI.getInstance(), 
            				"Es konnte keine Verbindung zum Server \nhergestellt werden. Bitte überprüfe die Verbindung.",
            				"Verbindungsproblem");
            		RiskClientGUI.getInstance().setVisible(false);
            		System.exit(-1);
            	} catch (SimonRemoteException e) {
            		System.out.println("SimonRemoteException");
            	} catch (IOException e) {
            		System.out.println("IOException");
            	} catch (LookupFailedException e) {
            		System.out.println("Lookupfailed");
            	}
            }
            return ClientEngineNetwork.instance;
    }

	/** Unreferenced-Methode des SimonUnreferenced-Interfaces. Beendet das Programm einfach, wenn der Server sich abschaltet */
	public void unreferenced() { System.exit(1); }
    
	/** Einloggen des Clients auf dem Server */
	public void login(Spieler user, ClientMethods client) throws SimonRemoteException, IOException, 
	SpielerExistiertBereitsException, SpielerzahlException { this.server.login(user, client); }

	/** Welt-Objekt vom Server holen
	 * @return Welt */
	public Welt getWelt() {	return this.server.getWelt(); }

	/** Registrieren eines Spielers in der Welt */
	public void register(Spieler name) throws IOException,
			SpielerExistiertBereitsException, SpielerzahlException { this.server.register(name); }

	/** Suchen eines Spieler-Objektes mit Namen
	 * @return Fund oder null */
	public Spieler searchSpieler(String name) {	return this.server.searchSpieler(name); }

	/** Spieler entfernen */
	public void disposeSpieler(Spieler s) {	this.server.disposeSpieler(s); }
	
	/** Spieler abmelden */
	public void unregister(Spieler c) {
		System.out.println("Abmelden...");
		this.server.unregister(RiskClientGUI.getSpieler());
	}
	
	/** Verbindung zum Server beenden */
	public void disconnect() {
		System.out.println("Client hat sich abgemeldet.");
		nameLookup.release(server);
		System.exit(1);
	}

	/** Methode aus dem ClientMethods-Interface. Setzt den Spielleiter-Status des Client-Spielers
	 * @param b	true wenn der Client Spielleiter ist, false sonst */
	public void setGameMaster(boolean b) { RiskClientGUI.getSpieler().setGameMaster(b); }

	/** Ermittelt die Zahl eingeloggter Spieler
	 * @return Zahl der Teilnehmer an der Partie */
	public int getLoggedInSpieler() { return this.server.getLoggedInSpieler(); }
	
	/** Startet die Partie */
	public void startGame() { this.server.startGame(); }
	
	/** Ermittelt den Spieler, der an der Reihe ist
	 * @return Akteur */
	public Spieler getAkteur() { return this.server.getAkteur(); }

	/** Diese Methode ist der Callback vom Server. Die mitgelieferte Konstante gibt an, was der Client verändern muss
	 * @param s		Verändertes Spieler-Objekt
	 * @param param Optionaler Parameter, der übergeben werden kann */
	public void aktualisiere(Spieler s, Result param) {
		// Spielerreferenz updaten, wenn es ein Update für den Spieler selbst ist
		if (s.getName().equals(RiskClientGUI.getSpieler().getName()))
			RiskClientGUI.setSpieler(s);
		this.setChanged();
		this.notifyObservers(param);
	}
	
	/** Ermittelt eine Landreferenz abhängig von der übergebenen Farbe, die ein Mausklick ermittelt hat
	 * @param farbMapVal	Int-Farbe an Klickposition
	 * @return Landreferenz, zu dem die Farbe passt, oder null */
	public Land getLandByFarbMap(int farbMapVal) { return this.server.getLandByFarbMap(farbMapVal); }

	/** Neue Armeen, die dem aktuellen Spieler zustehen, ausrechnen und zurückgeben */
	public int getNewArmeen() {	return this.server.getNewArmeen(); }
	
	/** Ein übergebenes Land um 1 Armee verstärken */
	public void verstaerkeArmeen(Land l) throws SpielerNichtBesitzerException {	this.server.verstaerkeArmeen(l); }

	/** Ein Observer-Objekt hinzufügen, das sich benachrichtigen lassen kann. */
	public void addObserver(Observer o) { super.addObserver(o); }
	
	/** Ein Observer-Objekt von der Liste der zu Benachrigenden löschen. */
	public void removeObserver(Observer o) { super.deleteObserver(o); }
	
	/** Alle Länder ohne Kontinenteinteilung zurückgeben lassen
	 * @return Länderliste */
	public Vector<Land> getAlleLaender() { return this.server.getAlleLaender(); }
	
	/** Server über Spielerstatus dieses Clients informieren (aktualisieren der Serverreferenz) */
	public void informServer(Spieler s) { this.server.informServer(s); }
	
	/** Einleiten der Kartentauschphase
	 * @return Kartentausch möglich oder nicht */
	public boolean kartentauschInit() {	return this.server.kartentauschInit(); }
	
	/** Kartentauschphase
	 * @return Result-Objekt mit eingetauschten Karten und Anzahl neuer Armeen */
	public Result kartentausch() { return this.server.kartentausch(); }
	
	/** Einleiten der Angriffsphase
	 * @return Zahl möglicher Angriffe, die durchführbar sind */
	public int angriffInit(Spieler s) {	return this.server.angriffInit(s); }
	
	/** Angriffsphase
	 * @param a	Angriff, der durchgeführt werden soll */
	public void attack(Angriff a) {	this.server.attack(a); }
	
	/** Verteidigungsphase
	 * @param d Zahl der verteidigenden Armeen */
	public void defend(int d) {	this.server.defend(d); }
	
	/** Armeen nachrücken lassen
	 * @param zahl Zahl der nachrückenden Armeen */
	public void nachziehen(int zahl) { this.server.nachziehen(zahl); }

	/** Prüfen ob ein Spieler verloren hat
	 * @param s Zu prüfender Spieler
	 * @return wahr oder falsch */
	public boolean hasLost(Spieler s) {	return this.server.hasLost(s); }

	/** Prüfen ob nur noch ein Spieler übrig ist (also das Spiel entschieden ist)
	 * @return ja oder nein */
	public boolean einSpielerUebrig() {	return this.server.einSpielerUebrig(); }

	/** Ersten Spieler im Spieler-Array holen (nach der einSpielerUebrig()-Frage der Gewinner)
	 * @return Spieler-Referenz des potentiellen Gewinners */
	public Spieler grabFirstSpieler() {	return this.server.grabFirstSpieler(); }
	
	/** Zug beenden und an den nächsten Spieler weitergeben */
	public void nextSpieler() {	this.server.nextSpieler(); }
	
	/** Nachricht an alle Spieler senden
	 * @param nachricht	Nachricht (duh) */
	public void sendChatMessage(ChatMessage nachricht) { this.server.sendChatMessage(nachricht); }
	
	/** Einleiten der Bewegungsphase
	 * @return Anzahl möglicher Bewegungen */
	public int armeenbewegenInit(Spieler s) { return this.server.armeenbewegenInit(s); }
	
	/** Bewegungsphase
	 * @param von 	Land, von dem aus die Bewegung passiert
	 * @param nach 	Land, in das die Armeen ziehen
	 * @param zahl	Anzahl zu bewegender Armeen */
	public void armeenbewegen(Land von, Land nach, int zahl) { this.server.armeenbewegen(von,nach,zahl); }
	
	/** informClients()-Methode auf dem Server erzwingen
	 * @param mode	Welcher Mode soll auf allen Clients aufgerufen werden? */
	public void forceUpdate(Spieler s, int mode) { this.server.forceUpdate(s, mode); }
	
	/** Client-Spieler ziehen (für Server)
	 * @return Client-Spieler dieses Clients */
	public Spieler getSpieler() { return RiskClientGUI.getSpieler(); }
	
	/** Ist der Spieler eingeloggt?
	 * @return ja oder nein */
	public boolean isLoggedIn() { return LoginPanel.getLoginState(); }
	
	/** Persistenz: Laden einer externen Spielstanddatei */
	public void load(File file) throws FileNotFoundException, ClassNotFoundException,
	IOException, IncompatibleWeltException { this.server.load(file); }
	
	/** Persistenz: Speichern einer externen Spielstanddatei */
	public void save(File file) throws FileNotFoundException, IOException { this.server.save(file); }

	/** Prüfen der Mission vom übergebenen Spieler
	 * @param s Spieler */
	public boolean pruefeMission(Spieler s) { return this.server.pruefeMission(s); }

	/** Prüfen aller Missionen
	 * @return Spieler, dessen Mission erfüllt worden ist, oder null */
	public Spieler pruefeAlleMissionen() { return this.server.pruefeAlleMissionen(); }

	/** Anfragen, ob die Partie bereits gestartet worden ist */
	public boolean isSpielGestartet() {	return this.server.isSpielGestartet(); }
}
