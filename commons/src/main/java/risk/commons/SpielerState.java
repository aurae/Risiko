package risk.commons;

/**
 * Diese Klasse definiert lediglich Konstanten, die den aktuellen Zustand eines Clients (und dem
 * Spieler dahinter) repräsentieren. Jedes Spieler-Objekt hat demnach zu jeder Zeit einen
 * String, die seinen Zustand repräsentiert.
 * @author Marcel
 */
public abstract class SpielerState {
	/** Nicht eingeloggt */
	public static final String NOT_LOGGED_IN				= "NOLOGIN ";
	/** Eingeloggt */
	public static final String LOGGED_IN 					= "LOGIN ";
	/** Nach Spielstart, aber nicht am Zug */
	public static final String WAIT_FOR_TURN				= "WAIT ";
	/** Nach Spielstart, nicht am Zug, aber wurde angegriffen */
	public static final String WAIT_ATTACKED				= WAIT_FOR_TURN + "ATTACKED";
	/** Nach Spielstart, am Zug allgemein */
	public static final String ON_TURN 						= "TURN ";
	/** Nach Spielstart, am Zug und beim Armeen verteilen */
	public static final String ON_TURN_ARMEENVERTEILEN		= ON_TURN + "ARMEENVERTEILEN";
	/** Nach Spielstart, am Zug und beim Kartentauschen */
	public static final String ON_TURN_KARTENTAUSCHEN		= ON_TURN + "KARTENTAUSCHEN";
	/** Nach Spielstart, am Zug, Karten eingetauscht und Armeen zum Verteilen erhalten */
	public static final String ON_TURN_KARTENZUARMEENGEMACHT= ON_TURN + ON_TURN_KARTENTAUSCHEN + " " + ON_TURN_ARMEENVERTEILEN;
	/** Nach Spielstart, am Zug und beim Kämpfen */
	public static final String ON_TURN_KAEMPFEN				= ON_TURN + "KAEMPFEN";
	/** Nach Spielstart, am Zug und beim Armeen bewegen */
	public static final String ON_TURN_ARMEENBEWEGEN		= ON_TURN + "BEWEGEN";
}
