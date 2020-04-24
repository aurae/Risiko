package risk.commons.interfaces;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import risk.commons.exceptions.IncompatibleWeltException;
import risk.commons.exceptions.SpielerExistiertBereitsException;
import risk.commons.exceptions.SpielerNichtBesitzerException;
import risk.commons.exceptions.SpielerzahlException;
import risk.commons.valueobjects.Angriff;
import risk.commons.valueobjects.ChatMessage;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import de.root1.simon.exceptions.SimonRemoteException;

/**
 * Dieses Interface definiert die Schnittstelle von Client zu Server. Alle Methoden, die hier drin stehen,
 * können vom Client aufgerufen werden, um etwas auf dem Server zu tun. Dazu gehören weite Teile der Spiellogik,
 * das Chatsystem und das An- und Abmelden eines Clients.
 * @author Marcel
 */
public interface ServerMethods {
	
	/** Server von Client-Seite aus über Spieler-Objekt-Veränderungen informieren */
	public void informServer(Spieler user);
	/** Läuft das Spiel bereits? */
	public boolean isSpielGestartet();
	
	/** Spielstand abspeichern */
	public void load(File file) throws FileNotFoundException, ClassNotFoundException, IOException,
	IncompatibleWeltException;
	/** Spielstand laden */
	public void save(File file) throws FileNotFoundException, IOException;
	
	/** Spieler einloggen */
	public void login(Spieler user, ClientMethods client) throws SimonRemoteException, IOException, 
	SpielerExistiertBereitsException, SpielerzahlException;
	/** Spieler registrieren */
	public void register(Spieler name) throws IOException, SpielerExistiertBereitsException, SpielerzahlException;
	/** Spieler abmelden */
	public void unregister(Spieler name);
	
	/** Spiel starten */
	public void startGame();
	
	/** Update auf allen Clients erzwingen */
	public void forceUpdate(Spieler s, int mode);
	
	/** Spieler bekommen, der gerade an der Reihe ist */
	public Spieler getAkteur();
	/** Spieler mit übergebenem Suchstring suchen */
	public Spieler searchSpieler(String name);
	/** Spieler entfernen */
	public void disposeSpieler(Spieler s);
	/** Zahl der Mitspieler am Spiel bekommen */
	public int getLoggedInSpieler();
	
	/** Land-Objekt anhand seines Farbmap-Werts ermitteln */
	public Land getLandByFarbMap(int farbMapVal);
	
	/** Neue Armeen zum Verstärken berechnen */
	public int getNewArmeen();
	/** Land verstärken */
	public void verstaerkeArmeen(Land l) throws SpielerNichtBesitzerException;
	
	/** Alle Länder in einer Liste bekommen */
	public Vector<Land> getAlleLaender();
	/** Welt-Objekt bekommen */
	public Welt getWelt();
	
	/** Kartentausch initialisieren. Ist er möglich oder nicht? */
	public boolean kartentauschInit();
	/** Kartentausch durchführen */
	public Result kartentausch();
	
	/** Angriffe initialisieren. Wie viele Angriffe sind möglich? */
	public int angriffInit(Spieler s);
	/** Angriff durchführen */
	public void attack(Angriff a);
	/** Verteidigung durchführen */
	public void defend(int d);
	/** Armeen nachrücken lassen */
	public void nachziehen(int zahl);
	
	/** Bewegungen initialisieren. Wie viele sind möglich? */
	public int armeenbewegenInit(Spieler s);
	/** Bewegung von a nach b durchführen */
	public void armeenbewegen(Land source, Land dest, int zahl);
	
	/** Hat ein Spieler verloren? */
	public boolean hasLost(Spieler s);
	/** Ist nur noch ein Spieler übrig? */
	public boolean einSpielerUebrig();
	/** Ermitteln des ersten Spielers im Array */
	public Spieler grabFirstSpieler();
	/** Weiterreichen des Akteur-Status an den nächsten Spieler */
	public void nextSpieler();
	
	/** Nachricht über den Chat-Kanal senden */
	public void sendChatMessage(ChatMessage nachricht);
	
	/** Mission eines einzelnen Spielers überprüfen */
	public boolean pruefeMission(Spieler s);
	/** Alle Missionen überprüfen */
	public Spieler pruefeAlleMissionen();
}
