/*
 * PreerquisiteTestFactory.java Copyright 2003 (C) Chris Ward
 * <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on November 28, 2003
 *
 * Current Ver: $Revision: 1.15 $ Last Editor: $Author: binkley $ Last Edited: $Date: 2005/10/18 20:23:37 $
 *
 */
package pcgen.core.prereq;

import pcgen.util.Logging;
import pcgen.util.PropertyFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wardc
 *
 */
public class PrerequisiteTestFactory {
	private static PrerequisiteTestFactory instance = null;
	private static Map testLookup = new HashMap();

	/**
	 * @return Returns the instance.
	 */
	public static PrerequisiteTestFactory getInstance() {
		if (instance == null)
			instance = new PrerequisiteTestFactory();
		return instance;
	}

	private PrerequisiteTestFactory() {
		register(new PreAlign());
		register(new PreApply());
		register(new PreArmorProficiency());
		register(new PreArmorType());
		register(new PreAttack());
		register(new PreBaseSize());
		register(new PreBirthPlace());
		register(new PreCity());
		register(new PreCheck());
		register(new PreCheckBase());
		register(new PreClass());
		register(new PreCSkill());
		register(new PreDamageReduction());
		register(new PreDefaultMonster());
		register(new PreDeity());
		register(new PreDeityAlign());
		register(new PreDeityDomain());
		register(new PreDomain());
		register(new PreEquip());
		register(new PreEquippedBoth());
		register(new PreEquippedPrimary());
		register(new PreEquippedSecondary());
		register(new PreEquippedTwoWeapon());
		register(new PreFeat());
		register(new PreGender());
		register(new PreHands());
		register(new PreHasDeity());
		register(new PreHD());
		register(new PreHP());
		register(new PreItem());
		register(new PreLanguage());
		register(new PreLegs());
		register(new PreLevel());
		register(new PreLevelMax());
		register(new PreMove());
		register(new PrePointBuyMethod());
		register(new PreRace());
		register(new PreRegion());
		register(new PreRule());
		register(new PreShieldProficiency());
		register(new PreSize());
		register(new PreSkill());
		register(new PreSkillMult());
		register(new PreSpell());
		register(new PreSpecialAbility());
		register(new PreSpellBook());
		register(new PreSpellCast());
		register(new PreSpellCastMemorize());
		register(new PreSpellResistance());
		register(new PreSpellSchool());
		register(new PreSpellSchoolSub());
		register(new PreSpellDescriptor());
		register(new PreSpellType());
		register(new PreStat());
		register(new PreSubClass());
		register(new PreTemplate());
		register(new PreText());
		register(new PreType());
		register(new PreUnarmedAttack());
		register(new PreVariable());
		register(new PreVision());
		register(new PreWield());
		register(new PreWeaponProficiency());
	}

	private void register(final PrerequisiteTest testClass) {
		final String kindHandled = testClass.kindHandled();
			final Object test = testLookup.get(kindHandled);
			if (test != null) {
				Logging.errorPrint(
					PropertyFactory.getFormattedString("PrerequisiteTestFactory.error.already_registered", //$NON-NLS-1$
						testClass.getClass().getName(),
						kindHandled,
						test.getClass().getName() ));
			}
			testLookup.put(kindHandled.toUpperCase(), testClass);
	}

	public PrerequisiteTest getTest(final String kind) {
		PrerequisiteTest test;
		if (kind == null)
		{
			test = new PreMult();
		}
		else
		{
			test = (PrerequisiteTest) testLookup.get(kind.toUpperCase());
			if (test==null) {
				Logging.errorPrintLocalised("PrerequisiteTestFactory.error.cannot_find_test", kind); //$NON-NLS-1$
			}
		}
		return test;
	}



}
