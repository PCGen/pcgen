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

package plugin.lsttokens.kit.prof;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.WeaponProf;
import pcgen.core.kit.KitProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * PROF Token part of Kit Prof Lst Token
 */
public class ProfToken extends AbstractTokenWithSeparator<KitProf> implements CDOMPrimaryToken<KitProf>
{
    private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "PROF";
    }

    @Override
    public Class<KitProf> getTokenClass()
    {
        return KitProf.class;
    }

    @Override
    protected char separator()
    {
        return '|';
    }

    @Override
    protected ParseResult parseTokenWithSeparator(LoadContext context, KitProf obj, String value)
    {
        StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
        while (tok.hasMoreTokens())
        {
            String tokText = tok.nextToken();
            CDOMSingleRef<WeaponProf> ref = context.getReferenceContext().getCDOMReference(WEAPONPROF_CLASS, tokText);
            obj.addProficiency(ref);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitProf obj)
    {
        Collection<CDOMSingleRef<WeaponProf>> ref = obj.getProficiencies();
        if (ref == null || ref.isEmpty())
        {
            return null;
        }
        return new String[]{ReferenceUtilities.joinLstFormat(ref, Constants.PIPE)};
    }
}
