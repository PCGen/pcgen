package plugin.lsttokens.pcclass.level;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DONOTADD Token
 */
public class DonotaddToken extends AbstractTokenWithSeparator<PCClassLevel> implements CDOMPrimaryToken<PCClassLevel>
{
    @Override
    public String getTokenName()
    {
        return "DONOTADD";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClassLevel po, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            if ("HITDIE".equals(tokText))
            {
                context.getObjectContext().put(po, ObjectKey.DONTADD_HITDIE, Boolean.TRUE);
            } else if ("SKILLPOINTS".equals(tokText))
            {
                context.getObjectContext().put(po, ObjectKey.DONTADD_SKILLPOINTS, Boolean.TRUE);
            } else
            {
                ComplexParseResult pr = new ComplexParseResult();
                pr.addErrorMessage(getTokenName() + " encountered an invalid 'Do Not Add' type: " + value);
                pr.addErrorMessage("  Legal values are: HITDIE, SKILLPOINTS");
                return pr;
            }
        }

        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClassLevel po)
    {
        Boolean increaseHitDice = context.getObjectContext().getObject(po, ObjectKey.DONTADD_HITDIE);
        Boolean increaseSkills = context.getObjectContext().getObject(po, ObjectKey.DONTADD_SKILLPOINTS);

        if ((increaseHitDice == null) && (increaseSkills == null))
        {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        if (increaseHitDice != null)
        {
            sb.append("HITDIE");
        }

        if (increaseSkills != null)
        {
            if (increaseHitDice != null)
            {
                sb.append(Constants.PIPE);
            }
            sb.append("SKILLPOINTS");
        }

        return new String[]{sb.toString()};
    }

    @Override
    public Class<PCClassLevel> getTokenClass()
    {
        return PCClassLevel.class;
    }
}
