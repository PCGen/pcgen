/*
 * AssociatedChoice.java
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core;

import java.util.Collection;
import java.util.HashMap;

/**
 * This class represents the result of a single selection from a chooser.  The
 * class implements a key-based store so that multiple values can be returned
 * for a single choice.  Functionality is provided to work with the default
 * choice to maintain backward compatibility.
 *
 * @author Aaron Divinsky (boomer70)
 * @version $Revision$
 * @param <T> 
 */
public class AssociatedChoice <T extends Comparable> implements Comparable
{
	/** A map of key, value pairs for this choice. */
	protected HashMap<String, T> choices = new HashMap<String, T>();

	/** The "default" key to allow functionality to pretend it is a single
	 * string value. */
	public static final String DEFAULT_KEY = "CHOICE";

	/**
	 * Empty constructor.  No choices are associated yet.
	 */
	public AssociatedChoice()
	{
        // Do Nothing
	}

	/**
	 * Constructs a simple choice object with a single choice
	 * @param aChoice The selected item to associate
	 */
	public AssociatedChoice( final T aChoice )
	{
		// Convience method to add just a single choice
		choices.put( DEFAULT_KEY, aChoice );
	}

	/**
	 * Adds a list of choices keyed by the order returned by the collection's
	 * iterator.
	 * @param aGroup A list of choices to associate
	 */
	public AssociatedChoice( final Collection<T> aGroup )
	{
		int count = 0;
		for ( T val : aGroup )
		{
			choices.put( String.valueOf(++count), val );
		}
	}

	/**
	 * Construct a choice with a specific key
	 * @param aKey Key used to reference this choice option
	 * @param aChoice The value to associate with this choice key
	 */
	public AssociatedChoice( final String aKey, final T aChoice )
	{
		choices.put( aKey, aChoice );
	}

	/**
	 * Adds a choice to the default key
	 * @param aChoice The value to associate
	 */
	public void addChoice( final T aChoice )
	{
		choices.put( DEFAULT_KEY, aChoice );
	}

	/**
	 * Adds a choice with the specified key
	 * @param aKey Key to use for this choice
	 * @param aChoice Value of the choice
	 */
	public void addChoice( final String aKey, final T aChoice )
	{
		choices.put( aKey, aChoice );
	}

	/**
	 * Get the choice for the specified key
	 * @param aKey Key to retreive choice for
	 * @return The choice associated with the specified key
	 */
	public T getChoice( final String aKey )
	{
		return choices.get( aKey );
	}

	/**
	 * Returns the choice for the "default" key.
	 * @return The choice or null if no choice was set.
	 */
	public T getDefaultChoice()
	{
		return choices.get( DEFAULT_KEY );
	}

	/**
	 * Returns all the values for all keys in the choice.
	 * @return Collection of values.
	 */
	public Collection<T> getChoices()
	{
		return choices.values();
	}

	/**
	 * Clears the choice associated with the specified key.
	 * @param aKey The key to clear.
	 * @return True if the choice was cleared.
	 */
	public boolean remove( final String aKey )
	{
		return choices.remove( aKey ) == null ? false : true;
	}

	/**
	 * Clears the default choice if it matches the specified choice.
	 * @param aChoice A choice to match against.
	 * @return True if the choice was cleared, false otherwise.
	 */
	public boolean removeDefaultChoice( final T aChoice )
	{
		T result = choices.get( DEFAULT_KEY );
		if ( result != null )
		{
			if ( result.equals( aChoice ) )
			{
				choices.remove( DEFAULT_KEY );
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of choices made
	 * @return Number of choices
	 */
	public int size()
	{
		return choices.size();
	}

	public int compareTo(Object o)
	{
		AssociatedChoice<T> other = (AssociatedChoice<T>)o;

		T defaultValue = getDefaultChoice();
		if ( defaultValue != null )
		{
			T otherDefault = other.getDefaultChoice();
			if ( otherDefault != null )
			{
				return defaultValue.compareTo(otherDefault);
			}
		}
		return 1;
	}
}
