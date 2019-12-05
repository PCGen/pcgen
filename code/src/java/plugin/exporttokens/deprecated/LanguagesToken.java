/*
 * LanguagesToken.java
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
package plugin.exporttokens.deprecated;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.core.Language;
import pcgen.core.display.CharacterDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.AbstractExportToken;

/**
 * LANGUAGES.x Token
 */
public class LanguagesToken extends AbstractExportToken
{
    @Override
    public String getTokenName()
    {
        return "LANGUAGES";
    }

    @Override
    public String getToken(String tokenSource, CharacterDisplay display, ExportHandler eh)
    {
        StringTokenizer aTok = new StringTokenizer(tokenSource, ".");
        aTok.nextToken();

        int languageIndex = 0;
        int startIndex = 0;

        List<Language> languageList = new ArrayList<>(display.getSortedLanguageSet());

        if (aTok.hasMoreTokens())
        {
            try
            {
                startIndex = Integer.parseInt(aTok.nextToken());
                languageIndex = startIndex + 1;
                /*
                 * PERFORMANCE This can actually shortcut the subList below, as
                 * it really is only grabbling one language
                 */
            } catch (NumberFormatException e)
            {
                //TODO: Should this really be ignored?
            }
        } else
        {
            languageIndex = languageList.size();
        }

        if (languageList.isEmpty())
        {
            return "";
        }

        List<Language> subList =
                languageList.subList(Math.max(startIndex, 0), Math.min(languageIndex, languageList.size()));

        return StringUtil.join(subList, ", ");
    }
}
