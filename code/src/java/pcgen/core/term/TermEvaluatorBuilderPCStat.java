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
 * Created 21-Sep-2008 01:29:15
 */

package pcgen.core.term;

public class TermEvaluatorBuilderPCStat implements TermEvaluatorBuilder
{

    private final String termConstructorPattern;
    private final String[] termConstructorKeys;
    private final boolean patternMatchesEntireTerm;

    TermEvaluatorBuilderPCStat(String pattern, String[] keys, boolean matchEntireTerm)
    {
        termConstructorPattern = pattern;
        termConstructorKeys = keys;
        patternMatchesEntireTerm = matchEntireTerm;
    }

    @Override
    public String getTermConstructorPattern()
    {
        return termConstructorPattern;
    }

    @Override
    public String[] getTermConstructorKeys()
    {
        return termConstructorKeys;
    }

    @Override
    public boolean isEntireTerm()
    {
        return patternMatchesEntireTerm;
    }

    @Override
    public TermEvaluator getTermEvaluator(String expressionString, String src, String matchedSection)
    {
        if (expressionString.equals(matchedSection))
        {
            return new PCStatModTermEvaluator(expressionString, matchedSection);
        } else if ((matchedSection + ".BASE").equals(expressionString))
        {
            return new PCStatBaseTermEvaluator(expressionString, matchedSection);
        } else if (expressionString.substring(matchedSection.length()).startsWith("SCORE"))
        {
            if (expressionString.endsWith(".BASE"))
            {
                return new PCStatBaseTermEvaluator(expressionString, matchedSection);
            } else
            {
                return new PCStatTotalTermEvaluator(expressionString, matchedSection);
            }
        }

        // the string hapened to start with a Stat abbreviation, but it's not
        // a value we handle here.
        return null;
    }
}
