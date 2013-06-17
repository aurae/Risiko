package risk.commons.exceptions;

/**
 * Eine FalscherModeException wird dann geworfen, wenn ein Lade-/Speicherdialog
 * nicht mit einer dieser Konstanten initialisiert werden soll.
 * @author Marcel
 */
public class FalscherModeException extends Exception {
	private static final long serialVersionUID = -278577839935861562L;

	public FalscherModeException(String mode) {	super(mode + " ist kein akzeptierter Modus!"); }
}
