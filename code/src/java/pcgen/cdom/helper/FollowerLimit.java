/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.helper;

import java.util.Objects;

import pcgen.base.formula.Formula;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMSingleRef;

/**
 * A FollowerLimit represents an upper bound (stored as a Formula) on the number
 * of Companions a PlayerCharacter may have for a specific CompanionList.
 */
public class FollowerLimit
{

    /**
     * A reference to the CompanionList to which this FollowerLimit applies
     */
    private final CDOMSingleRef<CompanionList> ref;

    /**
     * The Formula that represents the upper bound on the number of Companions a
     * PlayerCharacter may have for the CompanionList.
     */
    private final Formula formula;

    /**
     * Creates a new FollowerLimit for the given CompanionList (provided by
     * reference), which is limited by the given Formula.
     *
     * @param clRef A reference to the CompanionList to which this FollowerLimit
     *              applies
     * @param limit The Formula that represents the upper bound on the number of
     *              Companions a PlayerCharacter may have for the CompanionList.
     */
    public FollowerLimit(CDOMSingleRef<CompanionList> clRef, Formula limit)
    {
        Objects.requireNonNull(clRef, "Reference for FollowerLimit cannot be null");
        Objects.requireNonNull(limit, "Formula for FollowerLimit cannot be null");
        ref = clRef;
        formula = limit;
    }

    /**
     * Returns a reference to the CompanionList to which this FollowerLimit
     * applies
     *
     * @return A reference to the CompanionList to which this FollowerLimit
     * applies
     */
    public CDOMSingleRef<CompanionList> getCompanionList()
    {
        return ref;
    }

    /**
     * Returns the Formula that represents the upper bound on the number of
     * Companions a PlayerCharacter may have for the CompanionList.
     *
     * @return The Formula that represents the upper bound on the number of
     * Companions a PlayerCharacter may have for the CompanionList.
     */
    public Formula getValue()
    {
        return formula;
    }

    @Override
    public int hashCode()
    {
        return ref.hashCode() * 31 + formula.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof FollowerLimit)
        {
            FollowerLimit other = (FollowerLimit) o;
            return ref.equals(other.ref) && formula.equals(other.formula);
        }
        return false;
    }

}
