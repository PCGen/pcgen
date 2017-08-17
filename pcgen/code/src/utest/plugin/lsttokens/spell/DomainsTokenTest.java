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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.Constants;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.ConsolidationRule.AppendingConsolidation;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreRaceWriter;

public class DomainsTokenTest extends AbstractCDOMTokenTestCase<Spell>
{

	static DomainsToken token = new DomainsToken();
	static CDOMTokenLoader<Spell> loader =
			new CDOMTokenLoader<>();

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	@Override
	@Before
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
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputClassOnly() throws PersistenceLayerException
	{
		assertFalse(parse("Fire"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLevelOnly() throws PersistenceLayerException
	{
		assertFalse(parse("3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputChainClassOnly()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=3|Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleEquals() throws PersistenceLayerException
	{
		assertFalse(parse("Fire==4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadLevel() throws PersistenceLayerException
	{
		assertFalse(parse("Fire=Good"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeLevel()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=-4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingBar() throws PersistenceLayerException
	{
		assertFalse(parse("|Fire=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingBar() throws PersistenceLayerException
	{
		assertFalse(parse("Fire=4|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Fire=3||Good=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleComma() throws PersistenceLayerException
	{
		assertFalse(parse("Fire,,Good=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputLeadingComma() throws PersistenceLayerException
	{
		assertFalse(parse(",Fire=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingEquals()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=4="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleSet() throws PersistenceLayerException
	{
		assertFalse(parse("Fire=4=3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTrailingComma()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire,=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyType() throws PersistenceLayerException
	{
		assertFalse(parse("TYPE.=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptyPrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=4[]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOpenEndedPrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=4[PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativePrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=-1[PRERACE:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativePre() throws PersistenceLayerException
	{
		assertFalse(parse("Fire=-1[PRERACE:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputBadPrerequisite()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fire=4[PREFOO:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotClass() throws PersistenceLayerException
	{
		assertTrue(parse("Fire=4"));
		assertConstructionError();
	}

	@Test
	public void testInvalidInputNotClassCompound()
		throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		// Intentionally do NOT build Good
		assertTrue(parse("Fire,Good=4"));
		assertConstructionError();
	}

	@Test
	public void testValidInputClearAll() throws PersistenceLayerException
	{
		assertTrue(parse(Constants.LST_DOT_CLEAR_ALL));
		assertCleanConstruction();
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("Fire=4");
	}

	@Test
	public void testRoundRobinPrereq() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("Fire=4[PRERACE:1,Human]");
	}

	@Test
	public void testRoundRobinComma() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		runRoundRobin("Fire,Good=4");
	}

	@Test
	public void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Good");
		runRoundRobin("Fire=3|Good=4");
	}

	@Test
	public void testRoundRobinCommaPipe() throws PersistenceLayerException
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
	public void testInvalidInputAllPlus() throws PersistenceLayerException
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
	public void testInvalidInputPlusAll() throws PersistenceLayerException
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
	public void testRoundRobinAll() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(DomainSpellList.class, "Fire");
		runRoundRobin("ALL=3");
	}

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		String[] unparsed;
		assertTrue(parse("Fire=-1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull("Expected item to be null", unparsed);
		assertTrue(parse("Fire=1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "Fire=1", unparsed[0]);
		assertTrue(parse("Fire=-1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull("Expected item to be null", unparsed);
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
	public void testClearPrereqInvalid() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		secondaryContext.getReferenceContext().constructCDOMObject(DomainSpellList.class, "Fire");
		assertFalse(parse("Fire=-1[PRERACE:1,Human]"));
	}

}
