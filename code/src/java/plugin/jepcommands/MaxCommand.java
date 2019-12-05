package plugin.jepcommands;

import java.util.Stack;

import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

/**
 * Deal with max() JEP command eg. {@literal max(12.6, 20) --> 20}
 */
public class MaxCommand extends PCGenCommand
{
    /**
     * <p>
     * Initializes the number of parameters to = -1, indicating a variable
     * number of parameters.
     * </p>
     */
    public MaxCommand()
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
        return "MAX";
    }

    /**
     * <p>
     * Calculates the maximum of the parameters on the stack, all of which
     * are assumed to be of type double.
     * </p>
     *
     * @param stack Stack of incoming arguments.
     * @throws ParseException
     */
    @SuppressWarnings("unchecked") //Uses JEP, which doesn't use generics
    @Override
    public void run(final Stack stack) throws ParseException
    {
        // Check if stack is null
        if (null == stack)
        {
            throw new ParseException("Stack argument null");
        }

        double result = 0;
        boolean first = true;
        int i = 0;

        // repeat summation for each one of the current parameters
        while (i < curNumberOfParameters)
        {
            // get the parameter from the stack
            final Object param = stack.pop();
            if (param instanceof Number)
            {
                // calculate the result
                if (first || ((Number) param).doubleValue() > result)
                {
                    result = ((Number) param).doubleValue();
                }
            } else
            {
                throw new ParseException("Invalid parameter type");
            }
            first = false;

            i++;
        }

        // push the result on the inStack
        stack.push(result);
    }
}
