package risk.commons;

import com.apple.eawt.Application;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class RiskUtils {

  private static final ResourceLoader resources = new ResourceLoader(RiskUtils.class.getClassLoader());

  private RiskUtils() {
    //no instance
  }

  public static File loadResourceFile(String path) throws IOException {
    return resources.loadFile(path);
  }

  public static URL loadResourceURL(String path) throws IOException {
    return resources.loadURL(path);
  }

  public static void setIconImage(JFrame frame, String path) throws IOException {
    URL url = loadResourceURL(path);
    ImageIcon icon = new ImageIcon(url);
    Image image = icon.getImage();
    frame.setIconImage(image);

    // On macOS, apply it to the dock as well
    try {
      Application.getApplication().setDockIconImage(image);
    } catch (Exception ignored) {
    }
  }
}
