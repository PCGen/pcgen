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

import org.junit.Test;

import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class SkillListTokenTest extends AbstractTokenTestCase<PCClass>
{
	static SkilllistToken token = new SkilllistToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);

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
	public void testInvalidInputString() throws PersistenceLayerException
	{
		assertFalse(parse("String"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNaN() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("NaN|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoObject() throws PersistenceLayerException
	{
		assertFalse(parse("1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNoCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidDoublePipe() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputType() throws PersistenceLayerException
	{
		try
		{
			assertFalse(parse("1|TYPE=Test"));
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputUnbuilt() throws PersistenceLayerException
	{
		assertTrue(parse("1|String"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testInvalidInputNoCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCountNaN() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("x|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputJoinedDot() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("1|TestWP1.TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1|ALL|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1|TestWP1|ALL"));
		assertNoSideEffects();
	}

	@Test
	public void testInputInvalidAddsAllNoSideEffect()
			throws PersistenceLayerException
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
	public void testInvalidListEnd() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("1|TestWP1|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidZeroCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("0|TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidNegativeCount() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("-1|TestWP1|TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidListDoubleJoin() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertFalse(parse("1|TestWP2||TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputCheckMult() throws PersistenceLayerException
	{
		// Explicitly do NOT build TestWP2
		construct(primaryContext, "TestWP1");
		assertTrue(parse("1|TestWP1|TestWP2"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		assertTrue(parse("1|TestWP1"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse("1|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
		assertTrue(primaryContext.ref.validate());
		assertTrue(parse("2|TestWP1|TestWP2"));
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		runRoundRobin("1|TestWP1");
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
		loadContext.ref.constructCDOMObject(ClassSkillList.class, one);
	}

}
