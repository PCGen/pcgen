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
package plugin.lsttokens.kit.gear;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.EquipmentModifier;
import pcgen.core.kit.KitGear;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

import org.junit.jupiter.api.Test;

public class EqModTokenTest extends AbstractKitTokenTestCase<KitGear>
{

    static EqmodToken token = new EqmodToken();
    static CDOMSubLineLoader<KitGear> loader = new CDOMSubLineLoader<>(
            "TABLE", KitGear.class);

    @Override
    public Class<KitGear> getCDOMClass()
    {
        return KitGear.class;
    }

    @Override
    public CDOMSubLineLoader<KitGear> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<KitGear> getToken()
    {
        return token;
    }

    public static char getJoinCharacter()
    {
        return '.';
    }

    // TODO Implement after 5.16
    // @Test
    // public void testInvalidMiddleNone() throws PersistenceLayerException
    // {
    // assertFalse(parse("EQMOD1.NONE.EQMOD2"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidStartingNone() throws PersistenceLayerException
    // {
    // assertFalse(parse("NONE.EQMOD2"));
    // assertNoSideEffects();
    // }
    //
    // @Test
    // public void testInvalidEndingNone() throws PersistenceLayerException
    // {
    // assertFalse(parse("EQMOD2.NONE"));
    // assertNoSideEffects();
    // }

    @Test
    public void testInvalidEmptyAssociation()
    {
        assertFalse(parse("EQMOD2|"));
    }

    @Test
    public void testInvalidTrailingAssociation()
    {
        assertFalse(parse("EQMOD2|Assoc|"));
    }

    @Test
    public void testInvalidEmptyModAssociation()
    {
        assertFalse(parse("|Assoc|Assoc2"));
    }

    @Test
    public void testInvalidEmptySecondModAssociation()
    {
        assertFalse(parse("MOD1.|Assoc|Assoc2"));
    }

    @Test
    public void testInvalidEmptySecondModAfterAssociation()
    {
        assertFalse(parse("MOD1|ModAssoc.|Assoc|Assoc2"));
    }

    @Test
    public void testInvalidEmptyComplexAssociation()
    {
        assertFalse(parse("MOD1|ModAssoc[]"));
    }

    @Test
    public void testInvalidNoOpenBracketComplexAssociation()
    {
        assertFalse(parse("MOD1|ModAssoc Assoc]"));
    }

    @Test
    public void testInvalidTwoOpenBracketComplexAssociation()
    {
        assertFalse(parse("MOD1|ModAssoc[[Assoc]"));
    }

    @Test
    public void testInvalidDoubleBarAssociation()
    {
        assertFalse(parse("EQMOD2|Assoc||Assoc2"));
    }

    public void testRoundRobinOnlyAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD2|9500");
    }

    public void testRoundRobinComplexAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD2|COST[9500]");
    }

    // public void testRoundRobinInnerBracketAssociation()
    // throws PersistenceLayerException
    // {
    // runRoundRobin("EQMOD2|COST[[9500]]");
    // }

    @Test
    public void testRoundRobinComplexMultipleAssociation()
            throws PersistenceLayerException
    {
        primaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        secondaryContext.getReferenceContext().constructCDOMObject(EquipmentModifier.class,
                "EQMOD2");
        runRoundRobin("EQMOD2|COST[9500]PLUS[+1]");
    }

    @Test
    public void testInvalidInputEmptyString()
    {
        assertFalse(parse(""));
    }

    @Test
    public void testInvalidInputJoinOnly()
    {
        assertFalse(parse(Character.toString(getJoinCharacter())));
    }

    @Test
    public void testInvalidInputString()
    {
        assertTrue(parse("String"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputType()
    {
        assertTrue(parse("TestType"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputJoinedComma()
    {
        if (getJoinCharacter() != ',')
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertTrue(parse("TestWP1,TestWP2"));
            assertConstructionError();
        }
    }

    private static void construct(LoadContext context, String string)
    {
        context.getReferenceContext().constructCDOMObject(EquipmentModifier.class, string);
    }

    // TODO Need to catch this someday - currently not caught as a problem
    // TestWP2 is assumed to be an association
    // @Test
    // public void testInvalidInputJoinedPipe() throws PersistenceLayerException
    // {
    // if (getJoinCharacter() != '|')
    // {
    // construct(primaryContext, "TestWP1");
    // construct(primaryContext, "TestWP2");
    // boolean parse = parse("TestWP1|TestWP2");
    // if (parse)
    // {
    // assertFalse(primaryContext.ref.validate());
    // }
    // else
    // {
    // }
    // }
    // }

    @Test
    public void testInvalidInputJoinedDot()
    {
        if (getJoinCharacter() != '.')
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertTrue(parse("TestWP1.TestWP2"));
            assertConstructionError();
        }
    }

    @Test
    public void testInvalidInputAny()
    {
        try
        {
            boolean result = parse("ANY");
            if (result)
            {
                assertConstructionError();
            }
        } catch (IllegalArgumentException e)
        {
            //This is okay too
        }
    }

    @Test
    public void testInvalidInputCheckType()
    {
        try
        {
            boolean result = token.parseToken(primaryContext, primaryProf,
                    "TYPE=TestType").passed();
            if (result)
            {
                assertConstructionError();
            }
        } catch (IllegalArgumentException e)
        {
            // This is okay too
        }
    }

    @Test
    public void testInvalidListEnd()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1" + getJoinCharacter()));
    }

    @Test
    public void testInvalidListStart()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse(getJoinCharacter() + "TestWP1"));
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("TestWP2" + getJoinCharacter() + getJoinCharacter()
                + "TestWP1"));
    }

    @Test
    public void testInvalidInputCheckMult()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, "TestWP1");
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testValidInputs()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertCleanConstruction();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        runRoundRobin("TestWP1");
    }

    @Test
    public void testRoundRobinThree() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2"
                + getJoinCharacter() + "TestWP3");
    }

    @Test
    public void testInputInvalidAddsBasicNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        construct(primaryContext, "TestWP4");
        construct(secondaryContext, "TestWP4");
        assertTrue(parse("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertTrue(parseSecondary("TestWP1" + getJoinCharacter() + "TestWP2"));
        assertFalse(parse("TestWP3" + getJoinCharacter() + getJoinCharacter()
                + "TestWP4"));
    }
}
