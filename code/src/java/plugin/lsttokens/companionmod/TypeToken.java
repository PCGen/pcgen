/*
 * Copyright 2012 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.companionmod;

import java.util.Collection;

import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class TypeToken extends AbstractNonEmptyToken<CompanionMod> implements CDOMPrimaryToken<CompanionMod>
{

    @Override
    public String getTokenName()
    {
        return "TYPE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CompanionMod mod, String value)
    {
        //TODO Check for "." and warn?
        Type type = Type.getConstant(value);
        context.getObjectContext().addToList(mod, ListKey.TYPE, type);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, CompanionMod mod)
    {
        Changes<Type> changes = context.getObjectContext().getListChanges(mod, ListKey.TYPE);
        if (changes == null || changes.isEmpty())
        {
            return null;
        }
        Collection<?> added = changes.getAdded();
        if (added != null && !added.isEmpty())
        {
            if (added.size() == 1)
            {
                return new String[]{added.iterator().next().toString()};
            }
            Logging.errorPrint("CompanionMod " + mod.getKeyName() + " had more than one TYPE specified.  "
                    + "A single TYPE is required for a CompanionMod.");
        }
        return null;
    }

    @Override
    public Class<CompanionMod> getTokenClass()
    {
        return CompanionMod.class;
    }
}
