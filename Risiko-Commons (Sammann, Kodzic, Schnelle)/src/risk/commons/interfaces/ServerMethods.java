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
 * k�nnen vom Client aufgerufen werden, um etwas auf dem Server zu tun. Dazu geh�ren weite Teile der Spiellogik,
 * das Chatsystem und das An- und Abmelden eines Clients.
 * @author Marcel
 */
public interface ServerMethods {
	
	/** Server von Client-Seite aus �ber Spieler-Objekt-Ver�nderungen informieren */
	public void informServer(Spieler user);
	/** L�uft das Spiel bereits? */
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
	/** Spieler mit �bergebenem Suchstring suchen */
	public Spieler searchSpieler(String name);
	/** Spieler entfernen */
	public void disposeSpieler(Spieler s);
	/** Zahl der Mitspieler am Spiel bekommen */
	public int getLoggedInSpieler();
	
	/** Land-Objekt anhand seines Farbmap-Werts ermitteln */
	public Land getLandByFarbMap(int farbMapVal);
	
	/** Neue Armeen zum Verst�rken berechnen */
	public int getNewArmeen();
	/** Land verst�rken */
	public void verstaerkeArmeen(Land l) throws SpielerNichtBesitzerException;
	
	/** Alle L�nder in einer Liste bekommen */
	public Vector<Land> getAlleLaender();
	/** Welt-Objekt bekommen */
	public Welt getWelt();
	
	/** Kartentausch initialisieren. Ist er m�glich oder nicht? */
	public boolean kartentauschInit();
	/** Kartentausch durchf�hren */
	public Result kartentausch();
	
	/** Angriffe initialisieren. Wie viele Angriffe sind m�glich? */
	public int angriffInit(Spieler s);
	/** Angriff durchf�hren */
	public void attack(Angriff a);
	/** Verteidigung durchf�hren */
	public void defend(int d);
	/** Armeen nachr�cken lassen */
	public void nachziehen(int zahl);
	
	/** Bewegungen initialisieren. Wie viele sind m�glich? */
	public int armeenbewegenInit(Spieler s);
	/** Bewegung von a nach b durchf�hren */
	public void armeenbewegen(Land source, Land dest, int zahl);
	
	/** Hat ein Spieler verloren? */
	public boolean hasLost(Spieler s);
	/** Ist nur noch ein Spieler �brig? */
	public boolean einSpielerUebrig();
	/** Ermitteln des ersten Spielers im Array */
	public Spieler grabFirstSpieler();
	/** Weiterreichen des Akteur-Status an den n�chsten Spieler */
	public void nextSpieler();
	
	/** Nachricht �ber den Chat-Kanal senden */
	public void sendChatMessage(ChatMessage nachricht);
	
	/** Mission eines einzelnen Spielers �berpr�fen */
	public boolean pruefeMission(Spieler s);
	/** Alle Missionen �berpr�fen */
	public Spieler pruefeAlleMissionen();
}
