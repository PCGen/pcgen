/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package plugin.lsttokens.equipment;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.MapKey;
import pcgen.core.Equipment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with ACCHECK token
 */
public class QualityToken extends AbstractNonEmptyToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    @Override
    public String getTokenName()
    {
        return "QUALITY";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
    {
        int pipeLoc = value.indexOf(Constants.PIPE);
        if (pipeLoc == -1)
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting '|', format is: " + "QualityType|Quality value was: " + value);
        }
        if (pipeLoc != value.lastIndexOf(Constants.PIPE))
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting only one '|', " + "format is: QualityType|Quality value was: " + value);
        }
        String key = value.substring(0, pipeLoc);
        if (key.isEmpty())
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting non-empty type, " + "format is: QualityType|Quality value was: " + value);
        }
        String val = value.substring(pipeLoc + 1);
        if (val.isEmpty())
        {
            return new ParseResult.Fail(
                    getTokenName() + " expecting non-empty value, " + "format is: QualityType|Quality value was: " + value);
        }
        context.getObjectContext().put(eq, MapKey.QUALITY, key, val);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, Equipment eq)
    {
        MapChanges<String, String> changes = context.getObjectContext().getMapChanges(eq, MapKey.QUALITY);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Set<String> set = new TreeSet<>();
        Map<String, String> added = changes.getAdded();
        for (Map.Entry<String, String> me : added.entrySet())
        {
            set.add(me.getKey() + Constants.PIPE + me.getValue());
        }
        return set.toArray(new String[0]);
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }
}
