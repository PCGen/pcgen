/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit;

import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Kit;
import pcgen.core.PCStat;
import pcgen.core.kit.BaseKit;
import pcgen.core.kit.KitStat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * This class handles the STAT tag for Kits.<br>
 * The tag format is:<br>
 * {@code STAT:STR=15|DEX=14|WIS=10|CON=10|INT=10|CHA=18}
 */
public class StatToken extends AbstractTokenWithSeparator<KitStat>
        implements CDOMPrimaryToken<KitStat>, DeferredToken<Kit>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "STAT";
    }

    @Override
    public Class<KitStat> getTokenClass()
    {
        return KitStat.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitStat kitStat, String value)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();
            int equalLoc = token.indexOf('=');
            if (equalLoc == -1)
            {
                return new ParseResult.Fail("Illegal " + getTokenName() + " did not have Stat=X format: " + value);
            }
            if (equalLoc != token.lastIndexOf('='))
            {
                return new ParseResult.Fail(
                        "Illegal " + getTokenName() + " had two equal signs, is not Stat=X format: " + value);
            }
            String statName = token.substring(0, equalLoc);
            if (statName.isEmpty())
            {
                return new ParseResult.Fail(
                        "Illegal " + getTokenName() + " had no stat, is not Stat=X format: " + value);
            }
            CDOMSingleRef<PCStat> stat = context.getReferenceContext().getCDOMReference(PCStat.class, statName);
            String formula = token.substring(equalLoc + 1);
            if (formula.isEmpty())
            {
                return new ParseResult.Fail("Unable to find STAT value: " + value);
            }
            Formula statValue = FormulaFactory.getFormulaFor(formula);
            if (!statValue.isValid())
            {
                return new ParseResult.Fail(
                        "StatValue in " + getTokenName() + " was not valid: " + statValue.toString());
            }
            kitStat.addStat(stat, statValue);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitStat kitStat)
    {
        return kitStat.isEmpty() ? null : new String[]{kitStat.toString()};
    }

    @Override
    public boolean process(LoadContext context, Kit obj)
    {
        for (BaseKit bk : obj.getSafeListFor(ListKey.KIT_TASKS))
        {
            if (bk instanceof KitStat)
            {
                obj.removeFromListFor(ListKey.KIT_TASKS, bk);
                obj.addStat((KitStat) bk);
            }
        }
        return true;
    }

    @Override
    public Class<Kit> getDeferredTokenClass()
    {
        return Kit.class;
    }
}
