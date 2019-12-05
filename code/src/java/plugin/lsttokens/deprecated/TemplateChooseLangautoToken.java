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
package plugin.lsttokens.deprecated;

import pcgen.core.PCTemplate;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class TemplateChooseLangautoToken extends AbstractTokenWithSeparator<PCTemplate>
        implements CDOMCompatibilityToken<PCTemplate>
{

    @Override
    public String getTokenName()
    {
        return "CHOOSE";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCTemplate template, String value)
    {
        if (!value.startsWith("LANGAUTO|"))
        {
            return new ParseResult.Fail("Incompatible with CHOOSE:LANGAUTO replacement in Template");
        }
        Logging.deprecationPrint("CHOOSE:LANGAUTO is deprecated, " + "please use CHOOSE:LANG and AUTO:LANG|%LIST");

        if (!context.processToken(template, "CHOOSE", "LANG|" + value.substring(9)))
        {
            Logging.replayParsedMessages();
            return new ParseResult.Fail("Internal Error in delegation of CHOOSE:LANGAUTO to CHOOSE:LANG");
        }


        if (!context.processToken(template, "AUTO", "LANG|%LIST"))
        {
            Logging.replayParsedMessages();
            return new ParseResult.Fail("Internal Error in delegation of CHOOSE:LANGAUTO to AUTO:LANG");
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public Class<PCTemplate> getTokenClass()
    {
        return PCTemplate.class;
    }

    @Override
    public int compatibilityLevel()
    {
        return 6;
    }

    @Override
    public int compatibilitySubLevel()
    {
        return 4;
    }

    @Override
    public int compatibilityPriority()
    {
        return 14;
    }

}
