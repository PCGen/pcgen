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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractStringTokenTestCase<T extends CDOMObject> extends
		AbstractCDOMTokenTestCase<T>
{

	@Test
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		Assert.assertFalse(parse(""));
		Assert.assertEquals(null, primaryProf.get(getStringKey()));
		assertNoSideEffects();
	}

	protected abstract boolean isClearLegal();

	@Test
	public void testInputClear() throws PersistenceLayerException
	{
		try
		{
			Assert.assertEquals(isClearLegal(), parse(Constants.LST_DOT_CLEAR));
		}
		catch (IllegalArgumentException e)
		{
			if (isClearLegal())
			{
				throw e;
			}
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("Niederösterreich"));
		Assert.assertEquals("Niederösterreich", primaryProf.get(getStringKey()));
		Assert.assertTrue(parse("Finger Lakes"));
		Assert.assertEquals("Finger Lakes", primaryProf.get(getStringKey()));
		Assert.assertTrue(parse("Rheinhessen"));
		Assert.assertEquals("Rheinhessen", primaryProf.get(getStringKey()));
		Assert.assertTrue(parse("Languedoc-Roussillon"));
		Assert.assertEquals("Languedoc-Roussillon", primaryProf.get(getStringKey()));
		Assert.assertTrue(parse("Yarra Valley"));
		Assert.assertEquals("Yarra Valley", primaryProf.get(getStringKey()));
	}

	public abstract StringKey getStringKey();

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		String[] unparsed;
		if (isClearLegal())
		{
			Assert.assertTrue(parse(Constants.LST_DOT_CLEAR));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertNull("Expected item to be equal", unparsed);
		}
		Assert.assertTrue(parse("Start"));
		Assert.assertTrue(parse("Mod"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		Assert.assertEquals("Expected item to be equal", "Mod", unparsed[0]);
		if (isClearLegal())
		{
			Assert.assertTrue(parse(Constants.LST_DOT_CLEAR));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertNull("Expected item to be equal", unparsed);
		}
	}

	@Test
	public void testRoundRobinBase() throws PersistenceLayerException
	{
		runRoundRobin("Rheinhessen");
	}

	@Test
	public void testRoundRobinWithSpace() throws PersistenceLayerException
	{
		runRoundRobin("Finger Lakes");
	}

	@Test
	public void testRoundRobinNonEnglishAndN() throws PersistenceLayerException
	{
		runRoundRobin("Niederösterreich");
	}

	@Test
	public void testRoundRobinHyphen() throws PersistenceLayerException
	{
		runRoundRobin("Languedoc-Roussillon");
	}

	@Test
	public void testRoundRobinY() throws PersistenceLayerException
	{
		runRoundRobin("Yarra Valley");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "Languedoc-Roussillon";
	}

	@Override
	protected String getLegalValue()
	{
		return "Yarra Valley";
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		expectSingle(setAndUnparse(getLegalValue()), getLegalValue());
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getStringKey(), null);
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	/*
	 * TODO Need to define the appropriate behavior here - is the token
	 * responsible for catching this?
	 */
	// @Test
	// public void testUnparseEmpty() throws PersistenceLayerException
	// {
	// primaryProf.put(getStringKey(), "");
	// assertBadUnparse();
	// }

	protected String[] setAndUnparse(String val)
	{
		primaryProf.put(getStringKey(), val);
		return getToken().unparse(primaryContext, primaryProf);
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
