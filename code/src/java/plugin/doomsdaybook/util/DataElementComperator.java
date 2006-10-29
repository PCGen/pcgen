/* DataElementComperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package plugin.doomsdaybook.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * DataElementComperator
 */
public class DataElementComperator implements Comparator<DataElement>, Serializable
{
	public int compare(DataElement o1, DataElement o2)
	{
		return o1.getTitle().compareTo(o2.getTitle());
	}
}
