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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

	@ParameterizedTest(name = "{0}")
	@CsvSource(delimiter = '|', quoteCharacter = '"', value = {
			"testInvalidEmpty                    | ''",
			"testInvalidListNameOnly             | Familiar",
			"testInvalidListNameBarOnly          | Familiar|",
			"testInvalidEmptyListName            | |Lion",
			"testInvalidTypeRaceBarOnly          | Familiar|Lion|",
			"testInvalidTypeRaceTypeEmpty        | Familiar|RACETYPE=",
			"testInvalidNonSensicalAnyType       | Familiar|ANY,TYPE=Foo",
			"testInvalidRaceCommaStarting        | Familiar|,Lion",
			"testInvalidRaceCommaEnding          | Familiar|Lion,",
			"testInvalidRaceDoubleComma          | Familiar|Lion,,Tiger",
			"testInvalidRacePipe                 | Familiar|Lion|Tiger",
			"testInvalidSpellEmbeddedPre         | Familiar|Lion|PRERACE:1,Human|Tiger",
			"testInvalidNonSensicalAnyLast       | Familiar|Tiger,Any",
			"testInvalidNonSensicalAnyFirst      | Familiar|Any,Lion",
			"testInvalidEmbeddedFA               | Familiar|FOLLOWERADJUSTMENT:-4|Lion",
			"testInvalidMultipleFOLLOWERADJUSTMENT| Familiar|Lion|FOLLOWERADJUSTMENT:-2|FOLLOWERADJUSTMENT:-3",
			"testInvalidOnlyFOLLOWERADJUSTMENTBar | Familiar|FOLLOWERADJUSTMENT:-3|",
			"testInvalidEmptyTimes                | Familiar||Lion",
			"testInvalidBadFA                     | Familiar|Lion|FOLLOWERADJUSTMENT:",
			"testInvalidFANaN                     | Familiar|Lion|FOLLOWERADJUSTMENT:-T",
			"testInvalidFADecimal                 | Familiar|Lion|FOLLOWERADJUSTMENT:-4.5",
	})
	void testInvalidParse(String label, String value)
	{
		assertFalse(parse(value), label + ": expected parse to fail for input <" + value + ">");
		assertNull(getWriteToken().unparse(primaryContext, primaryProf), label + ": no partial state should have been committed");
		assertNoSideEffects();
	}

	@ParameterizedTest(name = "{1}: {0}")
	@CsvSource({
			"'Familiar|TYPE=',         Empty TYPE= payload should fail to parse",
			"'Familiar|TYPE=Foo.',     Trailing dot in TYPE= should fail to parse",
			"'Familiar|TYPE=.Foo',     Leading dot in TYPE= should fail to parse",
			"'Familiar|TYPE=Foo..Bar', Empty inner segment in TYPE= should fail to parse",
	})
	void testInvalidTypeClause(String value, String reason)
	{
		assertFalse(parse(value), reason);
		assertNull(getWriteToken().unparse(primaryContext, primaryProf));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidOnlyFOLLOWERADJUSTMENT()
	{
		boolean parse = parse("Familiar|FOLLOWERADJUSTMENT:-3");
		if (parse)
		{
			assertConstructionError();
		}
		else
		{
			assertNoSideEffects();
		}
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
			}
			else
			{
				assertNoSideEffects();
			}
		}
		catch (IllegalArgumentException iae)
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
	void testRoundRobinType() throws PersistenceLayerException
	{
		construct(CompanionList.class, "Familiar");
		Race primary = primaryContext.getReferenceContext().constructCDOMObject(Race.class, "Lion");
		primary.addToListFor(ListKey.TYPE, Type.getConstant("Animal"));
		Race secondary = secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "Lion");
		secondary.addToListFor(ListKey.TYPE, Type.getConstant("Animal"));
		runRoundRobin("Familiar|TYPE=Animal");
		assertNotNull(getWriteToken().unparse(primaryContext, primaryProf));
	}

	@Test
	void testRoundRobinTypeCompound() throws PersistenceLayerException
	{
		construct(CompanionList.class, "Familiar");
		Race primary = primaryContext.getReferenceContext().constructCDOMObject(Race.class, "Lion");
		primary.addToListFor(ListKey.TYPE, Type.getConstant("Animal"));
		primary.addToListFor(ListKey.TYPE, Type.getConstant("Magical"));
		Race secondary = secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "Lion");
		secondary.addToListFor(ListKey.TYPE, Type.getConstant("Animal"));
		secondary.addToListFor(ListKey.TYPE, Type.getConstant("Magical"));
		runRoundRobin("Familiar|TYPE=Animal.Magical");
		assertNotNull(getWriteToken().unparse(primaryContext, primaryProf));
	}

	@Test
	void testRoundRobinMultipleType() throws PersistenceLayerException
	{
		construct(CompanionList.class, "Familiar");
		Race primaryLion = primaryContext.getReferenceContext().constructCDOMObject(Race.class, "Lion");
		primaryLion.addToListFor(ListKey.TYPE, Type.getConstant("Animal"));
		Race primarySpider = primaryContext.getReferenceContext().constructCDOMObject(Race.class, "Spider");
		primarySpider.addToListFor(ListKey.TYPE, Type.getConstant("Vermin"));
		Race secondaryLion = secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "Lion");
		secondaryLion.addToListFor(ListKey.TYPE, Type.getConstant("Animal"));
		Race secondarySpider = secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "Spider");
		secondarySpider.addToListFor(ListKey.TYPE, Type.getConstant("Vermin"));
		runRoundRobin("Familiar|TYPE=Animal,TYPE=Vermin");
		assertNotNull(getWriteToken().unparse(primaryContext, primaryProf));
	}

	@Test
	void testRoundRobinMixedClauses() throws PersistenceLayerException
	{
		construct(CompanionList.class, "Familiar");
		construct(Race.class, "Cat");
		Race primary = primaryContext.getReferenceContext().constructCDOMObject(Race.class, "MyCompanionRace");
		primary.addToListFor(ListKey.TYPE, Type.getConstant("MyCompanion"));
		Race secondary = secondaryContext.getReferenceContext().constructCDOMObject(Race.class, "MyCompanionRace");
		secondary.addToListFor(ListKey.TYPE, Type.getConstant("MyCompanion"));
		runRoundRobin("Familiar|Cat,TYPE=MyCompanion,RACESUBTYPE=Fire,RACETYPE=Animal|FOLLOWERADJUSTMENT:-3");
		assertNotNull(getWriteToken().unparse(primaryContext, primaryProf));
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
