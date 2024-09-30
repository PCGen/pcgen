/**
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
 */
package pcgen.gui2.facade;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.base.util.Indirect;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbility;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.content.HitDie;
import pcgen.cdom.content.LevelCommandFactory;
import pcgen.cdom.content.fact.FactDefinition;
import pcgen.cdom.content.factset.FactSetDefinition;
import pcgen.cdom.enumeration.AspectName;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.RaceSubType;
import pcgen.cdom.enumeration.RaceType;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.AllowUtilities;
import pcgen.cdom.helper.Aspect;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Ability;
import pcgen.core.BenefitFormatting;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.Race;
import pcgen.core.SettingsHandler;
import pcgen.core.SimpleMovement;
import pcgen.core.Skill;
import pcgen.core.SpecialProperty;
import pcgen.core.SubClass;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SkillInfoUtilities;
import pcgen.core.bonus.BonusObj;
import pcgen.core.character.CharacterSpell;
import pcgen.core.character.SpellBook;
import pcgen.core.character.SpellInfo;
import pcgen.core.character.WieldCategory;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.DescriptionFormatting;
import pcgen.core.display.SkillCostDisplay;
import pcgen.core.display.TemplateModifier;
import pcgen.core.display.VisionDisplay;
import pcgen.core.kit.BaseKit;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.core.spell.Spell;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.SpellFacade;
import pcgen.facade.core.TempBonusFacade;
import pcgen.gui2.util.HtmlInfoBuilder;
import pcgen.io.exporttoken.EqToken;
import pcgen.io.exporttoken.WeaponToken;
import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenSettings;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;
import pcgen.util.enumeration.View;
import pcgen.util.enumeration.Visibility;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code Gui2InfoFactory} provides character related information
 * on various facade objects. The information is displayed to the user via the
 * new user interface.
 *
 *
 */
public class Gui2InfoFactory implements InfoFactory
{
	/** A default return value for an invalid request. */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	private static final NumberFormat ADJ_FMT = new DecimalFormat("+0;-0"); //$NON-NLS-1$
	private static final NumberFormat COST_FMT = new DecimalFormat("#,##0.##"); //$NON-NLS-1$

	/** Constant for 2 spaces in HTML */
	private static final String TWO_SPACES = " &nbsp;"; //$NON-NLS-1$
	/** Constant for HTML bold start tag */
	private static final String BOLD = "<b>"; //$NON-NLS-1$
	/** Constant for HTML bold end tag */
	private static final String END_BOLD = "</b>"; //$NON-NLS-1$

	private final PlayerCharacter pc;
	private final CharacterDisplay charDisplay;

	/**
	 * Create a new Gui2InfoFactory instance for the character.
	 * @param pc The character
	 */
	public Gui2InfoFactory(PlayerCharacter pc)
	{
		this.pc = pc;
		this.charDisplay = pc == null ? null : pc.getDisplay();
	}

	@Override
	public String getFavoredClass(Race race)
	{
		String[] favClass = Globals.getContext().unparseSubtoken(race, "FAVCLASS");
		return StringUtil.join(favClass, ", ");
	}

	@Override
	public String getHTMLInfo(Race race)
	{
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		if (!race.isUnselected())
		{
			infoText.appendTitleElement(OutputNameFormatting.piString(race));

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
			if (!race.getType().isEmpty())
			{
				infoText.appendSpacer();
				infoText.appendI18nElement("in_irInfoType", race.getType()); //$NON-NLS-1$
			}

			appendFacts(infoText, race);

			infoText.appendLineBreak();
			String size = getSize(race);

			if (StringUtils.isNotEmpty(size))
			{
				infoText.appendI18nElement("in_size", size); //$NON-NLS-1$
			}
			String movement = getMovement(race);
			if (!movement.isEmpty())
			{
				infoText.appendSpacer();
				infoText.appendI18nElement("in_movement", movement); //$NON-NLS-1$
			}
			String vision = getVision(race);
			if (!vision.isEmpty())
			{
				infoText.appendSpacer();
				infoText.appendI18nElement("in_vision", vision); //$NON-NLS-1$
			}

			String bString =
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, race.getPrerequisiteList(), false);
			if (!bString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_requirements", bString); //$NON-NLS-1$
			}
			String aString = AllowUtilities.getAllowInfo(pc, race);
			if (!aString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
			}

			String desc = pc.getDescription(race);
			if (!desc.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
					DescriptionFormatting.piWrapDesc(race, desc, false));
			}

			String statAdjustments = getStatAdjustments(race);
			if (StringUtils.isNotEmpty(statAdjustments))
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_irTableStat", statAdjustments); //$NON-NLS-1$
			}

			LevelCommandFactory levelCommandFactory = race.get(ObjectKey.MONSTER_CLASS);
			if (levelCommandFactory != null)
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_irInfoMonsterClass", //$NON-NLS-1$
					String.valueOf(levelCommandFactory.getLevelCount()),
					OutputNameFormatting.piString(levelCommandFactory.getPCClass()));

			}
			String favoredClass = getFavoredClass(race);
			if (StringUtils.isNotEmpty(favoredClass))
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_favoredClass", favoredClass); //$NON-NLS-1$
			}
			bString = race.getSource();
			if (!bString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_sourceLabel", bString); //$NON-NLS-1$
			}
		}

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(PCClass aClass, PCClass possibleParentClass)
	{
		PCClass parentClass = aClass;

		String aString;
		boolean isSubClass = aClass instanceof SubClass;
		if (isSubClass && possibleParentClass != null)
		{
			parentClass = possibleParentClass;
		}

		final HtmlInfoBuilder b = new HtmlInfoBuilder(OutputNameFormatting.piString(aClass));
		b.appendLineBreak();

		// Subclass cost - at the top to make choices easier
		if (isSubClass && aClass.getSafe(IntegerKey.COST) != 0)
		{
			b.appendI18nElement("in_clInfoCost", String.valueOf(aClass.getSafe(IntegerKey.COST))); //$NON-NLS-1$
			b.appendLineBreak();
		}

		// Type
		aString = aClass.getType();
		if (isSubClass && (aString.isEmpty()))
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

		appendFacts(b, aClass);

		if (SettingsHandler.getGameAsProperty().get().getTabShown(Tab.SPELLS))
		{
			FactKey<String> fk = FactKey.valueOf("SpellType");
			aString = aClass.getResolved(fk);

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
			if (isSubClass && ((aString == null) || (aString.isEmpty())))
			{
				aString = parentClass.getSpellBaseStat();
			}

			b.appendSpacer();
			b.appendI18nElement("in_clInfoBaseStat", aString); //$NON-NLS-1$
		}

		// Prereqs
		aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, aClass.getPrerequisiteList(), false);
		if (isSubClass && (aString.isEmpty()))
		{
			aString =
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, parentClass.getPrerequisiteList(), false);
		}
		if (!aString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}
		aString = AllowUtilities.getAllowInfo(pc, aClass);
		if (isSubClass && aString.isEmpty())
		{
			aString = AllowUtilities.getAllowInfo(pc, parentClass);
		}
		if (!aString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		//Description
		String desc = pc.getDescription(aClass);
		if (!desc.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piWrapDesc(aClass, desc, false));
		}
		// Sub class extra info
		if (isSubClass)
		{
			int specialtySpells = aClass.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY);
			b.appendLineBreak();
			b.appendI18nElement("in_clSpecialtySpells", Delta.toString(specialtySpells)); //$NON-NLS-1$
			b.appendSpacer();
			b.appendI18nElement("in_clSpecialty", ((SubClass) aClass).getChoice()); //$NON-NLS-1$
		}

		// Source
		aString = aClass.getSource();
		if (isSubClass && (aString.isEmpty()))
		{
			aString = parentClass.getSource();
		}
		if (!aString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_source", aString); //$NON-NLS-1$
		}

		return b.toString();
	}

	@Override
	public String getHTMLInfo(Skill skill)
	{
		if (skill == null)
		{
			return EMPTY_STRING;
		}

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(skill));

		infoText.appendLineBreak();
		String typeString = StringUtil.join(skill.getTrueTypeList(true), ". ");
		if (StringUtils.isNotBlank(typeString))
		{
			infoText.appendI18nElement("in_igInfoLabelTextType", //$NON-NLS-1$
				typeString);
			infoText.appendLineBreak();
		}

		appendFacts(infoText, skill);

		String aString = SkillInfoUtilities.getKeyStatFromStats(pc, skill);
		if (!aString.isEmpty())
		{
			infoText.appendI18nElement("in_iskKEY_STAT", aString); //$NON-NLS-1$
		}
		infoText.appendLineBreak();
		infoText.appendI18nElement("in_iskUntrained", //$NON-NLS-1$
			skill.getSafe(ObjectKey.USE_UNTRAINED) ? LanguageBundle.getString("in_yes")
				: LanguageBundle.getString("in_no"));
		infoText.appendLineBreak();
		infoText.appendI18nElement("in_iskEXCLUSIVE", //$NON-NLS-1$
			skill.getSafe(ObjectKey.EXCLUSIVE) ? LanguageBundle.getString("in_yes")
				: LanguageBundle.getString("in_no"));

		String bString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, skill.getPrerequisiteList(), false);
		if (!bString.isEmpty())
		{
			infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
				bString);
		}

		aString = AllowUtilities.getAllowInfo(pc, skill);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		//Description
		String desc = pc.getDescription(skill);
		if (!desc.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piWrapDesc(skill, desc, false));
		}

		bString = skill.getSource();
		if (!bString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_iskSource", bString); //$NON-NLS-1$
		}

		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_SKILL_MOD_BREAKDOWN, false))
		{
			bString = SkillCostDisplay.getModifierExplanation(skill, pc, false);
			if (!bString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_iskHtml_PcMod", //$NON-NLS-1$
					bString);
			}
		}

		if (PCGenSettings.OPTIONS_CONTEXT.getBoolean(PCGenSettings.OPTION_SHOW_SKILL_RANK_BREAKDOWN, false))
		{
			bString = SkillCostDisplay.getRanksExplanation(pc, skill);
			if (bString.isEmpty())
			{
				bString = LanguageBundle.getString("in_none"); //$NON-NLS-1$
			}
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_iskHtml_Ranks", //$NON-NLS-1$
				bString);
		}

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(AbilityFacade abilityFacade)
	{
		if (!(abilityFacade instanceof Ability ability))
		{
			return EMPTY_STRING;
		}

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();
		infoText.appendTitleElement(OutputNameFormatting.piString(ability));
		infoText.appendLineBreak();

		infoText.appendI18nFormattedElement("Ability.Info.Type", //$NON-NLS-1$
			StringUtil.join(ability.getTrueTypeList(true), ". ")); //$NON-NLS-1$

		BigDecimal costStr = ability.getSafe(ObjectKey.SELECTION_COST);
		if (!costStr.equals(BigDecimal.ONE))
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

		appendFacts(infoText, ability);

		final String cString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, ability.getPrerequisiteList(), false);
		if (!cString.isEmpty())
		{
			infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
				cString);
		}

		String aString = AllowUtilities.getAllowInfo(pc, ability);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		infoText.appendLineBreak();
		infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
			getDescription(abilityFacade));

		List<CNAbility> wrappedAbility = getWrappedAbility(ability);

		if (ability.getSafeSizeOfMapFor(MapKey.ASPECT) > 0)
		{
			Set<AspectName> aspectKeys = ability.getKeysFor(MapKey.ASPECT);
			StringBuilder buff = new StringBuilder(100);
			for (AspectName key : aspectKeys)
			{
				if (buff.length() > 0)
				{
					buff.append(", ");
				}
				//Assert here that the actual text displayed is not critical
				buff.append(Aspect.printAspect(pc, key, wrappedAbility));
			}
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("Ability.Info.Aspects", //$NON-NLS-1$
				buff.toString());
		}

		final String bene = BenefitFormatting.getBenefits(pc, wrappedAbility);
		if (bene != null && !bene.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("Ability.Info.Benefit", //$NON-NLS-1$
				BenefitFormatting.getBenefits(pc, wrappedAbility));
		}

		infoText.appendLineBreak();
		infoText.appendI18nFormattedElement("in_InfoSource", //$NON-NLS-1$
			ability.getSource());

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(Deity deity)
	{
		if (deity == null)
		{
			return EMPTY_STRING;
		}
		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(deity));
		infoText.appendLineBreak();

		infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
			DescriptionFormatting.piWrapDesc(deity, pc.getDescription(deity), false));

		appendFacts(infoText, deity);

		String aString = getPantheons(deity);
		if (aString != null)
		{
			infoText.appendSpacer();
			infoText.appendI18nElement("in_pantheon", aString); //$NON-NLS-1$
		}

		infoText.appendSpacer();
		infoText.appendI18nElement("in_domains", getDomains(deity)); //$NON-NLS-1$

		List<CDOMReference<WeaponProf>> dwp = deity.getListFor(ListKey.DEITYWEAPON);
		if (dwp != null)
		{
			infoText.appendSpacer();
			infoText.appendI18nFormattedElement("in_deityFavWeap", //$NON-NLS-1$
				ReferenceUtilities.joinLstFormat(dwp, "|"));
		}

		aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, deity.getPrerequisiteList(), false);
		if (!aString.isEmpty())
		{
			infoText.appendSpacer();
			infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
				aString);
		}

		aString = AllowUtilities.getAllowInfo(pc, deity);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		aString = deity.getSource();
		if (!aString.isEmpty())
		{
			infoText.appendSpacer();
			infoText.appendI18nFormattedElement("in_InfoSource", //$NON-NLS-1$
				aString);
		}

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(QualifiedObject<Domain> domain)
	{
		Domain aDomain = domain.getRawObject();

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		if (aDomain != null)
		{
			infoText.appendTitleElement(OutputNameFormatting.piString(aDomain));

			appendFacts(infoText, aDomain);

			String aString = pc.getDescription(aDomain);
			if (!aString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_domainGrant", //$NON-NLS-1$
					aString);
			}

			aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, aDomain.getPrerequisiteList(), false);
			if (!aString.isEmpty())
			{
				infoText.appendI18nFormattedElement("in_InfoRequirements", //$NON-NLS-1$
					aString);
			}

			aString =
					PrerequisiteUtilities.preReqHTMLStringsForList(pc, aDomain, domain.getPrerequisiteList(), false);
			if (!aString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nFormattedElement("in_domainRequirements", //$NON-NLS-1$
					aString);
			}

			aString = AllowUtilities.getAllowInfo(pc, aDomain);
			if (!aString.isEmpty())
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
			}

			aString = SourceFormat.getFormattedString(aDomain, Globals.getSourceDisplay(), true);
			if (!aString.isEmpty())
			{
				infoText.appendI18nFormattedElement("in_InfoSource", //$NON-NLS-1$
					aString);
			}

		}

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(EquipmentFacade equipFacade)
	{
		if (equipFacade == null || !(equipFacade instanceof Equipment equip))
		{
			return EMPTY_STRING;
		}

		final HtmlInfoBuilder b = getEquipmentHtmlInfo(equip);

		String bString = equip.getSource();
		if (!bString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextSource", bString); //$NON-NLS-1$
		}
		b.appendLineBreak();

		return b.toString();
	}

	private HtmlInfoBuilder getEquipmentHtmlInfo(Equipment equip)
	{
		final StringBuilder title = new StringBuilder(50);
		title.append(OutputNameFormatting.piString(equip));

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

		String baseName = equip.getBaseItemName();
		if (StringUtils.isNotEmpty(baseName) && !baseName.equals(equip.getName()))
		{
			b.appendI18nElement("in_igInfoLabelTextBaseItem", //$NON-NLS-1$
				baseName);
			b.appendLineBreak();
		}

		b.appendI18nElement("in_igInfoLabelTextType", //$NON-NLS-1$
			StringUtil.join(equip.getTrueTypeList(true), ". "));

		appendFacts(b, equip);

		//
		// Should only be meaningful for weapons, but if included on some other piece of
		// equipment, show it anyway
		//
		if (equip.isWeapon() || equip.get(ObjectKey.WIELD) != null)
		{
			b.appendLineBreak();
			final WieldCategory wCat = equip.getEffectiveWieldCategory(pc);
			if (wCat != null)
			{
				b.appendI18nElement("in_igInfoLabelTextWield", //$NON-NLS-1$
					wCat.getDisplayName());
			}
		}

		//
		// Only meaningful for weapons, armor and shields
		//
		if (equip.isWeapon() || equip.isArmor() || equip.isShield())
		{
			b.appendLineBreak();
			final String value = (pc.isProficientWith(equip) && equip.meetsPreReqs(pc))
				? LanguageBundle.getString("in_igInfoLabelTextYes") //$NON-NLS-1$
				: (SettingsHandler.getPrereqFailColorAsHtmlStart()
						+ LanguageBundle.getString("in_igInfoLabelTextNo") //$NON-NLS-1$
						+ SettingsHandler.getPrereqFailColorAsHtmlEnd());
			b.appendI18nElement("in_igInfoLabelTextProficient", value); //$NON-NLS-1$
		}

		final String cString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, equip.getPrerequisiteList(), false);

		if (!cString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextReq", cString); //$NON-NLS-1$
		}

		String aString = AllowUtilities.getAllowInfo(pc, equip);
		if (!aString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		BigDecimal cost = equip.getCost(pc);
		if (cost != BigDecimal.ZERO)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igEqModelColCost", COST_FMT.format(cost.doubleValue())); //$NON-NLS-1$
			b.append(" ");
			b.append(SettingsHandler.getGameAsProperty().get().getCurrencyDisplay());
		}

		String bString = Globals.getGameModeUnitSet().displayWeightInUnitSet(equip.getWeight(pc).doubleValue());

		if (!bString.isEmpty())
		{
			b.appendLineBreak();
			bString += Globals.getGameModeUnitSet().getWeightUnit();
			b.appendI18nElement("in_igInfoLabelTextWeight", bString); //$NON-NLS-1$

		}

		int a = EqToken.getMaxDexTokenInt(pc, equip);

		if (a != Constants.MAX_MAXDEX)
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextMaxDex", Integer.toString(a)); //$NON-NLS-1$
		}

		a = EqToken.getAcCheckTokenInt(pc, equip);

		if (equip.isArmor() || equip.isShield() || (a != 0))
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextAcCheck", Integer.toString(a)); //$NON-NLS-1$
		}

		if (!SettingsHandler.getGameAsProperty().get().getACText().isEmpty())
		{
			a = equip.getACMod(pc);

			if (equip.isArmor() || equip.isShield() || (a != 0))
			{
				b.appendSpacer();
				b.appendElement(LanguageBundle.getFormattedString("in_igInfoLabelTextAcBonus", //$NON-NLS-1$
					SettingsHandler.getGameAsProperty().get().getACText()), Integer.toString(a));
			}
		}

		if (SettingsHandler.getGameAsProperty().get().getTabShown(Tab.SPELLS))
		{
			a = EqToken.getSpellFailureTokenInt(pc, equip);

			if (equip.isArmor() || equip.isShield() || (a != 0))
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextArcaneFailure", Integer.toString(a)); //$NON-NLS-1$
			}
		}

		bString = SettingsHandler.getGameAsProperty().get().getDamageResistanceText();

		if (!bString.isEmpty())
		{
			a = EqToken.getEdrTokenInt(pc, equip);

			if (equip.isArmor() || equip.isShield() || (a != 0))
			{
				b.appendSpacer();
				b.appendElement(bString, Integer.toString(a));
			}
		}

		bString = equip.moveString();

		if (!bString.isEmpty())
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextMove", bString); //$NON-NLS-1$
		}

		bString = equip.getSize();

		if (!bString.isEmpty())
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextSize", bString); //$NON-NLS-1$
		}

		bString = equip.getDamage(pc);

		if (!bString.isEmpty())
		{

			if (equip.isDouble())
			{
				bString += "/" + equip.getAltDamage(pc); //$NON-NLS-1$
			}

			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextDamage", bString); //$NON-NLS-1$
		}

		String critRangeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.CRITRANGE);
		if (critRangeVar == null)
		{
			int critrange = EqToken.getOldBonusedCritRange(pc, equip, true);
			int altcritrange = EqToken.getOldBonusedCritRange(pc, equip, false);
			bString = critrange == 0 ? EMPTY_STRING : Integer.toString(critrange);
			if (equip.isDouble() && critrange != altcritrange)
			{
				bString += "/" //$NON-NLS-1$
					+ (altcritrange == 0 ? EMPTY_STRING : Integer.toString(altcritrange));
			}
		}
		else
		{
			bString = WeaponToken.getNewCritRangeString(pc, equip, critRangeVar);
		}

		if (!bString.isEmpty())
		{
			b.appendSpacer();
			b.appendI18nElement("in_ieInfoLabelTextCritRange", bString); //$NON-NLS-1$
		}

		String critMultVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.CRITMULT);
		if (critMultVar == null)
		{
			bString = EqToken.multAsString(equip.getCritMultiplier());
			if (equip.isDouble() && (equip.getCritMultiplier() != equip.getAltCritMultiplier()))
			{
				bString += "/" + EqToken.multAsString(equip.getAltCritMultiplier()); //$NON-NLS-1$
			}
		}
		else
		{
			bString = WeaponToken.getNewCritMultString(pc, equip, critMultVar);
		}

		if (!bString.isEmpty())
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextCritMult", bString); //$NON-NLS-1$
		}

		if (equip.isWeapon())
		{
			bString = Globals.getGameModeUnitSet().displayDistanceInUnitSet(EqToken.getRange(pc, equip));

			if (!bString.isEmpty())
			{
				b.appendSpacer();
				b.appendI18nElement("in_igInfoLabelTextRange", bString //$NON-NLS-1$
					+ Globals.getGameModeUnitSet().getDistanceUnit());
			}
		}

		bString = equip.getContainerCapacityString();

		if (!bString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextContainer", bString); //$NON-NLS-1$
		}

		bString = equip.getContainerContentsString();

		if (!bString.isEmpty())
		{
			b.appendSpacer();
			b.appendI18nElement("in_igInfoLabelTextCurrentlyContains", bString); //$NON-NLS-1$
		}

		final int charges = equip.getRemainingCharges();

		if (charges >= 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextCharges", //$NON-NLS-1$
				Integer.valueOf(charges).toString());
		}

		Map<String, String> qualityMap = equip.getMapFor(MapKey.QUALITY);
		if (qualityMap != null && !qualityMap.isEmpty())
		{
			Set<String> qualities = new TreeSet<>();
			for (Map.Entry<String, String> me : qualityMap.entrySet())
			{
				qualities.add(me.getKey() + ": " + me.getValue());
			}

			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextQualities", //$NON-NLS-1$
				StringUtil.join(qualities, ", "));
		}
		//Description
		String desc = pc.getDescription(equip);
		if (!desc.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piWrapDesc(equip, desc, false));
		}
		String IDS = equip.getInterestingDisplayString(pc);
		if (!IDS.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextProp", IDS); //$NON-NLS-1$
		}

		String note = equip.getNote();
		if (!note.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextNote", note); //$NON-NLS-1$
		}

		return b;
	}

	@Override
	public String getHTMLInfo(EquipmentModifier equipMod, EquipmentFacade equipFacade)
	{
		if (equipMod == null || equipFacade == null
			|| !(equipFacade instanceof Equipment equip))
		{
			return EMPTY_STRING;
		}

		final HtmlInfoBuilder b = new HtmlInfoBuilder(null, false);
		b.appendTitleElement(OutputNameFormatting.piString(equipMod));
		b.appendLineBreak();

		b.appendI18nElement("in_igInfoLabelTextType", //$NON-NLS-1$
			StringUtil.join(equipMod.getTrueTypeList(true), ". "));

		// Various cost types
		int iPlus = equipMod.getSafe(IntegerKey.PLUS);
		if (iPlus != 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextPlus", String.valueOf(iPlus));
		}
		Formula baseCost = equipMod.getSafe(FormulaKey.BASECOST);
		if (!"0".equals(baseCost.toString()))
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextPrecost", String.valueOf(baseCost));
		}
		Formula cost = equipMod.getSafe(FormulaKey.COST);
		if (!"0".equals(cost.toString()))
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igEqModelColCost", String.valueOf(cost));
		}

		//Description
		String desc = pc.getDescription(equipMod);
		if (!desc.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piWrapDesc(equipMod, desc, false));
		}

		// Special properties
		StringBuilder sb = new StringBuilder(100);
		boolean first = true;
		for (SpecialProperty sp : equipMod.getSafeListFor(ListKey.SPECIAL_PROPERTIES))
		{
			if (!first)
			{
				sb.append(", ");
			}
			first = false;
			sb.append(sp.getDisplayName());
		}
		if (sb.length() > 0)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextSprop", sb.toString());
		}

		final String cString =
				PrerequisiteUtilities.preReqHTMLStringsForList(pc, equip, equipMod.getPrerequisiteList(), false);
		if (!cString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextReq", cString); //$NON-NLS-1$
		}

		String aString = AllowUtilities.getAllowInfo(pc, equipMod);
		if (!aString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		String bString = equipMod.getSource();
		if (!bString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_igInfoLabelTextSource", bString); //$NON-NLS-1$
		}
		b.appendLineBreak();

		return b.toString();
	}

	@Override
	public String getHTMLInfo(PCTemplate template)
	{
		if (template == null)
		{
			return EMPTY_STRING;
		}

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(template));

		appendFacts(infoText, template);

		RaceType rt = template.get(ObjectKey.RACETYPE);
		if (rt != null)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_irInfoRaceType", rt.toString()); //$NON-NLS-1$
		}

		if (!template.getType().isEmpty())
		{
			infoText.appendSpacer();
			infoText.appendI18nElement("in_irInfoType", template.getType()); //$NON-NLS-1$
		}

		String aString = pc.getDescription(template);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				aString);
		}

		aString = TemplateModifier.modifierString(template, pc);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_modifier", aString); //$NON-NLS-1$
		}

		aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, template.getPrerequisiteList(), false);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		aString = AllowUtilities.getAllowInfo(pc, template);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		aString = template.getSource();
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_sourceLabel", aString); //$NON-NLS-1$
		}

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(Kit kit)
	{
		if (kit == null)
		{
			return EMPTY_STRING;
		}

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();

		infoText.appendTitleElement(OutputNameFormatting.piString(kit));

		appendFacts(infoText, kit);

		String aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, kit.getPrerequisiteList(), false);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		aString = AllowUtilities.getAllowInfo(pc, kit);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		List<BaseKit> sortedObjects = new ArrayList<>(kit.getSafeListFor(ListKey.KIT_TASKS));
		sortedObjects.sort(Comparator.comparing(BaseKit::getObjectName));

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
				else
				{
					infoText.appendLineBreak();
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

		BigDecimal totalCost = kit.getTotalCost(pc);
		if (totalCost != null)
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_kitInfo_TotalCost", //$NON-NLS-1$
				COST_FMT.format(totalCost), SettingsHandler.getGameAsProperty().get().getCurrencyDisplay());
		}

		String desc = pc.getDescription(kit);
		if (!desc.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nFormattedElement("in_InfoDescription", //$NON-NLS-1$
				DescriptionFormatting.piWrapDesc(kit, desc, false));
		}

		aString = kit.getSource();
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_sourceLabel", aString); //$NON-NLS-1$
		}
		//TODO ListKey.KIT_TASKS
		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(TempBonusFacade tempBonusFacade)
	{
		if (tempBonusFacade == null)
		{
			return EMPTY_STRING;
		}

		if (!(tempBonusFacade instanceof TempBonusFacadeImpl tempBonus))
		{
			final HtmlInfoBuilder infoText = new HtmlInfoBuilder();
			infoText.appendTitleElement(tempBonusFacade.toString());
			return infoText.toString();
		}

		CDOMObject originObj = tempBonus.getOriginObj();

		final HtmlInfoBuilder infoText;
		if (originObj instanceof Equipment)
		{
			infoText = getEquipmentHtmlInfo((Equipment) originObj);
		}
		else
		{
			infoText = new HtmlInfoBuilder();
			infoText.appendTitleElement(OutputNameFormatting.piString(originObj));
			infoText.append(" (").append(tempBonus.getOriginType()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if (tempBonus.getTarget() != null)
		{
			String targetName = charDisplay.getName();
			if (tempBonus.getTarget() instanceof CDOMObject)
			{
				targetName = ((CDOMObject) tempBonus.getTarget()).getKeyName();
			}

			infoText.appendLineBreak();
			infoText.appendI18nElement("in_itmInfoLabelTextTarget", targetName); //$NON-NLS-1$

			StringBuilder bonusValues = new StringBuilder(100);
			Map<BonusObj, TempBonusInfo> bonusMap = pc.getTempBonusMap(originObj.getKeyName(), targetName);
			boolean first = true;
			List<BonusObj> bonusList = new ArrayList<>(bonusMap.keySet());
			bonusList.sort(BONUS_COMPARATOR);
			for (BonusObj bonusObj : bonusList)
			{
				if (!first)
				{
					bonusValues.append(", "); //$NON-NLS-1$
				}
				first = false;
				String adj = ADJ_FMT.format(bonusObj.resolve(pc, "")); //$NON-NLS-1$
				bonusValues.append(adj).append(" ").append(bonusObj.getDescription()); //$NON-NLS-1$
			}
			if (bonusValues.length() > 0)
			{
				infoText.appendLineBreak();
				infoText.appendI18nElement("in_itmInfoLabelTextEffect", //$NON-NLS-1$
					bonusValues.toString());
			}
		}

		if (originObj instanceof Spell aSpell)
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_spellDuration", //$NON-NLS-1$
				aSpell.getListAsString(ListKey.DURATION));
			infoText.appendSpacer();
			infoText.appendI18nElement("in_spellRange", //$NON-NLS-1$
				aSpell.getListAsString(ListKey.RANGE));
			infoText.appendSpacer();
			infoText.appendI18nElement("in_spellTarget", //$NON-NLS-1$
				aSpell.getSafe(StringKey.TARGET_AREA));
		}

		String aString = originObj.getSafe(StringKey.TEMP_DESCRIPTION);
		if (StringUtils.isEmpty(aString) && originObj instanceof Spell sp)
		{
			aString = DescriptionFormatting.piWrapDesc(sp, pc.getDescription(sp), false);
		}
		else if (StringUtils.isEmpty(aString) && originObj instanceof Ability ab)
		{
			List<CNAbility> wrappedAbility =
					Collections.singletonList(CNAbilityFactory.getCNAbility(ab.getCDOMCategory(), Nature.NORMAL, ab));
			aString = DescriptionFormatting.piWrapDesc(ab, pc.getDescription(wrappedAbility), false);
		}
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_itmInfoLabelTextDesc", aString); //$NON-NLS-1$
		}

		aString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, originObj.getPrerequisiteList(), false);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		aString = AllowUtilities.getAllowInfo(pc, originObj);
		if (!aString.isEmpty())
		{
			infoText.appendLineBreak();
			infoText.appendI18nElement("in_requirements", aString); //$NON-NLS-1$
		}

		infoText.appendLineBreak();
		infoText.appendI18nElement("in_itmInfoLabelTextSource", //$NON-NLS-1$
			SourceFormat.getFormattedString(originObj, Globals.getSourceDisplay(), true));

		return infoText.toString();
	}

	@Override
	public String getHTMLInfo(InfoFacade facade)
	{
		if (facade == null)
		{
			return EMPTY_STRING;
		}

		// Use a more detailed info if we can
		if (facade instanceof AbilityFacade)
		{
			return getHTMLInfo((AbilityFacade) facade);
		}
		if (facade instanceof PCClass)
		{
			return getHTMLInfo((PCClass) facade, null);
		}
		if (facade instanceof EquipmentFacade)
		{
			return getHTMLInfo((EquipmentFacade) facade);
		}
		if (facade instanceof SpellFacade)
		{
			return getHTMLInfo((SpellFacade) facade);
		}
		if (facade instanceof TempBonusFacade)
		{
			return getHTMLInfo((TempBonusFacade) facade);
		}

		final HtmlInfoBuilder infoText = new HtmlInfoBuilder();
		infoText.appendTitleElement(facade.toString());
		infoText.appendLineBreak();

		if (!facade.getType().isEmpty())
		{
			infoText.appendI18nElement("in_irInfoType", facade.getType()); //$NON-NLS-1$
			infoText.appendLineBreak();
		}

		infoText.appendI18nElement("in_itmInfoLabelTextSource", //$NON-NLS-1$
			facade.getSource());

		return infoText.toString();
	}

	private final Comparator<BonusObj> BONUS_COMPARATOR =
			Comparator.comparing(BonusObj::getTypeOfBonus)
			.thenComparing(BonusObj::getBonusInfo);

	@Override
	public String getLevelAdjustment(Race race)
	{
		return ADJ_FMT.format(race.getSafe(FormulaKey.LEVEL_ADJUSTMENT).resolve(pc, EMPTY_STRING));
	}

	@Override
	public int getNumMonsterClassLevels(Race race)
	{
		LevelCommandFactory levelCommandFactory = race.get(ObjectKey.MONSTER_CLASS);
		if (levelCommandFactory == null)
		{
			return 0;
		}
		return levelCommandFactory.getLevelCount().resolve(pc, EMPTY_STRING).intValue();
	}

	@Override
	public String getPreReqHTML(Race race)
	{
		return "<html>"
				+ PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, race.getPrerequisiteList(), false)
				+ AllowUtilities.getAllowInfo(pc, race)
				+ "</html>";
	}

	@Override
	public String getStatAdjustments(Race race)
	{
		final StringBuilder retString = new StringBuilder(100);

		for (PCStat stat : charDisplay.getStatSet())
		{
			if (charDisplay.isNonAbility(stat))
			{
				if (retString.length() > 0)
				{
					retString.append(' ');
				}

				retString.append(stat.getKeyName()).append(":Nonability");
			}
			else
			{
				if (BonusCalc.getStatMod(race, stat, pc) != 0)
				{
					if (retString.length() > 0)
					{
						retString.append(' ');
					}

					retString.append(stat.getKeyName()).append(":").append(BonusCalc.getStatMod(race, stat, pc));
				}
			}
		}

		return retString.toString();
	}

	@Override
	public String getVision(Race race)
	{
		return VisionDisplay.getVision(pc, race);
	}

	@Override
	public float getCost(EquipmentFacade equipment)
	{
		if (equipment instanceof Equipment)
		{
			return ((Equipment) equipment).getCost(pc).floatValue();
		}
		return 0;
	}

	@Override
	public float getWeight(EquipmentFacade equipment)
	{
		if (equipment instanceof Equipment)
		{
			Float weight = ((Equipment) equipment).getWeight(pc);
			return (float) Globals.getGameModeUnitSet().convertWeightToUnitSet(weight);
		}
		return 0;
	}

	@Override
	public String getLevelAdjustment(PCTemplate template)
	{
		return ADJ_FMT.format(template.getSafe(FormulaKey.LEVEL_ADJUSTMENT).resolve(pc, EMPTY_STRING));
	}

	@Override
	public String getModifier(PCTemplate template)
	{
		return TemplateModifier.modifierString(template, pc);
	}

	@Override
	public String getPreReqHTML(PCTemplate template)
	{
		return "<html>"
				+ PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, template.getPrerequisiteList(), false)
				+ AllowUtilities.getAllowInfo(pc, template)
				+ "</html>";
	}

	@Override
	public String getHTMLInfo(SpellFacade spell)
	{
		Objects.requireNonNull(spell);
		CharacterSpell cs = spell.getCharSpell();
		SpellInfo si = spell.getSpellInfo();
		Spell aSpell = cs.getSpell();

		if (aSpell == null)
		{
			return EMPTY_STRING;
		}
		final HtmlInfoBuilder b = new HtmlInfoBuilder(OutputNameFormatting.piString(aSpell));

		if (si != null)
		{
			final String addString = si.toString(); // would add [featList]
			if (!addString.isEmpty())
			{
				b.append(" ").append(addString); //$NON-NLS-1$
			}
			b.appendLineBreak();
			b.appendI18nElement("InfoSpells.level.title", Integer.toString(si.getOriginalLevel())); //$NON-NLS-1$
		}
		b.appendLineBreak();

		String classlevels = aSpell.getListAsString(ListKey.SPELL_CLASSLEVEL);
		if (StringUtils.isNotEmpty(classlevels))
		{
			b.appendI18nElement("in_clClass", classlevels);
			b.appendLineBreak();
		}
		String domainlevels = aSpell.getListAsString(ListKey.SPELL_DOMAINLEVEL);
		if (StringUtils.isNotEmpty(domainlevels))
		{
			b.appendI18nElement("in_domains", domainlevels);
			b.appendLineBreak();
		}

		b.appendI18nElement("in_spellSchool", aSpell.getListAsString(ListKey.SPELL_SCHOOL));

		String subSchool = aSpell.getListAsString(ListKey.SPELL_SUBSCHOOL);
		if (StringUtils.isNotEmpty(subSchool))
		{
			b.append(" (").append(subSchool).append(")");
		}
		String spellDescriptor = aSpell.getListAsString(ListKey.SPELL_DESCRIPTOR);
		if (StringUtils.isNotEmpty(spellDescriptor))
		{
			b.append(" [").append(spellDescriptor).append("]");
		}
		b.appendLineBreak();

		appendFacts(b, aSpell);
		b.appendLineBreak();

		b.appendI18nElement("in_spellComponents", aSpell.getListAsString(ListKey.COMPONENTS));
		b.appendLineBreak();

		b.appendI18nElement("in_spellCastTime", aSpell.getListAsString(ListKey.CASTTIME));
		b.appendLineBreak();

		b.appendI18nElement("in_spellDuration", pc.parseSpellString(cs, aSpell.getListAsString(ListKey.DURATION)));
		b.appendLineBreak();

		b.appendI18nElement("in_spellRange", pc.getSpellRange(cs, si));
		b.appendSpacer();
		b.appendI18nElement("in_spellTarget", pc.parseSpellString(cs, aSpell.getSafe(StringKey.TARGET_AREA)));
		b.appendLineBreak();

		b.appendI18nElement("in_spellSavingThrow", aSpell.getListAsString(ListKey.SAVE_INFO));
		b.appendSpacer();
		b.appendI18nElement("in_spellSpellResist", aSpell.getListAsString(ListKey.SPELL_RESISTANCE));
		b.appendLineBreak();

		b.appendLineBreak();
		b.appendI18nElement("in_descrip", pc.parseSpellString(cs, //$NON-NLS-1$
			pc.getDescription(aSpell)));

		String cString = PrerequisiteUtilities.preReqHTMLStringsForList(pc, null, aSpell.getPrerequisiteList(), false);
		if (!cString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", cString); //$NON-NLS-1$
		}
		cString = AllowUtilities.getAllowInfo(pc, aSpell);
		if (!cString.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_requirements", cString); //$NON-NLS-1$
		}

		b.appendLineBreak();

		String spellSource = SourceFormat.getFormattedString(aSpell, Globals.getSourceDisplay(), true);
		if (!spellSource.isEmpty())
		{
			b.appendLineBreak();
			b.appendI18nElement("in_source", spellSource); //$NON-NLS-1$
		}

		return b.toString();
	}

	@Override
	public String getSpellBookInfo(String name)
	{
		SpellBook book = charDisplay.getSpellBookByName(name);
		if (book == null)
		{
			return EMPTY_STRING;
		}

		return switch (book.getType())
				{
					case SpellBook.TYPE_PREPARED_LIST -> produceSpellListInfo(book);
					case SpellBook.TYPE_SPELL_BOOK -> produceSpellBookInfo(book);
					default -> EMPTY_STRING;
				};
	}

	/**
	 * Produce the HTML info label for a prepared spell list.
	 * @param spelllist The spell list being output.
	 * @return String  The HTML info for the list.
	 */
	private String produceSpellListInfo(SpellBook spelllist)
	{
		final HtmlInfoBuilder b = new HtmlInfoBuilder(spelllist.getName());

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
		for (PCClass pcClass : charDisplay.getClassSet())
		{
			Map<Integer, Integer> spellCountMap = new TreeMap<>();
			int highestSpellLevel = -1;
			Collection<? extends CharacterSpell> sp = charDisplay.getCharacterSpells(pcClass);
			List<CharacterSpell> classSpells = new ArrayList<>(sp);
			// Add in the spells granted by objects
			pc.addBonusKnownSpellsToList(pcClass, classSpells);
			for (CharacterSpell charSpell : classSpells)
			{
				for (SpellInfo spellInfo : charSpell.getInfoList())
				{
					if (!spelllist.getName().equals(spellInfo.getBook()))
					{
						continue;
					}
					int level = spellInfo.getActualLevel();

					int count = spellCountMap.getOrDefault(level, 0);
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
				b.append(OutputNameFormatting.piString(pcClass));
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
					b.append(String.valueOf(spellCountMap.getOrDefault(i, 0)));
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
		final HtmlInfoBuilder b = new HtmlInfoBuilder(book.getName());

		b.append(" ("); //$NON-NLS-1$
		b.append(book.getTypeName());
		if (book.getName().equals(charDisplay.getSpellBookNameToAutoAddKnown()))
		{
			b.append(TWO_SPACES).append(BOLD);
			b.append(LanguageBundle.getString("InfoSpellsSubTab.DefaultKnownBook")) //$NON-NLS-1$
				.append(END_BOLD);
		}
		b.append(")"); //$NON-NLS-1$
		b.appendLineBreak();

		b.append(LanguageBundle.getFormattedString("InfoSpells.html.spellbook.details", //$NON-NLS-1$
                book.getNumPages(), book.getNumPagesUsed(), book.getPageFormula(), book.getNumSpells()));

		if (book.getDescription() != null)
		{
			b.appendLineBreak();
			b.appendI18nElement("in_descrip", book.getDescription()); //$NON-NLS-1$
		}
		return b.toString();
	}

	@Override
	public String getDescription(AbilityFacade ability)
	{
		if (ability == null || !(ability instanceof Ability))
		{
			return EMPTY_STRING;
		}

		try
		{
			Ability a = (Ability) ability;
			List<CNAbility> wrappedAbility = getWrappedAbility(a);
			return DescriptionFormatting.piWrapDesc(a, pc.getDescription(wrappedAbility), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + ability, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(Race race)
	{
		if (race == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(race, pc.getDescription(race), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + race, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(PCTemplate template)
	{
		if (template == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(template, pc.getDescription(template), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + template, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(PCClass pcClass)
	{
		if (pcClass == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(pcClass, pc.getDescription(pcClass), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + pcClass, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(Skill skill)
	{
		if (skill == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(skill, pc.getDescription(skill), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + skill, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(EquipmentFacade equipFacade)
	{
		if (equipFacade == null || !(equipFacade instanceof Equipment))
		{
			return EMPTY_STRING;
		}
		try
		{
			Equipment equip = (Equipment) equipFacade;
			return DescriptionFormatting.piWrapDesc(equip, pc.getDescription(equip), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + equipFacade, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(Kit kit)
	{
		if (kit == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(kit, pc.getDescription(kit), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + kit, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(Deity deity)
	{
		if (deity == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(deity, pc.getDescription(deity), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + deity, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(Domain domain)
	{
		if (domain == null)
		{
			return EMPTY_STRING;
		}
		try
		{
			return DescriptionFormatting.piWrapDesc(domain, pc.getDescription(domain), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + domain, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(SpellFacade spellFacade)
	{
		if (spellFacade == null || !(spellFacade instanceof SpellFacadeImplem))
		{
			return EMPTY_STRING;
		}
		try
		{
			SpellFacadeImplem spell = (SpellFacadeImplem) spellFacade;
			Spell aSpell = spell.getSpell();
			if (aSpell == null)
			{
				return EMPTY_STRING;
			}
			return DescriptionFormatting.piWrapDesc(aSpell, pc.getDescription(aSpell), false);
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + spellFacade, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	@Override
	public String getDescription(TempBonusFacade tempBonusFacade)
	{
		if (tempBonusFacade == null || !(tempBonusFacade instanceof TempBonusFacadeImpl))
		{
			return EMPTY_STRING;
		}
		try
		{
			TempBonusFacadeImpl tempBonus = (TempBonusFacadeImpl) tempBonusFacade;
			CDOMObject originObj = tempBonus.getOriginObj();
			String desc = originObj.getSafe(StringKey.TEMP_DESCRIPTION);
			if (StringUtils.isEmpty(desc))
			{
				if (originObj instanceof Spell sp)
				{
					desc = DescriptionFormatting.piWrapDesc(sp, pc.getDescription(sp), false);
				}
				else if (originObj instanceof Ability ab)
				{
					List<CNAbility> wrappedAbility = Collections
						.singletonList(CNAbilityFactory.getCNAbility(ab.getCDOMCategory(), Nature.NORMAL, ab));
					desc = DescriptionFormatting.piWrapDesc(ab, pc.getDescription(wrappedAbility), false);
				}
			}
			return desc;
		}
		catch (Exception e)
		{
			Logging.errorPrint("Failed to get description for " + tempBonusFacade, e); //$NON-NLS-1$
			return EMPTY_STRING;
		}
	}

	/**
	 * Retrieve a wrapped instance of the provided ability, whether the
	 * character has the ability or not. This will either be a list of the
	 * specific occurrences of the ability the character, or a list of a new
	 * CNAbility if the character does not possess the ability.
	 *
	 * @param a The ability to be wrapped.
	 * @return The list of wrapped abilities.
	 */
	private List<CNAbility> getWrappedAbility(Ability a)
	{
		/*
		 * TODO this is probably a problem in that it is doing a target (all
		 * associations for an ability, not related to the current CNAbility
		 * selected in the UI)
		 */
		List<CNAbility> wrappedAbility = pc.getMatchingCNAbilities(a);
		if (wrappedAbility.isEmpty())
		{
			CNAbility cna = CNAbilityFactory.getCNAbility(a.getCDOMCategory(), Nature.NORMAL, a);
			wrappedAbility.add(cna);
		}
		return wrappedAbility;
	}

	@Override
	public String getDomains(Deity deity)
	{
		if (deity == null)
		{
			return EMPTY_STRING;
		}
		Set<String> set = new TreeSet<>();
		for (CDOMReference<Domain> ref : deity.getSafeListMods(Deity.DOMAINLIST))
		{
			ref.getContainedObjects()
				.forEach(domain -> set.add(OutputNameFormatting.piString(domain)));
		}
		return StringUtil.join(set, ", "); //$NON-NLS-1$
	}

	@Override
	public String getPantheons(Deity deity)
	{
		if (deity == null)
		{
			return EMPTY_STRING;
		}
		Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		FactSetKey<String> fk = FactSetKey.valueOf("Pantheon");
		deity.getSafeSetFor(fk).forEach(indirect -> set.add(indirect.get()));
		return StringUtil.join(set, ","); //$NON-NLS-1$

	}

	/**
	 * Get a display string of the deity's favored weapons.
	 * @param deity The deity to be output.
	 * @return The comma separated list of weapons.
	 */
	@Override
	public String getFavoredWeapons(Deity deity)
	{
		if (deity == null)
		{
			return EMPTY_STRING;
		}
		List<CDOMReference<WeaponProf>> wpnList = deity.getSafeListFor(ListKey.DEITYWEAPON);
		return ReferenceUtilities.joinLstFormat(wpnList, ",");
	}

	@Override
	public String getChoices(AbilityFacade abilityFacade)
	{
		if (abilityFacade == null || !(abilityFacade instanceof final Ability ability))
		{
			return EMPTY_STRING;
		}
		final StringBuilder result = new StringBuilder(100);

		Collection<CNAbility> targetAbilities = pc.getMatchingCNAbilities(ability);
		if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			ChooseInformation<?> chooseInfo = ability.get(ObjectKey.CHOOSE_INFO);
			processAbilities(result, targetAbilities, chooseInfo);
		}
		return result.toString();
	}

	private <T> void processAbilities(final StringBuilder result, Collection<CNAbility> targetAbilities,
		ChooseInformation<T> chooseInfo)
	{
		if (chooseInfo == null)
		{
			return;
		}

		List<T> choices = new ArrayList<>();
		for (CNAbility ab : targetAbilities)
		{
			List<? extends T> sel = (List<? extends T>) pc.getDetailedAssociations(ab);
			if (sel != null)
			{
				choices.addAll(sel);
			}
		}

		String choiceInfo = chooseInfo.composeDisplay(choices).toString();
		if (!choiceInfo.isEmpty())
		{
			result.append(choiceInfo);
		}
	}

	@Override
	public String getTempBonusTarget(TempBonusFacade tempBonusFacade)
	{
		if (tempBonusFacade == null || !(tempBonusFacade instanceof TempBonusFacadeImpl tempBonus))
		{
			return EMPTY_STRING;
		}

		Set<String> targetSet = new HashSet<>();
		if (TempBonusHelper.hasCharacterTempBonus(tempBonus.getOriginObj()))
		{
			targetSet.add(LanguageBundle.getString("in_itmBonModelTargetTypeCharacter")); //$NON-NLS-1$
		}
		if (TempBonusHelper.hasEquipmentTempBonus(tempBonus.getOriginObj()))
		{
			targetSet.addAll(TempBonusHelper.getEquipmentApplyString(tempBonus.getOriginObj()));
		}
		return StringUtil.join(targetSet, ";");
	}

	@Override
	public String getMovement(Race race)
	{
		List<SimpleMovement> movements = race.getListFor(ListKey.BASE_MOVEMENT);
		if (movements != null && !movements.isEmpty())
		{
			return movements.get(0).toString();
		}
		return EMPTY_STRING;
	}

	private void appendFacts(HtmlInfoBuilder infoText, CDOMObject cdo)
	{
		Class<? extends CDOMObject> cl = cdo.getClass();
		LoadContext context = Globals.getContext();
		Collection<FactDefinition> defs = context.getReferenceContext().getConstructedCDOMObjects(FactDefinition.class);
		for (FactDefinition<?, ?> def : defs)
		{
			if (def.getUsableLocation().isAssignableFrom(cl))
			{
				Visibility visibility = def.getVisibility();
				if (visibility != null && visibility.isVisibleTo(View.VISIBLE_DISPLAY))
				{
					FactKey<?> fk = def.getFactKey();
					Indirect<?> fact = cdo.get(fk);
					if (fact != null)
					{
						infoText.appendSpacer();
						infoText.append("<b>");
						infoText.append(fk.toString());
						infoText.append(":</b>&nbsp;");
						infoText.append(fact.getUnconverted());
					}
				}
			}
		}
		Collection<FactSetDefinition> setdefs =
				context.getReferenceContext().getConstructedCDOMObjects(FactSetDefinition.class);
		for (FactSetDefinition<?, ?> def : setdefs)
		{
			if (def.getUsableLocation().isAssignableFrom(cl))
			{
				Visibility visibility = def.getVisibility();
				if (visibility != null && visibility.isVisibleTo(View.VISIBLE_DISPLAY))
				{
					FactSetKey<?> fk = def.getFactSetKey();
					String s = getSetString(cdo, fk);
					if (s != null)
					{
						infoText.appendSpacer();
						infoText.append("<b>");
						infoText.append(fk.toString());
						infoText.append(":</b>&nbsp;");
						infoText.append(s);
					}
				}
			}
		}

	}

	private <T> String getSetString(CDOMObject cdo, FactSetKey<T> fk)
	{
		List<Indirect<T>> set = cdo.getSetFor(fk);
		if (set == null)
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Indirect<T> indirect : set)
		{
			if (!first)
			{
				sb.append(Constants.COMMA);
			}
			sb.append(indirect.get());
			first = false;
		}
		return sb.toString();
	}

	@Override
	public String getSize(Race race)
	{
		Formula formula = race.get(FormulaKey.SIZE);
		return (formula == null) ? "" : formula.toString();
	}

}
