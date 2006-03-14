/*
 * SpaceRule.java
 *
 * Created on April 25, 2003, 2:20 PM
 */
package plugin.doomsdaybook.util;

import java.util.ArrayList;

/**
 *
 * @author  devon
 */
public class SpaceRule implements DataElement
{
	ArrayList retList = new ArrayList();

	/** Creates a new instance of SpaceRule */
	public SpaceRule()
	{
		retList.add(new DataValue(" "));
	}

	public ArrayList getData()
	{
		return retList;
	}

	public ArrayList getData(int choice)
	{
		return retList;
	}

	public String getId()
	{
		return " ";
	}

	public ArrayList getLastData()
	{
		return retList;
	}

	public String getTitle()
	{
		return null;
	}

	public int getWeight()
	{
		return 1;
	}

	public void trimToSize()
	{
	    // TODO:  Method doesn't do anything?
	}
}
