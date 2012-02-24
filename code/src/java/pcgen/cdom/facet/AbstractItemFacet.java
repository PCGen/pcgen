/*
 * Copyright (c) Thomas Parker, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import pcgen.cdom.enumeration.CharID;
import pcgen.util.Logging;

/**
 * An AbstractItemFacet is a DataFacet that contains information about Objects
 * that are contained in a PlayerCharacter when a PlayerCharacter may have only
 * one of that type of Object (e.g. Race, Deity). This is not used for Objects
 * where the PlayerCharacter may possess more than one of that type of object
 * (e.g. Template, Language)
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public abstract class AbstractItemFacet<T> extends AbstractDataFacet<T>
{
	private final Class<?> thisClass = getClass();

	/**
	 * Sets the item for this AbstractItemFacet and the Player Character
	 * represented by the given CharID to the given value.
	 * 
	 * Note that a null set value is IGNORED, and an error is logged. If you
	 * wish to unset a value, you should use the remove(CharID id) method of
	 * AbstractItemFacet
	 * 
	 * @see remove(CharID id)
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            item value should be set
	 * @param obj
	 *            The Item for this AbstractItemFacet and the Player Character
	 *            represented by the given CharID.
	 */
	public void set(CharID id, T obj)
	{
		if (obj == null)
		{
			Logging.errorPrint(thisClass + " received null item: ignoring");
			return;
		}
		T old = get(id);
		if (old != obj)
		{
			if (old != null)
			{
				fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
			}
			setCache(id, thisClass, obj);
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Removes the item for this AbstractItemFacet and the Player Character
	 * represented by the given CharID. May return null if no value was set for
	 * the Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            item value should be removed
	 */
	public void remove(CharID id)
	{
		T old = (T) removeCache(id, thisClass);
		if (old != null)
		{
			fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
		}
	}

	/**
	 * Returns the item value for this AbstractItemFacet and the Player
	 * Character represented by the given CharID. Note that this method will
	 * return null if no value for the Player Character has been set.
	 * 
	 * @param id
	 *            The CharID representing the PlayerCharacter for which the item
	 *            should be returned.
	 * @return the item value for this AbstractItemFacet and the Player
	 *         Character represented by the given CharID.
	 */
	public T get(CharID id)
	{
		return (T) getCache(id, thisClass);
	}

	/**
	 * Returns true if the item in this AbstractItemFacet for the Player
	 * Character represented by the given CharID matches the given value. null
	 * may be used to test that there is no set value for this AbstractItemFacet
	 * and the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            item should be tested
	 * @param obj
	 *            The object to test against the item in this AbstractItemFacet
	 *            for the Player Character represented by the given CharID
	 * @return true if the item in this AbstractItemFacet for the Player
	 *         Character represented by the given CharID matches the given
	 *         value; false otherwise
	 */
	public boolean matches(CharID id, T obj)
	{
		T current = get(id);
		return (obj == null && current == null)
				|| (obj != null && obj.equals(current));
	}

	/**
	 * Copies the contents of the AbstractItemFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in AbstractItemFacet in order to avoid exposing the
	 * internal contents of AbstractItemFacet to other classes. This should not
	 * be inlined, as the internal information should not be exposed to other
	 * classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AbstractItemFacet of one
	 * Player Character will only impact the Player Character where the
	 * AbstractItemFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		T obj = get(source);
		if (obj != null)
		{
			setCache(copy, thisClass, obj);
		}
	}
}
