/*
 * XMLFilter.java
 *
 * Created on April 25, 2003, 5:41 PM
 */
package plugin.overland.gui;

/**
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
	 * Return true if filter matches *.xml
	 * 
	 * @param file 
	 * @param str 
	 * @return true if filter matches 
	 */
	public boolean accept(java.io.File file, String str)
	{
		return str.matches(".*\\.xml$"); //$NON-NLS-1$
	}
}
