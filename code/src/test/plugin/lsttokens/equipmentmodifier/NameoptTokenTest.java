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
package plugin.lsttokens.equipmentmodifier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.cdom.enumeration.EqModNameOpt;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractCDOMTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

import org.junit.jupiter.api.Test;

class NameoptTokenTest extends AbstractCDOMTokenTestCase<EquipmentModifier>
{
	static NameoptToken token = new NameoptToken();
	static CDOMTokenLoader<EquipmentModifier> loader = new CDOMTokenLoader<>();

	@Override
	public Class<EquipmentModifier> getCDOMClass()
	{
		return EquipmentModifier.class;
	}

	@Override
	public CDOMLoader<EquipmentModifier> getLoader()
	{
		return loader;
	}

	@Override
	public CDOMPrimaryToken<EquipmentModifier> getToken()
	{
		return token;
	}

	@Test
	void testBadInputNegative()
	{
		try
		{
			boolean parse = parse("INVALID");
			assertFalse(parse);
		}
		catch (IllegalArgumentException e)
		{
			// OK
		}
	}

	@Test
	void testBadInputEmpty()
	{
		assertFalse(parse(""));
		assertNoSideEffects();
	}

	@Test
	void testBadInputPlainText()
	{
		assertFalse(parse("TEXT"));
		assertNoSideEffects();
	}

	@Test
	void testBadInputEmptyText()
	{
		assertFalse(parse("TEXT="));
		assertNoSideEffects();
	}

	@Test
	void testRoundRobinNormal() throws PersistenceLayerException
	{
		runRoundRobin("NORMAL");
	}

	@Test
	void testRoundRobinNoList() throws PersistenceLayerException
	{
		runRoundRobin("NOLIST");
	}

	@Test
	void testRoundRobinNoName() throws PersistenceLayerException
	{
		runRoundRobin("NONAME");
	}

	@Test
	void testRoundRobinNothing() throws PersistenceLayerException
	{
		runRoundRobin("NOTHING");
	}

	@Test
	void testRoundRobinSpell() throws PersistenceLayerException
	{
		runRoundRobin("SPELL");
	}

	@Test
	void testRoundRobinText() throws PersistenceLayerException
	{
		runRoundRobin("TEXT=This is the text");
	}

	@Override
	protected String getAlternateLegalValue()
	{
		return "SPELL";
	}

	@Override
	protected String getLegalValue()
	{
		return "TEXT=This is the text";
	}

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
	}


	@Test
	void testOverwriteToText()
	{
		parse("SPELL");
		validateUnparsed(primaryContext, primaryProf, "SPELL");
		parse("TEXT=This is the text");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("SPELL", "TEXT=This is the text"));
	}

	@Test
	void testOverwriteFromText()
	{
		parse("TEXT=This is the text");
		validateUnparsed(primaryContext, primaryProf, "TEXT=This is the text");
		parse("NOTHING");
		validateUnparsed(primaryContext, primaryProf, getConsolidationRule()
				.getAnswer("TEXT=This is the text", "NOTHING"));
	}
	@Test
	void testUnparseNull()
	{
		primaryProf.put(getObjectKey(), null);
		assertNull(getToken().unparse(primaryContext, primaryProf));
	}

	private static ObjectKey<EqModNameOpt> getObjectKey()
	{
		return ObjectKey.NAME_OPT;
	}

	@Test
	void testUnparseLegal()
	{
		primaryProf.put(getObjectKey(), EqModNameOpt.SPELL);
		expectSingle(getToken().unparse(primaryContext, primaryProf),
				EqModNameOpt.SPELL.toString());
	}

	@Test
	void testUnparseLegalName()
	{
		primaryProf.put(StringKey.NAME_TEXT, "MyText");
		primaryProf.put(ObjectKey.NAME_OPT, EqModNameOpt.TEXT);
		expectSingle(getToken().unparse(primaryContext, primaryProf),
				"TEXT=MyText");
	}

	@SuppressWarnings("unchecked")
	@Test
	void testUnparseGenericsFail()
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

	@Test
	void testUnparseIncompleteSpell()
	{
		primaryProf.put(ObjectKey.NAME_OPT, EqModNameOpt.TEXT);
		assertBadUnparse();
	}

	/*
	 * TODO Another item that is overwrite sensitive, need to understand how
	 * this should work and whether this is ok based on overwrite unit tests
	 * above, or whether this is invalid
	 */
	// @Test
	// public void testUnparseOther() throws PersistenceLayerException
	// {
	// primaryProf.put(ObjectKey.NAME_OPT, EqModNameOpt.SPELL);
	// primaryProf.put(StringKey.NAME_TEXT, "MyText");
	// assertBadUnparse();
	// }
}
