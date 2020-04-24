package risk.commons;


/**
 * Hier werden Konstanten definiert, die Client und Server bei der Kommunikation helfen. Beim Callback
 * des Servers wird ein Wert dieser Klasse mitgeliefert, der den Clients sagt, welche Teile der
 * GUI aktualisiert werden müssen (also gewissermaßen, ob die Aktualisierung überhaupt relevant
 * für den aktuellen GUI-Status ist).
 * @author Marcel
 */
public abstract class ChangeConst {
	/** Eine Nachricht wurde an den Chat gesendet */
	public static final int CHATMSG = 11001;
	/** Etwas hat sich auf der Weltkarte getan (Besitz/Anzahl geändert) */
	public static final int MAPUPDATE = 11002;
	/** Ein Spieler hat sich abgemeldet. */
	public static final int SPIELERLOGOUT = 11003;
	/** Ein Spieler hat sich angemeldet. */
	public static final int SPIELERLOGIN = 11004;
	/** Ein Spieler wurde angegriffen und muss sich verteidigen. */
	public static final int DEFENDYOURSELF = 11005;
	/** Ein Kampfergebnis wird übertragen */
	public static final int KAMPFERGEBNIS = 11006;
	/** Das Spiel wurd gewaltvoll beendet */
	public static final int GAMEABORT = 11007;
	/** Ein neuer Zug beginnt */
	public static final int NEWTURN = 11008;
	/** Eine Welt wurd erfolgreich geladen */
	public static final int WELTLOADED = 11009;
	/** Ein Kampf hat stattgefunden, aber der Client-Spieler war daran unbeteiligt. */
	public static final int KAMPFNOTICE = 11010;
	/** Das Spiel ist vorbei und der Gewinner steht fest. */
	public static final int GAMEFINISHED = 11011;
}
