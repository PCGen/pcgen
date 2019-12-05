/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2006 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.paper;

import pcgen.core.PaperInfo;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * {@code HeightToken}
 */
public class HeightToken extends AbstractNonEmptyToken<PaperInfo> implements CDOMPrimaryToken<PaperInfo>
{

    @Override
    public String getTokenName()
    {
        return "HEIGHT";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, PaperInfo pi, String value)
    {
        pi.setPaperInfo(PaperInfo.HEIGHT, value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, PaperInfo pi)
    {
        String info = pi.getPaperInfo(PaperInfo.HEIGHT);
        if (info == null)
        {
            // Probably an error
            return null;
        }
        return new String[]{info};
    }

    @Override
    public Class<PaperInfo> getTokenClass()
    {
        return PaperInfo.class;
    }
}
