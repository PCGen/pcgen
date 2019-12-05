/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.UserChooseInformation;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * New chooser plugin, handles User Input.
 */
public class UserInputToken implements CDOMSecondaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "USERINPUT";
    }

    @Override
    public String getParentToken()
    {
        return "CHOOSE";
    }

    @Override
    public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
    {
        UserChooseInformation ci = new UserChooseInformation();
        if (value != null)
        {
            int pipeLoc = value.indexOf('|');
            String titleString;
            if (pipeLoc == -1)
            {
                titleString = value;
            } else
            {
                String countString = value.substring(0, pipeLoc);
                Logging.deprecationPrint("CHOOSE:USERINPUT with count is deprecated, "
                        + "please use SELECT: to identify the quantity of selections", context);
                int firstarg;
                try
                {
                    firstarg = Integer.parseInt(countString);
                } catch (NumberFormatException nfe)
                {
                    return new ParseResult.Fail("If CHOOSE:" + getTokenName() + " contains a pipe, "
                            + "first argument must be an Integer : " + value);
                }
                Formula count = FormulaFactory.getFormulaFor(firstarg);
                context.getObjectContext().put(obj, FormulaKey.NUMCHOICES, count);
                context.getObjectContext().put(obj, FormulaKey.SELECT, count);
                titleString = value.substring(pipeLoc + 1);
            }
            if (!titleString.startsWith("TITLE="))
            {
                return new ParseResult.Fail("CHOOSE:" + getTokenName() + " in " + obj.getClass() + ' '
                        + obj.getKeyName() + " had invalid arguments: " + value);
            }
            String title = titleString.substring(6);
            if (title.startsWith("\""))
            {
                title = title.substring(1, title.length() - 1);
            }
            ci.setTitle(title);
        } else
        {
            ci.setTitle(getDefaultTitle());
        }
        // No args - legal
        context.getObjectContext().put(obj, ObjectKey.CHOOSE_INFO, ci);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        ChooseInformation<?> ci = context.getObjectContext().getObject(cdo, ObjectKey.CHOOSE_INFO);
        if ((ci == null) || !ci.getName().equals(UserChooseInformation.UCI_NAME))
        {
            return null;
        }
        String title = ci.getTitle();
        String result = "";
        if (!title.equals(getDefaultTitle()))
        {
            result = "TITLE=" + title;
        }
        return new String[]{result};
    }

    private String getDefaultTitle()
    {
        return "Provide User Input";
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
