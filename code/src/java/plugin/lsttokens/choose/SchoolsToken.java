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
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.identifier.SpellSchool;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractSimpleChooseToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * New chooser plugin, handles spell schools.
 */
public class SchoolsToken extends AbstractSimpleChooseToken<SpellSchool>
{

    private static final Class<SpellSchool> SPELLSCHOOL_CLASS = SpellSchool.class;

    @Override
    public String getTokenName()
    {
        return "SCHOOLS";
    }

    @Override
    protected Class<SpellSchool> getChooseClass()
    {
        return SPELLSCHOOL_CLASS;
    }

    @Override
    protected String getDefaultTitle()
    {
        return "School choice";
    }

    @Override
    public SpellSchool decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(SPELLSCHOOL_CLASS, s);
    }

    @Override
    public String encodeChoice(SpellSchool choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected AssociationListKey<SpellSchool> getListKey()
    {
        return AssociationListKey.getKeyFor(SPELLSCHOOL_CLASS, "CHOOSE*SPELLSCHOOL");
    }

    @Override
    public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
    {
        if (value == null)
        {
            // No args - deprecated
            Logging.deprecationPrint("CHOOSE:" + getTokenName() + " with no argument has been deprecated", context);
            value = Constants.LST_ALL;
        }
        return super.parseToken(context, obj, value);
    }

}
