/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.pcclass.level;

import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class CSkillTokenTest extends AbstractListInputTokenTestCase<PCClassLevel, Skill>
{
    private static CDOMPrimaryToken<PCClassLevel> token = new CskillToken();
    private static CDOMTokenLoader<PCClassLevel> loader =
            new CDOMTokenLoader<>();

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public Class<Skill> getTargetClass()
    {
        return Skill.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return true;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    public boolean isClearDotLegal()
    {
        return true;
    }

    @Override
    public boolean isClearLegal()
    {
        return true;
    }

    @Override
    public CDOMLoader<PCClassLevel> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCClassLevel> getCDOMClass()
    {
        return PCClassLevel.class;
    }

    @Override
    public CDOMPrimaryToken<PCClassLevel> getToken()
    {
        return token;
    }

    @Test
    public void testRoundRobinList()
    {
        boolean result = parse("LIST");
        if (result)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testRoundRobinPattern() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("Pattern%");
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

}
