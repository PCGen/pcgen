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
package plugin.lsttokens.testsupport;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractGlobalYesNoTokenTestCase extends
		AbstractGlobalTokenTestCase
{

	public abstract ObjectKey<Boolean> getObjectKey();

	@Test
	public void testInvalidInputString() throws PersistenceLayerException
	{
		internalTestInvalidInputString(null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputStringSet() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("YES"));
		Assert.assertTrue(parseSecondary("YES"));
		Assert.assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
		internalTestInvalidInputString(Boolean.TRUE);
		assertNoSideEffects();
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("Yo!"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("Now"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("YES"));
		Assert.assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
		Assert.assertTrue(parse("NO"));
		Assert.assertEquals(Boolean.FALSE, primaryProf.get(getObjectKey()));
		// We're nice enough to be case insensitive here...
		Assert.assertTrue(parse("YeS"));
		Assert.assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
		Assert.assertTrue(parse("Yes"));
		Assert.assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
		Assert.assertTrue(parse("No"));
		Assert.assertEquals(Boolean.FALSE, primaryProf.get(getObjectKey()));
		// Allow abbreviations
		Assert.assertTrue(parse("Y"));
		Assert.assertEquals(Boolean.TRUE, primaryProf.get(getObjectKey()));
		Assert.assertTrue(parse("N"));
		Assert.assertEquals(Boolean.FALSE, primaryProf.get(getObjectKey()));
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

	@Override
	protected String getAlternateLegalValue()
	{
		return "YES";
	}

	@Override
	protected String getLegalValue()
	{
		return "NO";
	}

	@Test
	public void testUnparseYes() throws PersistenceLayerException
	{
		expectSingle(setAndUnparse(true), "YES");
	}

	@Test
	public void testUnparseNo() throws PersistenceLayerException
	{
		expectSingle(setAndUnparse(false), "NO");
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		Assert.assertNull(unparsed);
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

	protected String[] setAndUnparse(boolean val)
	{
		primaryProf.put(getObjectKey(), val);
		return getToken().unparse(primaryContext, primaryProf);
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
