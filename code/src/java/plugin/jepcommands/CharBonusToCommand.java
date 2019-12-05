package plugin.jepcommands;

import java.util.Stack;

import pcgen.core.PlayerCharacter;
import pcgen.core.VariableProcessor;
import pcgen.util.PCGenCommand;

import org.nfunk.jep.ParseException;

/**
 * Deals with JEP command for charbonusto
 * <p>
 * eg. charbonusto("PCLEVEL", "Wizard");
 * eg. charbonusto("Wizard");
 */
public class CharBonusToCommand extends PCGenCommand
{

    /**
     * Constructor
     */
    public CharBonusToCommand()
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
        return "CHARBONUSTO";
    }

    /**
     * Runs charbonusto on the inStack. The parameter is popped
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
        final Object param2;

        //
        // have to do this in reverse order...this is a stack afterall
        //
        if (curNumberOfParameters == 1)
        {
            param2 = inStack.pop();
            param1 = "PCLEVEL";
        } else if (curNumberOfParameters == 2)
        {
            param2 = inStack.pop();
            param1 = inStack.pop();
        } else
        {
            throw new ParseException("Invalid parameter count");
        }

        if ((param1 instanceof String) && (param2 instanceof String))
        {
            PlayerCharacter pc = null;
            if (parent instanceof VariableProcessor)
            {
                pc = ((VariableProcessor) parent).getPc();
            } else if (parent instanceof PlayerCharacter)
            {
                pc = (PlayerCharacter) parent;
            }
            if (pc == null)
            {
                throw new ParseException("Invalid parent (no PC): " + parent.getClass().getName());
            }

            final Object result = pc.getTotalBonusTo((String) param1, (String) param2);

            inStack.push(result);
        } else
        {
            throw new ParseException("Invalid parameter type");
        }
    }
}
