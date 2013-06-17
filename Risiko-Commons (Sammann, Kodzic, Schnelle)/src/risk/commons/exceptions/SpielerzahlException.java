package risk.commons.exceptions;

/**
 * Eine SpielerzahlException wird dann geworfen, wenn sich bspw. ein siebter Spieler
 * zu registrieren versucht, oder das Spiel mit weniger als drei Teilnehmern gestartet
 * wird (Letzteres ist in der GUI-Version nicht mehr gebraucht).
 * @author Marcel
 */
public class SpielerzahlException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/** Konstante, die angibt, dass diese SpielerzahlException wegen zu vielen Spielern geworfen worden ist */
	public static final int ZUVIEL = 101;
	/** Konstante, die angibt, dass diese SpielerzahlException wegen zu wenigen Spielern geworfen worden ist */
	public static final int ZUWENIG = 100;
	
	public SpielerzahlException(int mode) {
		super(mode == ZUVIEL ? "Es sind bereits sechs Spieler registriert!" :
			(mode == ZUWENIG ? "Es müssen mindestens drei Spieler registriert sein!" : "Unbekannter Mode"));
	}
}
