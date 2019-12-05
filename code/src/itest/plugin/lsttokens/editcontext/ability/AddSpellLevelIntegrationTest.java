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
package plugin.lsttokens.editcontext.ability;

import pcgen.core.Ability;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.ability.AddspelllevelToken;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegerIntegrationTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

public class AddSpellLevelIntegrationTest extends
        AbstractIntegerIntegrationTestCase<Ability>
{

    private static AddspelllevelToken token = new AddspelllevelToken();
    private static CDOMTokenLoader<Ability> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Ability> getCDOMClass()
    {
        return Ability.class;
    }

    @Override
    public CDOMLoader<Ability> getLoader()
    {
        return loader;
    }

    @Override
    public CDOMPrimaryToken<Ability> getToken()
    {
        return token;
    }

    @Override
    public boolean isNegativeAllowed()
    {
        return true;
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
    public boolean doesOverwrite()
    {
        return true;
    }

    @Override
    protected boolean isClearAllowed()
    {
        return false;
    }

    @Override
    protected Ability construct(LoadContext context, String name)
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(name);
        context.getReferenceContext().importObject(a);
        return a;
    }
}
