/*
 * Copyright 2008 (C) James Dempsey
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

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.kit.KitLangBonus;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * The Class {@code LangBonusToken} handles the LANGBONUS kit tag.
 */
public class LangBonusToken extends AbstractTokenWithSeparator<KitLangBonus> implements CDOMPrimaryToken<KitLangBonus>
{

    private static final Class<Language> LANGUAGE_CLASS = Language.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "LANGBONUS";
    }

    @Override
    public Class<KitLangBonus> getTokenClass()
    {
        return KitLangBonus.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitLangBonus kitLangBonus, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            kitLangBonus.addLanguage(context.getReferenceContext().getCDOMReference(LANGUAGE_CLASS, tok.nextToken()));
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitLangBonus kitLangBonus)
    {
        List<CDOMSingleRef<Language>> languages = kitLangBonus.getLanguages();
        if (languages == null || languages.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(languages, Constants.PIPE)};
    }
}
