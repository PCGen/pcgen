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
package plugin.lsttokens.equipmentmodifier;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.EqModFormatCat;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class FormatcatTokenTest extends
		AbstractCDOMTokenTestCase<EquipmentModifier>
{

	static FormatcatToken token = new FormatcatToken();
	static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<EquipmentModifier>();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public CDOMLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<EquipmentModifier> getToken()
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
		Assert.assertTrue(parse("FRONT"));
		Assert.assertTrue(parseSecondary("FRONT"));
		Assert.assertEquals(EqModFormatCat.FRONT, primaryProf.get(ObjectKey.FORMAT));
		internalTestInvalidInputString(EqModFormatCat.FRONT);
		assertNoSideEffects();
	}

	public void internalTestInvalidInputString(Object val)
			throws PersistenceLayerException
	{
		Assert.assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertFalse(parse("Always"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.FORMAT));
		// Note case sensitivity
		Assert.assertFalse(parse("Middle"));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("FRONT"));
		Assert.assertEquals(EqModFormatCat.FRONT, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertTrue(parse("MIDDLE"));
		Assert.assertEquals(EqModFormatCat.MIDDLE, primaryProf.get(ObjectKey.FORMAT));
		Assert.assertTrue(parse("PARENS"));
		Assert.assertEquals(EqModFormatCat.PARENS, primaryProf.get(ObjectKey.FORMAT));
	}

	@Test
	public void testRoundRobinFront() throws PersistenceLayerException
	{
		runRoundRobin("FRONT");
	}

	@Test
	public void testRoundRobinMiddle() throws PersistenceLayerException
	{
		runRoundRobin("MIDDLE");
	}

	@Test
	public void testRoundRobinParens() throws PersistenceLayerException
	{
		runRoundRobin("PARENS");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "FRONT";
	}

	@Override
	protected String getLegalValue()
	{
		return "PARENS";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private ObjectKey<EqModFormatCat> getObjectKey()
	{
		return ObjectKey.FORMAT;
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), EqModFormatCat.FRONT);
		expectSingle(getToken().unparse(primaryContext, primaryProf), EqModFormatCat.FRONT.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = getObjectKey();
		primaryProf.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			Assert.fail();
		}
		catch (ClassCastException e)
		{
			//Yep!
		}
	}
}
