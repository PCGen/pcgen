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
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.reference.CDOMGroupRef;
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
import plugin.pretokens.parser.PreSubClassParser;
import plugin.pretokens.writer.PreRaceWriter;
import plugin.pretokens.writer.PreSubClassWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClassesTokenTest extends AbstractCDOMTokenTestCase<Spell>
{

	static ClassesToken token = new ClassesToken();
	static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	PreSubClassParser presubclass = new PreSubClassParser();
	PreSubClassWriter presubclasswriter = new PreSubClassWriter();

	@Override
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
		TokenRegistration.register(presubclass);
		TokenRegistration.register(presubclasswriter);
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
		assertFalse(parse("Wizard"));
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
		assertFalse(parse("Wizard=3|Sorcerer"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleEquals()
	{
		assertFalse(parse("Wizard==4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputBadLevel()
	{
		assertFalse(parse("Wizard=Sorcerer"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeLevel()
	{
		assertFalse(parse("Wizard=-4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputLeadingBar()
	{
		assertFalse(parse("|Wizard=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingBar()
	{
		assertFalse(parse("Wizard=4|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipe()
	{
		assertFalse(parse("Wizard=3||Sorcerer=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleComma()
	{
		assertFalse(parse("Wizard,,Sorcerer=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputLeadingComma()
	{
		assertFalse(parse(",Wizard=4"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingEquals()
	{
		assertFalse(parse("Wizard=4="));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleSet()
	{
		assertFalse(parse("Wizard=4=3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputTrailingComma()
	{
		assertFalse(parse("Wizard,=4"));
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
		assertFalse(parse("Wizard=4[]"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOpenEndedPrerequisite()
	{
		assertFalse(parse("Wizard=4[PRERACE:1,Human"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativePrerequisite()
	{
		assertFalse(parse("Wizard=-1[PRERACE:1,Human]"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativePre()
	{
		assertFalse(parse("Wizard=-1[PRERACE:1,Human]"));
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
		assertTrue(parse("Wizard=4"));
		assertConstructionError();
	}

	@Test
	void testInvalidInputNotClassCompound()
	{
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		assertTrue(parse("Wizard,Sorcerer=4"));
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
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		runRoundRobin("Wizard=4");
	}

	@Test
	void testRoundRobinPrereq() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		runRoundRobin("Wizard=4[PRERACE:1,Human]");
	}

	@Test
	void testRoundRobinType() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		ClassSpellList classSpellList =
				secondaryContext.getReferenceContext().constructCDOMObject(
					ClassSpellList.class, "Psion");
		classSpellList.addType(Type.getConstant("Psionic"));
		CDOMGroupRef<ClassSpellList> typeReference =
				primaryContext.getReferenceContext()
					.getManufacturer(ClassSpellList.class)
					.getTypeReference("Psionic");
		typeReference.addResolution(classSpellList);
		runRoundRobin("TYPE=Psionic=1[PRERACE:1,Human]");
	}

	@Test
	void testRoundRobinComma() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		runRoundRobin("Sorcerer,Wizard=4");
	}

	@Test
	void testRoundRobinPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		runRoundRobin("Wizard=3|Sorcerer=4");
	}

	@Test
	void testRoundRobinCommaPipe() throws PersistenceLayerException
	{
		assertEquals(0, primaryContext.getWriteMessageCount());
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Sorcerer");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Bard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class,
				"Sorcerer");
		secondaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Bard");
		runRoundRobin("Sorcerer,Wizard=3|Bard=4");
	}

	// @Test(expected = IllegalArgumentException.class)
	public void testInvalidInputAllPlus()
	{
		try
		{
			assertFalse(parse("Wizard,ALL=3"));
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
			assertFalse(parse("ALL,Wizard=4"));
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
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		runRoundRobin("ALL=3");
	}

	@Test
	void testInvalidRoundRobinMixedPrereqs()
	{
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Cleric");
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Priest");
		secondaryContext.getReferenceContext()
			.constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
			.constructCDOMObject(ClassSpellList.class, "Cleric");
		secondaryContext.getReferenceContext()
			.constructCDOMObject(ClassSpellList.class, "Priest");
		assertFalse(
			parse("Sorcerer=5|Wizard=5|Cleric=4[PRESUBCLASS:1,Sarish]|Priest=4[PRESUBCLASS:1,Priest of Sarish]"));
	}

	@Test
	void testReplacementInputs()
	{
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		String[] unparsed;
		assertTrue(parse("Wizard=-1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull(unparsed);
		assertTrue(parse("Wizard=1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Wizard=1", unparsed[0]);
		assertTrue(parse("Wizard=-1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertNull(unparsed);
	}

	@Test
	void testReplacementTypeDot()
	{
		String[] unparsed;
		assertTrue(parse("TYPE.Arcane=1"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("TYPE=Arcane=1", unparsed[0]);
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Sorcerer=6";
	}

	@Override
	protected String getLegalValue()
	{
		return "Abjurer=3|Bard=4";
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
		primaryContext.getReferenceContext().constructCDOMObject(ClassSpellList.class, "Wizard");
		secondaryContext.getReferenceContext()
				.constructCDOMObject(ClassSpellList.class, "Wizard");
		assertFalse(parse("Wizard=-1[PRERACE:1,Human]"));
	}

}
