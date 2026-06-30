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
package plugin.lsttokens.spell;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

import pcgen.cdom.base.Constants;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class DomainsTokenTest extends AbstractCDOMTokenTestCase<Spell>
{

	static DomainsToken token = new DomainsToken();
	static CDOMTokenLoader<Spell> loader =
			new CDOMTokenLoader<>();

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public CDOMLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Spell> getToken()
	{
		return token;
	}

	@Test
	void testInvalidInputEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputClassOnly()
	{
		assertFalse(parse("Fire"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputLevelOnly()
	{
		assertFalse(parse("3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputChainClassOnly()
	{
		assertFalse(parse("Fire=3|Good"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleEquals()
	{
		assertFalse(parse("Fire==4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputBadLevel()
	{
		assertFalse(parse("Fire=Good"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeLevel()
	{
		assertFalse(parse("Fire=-4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputLeadingBar()
	{
		assertFalse(parse("|Fire=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingBar()
	{
		assertFalse(parse("Fire=4|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipe()
	{
		assertFalse(parse("Fire=3||Good=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleComma()
	{
		assertFalse(parse("Fire,,Good=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputLeadingComma()
	{
		assertFalse(parse(",Fire=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingEquals()
	{
		assertFalse(parse("Fire=4="));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleSet()
	{
		assertFalse(parse("Fire=4=3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingComma()
	{
		assertFalse(parse("Fire,=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyType()
	{
		assertFalse(parse("TYPE.=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEmptyPrerequisite()
	{
		assertFalse(parse("Fire=4[]"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOpenEndedPrerequisite()
	{
		assertFalse(parse("Fire=4[PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativePrerequisite()
	{
		assertFalse(parse("Fire=-1[PRERACE:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativePre()
	{
		assertFalse(parse("Fire=-1[PRERACE:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputBadPrerequisite()
	{
		assertFalse(parse("Fire=4[PREFOO:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNotClass()
	{
		assertTrue(parse("Fire=4"));
		assertConstructionError();
	}

	@Test
	void testInvalidInputNotClassCompound()
	{
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		// Intentionally do NOT build Good
		assertTrue(parse("Fire,Good=4"));
		assertConstructionError();
	}

	@Test
	void testValidInputClearAll()
	{
		assertTrue(parse(Constants.LST_DOT_CLEAR_ALL));
		assertCleanConstruction();
	}

	@Test
	void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("Fire=4");
	}

	@Test
	void testRoundRobinPrereq() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("Fire=4[PRERACE:1,Human]");
	}

	@Test
	void testRoundRobinComma() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		runRoundRobin("Fire,Good=4");
	}

	@Test
	void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		runRoundRobin("Fire=3|Good=4");
	}

	@Test
	void testRoundRobinCommaPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Sun");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Sun");
		runRoundRobin("Fire,Good=3|Sun=4");
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputAllPlus()
	{
		try
		{
			assertFalse(parse("Fire,ALL=3"));
		}
		catch (IllegalArgumentException iae)
		{
			// OK as well
		}
		assertNoSideEffects();
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputPlusAll()
	{
		try
		{
			assertFalse(parse("ALL,Fire=4"));
		}
		catch (IllegalArgumentException iae)
		{
			// OK as well
		}
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinAll() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("ALL=3");
	}

	@Test
	void testReplacementInputs()
	{
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		String[] unparsed;
		assertTrue(parse("Fire=-1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull(unparsed);
		assertTrue(parse("Fire=1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Fire=1", unparsed[0]);
		assertTrue(parse("Fire=-1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull(unparsed);
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Sun=5";
	}

	@Override
	protected String getLegalValue()
	{
		return "Fire=3|Good=4";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return new AppendingConsolidation('|');
	}

	@Test
	void testClearPrereqInvalid()
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		assertFalse(parse("Fire=-1[PRERACE:1,Human]"));
	}

}
