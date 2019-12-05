/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.helper;

import java.text.MessageFormat;
import java.util.List;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;

import org.springframework.web.util.HtmlUtils;

/**
 * InfoUtilities is a set of utilities related to the ALLOW token.
 */
public final class AllowUtilities
{

    private AllowUtilities()
    {
        //Do not instantiate utility class
    }

    /**
     * Returns an HTML String indicating the state of the ALLOW items on an object.
     *
     * @param pc  The PlayerCharacter in which the state should be processed
     * @param cdo The CDOMObject on which the ALLOW items are being analyzed
     * @return An HTML String with the text indicating the state of the ALLOW items for
     * the given PlayerCharacter and CDOMObject
     */
    public static String getAllowInfo(PlayerCharacter pc, CDOMObject cdo)
    {
        List<InfoBoolean> allowItems = cdo.getListFor(ListKey.ALLOW);
        if ((allowItems == null) || allowItems.isEmpty())
        {
            return Constants.EMPTY_STRING;
        }
        StringBuilder sb = new StringBuilder(400);
        StringBuffer tempBuffer = new StringBuffer(200);
        boolean needSeparator = false;
        for (InfoBoolean infoBoolean : allowItems)
        {
            CaseInsensitiveString cis = new CaseInsensitiveString(infoBoolean.getInfoName());
            MessageFormat info = cdo.get(MapKey.INFO, cis);
            if (info != null)
            {
                if (needSeparator)
                {
                    sb.append(" and ");
                }
                needSeparator = true;
                boolean passes = pc.solve(infoBoolean.getFormula());
                if (!passes)
                {
                    sb.append(SettingsHandler.getPrereqFailColorAsHtmlStart());
                    sb.append("<i>");
                }
                Object[] infoVars = InfoUtilities.getInfoVars(pc.getCharID(), cdo, cis);
                tempBuffer.setLength(0);
                info.format(infoVars, tempBuffer, null);
                sb.append(HtmlUtils.htmlEscape(tempBuffer.toString()));
                if (!passes)
                {
                    sb.append("</i>");
                    sb.append(SettingsHandler.getPrereqFailColorAsHtmlEnd());
                }
            }
        }
        return sb.toString();
    }

}
