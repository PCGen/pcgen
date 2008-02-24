/*
 * TimeUnit.java
 * Copyright 2008 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 * Created on 24/02/2008
 *
 * $Id$
 */
package pcgen.core;

import pcgen.util.PropertyFactory;

/**
 * <code>TimeUnit</code> stores and manages information about Time units.
 * 
 * <p>Used for spells currently, the time unit describes a block of time.
 * This is normally used to denote how often a spell of spell like ability
 * may be used e.g a number of times per day or week.
 * 
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 * @since 5.13.10
 */
public class TimeUnit implements KeyedObject
{

	/** The display name. */
	private String theDisplayName;

	/** The key name. */
	private String theKeyName;

	/** The sort order. */
	private int theOrder;

	/** A constant used to refer to the default time unit of DAY. */
	public static final TimeUnit DAY = new TimeUnit("DAY", "in_day"); //$NON-NLS-1$ //$NON-NLS-2$

	static
	{
		DAY.theOrder = 1; //$NON-NLS-1$
	}

	/**
	 * Constructs a new <tt>TimeUnit</tt> with the specified key.
	 * 
	 * <p>This method sets the display and plural names to the same value as
	 * the key name.
	 * 
	 * @param aKeyName The name to use to reference this time unit.
	 */
	public TimeUnit(final String aKeyName)
	{
		theKeyName = aKeyName;
		theDisplayName = aKeyName;
	}

	/**
	 * Constructor takes a key name and display name for the Time Unit.
	 * 
	 * @param aKeyName The name to use to reference this time unit.
	 * @param aDisplayName The resource key to use for the display name
	 */
	public TimeUnit(final String aKeyName, final String aDisplayName)
	{
		theKeyName = aKeyName;
		setName(aDisplayName);
	}

	/**
	 * Get the sorting order of this time unit.
	 * 
	 * @return the order
	 */
	int getOrder()
	{
		return theOrder;
	}

	/**
	 * Set the sorting order of this time unit.
	 * 
	 * @param order the order to set
	 */
	void setOrder(int order)
	{
		this.theOrder = order;
	}

	// -------------------------------------------
	// KeyedObject Support
	// -------------------------------------------
	/**
	 * Gets the display name.
	 * 
	 * @return the display name
	 * 
	 * @see pcgen.core.KeyedObject#getDisplayName()
	 */
	public String getDisplayName()
	{
		return theDisplayName;
	}

	/**
	 * Gets the key name.
	 * 
	 * @return the key name
	 * 
	 * @see pcgen.core.KeyedObject#getKeyName()
	 */
	public String getKeyName()
	{
		return theKeyName;
	}

	/**
	 * Sets the key name.
	 * 
	 * @param aKey the a key
	 * 
	 * @see pcgen.core.KeyedObject#setKeyName(java.lang.String)
	 */
	public void setKeyName(final String aKey)
	{
		theKeyName = aKey;
	}

	/**
	 * Sets the name.
	 * 
	 * @param aName the a name
	 * 
	 * @see pcgen.core.KeyedObject#setName(java.lang.String)
	 */
	public void setName(final String aName)
	{
		if (aName.startsWith("in_"))
		{
			theDisplayName = PropertyFactory.getString(aName);
		}
		else
		{
			theDisplayName = aName;
		}
	}

	/**
	 * Returns the display name for this category.
	 * 
	 * @return the string
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return theDisplayName;
	}

	/**
	 * Generates a hash code using the key, category and types.
	 * 
	 * @return the int
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result =
				PRIME * result
					+ ((theKeyName == null) ? 0 : theKeyName.hashCode());
		return result;
	}

	/**
	 * Equals.
	 * 
	 * @param obj the obj
	 * 
	 * @return true, if equals
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null || getClass() != obj.getClass())
		{
			return false;
		}

		final TimeUnit other = (TimeUnit) obj;
		if (theKeyName == null)
		{
			if (other.theKeyName != null)
			{
				return false;
			}
		}
		else if (!theKeyName.equals(other.theKeyName))
		{
			return false;
		}
		return true;
	}
}
