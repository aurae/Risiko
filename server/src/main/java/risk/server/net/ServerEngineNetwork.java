package risk.server.net;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import risk.commons.ChangeConst;
import risk.commons.SpielerState;
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
import risk.commons.valueobjects.Karte;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import risk.server.domain.LandVerwaltung;
import risk.server.domain.SpielVerwaltung;
import risk.server.domain.SpielerVerwaltung;
import risk.server.ui.gui.ServerGUI;
import de.root1.simon.Registry;
import de.root1.simon.Simon;
import de.root1.simon.SimonUnreferenced;
import de.root1.simon.annotation.SimonRemote;
import de.root1.simon.exceptions.NameBindingException;
import de.root1.simon.exceptions.SimonRemoteException;

/**
 * Serverseitige Implementierung der Client/Server-Schnittstelle.
 * @author Marcel, Srdan
 */
// Klasse markieren
@SimonRemote(value = {ServerMethods.class, ClientMethods.class})
public class ServerEngineNetwork implements ServerMethods, SimonUnreferenced {
	/** Port, auf dem der Server läuft */
	private int port = 33333;
	/** Name des Dienstes */
	private String name = "risk";
	/** Registry für Serveranbindung */
	private Registry registry;
	/** Läuft der Server? */
	private boolean running = false;

	/** Ist das Spiel gestartet? */
	private boolean spielGestartet = false;

	/** HashMap mit Spieler-Client-Zuordnungen. Hier sind alle teilnehmenden Spieler gespeichert */
	private HashMap<Spieler, ClientMethods> clients = new HashMap<Spieler, ClientMethods>(6);
	
	// Attribute, die für die Kampfabhandlung wichtig sind.
	/** Aktueller Angriff */
	private Angriff curAngriff = null;
	/** Aktueller Angreifer-Client */
	private ClientMethods curAttacker = null;
	/** Aktuelles Angreifer-Spieler-Objekt */
	private Spieler spielerAttacker = null;
	/** Aktueller Verteidiger-Client */
	private ClientMethods curDefender = null;
	/** Aktuelles Verteidiger-Spieler-Objekt */
	private Spieler spielerDefender = null;
	
	/** Referenz auf Server-GUI-Klasse */
	private ServerGUI gui;
	
	/** Konstruktor
	 * @throws UnknownHostException	wenn der Host nicht bekannt ist
	 * @throws IOException			wenn beim Einrichten des Servers ein Problem auftritt
	 * @throws NameBindingException wenn die Registry nicht gebunden werden kann */
	public ServerEngineNetwork() throws UnknownHostException, IOException, NameBindingException {
        // SpielVerwaltung erstellen
		new SpielVerwaltung();
		// Server öffnen
		registry = Simon.createRegistry(port);
		registry.start();
        registry.bind(name, this);
        // Running-Stat setzen
        running = true;
		// GUI öffnen
		gui = new ServerGUI(this);
	}
	
	/** Diese Methode wird von Serverseite aufgerufen, wenn sich irgendetwas auf dem User Interface der Clients
	 * verändert, und kein weiterer Parameter übergeben werden muss. Ist eine Convenience Method für "informClients(mode, null)"
	 * @param mode	Konstante aus der ChangeConst-Klasse, deren Wert den Clients sagt, _was_ sie überhaupt aktualisieren sollen */
	public void informClients(int mode) {
		Result res = new Result();
		res.push(mode);
		this.informClients(res);
	}
	
	/** Diese Methode wird von Serverseite aufgerufen, wenn sich irgendetwas auf dem User Interface der Clients
	 * verändert. Prinzipiell sagt diese Methode nur: "Da ist was passiert; aktualisiert euch mal, damit ihr das mitbekommt."
	 * @param res	Result-Objekt mit beliebig vielen Parametern */
	public void informClients(Result res) {
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
                Entry<Spieler, ClientMethods> spieler = iter.next();
                spieler.getValue().aktualisiere(spieler.getKey(), res);
        }
	}
	
	
	/** Verbindungen aller Spieler trennen und den Server herunterfahren. */
	public void disconnectGame() {
		try {
			registry.unbind(name);
			registry.stop();
			System.exit(1);
		} catch (Exception e) {
			System.exit(1);
		}
		gui.dispose();
	}
	
	/** Gibt den Status des Servers zurück (wird von der ServerGUI benutzt).
	 * @return	Serverstatus als String */
	public String getStatus() {
		if (running) return ("RISKSERVER läuft auf Port " + port + " als " + name + "...");
		else return ("RISKSERVER wartet...");
	}

	/** Login eines Spielers.
	 * @param user		Einzuloggendes Spieler-Objekt
	 * @param client	Referenz auf Client-Objekt (wichtig für Callback) */
	public void login(Spieler user, ClientMethods client) throws SimonRemoteException, IOException, 
	SpielerExistiertBereitsException, SpielerzahlException {
		// Eventuell Game Master-Status vergeben, wenn noch niemand vorher registriert war
		if (SpielerVerwaltung.countSpieler() == 0)  {
			user.setGameMaster(true);
		} else
			user.setGameMaster(false);
		
		// Neuen Spieler in die Clients-Liste eintragen
		clients.put(user, client);
		
		// In der Welt registrieren
		this.register(user);
	}

	/** Getter-Methode für das Weltobjekt. Kurzform für "SpielVerwaltung.getInstance().getWelt()" */
	public Welt getWelt() {
		return SpielVerwaltung.getInstance().getWelt();
	}

	/** Registrieren eines Spielers.
	 * @param name	Der zu registrierende Spieler */
	public void register(Spieler name) throws IOException,
			SpielerExistiertBereitsException, SpielerzahlException {
		// Eine Farbe aussuchen, die der Spieler bekommen soll
		int index = getWelt().getFarben().get(
				(int) (Math.floor(getWelt().getFarben().toArray().length * Math.random())));
		String farbe = getWelt().getHash().get(index);
		// Registrieren
		SpielVerwaltung.getInstance().register(name, farbe);
		gui.updateGUI();
		// Clients über die Änderung informieren
		this.informClients(ChangeConst.SPIELERLOGIN);
	}

	/** Suchen und Zurückgeben eines Spielers aufgrund des Namens */
	public Spieler searchSpieler(String name) {
		return SpielVerwaltung.getInstance().searchSpieler(name);
	}

	/** Löschen eines Spielers aus der Liste der Teilnehmer */
	public void disposeSpieler(Spieler s) {
		// Wenn der abgemeldete Spieler der Spielleiter gewesen ist...
		if (s.isGameMaster()) {
			// wähle per Zufall einen neuen aus, der seinen Platz einnimmt. Dazu:
			Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
			while (iter.hasNext()) {
	        	// Wähle den nächsten Client aus
				Entry<Spieler, ClientMethods> pair = iter.next();
				// Wenn es nicht der Client mit dem sich abmeldenden Spieler ist, mach ihn zum Spielleiter
				if (pair.getValue().isLoggedIn() && !pair.getKey().equals(s)) {
	        		SpielerVerwaltung.searchSpieler(pair.getKey().getName()).setGameMaster(true);
	        		pair.getKey().setGameMaster(true);
	        		pair.getValue().setGameMaster(true);
	        		break;
	        	}
	        }
		}
		// Zuletzt entferne den alten Spieler
		SpielVerwaltung.getInstance().disposeSpieler(s);
		// Informiere alle Clients
		this.informClients(ChangeConst.SPIELERLOGOUT);
	}
	
	/** Gibt die Client-Referenz eines übergebenen Spielers zurück.
	 * @param s	Spieler-Objekt
	 * @return	Client zum Spieler oder null bei Nichtfund */
	private ClientMethods getClient(Spieler s) {
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
                Entry<Spieler, ClientMethods> pair = iter.next();
                if (pair.getKey().getName().equals(s.getName()))
                	return pair.getValue();
        }
        return null;
	}

	/** Meldet einen übergebenen Client wieder ab. */
	public void unregister(Spieler sp) { System.exit(1); }

	/** Rückgabe der Zahl eingeloggter Spieler für die aktuelle Partie. */
	public int getLoggedInSpieler() { return SpielerVerwaltung.countSpieler(); }
	
	/** Starten der Partie. Diese Methode verteilt die States zu Beginn und ist somit dafür zuständig,
	 * dass der erste Spieler seinen Zug beginnen kann. */
	public void startGame() {
		// Spielstart einleiten. Dazu:
		try {
			// Boolean setzen (jetzt kann kein Client mehr dazukommen!)
			spielGestartet = true;
			// Normale StartePartie-Methode aufrufen
			SpielVerwaltung.getInstance().startePartie();
			// Danach alle Clients auf "WAIT_FOR_TURN" ändern
			Spieler ersterSpieler = SpielerVerwaltung.getAkteur();
			Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
	        while (iter.hasNext()) {
	        	// Alle Clients ziehen
	            Entry<Spieler, ClientMethods> spieler = iter.next();
	            // Wenn der Spieler der erste ist, der dran ist, wird sein State auf "Du bist dran" geändert. Sonst auf "Warten"
	            if (ersterSpieler.equals(spieler.getKey()))
	              	spieler.getKey().setState(SpielerState.ON_TURN);
	            else
                	spieler.getKey().setState(SpielerState.WAIT_FOR_TURN);
	        }
	        // Clients informieren, dass sie die Map aktualisieren sollen
	        this.informClients(ChangeConst.MAPUPDATE);
		} catch (SpielerzahlException e) {
			// Nichts tun. Tritt nicht auf, weil ein Game Master ohne passende Anzahl Spieler gar nicht auf den Button drücken kann.
		}
	}
	
	/** Rückgabe des Spielers, der aktuell am Zug ist.
	 * @return Der Spieler, der an der Reihe ist */
	public Spieler getAkteur() { return SpielVerwaltung.getInstance().getAkteur(); }
	
	/** Rückgabe eines Landes aufgrund der FarbMap. Diese Methode wird von einem Client gebraucht, wenn
	 * der Spieler auf ein Land auf der Weltkarte klickt. Über die Farbkodierung des Landes
	 * sucht diese Methode die passende Land-Referenz heraus.
	 * @param farbMapVal	Integer-Farbe, deren zugehöriges Land gesucht wird
	 * @return	Landreferenz mit passender FarbMap */
	public Land getLandByFarbMap(int farbMapVal) { return SpielVerwaltung.getInstance().getLandByFarbMap(farbMapVal); }
	
	/** Berechnet die neuen Armeen, die dem anfragenden Spieler zum Verteilen zustehen.
	 * Sie wird zum Beispiel zu Beginn eines Zuges aufgerufen, oder dann, wenn der Spieler
	 * einen erfolgreichen Kartentausch durchgeführt hat. */
	public int getNewArmeen() { return SpielVerwaltung.getInstance().getNewArmeen(); }
	
	/** Diese Methode verstärkt die Armeen auf dem übergebenen Land um den Wert 1.
	 * @param 	l	Zu verstärkendes Land
	 * @throws SpielerNichtBesitzerException	...wenn Spieler nicht der Besitzer des Landes ist */
	public void verstaerkeArmeen(Land l) throws SpielerNichtBesitzerException { SpielVerwaltung.getInstance().verstaerkeArmeen(l); }
	
	/** Diese Methode gibt alle Länder in einem Vektor zurück, ohne die Aufteilung der Länder in Kontinente
	 * zu berücksichtigen. In der Rückgabeliste stehen also quasi alle Länder unsortiert drin.
	 * @return	Vector mit allen Land-Referenzen */
	public Vector<Land> getAlleLaender() { return SpielVerwaltung.getInstance().getAlleLaender(); }
	
	/** Kopieren einer Spieler-Referenz auf den Server. Wird an manchen Stellen vom Client gebraucht. */
	public void informServer(Spieler s) { Spieler.copy(s, SpielVerwaltung.getInstance().searchSpieler(s.getName())); }
	
	/** Einleiten des Kartentausch-Vorgangs.
	 * @return true, wenn der Spieler drei Karten eintauschen kann, false sonst */
	public boolean kartentauschInit() { return SpielVerwaltung.getInstance().kartentauschInit(); }
	
	/** Kartentausch-Vorgang durchführen.
	 * @return Result-Objekt mit mehreren Parametern, wie "Anzahl neuer Armeen"  */
	public Result kartentausch() { return SpielVerwaltung.getInstance().kartentausch(); }
	
	/** Einleiten des Angriffs-Vorgangs. Diese Methode prüft für die Client-GUI, ob ein Angriff überhaupt
	 * durchgeführt werden kann. Sie ermittelt die aktuell durchführbaren Angriffe
	 * @param s	Spieler-Objekt, das geprüft werden soll
	 * @return Anzahl möglicher Angriffe, die der Spieler zur Zeit durchführen kann */
	public int angriffInit(Spieler s) { return SpielVerwaltung.getInstance().angriffInit(s); }
	
	/** Angriff durchführen.
	 * @param 	a	Angriffsobjekt, das die Kampfteilnehmer und die beteiligten Länder beinhaltet. */
	public void attack(Angriff a) {
		// Land-Objekte von Client in Land-Objekte auf dem Server "parsen"
		// Client-Referenzen und der aktuelle Angriff in Attribute gespeichert
		try {
			curAngriff = new Angriff(LandVerwaltung.searchLand(a.getVon().getName()),
					LandVerwaltung.searchLand(a.getNach().getName()), a.getAngrStr(), 0);
		} catch (KeinLandException e) {	}
		SpielVerwaltung.getInstance().setAngriff(curAngriff);
		spielerAttacker = a.getAngreifer();
		spielerDefender = a.getVerteidiger();
		curAttacker = this.getClient(spielerAttacker);
		curDefender = this.getClient(spielerDefender);
		// Der Verteidiger wird darüber informiert, dass er angegriffen worden ist und sich verteidigen muss
		Result res = new Result();
		res.push(ChangeConst.DEFENDYOURSELF);
		res.push(a);
		curDefender.aktualisiere(spielerDefender,res);
	}
	
	/** Verteidigen-Methode. Diese wird vom Verteidigenden eines Angriffs aufgerufen, und beinhaltet
	 * das tatsächliche Einleiten der Kampfberechnung, nachdem die "Verteidigungsstärke" übermittelt wurde.
	 * @param d	Anzahl der verteidigenden Armeen */
	public void defend(int d) {
		// Die übermittelte Verteidigungsstärke wird in das aktuelle Angriffsobjekt abgespeichert
		curAngriff.setVertStr(d);
		// Jetzt kann der Angriff durchgeführt werden.
		// Würfeln
		int[] erg_angr = SpielVerwaltung.getInstance().wuerfeln(curAngriff.getAngrStr());
		int[] erg_vert = SpielVerwaltung.getInstance().wuerfeln(curAngriff.getVertStr());

		// Angriff durchführen
		Result erg = null;
		try {
			erg = SpielVerwaltung.getInstance().angriffAufLand(erg_angr, erg_vert);
		} catch (SpielBeendetException e) {
			System.out.println("SpielBeendetException in ServerEngineNetwork/defend");
		}

		// Result auspacken
		boolean erfolgreich = (Boolean) erg.pop();
		int rest = (Integer) erg.pop();
		Karte karte = (Karte) erg.pop();
		
		// Result bauen
		Result res1 = new Result();
		res1.push(ChangeConst.KAMPFERGEBNIS);	// 0: Die Konstante (wie immer)
		res1.push(curAngriff);					// 1: Angriffs-Objekt für Spieler- und Landzuordnung
		res1.push(erfolgreich);					// 2: Angriff erfolgreich = true; false sonst
		res1.push(rest);						// 3: Resteinheiten
		res1.push(karte);						// 4: u.U. gewonnene Karte
				
		// Result bauen
		Result res2 = new Result();
		res2.push(ChangeConst.KAMPFNOTICE);		// 0: Die Konstante (wie immer)
		res2.push(curAngriff);					// 1: Angriffs-Objekt für Spieler- und Landzuordnung
		res2.push(erfolgreich);					// 2: Angriff erfolgreich = true; false sonst
		res2.push(rest);						// 3: Resteinheiten
		res2.push(karte);						// 4: u.U. gewonnene Karte
		
		// Besitz-Listen aktualisieren
		LandVerwaltung.updateBesitz(spielerAttacker);
		LandVerwaltung.updateBesitz(spielerDefender);
		
		System.out.println("Angriff fertig, Teilnehmer aktualisieren lassen");
		// Kampfteilnehmer aktualisieren lassen
		System.out.println("Verteidiger aktualisieren lassen");
		curDefender.aktualisiere(spielerDefender, res1);
		System.out.println("Angreifer aktualisieren lassen");
		curAttacker.aktualisiere(spielerAttacker, res1);
		
		System.out.println("Alle anderen aktualisieren lassen");
		// Zuletzt alle anderen Clients über die Map-Änderung informieren
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
                Entry<Spieler, ClientMethods> spieler = iter.next();
                if (!spieler.getKey().equals(spielerAttacker) &&
                	!spieler.getKey().equals(spielerDefender))
                			spieler.getValue().aktualisiere(spieler.getKey(), res2);
        }
		
		// Angriff ist abgeschlossen; Attribute zurücksetzen
		curAngriff = null;
		curAttacker = null;
		curDefender = null;
		spielerAttacker = null;
		spielerDefender = null;
	}

	/** Lässt Armeen nach einem Angriff auf das Zielland nachrücken 
	 * @param zahl Anzahl nachgezogener Armeen */
	public void nachziehen(int zahl) {
		SpielVerwaltung.getInstance().nachziehen(zahl);
		// Clients über veränderte Stärken informieren
		this.informClients(ChangeConst.MAPUPDATE);
	}
	
	/** Methode zum Prüfen auf Verlust eines Spielers
	 * @param 	s	Spieler-Objekt, das geprüft werden soll
	 * @return true, wenn der Spieler keine Länder mehr hat, false sonst */
	public boolean hasLost(Spieler s) { return SpielVerwaltung.getInstance().hasLost(s); }

	/** Diese Methode prüft, ob das Spiel beendet ist, also nur noch ein Spieler übrig ist, der noch Länder hat.
	 * @return true, wenn dies zutrifft, false sonst */
	public boolean einSpielerUebrig() { return SpielVerwaltung.getInstance().einSpielerUebrig(); }

	/** Diese Methode gibt den ersten Spieler im Spieler-Array zurück, der noch "da" ist. Damit
	 * kann nach der Prüfung "einSpielerUebrig()" der Partiesieger ermittelt werden.
	 * @return Spielsieger */
	public Spieler grabFirstSpieler() { return SpielVerwaltung.getInstance().grabFirstSpieler(); }	
	
	/** Diese Methode setzt den Akteur-Status weiter, damit nach dem Beenden des Zuges eines Spielers
	 * der nächste seine Aktionen tätigen kann. Sie informiert alle Clients abschließend darüber,
	 * wer nun dran ist (bzw den neuen Akteur darüber, DASS er dran ist). */
	public void nextSpieler() {
		// Nächsten Spieler ansprechen!
		// Dazu: Erstmal den Index weitersetzen (analog zur alten Methode)
		SpielVerwaltung.getInstance().nextSpieler();
		// Anschließend den neuen Akteur ziehen
		Spieler neuerAkteur = SpielVerwaltung.getInstance().getAkteur();
		// ...und die Clients informieren!
		// Dabei wird auch noch der State geändert. Der neue Akteur wird auf On TURN gesetzt, der Rest auf WAIT
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
        	// Alle Clients ziehen
            Entry<Spieler, ClientMethods> spieler = iter.next();
            // Wenn der Spieler der erste ist, der dran ist, wird sein State auf "Du bist dran" geändert. Sonst auf "Warten"
            if (neuerAkteur.equals(spieler.getKey()))
              	spieler.getKey().setState(SpielerState.ON_TURN);
            else
            	spieler.getKey().setState(SpielerState.WAIT_FOR_TURN);
        }
        // Clients informieren, dass sie die Map aktualisieren sollen und ein neuer Zug begonnen hat
        informClients(ChangeConst.NEWTURN);
	}
	
	/** Nachricht weitersenden. Clients informieren, dass es eine neue Nachricht gibt, und diese an alle rausschicken */
	public void sendChatMessage(ChatMessage nachricht) {
		// Erg-Objekt erstellen und Chatmsg-Konstante und Nachricht, die angekommen ist, reinpacken
		Result r = new Result();
		r.push(ChangeConst.CHATMSG);
		r.push(nachricht);
		// Alle Clients nacheinander informieren
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Spieler, ClientMethods> spieler = iter.next();
            spieler.getValue().aktualisiere(spieler.getKey(), r);
        }
	}
	
	/** Einleiten des Armeen-bewegen-Vorgangs. Diese Methode prüft, wie viele Armeen-bewegen-Vorgänge
	 * für den übergebenen Spieler zur Zeit möglich sind.
	 * @param s	Spieler, für den die Zahl möglicher Bewegungen errechnet werden soll
	 * @return Zahl der möglichen Bewegungen */
	public int armeenbewegenInit(Spieler s) { return SpielVerwaltung.getInstance().armeenbewegenInit(s); }
	
	/** Armeen-bewegen-Vorgang von einem zum anderen Land mit übergebener Armeenzahl.
	 * @param von	Land, von dem Armeen genommen werden
	 * @param nach	Land, das neue Armeen erhält
	 * @param zahl	Anzahl der Armeen, die verschoben werden sollen */
	public void armeenbewegen(Land von, Land nach, int zahl) {
		SpielVerwaltung.getInstance().armeenbewegen(von,nach,zahl);
		// Clients über Änderungen informieren
		this.informClients(ChangeConst.MAPUPDATE);
	}
	
	/** Erzwingen eines Updates für alle Clients von Client-Seite aus. Wird bspw dann benutzt,
	 * um die Verstärkungs-Vorgänge eines Spielers am Ende an alle weiterzugeben.
	 * @param s		Spieler, von dem das Force-Update ausgeht
	 * @param mode	Art des Update-Vorgangs 
	 * @see ChangeConst */
	public void forceUpdate(Spieler s, int mode) {
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Spieler, ClientMethods> spieler = iter.next();
             if (spieler.getKey().getName().equals(s.getName())) {
            	SpielerVerwaltung.searchSpieler(s.getName()).setState(s.getState());
            	spieler.getKey().setState(s.getState());
            }
        }
        this.informClients(mode);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	/** Lademethode für eine risksav-Datei, die einen Spielstand angibt.
	 * Eine gespeicherte Partie kann zu einem späteren Zeitpunkt vom Spielleiter
	 * wieder geladen werden, sofern alle Teilnehmer wieder eingeloggt sind.
	 * @param  file							Datei, die geladen werden soll
	 * @throws FileNotFoundException		wenn die Datei nicht gefunden werden kann
	 * @throws ClassNotFoundException		wenn die Datei unerwünschte Klassen oder Formate enthält
	 * @throws IOException					bei Lesefehlern
	 * @throws IncompatibleWeltException	wenn Spieleranzahl oder Spielernamen der registrierten nicht mit den gespeicherten Daten übereinstimmt */
	public void load(File file) throws FileNotFoundException, 
			ClassNotFoundException, IOException, IncompatibleWeltException {
		Welt welt = SpielVerwaltung.getInstance().getWelt();
		// Alte Welt "sichern" über Copy-Methode der Welt-Klasse
		Welt alteWelt = welt.copyWelt();
		// Dann den ganz normalen Ladevorgang durchführen
		SpielVerwaltung.getInstance().load(file);
		// Anschließend aktuell eingetragene Spieler im Welt-Objekt mit registrierten Clients abgleichen
		// 1. Stimmt die Anzahl der in der Datei gespeicherten Spieler mit den Clients überein?
		Spieler[] spieler = SpielVerwaltung.getInstance().getWelt().getSpieler();		
		if (SpielerVerwaltung.countSpieler() == this.clients.size()) {
			// 2. Sind die tatsächlich registrierten Clients auch hinter den Spielern?
			Set<Spieler> set = this.clients.keySet();
			for (int j = 0; j < spieler.length; j++) {
				if (spieler[j] != null) {
					// Wenn da ein Spieler ist, den es nicht in den Clients gibt, rausschmeißen
					// Dazu: KeySet durchsuchen
					boolean found = false;
					Iterator<Spieler> iter = set.iterator();
					while(iter.hasNext()) {
						// ... wenn der Spieler gefunden wurde, wird das im found-Flag markiert und
						// die Schleife im Anschluss abgebrochen.
						Spieler next = iter.next();
						if (next.getName().equals(spieler[j].getName())) {
							found = true;
							break;
						}
					}
					// Wenn es keinen Fund für den angemeldeten Spieler gab, wird der Ladevorgang
					// sofort abgebrochen.
					if (!found) {
						welt.setProperties(alteWelt);
						this.copyClientsToSpieler();
						return;
					}
				}
			}
			// Ergebnis-Objekt erstellen, das an alle Clients geschickt werden soll
			Result mapupdate = new Result();
			mapupdate.push(ChangeConst.MAPUPDATE);
			// Eigenschaften der gespeicherten auf die eingeloggten Objekte übertragen
			for (int i = 0; i < spieler.length; i++) {
				Spieler sp = spieler[i];
				if (sp != null) {
					// "Vergleichs-Spieler" holen, Client-Zugehörigkeiten ermitteln
					Spieler compare = this.getClientSpielerFromName(sp);
					if (compare == null) {
						welt.setProperties(alteWelt);
						this.copyClientsToSpieler();
						return;
					}
					ClientMethods client = this.getClient(compare);
					// Wenn es den Client gibt, muss diesem das "neue" Spielerobjekt mitgeteilt werden
					// Die Eigenschaften vom compare-Objekt müssen an vielen Stellen übernommen werden. Dies geschieht hier:
					// Farben-Vektor erzeugen, der den Objekten mitgeteilt wird
					Vector farben = new Vector();
					farben.add((Integer) compare.getFarbe());
					farben.add((String) compare.getFarbtext());
					// Welt-Spieler updaten
					SpielerVerwaltung.searchSpieler(compare.getName()).setState(compare.getState());
					client.getSpieler().setState(compare.getState());
					client.getSpieler().setFarben(farben);
					client.getSpieler().setGameMaster(compare.isGameMaster());
					client.getSpieler().setMission(compare.getMission());
					client.getSpieler().setBesitz((compare.getBesitz()));
					// Client-Spieler updaten
					Iterator<Spieler> iterS = clients.keySet().iterator();
					while(iterS.hasNext()) {
						Spieler next = iterS.next();
						if (next.equals(compare)) {
							next.setFarben(farben);
							next.setMission(compare.getMission());
							next.setGameMaster(compare.isGameMaster());
							next.setState(compare.getState());
							next.setBesitz((compare.getBesitz()));
							break;
						}
					}
					// Zuletzt den Client aktualisieren!
					client.aktualisiere(compare, mapupdate);
				}
			}
		} else {
			welt.setProperties(alteWelt);
			this.copyClientsToSpieler();
			return;
		}
	}
	
	/** Diese Methode wird von den Persistenz-Methoden der Serverschnittstelle benutzt, wenn es
	 * beim Laden einer gespeicherten Welt zu einem Fehler kam. Sie kopiert in diesem Fall alle
	 * Spieler aus Client-Referenzen zurück in die Welt, nachdem alle reingeladenen Spieler
	 * gelöscht wurden. */
	private void copyClientsToSpieler() throws IncompatibleWeltException {
		// Bei einem Fehler alle Spieler löschen
		Spieler[] spieler = SpielVerwaltung.getInstance().getWelt().getSpieler();
		spieler = new Spieler[6];
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        for (int i = 0; i < this.clients.size(); i++) {
            Entry<Spieler, ClientMethods> client = iter.next();
            spieler[i] = client.getKey();
        }
		throw new IncompatibleWeltException();
	}
	
	/** Ermitteln des Spieler-Objektes, das zu einem Client gehört, mittels eines anderen Spieler-Objekts */
	private Spieler getClientSpielerFromName(Spieler other) {
		Iterator<Entry<Spieler, ClientMethods>> iter = this.clients.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Spieler, ClientMethods> spieler = iter.next();
            if (spieler.getKey().getName().equals(other.getName()))
            	return other;
        }
        return null;
	}

	/** Abspeichern des Spielstands in einer externen Datei.
	 * @param file	Die Datei, in die gespeichert werden soll
	 * @throws FileNotFoundException	wenn die Datei nicht gefunden werden kann
	 * @throws IOException				wenn es z.B. Schreibfehler beim Speichern gibt */
	public void save(File file) throws FileNotFoundException, IOException {
		// Der Speichervorgang funktioniert analog zum CUI-Speichern.
		// Einfach weiterleiten an die SpielVerwaltung
		SpielVerwaltung.getInstance().save(file);
	}

	/** Methode aus SimonUnreferenced */
	public void unreferenced() { System.out.println("Unreferenced SERVER"); }

	/** Prüft die Mission eines übergebenen Spielers auf Erfülltheit
	 * @return erfüllt oder nicht erfüllt */
	public boolean pruefeMission(Spieler s) {
		boolean erfuellt = SpielerVerwaltung.searchSpieler(s.getName()).getMission().pruefeSieg();
		if (erfuellt) {
			System.out.println(s+"s Mission ist erfüllt! Mission: "+s.getMission());
			Result res = new Result();
			res.push(ChangeConst.GAMEFINISHED);
			res.push(s);
			res.push(SpielerState.LOGGED_IN);
			// Spielgestartet-Zustand zurück auf false setzen
			spielGestartet = false;
			// Clients informieren
			this.informClients(res);
		}
		return erfuellt;
	}

	/** Prüft die Missionen aller Spieler auf Erfülltheit
	 * @return Spieler-Objekt, dessen Mission erfüllt wurde, oder null */
	public Spieler pruefeAlleMissionen() {
		Spieler[] alle = SpielVerwaltung.getInstance().getWelt().getSpieler();
		for (int i = 0; i < alle.length; i++) {
			if (alle[i] != null) {
				this.pruefeMission(alle[i]);
				return alle[i];
			}
		}
		return null;
	}

	/** Ist das Spiel gestartet? */
	public boolean isSpielGestartet() { return spielGestartet; }
}
