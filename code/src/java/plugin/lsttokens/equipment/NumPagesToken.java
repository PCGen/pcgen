/*
 * NumPagesToken.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.core.Equipment;
import pcgen.rules.persistence.token.AbstractIntToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * {@code NumPagesToken} deals with NUMPAGES token
 * <p>
 * (Tue, 14 Mar 2006) $
 */
public class NumPagesToken extends AbstractIntToken<Equipment> implements CDOMPrimaryToken<Equipment>
{

    @Override
    public String getTokenName()
    {
        return "NUMPAGES";
    }

    @Override
    protected IntegerKey integerKey()
    {
        return IntegerKey.NUM_PAGES;
    }

    @Override
    protected int minValue()
    {
        return 1;
    }

    @Override
    public Class<Equipment> getTokenClass()
    {
        return Equipment.class;
    }
}
