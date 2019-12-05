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
package plugin.lsttokens.editcontext;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Ability;
import pcgen.core.WeaponProf;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.CDOMLoader;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import plugin.lsttokens.ChangeprofLst;
import plugin.lsttokens.editcontext.testsupport.AbstractIntegrationTestCase;
import plugin.lsttokens.editcontext.testsupport.TestContext;
import plugin.lsttokens.testsupport.BuildUtilities;
import plugin.lsttokens.testsupport.CDOMTokenLoader;

import org.junit.jupiter.api.Test;

public class ChangeProfIntegrationTest extends
        AbstractIntegrationTestCase<CDOMObject>
{
    private static ChangeprofLst token = new ChangeprofLst();
    private static CDOMTokenLoader<CDOMObject> loader = new CDOMTokenLoader<>();

    @Override
    public Class<Ability> getCDOMClass()
    {
        return Ability.class;
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

    @Test
    public void testRoundRobinSimple() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Hammer=Martial");
        commit(modCampaign, tc, "Hammer,Pipe=Martial");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinRemove() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Hammer,TYPE.Heavy=Martial");
        commit(modCampaign, tc, "Hammer=Martial|Pipe=Exotic");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoSet() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        TestContext tc = new TestContext();
        emptyCommit(testCampaign, tc);
        commit(modCampaign, tc, "Hammer=Martial|Pipe=Exotic");
        completeRoundRobin(tc);
    }

    @Test
    public void testRoundRobinNoReset() throws PersistenceLayerException
    {
        verifyCleanStart();
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Hammer");
        primaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        secondaryContext.getReferenceContext().constructCDOMObject(WeaponProf.class, "Pipe");
        TestContext tc = new TestContext();
        commit(testCampaign, tc, "Hammer=Martial|Pipe=Exotic");
        emptyCommit(modCampaign, tc);
        completeRoundRobin(tc);
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
