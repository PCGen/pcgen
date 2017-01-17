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
package plugin.lsttokens.skill;

import org.junit.Assert;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Skill;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.enumeration.Visibility;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class VisibleTokenTest extends AbstractCDOMTokenTestCase<Skill>
{

	static VisibleToken token = new VisibleToken();
	static CDOMTokenLoader<Skill> loader = new CDOMTokenLoader<Skill>();

	@Override
	public Class<Skill> getCDOMClass()
	{
		return Skill.class;
	}

	@Override
	public CDOMLoader<Skill> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Skill> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidOutput()
	{
		Assert.assertEquals(0, primaryContext.getWriteMessageCount());
		primaryProf.put(ObjectKey.VISIBILITY, Visibility.QUALIFY);
		Assert.assertNull(token.unparse(primaryContext, primaryProf));
		Assert.assertFalse(primaryContext.getWriteMessageCount() == 0);
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
		Assert.assertTrue(parse("EXPORT"));
		Assert.assertEquals(Visibility.OUTPUT_ONLY, primaryProf.get(ObjectKey.VISIBILITY));
		internalTestInvalidInputString(Visibility.OUTPUT_ONLY);
	}

	@Test
	public void testInvalidInputStringSetDisplay()
		throws PersistenceLayerException
	{
		Assert.assertTrue(parse("DISPLAY"));
		Assert.assertEquals(Visibility.DISPLAY_ONLY, primaryProf.get(ObjectKey.VISIBILITY));
		internalTestInvalidInputString(Visibility.DISPLAY_ONLY);
	}

	public void internalTestInvalidInputString(Object val)
		throws PersistenceLayerException
	{
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("Always"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("String"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("TYPE=TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("TYPE.TestType"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("ALL"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		// Must be EXPORT|READONLY
		Assert.assertFalse(parse("DISPLAY|"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("DISPLAY|FLUFF"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		// Note case sensitivity
		Assert.assertFalse(parse("Display"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("DISPLAY|ReadOnly"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertFalse(parse("EXPORT|READONLY"));
		Assert.assertEquals(val, primaryProf.get(ObjectKey.VISIBILITY));
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		Assert.assertTrue(parse("DISPLAY"));
		Assert.assertEquals(Visibility.DISPLAY_ONLY, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertTrue(parse("EXPORT"));
		Assert.assertEquals(Visibility.OUTPUT_ONLY, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertTrue(parse("YES"));
		Assert.assertEquals(Visibility.DEFAULT, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertTrue(parse("GUI"));
		Assert.assertEquals(Visibility.DISPLAY_ONLY, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertTrue(parse("CSHEET"));
		Assert.assertEquals(Visibility.OUTPUT_ONLY, primaryProf.get(ObjectKey.VISIBILITY));
		Assert.assertTrue(parse("ALWAYS"));
		Assert.assertEquals(Visibility.DEFAULT, primaryProf.get(ObjectKey.VISIBILITY));
	}

	@Test
	public void testRoundRobinYes() throws PersistenceLayerException
	{
		runRoundRobin("YES");
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("DISPLAY");
	}

	@Test
	public void testRoundRobinExport() throws PersistenceLayerException
	{
		runRoundRobin("EXPORT");
	}

	@Test
	public void testRoundRobinDisplayReadOnly()
		throws PersistenceLayerException
	{
		runRoundRobin("DISPLAY|READONLY");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "EXPORT";
	}

	@Override
	protected String getLegalValue()
	{
		return "DISPLAY|READONLY";
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		Assert.assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private ObjectKey<Visibility> getObjectKey()
	{
		return ObjectKey.VISIBILITY;
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), Visibility.DEFAULT);
		expectSingle(getToken().unparse(primaryContext, primaryProf), Visibility.DEFAULT.getLSTFormat());
	}

	@Test
	public void testUnparseIllegal() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), Visibility.HIDDEN);
		assertBadUnparse();
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

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}
}
