package risk.commons.valueobjects;

/**
 * Diese Klasse fasst die Aufgaben zusammen, deren Ziel die restlose
 * Befreiung einer Farbe vom Spielfeld ist. Bekannt ist den Objekten
 * das "Opfer" und der Spieler, der diese Aufgabe hat. Beim Zuweisen
 * eines BefreiungsMission-Objektes an einen Spieler wird gepr�ft,
 * ob der Spieler dem Opfer entspricht. Wenn ja, wird ihm stattdessen
 * ein LandMission-Objekt zugewiesen (alternatives Ziel bei gleicher
 * Farbe).
 * @author Marcel
 */
public class BefreiungsMission extends Mission {
	private static final long serialVersionUID = -5599607692578810634L;
	
	// Attribute
	/** Der zu besiegende Spieler */
	private Spieler o;			
	/** Die Spielfarbe des zu besiegenden Spielers */
	private int farbe;			

	/** Pr�f-Methode der Befreiungsmission. Diese Art Mission pr�ft nur, ob
	 * das "Opfer", also der zu besiegende Spieler, keinen Besitz mehr hat. */
	public boolean pruefeSieg() {
		// Besitz des Opfers pr�fen; wenn die Gr��e == 0 ist, ist die Mission erf�llt
		if (o.getBesitz().size() == 0)
			return true;
		else return false;
	}
	
	/** Zuweisung eines Spielers an dieses Missionsobjekt.
	 * Wenn der �bergebene Spieler gleich dem Opfer aus dem
	 * Missionstext ist, wird stattdessen ein LandMission-Objekt
	 * an den Spieler gebunden */
	public void setSpieler(Spieler s) {
		if (s.equals(this.o)) {
			// Spieler ist gleich Opfer => LandMission zuweisen
			LandMission neu = new LandMission();
			neu.setAnzahl(24);
			neu.setStaerke(1);
			neu.setText("Befreien Sie 24 L�nder Ihrer Wahl!");
			neu.setSpieler(s);
			s.setMission(neu);
		} else {
			super.setSpieler(s);
		}
	}
	
	/** Farbe des "Opfers" setzen */
	public void setFarbe(int n) { this.farbe = n; }
	/** R�ckgabe der Farbe des "Opfers" */
	public int getFarbe() {	return farbe; }
	/** "Opfer" setzen */
	public void setOpfer(Spieler s) { o = s; }	
}
