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

import static org.junit.Assert.assertEquals;

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
public class VarTokenTest extends AbstractCharacterTestCase
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
    public void testPositiveFractOutput()
    {
        assertEquals("VAR.Pos", "100.35", new VarToken().getToken("VAR.Pos",
                getCharacter(), null));
        assertEquals("VAR.Pos.INTVAL", "100", new VarToken().getToken(
                "VAR.Pos.INTVAL", getCharacter(), null));
        assertEquals("VAR.Pos.NOSIGN", "100.35", new VarToken().getToken(
                "VAR.Pos.NOSIGN", getCharacter(), null));
        assertEquals("VAR.Pos.NOSIGN.INTVAL", "100", new VarToken().getToken(
                "VAR.Pos.NOSIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.Pos.INTVAL.NOSIGN", "100", new VarToken().getToken(
                "VAR.Pos.INTVAL.NOSIGN", getCharacter(), null));
        assertEquals("VAR.Pos.SIGN", "+100.35", new VarToken().getToken(
                "VAR.Pos.SIGN", getCharacter(), null));
        assertEquals("VAR.Pos.SIGN.INTVAL", "+100", new VarToken().getToken(
                "VAR.Pos.SIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.Pos.INTVAL.SIGN", "+100", new VarToken().getToken(
                "VAR.Pos.INTVAL.SIGN", getCharacter(), null));
    }

    /**
     * Test the output for negative numbers with fractions.
     */
    @Test
    public void testNegativeFractOutput()
    {
        assertEquals("VAR.Neg", "-555.55", new VarToken().getToken("VAR.Neg",
                getCharacter(), null));
        assertEquals("VAR.Neg.INTVAL", "-555", new VarToken().getToken(
                "VAR.Neg.INTVAL", getCharacter(), null));
        assertEquals("VAR.Neg.NOSIGN", "-555.55", new VarToken().getToken(
                "VAR.Neg.NOSIGN", getCharacter(), null));
        assertEquals("VAR.Neg.NOSIGN.INTVAL", "-555", new VarToken().getToken(
                "VAR.Neg.NOSIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.Neg.INTVAL.NOSIGN", "-555", new VarToken().getToken(
                "VAR.Neg.INTVAL.NOSIGN", getCharacter(), null));
        assertEquals("VAR.Neg.SIGN", "-555.55", new VarToken().getToken(
                "VAR.Neg.SIGN", getCharacter(), null));
        assertEquals("VAR.Neg.SIGN.INTVAL", "-555", new VarToken().getToken(
                "VAR.Neg.SIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.Neg.INTVAL.SIGN", "-555", new VarToken().getToken(
                "VAR.Neg.INTVAL.SIGN", getCharacter(), null));
    }

    /**
     * Test the output for positive numbers without fractions.
     */
    @Test
    public void testPositiveIntOutput()
    {
        assertEquals("VAR.PosInt", "105.0", new VarToken().getToken(
                "VAR.PosInt", getCharacter(), null));
        assertEquals("VAR.PosInt.INTVAL", "105", new VarToken().getToken(
                "VAR.PosInt.INTVAL", getCharacter(), null));
        assertEquals("VAR.PosInt.NOSIGN", "105.0", new VarToken().getToken(
                "VAR.PosInt.NOSIGN", getCharacter(), null));
        assertEquals("VAR.PosInt.NOSIGN.INTVAL", "105", new VarToken()
                .getToken("VAR.PosInt.NOSIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.PosInt.INTVAL.NOSIGN", "105", new VarToken()
                .getToken("VAR.PosInt.INTVAL.NOSIGN", getCharacter(), null));
        assertEquals("VAR.PosInt.SIGN", "+105.0", new VarToken().getToken(
                "VAR.PosInt.SIGN", getCharacter(), null));
        assertEquals("VAR.PosInt.SIGN.INTVAL", "+105", new VarToken().getToken(
                "VAR.PosInt.SIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.PosInt.INTVAL.SIGN", "+105", new VarToken().getToken(
                "VAR.PosInt.INTVAL.SIGN", getCharacter(), null));
    }

    /**
     * Test the output for negative numbers without fractions.
     */
    @Test
    public void testNegativeIntOutput()
    {
        assertEquals("VAR.NegInt", "-560.0", new VarToken().getToken(
                "VAR.NegInt", getCharacter(), null));
        assertEquals("VAR.NegInt.INTVAL", "-560", new VarToken().getToken(
                "VAR.NegInt.INTVAL", getCharacter(), null));
        assertEquals("VAR.NegInt.NOSIGN", "-560.0", new VarToken().getToken(
                "VAR.NegInt.NOSIGN", getCharacter(), null));
        assertEquals("VAR.NegInt.NOSIGN.INTVAL", "-560", new VarToken()
                .getToken("VAR.NegInt.NOSIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.NegInt.INTVAL.NOSIGN", "-560", new VarToken()
                .getToken("VAR.NegInt.INTVAL.NOSIGN", getCharacter(), null));
        assertEquals("VAR.NegInt.SIGN", "-560.0", new VarToken().getToken(
                "VAR.NegInt.SIGN", getCharacter(), null));
        assertEquals("VAR.NegInt.SIGN.INTVAL", "-560", new VarToken().getToken(
                "VAR.NegInt.SIGN.INTVAL", getCharacter(), null));
        assertEquals("VAR.NegInt.INTVAL.SIGN", "-560", new VarToken().getToken(
                "VAR.NegInt.INTVAL.SIGN", getCharacter(), null));
    }

}
