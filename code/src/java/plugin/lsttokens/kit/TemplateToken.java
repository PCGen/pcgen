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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCTemplate;
import pcgen.core.kit.KitTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * This class parses a TEMPLATE line from a Kit file. It handles the TEMPLATE
 * tag as well as all common tags.
 * <p>
 * <strong>Tag Name:</strong> TEMPLATE:x|x <br>
 * <strong>Variables Used (x):</strong> Text (Name of template)<br>
 * <strong>What it does:</strong><br>
 * &nbsp;&nbsp;This is a | (pipe) delimited list of templates that are granted
 * by the feat.<br>
 * <strong>Example:</strong><br>
 * &nbsp;&nbsp;{@code TEMPLATE:Celestial}<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;Adds the "Celestial" template to the character.<br>
 * </p>
 */
public class TemplateToken extends AbstractTokenWithSeparator<KitTemplate> implements CDOMPrimaryToken<KitTemplate>
{
    private static final Class<PCTemplate> TEMPLATE_CLASS = PCTemplate.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "TEMPLATE";
    }

    @Override
    public Class<KitTemplate> getTokenClass()
    {
        return KitTemplate.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitTemplate kitTemplate, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            int openLoc = tokText.indexOf('[');
            String name;
            List<CDOMSingleRef<PCTemplate>> subList;
            if (openLoc == -1)
            {
                name = tokText;
                subList = null;
            } else
            {
                name = tokText.substring(0, openLoc);
                subList = new ArrayList<>();
                String rest = tokText.substring(openLoc + 1);
                StringTokenizer subTok = new StringTokenizer(rest, "[]");
                while (subTok.hasMoreTokens())
                {
                    String subStr = subTok.nextToken();
                    if (subStr.startsWith("TEMPLATE:"))
                    {
                        String ownedTemplateName = subStr.substring(9);

                        CDOMSingleRef<PCTemplate> ref =
                                context.getReferenceContext().getCDOMReference(TEMPLATE_CLASS, ownedTemplateName);
                        subList.add(ref);
                    } else
                    {
                        return new ParseResult.Fail(
                                "Did not understand " + getTokenName() + " option: " + subStr + " in line: " + value);
                    }
                }
            }
            CDOMSingleRef<PCTemplate> ref = context.getReferenceContext().getCDOMReference(TEMPLATE_CLASS, name);
            kitTemplate.addTemplate(ref, subList);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitTemplate kitTemplate)
    {
        return kitTemplate.isEmpty() ? null : new String[]{kitTemplate.toString()};
    }
}
