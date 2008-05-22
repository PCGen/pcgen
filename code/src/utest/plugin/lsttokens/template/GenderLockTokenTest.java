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

import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.CDOMTokenLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;

public class GenderLockTokenTest extends AbstractTokenTestCase<PCTemplate>
{

	static GenderlockToken token = new GenderlockToken();
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
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		assertTrue(parse("Male"));
		assertTrue(parseSecondary("Male"));
		assertEquals(Gender.Male, primaryProf.get(ObjectKey.GENDER_LOCK));
		internalTestInvalidInputString(Gender.Male);
		assertNoSideEffects();
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(parse("Always"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(parse("String"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(parse("TYPE=TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(parse("TYPE.TestType"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertFalse(parse("ALL"));
		assertEquals(val, primaryProf.get(ObjectKey.GENDER_LOCK));
		// Note case sensitivity
		assertFalse(parse("MALE"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("Male"));
		assertEquals(Gender.Male, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertTrue(parse("Female"));
		assertEquals(Gender.Female, primaryProf.get(ObjectKey.GENDER_LOCK));
		assertTrue(parse("Neuter"));
		assertEquals(Gender.Neuter, primaryProf.get(ObjectKey.GENDER_LOCK));
	}

	@Test
	public void testRoundRobinMale() throws PersistenceLayerException
	{
		runRoundRobin("Male");
	}

	@Test
	public void testRoundRobinFemale() throws PersistenceLayerException
	{
		runRoundRobin("Female");
	}

	@Test
	public void testRoundRobinNeuter() throws PersistenceLayerException
	{
		runRoundRobin("Neuter");
	}

}
