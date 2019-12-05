/*
 * CountDistinctCommand.java
 * Copyright 2013 (C) James Dempsey
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

package plugin.jepcommands;

import java.util.Stack;

import pcgen.core.PlayerCharacter;
import pcgen.util.AbstractCountCommand;
import pcgen.util.JepCountType;
import pcgen.util.Logging;

import org.nfunk.jep.ParseException;

/**
 * {@code CountDistinctCommand} deals with the count() JEP command. The first parameter will
 * be the type of object being counted and further parameters will specify the criteria.
 * <p>
 */
public class CountDistinctCommand extends AbstractCountCommand
{

    /**
     * Constructor.
     */
    public CountDistinctCommand()
    {
        numberOfParameters = -1;
    }

    /**
     * Gets the name of the function handled by this class.
     *
     * @return The name of the function.
     */
    @Override
    public String getFunctionName()
    {
        return "COUNTDISTINCT";
    }

    /**
     * Runs count on the inStack. The parameter is popped off the {@code inStack},
     * and the variable's value is pushed back to the top of {@code inStack}.
     *
     * @param inStack The jep stack that the count command will process
     * @throws ParseException
     */
    @Override
    @SuppressWarnings("unchecked")
    //Uses JEP, which doesn't use generics
    public void run(final Stack inStack) throws ParseException
    {
        // Grab the character under scrutiny
        final PlayerCharacter pc = getPC();

        if (pc == null)
        {
            throw new ParseException("Invalid parent (no PC): " + parent.getClass().getName());
        }

        // check the stack
        checkStack(inStack);

        if (1 <= curNumberOfParameters)
        {
            // move all but the first parameter from the stack into and array of Objects
            final Object[] params = paramStackToArray(inStack, curNumberOfParameters - 1);

            // retrieve the first Object, this should be a String which will map directly to
            // a JepCountDistinctEnum or JepCountEnum, this specifies the type of count to perform
            final Object toCount = inStack.pop();
            if (toCount instanceof String)
            {
                JepCountType countEnum = JepCountType.valueOf(toCount + "DISTINCT");
                if (countEnum == null)
                {
                    // Fall back to count
                    countEnum = JepCountType.valueOf((String) toCount);
                }
                if (countEnum == null)
                {
                    Logging.errorPrint("Unable to find count type: " + toCount);
                }
                final Double result = (Double) countEnum.count(pc, params);
                inStack.push(result);
            } else
            {
                throw new ParseException("Invalid parameter type");
            }
        } else
        {
            throw new ParseException("missing parameter, nothing to count");
        }
    }
}
