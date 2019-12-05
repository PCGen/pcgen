/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.inst;

import java.util.Objects;
import java.util.Optional;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.analysis.ResultFacet;
import pcgen.cdom.formula.scope.EquipmentPartScope;

/**
 * An EquipmentHead is a CDOMObject that represents characteristics of a single
 * "head" of a weapon. It is possible for a weapon to have more than one "head",
 * such as a Double Axe.
 */
public final class EquipmentHead extends CDOMObject
{

    /*
     * Note: The equality issue referenced below (and the reason for the
     * headSource field) doesn't necessarily present itself within PCGen at SVN
     * 6700, but it is an issue if this is ever stored in a Graph, as the
     * EquipmentHead from two pieces of Equipment must not be shared, in case
     * .MODs are applied.
     */
    /**
     * The source of this EquipmentHead; used to establish equality
     */
    private final VarScoped headSource;

    /**
     * The index (location) of this Head on the Equipment
     */
    private final int index;

    /**
     * Creates a new EquipmentHead with the given source and index.
     *
     * @param source The source object for this EquipmentHead
     * @param idx    The index of this EquipmentHead for the given source
     * @throws IllegalArgumentException if the given source is null
     */
    public EquipmentHead(VarScoped source, int idx)
    {
        Objects.requireNonNull(source, "Source for EquipmentHead cannot be null");
        index = idx;
        headSource = source;
    }

    /**
     * Returns the index (location) of this Head on the Equipment
     *
     * @return the index (location) of this Head on the Equipment
     */
    public int getHeadIndex()
    {
        return index;
    }

    @Override
    public int hashCode()
    {
        return index ^ headSource.hashCode();
    }

    public Object getOwner()
    {
        return headSource;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof EquipmentHead))
        {
            return false;
        }
        EquipmentHead other = (EquipmentHead) obj;
        return other.index == index && other.headSource.equals(headSource);
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }

    @Override
    public Optional<String> getLocalScopeName()
    {
        return Optional.of(EquipmentPartScope.PC_EQUIPMENT_PART);
    }

    @Override
    public Optional<VarScoped> getVariableParent()
    {
        return Optional.of(headSource);
    }

    public Object getLocalVariable(CharID id, String varName)
    {
        ResultFacet resultFacet = FacetLibrary.getFacet(ResultFacet.class);
        return resultFacet.getLocalVariable(id, this, varName);
    }

}
