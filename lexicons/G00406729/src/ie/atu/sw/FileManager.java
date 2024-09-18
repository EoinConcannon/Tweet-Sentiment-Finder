package ie.atu.sw;

import java.io.File;

/**
 * 
 * This is the abstract class object for the Runner class. 
 * This object store the file and file path name. 
 * It contains auto-generated getters and setters.
 * 
 * @author Eoin Concannon
 *
 */
public abstract class FileManager {
	private File myFile;
	private String path;

	public File getFile() {
		return myFile;
	}

	public void setFile(File myFile) {
		this.myFile = myFile;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
