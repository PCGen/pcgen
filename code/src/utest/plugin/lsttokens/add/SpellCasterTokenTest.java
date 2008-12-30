/*
 * 
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class SpellCasterTokenTest extends
		AbstractAddTokenTestCase<CDOMObject, PCClass>
{

	static AddLst token = new AddLst();
	static SpellCasterToken subtoken = new SpellCasterToken();
	static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<CDOMObject>(
			CDOMObject.class);

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<CDOMObject> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<CDOMObject> getToken()
	{
		return token;
	}

	@Override
	public CDOMSecondaryToken<?> getSubToken()
	{
		return subtoken;
	}

	@Override
	public Class<PCClass> getTargetClass()
	{
		return PCClass.class;
	}

	@Override
	public boolean isAllLegal()
	{
		return true;
	}

	@Override
	public boolean isTypeLegal()
	{
		return true;
	}

	@Test
	public void testEmpty()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Override
	public boolean allowsParenAsSub()
	{
		return false;
	}

	@Override
	public boolean allowsFormula()
	{
		return true;
	}

	@Override
	public String getAllString()
	{
		return "ANY";
	}


	@Test
	public void testRoundRobinArcane() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "Arcane");
	}

	@Test
	public void testRoundRobinDivine() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "Divine");
	}

	@Test
	public void testRoundRobinPsionic() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "Psionic");
	}

	@Test
	public void testRoundRobinThreeSpellType() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "Arcane" + getJoinCharacter()
			+ "Divine" + getJoinCharacter() + "Psionic");
	}

	@Test
	public void testInvalidInputAnySpellType() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + getAllString()
				+ getJoinCharacter() + "Arcane"));
			assertNoSideEffects();
		}
	}

	@Test
	public void testInvalidInputSpellTypeAny() throws PersistenceLayerException
	{
		if (isAllLegal())
		{
			assertFalse(parse(getSubTokenName() + '|' + "Arcane"
				+ getJoinCharacter() + getAllString()));
			assertNoSideEffects();
		}
	}
}
