/*
 * List.java
 *
 * Created on April 24, 2003, 4:30 PM
 */
package pcgen.core.doomsdaybook;

import gmgen.plugin.Dice;

import java.util.ArrayList;

/**
 *
 * @author  devon
 */
public class DDList extends ArrayList<WeightedDataValue> implements DataElement
{
	ArrayList<DataValue> retList = new ArrayList<DataValue>();
	String id;
	String title;
	VariableHashMap allVars;
	int weight;

	/** Creates a new instance of List
	 * @param allVars
	 */
	public DDList(VariableHashMap allVars)
	{
		this(allVars, "", "", 1);
	}

	/**
	 * Constructor
	 * @param allVars
	 * @param title
	 * @param id
	 */
	public DDList(VariableHashMap allVars, String title, String id)
	{
		this(allVars, title, id, 1);
	}

	/**
	 * Constructor
	 * @param allVars
	 * @param title
	 * @param id
	 * @param weight
	 */
	public DDList(VariableHashMap allVars, String title, String id, int weight)
	{
		this.allVars = allVars;
		this.title = title;
		this.id = id;
		this.weight = weight;
	}

    @Override
	public ArrayList<DataValue> getData()
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
		for (WeightedDataValue chkValue : this)
		{
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

    @Override
	public ArrayList<DataValue> getData(int choice)
	{
		retList.clear();

		//select the detail to return
		int aWeight = 0;

		//Iterate through the list of choices until the weights (from each DataValue) are greater the the num chosen as the 'choice'
		for (WeightedDataValue chkValue : this)
		{
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

	/**
	 * Set the id of the list
	 * @param id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

    @Override
	public String getId()
	{
		return id;
	}

    @Override
	public ArrayList<DataValue> getLastData()
	{
		return retList;
	}

	/**
	 * Get the range
	 * @return the range
	 */
	public int getRange()
	{
		int rangeTop = 0;

		for (WeightedDataValue value : this)
		{
			rangeTop += value.getWeight();
		}

		if (rangeTop <= 0)
		{ //the die will nullpointer if it is not at least 1
			rangeTop = 1;
		}

		return rangeTop;
	}

	/**
	 * Set the title of the list
	 * @param title
	 */
	public void setTitle(String title)
	{
		this.title = title;
	}

    @Override
	public String getTitle()
	{
		return title;
	}

	/**
	 * Set the weight
	 * @param weight
	 */
	public void setWeight(int weight)
	{
		this.weight = weight;
	}

    @Override
	public int getWeight()
	{
		return weight;
	}
}
