package risk.server.persistence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import risk.commons.RiskUtils;
import risk.commons.valueobjects.Kontinent;

/**
 * Diese Klasse liest die XML-Datei der Länder-Daten ein
 * und verwendet den LandContentHandler, um die "Weltkarte aufzubauen"
 * @author Marcel, Srdan
 */
public class ReadLandXML implements Serializable {
	private static final long serialVersionUID = 3003816882495042782L;

	/** Konstruktor */
	public ReadLandXML() { }
	
	/** Methode für das Parsen der XML
	 * @return Die komplette Kontinent- und Strukturliste der XML */
	public ArrayList<Kontinent> read(String datei) {
		try {
			// Reader initialisieren
			XMLReader xmlreader = XMLReaderFactory.createXMLReader();
			// Datei einlesen
			File file = RiskUtils.loadResourceFile(datei);
			FileReader reader = new FileReader(file);
			InputSource inputSource = new InputSource(reader);
			// ContentHandler setzen
			LandContentHandler content = new LandContentHandler();
			xmlreader.setContentHandler(content);
			// Parsen
			xmlreader.parse(inputSource);
			// Kontinentliste holen und zurückgeben
			ArrayList<Kontinent> k = content.getAlleK();
			return k;
	    } catch (FileNotFoundException e) {
	        System.out.println("laender.xml nicht gefunden");
	      } catch (Exception e) {
	        e.printStackTrace();
	      }
	      return null;
	}
}
