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
package plugin.lsttokens.ability;

import java.net.URISyntaxException;

import org.junit.Test;

import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class CategoryTokenTest extends AbstractCDOMTokenTestCase<Ability>
{

	static CategoryToken token = new CategoryToken();
	static CDOMTokenLoader<Ability> loader = new CDOMTokenLoader<>();

	@Override
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		primaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class, "Mutation");
		secondaryContext.getReferenceContext().constructCDOMObject(AbilityCategory.class,
				"Mutation");
	}

	@Override
	public Class<Ability> getCDOMClass()
	{
		return Ability.class;
	}

	@Override
	public CDOMLoader<Ability> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Ability> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidCategory() throws PersistenceLayerException
	{
		assertFalse(parse("Foo"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinFeat() throws PersistenceLayerException
	{
		runRoundRobin("FEAT");
	}

	@Test
	public void testRoundRobinMutation() throws PersistenceLayerException
	{
		runRoundRobin("Mutation");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Mutation";
	}

	@Override
	protected String getLegalValue()
	{
		return "FEAT";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
