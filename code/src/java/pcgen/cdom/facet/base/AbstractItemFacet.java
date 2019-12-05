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
package pcgen.cdom.facet.base;

import pcgen.cdom.base.PCGenIdentifier;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.util.Logging;

/**
 * An AbstractItemFacet is a DataFacet that contains information about Objects
 * that are contained in a PlayerCharacter when a PlayerCharacter may have only
 * one of that type of Object (e.g. Race, Deity). This is not used for Objects
 * where the PlayerCharacter may possess more than one of that type of object
 * (e.g. Template, Language)
 *
 * @param <IDT> The Type of identifier used in this AbstractItemFacet
 * @param <T>   The Type of object stored in this AbstractItemFacet
 */
public abstract class AbstractItemFacet<IDT extends PCGenIdentifier, T> extends AbstractDataFacet<IDT, T>
{
    /**
     * Sets the item for this AbstractItemFacet and the Player Character
     * represented by the given PCGenIdentifier to the given value.
     * <p>
     * Note that a null set value is IGNORED, and an error is logged. If you
     * wish to unset a value, you should use the remove(PCGenIdentifier id)
     * method of AbstractItemFacet
     *
     * @param id  The PCGenIdentifier representing the Player Character for
     *            which the item value should be set
     * @param obj The Item for this AbstractItemFacet and the Player Character
     *            represented by the given PCGenIdentifier.
     * @return true if the item was set; false otherwise
     * @see AbstractItemFacet#remove(PCGenIdentifier)
     */
    public boolean set(IDT id, T obj)
    {
        if (obj == null)
        {
            Logging.errorPrint(getClass() + " received null item: ignoring");
            return false;
        }
        T old = get(id);
        if (old == obj)
        {
            return false;
        } else
        {
            if (old != null)
            {
                fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
            }
            setCache(id, obj);
            fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);

            return true;
        }
    }

    /**
     * Removes the item for this AbstractItemFacet and the Player Character
     * represented by the given PCGenIdentifier. May return null if no value was
     * set for the Player Character identified by the given PCGenIdentifier.
     *
     * @param id The PCGenIdentifier representing the Player Character for
     *           which the item value should be removed
     */
    public T remove(IDT id)
    {
        @SuppressWarnings("unchecked")
        T old = (T) removeCache(id);
        if (old != null)
        {
            fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
        }
        return old;
    }

    /**
     * Returns the item value for this AbstractItemFacet and the Player
     * Character represented by the given PCGenIdentifier. Note that this method
     * will return null if no value for the Player Character has been set.
     *
     * @param id The PCGenIdentifier representing the PlayerCharacter for which
     *           the item should be returned.
     * @return the item value for this AbstractItemFacet and the Player
     * Character represented by the given PCGenIdentifier.
     */
    @SuppressWarnings("unchecked")
    public T get(IDT id)
    {
        return (T) getCache(id);
    }

    /**
     * Returns true if the item in this AbstractItemFacet for the Player
     * Character represented by the given PCGenIdentifier matches the given
     * value. null may be used to test that there is no set value for this
     * AbstractItemFacet and the Player Character represented by the given
     * PCGenIdentifier.
     *
     * @param id  The PCGenIdentifier representing the Player Character for
     *            which the item should be tested
     * @param obj The object to test against the item in this AbstractItemFacet
     *            for the Player Character represented by the given
     *            PCGenIdentifier
     * @return true if the item in this AbstractItemFacet for the Player
     * Character represented by the given PCGenIdentifier matches the
     * given value; false otherwise
     */
    public boolean matches(IDT id, T obj)
    {
        T current = get(id);
        return ((obj == null) && (current == null)) || ((obj != null) && obj.equals(current));
    }

    /**
     * Copies the contents of the AbstractItemFacet from one Player Character to
     * another Player Character, based on the given PCGenIdentifiers
     * representing those Player Characters.
     * <p>
     * This is a method in AbstractItemFacet in order to avoid exposing the
     * internal contents of AbstractItemFacet to other classes. This should not
     * be inlined, as the internal information should not be exposed to other
     * classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given PCGenIdentifiers
     * (meaning once this copy takes place, any change to the AbstractItemFacet
     * of one Player Character will only impact the Player Character where the
     * AbstractItemFacet was changed).
     *
     * @param source The PCGenIdentifier representing the Player Character from
     *               which the information should be copied
     * @param copy   The PCGenIdentifier representing the Player Character to which
     *               the information should be copied
     */
    @Override
    public void copyContents(IDT source, IDT copy)
    {
        T obj = get(source);
        if (obj != null)
        {
            setCache(copy, obj);
        }
    }
}
