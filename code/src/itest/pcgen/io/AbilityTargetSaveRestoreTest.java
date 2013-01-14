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

import pcgen.cdom.base.Loadable;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Language;
import pcgen.core.Skill;
import pcgen.core.chooser.ChooserUtilities;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.choose.SkillToken;

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
			((Ability) ab).put(ObjectKey.MULTIPLE_ALLOWED, true);
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
		AbilityUtilities.modAbility(pc, obj, "Granted", AbilityCategory.FEAT);
	}

	@Override
	protected Object prepare(Ability obj)
	{
		return obj;
	}

	@Override
	protected void remove(Object o)
	{
		ChooserUtilities.modChoices((Ability) o, new ArrayList<String>(),
			new ArrayList<String>(), true, reloadedPC, false,
			AbilityCategory.FEAT);
		reloadedPC.removeRealAbility(AbilityCategory.FEAT, (Ability) o);
	}

	@Override
	@Test
	public void testGlobalCSkill()
	{
		//CODE-2016 needs to ensure this is implemented
	}

	@Override
	@Test
	public void testGlobalCCSkill()
	{
		//CODE-2016 needs to ensure this is implemented
	}

	@Override
	@Test
	public void testAutoWeaponProf()
	{
		//CODE-2016 needs to ensure this is implemented
	}

	@Override
	@Test
	public void testAutoShieldProf()
	{
		//CODE-2016 needs to ensure this is implemented
	}

	@Override
	@Test
	public void testAutoArmorProf()
	{
		//CODE-2016 needs to ensure this is implemented
	}

	@Override
	@Test
	public void testAutoLanguage()
	{
		Ability target = create(getObjectClass(), "Target");
		Language granted = create(Language.class, "Granted");
		create(Language.class, "Ignored");
		new plugin.lsttokens.auto.LangToken().parseToken(context, target,
			"%LIST");
		new plugin.lsttokens.choose.LangToken().parseToken(context,
			target, "Granted|Ignored");
		Object o = prepare(target);
		finishLoad();
		assertFalse(pc.hasLanguage(granted));
		applyObject(target);
		assertTrue(pc.hasLanguage(granted));
		runRoundRobin();
		assertTrue(pc.hasLanguage(granted));
		assertTrue(reloadedPC.hasLanguage(granted));
		remove(o);
		reloadedPC.setDirty(true);
		//CODE-2016 needs to ensure this is implemented
		//assertFalse(reloadedPC.hasLanguage(granted));
	}

	@Override
	@Test
	public void testAddLanguage()
	{
		Language granted = create(Language.class, "MyLanguage");
		create(Language.class, "Ignored");
		Ability target = create(getObjectClass(), "Target");
		//This is a distraction to avoid failure
		create(Skill.class, "Granted");
		new SkillToken().parseToken(context, target, "Granted");
		//end distraction
		new plugin.lsttokens.add.LanguageToken().parseToken(context, target,
			"MyLanguage");
		Object o = prepare(target);
		finishLoad();
		assertFalse(pc.hasLanguage(granted));
		applyObject(target);
		assertTrue(pc.hasLanguage(granted));
		runRoundRobin();
		assertTrue(pc.hasLanguage(granted));
		assertTrue(reloadedPC.hasLanguage(granted));
		remove(o);
		reloadedPC.setDirty(true);
		//CODE-2016 needs to ensure this is implemented
		//assertFalse(reloadedPC.hasLanguage(granted));
	}

	@Override
	@Test
	public void testAddTemplate()
	{
		//CODE-2016 needs to ensure this is implemented
	}

}
