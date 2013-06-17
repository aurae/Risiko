package risk.server.persistence;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import risk.commons.valueobjects.Mission;

/**
 * Diese Klasse liest die XML-Datei der Länder-Daten ein
 * und verwendet den LandContentHandler, um die "Weltkarte aufzubauen"
 * @author Marcel, Srdan
 */
public class ReadMissionXML implements Serializable {
	private static final long serialVersionUID = 5022332657956137434L;

	/** Konstruktor */
	public ReadMissionXML() { }
	
	/** Methode für das Parsen der XML
	 * @return Die komplette Kontinent- und Strukturliste der XML */
	public ArrayList<Mission> read(String datei) {
		try {
			// Reader initialisieren
			XMLReader xmlreader = XMLReaderFactory.createXMLReader();
			// Datei einlesen
			FileReader reader = new FileReader(datei);
			InputSource inputSource = new InputSource(reader);
			// ContentHandler setzen
			MissionContentHandler content = new MissionContentHandler();
			xmlreader.setContentHandler(content);
			// Parsen
			xmlreader.parse(inputSource);
			// Kontinentliste holen und zurückgeben
			ArrayList<Mission> k = content.getAlleM();
			return k;
	    } catch (FileNotFoundException e) {
	        System.out.println("missionen.xml nicht gefunden");
	      } catch (Exception e) {
	    	  e.printStackTrace();
	      }
	      return null;
	}
}
