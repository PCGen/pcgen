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
package pcgen.core.npcgen;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.Constants;

/**
 * This is a base class for all of the configurable options for the generator.
 * 
 * <p>Options are represented by a name and a list of possible choices for that
 * options.  Each choice can have a weight specified.
 * 
 * 
 */
public abstract class GeneratorOption
{
	private String theName = Constants.EMPTY_STRING;

	/**
	 * Default constructor.
	 */
	public GeneratorOption()
	{
		// Do nothing
	}

	/**
	 * Sets the display name for this option.
	 * 
	 * @param aName The display name
	 */
	public void setName(final String aName)
	{
		theName = aName;
	}

	/**
	 * Gets the display name for this option
	 * 
	 * @return The name of the option or an empty string.
	 */
	public String getName()
	{
		return theName;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return theName;
	}

	/**
	 * Adds a potential choice to this option.
	 * 
	 * @param aWeight The weight to assign this choice.
	 * @param aChoice The String key for this choice.
	 */
	public abstract void addChoice( final int aWeight, final String aChoice );

	/**
	 * Gets a <tt>WeightedCollection</tt> of choices for this option.
	 * 
	 * @return A <tt>WeightedCollection</tt>.
	 */
	public abstract WeightedCollection<?> getList();
}
