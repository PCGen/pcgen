
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.StringKey;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

public class SourcepageLst extends AbstractNonEmptyToken<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

    @Override
    public String getTokenName()
    {
        return "SOURCEPAGE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject cdo, String value)
    {
        if (Constants.LST_DOT_CLEAR.equals(value))
        {
            context.getObjectContext().remove(cdo, StringKey.SOURCE_PAGE);
        } else
        {
            context.getObjectContext().put(cdo, StringKey.SOURCE_PAGE, value);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject cdo)
    {
        String page = context.getObjectContext().getString(cdo, StringKey.SOURCE_PAGE);
        boolean removed = context.getObjectContext().wasRemoved(cdo, StringKey.SOURCE_PAGE);
        List<String> list = new ArrayList<>();
        if (removed)
        {
            list.add(Constants.LST_DOT_CLEAR);
        }
        if (page != null)
        {
            list.add(page);
        }
        if (list.isEmpty())
        {
            return null;
        }
        return list.toArray(new String[0]);
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }
}
