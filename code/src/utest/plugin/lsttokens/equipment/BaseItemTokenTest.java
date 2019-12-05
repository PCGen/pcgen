/*
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.equipment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractItemTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class BaseItemTokenTest extends
        AbstractItemTokenTestCase<Equipment, Equipment>
{

    static BaseitemToken token = new BaseitemToken();
    static CDOMTokenLoader<Equipment> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Equipment> getCDOMClass()
    {
        return Equipment.class;
    }

    @Override
    public CDOMLoader<Equipment> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Equipment> getToken()
    {
        return token;
    }

    @Override
    public Class<Equipment> getTargetClass()
    {
        return Equipment.class;
    }

    @Override
    public boolean isClearLegal()
    {
        return false;
    }

    @Override
    public ObjectKey<CDOMSingleRef<Equipment>> getObjectKey()
    {
        return ObjectKey.BASE_ITEM;
    }
}
