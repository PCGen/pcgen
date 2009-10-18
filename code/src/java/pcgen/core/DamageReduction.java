/*
 * DamageReduction.java
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
 *
 * Created on March 16, 2006
 *
 * Current Ver: $Revision: $
 * Last Editor: $Author: $
 * Last Edited: $Date: $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ConcretePrereqObject;

/**
 * Encapsulates a single DR entity.
 * This class encapsulates a DR entity and provides utility methods to
 * manipulate and combine multiple DRs together.  The consensus seems to be
 * that brievity over clarity is preferred in the output so that is what the
 * methods attempt to provide.
 *
 * @author boomer70
 *
 */
public class DamageReduction extends ConcretePrereqObject
{
	private String theReduction = "0";
	private String theBypass = "-";
	private static final int NO_JOIN = -1;
	private static final int AND_JOIN = 0;
	private static final int OR_JOIN = 1;
	private int join = AND_JOIN;

	/**
	 * Constructs a DamageReduction object.  The reduction is stored
	 * as a string to allow use of JEP formulas and variables.
	 *
	 * @param aReduction The reduction to set
	 * @param aBypass The bypass type to set.
	 */
	public DamageReduction(final String aReduction, final String aBypass)
	{
		theReduction = aReduction;
		theBypass = aBypass;
		final String tempBypass = theBypass.toLowerCase();
		if (tempBypass.indexOf(" and ") != -1)
		{
			join = AND_JOIN;
		}
		else if (tempBypass.indexOf(" or ") != -1)
		{
			join = OR_JOIN;
		}
		else
		{
			join = NO_JOIN;
		}
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
	 * Sets the string of damage types that bypass this DR.
	 *
	 * @param aBypass The bypass to set.
	 */
	public void setBypass(String aBypass)
	{
		this.theBypass = aBypass;
	}

	/**
	 * Gets the String representation of the amount of damage this DR reduces.
	 *
	 * @return Returns the amount of reduction.
	 */
	public String getReduction()
	{
		return theReduction;
	}

	/**
	 * Gets the actual reduction this DR will apply.  If a PC has been set on
	 * the DR object it will evaluate any formulas in the DR and apply any
	 * bonuses to this DR type that are appropriate.
	 *
	 * @return Amount of damage this DR reduces
	 */
	public int getReductionValue(PlayerCharacter pc)
	{
		if (pc != null)
		{
			int protectionValue = pc.getVariableValue(theReduction, "getDR").
				intValue();
			protectionValue += (int) pc.getTotalBonusTo("DR", theBypass);
			return protectionValue;
		}
		// If we don't have a PC we will see if we can parse the
		// reduction value as an int, if we can't we will return
		// the value "variable" for it instead.
		return getRawReductionValue();
	}

	/**
	 * Gets the actual reduction this DR will apply.  If a PC has been set on
	 * the DR object it will evaluate any formulas in the DR and apply any
	 * bonuses to this DR type that are appropriate.
	 *
	 * @return Amount of damage this DR reduces
	 */
	public int getRawReductionValue()
	{
		try
		{
			return Integer.parseInt(theReduction);
		}
		catch (NumberFormatException notUsed)
		{
			// Nothing we can do.
		}
		return -1;
	}

	/**
	 * Sets the reduction string to use for this DR.
	 *
	 * Do Not Remove.
	 *
	 * @param aReduction The reduction to set.
	 */
	public void setReduction(String aReduction)
	{
		this.theReduction = aReduction;
	}

	/**
	 * Gets a list of Damage Types that bypass this DR.  This ls just a raw
	 * list of types.
	 * @return Collection of unique types converted to lower case
	 */
	public Collection<String> getBypassList()
	{
		StringTokenizer tok = new StringTokenizer(theBypass, " ");
		HashSet<String> ret = new HashSet<String>();

		while (tok.hasMoreTokens())
		{
			final String val = tok.nextToken();
			if (! ("or".equalsIgnoreCase(val) || "and".equalsIgnoreCase(val)))
			{
				ret.add(val.toLowerCase());
			}
		}
		return Collections.unmodifiableSet(ret);
	}

	/**
	 * Returns a String representation of this DamageReduction object.
	 * @return String
	 */
	@Override
	public String toString()
	{
		String reductionString = theReduction;
		int val = getRawReductionValue();
		if (val < 0)
		{
			reductionString = "variable";
		}
		return reductionString + "/" + theBypass;
	}
	
	public String toString(PlayerCharacter pc)
	{
		if (pc == null)
		{
			return toString();
		}
		return getReductionValue(pc) + "/" + theBypass;
	}

	/**
	 * Tests if two DR objects are the same.  The test checks that all bypasses
	 * are present in any order and that the values are the same
	 * @param other The DR to test against.
	 * @return true if the DRs are the same.
	 */
	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof DamageReduction)) {
			return false;
		}
		Collection<String> l1 = getBypassList();
		Collection<String> l2 = ( (DamageReduction) other).getBypassList();
		if (l1.containsAll(l2) && l2.containsAll(l1))
		{
			return theReduction.equals(((DamageReduction) other).theReduction);
		}
		return false;
	}

	public static class DamageReductionComparator implements
			Comparator<DamageReduction>
	{

		private final PlayerCharacter character;

		public DamageReductionComparator(PlayerCharacter pc)
		{
			character = pc;
		}

		/**
		 * Compares two DR objects and returns an integer based on their relative
		 * sorting order.  DRs are sorted from highest reduction to lowest.
		 * @return -1 if dr2 in object is less than dr1, 0 if they are
		 * equal and 1 if dr2 is greater then dr1.
		 */
		public int compare(DamageReduction dr1, DamageReduction dr2)
		{
			int v1 = dr1.getReductionValue(character);
			int v2 = dr2.getReductionValue(character);
			return v1 < v2 ? 1 : v1 > v2 ? -1 : 0;
		}

	}

	/**
	 * Returns a hash code to use for this object.  This method is overridden to
	 * return the same hashcode for the same DR object.  That is if
	 * dr.equals(dr1) the hashcodes must be the same
	 * @return A hashcode
	 */
	@Override
	public int hashCode()
	{
		ArrayList<String> l = new ArrayList<String>(getBypassList());
		Collections.sort(l);
		int hash = 0;
		for (Iterator<String> i = l.iterator(); i.hasNext(); )
		{
			hash += i.next().hashCode();
		}
		return theReduction.hashCode() + hash;
	}

	/**
	 * Builds a list of DRs that have OR logic in them.  Also sets the PC for
	 * each DR to the passed in PC and checks prerequisites.
	 *
	 * @param aPC PlayerCharacter these DRs should be processed for.
	 * @param drList List of DRs to process.  The list is modified by this
	 * method.
	 * @return The list of DRs that contain ORs.
	 */
	private static List<DamageReduction> parseOrList(final PlayerCharacter aPC, Map<DamageReduction, CDOMObject> drList)
	{
		ArrayList<DamageReduction> ret = new ArrayList<DamageReduction>();
		for (Iterator<Map.Entry<DamageReduction, CDOMObject>> it = drList
				.entrySet().iterator(); it.hasNext();)
		{
			Entry<DamageReduction, CDOMObject> me = it.next();
			DamageReduction dr = me.getKey();
			CDOMObject owner = me.getValue();
			if (!dr.qualifies(aPC, owner))
			{
				continue;
			}

			if (dr.getBypass().toLowerCase().indexOf(" or ") != -1)
			{
				ret.add(dr);
				it.remove();
			}
		}
		return ret;
	}

	/**
	 * Processes a list of DRs and splits AND values into multiple DRs.  This
	 * works because 10/magic and good is the same as 10/magic; 10/good.
	 * @param inList List
	 * @return List of separated DRs
	 */
	private static List<DamageReduction> parseAndList(final PlayerCharacter aPC, final Map<DamageReduction, CDOMObject> inList)
	{
		ArrayList<DamageReduction> ret = new ArrayList<DamageReduction>();
		for (Iterator<Map.Entry<DamageReduction, CDOMObject>> i = inList.entrySet().iterator(); i.hasNext(); )
		{
			Entry<DamageReduction, CDOMObject> me = i.next();
			DamageReduction dr = me.getKey();
			if (!dr.qualifies(aPC, me.getValue()))
			{
				continue;
			}
			final String bypass = dr.getBypass().replaceAll(" AND ", " and ");
			String[] splits = bypass.split(" and ");
			if (splits.length == 1)
			{
				ret.add(dr);
			}
			else
			{
				for (int j = 0; j < splits.length; j++)
				{
					DamageReduction newDR = new DamageReduction(dr.getReduction(),
						splits[j]);
					ret.add(newDR);
				}
			}
		}
		return ret;
	}

	/**
	 * Processes a list of AND and OR DR values and combines them together.
	 * @param andList List of AND type DRs
	 * @param orList List of OR type DRs
	 * @return Resulting List generated by combining both passed in lists.
	 */
	private static List<DamageReduction> processList(PlayerCharacter pc, List<DamageReduction> andList, List<DamageReduction> orList)
	{
		List<DamageReduction> ret = new ArrayList<DamageReduction>();
		HashMap<String, DamageReduction> lookup = new HashMap<String, DamageReduction>();
		for (Iterator<DamageReduction> i = andList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			final String bypass = dr.getBypass().toLowerCase();
			if (dr.getReductionValue(pc) == -1)
			{
				ret.add(dr);
				continue;
			}
			DamageReduction match = lookup.get(bypass);
			if (match == null)
			{
				lookup.put(bypass, dr);
			}
			else if (dr.getReductionValue(pc) > match.getReductionValue(pc))
			{
				lookup.remove(match.getBypass().toLowerCase());
				lookup.put(bypass, dr);
			}
		}
		ret.addAll(lookup.values());

		// For each 'or'
		// Case 1: A greater or equal DR for any value in the OR
		//         e.g. 10/good + 5/magic or good = 10/good
		// Case 2: A smaller DR for any value in the OR
		//         e.g. 10/magic or good + 5/good = 10/magic or good; 5/good
		//         e.g. 10/magic or good or lawful + 5/good = 10/good; 5/magic or good
		for (Iterator<DamageReduction> i = orList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			final String bypass = dr.getBypass().replaceAll(" OR ", " or ");
			String[] orValues = bypass.split(" or ");
			boolean shouldAdd = true;
			for (int j = 0; j < orValues.length; j++)
			{
				// See if we already have a value for this type from the 'and'
				// processing.
				DamageReduction andDR = lookup.get(orValues[j].toLowerCase());
				if (andDR != null)
				{
					if (andDR.getReductionValue(pc) >= dr.getReductionValue(pc))
					{
						shouldAdd = false;
						break;
					}
				}
			}
			if (shouldAdd)
			{
				ret.add(dr);
			}
		}

		Collections.sort(ret, new DamageReductionComparator(pc));
		return ret;
	}

	/**
	 * Process a List of DRs and try and recombine multiple values into a single
	 * value.  For example 10/magic; 10/good = 10/magic and good.
	 * @param drList The list of DRs to combine.  The list is modified by the
	 * method.
	 */
	private static void mergeAnds(List<DamageReduction> drList)
	{
		// Assumes the input list is sorted in DR rating order
		DamageReduction currentDR = null;
		for (Iterator<DamageReduction> i = drList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			if (dr.join != OR_JOIN)
			{
				if (currentDR != null && dr.getRawReductionValue() > 0
					&& dr.getRawReductionValue() == currentDR.getRawReductionValue())
				{
					// We can merge these two DRs into one.
					currentDR.setBypass(currentDR.getBypass() + " and "
										+ dr.getBypass());
					i.remove();
				}
				else
				{
					currentDR = dr;
				}
			}
		}
	}

	/**
	 * Process a List of DRs and try and recombine multiple values into a single
	 * value.  For example 10/magic; 10/good = 10/magic and good. Only DR values
	 * that the PC qualifies for will be included in the final list.
	 * @param drList The list of DRs to combine.  The list is modified by the
	 * method.
	 * @param pc The character the list is for
	 */
	private static void mergeAnds(List<DamageReduction> drList, PlayerCharacter pc)
	{
		if (pc == null)
		{
			mergeAnds(drList);
			return;
		}
		// Assumes the input list is sorted in DR rating order
		DamageReduction currentDR = null;
		for (Iterator<DamageReduction> i = drList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			if (dr.join != OR_JOIN)
			{
				if (currentDR != null
					&& dr.getReductionValue(pc) == currentDR.getReductionValue(pc))
				{
					// We can merge these two DRs into one.
					currentDR.setBypass(currentDR.getBypass() + " and "
										+ dr.getBypass());
					i.remove();
				}
				else
				{
					currentDR = dr;
				}
			}
		}
	}

	/**
	 * Returns a list of merged DamageReduction objects "and" values are not
	 * merged.
	 * @param aPC The PC the DR rating is being calculated for.
	 * @param drList List of DamageReduction objects to combine
	 * @return List of combined DamageReduction objects without merging "ands"
	 */
	public static List<DamageReduction> getDRList(final PlayerCharacter aPC, Map<DamageReduction, CDOMObject> drList)
	{
		Map<DamageReduction, CDOMObject> inList = new IdentityHashMap<DamageReduction, CDOMObject>(drList);
		List<DamageReduction> orList = parseOrList(aPC, inList);
		List<DamageReduction> andList = parseAndList(aPC, inList);

		List<DamageReduction> resultList = processList(aPC, andList, orList);

		return Collections.unmodifiableList(resultList);
	}

	/**
	 * Returns a single String representing all DRs applicible to the passed in
	 * PC.
	 * @param aPC PlayerCharacter these DRs apply to.
	 * @param drList List of DRs
	 * @return String based result of combine.
	 */
	public static String getDRString(final PlayerCharacter aPC, Map<DamageReduction, CDOMObject> drList)
	{
		List<DamageReduction> resultList = new ArrayList<DamageReduction>(getDRList(aPC, drList));
		mergeAnds(resultList, aPC);

		StringBuffer buffer = new StringBuffer();
		boolean doneFirst = false;
		for ( DamageReduction dr : resultList )
		{
			if (doneFirst)
			{
				buffer.append("; ");
			}

			String value = dr.toString(aPC);
			if (value != null && value.trim().length() > 0)
			{
				buffer.append(value);
				doneFirst = true;
			}
		}
		return buffer.toString();
	}

	public String getLSTformat()
	{
		StringBuffer result = new StringBuffer();
		result.append(theReduction);
		result.append("/");
		result.append(theBypass);
		return result.toString();
	}
}
