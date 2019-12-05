/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.subclass;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.ProhibitedSpellType;

/**
 * Class deals with CHOICE Token
 */
public class ChoiceToken extends AbstractTokenWithSeparator<SubClass> implements CDOMPrimaryToken<SubClass>
{

    @Override
    public String getTokenName()
    {
        return "CHOICE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, SubClass sc, String value)
    {
        int pipeLoc = value.indexOf('|');
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(getTokenName() + " has no | separator for arguments: " + value);
        }

        if (value.lastIndexOf('|') != pipeLoc)
        {
            return new ParseResult.Fail(getTokenName() + " has more than two | separated arguments: " + value);
        }

        String pstString = value.substring(0, pipeLoc);
        ProhibitedSpellType type;

        try
        {
            type = ProhibitedSpellType.valueOf(pstString);
        } catch (IllegalArgumentException e)
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage(getTokenName() + " encountered an invalid Prohibited Spell Type: " + value);
            cpr.addErrorMessage(
                    "  Legal values are: " + StringUtil.join(Arrays.asList(ProhibitedSpellType.values()), ", "));
            return cpr;
        }
        if (type.equals(ProhibitedSpellType.SCHOOL) || type.equals(ProhibitedSpellType.SUBSCHOOL)
                || type.equals(ProhibitedSpellType.DESCRIPTOR))
        {
            SpellProhibitor sp = new SpellProhibitor();
            sp.setType(type);
            sp.addValue(value.substring(pipeLoc + 1));
            context.getObjectContext().put(sc, ObjectKey.CHOICE, sp);
            return ParseResult.SUCCESS;
        }

        return new ParseResult.Fail("Invalid TYPE in " + getTokenName() + ": " + pstString);
    }

    @Override
    public String[] unparse(LoadContext context, SubClass pcc)
    {
        SpellProhibitor sp = context.getObjectContext().getObject(pcc, ObjectKey.CHOICE);
        if (sp == null)
        {
            // Zero indicates no Token present
            return null;
        }
        StringBuilder sb = new StringBuilder();
        ProhibitedSpellType pst = sp.getType();
        sb.append(pst.toString().toUpperCase());
        sb.append('|');
        Collection<String> valueSet = sp.getValueList();
        sb.append(StringUtil.join(new TreeSet<>(valueSet), Constants.PIPE));
        return new String[]{sb.toString()};
    }

    @Override
    public Class<SubClass> getTokenClass()
    {
        return SubClass.class;
    }

}
