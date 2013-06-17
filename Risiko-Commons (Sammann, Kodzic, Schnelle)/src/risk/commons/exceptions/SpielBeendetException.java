package risk.commons.exceptions;

/**
 * Eine SpielBeendetException wird dann geworfen, wenn das Spiel
 * durch einen "K.O.-Sieg" beendet wird.
 * @author Marcel
 */
public class SpielBeendetException extends Exception {
	private static final long serialVersionUID = 3046371524126471688L;

	public SpielBeendetException() { super(); }
}
