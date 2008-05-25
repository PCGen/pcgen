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

public class NameoptTokenTest extends AbstractTokenTestCase<EquipmentModifier>
{
	static NameoptToken token = new NameoptToken();
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
	public void testBadInputNegative() throws PersistenceLayerException
	{
		try
		{
			boolean parse = parse("INVALID");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	public void testBadInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	public void testBadInputPlainText() throws PersistenceLayerException
	{
		assertFalse(parse("TEXT"));
		assertNoSideEffects();
	}

	@Test
	public void testBadInputEmptyText() throws PersistenceLayerException
	{
		assertFalse(parse("TEXT="));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinNormal() throws PersistenceLayerException
	{
		runRoundRobin("NORMAL");
	}

	@Test
	public void testRoundRobinNoList() throws PersistenceLayerException
	{
		runRoundRobin("NOLIST");
	}

	@Test
	public void testRoundRobinNoName() throws PersistenceLayerException
	{
		runRoundRobin("NONAME");
	}

	@Test
	public void testRoundRobinNothing() throws PersistenceLayerException
	{
		runRoundRobin("NOTHING");
	}

	@Test
	public void testRoundRobinSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL");
	}

	@Test
	public void testRoundRobinText() throws PersistenceLayerException
	{
		runRoundRobin("TEXT=This is the text");
	}

}
