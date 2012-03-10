/**
 * Gui2InfoFactory.java
 * Copyright James Dempsey, 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 07/02/2011 7:13:32 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.BenefitFormatting;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SubClass;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.analysis.DescriptionFormatting;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SkillInfoUtilities;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SpellPoint;
import pcgen.core.analysis.TemplateModifier;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.character.WieldCategory;
import pcgen.core.display.VisionDisplay;
import pcgen.core.facade.AbilityFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.DeityFacade;
import pcgen.core.facade.DomainFacade;
import pcgen.core.facade.EquipmentFacade;
import pcgen.core.facade.InfoFactory;
import pcgen.core.facade.KitFacade;
import pcgen.core.facade.RaceFacade;
import pcgen.core.facade.SkillFacade;
import pcgen.core.facade.SpellFacade;
import pcgen.core.facade.TemplateFacade;
import pcgen.core.kit.BaseKit;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.spell.Spell;
import pcgen.gui.HTMLUtils;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * The Class <code>Gui2InfoFactory</code> provides character related information 
 * on various facade objects. The information is displayed to the user via the 
 * new user interface. 
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class Gui2InfoFactory implements InfoFactory
{
	/** A default return value for an invalid request. */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static NumberFormat ADJ_FMT = new DecimalFormat("+0;-0"); //$NON-NLS-1$
	private static NumberFormat COST_FMT = new DecimalFormat("0.#"); //$NON-NLS-1$

	private PlayerCharacter pc;
	
	/**
	 * Create a new Gui2InfoFactory instance for the character.
	 * @param pc The character
	 */
	public Gui2InfoFactory(PlayerCharacter pc)
	{
		this.pc =pc;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getFavoredClass(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public String getFavoredClass(RaceFacade race)
	{
		if (!(race instanceof Race))
		{
			return EMPTY_STRING;
		}
		String[] favClass = Globals.getContext().unparseSubtoken((Race)race, "FAVCLASS");
		return StringUtil.join(favClass, EMPTY_STRING);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public String getHTMLInfo(RaceFacade raceFacade)
	{
		if (!(raceFacade instanceof Race))
		{
			return EMPTY_STRING;
		}
		Race race = (Race) raceFacade;
		
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		if (!race.getKeyName().startsWith("<none"))
		{
			infoText.appendTitleElement(OutputNameFormatting.piString(race, false));

			infoText.appendLineBreak();
			RaceType rt = race.get(ObjectKey.RACETYPE);
			if (rt != null)
			{
				infoText.appendI18nElement("in_irInfoRaceType", rt.toString()); //$NON-NLS-1$
			}

			List<RaceSubType> rst = race.getListFor(ListKey.RACESUBTYPE);
			if (rst != null)
			{
				infoText.appendSpacer();
				infoText.appendI18nElement("in_irInfoSubType", StringUtil.join(rst, ", ")); //$NON-NLS-1$
			}
			if (race.getType().length() > 0)
			{
				infoText.appendSpacer();
				infoText.appendI18nElement("in_irInfoType", race.getType()); //$NON-NLS-1$
			}

			String bString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			race.getPrerequisiteList(), false);
			if (bString.length() > 0)
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_requirements", bString); //$NON-NLS-1$
			}

			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piDescSubString(pc, race));

			bString = race.getSource();
			if (bString.length() > 0)
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_sourceLabel", bString); //$NON-NLS-1$
			}
		}

		return infoText.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.ClassFacade, pcgen.core.facade.ClassFacade)
	 */
	@Override
	public String getHTMLInfo(ClassFacade classFacade,
		ClassFacade parentClassFacade)
	{
		if (!(classFacade instanceof PCClass))
		{
			return EMPTY_STRING;
		}
		PCClass aClass = (PCClass) classFacade;
		PCClass parentClass = aClass;

		String aString;
		boolean isSubClass = aClass instanceof SubClass;
		if (isSubClass)
		{
			parentClass = (PCClass) parentClassFacade;
		}

		final HtmlInfoBuilder b =
				new HtmlInfoBuilder(OutputNameFormatting
					.piString(aClass, false));
		b.appendLineBreak();

		// Type
		aString = aClass.getType();
		if (isSubClass && (aString.length() == 0))
		{
			aString = parentClass.getType();
		}
		b.appendI18nElement("in_clInfoType", aString); //$NON-NLS-1$

		// Hit Die
		HitDie hitDie = aClass.getSafe(ObjectKey.LEVEL_HITDIE);
		if (isSubClass && HitDie.ZERO.equals(hitDie))
		{
			hitDie = parentClass.getSafe(ObjectKey.LEVEL_HITDIE);
		}
		if (!HitDie.ZERO.equals(hitDie))
		{
			b.appendSpacer();
			b.appendI18nElement("in_clInfoHD", "d" + hitDie.getDie()); //$NON-NLS-1$  //$NON-NLS-2$
		}

		if (Globals.getGameModeShowSpellTab())
		{
			aString = aClass.get(StringKey.SPELLTYPE);

			if (isSubClass && aString == null)
			{
				aString = parentClass.getSpellType();
			}

			b.appendSpacer();
			b.appendI18nElement("in_clInfoSpellType", aString); //$NON-NLS-1$

			aString = aClass.getSpellBaseStat();

			/*
			 * CONSIDER This test here is the ONLY place where the "magical"
			 * value of null is tested for in getSpellBaseStat(). This is
			 * currently set by SubClass and SubstititionClass, so it IS
			 * used, but the question is: Is there a better method for
			 * identifying this special deferral to the "parentClass" other
			 * than null SpellBaseStat? - thpr 11/9/06
			 */
			if (isSubClass && ((aString == null) || (aString.length() == 0)))
			{
				aString = parentClass.getSpellBaseStat();
			}

			b.appendSpacer();
			b.appendI18nElement("in_clInfoBaseStat", aString); //$NON-NLS-1$
		}

		// Prereqs
		aString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, aClass
					.getPrerequisiteList(), false);
		if (isSubClass && (aString.length() == 0))
		{
			aString =
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
						parentClass.getPrerequisiteList(), false);
		}
		if (aString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		// Source
		aString = aClass.getSource();
		if (isSubClass && (aString.length() == 0))
		{
			aString = parentClass.getSource();
		}
		if (aString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_source", aString); //$NON-NLS-1$
		}

		return b.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.SkillFacade)
	 */
	@Override
	public String getHTMLInfo(SkillFacade skillFacade)
	{
		if (!(skillFacade instanceof Skill))
		{
			return EMPTY_STRING;
		}
		Skill skill = (Skill) skillFacade;

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(skill, false));

		infoText.appendLineBreak();
		infoText.appendI18nElement("in_igInfoLabelTextType", //$NON-NLS-1$
			StringUtil.join(skill.getTrueTypeList(true), ". "));

		infoText.appendLineBreak();
		String aString = SkillInfoUtilities.getKeyStatFromStats(pc, skill);
		if (aString.length() != 0)
		{
			infoText.appendI18nElement("in_iskKEY_STAT", aString); //$NON-NLS-1$
		}
		infoText.appendLineBreak();
		infoText.appendI18nElement("in_iskUntrained", //$NON-NLS-1$
			skill.getSafe(ObjectKey.USE_UNTRAINED) ? LanguageBundle
				.getString("in_yes") : LanguageBundle.getString("in_no"));
		infoText.appendLineBreak();
		infoText.appendI18nElement("in_iskEXCLUSIVE", //$NON-NLS-1$
			skill.getSafe(ObjectKey.EXCLUSIVE) ? LanguageBundle
				.getString("in_yes") : LanguageBundle.getString("in_no"));

		String bString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, skill
					.getPrerequisiteList(), false);
		if (bString.length() > 0)
		{
			infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
				bString);
		}

		bString = skill.getSource();
		if (bString.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_iskSource", bString); //$NON-NLS-1$
		}

		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN, false))
		{
			bString = SkillModifier.getModifierExplanation(skill, pc, false);
			if (bString.length() != 0)
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_iskHtml_PcMod", //$NON-NLS-1$
					bString);
			}
		}

		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(
			PCGenSettings.OPTION_SHOW_SKILL_RANK_BREAKDOWN, false))
		{
			bString = SkillRankControl.getRanksExplanation(pc, skill);
			if (bString.length() == 0)
			{
				bString = LanguageBundle.getString("in_none"); //$NON-NLS-1$
			}
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_iskHtml_Ranks", //$NON-NLS-1$
				bString);
		}

		return infoText.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.AbilityFacade)
	 */
	@Override
	public String getHTMLInfo(AbilityFacade abilityFacade)
	{
		if (!(abilityFacade instanceof Ability))
		{
			return EMPTY_STRING;
		}
		Ability ability = (Ability) abilityFacade;

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();
		infoText.appendTitleElement(OutputNameFormatting.piString(ability, false));
		infoText.appendLineBreak();

		infoText.appendI18nFormattedElement("Ability.Info.Type", //$NON-NLS-1$
			StringUtil.join(ability.getTrueTypeList(true), ". ")); //$NON-NLS-1$

		BigDecimal costStr = ability.getSafe(ObjectKey.SELECTION_COST);
		if (!costStr.equals(BigDecimal.ONE)) //$NON-NLS-1$
		{
			infoText.appendI18nFormattedElement("Ability.Info.Cost", //$NON-NLS-1$
				COST_FMT.format(costStr));
		}

		if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			infoText.appendSpacer();
			infoText.append(LanguageBundle.getString("Ability.Info.Multiple")); //$NON-NLS-1$
		}

		if (ability.getSafe(ObjectKey.STACKS))
		{
			infoText.appendSpacer();
			infoText.append(LanguageBundle.getString("Ability.Info.Stacks")); //$NON-NLS-1$
		}

		final String cString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
					ability.getPrerequisiteList(), false);
		if (cString.length() > 0)
		{
			infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
				cString);
		}

		infoText.appendLineBreak();
		infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
			DescriptionFormatting.piDescSubString(pc, ability));

		if (ability.getSafeSizeOfMapFor(MapKey.ASPECT) > 0)
		{
			Set<AspectName> aspectKeys = ability.getKeysFor(MapKey.ASPECT);
			StringBuffer buff = new StringBuffer();
			for (AspectName key : aspectKeys)
			{
				if (buff.length() > 0)
				{
					buff.append(", ");
				}
				buff.append(ability.printAspect(pc, key));
			}
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("Ability.Info.Aspects", //$NON-NLS-1$
				buff.toString());
		}
		
		final String bene = BenefitFormatting.getBenefits(pc, ability);
		if (bene != null && bene.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("Ability.Info.Benefit", //$NON-NLS-1$
				BenefitFormatting.getBenefits(pc, ability));
		}

		infoText.appendLineBreak();
		infoText.appendI18nFormattedElement("in_InfoSource", //$NON-NLS-1$
			ability.getSource());

		return infoText.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.DeityFacade)
	 */
	@Override
	public String getHTMLInfo(DeityFacade deityFacade)
	{
		if (!(deityFacade instanceof Deity))
		{
			return EMPTY_STRING;
		}
		Deity aDeity = (Deity) deityFacade;
		
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();
		if (aDeity != null)
		{
			infoText.appendTitleElement(OutputNameFormatting.piString(aDeity, false));
			infoText.appendLineBreak();

			String aString = aDeity.get(StringKey.TITLE);
			if (aString != null)
			{
				infoText.appendI18nFormattedElement("in_deityTitle", //$NON-NLS-1$
					aString);
				infoText.appendLineBreak();
			}

			infoText
				.appendI18nFormattedElement(
					"in_InfoDescription", DescriptionFormatting.piDescString(pc, aDeity)); //$NON-NLS-1$

			aString = getPantheons(aDeity);
			if (aString != null)
			{
				infoText.appendSpacer();
				infoText.appendI18nElement(
						"in_pantheon", aString); //$NON-NLS-1$
			}

			infoText.appendSpacer();
			infoText.appendI18nElement(
				"in_domains", getDomains(aDeity)); //$NON-NLS-1$

			List<CDOMReference<WeaponProf>> dwp = aDeity.getListFor(
					ListKey.DEITYWEAPON);
			if (dwp != null)
			{
				infoText.appendSpacer();
				infoText.appendI18nFormattedElement(
					"in_deityFavWeap", //$NON-NLS-1$
					ReferenceUtilities.joinLstFormat(dwp, "|"));
			}

			aString = aDeity.get(StringKey.HOLY_ITEM);
			if (aString != null)
			{
				infoText.appendSpacer();
				infoText.appendI18nFormattedElement("in_deityHolyIt", //$NON-NLS-1$
					aString);
			}

			aString = aDeity.get(StringKey.WORSHIPPERS);
			if (aString != null)
			{
				infoText.appendSpacer();
				infoText.appendI18nFormattedElement("in_deityWorshippers", //$NON-NLS-1$
					aString);
			}

			aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			aDeity.getPrerequisiteList(), false);
			if (aString.length() != 0)
			{
				infoText.appendSpacer();
				infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
					aString);
			}

			aString = aDeity.getSource();
			if (aString.length() > 0)
			{
				infoText.appendSpacer();
				infoText.appendI18nFormattedElement("in_InfoSource", //$NON-NLS-1$
					aString);
			}

		}
		return infoText.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.DomainFacade)
	 */
	@Override
	public String getHTMLInfo(DomainFacade domainFacade)
	{
		if (!(domainFacade instanceof DomainFacadeImpl))
		{
			return EMPTY_STRING;
		}
		DomainFacadeImpl domainFI = (DomainFacadeImpl) domainFacade;
		Domain aDomain = domainFI.getRawObject();
		
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		if (aDomain != null)
		{
			infoText.appendTitleElement(OutputNameFormatting.piString(aDomain, false));

			String aString = pc.getDescription(aDomain);
			if (aString.length() != 0)
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_domainGrant", //$NON-NLS-1$
					aString);
			}

			aString =
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
						aDomain.getPrerequisiteList(), false);
			if (aString.length() != 0)
			{
				infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
					aString);
			}
			
			aString =
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, aDomain,
						domainFI.getPrerequisiteList(), false);
			if (aString.length() != 0)
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement(
					"in_domainRequirements", //$NON-NLS-1$
					aString);
			}

			aString =
					SourceFormat.getFormattedString(aDomain, Globals
						.getSourceDisplay(), true);
			if (aString.length() > 0)
			{
				infoText.appendI18nFormattedElement("in_InfoSource", //$NON-NLS-1$
					aString);
			}
			

		}

		return infoText.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.EquipmentFacade)
	 */
	@Override
	public String getHTMLInfo(EquipmentFacade equipFacade)
	{
		if (equipFacade == null || !(equipFacade instanceof Equipment))
		{
			return EMPTY_STRING;
		}
		
		Equipment equip = (Equipment) equipFacade;

		final StringBuilder title = new StringBuilder(50);
		title.append(OutputNameFormatting.piString(equip, false));

		if (!equip.longName().equals(equip.getName()))
		{
			title.append("(").append(equip.longName()).append(")");
		}

		final HtmlInfoBuilder b = new HtmlInfoBuilder(null, false);
		File icon = equip.getIcon();
		if (icon != null)
		{
			b.appendIconElement(icon.toURI().toString());
		}
		b.appendTitleElement(title.toString());
		b.appendLineBreak();

		b.appendI18nElement("in_igInfoLabelTextType", //$NON-NLS-1$
			StringUtil.join(equip.getTrueTypeList(true), ". "));

		//
		// Should only be meaningful for weapons, but if included on some other piece of
		// equipment, show it anyway
		//
		if (equip.isWeapon() || equip.get(ObjectKey.WIELD) != null)
		{
			b.appendLineBreak();
			final WieldCategory wCat = equip.getEffectiveWieldCategory(pc);
			b.appendI18nElement("in_igInfoLabelTextWield", //$NON-NLS-1$
				wCat.getDisplayName());
		}

		//
		// Only meaningful for weapons, armor and shields
		//
		if (equip.isWeapon() || equip.isArmor() || equip.isShield())
		{
			b.appendLineBreak();
			final String value =
					(pc.isProficientWith(equip) && equip.meetsPreReqs(pc))
						? LanguageBundle.getString("in_igInfoLabelTextYes") //$NON-NLS-1$
						: (SettingsHandler.getPrereqFailColorAsHtmlStart()
							+ LanguageBundle.getString("in_igInfoLabelTextNo") + //$NON-NLS-1$
						SettingsHandler.getPrereqFailColorAsHtmlEnd());
			b.appendI18nElement("in_igInfoLabelTextProficient", value); //$NON-NLS-1$
		}

		final String cString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, equip
					.getPrerequisiteList(), false);

		if (cString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextReq", cString); //$NON-NLS-1$
		}

		String IDS = equip.getInterestingDisplayString(pc);

		if (IDS.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextProp", IDS); //$NON-NLS-1$
		}

		String bString =
				Globals.getGameModeUnitSet().displayWeightInUnitSet(
					equip.getWeight(pc).doubleValue());

		if (bString.length() > 0)
		{
			b.appendLineBreak();
			bString += Globals.getGameModeUnitSet().getWeightUnit();
			b.appendI18nElement("in_igInfoLabelTextWeight", bString); //$NON-NLS-1$

		}

		Integer a = equip.getMaxDex(pc);

		if (a.intValue() != 100)
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextMaxDex", a.toString()); //$NON-NLS-1$
		}

		a = equip.acCheck(pc);

		if (equip.isArmor() || equip.isShield() || (a.intValue() != 0))
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextAcCheck", a.toString()); //$NON-NLS-1$
		}

		if (Globals.getGameModeACText().length() != 0)
		{
			a = equip.getACBonus(pc);

			if (equip.isArmor() || equip.isShield() || (a.intValue() != 0))
			{
				b.appendSpacer();
				b.appendElement(LanguageBundle.getFormattedString(
					"in_igInfoLabelTextAcBonus", //$NON-NLS-1$
					Globals.getGameModeACText()), a.toString());
			}
		}

		if (Globals.getGameModeShowSpellTab())
		{
			a = equip.spellFailure(pc);

			if (equip.isArmor() || equip.isShield() || (a.intValue() != 0))
			{
				b.appendSpacer();
				b.appendI18nElement(
					"in_igInfoLabelTextArcaneFailure", a.toString()); //$NON-NLS-1$
			}
		}

		bString = Globals.getGameModeDamageResistanceText();

		if (bString.length() != 0)
		{
			a = equip.eDR(pc);

			if (equip.isArmor() || equip.isShield() || (a.intValue() != 0))
			{
				b.appendSpacer();
				b.appendElement(bString, a.toString());
			}
		}

		bString = equip.moveString();

		if (bString.length() > 0)
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextMove", bString); //$NON-NLS-1$
		}

		bString = equip.getSize();

		if (bString.length() > 0)
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextSize", bString); //$NON-NLS-1$
		}

		bString = equip.getDamage(pc);

		if (bString.length() > 0)
		{

			if (equip.isDouble())
			{
				bString += "/" + equip.getAltDamage(pc); //$NON-NLS-1$
			}

			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextDamage", bString); //$NON-NLS-1$
		}

		int critrange = pc.getCritRange(equip, true);
		int altcritrange = pc.getCritRange(equip, false);
		bString = critrange == 0 ? EMPTY_STRING : Integer.toString(critrange);
		if (equip.isDouble() && critrange != altcritrange)
		{
			bString +=
					"/" //$NON-NLS-1$
						+ (altcritrange == 0 ? EMPTY_STRING : Integer
							.toString(altcritrange));
		}

		if (bString.length() > 0)
		{
			b.appendSpacer();
			b.appendI18nElement("in_ieInfoLabelTextCritRange", bString); //$NON-NLS-1$
		}

		bString = equip.getCritMult();
		if (equip.isDouble()
			&& !(equip.getCritMultiplier() == equip.getAltCritMultiplier()))
		{
			bString += "/" + equip.getAltCritMult(); //$NON-NLS-1$
		}

		if (bString.length() > 0)
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextCritMult", bString); //$NON-NLS-1$
		}

		if (equip.isWeapon())
		{
			bString =
					Globals.getGameModeUnitSet().displayDistanceInUnitSet(
						equip.getRange(pc).intValue());

			if (bString.length() > 0)
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextRange", bString + //$NON-NLS-1$
					Globals.getGameModeUnitSet().getDistanceUnit());
			}
		}

		bString = equip.getContainerCapacityString();

		if (bString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextContainer", bString); //$NON-NLS-1$
		}

		bString = equip.getContainerContentsString();

		if (bString.length() > 0)
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextCurrentlyContains", bString); //$NON-NLS-1$
		}

		final int charges = equip.getRemainingCharges();

		if (charges >= 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextCharges", Integer.valueOf( //$NON-NLS-1$
				charges).toString());
		}

		Map<String, String> qualityMap = equip.getMapFor(MapKey.QUALITY);
		if (qualityMap != null && !qualityMap.isEmpty())
		{
			Set<String> qualities = new TreeSet<String>();
			for (Map.Entry<String, String> me : qualityMap.entrySet())
			{
				qualities.add(new StringBuilder().append(me.getKey()).append(
					": ").append(me.getValue()).toString());
			}

			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextQualities", StringUtil.join( //$NON-NLS-1$
				qualities, ", ")); //$NON-NLS-2$
		}

		bString = equip.getSource();
		if (bString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextSource", bString); //$NON-NLS-1$
		}
		b.appendLineBreak();

		return b.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getHTMLInfo(pcgen.core.facade.TemplateFacade)
	 */
	@Override
	public String getHTMLInfo(TemplateFacade templateFacade)
	{
		if (templateFacade == null)
		{
			return EMPTY_STRING;
		}

		PCTemplate template = (PCTemplate) templateFacade;

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(template,
			false));

		RaceType rt = template.get(ObjectKey.RACETYPE);
		if (rt != null)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_irInfoRaceType", rt.toString()); //$NON-NLS-1$
		}

		if (template.getType().length() > 0)
		{
			infoText.appendSpacer();
			infoText.appendI18nElement("in_irInfoType", template.getType()); //$NON-NLS-1$
		}

		String aString = pc.getDescription(template);
		if (aString.length() != 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				aString);
		}

		aString = TemplateModifier.modifierString(template, pc);
		if (aString.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_modifier", aString); //$NON-NLS-1$
		}

		aString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
					template.getPrerequisiteList(), false);
		if (aString.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		aString = template.getSource();
		if (aString.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_sourceLabel", aString); //$NON-NLS-1$
		}

		return infoText.toString();
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHTMLInfo(KitFacade kitFacade)
	{
		if (kitFacade == null)
		{
			return EMPTY_STRING;
		}

		Kit kit = (Kit) kitFacade;

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(kit, false));

		String aString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
					kit.getPrerequisiteList(), false);
		if (aString.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		List<BaseKit> sortedObjects = new ArrayList<BaseKit>();
		sortedObjects.addAll(kit.getSafeListFor(ListKey.KIT_TASKS));
		Collections.sort(sortedObjects, new ObjectTypeComparator());

		String lastObjectName = EMPTY_STRING;
		for (BaseKit bk : sortedObjects)
		{
			String objName = bk.getObjectName();
			if (!objName.equals(lastObjectName))
			{
				if (!EMPTY_STRING.equals(lastObjectName))
				{
					infoText.append("; ");
				}
				infoText.append("  <b>" + objName + "</b>: ");
				lastObjectName = objName;
			}
			else
			{
				infoText.append(", ");
			}
			infoText.append(bk.toString());
		}

		aString = kit.getSource();
		if (aString.length() > 0)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_sourceLabel", aString); //$NON-NLS-1$
		}
		//TODO ListKey.KIT_TASKS
		return infoText.toString();
	}

	private static class ObjectTypeComparator implements Comparator<BaseKit>
	{
		@Override
		public int compare(BaseKit bk1, BaseKit bk2)
		{
			String name1 = bk1.getObjectName();
			String name2 = bk2.getObjectName();
			return name1.compareTo(name2);
		}
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getLevelAdjustment(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public String getLevelAdjustment(RaceFacade raceFacade)
	{
		if (!(raceFacade instanceof Race))
		{
			return EMPTY_STRING;
		}
		Race race = (Race) raceFacade;
		return ADJ_FMT.format(race.getSafe(FormulaKey.LEVEL_ADJUSTMENT)
			.resolve(pc, EMPTY_STRING));
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getPreReqHTML(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public String getPreReqHTML(RaceFacade race)
	{
		if (!(race instanceof Race))
		{
			return EMPTY_STRING;
		}
		return PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			((Race) race).getPrerequisiteList(), true);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getStatAdjustments(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public String getStatAdjustments(RaceFacade raceFacade)
	{
		if (!(raceFacade instanceof Race))
		{
			return EMPTY_STRING;
		}
		Race race = (Race) raceFacade;
		final StringBuffer retString = new StringBuffer();

		for (PCStat stat : pc.getStatSet())
		{
			if (pc.isNonAbility(stat))
			{
				if (retString.length() > 0)
				{
					retString.append(' ');
				}

				retString.append(stat.getAbb() + ":Nonability");
			}
			else
			{
				if (BonusCalc.getStatMod(race, stat, pc) != 0)
				{
					if (retString.length() > 0)
					{
						retString.append(' ');
					}

					retString.append(stat.getAbb() + ":"
						+ BonusCalc.getStatMod(race, stat, pc));
				}
			}
		}

		return retString.toString();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getVision(pcgen.core.facade.RaceFacade)
	 */
	@Override
	public String getVision(RaceFacade race)
	{
		if (!(race instanceof Race))
		{
			return EMPTY_STRING;
		}
		return VisionDisplay.getVision(pc, (Race) race);
	}

	@Override
	public float getCost(EquipmentFacade equipment)
	{
		if (equipment instanceof Equipment)
		{
			return ((Equipment)equipment).getCost(pc).floatValue();
		}
		return 0;
	}

	@Override
	public float getWeight(EquipmentFacade equipment)
	{
		if (equipment instanceof Equipment)
		{
			return ((Equipment)equipment).getWeight(pc);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getLevelAdjustment(pcgen.core.facade.TemplateFacade)
	 */
	@Override
	public String getLevelAdjustment(TemplateFacade templateFacade)
	{
		if (!(templateFacade instanceof PCTemplate))
		{
			return EMPTY_STRING;
		}
		PCTemplate template = (PCTemplate) templateFacade;
		return ADJ_FMT.format(template.getSafe(FormulaKey.LEVEL_ADJUSTMENT)
			.resolve(pc, EMPTY_STRING));
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getModifier(pcgen.core.facade.TemplateFacade)
	 */
	@Override
	public String getModifier(TemplateFacade templateFacade)
	{
		if (!(templateFacade instanceof PCTemplate))
		{
			return EMPTY_STRING;
		}
		PCTemplate template = (PCTemplate) templateFacade;
		return TemplateModifier.modifierString(template, pc);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFactory#getPreReqHTML(pcgen.core.facade.TemplateFacade)
	 */
	@Override
	public String getPreReqHTML(TemplateFacade template)
	{
		if (!(template instanceof PCTemplate))
		{
			return EMPTY_STRING;
		}
		return PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
			((PCTemplate) template).getPrerequisiteList(), true);
	}

	@Override
	public String getHTMLInfo(SpellFacade spell)
	{
		if (spell == null || !(spell instanceof SpellFacadeImplem))
		{
			return EMPTY_STRING;
		}

		SpellFacadeImplem sfi = (SpellFacadeImplem) spell;
		CharacterSpell cs = sfi.getCharSpell();
		SpellInfo si = sfi.getSpellInfo();
		Spell aSpell = cs.getSpell();

		if (aSpell == null)
		{
			return EMPTY_STRING;
		}
		final HtmlInfoBuilder b =
				new HtmlInfoBuilder(OutputNameFormatting.piString(aSpell, false));

		if (si != null)
		{
			final String addString = si.toString(); // would add [featList]
			if (addString.length() > 0)
			{
				b.append(" ").append(addString); //$NON-NLS-1$
			}
			b.appendLineBreak();
			b.appendI18nElement("InfoSpells.level.title", Integer.toString(si.getOriginalLevel())); //$NON-NLS-1$
		}
		else
		{
			b.appendLineBreak();
		}

		b.append(LanguageBundle.getFormattedString(
			"InfoSpells.html.spell.details", //$NON-NLS-1$
                aSpell.getListAsString(ListKey.SPELL_SCHOOL),
                aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL),
                aSpell.getListAsString(ListKey.SPELL_DESCRIPTOR),
                aSpell.getListAsString(ListKey.COMPONENTS),
                aSpell.getListAsString(ListKey.CASTTIME),
                pc.parseSpellString(cs, aSpell.getListAsString(ListKey.DURATION)),
                pc.getSpellRange(cs, si),
                pc.parseSpellString(cs, aSpell.getSafe(StringKey.TARGET_AREA)),
                aSpell.getListAsString(ListKey.SAVE_INFO),
                aSpell.getListAsString(ListKey.SPELL_RESISTANCE)));
		
		if (Globals.hasSpellPPCost())
		{
			b.appendSpacer();
			b.appendI18nElement("InfoSpellsSubTab.PPCost", String //$NON-NLS-1$
				.valueOf(aSpell.getSafe(IntegerKey.PP_COST)));
		}
		if (Spell.hasSpellPointCost())
		{
			b.appendSpacer();
			b.appendI18nElement("InfoSpellsSubTab.SpellPointCost", String //$NON-NLS-1$
				.valueOf(SpellPoint.getSPCostStrings(pc, aSpell)));
		}
		b.appendLineBreak();
		b.appendI18nElement("in_descrip", pc.parseSpellString(cs, 
			pc.getDescription(aSpell)));

		final String cString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null,
		aSpell.getPrerequisiteList(), false);
		if (cString.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", cString); //$NON-NLS-1$
		}

		String spellSource = SourceFormat.getFormattedString(aSpell,
		Globals.getSourceDisplay(), true);
		if (spellSource.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_source", spellSource); //$NON-NLS-1$
		}

		return b.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSpellBookInfo(String name)
	{
		SpellBook book = pc.getDisplay().getSpellBookByName(name);
		if (book == null)
		{
			return EMPTY_STRING;
		}
		
		switch (book.getType())
		{
			case SpellBook.TYPE_PREPARED_LIST:
				return produceSpellListInfo(book);

			case SpellBook.TYPE_SPELL_BOOK:
				return produceSpellBookInfo(book);
				
			default:
				return EMPTY_STRING;
		}
	}

	/**
	 * Produce the HTML info label for a prepared spell list.
	 * @param book The spell list being output.
	 * @return The HTML info for the list.
	 */
	private String produceSpellListInfo(SpellBook spelllist)
	{
		final HtmlInfoBuilder b =
				new HtmlInfoBuilder(spelllist.getName());

		b.append(" ("); //$NON-NLS-1$
		b.append(spelllist.getTypeName());
		b.append(")"); //$NON-NLS-1$
		b.appendLineBreak();

		if (spelllist.getDescription() != null)
		{
			b.appendI18nElement("in_descrip", spelllist.getDescription()); //$NON-NLS-1$
			b.appendLineBreak();
		}
		
		// Look at each spell on each spellcasting class
		for (PCClass pcClass : pc.getClassSet())
		{
			Map<Integer, Integer> spellCountMap = new TreeMap<Integer, Integer>();
			int highestSpellLevel = -1;
			for (CharacterSpell charSpell : pc.getCharacterSpells(pcClass))
			{
				for (SpellInfo spellInfo : charSpell.getInfoList())
				{
					if (!spelllist.getName().equals(spellInfo.getBook()))
					{
						continue;
					}
					int level = spellInfo.getActualLevel();
					
					int count = spellCountMap.containsKey(level) ? spellCountMap.get(level) : 0;
					count += spellInfo.getTimes();
					spellCountMap.put(level, count);
					if (level > highestSpellLevel)
					{
						highestSpellLevel = level;
					}
				}
			}

			if (!spellCountMap.isEmpty())
			{
				b.append("<table border=1><tr><td><font size=-1><b>"); //$NON-NLS-1$
				b.append(OutputNameFormatting.piString(pcClass, false));
				b.append("</b></font></td>"); //$NON-NLS-1$

				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					b.append("<td><font size=-2><b><center>&nbsp;"); //$NON-NLS-1$
					b.append(String.valueOf(i));
					b.append("&nbsp;</b></center></font></td>"); //$NON-NLS-1$
				}

				b.append("</tr>"); //$NON-NLS-1$
				b.append("<tr><td><font size=-1><b>Prepared</b></font></td>"); //$NON-NLS-1$

				for (int i = 0; i <= highestSpellLevel; ++i)
				{
					b.append("<td><font size=-1><center>"); //$NON-NLS-1$
					b.append(String.valueOf(spellCountMap.get(i) == null ? 0
						: spellCountMap.get(i)));
					b.append("</center></font></td>"); //$NON-NLS-1$
				}
				b.append("</tr></table>"); //$NON-NLS-1$
				b.appendLineBreak();
			}
			
		}
		
		return b.toString();
	}

	/**
	 * Produce the HTML info label for a spell book.
	 * @param book The spell book being output.
	 * @return The HTML info for the book.
	 */
	private String produceSpellBookInfo(SpellBook book)
	{
		final HtmlInfoBuilder b =
				new HtmlInfoBuilder(book.getName());

		b.append(" ("); //$NON-NLS-1$
		b.append(book.getTypeName());
		if (book.getName().equals(pc.getSpellBookNameToAutoAddKnown()))
		{
			b.append(HTMLUtils.TWO_SPACES).append(HTMLUtils.BOLD);
			b.append(
				LanguageBundle.getString("InfoSpellsSubTab.DefaultKnownBook")) //$NON-NLS-1$
				.append(HTMLUtils.END_BOLD);
		}
		b.append(")"); //$NON-NLS-1$
		b.appendLineBreak();
		
		b.append(LanguageBundle.getFormattedString(
			"InfoSpells.html.spellbook.details", //$NON-NLS-1$
			new Object[]{
				book.getNumPages(),
				book.getNumPagesUsed(),
				book.getPageFormula(),
				book.getNumSpells()}));

		if (book.getDescription() != null)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_descrip", book.getDescription()); //$NON-NLS-1$
		}
		return b.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription(AbilityFacade ability)
	{
		if (ability == null || !(ability instanceof Ability))
		{
			return EMPTY_STRING;
		}

		try
		{
			return DescriptionFormatting.piDescSubString(pc, (Ability) ability);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + ability, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDomains(DeityFacade deityFacade)
	{
		if (deityFacade == null || !(deityFacade instanceof Deity))
		{
			return EMPTY_STRING;
		}
		Deity deity = (Deity) deityFacade;
		Set<String> set = new TreeSet<String>();
		for (CDOMReference<Domain> ref : deity.getSafeListMods(Deity.DOMAINLIST))
		{
			for (Domain d : ref.getContainedObjects())
			{
				set.add(OutputNameFormatting.piString(d, false));
			}
		}
		final StringBuffer piString = new StringBuffer(100);
		//piString.append("<html>"); //$NON-NLS-1$
		piString.append(StringUtil.joinToStringBuffer(set, ",")); //$NON-NLS-1$
		//piString.append("</html>"); //$NON-NLS-1$
		return piString.toString();
		
	}
	
	@Override
	public String getPantheons(DeityFacade deityFacade)
	{
		if (deityFacade == null || !(deityFacade instanceof Deity))
		{
			return EMPTY_STRING;
		}
		Deity deity = (Deity) deityFacade;
		Set<String> set = new TreeSet<String>();
		for (Pantheon p : deity.getSafeListFor(ListKey.PANTHEON))
		{
			set.add(p.toString());
		}
		final StringBuffer piString = new StringBuffer(100);
		piString.append(StringUtil.joinToStringBuffer(set, ",")); //$NON-NLS-1$
		return piString.toString();
	
	}

}
