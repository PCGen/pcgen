/*
 *
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.choose;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCCheck;
import pcgen.core.Race;
import pcgen.core.SizeAdjustment;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.ChooseLst;
import plugin.lsttokens.testsupport.AbstractChooseTokenTestCase;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class SizeTokenTest extends
        AbstractChooseTokenTestCase<CDOMObject, SizeAdjustment>
{

    static ChooseLst token = new ChooseLst();
    static SizeToken subtoken = new SizeToken();
    static CDOMTokenLoader<CDOMObject> loader =
            new CDOMTokenLoader<>();

    @Override
    public Class<Race> getCDOMClass()
    {
        return Race.class;
    }

    @Override
    public CDOMLoader<CDOMObject> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<CDOMObject> getToken()
    {
        return token;
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<SizeAdjustment> getTargetClass()
    {
        return SizeAdjustment.class;
    }

    @Override
    protected boolean allowsQualifier()
    {
        return false;
    }

    @Override
    protected String getChoiceTitle()
    {
        return subtoken.getDefaultTitle();
    }

    @Override
    protected QualifierToken<PCCheck> getPCQualifier()
    {
        return null;
    }

    @Override
    protected boolean isTypeLegal()
    {
        return false;
    }

    @Override
    protected boolean isAllLegal()
    {
        return true;
    }

    @Override
    public void testOverwrite()
    {
        construct(primaryContext, "TestWP1");
        construct(primaryContext, "TestWP2");
        construct(primaryContext, "TestWP3");
        construct(secondaryContext, "TestWP1");
        construct(secondaryContext, "TestWP2");
        construct(secondaryContext, "TestWP3");
        super.testOverwrite();
    }

    @Override
    protected boolean usesComma()
    {
        return false;
    }
}
