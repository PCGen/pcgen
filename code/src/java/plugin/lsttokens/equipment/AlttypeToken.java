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
package plugin.lsttokens.equipment;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.core.Equipment;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with ALTTYPE token
 */
public class AlttypeToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    @Override
    public String getTokenName()
    {
        return "ALTTYPE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
    {
        EquipmentHead head = eq.getEquipmentHead(2);
        if (value.startsWith(Constants.LST_DOT_CLEAR))
        {
            context.getObjectContext().removeList(head, ListKey.TYPE);
            if (value.length() == 6)
            {
                return ParseResult.SUCCESS;
            } else if (value.charAt(6) == '.')
            {
                value = value.substring(7);
                ParseResult pr = checkNonEmpty(value);
                if (!pr.passed())
                {
                    return new ParseResult.Fail(
                            getTokenName() + "started with .CLEAR. but expected to have a Type after .: " + value);
                }
            } else
            {
                return new ParseResult.Fail(
                        getTokenName() + "started with .CLEAR but expected next character to be .: " + value);
            }
        }
        ParseResult pr = checkForIllegalSeparator('.', value);
        if (!pr.passed())
        {
            return pr;
        }

        StringTokenizer aTok = new StringTokenizer(value, Constants.DOT);

        boolean bRemove = false;
        boolean bAdd = false;
        while (aTok.hasMoreTokens())
        {
            final String aType = aTok.nextToken();
            if ("ADD".equals(aType))
            {
                if (bRemove)
                {
                    return new ParseResult.Fail("Non-sensical use of .REMOVE.ADD. in " + getTokenName() + ": " + value);
                }
                bRemove = false;
                bAdd = true;
            } else if ("REMOVE".equals(aType))
            {
                if (bAdd)
                {
                    return new ParseResult.Fail("Non-sensical use of .ADD.REMOVE. in " + getTokenName() + ": " + value);
                }
                bRemove = true;
            } else if ("CLEAR".equals(aType))
            {
                return new ParseResult.Fail("Non-sensical use of .CLEAR in " + getTokenName() + ": " + value);
            } else if (bRemove)
            {
                Type type = Type.getConstant(aType);
                context.getObjectContext().removeFromList(head, ListKey.TYPE, type);
                bRemove = false;
            } else
            {
                Type type = Type.getConstant(aType);
                context.getObjectContext().addToList(head, ListKey.TYPE, type);
                bAdd = false;
            }
        }
        if (bRemove)
        {
            return new ParseResult.Fail(
                    getTokenName() + "ended with REMOVE, so didn't have any Type to remove: " + value);
        }
        if (bAdd)
        {
            return new ParseResult.Fail(getTokenName() + "ended with ADD, so didn't have any Type to add: " + value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        EquipmentHead head = eq.getEquipmentHead(2);
        Changes<Type> changes = context.getObjectContext().getListChanges(head, ListKey.TYPE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        Collection<?> added = changes.getAdded();
        boolean globalClear = changes.includesGlobalClear();
        if (globalClear)
        {
            sb.append(Constants.LST_DOT_CLEAR);
        }
        if (added != null && !added.isEmpty())
        {
            if (globalClear)
            {
                sb.append(Constants.DOT);
            }
            sb.append(StringUtil.join(added, Constants.DOT));
        }
        Collection<Type> removed = changes.getRemoved();
        if (removed != null && !removed.isEmpty())
        {
            if (sb.length() > 0)
            {
                sb.append(Constants.DOT);
            }
            sb.append("REMOVE.");
            sb.append(StringUtil.join(removed, Constants.DOT));
        }
        if (sb.length() == 0)
        {
            context.addWriteMessage(
                    getTokenName() + " was expecting non-empty changes to include " + "added items or global clear");
            return null;
        }
        return new String[]{sb.toString()};
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }

}
