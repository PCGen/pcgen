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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Domain;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class RegionLstTest extends AbstractGlobalTokenTestCase
{
	static RegionLst token = new RegionLst();
	static CDOMTokenLoader<Domain> loader = new CDOMTokenLoader<>();

	@Override
	public Class<Domain> getCDOMClass()
	{
		return Domain.class;
	}

	@Override
	public CDOMLoader<Domain> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputOnlyNumber()
	{
		assertFalse(parse("1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNaN()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("NaN|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoObject()
	{
		assertFalse(parse("1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoCount()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("|TestWP1"));
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
	public void testInvalidInputNoCount()
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListEnd()
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1|TestWP1|"));
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
	public void testValidInputs()
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("1|TestWP1"));
		assertCleanConstruction();
		assertTrue(parse("1|TestWP1|TestWP2"));
		assertCleanConstruction();
		assertTrue(parse("2|TestWP1|TestWP2"));
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
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("TestWP1|TestWP2");
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
		runRoundRobin("2|TestWP1|TestWP2|TestWP3");
	}

	protected void construct(LoadContext loadContext, String one)
	{
		loadContext.getReferenceContext().constructCDOMObject(ClassSkillList.class, one);
	}

	@Override
	protected String getLegalValue()
	{
		return "2|TestWP1|TestWP2";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "TestWP1|TestWP3";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
