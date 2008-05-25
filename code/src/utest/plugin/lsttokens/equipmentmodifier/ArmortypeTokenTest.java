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
package plugin.lsttokens.equipmentmodifier;

import org.junit.Test;

import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class ArmortypeTokenTest extends
		AbstractTokenTestCase<EquipmentModifier>
{

	static ArmortypeToken token = new ArmortypeToken();
	static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<EquipmentModifier>(
			EquipmentModifier.class);

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public CDOMLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	// TODO After format change
	// @Test
	// public void testInvalidInputNoResult() throws PersistenceLayerException
	// {
	// assertFalse(parse("Medium"));
	// assertNoSideEffects();
	// }

	@Test
	public void testInvalidInputEmptyResult() throws PersistenceLayerException
	{
		assertFalse(parse("Medium|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputEmptySource() throws PersistenceLayerException
	{
		assertFalse(parse("|Medium"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputDoublePipe() throws PersistenceLayerException
	{
		assertFalse(parse("Light||Medium"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoPipe() throws PersistenceLayerException
	{
		assertFalse(parse("Light|Medium|Heavy"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinLightRemoveOldSyntax() throws PersistenceLayerException
	{
		runRoundRobin("Light");
	}

	@Test
	public void testRoundRobinLightMedium() throws PersistenceLayerException
	{
		runRoundRobin("Light|Medium");
	}

	@Test
	public void testRoundRobinMediumLight() throws PersistenceLayerException
	{
		runRoundRobin("Medium|Light");
	}
}
