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

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class BonusSpellStatTokenTest extends AbstractTokenTestCase<PCClass>
{

	static BonusspellstatToken token = new BonusspellstatToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);
	private PCStat ps;

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		ps = primaryContext.ref.constructCDOMObject(PCStat.class,
				"Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		PCStat ss = secondaryContext.ref.constructCDOMObject(PCStat.class,
				"Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
		PCStat pi = primaryContext.ref.constructCDOMObject(PCStat.class,
				"Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "INT");
		PCStat si = secondaryContext.ref.constructCDOMObject(PCStat.class,
				"Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "INT");

	}

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
	public void testRoundRobinStr() throws PersistenceLayerException
	{
		runRoundRobin("STR");
	}

	@Test
	public void testRoundRobinNone() throws PersistenceLayerException
	{
		runRoundRobin("NONE");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "NONE";
	}

	@Override
	protected String getLegalValue()
	{
		return "STR";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}

	@Test
	public void testOverwriteNoneStr() throws PersistenceLayerException
	{
		parse("NONE");
		validateUnparsed(primaryContext, primaryProf, "NONE");
		parse("STR");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("STR"));
	}

	@Test
	public void testOverwriteStrNone() throws PersistenceLayerException
	{
		parse("STR");
		validateUnparsed(primaryContext, primaryProf, "STR");
		parse("NONE");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("NONE"));
	}

	@Test
	public void testUnparseNull() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private ObjectKey<PCStat> getObjectKey()
	{
		return ObjectKey.BONUS_SPELL_STAT;
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(getObjectKey(), ps);
		primaryProf.put(ObjectKey.HAS_BONUS_SPELL_STAT, true);
		expectSingle(getToken().unparse(primaryContext, primaryProf), ps.getAbb());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFailStat() throws PersistenceLayerException
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

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFailHas() throws PersistenceLayerException
	{
		ObjectKey objectKey = ObjectKey.HAS_BONUS_SPELL_STAT;
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

	@Test
	public void testUnparseNone() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.HAS_BONUS_SPELL_STAT, false);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "NONE");
	}

	/*
	 * TODO Need to define if unparse if priority-based or whether this is
	 * illegal. Changes parse if not priority based (Due to mods)
	 */
	// @Test
	// public void testUnparseInvalidNonePlus() throws PersistenceLayerException
	// {
	// primaryProf.put(getObjectKey(), ps);
	// primaryProf.put(ObjectKey.HAS_BONUS_SPELL_STAT, false);
	// assertBadUnparse();
	// }

	@Test
	public void testUnparseIllegal() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.HAS_BONUS_SPELL_STAT, true);
		assertBadUnparse();
	}
}
