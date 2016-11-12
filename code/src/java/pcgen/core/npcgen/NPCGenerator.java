/*
 * NPCGenerator.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 * Current Ver: $Revision$
 */
package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.base.util.RandomUtil;
import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.CNAbilityFactory;
import pcgen.cdom.content.RollMethod;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.Gender;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.ClassSource;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.cdom.enumeration.PCAttribute;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.analysis.DomainApplication;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.analysis.SubClassApplication;
import pcgen.core.character.CharacterSpell;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.spell.Spell;
import pcgen.gui2.UIPropertyContext;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.enumeration.Visibility;

/**
 * This class implements the NPC generator.  It is a singleton object and 
 * therefore should not be created locally.
 * 
 * @author boomer70
 *
 */
public final class NPCGenerator
{
	private static final NPCGenerator theInstance = new NPCGenerator();

	private Configuration theConfiguration = null;

	// Rule options
	private int theSubSkillWeightAdd = 10;
	
	private NPCGenerator()
	{
		// Private so this can't be constructed.
	}

	/**
	 * Gets the generator instance.
	 * 
	 * @return The <tt>NPCGenerator</tt> instance.
	 */
	public static NPCGenerator getInst()
	{
		theInstance.setConfiguration(SettingsHandler.getGame());
		return theInstance;
	}

	private void setConfiguration( final GameMode aGameMode )
	{
		theConfiguration = Configuration.get(aGameMode);
	}
	
	public static int getSubSkillWeightAdd()
	{
		return getInst().theSubSkillWeightAdd;
	}
	
	/**
	 * Returns the options for alignment.
	 * 
	 * @return A <tt>List</tt> of AlignGeneratorOption
	 */
	public List<AlignGeneratorOption> getAlignmentOptions()
	{
		return theConfiguration.getAlignmentOptions();
	}

	/**
	 * Returns the options for races.
	 * 
	 * @return A <tt>List</tt> of RaceGeneratorOption
	 */
	public List<RaceGeneratorOption> getCustomRaceOptions()
	{
		return theConfiguration.getRaceOptions();
	}

	/**
	 * Returns the options for genders.
	 * 
	 * @return A <tt>List</tt> of GenderGeneratorOption
	 */
	public List<GenderGeneratorOption> getCustomGenderOptions()
	{
		return theConfiguration.getGenderOptions();
	}

	/**
	 * Returns the options for classes.
	 * 
	 * @return A <tt>List</tt> of ClassGeneratorOption
	 */
	public List<ClassGeneratorOption> getCustomClassOptions()
	{
		return theConfiguration.getClassOptions();
	}

	/**
	 * Returns the options for levels.
	 * 
	 * @return A <tt>List</tt> of LevelGeneratorOption
	 */
	public List<LevelGeneratorOption> getCustomLevelOptions()
	{
		return theConfiguration.getLevelOptions();
	}

	private WeightedCollection<SkillChoice> getSkillWeights(final PCClass aClass,
										final PlayerCharacter aPC)
	{
		WeightedCollection<SkillChoice> WeightedCollection = theConfiguration.getSkillWeights(aClass.getKeyName());
		if (WeightedCollection == null)
		{
			WeightedCollection = new WeightedCollection<>();
			// User has not specified a weighting for skills for this class
			// Assume class skills are picked uniformly and cross-class skills
			// are 1/8 as likely to be selected.
			for ( Skill skill : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Skill.class) )
			{
				if ( skill.getSafe(ObjectKey.VISIBILITY) == Visibility.DEFAULT )
				{
					if (aPC.isClassSkill(aClass, skill))
					{
						WeightedCollection.add(new SkillChoice(skill.getKeyName()), 8);
					}
					else if (!skill.getSafe(ObjectKey.EXCLUSIVE))
					{
						WeightedCollection.add(new SkillChoice(skill.getKeyName()), 1);
					}
				}
			}
		}
		return WeightedCollection;
	}

	private void selectSkills(final PlayerCharacter aPC, final WeightedCollection<SkillChoice> skillList,
									 final PCClass aClass, final int level)
	{
		// Select a potential skill

		PCLevelInfo levelInfo = null;

		int curLevel = 0;
		for ( PCLevelInfo li : aPC.getLevelInfo() )
		{
			if (li.getClassKeyName().equals(aClass.getKeyName()))
			{
				curLevel++;
			}
			if (curLevel == level)
			{
				levelInfo = li;
				break;
			}
		}
		if (levelInfo == null)
		{
			return;
		}
		int skillPts = levelInfo.getSkillPointsRemaining();
		Logging.debugPrint( "NPCGenerator: Selecting " + skillPts + " skill points for " + aClass + "/" + level ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		Logging.debugPrint( "NPCGenerator: Initial skillList is " + skillList ); //$NON-NLS-1$
		while (skillPts > 0)
		{
			final SkillChoice choice = skillList.getRandomValue();
			final Skill skill = choice.getSkill();
			Logging.debugPrint( "NPCGenerator: Selected " + skill ); //$NON-NLS-1$

			if (skill == null)
			{
				Logging.debugPrint("NPCGenerator: Skill not found"); //$NON-NLS-1$
				continue;
			}

			final int cost = aPC.getSkillCostForClass(skill, aClass).getCost();
			double ranks = 1.0 / cost;
			Logging.debugPrint( "NPCGenerator: Adding " + (int)ranks + "ranks" ); //$NON-NLS-1$ //$NON-NLS-2$
			if (!Globals.checkRule(RuleConstants.SKILLMAX))
			{
				// If we are not told to ignore rank maxes we need to make sure
				// we can add this rank to this skill.
				double maxRanks = aPC.getMaxRank(skill, aClass).
					doubleValue();
				double pcRanks = aPC.getRank(skill).doubleValue();
				if (pcRanks + ranks > maxRanks)
				{
					Logging.debugPrint("NPCGenerator: Skill already at max."); //$NON-NLS-1$
					// Check that there are some skills we can advance in
					boolean ranksLeft = false;
					for (SkillChoice skillChoice : skillList)
					{
						Skill chkSkill = skillChoice.getSkill();
						if (chkSkill != null)
						{
							if (aPC.getRank(chkSkill).doubleValue() < aPC.getMaxRank(chkSkill, aClass).
									doubleValue())
							{
								ranksLeft = true;
								break;
							}
						}
					}
					if (!ranksLeft)
					{
						Logging.errorPrint("Unable to spend all skill points.");
						break;
					}
					continue;
				}
			}

			SkillRankControl.modRanks(ranks, aClass, false, aPC, skill);
			// Add weight to skills we select to try and encourage us to select
			// them again.
			skillList.add(choice, 4/cost);
			skillPts--;
			levelInfo.setSkillPointsRemaining(skillPts);
			Logging.debugPrint( "NPCGenerator: Skill list now " + skillList ); //$NON-NLS-1$
		}
	}

	private PCAlignment getAlignment(final AlignGeneratorOption option)
	{
		if (option == null)
		{
			return null;
		}
		return option.getList().getRandomValue();
	}

	private Race getRace(final RaceGeneratorOption option)
	{
		return option.getList().getRandomValue();
	}

	private Gender getGender(final GenderGeneratorOption option)
	{
		return option.getList().getRandomValue();
	}

	private PCClass getClass(final ClassGeneratorOption option)
	{
		return option.getList().getRandomValue();
	}

	private int getLevel(final LevelGeneratorOption option)
	{
		return option.getList().getRandomValue();
	}

	private List<PCStat> getStatWeights(PlayerCharacter pc, final PCClass aClass)
	{
		final WeightedCollection<PCStat> stats = new WeightedCollection<>(
                theConfiguration.getStatWeights(aClass.getKeyName()));

		final List<PCStat> ret = new ArrayList<>();
		for (int i = 0; i < pc.getDisplay().getStatCount(); i++)
		{
			final PCStat stat = stats.getRandomValue();
			ret.add(stat);
			stats.remove(stat);
		}

		return ret;
	}

	private void generateStats(final PlayerCharacter aPC, final PCClass aClass, final RollMethod aRollMethod)
	{
		final List<PCStat> statOrder = getStatWeights(aPC, aClass);
		Logging.debugPrint( "NPCGenerator: Stat order is " + statOrder ); //$NON-NLS-1$
		aPC.rollStats(Constants.CHARACTER_STAT_METHOD_ROLLED, statOrder, aRollMethod, true);
		for (PCStat stat : aPC.getStatSet())
		{
			Logging.debugPrint( "NPCGenerator: Setting stat " + stat.getKeyName()
				+ " to " + aPC.getStat(stat) );  //$NON-NLS-1$//$NON-NLS-2$
			aPC.setStat(stat, aPC.getStat(stat));
		}
	}

	private WeightedCollection<Ability> getFeatWeights(final PCClass aClass)
	{
		WeightedCollection<Ability> weightedCollection =
			theConfiguration.getAbilityWeights(aClass.getKeyName(), AbilityCategory.FEAT);
		if (weightedCollection == null)
		{
			weightedCollection = new WeightedCollection<>();
			// User has not specified a weighting for feats for this class
			// Assume General feats are 5 times as likely to be selected as
			// any other type
			for (Ability ability : Globals.getContext().getReferenceContext().getManufacturer(
					Ability.class, AbilityCategory.FEAT).getAllObjects())
			{
				int weight = 1;
				if (ability.getSafe(ObjectKey.VISIBILITY) != Visibility.DEFAULT)
				{
					continue;
				}
				if (ability.isType("GENERAL")) //$NON-NLS-1$
				{
					weight = 5;
				}
				weightedCollection.add(ability, weight);
			}
		}
		return weightedCollection;
	}

	private void selectFeats(final PlayerCharacter aPC, final WeightedCollection<Ability> aFeatList)
	{
		while ((int)aPC.getRemainingFeatPoolPoints() > 0)
		{
			final Ability ability = aFeatList.getRandomValue();

			if (!ability.qualifies(aPC, ability))
			{
				// We will leave the feat because we may qualify later.
				continue;
			}
			AbilityUtilities.driveChooseAndAdd(CNAbilityFactory.getCNAbility(AbilityCategory.FEAT, Nature.NORMAL, ability), aPC, true);
		}
	}

	private void selectDeity( final PlayerCharacter aPC, final PCClass aClass )
	{
		// Copy the list since we may modify it
		final WeightedCollection<Deity> deities = new WeightedCollection<>(theConfiguration.getDeityWeights(aClass.getKeyName()));
		boolean selected = false;
		while (!deities.isEmpty())
		{
			final Deity deity = deities.getRandomValue();
			if ( aPC.canSelectDeity(deity))
			{
				aPC.setDeity(deity);
				selected = true;
				break;
			}
			deities.remove(deity);
		}
		if (!selected )
		{
			Logging.errorPrintLocalised("NPCGen.Errors.CantSelectDeity"); //$NON-NLS-1$
		}
	}
	
	private void selectDomains( final PlayerCharacter aPC, final PCClass aClass )
	{
		final WeightedCollection<Domain> domains = theConfiguration.getDomainWeights(aPC.getDeity().getKeyName(), aClass.getKeyName());
		for (Iterator<Domain> iterator = domains.iterator(); iterator.hasNext();)
		{
			Domain domain = iterator.next();
			if (!domain.qualifies(aPC, domain))
			{
				iterator.remove();
			}
		}
		if (domains.isEmpty())
		{
			return;
		}
		while (aPC.getDomainCount() < aPC.getMaxCharacterDomains())
		{
			final Domain domain = domains.getRandomValue();

			//Can't add twice, so have to select another...
			if (aPC.hasDomain(domain))
			{
				continue;
			}
			
			// space remains for another domain, so add it
			aPC.addDomain(domain, new ClassSource(aClass));
			DomainApplication.applyDomain(aPC, domain);
			aPC.calcActiveBonuses();
		}
	}
	
	private WeightedCollection<Spell> getKnownSpellWeights(PlayerCharacter pc, final PCClass aClass, final int aLevel )
	{
		WeightedCollection<Spell> WeightedCollection = theConfiguration.getKnownSpellWeights(pc, aClass.getKeyName(), aLevel);
		if (WeightedCollection == null)
		{
			WeightedCollection = new WeightedCollection<>();
			for (final Spell spell : pc.getSpellsIn(aClass.get(ObjectKey.CLASS_SPELLLIST),
				aLevel))
			{
				WeightedCollection.add(spell, 1);
			}
		}
		return WeightedCollection;
	}

	private WeightedCollection<Spell> getPreparedSpellWeights(PlayerCharacter pc, final PCClass aClass, final int aLevel )
	{
		WeightedCollection<Spell> WeightedCollection = theConfiguration.getPreparedSpellWeights(aClass.getKeyName(), aLevel, pc);
		if (WeightedCollection == null)
		{
			WeightedCollection = new WeightedCollection<>();
			for (final Spell spell : pc.getSpellsIn(aClass.get(ObjectKey.CLASS_SPELLLIST),
				aLevel)) 
			{
				WeightedCollection.add(spell, 1);
			}
		}
		return WeightedCollection;
	}

	private void selectDomainSpell( final PlayerCharacter aPC, final PCClass aClass, final int aLevel )
	{
		if (!aPC.hasDomains())
		{
			return;
		}
		final WeightedCollection<Domain> domains = new WeightedCollection<>();
		for (Domain d : aPC.getDomainSet())
		{
			// if any domains have this class as a source
			// and is a valid domain, add them
			if (aClass.equals(aPC.getDomainSource(d).getPcclass()))
			{
				domains.add(d);
			}
		}
		final Domain domain = domains.getRandomValue();
		final WeightedCollection<Spell> domainSpells =
                new WeightedCollection<>(aPC.getSpellsIn(domain.get(ObjectKey.DOMAIN_SPELLLIST),
                        aLevel));
		selectSpell( aPC, aClass, domain, "Prepared Spells", domainSpells, aLevel ); //$NON-NLS-1$
	}
	
	private void selectSpell( final PlayerCharacter aPC, final PCClass aClass, final Domain aDomain, final String aBookName, final WeightedCollection<Spell> aSpellList, final int aLevel )
	{
		boolean added = false;
		while ( !added )
		{
			final Spell spell = aSpellList.getRandomValue();
			// TODO - How do I check if this spell is prohibiited?
			
			final CharacterSpell cs;
			if ( aDomain != null )
			{
				cs = new CharacterSpell( aDomain, spell );
			}
			else
			{
				cs = new CharacterSpell( aClass, spell );
			}
			final String aString = aPC.addSpell(cs, new ArrayList<>(), aClass.getKeyName(),
					   aBookName, aLevel, aLevel);
			if (!aString.isEmpty())
			{
				Logging.debugPrint("Add spell failed: " + aString); //$NON-NLS-1$
			}
			else
			{
				added = true;
			}
		}
	}
	
	private void selectSubClass( final PlayerCharacter aPC, final PCClass aClass )
	{
		WeightedCollection<String> subClasses = theConfiguration.getSubClassWeights( aClass.getKeyName() );
		if (subClasses != null && !subClasses.isEmpty())
		{
			SubClassApplication.setSubClassKey(aPC, aClass, subClasses
					.getRandomValue());
		}
	}
	
	/**
	 * Generate a new NPC
	 * 
	 * @param aPC The PlayerCharacter to fill in options for
	 * @param align Alignment options to choose from
	 * @param aRace Race options to choose from
	 * @param aGender Gender options to choose from
	 * @param classList <tt>List</tt> of class options to choose from
	 * @param levels <tt>List</tt> of level choices
	 * @param aRollMethod the RollMethod to use for stats
	 */
	public void generate(	final PlayerCharacter aPC, 
							final AlignGeneratorOption align,
							final RaceGeneratorOption aRace, 
							final GenderGeneratorOption aGender,
							final List<ClassGeneratorOption> classList, 
							final List<LevelGeneratorOption> levels,
							final RollMethod aRollMethod)
	{
		// Force a more quiet process
		ChooserFactory.pushChooserClassname(
			"pcgen.util.chooser.RandomChooser"); //$NON-NLS-1$

		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);

		int tempChoicePref = UIPropertyContext.getSingleChoiceAction();
		UIPropertyContext.setSingleChoiceAction(Constants.
			CHOOSER_SINGLE_CHOICE_METHOD_SELECT_EXIT);

		try
		{
			final int MAX_RETRIES = 5;
			for ( int i = 0; i < MAX_RETRIES; i++ )
			{
				PCAlignment randAlign = getAlignment( align );
				if (randAlign != null)
				{
					Logging
						.debugPrint("NPCGenerator: Selected " + randAlign + " for alignment " + align); //$NON-NLS-1$//$NON-NLS-2$
					aPC.setAlignment(randAlign);
				}
				
				final Race r = getRace(aRace);
				if (r == null)
				{
					Logging.debugPrint( "NPCGenerator: Got null race.  Retrying." ); //$NON-NLS-1$
					continue;
				}
				Logging.debugPrint( "NPCGenerator: Selected " + r + " for race " + aRace ); //$NON-NLS-1$ //$NON-NLS-2$
				if (r.qualifies(aPC, r))
				{
					Logging.debugPrint( "NPCGenerator: PC qualifies for race " + r ); //$NON-NLS-1$
					aPC.setRace(r);
					break;
				}
			}
			if ( aPC.getRace() == Globals.s_EMPTYRACE )
			{
				Logging.errorPrint("Unable to select race");
				return;
			}
			
			final Gender gender = getGender( aGender );
			Logging.debugPrint( "NPCGenerator: Selecting " + gender + " for gender " + aGender ); //$NON-NLS-1$ //$NON-NLS-2$
			aPC.setGender(gender);

			boolean doneRacialClasses = false;
			for (int i = 0; i < classList.size(); i++)
			{
				int numLevels = getLevel(levels.get(i));
				Logging.debugPrint( "NPCGenerator: Selecting " + numLevels + " for level " + levels.get(i) ); //$NON-NLS-1$ //$NON-NLS-2$
				PCClass aClass = null;
				
				if ( !doneRacialClasses && aPC.hasClass())
				{
					aClass = aPC.getClassList().get(0);
					numLevels = aPC.getLevel(aClass);
					doneRacialClasses = true;
					i--;
				}
				else
				{
					doneRacialClasses = true;
					for ( ; ; )
					{
						aClass = getClass(classList.get(i));
						if (aClass == null)
						{
							break;
						}
						if (aClass.getSafe(ObjectKey.VISIBILITY).equals(Visibility.DEFAULT)
							&& aClass.qualifies(aPC, aClass))
						{
							Logging.debugPrint( "NPCGenerator: Selecting " + aClass + " for class " + classList.get(i) ); //$NON-NLS-1$ //$NON-NLS-2$
							break;
						}
						// TODO Remove a failed class from the list.
						Logging.errorPrint("Counld not add a level of " + aClass);
						aClass = null;
						break;
					}
				}
				if (aClass == null)
				{
					continue;
				}
				
				final PCClass classCopy = aClass.clone();
				if ( classCopy.containsListFor(ListKey.SUB_CLASS) )
				{
					selectSubClass(aPC, classCopy);
				}
				if (i == 0)
				{
					generateStats(aPC, classCopy, aRollMethod);
					selectDeity(aPC, classCopy);
				}

				int highestSpellLevel = aPC.getSpellSupport(aClass).getHighestLevelSpell(aPC);
				final int[] selectedSpells = new int[highestSpellLevel + 1];
				for ( int k = 0; k < highestSpellLevel; k++ ) { selectedSpells[k] = 0; }
				
				final int[] bonusSpells = new int[highestSpellLevel + 1];
				for ( int k = 0; k < highestSpellLevel; k++ ) { bonusSpells[k] = 0; }

				// Make a copy of the list because we are going to modify it.
				WeightedCollection<SkillChoice> skillList = new WeightedCollection<>(getSkillWeights(classCopy, aPC));
				WeightedCollection<Ability> featList = new WeightedCollection<>(getFeatWeights(classCopy));
				for (int j = 0; j < numLevels; j++)
				{
					if ( i >= 0 )
					{
						aPC.incrementClassLevel(1, classCopy, true);
					}

					final PCClass pcClass = aPC.getClassKeyed(classCopy.getKeyName());
					selectSkills(aPC, skillList, pcClass, j + 1);
					selectFeats(aPC, featList);
					
					selectDomains( aPC, pcClass );
					
					if (pcClass.get(FactKey.valueOf("SpellType")) != null)
					{
						// This is a spellcasting class.  We may have to select
						// spells of some sort (known or prepared).
						if ( aPC.getSpellSupport(pcClass).hasKnownList() || aPC.getSpellSupport(pcClass).hasKnownSpells(aPC) )
						{
							Logging.debugPrint("NPCGenerator: known spells to select"); //$NON-NLS-1$
							for (int lvl = 0; lvl <= highestSpellLevel; ++lvl)
							{
								if (aPC.availableSpells(lvl, pcClass, Globals.getDefaultSpellBook(), true, true))
								{
									final int a = aPC.getSpellSupport(pcClass).getKnownForLevel(lvl, aPC);
									//final int bonus = aPC.getSpellSupport(pcClass).getSpecialtyKnownForLevel(lvl, aPC);
									Logging.debugPrint("NPCGenerator: " + a + "known spells to select"); //$NON-NLS-1$ //$NON-NLS-2$
									
									final WeightedCollection<Spell> spellChoices = getKnownSpellWeights(aPC, pcClass, lvl);

									final int numToSelect = a - selectedSpells[lvl];
									for ( int sp = 0; sp < numToSelect; sp ++ )
									{
										selectSpell( aPC, pcClass, null, Globals.getDefaultSpellBook(), spellChoices, lvl );
										selectedSpells[lvl]++;
									}
									
								}
							}
						}
						else
						{
							// Prepared spells?
							Logging.debugPrint("NPCGenerator: prepared spells to select"); //$NON-NLS-1$
							
							aPC.addSpellBook("Prepared Spells");
							for (int lvl = 0; lvl <= highestSpellLevel; ++lvl)
							{
								final int castTot = aPC.getSpellSupport(pcClass).getCastForLevel(lvl, "Prepared Spells", true, true, aPC);
								final int castNon = aPC.getSpellSupport(pcClass).getCastForLevel(lvl, "Prepared Spells", false, true, aPC);
								final int castSpec = castTot - castNon;
								Logging.debugPrint("NPCGenerator: " + castTot + "+" + castSpec + " prepared spells to select"); //$NON-NLS-1$ //$NON-NLS-2$
								if ( castSpec - bonusSpells[lvl] > 0 )
								{
									selectDomainSpell( aPC, pcClass, lvl );
									bonusSpells[lvl]++;
								}
								
								if (castTot > 0)
								{
									final WeightedCollection<Spell> spellChoices = getPreparedSpellWeights(aPC, pcClass, lvl);

									final int numToSelect = castNon - selectedSpells[lvl];
									for ( int sp = 0; sp < numToSelect; sp ++ )
									{
										selectSpell( aPC, pcClass, null, "Prepared Spells", spellChoices, lvl );
										selectedSpells[lvl]++;
									}
									
								}
							}
						}
					}
				}
			}
			
			final String randBioString = "EYES.HAIR.SKIN.HT.WT.AGE."; //$NON-NLS-1$
			aPC.getBioSet().randomize(randBioString, aPC);
			
			final List<String> globalHairStyleList = SystemCollections.getUnmodifiableHairStyleList();
			aPC.setPCAttribute(PCAttribute.HAIRSTYLE, globalHairStyleList.get(RandomUtil.getRandomInt(globalHairStyleList.size())));
			final List<String> speechList = SystemCollections.getUnmodifiableSpeechList();
			aPC.setPCAttribute(PCAttribute.SPEECHTENDENCY, speechList.get(RandomUtil.getRandomInt(speechList.size())));
			final List<String> globalPhobiaList = SystemCollections.getUnmodifiablePhobiaList();
			aPC.setPCAttribute(PCAttribute.PHOBIAS, globalPhobiaList.get(RandomUtil.getRandomInt(globalPhobiaList.size())));
			final List<String> globalInterestsList = SystemCollections.getUnmodifiableInterestsList();
			aPC.setPCAttribute(PCAttribute.INTERESTS, globalInterestsList.get(RandomUtil.getRandomInt(globalInterestsList.size())));
			final List<String> globalPhraseList = SystemCollections.getUnmodifiablePhraseList();
			aPC.setPCAttribute(PCAttribute.CATCHPHRASE, globalPhraseList.get(RandomUtil.getRandomInt(globalPhraseList.size())));
			final List<String> globalTraitList = SystemCollections.getUnmodifiableTraitList();
			// TODO: it is possible for trait1 == trait2
			aPC.setPCAttribute(PCAttribute.PERSONALITY1, globalTraitList.get(RandomUtil.getRandomInt(globalTraitList.size())));
			aPC.setPCAttribute(PCAttribute.PERSONALITY2, globalTraitList.get(RandomUtil.getRandomInt(globalTraitList.size())));

			final List<String> globalCityList = SystemCollections.getUnmodifiableCityList();
			aPC.setPCAttribute(PCAttribute.RESIDENCE, globalCityList.get(RandomUtil.getRandomInt(globalCityList.size())));
			final List<String> globalLocationList = SystemCollections.getUnmodifiableLocationList();
			aPC.setPCAttribute(PCAttribute.LOCATION, globalLocationList.get(RandomUtil.getRandomInt(globalLocationList.size())));
			final List<String> globalBirthplaceList = SystemCollections.getUnmodifiableBirthplaceList();
			aPC.setPCAttribute(PCAttribute.BIRTHPLACE, globalBirthplaceList.get(RandomUtil.getRandomInt(globalBirthplaceList.size())));
			
			//TODO: Link in with the doomsday book name generator
//			final Names nameGen = Names.getInstance();
//			nameGen.init(aNameChoice, aPC);
//			aPC.setName(nameGen.getRandomName());
		}
		catch (Exception e)
		{
			Logging.errorPrint("Problem generation NPC", e);
		}
		finally
		{
			SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
			UIPropertyContext.setSingleChoiceAction(tempChoicePref);
			ChooserFactory.popChooserClassname();
		}
	}
}
