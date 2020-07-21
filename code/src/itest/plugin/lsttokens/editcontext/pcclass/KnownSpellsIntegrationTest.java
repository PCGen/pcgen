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
package plugin.lsttokens.editcontext.pcclass;

import java.net.URISyntaxException;

import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.editcontext.testsupport.AbstractListIntegrationTestCase;
import plugin.lsttokens.pcclass.KnownspellsToken;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class KnownSpellsIntegrationTest extends
		AbstractListIntegrationTestCase<PCClass, Spell>
{

	private static KnownspellsToken token = new KnownspellsToken();
	private static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<>();

	@Override
	@BeforeEach
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		prefix = "CLASS:";
	}

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClass> getToken()
	{
		return token;
	}

	@Override
	public Class<Spell> getTargetClass()
	{
		return Spell.class;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Override
	public boolean isClearAll()
	{
		return true;
	}

	@Override
	public boolean isPrereqLegal()
	{
		return false;
	}

	@Override
	public boolean isAllLegal()
	{
		return false;
	}

	// @Test
	// public void testRoundRobinLevelAddType() throws PersistenceLayerException
	// {
	// if (isTypeLegal())
	// {
	// verifyCleanStart();
	// TestContext tc = new TestContext();
	// commit(testCampaign, tc, "LEVEL=2");
	// commit(modCampaign, tc, "TYPE=TestType");
	// completeRoundRobin(tc);
	// }
	// }
	//
	// @Test
	// public void testRoundRobinLevelStartType() throws
	// PersistenceLayerException
	// {
	// if (isTypeLegal())
	// {
	// verifyCleanStart();
	// TestContext tc = new TestContext();
	// commit(testCampaign, tc, "TYPE=TestAltType.TestThirdType.TestType");
	// commit(modCampaign, tc, "LEVEL=5");
	// completeRoundRobin(tc);
	// }
	// }
	//
	// @Test
	// public void testRoundRobinLevelDotClear() throws
	// PersistenceLayerException
	// {
	// if (isClearLegal())
	// {
	// verifyCleanStart();
	// TestContext tc = new TestContext();
	// commit(testCampaign, tc, "LEVEL=1");
	// commit(modCampaign, tc, Constants.LST_DOT_CLEAR);
	// completeRoundRobin(tc);
	// }
	// }

}
