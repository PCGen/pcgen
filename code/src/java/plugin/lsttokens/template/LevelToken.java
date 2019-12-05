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
package plugin.lsttokens.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.Visibility;

/**
 * Class deals with LEVEL Token
 * <p>
 * (Wed, 11 Jun 2008) $
 */
public class LevelToken extends AbstractTokenWithSeparator<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{
    @Override
    public String getTokenName()
    {
        return "LEVEL";
    }

    @Override
    public ParseResult parseToken(LoadContext context, PCTemplate template, String value)
    {
        if (Constants.LST_DOT_CLEAR.equals(value))
        {

            context.getObjectContext().removeList(template, ListKey.LEVEL_TEMPLATES);
            return ParseResult.SUCCESS;
        }
        return super.parseToken(context, template, value);
    }

    @Override
    protected char separator()
    {
        return ':';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate template, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.COLON);

        String levelStr = tok.nextToken();
        int plusLoc = levelStr.indexOf('+');
        if (plusLoc == 0)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Level cannot start with +: " + value);
        }
        int lvl;
        try
        {
            /*
             * Note this test of integer (even if it doesn't get used outside
             * this try) is necessary for catching errors.
             */
            lvl = Integer.parseInt(levelStr);
            if (lvl <= 0)
            {
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Malformed " + getTokenName() + " Token (Level was <= 0): " + lvl);
                cpr.addErrorMessage("  Line was: " + value);
                return cpr;
            }
        } catch (NumberFormatException ex)
        {
            return new ParseResult.Fail("Misunderstood Level value: " + levelStr + " in " + getTokenName());
        }

        if (!tok.hasMoreTokens())
        {
            return new ParseResult.Fail(
                    "Invalid " + getTokenName() + ": requires 3 colon separated elements (has one): " + value);
        }
        String typeStr = tok.nextToken();
        if (!tok.hasMoreTokens())
        {
            return new ParseResult.Fail(
                    "Invalid " + getTokenName() + ": requires 3 colon separated elements (has two): " + value);
        }
        String argument = tok.nextToken();
        PCTemplate derivative = new PCTemplate();
        derivative.put(ObjectKey.VISIBILITY, Visibility.HIDDEN);
        derivative.put(IntegerKey.LEVEL, lvl);
        context.getReferenceContext().getManufacturer(PCTemplate.class).addDerivativeObject(derivative);
        context.getObjectContext().addToList(template, ListKey.LEVEL_TEMPLATES, derivative);
        if (context.processToken(derivative, typeStr, argument))
        {
            return ParseResult.SUCCESS;
        }
        return ParseResult.INTERNAL_ERROR;
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        Changes<PCTemplate> changes = context.getObjectContext().getListChanges(pct, ListKey.LEVEL_TEMPLATES);
        Collection<PCTemplate> added = changes.getAdded();
        List<String> ret = new ArrayList<>();
        boolean globalClear = changes.includesGlobalClear();
        if (globalClear)
        {
            ret.add(Constants.LST_DOT_CLEAR);
        }
        if (added != null)
        {
            Set<String> set = new TreeSet<>();
            for (PCTemplate pctChild : added)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(pctChild.get(IntegerKey.LEVEL)).append(':');
                Collection<String> unparse = context.unparse(pctChild);
                if (unparse != null)
                {
                    int masterLength = sb.length();
                    for (String str : unparse)
                    {
                        sb.setLength(masterLength);
                        set.add(sb.append(str).toString());
                    }
                }
            }
            ret.addAll(set);
        }
        if (ret.isEmpty())
        {
            return null;
        }
        return ret.toArray(new String[0]);
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }

}
