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
package plugin.lsttokens.template;

import org.junit.Test;

import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractListTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class FavoredClassTokenTest extends
		AbstractListTokenTestCase<PCTemplate, PCClass>
{

	static FavoredclassToken token = new FavoredclassToken();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<PCTemplate>(
			PCTemplate.class);

	@Override
	public Class<PCTemplate> getCDOMClass()
	{
		return PCTemplate.class;
	}

	@Override
	public CDOMLoader<PCTemplate> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCTemplate> getToken()
	{
		return token;
	}

	@Override
	public Class<PCClass> getTargetClass()
	{
		return PCClass.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return false;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return false;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void testInvalidInputSubClassNoSub()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1."));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSubClassNoClass()
			throws PersistenceLayerException
	{
		assertFalse(parse(".TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSubDoubleSeparator()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse("TestWP1..Two"));
		assertNoSideEffects();
	}

	@Test
	public void testCategorizationFail() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertTrue(parse("TestWP1.Two"));
		SubClass obj = primaryContext.ref.constructCDOMObject(
				SubClass.class, "Two");
		SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
		primaryContext.ref.reassociateCategory(cat, obj);
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testCategorizationPass() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertTrue(parse("TestWP1.Two"));
		SubClass obj = primaryContext.ref.constructCDOMObject(
				SubClass.class, "Two");
		SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
		primaryContext.ref.reassociateCategory(cat, obj);
		obj = primaryContext.ref.constructCDOMObject(SubClass.class, "Two");
		cat = SubClassCategory.getConstant("TestWP1");
		primaryContext.ref.reassociateCategory(cat, obj);
		assertTrue(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinThreeSub() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		SubClass obj = primaryContext.ref.constructCDOMObject(
				SubClass.class, "Sub");
		SubClassCategory cat = SubClassCategory.getConstant("TestWP2");
		primaryContext.ref.reassociateCategory(cat, obj);
		obj = secondaryContext.ref.constructCDOMObject(SubClass.class,
				"Sub");
		secondaryContext.ref.reassociateCategory(cat, obj);
		runRoundRobin("TestWP1" + getJoinCharacter() + "TestWP2.Sub"
				+ getJoinCharacter() + "TestWP3");
	}

	// TODO This really need to check the object is also not modified, not just
	// that the graph is empty (same with other tests here)
	@Test
	public void testInvalidInputAnyItem() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("ANY" + getJoinCharacter() + "TestWP1"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputItemAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			construct(primaryContext, "TestWP1");
			assertFalse(parse("TestWP1" + getJoinCharacter() + "ANY"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testRoundRobinList() throws PersistenceLayerException
	{
		runRoundRobin("%LIST");
	}

	@Override
	public boolean allowDups()
	{
		return false;
	}
}
