/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
import pcgen.core.PCStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractSimpleChooseToken;

/**
 * New chooser plugin, handles stats.
 */
public class PCStatToken extends AbstractSimpleChooseToken<PCStat>
{
    private static final Class<PCStat> PCSTAT_CLASS = PCStat.class;

    @Override
    public String getTokenName()
    {
        return "PCSTAT";
    }

    @Override
    protected Class<PCStat> getChooseClass()
    {
        return PCSTAT_CLASS;
    }

    @Override
    protected String getDefaultTitle()
    {
        return "Stat choice";
    }

    @Override
    public PCStat decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(PCSTAT_CLASS, s);
    }

    @Override
    public String encodeChoice(PCStat choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected AssociationListKey<PCStat> getListKey()
    {
        return AssociationListKey.getKeyFor(PCSTAT_CLASS, "CHOOSE*PCSTAT");
    }

}
