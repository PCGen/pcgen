/*
 * SpaceRule.java
 *
 * Created on April 25, 2003, 2:20 PM
 */
package pcgen.core.doomsdaybook;

import java.util.ArrayList;

/**
 *
 * @author  devon
 */
public class CRRule implements DataElement
{
	ArrayList<DataValue> retList = new ArrayList<DataValue>();

	/** Creates a new instance of SpaceRule */
	public CRRule()
	{
		retList.add(new DataValue(" "));
	}

	public ArrayList<DataValue> getData()
	{
		return retList;
	}

	public ArrayList<DataValue> getData(int choice)
	{
		return retList;
	}

	public String getId()
	{
		return "\n";
	}

	public ArrayList<DataValue> getLastData()
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
