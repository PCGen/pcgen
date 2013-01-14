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

import org.junit.Test;

import pcgen.core.PCClass;
import pcgen.core.Skill;
import pcgen.core.analysis.SkillRankControl;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.pcclass.HdToken;

public class SkillTargetSaveRestoreTest extends
		AbstractGlobalTargetedSaveRestoreTest<Skill>
{

	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	@Override
	public Class<Skill> getObjectClass()
	{
		return Skill.class;
	}

	@Override
	protected void applyObject(Skill obj)
	{
		PCClass cl =
				context.ref.silentlyGetConstructedCDOMObject(PCClass.class,
					"MyClass");
		pc.addClass(cl);
		pc.incrementClassLevel(1, cl);
		pc.setHP(pc.getActiveClassLevel(cl, 0), 4);
		pc.addSkill(obj);
		SkillRankControl.modRanks(1.0, cl, true, pc, obj);
		SkillRankControl.getSkillRankBonusTo(pc, obj);
	}

	@Override
	protected Object prepare(Skill obj)
	{
		PCClass cl = create(PCClass.class, "MyClass");
		new HdToken().parseToken(context, cl, "6");
		return obj;
	}

	@Override
	protected void remove(Object o)
	{
		PCClass cl =
				context.ref.silentlyGetConstructedCDOMObject(PCClass.class,
					"MyClass");
		Skill sk = (Skill) o;
		SkillRankControl.modRanks(-1.0, cl, true, reloadedPC, sk);
		assertTrue(reloadedPC.getRank(sk).equals(0.0f));
		SkillRankControl.getSkillRankBonusTo(reloadedPC, sk);
		reloadedPC.removeSkill(sk);
		assertFalse(reloadedPC.hasSkill(sk));
	}

	@Override
	protected void preEqualityCleanup()
	{
		//TODO need this to create the spell support :/
		PCClass cl =
				context.ref.silentlyGetConstructedCDOMObject(PCClass.class,
					"MyClass");
		reloadedPC.getSpellSupport(cl);
	}

	@Override
	@Test
	public void testGlobalCSkill()
	{
		//CODE-2015 needs to ensure this gets done
	}

	@Override
	@Test
	public void testGlobalCCSkill()
	{
		//CODE-2015 needs to ensure this gets done
	}

	@Override
	@Test
	public void testAutoWeaponProf()
	{
		//CODE-2015 needs to ensure this gets done
	}

	@Override
	@Test
	public void testAutoShieldProf()
	{
		//CODE-2015 needs to ensure this gets done
	}

	@Override
	@Test
	public void testAutoArmorProf()
	{
		//CODE-2015 needs to ensure this gets done
	}

	@Override
	@Test
	public void testAutoLanguage()
	{
		//CODE-2015 needs to ensure this gets done
	}
	
	
}
