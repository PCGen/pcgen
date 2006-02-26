package gmgen.io;

import java.io.File;

/**
 * This is the interface for the classes that will be exporting or saving to a
 * file.<br>
 * Created on March 3, 2003
 * @author Expires 2003
 * @version 2.10
 */
public abstract interface ExportHandler
{
	/**
	 * This method will be overridden in the using classes to save
	 * the class to a file.
	 * @param path the file and path which will be used to save.
	 */
	public abstract void export(File path);
}
