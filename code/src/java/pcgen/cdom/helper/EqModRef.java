/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.EquipmentModifier;

/**
 * An EqModRef represents a reference to a Specific EquipmentModifier with set
 * choices.
 * <p>
 * This is typically used for tokens where an EquipmentModifier is directly
 * granted, or a selection of only a specific choice for an EquipmentModifier is
 * allowed.
 */
public class EqModRef
{

    /**
     * A reference to the EquipmentModifier which this EqModRef contains
     */
    private final CDOMSingleRef<EquipmentModifier> eqMod;

    /**
     * The specific choices (associations) for the EquipmentModifier this
     * EqModRef contains. May remain null if the given EquipmentModifier does
     * not have a specific choice (or does not require a specific choice)
     */
    private List<String> choices = null;

    /**
     * Constructs a new EqModRef for the EquipmentModifier in the given
     * reference.
     *
     * @param modRef A reference to the EquipmentModifier which this EqModRef
     *               contains
     */
    public EqModRef(CDOMSingleRef<EquipmentModifier> modRef)
    {
        eqMod = modRef;
    }

    /**
     * Adds the specific choice (association) for the EquipmentModifier this
     * EqModRef contains.
     *
     * @param choice The specific choice (association) that should be added for the
     *               EquipmentModifier this EqModRef contains.
     */
    public void addChoice(String choice)
    {
        if (choices == null)
        {
            choices = new LinkedList<>();
        }
        choices.add(choice);
    }

    /**
     * Returns the reference to the EquipmentModifier that this EqModRef
     * contains
     *
     * @return The reference to the EquipmentModifier that this EqModRef
     * contains
     */
    public CDOMSingleRef<EquipmentModifier> getRef()
    {
        return eqMod;
    }

    /**
     * Returns the specific choices (associations) for the EquipmentModifier
     * this EqModRef contains.
     * <p>
     * This method is value-semantic in that ownership of the returned List is
     * transferred to the class calling this method. Modification of the
     * returned List will not modify this EqModRef and modification of this
     * EqModRef will not modify the returned List.
     * <p>
     * This method will not return null, even if addChoice was never called on
     * this EqModRef.
     *
     * @return The specific choices (associations) for the EquipmentModifier
     * this EqModRef contains.
     */
    public List<String> getChoices()
    {
        return (choices == null ? Collections.emptyList() : new ArrayList<>(choices));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof EqModRef)
        {
            EqModRef other = (EqModRef) obj;
            if (other.eqMod.equals(eqMod))
            {
                if (choices == null)
                {
                    return other.choices == null;
                } else
                {
                    return choices.equals(other.choices);
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return 3 - eqMod.hashCode();
    }

}
