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

package plugin.lsttokens.kit.gear;

import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.SizeAdjustment;
import pcgen.core.kit.KitGear;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * SIZE token for KitGear
 */
public class SizeToken extends AbstractNonEmptyToken<KitGear> implements CDOMPrimaryToken<KitGear>
{
    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "SIZE";
    }

    @Override
    public Class<KitGear> getTokenClass()
    {
        return KitGear.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, KitGear kitGear, String value)
    {
        if ("PC".equals(value))
        {
            kitGear.setSizeToPC(true);
        } else
        {
            CDOMSingleRef<SizeAdjustment> size =
                    context.getReferenceContext().getCDOMReference(SizeAdjustment.class, value);
            kitGear.setSize(size);
        }
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitGear kitGear)
    {
        CDOMSingleRef<SizeAdjustment> sz = kitGear.getSize();
        if (sz == null)
        {
            Boolean b = kitGear.getSizeToPC();
            if (b == null)
            {
                return null;
            }
            return new String[]{"PC"};
        }
        return new String[]{sz.getLSTformat(false)};
    }
}
