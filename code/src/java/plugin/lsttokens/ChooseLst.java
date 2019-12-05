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

import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.NonInteractive;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Ability;
import pcgen.core.Domain;
import pcgen.core.EquipmentModifier;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.util.Logging;

public class ChooseLst extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMPrimaryToken<CDOMObject>, PostDeferredToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "CHOOSE";
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
        String key;
        String val;
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (value.startsWith("FEAT="))
        {
            key = "FEATEQ";
            val = value.substring(5);
        } else if (value.startsWith("LANGAUTO:"))
        {
            key = "LANGAUTO";
            val = value.substring(9);
        } else if (pipeLoc == -1)
        {
            key = value;
            val = null;
        } else
        {
            key = value.substring(0, pipeLoc);
            val = value.substring(pipeLoc + 1);
        }
        if (!((obj instanceof Ability) || (obj instanceof Domain) || (obj instanceof Race)
                || (obj instanceof PCTemplate) || (obj instanceof Skill) || (obj instanceof EquipmentModifier)))
        {
            //Allow TEMPBONUS related choose
            if (!"NUMBER".equals(key))
            {
                return new ParseResult.Fail(getTokenName() + " is not supported for " + obj.getClass().getSimpleName());
            }
        }

        if (key.startsWith("NUMCHOICES="))
        {
            String maxCount = key.substring(11);
            if (maxCount == null || maxCount.isEmpty())
            {
                return new ParseResult.Fail("NUMCHOICES in CHOOSE must be a formula: " + value);
            }
            Formula f = FormulaFactory.getFormulaFor(maxCount);
            if (!f.isValid())
            {
                return new ParseResult.Fail(
                        "Number of Choices in " + getTokenName() + " was not valid: " + f.toString());
            }
            context.getObjectContext().put(obj, FormulaKey.NUMCHOICES, f);
            pipeLoc = val.indexOf(Constants.PIPE);
            if (pipeLoc == -1)
            {
                key = val;
                val = null;
            } else
            {
                key = val.substring(0, pipeLoc);
                val = val.substring(pipeLoc + 1);
            }
        }

        return context.processSubToken(obj, getTokenName(), key, val);
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        String[] str = context.unparseSubtoken(obj, getTokenName());
        if (str == null)
        {
            return null;
        }
        Formula choices = context.getObjectContext().getFormula(obj, FormulaKey.NUMCHOICES);
        String choicesString = choices == null ? null : "NUMCHOICES=" + choices.toString() + Constants.PIPE;
        for (int i = 0;i < str.length;i++)
        {
            if (str[i].endsWith(Constants.PIPE))
            {
                str[i] = str[i].substring(0, str[i].length() - 1);
            }
            if (choicesString != null)
            {
                str[i] = choicesString + str[i];
            }
            if (str[i].startsWith("FEATEQ|"))
            {
                str[i] = "FEAT=" + str[i].substring(7);
            }
        }
        return str;
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    public Class<CDOMObject> getDeferredTokenClass()
    {
        return CDOMObject.class;
    }

    /*
     * This makes an editor a bit more difficult, but since CHOOSE is an early
     * target of 5.17, this probably isn't a big deal.
     */
    @Override
    public boolean process(LoadContext context, CDOMObject obj)
    {
        ChooseInformation<?> newChoose = obj.get(ObjectKey.CHOOSE_INFO);
        String oldChoose = obj.get(StringKey.CHOICE_STRING);
        if (newChoose != null && oldChoose != null)
        {
            Logging.errorPrint("New style CHOOSE " + "and old style CHOOSE both found on "
                    + obj.getClass().getSimpleName() + ' ' + obj.getKeyName());
            return false;
        }
        if (newChoose != null)
        {
            Class<?> chooseClass = newChoose.getReferenceClass();
            List<ChooseSelectionActor<?>> newactors = obj.getListFor(ListKey.NEW_CHOOSE_ACTOR);
            if (newactors != null)
            {
                for (ChooseSelectionActor<?> csa : newactors)
                {
                    if (!chooseClass.equals(csa.getChoiceClass()))
                    {
                        Logging.errorPrint("CHOOSE of type " + chooseClass.getName() + " on "
                                + obj.getClass().getSimpleName() + ' ' + obj.getKeyName() + " had an actor from token "
                                + csa.getSource() + " that was expecting a " + csa.getChoiceClass().getSimpleName());
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }
}
