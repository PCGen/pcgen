/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import pcgen.system.LanguageBundle;

/**
 * SkillsExport defines which skills are to be displayed on an output sheet.
 */
public enum SkillFilter
{
    /**
     * Display only those skills in which the character has ranks
     */
    Ranks(0, "RANKS", LanguageBundle.getString("in_Prefs_skillFilterRanks")),

    /**
     * Display only those skills for which the character's total modifier
     * deviates from the default of ability bonus plus size bonus. This includes
     * all skills in which the character has ranks or some other kind of bonus.
     */
    NonDefault(1, "NONDEFAULT", LanguageBundle.getString("in_Prefs_skillFilterNondefault")),

    /**
     * Display only those skills the character can use, i.e. all skills in which
     * the character has ranks and all skills which can be used untrained
     */
    Usable(2, "USABLE", LanguageBundle.getString("in_Prefs_skillFilterUsable")),

    /**
     * Display all skills
     */
    All(3, "ALL", LanguageBundle.getString("in_Prefs_skillFilterAll")),

    /**
     * Global preference to use the skill display mode defined on the
     * character's skill tab.
     *
     * @deprecated - the user's skill setting is now always used if present.
     * This is retained for compatibility with saved characters.
     */

    @Deprecated
    SkillsTab(4, "", LanguageBundle.getString("in_Prefs_skillFilterSkillTab")),

    Selected(5, "SELECTED", null);

    private final int value;
    private final String token;
    private final String text;

    private SkillFilter(int value, String token, String text)
    {
        this.value = value;
        this.token = token;
        this.text = text;
    }

    public int getValue()
    {
        return value;
    }

    public String getToken()
    {
        return token;
    }

    @Override
    public String toString()
    {
        return text;
    }

    public static SkillFilter getByValue(int value)
    {
        for (SkillFilter filter : values())
        {
            if (filter.getValue() == value)
            {
                return filter;
            }
        }
        return null;
    }

    public static SkillFilter getByToken(String value)
    {
        for (SkillFilter filter : values())
        {
            if (filter.getToken().equalsIgnoreCase(value))
            {
                return filter;
            }
        }
        return null;
    }

}
