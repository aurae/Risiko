package risk.client.ui.gui.comp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

/**
 * Instanzen dieser Klasse werden im Programmablauf dann gebraucht,
 * wenn generelle Nachrichten auf dem Bildschirm ausgegeben werden sollen.
 * @author Yannik
 */
public class MessageDialog extends RiskDialog {
	private static final long serialVersionUID = 775521544262648898L;
	
	/** Inhalts-Panel */
	private final JPanel contentPanel = new JPanel();

	/** Konstruktor mit Superframe-Einbindung
	 * @param f		übergeordneter JFrame
	 * @param s		Text für die Nachrichtenbox
	 * @param title	Titel */
	public MessageDialog(JFrame f, String s, String title) {
		super(f, new Dimension(400,120));
		// Generelle Einstellungen
		setTitle(title);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		// Textarea
			JTextArea txtrContenttext = new JTextArea();
			txtrContenttext.setEditable(false);
			txtrContenttext.setWrapStyleWord(true);
			txtrContenttext.setLineWrap(true);
			txtrContenttext.setFont(new Font("Tahoma", Font.PLAIN, 13));
			txtrContenttext.setBackground(null);
			txtrContenttext.setBorder(null);
			txtrContenttext.setText(s);
			contentPanel.add(txtrContenttext);
		// Unterer Panel-Teil für den Bestätigen-Button
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BorderLayout(0, 0));
		// Bestätigen-Button
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent a) {
						MessageDialog.this.dispose();
					}
				});
		// Trenner 1
				JLabel label = new JLabel("                                                      ");
				buttonPane.add(label, BorderLayout.WEST);
		// Trenner 2
			    
				JLabel label2 =new JLabel("                                                      ");
				buttonPane.add(label2, BorderLayout.EAST);
		// Sichtbar schalten
		setVisible(true);
		setModal(true);
	}

}
