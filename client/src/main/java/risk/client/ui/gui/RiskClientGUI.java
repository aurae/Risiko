package risk.client.ui.gui;

import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import risk.commons.RiskUtils;
import risk.client.net.ClientEngineNetwork;
import risk.client.ui.gui.comp.MessageDialog;
import risk.client.ui.gui.panels.DefaultPanel;
import risk.client.ui.gui.panels.ImagePanel;
import risk.client.ui.gui.panels.LoginPanel;
import risk.commons.exceptions.FalscherModeException;
import risk.commons.exceptions.IncompatibleWeltException;
import risk.commons.interfaces.ClientMethods;
import risk.commons.interfaces.ServerMethods;
import risk.commons.valueobjects.Spieler;
import de.root1.simon.annotation.SimonRemote;

/**
 * Das Graphische User Interface eines Clients wird in dieser Klasse realisiert. Die sichtbaren
 * Informationen auf dem Bildschirm werden über ein Attribut gewechselt.
 * @author Marcel
 */
//Klasse markieren
@SimonRemote(value = {ClientMethods.class}) 
public class RiskClientGUI extends JFrame {
	private static final long serialVersionUID = -5427471142201034999L;

	/** Fenstergröße */
	public static final Dimension windowsize = new Dimension(1024,768);
	
	// Attribute
	/** Serveranbindungs-Objekt, das Anfragen der GUI an den Server weiterleitet */
	private static ServerMethods risk;
	/** Statische Selbstreferenz, damit andere Klassen auf GUI-Elemente zugreifen dürfen */
	private static RiskClientGUI instance;
	/** Spieler-Objekt, das mit diesem Client verbunden ist */
	private static Spieler clientSpieler;
	/** Datei, in die automatisch gespeichert werden soll. Nach dem ersten Speichervorgang wird die Referenz gesetzt */
	private static File savegameFile = null;
	
	/** Fenstertiteltext */
	public static final String titel = "RISK CLIENT GUI by SAMMANN, KODZIC, SCHNELLE";

	/** Sichtbares Panel, das aktuell relevante Informationen enthält */
	private DefaultPanel currentFrame;
	/** Menüleiste */
	private MenuBar menubar;
	/** Menü "Spiel" */
	private Menu gameMenu;
	/** Menüpunkt zum Laden einer Partie */
	private static MenuItem loadGame;
	/** Menüpunkt zum Speichern einer Partie */
	private static MenuItem saveGame;
	/** Checkbox für automatisches Speichern */
	private static CheckboxMenuItem autoSave;
	/** Menüpunkt zum Verlassen einer Partie */
	private MenuItem quitGame;
	
	/** Host */
	private String host = "";

	/** Konstruktor */
	public RiskClientGUI() {
		RiskClientGUI.instance = this;
	}

	/** Beginn des GUI-Initialisierungsvorgangs. Hier werden zunächst alle grundlegenden
	 * Darstellungsparameter, Objekte etc. definiert, bevor das Login-Panel angezeigt wird
	 * und die Spiellogik langsam zu laufen beginnt.
	 * @param args	Programmstartparameter für einen externen Host (Default: localhost) */
	public void run(String[] args) throws IOException {
		// Host ziehen, wenn da einer ist
		if (args.length == 1)
			host = args[0];
		
		// Generelle Einstellungen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle(titel);
        
		// Tooltip-Zeiten setzen
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(10);
		
		// Icon setzen
		RiskUtils.setIconImage(this, "images/riskicon_client.png");

		// Menü zusammenbauen
		menubar = new MenuBar();
			gameMenu = new Menu("Spiel");
				loadGame = new MenuItem("Partie laden...");
				loadGame.setEnabled(false);
				saveGame = new MenuItem("Partie speichern...");
				saveGame.setEnabled(false);
				autoSave = new CheckboxMenuItem("Automatisches Speichern");
				autoSave.setState(true);
				quitGame = new MenuItem("RISK beenden");
				
			gameMenu.add(loadGame);
			gameMenu.add(saveGame);
			gameMenu.add(new MenuItem("-"));
			gameMenu.add(autoSave);
			gameMenu.add(new MenuItem("-"));
			gameMenu.add(quitGame);
		
		menubar.add(gameMenu);
		this.setMenuBar(menubar);
		// Menülistener erstellen
		this.addMenuListeners();
		
		try {
			// Splash-Screen anzeigen lassen
			ImagePanel ip = new ImagePanel("images/splash.png");
			ip.setLayout(null);
			ip.setMinimumSize(RiskClientGUI.windowsize);
			ip.setMaximumSize(RiskClientGUI.windowsize);
			ip.setPreferredSize(RiskClientGUI.windowsize);
			this.currentFrame = ip;
		} catch (IOException e1) {
			// Wenn das Bild nicht gefunden wurde
			System.out.println("IOException: " + e1.getMessage());
		}
		if (currentFrame != null)
			this.setContentPane(currentFrame);

        this.validate();
        
        // Fensterschließ-Listener hinzufügen
        this.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent arg0) {
        		// Serververbindung noch trennen
        		((ClientEngineNetwork) RiskClientGUI.risk).unregister(clientSpieler);
        	}
        });
        
        this.pack();
        
        // Bildschirmgröße definieren
        Dimension bildschirm = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (bildschirm.width/2) - (this.getWidth()/2);
        int y = (bildschirm.height/2) - (this.getHeight()/2);
        this.setBounds(x, y, this.getWidth(),getHeight());
        
        this.setVisible(true);
        
		// Attribute setzen
		RiskClientGUI.risk = ClientEngineNetwork.getEngine();
		
		// Nach 100ms Splashscreen verlassen und auf das Einstellungs-Panel wechseln
        Timer timer = new Timer(100, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                        if (e.getSource() instanceof Timer) {
                                // Auf Login-Panel wechseln
                        		instance.changePanel(new LoginPanel());
                                ClientEngineNetwork.getEngine().addObserver(RiskClientGUI.this.currentFrame);
                                ((Timer) e.getSource()).stop();
                        }
                }
        });

        timer.start();
	}

	/** Diese Methode fügt die Listener-Methoden für die Menüeinträge hinzu */
	private void addMenuListeners() {
		// Laden-Menüpunkt
		loadGame.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					// Laden-Fenster aufrufen, danach die Laden-Methode aufrufen lassen
					File welt = null;
					try {
						welt = RiskClientGUI.this.chooseFile("LOAD");
						if (welt != null) {
							// Wenn der Benutzer tatsächlich etwas ausgewählt hat, beginne den Ladevorgang
							try {
								ClientEngineNetwork.getEngine().load(welt);
							} catch(IncompatibleWeltException e) {
								// Spielernamen oder -zahl passt nicht
								new MessageDialog(RiskClientGUI.this, e.getMessage(), "Fehler beim Laden");
							} catch(Exception e) {	}
						}
					} catch (FalscherModeException e) {
						new MessageDialog(RiskClientGUI.this, e.getMessage(), "Fehler beim Laden");
					}
				}
		});
		
		// Speichern-Menüpunkt
		saveGame.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent a) {
					// Speichern-Fenster aufrufen, danach die Laden-Methode aufrufen lassen
					File welt = null;
					try {
						welt = RiskClientGUI.this.chooseFile("SAVE");
						if (welt != null) {
							// Wenn der Benutzer tatsächlich etwas ausgewählt hat, beginne den Speichervorgang
							ClientEngineNetwork.getEngine().save(welt);
							// Attribut für Autosave setzen
							savegameFile = welt;
							// Dialogfenster anzeigen
							new MessageDialog(RiskClientGUI.this, "Speichern in "+ welt.getName() +
									" erfolgreich!", "Speichervorgang");
						}
					} catch (Exception e) {
						new MessageDialog(RiskClientGUI.this, e.getMessage(), "Fehler beim Speichern");
					}
				}
		});
		
		// Quit-Menüpunkt
		quitGame.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent a) {
						System.exit(1);
				}
		});	
	}

	/** Aktuell angezeigtes Panel verändern
	 * @param newPanel	Neues aktuelles Panel zur Anzeige */
	public void changePanel(DefaultPanel newPanel) {
		this.currentFrame = newPanel;
		this.setContentPane(currentFrame);
		this.pack();
	}
	
	public static RiskClientGUI getInstance() {
		return RiskClientGUI.instance;
	}
	
	public static Spieler getSpieler() {
		return clientSpieler;
	}
	public static void setSpieler(Spieler s) {
		clientSpieler = s;
	}
	
	public DefaultPanel getCurrentFrame() {
		return currentFrame;
	}
	
	/** Methode zum Auswählen einer Datei zum Laden oder Speichern.
	 * Gültige Modi für das Aufrufen der chooseFile-Methode sind:
	 * 	LOAD	:	Kreiert einen Dialog zum Einlesen einer Speicherdatei
	 * 	SAVE	:	Kreiert einen Dialog zum Speichern eines Spielstandes in einer Speicherdatei
	 * @param mode Konstante, die den Mode angibt  */
	private File chooseFile(String mode) throws FalscherModeException {
		JFileChooser chooser = new JFileChooser(".\\savegames");
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	        "Risiko-Spielstände (.risksav)", "risksav");
	    chooser.setFileFilter(filter);
	    chooser.setAcceptAllFileFilterUsed(false);
	    int returnVal = 0;
	    if (mode.equals("LOAD"))
	    	returnVal = chooser.showOpenDialog(this);
	    else if (mode.equals("SAVE"))
	    	returnVal = chooser.showSaveDialog(this);
	    else throw new FalscherModeException(mode);
	    File sel = null;
	    if(returnVal == JFileChooser.APPROVE_OPTION)
	    	sel = chooser.getSelectedFile();
	    return sel;
	}
	
	/** Rückgabe des Lade-Partie-Menüpunkt-Objektes */
	public static MenuItem getLoadGameMenuItem() { return loadGame; }
	/** Rückgabe des Speichere-Partie-Menüpunkt-Objektes */
	public static MenuItem getSaveGameMenuItem() { return saveGame;	}
	
	/** Host zurückgeben */
	public String getHost() { return host; }
	
	/** Angabe, ob das automatische Speichern aktiviert oder deaktiviert ist */
	public static boolean isAutoSaveOn() { return autoSave.getState(); }
	
	/** Rückgabe der Speicherdatei, wenn sie existiert (für AutoSave wichtig) */
	public static File getSavegameFile() { return savegameFile; }
}
