/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.deprecated;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCStat;
import pcgen.persistence.lst.DeprecatedToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with PENALTYVAR Token
 */
public class PenaltyvarToken implements CDOMCompatibilityToken<PCStat>, DeprecatedToken
{

    @Override
    public String getTokenName()
    {
        return "PENALTYVAR";
    }

    @Override
    public ParseResult parseToken(LoadContext context, PCStat obj, String value)
    {
        return new ParseResult.Fail(getMessage(obj, value));
    }

    @Override
    public Class<PCStat> getTokenClass()
    {
        return PCStat.class;
    }

    @Override
    public int compatibilityLevel()
    {
        return 6;
    }

    @Override
    public int compatibilitySubLevel()
    {
        return 2;
    }

    @Override
    public int compatibilityPriority()
    {
        return 2;
    }

    @Override
    public String getMessage(CDOMObject obj, String value)
    {
        return getTokenName() + " is no longer supported";
    }
}
