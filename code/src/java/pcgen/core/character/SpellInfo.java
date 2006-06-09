/*
 * SpellInfo.java
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * @author Bryan McRoberts <merton_monk@yahoo.com>
 * Created on July 10, 2002, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.character;

import pcgen.core.Globals;

import java.util.ArrayList;
import java.util.List;
import pcgen.core.Ability;

/**
 * <code>SpellInfo</code>
 * this is a helper-class for CharacterSpell
 * meant to contain the book, whether or not this spell
 * is in the specialtySlot for characters which have them,
 * and the list of meta-magic feats which have been applied.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class SpellInfo
{
	private CharacterSpell owner;
	private List<Ability> featList; // a List of Feat objects
	private String book = Globals.getDefaultSpellBook(); // name of book
	private int actualLevel = -1;
	private int times; // times the spell is in this list
	private int actualPPCost = -1;
	private int numPages = 0;

	/**
	 * added package-private constructor to enforce usage of public constructor
	 */
	SpellInfo()
	{
		// Empty Constructor
	}

	SpellInfo(final CharacterSpell owner, final int actualLevel,
			final int times, final String book)
	{
		this.owner = owner;
		this.actualLevel = actualLevel;
		this.times = times;

		//
		// use the default book
		//
		if (book != null)
		{
			this.book = book;
		}
	}

	public int getActualLevel()
	{
		return actualLevel;
	}

	public void setActualPPCost(final int argActualPPCost)
	{
		actualPPCost = argActualPPCost;
	}

	public int getActualPPCost()
	{
		return actualPPCost;
	}

	public String getBook()
	{
		return book;
	}

	public List<Ability> getFeatList()
	{
		return featList;
	}

	public CharacterSpell getOwner()
	{
		return owner;
	}

	public void setTimes(final int times)
	{
		this.times = times;
	}

	public int getTimes()
	{
		return times;
	}

	public final int getNumPages()
	{
		return numPages;
	}

	public final void setNumPages(int numPages)
	{
		this.numPages = numPages;
	}

	public void addFeatsToList(final List<Ability> aList)
	{
		if (featList == null)
		{
			featList = new ArrayList<Ability>(aList.size());
		}

		featList.addAll(aList);
	}

	public String toString()
	{
		if (featList == null || featList.isEmpty())
		{
			return "";
		}

		final StringBuffer aBuf = new StringBuffer(" [" + featList.get(0).toString());

		for (int i = 1; i < featList.size(); i++)
		{
			aBuf.append(", ").append(featList.get(i).toString());
		}

		aBuf.append("] ");

		return aBuf.toString();
	}
}
