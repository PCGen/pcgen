/*
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
package pcgen.core;

import java.net.URI;

import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.Loadable;

/**
 * {@code Campaign}.
 * A simple structure.
 */
public final class ClassType implements Cloneable, Loadable
{
    /**
     * The source URI of this RollMethod.
     */
    private URI sourceURI;

    /**
     * The name of this ClassType.
     */
    private String theName = "";
    private String crFormula = "";
    private String crMod = "";
    private int crModPriority = 0;
    private boolean xpPenalty = true;
    private boolean isMonster = false;

    /**
     * Get the name of the class type.
     *
     * @return name of the class type
     */
    public String getName()
    {
        return theName;
    }

    /**
     * Set the name of the class type.
     *
     * @param aName
     */
    @Override
    public void setName(final String aName)
    {
        theName = aName;
    }

    /**
     * Set the CR Formula.
     *
     * @param crFormula
     */
    public void setCRFormula(final String crFormula)
    {
        this.crFormula = crFormula;
    }

    /**
     * Get the CR formula.
     *
     * @return CR Formula
     */
    public String getCRFormula()
    {
        return crFormula;
    }

    /**
     * Set the CR Mod.
     *
     * @param crMod
     */
    public void setCRMod(final String crMod)
    {
        this.crMod = crMod;
    }

    /**
     * Get the CR Mod.
     *
     * @return CR Mod
     */
    public String getCRMod()
    {
        return crMod;
    }

    /**
     * Set the CR Mod priority.
     *
     * @param crModPriority
     */
    public void setCRModPriority(final int crModPriority)
    {
        this.crModPriority = crModPriority;
    }

    /**
     * Get the CR Mod priority.
     *
     * @return CR Mod priority
     */
    public int getCRModPriority()
    {
        return crModPriority;
    }

    /**
     * Set the monster.
     *
     * @param monster
     */
    public void setMonster(final boolean monster)
    {
        isMonster = monster;
    }

    /**
     * Answer if this is a monster.
     *
     * @return TRUE if it is a monster
     */
    public boolean isMonster()
    {
        return isMonster;
    }

    /**
     * Set the XP penalty.
     *
     * @param xpPenalty
     */
    public void setXPPenalty(final boolean xpPenalty)
    {
        this.xpPenalty = xpPenalty;
    }

    /**
     * Get the XP penalty.
     *
     * @return true if there is a penalty
     */
    public boolean getXPPenalty()
    {
        return xpPenalty;
    }

    @Override
    public ClassType clone()
    {
        try
        {
            return (ClassType) super.clone();
        } catch (CloneNotSupportedException e)
        {
            throw new UnreachableError(e);
        }
    }

    @Override
    public URI getSourceURI()
    {
        return sourceURI;
    }

    @Override
    public void setSourceURI(URI source)
    {
        sourceURI = source;
    }

    @Override
    public String getDisplayName()
    {
        return theName;
    }

    @Override
    public String getKeyName()
    {
        return getDisplayName();
    }

    @Override
    public boolean isInternal()
    {
        return false;
    }

    @Override
    public boolean isType(String type)
    {
        return false;
    }
}
