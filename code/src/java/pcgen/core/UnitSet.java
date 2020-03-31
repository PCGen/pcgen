/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
 *
 */
package pcgen.core;

import java.math.BigDecimal;
import java.net.URI;
import java.text.DecimalFormat;

import pcgen.cdom.base.Loadable;

/**
 * {@code UnitSet}.
 *
 */
public final class UnitSet implements Loadable
{
	private DecimalFormat distanceDisplayPattern;
	private String distanceUnit;
	private DecimalFormat heightDisplayPattern;
	private String heightUnit;
	private String name;
	private DecimalFormat weightDisplayPattern;
	private String weightUnit;
	private BigDecimal distanceFactor;
	private BigDecimal heightFactor;
	private BigDecimal weightFactor;
	private boolean isInternal = false;
	private URI sourceURI;

	/**
	 * Set the distance display pattern.
	 * 
	 * @param dd distance display pattern.
	 */
	public void setDistanceDisplayPattern(final DecimalFormat dd)
	{
		distanceDisplayPattern = dd;
	}

	/**
	 * Set the distance factor.
	 * 
	 * @param df distance factor.
	 */
	public void setDistanceFactor(final BigDecimal df)
	{
		distanceFactor = df;
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
	public void setHeightDisplayPattern(final DecimalFormat hd)
	{
		heightDisplayPattern = hd;
	}

	/**
	 * Set the height factor.
	 * 
	 * @param hf height factor.
	 */
	public void setHeightFactor(final BigDecimal hf)
	{
		heightFactor = hf;
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
	@Override
	public void setName(final String n)
	{
		name = n;
	}

	/**
	 * Get name.
	 * 
	 * @return String name.
	 */
	@Override
	public String getDisplayName()
	{
		return name;
	}

	/**
	 * Set the weight display pattern
	 * 
	 * @param wd weight display pattern.
	 */
	public void setWeightDisplayPattern(final DecimalFormat wd)
	{
		weightDisplayPattern = wd;
	}

	/**
	 * Set the weight factor.
	 * 
	 * @param wf weight factor.
	 */
	public void setWeightFactor(final BigDecimal wf)
	{
		weightFactor = wf;
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
			return ' ' + unitString;
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
		return distanceInFeet * distanceFactor.doubleValue();
	}

	/**
	 * Convert height in units to height in inches.
	 * 
	 * @param height
	 * @return int height in inches.
	 */
	public int convertHeightFromUnitSet(final double height)
	{
		final double heightInInches = height / heightFactor.doubleValue();

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
		return heightInInches * heightFactor.doubleValue();
	}

	/**
	 * Convert weight in units to weight in pounds.
	 * 
	 * @param weight
	 * @return double weight in pounds.
	 */
	public double convertWeightFromUnitSet(final double weight)
	{

        return weight / weightFactor.doubleValue();
	}

	/**
	 * Convert weight in pounds to weight in units.
	 * 
	 * @param weightInPounds
	 * @return double weight in units.
	 */
	public double convertWeightToUnitSet(final double weightInPounds)
	{
		return weightInPounds * weightFactor.doubleValue();
	}

	/**
	 * Convert weight in pounds to weight in units.
	 * 
	 * @param weightInPounds
	 * @return int weight in units.
	 */
	public int convertWeightToUnitSet(final int weightInPounds)
	{
		final double weight = weightInPounds * weightFactor.doubleValue();

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
		return distanceDisplayPattern.format(convertDistanceToUnitSet(distanceInFeet));
	}

	/**
	 * Convert height in inches to display height in units.
	 * 
	 * @param heightInInches
	 * @return String display height in units.
	 */
	public String displayHeightInUnitSet(final int heightInInches)
	{
		return heightDisplayPattern.format(convertHeightToUnitSet(heightInInches));
	}

	/**
	 * Convert weight in pounds to display weight in units.
	 * 
	 * @param weightInPounds
	 * @return String display weight in units.
	 */
	public String displayWeightInUnitSet(final double weightInPounds)
	{
		return weightDisplayPattern.format(convertWeightToUnitSet(weightInPounds));
	}

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	@Override
	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	@Override
	public String getKeyName()
	{
		return getDisplayName();
	}

	@Override
	public boolean isInternal()
	{
		return isInternal;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}

	public void setInternal(boolean internal)
	{
		isInternal = internal;
	}

	public String getRawWeightUnit()
	{
		return weightUnit;
	}

	public String getRawHeightUnit()
	{
		return heightUnit;
	}

	public String getRawDistanceUnit()
	{
		return distanceUnit;
	}

	public DecimalFormat getWeightDisplayPattern()
	{
		return weightDisplayPattern;
	}

	public DecimalFormat getHeightDisplayPattern()
	{
		return heightDisplayPattern;
	}

	public DecimalFormat getDistanceDisplayPattern()
	{
		return distanceDisplayPattern;
	}

	public BigDecimal getDistanceFactor()
	{
		return distanceFactor;
	}

	public BigDecimal getHeightFactor()
	{
		return heightFactor;
	}

	public BigDecimal getWeightFactor()
	{
		return weightFactor;
	}

}
