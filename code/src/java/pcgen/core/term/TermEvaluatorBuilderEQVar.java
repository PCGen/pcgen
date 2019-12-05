/**
 * Copyright 2008 Andrew Wilson
 * <nuance@users.sourceforge.net>.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * <p>
 * Created 03 Oct 2008
 */

package pcgen.core.term;

/**
 * {@code EvaluatorFactoryEQVar}
 * <p>
 * This individual enumerations in this class are each responsible for making
 * and returning an object that implements the TermEvaluator interface.  Each
 * enumeration has a regular expression that matches one of the "hardcoded"
 * internal variables that every piece of equipment has a value for.  They also
 * have an array of string keys that enumerate every string that the regular
 * expression can match (this is not as bad as it sounds since each can only
 * match at most eight strings).  The array of string is used to populate a
 * {@code Map<String, Enum>}
 */

public enum TermEvaluatorBuilderEQVar implements TermEvaluatorBuilder
{

    COMPLETE_EQ_ALTPLUSTOTAL("ALTPLUSTOTAL", new String[]{"ALTPLUSTOTAL"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQAltPlusTotalTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_BASECOST("BASECOST", new String[]{"BASECOST"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQBaseCostTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_CRITMULT("CRITMULT", new String[]{"CRITMULT"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQCritMultTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_DMGDICE("DMGDICE", new String[]{"DMGDICE"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQDamageDiceTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_DMGDIE("DMGDIE", new String[]{"DMGDIE"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQDamageDieTermEvaluator(expressionString);
                }
            },
    COMPLETE_EQ_EQACCHECK("EQACCHECK", new String[]{"EQACCHECK"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQACCheckTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_EQHANDS("EQHANDS", new String[]{"EQHANDS"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQHandsTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_EQSPELLFAIL("EQSPELLFAIL", new String[]{"EQSPELLFAIL"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQSpellFailureTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_EQUIP_SIZE_INT("EQUIP\\.SIZE(?:\\.INT)?", new String[]{"EQUIP.SIZE.INT", "EQUIP.SIZE"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    if (matchedSection.length() == 14)
                    {
                        return new EQSizeTermEvaluator(expressionString);
                    } else
                    {
                        return new EQEquipSizeTermEvaluator(expressionString);
                    }
                }
            },

    COMPLETE_EQ_HEADPLUSTOTAL("HEADPLUSTOTAL", new String[]{"HEADPLUSTOTAL"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQHeadPlusTotalTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_PLUSTOTAL("PLUSTOTAL", new String[]{"PLUSTOTAL"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQPlusTotalTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_RANGE("RANGE", new String[]{"RANGE"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQRangeTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_REACHMULT("(?:RACEREACH|REACHMULT|REACH)", new String[]{"RACEREACH", "REACHMULT", "REACH"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    if ("RACEREACH".equals(expressionString))
                    {
                        return new EQRaceReachTermEvaluator(expressionString, src);
                    } else if ("REACHMULT".equals(expressionString))
                    {
                        return new EQReachMultTermEvaluator(expressionString);
                    } else if ("REACH".equals(expressionString))
                    {
                        return new EQReachTermEvaluator(expressionString);
                    }
                    return null;
                }
            },

    COMPLETE_EQ_SIZE("SIZE", new String[]{"SIZE"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQSizeTermEvaluator(expressionString);
                }
            },

    COMPLETE_EQ_WT("WT", new String[]{"WT"}, true)
            {
                @Override
                public TermEvaluator getTermEvaluator(final String expressionString, final String src,
                        final String matchedSection)
                {

                    return new EQWeightTermEvaluator(expressionString);
                }
            };

    private final String termConstructorPattern;
    private final String[] termConstructorKeys;
    private final boolean patternMatchesEntireTerm;

    TermEvaluatorBuilderEQVar(String pattern, String[] keys, boolean matchEntireTerm)
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
}
