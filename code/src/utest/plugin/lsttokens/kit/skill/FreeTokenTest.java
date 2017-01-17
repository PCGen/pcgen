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
package plugin.lsttokens.kit.skill;

import org.junit.Assert;
import org.junit.Test;

import pcgen.core.kit.KitSkill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMSubLineLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractKitTokenTestCase;

public class FreeTokenTest extends AbstractKitTokenTestCase<KitSkill>
{

	static FreeToken token = new FreeToken();
	static CDOMSubLineLoader<KitSkill> loader = new CDOMSubLineLoader<>(
			"SKILL", KitSkill.class);

	@Override
	public Class<KitSkill> getCDOMClass()
	{
		return KitSkill.class;
	}

	@Override
	public CDOMSubLineLoader<KitSkill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<KitSkill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("YES"));
		Assert.assertTrue(parseSecondary("YES"));
		Assert.assertEquals(Boolean.TRUE, getValue());
		internalTestInvalidInputString(Boolean.TRUE);
	}

	public void internalTestInvalidInputString(Object val)
			throws PersistenceLayerException
	{
		Assert.assertEquals(val, getValue());
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, getValue());
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, getValue());
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, getValue());
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, getValue());
		Assert.assertFalse(parse("Yo!"));
		Assert.assertEquals(val, getValue());
		Assert.assertFalse(parse("Now"));
		Assert.assertEquals(val, getValue());
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("YES"));
		Assert.assertEquals(Boolean.TRUE, getValue());
		Assert.assertTrue(parse("NO"));
		Assert.assertEquals(Boolean.FALSE, getValue());
		// We're nice enough to be case insensitive here...
		Assert.assertTrue(parse("YeS"));
		Assert.assertEquals(Boolean.TRUE, getValue());
		Assert.assertTrue(parse("Yes"));
		Assert.assertEquals(Boolean.TRUE, getValue());
		Assert.assertTrue(parse("No"));
		Assert.assertEquals(Boolean.FALSE, getValue());
		// Allow abbreviations
		Assert.assertTrue(parse("Y"));
		Assert.assertEquals(Boolean.TRUE, getValue());
		Assert.assertTrue(parse("N"));
		Assert.assertEquals(Boolean.FALSE, getValue());
	}

	private Boolean getValue()
	{
		return primaryProf.getFree();
	}

	@Test
	public void testRoundRobinYes() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinNo() throws PersistenceLayerException
	{
		runRoundRobin("NO");
	}
}
