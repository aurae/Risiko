package risk.commons.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;

import risk.commons.valueobjects.Land;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;

/**
 * GUI-Komponente für die Risiko-Anwendung. Ein Objekt der ArmeenButton-Klasse repräsentiert
 * einen kleinen Kreis mit einer Zahl, der auf jedem Land auf der Weltkarte zu finden ist.
 * Seine Farbe stellt den Besitzer des Gebietes dar, die Zahl auf ihm die Einheitenstärke.
 * @author Yannik
 */
public class ArmeenButton extends JButton {
	private static final long serialVersionUID = -3550459968114585308L;

	/** Strichstärke der "Umrandung" eines ArmeenButtons bei Auswahl */
	public static int BORDER = 2;
	
	// Attribute
	/** Farbe des Spielers, der das Land besitzt, zu dem der Button gehört */
	private int farbe;			
	/** Zahl der Armeenstärke in diesem Land */
	private String zahl;		
	/** Land zum Button */
	private Land l;	

	/** Umrandung anzeigen ja/nein */
	private boolean showBorder;	
	/** Dimensionen des Buttons */
	private int width	= 20;
	/** Dimensionen des Buttons */
	private int height	= 20;	
	
	/** Konstruktor
	 * @param s	Spieler, dem das Land gehört, zu dem der ArmeenButton gehört
	 * @param l Land, dessen ArmeenButton die Instanz zugeordnet ist */
	public ArmeenButton(Spieler s, Land l) {
		super();
		farbe = s.getFarbe();
		this.l = l;
		this.setToolTipText(l.getName() + " (Besitzer: "+l.getSpieler() + ")");
		zahl = "1";
		showBorder = false;
		
		// Hintergrund löschen
		this.setBorder(null);
		this.setBackground(null);
		this.setBorderPainted(false);
		this.setOpaque(false);
		
		// Größe setzen
		Dimension d = new Dimension(width,height);
		this.setMinimumSize(d);
		this.setPreferredSize(d);
		this.setMaximumSize(d);
	}
	
	/** paint()-Methode, welche für das Zeichnen des ArmeenButtons zuständig ist */
	public void paint(Graphics g) {
		Dimension d = this.getSize();
		int width = (int) d.getWidth();
		int height = (int) d.getHeight();
		// Umrandung zeichnen? (Ja, wenn der Button "aktiv" ist)
		Color c = showBorder ? new Color(254,254,254) : new Color(farbe);
		g.setColor(c);
		g.fillArc(0,0,width,height,0,360);
		// Rest zeichnen
		g.setColor(new Color(farbe));
		g.fillArc(BORDER, BORDER, width-2*BORDER,height-2*BORDER,0,360);
		// Text drauf
		writeLabel(g, width, height);
	}
	
	/** Text auf dem Button ausgeben / aktualisieren
	 * @param g			Graphics-Objekt aus der paint()-Methode
	 * @param width		Breite des Buttons
	 * @param height	Höhe des Buttons */
	private void writeLabel(Graphics g, int width, int height) {
			Font myFont = new Font(Font.SANS_SERIF, Font.BOLD, 16);
			g.setFont(myFont);
			FontMetrics fm = g.getFontMetrics();
			int textWidth = fm.stringWidth(zahl);
			int textHeight = fm.getHeight();
			int descent = fm.getDescent();
			Color c = (farbe == Welt.BLACK) ||
					  (farbe == Welt.BLUE) ||
					  (farbe == Welt.RED) ? new Color(255,255,255) : new Color(0,0,0); 
			g.setColor(c);
			g.drawString(zahl, width / 2 - textWidth / 2, height / 2
					+ (textHeight / 2 - descent));
	}
	
	/** Diese Methode frischt die Informationen über diesen ArmeenButton wieder auf. Sie wird
	 * vom MainPanel der GUI im Zusammenhang mit verschiedenen Update-Methoden aufgerufen
	 * @param s	Spieler-Objekt mit neuem Zustand
	 * @param l	Land-Objekt mit neuem Zustand */
	public void updateInfos(Spieler s, Land l) {
		this.l = l;
		this.farbe = s.getFarbe();
		this.zahl = "" + l.getStaerke();
		this.setToolTipText(l.getName() + " (Besitzer: "+l.getSpieler() + ")");
		repaint();
	}

	/** Rückgabe der Breite des ArmeenButtons */
	public int getWidth() {
		return width;
	}
	
	/** Rückgabe der Höhe des ArmeenButtons */
	public int getHeight() {
		return height;
	}
	
	/** toString()-Methode für println-Einbindung von ArmeenButtons */
	public String toString() {
		return l.getName();
	}

	/** Auswählen des ArmeenButtons. Wird aufgerufen, wenn er oder das zugehörige Land angeklickt worden ist */
	public void select(boolean b) {
		this.showBorder = b;
		this.repaint();
	}
	
	/** Sucht aus einer übergebenen Land-ArmeenButton-Hashmap den Button raus, der zum außerdem übergebenen Land gehört
	 * @return ArmeenButton-Objekt, das zum Land l gehört, oder null bei keiner Übereinstimmung */
	public static ArmeenButton getButtonFromLand(HashMap<Land, ArmeenButton> liste, Land l) {
		Iterator<Map.Entry<Land,ArmeenButton>> iter = liste.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Land,ArmeenButton> item = iter.next();
			if (item.getKey().equals(l))
				return item.getValue();
		}
		return null;
	}
	
	/** Markieren aller ArmeenButtons der Länder, die im Nachbar-Array des übergebenen Landes stehen
	 * @param	liste	Liste mit allen Land-ArmeenButton-Zuordnungen
	 * @param	l		Land, dessen Nachbarn durchgegangen werden
	 * @param	cur		Spieler-Objekt, mit dem die Nachbaren abgeglichen werden */
	public static void selectNachbarButtons(HashMap<Land, ArmeenButton> liste, Land l, Spieler cur) {
		Iterator<Land> iterator = l.getNachbaren().iterator();
		while(iterator.hasNext()) {
			Land next = iterator.next();
			// Bedingung: Nachbarland gehört nicht dem Spieler, der übergeben wurde
			if (!next.getSpieler().equals(cur))
				ArmeenButton.getButtonFromLand(liste, next).select(true);
		}
	}
	
	/** Markieren aller ArmeenButtons der Länder, die mit dem übergebenen Land benachbart sind und für einen Bewegungsvorgang in Frage kommen
	 * @param	liste	Liste mit allen Land-ArmeenButton-Zuordnungen
	 * @param	l		Land, dessen Nachbarn durchgegangen werden
	 * @param	cur		Spieler-Objekt, mit dem die Nachbaren abgeglichen werden */
	public static void selectMovableButtons(HashMap<Land, ArmeenButton> liste, Land l, Spieler cur) {
		Iterator<Land> iterator = l.getNachbaren().iterator();
		while(iterator.hasNext()) {
			Land next = iterator.next();
			// Bedingungen: Nachbarland gehört dem Spieler und es wurde in diesem Zug nicht bewegt
			if (next.getSpieler().equals(cur) && !next.getBewegt())
				ArmeenButton.getButtonFromLand(liste, next).select(true);
		}
	}
	
	/** Mittels dieser Methode werden alle ArmeenButtons deselektiert. Das ist nötig, damit die neue Markierung
	 * richtig angezeigt werden kann. 
	 * @param liste	Liste mit allen Land-ArmeenButton-Zuordnungen */
	public static void deselectAll(HashMap<Land, ArmeenButton> liste) {
		Iterator<Map.Entry<Land,ArmeenButton>> iter = liste.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Land,ArmeenButton> land = iter.next();
			land.getValue().select(false);
		}
	}

	/** Update-Methode für einen ArmeenButton in der übergebenen Liste
	 * @param liste	Liste mit allen Land-ArmeenButton-Zuordnungen
	 * @param l Land, dessen ArmeenButton geupdatet werden soll */
	public static void update(HashMap<Land, ArmeenButton> liste, Land l) {
		// ArmeenButton ziehen
		Iterator<Map.Entry<Land,ArmeenButton>> iter = liste.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Land,ArmeenButton> pair = iter.next();
			if (pair.getKey().equals(l))
				pair.getValue().updateInfos(l.getSpieler(), l);
		}
	}
}
