package risk.client.ui.gui.comp;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import risk.commons.valueobjects.Land;

/**
 * Instanzen dieser Klasse werden dann gebraucht, wenn ein Spieler einen
 * Bewegungsbefehl in der Armeen-Bewegen-Phase aussprechen möchte.
 * Vom Aufbau her ähnelt ein MoveDialog-Objekt einem Objekt der AngriffsDialog-
 * Klasse; Beschriftungen sind teilweise aber anders.
 * @author Yannik
 */
public class MoveDialog extends RiskDialog implements ChangeListener {
	private static final long serialVersionUID = 8104085654530371866L;
	
	/** Konstante für den Nachziehen-Mode. Per Default ist der normale Bewegungs-Mode eingestellt.
	 *  Diese Konstante regelt nur die Darstellung der Texte im MoveDialog */
	public static final int NACHZIEHEN = 1248214;
	
	/** Textfeld für die Anzeige, wie viele Armeen über den Slider ausgewählt worden sind */
	private JTextField textField;			
	/** Label für das Land, von dem aus angegriffen wird */
	private JLabel lblVonLand;				
	/** Label für das Land, das angegriffen wird */
	private JLabel lblNachLand;				
	/** Label für Informationen zum Land, von dem aus angegriffen wird */
	private JLabel lblVonInfo;				
	/** Label für Informationen zum Land, das angegriffen wird */
	private JLabel lblNachInfo;				
	/** Slider-Komponente zum Einstellen der teilnehmenden Armeen (Angreifer oder Verteidiger) */
	private JSlider slider;					
	/** Button zum Bestätigen des Zuges */
	private JButton okButton;				
	/** Button zum Widerrufen des Zuges */
	private JButton cancelButton;			
	
	/** Ausgewählte Armeenzahl. Standard-Wert: 1 */
	private int selectedArmeen = 1;			
	
	public MoveDialog(JFrame frame, Land von, Land nach, int mode) {
		super(frame);
		System.out.println("Movedialogkonstruktor beginnt");
		// Inhalte des Dialogs
		// Titel
			setTitle("");
		// Close-Operation gemäß des Modes setzen (das Verteidigen-Fenster kann man nicht einfach so schließen!)
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		// Layouting des Hintergrunds
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(null);
		// Befehl-Label
			JLabel lblBefehl = new JLabel(mode == MoveDialog.NACHZIEHEN ? 
					"Wie viele Armeen sollen nachrücken?" : "Der Bewegungsbefehl lautet:");
			lblBefehl.setBounds(10, 13, 414, 14);
			contentPanel.add(lblBefehl);
		// Separierer
			JSeparator separator = new JSeparator();
			separator.setBounds(10, 36, 414, 2);
			contentPanel.add(separator);
		// Von-Label
			JLabel lblVon = new JLabel("von");
			lblVon.setBounds(45, 58, 32, 14);
			contentPanel.add(lblVon);
		// Von-Name (wird aus dem übergebenen Von-Land gezogen)
			Font f = new Font("Tahoma", Font.BOLD, 15);
			lblVonLand = new JLabel(von.getName());
			lblVonLand.setFont(f);
			lblVonLand.setBounds(76, 50, 170, 28);
			contentPanel.add(lblVonLand);
		// Nach-Label
			JLabel lblNach = new JLabel("nach");
			lblNach.setBounds(37, 92, 32, 14);
			contentPanel.add(lblNach);
		// Nach-Name (wird aus dem übergebenen Nach-Land gezogen)
			lblNachLand = new JLabel(nach.getName());
			lblNachLand.setFont(f);
			lblNachLand.setBounds(76, 84, 170, 28);
			contentPanel.add(lblNachLand);
		// Von-Info-Label (wird aus dem übergebenen Von-Land gezogen)
			lblVonInfo = new JLabel("(St\u00E4rke: "+von.getStaerke() + ")");
			lblVonInfo.setBounds(256, 58, 168, 14);
			contentPanel.add(lblVonInfo);
		// Nach-Info-Label (wird aus dem übergebenen Nach-Land gezogen)
			lblNachInfo = new JLabel("(St\u00E4rke: "+nach.getStaerke() + ")");
			lblNachInfo.setBounds(256, 92, 168, 14);
			contentPanel.add(lblNachInfo);
		// Separierer
			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(10, 122, 414, 2);
			contentPanel.add(separator_1);
		// Textfeld, das die Stärke der eingegebenen Armeen anzeigt (gemäß des Sliders)
			textField = new JTextField();
			textField.setBounds(227, 156, 32, 20);
			textField.setColumns(10);
			textField.setEditable(false);
			textField.setText("1");
			contentPanel.add(textField);
		// Text über dem Slider, je nach Mode für Angreifer oder Verteidiger
			JLabel lblWieVieleArmeen = new JLabel("Wie viele Armeen sollen bewegt werden?");
			lblWieVieleArmeen.setBounds(10, 134, 249, 14);
			contentPanel.add(lblWieVieleArmeen);
		// Slider für das Einstellen der Einheiten
			int minEinheiten = (mode == MoveDialog.NACHZIEHEN ? 0 : 1);
			slider = new JSlider(JSlider.HORIZONTAL, minEinheiten, von.getStaerke() - 1, minEinheiten);
			slider.setBounds(10, 156, 200, 47);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setSnapToTicks(true);
			slider.addChangeListener(this);
			contentPanel.add(slider);
		// Button für Angriff bzw. Verteidigung
			okButton = new JButton("Bewegung!");
			okButton.setBounds(266, 135, 158, 68);
			contentPanel.add(okButton);
			okButton.setActionCommand("Confirm");
			getRootPane().setDefaultButton(okButton);
		// Abbrechen-Button (nur bei Angriff-Mode sichtbar)
		cancelButton = new JButton("Doch nicht...");
		cancelButton.setBounds(266, 214, 158, 27);
		cancelButton.setActionCommand("Cancel");
		contentPanel.add(cancelButton);
		
		setModal(mode == MoveDialog.NACHZIEHEN ? false : true);
		
		System.out.println("Movedialogkonstruktor ist fertig");
	}
	
	/** Konstruktor mit Land-Referenzen und ohne Mode-Angabe
	 * @param frame	Übergeordneter Frame
	 * @param von	Land-Referenz, von dem aus bewegt wird
	 * @param nach	Land, in das bewegt wird */
	public MoveDialog(JFrame frame, Land von, Land nach) {
		this(frame,von,nach,0);
	}

	/** Änderung am Slider wird hier bemerkt und behandelt. In diesem Fall wird die Zahl ausgewählter Armeen verändert */
	public void stateChanged(ChangeEvent evt) {
		JSlider sl = (JSlider) evt.getSource();
	    if (!sl.getValueIsAdjusting()) {
	        selectedArmeen = (int)sl.getValue();
	        textField.setText(""+selectedArmeen);
	    }
	}
	
	/** Ausgeben beider Buttons im AngriffsDialog als Array. [0] hat den OK-, [1] den Cancelbutton (für VERTEIDIGEN-Mode nicht wichtig)
	 * @return	Array mit zwei Elementen */
	public JButton[] getButtons() {
		JButton[] arr = new JButton[2];
		arr[0] = okButton;
		arr[1] = cancelButton;
		return arr;
	}

	/** Leere Implementierung */
	public void wartenLassen() { }
	
	/** Ausgabe der ausgewählten Armeen für das MainPanel */
	public int getInput() { return selectedArmeen; }
}
