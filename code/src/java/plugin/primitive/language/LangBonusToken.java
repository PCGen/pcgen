/*
 * Copyright 2010 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.primitive.language;

import java.util.Collection;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Converter;
import pcgen.cdom.base.PrimitiveFilter;
import pcgen.cdom.enumeration.GroupingState;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

/**
 * LangBonusToken is a Primitive that includes bonus languages available to a PC.
 */
public class LangBonusToken implements PrimitiveToken<Language>, PrimitiveFilter<Language>
{

    private static final Class<Language> LANGUAGE_CLASS = Language.class;
    private CDOMReference<Language> allLanguages;

    @Override
    public boolean initialize(LoadContext context, Class<Language> cl, String value, String args)
    {
        allLanguages = context.getReferenceContext().getCDOMAllReference(LANGUAGE_CLASS);
        return (value == null) && (args == null);
    }

    @Override
    public String getTokenName()
    {
        return "LANGBONUS";
    }

    @Override
    public Class<Language> getReferenceClass()
    {
        return LANGUAGE_CLASS;
    }

    @Override
    public String getLSTformat(boolean useAny)
    {
        return getTokenName();
    }

    @Override
    public boolean allow(PlayerCharacter pc, Language l)
    {
        return pc.getDisplay().getLanguageBonusSelectionList().contains(l);
    }

    @Override
    public GroupingState getGroupingState()
    {
        return GroupingState.ANY;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof LangBonusToken;
    }

    @Override
    public int hashCode()
    {
        return 3568;
    }

    @Override
    public <R> Collection<? extends R> getCollection(PlayerCharacter pc, Converter<Language, R> c)
    {
        return c.convert(allLanguages, this);
    }
}
