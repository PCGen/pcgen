/*
 * IfCommand.java
 * Copyright 2007 (C) Andrew Wilson <nuance@sourceforge.net>
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

import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

/**
 * <p>
 * Or class; extends PostfixMathCommand. This class accepts two or more
 * agruments. Each may be a boolean or a number interpreted as a boolean.
 * Returns a logical or of the operands.
 * </p>
 * <p>
 * So, given or(x,y,z), x or y or z is returned
 * </p>
 */
public class OrCommand extends PCGenCommand
{

    public OrCommand()
    {
        super();
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
        return "OR";
    }

    /**
     * @param inStack Stack of incoming arguments.
     * @throws ParseException
     */
    @Override
    @SuppressWarnings({"unchecked"})
    //Uses JEP, which doesn't use generics
    public void run(final Stack inStack) throws ParseException
    {

        // check the stack
        checkStack(inStack);

        // Check if stack is null
        if (null == inStack)
        {
            throw new ParseException("Stack argument null");
        }

        final Stack newStack = new Stack();
        int paramCount = curNumberOfParameters;

        while (paramCount > 0)
        {
            paramCount--;
            newStack.push(inStack.pop());
        }

        int paramCount1 = curNumberOfParameters;
        Object result = 0.0;

        while (paramCount1 > 0)
        {
            paramCount1--;

            final Object operand = newStack.pop();

            // If we're haven't found a true value yet
            if (operand instanceof Number)
            {
                if (((Number) operand).doubleValue() != 0.0d)
                {
                    result = operand;
                    break;
                }
            } else if (operand instanceof Boolean)
            {
                if ((Boolean) operand)
                {
                    result = operand;
                    break;
                }
            } else
            {
                throw new ParseException("Invalid parameter type for: " + operand);
            }
        }

        // finally, put back the result
        inStack.push(result);
    }
}
