/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
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
package plugin.qualifier.ability;

import java.net.URISyntaxException;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.QualifierToken;
import plugin.lsttokens.choose.AbilityToken;
import plugin.lsttokens.testsupport.AbstractPCQualifierTokenTestCase;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.TokenRegistration;
import plugin.lsttokens.testsupport.TransparentPlayerCharacter;

import org.junit.jupiter.api.BeforeEach;

public class PCQualifierTokenTest extends
        AbstractPCQualifierTokenTestCase<Ability>
{

    static AbilityToken subtoken = new AbilityToken();

    private static final plugin.qualifier.ability.PCToken PC_TOKEN =
            new plugin.qualifier.ability.PCToken();

    @BeforeEach
    @Override
    public void setUp() throws PersistenceLayerException, URISyntaxException
    {
        super.setUp();
        TokenRegistration.register(PC_TOKEN);
    }

    @Override
    public CDOMSecondaryToken<?> getSubToken()
    {
        return subtoken;
    }

    @Override
    public Class<Ability> getTargetClass()
    {
        return Ability.class;
    }

    @Override
    protected void addToPCSet(TransparentPlayerCharacter pc, Ability item)
    {
        pc.abilitySet.add(CNAbilityFactory.getCNAbility(BuildUtilities.getFeatCat(),
                Nature.NORMAL, item));
    }

    @Override
    protected Class<? extends QualifierToken<?>> getQualifierClass()
    {
        return plugin.qualifier.ability.PCToken.class;
    }

    @Override
    public String getSubTokenName()
    {
        return "ABILITY|FEAT";
    }

    @Override
    protected void setUpPC()
    {
        super.setUpPC();
        construct(primaryContext, "Goo");
        construct(secondaryContext, "Goo");
    }

    @Override
    protected CDOMObject construct(LoadContext loadContext, String one)
    {
        Ability a = BuildUtilities.getFeatCat().newInstance();
        a.setName(one);
        loadContext.getReferenceContext().importObject(a);
        return a;
    }

    @Override
    protected CDOMObject construct(LoadContext loadContext,
            Class<? extends CDOMObject> cl, String name)
    {
        return construct(loadContext, name);
    }

    @Override
    protected void additionalSetup(LoadContext context)
    {
        super.additionalSetup(context);
        //Dummy to ensure initialization
        construct(context, "Dummy");
    }


}
