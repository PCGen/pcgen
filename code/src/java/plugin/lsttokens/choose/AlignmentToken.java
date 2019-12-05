/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.PCAlignment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractSimpleChooseToken;

/**
 * New chooser plugin, handles PC Alignment.
 */
public class AlignmentToken extends AbstractSimpleChooseToken<PCAlignment>
{
    private static final Class<PCAlignment> PCALIGNMENT_CLASS = PCAlignment.class;

    @Override
    public String getTokenName()
    {
        return "ALIGNMENT";
    }

    @Override
    protected Class<PCAlignment> getChooseClass()
    {
        return PCALIGNMENT_CLASS;
    }

    @Override
    protected String getDefaultTitle()
    {
        return "Alignment choice";
    }

    @Override
    public PCAlignment decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(PCALIGNMENT_CLASS, s);
    }

    @Override
    public String encodeChoice(PCAlignment choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected AssociationListKey<PCAlignment> getListKey()
    {
        return AssociationListKey.getKeyFor(PCALIGNMENT_CLASS, "CHOOSE*ALIGNMENT");
    }
}
