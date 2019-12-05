/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.kit.skill;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.Skill;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class SkillTokenTest extends AbstractKitTokenTestCase<KitSkill>
{

    static SkillToken token = new SkillToken();
    static CDOMSubLineLoader<KitSkill> loader = new CDOMSubLineLoader<>(
            "SKILL", KitSkill.class);

    @Override
    public Class<KitSkill> getCDOMClass()
    {
        return KitSkill.class;
    }

    @Override
    public CDOMSubLineLoader<KitSkill> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitSkill> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmptyCount()
    {
        assertTrue(parse("Fireball"));
        assertConstructionError();
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
        secondaryContext.getReferenceContext().constructCDOMObject(Skill.class, "Fireball");
        runRoundRobin("Fireball");
    }

    @Test
    public void testInvalidInputEmptyType()
    {
        assertFalse(parse("TYPE="));
    }

    @Test
    public void testInvalidInputTrailingType()
    {
        assertFalse(parse("TYPE=One."));
    }

    @Test
    public void testInvalidInputDoubleType()
    {
        assertFalse(parse("TYPE=One..Two"));
    }

    @Test
    public void testRoundRobinType() throws PersistenceLayerException
    {
        Skill a = primaryContext.getReferenceContext().constructCDOMObject(Skill.class,
                "Fireball");
        a.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        Skill b = secondaryContext.getReferenceContext().constructCDOMObject(Skill.class,
                "Fireball");
        b.addToListFor(ListKey.TYPE, Type.getConstant("Foo"));
        runRoundRobin("TYPE=Foo");
    }
}
