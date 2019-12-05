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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Deity;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles Deity.
 */
public class DeityToken extends AbstractQualifiedChooseToken<Deity>
{

    private static final Class<Deity> DEITY_CLASS = Deity.class;

    @Override
    public String getTokenName()
    {
        return "DEITY";
    }

    @Override
    protected String getDefaultTitle()
    {
        return "Deity choice";
    }

    @Override
    public Deity decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(DEITY_CLASS, s);
    }

    @Override
    public String encodeChoice(Deity choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected AssociationListKey<Deity> getListKey()
    {
        return AssociationListKey.getKeyFor(DEITY_CLASS, "CHOOSE*DEITY");
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return super.parseTokenWithSeparator(context, context.getReferenceContext().getManufacturer(DEITY_CLASS), obj,
                value);
    }

    @Override
    protected String getPersistentFormat()
    {
        return "DEITY";
    }
}
