/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence.token;

import java.util.Collection;

import pcgen.base.lang.UnreachableError;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import org.apache.xml.utils.XMLChar;

public abstract class AbstractToken
{
    private final PreParserFactory prereqParser;

    protected AbstractToken()
    {
        try
        {
            prereqParser = PreParserFactory.getInstance();
        } catch (PersistenceLayerException ple)
        {
            Logging.errorPrint("Error Initializing PreParserFactory");
            Logging.errorPrint("  " + ple.getMessage(), ple);
            throw new UnreachableError(ple);
        }
    }

    protected boolean looksLikeAPrerequisite(String activeValue)
    {
        return (activeValue.startsWith("PRE") || activeValue.startsWith("!PRE")) && activeValue.contains(":");
    }

    protected Prerequisite getPrerequisite(String token)
    {
        /*
         * CONSIDER Need to add a Key, Value method to getPrerequisite and to
         * .parse in the PRE_PARSER
         */
        try
        {
            return prereqParser.parse(token);
        } catch (PersistenceLayerException ple)
        {
            Logging.addParseMessage(Logging.LST_ERROR,
                    "Error parsing Prerequisite in " + getTokenName() + ": " + token + "\n  " + ple.getMessage());
        }
        return null;
    }

    /**
     * Checks a string to see if it is non-empty and any separators are used correctly.
     *
     * @param separator The separator that is used in the string.
     * @param value     The string to check.
     * @return A parse result of success if the string is non-empty uses separators correctly.
     */
    protected ParseResult checkSeparatorsAndNonEmpty(char separator, String value)
    {
        ParseResult pr = checkNonEmpty(value);
        if (pr.passed())
        {
            pr = checkForIllegalSeparator(separator, value);
        }
        return pr;
    }

    /**
     * Checks a string to see if any separators are used correctly.
     *
     * @param separator The separator that is used in the string.
     * @param value     The string to check.
     * @return A parse result of success if the string uses separators correctly.
     */
    protected ParseResult checkForIllegalSeparator(char separator, String value)
    {
        if (value.charAt(0) == separator)
        {
            return new ParseResult.Fail(getTokenName() + " arguments may not start with " + separator + " : " + value);
        }
        if (value.charAt(value.length() - 1) == separator)
        {
            return new ParseResult.Fail(getTokenName() + " arguments may not end with " + separator + " : " + value);
        }
        if (value.contains(String.valueOf(new char[]{separator, separator})))
        {
            return new ParseResult.Fail(
                    getTokenName() + " arguments uses double separator " + separator + separator + " : " + value);
        }
        return ParseResult.SUCCESS;
    }

    /**
     * Checks that a string is non-empty.
     *
     * @param value The string to check.
     * @return A parse result of success if the string in non-empty.
     */
    protected ParseResult checkNonEmpty(String value)
    {
        if (value == null)
        {
            return new ParseResult.Fail(getTokenName() + " may not have null argument");
        }
        if (value.isEmpty())
        {
            return new ParseResult.Fail(getTokenName() + " may not have empty argument");
        }
        return ParseResult.SUCCESS;
    }

    /**
     * Checks a string to see if any characters are invalid for inclusion in
     * XML strings.
     *
     * @param value The string to check.
     * @return A parse result of success if the string uses only valid characters.
     */
    protected ParseResult checkForInvalidXMLChars(String value)
    {
        for (char character : value.toCharArray())
        {
            if (!XMLChar.isValid(character))
            {
                return new ParseResult.Fail(
                        "Invalid XML character 0x" + Integer.toString(character, 16) + " in " + value);
            }
        }

        return ParseResult.SUCCESS;
    }

    /**
     * Return the token name
     */
    public abstract String getTokenName();

    protected String getPrerequisiteString(LoadContext context, Collection<Prerequisite> prereqs)
    {
        return context.getPrerequisiteString(prereqs);
    }
}
