package risk.server.domain;

import java.io.Serializable;
import java.util.ArrayList;

import risk.commons.valueobjects.BefreiungsMission;
import risk.commons.valueobjects.Mission;
import risk.commons.valueobjects.Spieler;
import risk.commons.valueobjects.Welt;
import risk.server.persistence.ReadMissionXML;

/**
 * Diese Klasse ist mit der Verwaltung der Missionen und Siegbedingungen beauftragt.
 * Sie wird beispielsweise zu Partiebeginn angesprochen, um die Missionen aus der
 * zugehörigen XML-Datei zu lesen und sie and die Spieler zu verteilen.
 * Außerdem kann das Welt-Objekt diese Klasse fragen, ob ein Spieler gewonnen hat.
 * @author Marcel
 */
public class MissionsVerwaltung implements Serializable {
	private static final long serialVersionUID = 8920382590905049864L;
	
	// Attribute
	/** Handler-Objekt, das die XML-Datei einparst und mit den eingelesenen Werten hantiert */
	private ReadMissionXML handler;
	/** Welt-Referenz */
	private Welt w;

	/** Konstruktor mit Welt-Referenz */
	public MissionsVerwaltung(Welt w) {
		this.w = w;
		handler = new ReadMissionXML();
	}
	
	/** Diese Methode lässt die verfügbaren Missionen aus der XML-Datei lesen
	 * und verteilt direkt eine Mission an jeden Spieler */
	public void organizeMissionen() {
		// Handler erzeugen und Liste in der Variable speichern
		ArrayList<Mission> listeMission = handler.read("xml/missionen.xml");
		int missionsanzahl = listeMission.size();
		// Kopie der Missions-Liste erstellen (daraus werden ggf. Missionen entfernt, wenn die Anforderungen nicht erfüllt sind)
		ArrayList<Mission> missionenCopy = new ArrayList<Mission>(listeMission);
		// BefreiungsMissionen aussortieren
		for (int i = 0; i < missionsanzahl; i++){
			// Aktuelle Mission
			Mission aktuelleMission = listeMission.get(i);
			// Wenn es eine BefreiungsMission ist...
			if (aktuelleMission instanceof BefreiungsMission) {
				// ...hole die Farbe, die diese BefreiungsMission zum Ziel hat...
				Integer aktuelleFarbe = ((BefreiungsMission) aktuelleMission).getFarbe();
				// ...und vergleiche mit den Farben, die noch im Welt-Objekt verfügbar sind
				if (w.getFarben().contains(aktuelleFarbe)) {
					// Wenn die Farbe noch im Farb-Array vorhanden ist, hat
					// sie kein Spieler bekommen. Dann kann die Mission aus der Liste raus
					missionenCopy.remove(aktuelleMission);
				}
			}
		}
		// Copy "zurückkopieren"
		listeMission = missionenCopy;
		// Missionen an die Spieler verteilen
		// Dazu: Spieler-Liste vom Welt-Objekt holen
		Spieler[] spieler = w.getSpieler();
		// Nacheinander bekommt nun jeder Spieler zufällig eine Mission zugeteilt
		// Zufallszahl-Variable vorstellen
		int zufall;
		// Jetzt nacheinander jedem Spieler-Objekt eine zufällige Mission zuteilen.
		// Danach die Mission aus der Liste entfernen
		for(int i = 0; i < SpielerVerwaltung.countSpieler(); i++) {
			// Zufallszahl generieren
			 zufall = (int) (Math.random() * listeMission.size());
			 // Wechselseitige Beziehung zwischen Spieler und Mission herstellen
			 spieler[i].setMission(listeMission.get(zufall));
			 listeMission.get(zufall).setSpieler(spieler[i]);
			 // Mission aus der Liste entfernen
			 listeMission.remove(zufall);
		}
	}
	
	/** Setzt die Welt-Referenz auf das übergebene Welt-Objekt */
	public void setWelt(Welt w) { this.w = w; }
}
