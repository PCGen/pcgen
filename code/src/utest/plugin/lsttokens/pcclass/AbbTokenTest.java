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

import org.junit.Test;

import pcgen.cdom.enumeration.StringKey;
import pcgen.core.PCClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;
import plugin.lsttokens.testsupport.ConsolidationRule;

public class AbbTokenTest extends AbstractTokenTestCase<PCClass>
{
	static AbbToken token = new AbbToken();

	static CDOMTokenLoader<PCClass> loader = new CDOMTokenLoader<PCClass>(
			PCClass.class);

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
	public void testInvalidInputEmpty() throws PersistenceLayerException
	{
		assertFalse(parse(""));
		assertEquals(null, primaryContext.ref.getAbbreviation(primaryProf));
		assertNoSideEffects();
	}

	@Test
	public void testValidInputs() throws PersistenceLayerException
	{
		assertTrue(parse("Nieder�sterreich"));
		assertEquals("Nieder�sterreich", primaryProf.get(StringKey.ABB));
		assertTrue(parse("Finger Lakes"));
		assertEquals("Finger Lakes", primaryProf.get(StringKey.ABB));
		assertTrue(parse("Rheinhessen"));
		assertEquals("Rheinhessen", primaryProf.get(StringKey.ABB));
		assertTrue(parse("Languedoc-Roussillon"));
		assertEquals("Languedoc-Roussillon", primaryProf.get(StringKey.ABB));
		assertTrue(parse("Yarra Valley"));
		assertEquals("Yarra Valley", primaryProf.get(StringKey.ABB));
	}

	@Test
	public void testReplacementInputs() throws PersistenceLayerException
	{
		String[] unparsed;
		assertTrue(parse("Start"));
		assertTrue(parse("Mod"));
		unparsed = getToken().unparse(primaryContext, primaryProf);
		assertEquals("Expected item to be equal", "Mod", unparsed[0]);
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
		runRoundRobin("Nieder�sterreich");
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

	@Override
	protected ConsolidationRule getConsolidationRule()
	{
		return ConsolidationRule.OVERWRITE;
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
		assertNull(getToken().unparse(primaryContext, primaryProf));
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

	private StringKey getStringKey()
	{
		return StringKey.ABB;
	}
}
