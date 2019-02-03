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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.spell.Spell;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractTypeSafeListTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;
public class RangeTokenTest extends AbstractTypeSafeListTestCase<Spell, String>
{

	static RangeToken token = new RangeToken();
	static CDOMTokenLoader<Spell> loader = new CDOMTokenLoader<>();

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

	@Override
	public String getConstant(String string)
	{
		return string;
	}

	@Override
	public char getJoinCharacter()
	{
		return '|';
	}

	@Override
	public ListKey<String> getListKey()
	{
		return ListKey.RANGE;
	}

	@Override
	public boolean isClearDotLegal()
	{
		return false;
	}

	@Override
	public boolean isClearLegal()
	{
		return true;
	}

	@Override
	protected boolean requiresPreconstruction()
	{
		return false;
	}

	@Test
	public void testGoodParentheses()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		List<?> coll;
		assertTrue(parse("(first)"));
		coll = primaryProf.getListFor(getListKey());
		assertEquals(1, coll.size());
		assertTrue(coll.contains(getConstant("(first)")));
		assertCleanConstruction();
	}

	@Test
	public void testBadParentheses()
	{
		primaryContext.getReferenceContext().constructCDOMObject(getCDOMClass(), "Rheinhessen");
		assertFalse(parse("(first"), "Missing end paren should have been flagged.");
		assertFalse(parse("first)"), "Missing start paren should have been flagged.");
		assertFalse(parse("(fir)st)"), "Missing start paren should have been flagged.");
		assertFalse(parse(")(fir(st)"), "Out of order parens should have been flagged.");
	}

	/*
	 * TODO Need to figure out ownership of this responsibility
	 */
	// @Test
	// public void testUnparseBadParens() throws PersistenceLayerException
	// {
	// primaryProf.addToListFor(getListKey(), "(first");
	// assertBadUnparse();
	//	}
}
