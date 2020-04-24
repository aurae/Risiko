package risk.server.persistence;

import java.io.Serializable;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import risk.commons.exceptions.KeinLandException;
import risk.commons.valueobjects.BefreiungsMission;
import risk.commons.valueobjects.DummyKontinent;
import risk.commons.valueobjects.Kontinent;
import risk.commons.valueobjects.KontinentMission;
import risk.commons.valueobjects.LandMission;
import risk.commons.valueobjects.Mission;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import risk.server.domain.LandVerwaltung;
import risk.server.domain.SpielerVerwaltung;

/**
 * Diese Klasse arbeitet mit ReadMissionXML zusammen, um die Länder-XML-Datei auszulesen.
 * Sie muss, um die eingelesenen Werte ordentlich verarbeiten zu können,
 * den Aufbau eines Mission-Objektes kennen.
 * @author Marcel. Srdan
 */
public class MissionContentHandler implements ContentHandler, Serializable {
	private static final long serialVersionUID = 4349119094114963580L;
	
	/*
	 * AUFBAU DER MISSION-XML:
	 * <missionen>
	 * 		<item nr="0" art="LAND">
	 * 			<anzahl>24</anzahl>
	 * 			<minStaerke>1</minStaerke>
	 * 			<text>Befreien Sie 24 Länder!</text>
	 * 		</item>
	 * 		<item nr="1" art="KONTINENT">
	 * 			<kontinent>Asien</kontinent>
	 * 			<kontinent>Europa</kontinent>
	 * 			<kontinent>DUMMY</kontinent>
	 * 			<text>Befreien Sie Asien, Europa und einen 
	 * 			dritten Kontinent Ihrer Wahl!</text>
	 * 		</item>
	 * 		<item nr="2" art="BEFREIUNG">
	 * 			<farbe>ROT</farbe>
	 * 			<text>Befreien Sie alle Länder von den roten 
	 * 			Armeen!</text>
	 * 		</item>
	 * </missionen>
	 */
	
	// Attribute
	/** ArrayList mit allen eingelesenen Missionen */
	private ArrayList<Mission> alleMissionen = new ArrayList<Mission>();
	/** Aktueller Wert des Parsers */
	private String currentValue;
	/** Aktuelle Mission */
	private Mission mission;
	
	/** Eigene Methode: Rückgabe der Kontinent-ArrayList */
	public ArrayList<Mission> getAlleM() { return this.alleMissionen; }
	
	/** Zwischenspeicher für gelesene Zeichen */
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
	}
	
	/** Wenn der Parser zu einem Start-Tag kommt */
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("item")) {
			// Neue Mission gemäß ihrer Art (Attribut) erstellen
			if (atts.getValue("art").equals("LAND")) {
				mission = new LandMission();
			} else if (atts.getValue("art").equals("BEFREIUNG")) {
				mission = new BefreiungsMission();
			} else if (atts.getValue("art").equals("KONTINENT")) {
				mission = new KontinentMission();
			}
			// ...und ab in die Liste aller Missionen
			alleMissionen.add(mission);
		}
	}
	
	/** Wenn der Parser zu einem End-Tag kommt */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		// Zuerst nach dem Missionstext fragen: Der ist bei allen Typen gleich
		if (localName.equals("text")) {
			mission.setText(currentValue);
		}
		// Ermitteln, welchen Typ mission hat. Abhängig davon eigene Abfragen durchführen
		if (mission instanceof LandMission) {
			// LandMissionen: Anzahl der zu befreienden Länder, Mindeststärke auf jedem Feld
			LandMission lmission = (LandMission) mission;
			if (localName.equals("anzahl")) {
				// Anzahl speichern
				lmission.setAnzahl(Integer.parseInt(currentValue));
			}
			if (localName.equals("minStaerke")) {
				// Mindeststärke speichern
				lmission.setStaerke(Integer.parseInt(currentValue));
			}
		} 
		else if (mission instanceof BefreiungsMission) {
			// BefreiungsMissionen: Farbe des zu vernichtenden Spielers
			BefreiungsMission bmission = (BefreiungsMission) mission;
			if (localName.equals("farbe")) {
				// Farbe speichern (Konstante in der Klasse Welt)
				int farbe = 0;
				if (currentValue.equals("ROT")) {
					farbe = Welt.RED;
				} else if (currentValue.equals("SCHWARZ")) {
					farbe = Welt.BLACK;
				} else if (currentValue.equals("BLAU")) {
					farbe = Welt.BLUE;
				} else if (currentValue.equals("GRÜN")) {
					farbe = Welt.GREEN;
				} else if (currentValue.equals("PINK")) {
					farbe = Welt.PINK;
				} else if (currentValue.equals("GELB")) {
					farbe = Welt.YELLOW;
				}
				bmission.setFarbe(farbe);
				// Jetzt den Spieler suchen, der diese Farbe hat
				Spieler spieler = SpielerVerwaltung.searchSpieler(farbe);
				// Dies auch dem Missions-Objekt mitgeben
				bmission.setOpfer(spieler);
			}
		}
		else if (mission instanceof KontinentMission) {
			// KontinentMissionen: Kontinente, die es zu erobern gilt
			KontinentMission kmission = (KontinentMission) mission;
			if (localName.equals("kontinent")) {
				// Kontinent zur Liste hinzufügen
				// (dazu wird direkt das Welt-Objekt angesprochen und die Referenz herausgesucht;
				// deshalb braucht ein KontinentMission-Objekt keine String-ArrayList)
				try {
					// Nur, wenn der Name nicht DUMMY ist, wird der Kontinent gesucht
					Kontinent k;
					if (!currentValue.equals("DUMMY"))
						k = LandVerwaltung.searchKontinent(currentValue);
					else {
						// Ansonsten wird ein Dummy-Kontinent erstellt
						k = new DummyKontinent(Welt.getWelt());
						// Dieser bekommt die Mission zugeteilt und erschließt sich
						// seine "Kontinent-Kollegen"
						((DummyKontinent) k).setMission(kmission);
						((DummyKontinent) k).updateOtherKons();
					}
					kmission.addKontinent(k);
				} catch(KeinLandException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	// Ungenutzte Methoden, die aber vom Interface verlangt werden
	/** Leere Implementierung */
	public void endDocument() throws SAXException {}
	/** Leere Implementierung */
	public void endPrefixMapping(String prefix) throws SAXException {}
	/** Leere Implementierung */
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	/** Leere Implementierung */
	public void processingInstruction(String target, String data) throws SAXException {}
	/** Leere Implementierung */
	public void setDocumentLocator(Locator locator) {}
	/** Leere Implementierung */
	public void skippedEntity(String name) throws SAXException {}
	/** Leere Implementierung */
	public void startDocument() throws SAXException {}
	/** Leere Implementierung */
	public void startPrefixMapping(String prefix, String uri)throws SAXException {}
}
