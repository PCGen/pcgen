/*
 * Copyright 2003 (C) Devon Jones
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
 package pcgen.core.doomsdaybook;

import gmgen.plugin.dice.Dice;

import java.util.ArrayList;

/**
 *
 */
public class DDList extends ArrayList<WeightedDataValue> implements DataElement
{
	ArrayList<DataValue> retList = new ArrayList<>();
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

		//Iterate through the list of choices until the weights (from each DataValue) are greater the num chosen as the 'choice'
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

		//Iterate through the list of choices until the weights (from each DataValue) are greater the num chosen as the 'choice'
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
