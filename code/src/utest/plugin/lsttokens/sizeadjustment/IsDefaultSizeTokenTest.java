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
package plugin.lsttokens.sizeadjustment;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.SizeAdjustment;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.testsupport.AbstractYesNoTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class IsDefaultSizeTokenTest extends
        AbstractYesNoTokenTestCase<SizeAdjustment>
{

    private static IsdefaultsizeToken token = new IsdefaultsizeToken();
    private static CDOMTokenLoader<SizeAdjustment> loader = new CDOMTokenLoader<>();

    @Override
    public Class<SizeAdjustment> getCDOMClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    public CDOMLoader<SizeAdjustment> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<SizeAdjustment> getToken()
    {
        return token;
    }

    @Override
    public ObjectKey<Boolean> getObjectKey()
    {
        return ObjectKey.IS_DEFAULT_SIZE;
    }

}
