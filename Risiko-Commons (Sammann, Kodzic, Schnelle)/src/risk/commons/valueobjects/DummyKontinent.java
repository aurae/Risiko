package risk.commons.valueobjects;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Ein Objekt dieser Klasse wird nur von bestimmten Missionen benutzt,
 * n�mlich dann, wenn die Siegbedingung eines Spielers ist, einen
 * beliebigen Kontinent einzunehmen. In Erweiterung zur Oberklasse
 * Kontinent beherrscht die Klasse DummyKontinent eine Methode, die
 * alle Kontinente auf "komplett eingenommen" pr�fen kann. Zudem
 * kennt ein DummyKontinent-Objekt die Kontinentmission, um die
 * "nicht zu pr�fenden" Kontinente von der Suche auszuschlie�en.
 * @author Marcel
 * @version 1
 */
public class DummyKontinent extends Kontinent {
	private static final long serialVersionUID = -6687343020062774135L;
	
	// Attribute
	/** Die Mission */
	private KontinentMission mission;			
	/** Die �brigen einzunehmenden Kontinente */
	private ArrayList<Kontinent> andereKons;	
	/** Die Welt, aus der er die �brigen Kontinente bekommt */
	private Welt w;								
	
	/** Konstruktor mit Welt-Referenz */
	public DummyKontinent(Welt w) {
		this.w = w;
	}
	
	/** �berschriebene Methode zur �berpr�fung aller Kontinente auf einheitlichen Besitz */
	public boolean pruefeEinnahme() {
		// Alle Kontinente holen:
		ArrayList<Kontinent> alleK = w.getKontinente();
		// Entfernen aller Kontinente, die in der andereKons-Liste stehen
		Iterator<Kontinent> iter_alleK = alleK.iterator();
		while(iter_alleK.hasNext()) {
			Kontinent k = iter_alleK.next();
			if (!andereKons.contains(k)) {
				// Wenn der aktuelle Kontinent nicht in der speziellen Eroberungsliste
				// drin steht, wird gepr�ft, ob er vollst�ndig eingenommen ist
				// Dazu: Besitzer des ersten Landes im Kontinent ziehen
				Spieler s = k.getLaender().get(0).getSpieler();
				// auf Gleichheit pr�fen
				boolean geschafft = false;
				if (mission.getSpieler().equals(s))
					geschafft = k.pruefeEinnahme(s);
				// Wenn tats�chlich noch ein Kontinent gefunden wurde, der dem
				// Spieler mit der KontinentMission geh�rt, true zur�ckgeben
				if (geschafft) return true;
			}
		}
		// Wenn kein weiterer Kontinent gefunden wurde, false zur�ckgeben
		return false;
	}
	
	/** Diese Methode wird vom Dummy-Kontinent direkt nach der Erstellung
	 * durch den MissionContentHandler aufgerufen. Sie dient dazu, den
	 * Dummy-Kontinent dar�ber zu informieren, welche Kontinente der
	 * Spieler zus�tzlich erobern _muss_. Dies ist notwendig, damit seine
	 * pruefeEinnahme-Methode korrekt arbeitet */
	public void updateOtherKons() {
		andereKons = mission.getKontinente();
		andereKons.remove(this);
	}
	
	/** Setzen der Mission */
	public void setMission(Mission m) {
		if (m instanceof KontinentMission)
			mission = (KontinentMission) m;
	}
}
