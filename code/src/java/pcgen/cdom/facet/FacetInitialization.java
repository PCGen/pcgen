/*
 * Copyright (c) Thomas Parker, 2009.
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
package pcgen.cdom.facet;

import pcgen.cdom.facet.analysis.ChangeProfFacet;
import pcgen.cdom.facet.analysis.LevelFacet;
import pcgen.cdom.facet.input.ActiveAbilityFacet;
import pcgen.cdom.facet.model.ActiveEqModFacet;
import pcgen.cdom.facet.model.AlignmentFacet;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassLevelFacet;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.DeityFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.ExpandedCampaignFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.facet.model.WeaponProfFacet;

public class FacetInitialization {

	public static void initialize()
	{
		doBridges();
		TemplateFacet templateFacet = FacetLibrary.getFacet(TemplateFacet.class);
		ConditionalTemplateFacet conditionalTemplateFacet = FacetLibrary.getFacet(ConditionalTemplateFacet.class);
		RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
		ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
		ClassLevelFacet classLevelFacet = FacetLibrary.getFacet(ClassLevelFacet.class);
		ExpandedCampaignFacet expandedCampaignFacet = FacetLibrary.getFacet(ExpandedCampaignFacet.class);
		EquipmentFacet equipmentFacet = FacetLibrary.getFacet(EquipmentFacet.class);
		EquippedEquipmentFacet equippedFacet = FacetLibrary.getFacet(EquippedEquipmentFacet.class);
		NaturalEquipmentFacet naturalEquipmentFacet = FacetLibrary.getFacet(NaturalEquipmentFacet.class);
		SourcedEquipmentFacet activeEquipmentFacet = FacetLibrary.getFacet(SourcedEquipmentFacet.class);
		ActiveEqModFacet activeEqModFacet = FacetLibrary.getFacet(ActiveEqModFacet.class);

		AlignmentFacet alignmentFacet = FacetLibrary.getFacet(AlignmentFacet.class);
		BioSetFacet bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
		BioSetTrackingFacet bioSetTrackingFacet = FacetLibrary.getFacet(BioSetTrackingFacet.class);
		CheckFacet checkFacet = FacetLibrary.getFacet(CheckFacet.class);

		AutoLanguageFacet autoLangFacet = FacetLibrary.getFacet(AutoLanguageFacet.class);
		WeaponProfFacet weaponProfFacet = FacetLibrary.getFacet(WeaponProfFacet.class);

		LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
		SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
		BonusChangeFacet bonusChangeFacet = FacetLibrary.getFacet(BonusChangeFacet.class);
		DeityFacet deityFacet = FacetLibrary.getFacet(DeityFacet.class);
		DomainFacet domainFacet = FacetLibrary.getFacet(DomainFacet.class);
		CompanionModFacet companionModFacet = FacetLibrary.getFacet(CompanionModFacet.class);
		StatFacet statFacet = FacetLibrary.getFacet(StatFacet.class);
		SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		ActiveAbilityFacet abFacet = FacetLibrary.getFacet(ActiveAbilityFacet.class);

		NaturalWeaponProfFacet nwpFacet = FacetLibrary.getFacet(NaturalWeaponProfFacet.class);
		UserEquipmentFacet userEquipmentFacet = FacetLibrary.getFacet(UserEquipmentFacet.class);
		NaturalWeaponFacet naturalWeaponFacet = FacetLibrary.getFacet(NaturalWeaponFacet.class);
		EquipSetFacet equipSetFacet = FacetLibrary.getFacet(EquipSetFacet.class);
		ChooseDriverFacet chooseDriverFacet = FacetLibrary.getFacet(ChooseDriverFacet.class);

		CDOMObjectConsolidationFacet cdomObjectFacet = FacetLibrary.getFacet(CDOMObjectConsolidationFacet.class);
		CDOMObjectSourceFacet cdomSourceFacet = FacetLibrary.getFacet(CDOMObjectSourceFacet.class);
		CharacterConsolidationFacet charObjectFacet = FacetLibrary.getFacet(CharacterConsolidationFacet.class);
		EquipmentConsolidationFacet eqObjectFacet = FacetLibrary.getFacet(EquipmentConsolidationFacet.class);
		GrantedAbilityFacet grantedAbilityFacet = FacetLibrary.getFacet(GrantedAbilityFacet.class);
		DirectAbilityFacet directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
		ConditionallyGrantedAbilityFacet cabFacet = FacetLibrary.getFacet(ConditionallyGrantedAbilityFacet.class);

		equipmentFacet.addDataFacetChangeListener(naturalEquipmentFacet);
		equippedFacet.addDataFacetChangeListener(activeEquipmentFacet);
		naturalEquipmentFacet.addDataFacetChangeListener(activeEquipmentFacet);
		activeEquipmentFacet.addDataFacetChangeListener(activeEqModFacet);

		nwpFacet.addDataFacetChangeListener(weaponProfFacet);

		domainFacet.addDataFacetChangeListener(-1000, chooseDriverFacet);
		raceFacet.addDataFacetChangeListener(-1000, chooseDriverFacet);
		templateFacet.addDataFacetChangeListener(-1000, chooseDriverFacet);

		charObjectFacet.addDataFacetChangeListener(naturalWeaponFacet);
		naturalWeaponFacet.addDataFacetChangeListener(equipmentFacet);
		naturalWeaponFacet.addDataFacetChangeListener(userEquipmentFacet);
		naturalWeaponFacet.addDataFacetChangeListener(equipSetFacet);

		classFacet.addLevelChangeListener(levelFacet);
		levelFacet.addLevelChangeListener(conditionalTemplateFacet);
		levelFacet.addLevelChangeListener(sizeFacet);

		directAbilityFacet.addDataFacetChangeListener(grantedAbilityFacet);
		cabFacet.addDataFacetChangeListener(grantedAbilityFacet);

		raceFacet.addDataFacetChangeListener(bioSetTrackingFacet);

		raceFacet.addDataFacetChangeListener(sizeFacet);
		templateFacet.addDataFacetChangeListener(sizeFacet);
		bonusChangeFacet.addBonusChangeListener(sizeFacet, "SIZEMOD", "NUMBER");

		expandedCampaignFacet.addDataFacetChangeListener(charObjectFacet); //model done
		alignmentFacet.addDataFacetChangeListener(charObjectFacet); //model done
		bioSetFacet.addDataFacetChangeListener(charObjectFacet); //model done
		checkFacet.addDataFacetChangeListener(charObjectFacet); //model done
		classFacet.addDataFacetChangeListener(charObjectFacet); //model done
		deityFacet.addDataFacetChangeListener(charObjectFacet); //model done
		domainFacet.addDataFacetChangeListener(charObjectFacet); //model done
		abFacet.addDataFacetChangeListener(charObjectFacet);
		raceFacet.addDataFacetChangeListener(charObjectFacet); //model done
		sizeFacet.addDataFacetChangeListener(charObjectFacet);
		skillFacet.addDataFacetChangeListener(charObjectFacet); //model done
		statFacet.addDataFacetChangeListener(charObjectFacet); //model done
		templateFacet.addDataFacetChangeListener(charObjectFacet); //model done

		// weaponProfList is still just a list of Strings
		// results.addAll(getWeaponProfList());
		classLevelFacet.addDataFacetChangeListener(charObjectFacet); //model done
		grantedAbilityFacet.addDataFacetChangeListener(charObjectFacet);
		companionModFacet.addDataFacetChangeListener(charObjectFacet); //model done

		activeEquipmentFacet.addDataFacetChangeListener(eqObjectFacet);
		activeEqModFacet.addDataFacetChangeListener(eqObjectFacet);

		eqObjectFacet.addDataFacetChangeListener(cdomObjectFacet);
		charObjectFacet.addDataFacetChangeListener(cdomObjectFacet);

		cdomObjectFacet.addDataFacetChangeListener(nwpFacet);
		cdomSourceFacet.addDataFacetChangeListener(autoLangFacet);
	}

	private static void doBridges()
	{
		/*
		 * TODO These are required because they are "bridges" - meaning they
		 * refer to others, but no one refers to them. Need to consider if these
		 * need to be redesigned...
		 */
		FacetLibrary.getFacet(AgeSetKitFacet.class);
		FacetLibrary.getFacet(DomainSpellListFacet.class);
		FacetLibrary.getFacet(NaturalEquipSetFacet.class);
		FacetLibrary.getFacet(ShieldProfFacet.class);
		FacetLibrary.getFacet(ArmorProfFacet.class);
		FacetLibrary.getFacet(MonsterClassFacet.class);
		FacetLibrary.getFacet(KitChoiceFacet.class);
		FacetLibrary.getFacet(RegionChoiceFacet.class);
		FacetLibrary.getFacet(AddFacet.class);
		FacetLibrary.getFacet(RemoveFacet.class);
		FacetLibrary.getFacet(CalcBonusFacet.class);
		FacetLibrary.getFacet(DomainSpellsFacet.class);
		FacetLibrary.getFacet(ObjectAdditionFacet.class);
		FacetLibrary.getFacet(AddLevelFacet.class);
		//This one is a just in case
		FacetLibrary.getFacet(ChangeProfFacet.class);
		//and others just in case...
		FacetLibrary.getFacet(ClassLevelChangeFacet.class);
		FacetLibrary.getFacet(UnconditionalTemplateFacet.class);
	}
}
