/*
 * XMLFilter.java
 *
 * Created on April 25, 2003, 5:41 PM
 */
package pcgen.gui2.doomsdaybook;

/**
 *
 * @author  devon
 */
public class XMLFilter implements java.io.FilenameFilter
{
	/** Creates a new instance of XMLFilter */
	public XMLFilter()
	{
		// Empty Constructor
	}

	/**
	 * Returns true if file matches *.xml
	 * 
	 * @param file 
	 * @param str 
	 * @return true if filter matches *.xml
	 */
	public boolean accept(java.io.File file, String str)
	{
		return str.matches(".*\\.xml$");
	}
}
