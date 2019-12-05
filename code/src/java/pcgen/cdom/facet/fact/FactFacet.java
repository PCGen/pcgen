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
package pcgen.cdom.facet.fact;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.PCStringKey;
import pcgen.cdom.facet.base.AbstractStorageFacet;

/**
 * FactFacet stores basic String information about a Player Character.
 */
public class FactFacet extends AbstractStorageFacet<CharID>
{

    /**
     * Returns the type-safe Map for this FactFacet and the given CharID. Will
     * return a new, empty Map if no information has been set in this FactFacet
     * for the given CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * FactFacet, and since it can be modified, a reference to that object
     * should not be exposed to any object other than FactFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID.
     */
    private Map<PCStringKey, String> getConstructingInfo(CharID id)
    {
        Map<PCStringKey, String> rci = getInfo(id);
        if (rci == null)
        {
            rci = new HashMap<>();
            setCache(id, rci);
        }
        return rci;
    }

    /**
     * Returns the type-safe Map for this FactFacet and the given CharID. May
     * return null if no information has been set in this FactFacet for the
     * given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * FactFacet, and since it can be modified, a reference to that object
     * should not be exposed to any object other than FactFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Map for the Player Character represented by the given CharID;
     * null if no information has been set in this FactFacet for the
     * Player Character.
     */
    @SuppressWarnings("unchecked")
    private Map<PCStringKey, String> getInfo(CharID id)
    {
        return (Map<PCStringKey, String>) getCache(id);
    }

    /**
     * Sets a String to be contained in the FactFacet for the given StringKey
     * and Player Character identified by the given CharID. null is a legal
     * value in order to "unset" a String.
     *
     * @param id  The CharID identifying the Player Character for which the
     *            String should be set
     * @param key The StringKey identifying which String should be set
     * @param s   The value of the String to be contained in the FactFacet for
     *            the given StringKey and Player Character identified by the
     *            given CharID
     */
    public void set(CharID id, PCStringKey key, String s)
    {
        getConstructingInfo(id).put(key, s);
    }

    /**
     * Returns a String contained in the FactFacet for the given StringKey and
     * Player Character identified by the given CharID. May return null if no
     * String is contained in the FactFacet for the StringKey and the Player
     * Character identified by the given CharID.
     *
     * @param id  The CharID identifying the Player Character for which the
     *            String should be returned
     * @param key The StringKey identifying which String contained in the
     *            FactFacet should be returned
     * @return A String contained in the FactFacet for the given StringKey and
     * Player Character identified by the given CharID
     */
    public String get(CharID id, PCStringKey key)
    {
        Map<PCStringKey, String> rci = getInfo(id);
        if (rci != null)
        {
            return rci.get(key);
        }
        return null;
    }

    /**
     * Copies the contents of the FactFacet from one Player Character to another
     * Player Character, based on the given CharIDs representing those Player
     * Characters.
     * <p>
     * This is a method in FactFacet in order to avoid exposing the mutable Map
     * object to other classes. This should not be inlined, as the Map is
     * internal information to FactFacet and should not be exposed to other
     * classes.
     * <p>
     * Note also the copy is a one-time event and no references are maintained
     * between the Player Characters represented by the given CharIDs (meaning
     * once this copy takes place, any change to the FactFacet of one Player
     * Character will only impact the Player Character where the FactFacet was
     * changed).
     *
     * @param source      The CharID representing the Player Character from which the
     *                    information should be copied
     * @param destination The CharID representing the Player Character to which the
     *                    information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID destination)
    {
        Map<PCStringKey, String> sourceRCI = getInfo(source);
        if (sourceRCI != null)
        {
            getConstructingInfo(destination).putAll(sourceRCI);
        }
    }

}
