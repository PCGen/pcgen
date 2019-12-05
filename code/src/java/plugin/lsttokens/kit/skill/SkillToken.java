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

package plugin.lsttokens.kit.skill;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Skill;
import pcgen.core.kit.KitSkill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * SKILL for Kit Skill
 */
public class SkillToken extends AbstractTokenWithSeparator<KitSkill> implements CDOMPrimaryToken<KitSkill>
{
    private static final Class<Skill> SKILL_CLASS = Skill.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "SKILL";
    }

    @Override
    public Class<KitSkill> getTokenClass()
    {
        return KitSkill.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitSkill kitSkill, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            CDOMReference<Skill> ref = TokenUtilities.getTypeOrPrimitive(context, SKILL_CLASS, tokText);
            if (ref == null)
            {
                return ParseResult.INTERNAL_ERROR;
            }
            kitSkill.addSkill(ref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitSkill kitSkill)
    {
        Collection<CDOMReference<Skill>> ref = kitSkill.getSkills();
        if (ref == null || ref.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(ref, Constants.PIPE)};
    }
}
