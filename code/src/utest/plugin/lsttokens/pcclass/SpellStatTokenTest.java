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
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class SpellStatTokenTest extends AbstractTokenTestCase<PCClass>
{

	static SpellstatToken token = new SpellstatToken();
	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);
	private PCStat ps;

	@Override
	@Before
	public void setUp() throws PersistenceLayerException, URISyntaxException
	{
		super.setUp();
		ps = primaryContext.ref.constructCDOMObject(PCStat.class, "Strength");
		primaryContext.ref.registerAbbreviation(ps, "STR");
		ps.put(StringKey.ABB, "STR");
		PCStat ss = secondaryContext.ref.constructCDOMObject(PCStat.class,
				"Strength");
		secondaryContext.ref.registerAbbreviation(ss, "STR");
		ss.put(StringKey.ABB, "STR");
		PCStat pi = primaryContext.ref.constructCDOMObject(PCStat.class,
				"Intelligence");
		primaryContext.ref.registerAbbreviation(pi, "INT");
		pi.put(StringKey.ABB, "INT");
		PCStat si = secondaryContext.ref.constructCDOMObject(PCStat.class,
				"Intelligence");
		secondaryContext.ref.registerAbbreviation(si, "INT");
		si.put(StringKey.ABB, "INT");
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
	public void testRoundRobinStat() throws PersistenceLayerException
	{
		runRoundRobin("STR");
	}

	@Test
	public void testRoundRobinSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL");
	}

	@Test
	public void testRoundRobinOther() throws PersistenceLayerException
	{
		runRoundRobin("OTHER");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "OTHER";
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
	public void testOverwriteStrSpell() throws PersistenceLayerException
	{
		parse("STR");
		validateUnparsed(primaryContext, primaryProf, "STR");
		parse("SPELL");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("STR", "SPELL"));
	}

	@Test
	public void testOverwriteStrOther() throws PersistenceLayerException
	{
		parse("STR");
		validateUnparsed(primaryContext, primaryProf, "STR");
		parse("OTHER");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("STR", "OTHER"));
	}

	@Test
	public void testOverwriteSpellOther() throws PersistenceLayerException
	{
		parse("SPELL");
		validateUnparsed(primaryContext, primaryProf, "SPELL");
		parse("OTHER");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("SPELL", "OTHER"));
	}

	@Test
	public void testOverwriteSpellStr() throws PersistenceLayerException
	{
		parse("SPELL");
		validateUnparsed(primaryContext, primaryProf, "SPELL");
		parse("STR");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("SPELL", "STR"));
	}

	@Test
	public void testOverwriteOtherStr() throws PersistenceLayerException
	{
		parse("OTHER");
		validateUnparsed(primaryContext, primaryProf, "OTHER");
		parse("STR");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("OTHER", "STR"));
	}

	@Test
	public void testOverwriteOtherSpell() throws PersistenceLayerException
	{
		parse("OTHER");
		validateUnparsed(primaryContext, primaryProf, "OTHER");
		parse("SPELL");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("OTHER", "SPELL"));
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

	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFailHas() throws PersistenceLayerException
	{
		ObjectKey objectKey = ObjectKey.USE_SPELL_SPELL_STAT;
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

	@Test
	public void testUnparseSpell() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, true);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "SPELL");
	}

	/*
	 * TODO Is unparse priority based, or are there a set of legal items (forces
	 * parse to change as well due to mod behavior)
	 */
	// @Test
	// public void testUnparseIllegalMixTrue() throws PersistenceLayerException
	// {
	// primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, true);
	// primaryProf.put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, true);
	// assertBadUnparse();
	// }
	//
	// @Test
	// public void testUnparseIllegalMixTruePlus() throws
	// PersistenceLayerException
	// {
	// primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, true);
	// primaryProf.put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, true);
	// primaryProf.put(getObjectKey(), ps);
	// assertBadUnparse();
	// }
	//
	// @Test
	// public void testUnparseIllegalMixFalse() throws PersistenceLayerException
	// {
	// primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, true);
	// primaryProf.put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, false);
	// primaryProf.put(getObjectKey(), ps);
	// assertBadUnparse();
	// }
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUnparseGenericsFailCaster()
			throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, false);
		ObjectKey objectKey = ObjectKey.CASTER_WITHOUT_SPELL_STAT;
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

	@Test
	public void testUnparseIncompleteSpell() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, false);
		assertBadUnparse();
	}

	@Test
	public void testUnparseOther() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, false);
		primaryProf.put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, true);
		expectSingle(getToken().unparse(primaryContext, primaryProf), "OTHER");
	}

	@Test
	public void testUnparseIncompleteOther() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, false);
		primaryProf.put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, false);
		assertBadUnparse();
	}

	@Test
	public void testUnparseLegal() throws PersistenceLayerException
	{
		primaryProf.put(ObjectKey.USE_SPELL_SPELL_STAT, false);
		primaryProf.put(ObjectKey.CASTER_WITHOUT_SPELL_STAT, false);
		primaryProf.put(getObjectKey(), ps);
		expectSingle(getToken().unparse(primaryContext, primaryProf), ps
				.getAbb());
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
			// Yep!
		}
	}
}
