/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pcgen.cdom.enumeration.Type;
import pcgen.core.character.EquipSlot;

/**
 * The Class {@code BodyStructure} represents a part of a character's
 * body that may hold equipment.
 */
public class BodyStructure
{

    private String name;
    private List<EquipSlot> slots;
    private boolean holdsAnyType;
    private Set<Type> forbiddenTypes;

    /**
     * Create a new BodyStructure instance.
     *
     * @param name The name of the body structure.
     */
    public BodyStructure(String name)
    {
        this(name, false);
    }

    /**
     * Create a new BodyStructure instance noting if it can hold any type
     * of equipment item. BodyStructures such as equipped, carried or not
     * carried may hold items of any type.
     *
     * @param name         The name of the body structure.
     * @param holdsAnyType Can this item hold anything at all
     */
    public BodyStructure(String name, boolean holdsAnyType)
    {
        this(name, holdsAnyType, null);
    }

    /**
     * Create a new BodyStructure instance noting if it can hold any type
     * of equipment item. BodyStructures such as equipped, carried or not
     * carried may hold items of any type.
     *
     * @param name           The name of the body structure.
     * @param holdsAnyType   Can this item hold anything at all
     * @param forbiddenTypes The exceptions to the 'holds any type' rule.
     */
    public BodyStructure(String name, boolean holdsAnyType, Set<Type> forbiddenTypes)
    {
        this.name = name;
        this.forbiddenTypes = new HashSet<>();
        if (forbiddenTypes != null)
        {
            this.forbiddenTypes.addAll(forbiddenTypes);
        }
        slots = new ArrayList<>();
        this.holdsAnyType = holdsAnyType;
    }

    /**
     * @return the name
     */
    String getName()
    {
        return name;
    }

    /**
     * Add an EquipSlot to the list of those contained by the body structure.
     *
     * @param slot The EquipSlot to be added
     */
    public void addEquipSlot(EquipSlot slot)
    {
        if (!slots.contains(slot))
        {
            slots.add(slot);
        }
    }

    /**
     * @return A read-only list of EquipSlots that are contained by this BodyStructure.
     */
    public List<EquipSlot> getEquipSlots()
    {
        return Collections.unmodifiableList(slots);
    }

    @Override
    public String toString()
    {
        return name;
    }

    /**
     * @return the holdsAnyType
     */
    public boolean isHoldsAnyType()
    {
        return holdsAnyType;
    }

    /**
     * Identify if the set of types contains any forbidden ones.
     *
     * @param types the types to be checked
     * @return true if any type is not allowed, false otherwise
     */
    public boolean isForbidden(Collection<Type> types)
    {
        for (Type type : types)
        {
            if (forbiddenTypes.contains(type))
            {
                return true;
            }
        }
        return false;
    }
}
