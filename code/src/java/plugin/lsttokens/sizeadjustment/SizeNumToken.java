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
package plugin.lsttokens.sizeadjustment;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.PostValidationToken;
import pcgen.util.Logging;

/**
 * Class deals with LEGS Token
 */
public class SizeNumToken extends AbstractIntToken<SizeAdjustment>
        implements CDOMPrimaryToken<SizeAdjustment>, PostValidationToken<SizeAdjustment>
{

    @Override
    public String getTokenName()
    {
        return "SIZENUM";
    }

    @Override
    protected IntegerKey integerKey()
    {
        return IntegerKey.SIZENUM;
    }

    @Override
    protected int minValue()
    {
        return 0;
    }

    @Override
    public Class<SizeAdjustment> getTokenClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    public boolean process(LoadContext context, Collection<? extends SizeAdjustment> obj)
    {
        boolean returnValue = true;
        Map<Integer, SizeAdjustment> map = new TreeMap<>();
        for (SizeAdjustment sa : obj)
        {
            Integer sizenum = sa.get(IntegerKey.SIZENUM);
            if (sizenum == null)
            {
                Logging.errorPrint("Size: " + sa.getKeyName() + " did not have a SIZENUM (cannot be assumed)");
                returnValue = false;
                continue;
            }
            SizeAdjustment previous = map.put(sizenum, sa);
            if (previous != null)
            {
                Logging.errorPrint("Size: " + sa.getKeyName() + " and size: " + previous.getKeyName()
                        + " had identical SIZENUM: " + sizenum);
                returnValue = false;
            }
        }
        int order = 0;
        for (SizeAdjustment sa : map.values())
        {
            sa.put(IntegerKey.SIZEORDER, order++);
        }
        return returnValue;
    }

    @Override
    public Class<SizeAdjustment> getValidationTokenClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    public int getPriority()
    {
        return 1;
    }
}
