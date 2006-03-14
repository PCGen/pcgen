/* DataElementComperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package plugin.doomsdaybook.util;

import java.util.Comparator;

/**
 * DataElementComperator
 */
public class DataElementComperator implements Comparator
{
	public int compare(Object o1, Object o2)
	{
		DataElement i1 = (DataElement) o1;
		DataElement i2 = (DataElement) o2;

		return i1.getTitle().compareTo(i2.getTitle());
	}
}
