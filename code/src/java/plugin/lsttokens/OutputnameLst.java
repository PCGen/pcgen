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
import pcgen.cdom.enumeration.EqModNameOpt;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class OutputnameLst extends AbstractNonEmptyToken<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "OUTPUTNAME";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof EquipmentModifier)
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addWarningMessage(getTokenName() + " is not valid for an equipment modifier. The "
                    + "FORMATCAT and NAMEOPT tags should be used instead. Will assume " + "NAMEOPT:TEXT=" + value
                    + ". Object was " + obj.toString());
            context.getObjectContext().put(obj, StringKey.NAME_TEXT, value);
            context.getObjectContext().put(obj, ObjectKey.NAME_OPT, EqModNameOpt.valueOfIgnoreCase("TEXT"));
            return cpr;
        }
        context.getObjectContext().put(obj, StringKey.OUTPUT_NAME, value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        String oname = context.getObjectContext().getString(obj, StringKey.OUTPUT_NAME);
        if (oname == null)
        {
            return null;
        }
        return new String[]{oname};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
