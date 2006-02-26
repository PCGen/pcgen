/*
 * AbilityInfo.java
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
 * Current Version: $Revision: 1.7 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:34 $
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core;

import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.utils.CoreUtility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This tiny little class replaces a simple string representation of an Ability.
 * Since the move to Abilities, we can no longer look up these up based solely
 * on name, we now also need category.  This also allows for a set of
 * Prerequisites to be associated with this AbilityInfo Object and can check
 * whether a given PC qualifies.
 *
 * @author  Andrew Wilson <nuance@sourceforge.net>
 */
public class AbilityInfo extends Object implements Comparable, Categorisable
{
	private String       keyName;
	private final String category;
	private Ability      realThing;
	private List         prereqList;

	/**
	 * Make a new object to hold minimal info about Abilities
	 *
	 * @param  category  the Ability's category
	 * @param  key       the Key of the Ability
	 */
	public AbilityInfo(String category, String key)
	{
		super();

		this.category = category;
		this.extractPrereqs(key);
	}

	/**
	 * Creates a new AbilityInfo object.
	 */
	public AbilityInfo()
	{
		super();

		this.category = "";
		this.keyName  = "";
	}

	/**
	 * Get the Ability Object that this was a proxy for
	 *
	 * @return  Returns the Ability.
	 */
	public final Ability getAbility()
	{
		if (realThing == null)
		{
			realThing = AbilityUtilities.retrieveAbilityKeyed(this.category, this.keyName);
			// Globals.getAbilityKeyed(this.category, this.keyName);
		}

		return realThing;
	}

	/**
	 * Get the category of the Ability this AbilityInfo object represents
	 *
	 * @return  Returns the category.
	 */
	public final String getCategory()
	{
		return category;
	}

	/**
	 * Get the key of the Ability this AbilityInfo object represents
	 *
	 * @return  Returns the key.
	 */
	public final String getKeyName()
	{
		return keyName;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @return  a String representation of this AbilityInfo
	 *
	 * @see     java.lang.Object#toString()
	 */
	public String toString()
	{
		return this.keyName;
	}

	/**
	 * Extract the key and any prerequisites that this Ability has, store them
	 * in the object's fields
	 *
	 * @param  unparsed  "Item name[PRE1|PRE2|...|PREn]"
	 */
	private void extractPrereqs(String unparsed)
	{
		int start = unparsed.indexOf('[');

		if ((start < 0) || !unparsed.endsWith("]"))
		{
			// no Prereqs, assign directly to key field
			this.keyName = unparsed;
		}
		else
		{
			// extract and assign the key from the unparsed key, prereq parameter
			this.keyName = unparsed.substring(0, start);

			int end = unparsed.length() - 1;

			// extract the list of prereq strings
			final String prereqString = unparsed.substring(start, end);
			final List   preString    = CoreUtility.split(prereqString, '|');

			if (prereqList == null)
			{
				prereqList = new ArrayList();
			}

			try
			{
				final PreParserFactory factory = PreParserFactory.getInstance();

				for (Iterator it = preString.iterator(); it.hasNext();)
				{
					final Prerequisite prereq = factory.parse((String) it.next());

					if (prereq != null)
					{
						prereqList.add(prereq);
					}
				}
			}
			catch (PersistenceLayerException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Does the PC qualify to take this Ability
	 *
	 * @param   pc  The Player Character to test the prerequisites against.
	 *
	 * @return  whether the PC qualifies
	 */
	public boolean qualifies(PlayerCharacter pc)
	{
		if (prereqList == null)
		{
			return true;
		}

		return PrereqHandler.passesAll(prereqList, pc, this.getAbility());
	}

	/**
	 * Compares this AbilityInfo Object with an Object passed in.  The object
	 * passed in should be either an AbilityInfo Object or a PObject.
	 *
	 * @param   obj  the object to test against
	 *
	 * @return  the result of the compare, negative integer if this should sort
	 *          before
	 */
	public int compareTo(Object obj)
	{
		String otherCat = this.category;

		try
		{
			otherCat = ((AbilityInfo) obj).getCategory();

			if (otherCat.compareTo(this.category) != 0)
			{
				return otherCat.compareTo(this.getCategory());
			}
			return ((AbilityInfo) obj).getKeyName().compareTo(this.getKeyName());
		}
		catch (ClassCastException e)
		{
			try
			{
				Ability ab = (Ability) obj;

				return this.getAbility().compareTo(ab);
			}
			catch (ClassCastException ex)
			{
				// If this can't be converted to a PObject then they aren't comparable and
				// Should throw the ClassCastException
				PObject pObj = (PObject) obj;

				return this.keyName.compareTo(pObj.getKeyName());
			}
		}
	}

	/**
	 * this is only here so that this implements all the methods of
	 * Categorisable
	 *
	 * @return  the name of the object
	 */
	public String getName()
	{
		return this.getKeyName();
	}
}
