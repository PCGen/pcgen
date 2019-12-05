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
package selectionactor.domain;

import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import plugin.lsttokens.domain.CskillToken;

import selectionactor.testsupport.AbstractSelectionActorTest;

public class CskillTokenTest extends AbstractSelectionActorTest<Skill>
{

    private static final ChooseSelectionActor CRA = new CskillToken();

    @Override
    public ChooseSelectionActor<Skill> getActor()
    {
        return CRA;
    }

    @Override
    public Class<Skill> getCDOMClass()
    {
        return Skill.class;
    }

    @Override
    public boolean isGranted()
    {
        return false;
    }

    @Override
    public ChooseDriver getOwner()
    {
        return new Domain();
    }

    @Override
    protected void preparePC(PlayerCharacter pc1, ChooseDriver owner)
    {
        PCClass c1 = Globals.getContext().getReferenceContext().constructCDOMObject(PCClass.class, "Class1");
        pc1.setDefaultDomainSource(new ClassSource(c1));
        pc1.addDomain((Domain) owner);
        super.preparePC(pc1, owner);
    }

}
