/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.race;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListContextTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MonCSkillTokenTest extends
        AbstractListContextTokenTestCase<Race, Skill>
{
    static MoncskillToken token = new MoncskillToken();
    static CDOMTokenLoader<Race> loader = new CDOMTokenLoader<>();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        ClassSkillList a =
                primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Scary Monster");
        a.addType(Type.MONSTER);
        ClassSkillList b =
                secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Scary Monster");
        b.addType(Type.MONSTER);
    }

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<Race> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Race> getToken()
    {
        return token;
    }

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

    @Test
    public void testRoundRobinList() throws PersistenceLayerException
    {
        runRoundRobin("LIST");
    }

    @Test
    public void testRoundRobinPattern() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("Pattern%");
    }

    @Test
    public void testValidInputClearList()
    {
        assertTrue(parse(".CLEAR.LIST"));
    }

    @Test
    public void testInvalidInputAllList()
    {
        assertFalse(parse("ALL" + getJoinCharacter() + "LIST"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputAllPattern()
    {
        assertFalse(parse("ALL" + getJoinCharacter() + "Pattern%"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinThreePattern() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("TestWP%" + getJoinCharacter() + "TestWZ%");
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Override
    protected void doCustomAssociations(AssociatedPrereqObject sao)
    {
        sao.setAssociation(AssociationKey.SKILL_COST, SkillCost.CLASS);
    }

    @Override
    protected CDOMReference<? extends CDOMList<? extends PrereqObject>> getListReference()
    {
        return primaryContext.getReferenceContext().getCDOMTypeReference(ClassSkillList.class, "Monster");
    }
}
