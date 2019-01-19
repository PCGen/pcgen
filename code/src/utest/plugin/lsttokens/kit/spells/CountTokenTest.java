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
package plugin.lsttokens.kit.spells;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.BeforeEach;

import pcgen.core.kit.KitSpells;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class CountTokenTest extends AbstractKitTokenTestCase<KitSpells>
{

	static CountToken token = new CountToken();
	static CDOMSubLineLoader<KitSpells> loader = new CDOMSubLineLoader<>(
			"SPELLS", KitSpells.class);

	@Override
	public Class<KitSpells> getCDOMClass()
	{
		return KitSpells.class;
	}

	@Override
	public CDOMSubLineLoader<KitSpells> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitSpells> getToken()
	{
		return token;
	}

	@Test
	public void testRoundRobinNumber() throws PersistenceLayerException
	{
		runRoundRobin("3");
	}

	@Test
	public void testRoundRobinFormula() throws PersistenceLayerException
	{
		runRoundRobin("Formula");
	}
}
