/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package plugin.jepcommands;

import java.util.Stack;

import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

/**
 * JEP command for get vars
 * <p>
 * eg. mastervar("CL=Fighter")
 */
public class MasterVarCommand extends PCGenCommand
{

    /**
     * Constructor
     */
    public MasterVarCommand()
    {
        //We say variable here and enforce below like our other JEP commands
        //Not sure why exactly, but not messing with the black box
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
        return "MASTERVAR";
    }

    /**
     * Runs mastervar on the inStack. The parameter is popped
     * off the {@code inStack}, and the variable's value is
     * pushed back to the top of {@code inStack}.
     *
     * @param inStack the jep stack
     * @throws ParseException
     */
    @SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
    @Override
    public void run(final Stack inStack) throws ParseException
    {
        // check the stack
        checkStack(inStack);

        // get the parameter from the stack
        final Object param1;

        if (curNumberOfParameters == 1)
        {
            param1 = inStack.pop();
        } else
        {
            throw new ParseException("Invalid parameter count");
        }

        if (param1 instanceof String)
        {
            Float result = null;
            PlayerCharacter pc = getPC();
            if (pc == null)
            {
                throw new ParseException("Invalid parent for function");
            }
            PlayerCharacter master = pc.getMasterPC();
            if (master == null)
            {
                throw new ParseException("Invalid: PC had no master");
            }
            result = master.getVariableValue((String) param1, variableSource);

            if (result == null)
            {
                throw new ParseException("Error retreiving variable:" + param1);
            }

            inStack.push(result.doubleValue());
        } else
        {
            throw new ParseException("Invalid parameter type");
        }
    }

    /**
     * Get the PC that will be used to determine the master
     *
     * @return the pc
     */
    private PlayerCharacter getPC()
    {
        PlayerCharacter pc = null;
        if (parent instanceof VariableProcessor)
        {
            pc = ((VariableProcessor) parent).getPc();
        } else if (parent instanceof PlayerCharacter)
        {
            pc = (PlayerCharacter) parent;
        }
        return pc;
    }
}
