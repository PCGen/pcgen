/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 09-Aug-2008 11:44:20
 */

package pcgen.core.term;

import java.util.regex.Pattern;

import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.io.exporttoken.EqTypeToken;
import pcgen.io.exporttoken.Token;

public class PCEqTypeTermEvaluator extends BasePCTermEvaluator implements TermEvaluator
{
    private static final String DIGITS = "(\\p{Digit}+)";
    private static final String HEXDIGITS = "(\\p{XDigit}+)";
    // an exponent is 'e' or 'E' followed by an optionally
    // signed decimal integer.
    private static final String EXP = "[eE][+-]?" + DIGITS;
    private static final String FP_REGEX = ("[\\x00-\\x20]*" // Optional leading "whitespace"
            + "[+-]?(" // Optional sign character
            + "NaN|" // "NaN" string
            + "Infinity|" // "Infinity" string

            // A decimal floating-point string representing a finite positive
            // number without a leading sign has at most five basic pieces:
            // Digits . Digits ExponentPart FloatTypeSuffix
            //
            // Since this method allows integer-only strings as input
            // in addition to strings of floating-point literals, the
            // two sub-patterns below are simplifications of the grammar
            // productions from the Java Language Specification, 2nd
            // edition, section 3.10.2.

            // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
            + "(((" + DIGITS + "(\\.)?(" + DIGITS + "?)(" + EXP + ")?)|"

            // . Digits ExponentPart_opt FloatTypeSuffix_opt
            + "(\\.(" + DIGITS + ")(" + EXP + ")?)|"

            // Hexadecimal strings
            + "(("
            // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
            + "(0[xX]" + HEXDIGITS + "(\\.)?)|"

            // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
            + "(0[xX]" + HEXDIGITS + "?(\\.)" + HEXDIGITS + ")"

            + ")[pP][+-]?" + DIGITS + "))" + "[fFdD]?))" + "[\\x00-\\x20]*"); // Optional trailing "whitespace"

    public PCEqTypeTermEvaluator(String originalText)
    {
        this.originalText = originalText;
    }

    @Override
    public Float resolve(PlayerCharacter pc)
    {
        final String sTok = evaluate(pc);

        if (Pattern.matches(FP_REGEX, sTok))
        {
            return TermUtil.convertToFloat(originalText, sTok);
        }

        return 0.0f;
    }

    @Override
    public String evaluate(PlayerCharacter pc)
    {
        final Token token = new EqTypeToken();
        return token.getToken(originalText, pc, null);
    }

    @Override
    public String evaluate(PlayerCharacter pc, Spell aSpell)
    {
        return evaluate(pc);
    }

    @Override
    public boolean isSourceDependant()
    {
        return false;
    }

    public boolean isStatic()
    {
        return false;
    }
}
