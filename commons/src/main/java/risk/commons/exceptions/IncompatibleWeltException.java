package risk.commons.exceptions;

/**
 * Eine Instanz dieser Klasse wird dann geworfen, wenn beim Laden
 * eines gespeicherten Spielstandes ein Fehler aufgetreten ist.
 * Dieser Fehler kann entweder auf die falsche Anzahl eingeloggter
 * Spieler oder auf die falsche Identität eines Spielers zurück-
 * zuführen sein.
 * @author Marcel
 */
public class IncompatibleWeltException extends Exception {
	private static final long serialVersionUID = -278577839935861562L;

	public IncompatibleWeltException() { super("Diese Welt ist nicht mit der Zahl oder den Namen der registrierten Spieler kompatibel!"); }
}
