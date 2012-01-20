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


public class FacetInitialization
{

	public static void initialize()
	{
		TemplateFacet templateFacet = FacetLibrary
				.getFacet(TemplateFacet.class);
		ConditionalTemplateFacet conditionalTemplateFacet = FacetLibrary
				.getFacet(ConditionalTemplateFacet.class);
		RaceFacet raceFacet = FacetLibrary.getFacet(RaceFacet.class);
		ClassFacet classFacet = FacetLibrary.getFacet(ClassFacet.class);
		ClassLevelFacet classLevelFacet = FacetLibrary
				.getFacet(ClassLevelFacet.class);
		CampaignFacet campaignFacet = FacetLibrary
				.getFacet(CampaignFacet.class);
		ExpandedCampaignFacet expandedCampaignFacet = FacetLibrary
				.getFacet(ExpandedCampaignFacet.class);
		EquipmentFacet equipmentFacet = FacetLibrary
				.getFacet(EquipmentFacet.class);
		EquippedEquipmentFacet equippedFacet = FacetLibrary
				.getFacet(EquippedEquipmentFacet.class);
		NaturalEquipmentFacet naturalEquipmentFacet = FacetLibrary
				.getFacet(NaturalEquipmentFacet.class);
		SourcedEquipmentFacet activeEquipmentFacet = FacetLibrary
				.getFacet(SourcedEquipmentFacet.class);
		ActiveEqModFacet activeEqModFacet = FacetLibrary
				.getFacet(ActiveEqModFacet.class);

		AlignmentFacet alignmentFacet = FacetLibrary
				.getFacet(AlignmentFacet.class);
		BioSetFacet bioSetFacet = FacetLibrary.getFacet(BioSetFacet.class);
		CheckFacet checkFacet = FacetLibrary.getFacet(CheckFacet.class);

		LanguageFacet languageFacet = FacetLibrary
				.getFacet(LanguageFacet.class);
		FreeLanguageFacet freeLangFacet = FacetLibrary
				.getFacet(FreeLanguageFacet.class);
		AutoLanguageFacet autoLangFacet = FacetLibrary
				.getFacet(AutoLanguageFacet.class);
		AddLanguageFacet addLangFacet = FacetLibrary
				.getFacet(AddLanguageFacet.class);
		SkillLanguageFacet skillLangFacet = FacetLibrary
				.getFacet(SkillLanguageFacet.class);
		StartingLanguageFacet startingLangFacet = FacetLibrary
				.getFacet(StartingLanguageFacet.class);
		DeityWeaponProfFacet deityWeaponProfFacet = FacetLibrary
				.getFacet(DeityWeaponProfFacet.class);
		WeaponProfFacet weaponProfFacet = FacetLibrary
				.getFacet(WeaponProfFacet.class);

		QualifyFacet qualifyFacet = FacetLibrary.getFacet(QualifyFacet.class);
		LevelFacet levelFacet = FacetLibrary.getFacet(LevelFacet.class);
		SizeFacet sizeFacet = FacetLibrary.getFacet(SizeFacet.class);
		BonusChangeFacet bonusChangeFacet = FacetLibrary
				.getFacet(BonusChangeFacet.class);
		DeityFacet deityFacet = FacetLibrary.getFacet(DeityFacet.class);
		DomainFacet domainFacet = FacetLibrary.getFacet(DomainFacet.class);
		CompanionModFacet companionModFacet = FacetLibrary
				.getFacet(CompanionModFacet.class);
		StatFacet statFacet = FacetLibrary.getFacet(StatFacet.class);
		SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		DamageReductionFacet drFacet = FacetLibrary
				.getFacet(DamageReductionFacet.class);
		ActiveAbilityFacet abFacet = FacetLibrary
				.getFacet(ActiveAbilityFacet.class);
		AutoListShieldProfFacet splFacet = FacetLibrary
				.getFacet(AutoListShieldProfFacet.class);
		ShieldProfProviderFacet sppFacet = FacetLibrary
				.getFacet(ShieldProfProviderFacet.class);
		ShieldProfFacet spFacet = FacetLibrary.getFacet(ShieldProfFacet.class);
		AutoListArmorProfFacet aplFacet = FacetLibrary
				.getFacet(AutoListArmorProfFacet.class);
		ArmorProfProviderFacet appFacet = FacetLibrary
				.getFacet(ArmorProfProviderFacet.class);
		ArmorProfFacet apFacet = FacetLibrary.getFacet(ArmorProfFacet.class);
		VariableFacet variableFacet = FacetLibrary
				.getFacet(VariableFacet.class);
		UnlockedStatFacet unlockedStatFacet = FacetLibrary
				.getFacet(UnlockedStatFacet.class);
		StatLockFacet statLockFacet = FacetLibrary
				.getFacet(StatLockFacet.class);
		FavoredClassFacet favClassFacet = FacetLibrary
				.getFacet(FavoredClassFacet.class);
		AddedTemplateFacet addedTemplateFacet =
				FacetLibrary.getFacet(AddedTemplateFacet.class);
		MonsterClassFacet monsterClassFacet = FacetLibrary
				.getFacet(MonsterClassFacet.class);
		SpellsFacet spellsFacet = FacetLibrary.getFacet(SpellsFacet.class);

		VisionFacet visionFacet = FacetLibrary.getFacet(VisionFacet.class);
		FollowerOptionFacet foFacet = FacetLibrary
				.getFacet(FollowerOptionFacet.class);
		FollowerLimitFacet flFacet = FacetLibrary
				.getFacet(FollowerLimitFacet.class);
		CharacterSpellResistanceFacet srFacet = FacetLibrary
				.getFacet(CharacterSpellResistanceFacet.class);
		ChangeProfFacet cpFacet = FacetLibrary.getFacet(ChangeProfFacet.class);
		AutoWeaponProfFacet awpFacet = FacetLibrary
				.getFacet(AutoWeaponProfFacet.class);
		HasDeityWeaponProfFacet hdwpFacet = FacetLibrary
				.getFacet(HasDeityWeaponProfFacet.class);
		NaturalWeaponProfFacet nwpFacet = FacetLibrary
				.getFacet(NaturalWeaponProfFacet.class);
		KnownSpellFacet knownSpellFacet = FacetLibrary
				.getFacet(KnownSpellFacet.class);
		AvailableSpellFacet availSpellFacet = FacetLibrary
				.getFacet(AvailableSpellFacet.class);
		AutoListWeaponProfFacet alWeaponProfFacet = FacetLibrary
				.getFacet(AutoListWeaponProfFacet.class);
		BonusWeaponProfFacet wpBonusFacet = FacetLibrary
				.getFacet(BonusWeaponProfFacet.class);
		MovementFacet moveFacet = FacetLibrary.getFacet(MovementFacet.class);
		UnarmedDamageFacet unarmedDamageFacet = FacetLibrary
				.getFacet(UnarmedDamageFacet.class);
		AutoEquipmentFacet autoEquipFacet = FacetLibrary
				.getFacet(AutoEquipmentFacet.class);
		UserEquipmentFacet userEquipmentFacet = FacetLibrary
				.getFacet(UserEquipmentFacet.class);
		NaturalWeaponFacet naturalWeaponFacet =
				FacetLibrary.getFacet(NaturalWeaponFacet.class);
		NaturalEquipSetFacet naturalEquipSetFacet =
				FacetLibrary.getFacet(NaturalEquipSetFacet.class);
		EquipSetFacet equipSetFacet =
				FacetLibrary
				.getFacet(EquipSetFacet.class);
		GlobalSkillCostFacet globalSkillCostFacet = FacetLibrary
				.getFacet(GlobalSkillCostFacet.class);
		LocalSkillCostFacet localSkillCostFacet = FacetLibrary
				.getFacet(LocalSkillCostFacet.class);
		ListSkillCostFacet listSkillCostFacet = FacetLibrary
				.getFacet(ListSkillCostFacet.class);
		ChooseDriverFacet chooseDriverFacet = FacetLibrary
				.getFacet(ChooseDriverFacet.class);
		DomainSpellListFacet domainSpellListFacet = FacetLibrary
				.getFacet(DomainSpellListFacet.class);

		CDOMObjectConsolidationFacet cdomObjectFacet = FacetLibrary
				.getFacet(CDOMObjectConsolidationFacet.class);
		CDOMObjectSourceFacet cdomSourceFacet = FacetLibrary
				.getFacet(CDOMObjectSourceFacet.class);
		CharacterConsolidationFacet charObjectFacet = FacetLibrary
				.getFacet(CharacterConsolidationFacet.class);
		EquipmentConsolidationFacet eqObjectFacet = FacetLibrary
				.getFacet(EquipmentConsolidationFacet.class);
		ObjectAdditionFacet additionFacet = FacetLibrary
				.getFacet(ObjectAdditionFacet.class);
		GrantedAbilityFacet grantedAbilityFacet = FacetLibrary
				.getFacet(GrantedAbilityFacet.class);
		DirectAbilityFacet directAbilityFacet = FacetLibrary
				.getFacet(DirectAbilityFacet.class);
		ConditionallyGrantedAbilityFacet cabFacet = FacetLibrary
				.getFacet(ConditionallyGrantedAbilityFacet.class);
		HasAnyFavoredClassFacet hasAnyFavoredFacet = FacetLibrary
				.getFacet(HasAnyFavoredClassFacet.class);
		SpellBookFacet spellBookFacet = FacetLibrary
				.getFacet(SpellBookFacet.class);
		AddLevelFacet addLevelFacet = FacetLibrary
				.getFacet(AddLevelFacet.class);
		AppliedBonusFacet appliedBonusFacet =
				FacetLibrary.getFacet(AppliedBonusFacet.class);
		UnencumberedArmorFacet unencumberedArmorFacet =
			FacetLibrary.getFacet(UnencumberedArmorFacet.class);		
		UnencumberedLoadFacet unencumberedLoadFacet =
				FacetLibrary.getFacet(UnencumberedLoadFacet.class);		

		HitPointFacet hitPointFacet = FacetLibrary.getFacet(HitPointFacet.class);

		AgeFacet ageFacet = FacetLibrary.getFacet(AgeFacet.class);
		RegionFacet regionFacet = FacetLibrary.getFacet(RegionFacet.class);
		AgeSetFacet ageSetFacet = FacetLibrary.getFacet(AgeSetFacet.class);
		AgeSetKitFacet ageSetKitFacet = FacetLibrary.getFacet(AgeSetKitFacet.class);

		raceFacet.addDataFacetChangeListener(ageSetFacet);
		regionFacet.addDataFacetChangeListener(ageSetFacet);
		ageFacet.addDataFacetChangeListener(ageSetFacet);
		bioSetFacet.addDataFacetChangeListener(ageSetFacet);

		ageFacet.addDataFacetChangeListener(ageSetKitFacet);

		autoLangFacet.addDataFacetChangeListener(languageFacet);
		freeLangFacet.addDataFacetChangeListener(languageFacet);
		addLangFacet.addDataFacetChangeListener(languageFacet);
		skillLangFacet.addDataFacetChangeListener(languageFacet);

		equipmentFacet.addDataFacetChangeListener(spellBookFacet);

		raceFacet.addDataFacetChangeListener(appliedBonusFacet);

		equipmentFacet.addDataFacetChangeListener(naturalEquipmentFacet);
		equippedFacet.addDataFacetChangeListener(activeEquipmentFacet);
		naturalEquipmentFacet.addDataFacetChangeListener(activeEquipmentFacet);
		activeEquipmentFacet.addDataFacetChangeListener(activeEqModFacet);

		campaignFacet.addDataFacetChangeListener(expandedCampaignFacet);

		wpBonusFacet.addDataFacetChangeListener(weaponProfFacet);
		nwpFacet.addDataFacetChangeListener(weaponProfFacet);
		alWeaponProfFacet.addDataFacetChangeListener(weaponProfFacet);

		domainFacet.addDataFacetChangeListener(-1000, chooseDriverFacet);
		raceFacet.addDataFacetChangeListener(-1000, chooseDriverFacet);
		templateFacet.addDataFacetChangeListener(-1000, chooseDriverFacet);

		aplFacet.addDataFacetChangeListener(appFacet);
		splFacet.addDataFacetChangeListener(sppFacet);

		deityFacet.addDataFacetChangeListener(deityWeaponProfFacet);
		templateFacet.addDataFacetChangeListener(addLevelFacet);

		classFacet.addDataFacetChangeListener(localSkillCostFacet);
		domainFacet.addDataFacetChangeListener(localSkillCostFacet);
		classLevelFacet.addDataFacetChangeListener(localSkillCostFacet);

		charObjectFacet.addDataFacetChangeListener(naturalWeaponFacet);
		naturalWeaponFacet.addDataFacetChangeListener(equipmentFacet);
		naturalWeaponFacet.addDataFacetChangeListener(userEquipmentFacet);
		naturalWeaponFacet.addDataFacetChangeListener(equipSetFacet);

		classFacet.addLevelChangeListener(classLevelFacet);
		classFacet.addLevelChangeListener(levelFacet);
		levelFacet.addLevelChangeListener(conditionalTemplateFacet);

		directAbilityFacet.addDataFacetChangeListener(grantedAbilityFacet);
		cabFacet.addDataFacetChangeListener(grantedAbilityFacet);

		domainFacet.addDataFacetChangeListener(domainSpellListFacet);

		templateFacet.addDataFacetChangeListener(hitPointFacet);
		conditionalTemplateFacet.addDataFacetChangeListener(hitPointFacet);

		raceFacet.addDataFacetChangeListener(listSkillCostFacet);
		raceFacet.addDataFacetChangeListener(bioSetFacet);
		raceFacet.addDataFacetChangeListener(monsterClassFacet);

		raceFacet.addDataFacetChangeListener(startingLangFacet);
		templateFacet.addDataFacetChangeListener(startingLangFacet);
		conditionalTemplateFacet.addDataFacetChangeListener(startingLangFacet);
		classFacet.addDataFacetChangeListener(startingLangFacet);

		raceFacet.addDataFacetChangeListener(sizeFacet);
		templateFacet.addDataFacetChangeListener(sizeFacet);
		conditionalTemplateFacet.addDataFacetChangeListener(sizeFacet);
		bonusChangeFacet.addBonusChangeListener(sizeFacet, "SIZEMOD", "NUMBER");

		raceFacet.addDataFacetChangeListener(favClassFacet);
		templateFacet.addDataFacetChangeListener(favClassFacet);
		conditionalTemplateFacet.addDataFacetChangeListener(favClassFacet);
		raceFacet.addDataFacetChangeListener(hasAnyFavoredFacet);
		templateFacet.addDataFacetChangeListener(hasAnyFavoredFacet);
		conditionalTemplateFacet.addDataFacetChangeListener(hasAnyFavoredFacet);

		expandedCampaignFacet.addDataFacetChangeListener(charObjectFacet);
		alignmentFacet.addDataFacetChangeListener(charObjectFacet);
		bioSetFacet.addDataFacetChangeListener(charObjectFacet);
		checkFacet.addDataFacetChangeListener(charObjectFacet);
		classFacet.addDataFacetChangeListener(charObjectFacet);
		deityFacet.addDataFacetChangeListener(charObjectFacet);
		domainFacet.addDataFacetChangeListener(charObjectFacet);
		abFacet.addDataFacetChangeListener(charObjectFacet);
		raceFacet.addDataFacetChangeListener(charObjectFacet);
		sizeFacet.addDataFacetChangeListener(charObjectFacet);
		skillFacet.addDataFacetChangeListener(charObjectFacet);
		statFacet.addDataFacetChangeListener(charObjectFacet);
		templateFacet.addDataFacetChangeListener(charObjectFacet);

		naturalWeaponFacet.addDataFacetChangeListener(naturalEquipSetFacet);

		// weaponProfList is still just a list of Strings
		// results.addAll(getWeaponProfList());
		classLevelFacet.addDataFacetChangeListener(charObjectFacet);
		conditionalTemplateFacet.addDataFacetChangeListener(charObjectFacet);
		grantedAbilityFacet.addDataFacetChangeListener(charObjectFacet);
		companionModFacet.addDataFacetChangeListener(charObjectFacet);

		activeEquipmentFacet.addDataFacetChangeListener(eqObjectFacet);
		activeEqModFacet.addDataFacetChangeListener(eqObjectFacet);

		eqObjectFacet.addDataFacetChangeListener(cdomObjectFacet);
		charObjectFacet.addDataFacetChangeListener(cdomObjectFacet);

		cdomObjectFacet.addDataFacetChangeListener(additionFacet);
		cdomObjectFacet.addDataFacetChangeListener(spFacet);
		cdomObjectFacet.addDataFacetChangeListener(srFacet);
		cdomObjectFacet.addDataFacetChangeListener(apFacet);
		cdomObjectFacet.addDataFacetChangeListener(qualifyFacet);
		cdomObjectFacet.addDataFacetChangeListener(drFacet);
		cdomObjectFacet.addDataFacetChangeListener(variableFacet);
		cdomObjectFacet.addDataFacetChangeListener(unlockedStatFacet);
		cdomObjectFacet.addDataFacetChangeListener(statLockFacet);
		cdomObjectFacet.addDataFacetChangeListener(visionFacet);
		cdomObjectFacet.addDataFacetChangeListener(foFacet);
		cdomObjectFacet.addDataFacetChangeListener(flFacet);
		cdomObjectFacet.addDataFacetChangeListener(cpFacet);
		cdomObjectFacet.addDataFacetChangeListener(awpFacet);
		cdomObjectFacet.addDataFacetChangeListener(hdwpFacet);
		cdomObjectFacet.addDataFacetChangeListener(nwpFacet);
		cdomObjectFacet.addDataFacetChangeListener(knownSpellFacet);
		cdomObjectFacet.addDataFacetChangeListener(availSpellFacet);
		cdomObjectFacet.addDataFacetChangeListener(moveFacet);
		cdomObjectFacet.addDataFacetChangeListener(unarmedDamageFacet);
		cdomObjectFacet.addDataFacetChangeListener(autoEquipFacet);
		cdomObjectFacet.addDataFacetChangeListener(globalSkillCostFacet);
		cdomObjectFacet.addDataFacetChangeListener(addedTemplateFacet);
		cdomSourceFacet.addDataFacetChangeListener(autoLangFacet);
		cdomSourceFacet.addDataFacetChangeListener(unencumberedArmorFacet);
		cdomSourceFacet.addDataFacetChangeListener(unencumberedLoadFacet);
		cdomSourceFacet.addDataFacetChangeListener(spellsFacet);

	}
}
