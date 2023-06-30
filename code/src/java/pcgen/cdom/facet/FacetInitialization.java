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
import pcgen.cdom.facet.input.ClassSkillListFacet;
import pcgen.cdom.facet.input.DynamicFacet;
import pcgen.cdom.facet.input.DynamicWatchingFacet;
import pcgen.cdom.facet.input.MasterUsableSkillFacet;
import pcgen.cdom.facet.model.ActiveEqHeadFacet;
import pcgen.cdom.facet.model.ActiveEqModFacet;
import pcgen.cdom.facet.model.BioSetFacet;
import pcgen.cdom.facet.model.CheckFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassLevelFacet;
import pcgen.cdom.facet.model.CompanionModFacet;
import pcgen.cdom.facet.model.DomainFacet;
import pcgen.cdom.facet.model.DynamicConsolidationFacet;
import pcgen.cdom.facet.model.ExpandedCampaignFacet;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.SimpleAbilityFacet;
import pcgen.cdom.facet.model.SizeFacet;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.facet.model.StatFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.cdom.facet.model.VarScopedFacet;
import pcgen.cdom.util.CControl;
import pcgen.output.factory.CodeControlModelFactory;
import pcgen.output.publish.OutputDB;

public final class FacetInitialization
{

	private static boolean isInitialized = false;

	private FacetInitialization()
	{
		//Do not instantiate
	}

	public static synchronized void initialize()
	{
		if (!isInitialized)
		{
			doInitialization();
			isInitialized = true;
		}
	}

	private static void doInitialization()
	{
		doOtherInitialization();
		doBridges();
		ScopedDistributionFacet scopedDistributionFacet = FacetLibrary.getFacet(ScopedDistributionFacet.class);
		GrantedVarFacet grantedVarFacet = FacetLibrary.getFacet(GrantedVarFacet.class);
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
		ActiveEqHeadFacet activeEqHeadFacet = FacetLibrary.getFacet(ActiveEqHeadFacet.class);
		ActiveEqModFacet activeEqModFacet = FacetLibrary.getFacet(ActiveEqModFacet.class);

		GlobalModifierFacet globalModifierFacet = FacetLibrary.getFacet(GlobalModifierFacet.class);
		BioSetFacet bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
		BioSetTrackingFacet bioSetTrackingFacet = FacetLibrary.getFacet(BioSetTrackingFacet.class);
		CheckFacet checkFacet = FacetLibrary.getFacet(CheckFacet.class);

		DynamicWatchingFacet dynamicWatchingFacet = FacetLibrary.getFacet(DynamicWatchingFacet.class);
		DynamicFacet dynamicFacet = FacetLibrary.getFacet(DynamicFacet.class);
		DynamicConsolidationFacet dynamicConsolidationFacet = FacetLibrary.getFacet(DynamicConsolidationFacet.class);
		VarScopedFacet varScopedFacet = FacetLibrary.getFacet(VarScopedFacet.class);

		AutoLanguageFacet autoLangFacet = FacetLibrary.getFacet(AutoLanguageFacet.class);
		WeaponProfFacet weaponProfFacet = FacetLibrary.getFacet(WeaponProfFacet.class);

		LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
		SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
		BonusChangeFacet bonusChangeFacet = FacetLibrary.getFacet(BonusChangeFacet.class);
		DomainFacet domainFacet = FacetLibrary.getFacet(DomainFacet.class);
		CompanionModFacet companionModFacet = FacetLibrary.getFacet(CompanionModFacet.class);
		StatFacet statFacet = FacetLibrary.getFacet(StatFacet.class);
		SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);

		NaturalWeaponProfFacet nwpFacet = FacetLibrary.getFacet(NaturalWeaponProfFacet.class);
		UserEquipmentFacet userEquipmentFacet = FacetLibrary.getFacet(UserEquipmentFacet.class);
		NaturalWeaponFacet naturalWeaponFacet = FacetLibrary.getFacet(NaturalWeaponFacet.class);
		EquipSetFacet equipSetFacet = FacetLibrary.getFacet(EquipSetFacet.class);

		CDOMObjectConsolidationFacet cdomObjectFacet = FacetLibrary.getFacet(CDOMObjectConsolidationFacet.class);
		CDOMObjectSourceFacet cdomSourceFacet = FacetLibrary.getFacet(CDOMObjectSourceFacet.class);
		CharacterConsolidationFacet charObjectFacet = FacetLibrary.getFacet(CharacterConsolidationFacet.class);
		EquipmentConsolidationFacet eqObjectFacet = FacetLibrary.getFacet(EquipmentConsolidationFacet.class);
		GrantedAbilityFacet grantedAbilityFacet = FacetLibrary.getFacet(GrantedAbilityFacet.class);
		DirectAbilityFacet directAbilityFacet = FacetLibrary.getFacet(DirectAbilityFacet.class);
		DirectAbilityInputFacet directAbilityInputFacet = FacetLibrary.getFacet(DirectAbilityInputFacet.class);
		ConditionallyGrantedAbilityFacet cabFacet = FacetLibrary.getFacet(ConditionallyGrantedAbilityFacet.class);
		SimpleAbilityFacet simpleAbilityFacet = FacetLibrary.getFacet(SimpleAbilityFacet.class);
		AbilitySelectionApplication abilitySelectionApplication =
				FacetLibrary.getFacet(AbilitySelectionApplication.class);

		equipmentFacet.addDataFacetChangeListener(naturalEquipmentFacet);
		equippedFacet.addDataFacetChangeListener(activeEquipmentFacet);
		naturalEquipmentFacet.addDataFacetChangeListener(activeEquipmentFacet);
		activeEquipmentFacet.addDataFacetChangeListener(activeEqHeadFacet);
		activeEqHeadFacet.addDataFacetChangeListener(activeEqModFacet);

		nwpFacet.addDataFacetChangeListener(weaponProfFacet);

		dynamicWatchingFacet.addDataFacetChangeListener(dynamicFacet);
		dynamicFacet.addScopeFacetChangeListener(dynamicConsolidationFacet);

		charObjectFacet.addDataFacetChangeListener(naturalWeaponFacet);
		naturalWeaponFacet.addDataFacetChangeListener(equipmentFacet);
		naturalWeaponFacet.addDataFacetChangeListener(userEquipmentFacet);
		naturalWeaponFacet.addDataFacetChangeListener(equipSetFacet);

		classFacet.addLevelChangeListener(levelFacet);
		levelFacet.addLevelChangeListener(conditionalTemplateFacet);
		levelFacet.addLevelChangeListener(sizeFacet);

		grantedAbilityFacet.addDataFacetChangeListener(abilitySelectionApplication);
		grantedAbilityFacet.addDataFacetChangeListener(simpleAbilityFacet);
		directAbilityFacet.addDataFacetChangeListener(grantedAbilityFacet);
		directAbilityInputFacet.addDataFacetChangeListener(grantedAbilityFacet);
		cabFacet.addDataFacetChangeListener(grantedAbilityFacet);

		raceFacet.addDataFacetChangeListener(bioSetTrackingFacet);

		bonusChangeFacet.addBonusChangeListener(sizeFacet, "SIZEMOD", "NUMBER");

		grantedVarFacet.addDataFacetChangeListener(scopedDistributionFacet); //model done

		expandedCampaignFacet.addDataFacetChangeListener(charObjectFacet); //model done
		globalModifierFacet.addDataFacetChangeListener(charObjectFacet); //model done
		bioSetFacet.addDataFacetChangeListener(charObjectFacet); //model done
		checkFacet.addDataFacetChangeListener(charObjectFacet); //model done
		classFacet.addDataFacetChangeListener(charObjectFacet); //model done
		domainFacet.addDataFacetChangeListener(charObjectFacet); //model done
		raceFacet.addDataFacetChangeListener(charObjectFacet); //model done
		sizeFacet.addDataFacetChangeListener(charObjectFacet);
		skillFacet.addDataFacetChangeListener(charObjectFacet); //model done
		statFacet.addDataFacetChangeListener(charObjectFacet); //model done
		templateFacet.addDataFacetChangeListener(charObjectFacet); //model done

		// weaponProfList is still just a list of Strings
		// results.addAll(getWeaponProfList());
		classLevelFacet.addDataFacetChangeListener(charObjectFacet); //model done
		simpleAbilityFacet.addDataFacetChangeListener(charObjectFacet); //model done
		companionModFacet.addDataFacetChangeListener(charObjectFacet); //model done

		activeEquipmentFacet.addDataFacetChangeListener(eqObjectFacet);
		activeEqHeadFacet.addDataFacetChangeListener(eqObjectFacet);
		activeEqModFacet.addDataFacetChangeListener(eqObjectFacet);

		eqObjectFacet.addDataFacetChangeListener(cdomObjectFacet);
		charObjectFacet.addDataFacetChangeListener(cdomObjectFacet);

		cdomObjectFacet.addDataFacetChangeListener(nwpFacet);
		cdomSourceFacet.addDataFacetChangeListener(autoLangFacet);
		cdomSourceFacet.addDataFacetChangeListener(dynamicWatchingFacet);

		cdomObjectFacet.addDataFacetChangeListener(varScopedFacet);
		dynamicConsolidationFacet.addDataFacetChangeListener(varScopedFacet); //model done
	}

	private static void doOtherInitialization()
	{
		OutputDB.registerMode("cc", new CodeControlModelFactory());
	}

	private static void doBridges()
	{
		/*
		 * Do dataset-level facets
		 */
		FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		FacetLibrary.getFacet(ObjectWrapperFacet.class);
		FacetLibrary.getFacet(MasterSkillFacet.class);
		FacetLibrary.getFacet(MasterAvailableSpellFacet.class);
		FacetLibrary.getFacet(MasterUsableSkillFacet.class);
		FacetLibrary.getFacet(EquipmentTypeFacet.class);
		FacetLibrary.getFacet(ObjectWrapperFacet.class);
		FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		FacetLibrary.getFacet(HiddenTypeFacet.class);
		FacetLibrary.getFacet(LoadContextFacet.class);
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
		FacetLibrary.getFacet(AddFacet.class);
		FacetLibrary.getFacet(RemoveFacet.class);
		FacetLibrary.getFacet(ModifierFacet.class);
		FacetLibrary.getFacet(RemoteModifierFacet.class);
		FacetLibrary.getFacet(CalcBonusFacet.class);
		FacetLibrary.getFacet(DomainSpellsFacet.class);
		FacetLibrary.getFacet(ObjectAdditionFacet.class);
		FacetLibrary.getFacet(AddLevelFacet.class);
		FacetLibrary.getFacet(ChooseDriverFacet.class);
		FacetLibrary.getFacet(AvailableSpellInputFacet.class);
		FacetLibrary.getFacet(KnownSpellInputFacet.class);
		FacetLibrary.getFacet(ClassSkillListFacet.class);
		FacetLibrary.getFacet(SpellListToAvailableSpellFacet.class);
		//This one is a just in case
		FacetLibrary.getFacet(ChangeProfFacet.class);
		//and others just in case...
		FacetLibrary.getFacet(ClassLevelChangeFacet.class);
		FacetLibrary.getFacet(UnconditionalTemplateFacet.class);
		
		/*
		 * As good of a place as any to do this
		 */
		OutputDB.register("deity", CControl.DEITYINPUT);
		OutputDB.register("alignment", CControl.ALIGNMENTINPUT);
	}
}
