/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.gamemode.rollmethod;

import pcgen.cdom.content.RollMethod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class MethodToken extends AbstractNonEmptyToken<RollMethod>
        implements CDOMPrimaryToken<RollMethod>, DeferredToken<RollMethod>
{

    @Override
    public String getTokenName()
    {
        return "METHOD";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, RollMethod rm, String value)
    {
        rm.setMethodRoll(value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, RollMethod rm)
    {
        return new String[]{rm.getMethodRoll()};
    }

    @Override
    public Class<RollMethod> getTokenClass()
    {
        return RollMethod.class;
    }

    @Override
    public Class<RollMethod> getDeferredTokenClass()
    {
        return RollMethod.class;
    }

    @Override
    public boolean process(LoadContext context, RollMethod rm)
    {
        String method = rm.getMethodRoll();
        if ((method == null) || method.isEmpty())
        {
            Logging.errorPrint("Roll Method " + rm.getDisplayName() + " did not have a Method in " + rm.getSourceURI());
            return false;
        }
        return true;
    }
}
