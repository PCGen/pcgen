package plugin.jepcommands;

import java.util.Stack;

import pcgen.util.PCGenCommand;
import pcgen.util.PJEP;

import org.nfunk.jep.ParseException;

/**
 * eg. roll("10+d10")
 */
public class RollCommand extends PCGenCommand
{

    /**
     * Constructor
     */
    public RollCommand()
    {
        numberOfParameters = 1;
    }

    /**
     * Gets the name of the function handled by this class.
     *
     * @return The name of the function.
     */
    @Override
    public String getFunctionName()
    {
        return "ROLL";
    }

    @Override
    public boolean updateVariables(final PJEP jep)
    {
        return false;
    }

    /**
     * Is this command cacheable?
     *
     * @return true if cacheable, false if not.
     */
    @Override
    public boolean getCachable()
    {
        return false;
    }

    /**
     * Runs getvar on the inStack. The parameter is popped
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

        //
        // have to do this in reverse order...this is a stack afterall
        //
        final Object param1 = inStack.pop();

        if (param1 instanceof String)
        {
            final Object result = pcgen.core.RollingMethods.roll((String) param1);
            inStack.push(result);
        } else
        {
            throw new ParseException("Invalid parameter type");
        }
    }
}
