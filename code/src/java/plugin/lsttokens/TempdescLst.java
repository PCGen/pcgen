/*
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.spell.Spell;
import pcgen.io.EntityEncoder;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * TempdescLst - records a sentence that is used to describe what the TEMPBONUS
 * entries are going to do or when they should be used. The text will only be
 * displayed in the Temporary Bonus Tab.
 */
public class TempdescLst extends AbstractNonEmptyToken<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "TEMPDESC";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        if ((obj instanceof Ungranted) && !(obj instanceof Spell))
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        context.getObjectContext().put(obj, StringKey.TEMP_DESCRIPTION, EntityEncoder.decode(value));
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        String descr = context.getObjectContext().getString(obj, StringKey.TEMP_DESCRIPTION);
        if (descr == null)
        {
            return null;
        }
        return new String[]{EntityEncoder.encode(descr)};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
