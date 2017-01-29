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
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.persistence.PersistenceLayerException;

public abstract class AbstractIntegerTokenTestCase<T extends CDOMObject>
		extends AbstractCDOMTokenTestCase<T>
{

	public abstract IntegerKey getIntegerKey();

	public abstract boolean isZeroAllowed();

	public abstract boolean isNegativeAllowed();

	public abstract boolean isPositiveAllowed();

	@Test
	public void testInvalidInputUnset() throws PersistenceLayerException
	{
		testInvalidInputs(null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSet() throws PersistenceLayerException
	{
		Integer con;
		if (isPositiveAllowed())
		{
			con = 3;
		}
		else
		{
			con = -3;
		}
		Assert.assertTrue(parse(con.toString()));
		Assert.assertTrue(parseSecondary(con.toString()));
		Assert.assertEquals(con, primaryProf.get(getIntegerKey()));
		testInvalidInputs(con);
		assertNoSideEffects();
	}

	public void testInvalidInputs(Integer val) throws PersistenceLayerException
	{
		// Always ensure get is unchanged
		// since no invalid item should set or reset the value
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("TestWP"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("ANY"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("FIVE"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("4.5"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("1/2"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		Assert.assertFalse(parse("1+3"));
		Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		// Require Integer greater than or equal to zero
		if (!isNegativeAllowed())
		{
			Assert.assertFalse(parse("-1"));
			Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		}
		if (!isPositiveAllowed())
		{
			Assert.assertFalse(parse("1"));
			Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		}
		if (!isZeroAllowed())
		{
			Assert.assertFalse(parse("0"));
			Assert.assertEquals(val, primaryProf.get(getIntegerKey()));
		}
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			Assert.assertTrue(parse("5"));
			Assert.assertEquals(Integer.valueOf(5), primaryProf.get(getIntegerKey()));
			Assert.assertTrue(parse("1"));
			Assert.assertEquals(Integer.valueOf(1), primaryProf.get(getIntegerKey()));
		}
		if (isZeroAllowed())
		{
			Assert.assertTrue(parse("0"));
			Assert.assertEquals(Integer.valueOf(0), primaryProf.get(getIntegerKey()));
		}
		if (isNegativeAllowed())
		{
			Assert.assertTrue(parse("-2"));
			Assert.assertEquals(Integer.valueOf(-2), primaryProf.get(getIntegerKey()));
		}
	}

	@Test
	public void testOutputOne() throws PersistenceLayerException
	{
		Assert.assertTrue(0 == primaryContext.getWriteMessageCount());
		primaryProf.put(getIntegerKey(), 1);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		if (isPositiveAllowed())
		{
			Assert.assertEquals(1, unparsed.length);
			Assert.assertEquals("1", unparsed[0]);
		}
		else
		{
			Assert.assertNull(unparsed);
			Assert.assertTrue(0 != primaryContext.getWriteMessageCount());
		}
	}

	@Test
	public void testOutputZero() throws PersistenceLayerException
	{
		Assert.assertTrue(0 == primaryContext.getWriteMessageCount());
		primaryProf.put(getIntegerKey(), 0);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		if (isZeroAllowed())
		{
			Assert.assertEquals(1, unparsed.length);
			Assert.assertEquals("0", unparsed[0]);
		}
		else
		{
			Assert.assertNull(unparsed);
			Assert.assertTrue(0 != primaryContext.getWriteMessageCount());
		}
	}

	@Test
	public void testOutputMinusTwo() throws PersistenceLayerException
	{
		Assert.assertTrue(0 == primaryContext.getWriteMessageCount());
		primaryProf.put(getIntegerKey(), -2);
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		if (isNegativeAllowed())
		{
			Assert.assertEquals(1, unparsed.length);
			Assert.assertEquals("-2", unparsed[0]);
		}
		else
		{
			Assert.assertNull(unparsed);
			Assert.assertTrue(0 != primaryContext.getWriteMessageCount());
		}
	}

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			Assert.assertTrue(parse("5"));
			Assert.assertTrue(parse("1"));
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertEquals("Expected item to be equal", "1", unparsed[0]);
		}
		else
		{
			Assert.assertTrue(parse("-2"));
			Assert.assertTrue(parse("-4"));
			String[] unparsed = getToken().unparse(primaryContext, primaryProf);
			Assert.assertEquals("Expected item to be equal", "-4", unparsed[0]);
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
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			runRoundRobin("5");
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
			return "2";
		}
		else
		{
			return "-2";
		}
	}

	@Test
	public void testArchitecturePositiveNegative()
	{
		Assert.assertTrue(isPositiveAllowed() || isNegativeAllowed());
	}

	@Test
	public void testUnparseOne() throws PersistenceLayerException
	{
		if (isPositiveAllowed())
		{
			expectSingle(setAndUnparse(1), Integer.toString(1));
		}
		else
		{
			primaryProf.put(getIntegerKey(), 1);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseZero() throws PersistenceLayerException
	{
		if (isZeroAllowed())
		{
			expectSingle(setAndUnparse(0), Integer.toString(0));
		}
		else
		{
			primaryProf.put(getIntegerKey(), 0);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseNegative() throws PersistenceLayerException
	{
		if (isNegativeAllowed())
		{
			expectSingle(setAndUnparse(-3), Integer.toString(-3));
		}
		else
		{
			primaryProf.put(getIntegerKey(), -3);
			assertBadUnparse();
		}
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getIntegerKey(), null);
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	protected String[] setAndUnparse(int val)
	{
		primaryProf.put(getIntegerKey(), val);
		return getToken().unparse(primaryContext, primaryProf);
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
