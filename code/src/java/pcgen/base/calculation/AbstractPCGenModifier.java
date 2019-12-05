/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.calculation;

import java.util.Collection;
import java.util.Collections;

import pcgen.base.util.Indirect;
import pcgen.cdom.formula.AssociationUtilities;

/**
 * An AbstractPCGenModifier is a FormulaModifier that supports generalized management of
 * references.
 *
 * @param <T> The format that this AbstractPCGenModifier acts upon
 */
public abstract class AbstractPCGenModifier<T> implements FormulaModifier<T>
{

    /**
     * The user priority of this SetModifier. Stored as an Object so we can detect "null"
     * - meaning it was unset in the data and we should not write out PRIORITY=0
     */
    private Integer userPriority;

    /**
     * The object references referred to by the embedded AbstractPCGenModifier.
     * <p>
     * NOTE: DO NOT DELETE THIS EVEN THOUGH IT APPEARS UNUSED. Its use is holding the
     * references so that they are not garbage collected.
     */
    @SuppressWarnings("unused")
    private Collection<Indirect<?>> references;

    protected int getUserPriority()
    {
        return (userPriority == null) ? 0 : userPriority;
    }

    @Override
    public void addAssociation(String assocInstructions)
    {
        userPriority = AssociationUtilities.processUserPriority(assocInstructions);
    }

    @Override
    public Collection<String> getAssociationInstructions()
    {
        if (userPriority == null)
        {
            return Collections.emptyList();
        } else
        {
            String priority = AssociationUtilities.unprocessUserPriority(userPriority);
            return Collections.singleton(priority);
        }
    }

    @Override
    public void addReferences(Collection<Indirect<?>> collection)
    {
        references = collection;
    }
}
