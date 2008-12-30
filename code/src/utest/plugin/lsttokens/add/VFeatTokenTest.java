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

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class VFeatTokenTest extends
		AbstractAddTokenTestCase<CDOMObject, Ability>
{

	static AddLst token = new AddLst();
	static VFeatToken subtoken = new VFeatToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

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
	public Class<Ability> getTargetClass()
	{
		return Ability.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
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
		return true;
	}

	@Override
	protected void construct(LoadContext loadContext, String one)
	{
		Ability obj = loadContext.ref.constructCDOMObject(Ability.class, one);
		loadContext.ref.reassociateCategory(AbilityCategory.FEAT, obj);
	}

	@Override
	public boolean allowsFormula()
	{
		return true;
	}

	@Test
	public void testRoundRobinStacks() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "STACKS,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testRoundRobinStacksValue() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		construct(primaryContext, "TestWP2");
		construct(primaryContext, "TestWP3");
		construct(secondaryContext, "TestWP1");
		construct(secondaryContext, "TestWP2");
		construct(secondaryContext, "TestWP3");
		runRoundRobin(getSubTokenName() + '|' + "STACKS=5,TestWP1"
				+ getJoinCharacter() + "TestWP2" + getJoinCharacter()
				+ "TestWP3");
	}

	@Test
	public void testInvalidInputDoubleStacks() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS,STACKS,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoubleStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=3,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStacksStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS,STACKS=2,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeStack()
			throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=-4,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroStack() throws PersistenceLayerException
	{
		construct(primaryContext, "TestWP1");
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=0,TestWP1"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStacksNaN()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ "STACKS=x,TestWP1" + getJoinCharacter()
				+ "TestWP2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyStacks()
			throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "STACKS=4"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMultTarget() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|'
				+ "TestWP1(Foo,Bar)" + getJoinCharacter()
				+ "TestWP2"));
		assertNoSideEffects();
	}

}
