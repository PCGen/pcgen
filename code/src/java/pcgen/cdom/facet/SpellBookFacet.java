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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.core.Equipment;
import pcgen.core.character.SpellBook;

/**
 * SpellBookFacet is a Facet that tracks the SpellBooks possessed by a Player
 * Character.
 */
public class SpellBookFacet extends AbstractStorageFacet<CharID> implements DataFacetChangeListener<CharID, Equipment>
{
    private EquipmentFacet equipmentFacet;

    /**
     * Adds a SpellBook to this facet if the Equipment added to a Player
     * Character was a SpellBook.
     * <p>
     * Triggered when one of the Facets to which SpellBookFacet listens fires a
     * DataFacetChangeEvent to indicate a piece of Equipment was added to a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataAdded(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        Equipment eq = dfce.getCDOMObject();
        if (eq.isType(Constants.TYPE_SPELLBOOK))
        {
            CharID id = dfce.getCharID();
            String baseBookname = eq.getName();
            String bookName = eq.getName();
            int qty = (int) eq.qty();
            for (int i = 0;i < qty;i++)
            {
                if (i > 0)
                {
                    bookName = baseBookname + " #" + (i + 1);
                }
                SpellBook book = getBookNamed(id, bookName);
                if (book == null)
                {
                    book = new SpellBook(bookName, SpellBook.TYPE_SPELL_BOOK);
                }
                book.setEquip(eq);
                if (!containsBookNamed(id, book.getName()))
                {
                    add(id, book);
                }
            }
        }
    }

    /**
     * Triggered when one of the Facets to which SpellBookFacet listens fires a
     * DataFacetChangeEvent to indicate a piece of Equipment was removed from a
     * Player Character.
     *
     * @param dfce The DataFacetChangeEvent containing the information about the
     *             change
     */
    @Override
    public void dataRemoved(DataFacetChangeEvent<CharID, Equipment> dfce)
    {
        //Ignore - for now this is one in PlayerCharacter...
        /*
         * TODO This method should eventually be symmetric with dataAdded
         */
    }

    /**
     * Adds all of the SpellBooks in the given Collection to the list of
     * SpellBooks stored in this SpellBookFacet for the Player Character
     * represented by the given CharID.
     *
     * @param id   The CharID representing the Player Character for which the
     *             given SpellBooks should be added
     * @param list The Collection of SpellBooks to be added to the list of
     *             SpellBooks stored in this SpellBookFacet for the Player
     *             Character represented by the given CharID
     * @throws NullPointerException if the given Collection is null
     */
    public void addAll(CharID id, Collection<SpellBook> list)
    {
        for (SpellBook sb : list)
        {
            add(id, sb);
        }
    }

    /**
     * Add the given SpellBook to the list of SpellBooks stored in this
     * SpellBookFacet for the Player Character represented by the given CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           given item should be added
     * @param sb The SpellBook to be added to the list of SpellBooks stored in
     *           this SpellBookFacet for the Player Character represented by
     *           the given CharID
     */
    public void add(CharID id, SpellBook sb)
    {
        Objects.requireNonNull(sb, "Object to add may not be null");
        Map<String, SpellBook> sbMap = getConstructingCachedMap(id);
        String name = sb.getName();
        sbMap.put(name, sb);
    }

    /**
     * Removes all of the SpellBooks in the given Collection from the list of
     * SpellBooks stored in this SpellBookFacet for the Player Character
     * represented by the given CharID.
     *
     * @param id The CharID representing the Player Character from which the
     *           given SpellBooks should be removed
     * @throws NullPointerException if the given Collection is null
     */
    public void removeAll(CharID id)
    {
        removeCache(id);
    }

    /**
     * Returns the type-safe Map for this SpellBookFacet and the given CharID.
     * May return null if no information has been set in this SpellBookFacet for
     * the given CharID.
     * <p>
     * Note that this method SHOULD NOT be public. The Map is owned by
     * SpellBookFacet, and since it can be modified, a reference to that object
     * should not be exposed to any object other than SpellBookFacet.
     *
     * @param id The CharID for which the Set should be returned
     * @return The Set for the Player Character represented by the given CharID;
     * null if no information has been set in this
     * AbstractSourcedListFacet for the Player Character.
     */
    private Map<String, SpellBook> getCachedMap(CharID id)
    {
        return (Map<String, SpellBook>) getCache(id);
    }

    /**
     * Returns a type-safe Map for this SpellBookFacet and the given CharID.
     * Will return a new, empty Map if no information has been set in this
     * SpellBookFacet for the given CharID. Will not return null.
     * <p>
     * Note that this method SHOULD NOT be public. The Map object is owned by
     * SpellBookFacet, and since it can be modified, a reference to that object
     * should not be exposed to any object other than SpellBookFacet.
     *
     * @param id The CharID for which the Map should be returned
     * @return The Map for the Player Character represented by the given CharID.
     */
    private Map<String, SpellBook> getConstructingCachedMap(CharID id)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            componentMap = new LinkedHashMap<>();
            setCache(id, componentMap);
        }
        return componentMap;
    }

    /**
     * Returns the SpellBook for the given SpellBook name and the Player
     * Character identified by the given CharID.
     *
     * @param id   The CharID identifying the PlayerCharacter for which the
     *             SpellBook for the given name should be returned
     * @param name The name of the SpellBook to be returned
     * @return The SpellBook for the given SpellBook name and the Player
     * Character identified by the given CharID
     */
    public SpellBook getBookNamed(CharID id, String name)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return null;
        }
        return componentMap.get(name);
    }

    /**
     * Returns a non-null Collection of SpellBook names in this SpellBookFacet
     * for the Player Character represented by the given CharID. This method
     * returns an empty Set if no SpellBooks are in this SpellBookFacet for the
     * Player Character identified by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Modification
     * of the returned Collection will not modify this SpellBookFacet and
     * modification of this SpellBookFacet will not modify the returned
     * Collection. Modifications to the returned Collection will also not modify
     * any future or previous objects returned by this (or other) methods on
     * SpellBookFacet. If you wish to modify the information stored in this
     * SpellBookFacet, you must use the add*() and remove*() methods of
     * SpellBookFacet.
     *
     * @param id The CharID representing the Player Character for which a copy
     *           of the SpellBooks in this SpellBookFacet should be returned.
     * @return A non-null Collection of SpellBooks in this SpellBookFacet for
     * the Player Character represented by the given CharID
     */
    public Collection<String> getBookNames(CharID id)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(componentMap.keySet());
    }

    /**
     * Returns a non-null copy of the Collection of SpellBooks in this
     * SpellBookFacet for the Player Character represented by the given CharID.
     * This method returns an empty Set if no SpellBooks are in this
     * SpellBookFacet for the Player Character identified by the given CharID.
     * <p>
     * This method is value-semantic in that ownership of the returned
     * Collection is transferred to the class calling this method. Modification
     * of the returned Collection will not modify this SpellBookFacet and
     * modification of this SpellBookFacet will not modify the returned
     * Collection. Modifications to the returned Collection will also not modify
     * any future or previous objects returned by this (or other) methods on
     * SpellBookFacet. If you wish to modify the information stored in this
     * SpellBookFacet, you must use the add*() and remove*() methods of
     * SpellBookFacet.
     *
     * @param id The CharID representing the Player Character for which a copy
     *           of the SpellBooks in this SpellBookFacet should be returned.
     * @return A non-null Collection of SpellBooks in this SpellBookFacet for
     * the Player Character represented by the given CharID
     */
    public Collection<SpellBook> getBooks(CharID id)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        if (componentMap == null)
        {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(componentMap.values());
    }

    /**
     * Returns true if this SpellBookFacet contains a SpellBook of the given
     * name in the list of SpellBooks for the Player Character represented by
     * the given CharID.
     *
     * @param id   The CharID representing the Player Character used for testing
     * @param name The SpellBook name to test if this SpellBookFacet contains a
     *             SpellBook by that name for the Player Character represented by
     *             the given CharID
     * @return true if this SpellBookFacet contains a SpellBook with the given
     * name for the Player Character represented by the given CharID;
     * false otherwise
     */
    public boolean containsBookNamed(CharID id, String name)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        return (componentMap != null) && componentMap.containsKey(name);
    }

    /**
     * Returns the count of SpellBooks in this SpellBookFacet for the Player
     * Character represented by the given CharID.
     *
     * @param id The CharID representing the Player Character for which the
     *           count of SpellBooks should be returned
     * @return The count of SpellBooks in this SpellBookFacet for the Player
     * Character represented by the given CharID
     */
    public int getCount(CharID id)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        return (componentMap == null) ? 0 : componentMap.size();
    }

    /**
     * Removes the SpellBook with the given name from the list of SpellBooks
     * stored in this SpellBookFacet for the Player Character represented by the
     * given CharID.
     *
     * @param id   The CharID representing the Player Character from which the
     *             SpellBook with the given name should be removed
     * @param name The name of the SpellBook to be removed from the list of
     *             SpellBooks stored in this SpellBookFacet for the Player
     *             Character represented by the given CharID
     */
    public void removeBookNamed(CharID id, String name)
    {
        Map<String, SpellBook> componentMap = getCachedMap(id);
        if (componentMap != null)
        {
            componentMap.remove(name);
        }
    }

    public void setEquipmentFacet(EquipmentFacet equipmentFacet)
    {
        this.equipmentFacet = equipmentFacet;
    }

    /**
     * Initializes the connections for SpellBookFacet to other facets.
     * <p>
     * This method is automatically called by the Spring framework during
     * initialization of the SpellBookFacet.
     */
    public void init()
    {
        equipmentFacet.addDataFacetChangeListener(this);
    }

    /**
     * Copies the contents of the SpellBookFacet from one Player Character to
     * another Player Character, based on the given CharIDs representing those
     * Player Characters.
     * <p>
     * This is a method in SpellBookFacet in order to avoid exposing the mutable
     * Map object to other classes. This should not be inlined, as the Map is
     * internal information to SpellBookFacet and should not be exposed to other
     * classes.
     * <p>
     * Note also the copy is a one-time event and no SpellBook references are
     * maintained between the Player Characters represented by the given CharIDs
     * (meaning once this copy takes place, any change to the SpellBook will
     * only impact the Player Character where the SpellBook was changed).
     *
     * @param source The CharID representing the Player Character from which the
     *               information should be copied
     * @param copy   The CharID representing the Player Character to which the
     *               information should be copied
     */
    @Override
    public void copyContents(CharID source, CharID copy)
    {
        Map<String, SpellBook> map = getCachedMap(source);
        if (map != null)
        {
            addAll(copy, map.values());
        }
    }
}
