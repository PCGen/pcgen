/*
 *
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceActor;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ConcretePersistentTransitionChoice;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ClassSkillChoiceActor;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.ObjectMatchingReference;
import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClassSkillsTokenTest extends AbstractAddTokenTestCase<Skill>
{

    static ClassSkillsToken subtoken = new ClassSkillsToken();
    private PCClass fighter;

    @Override
    public Class<PCClass> getCDOMClass()
    {
        return PCClass.class;
    }

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        fighter = primaryContext.getReferenceContext().constructCDOMObject(PCClass.class,
                "Fighter");
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<Skill> getTargetClass()
    {
        return Skill.class;
    }

    @Override
    public boolean isAllLegal()
    {
        return true;
    }

    @Override
    public boolean allowsFormula()
    {
        return true;
    }

    @Test
    public void testRoundRobinTrained() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "TRAINED");
    }

    @Test
    public void testRoundRobinUntrained() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "UNTRAINED");
    }

    @Test
    public void testRoundRobinExclusive() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "EXCLUSIVE");
    }

    @Test
    public void testRoundRobinNonExclusive() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "NONEXCLUSIVE");
    }

    @Test
    public void testRoundRobinAutorank() throws PersistenceLayerException
    {
        construct(primaryContext, "TestWP1");
        construct(secondaryContext, "TestWP1");
        runRoundRobin(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=3");
    }

    @Test
    public void testInvalidInputAutoRankNoRank()
    {
        assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputAutoRankNegativeRank()
    {
        assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=-3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputAutoRankZeroRank()
    {
        assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=0"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputAutoRankDuplicated()
    {
        assertFalse(parse(getSubTokenName() + '|'
                + "NONEXCLUSIVE,AUTORANK=3,AUTORANK=2"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidInputOnlyAutoRank()
    {
        assertFalse(parse(getSubTokenName() + '|' + "AUTORANK=3"));
        assertNoSideEffects();
    }

    @Override
    public String getAllString()
    {
        return "ANY";
    }

    @Override
    protected ChoiceActor<Skill> getActor()
    {
        // TODO This is a hack, to get this to work pre-CDOM
        return new ClassSkillChoiceActor(fighter, null);
    }

    @Test
    public void testUnparseSingleRanked()
    {
        List<CDOMReference<Skill>> refs = new ArrayList<>();
        addSingleRef(refs, "TestWP1");
        ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<>(refs);
        ChoiceSet<Skill> cs = new ChoiceSet<>(
                getSubToken().getTokenName(), rcs);
        PersistentTransitionChoice<Skill> tc = new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
        primaryProf.addToListFor(ListKey.ADD, tc);
        tc.setChoiceActor(new ClassSkillChoiceActor(fighter, 3));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "TestWP1,AUTORANK=3");
    }

    @Test
    public void testUnparseUntrained()
    {
        List<CDOMReference<Skill>> refs = new ArrayList<>();
        ObjectMatchingReference<Skill, Boolean> omr = new ObjectMatchingReference<>(
                "UNTRAINED", getAllRef(), ObjectKey.USE_UNTRAINED,
                Boolean.TRUE);
        omr.returnIncludesNulls(true);
        refs.add(omr);
        ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<>(refs);
        ChoiceSet<Skill> cs = new ChoiceSet<>(
                getSubToken().getTokenName(), rcs);
        PersistentTransitionChoice<Skill> tc = new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
        primaryProf.addToListFor(ListKey.ADD, tc);
        tc.setChoiceActor(new ClassSkillChoiceActor(fighter, null));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "UNTRAINED");
    }

    @Test
    public void testUnparseTrained()
    {
        List<CDOMReference<Skill>> refs = new ArrayList<>();
        ObjectMatchingReference<Skill, Boolean> omr = new ObjectMatchingReference<>(
                "TRAINED", getAllRef(), ObjectKey.USE_UNTRAINED,
                Boolean.FALSE);
        omr.returnIncludesNulls(true);
        refs.add(omr);
        ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<>(refs);
        ChoiceSet<Skill> cs = new ChoiceSet<>(
                getSubToken().getTokenName(), rcs);
        PersistentTransitionChoice<Skill> tc = new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
        primaryProf.addToListFor(ListKey.ADD, tc);
        tc.setChoiceActor(new ClassSkillChoiceActor(fighter, null));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "TRAINED");
    }

    @Test
    public void testUnparseExclusive()
    {
        List<CDOMReference<Skill>> refs = new ArrayList<>();
        ObjectMatchingReference<Skill, Boolean> omr = new ObjectMatchingReference<>(
                "EXCLUSIVE", getAllRef(), ObjectKey.EXCLUSIVE,
                Boolean.TRUE);
        omr.returnIncludesNulls(true);
        refs.add(omr);
        ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<>(refs);
        ChoiceSet<Skill> cs = new ChoiceSet<>(
                getSubToken().getTokenName(), rcs);
        PersistentTransitionChoice<Skill> tc = new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
        primaryProf.addToListFor(ListKey.ADD, tc);
        tc.setChoiceActor(new ClassSkillChoiceActor(fighter, null));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "EXCLUSIVE");
    }

    @Test
    public void testUnparseNonExclusive()
    {
        List<CDOMReference<Skill>> refs = new ArrayList<>();
        ObjectMatchingReference<Skill, Boolean> omr = new ObjectMatchingReference<>(
                "NONEXCLUSIVE", getAllRef(), ObjectKey.EXCLUSIVE,
                Boolean.FALSE);
        omr.returnIncludesNulls(true);
        refs.add(omr);
        ReferenceChoiceSet<Skill> rcs = new ReferenceChoiceSet<>(refs);
        ChoiceSet<Skill> cs = new ChoiceSet<>(
                getSubToken().getTokenName(), rcs);
        PersistentTransitionChoice<Skill> tc = new ConcretePersistentTransitionChoice<>(
                cs, FormulaFactory.ONE);
        primaryProf.addToListFor(ListKey.ADD, tc);
        tc.setChoiceActor(new ClassSkillChoiceActor(fighter, null));
        String[] unparsed = getToken().unparse(primaryContext, primaryProf);
        expectSingle(unparsed, getSubTokenName() + '|' + "NONEXCLUSIVE");
    }

    private CDOMGroupRef<Skill> getAllRef()
    {
        return primaryContext.getReferenceContext().getCDOMAllReference(getTargetClass());
    }
}
