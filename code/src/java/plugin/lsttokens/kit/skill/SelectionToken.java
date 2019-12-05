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

package plugin.lsttokens.kit.skill;

import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Language;
import pcgen.core.kit.KitSkill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * SELECTION token for KitSkill
 */
public class SelectionToken extends AbstractTokenWithSeparator<KitSkill> implements CDOMPrimaryToken<KitSkill>
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
        return "SELECTION";
    }

    @Override
    public Class<KitSkill> getTokenClass()
    {
        return KitSkill.class;
    }

    @Override
    protected char separator()
    {
        return ',';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitSkill kitSkill, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.COMMA);

        while (tok.hasMoreTokens())
        {
            kitSkill.addSelection(context.getReferenceContext().getCDOMReference(LANGUAGE_CLASS, tok.nextToken()));
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitSkill kitSkill)
    {
        List<CDOMSingleRef<Language>> ref = kitSkill.getSelections();
        if (ref == null || ref.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(ref, Constants.COMMA)};
    }

    //TODO DeferredToken - check this?
	/*
	 if (SkillLanguage.isLanguage(aSkill) && !selection.isEmpty())
	 */
}
