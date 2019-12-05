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
package plugin.lsttokens;

import pcgen.base.util.ArrayUtilities;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.NonInteractive;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

public class AddLst extends AbstractNonEmptyToken<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{
    /*
     * Template's LevelToken adjustment done in addAddsFromAllObjForLevel() in
     * PlayerCharacter
     */

    @Override
    public String getTokenName()
    {
        return "ADD";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        if (obj instanceof Ungranted)
        {
            return new ParseResult.Fail(
                    "Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
        }
        if (obj instanceof NonInteractive)
        {
            return new ParseResult.Fail("Cannot use " + getTokenName() + " on an Non-Interactive object type: "
                    + obj.getClass().getSimpleName());
        }
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            if (Constants.LST_DOT_CLEAR.equals(value))
            {
                if (obj instanceof PCClassLevel)
                {
                    ComplexParseResult cpr = new ComplexParseResult();
                    cpr.addErrorMessage("Warning: You performed an invalid " + ".CLEAR in a ADD: Token");
                    cpr.addErrorMessage(
                            "  A non-level limited .CLEAR was " + "used in a Class Level line in " + obj.getKeyName());
                    return cpr;
                }
            } else if (value.startsWith(".CLEAR.LEVEL"))
            {
                if (!(obj instanceof PCClassLevel))
                {
                    ComplexParseResult cpr = new ComplexParseResult();
                    cpr.addErrorMessage("Warning: You performed an invalid .CLEAR in a ADD: Token");
                    cpr.addErrorMessage(
                            "  A level limited .CLEAR ( " + value + " ) was not used in a Class Level line in "
                                    + obj.getClass().getSimpleName() + ' ' + obj.getKeyName());
                    return cpr;
                }
                String levelString = value.substring(12);
                try
                {
                    int level = Integer.parseInt(levelString);
                    if (level != obj.get(IntegerKey.LEVEL))
                    {
                        ComplexParseResult cpr = new ComplexParseResult();
                        cpr.addErrorMessage("Warning: You performed an invalid " + ".CLEAR in a ADD: Token");
                        cpr.addErrorMessage(
                                "  A level limited .CLEAR ( " + value + " ) was used in a Class Level line");
                        cpr.addErrorMessage("  But was asked to clear a " + "different Class Level ( " + level
                                + " ) than the Class Level Line it appeared on: " + obj.getKeyName());
                        return cpr;
                    }
                } catch (NumberFormatException e)
                {
                    ComplexParseResult cpr = new ComplexParseResult();
                    cpr.addErrorMessage("Warning: You performed an invalid .CLEAR in a ADD: Token");
                    cpr.addErrorMessage("  A level limited .CLEAR ( " + value + " ) was used in a Class Level line");
                    cpr.addErrorMessage(
                            "  But the level ( " + levelString + " ) was not an integer in: " + obj.getKeyName());
                    return cpr;
                }
            } else
            {
                return new ParseResult.Fail(getTokenName() + " requires a SubToken and argument, found: " + value);
            }
            context.getObjectContext().removeList(obj, ListKey.ADD);
            return ParseResult.SUCCESS;
        }

        return context.processSubToken(obj, getTokenName(), value.substring(0, pipeLoc), value.substring(pipeLoc + 1));
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        String[] unparsed = context.unparseSubtoken(obj, getTokenName());
        Changes<PersistentTransitionChoice<?>> changes = context.getObjectContext().getListChanges(obj, ListKey.ADD);
        if (changes.includesGlobalClear())
        {
            StringBuilder clearSB = new StringBuilder();
            clearSB.append(Constants.LST_DOT_CLEAR);
            if (obj instanceof PCClassLevel)
            {
                clearSB.append(".LEVEL");
                Integer lvl = obj.get(IntegerKey.LEVEL);
                clearSB.append(lvl);
            }
            unparsed = ArrayUtilities.prependOnCopy(clearSB.toString(), unparsed, String.class);
        }
        return unparsed;
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
