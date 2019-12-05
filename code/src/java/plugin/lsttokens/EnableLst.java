package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

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
 * Processes the ENABLE token, which is the "new formula token" for Requirements. This is
 * designed to control ONLY ongoing enforcement. It does not do enforcement at user
 * selection. For enforcement at user selection, ALLOW is used.
 */
public class EnableLst extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "ENABLE";
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
    {
        return new ParseResult.Fail("Not supported since it is not monitored in an ongoing fashion");
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Changes<InfoBoolean> changes = context.getObjectContext().getListChanges(obj, ListKey.ENABLE);
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
