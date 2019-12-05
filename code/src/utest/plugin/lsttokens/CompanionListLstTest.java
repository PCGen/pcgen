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
package plugin.lsttokens;


import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.list.CompanionList;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CompanionListLstTest extends AbstractGlobalTokenTestCase
{

    static CDOMPrimaryToken<CDOMObject> token = new CompanionListLst();
    static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

    PreClassParser preclass = new PreClassParser();
    PreClassWriter preclasswriter = new PreClassWriter();
    PreRaceParser prerace = new PreRaceParser();
    PreRaceWriter preracewriter = new PreRaceWriter();

    @Override
    @BeforeEach
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(preclass);
        TokenRegistration.register(preclasswriter);
        TokenRegistration.register(prerace);
        TokenRegistration.register(preracewriter);
    }

    @Override
    public CDOMLoader<PCTemplate> getLoader()
    {
        return loader;
    }

    @Override
    public Class<PCTemplate> getCDOMClass()
    {
        return PCTemplate.class;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getReadToken()
    {
        return token;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getWriteToken()
    {
        return token;
    }

    @Test
    public void testInvalidEmpty()
    {
        assertFalse(parse(""));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListNameOnly()
    {
        assertFalse(parse("Familiar"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidListNameBarOnly()
    {
        assertFalse(parse("Familiar|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyListName()
    {
        assertFalse(parse("|Lion"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeRaceBarOnly()
    {
        assertFalse(parse("Familiar|Lion|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidTypeRaceTypeEmpty()
    {
        assertFalse(parse("Familiar|RACETYPE="));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRaceCommaStarting()
    {
        assertFalse(parse("Familiar|,Lion"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRaceCommaEnding()
    {
        assertFalse(parse("Familiar|Lion,"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRaceDoubleComma()
    {
        assertFalse(parse("Familiar|Lion,,Tiger"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidRacePipe()
    {
        assertFalse(parse("Familiar|Lion|Tiger"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidSpellEmbeddedPre()
    {
        assertFalse(parse("Familiar|Lion|PRERACE:1,Human|Tiger"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNonSensicalAnyLast()
    {
        assertFalse(parse("Familiar|Tiger,Any"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidNonSensicalAnyFirst()
    {
        assertFalse(parse("Familiar|Any,Lion"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmbeddedFA()
    {
        assertFalse(parse("Familiar|FOLLOWERADJUSTMENT:-4|Lion"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyFOLLOWERADJUSTMENT()
    {
        boolean parse = parse("Familiar|FOLLOWERADJUSTMENT:-3");
        if (parse)
        {
            assertConstructionError();
        } else
        {
            assertNoSideEffects();
        }
    }

    @Test
    public void testInvalidMultipleFOLLOWERADJUSTMENT()
    {
        assertFalse(parse("Familiar|Lion|FOLLOWERADJUSTMENT:-2|FOLLOWERADJUSTMENT:-3"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyFOLLOWERADJUSTMENTBar()
    {
        assertFalse(parse("Familiar|FOLLOWERADJUSTMENT:-3|"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidEmptyTimes()
    {
        assertFalse(parse("Familiar||Lion"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidBadFA()
    {
        assertFalse(parse("Familiar|Lion|FOLLOWERADJUSTMENT:"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidFANaN()
    {
        assertFalse(parse("Familiar|Lion|FOLLOWERADJUSTMENT:-T"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidFADecimal()
    {
        assertFalse(parse("Familiar|Lion|FOLLOWERADJUSTMENT:-4.5"));
        assertNoSideEffects();
    }

    @Test
    public void testInvalidOnlyPre()
    {
        try
        {
            boolean parse = parse("Familiar|FOLLOWERADJUSTMENT:-3|PRERACE:1,Human");
            if (parse)
            {
                assertConstructionError();
            } else
            {
                assertNoSideEffects();
            }
        } catch (IllegalArgumentException iae)
        {
            assertNoSideEffects();
            // This is ok too
        }
    }

    @Test
    public void testRoundRobinJustRace() throws PersistenceLayerException
    {
        construct(Race.class, "Lion");
        construct(CompanionList.class, "Familiar");
        runRoundRobin("Familiar|Lion");
    }

    private <T extends Loadable> void construct(Class<T> cl, String name)
    {
        primaryContext.getReferenceContext().constructCDOMObject(cl, name);
        secondaryContext.getReferenceContext().constructCDOMObject(cl, name);
    }

    @Test
    public void testRoundRobinTwoRace() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Familiar|Lion,Tiger");
    }

    @Test
    public void testRoundRobinAnyRace() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Familiar|ANY");
    }

    @Test
    public void testRoundRobinTwoWithRacetype() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Familiar|Lion,RACETYPE=Clawed");
    }

    @Test
    public void testRoundRobinFA() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Lion");
        runRoundRobin("Familiar|Lion|FOLLOWERADJUSTMENT:-4");
    }

    @Test
    public void testRoundRobinThreeFA() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Bear");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Familiar|Bear|FOLLOWERADJUSTMENT:-6",
                "Familiar|Lion|FOLLOWERADJUSTMENT:-4",
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-5");
    }

    @Test
    public void testRoundRobinTwoType() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(CompanionList.class, "Companion");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Companion|Lion|FOLLOWERADJUSTMENT:-5",
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-5");
    }

    @Test
    public void testRoundRobinComplex() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Familiar|Lion,Tiger|FOLLOWERADJUSTMENT:-3|!PRECLASS:1,Cleric=1|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinTwoPRE() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Lion");
        construct(Race.class, "Tiger");
        runRoundRobin("Familiar|Lion|FOLLOWERADJUSTMENT:-5",
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-5|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinDupePre() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Tiger");
        runRoundRobin(
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-5|PRECLASS:1,Cleric=1",
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-5|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinDupePreDiffFA() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Familiar");
        construct(Race.class, "Tiger");
        runRoundRobin(
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-3|PRECLASS:1,Cleric=1",
                "Familiar|Tiger|FOLLOWERADJUSTMENT:-5|PRERACE:1,Human");
    }

    @Test
    public void testRoundRobinReal() throws PersistenceLayerException
    {
        construct(CompanionList.class, "Psicrystal");
        construct(Race.class, "Psicrystal (Single Minded)");
        construct(Race.class, "Psicrystal (Resolved)");
        construct(Race.class, "Psicrystal (Bully)");
        construct(Race.class, "Psicrystal (Artiste)");
        construct(Race.class, "Psicrystal (Liar)");
        construct(Race.class, "Psicrystal (Poised)");
        construct(Race.class, "Psicrystal (Sage)");
        construct(Race.class, "Psicrystal (Meticulous)");
        construct(Race.class, "Psicrystal (Sneaky)");
        construct(Race.class, "Psicrystal (Sympathetic)");
        construct(Race.class, "Psicrystal (Hero)");
        construct(Race.class, "Psicrystal (Friendly)");
        construct(Race.class, "Psicrystal (Coward)");
        construct(Race.class, "Psicrystal (Nimble)");
        construct(Race.class, "Psicrystal (Observant)");
        runRoundRobin(
                "Psicrystal|Psicrystal (Artiste),Psicrystal (Bully),Psicrystal (Coward),Psicrystal (Friendly),"
                        + "Psicrystal (Hero),Psicrystal (Liar),Psicrystal (Meticulous),Psicrystal (Nimble),Psicrystal (Observant),"
                        + "Psicrystal (Poised),Psicrystal (Resolved),Psicrystal (Sage),Psicrystal (Single Minded),"
                        + "Psicrystal (Sneaky),Psicrystal (Sympathetic)");
    }

    @Override
    protected String getLegalValue()
    {
        return "Familiar|Tiger|FOLLOWERADJUSTMENT:-3|PRECLASS:1,Cleric=1";
    }

    @Override
    protected String getAlternateLegalValue()
    {
        return "Familiar|Tiger|FOLLOWERADJUSTMENT:-5|PRERACE:1,Human";
    }

    @Override
    protected ConsolidationRule getConsolidationRule()
    {
        return ConsolidationRule.SEPARATE;
    }

//	private void buildCompanionMod(String type)
//	{
//		String mod = "isAMod";
//		ReferenceContext ref1 = primaryContext.ref;
//		ReferenceContext ref2 = secondaryContext.ref;
//		CompanionList cl1 = ref1.silentlyGetConstructedCDOMObject(CompanionList.class, type);
//		CompanionList cl2 = ref2.silentlyGetConstructedCDOMObject(CompanionList.class, type);
//		CompanionMod cm1 = ref1.constructCDOMObject(CompanionMod.class, mod);
//		CompanionMod cm2 = ref2.constructCDOMObject(CompanionMod.class, mod);
//		ref1.reassociateCategory(cl1, cm1);
//		ref1.reassociateCategory(cl2, cm2);
//	}
}
