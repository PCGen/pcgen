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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.ArmorProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles Armour Proficiency.
 */
public class ArmorProficiencyToken extends AbstractQualifiedChooseToken<ArmorProf>
{

    private static final Class<ArmorProf> ARMORPROF_CLASS = ArmorProf.class;

    @Override
    public String getTokenName()
    {
        return "ARMORPROFICIENCY";
    }

    @Override
    protected String getDefaultTitle()
    {
        return "Armor Proficiency choice";
    }

    @Override
    public ArmorProf decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(ARMORPROF_CLASS, s);
    }

    @Override
    public String encodeChoice(ArmorProf choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected AssociationListKey<ArmorProf> getListKey()
    {
        return AssociationListKey.getKeyFor(ARMORPROF_CLASS, "CHOOSE*ARMORPROF");
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return super.parseTokenWithSeparator(context, context.getReferenceContext().getManufacturer(ARMORPROF_CLASS),
                obj, value);
    }

    @Override
    protected String getPersistentFormat()
    {
        return "ARMORPROF";
    }
}
