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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

import java.util.Collection;

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
public class DamageReduction extends PrereqObject implements Comparable<DamageReduction>, Cloneable
{
	private String theReduction = "0";
	private String theBypass = "-";
	private PlayerCharacter thePC = null;
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
	 * Sets what PlayerCharacter this DR is associated with.  Used to evaluate
	 * and variables or formulas that may be present.  A null PC can be used
	 * if no formulas are needed by the DR.
	 *
	 * @param aPC The PlayerCharacter to associate with this DR.
	 */
	public void setPC(final PlayerCharacter aPC)
	{
		if (thePC == null)
		{
			thePC = aPC;
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
	public int getReductionValue()
	{
		if (thePC != null)
		{
			int protectionValue = thePC.getVariableValue(theReduction, "getDR").
				intValue();
			protectionValue += (int) thePC.getTotalBonusTo("DR", theBypass);
			return protectionValue;
		}
		// If we don't have a PC we will see if we can parse the
		// reduction value as an int, if we can't we will return
		// the value "variable" for it instead.
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
		if (thePC == null)
		{
			String reductionString = theReduction;
			int val = getReductionValue();
			if (val < 0)
			{
				reductionString = "variable";
			}
			return reductionString + "/" + theBypass;
		}
		if (qualifies(thePC))
		{
			return getReductionValue() + "/" + theBypass;
		}

		return "";
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
			return getReductionValue()
				== ( (DamageReduction) other).getReductionValue();
		}
		return false;
	}

	/**
	 * Compares two DR objects and returns an integer based on their relative
	 * sorting order.  DRs are sorted from highest reduction to lowest.
	 * @param dr The DR to test against
	 * @return -1 if the passed in object is less that this one, 0 if they are
	 * equal and 1 if the passed in object is greater.
	 */
	public int compareTo(DamageReduction dr)
	{
		int v1 = getReductionValue();
		int v2 = dr.getReductionValue();
		return v1 < v2 ? 1 : v1 > v2 ? -1 : 0;
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
	 * Add to DRs together and return the result.  If the combined value can
	 * be represented as a single value, it is returned otherwise null is
	 * returned.
	 * @param dr1 DamageReduction
	 * @param dr2 DamageReduction
	 * @return The new DamageReduction object or null
	 */
	public static DamageReduction addDRs(final DamageReduction dr1,
										 final DamageReduction dr2)
	{
		DamageReduction DR1 = dr1.qualifies(dr1.thePC) ? dr1 : null;
		DamageReduction DR2 = dr2.qualifies(dr2.thePC) ? dr2 : null;
		if (DR1 == null && DR2 != null)
		{
			return DR2;
		}
		else if (DR1 != null && DR2 == null)
		{
			return DR1;
		}
		else if (DR1 == null && DR2 == null)
		{
			return null;
		}
		// Intersect the two DRs to see if we have anything in common.
		HashSet<String> hs1 = new HashSet<String>(dr1.getBypassList());
		HashSet<String> hs2 = new HashSet<String>(dr2.getBypassList());

		if (dr1.getBypass().equalsIgnoreCase(dr2.getBypass()))
		{
			// These are the same DRs.  Return the highest one.
			return new DamageReduction(Math.max(dr1.getReductionValue(),
												dr2.getReductionValue()) + "",
									   dr1.getBypass());
		}
		hs1.retainAll(hs2);
		if (hs1.size() > 0)
		{
			// OK, there are at least some items in common.
			// Figure out what to do about it.
			// The consensus seems to be that we should only combine DRs if
			// we can represent the result as a single DR.
			// The only way we can do that is if either bypass string contains
			// an "or".

			if ( (dr1.join == OR_JOIN && dr2.join != OR_JOIN))
			{
				// If the item is equal to or greater than the or item
				// drop the or item.
				if (dr2.getReductionValue() >= dr1.getReductionValue())
				{
					return dr2;
				}
			}
			else if ( (dr2.join == OR_JOIN && dr1.join != OR_JOIN))
			{
				if (dr1.getReductionValue() >= dr2.getReductionValue())
				{
					return dr1;
				}
			}
			else if (dr1.join == NO_JOIN && dr2.join != OR_JOIN)
			{
				// 5/good + 10/magic and good = 10/magic and good
				if (dr2.getReductionValue() >= dr1.getReductionValue())
				{
					return dr2;
				}
			}
			else if (dr2.join == NO_JOIN && dr1.join != OR_JOIN)
			{
				// 5/good + 10/magic and good = 10/magic and good
				if (dr1.getReductionValue() >= dr2.getReductionValue())
				{
					return dr1;
				}
			}
			else if (dr1.getReductionValue() == dr2.getReductionValue())
			{
				// If the value is the same we can combine items
				if (dr1.join != OR_JOIN && dr2.join != OR_JOIN)
				{
					HashSet<String> unique = new HashSet<String>(dr1.getBypassList());
					unique.addAll(hs2);
					boolean doneFirst = false;
					StringBuffer buffer = new StringBuffer();
					for (Iterator<String> i = unique.iterator(); i.hasNext(); )
					{
						if (doneFirst)
						{
							buffer.append(" and ");
						}
						buffer.append(i.next());
						doneFirst = true;
					}
					return new DamageReduction(dr1.getReduction(),
											   buffer.toString());
				}
			}
		}
		else
		{
			if ( (dr1.join != OR_JOIN && dr2.join != OR_JOIN)
				&& dr1.getReductionValue() == dr2.getReductionValue())
			{
				return new DamageReduction(dr1.getReduction(),
										   dr1.getBypass() + " and "
										   + dr2.getBypass());
			}
		}
		return null;
	}

	/**
	 * Adds two DRs together and return a String based representation of the
	 * result.
	 * @param dr1 DamageReduction
	 * @param dr2 DamageReduction
	 * @return String-base representation of the result.
	 */
	public static String combineDRs(final DamageReduction dr1,
									final DamageReduction dr2)
	{
		DamageReduction DR1 = dr1.qualifies(dr1.thePC) ? dr1 : null;
		DamageReduction DR2 = dr2.qualifies(dr2.thePC) ? dr2 : null;
		if (DR1 == null && DR2 != null)
		{
			return DR2.toString();
		}
		else if (DR1 != null && DR2 == null)
		{
			return DR1.toString();
		}
		else if (DR1 == null && DR2 == null)
		{
			return "";
		}
		DamageReduction drResult = addDRs(dr1, dr2);
		if (drResult == null)
		{
			if (dr1.compareTo(dr2) <= 0)
			{
				return dr1.toString() + "; " + dr2.toString();
			}
			return dr2.toString() + "; " + dr1.toString();
		}
		return drResult.toString();
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
	private static List<DamageReduction> parseOrList(final PlayerCharacter aPC, List<DamageReduction> drList)
	{
		ArrayList<DamageReduction> ret = new ArrayList<DamageReduction>();
		for (Iterator<DamageReduction> i = drList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			dr.setPC(aPC);
			if (!dr.qualifies(aPC))
			{
				continue;
			}

			if (dr.getBypass().toLowerCase().indexOf(" or ") != -1)
			{
				ret.add(dr);
				i.remove();
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
	private static List<DamageReduction> parseAndList(final PlayerCharacter aPC, final List<DamageReduction> inList)
	{
		ArrayList<DamageReduction> ret = new ArrayList<DamageReduction>();
		for (Iterator<DamageReduction> i = inList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			dr.setPC(aPC);
			if (!dr.qualifies(aPC))
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
	private static List<DamageReduction> processList(List<DamageReduction> andList, List<DamageReduction> orList)
	{
		List<DamageReduction> ret = new ArrayList<DamageReduction>();
		HashMap<String, DamageReduction> lookup = new HashMap<String, DamageReduction>();
		for (Iterator<DamageReduction> i = andList.iterator(); i.hasNext(); )
		{
			DamageReduction dr = i.next();
			final String bypass = dr.getBypass().toLowerCase();
			if (dr.getReductionValue() == -1)
			{
				ret.add(dr);
				continue;
			}
			DamageReduction match = lookup.get(bypass);
			if (match == null)
			{
				lookup.put(bypass, dr);
			}
			else if (dr.getReductionValue() > match.getReductionValue())
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
					if (andDR.getReductionValue() >= dr.getReductionValue())
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

		Collections.sort(ret);
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
				if (currentDR != null
					&& dr.getReductionValue() == currentDR.getReductionValue())
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
			if (!dr.qualifies(pc))
			{
				i.remove();
			}
			else if (dr.join != OR_JOIN)
			{
				if (currentDR != null
					&& dr.getReductionValue() == currentDR.getReductionValue())
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
	public static List<DamageReduction> getDRList(final PlayerCharacter aPC, List<DamageReduction> drList)
	{
		List<DamageReduction> inList = new ArrayList<DamageReduction>(drList);
		List<DamageReduction> orList = parseOrList(aPC, inList);
		List<DamageReduction> andList = parseAndList(aPC, inList);

		List<DamageReduction> resultList = processList(andList, orList);

		return Collections.unmodifiableList(resultList);
	}

	/**
	 * Returns a single String representing all DRs applicible to the passed in
	 * PC.
	 * @param aPC PlayerCharacter these DRs apply to.
	 * @param drList List of DRs
	 * @return String based result of combine.
	 */
	public static String getDRString(final PlayerCharacter aPC, List<DamageReduction> drList)
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

			String value = dr.toString();
			if (value != null && value.trim().length() > 0)
			{
				buffer.append(dr.toString());
				doneFirst = true;
			}
		}
		return buffer.toString();
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public DamageReduction clone() throws CloneNotSupportedException
	{
		DamageReduction clone = (DamageReduction) super.clone();
		//Have to do deep clone of Prereqs to match previous behavior :/
		if (hasPreReqs()) {
			clone.clearPreReq();
			for (Prerequisite prereq : getPreReqList())
			{
				clone.addPreReq(prereq.clone());
			}
		}
		return clone;
	}

	/**
	 * Generate the text to be included in a LST file to represent this DR object.
	 *
	 * @param includeLevel Should level prereqs be included?
	 * @return The LST code for the DR.
	 */
	public String getPCCText(boolean includeLevel)
	{
		StringBuffer result = new StringBuffer("DR:");
		result.append(theReduction);
		result.append("/");
		result.append(theBypass);

		final StringWriter writer = new StringWriter();
		for (Prerequisite prereq : getPreReqList())
		{
			if (!includeLevel && "class".equals(prereq.getKind()))
			{
				continue;
			}
			final PrerequisiteWriter prereqWriter = new PrerequisiteWriter();
			try
			{
				writer.write("|");
				prereqWriter.write(writer, prereq);
			}
			catch (PersistenceLayerException e1)
			{
				e1.printStackTrace();
			}
			result.append(writer);
		}

		return result.toString();
	}

	/**
	 * Determine if this damage reduction object is associated with a
	 * level of a class.
	 *
	 * @param keyName The key nameof the PCClass.
	 * @return true if it is associated with a level of the class, false otherwise.
	 */
	public boolean isForClassLevel(String keyName)
	{
		for (Prerequisite prereq : getPreReqList())
		{
			if (DamageReduction.isPrereqForClassLevel(prereq, keyName))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine if this damage reduction object is associated with a
	 * level of a class.
	 *
	 * @param prereq The prerequisite to check.
	 * @param keyName The key nameof the PCClass.
	 * @return true if it is associated with a level of the class, false otherwise.
	 */
	public static boolean isPrereqForClassLevel(Prerequisite prereq,
		String keyName)
	{
		if (prereq.getKind().equals("class") && prereq.getKey().equals(keyName))
		{
			return true;
		}
		return false;
	}
}
