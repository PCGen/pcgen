/*
 * Copyright 2016 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence.token;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.rules.context.LoadContext;

public abstract class AbstractStringStoringToken<T extends CDOMObject> extends AbstractNonEmptyToken<T>
        implements CDOMPrimaryToken<T>
{

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, T cdo, String value)
    {
        context.getObjectContext().put(cdo, ObjectKey.getKeyFor(String.class, '*' + getTokenName()), value);
        return ParseResult.SUCCESS;
    }

    @Override
    public String[] unparse(LoadContext context, T cdo)
    {
        ObjectKey<String> ok = ObjectKey.getKeyFor(String.class, '*' + getTokenName());
        String value = context.getObjectContext().getObject(cdo, ok);
        if (value == null)
        {
            return null;
        }
        return new String[]{value};
    }
}
