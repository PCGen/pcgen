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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

public class SpellListTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{
    static SpelllistToken token = new SpelllistToken();
    static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

    @Override
    public Class<? extends PCClass> getCDOMClass()
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

    @Test
    public void testInvalidInputString()
    {
        assertFalse(parse("String"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlyNumber()
    {
        assertFalse(parse("1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNoObject()
    {
        assertFalse(parse("1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDoublePipe()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputNaN()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("NaN|TestWP1"));
    }

    @Test
    public void testInvalidInputType()
    {
        try
        {
            assertFalse(parse("1|TYPE=Test"));
        } catch (IllegalArgumentException e)
        {
            // OK
        }
    }

    @Test
    public void testInvalidInputUnbuilt()
    {
        assertTrue(parse("1|String"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputUnbuiltDomain()
    {
        assertTrue(parse("1|DOMAIN.String"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputNoCount()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("|TestWP1|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputJoinedDot()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertTrue(parse("1|TestWP1.TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testValidInputAll()
    {
        assertTrue(parse("1|ALL"));
    }

    @Test
    public void testInvalidListEnd()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1|TestWP1|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListStart()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidZeroCount()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("0|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDomainOnly()
    {
        if (parse("0|DOMAIN"))
        {
            assertConstructionError();
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDomainDotOnly()
    {
        assertFalse(parse("0|DOMAIN."));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDomainDoubleDot()
    {
        constructDomain(primaryContext, "TestWP1");
        if (parse("0|DOMAIN.TestWP1."))
        {
            assertConstructionError();
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidDomainSeparatorClarification()
    {
        constructDomain(primaryContext, "TestWP1");
        constructDomain(primaryContext, "TestWP2");
        // This is NOT valid!!! Must list domains separately...
        if (parse("0|DOMAIN.TestWP1.TestWP2"))
        {
            assertConstructionError();
        }
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNegativeCount()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("-1|TestWP1|TestWP2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListDoubleJoin()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        assertFalse(parse("1|TestWP2||TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputCheckMult()
    {
        // Explicitly do NOT build TestWP2
        construct(primaryContext, "TestWP1");
        assertTrue(parse("1|TestWP1|TestWP2"));
        assertConstructionError();
    }

    @Test
    public void testInvalidInputAnyItem()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1|ALL|TestWP1"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputItemAny()
    {
        construct(primaryContext, "TestWP1");
        assertFalse(parse("1|TestWP1|ALL"));
        assertNoSideEffects();
    }

    @Test
    public void testInputInvalidAddsAllNoSideEffect()
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(secondaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP3");
        assertTrue(parse("1|TestWP1|TestWP2"));
        assertTrue(parseSecondary("1|TestWP1|TestWP2"));
        assertFalse(parse("1|TestWP3|ALL"));
        assertNoSideEffects();
    }

    @Test
    public void testRoundRobinOne() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("1|TestWP1");
    }

    @Test
    public void testRoundRobinAll() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin("1|ALL");
    }

    @Test
    public void testRoundRobinOneDomain() throws PersistenceLayerException
    {
        constructDomain(primaryContext, "TestWP1");
        constructDomain(secondaryContext, "TestWP1");
        runRoundRobin("1|DOMAIN.TestWP1");
    }

    @Test
    public void testRoundRobinThree() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        constructDomain(primaryContext, "AestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        constructDomain(secondaryContext, "AestWP3");
        // Note force of Domain after Classes in alpha ordering
        runRoundRobin("2|TestWP1|TestWP2|DOMAIN.AestWP3");
    }

    protected static ClassSpellList construct(LoadContext loadContext, String one)
    {
        return loadContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, one);
    }

    protected static void constructDomain(LoadContext loadContext, String one)
    {
        loadContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, one);
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "2|TestWP1|TestWP3";
    }

    @Override
    protected String getLegalValue()
    {
        return "1|TestWP1|TestWP2";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.OVERWRITE;
    }

    protected PersistentTransitionChoice<CDOMListObject<Spell>> buildChoice(
            CDOMReference<ClassSpellList>... refs)
    {
        ReferenceChoiceSet<ClassSpellList> rcs = buildRCS(refs);
        assertTrue(rcs.getGroupingState().isValid());
        return buildTC(rcs);
    }

    protected PersistentTransitionChoice<CDOMListObject<Spell>> buildTC(
            ReferenceChoiceSet<ClassSpellList> rcs)
    {
        ChoiceSet<ClassSpellList> cs = new ChoiceSet<>(getToken()
                .getTokenName(), rcs);
        cs.setTitle("Pick a SpellList");
        return new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
    }

    protected static ReferenceChoiceSet<ClassSpellList> buildRCS(
            CDOMReference<ClassSpellList>... refs)
    {
        return new ReferenceChoiceSet<>(
                Arrays.asList(refs));
    }

    @Test
    public void testUnparseNull()
    {
        primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, null);
        assertNull(getToken().unparse(primaryContext, primaryProf));
    }

    @Test
    public void testUnparseSingle()
    {
        ClassSpellList wp1 = construct(primaryContext, "TestWP1");
        PersistentTransitionChoice<CDOMListObject<Spell>> tc = buildChoice(CDOMDirectSingleRef
                .getRef(wp1));
        primaryProf.put(ObjectKey.SPELLLIST_CHOICE, tc);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "1|TestWP1");
    }

    @Test
    public void testUnparseBadCount()
    {
        ClassSpellList wp1 = construct(primaryContext, "TestWP1");
        ReferenceChoiceSet<ClassSpellList> rcs = new ReferenceChoiceSet<>(
                Collections.singletonList(CDOMDirectSingleRef.getRef(wp1)));
        ChoiceSet<ClassSpellList> cs = new ChoiceSet<>(token.getTokenName(), rcs);
        cs.setTitle("Pick a ClassSpellList");
        PersistentTransitionChoice<CDOMListObject<Spell>> tc1 = new ConcretePersistentTransitionChoice<>(
                cs, null);
        primaryProf.put(ObjectKey.SPELLLIST_CHOICE, tc1);
        assertBadUnparse();
    }

    /*
     * TODO Need to figure out who's responsibility this is!
     */
    // @Test
    // public void testUnparseBadList() throws PersistenceLayerException
    // {
    // Language wp1 = construct(primaryContext, "TestWP1");
    // ReferenceChoiceSet<Language> rcs = buildRCS(CDOMDirectSingleRef
    // .getRef(wp1), primaryContext.ref
    // .getCDOMAllReference(getTargetClass()));
    // assertFalse(rcs.getGroupingState().isValid());
    // PersistentTransitionChoice<Language> tc = buildTC(rcs);
    // tc.setChoiceActor(subtoken);
    // primaryProf.put(ObjectKey.CHOOSE_LANGAUTO, tc);
    // assertBadUnparse();
    // }

    @Test
    public void testUnparseMultiple()
    {
        ClassSpellList wp1 = construct(primaryContext, "TestWP1");
        ClassSpellList wp2 = construct(primaryContext, "TestWP2");
        PersistentTransitionChoice<CDOMListObject<Spell>> tc = buildChoice(
                CDOMDirectSingleRef.getRef(wp1), CDOMDirectSingleRef
                        .getRef(wp2));
        primaryProf.put(ObjectKey.SPELLLIST_CHOICE, tc);
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, "1|TestWP1|TestWP2");
    }

    @Test
    public void testUnparseNullInList()
    {
        ClassSpellList wp1 = construct(primaryContext, "TestWP1");
        ReferenceChoiceSet<ClassSpellList> rcs = buildRCS(CDOMDirectSingleRef
                .getRef(wp1), null);
        PersistentTransitionChoice<CDOMListObject<Spell>> tc = buildTC(rcs);
        primaryProf.put(ObjectKey.SPELLLIST_CHOICE, tc);
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (NullPointerException e)
        {
            // Yep!
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUnparseGenericsFail()
    {
        ObjectKey objectKey = ObjectKey.SPELLLIST_CHOICE;
        primaryProf.put(objectKey, new Object());
        try
        {
            getToken().unparse(primaryContext, primaryProf);
            fail();
        } catch (ClassCastException e)
        {
            // Yep!
        }
    }
}
