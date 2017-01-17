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

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractBigDecimalTokenTestCase<T extends CDOMObject>
		extends AbstractCDOMTokenTestCase<T>
{

	public abstract ObjectKey<BigDecimal> getObjectKey();

	public abstract boolean isZeroAllowed();

	public abstract boolean isNegativeAllowed();

	public abstract boolean isPositiveAllowed();
	
	public abstract boolean isClearLegal();

	@Test
	public void testInvalidInputUnset() throws PersistenceLayerException
	{
		testInvalidInputs(null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSet() throws PersistenceLayerException
	{
		BigDecimal con;
		if (isPositiveAllowed())
		{
			con = new BigDecimal(3);
		}
		else
		{
			con = new BigDecimal(-3);
		}
		Assert.assertTrue(parse(con.toString()));
		Assert.assertTrue(parseSecondary(con.toString()));
		Assert.assertEquals(con, primaryProf.get(getObjectKey()));
		testInvalidInputs(con);
		assertNoSideEffects();
	}

	public void testInvalidInputs(BigDecimal val)
			throws PersistenceLayerException
	{
		// Always ensure get is unchanged
		// since no invalid item should set or reset the value
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("TestWP"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("ANY"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("FIVE"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("1/2"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		Assert.assertFalse(parse("1+3"));
		Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		// Require Integer greater than or equal to zero
		if (!isNegativeAllowed())
		{
			Assert.assertFalse(parse("-1"));
			Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		}
		if (!isPositiveAllowed())
		{
			Assert.assertFalse(parse("1"));
			Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		}
		if (!isZeroAllowed())
		{
			Assert.assertFalse(parse("0"));
			Assert.assertEquals(val, primaryProf.get(getObjectKey()));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			Assert.assertTrue(parse("4.5"));
			Assert.assertEquals(new BigDecimal(4.5), primaryProf.get(getObjectKey()));
			Assert.assertTrue(parse("5"));
			Assert.assertEquals(new BigDecimal(5), primaryProf.get(getObjectKey()));
			Assert.assertTrue(parse("1"));
			Assert.assertEquals(new BigDecimal(1), primaryProf.get(getObjectKey()));
		}
		if (isZeroAllowed())
		{
			Assert.assertTrue(parse("0"));
			Assert.assertEquals(new BigDecimal(0), primaryProf.get(getObjectKey()));
		}
		if (isNegativeAllowed())
		{
			Assert.assertTrue(parse("-2"));
			Assert.assertEquals(new BigDecimal(-2), primaryProf.get(getObjectKey()));
		}
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			runRoundRobin("1");
		}
	}

	@Test
	public void testRoundRobinZero() throws PersistenceLayerException
	{
		if (isZeroAllowed())
		{
			runRoundRobin("0");
		}
	}

	@Test
	public void testRoundRobinNegative() throws PersistenceLayerException
	{
		if (isNegativeAllowed())
		{
			runRoundRobin("-3");
		}
	}

	@Test
	public void testRoundRobinThreePointFive() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			runRoundRobin("3.5");
		}
	}

	@Override
	protected String getLegalValue()
	{
		if (isPositiveAllowed())
		{
			return "1";
		}
		else
		{
			return "-1";
		}
	}

	@Override
	protected String getAlternateLegalValue()
	{
		if (isPositiveAllowed())
		{
			return "2.2";
		}
		else
		{
			return "-2.2";
		}
	}

	@Test
	public void testArchitecturePositiveNegative()
	{
		Assert.assertTrue (isPositiveAllowed() || isNegativeAllowed());
	}

	@Test
	public void testUnparseOne() throws PersistenceLayerException
	{
		BigDecimal val = new BigDecimal(4.5);
		if (isPositiveAllowed())
		{
			primaryProf.put(getObjectKey(), val);
			expectSingle(getToken().unparse(primaryContext, primaryProf), val
					.toString());
		}
		else
		{
			primaryProf.put(getObjectKey(), val);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseZero() throws PersistenceLayerException
	{
		BigDecimal val = new BigDecimal(0);
		if (isZeroAllowed())
		{
			primaryProf.put(getObjectKey(), val);
			expectSingle(getToken().unparse(primaryContext, primaryProf), val
					.toString());
		}
		else
		{
			primaryProf.put(getObjectKey(), val);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseNegative() throws PersistenceLayerException
	{
		BigDecimal val = new BigDecimal(-2);
		if (isNegativeAllowed())
		{
			primaryProf.put(getObjectKey(), val);
			expectSingle(getToken().unparse(primaryContext, primaryProf), val
					.toString());
		}
		else
		{
			primaryProf.put(getObjectKey(), val);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
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
			// Yep!
		}
	}

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
		Assert.assertTrue(parse("3.14"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		Assert.assertEquals("Expected item to be equal", "3.14", unparsed[0]);
		if (isClearLegal())
		{
			Assert.assertTrue(parse(Constants.LST_DOT_CLEAR));
			unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertNull("Expected item to be equal", unparsed);
		}
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
