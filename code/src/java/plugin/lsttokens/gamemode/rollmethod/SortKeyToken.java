/*
 * Copyright (c) 2018 Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.gamemode.rollmethod;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import pcgen.cdom.content.RollMethod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

/**
 * Processes the SORTKEY token for RollMethod objects (Game Mode), loading it into the
 * SortKey field of the RollMethod.
 * <p>
 * Note: While the intent is the same, this is necessary as a separate token from the
 * "Global" SortKey since RollMethod does not extend CDOMObject.
 */
public class SortKeyToken extends AbstractNonEmptyToken<RollMethod>
        implements CDOMPrimaryToken<RollMethod>, PostValidationToken<RollMethod>
{

    @Override
    public String getTokenName()
    {
        return "SORTKEY";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, RollMethod rollMethod, String value)
    {
        rollMethod.setSortKey(value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, RollMethod rollMethod)
    {
        String info = rollMethod.getSortKey();
        if (info == null)
        {
            // Probably an error
            return null;
        }
        return new String[]{info};
    }

    @Override
    public Class<RollMethod> getTokenClass()
    {
        return RollMethod.class;
    }

    @Override
    public boolean process(LoadContext context, Collection<? extends RollMethod> c)
    {
        boolean returnValue = true;
        Map<String, RollMethod> keys = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        for (RollMethod rollMethod : c)
        {
            String keyName = rollMethod.getKeyName();
            if (keyName == null)
            {
                Logging.errorPrint("RollMethod: " + rollMethod.getDisplayName() + " requires a SortKey, but was null",
                        context);
                returnValue = false;
            } else if (keys.put(keyName, rollMethod) != null)
            {
                Logging.errorPrint("Found more than one RollMethod with (case insensitive) Sort Key: " + keyName,
                        context);
                returnValue = false;
            }
        }
        return returnValue;
    }

    @Override
    public Class<RollMethod> getValidationTokenClass()
    {
        return RollMethod.class;
    }

    @Override
    public int getPriority()
    {
        return 0;
    }
}
