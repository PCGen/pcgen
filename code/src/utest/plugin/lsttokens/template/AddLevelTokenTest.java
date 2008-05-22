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

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class AddLevelTokenTest extends AbstractTokenTestCase<PCTemplate>
{
	static AddLevelToken token = new AddLevelToken();
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

	@Test
	public void testInvalidInputNoPipe() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter:3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoClass() throws PersistenceLayerException
	{
		assertFalse(parse("|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNoLevelCount() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputTwoPipes() throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|3|3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNegativeLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|-5"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputZeroLevelCount()
		throws PersistenceLayerException
	{
		assertFalse(parse("Fighter|0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputNotAClass() throws PersistenceLayerException
	{
		assertTrue(parse("NotAClass|3"));
		assertFalse(primaryContext.ref.validate());
	}

	@Test
	public void testRoundRobinSimple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		runRoundRobin("Fighter|3");
	}

	@Test
	public void testRoundRobinMultiple() throws PersistenceLayerException
	{
		primaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		primaryContext.ref.constructCDOMObject(PCClass.class, "Thief");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Fighter");
		secondaryContext.ref.constructCDOMObject(PCClass.class, "Thief");
		runRoundRobin("Fighter|3", "Thief|4");
	}

}