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
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.AddLst;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class ClassSkillsTokenTest extends
		AbstractAddTokenTestCase<CDOMObject, Skill>
{

	static AddLst token = new AddLst();
	static ClassSkillsToken subtoken = new ClassSkillsToken();
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
	public Class<Skill> getTargetClass()
	{
		return Skill.class;
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

	@Test
	public void testRoundRobinTrained() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "TRAINED");
	}

	@Test
	public void testRoundRobinUntrained() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "UNTRAINED");
	}

	@Test
	public void testRoundRobinExclusive() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "EXCLUSIVE");
	}

	@Test
	public void testRoundRobinNonExclusive() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "NONEXCLUSIVE");
	}

	@Test
	public void testRoundRobinAutorank() throws PersistenceLayerException
	{
		runRoundRobin(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=3");
	}

	@Test
	public void testInvalidInputAutoRankNoRank() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK="));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAutoRankNegativeRank() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=-3"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAutoRankZeroRank() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=0"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputAutoRankDuplicated() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "NONEXCLUSIVE,AUTORANK=3,AUTORANK=2"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputOnlyAutoRank() throws PersistenceLayerException
	{
		assertFalse(parse(getSubTokenName() + '|' + "AUTORANK=3"));
		assertNoSideEffects();
	}

	@Override
	public String getAllString()
	{
		return "ANY";
	}
}
