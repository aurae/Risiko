package risk.commons.exceptions;

/**
 * Eine Instanz dieser Klasse wird dann geworfen, wenn die Suchen-Methode der LandVerwaltung
 * ein Land nicht anhand seiner FarbMap oder seines Namens finden kann.
 * @author Marcel
 */
public class KeinLandException extends Exception {
	private static final long serialVersionUID = 1L;

	public KeinLandException(String s) { super("Es existiert kein Land namens "+s+"!"); }
}
