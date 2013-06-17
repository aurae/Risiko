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

import risk.commons.valueobjects.Angriff;
import risk.commons.valueobjects.Land;

/**
 * Ein Objekt dieser Klasse wird immer dann vom MainPanel ben�tigt, wenn ein Spieler
 * einen Angriff durchf�hren m�chte, oder wenn ein Spieler von einem anderen angegriffen
 * worden ist und er sich verteidigen muss. In dem Dialog werden Informationen zum
 * ausgew�hlten Angriff dargestellt und mittels verschiedener GUI-Komponenten
 * Einstellungsm�glichkeiten f�r den Benutzer gew�hrt.
 * @author Yannik
 *
 */
public class AngriffsDialog extends RiskDialog implements ChangeListener {
	private static final long serialVersionUID = 8104085654530371866L;

	/** Konstante, die definiert, dass der AngriffsDialog in Zusammenhang mit
	 * einem ANGRIFF dargestellt wird */
	public static final int ANGRIFF = 123712547;
	/** Konstante, die definiert, dass der AngriffsDialog in Zusammenhang mit
	 * einer VERTEIDIGUNG dargestellt wird */
	public static final int VERTEIDIGUNG = 1241267;
	
	// GUI-Komponenten
	/** Textfeld f�r die Anzeige, wie viele Armeen �ber den Slider ausgew�hlt worden sind */
	private JTextField textField;			
	/** Label f�r das Land, von dem aus angegriffen wird */
	private JLabel lblVonLand;				
	/** Label f�r das Land, das angegriffen wird */
	private JLabel lblNachLand;				
	/** Label f�r Informationen zum Land, von dem aus angegriffen wird */
	private JLabel lblVonInfo;				
	/** Label f�r Informationen zum Land, das angegriffen wird */
	private JLabel lblNachInfo;				
	/** Slider-Komponente zum Einstellen der teilnehmenden Armeen (Angreifer oder Verteidiger) */
	private JSlider slider;					
	/** Button zum Best�tigen des Zuges */
	private JButton okButton;				
	/** Button zum Widerrufen des Zuges */
	private JButton cancelButton;			
	
	/** Ausgew�hlte Armeenzahl. Standard-Wert: 1 */
	private int selectedArmeen = 1;			
	/** "Warten-auf-Reaktion"-Label */
	private JLabel lblWartenAufReaktion;	
	
	/** Konstruktor mit Land-Referenzen
	 * @param frame	�bergeordneter Frame
	 * @param von	Land-Referenz, von dem aus angegriffen wird
	 * @param nach	Land, das angegriffen wird
	 * @param mode	Eine Konstante dieser Klasse, die angibt, ob Angriffs- oder Verteidigungsfall vorliegt */
	public AngriffsDialog(JFrame frame, Land von, Land nach, int mode) {
		super(frame);
		// Inhalte des Dialogs
		// Titel
			String title = (mode == AngriffsDialog.ANGRIFF) ? "Einen Angriff durchf\u00FChren" : "Du bist angegriffen worden!";
			setTitle(title);
		// Close-Operation gem�� des Modes setzen (das Verteidigen-Fenster kann man nicht einfach so schlie�en!)
			int operation = (mode == AngriffsDialog.ANGRIFF) ? JDialog.DISPOSE_ON_CLOSE : JDialog.DO_NOTHING_ON_CLOSE;
			setDefaultCloseOperation(operation);
		// Layouting des Hintergrunds
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(null);
		// Befehl-Label
			JLabel lblBefehl = new JLabel("Der Angriffsbefehl lautet:");
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
		// Von-Name (wird aus dem �bergebenen Von-Land gezogen)
			Font f = new Font("Tahoma", Font.BOLD, 15);
			lblVonLand = new JLabel(von.getName());
			lblVonLand.setFont(f);
			lblVonLand.setBounds(76, 50, 170, 28);
			contentPanel.add(lblVonLand);
		// Nach-Label
			JLabel lblNach = new JLabel("nach");
			lblNach.setBounds(37, 92, 32, 14);
			contentPanel.add(lblNach);
		// Nach-Name (wird aus dem �bergebenen Nach-Land gezogen)
			lblNachLand = new JLabel(nach.getName());
			lblNachLand.setFont(f);
			lblNachLand.setBounds(76, 84, 170, 28);
			contentPanel.add(lblNachLand);
		// Von-Info-Label (wird aus dem �bergebenen Von-Land gezogen)
			lblVonInfo = new JLabel("(St\u00E4rke: "+von.getStaerke()+", Spieler: "+von.getSpieler()+")");
			lblVonInfo.setBounds(256, 58, 168, 14);
			contentPanel.add(lblVonInfo);
		// Nach-Info-Label (wird aus dem �bergebenen Nach-Land gezogen)
			lblNachInfo = new JLabel("(St\u00E4rke: "+nach.getStaerke()+", Spieler: "+nach.getSpieler()+")");
			lblNachInfo.setBounds(256, 92, 168, 14);
			contentPanel.add(lblNachInfo);
		// Separierer
			JSeparator separator_1 = new JSeparator();
			separator_1.setBounds(10, 122, 414, 2);
			contentPanel.add(separator_1);
		// Textfeld, das die St�rke der eingegebenen Armeen anzeigt (gem�� des Sliders)
			textField = new JTextField();
			textField.setBounds(227, 156, 32, 20);
			textField.setColumns(10);
			textField.setEditable(false);
			textField.setText("1");
			contentPanel.add(textField);
		// Text �ber dem Slider, je nach Mode f�r Angreifer oder Verteidiger
			String armeencnt = (mode == AngriffsDialog.ANGRIFF) ? "Wie viele Armeen sollen k\u00E4mpfen?" : "Wie viele Armeen sollen verteidigen?";
			JLabel lblWieVieleArmeen = new JLabel(armeencnt);
			lblWieVieleArmeen.setBounds(10, 134, 249, 14);
			contentPanel.add(lblWieVieleArmeen);
		// Maximal benutzbare Armeen ausrechnen
		int maximum = (mode == AngriffsDialog.ANGRIFF) ? 
				// wenn der Mode ANGRIFF ist, dann nachschauen, ob da mehr als drei Armeen stehen. Denn so viel d�rfen maximal k�mpfen
				( (von.getStaerke() - 1 > 3) ? 3 : von.getStaerke() - 1 )
				// ansonsten ist der Mode VERTEIDIGUNG, Maximum: 2
				: ( (nach.getStaerke() > 2) ? 2 : nach.getStaerke() );
		// Slider f�r das Einstellen der Einheiten
			slider = new JSlider(JSlider.HORIZONTAL, 1, maximum, 1);
			slider.setBounds(10, 156, 200, 47);
			slider.setMajorTickSpacing(2);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setSnapToTicks(true);
			slider.addChangeListener(this);
			contentPanel.add(slider);
		// Button f�r Angriff bzw. Verteidigung
			String buttonText = (mode == AngriffsDialog.ANGRIFF) ? "Angriff!" : "Abwehr!";
			okButton = new JButton(buttonText);
			okButton.setBounds(266, 135, 158, 68);
			contentPanel.add(okButton);
			okButton.setActionCommand("Confirm");
			getRootPane().setDefaultButton(okButton);
		// Warten-auf-Reaktion-Label (nur bei Angriff-Mode sichtbar)
			lblWartenAufReaktion = new JLabel("");
			lblWartenAufReaktion.setBounds(10, 214, 200, 28);
			contentPanel.add(lblWartenAufReaktion);
		// Abbrechen-Button (nur bei Angriff-Mode sichtbar)
		cancelButton = new JButton("Doch nicht...");
		cancelButton.setBounds(266, 214, 158, 27);
		if (mode == AngriffsDialog.ANGRIFF)
			contentPanel.add(cancelButton);
		cancelButton.setActionCommand("Cancel");
		
		setModal(true);
	}
	
	/** Konstruktor mit Angriffsobjekt
	 * @param frame	�bergeordneter Frame
	 * @param a		Angriffsobjekt mit von- und nach-Referenzen auf Landobjekten
	 * @param mode	Eine Konstante dieser Klasse, die angibt, ob Angriffs- oder Verteidigungsfall vorliegt */
	public AngriffsDialog(JFrame frame, Angriff a, int mode) { this(frame, a.getVon(), a.getNach(), mode); }

	/** �nderung am Slider wird hier bemerkt und behandelt. In diesem Fall wird die Zahl ausgew�hlter Armeen ver�ndert */
	public void stateChanged(ChangeEvent evt) {
		JSlider sl = (JSlider) evt.getSource();
	    if (!sl.getValueIsAdjusting()) {
	        selectedArmeen = (int)sl.getValue();
	        textField.setText(""+selectedArmeen);
	    }
	}
	
	/** Ausgeben beider Buttons im AngriffsDialog als Array. [0] hat den OK-, [1] den Cancelbutton (f�r VERTEIDIGEN-Mode nicht wichtig)
	 * @return	Array mit zwei Elementen */
	public JButton[] getButtons() {
		JButton[] arr = new JButton[2];
		arr[0] = okButton;
		arr[1] = cancelButton;
		return arr;
	}

	/** Methode, die auf Antwortet warten l�sst (f�r Angriffs-Modus) */
	public void wartenLassen() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		lblWartenAufReaktion.setText("Warten auf Reaktion...");
		okButton.setEnabled(false);
		cancelButton.setEnabled(false);
	}
	
	/** Ausgabe der ausgew�hlten Armeen f�r das MainPanel */
	public int getInput() { return selectedArmeen; }
}
