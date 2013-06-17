package risk.client.ui.gui.panels.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import risk.client.ui.gui.RiskClientGUI;
import risk.client.net.ClientEngineNetwork;
import risk.client.ui.gui.panels.ChatPanel;
import risk.commons.valueobjects.ChatMessage;

/**
 * Wird auf den Button und das Textfeld im ChatPanel gelegt und ist f�r
 * den Versand der neuen Chat-Nachricht zust�ndig.
 * @author Marcel
 */
public class ChatMessageListener implements ActionListener {
	/** ChatPanel, f�r das der Listener arbeitet */
	private ChatPanel c;
	
	/** Konstruktor
	 * @param c	ChatPanel-Referenz f�r das Einsehen von eingebenen Texten im Eingabefeld */
	public ChatMessageListener(ChatPanel c) { this.c = c; }
	
	/** �berschriebene ActionPerformed-Methode. Sie ist daf�r da, zu pr�fen, ob ein g�ltiger Text in
	 * das Eingabefeld des ChatPanels eingetragen worden ist, und sendet ihn ggf. �ber die Client-
	 * Server-Schnittstelle an den Server, welcher die neue Nachricht dann an alle Spieler
	 * distributiert. */
	public void actionPerformed(ActionEvent e) {
		// Zuerst pr�fen, ob �berhaupt etwas g�ltiges eingegeben wurde ("" wird nicht �bertragen)
		String eingabe = c.getEingabe();
		// Nur wenn die Eingabe nicht leer ist, ChatMessage-Objekt wegschicken
		if (!eingabe.isEmpty())
			ClientEngineNetwork.getEngine().sendChatMessage(new ChatMessage(RiskClientGUI.getSpieler(), eingabe));
		// Auf jeden Fall aber das Feld leeren
		c.eraseEingabefeld();
	}
}