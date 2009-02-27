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
package plugin.lsttokens.auto;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AutoLst;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.pretokens.parser.PreClassParser;
import plugin.pretokens.parser.PreRaceParser;
import plugin.pretokens.writer.PreClassWriter;
import plugin.pretokens.writer.PreRaceWriter;

public class EquipTokenTest extends
		AbstractAddTokenTestCase<CDOMObject, Equipment>
{

	PreClassParser preclass = new PreClassParser();
	PreClassWriter preclasswriter = new PreClassWriter();
	PreRaceParser prerace = new PreRaceParser();
	PreRaceWriter preracewriter = new PreRaceWriter();

	static AutoLst token = new AutoLst();
	static EquipToken subtoken = new EquipToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		TokenRegistration.register(preclass);
		TokenRegistration.register(preclasswriter);
		TokenRegistration.register(prerace);
		TokenRegistration.register(preracewriter);
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<Equipment> getTargetClass()
	{
		return Equipment.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean allowsParenAsSub()
	{
		return false;
	}

	@Override
	public boolean allowsFormula()
	{
		return false;
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "%LIST");
	}

	@Test
	public void testInvalidEmptyPre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1[]"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyPre2() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1["));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidEmptyPre3() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		boolean parse = parse(getSubTokenName() + '|' + "TestWP1]");
		if (parse)
		{
			assertFalse(primaryContext.ref.validate(null));
		}
		else
		{
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidMismatchedBracket() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1[PRERACE:Dwarf"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidTrailingAfterBracket()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1[PRERACE:Dwarf]Hi"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinOnePre() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1[PRERACE:1,Dwarf]");
	}

	@Test
	public void testRoundRobinListPre() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "%LIST[PRERACE:1,Dwarf]");
	}

	@Test
	public void testRoundRobinDupeTwoPrereqs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1[PRERACE:1,Dwarf]",
				getSubTokenName() + '|' + "TestWP1[PRERACE:1,Human]");
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		runRoundRobin(getSubTokenName() + '|' + "TestWP1|TestWP1");
	}

	@Test
	public void testInvalidInputBadPrerequisite()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(secondaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "TestWP1[PREFOO:1,Human]"));
		assertNoSideEffects();
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return new ConsolidationRule()
		{

			public String[] getAnswer(String... strings)
			{
				return new String[] { "EQUIP|TestWP1|TestWP1|TestWP2|TestWP2|TestWP3" };
			}
		};
	}
}
