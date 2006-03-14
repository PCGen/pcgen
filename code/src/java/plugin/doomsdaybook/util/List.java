/*
 * List.java
 *
 * Created on April 24, 2003, 4:30 PM
 */
package plugin.doomsdaybook.util;

import gmgen.plugin.Dice;

import java.util.ArrayList;

/**
 *
 * @author  devon
 */
public class List extends ArrayList implements DataElement
{
	ArrayList retList = new ArrayList();
	String id;
	String title;
	VariableHashMap allVars;
	int weight;

	/** Creates a new instance of List
	 * @param allVars
	 */
	public List(VariableHashMap allVars)
	{
		this(allVars, "", "", 1);
	}

	public List(VariableHashMap allVars, String title, String id)
	{
		this(allVars, title, id, 1);
	}

	public List(VariableHashMap allVars, String title, String id, int weight)
	{
		this.allVars = allVars;
		this.title = title;
		this.id = id;
		this.weight = weight;
	}

	public ArrayList getData()
	{
		retList.clear();

		int rangeTop = getRange();
		int modifier;

		try
		{
			modifier = Integer.parseInt(allVars.getVal(getId() + "modifier"));
		}
		catch (Exception e)
		{
			modifier = 0;
		}

		// Determine which entry to choose
		Dice die = new Dice(1, rangeTop, 0);
		int choice = die.roll();
		choice += modifier;
		choice = (choice < 0) ? rangeTop : choice;

		//select the detail to return
		int aWeight = 0;

		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for (int i = 0; i < this.size(); i++)
		{
			WeightedDataValue chkValue = (WeightedDataValue) get(i);
			int valueWeight = chkValue.getWeight();

			if (valueWeight > 0)
			{
				aWeight += valueWeight;

				if (aWeight >= choice)
				{
					retList.add(chkValue);

					break;
				}
			}
		}

		return retList;
	}

	public ArrayList getData(int choice)
	{
		retList.clear();

		//select the detail to return
		int aWeight = 0;

		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for (int i = 0; i < this.size(); i++)
		{
			WeightedDataValue chkValue = (WeightedDataValue) get(i);
			int valueWeight = chkValue.getWeight();

			if (valueWeight > 0)
			{
				aWeight += valueWeight;

				if (aWeight >= choice)
				{
					retList.add(chkValue);

					break;
				}
			}
		}

		return retList;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getId()
	{
		return id;
	}

	public ArrayList getLastData()
	{
		return retList;
	}

	public int getRange()
	{
		int rangeTop = 0;

		for (int i = 0; i < this.size(); i++)
		{
			WeightedDataValue value = (WeightedDataValue) get(i);
			rangeTop += value.getWeight();
		}

		if (rangeTop <= 0)
		{ //the die will nullpointer if it is not at least 1
			rangeTop = 1;
		}

		return rangeTop;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getTitle()
	{
		return title;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	public int getWeight()
	{
		return weight;
	}
}
