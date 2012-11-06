/* DataElementComperator - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package pcgen.core.doomsdaybook;

import java.io.Serializable;
import java.util.Comparator;

/**
 * DataElementComperator
 */
public class DataElementComperator implements Comparator<DataElement>,
		Serializable
{
    @Override
	public int compare(DataElement o1, DataElement o2)
	{
		return o1.getTitle().compareTo(o2.getTitle());
	}
}
