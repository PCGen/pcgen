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
package plugin.lsttokens.pcclass;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class HDTokenTest extends AbstractCDOMTokenTestCase<PCClass>
{

	static HdToken token = new HdToken();
	static CDOMTokenLoader<PCClass> loader =
			new CDOMTokenLoader<PCClass>();

	@Override
	public Class<PCClass> getCDOMClass()
	{
		return PCClass.class;
	}

	@Override
	public CDOMLoader<PCClass> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<PCClass> getToken()
	{
		return token;
	}

	@Test
	public void dummyTest()
	{
		// Just to get Eclipse to recognize this as a JUnit 4.0 Test Case
	}

	@Test
	public void testInvalidInputUnset() throws PersistenceLayerException
	{
		testInvalidInputs(null);
		assertNoSideEffects();
	}

	@Test
	public void testInvalidInputSet() throws PersistenceLayerException
	{
		Integer con = 3;
		Assert.assertTrue(parse(con.toString()));
		Assert.assertTrue(parseSecondary(con.toString()));
		Assert.assertEquals(con.intValue(), primaryProf.get(ObjectKey.LEVEL_HITDIE)
                                                       .getDie());
		testInvalidInputs(new HitDie(con));
		assertNoSideEffects();
	}

	public void testInvalidInputs(HitDie val) throws PersistenceLayerException
	{
		// Always ensure get is unchanged
		// since no invalid item should set or reset the value
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("TestWP"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("ANY"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("FIVE"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("4.5"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("1/2"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("1+3"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		// Require Integer greater than or equal to zero
		Assert.assertFalse(parse("-1"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertFalse(parse("0"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.LEVEL_HITDIE));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("5"));
		Assert.assertEquals(new HitDie(5), primaryProf.get(ObjectKey.LEVEL_HITDIE));
		Assert.assertTrue(parse("1"));
		Assert.assertEquals(new HitDie(1), primaryProf.get(ObjectKey.LEVEL_HITDIE));
	}

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("5"));
		Assert.assertTrue(parse("1"));
		String[] unparsed = getToken().unparse(primaryContext, primaryProf);
		Assert.assertEquals("Expected item to be equal", "1", unparsed[0]);
	}

	@Test
	public void testRoundRobinOne() throws PersistenceLayerException
	{
		runRoundRobin("1");
	}

	@Test
	public void testRoundRobinFive() throws PersistenceLayerException
	{
		runRoundRobin("5");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "4";
	}

	@Override
	protected String getLegalValue()
	{
		return "2";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.LEVEL_HITDIE, null);
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.LEVEL_HITDIE, new HitDie(1));
		expectSingle(getToken().unparse(primaryContext, primaryProf), "1");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = ObjectKey.LEVEL_HITDIE;
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
	public void testUnparseNegativeLevel() throws PersistenceLayerException
	{
		try
		{
			primaryProf.put(ObjectKey.LEVEL_HITDIE, new HitDie(-1));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}

	@Test
	public void testUnparseZero() throws PersistenceLayerException
	{
		try
		{
			primaryProf.put(ObjectKey.LEVEL_HITDIE, new HitDie(0));
			assertBadUnparse();
		}
		catch (IllegalArgumentException e)
		{
			// Good here too :)
		}
	}
}
