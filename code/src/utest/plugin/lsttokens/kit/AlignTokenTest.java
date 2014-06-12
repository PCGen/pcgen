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
package plugin.lsttokens.kit;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.core.PCAlignment;
import pcgen.core.kit.KitAlignment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class AlignTokenTest extends AbstractKitTokenTestCase<KitAlignment>
{

	static AlignToken token = new AlignToken();
	static CDOMSubLineLoader<KitAlignment> loader = new CDOMSubLineLoader<KitAlignment>(
			"SPELLS", KitAlignment.class);

	@Override
	@Before
	public final void setUp() throws PersistenceLayerException,
			URISyntaxException
	{
		super.setUp();
		PCAlignment lg = primaryContext.getReferenceContext().constructCDOMObject(
				PCAlignment.class, "Lawful Good");
		primaryContext.getReferenceContext().registerAbbreviation(lg, "LG");
		PCAlignment ln = primaryContext.getReferenceContext().constructCDOMObject(
				PCAlignment.class, "Lawful Neutral");
		primaryContext.getReferenceContext().registerAbbreviation(ln, "LN");
		PCAlignment slg = secondaryContext.getReferenceContext().constructCDOMObject(
				PCAlignment.class, "Lawful Good");
		secondaryContext.getReferenceContext().registerAbbreviation(slg, "LG");
		PCAlignment sln = secondaryContext.getReferenceContext().constructCDOMObject(
				PCAlignment.class, "Lawful Neutral");
		secondaryContext.getReferenceContext().registerAbbreviation(sln, "LN");
	}

	@Override
	public Class<KitAlignment> getCDOMClass()
	{
		return KitAlignment.class;
	}

	@Override
	public CDOMSubLineLoader<KitAlignment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitAlignment> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputEmptySpellbook()
			throws PersistenceLayerException
	{
		assertFalse(parse("NoAlign"));
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		runRoundRobin("LG");
	}

	@Test
	public void testRoundRobinTwo() throws PersistenceLayerException
	{
		runRoundRobin("LG|LN");
	}

}
