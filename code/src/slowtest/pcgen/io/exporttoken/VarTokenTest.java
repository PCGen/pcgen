/*
 * Copyright 2005 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.io.exporttoken;

import static org.junit.jupiter.api.Assertions.assertEquals;

import pcgen.AbstractCharacterTestCase;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.VariableKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import plugin.exporttokens.VarToken;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@code VarTokenTest} tests the functioning of the VAR
 * token processing code. 
 */
class VarTokenTest extends AbstractCharacterTestCase
{
	@BeforeEach
    @Override
	protected void setUp() throws Exception
	{
		super.setUp();
		PlayerCharacter character = getCharacter();

		final PCClass varClass = new PCClass();
		varClass.setName("varClass");
		varClass.put(VariableKey.getConstant("Pos"), FormulaFactory.getFormulaFor(100.35));
		varClass.put(VariableKey.getConstant("Neg"), FormulaFactory.getFormulaFor(-555.55));
		varClass.put(VariableKey.getConstant("PosInt"), FormulaFactory.getFormulaFor(105));
		varClass.put(VariableKey.getConstant("NegInt"), FormulaFactory.getFormulaFor(-560));

		// Set a couple of vars one positive and one negative
		character.incrementClassLevel(1, varClass);
	}

	/**
	 * Test the output for positive numbers with fractions.
	 */
	@Test
	void testPositiveFractOutput()
	{
		assertEquals("100.35", new VarToken().getToken("VAR.Pos",
			getCharacter(), null), "VAR.Pos");
		assertEquals("100", new VarToken().getToken(
			"VAR.Pos.INTVAL", getCharacter(), null), "VAR.Pos.INTVAL");
		assertEquals("100.35", new VarToken().getToken(
			"VAR.Pos.NOSIGN", getCharacter(), null), "VAR.Pos.NOSIGN");
		assertEquals("100", new VarToken().getToken(
			"VAR.Pos.NOSIGN.INTVAL", getCharacter(), null), "VAR.Pos.NOSIGN.INTVAL");
		assertEquals("100", new VarToken().getToken(
			"VAR.Pos.INTVAL.NOSIGN", getCharacter(), null), "VAR.Pos.INTVAL.NOSIGN");
		assertEquals("+100.35", new VarToken().getToken(
			"VAR.Pos.SIGN", getCharacter(), null), "VAR.Pos.SIGN");
		assertEquals("+100", new VarToken().getToken(
			"VAR.Pos.SIGN.INTVAL", getCharacter(), null), "VAR.Pos.SIGN.INTVAL");
		assertEquals("+100", new VarToken().getToken(
			"VAR.Pos.INTVAL.SIGN", getCharacter(), null), "VAR.Pos.INTVAL.SIGN");
	}

	/**
	 * Test the output for negative numbers with fractions.
	 */
	@Test
	void testNegativeFractOutput()
	{
		assertEquals("-555.55", new VarToken().getToken("VAR.Neg",
			getCharacter(), null), "VAR.Neg");
		assertEquals("-555", new VarToken().getToken(
			"VAR.Neg.INTVAL", getCharacter(), null), "VAR.Neg.INTVAL");
		assertEquals("-555.55", new VarToken().getToken(
			"VAR.Neg.NOSIGN", getCharacter(), null), "VAR.Neg.NOSIGN");
		assertEquals("-555", new VarToken().getToken(
			"VAR.Neg.NOSIGN.INTVAL", getCharacter(), null), "VAR.Neg.NOSIGN.INTVAL");
		assertEquals("-555", new VarToken().getToken(
			"VAR.Neg.INTVAL.NOSIGN", getCharacter(), null), "VAR.Neg.INTVAL.NOSIGN");
		assertEquals("-555.55", new VarToken().getToken(
			"VAR.Neg.SIGN", getCharacter(), null), "VAR.Neg.SIGN");
		assertEquals("-555", new VarToken().getToken(
			"VAR.Neg.SIGN.INTVAL", getCharacter(), null), "VAR.Neg.SIGN.INTVAL");
		assertEquals("-555", new VarToken().getToken(
			"VAR.Neg.INTVAL.SIGN", getCharacter(), null), "VAR.Neg.INTVAL.SIGN");
	}

	/**
	 * Test the output for positive numbers without fractions.
	 */
	@Test
	void testPositiveIntOutput()
	{
		assertEquals("105.0", new VarToken().getToken(
			"VAR.PosInt", getCharacter(), null), "VAR.PosInt");
		assertEquals("105", new VarToken().getToken(
			"VAR.PosInt.INTVAL", getCharacter(), null), "VAR.PosInt.INTVAL");
		assertEquals("105.0", new VarToken().getToken(
			"VAR.PosInt.NOSIGN", getCharacter(), null), "VAR.PosInt.NOSIGN");
		assertEquals("105", new VarToken()
			.getToken("VAR.PosInt.NOSIGN.INTVAL", getCharacter(), null), "VAR.PosInt.NOSIGN.INTVAL");
		assertEquals("105", new VarToken()
			.getToken("VAR.PosInt.INTVAL.NOSIGN", getCharacter(), null), "VAR.PosInt.INTVAL.NOSIGN");
		assertEquals("+105.0", new VarToken().getToken(
			"VAR.PosInt.SIGN", getCharacter(), null), "VAR.PosInt.SIGN");
		assertEquals("+105", new VarToken().getToken(
			"VAR.PosInt.SIGN.INTVAL", getCharacter(), null), "VAR.PosInt.SIGN.INTVAL");
		assertEquals("+105", new VarToken().getToken(
			"VAR.PosInt.INTVAL.SIGN", getCharacter(), null), "VAR.PosInt.INTVAL.SIGN");
	}

	/**
	 * Test the output for negative numbers without fractions.
	 */
	@Test
	void testNegativeIntOutput()
	{
		assertEquals("-560.0", new VarToken().getToken(
			"VAR.NegInt", getCharacter(), null), "VAR.NegInt");
		assertEquals("-560", new VarToken().getToken(
			"VAR.NegInt.INTVAL", getCharacter(), null), "VAR.NegInt.INTVAL");
		assertEquals("-560.0", new VarToken().getToken(
			"VAR.NegInt.NOSIGN", getCharacter(), null), "VAR.NegInt.NOSIGN");
		assertEquals("-560", new VarToken()
			.getToken("VAR.NegInt.NOSIGN.INTVAL", getCharacter(), null), "VAR.NegInt.NOSIGN.INTVAL");
		assertEquals("-560", new VarToken()
			.getToken("VAR.NegInt.INTVAL.NOSIGN", getCharacter(), null), "VAR.NegInt.INTVAL.NOSIGN");
		assertEquals("-560.0", new VarToken().getToken(
			"VAR.NegInt.SIGN", getCharacter(), null), "VAR.NegInt.SIGN");
		assertEquals("-560", new VarToken().getToken(
			"VAR.NegInt.SIGN.INTVAL", getCharacter(), null), "VAR.NegInt.SIGN.INTVAL");
		assertEquals("-560", new VarToken().getToken(
			"VAR.NegInt.INTVAL.SIGN", getCharacter(), null), "VAR.NegInt.INTVAL.SIGN");
	}

}
