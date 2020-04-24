package risk.client.ui.gui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import net.miginfocom.swing.MigLayout;
import risk.client.ui.gui.panels.listener.ChatMessageListener;
import risk.commons.valueobjects.ChatMessage;

/**
 * Dieses Panel wird im Osten des South-Panels angezeigt und beinhaltet eine TextArea und ein TextField,
 * über das der Client-Spieler mit seinen Mitspielern kommunizieren kann.
 * @author Yannik
 */
public class ChatPanel extends JPanel {
	private static final long serialVersionUID = -2516145855612575689L;
	
	/** Textfeld für Benutzereingabe */
	private JTextField textMessage;
	/** TextPane mit Nachrichtenverlauf */
	private JTextPane textHistory;
	/** Sende-Button für eine neue Nachricht*/
	private JButton btnSenden;

	/** Konstruktor ohne Parameter */
	public ChatPanel() {
		// Allg. Infos
		setBounds(new Rectangle(0, 0, 336, 100));
		setLayout(new BorderLayout(0, 0));
		setBackground(null);
		
		// Center-Teil-ScrollPane
		JScrollPane center = new JScrollPane();
		add(center, BorderLayout.CENTER);
		// Center-Teil-Textarea
		textHistory = new JTextPane();
		textHistory.setFont(new Font("Tahoma", Font.PLAIN, 10));
		textHistory.setEditable(false);
		center.setViewportView(textHistory);
		
		// South-Teil-Panel
		JPanel south = new JPanel();
		add(south, BorderLayout.SOUTH);
		south.setBackground(null);
		south.setLayout(new MigLayout("", "[339.00px][168px]", "[23px]"));
		// South-Teil-Textfeld
		textMessage = new JTextField();
		south.add(textMessage, "cell 0 0,grow");
		textMessage.setColumns(10);
		// South-Teil-Button
		btnSenden = new JButton("Senden");
		south.add(btnSenden, "cell 1 0,grow");

		// Listener
		btnSenden.addActionListener(new ChatMessageListener(this));
		textMessage.addActionListener(new ChatMessageListener(this));
	}
	
	/** Update-Methode. Wird vom MainPanel aus aufgerufen, wenn eine neue Chatnachricht angekommen ist */
	public void updateBox(ChatMessage msg) {
		// Nachrichtentext anhängen
		textHistory.setText(textHistory.getText() + msg.get());
		// "Auto-Scrollen"
		textHistory.setCaretPosition(textHistory.getText().length());
		// Neu zeichnen
		textHistory.repaint();
	}
	
	/** Inhalt des Textfeldes ausgeben (für Listener) */
	public String getEingabe() { return textMessage.getText(); }
	
	/** Eingabefeld löschen lassen */
	public void eraseEingabefeld() { textMessage.setText(""); }
}
