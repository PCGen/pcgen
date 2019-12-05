/*
 * Copyright 2018 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.grouping;

import java.util.Arrays;
import java.util.Stack;

import pcgen.base.formula.base.LegalScope;
import pcgen.cdom.formula.scope.PCGenScope;

/**
 * A GroupingInfoFactory is designed to construct GroupingInfo objects given a PCGenScope
 * and the String of instructions for the grouping information.
 * <p>
 * Warning: A GroupingInfoFactory is very much NOT thread safe.
 */
public class GroupingInfoFactory
{
    /**
     * Any characters expected in the future parsing of the instructions.
     */
    private final Stack<String> expected = new Stack<>();

    /**
     * The current depth of grouping for the instructions being analyzed.
     */
    private int depth;

    /**
     * The scope name for the instructions being analyzed.
     */
    private String[] scopeName;

    /**
     * The GroupingTokenizer that will be used to separate the instructions.
     */
    private GroupingTokenizer fullTokenizer;

    /**
     * The Active GroupingInfo for the instructions being analyzed.
     */
    private GroupingInfo<?> activeInfo;

    /**
     * Processes the given scope and instructions to generate a GroupingInfo.
     *
     * @param scope        The PCGenScope in which the instructions will be analyzed
     * @param instructions The instructions for producing a GroupingInfo
     * @return A GroupingInfo derived from the given scope and instructions
     * @throws GroupingStateException If there is a problem in analysis of the scope and/or instructions
     */
    public GroupingInfo<?> process(PCGenScope scope, String instructions) throws GroupingStateException
    {
        String fullScopeName = LegalScope.getFullName(scope);
        this.scopeName = fullScopeName.split("\\.");
        depth = 0;
        expected.clear();
        fullTokenizer = new GroupingTokenizer(instructions);
        GroupingInfo<?> topInfo = new GroupingInfo<>();
        topInfo.setScope(scope);
        activeInfo = topInfo;
        consumeGrouping();
        if (fullTokenizer.hasNext())
        {
            throw new GroupingStateException("After " + fullTokenizer.getConsumed()
                    + " expected end of string, but had additional content: " + fullTokenizer.next());
        }
        return topInfo;
    }

    //Consumes a grouping (with potential child)
    private void consumeGrouping() throws GroupingStateException
    {
        if (!fullTokenizer.hasNext())
        {
            throw new GroupingStateException("Expected a Grouping, but string ended: " + fullTokenizer.getConsumed());
        }
        String item = fullTokenizer.next();
        if (isSeparator(item))
        {
            throw new GroupingStateException(
                    "Expected text, but " + item + " was found: " + fullTokenizer.getConsumed());
        }
        if (fullTokenizer.hasNext())
        {
            String next = fullTokenizer.peek();
            if ("=".equals(next))
            {
                activeInfo.setCharacteristic(item);
                //Skip the Equals
                fullTokenizer.next();
                consumeTarget();
                allowChild();
            } else if ("[".equals(next))
            {
                activeInfo.setValue(item);
                allowChild();
            } else if ("]".equals(next))
            {
                consumeCloseBracket();
            } else
            {
                if (expected.isEmpty())
                {
                    throw new GroupingStateException(
                            "Expected '=' or '[', but " + item + " was found: " + fullTokenizer.getConsumed());
                } else
                {
                    throw new GroupingStateException(
                            "Expected '=' or '[' or ']', but " + item + " was found: " + fullTokenizer.getConsumed());
                }
            }
        } else
        {
            //Could be simply "ALL" (so no additional tokens)
            activeInfo.setValue(item);
        }
    }

    //Consumes a target (item after '=')
    private void consumeTarget() throws GroupingStateException
    {
        if (!fullTokenizer.hasNext())
        {
            throw new GroupingStateException(
                    "Expected target after '=', but string ended: " + fullTokenizer.getConsumed());
        }
        String expectedTarget = fullTokenizer.next();
        if (isSeparator(expectedTarget))
        {
            throw new GroupingStateException(
                    "Expected target type, but " + expectedTarget + " was found: " + fullTokenizer.getConsumed());
        }
        activeInfo.setValue(expectedTarget);
    }

    //Allows and consumes a child, if present (is not required)
    private void allowChild() throws GroupingStateException
    {
        if (!fullTokenizer.hasNext())
        {
            //This is allow, not require
            return;
        }
        String expectedOpenBracket = fullTokenizer.next();
        if (!"[".equals(expectedOpenBracket))
        {
            throw new GroupingMismatchException("Expected '[' to start a child but found: " + expectedOpenBracket
                    + " in " + fullTokenizer.getConsumed());
        }
        expected.push("]");
        consumeChild();
    }

    //Consumes the child
    private void consumeChild() throws GroupingStateException
    {
        if (scopeName.length <= depth)
        {
            throw new GroupingStateException(
                    "Encountered a Child, but didn't have sufficient format: " + Arrays.asList(scopeName));
        }
        String expectedType = scopeName[depth++];
        GroupingInfo<?> newInfo = new GroupingInfo<>();
        activeInfo.setChild(newInfo);
        newInfo.setObjectType(expectedType);
        activeInfo = newInfo;
        consumeGrouping();
    }

    //Consumes the close bracket at the end of a grouping
    private void consumeCloseBracket() throws GroupingStateException
    {
        if (!fullTokenizer.hasNext())
        {
            throw new GroupingStateException("Expected a ']', but string ended: " + fullTokenizer.getConsumed());
        }
        String expectedCloseBracket = fullTokenizer.next();
        if (!"]".equals(expectedCloseBracket))
        {
            throw new GroupingMismatchException(
                    "Expected ']' but found: " + expectedCloseBracket + " in " + fullTokenizer.getConsumed());
        }
        if (expected.isEmpty())
        {
            throw new GroupingMismatchException(
                    "Did not have an open bracket before close: " + fullTokenizer.getConsumed());
        }
        String nextExpected = expected.pop();
        if (!"]".equals(nextExpected))
        {
            throw new GroupingMismatchException(
                    "Expected " + nextExpected + " but did not have matching brackets: " + fullTokenizer.getConsumed());
        }
        if (!expected.isEmpty())
        {
            consumeCloseBracket();
        }
    }

    //Indicates key separator characters in a grouping
    private boolean isSeparator(String item)
    {
        return "=".equals(item) || "[".equals(item) || "]".equals(item);
    }

    /**
     * An Exception indicating a problem in analyzing the instructions
     */
    public static class GroupingStateException extends Exception
    {

        /**
         * Constructs a new GroupingStateException with the given error message.
         *
         * @param message The message for the GroupingStateException
         */
        public GroupingStateException(String message)
        {
            super(message);
        }

    }

    /**
     * A GroupingMismatchException is a specific form of GroupingStateException that
     * indicates a mismatch in brackets when parsing the instructions.
     */
    public static class GroupingMismatchException extends GroupingStateException
    {

        /**
         * Constructs a new GroupingMismatchException with the given error message.
         *
         * @param message The message for the GroupingMismatchException
         */
        public GroupingMismatchException(String message)
        {
            super(message);
        }

    }
}
