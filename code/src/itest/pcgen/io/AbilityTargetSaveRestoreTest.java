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

import java.util.ArrayList;

import org.junit.Test;

import pcgen.cdom.base.CDOMObjectUtilities;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;

public class AbilityTargetSaveRestoreTest extends
		AbstractGlobalTargetedSaveRestoreTest<Ability>
{

	@Override
	protected <T extends Loadable> T create(Class<T> cl, String key)
	{
		if (cl.equals(Ability.class))
		{
			T ab = super.create(cl, key);
			context.ref.reassociateCategory(AbilityCategory.FEAT, (Ability) ab);
			return ab;
		}
		else
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
		if (ChooseActivation.hasChooseToken(obj))
		{
			assoc = "Granted";
		}
		AbilityUtilities.modAbility(pc, obj, assoc, AbilityCategory.FEAT);
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
		if (ChooseActivation.hasChooseToken(abil))
		{
			ChooserUtilities.modChoices(abil, new ArrayList<String>(),
				new ArrayList<String>(), true, reloadedPC, false,
				AbilityCategory.FEAT);
		}
		//Have to do this check due to cloning...
		abil = reloadedPC.getAbilityKeyed(AbilityCategory.FEAT, abil.getKeyName());
		reloadedPC.removeRealAbility(AbilityCategory.FEAT, abil);
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

	@Override
	@Test
	public void testAddTemplate()
	{
		//CODE-2016 Ignore as known to be not symmetric yet :P
	}
	
	
}
