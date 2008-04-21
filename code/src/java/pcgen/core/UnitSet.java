/*
 * UnitSet.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on February 25, 2002, 10:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.text.DecimalFormat;


/**
 * <code>UnitSet</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class UnitSet
{
	private String distanceDisplayPattern;
	private String distanceUnit;
	private String heightDisplayPattern;
	private String heightUnit;
	private String name;
	private String weightDisplayPattern;
	private String weightUnit;
	private double distanceFactor;
	private double heightFactor;
	private double weightFactor;

	/**
	 * Set the distance display pattern.
	 * 
	 * @param dd distance display pattern.
	 */
	public void setDistanceDisplayPattern(final String dd)
	{
		distanceDisplayPattern = dd;
	}

	/**
	 * Get the distance display pattern.
	 * 
	 * @return String distance display pattern.
	 */
	public String getDistanceDisplayPattern()
	{
		return distanceDisplayPattern;
	}

	/**
	 * Set the distance factor.
	 * 
	 * @param df distance factor.
	 */
	public void setDistanceFactor(final double df)
	{
		distanceFactor = df;
	}

	/**
	 * Get the distance factor.
	 * 
	 * @return double distance factor.
	 */
	public double getDistanceFactor()
	{
		return distanceFactor;
	}

	/**
	 * Set the distance unit.
	 * 
	 * @param du distance unit.
	 */
	public void setDistanceUnit(final String du)
	{
		distanceUnit = du;
	}

	/**
	 * Get the distance unit.
	 * 
	 * @return String distance unit
	 */
	public String getDistanceUnit()
	{
		return getUnit(distanceUnit);
	}

	/**
	 * Set the height display pattern.
	 * 
	 * @param hd height display pattern.
	 */
	public void setHeightDisplayPattern(final String hd)
	{
		heightDisplayPattern = hd;
	}

	/**
	 * Get the height display pattern.
	 * 
	 * @return String height display pattern.
	 */
	public String getHeightDisplayPattern()
	{
		return heightDisplayPattern;
	}

	/**
	 * Set the height factor.
	 * 
	 * @param hf height factor.
	 */
	public void setHeightFactor(final double hf)
	{
		heightFactor = hf;
	}

	/**
	 * Get the height factor.
	 * 
	 * @return double height factor.
	 */
	public double getHeightFactor()
	{
		return heightFactor;
	}

	/**
	 * Set the height unit.
	 * 
	 * @param hu height unit.
	 */
	public void setHeightUnit(final String hu)
	{
		heightUnit = hu;
	}

	/**
	 * Get height unit.
	 * 
	 * @return String height unit.
	 */
	public String getHeightUnit()
	{
		return getUnit(heightUnit);
	}

	/**
	 * Set name.
	 * 
	 * @param n name.
	 */
	public void setName(final String n)
	{
		name = n;
	}

	/**
	 * Get name.
	 * 
	 * @return String name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the weight display pattern
	 * 
	 * @param wd weight display pattern.
	 */
	public void setWeightDisplayPattern(final String wd)
	{
		weightDisplayPattern = wd;
	}

	/**
	 * Get the weight display pattern.
	 * 
	 * @return String weight display pattern.
	 */
	public String getWeightDisplayPattern()
	{
		return weightDisplayPattern;
	}

	/**
	 * Set the weight factor.
	 * 
	 * @param wf weight factor.
	 */
	public void setWeightFactor(final double wf)
	{
		weightFactor = wf;
	}

	/**
	 * Get the weight factor.
	 * 
	 * @return double weight factor
	 */
	public double getWeightFactor()
	{
		return weightFactor;
	}

	/**
	 * Set the weight unit.
	 * 
	 * @param wu weight unit.
	 */
	public void setWeightUnit(final String wu)
	{
		weightUnit = wu;
	}

	/**
	 * Get the weight unit.
	 * 
	 * @return String weight unit.
	 */
	public String getWeightUnit()
	{
		return getUnit(weightUnit);
	}

	private String getUnit(final String unitString)
	{
		if ("ftin".equals(unitString))
		{
			return unitString;
		}
		else if (unitString.startsWith("~"))
		{
			return unitString.substring(1);
		}
		else
		{
			return " " + unitString;
		}
	}

	/**
	 * Convert distance in feet to distance in units. 
	 * 
	 * @param distanceInFeet
	 * @return double distance in units.
	 */
	public double convertDistanceToUnitSet(final double distanceInFeet)
	{
		final double distance = distanceInFeet * getDistanceFactor();

		return distance;
	}

	/**
	 * Convert height in units to height in inches.
	 * 
	 * @param height
	 * @return int height in inches.
	 */
	public int convertHeightFromUnitSet(final double height)
	{
		final double heightInInches = height / getHeightFactor();

		return (int) heightInInches;
	}

	/**
	 * Convert height in inches to height in units.
	 * 
	 * @param heightInInches
	 * @return double heights in units.
	 */
	public double convertHeightToUnitSet(final int heightInInches)
	{
		final double height = heightInInches * getHeightFactor();

		return height;
	}

	/**
	 * Convert weight in units to weight in pounds.
	 * 
	 * @param weight
	 * @return double weight in pounds.
	 */
	public double convertWeightFromUnitSet(final double weight)
	{
		final double weightInPounds = weight / getWeightFactor();

		return weightInPounds;
	}

	/**
	 * Convert weight in pounds to weight in units.
	 * 
	 * @param weightInPounds
	 * @return double weight in units.
	 */
	public double convertWeightToUnitSet(final double weightInPounds)
	{
		final double weight = weightInPounds * getWeightFactor();

		return weight;
	}

	/**
	 * Convert weight in pounds to weight in units.
	 * 
	 * @param weightInPounds
	 * @return int weight in units.
	 */
	public int convertWeightToUnitSet(final int weightInPounds)
	{
		final double weight = weightInPounds * getWeightFactor();

		return (int) weight;
	}

	/**
	 * Convert distance in feet to display distance in units.
	 * 
	 * @param distanceInFeet
	 * @return String display distance in units.
	 */
	public String displayDistanceInUnitSet(final double distanceInFeet)
	{
		final String output = new DecimalFormat(getDistanceDisplayPattern()).format(convertDistanceToUnitSet(distanceInFeet));

		return output;
	}

	/**
	 * Convert height in inches to display height in units.
	 * 
	 * @param heightInInches
	 * @return String display height in units.
	 */
	public String displayHeightInUnitSet(final int heightInInches)
	{
		final String output = new DecimalFormat(getHeightDisplayPattern()).format(convertHeightToUnitSet(heightInInches));

		return output;
	}

	/**
	 * Convert weight in pounds to display weight in units.
	 * 
	 * @param weightInPounds
	 * @return String display weight in units.
	 */
	public String displayWeightInUnitSet(final double weightInPounds)
	{
		final String output = new DecimalFormat(getWeightDisplayPattern()).format(convertWeightToUnitSet(weightInPounds));

		return output;
	}


}
