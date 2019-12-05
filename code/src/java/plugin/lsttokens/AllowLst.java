package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.inst.NEPFormula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.InfoBoolean;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Processes the ALLOW token, which is the "new formula token" for Prerequisites.
 * This is designed to control ONLY situations at a user selection - it does not do
 * ongoing enforcement. For ongoing enforcement, ENABLE is used.
 */
public class AllowLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "ALLOW";
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting '|', format is: InfoName|Formula value was: " + value);
        }
        String infoName = value.substring(0, pipeLoc);
        String formulaString = value.substring(pipeLoc + 1);
        NEPFormula<Boolean> formula = context.getValidFormula(FormatUtilities.BOOLEAN_MANAGER, formulaString);
        obj.addToListFor(ListKey.ALLOW, new InfoBoolean(infoName, formula));
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<InfoBoolean> changes = context.getObjectContext().getListChanges(obj, ListKey.ALLOW);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        List<String> items = new ArrayList<>();
        for (InfoBoolean info : changes.getAdded())
        {
            //This is correct - NEPFormula unparses to its instructions with toString()
            items.add(info.getInfoName() + Constants.PIPE + info.getFormula());
        }
        return items.toArray(new String[0]);
    }

    @Override
    protected char separator()
    {
        return '|';
    }
}
