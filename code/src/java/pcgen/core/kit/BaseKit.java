/*
 * BaseKit.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
 * Copyright 2003 (C) Jonas Karlson <jujutsunerd@sf.net>
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
 * Created on September 28, 2002, 11:50 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.util.Logging;
import java.util.Iterator;

/**
 * Common code for the kits.
 * @author Jonas Karlson <jujutsunerd@sf.net>
 * @version $Revision$
 */
public abstract class BaseKit implements Cloneable
{
	protected int choiceCount = 1;
	private List prereqs = null;
	private List options = new ArrayList();
	private ArrayList lookups = new ArrayList();

	/**
	 * Set the number of choices (after converting to an integer.)
	 * @param argChoiceCount the number of choices
	 */
	public void setChoiceCount(final String argChoiceCount)
	{
		try
		{
			choiceCount = Integer.parseInt(argChoiceCount);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Invalid choice count \"" + argChoiceCount + "\" in BaseKit.setChoiceCount");
		}
	}

	/**
	 * Get the number of choices.
	 * @return the number of choices
	 */
	public int getChoiceCount()
	{
		return choiceCount;
	}

	/**
	 * Get a list of the prereqs for the kit.
	 * @return the list of prereqs.
	 */
	public List getPrereqs()
	{
		return prereqs;
	}

	/**
	 * Add a prereq for the kit.
	 * @param argPrereq the prereq to add
	 */
	public void addPreReq(final String argPrereq)
	{
		if (prereqs == null)
		{
			prereqs = new ArrayList();
		}

		prereqs.add(argPrereq);
	}

	/**
	 * Add the lookup to the lookups list
	 * @param aLookup
	 */
	public void addLookup(final String aLookup)
	{
		lookups.add(aLookup);
	}

	/**
	 * Get an unmodifiable copy of the lookups list 
	 * @return an unmodifiable copy of the lookups list
	 */
	public List getLookups()
	{
		return Collections.unmodifiableList(lookups);
	}

	public Object clone()
	{
		BaseKit aClone = null;
		try
		{
			aClone = (BaseKit)super.clone();
		}
		catch (CloneNotSupportedException notUsed)
		{
			// This will never happen
		}
		aClone.prereqs = prereqs;
		aClone.choiceCount = choiceCount;
		aClone.options = options;

		return aClone;
	}

	/**
	 * Add range to the options
	 * @param lowVal Start of the range
	 * @param highVal End of the range
	 */
	public void addOptionRange(String lowVal, String highVal)
	{
		options.add(new Range(lowVal, highVal));
	}

	/**
	 * Returns true if the value is in the option range for this item
	 * @param pc Charater the kit is being applied to.  Used to eval formulas
	 * @param val the Select value
	 * @return true if the value is an option
	 */
	public boolean isOption(PlayerCharacter pc, int val)
	{
		if (options.size() == 0)
		{
			return true;
		}
		for (Iterator i = options.iterator(); i.hasNext(); )
		{
			Range r = (Range)i.next();
			if (r.isIn(pc, val))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Evaluate the the EVAL expression
	 * @param aPC
	 * @param aValue
	 * @return the answer
	 */
	public static String eval(PlayerCharacter aPC, String aValue)
	{
		String ret = aValue;

		int evalInd = aValue.indexOf("EVAL(");
		if (evalInd == -1)
		{
			return aValue;
		}

		while (evalInd != -1)
		{
			String evalStr = aValue.substring(evalInd);
			StringBuffer modString = new StringBuffer();

			modString.append(aValue.substring(0, evalInd));

			int nestingLevel = 1;
			int startInd = 4;
			int endInd = startInd + 1;
			while (endInd < aValue.length() - 1)
			{
				char c = evalStr.charAt(endInd);
				if (c == '(')
				{
					nestingLevel++;
				}
				else if (c == ')')
				{
					nestingLevel--;
					if (nestingLevel == 0)
					{
						// We found our matching paren
						break;
					}
				}
				endInd++;
			}
			if (nestingLevel != 0)
			{
				// We don't have a valid expression.  Maybe someone else will
				// handle it.
				return aValue;
			}
			evalStr = evalStr.substring(5, endInd);
			int val = aPC.getVariableValue(evalStr, "").intValue();
			String evalVal = "" + val;
			modString.append(evalVal);
			modString.append(aValue.substring(endInd + 5));
			ret = modString.toString();

			evalInd = ret.indexOf("EVAL(");
		}

		return ret;
	}

	/**
	 * Test applying a kit
	 * @param aKit The owning kit for this item
	 * @param aPC The character the kit is being applied to
	 * @param warnings A list of warnings generated while attempting to apply the kit
	 * @return true if OK
	 */
	public abstract boolean testApply(Kit aKit, PlayerCharacter aPC, List warnings);

	/**
	 * Apply Kit
	 * @param aPC The character to apply the kit to.
	 */
	public abstract void apply(PlayerCharacter aPC);

	/**
	 * Get object name
	 * @return object name
	 */
	public abstract String getObjectName();

	class Range
	{
		private String lowValue = "" + Integer.MIN_VALUE;
		private String highValue = "" + Integer.MAX_VALUE;

		/**
		 * Constructor
		 * @param lowVal
		 * @param highVal
		 */
		public Range(String lowVal, String highVal)
		{
			lowValue = lowVal;
			highValue = highVal;
		}

		/**
		 * True if value falls within a range
		 * @param pc
		 * @param value
		 * @return True if value falls within a range
		 */
		public boolean isIn(PlayerCharacter pc, int value)
		{
			int lv = pc.getVariableValue(lowValue, "").intValue();
			int hv = pc.getVariableValue(highValue, "").intValue();
			if (value >= lv && value <= hv)
				return true;
			return false;
		}
	}
}
