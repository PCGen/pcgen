/*
 * Copyright 2008-19 (C) Thomas Parker <thpr@users.sourceforge.net>
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

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.function.Function;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MovementType;
import pcgen.core.MoveClone;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class MovecloneLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "MOVECLONE";
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        StringTokenizer moves = new StringTokenizer(value, Constants.COMMA);

        if (moves.countTokens() != 3)
        {
            return new ParseResult.Fail("Invalid Version of MOVECLONE detected: " + value
                    + "\n  MOVECLONE has 3 arguments: " + "SourceMove,DestinationMove,Modifier");
        }

        MovementType oldType = MovementType.getConstant(moves.nextToken());
        MovementType newType = MovementType.getConstant(moves.nextToken());
        String formulaString = moves.nextToken();
        Function<Double, Double> conversion;

        if (formulaString.startsWith("/"))
        {
            try
            {
                int denom = Integer.parseInt(formulaString.substring(1));
                if (denom <= 0)
                {
                    return new ParseResult.Fail(getTokenName() + " was expecting a Positive Integer "
                            + "for dividing Movement, was : " + formulaString.substring(1));
                }
                conversion = moveRate -> (moveRate / denom);
            } catch (NumberFormatException e)
            {
                return new ParseResult.Fail(
                        getTokenName() + " was expecting an integer to follow /, was : " + formulaString);
            }
        } else if (formulaString.startsWith("*"))
        {
            try
            {
                float mult = Float.parseFloat(formulaString.substring(1));
                if (mult < 0.0)
                {
                    return new ParseResult.Fail(getTokenName() + " was expecting a "
                            + "Float >= 0 for multiplying Movement, was : " + formulaString.substring(1));
                }
                conversion = moveRate -> (moveRate * mult);
            } catch (NumberFormatException e)
            {
                return new ParseResult.Fail(
                        getTokenName() + " was expecting an integer to follow *, was : " + formulaString);
            }
        } else if (formulaString.startsWith("+"))
        {
            try
            {
                int add = Integer.parseInt(formulaString.substring(1));
                if (add < 0)
                {
                    return new ParseResult.Fail(getTokenName() + " was expecting a Non-Negative "
                            + "Integer for adding Movement, was : " + formulaString.substring(1));
                }
                conversion = moveRate -> (moveRate + add);
            } catch (NumberFormatException e)
            {
                return new ParseResult.Fail(
                        getTokenName() + " was expecting an integer to follow +, was : " + formulaString);
            }
        } else
        {
            try
            {
                int diff = Integer.parseInt(formulaString);
                conversion = moveRate -> (moveRate + diff);
            } catch (NumberFormatException e)
            {
                return new ParseResult.Fail(
                        getTokenName() + " was expecting a Formula as the final value, was : " + formulaString);
            }
        }
        MoveClone moveClone =
                new MoveClone(oldType, newType, conversion, formulaString);
        context.getObjectContext().addToList(obj, ListKey.MOVEMENTCLONE,
                moveClone);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<MoveClone> changes = context.getObjectContext()
                .getListChanges(obj, ListKey.MOVEMENTCLONE);
        Collection<MoveClone> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token
            return null;
        }
        WeightedCollection<String> set =
                new WeightedCollection<>(String.CASE_INSENSITIVE_ORDER);
        for (MoveClone m : added)
        {
            set.add(m.getBaseType() + Constants.COMMA + m.getCloneType()
                    + Constants.COMMA + m.getFormulaString());
        }
        if (set.isEmpty())
        {
            return null;
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
