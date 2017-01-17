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
package plugin.lsttokens.kit.startpack;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.KitApply;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Kit;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class ApplyTokenTest extends AbstractCDOMTokenTestCase<Kit>
{

	static ApplyToken token = new ApplyToken();

	static CDOMTokenLoader<Kit> loader = new CDOMTokenLoader<Kit>();

	@Override
	public Class<Kit> getCDOMClass()
	{
		return Kit.class;
	}

	@Override
	public CDOMLoader<Kit> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Kit> getToken()
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
		Assert.assertTrue(parse("INSTANT"));
		Assert.assertTrue(parseSecondary("INSTANT"));
		Assert.assertEquals(KitApply.INSTANT, primaryProf.get(ObjectKey.APPLY_MODE));
		internalTestInvalidInputString(KitApply.INSTANT);
	}

	public void internalTestInvalidInputString(Object val)
			throws PersistenceLayerException
	{
		Assert.assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
		Assert.assertFalse(parse("Always"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.APPLY_MODE));
		// Note case sensitivity
		Assert.assertFalse(parse("Permanent"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("INSTANT"));
		Assert.assertEquals(KitApply.INSTANT, primaryProf.get(ObjectKey.APPLY_MODE));
		Assert.assertTrue(parse("PERMANENT"));
		Assert.assertEquals(KitApply.PERMANENT, primaryProf.get(ObjectKey.APPLY_MODE));
	}

	@Test
	public void testRoundRobinPermanent() throws PersistenceLayerException
	{
		runRoundRobin("PERMANENT");
	}

	@Test
	public void testRoundRobinInstant() throws PersistenceLayerException
	{
		runRoundRobin("INSTANT");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "PERMANENT";
	}

	@Override
	protected String getLegalValue()
	{
		return "INSTANT";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
