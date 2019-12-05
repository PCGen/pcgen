/*
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
 */
package pcgen.core;

import pcgen.core.character.CharacterSpell;
import pcgen.core.term.EvaluatorFactory;
import pcgen.core.term.TermEvaluator;

/**
 * {@code VariableProcessorEq} is a processor for variables
 * associated with a character's equipment. This class converts
 * formulas or variables into values and is used extensively
 * both in definitions of objects and for output to output sheets.
 */
public class VariableProcessorEq extends VariableProcessor
{

    private final Equipment eq;
    private final boolean primaryHead;

    /**
     * Create a new VariableProcessorEq instance for an equipment item, and
     * pc. It also allows splitting of the processing of the heads of double
     * weapons.
     *
     * @param eq          The item of equipment  being processed.
     * @param pc          The player character being processed.
     * @param primaryHead Is this the primary head of a double weapon?
     */
    public VariableProcessorEq(Equipment eq, PlayerCharacter pc, boolean primaryHead)
    {
        super(pc);
        this.eq = eq;
        this.primaryHead = primaryHead;
    }

    /**
     * Retrieve a pre-coded variable for a piece of equipment. These are known
     * properties of all equipment items. If a value is not found for the
     * equipment item, a search will be made of the character.
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
        TermEvaluator evaluator = getTermEvaluator(valString, src);

        return evaluator == null ? null : evaluator.resolve(eq, primaryHead, pc);
    }

    TermEvaluator getTermEvaluator(String valString, String src)
    {
        TermEvaluator evaluator = EvaluatorFactory.EQ.getTermEvaluator(valString, src);

        if (evaluator == null)
        {
            return EvaluatorFactory.PC.getTermEvaluator(valString, src);
        }

        return evaluator;
    }
}
