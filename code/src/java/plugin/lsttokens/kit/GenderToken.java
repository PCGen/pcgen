/*
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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

package plugin.lsttokens.kit;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.Gender;
import pcgen.core.kit.KitBio;
import pcgen.output.channel.compat.GenderCompat;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * GENDER token for Kits
 */
public class GenderToken extends AbstractTokenWithSeparator<KitBio> implements CDOMPrimaryToken<KitBio>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "GENDER";
    }

    @Override
    public Class<KitBio> getTokenClass()
    {
        return KitBio.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitBio kitGender, String value)
    {
        StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
        while (st.hasMoreTokens())
        {
            kitGender.addGender(GenderCompat.getGenderReference(context, st.nextToken()));
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitBio kitGender)
    {
        Collection<Indirect<Gender>> genders = kitGender.getGenders();
        if (genders == null)
        {
            return null;
        }
        String[] g = new String[genders.size()];
        int i = 0;
        for (Indirect<Gender> genderIndirect : genders)
        {
            g[i++] = genderIndirect.get().name();
        }
        return new String[]{StringUtil.join(g, Constants.PIPE)};
    }
}
