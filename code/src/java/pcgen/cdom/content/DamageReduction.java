/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 */
package pcgen.cdom.content;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.base.QualifyingObject;

/**
 * Encapsulates a single DamageReduction entity. This class encapsulates a
 * DamageReduction entity and provides utility methods to manipulate and combine
 * multiple DamageReductions together. The consensus seems to be that brevity
 * over clarity is preferred in the output so that is what the methods attempt
 * to provide.
 * 
 * 
 */
public class DamageReduction extends ConcretePrereqObject implements QualifyingObject
{
	private final Formula theReduction;
	private final String theBypass;

	/**
	 * Constructs a DamageReduction object. The reduction is stored as a string
	 * to allow use of JEP formulas and variables.
	 * 
	 * @param aReduction
	 *            The reduction to set
	 * @param aBypass
	 *            The bypass type to set.
	 */
	public DamageReduction(final Formula aReduction, final String aBypass)
	{
		theReduction = aReduction;
		theBypass = aBypass;
	}

	/**
	 * Gets the string of damage types that bypass this DR.
	 * 
	 * @return Returns the bypass.
	 */
	public String getBypass()
	{
		return theBypass;
	}

	/**
	 * Gets the String representation of the amount of damage this DR reduces.
	 * 
	 * @return Returns the amount of reduction.
	 */
	public Formula getReduction()
	{
		return theReduction;
	}

	/**
	 * Gets the actual reduction this DR will apply. If the reduction is
	 * variable and requires the PC to process, this will return -1
	 * 
	 * @return Amount of damage this DR reduces
	 */
	public int getRawReductionValue()
	{
		return theReduction.isStatic() ? theReduction.resolveStatic().intValue() : -1;
	}

	/**
	 * Gets a list of Damage Types that bypass this DR. This is just a raw list
	 * of types.
	 * 
	 * @return Collection of unique types converted to lower case
	 */
	public Collection<String> getBypassList()
	{
		StringTokenizer tok = new StringTokenizer(theBypass, " ");
		Set<String> ret = new HashSet<>();

		while (tok.hasMoreTokens())
		{
			final String val = tok.nextToken();
			if (!("or".equalsIgnoreCase(val) || "and".equalsIgnoreCase(val)))
			{
				ret.add(val.toLowerCase());
			}
		}
		return Collections.unmodifiableSet(ret);
	}

	/**
	 * Returns a String representation of this DamageReduction object.
	 * 
	 * @return String
	 */
	@Override
	public String toString()
	{
		String reductionString = theReduction.toString();
		int val = getRawReductionValue();
		if (val < 0)
		{
			reductionString = "variable";
		}
		return reductionString + "/" + theBypass;
	}

	/**
	 * Tests if two DR objects are the same. The test checks that all bypasses
	 * are present in any order and that the values are the same
	 * 
	 * @param other
	 *            The DR to test against.
	 * @return true if the DRs are the same.
	 */
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof DamageReduction))
		{
			return false;
		}
		Collection<String> coll1 = getBypassList();
		Collection<String> coll2 = ((DamageReduction) other).getBypassList();
		if (coll1.containsAll(coll2) && coll2.containsAll(coll1))
		{
			return theReduction.equals(((DamageReduction) other).theReduction);
		}
		return false;
	}

	/**
	 * Returns a hash code to use for this object. This method is overridden to
	 * return the same hashcode for the same DR object. That is if
	 * dr.equals(dr1) the hashcodes must be the same
	 * 
	 * @return A hashcode
	 */
	@Override
	public int hashCode()
	{
		List<String> list = new ArrayList<>(getBypassList());
		Collections.sort(list);

		int hash = list.stream()
		    .mapToInt(Object::hashCode)
		    .sum();
		return theReduction.hashCode() + hash;
	}

	public String getLSTformat()
	{
        return String.valueOf(theReduction)
                + '/'
                + theBypass;
	}
}
