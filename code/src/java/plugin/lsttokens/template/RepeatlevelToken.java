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

import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with REPEATLEVEL Token
 */
public class RepeatlevelToken extends AbstractTokenWithSeparator<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

    @Override
    public String getTokenName()
    {
        return "REPEATLEVEL";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate template, String value)
    {
        ParseResult pr = checkForIllegalSeparator(':', value);
        if (!pr.passed())
        {
            return pr;
        }
        //
        // x|y|z:level:<level assigned item>
        //
        int endRepeat = value.indexOf(Constants.COLON);
        if (endRepeat < 0)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Token (No Colon): " + value);
        }
        int endLevel = value.indexOf(Constants.COLON, endRepeat + 1);
        if (endLevel < 0)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Token (Only One Colon): " + value);
        }
        int endAssignType = value.indexOf(Constants.COLON, endLevel + 1);
        if (endAssignType == -1)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Token (Only Two Colons): " + value);
        }

        String repeatedInfo = value.substring(0, endRepeat);
        StringTokenizer repeatToken = new StringTokenizer(repeatedInfo, Constants.PIPE);
        if (repeatToken.countTokens() != 3)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (incorrect PIPE count in repeat): " + repeatedInfo);
        }

        String levelIncrement = repeatToken.nextToken();
        int lvlIncrement;
        try
        {
            lvlIncrement = Integer.parseInt(levelIncrement);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Level Increment was not an Integer): " + levelIncrement);
        }
        if (lvlIncrement <= 0)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Level Increment was <= 0): " + lvlIncrement);
        }

        String consecutiveString = repeatToken.nextToken();
        int consecutive;
        try
        {
            consecutive = Integer.parseInt(consecutiveString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Consecutive Value was not an Integer): " + consecutiveString);
        }
        if (consecutive < 0)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Consecutive String was <= 0): " + consecutive);
        }

        String maxLevelString = repeatToken.nextToken();
        int maxLevel;
        try
        {
            maxLevel = Integer.parseInt(maxLevelString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Max Level was not an Integer): " + maxLevelString);
        }
        if (maxLevel <= 0)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Token (Max Level was <= 0): " + maxLevel);
        }

        String levelString = value.substring(endRepeat + 1, endLevel);
        int iLevel;
        try
        {
            iLevel = Integer.parseInt(levelString);
        } catch (NumberFormatException nfe)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Level was not a number): " + levelString);
        }
        if (iLevel <= 0)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Token (Level was <= 0): " + iLevel);
        }

        if (iLevel > maxLevel)
        {
            return new ParseResult.Fail("Malformed " + getTokenName() + " Token (Starting Level was > Maximum Level)");
        }
        if (iLevel + lvlIncrement > maxLevel)
        {
            return new ParseResult.Fail(
                    "Malformed " + getTokenName() + " Token (Does not repeat, Staring Level + Increment > Maximum Level)");
        }
        if (consecutive != 0 && ((maxLevel - iLevel) / lvlIncrement) < consecutive)
        {
            ComplexParseResult cpr = new ComplexParseResult();
            cpr.addErrorMessage(
                    "Malformed " + getTokenName() + " Token (Does not use Skip Interval value): " + consecutive);
            cpr.addErrorMessage("  You should set the interval to zero");
            return cpr;
        }

        String typeStr = value.substring(endLevel + 1, endAssignType);
        String contentStr = value.substring(endAssignType + 1);
        /*
         * typeStr and contentStr can't be null due to hasIllegalSeparator check
         * on colon above
         */

        PCTemplate consolidator = new PCTemplate();
        consolidator.put(IntegerKey.CONSECUTIVE, consecutive);
        consolidator.put(IntegerKey.MAX_LEVEL, maxLevel);
        consolidator.put(IntegerKey.LEVEL_INCREMENT, lvlIncrement);
        consolidator.put(IntegerKey.START_LEVEL, iLevel);
        context.getObjectContext().addToList(template, ListKey.REPEATLEVEL_TEMPLATES, consolidator);
        context.getReferenceContext().getManufacturer(PCTemplate.class).addDerivativeObject(consolidator);

        for (int count = consecutive;iLevel <= maxLevel;iLevel += lvlIncrement)
        {
            if ((consecutive == 0) || (count != 0))
            {
                PCTemplate derivative = new PCTemplate();
                derivative.put(IntegerKey.LEVEL, count);
                context.getReferenceContext().getManufacturer(PCTemplate.class).addDerivativeObject(derivative);
                context.getObjectContext().addToList(consolidator, ListKey.LEVEL_TEMPLATES, derivative);
                if (!context.processToken(derivative, typeStr, contentStr))
                {
                    return ParseResult.INTERNAL_ERROR;
                }
            }
            if (consecutive != 0)
            {
                if (count == 0)
                {
                    count = consecutive;
                } else
                {
                    --count;
                }
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCTemplate pct)
    {
        Changes<PCTemplate> changes = context.getObjectContext().getListChanges(pct, ListKey.REPEATLEVEL_TEMPLATES);
        Collection<PCTemplate> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            return null;
        }
        Set<String> list = new TreeSet<>();
        for (PCTemplate agg : added)
        {
            StringBuilder sb = new StringBuilder();
            Integer consecutive = agg.get(IntegerKey.CONSECUTIVE);
            Integer maxLevel = agg.get(IntegerKey.MAX_LEVEL);
            Integer lvlIncrement = agg.get(IntegerKey.LEVEL_INCREMENT);
            Integer iLevel = agg.get(IntegerKey.START_LEVEL);
            sb.append(lvlIncrement).append(Constants.PIPE);
            sb.append(consecutive).append(Constants.PIPE);
            sb.append(maxLevel).append(Constants.COLON);
            sb.append(iLevel).append(Constants.COLON);
            Changes<PCTemplate> subchanges = context.getObjectContext().getListChanges(agg, ListKey.LEVEL_TEMPLATES);
            Collection<PCTemplate> perAddCollection = subchanges.getAdded();
            if (perAddCollection == null || perAddCollection.isEmpty())
            {
                context.addWriteMessage("Invalid Consolidator built in " + getTokenName() + ": had no subTemplates");
                return null;
            }
            PCTemplate next = perAddCollection.iterator().next();
            Collection<String> unparse = context.unparse(next);
            if (unparse != null)
            {
                int masterLength = sb.length();
                for (String str : unparse)
                {
                    sb.setLength(masterLength);
                    list.add(sb.append(str).toString());
                }
            }
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }
}
