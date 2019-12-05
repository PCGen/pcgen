/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.converter;

import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;

public class PreDRConvertPlugin implements TokenProcessorPlugin
{

    @Override
    public String process(TokenProcessEvent tpe)
    {
        tpe.append(tpe.getKey());
        tpe.append(':');
        String formula = tpe.getValue();

        int commaLoc = formula.indexOf(',');
        if (commaLoc == -1)
        {
            return "Prerequisite " + tpe.getKey() + " must have a count: " + formula;
        }
        if (commaLoc == formula.length() - 1)
        {
            return "Prerequisite " + tpe.getKey() + " can not have only a count: " + formula;
        }
        String num = formula.substring(0, commaLoc);
        String rest = formula.substring(commaLoc + 1);

        try
        {
            Integer.parseInt(num);
        } catch (NumberFormatException nfe)
        {
            return '\'' + num + "' in " + tpe.getKey() + " is not a valid integer";
        }

        tpe.append(num);

        // Work rest here:

        StringTokenizer st = new StringTokenizer(rest, ",");
        while (st.hasMoreTokens())
        {
            String tok = st.nextToken();
            int equalLoc = tok.indexOf('=');
            tpe.append(',');
            tpe.append(tok);
            if (equalLoc == -1)
            {
                tpe.append("=0");
            }
        }
        tpe.consume();
        return null;
    }

    @Override
    public Class<? extends CDOMObject> getProcessedClass()
    {
        return CDOMObject.class;
    }

    @Override
    public String getProcessedToken()
    {
        return "PREDR";
    }
}
