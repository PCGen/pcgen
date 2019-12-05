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
package plugin.lsttokens.pcclass;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Deity;
import pcgen.core.PCClass;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with DEITY Token
 */
public class DeityToken extends AbstractTokenWithSeparator<PCClass> implements CDOMPrimaryToken<PCClass>
{

    private static final Class<Deity> DEITY_CLASS = Deity.class;

    @Override
    public String getTokenName()
    {
        return "DEITY";
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, PCClass pcc, String value)
    {
        context.getObjectContext().removeList(pcc, ListKey.DEITY);

        final StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            CDOMReference<Deity> deity = context.getReferenceContext().getCDOMReference(DEITY_CLASS, tokText);
            context.getObjectContext().addToList(pcc, ListKey.DEITY, deity);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PCClass pcc)
    {
        Changes<CDOMReference<Deity>> changes = context.getObjectContext().getListChanges(pcc, ListKey.DEITY);
        Collection<CDOMReference<Deity>> added = changes.getAdded();
        if (added == null || added.isEmpty())
        {
            // Zero indicates no Token
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(added, Constants.PIPE)};
    }

    @Override
    public Class<PCClass> getTokenClass()
    {
        return PCClass.class;
    }
}
