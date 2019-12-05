package plugin.jepcommands;

import java.util.Stack;

import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

/**
 * <p>
 * If class; extends PostfixMathCommand. This class accepts three agruments.
 * The first is a number interpreted as a boolean. The other two may be any
 * supported classes. If the first argument != 0, the second argument is
 * returned. Otherwise, the third argument is returned.
 * </p>
 * <p>
 * So, given if(x,y,z), y is returned if x != 0, and z is returned
 * otherise.
 * </p>
 */
public class IfCommand extends PCGenCommand
{
    /**
     * <p>
     * Initializes the number of parameters to = 3, indicating three number
     * of parameters.
     * </p>
     */
    public IfCommand()
    {
        super();
        numberOfParameters = 3;
    }

    /**
     * Gets the name of the function handled by this class.
     *
     * @return The name of the function.
     */
    @Override
    public String getFunctionName()
    {
        return "IF";
    }

    /**
     * <p>
     * Evaluates the three parameters. The first may be a subclass of
     * Number, or a Boolean. The second and third can be any supported type.
     * If the first argument is true, the second argument is returned;
     * otherwise, the third argument is returned.
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

        final boolean condition;

        final Object param3 = stack.pop();
        final Object param2 = stack.pop();
        final Object param1 = stack.pop();

        if (param1 instanceof Number)
        {
            condition = (((Number) param1).doubleValue() != 0.0d);
        } else if (param1 instanceof Boolean)
        {
            condition = (Boolean) param1;
        } else
        {
            throw new ParseException("Invalid parameter type for Parameter 1");
        }

        // push the result on the inStack
        stack.push(condition ? param2 : param3);
    }
}
