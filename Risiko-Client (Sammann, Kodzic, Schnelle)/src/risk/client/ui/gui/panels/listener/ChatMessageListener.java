package risk.client.ui.gui.panels.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import risk.client.ui.gui.RiskClientGUI;
import risk.client.net.ClientEngineNetwork;
import risk.client.ui.gui.panels.ChatPanel;
import risk.commons.valueobjects.ChatMessage;

/**
 * Wird auf den Button und das Textfeld im ChatPanel gelegt und ist für
 * den Versand der neuen Chat-Nachricht zuständig.
 * @author Marcel
 */
public class ChatMessageListener implements ActionListener {
	/** ChatPanel, für das der Listener arbeitet */
	private ChatPanel c;
	
	/** Konstruktor
	 * @param c	ChatPanel-Referenz für das Einsehen von eingebenen Texten im Eingabefeld */
	public ChatMessageListener(ChatPanel c) { this.c = c; }
	
	/** Überschriebene ActionPerformed-Methode. Sie ist dafür da, zu prüfen, ob ein gültiger Text in
	 * das Eingabefeld des ChatPanels eingetragen worden ist, und sendet ihn ggf. über die Client-
	 * Server-Schnittstelle an den Server, welcher die neue Nachricht dann an alle Spieler
	 * distributiert. */
	public void actionPerformed(ActionEvent e) {
		// Zuerst prüfen, ob überhaupt etwas gültiges eingegeben wurde ("" wird nicht übertragen)
		String eingabe = c.getEingabe();
		// Nur wenn die Eingabe nicht leer ist, ChatMessage-Objekt wegschicken
		if (!eingabe.isEmpty())
			ClientEngineNetwork.getEngine().sendChatMessage(new ChatMessage(RiskClientGUI.getSpieler(), eingabe));
		// Auf jeden Fall aber das Feld leeren
		c.eraseEingabefeld();
	}
}