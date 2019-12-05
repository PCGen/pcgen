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
package plugin.lsttokens.skill;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassesTokenTest extends AbstractCDOMTokenTestCase<Skill>
{

    static ClassesToken token = new ClassesToken();
    static CDOMTokenLoader<Skill> loader = new CDOMTokenLoader<>();

    @BeforeAll
    public static void ltClassSetUp() throws PersistenceLayerException
    {
        TokenRegistration.register(new PreClassParser());
    }

    @BeforeEach
    @Override
    public final void setUp() throws PersistenceLayerException,
            URISyntaxException
    {
        super.setUp();
    }

    @Override
    public Class<Skill> getCDOMClass()
    {
        return Skill.class;
    }

    @Override
    public CDOMLoader<Skill> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Skill> getToken()
    {
        return token;
    }

    @Test
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputLeadingBar()
    {
        assertFalse(parse("|Wizard"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTrailingBar()
    {
        assertFalse(parse("Wizard|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNegationMix()
    {
        assertFalse(parse("Wizard|!Sorcerer"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNegationMixTwo()
    {
        assertFalse(parse("!Wizard|Sorcerer"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDoublePipe()
    {
        assertFalse(parse("Wizard||Sorcerer"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEmptyType()
    {
        try
        {
            boolean ret = parse("TYPE.");
            if (ret)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }

        } catch (IllegalArgumentException e)
        {
            // Okay as well
        }
    }

    @Test
    public void testInvalidInputNotClass()
    {
        assertTrue(parse("Wizard"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputNotClassCompound()
    {
        assertTrue(parse("Wizard|Sorcerer"));
        assertConstructionError();
    }

    // @Test(expected = IllegalArgumentException.class)
    public void testInvalidInputAllPlus()
    {
        try
        {
            assertFalse(parse("Wizard|ALL"));
        } catch (IllegalArgumentException iae)
        {
            // OK as well
        }
        assertNoSideEffects();
    }

    // @Test(expected = IllegalArgumentException.class)
    public void testInvalidInputNegativeAllPlus()
    {
        try
        {
            assertFalse(parse("!Wizard|ALL"));
        } catch (IllegalArgumentException iae)
        {
            // OK as well
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNegativeAll()
    {
        // This technically gets caught by the PRECLASS parser...
        assertFalse(parse("!ALL"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNonSensicalAll()
    {
        assertFalse(parse("ALL|!ALL"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNonSensicalAllSpecific()
    {
        assertFalse(parse("ALL|Wizard"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinAll() throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        runRoundRobin("ALL");
    }

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        runRoundRobin("Wizard");
    }

    @Test
    public void testRoundRobinNegated() throws PersistenceLayerException
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        runRoundRobin("ALL|!Wizard");
    }

    @Test
    public void testRoundRobinPipe() throws PersistenceLayerException
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        runRoundRobin("Sorcerer|Wizard");
    }

    @Test
    public void testRoundRobinNegatedPipe() throws PersistenceLayerException
    {
        assertEquals(0, primaryContext.getWriteMessageCount());
        primaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, "Wizard");
        secondaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Wizard");
        primaryContext.getReferenceContext()
                .constructCDOMObject(ClassSkillList.class, "Sorcerer");
        secondaryContext.getReferenceContext().constructCDOMObject(ClassSkillList.class,
                "Sorcerer");
        runRoundRobin("ALL|!Sorcerer|!Wizard");
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Sorcerer";
    }

    @Override
    protected String getLegalValue()
    {
        return "Bard|Wizard";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return strings -> new String[]{"Bard|Sorcerer|Wizard"};
    }
}
