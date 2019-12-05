/*
 *
 * Copyright (c) 2007 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.add;

import pcgen.cdom.base.ChoiceActor;
import pcgen.core.PCTemplate;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import plugin.lsttokens.testsupport.AbstractAddTokenTestCase;

public class TemplateTokenTest extends AbstractAddTokenTestCase<PCTemplate>
{

    static TemplateToken subtoken = new TemplateToken();

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<PCTemplate> getTargetClass()
    {
        return PCTemplate.class;
    }

    @Override
    public boolean isAllLegal()
    {
        return false;
    }

    @Override
    public boolean isTypeLegal()
    {
        return false;
    }

    @Override
    protected ChoiceActor<PCTemplate> getActor()
    {
        return subtoken;
    }

    @Override
    public boolean allowsFormula()
    {
        return true;
    }

}
