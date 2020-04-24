package risk.commons;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public final class ResourceLoader {

  private final ClassLoader classLoader;

  public ResourceLoader(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public File loadFile(String path) throws IOException {
    URL resource = loadURL(path);
    String filePath = resource.getFile();
    if (filePath == null) {
      throw new IOException("Cannot load resource at path '" + path + "'");
    }

    return new File(filePath);
  }

  public URL loadURL(String path) throws IOException {
    URL resource = classLoader.getResource(path);
    if (resource == null) {
      throw new IOException("Cannot load resource at path '" + path + "'");
    }

    return resource;
  }
}
