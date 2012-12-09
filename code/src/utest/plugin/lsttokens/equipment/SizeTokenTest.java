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
package plugin.lsttokens.equipment;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.core.SizeAdjustment;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class SizeTokenTest extends AbstractTokenTestCase<Equipment>
{

	static SizeToken token = new SizeToken();
	static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<Equipment>(
			Equipment.class);
	private SizeAdjustment ps;

	@Override
	public Class<Equipment> getCDOMClass()
	{
		return Equipment.class;
	}

	@Override
	public CDOMLoader<Equipment> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Equipment> getToken()
	{
		return token;
	}

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		ps = primaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Small");
		ps.put(StringKey.ABB, "S");
		primaryContext.ref.registerAbbreviation(ps, "S");
		SizeAdjustment pm = primaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Medium");
		pm.put(StringKey.ABB, "M");
		primaryContext.ref.registerAbbreviation(pm, "M");
		SizeAdjustment ss = secondaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Small");
		ss.put(StringKey.ABB, "S");
		secondaryContext.ref.registerAbbreviation(ss, "S");
		SizeAdjustment sm = secondaryContext.ref.constructCDOMObject(
				SizeAdjustment.class, "Medium");
		sm.put(StringKey.ABB, "M");
		secondaryContext.ref.registerAbbreviation(sm, "M");
	}

	@Test
	public void testInvalidNotASize()
	{
		assertFalse(token.parseToken(primaryContext, primaryProf, "W").passed());
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinS() throws PersistenceLayerException
	{
		runRoundRobin("S");
	}

	@Test
	public void testRoundRobinM() throws PersistenceLayerException
	{
		runRoundRobin("M");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "S";
	}

	@Override
	protected String getLegalValue()
	{
		return "M";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.BASESIZE, null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.BASESIZE, ps);
		expectSingle(getToken().unparse(primaryContext, primaryProf), ps
				.getAbbreviation());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFail() throws PersistenceLayerException
	{
		ObjectKey objectKey = ObjectKey.BASESIZE;
		primaryProf.put(objectKey, new Object());
		try
		{
			getToken().unparse(primaryContext, primaryProf);
			fail();
		}
		catch (ClassCastException e)
		{
			// Yep!
		}
	}
}
