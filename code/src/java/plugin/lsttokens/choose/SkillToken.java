/*
 * Copyright 2009 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.core.Skill;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractQualifiedChooseToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * New chooser plugin, handles skills.
 */
public class SkillToken extends AbstractQualifiedChooseToken<Skill>
{

    private static final Class<Skill> SKILL_CLASS = Skill.class;

    @Override
    public String getTokenName()
    {
        return "SKILL";
    }

    @Override
    protected String getDefaultTitle()
    {
        return "Skill choice";
    }

    @Override
    public Skill decodeChoice(LoadContext context, String s)
    {
        return context.getReferenceContext().silentlyGetConstructedCDOMObject(SKILL_CLASS, s);
    }

    @Override
    public String encodeChoice(Skill choice)
    {
        return choice.getKeyName();
    }

    @Override
    protected AssociationListKey<Skill> getListKey()
    {
        return AssociationListKey.getKeyFor(SKILL_CLASS, "CHOOSE*SKILL");
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return super.parseTokenWithSeparator(context, context.getReferenceContext().getManufacturer(SKILL_CLASS), obj,
                value);
    }

    @Override
    protected String getPersistentFormat()
    {
        return "SKILL";
    }
}
