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
package plugin.lsttokens.subclass;

import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.core.SubClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractIntegerTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class CostTokenTest extends AbstractIntegerTokenTestCase<SubClass>
{

    static CostToken token = new CostToken();
    static CDOMTokenLoader<SubClass> loader = new CDOMTokenLoader<>();

    @Override
    public Class<SubClass> getCDOMClass()
    {
        return SubClass.class;
    }

    @Override
    public CDOMLoader<SubClass> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<SubClass> getToken()
    {
        return token;
    }

    @Override
    public IntegerKey getIntegerKey()
    {
        return IntegerKey.COST;
    }

    @Override
    public boolean isNegativeAllowed()
    {
        return false;
    }

    @Override
    public boolean isZeroAllowed()
    {
        return true;
    }

    @Override
    public boolean isPositiveAllowed()
    {
        return true;
    }

    @Override
    protected SubClass get(LoadContext context, String name)
    {
        SubClassCategory scc = SubClassCategory.getConstant("SCC");
        SubClass sc = scc.newInstance();
        sc.setName(name);
        context.getReferenceContext().importObject(sc);
        return sc;
    }
}
