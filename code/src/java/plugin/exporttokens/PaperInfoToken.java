/*
 * PaperInfoToken.java
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.exporttokens;

import pcgen.core.Globals;
import pcgen.core.PaperInfo;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

//PAPERINFO
public class PaperInfoToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "PAPERINFO";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        String oString = tokenSource;
        String sourceText = tokenSource.substring(10);

        int infoType = -1;

        if (sourceText.startsWith("NAME"))
        {
            infoType = PaperInfo.NAME;
        } else if (sourceText.startsWith("HEIGHT"))
        {
            infoType = PaperInfo.HEIGHT;
        } else if (sourceText.startsWith("WIDTH"))
        {
            infoType = PaperInfo.WIDTH;
        } else if (sourceText.startsWith("MARGIN"))
        {
            sourceText = sourceText.substring(6);

            if (sourceText.startsWith("TOP"))
            {
                infoType = PaperInfo.TOPMARGIN;
            } else if (sourceText.startsWith("BOTTOM"))
            {
                infoType = PaperInfo.BOTTOMMARGIN;
            } else if (sourceText.startsWith("LEFT"))
            {
                infoType = PaperInfo.LEFTMARGIN;
            } else if (sourceText.startsWith("RIGHT"))
            {
                infoType = PaperInfo.RIGHTMARGIN;
            }
        }

        if (infoType >= 0)
        {
            int offs = sourceText.indexOf('=');
            String info = Globals.getPaperInfo(infoType);

            if (info == null)
            {
                if (offs >= 0)
                {
                    oString = sourceText.substring(offs + 1);
                }
            } else
            {
                oString = info;
            }
        }

        return oString;
    }
}
