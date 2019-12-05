/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.io;

import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.UserSelection;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.CNAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.analysis.ChooseActivation;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.testsupport.BuildUtilities;

public class AbilityTargetSaveRestoreTest extends
        AbstractGlobalTargetedSaveRestoreTest<Ability>
{

    @Override
    protected <T extends Loadable> T create(Class<T> cl, String key)
    {
        if (cl.equals(Ability.class))
        {
            T source = (T) BuildUtilities.getFeatCat().newInstance();
            source.setName(key);
            context.getReferenceContext().importObject(source);
            return source;
        } else
        {
            return super.create(cl, key);
        }
    }

    @Override
    public Class<Ability> getObjectClass()
    {
        return Ability.class;
    }

    @Override
    protected void applyObject(Ability obj)
    {
        String assoc = null;
        if (ChooseActivation.hasNewChooseToken(obj))
        {
            assoc = "Granted";
        }
        CNAbility cna = CNAbilityFactory.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, obj);
        CNAbilitySelection cnas = new CNAbilitySelection(cna, assoc);
        pc.addAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
    }

    @Override
    protected Object prepare(Ability obj)
    {
        return obj;
    }

    @Override
    protected void remove(Object o)
    {
        Ability abil = (Ability) o;
        CNAbility cna = CNAbilityFactory.getCNAbility(BuildUtilities.getFeatCat(), Nature.NORMAL, abil);
        String assoc = null;
        if (ChooseActivation.hasNewChooseToken(abil))
        {
            assoc = "Granted";
        }
        CNAbilitySelection cnas = new CNAbilitySelection(cna, assoc);
        reloadedPC.removeAbility(cnas, UserSelection.getInstance(), UserSelection.getInstance());
        //TODO These need to be moved into being core behaviors somehow
        CDOMObjectUtilities.removeAdds(abil, reloadedPC);
        CDOMObjectUtilities.restoreRemovals(abil, reloadedPC);
        reloadedPC.adjustMoveRates();
    }

    @Override
    protected void additionalChooseSet(Ability target)
    {
        target.put(ObjectKey.MULTIPLE_ALLOWED, true);
    }

    //CODE-2016 needs to ensure this gets removed...
    @Override
    protected boolean isSymmetric()
    {
        return false;
    }

}
