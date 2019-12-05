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
package plugin.lsttokens.pcclass;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.enumeration.AttackType;

/**
 * Class deals with ATTACKCYCLE Token
 */
public class AttackcycleToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    @Override
    public String getTokenName()
    {
        return "ATTACKCYCLE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
        if (aTok.countTokens() % 2 != 0)
        {
            return new ParseResult.Fail(getTokenName() + " must have an even number of arguments.");
        }

        while (aTok.hasMoreTokens())
        {
            AttackType at = AttackType.getAttackInstance(aTok.nextToken());
            if (AttackType.GRAPPLE.equals(at))
            {
                return new ParseResult.Fail("Error: Cannot Set Attack Cycle " + "for GRAPPLE Attack Type");
            }
            String cycle = aTok.nextToken();
            try
            {
                Integer i = Integer.parseInt(cycle);
                if (i <= 0)
                {
                    return new ParseResult.Fail("Invalid " + getTokenName() + ": " + value + " Cycle " + cycle
                            + " must be a positive integer.");
                }
                context.getObjectContext().put(pcc, MapKey.ATTACK_CYCLE, at, i);
                /*
                 * This is a bit of a hack - it is designed to account for the
                 * fact that the BAB tag in ATTACKCYCLE actually impacts both
                 * ATTACK.MELEE and ATTACK.GRAPPLE ... therefore, one method of
                 * handing this (which is done here) is to actually allow the
                 * pcgen.core code to keep the 4 attack type view (MELEE,
                 * RANGED, UNARMED, GRAPPLE) by simply loading the attackCycle
                 * for MELEE into GRAPPLE. This is done in the hope that this is
                 * a more flexible solution for potential future requirements
                 * for other attack types (rather than treating GRAPPLE as a
                 * special case throughout the core code) - thpr Nov 1, 2006
                 */
                if (at.equals(AttackType.MELEE))
                {
                    context.getObjectContext().put(pcc, MapKey.ATTACK_CYCLE, AttackType.GRAPPLE, i);
                }
            } catch (NumberFormatException e)
            {
                return new ParseResult.Fail(
                        "Invalid " + getTokenName() + ": " + value + " Cycle " + cycle + " must be a (positive) integer.");
            }
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        MapChanges<AttackType, Integer> changes = context.getObjectContext().getMapChanges(pcc, MapKey.ATTACK_CYCLE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Set<String> set = new TreeSet<>();
        Integer grappleValue = null;
        Integer meleeValue = null;
        Map<AttackType, Integer> added = changes.getAdded();
        for (Map.Entry<AttackType, Integer> me : added.entrySet())
        {
            AttackType attackType = me.getKey();
            Integer value = me.getValue();
            if (value != null)
            {
                if (value <= 0)
                {
                    context.addWriteMessage("Invalid " + getTokenName() + ": " + value + " Cycle " + attackType
                            + " must be a positive integer.");
                    return null;
                }
                if (attackType.equals(AttackType.GRAPPLE))
                {
                    grappleValue = value;
                } else
                {
                    if (attackType.equals(AttackType.MELEE))
                    {
                        meleeValue = value;
                    }
                    set.add(attackType.getIdentifier() + Constants.PIPE + value);
                }
            }
        }
        if (grappleValue != null)
        {
            // Validate same as MELEE
            if (!grappleValue.equals(meleeValue))
            {
                context.addWriteMessage("Grapple Attack Cycle (" + grappleValue + ") MUST be equal to "
                        + "Melee Attack Cycle (" + meleeValue + ") because it is not stored");
                return null;
            }
        }
        if (set.isEmpty())
        {
            //OK, someone set keys with no values
            return null;
        }
        return new String[]{StringUtil.join(set, Constants.PIPE)};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
