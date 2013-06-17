package risk.commons.valueobjects;

import java.io.Serializable;

/**
 * Repräsentant für eine Nachricht eines Spielers an die anderen über das Chat-Panel.
 * Sie beinhaltet nur Sender und Text
 * @author Marcel
 */
public class ChatMessage implements Serializable {
	private static final long serialVersionUID = -2044807075548716937L;
	
	/** Sender-String der Nachricht */
	private String sender;
	/** Nachricht */
	private String text;
	
	/** Konstruktor
	 * @param s		Spielerobjekt, das eine Nachricht senden will
	 * @param text	Text seiner Nachricht */
	public ChatMessage(Spieler s, String text) {
		this.sender = s.getName();
		this.text = text;
	}
	
	/** Rückgabe der formatierten Nachricht */
	public String get() { return "[" + sender + "]: " + text + "\n"; }
}
