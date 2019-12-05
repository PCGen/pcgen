/*
 * VariableProcessorgetPc().java
 * Copyright 2004 (C) Chris Ward <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package pcgen.core;

import pcgen.core.character.CharacterSpell;
import pcgen.core.term.EvaluatorFactory;
import pcgen.core.term.TermEvaluator;

/**
 * {@code VariableProcessorPC} is a processor for variables
 * associated with a character. This class converts formulas or
 * variables into values and is used extensively both in
 * definitions of objects and for output to output sheets.
 */
public class VariableProcessorPC extends VariableProcessor
{

    /**
     * Create a new VariableProcessorPC instance for the character.
     *
     * @param pc The character to be processed.
     */
    public VariableProcessorPC(PlayerCharacter pc)
    {
        super(pc);
    }

    /**
     * Retrieve a pre-coded variable for a PC. These are known
     * properties of all PCs.
     *
     * @param aSpell    This is specifically to compute bonuses to CASTERLEVEL
     *                  for a specific spell.
     * @param valString The variable to be evaluated
     * @param src       The source within which the variable is evaluated
     * @return The value of the variable
     */

    @Override
    Float getInternalVariable(final CharacterSpell aSpell, String valString, final String src)
    {
        TermEvaluator evaluator = EvaluatorFactory.PC.getTermEvaluator(valString, src);

        return (evaluator == null) ? null : evaluator.resolve(pc, aSpell);
    }

}
