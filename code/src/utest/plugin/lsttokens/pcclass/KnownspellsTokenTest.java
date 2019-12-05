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
package plugin.lsttokens.pcclass;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import pcgen.cdom.base.Constants;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListInputTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class KnownspellsTokenTest extends AbstractListInputTokenTestCase<PCClass, Spell>
{

    static KnownspellsToken token = new KnownspellsToken();
    static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @Override
    public CDOMLoader<PCClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<PCClass> getToken()
    {
        return token;
    }

    @Override
    public char getJoinCharacter()
    {
        return '|';
    }

    @Override
    public Class<Spell> getTargetClass()
    {
        return Spell.class;
    }

    @Override
    public boolean isTypeLegal()
    {
        return true;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Test
    public void testInvalidInputEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Override
    @Test
    public void testInvalidInputJoinedComma()
    {
        if (getJoinCharacter() != ',')
        {
            construct(primaryContext, "TestWP1");
            construct(primaryContext, "TestWP2");
            assertFalse(parse("TestWP1,TestWP2"));
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputTwoType()
    {
        assertFalse(parse("TYPE=TestWP1,TYPE=TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputSpellAndType()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("TestWP1,TYPE=TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputLevelEmpty()
    {
        assertFalse(parse("LEVEL="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputLevelNaN()
    {
        assertFalse(parse("LEVEL=One"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputLevelDouble()
    {
        assertFalse(parse("LEVEL=1.0"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputStart()
    {
        assertFalse(parse(",LEVEL=2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputEnd()
    {
        assertFalse(parse("LEVEL=2,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNegative()
    {
        assertFalse(parse("LEVEL=-2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputDouble()
    {
        if (isTypeLegal())
        {
            assertFalse(parse("TYPE=Foo,,LEVEL=2"));
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidInputTwoLevel()
    {
        assertFalse(parse("LEVEL=1,LEVEL=2"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinWithLevel() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("TestWP1" + getJoinCharacter() + "LEVEL=1");
    }

    @Test
    public void testRoundRobinLevels() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("LEVEL=0|LEVEL=1|LEVEL=2|LEVEL=3|LEVEL=4");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        Spell a = construct(primaryContext, "TestWP2");
        a.addToListFor(ListKey.TYPE, Type.getConstant("SpellType"));
        Spell c = construct(secondaryContext, "TestWP2");
        c.addToListFor(ListKey.TYPE, Type.getConstant("SpellType"));
        Spell b = construct(primaryContext, "TestWP3");
        b.addToListFor(ListKey.TYPE, Type.getConstant("ZOther"));
        b.addToListFor(ListKey.TYPE, Type.getConstant("ZType"));
        Spell d = construct(secondaryContext, "TestWP3");
        d.addToListFor(ListKey.TYPE, Type.getConstant("ZOther"));
        d.addToListFor(ListKey.TYPE, Type.getConstant("ZType"));
        runRoundRobin("TYPE=SpellType,LEVEL=0|TYPE=SpellType,LEVEL=1|TYPE=ZOther.ZType,LEVEL=1");
    }

    @Test
    public void testRoundRobinTypeLevel() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        Spell a = construct(primaryContext, "TestWP2");
        a.addToListFor(ListKey.TYPE, Type.getConstant("SpellType"));
        Spell c = construct(secondaryContext, "TestWP2");
        c.addToListFor(ListKey.TYPE, Type.getConstant("SpellType"));
        runRoundRobin("TestWP1" + getJoinCharacter() + "TYPE=SpellType,LEVEL=1");
    }

    @Test
    public void testRoundRobinTestEqualThreeLevel()
            throws PersistenceLayerException
    {
        if (isTypeLegal())
        {
            Spell b = construct(primaryContext, "TestWP3");
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            b.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            Spell d = construct(secondaryContext, "TestWP3");
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestAltType"));
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestThirdType"));
            d.addToListFor(ListKey.TYPE, Type.getConstant("TestType"));
            runRoundRobin("LEVEL=2|TYPE=TestAltType.TestThirdType.TestType,LEVEL=3");
        }
    }

    @Override
    public boolean isClearDotLegal()
    {
        return false;
    }

    @Override
    public boolean isClearLegal()
    {
        return true;
    }

    @Override
    public String getClearString()
    {
        return Constants.LST_DOT_CLEAR_ALL;
    }

    @Override
    public boolean allowDups()
    {
        return false;
    }

    @Test
    public void testUnparseOne()
    {
        Spell fireball = primaryContext.getReferenceContext().constructCDOMObject(Spell.class,
                "Fireball");
        CDOMDirectSingleRef<Spell> ref = CDOMDirectSingleRef.getRef(fireball);
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(ref, null));
        String[] a = getToken().unparse(primaryContext, primaryProf);
        expectSingle(a, "Fireball");
    }

    @Test
    public void testUnparseMultiple()
    {
        Spell fireball = primaryContext.getReferenceContext().constructCDOMObject(Spell.class,
                "Fireball");
        Spell bolt = primaryContext.getReferenceContext().constructCDOMObject(Spell.class,
                "Lightning Bolt");
        CDOMDirectSingleRef<Spell> ref = CDOMDirectSingleRef.getRef(fireball);
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(ref, null));
        CDOMDirectSingleRef<Spell> ref2 = CDOMDirectSingleRef.getRef(bolt);
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(ref2, null));
        String[] a = getToken().unparse(primaryContext, primaryProf);
        expectSingle(a, "Fireball" + getJoinCharacter() + "Lightning Bolt");
    }

    @Test
    public void testUnparseLevel()
    {
        CDOMGroupRef<Spell> all = primaryContext.getReferenceContext()
                .getCDOMAllReference(Spell.class);
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(all, 4));
        String[] sap = getToken().unparse(primaryContext, primaryProf);
        expectSingle(sap, "LEVEL=4");
    }

    @Test
    public void testUnparseNegativeLevel()
    {
        try
        {
            CDOMGroupRef<Spell> all = primaryContext.getReferenceContext()
                    .getCDOMAllReference(Spell.class);
            primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                    new KnownSpellIdentifier(all, -3));
            assertBadUnparse();
        } catch (IllegalArgumentException e)
        {
            //Good here too :)
        }
    }

    @Test
    public void testUnparseTypeLevel()
    {
        CDOMGroupRef<Spell> cool = primaryContext.getReferenceContext().getCDOMTypeReference(
                Spell.class, "Cool");
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(cool, 4));
        String[] sap = getToken().unparse(primaryContext, primaryProf);
        expectSingle(sap, "TYPE=Cool,LEVEL=4");
    }

    @Test
    public void testUnparseMultTypeLevel()
    {
        CDOMGroupRef<Spell> cool = primaryContext.getReferenceContext().getCDOMTypeReference(
                Spell.class, "Cool");
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(cool, 4));
        CDOMGroupRef<Spell> awesome = primaryContext.getReferenceContext().getCDOMTypeReference(
                Spell.class, "Awesome");
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS,
                new KnownSpellIdentifier(awesome, 7));
        String[] sap = getToken().unparse(primaryContext, primaryProf);
        expectSingle(sap, "TYPE=Awesome,LEVEL=7|TYPE=Cool,LEVEL=4");
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.addToListFor(ListKey.KNOWN_SPELLS, null);
        try
        {
            assertNull(getToken().unparse(primaryContext, primaryProf));
        } catch (NullPointerException e)
        {
            // This is okay too
        }
    }
}
