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

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.facade.core.InfoFacade;

/**
 * {@code Domain}.
 */
@SuppressWarnings("serial")
public final class Domain extends PObject implements InfoFacade, ChooseDriver
{
    @Override
    public ChooseInformation<?> getChooseInfo()
    {
        return get(ObjectKey.CHOOSE_INFO);
    }

    @Override
    public Formula getSelectFormula()
    {
        return getSafe(FormulaKey.SELECT);
    }

    @Override
    public List<ChooseSelectionActor<?>> getActors()
    {
        return getListFor(ListKey.NEW_CHOOSE_ACTOR);
    }

    @Override
    public String getFormulaSource()
    {
        return getKeyName();
    }

    @Override
    public Formula getNumChoices()
    {
        return getSafe(FormulaKey.NUMCHOICES);
    }
}
