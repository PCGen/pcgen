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
class UnencumberedmoveLstTest extends AbstractGlobalTokenTestCase
{

	static CDOMPrimaryToken<CDOMObject> token = new UnencumberedmoveLst();
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
	void testInvalidInputPipeOnly()
	{
		assertFalse(parse("|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputRandomString()
	{
		assertFalse(parse("String"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputEndPipe()
	{
		assertFalse(parse("HeavyLoad|"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputStartPipe()
	{
		assertFalse(parse("|HeavyLoad"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoublePipe()
	{
		assertFalse(parse("HeavyLoad||HeavyArmor"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleLoad()
	{
		assertFalse(parse("HeavyLoad|MediumLoad"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleLoad2()
	{
		assertFalse(parse("MediumLoad|HeavyLoad"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleLoad3()
	{
		assertFalse(parse("HeavyLoad|Overload"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleLoad4()
	{
		assertFalse(parse("HeavyLoad|LightLoad"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleArmor()
	{
		assertFalse(parse("MediumArmor|HeavyArmor"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleArmor2()
	{
		assertFalse(parse("HeavyArmor|MediumArmor"));
		assertNoSideEffects();
	}

	@Test
	void testInvalidInputDoubleArmor3()
	{
		assertFalse(parse("MediumArmor|LightArmor"));
		assertNoSideEffects();
	}

	@Test
	void testValidInputDoubleArmor()
	{
		assertTrue(parse("LightArmor"));
	}

	@Test
	void testRoundRobinLightLoad() throws PersistenceLayerException
	{
		runRoundRobin("LightLoad");
	}

	@Test
	void testRoundRobinLightLight() throws PersistenceLayerException
	{
		runRoundRobin("LightLoad|LightArmor");
	}

	@Test
	void testRoundRobinMediumLight() throws PersistenceLayerException
	{
		runRoundRobin("MediumLoad|LightArmor");
	}

	@Test
	void testRoundRobinLightMedium() throws PersistenceLayerException
	{
		runRoundRobin("LightLoad|MediumArmor");
	}

	@Test
	void testRoundRobinArmor() throws PersistenceLayerException
	{
		runRoundRobin("HeavyArmor");
	}

	@Test
	void testRoundRobinMediumLoad() throws PersistenceLayerException
	{
		runRoundRobin("MediumLoad");
	}

	@Test
	void testRoundRobinOverload() throws PersistenceLayerException
	{
		runRoundRobin("Overload");
	}

	@Test
	void testRoundRobinHeavyLoad() throws PersistenceLayerException
	{
		runRoundRobin("HeavyLoad");
	}

	@Test
	void testRoundRobinLoadArmor() throws PersistenceLayerException
	{
		runRoundRobin("HeavyLoad|MediumArmor");
	}

	@Override
	protected String getLegalValue()
	{
		return "HeavyLoad|MediumArmor";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "MediumLoad";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

}
