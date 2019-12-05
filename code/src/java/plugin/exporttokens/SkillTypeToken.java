/*
 * SkillToken.java
 * Copyright 2004 (C) James Dempsey <jdempsey@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.display.SkillDisplay;
import pcgen.io.ExportHandler;
import pcgen.io.exporttoken.SkillToken;
import pcgen.util.Logging;

/**
 * {@code SkillTypeToken} outputs the value of the Skill at
 * position x in a subset of character's skill list.The subset is
 * those skills matching the supplied type. The format
 * for this tag is SKILLTYPE.pos.type.property
 * eg SKILLTYPE.2.KNOWLEDGE.RANK
 */

// SKILLTYPE
public class SkillTypeToken extends SkillToken
{
    /**
     * token name
     */
    public static final String TOKEN_NAME = "SKILLTYPE";

    @Override
    public String getTokenName()
    {
        return TOKEN_NAME;
    }

    @Override
    public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
    {
        SkillDetails details = buildSkillDetails(tokenSource);

        if (details.getPropertyCount() < 2)
        {
            return "";
        }

        Skill aSkill = getSkill(tokenSource, pc, details, eh);

        return getSkillProperty(aSkill, details.getProperty(1), pc);
    }

    /**
     * Select the target skill based on the supplied critieria. Searches
     * through the characters skill list selecting those that have the type
     * of details.properties[0] and then uses the index in
     * details.skillId to select the skill.
     *
     * @param tokenSource The token being processed. Used for error reporting.
     * @param pc          The character being processed.
     * @param details     The parsed details of the token.
     * @param eh          The ExportHandler
     * @return The matching skill, or null if none match.
     */
    private Skill getSkill(String tokenSource, PlayerCharacter pc, SkillDetails details, ExportHandler eh)
    {
        int skillIndex;

        try
        {
            skillIndex = Integer.parseInt(details.getSkillId());
        } catch (NumberFormatException exc)
        {
            Logging.errorPrint("Error replacing SKILLTYPE." + tokenSource, exc);
            return null;
        }

        final List<Skill> skillSubset = new ArrayList<>();
        for (Skill bSkill : SkillDisplay.getSkillListInOutputOrder(pc))
        {
            if (bSkill.isType(details.getProperty(0)))
            {
                skillSubset.add(bSkill);
            }
        }

        if ((skillIndex >= (skillSubset.size() - 1)) && eh != null && eh.getExistsOnly())
        {
            eh.setNoMoreItems(true);
        }

        Skill aSkill = null;
        if (skillIndex <= (skillSubset.size() - 1))
        {
            aSkill = skillSubset.get(skillIndex);
        }
        return aSkill;
    }

}
