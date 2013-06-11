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

import org.junit.Test;

import pcgen.core.Ability;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class AppliedNameTokenTest extends AbstractTokenTestCase<Ability>
{

	static AppliedNameToken token = new AppliedNameToken();
	static CDOMTokenLoader<Ability> loader = new CDOMTokenLoader<Ability>(
			Ability.class);

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
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	protected String getLegalValue()
	{
		return "Empowered";
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Quickened";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
