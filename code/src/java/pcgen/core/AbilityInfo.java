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
 * Current Version: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 * Copyright 2005 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;

/**
 * This tiny little class replaces a simple string representation of an Ability.
 * Since the move to Abilities, we can no longer look up these up based solely
 * on name, we now also need category. This also allows for a set of
 * Prerequisites to be associated with this AbilityInfo Object and can check
 * whether a given PC qualifies.
 * 
 * @author Andrew Wilson <nuance@sourceforge.net>
 */
public class AbilityInfo implements Comparable<Object>, Categorisable
{
	protected String keyName;
	protected String category;
	private Ability realThing;
	private List<Prerequisite> prereqList;
	private ArrayList<String> decorations;
	protected char delim = '<';

	private static final String split1 = "[<>\\|]";
	private static final String split2 = "[\\[\\]\\|]";

	/**
	 * Make a new object to hold minimal info about Abilities
	 * 
	 * @param category
	 *            the Ability's category
	 * @param key
	 *            the Key of the Ability
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
		this.keyName = "";
	}

	/**
	 * Get the Ability Object that this was a proxy for
	 * 
	 * @return Returns the Ability.
	 */
	public Ability getAbility()
	{
		if (realThing == null)
		{
			realThing = AbilityUtilities.retrieveAbilityKeyed(this.category,
				this.keyName);
			decorations = new ArrayList<String>();

			if ((realThing != null)
				&& (!realThing.getKeyName().equals(this.keyName)))
			{
				// get the decorations, throw away the name (because we already
				// have it in keyname)
				EquipmentUtilities
					.getUndecoratedName(this.keyName, decorations);
			}
		}

		return realThing;
	}

	/**
	 * Return an iterator over any Choices made for the Ability represented
	 * 
	 * @return an iterator
	 */
	public Iterator<String> getChoicesIterator()
	{
		List<String> returnList;
		if (getAbility() != null)
		{
			returnList = decorations;
		}
		else
		{
			returnList = Collections.emptyList();
		}
		return returnList.iterator();

	}

	/**
	 * Get the category of the Ability this AbilityInfo object represents
	 * 
	 * @return Returns the category.
	 */
	public final String getCategory()
	{
		return category;
	}

	/**
	 * Get the key of the Ability this AbilityInfo object represents
	 * 
	 * @return Returns the key.
	 */
	public final String getKeyName()
	{
		return keyName;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @return a String representation of this AbilityInfo
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return this.keyName;
	}

	/**
	 * Extract the key and any prerequisites that this Ability has, store them
	 * in the object's fields
	 * 
	 * @param unparsed
	 */
	protected void extractPrereqs(String unparsed)
	{
		int start = unparsed.indexOf(delim);

		if ((start < 0))
		{
			// no Prereqs, assign directly to key field
			this.keyName = unparsed;
		}
		else
		{
			if (prereqList == null)
			{
				prereqList = new ArrayList<Prerequisite>();
			}

			List<String> tokens = Arrays.asList(unparsed
				.split(delim == '<' ? split1 : split2));
			Iterator<String> tokIt = tokens.iterator();

			// extract and assign the choice from the unparsed string
			this.keyName = tokIt.next();

			try
			{
				final PreParserFactory factory = PreParserFactory.getInstance();

				for (; tokIt.hasNext();)
				{
					final Prerequisite prereq = factory.parse(tokIt.next());

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
	 * @param pc
	 *            The Player Character to test the prerequisites against.
	 * 
	 * @return whether the PC qualifies
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
	 * Compares this AbilityInfo Object with an Object passed in. The object
	 * passed in should be either an AbilityInfo Object or a PObject.
	 * 
	 * @param obj
	 *            the object to test against
	 * 
	 * @return the result of the compare, negative integer if this should sort
	 *         before
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
			return ((AbilityInfo) obj).getKeyName()
				.compareTo(this.getKeyName());
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
				// If this can't be converted to a PObject then they aren't
				// comparable and
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
	 * @return the name of the object
	 */
	public String getDisplayName()
	{
		return this.getKeyName();
	}
}
