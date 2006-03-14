/*
 * XMLFilter.java
 *
 * Created on April 25, 2003, 5:41 PM
 */
package plugin.doomsdaybook.gui;


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

	public boolean accept(java.io.File file, String str)
	{
		return str.matches(".*\\.xml$");
	}
}
