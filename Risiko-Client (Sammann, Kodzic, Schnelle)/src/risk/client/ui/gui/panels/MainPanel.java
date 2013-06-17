package risk.client.ui.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import risk.client.net.ClientEngineNetwork;
import risk.client.ui.gui.RiskClientGUI;
import risk.client.ui.gui.comp.AngriffsDialog;
import risk.client.ui.gui.comp.ErrorDialog;
import risk.client.ui.gui.comp.MessageDialog;
import risk.client.ui.gui.comp.MoveDialog;
import risk.client.ui.gui.comp.OptionDialog;
import risk.client.ui.gui.comp.RiskDialog;
import risk.commons.ChangeConst;
import risk.commons.SpielerState;
import risk.commons.components.ArmeenButton;
import risk.commons.exceptions.SpielerNichtBesitzerException;
import risk.commons.interfaces.PanelInterface;
import risk.commons.valueobjects.Angriff;
import risk.commons.valueobjects.ChatMessage;
import risk.commons.valueobjects.Karte;
import risk.commons.valueobjects.Kontinent;
import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Result;
import risk.commons.valueobjects.Spieler;

/**
 * Das Panel der GUI, in der sich der Großteil des Spiels abspielt. Beinhaltet Subpanels für Weltkarte, Kartenanzahl, Chat etc.
 * @author Marcel, Yannik, Srdan
 */
public class MainPanel extends DefaultPanel implements PanelInterface, Observer {
	private static final long serialVersionUID = 7136772679026737388L;
	
	/** Referenz auf Server-Verbindungs-Objekt. Abkürzung für ClientEngineNetwork.getEngine() */
	private ClientEngineNetwork risk = ClientEngineNetwork.getEngine();
	
	/** Variable, in der gespeichert wird, wie viele neue Armeen dem Clientspieler aktuell zustehen. */
	private int neueArmeen;
	
	/** Quell-Land eines Befehls */
	private Land source = null;
	/** Ziel-Land eines Befehls */
	private Land dest = null;
	
	/** Speichervorgang bereits durchgeführt (durch autosave)? */
	private boolean alreadySaved = false;
	
	// GUI-Komponenten des Panels
			/** North-Panel */
			private JPanel north;									
				/** North/Farb-Label des akt.Spielers */
				private JLabel	   north_colorlabel;				
				/** North/"Am Zug ist:" */
				private JTextField north_txt_spielerAmZug;			
				/** North/Phase */
				private JTextField north_txt_phase;					
			/** Center-Panel mit Karten-Bild */
			private JLayeredPane center;							
				/** Center-Panel-Text für die Statusbox */
				private JTextField center_statusnachricht;			
			/** Center-Panel mit Mask-Bild */
			private JLayeredPane centermask;						
				/** Alle ArmeenButtons */
				private HashMap<Land, ArmeenButton> armeenButtons;	
			
			/** East */
			private InfoPanel east;									
			/** South */
			private JPanel south;					
				/** West-Teil des South-Panels (Missionsbox) */
				private JScrollPane southwest;			
					/** South/linke Seite/Missionstext für den aktuellen Spieler */
					private JTextArea southwest_txt_mission;		
				/** South/linke Seite/Chatpanel */
				private ChatPanel southeast_chatPanel;				
				/** South/Text für aktuellen Angriff */
				private JTextField south_txt_display;				
				/** South/Button zum Beenden der Angriffsphase */
				private JButton south_buttonAngriffEnde;			
				/** South/Button zum Beenden der Bewegungsphase */
				private JButton south_buttonBewegungsphaseBeenden;	
				
			/** AngriffsDialog-Instanz */
			private RiskDialog ad;			
			/** MessageDialog-Instanz */
			private MessageDialog md;								
	
	/** Konstruktor. Erzeugt das Main-Panel, initialisiert Subpanels und vergibt Listener */
	public MainPanel() {
		super();
		this.setLayout(new BorderLayout());
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds(dim.width / 8, dim.height / 8, 
				RiskClientGUI.windowsize.width, RiskClientGUI.windowsize.height);
			
		this.armeenButtons = new HashMap<Land, ArmeenButton>();
		
		// *================== NORTH ==================*
			// Im North-Panel wird der aktuell tätige Spieler dargestellt, und
			// die aktuelle Phase, wenn der Client-Spieler selbst der Tätige ist.
			north = new JPanel();
			north.setMinimumSize(new Dimension(1024,50));
			north.setMaximumSize(new Dimension(1024,50));
			north.setPreferredSize(new Dimension(1024,50));
			
			Color SCHWARZ = new Color(0x00000);
			
			north.setBounds(0,0,1024,150);
			north.setBorder(new LineBorder(SCHWARZ));
			// Farblabel
			north_colorlabel = new JLabel();
			north_colorlabel.setLocation(8, 6);
			north_colorlabel.setBackground(SCHWARZ);
			north_colorlabel.setFocusable(false);
			north_colorlabel.setBorder(new LineBorder(SCHWARZ));
			dim = new Dimension(12,12);
			north_colorlabel.setSize(dim);
			north_colorlabel.setMinimumSize(dim);
			north_colorlabel.setPreferredSize(dim);
			north_colorlabel.setMaximumSize(dim);
			north_colorlabel.setOpaque(true);
			north_colorlabel.setVisible(false);
			// TextField mit "Aktueller Spieler: "
			north_txt_spielerAmZug = new JTextField("Bitte warten...");
			north_txt_spielerAmZug.setEditable(false);
			north_txt_spielerAmZug.setFocusable(false);
			north_txt_spielerAmZug.setBorder(null);
			north_txt_spielerAmZug.setBackground(null);
			north_txt_spielerAmZug.setBounds(24,2,545,20);
			dim = new Dimension(200,20);
			// TextField mit "Aktuelle Phase: "
			north_txt_phase = new JTextField("");
			north_txt_phase.setEditable(false);
			north_txt_phase.setFocusable(false);
			north_txt_phase.setBorder(null);
			north_txt_phase.setBounds(8,23,1006,20);
			north_txt_phase.setBackground(null);
			north.setLayout(null);
			
			// Hinzufügen
			north.add(north_colorlabel);
			north.add(north_txt_spielerAmZug);
			north.add(north_txt_phase);
		// *==================/NORTH ==================*	
			
		// *================== CENTER==================*	
			// Center ist ein ImagePanel, das eine eigene Komponente in
			// der Risiko-Anwendung ist. Es beinhaltet die Karte als PNG,
			// welche auch angezeigt wird, sowie eine Methode, die die
			// maskierte Karte als BufferedImage zurück gibt. Letzteres
			// wird in der GUI als Attribut gespeichert.
			dim = new Dimension(905,563);
			try {
				center = new ImagePanel("images/map_karte.png");
			} catch(IOException e) {
				center = new JLayeredPane();
				JTextField err = new JTextField("<images/map.png kann "
						+ "nicht dargestellt werden.>");
				err.setEditable(false);
				err.setFocusable(false);
				err.setBorder(null);
				center.add(err);
			}
			center.setLayout(null);
			center.setMinimumSize(dim);
			center.setMaximumSize(dim);
			center.setPreferredSize(dim);
			center.setBorder(new LineBorder(new Color(0x000000)));
			center.setName("CENTER PANEL");
			center_statusnachricht = new JTextField("...");
			center_statusnachricht.setFont(new Font("SansSerif", Font.PLAIN, 9));
			center_statusnachricht.setBounds(4,520,400,40);
			center_statusnachricht.setEditable(false);
			TitledBorder statusborder = BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), "Status", 
					TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION);
			center_statusnachricht.setBorder(statusborder);
			center_statusnachricht.setBackground(null);
			center.add(center_statusnachricht, JLayeredPane.POPUP_LAYER);
			
			try {
				centermask = new ImagePanel("images/map_mask.png");
			} catch(IOException e) {
				centermask = new JLayeredPane();
				JTextField err = new JTextField("<images/mask.png kann "
						+ "nicht dargestellt werden.>");
				err.setEditable(false);
				err.setFocusable(false);
				err.setBorder(null);
				centermask.add(err);
			}
			
			centermask.setLayout(null);
			centermask.setMinimumSize(dim);
			centermask.setMaximumSize(dim);
			centermask.setPreferredSize(dim);
			centermask.setBorder(new LineBorder(new Color(0x000000)));
			// Listener
			centermask.setName("CENTER PANEL MASK");
			
		// *==================/CENTER==================*
			
		// *================== EAST  ==================*	
			east = new InfoPanel();
		// *================== /EAST ==================*		
			
		// *================== SOUTH ==================*	
			// Im South-Panel wird der aktuelle Zustand des Spielers dargestellt.
			// Dazu zählen die Handkarten-Bilder und die aktuelle Mission.
			south = new JPanel();
			dim = new Dimension(1024,110);
			south.setMinimumSize(dim);
			south.setMaximumSize(dim);
			south.setPreferredSize(dim);
			south.setBorder(new LineBorder(SCHWARZ));
			south.setLayout(null);
			// Linkes Panel
				// Missionstextfeld
				southwest_txt_mission = new JTextArea();
				dim = new Dimension(10,10);
				southwest_txt_mission.setMinimumSize(dim);
				southwest_txt_mission.setMaximumSize(dim);
				southwest_txt_mission.setPreferredSize(dim);
				southwest_txt_mission.setEditable(false);
				southwest_txt_mission.setFocusable(false);
				southwest_txt_mission.setBorder(new LineBorder(SCHWARZ));
				southwest_txt_mission.setLineWrap(true);
				southwest_txt_mission.setWrapStyleWord(true);
				southwest_txt_mission.setText("Mission: \n" + RiskClientGUI.getSpieler().getMission().toString());
				// Pane
				southwest = new JScrollPane(southwest_txt_mission);
				southwest.setBounds(8, 8, 250, 93);
				dim = new Dimension(250,93);
				southwest.setMinimumSize(dim);
				southwest.setMaximumSize(dim);
				southwest.setPreferredSize(dim);
				southwest.setBorder(new LineBorder(SCHWARZ));
				
			// Text für Angriffsdarstellung
			south_txt_display = new JTextField("");
			south_txt_display.setEditable(false);
			south_txt_display.setBounds(262, 10, 314, 20);
			south_txt_display.setBackground(null);
			south_txt_display.setBorder(null);
			
			southeast_chatPanel = new ChatPanel();
			southeast_chatPanel.setBounds(677, 10, 336, 99);
			south.add(southeast_chatPanel);
			
			// Hinzufügen
			south.add(southwest);
			south.add(south_txt_display);
		// *================== /SOUTH==================*	
			
		// Panels hinzufügen
			this.add(north, BorderLayout.NORTH);
			this.add(centermask, BorderLayout.CENTER, -20);
			this.add(center, BorderLayout.CENTER, -10);
			this.add(east, BorderLayout.EAST);
			this.add(south, BorderLayout.SOUTH);
			
			south_buttonAngriffEnde = new JButton("Angriffsphase beenden");
			south_buttonAngriffEnde.setEnabled(false);
			south_buttonAngriffEnde.setBounds(262, 37, 204, 29);
			south.add(south_buttonAngriffEnde);
			
			south_buttonBewegungsphaseBeenden = new JButton("Bewegungen beenden");
			south_buttonBewegungsphaseBeenden.setEnabled(false);
			south_buttonBewegungsphaseBeenden.setBounds(262, 72, 204, 29);
			south.add(south_buttonBewegungsphaseBeenden);
		
		// Darstellen
			this.setVisible(true);
			
		// ArmeenButtons erstellen
			armeenButtons = new HashMap<Land, ArmeenButton>();
			// ArmeenButtons drauf verteilen
						// Per Iterator über alle Kontinente gehen, deren Länder erfragen und ihre
						// ArmeenButtons setzen, doppelt verknüpfen und anzeigen
						Iterator<Kontinent> iterK = risk.getWelt().getKontinente().iterator();
						while (iterK.hasNext()) {
							Kontinent k = iterK.next();
							Iterator<Land> iterL = k.getLaender().iterator();
							while(iterL.hasNext()) {
								// Land holen
								Land l = iterL.next();
								// Besitzer holen
								Spieler besitzer = l.getSpieler();
								// Button in der Besitzerfarbe erzeugen und Land mit dem Button verbinden
								ArmeenButton ab = new ArmeenButton(besitzer, l);
								l.setButton(ab);
								// Abspeichern in der HashMap aller Land-Button-Verbindungen
								armeenButtons.put(l,ab);
								// Koordinaten festlegen
								int bx = (int) l.getButtonPos().getX() - 10;
								int by = (int) l.getButtonPos().getY() - 10;
								// Bounds festsetzen und dem Center-Panel hinzufügen
								ab.setBounds(bx, by, ab.getWidth(), ab.getHeight());
								center.add(ab);
							}
						}
			
			// Listener vergeben
				south_buttonAngriffEnde.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent a) {
						RiskClientGUI.getSpieler().setState(SpielerState.ON_TURN_ARMEENBEWEGEN);
						south_buttonAngriffEnde.setEnabled(false);
						updateTexts();
					}
				});
				south_buttonBewegungsphaseBeenden.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent a) {
						RiskClientGUI.getSpieler().setState(SpielerState.WAIT_FOR_TURN);
						south_buttonBewegungsphaseBeenden.setEnabled(false);
						// Der nächste Spieler muss gewählt werden!
						risk.nextSpieler();
						updateTexts();
					}
				});
				this.addMapListener();
				
			// Sich selbst als Observer registrieren
				risk.addObserver(this);
	}
	
	/** Diese Methode fügt dem Center-Panel einen MapListener hinzu, damit die Karte weiß, wenn sie angeklickt worden ist.
	 * Sie leitet das angeklickte Land ggf. direkt an die doActionsWith-Methode weiter, die dann abhängig von der
	 * aktuellen Phase etwas mit dem angeklickten Objekt anfangen kann. */
	private void addMapListener() {		
		// Initialisieren
		MouseAdapter listener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Land ziehen
				Land l = MainPanel.this.getLandFromClick();
				// Wenn es nicht null ist (also da tatsächlich ein Land liegt), wird die Actions-Methode aufgerufen, die alles weitere macht
				if (l != null) {
					MainPanel.this.doActionsWith(l);
				// Wenn es doch null ist, fragen, ob gerade die Kampfphase ist. Wenn ja, source und dest zurücksetzen
				} else if (RiskClientGUI.getSpieler().getState().equals(SpielerState.ON_TURN_KAEMPFEN) ||
						RiskClientGUI.getSpieler().getState().equals(SpielerState.ON_TURN_ARMEENBEWEGEN)) {
					source = null;
					dest = null;
					ArmeenButton.deselectAll(armeenButtons);
					updateTexts();
				}
			}
		};
		
		// Hinzufügen... erst dem Center-Panel...
		center.addMouseListener(listener);
		// ...und dann den Buttons
		Iterator<Map.Entry<Land,ArmeenButton>> iter = this.armeenButtons.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Land,ArmeenButton> land = iter.next();
			// armeenbutton
			land.getValue().addMouseListener(listener);
		}
	}

	/** Wenn auf die Karte (bzw. auf einen ArmeenButton auf der Karte) geklickt wurde,
	 * wird diese Methode aufgerufen, die das Land identifiziert, das an 
	 * dieser Stelle auf der Maske ist.
	 * @param evt	MouseEvent aus der mouseClicked()-Methode; enthält die Koordinaten des Klicks */
	private Land getLandFromClick() {
		// Koordinaten des Klicks von der MousePosition im Center-Panel auslesen (null kann hierbei
		// ausgeschlossen werden, weil diese Methode nur bei Klick im Center-Panel ausgelöst wird).
		Point clickPos = this.center.getMousePosition();
		// Farbe des Pixels im Mask-Bild erfragen: Hex-String
		String clicked_hex = ((ImagePanel) centermask).getMaskColorAt(clickPos);
		// Dezimal-int
		return risk.getLandByFarbMap(Integer.parseInt(clicked_hex,16));
	}

	/** Diese Methode wird immer dann aufgerufen, wenn ein Spieler auf ein Land geklickt hat.
	 * Abhängig vom Zustand, den er währenddessen eingenommen hat, passieren andere Methodenaufrufe.
	 * @param clickedOn	Das Land, mit dem der Spieler interagiert */
	private void doActionsWith(Land clickedOn) {
		// Neuen Zustand des Spieler-Objektes auf Server-Seite bekanntgeben und aktuellen State in Variable speichern
		//risk.informServer(RiskClientGUI.getSpieler());
		String state = RiskClientGUI.getSpieler().getState();
		
		// Armeen verteilen-Modus und Kartentausch-Modus bei erfolgreichem Tausch
		if (state.equals(SpielerState.ON_TURN_ARMEENVERTEILEN) || state.equals(SpielerState.ON_TURN_KARTENZUARMEENGEMACHT)) {
			this.armeenVerteilen(clickedOn);
	
		// Kampfmodus.
		} else if (state.equals(SpielerState.ON_TURN_KAEMPFEN)) {
			this.kaempfen(clickedOn);
		
		// Bewegungsmodus.
		} else if (state.equals(SpielerState.ON_TURN_ARMEENBEWEGEN)) {
			this.armeenBewegen(clickedOn);
			
		}
		
	}
	
	/** Methode, die das Verteilen von Armeen in der ersten Phase jedes Spielzugs übernimmt. Sie benutzt das Attribut neueArmeen, zählt es runter
	 * und verteilt neue Armeen an der richtigen Stelle
	 * @param clickedOn		Das Land-Objekt, das verstärkt werden soll */
	private void armeenVerteilen(Land clickedOn) {
			// Über Engine dieses Land verstärken
			try {
				RiskClientGUI.getSaveGameMenuItem().setEnabled(false);
				neueArmeen--;
				risk.verstaerkeArmeen(clickedOn);
				clickedOn.plusStaerke(1);
				ArmeenButton.update(armeenButtons, clickedOn);
				updateTexts();
			} catch (SpielerNichtBesitzerException e) {
				// Wenn der Spieler auf ein Land klickt, das ihm gar nicht gehört, wird die entstandene Exception hier aufgefangen und ausgegeben.
				new ErrorDialog(RiskClientGUI.getInstance(), e.getMessage(), "Problem beim Verstärken");
			}
	}
	
	/** Methode, die die Bewegungsphase realisiert und Armeen verschieben kann
	 * @param clickedOn		Das Land-Objekt, das angeklickt wurde und am Bewegungsvorgang teilnehmen soll */
	private void armeenBewegen(Land clickedOn) {
		// Wenn source noch "null" ist, hat der Spieler erst 1 Land ausgewählt
		if (source == null) {
			// Source-Land setzen, wenn das Angeklickte dem Spieler gehört, mehr als 1 Armee darauf steht und es diese Runde nicht bewegt worden ist
			if (clickedOn.getSpieler().equals(RiskClientGUI.getSpieler()) && clickedOn.getStaerke() > 1 &&
					!clickedOn.getBewegt()) {
				source = clickedOn;
				// Angeklickten Button markieren
				ArmeenButton.getButtonFromLand(armeenButtons, clickedOn).select(true);
				// ...und alle potentiellen Kandidaten.
				ArmeenButton.selectMovableButtons(armeenButtons, clickedOn, RiskClientGUI.getSpieler());
				this.updateTexts();
			}
		} else {
			// Ansonsten: Destination-Land setzen
			dest = clickedOn;
			// Jetzt nachschauen, ob das Folgende zutrifft:
			// 1. Source und Dest sind benachbart.
			// 2. Source und Dest gehören dem gleichen Spieler
			// 3. Source und Dest sind in diesem Zug nicht bewegt worden
			if (Land.sindBenachbart(source, dest) && dest.getSpieler().equals(RiskClientGUI.getSpieler()) && !dest.getBewegt()) {
				this.updateTexts();
				// Wenn beide Länder gewählt worden sind, prompten ob die Bewegung ausgeführt werden soll
				ad = new MoveDialog(RiskClientGUI.getInstance(), source, dest);
				// OK-Button-Listener
				ad.getButtons()[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
							// Bewegung durchführen
							ad.setModal(false);
							risk.armeenbewegen(source, dest, ad.getInput());
							// ArmeenButtons deselektieren, Source und Dest zurücksetzen und Fenster entfernen
							ArmeenButton.deselectAll(armeenButtons);
							source = null;
							dest = null;
							ad.dispose();
					}
				});
				// Cancel-Button-Listener
				ad.getButtons()[1].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
							ad.setModal(false);
							ad.dispose();
					}
				});
				ad.setVisible(true);
			} else
				dest = null;
		}
	}
	
	/** Methode, die die Kampfmechanismen koordiniert und ggf. den Angriff starten lässt. Sie prüft auch, ob der Angriff überhaupt möglich ist
	 * @param clickedOn		Das Land-Objekt, das am Kampf teilnehmen soll */
	private void kaempfen(Land clickedOn) {
		// Fallunterscheidung. Wenn source noch "null" ist, hat der Spieler erst 1 Land ausgewählt
		if (source == null) {
			// Source-Land setzen, wenn das Angeklickte dem Spieler gehört und mehr als 1 Armee darauf stehen
			if (clickedOn.getSpieler().equals(RiskClientGUI.getSpieler()) && clickedOn.getStaerke() > 1) {
				source = clickedOn;
				// Angeklickten Button markieren...
				ArmeenButton.getButtonFromLand(armeenButtons, clickedOn).select(true);
				// ...und alle potentiellen Kandidaten.
				ArmeenButton.selectNachbarButtons(armeenButtons, clickedOn, RiskClientGUI.getSpieler());
				this.updateTexts();
			}
		} else {
			// Ansonsten Destination-Land setzen
			dest = clickedOn;
			// Jetzt: Nachschauen, ob das Folgende zutrifft:
			// 1. Source und Dest sind benachbart.
			// 2. Dest ist ein Land eines anderen Spielers
			if (Land.sindBenachbart(source, dest) && !dest.getSpieler().equals(RiskClientGUI.getSpieler())) {
				this.updateTexts();
				// Wenn beide Länder gewählt worden sind, prompten ob der Angriff so ok ist
				ad = new AngriffsDialog(RiskClientGUI.getInstance(), source, dest, AngriffsDialog.ANGRIFF);
				// OK-Button-Listener
				ad.getButtons()[0].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
							// Angriff durchführen lassen
							ad.wartenLassen();
							ad.setModal(false);
							risk.attack(new Angriff(source, dest, ad.getInput(), 0));
					}
				});
				// Cancel-Button-Listener
				ad.getButtons()[1].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent ae) {
							ad.setModal(false);
							ad.dispose();
					}
				});
				ad.setVisible(true);
			} else
				dest = null;
		}
	}
	
	/** Methode, die das Eintauschen von Karten gegen neue Armeen koordiniert
	 * @param clickedOn	Angeklicktes Land */
	private void kartentausch() {
		Spieler s = RiskClientGUI.getSpieler();
		boolean trueIstTauschenFalseIstKaempfen = false;
		// Wenn der Spieler gar keine Karten auf der Hand hat, wird SOFORT weitergesprungen.
		// Nur, wenn er mehr als 0 Karten hat, wird der Rest überhaupt erst geprüft.
		if (s.getKarten().size() > 0) {
			// Init-Methode aufrufen, um zu prüfen, ob der Spieler überhaupt tauschen kann!
			boolean tauschIstMoeglich = risk.kartentauschInit();
			// Wenn die Init-Methode mindestens ein gültiges Ergebnis ermitteln konnte...
			if (tauschIstMoeglich) {
				// ...wird zuerst geprüft, ob der Spieler bereits fünf Karten besitzt. Ist das so,
				// MUSS er sie eintauschen.
				if (s.getKarten().size() == 5) {
						if (md != null)
							md.dispose();
						md = new MessageDialog(RiskClientGUI.getInstance(), "Du besitzt fünf Karten. Deshalb "+"" +
							"musst du nun drei von ihnen eintauschen.", "Kartentausch");
						Result rueckgabe = risk.kartentausch();
						// Result-Objekt aufbereiten
						// [0]	int		neue Armeen
						// [1]	Karte	Karte 1, die getauscht wurde
						// [2]	Karte	Karte 2, die getauscht wurde
						// [3]	Karte	Karte 3, die getauscht wurde
						neueArmeen = (Integer) rueckgabe.pop();
						Karte k1 = (Karte) rueckgabe.pop();
						Karte k2 = (Karte) rueckgabe.pop();
						Karte k3 = (Karte) rueckgabe.pop();
						String s1 = k1.getTitel();
						String s2 = k2.getTitel();
						String s3 = k3.getTitel();
						// Feedback
						if (md != null)
							md.dispose();
						center_statusnachricht.setText(s1+", "+s2+" und "+s3+" wurden erfolgreich eingetauscht! Du erhälst "+
							neueArmeen+" neue Armeen.");
						this.setStatusTimer();
						trueIstTauschenFalseIstKaempfen = true;
				// Wenn er weniger als 5 Karten hat, kann er sich das aussuchen.
				} else {
					OptionDialog od = new OptionDialog(RiskClientGUI.getInstance(), "Du kannst drei Karten für neue Armeen eintauschen."+
							" Möchtest du das tun?","Kartentausch");
					// Wenn der Spieler seine Karten eintauschen möchte...
					if (od.input == JOptionPane.YES_OPTION) {
						// ...tun wir das einfach automatisch!
						Result rueckgabe = risk.kartentausch();
						// Result-Objekt aufbereiten
						// [0]	int		neue Armeen
						// [1]	Karte	Karte 1, die getauscht wurde
						// [2]	Karte	Karte 2, die getauscht wurde
						// [3]	Karte	Karte 3, die getauscht wurde
						neueArmeen = (Integer) rueckgabe.pop();
						Karte k1 = (Karte) rueckgabe.pop();
						Karte k2 = (Karte) rueckgabe.pop();
						Karte k3 = (Karte) rueckgabe.pop();
						String s1 = k1.getTitel();
						String s2 = k2.getTitel();
						String s3 = k3.getTitel();
						// Feedback
						if (md != null)
							md.dispose();
						center_statusnachricht.setText(s1+", "+s2+" und "+s3+" wurden erfolgreich eingetauscht! Du erhälst "+
								neueArmeen+" neue Armeen.");
							this.setStatusTimer();
						trueIstTauschenFalseIstKaempfen = true;
					}
				}
			}
		}
		// Zum Schluss wird der State weiter gesetzt.
		if (trueIstTauschenFalseIstKaempfen == true) {
			s.setState(SpielerState.ON_TURN_KARTENZUARMEENGEMACHT);
			// Kartenliste updaten (immerhin sind jetzt Karten weg)
			east.updateKartenliste();
		} else
			s.setState(SpielerState.ON_TURN_KAEMPFEN);
	}

	/** Berechnet die neuen Armeen, die dem Spieler zustehen, am Anfang der Armeen-Verteilen-Phase */
	private void calcNeueArmeen() {
		neueArmeen = risk.getNewArmeen();
	}
	
	/** Methode zum Abwehren eines Angriffs. Wird nach Ausführung des Befehls auf einem
	 * Client auf dem angegriffenen Client aufgerufen, um die Verteidigungsstärke zu prompten
	 * @param pop	Angriffs-Objekt, in dem die Details des Angriffs festgelegt sind */
	private void defend(Angriff pop) {
		ad = new AngriffsDialog(RiskClientGUI.getInstance(), pop, AngriffsDialog.VERTEIDIGUNG);
		// OK-Button-Listener
		ad.getButtons()[0].addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// Verteidigung zum Server schicken
				int zahl = ad.getInput();
				risk.defend(zahl);
				ad.dispose();
			}
		});
		ad.setVisible(true);
	}
	
	/** Methode, die nach Ausgang einer Kampfsituation auf beiden teilnehmenden Clients aufgerufen wird.
	 * In ihr werden Länder umverteilt, Karten gespeichert und Statustexte eingeblendet
	 * @param angr			Der ausgeführte Angriff
	 * @param erfolgreich	Ist der Angreifer erfolgreich gewesen?
	 * @param restEinheiten	Wie viele Einheiten sind auf dem Quell-Land übrig?
	 * @param karte			Ggf. ein Kartenobjekt, das der Angreifer erhält, weil er erfolgreich war */
	private void nachKampf(Angriff angr, boolean erfolgreich,
			int restEinheiten, Karte karte) {
		String msg;
		// 1. Ist der Spieler der Angreifer gewesen?
		if (RiskClientGUI.getSpieler().equals(angr.getAngreifer())) {
			ad.setModal(false);
			ad.setVisible(false);
			// Spieler war der Angreifer
			if (erfolgreich) {
				// Angreifer war erfolgreich
				msg = "Du hast erfolgreich angegriffen! " + restEinheiten + " Armeen ziehen nach "+angr.getNach()+"!";
				RiskClientGUI.getSpieler().getBesitz().add(angr.getNach());
				if (restEinheiten > 0) {
					if (angr.getVon().getStaerke() > 1) {
						// Wenn da noch Armeen nachgezogen werden könnten, prompten
						// TODO Nachziehen
						final MoveDialog dialog = new MoveDialog(RiskClientGUI.getInstance(), angr.getVon(), angr.getNach(), MoveDialog.NACHZIEHEN);
						// OK-Button-Listener
						dialog.getButtons()[0].addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) {
								// Armeen nachrücken lassen
								risk.nachziehen(dialog.getInput());
								dialog.dispose();
							}
						});
						// Cancel-Button-Listener
						dialog.getButtons()[1].addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent ae) { dialog.dispose(); } });
						dialog.setVisible(true);
					}
				}
			} else {
				// Angreifer war nicht erfolgreich
				msg = angr.getVerteidiger() + " hat den Angriff überstanden! " + restEinheiten + " Armeen ziehen "+
						"zurück nach " + angr.getVon() + ".";
			}
		} else {
			// Spieler war der Verteidiger
			if (erfolgreich) {
				// Verteidiger war nicht erfolgreich
				msg = "Du hast den Angriff nicht überstanden. " + angr.getNach() + " wurde von "+angr.getAngreifer()+ " besetzt.";
				RiskClientGUI.getSpieler().getBesitz().remove(angr.getNach());
			} else {
				// Verteidiger war erfolgreich
				msg = "Du hast den Angriff überstanden! " + restEinheiten + " Armeen ziehen "+
						"zurück nach " + angr.getVon() + ".";
			}
		}
		center_statusnachricht.setText(msg);
		this.setStatusTimer();
		
		// Ist da eine Karte gekommen? Wenn ja, eintragen, InfoPanel bescheid geben!
		if (karte != null && RiskClientGUI.getSpieler().equals(angr.getAngreifer())) {
			RiskClientGUI.getSpieler().addKarte(karte);
			east.updateKartenliste();
		}
		
		// Alle Missionen prüfen lassen
		risk.pruefeAlleMissionen();
		
		// East-Panel-Update für die Landzahlen
		east.updateInfos();
	}

	/** Setzen eines Timers für das Status-Fenster. Wird z.B. von der NachKampf-Methode benutzt. Sie ist dafür zuständig, dass
	 * nach einem festgelegten Zeitintervall der Text in der Statusbox wieder zurückgesetzt wird. */
	private void setStatusTimer() {
		// Timer starten, nach dessen Ablauf (5s) der Text des Status' zurückgesetzt wird
				Timer timer = new Timer(5000, new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                    if (e.getSource() instanceof Timer) {
		                    	center_statusnachricht.setText("...");
		                    	((Timer)e.getSource()).stop();
		                    }
		            }
				});
				timer.start();
	}
	
	
	/** Mit dieser Methode werden alle ArmeenButton-Objekte auf der Weltkarte "auf den aktuellen Stand" gebracht.
	 * Sie wird von der Update-Methode aufgerufen. */
	private void updateArmeenButtons() {
		// Entry-Set aller Land-ArmeenButton-Paare iterieren
		Iterator<Map.Entry<Land,ArmeenButton>> iter = this.armeenButtons.entrySet().iterator();
		// Landliste einmal ziehen
		Vector<Land> alleLaender = risk.getAlleLaender();
		// Aktuell gesuchtes Land
		Land l;
		while (iter.hasNext()) {
			// Nächstes ArmeenButton-Paar
			Map.Entry<Land,ArmeenButton> paar = iter.next();
			// Land auf passenden Eintrag in der Landliste setzen...
			l = alleLaender.get(alleLaender.indexOf(paar.getKey()));
			//...und den passenden ArmeenButton mit den neuen Infos füttern!
			paar.getValue().updateInfos(l.getSpieler(), l);
		}
		this.validate();
		this.repaint();
	}

	/** Texte updaten, die im Main Panel angezeigt werden.
	 * Diese Funktion wechselt auch zwischen einigen Spieler-States */
	private void updateTexts() {
		Spieler s = RiskClientGUI.getSpieler();
		String state = s.getState();
		
		// Wenn der Spieler gerade dran ist...
		if (state.contains(SpielerState.ON_TURN)) {
				// Speichern-Menüounkt aktivieren, wenn der Spieler der Spielleiter ist
				if (s.isGameMaster()) {
					RiskClientGUI.getSaveGameMenuItem().setEnabled(true);
					// Automatisches Speichern durchführen, wenn verschiedene Bedingungen erfüllt sind:
					// 1. Er hat die Partie schon mal gespeichert (RiskClientGUI.getSavegameFile() != null)
					// 2. Die Checkbox für Automatisches Speichern ist an
					// 3. Er hat noch nicht gespeichert (autoSave nur einmal pro Zug am Anfang des Zuges)
					if (!alreadySaved && RiskClientGUI.getSavegameFile() != null && RiskClientGUI.isAutoSaveOn()) {
						try {
							risk.save(RiskClientGUI.getSavegameFile());
							center_statusnachricht.setText("Automatisches Speichern in "+RiskClientGUI.getSavegameFile().getName() + " erfolgreich...");
							this.setStatusTimer();
							alreadySaved = true;
						} catch (Exception e) {
							new MessageDialog(RiskClientGUI.getInstance(), e.getMessage(), "Fehler beim Speichern");
						}
					}
				}
				
				// ...seine Farbe anzeigen und "Du bist dran!"
				north_colorlabel.setBackground(new Color(s.getFarbe()));
				north_colorlabel.setVisible(true);
				north_txt_spielerAmZug.setText("Du bist dran, " + s + "!");
				
				// Wenn der State ausschließlich "ON_TURN" ist, dann hat sein Zug gerade erst begonnen. Dann Status weiterleiten
				if (state.equals(SpielerState.ON_TURN)) {
					this.calcNeueArmeen();
					s.setState(SpielerState.ON_TURN_ARMEENVERTEILEN);
					state = s.getState();
				}
				
				/*
				 * ====================================================
				 * ARMEEN VERTEILEN
				 * ====================================================
				 */
				if (state.equals(SpielerState.ON_TURN_ARMEENVERTEILEN)) {
						south_txt_display.setText("");
						// Beim Armeen verteilen noch anzeigen, wie viele noch verteilt werden dürfen.
						north_txt_phase.setText("Verteile deine Armeen! Verbleibend: " + neueArmeen + " Armeen.");
						// Wenn die Zahl aufgebraucht ist, automatisch in den Kartentauschmodus wechseln
						// (es sei denn, der Spieler kann keine Karten tauschen)
						if (neueArmeen <= 0) {
							// Mission des Spielers prüfen lassen
							risk.pruefeMission(RiskClientGUI.getSpieler());
							// State weiter setzen
							s.setState(SpielerState.ON_TURN_KARTENTAUSCHEN);
							state = s.getState();
							// Kartentausch direkt initialisieren
							this.kartentausch();
							// Alle Clients updaten lassen
							//System.out.println("State vom Clientspieler vorher : "+RiskClientGUI.getSpieler().getState());
							risk.forceUpdate(RiskClientGUI.getSpieler(), ChangeConst.MAPUPDATE);
							//System.out.println("State vom Clientspieler nachher: "+RiskClientGUI.getSpieler().getState());
						}
				/*
				 * ====================================================
				 * KARTENTAUSCHEN UND ARMEEN BEKOMMEN
				 * ====================================================
				 */
				} else if (state.equals(SpielerState.ON_TURN_KARTENZUARMEENGEMACHT)) {
						// Beim Armeen verteilen noch anzeigen, wie viele noch verteilt werden dürfen.
						north_txt_phase.setText("Verstärke deine Macht! Verbleibend: " + neueArmeen + " Armeen zum Verteilen.");
						// Wenn die Zahl aufgebraucht ist, automatisch in den Kartentauschmodus wechseln
						// (es sei denn, der Spieler kann keine Karten tauschen)
						if (neueArmeen <= 0) {
							// Übergang: KARTENTAUSCH --- KÄMPFEN
							s.setState(SpielerState.ON_TURN_KAEMPFEN);
						}
				/*
				 * ====================================================
				 * KÄMPFEN
				 * ====================================================
				 */
				} else if (state.equals(SpielerState.ON_TURN_KAEMPFEN)) {
						// Button anzeigen, mit dem der Spieler die Angriffsphase auch vorzeitig beenden kann
						south_buttonBewegungsphaseBeenden.setEnabled(false);
						south_buttonAngriffEnde.setEnabled(true);
						// Angriffszahl zurückgeben lassen
						//System.out.println("Angriffszahl, die möglich ist: "+ risk.angriffInit(RiskClientGUI.getSpieler()));
						//System.out.println("Besitz vom Clientspieler: "+RiskClientGUI.getSpieler().getBesitz().size());
						if (risk.angriffInit(RiskClientGUI.getSpieler()) > 0) {
							// Wenn es noch mögliche Angriffe gibt, das auch anzeigen
							// Beim Kämpfen oben im Phasentext anzeigen, dass es nun Zeit für einen Kampf ist.
							north_txt_phase.setText("Zeit für einen Kampf! Wähle zuerst eines deiner Länder und danach ein feindliches Land aus.");
							// Unten gleichzeitig anzeigen, welche Länder gerade ausgewählt wurden, zum Kampf.
							String textFuerUnten = "Ausgewählt zum Angriff: ";
							if (source != null) {
								textFuerUnten += source;
								if (dest != null) {
									textFuerUnten += " -> " + dest;
								}
							}
							south_txt_display.setText(textFuerUnten);
						} else {
							// Wenn keine Angriffe mehr möglich sind, Übergang zum Armeen umverteilen
							if (ad != null)
								ad.setVisible(false);
							// Source und Dest noch auf null zurücksetzen (werden auch für Bewegungen wieder gebraucht)
							source = null;
							dest = null;
							// Phase weiterleiten
							s.setState(SpielerState.ON_TURN_ARMEENBEWEGEN);
							south_buttonAngriffEnde.setEnabled(false);
							updateTexts();
						}
				/*
				 * ====================================================
				 * ARMEEN BEWEGEN
				 * ====================================================
				 */
				} else if (state.equals(SpielerState.ON_TURN_ARMEENBEWEGEN)) {
						// Button anzeigen, mit dem der Spieler die Bewegungsphase auch vorzeitig beenden kann
						south_buttonAngriffEnde.setEnabled(false);
						south_buttonBewegungsphaseBeenden.setEnabled(true);
						// Beim Armeen bewegen erstmal signalisieren, welche überhaupt geschoben werden dürfen.
						north_txt_phase.setText("Mobilisiere deine Armeen! Sofern sie nicht an einem Kampfgeschehen teilgenommen haben, kannst du sie bewegen.");
						south_txt_display.setText("");
						// Prüfen, ob Armeen bewegt werden können
						int moeglicheBewegungen = risk.armeenbewegenInit(RiskClientGUI.getSpieler());
						//System.out.println("bewegbar: " + moeglicheBewegungen);
						// Wenn ja, einleiten, wenn nein, Zug beenden
						if (moeglicheBewegungen > 0) {
							// Unten gleichzeitig anzeigen, welche Länder gerade ausgewählt wurden, zum Kampf.
							String textFuerUnten = "Armeen verschieben: ";
							if (source != null) {
								textFuerUnten += source;
								if (dest != null) {
									textFuerUnten += " -> " + dest;
								}
							}
							south_txt_display.setText(textFuerUnten);
						} else {
							RiskClientGUI.getSpieler().setState(SpielerState.WAIT_FOR_TURN);
							south_buttonBewegungsphaseBeenden.setEnabled(false);
							RiskClientGUI.getSpieler().setSiegreich(false);
							// Der nächste Spieler muss gewählt werden!
							risk.nextSpieler();
							ArmeenButton.deselectAll(armeenButtons);
							this.updateTexts();
						}
				}
			
		} else {
			// Ansonsten ist der Client gerade nicht an der Reihe. Trotzdem aktualisiert er das Colorlabel mit der Farbe des aktuellen Spielers.
			Spieler akteur = risk.getAkteur();
			north_colorlabel.setBackground(new Color(akteur.getFarbe()));
			north_colorlabel.setVisible(true);
			north_txt_spielerAmZug.setText("Momentan am Zug: " + akteur + "...");
			north_txt_phase.setText("");
		}
	}

	/** Update-Methode des PanelInterfaces */
	public void updateYourself() {
		// Center-Panel: ArmeenButtons updaten
		updateArmeenButtons();
		// Texte updaten
		updateTexts();
		// Repaint und UI
		this.validate();
		this.repaint();
	}
	
	/** Überschriebene Update-Methode vom Observer-Interface. Wenn die ClientEngineNetwork etwas raussendet, wird diese Methode angestoßen.
	 * @param o			Objekt, das die Nachricht ausgelöst hat (ungenutzt)
	 * @param objMode	Parameter-Objekt, enthält den Mode (also die "Art" der Nachricht) und ggf. weitere Zusatzinformationen zur Darstellung der GUI */
	public void update(Observable o, Object objMode) {
		// Objekt "entpacken" und schauen, welcher Konstante es entspricht. Dann abhängig vom eigenen SpielerState entscheiden, ob die Aktualisierung nötig ist.
		int mode = (Integer) ((Result)objMode).pop();
		// Was für ein Mode ist angekommen?
		switch (mode) {
			case ChangeConst.NEWTURN:
				alreadySaved = false;
			case ChangeConst.MAPUPDATE:
				// Bei einer Aktualisierung der Karte die ArmeenButtons neu zeichnen lassen
				this.updateArmeenButtons();
				// Und im East-Panel die Info-Tabelle neu berechnen (vll. hat sich ja die Länderanzahl geändert?)
				this.east.updateInfos();
				break;
			case ChangeConst.CHATMSG:
				// Bei einer Chatnachricht dem ChatPanel im South-Panel sagen, dass es sich neu zeichnen soll
				ChatMessage c = (ChatMessage) ((Result)objMode).pop();
				this.southeast_chatPanel.updateBox(c);
				break;
			case ChangeConst.DEFENDYOURSELF:
				// Der Spieler wurde angegriffen! In diesem Fall wird ein passender AngriffsDialog mit dem weiteren Parameter des Result-Objektes gezeigt
				this.defend((Angriff) ((Result)objMode).pop());
				break;
			case ChangeConst.KAMPFERGEBNIS:
				// Ein Kampfergebnis kommt an. Das erstmal auspacken
				Angriff a = (Angriff) ((Result)objMode).pop();
				boolean erfolgreich = (Boolean) ((Result)objMode).pop();
				int restEinheiten = (Integer) ((Result)objMode).pop();
				Karte karte	= (Karte) ((Result)objMode).pop();
				// Nach-Kampf-Methode anstoßen, die diese ganzen Sachen verarbeitet
				this.nachKampf(a, erfolgreich, restEinheiten, karte);
				// Source- und Dest-Länder zurücksetzen
				source = null;
				dest = null;
				// Zuletzt alle ArmeenButtons deaktivieren
				ArmeenButton.deselectAll(armeenButtons);
				this.updateArmeenButtons();
				break;
			case ChangeConst.KAMPFNOTICE:
				// ArmeenButtons updaten
				this.updateArmeenButtons();
				// Ein Kampfergebnis kommt an. Das erstmal auspacken
				if (ad != null)
					ad.setVisible(false);
				Angriff at = (Angriff) ((Result)objMode).pop();
				boolean win = (Boolean) ((Result)objMode).pop();
				if (win) {
					center_statusnachricht.setText(at.getAngreifer() + " hat " + at.getNach() + " eingenommen."); 
				} else {
					center_statusnachricht.setText(at.getVerteidiger() + " hat einen Angriff auf " + at.getNach() + " erfolgreich abgewehrt.");
				}
				this.setStatusTimer();
				break;
			case ChangeConst.GAMEFINISHED:
				// Spiel vorbei. Rausziehen des Gewinners, wechseln auf LoginPanel, anzeigen der Gewinnerbox
				Spieler sieger = (Spieler) ((Result)objMode).pop();
				// Rausziehen des neuen Spielerstates, der auch gleich gesetzt wird
				RiskClientGUI.getSpieler().setState((String) ((Result)objMode).pop());
				// Wechseln auf Login-Panel, Anzeigen des Gewinners
				RiskClientGUI.getInstance().changePanel(new LoginPanel());
				new MessageDialog(RiskClientGUI.getInstance(), "Das Spiel ist vorbei! Der Gewinner ist "+sieger+". Hurra!", "Spielende");
				// Observer entfernen
				risk.removeObserver(this);
				break;
			case ChangeConst.GAMEABORT:
				// Gewaltsamer Abbruch des Spiels. Beenden des Programms
				System.exit(1);
				break;
		}
		this.updateTexts();
		
		// Repaint
		this.validate();
		this.repaint();
	}
}