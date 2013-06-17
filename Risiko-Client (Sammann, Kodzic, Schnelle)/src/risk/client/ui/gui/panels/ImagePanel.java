package risk.client.ui.gui.panels;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Ein selbst definiertes JPanel mit der besonderen Eigenschaft, Bilddateien
 * halten zu können. Im Center-Bereich des Mainframes gibt es ein solches
 * ImagePanel, in dem die Weltkarte und (unsichtbar) die Maske dargestellt
 * werden.
 * @author Yannik
 */
public class ImagePanel extends DefaultPanel {
	private static final long serialVersionUID = 4857809766266844401L;
	
	/** Bilddatei */
	private BufferedImage image;

	/** Konstruktor
	 * @param quelle	Dateiname der Bilddatei (ausgehend vom Quellverzeichnis)
	 * @throws IOException	wenn die Bilddatei nicht gefunden oder gelesen werden konnte */
    public ImagePanel(String quelle) throws IOException {
    	super();
    	// Datei mit dem Dateinamen erzeugen, auf Vorhandensein prüfen und dann einlesen lassen
    	File f = new File(quelle);
    	if (f.exists())
    		image = ImageIO.read(f);
    	else
    		throw new IOException("Die Bilddatei " + quelle + " konnte nicht gefunden werden!");
    	repaint();
    }

    /** paint()-Methode zum Zeichnen des Panels */
    public void paint(Graphics g) {
    	// Zuerst das Bild zeichnen lassen...
    	g.drawImage(image, 0, 0, this);
    	// Am Schluss alle "Kinder" zeichnen (ArmeenButtons etc.)
        this.paintChildren(g);
    }
    
    /** Rückgabe der Bilddatei
     * @return	BufferedImage mit Bilddatei */
    public BufferedImage getImage() {
    	return image;
    }
    
    /** Rückgabe des Farbwertes an einem bestimmten Punkt in Hex-Schreibweise als String
     * @param p	Point mit den Koordinaten des zu suchenden Punktes in der Maske
     * @return	String à la "0xABCDEF", der den Hex-Farbwert an der Stelle repräsentiert */
    public String getMaskColorAt(Point p) {
    	if (p != null) {
    		return Integer.toHexString( image.getRGB(p.x,p.y) & 0x00ffffff );
    	} else return "FFFFFF";
    }
}