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
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractGlobalTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class MoveCloneLstTest extends AbstractGlobalTokenTestCase
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
	public CDOMPrimaryToken<CDOMObject> getToken()
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
	public void testInvalidInputOneItem() throws PersistenceLayerException
	{
		assertFalse(parse("Walk"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoSecondValue()
		throws PersistenceLayerException
	{
		assertFalse(parse("Walk,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoValue() throws PersistenceLayerException
	{
		assertFalse(parse("Walk,Fly,"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyValue() throws PersistenceLayerException
	{
		assertFalse(parse(",30"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMissingSecondValue()
		throws PersistenceLayerException
	{
		assertFalse(parse("Walk,,*2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputThreeComma() throws PersistenceLayerException
	{
		assertFalse(parse("Walk,0,Fly,30"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoBase() throws PersistenceLayerException
	{
		assertFalse(parse(",Fly,30"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOutOfOrder() throws PersistenceLayerException
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
	public void testInvalidInputNegativeMultiply()
		throws PersistenceLayerException
	{
		assertFalse(parse("Walk,Fly,*-3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeDivide()
		throws PersistenceLayerException
	{
		assertFalse(parse("Walk,Fly,/-3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroDivide() throws PersistenceLayerException
	{
		assertFalse(parse("Walk,Fly,/0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputMixedSigns() throws PersistenceLayerException
	{
		assertFalse(parse("Walk,Fly,+-3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultiple() throws PersistenceLayerException
	{
		assertFalse(parse("Walk,Fly,*3,Walk,Crawl,/2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNaNMovement() throws PersistenceLayerException
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
	public void testValidPositive() throws PersistenceLayerException
	{
		assertTrue(parse("Walk,Fly,30"));
	}

	@Test
	public void testValidZero() throws PersistenceLayerException
	{
		assertTrue(parse("Walk,Fly,0"));
	}

	@Test
	public void testValidPlusZero() throws PersistenceLayerException
	{
		assertTrue(parse("Walk,Fly,+0"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,30");
	}

	@Test
	public void testRoundRobinDupe() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,30", "Walk,Fly,30");
	}

	@Test
	public void testRoundRobinMultiply() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,*2");
	}

	@Test
	public void testRoundRobinMultiplyDecimal() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,*2.5");
	}

	@Test
	public void testRoundRobinDivide() throws PersistenceLayerException
	{
		runRoundRobin("Walk,Fly,/4");
	}

	@Test
	public void testRoundRobinSubtract() throws PersistenceLayerException
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
