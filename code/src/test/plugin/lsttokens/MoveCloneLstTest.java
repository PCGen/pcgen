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
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class MoveCloneLstTest extends AbstractGlobalTokenTestCase
{
	static CDOMPrimaryToken<CDOMObject> token = new MovecloneLst();
	static CDOMTokenLoader<PCTemplate> loader = new CDOMTokenLoader<>();

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
	void testInvalidInputEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOneItem()
	{
		assertFalse(parse("Walk"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoSecondValue()
	{
		assertFalse(parse("Walk,"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoValue()
	{
		assertFalse(parse("Walk,Fly,"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOnlyValue()
	{
		assertFalse(parse(",30"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputMissingSecondValue()
	{
		assertFalse(parse("Walk,,*2"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputThreeComma()
	{
		assertFalse(parse("Walk,0,Fly,30"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNoBase()
	{
		assertFalse(parse(",Fly,30"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputOutOfOrder()
	{
		try
		{
			assertFalse(parse("Walk,30,Fly"));
		}
		catch (NumberFormatException nfe)
		{
			// This is okay too
		}
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeMultiply()
	{
		assertFalse(parse("Walk,Fly,*-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNegativeDivide()
	{
		assertFalse(parse("Walk,Fly,/-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputZeroDivide()
	{
		assertFalse(parse("Walk,Fly,/0"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputMixedSigns()
	{
		assertFalse(parse("Walk,Fly,+-3"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidMultiple()
	{
		assertFalse(parse("Walk,Fly,*3,Walk,Crawl,/2"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputNaNMovement()
	{
		try
		{
			assertFalse(parse("Walk,Fly,Foo"));
		}
		catch (NumberFormatException nfe)
		{
			// This is okay too
		}
		assertNoSideEffects();
	}

	@Test
	void testValidPositive()
	{
		assertTrue(parse("Walk,Fly,30"));
	}

	@Test
	void testValidZero()
	{
		assertTrue(parse("Walk,Fly,0"));
	}

	@Test
	void testValidPlusZero()
	{
		assertTrue(parse("Walk,Fly,+0"));
	}

	@Test
	void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,30");
	}

	@Test
	void testRoundRobinDupe() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,30", "Walk,Fly,30");
	}

	@Test
	void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,*2");
	}

	@Test
	void testRoundRobinMultiplyDecimal() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,*2.5");
	}

	@Test
	void testRoundRobinDivide() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,/4");
	}

	@Test
	void testRoundRobinSubtract() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,-20");
	}

	@Override
	protected String getLegalValue()
	{
		return "Walk,Fly,-20";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Walk,Fly,/4";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.SEPARATE;
	}
}
