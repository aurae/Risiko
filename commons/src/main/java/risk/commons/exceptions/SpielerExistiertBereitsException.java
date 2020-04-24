package risk.commons.exceptions;

/**
 * Diese Klasse wird dann instanziiert, wenn ein Spieler sich zur Partie
 * anmelden möchte, der den gleichen Namen trägt wie ein bereits Eingeloggter.
 * @author Marcel
 */
public class SpielerExistiertBereitsException extends Exception {
	private static final long serialVersionUID = 1L;

	public SpielerExistiertBereitsException(String name) { super("Spieler "+name+" existiert bereits!"); }
}
