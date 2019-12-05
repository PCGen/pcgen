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

package plugin.lsttokens.kit.clazz;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.SubClass;
import pcgen.core.kit.KitClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * parses SUBCLASS token for Kit Class
 */
public class SubclassToken extends AbstractNonEmptyToken<KitClass> implements CDOMPrimaryToken<KitClass>
{

    /**
     * Gets the name of the tag this class will parse.
     *
     * @return Name of the tag this class handles
     */
    @Override
    public String getTokenName()
    {
        return "SUBCLASS";
    }

    @Override
    public Class<KitClass> getTokenClass()
    {
        return KitClass.class;
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, KitClass kitClass, String value)
    {
        /*
         * This call to kitClass.getPcclass() is safe, as the line is CLASS: and
         * thus the CLASS: token is always encountered first
         */
        SubClassCategory category = SubClassCategory.getConstant(kitClass.getPcclass().getLSTformat(false));
        CDOMReference<SubClass> sc = context.getReferenceContext().getManufacturerId(category).getReference(value);
        kitClass.setSubClass(sc);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, KitClass kitClass)
    {
        CDOMReference<SubClass> ref = kitClass.getSubClass();
        if (ref == null)
        {
            return null;
        }
        return new String[]{ref.getLSTformat(false)};
    }
}
