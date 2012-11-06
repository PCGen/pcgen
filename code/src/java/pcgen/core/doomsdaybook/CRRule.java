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

    @Override
	public ArrayList<DataValue> getData()
	{
		return retList;
	}

    @Override
	public ArrayList<DataValue> getData(int choice)
	{
		return retList;
	}

    @Override
	public String getId()
	{
		return "\n";
	}

    @Override
	public ArrayList<DataValue> getLastData()
	{
		return retList;
	}

    @Override
	public String getTitle()
	{
		return null;
	}

    @Override
	public int getWeight()
	{
		return 1;
	}

    @Override
	public void trimToSize()
	{
		// TODO:  Method doesn't do anything?
	}
}
