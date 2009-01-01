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
package plugin.lsttokens.kit.levelability;

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.kit.KitLevelAbility;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractSubTokenTestCase;

public class LevelAbilityTokenTest extends AbstractSubTokenTestCase<KitLevelAbility>
{

	static LevelAbilityToken token = new LevelAbilityToken();
	static CDOMSubLineLoader<KitLevelAbility> loader = new CDOMSubLineLoader<KitLevelAbility>(
			"*KITTOKEN", "PCClassS", KitLevelAbility.class);

	@Override
	public Class<KitLevelAbility> getCDOMClass()
	{
		return KitLevelAbility.class;
	}

	@Override
	public CDOMSubLineLoader<KitLevelAbility> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMSecondaryToken<KitLevelAbility> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptyPCClass() throws PersistenceLayerException
	{
		assertFalse(parse("=2"));
	}

	@Test
	public void testInvalidInputEmptyCount() throws PersistenceLayerException
	{
		assertFalse(parse("Fireball="));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fireball");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fireball");
		runRoundRobin("Fireball=2");
	}
}
