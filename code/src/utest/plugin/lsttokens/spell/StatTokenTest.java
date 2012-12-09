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
package plugin.lsttokens.spell;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCStat;
import pcgen.core.spell.Spell;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class StatTokenTest extends AbstractTokenTestCase<Spell>
{

	static StatToken token = new StatToken();
	static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<Spell>(
			Spell.class);
	private PCStat ps;

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		ps = primaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		ps.put(StringKey.ABB, "STR");
		PCStat ss = secondaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
		ss.put(StringKey.ABB, "STR");
		PCStat pi = primaryContext.ref.constructCDOMObject(PCStat.class, "Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "INT");
		pi.put(StringKey.ABB, "INT");
		PCStat si = secondaryContext.ref.constructCDOMObject(PCStat.class, "Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "INT");
		si.put(StringKey.ABB, "INT");
	}

	@Override
	public Class<Spell> getCDOMClass()
	{
		return Spell.class;
	}

	@Override
	public CDOMLoader<Spell> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<Spell> getToken()
	{
		return token;
	}

	@Test
	public void testInvalidNotAStat() throws PersistenceLayerException
	{
		assertFalse(parse("NAN"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultipleStatComma() throws PersistenceLayerException
	{
		assertFalse(parse("STR,INT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultipleStatBar() throws PersistenceLayerException
	{
		assertFalse(parse("STR|INT"));
		assertNoSideEffects();
	}

	@Test
	public void testInvalidMultipleStatDot() throws PersistenceLayerException
	{
		assertFalse(parse("STR.INT"));
		assertNoSideEffects();
	}

	@Test
	public void testRoundRobinDisplay() throws PersistenceLayerException
	{
		runRoundRobin("STR");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "STR";
	}

	@Override
	protected String getLegalValue()
	{
		return "INT";
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private ObjectKey<PCStat> getObjectKey()
	{
		return ObjectKey.SPELL_STAT;
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), ps);
		expectSingle(getToken().unparse(primaryContext, primaryProf), ps.getAbb());
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
			fail();
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
