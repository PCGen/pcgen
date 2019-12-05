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
 * Created 21-Sep-2008 01:22:27
 */

package pcgen.core.term;

public interface TermEvaluatorBuilder
{
    String getTermConstructorPattern();

    String[] getTermConstructorKeys();

    boolean isEntireTerm();

    /**
     * @param expressionString the term being processed
     * @param src              the source (class, race, etc. ) of the formula that this term is from
     * @param matchedSection   The portion at the start of expressionString that matched the term's pattern
     * @return a term evaluator
     * @throws TermEvaulatorException If the term does not parse properly, this error is thrown
     */
    TermEvaluator getTermEvaluator(String expressionString, String src, String matchedSection)
            throws TermEvaulatorException;
}
