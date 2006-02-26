/*
 * PrerequisiteWriterFactory.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 18-Dec-2003
 *
 * Current Ver: $Revision: 1.8 $
 *
 * Last Editor: $Author: binkley $
 *
 * Last Edited: $Date: 2005/10/18 20:23:56 $
 *
 */
package pcgen.persistence.lst.output.prereq;

import pcgen.persistence.PersistenceLayerException;
import pcgen.util.Logging;

import java.util.HashMap;
import java.util.Map;

public class PrerequisiteWriterFactory
{
	private static PrerequisiteWriterFactory instance = null;
	private static Map parserLookup = new HashMap();



	private PrerequisiteWriterFactory()
	{
		try
		{
			register(new PrerequisiteAlignWriter());
			register(new PrerequisiteApplyWriter());
			register(new PrerequisiteArmorProficiencyWriter());
			register(new PrerequisiteArmorTypeWriter());
			register(new PrerequisiteAttackWriter());
			register(new PrerequisiteBaseSizeWriter());
			register(new PrerequisiteBirthPlaceWriter());
			register(new PrerequisiteCityWriter());
			register(new PrerequisiteCheckWriter());
			register(new PrerequisiteCheckBaseWriter());
			register(new PrerequisiteClassWriter());
			register(new PrerequisiteCSkillWriter());
			register(new PrerequisiteDamageReductionWriter());
			register(new PrerequisiteDefaultMonsterWriter());
			register(new PrerequisiteDeityAlignWriter());
			register(new PrerequisiteDeityDomainWriter());
			register(new PrerequisiteDeityWriter());
			register(new PrerequisiteDomainWriter());
			register(new PrerequisiteEquipWriter());
			register(new PrerequisiteEquippedBothWriter());
			register(new PrerequisiteEquippedPrimaryWriter());
			register(new PrerequisiteEquippedSecondaryWriter());
			register(new PrerequisiteEquippedTwoWeaponWriter());
			register(new PrerequisiteFeatWriter());
			register(new PrerequisiteGenderWriter());
			register(new PrerequisiteHandsWriter());
			register(new PrerequisiteHasDeityWriter());
			register(new PrerequisiteHDWriter());
			register(new PrerequisiteHPWriter());
			register(new PrerequisiteItemWriter());
			register(new PrerequisiteLanguageWriter());
			register(new PrerequisiteLegsWriter());
			register(new PrerequisiteLevelWriter());
			register(new PrerequisiteLevelMaxWriter());
			register(new PrerequisiteMoveWriter());
			register(new PrerequisitePointBuyMethodWriter());
			register(new PrerequisiteRaceWriter());
			register(new PrerequisiteRuleWriter());
			register(new PrerequisiteRegionWriter());
			register(new PrerequisiteShieldProficiencyWriter());
			register(new PrerequisiteSizeWriter());
			register(new PrerequisiteSkillWriter());
			register(new PrerequisiteSkillMultWriter());
			register(new PrerequisiteSpellWriter());
			register(new PrerequisiteSpecialAbilityWriter());
			register(new PrerequisiteSpellBookWriter());
			register(new PrerequisiteSpellCastWriter());
			register(new PrerequisiteSpellCastMemorizeWriter());
			register(new PrerequisiteSpellDescriptorWriter());
			register(new PrerequisiteSpellResistanceWriter());
			register(new PrerequisiteSpellSchoolWriter());
			register(new PrerequisiteSpellSchoolSubWriter());
			register(new PrerequisiteSpellTypeWriter());
			register(new PrerequisiteStatWriter());
			register(new PrerequisiteSubClassWriter());
			register(new PrerequisiteTemplateWriter());
			register(new PrerequisiteTextWriter());
			register(new PrerequisiteTypeWriter());
			register(new PrerequisiteUnarmedAttackWriter());
			register(new PrerequisiteVariableWriter());
			register(new PrerequisiteVisionWriter());
			register(new PrerequisiteWieldWriter());
			register(new PrerequisiteWeaponProficiencyWriter());
		}
		catch (Exception e)
		{
			Logging.errorPrint("Unable to fully populate PrerequisiteriterFactory:" +e.getMessage());
		}
	}


	/**
	 * @return PrerequisiteWriterFactory
	 */
	public static PrerequisiteWriterFactory getInstance()
	{
		if (instance == null){
			instance = new PrerequisiteWriterFactory();
		}
		return instance;
	}

	/**
	 * @param kind
	 * @return PrerequisiteWriterInterface
	 */
	public PrerequisiteWriterInterface getWriter(String kind)
	{
		PrerequisiteWriterInterface test = null;
		if (kind == null)
		{
			test = new PrerequisiteMultWriter();
		}
		else
		{
			test = (PrerequisiteWriterInterface) parserLookup.get(kind.toLowerCase());
			if (test == null) {
				Logging.errorPrintLocalised("PrerequisiteTestFactory.error.cannot_find_test", kind); //$NON-NLS-1$
			}
		}
		return test;
	}


	private void register(PrerequisiteWriterInterface testClass)
	throws PersistenceLayerException
	{
		String kindHandled = testClass.kindHandled();

		Object test = parserLookup.get(kindHandled.toLowerCase());

		if (test != null)
		{
			throw new PersistenceLayerException("Error registering '" + testClass.getClass().getName()
					+ "' as test '" + kindHandled + "'. The test is already registered to '"
					+ test.getClass().getName() + "'");
		}

		parserLookup.put(kindHandled.toLowerCase(), testClass);
	}



}
