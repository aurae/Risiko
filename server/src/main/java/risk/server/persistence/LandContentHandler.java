package risk.server.persistence;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import risk.commons.valueobjects.Karte;
import risk.commons.valueobjects.Kontinent;
import risk.commons.valueobjects.Land;

/**
 * Diese Klasse arbeitet mit ReadLandXML zusammen, um die Länder-XML-Datei auszulesen.
 * Sie muss, um die eingelesenen Werte ordentlich verarbeiten zu können,
 * den Aufbau eines Land- und eines Kontinent-Objektes kennen.
 * @author Marcel, Srdan
 */
public class LandContentHandler implements ContentHandler, Serializable {
	private static final long serialVersionUID = 8679971730358346424L;
	
	/*
	 * AUFBAU DER LAND-XML:
	 * <welt>
	 * 		<kon name="Kontinentname">
	 * 			<laender>
	 * 				<land name="Landname">
	 * 					<nachbar>"Nachbarland"</nachbar>
	 * 					<nachbar>"Nachbarland"</nachbar>
	 * 					<farbMap>"Eindeutige Farbe des Landes auf der Overlay-Karte"</farbMap>
	 * 					<button x="Button-X-Pos" y="Button-Y-Pos"></button>
	 * 					<symbol>Symbol-Zeichen</symbol>
	 * 					...
	 * 				</land>
	 * 				<land name="Landname">	  					
	 * 					<nachbar>"Nachbarland"</nachbar>
	 * 					<nachbar>"Nachbarland"</nachbar>
	 * 					<farbMap>"Eindeutige Farbe des Landes auf der Overlay-Karte"</farbMap>
	 * 					<symbol>Symbol-Zeichen</symbol>
	 * 					...
	 * 				</land>
	 * 			</laender>
	 * 		</kon>
	 * 		<kon name="Kontinentname">
	 * 		...
	 * 		</kon>
	 * </welt>
	 */
	
	// Attribute
	/** Alle Kontinente, die eingelesen worden sind */
	private ArrayList<Kontinent> alleKontinente = new ArrayList<Kontinent>();
	/** Aktueller Wert des Parsers */
	private String currentValue;
	/** Aktuelles Land */
	private Land land;
	/** Aktueller Kontinent */
	private Kontinent kontinent;
	/** Liste aller Länder des aktuellen Kontinents */
	private ArrayList<Land> laender;
	
	/** Rückgabe der Kontinent-ArrayList */
	public ArrayList<Kontinent> getAlleK() { return this.alleKontinente; }
	
	/** Zwischenspeicher für gelesene Zeichen */
	public void characters(char[] ch, int start, int length) throws SAXException {
		currentValue = new String(ch, start, length);
	}
	
	/** Wenn der Parser zu einem Start-Tag kommt */
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		if (localName.equals("kon")) {
			// Neuen Kontinent mit Namen (Attribut) erzeugen und der Gesamtliste zufügen
			kontinent = new Kontinent();
		    kontinent.setName(atts.getValue("name"));
		    alleKontinente.add(kontinent);
		}
		if (localName.equals("laender")) {
		    // Neue Liste von Ländern anlegen und dem Kontinent hinzufügen
		    laender = new ArrayList<Land>();
		    kontinent.setLaender(laender);
		}
		if (localName.equals("land")) {
			// Neues Land anlegen, Name setzen, zur laender-ArrayList hinzufügen
			land = new Land(atts.getValue("name"));
			laender.add(land);
			//System.out.println("XML: "+land+" == " + land.getClass().getName() + '@' + Integer.toHexString(land.hashCode()));
		}
		if (localName.equals("button")) {
			// Button-Koordinaten aus den Attributen auslesen und in einem neuen Point-Objekt speichern
			int x = Integer.parseInt(atts.getValue("x"));
			int y = Integer.parseInt(atts.getValue("y"));
			land.setButtonPos(new Point(x,y));
		}
	}
	
	/** Wenn der Parser zu einem End-Tag kommt */
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (localName.equals("nachbar")) {
			// Nachbar in String-ArrayList packen
			this.land.addStrNachb(currentValue);
		}
		if (localName.equals("farbMap")) {
			// FarbMap-Wert speichern
			currentValue = currentValue.substring(2);
			land.setFarbMap(Integer.parseInt(currentValue,16));
		}
		if (localName.equals("symbol")) {
			// Symbol speichern
			if (currentValue.equals("REITER"))
				land.setSymbol(Karte.REITER);
			else if (currentValue.equals("SOLDAT"))
				land.setSymbol(Karte.SOLDAT);
			else if (currentValue.equals("KANONE"))
				land.setSymbol(Karte.KANONE);
			else if (currentValue.equals("JOKER"))
				land.setSymbol(Karte.JOKER);
		}
		if (localName.equals("welt")) {
			// Ende der Datei
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
