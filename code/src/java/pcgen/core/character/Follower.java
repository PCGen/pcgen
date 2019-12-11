/*
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
 */
package pcgen.core.character;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.list.CompanionList;
import pcgen.core.Race;

/**
 * {@code Follower.java}
 **/
public final class Follower implements Comparable<Object>, Cloneable
{
	/*
	 * the Structure of each Follower is as follows:
	 *
	 * FOLLOWER:name:type:race:HD:/path/to/some.pcg
	 *
	 * String name = name of the follower
	 * String type = Familiars, Mounts, Followers
	 * String race = race of follower
	 * int HD = Number of "used" HD
	 * String fileName = path and file name
	 */
	private String fileName;
	private String name;
	private Race race = null;
	private CompanionList type;
	private int usedHD;
	private int theAdjustment = 0;

	/**
	 * Constructor
	 * @param fName
	 * @param aName
	 * @param aType
	 */
	public Follower(final String fName, final String aName, final CompanionList aType)
	{
		fileName = fName;
		name = aName;
		type = aType;
	}

	/**
	 * Set file name
	 * @param x
	 */
	public void setFileName(final String x)
	{
		fileName = x;
	}

	/**
	 * Get file name
	 * @return file name
	 */
	public String getFileName()
	{
		return fileName;
	}

	/**
	 * Set name
	 * @param x
	 */
	public void setName(final String x)
	{
		name = x;
	}

	/**
	 * Get name
	 * @return name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set race
	 * @param x
	 */
	public void setRace(final Race x)
	{
		race = x;
	}

	/**
	 * Get race
	 * @return race
	 */
	public Race getRace()
	{
		return race;
	}

	/**
	 * Set type
	 * @param x
	 */
	public void setType(final CompanionList x)
	{
		type = x;
	}

	/**
	 * Get type
	 * @return type
	 */
	public CompanionList getType()
	{
		return type;
	}

	/**
	 * Set used HD
	 * @param x
	 */
	public void setUsedHD(final int x)
	{
		usedHD = x;
	}

	/**
	 * Get the HD used
	 * @return HD Used
	 */
	public int getUsedHD()
	{
		return usedHD;
	}

	public void setAdjustment(final int anAdjustment)
	{
		theAdjustment = anAdjustment;
	}

	public int getAdjustment()
	{
		return theAdjustment;
	}

	@Override
	public int compareTo(final Object obj)
	{
		final Follower aF = (Follower) obj;

		// Needs to be case-sensitive for filenames.
		return fileName.compareTo(aF.fileName);
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public Follower clone()
	{
		try
		{
			return (Follower) super.clone();
		}
		catch (CloneNotSupportedException exc)
		{
			throw new UnreachableError(exc);
		}
	}
}
