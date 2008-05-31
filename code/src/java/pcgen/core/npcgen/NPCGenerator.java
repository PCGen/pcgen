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
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import pcgen.base.util.WeightedList;
import pcgen.cdom.base.Constants;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Categorisable;
import pcgen.core.CharacterDomain;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.GameMode;
import pcgen.core.Globals;
import pcgen.core.Names;
import pcgen.core.PCAlignment;
import pcgen.core.PCClass;
import pcgen.core.PCStat;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.Skill;
import pcgen.core.SystemCollections;
import pcgen.core.character.CharacterSpell;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.spell.Spell;
import pcgen.core.system.GameModeRollMethod;
import pcgen.gui.NameElement;
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
public class NPCGenerator
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

	private WeightedList<SkillChoice> getSkillWeights(final PCClass aClass,
										final PlayerCharacter aPC)
	{
		WeightedList<SkillChoice> weightedList = theConfiguration.getSkillWeights(aClass.getKeyName());
		if (weightedList == null)
		{
			weightedList = new WeightedList<SkillChoice>();
			// User has not specified a weighting for skills for this class
			// Assume class skills are picked uniformly and cross-class skills
			// are 1/8 as likely to be selected.
			for ( Skill skill : Globals.getSkillList() )
			{
				if ( skill.getVisibility() == Visibility.DEFAULT )
				{
					if (skill.isClassSkill(aClass, aPC))
					{
						weightedList.add(8, new SkillChoice(skill.getKeyName()));
					}
					else if (!skill.isExclusive())
					{
						weightedList.add(1, new SkillChoice(skill.getKeyName()));
					}
				}
			}
		}
		return weightedList;
	}

	private void selectSkills(final PlayerCharacter aPC, final List<SkillChoice> skillList,
									 final PCClass aClass, final int level)
	{
		// Select a potential skill

		final List<PCLevelInfo> pcLvlInfo = aPC.getLevelInfo();
		PCLevelInfo levelInfo = null;

		int curLevel = 0;
		for ( PCLevelInfo li : pcLvlInfo )
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
			final SkillChoice choice = skillList.get(Globals.getRandomInt(skillList.size()));
			final Skill skill = choice.getSkill();
			Logging.debugPrint( "NPCGenerator: Selected " + skill ); //$NON-NLS-1$

			if (skill == null)
			{
				Logging.debugPrint("NPCGenerator: Skill not found"); //$NON-NLS-1$
				continue;
			}

			Skill pcSkill = aPC.getSkillKeyed(skill.getKeyName());
			final int cost = skill.costForPCClass(aClass, aPC);
			double ranks = 1.0 / cost;
			Logging.debugPrint( "NPCGenerator: Adding " + (int)ranks + "ranks" ); //$NON-NLS-1$ //$NON-NLS-2$
			if (!Globals.checkRule(RuleConstants.SKILLMAX))
			{
				// If we are not told to ignore rank maxes we need to make sure
				// we can add this rank to this skill.
				double maxRanks = aPC.getMaxRank(skill.getKeyName(), aClass).
					doubleValue();
				double pcRanks = pcSkill == null ? 0.0 : pcSkill.getRank().doubleValue();
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
							Skill chkPcSkill = aPC.getSkillKeyed(chkSkill.getKeyName());
							if (chkPcSkill == null)
							{
								ranksLeft = true;
								break;
							}
							if (chkPcSkill.getRank().doubleValue() < aPC.getMaxRank(chkPcSkill.getKeyName(), aClass).
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

			if (pcSkill == null)
			{
				pcSkill = aPC.addSkill(skill);
			}
			pcSkill.modRanks(ranks, aClass, aPC);
			// Add weight to skills we select to try and encourage us to select
			// them again.
			skillList.add(4/cost, choice);
			skillPts--;
			levelInfo.setSkillPointsRemaining(skillPts);
			Logging.debugPrint( "NPCGenerator: Skill list now " + skillList ); //$NON-NLS-1$
		}
	}

	private int getAlignment(final AlignGeneratorOption option)
	{
		if (option == null)
		{
			return -1;
		}
		final WeightedList<PCAlignment> options = option.getList();
		int val = Globals.getRandomInt(options.size());
		return SettingsHandler.getGame().getIndexOfAlignment(options.get(val).getKeyName());
	}

	private Race getRace(final RaceGeneratorOption option)
	{
		final WeightedList<Race> options = option.getList();
		int val = Globals.getRandomInt(options.size());
		return options.get(val);
	}

	private String getGender(final GenderGeneratorOption option)
	{
		final WeightedList<String> options = option.getList();
		int val = Globals.getRandomInt(options.size());
		return options.get(val);
	}

	private PCClass getClass(final ClassGeneratorOption option)
	{
		final WeightedList<PCClass> options = option.getList();
		int val = Globals.getRandomInt(options.size());
		return options.get(val);
	}

	private int getLevel(final LevelGeneratorOption option)
	{
		final WeightedList<Integer> options = option.getList();
		int val = Globals.getRandomInt(options.size());
		return options.get(val);
	}

	private List<PCStat> getStatWeights(final PCClass aClass)
	{
		final WeightedList<String> weightedList = new WeightedList<String>(theConfiguration.getStatWeights(aClass.getKeyName()));

		// Now determine that actual order
		final List<PCStat> statList = SettingsHandler.getGame().getUnmodifiableStatList();
		final List<PCStat> ret = new ArrayList<PCStat>(statList.size());
		for (int i = 0; i < statList.size(); i++)
		{
			final int val = Globals.getRandomInt(weightedList.size());
			final String statAbbrev = weightedList.get(val);
			final int sInd = SettingsHandler.getGame().getStatFromAbbrev(statAbbrev);
			final PCStat stat = statList.get(sInd);
			ret.add(stat);
			weightedList.remove(statAbbrev);
		}

		return ret;
	}

	private void generateStats(final PlayerCharacter aPC, final PCClass aClass, final GameModeRollMethod aRollMethod)
	{
		final List<PCStat> statOrder = getStatWeights(aClass);
		Logging.debugPrint( "NPCGenerator: Stat order is " + statOrder ); //$NON-NLS-1$
		aPC.rollStats(Constants.CHARACTERSTATMETHOD_ROLLED, statOrder, aRollMethod, true);
		final List<PCStat> pcStats = aPC.getStatList().getStatList();
		for (int i = 0; i < statOrder.size(); i++)
		{
			final PCStat newStat = statOrder.get(i);
			final PCStat pcStat = pcStats.get(aPC.getStatList().getIndexOfStatFor(newStat.getAbb()));
			Logging.debugPrint( "NPCGenerator: Setting stat " + pcStat.getAbb() + " to " + newStat.getBaseScore() );  //$NON-NLS-1$//$NON-NLS-2$
			pcStat.setBaseScore(newStat.getBaseScore());
		}
	}

	private WeightedList<Ability> getFeatWeights(final PCClass aClass)
	{
		WeightedList<Ability> weightedList = theConfiguration.getAbilityWeights(aClass.getKeyName(), AbilityCategory.FEAT);
		if (weightedList == null)
		{
			weightedList = new WeightedList<Ability>();
			// User has not specified a weighting for feats for this class
			// Assume General feats are 5 times as likely to be selected as
			// any other type
			Iterator<? extends Categorisable> i = Globals.getAbilityKeyIterator(Constants.FEAT_CATEGORY);
			while (i.hasNext())
			{
				int weight = 1;
				Ability ability = (Ability)i.next();
				if (!(ability.getVisibility() == Visibility.DEFAULT))
				{
					continue;
				}
				if (ability.isType("GENERAL")) //$NON-NLS-1$
				{
					weight = 5;
				}
				weightedList.add(weight, ability);
			}
		}
		return weightedList;
	}

	private void selectFeats(final PlayerCharacter aPC, final List<Ability> aFeatList)
	{
		while ((int)aPC.getFeats() > 0)
		{
			final Ability ability = aFeatList.get(Globals.getRandomInt(aFeatList.size()));

			if (!PrereqHandler.passesAll(ability.getPreReqList(), aPC, ability))
			{
				// We will leave the feat because we may qualify later.
				continue;
			}
			AbilityUtilities.modFeat(aPC, null, ability.getKeyName(), true, false);
		}
	}

	private void selectDeity( final PlayerCharacter aPC, final PCClass aClass )
	{
		// Copy the list since we may modify it
		final List<Deity> deities = new WeightedList<Deity>(theConfiguration.getDeityWeights(aClass.getKeyName()));
		boolean selected = false;
		while ( deities.size() > 0 )
		{
			final Deity deity = deities.get(Globals.getRandomInt(deities.size()));
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
		while (aPC.getCharacterDomainUsed() < aPC.getMaxCharacterDomains())
		{
			final List<Domain> domains = theConfiguration.getDomainWeights(aPC.getDeity().getKeyName(), aClass.getKeyName());
			for (Iterator<Domain> iterator = domains.iterator(); iterator.hasNext();)
			{
				Domain domain = (Domain) iterator.next();
				if (! PrereqHandler.passesAll(domain.getPreReqList(), aPC, domain))
				{
					iterator.remove();
				}
			}
			if (domains.size() == 0)
			{
				return;
			}
			final Domain domain = domains.get(Globals.getRandomInt(domains.size()));

			CharacterDomain aCD = aPC.getCharacterDomainForDomain(domain.getKeyName());

			if (aCD == null)
			{
				aCD = aPC.getNewCharacterDomain();
			}

			// TODO - This seems kind of silly.  How would this ever happen?
			final Domain existingDomain = aCD.getDomain();

			if ((existingDomain != null) && existingDomain.equals(domain))
			{
				aPC.removeCharacterDomain(aCD);
			}
			
			// space remains for another domain, so add it
			if (existingDomain == null)
			{
				domain.setIsLocked(true, aPC);
				aCD.setDomain(domain, aPC);
				aPC.addCharacterDomain(aCD);

				aPC.calcActiveBonuses();
			}
		}
	}
	
	private WeightedList<Spell> getKnownSpellWeights(final PCClass aClass, final int aLevel )
	{
		WeightedList<Spell> weightedList = theConfiguration.getKnownSpellWeights(aClass.getKeyName(), aLevel);
		if (weightedList == null)
		{
			weightedList = new WeightedList<Spell>();
			for ( final Spell spell : Globals.getSpellsIn(aLevel, aClass.getKeyName(), Constants.EMPTY_STRING) )
			{
				weightedList.add(1, spell);
			}
		}
		return weightedList;
	}

	private WeightedList<Spell> getPreparedSpellWeights(final PCClass aClass, final int aLevel )
	{
		WeightedList<Spell> weightedList = theConfiguration.getPreparedSpellWeights(aClass.getKeyName(), aLevel);
		if (weightedList == null)
		{
			weightedList = new WeightedList<Spell>();
			for ( final Spell spell : Globals.getSpellsIn(aLevel, aClass.getKeyName(), Constants.EMPTY_STRING) )
			{
				weightedList.add(1, spell);
			}
		}
		return weightedList;
	}

	private void selectDomainSpell( final PlayerCharacter aPC, final PCClass aClass, final int aLevel )
	{
		final int numDomains = aPC.getCharacterDomainList().size();
		if ( numDomains < 1 )
		{
			return;
		}
		final WeightedList<Domain> domains = new WeightedList<Domain>();
		for (int iDom = 0; iDom < numDomains; ++iDom)
		{
			CharacterDomain aCD = aPC.getCharacterDomainList().get(iDom);
			final Domain aDom = aCD.getDomain();

			// if any domains have this class as a source
			// and is a valid domain, add them
			if ((aDom != null) && aCD.isFromPCClass(aClass.getKeyName()))
			{
				domains.add( aDom );
			}
		}
		final Domain domain = domains.getRandomValue();
		final WeightedList<Spell> domainSpells = new WeightedList<Spell>(Globals.getSpellsIn(aLevel, Constants.EMPTY_STRING, domain.getKeyName()));
		selectSpell( aPC, aClass, domain, "Prepared Spells", domainSpells, aLevel ); //$NON-NLS-1$
	}
	
	private void selectSpell( final PlayerCharacter aPC, final PCClass aClass, final Domain aDomain, final String aBookName, final WeightedList<Spell> aSpellList, final int aLevel )
	{
		boolean added = false;
		while ( !added )
		{
			final Spell spell = aSpellList.get( Globals.getRandomInt(aSpellList.size()) );
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
			final String aString = aPC.addSpell(cs, new ArrayList<Ability>(), aClass.getKeyName(),
					   aBookName, aLevel, aLevel);
			if (aString.length() != 0)
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
		WeightedList<String> subClasses = theConfiguration.getSubClassWeights( aClass.getKeyName() );
		if (subClasses != null && subClasses.size() > 0)
		{
			aClass.setSubClassKey( subClasses.get( Globals.getRandomInt(subClasses.size()) ) );
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
	 * @param aNameChoice the name set to pick a name from
	 */
	public void generate(	final PlayerCharacter aPC, 
							final AlignGeneratorOption align,
							final RaceGeneratorOption aRace, 
							final GenderGeneratorOption aGender,
							final List<ClassGeneratorOption> classList, 
							final List<LevelGeneratorOption> levels,
							final GameModeRollMethod aRollMethod,
							final NameElement aNameChoice)
	{
		// Force a more quiet process
		final String oldChooser = ChooserFactory.getInterfaceClassname();
		ChooserFactory.setInterfaceClassname(
			"pcgen.util.chooser.RandomChooser"); //$NON-NLS-1$

		boolean tempShowHP = SettingsHandler.getShowHPDialogAtLevelUp();
		SettingsHandler.setShowHPDialogAtLevelUp(false);

		int tempChoicePref = SettingsHandler.getSingleChoicePreference();
		SettingsHandler.setSingleChoicePreference(Constants.
			CHOOSER_SINGLECHOICEMETHOD_SELECTEXIT);

		try
		{
			final int MAX_RETRIES = 5;
			for ( int i = 0; i < MAX_RETRIES; i++ )
			{
				final int randAlign = getAlignment( align );
				if (randAlign > 0)
				{
					Logging
						.debugPrint("NPCGenerator: Selected " + randAlign + " for alignment " + align); //$NON-NLS-1$//$NON-NLS-2$
					aPC.setAlignment(randAlign, false);
				}
				
				final Race r = getRace(aRace);
				if (r == null)
				{
					Logging.debugPrint( "NPCGenerator: Got null race.  Retrying." ); //$NON-NLS-1$
					continue;
				}
				Logging.debugPrint( "NPCGenerator: Selected " + r + " for race " + aRace ); //$NON-NLS-1$ //$NON-NLS-2$
				if (PrereqHandler.passesAll(r.getPreReqList(), aPC, r))
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
			
			final String gender = getGender( aGender );
			Logging.debugPrint( "NPCGenerator: Selecting " + gender + " for gender " + aGender ); //$NON-NLS-1$ //$NON-NLS-2$
			aPC.setGender( gender );

			boolean doneRacialClasses = false;
			for (int i = 0; i < classList.size(); i++)
			{
				int numLevels = getLevel(levels.get(i));
				Logging.debugPrint( "NPCGenerator: Selecting " + numLevels + " for level " + levels.get(i) ); //$NON-NLS-1$ //$NON-NLS-2$
				PCClass aClass = null;
				
				if ( !doneRacialClasses && aPC.getClassList().size() > 0 )
				{
					aClass = aPC.getClassList().get(0);
					numLevels = aClass.getLevel();
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
						if (aClass.getVisibility().equals(Visibility.DEFAULT)
							&& PrereqHandler.passesAll(aClass.getPreReqList(), aPC,
							aClass) && aClass.isQualified(aPC))
						{
							Logging.debugPrint( "NPCGenerator: Selecting " + aClass + " for class " + classList.get(i) ); //$NON-NLS-1$ //$NON-NLS-2$
							break;
						}
						// TODO Remove a failed class from the list.
						aClass = null;
						break;
					}
				}
				if (aClass == null)
				{
					continue;
				}
				
				final PCClass classCopy = aClass.clone();
				if ( classCopy.hasSubClass() )
				{
					selectSubClass(aPC, classCopy);
				}
				if (i == 0)
				{
					generateStats(aPC, classCopy, aRollMethod);
					selectDeity(aPC, classCopy);
				}

				int highestSpellLevel = aClass.getHighestLevelSpell(aPC);
				final int[] selectedSpells = new int[highestSpellLevel + 1];
				for ( int k = 0; k < highestSpellLevel; k++ ) { selectedSpells[k] = 0; }
				
				final int[] bonusSpells = new int[highestSpellLevel + 1];
				for ( int k = 0; k < highestSpellLevel; k++ ) { bonusSpells[k] = 0; }

				// Make a copy of the list because we are going to modify it.
				List<SkillChoice> skillList = new WeightedList<SkillChoice>(getSkillWeights(classCopy, aPC));
				List<Ability> featList = new WeightedList<Ability>(getFeatWeights(classCopy));
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
					
					if ( !pcClass.getSpellType().equals( Constants.s_NONE ) )
					{
						// This is a spellcasting class.  We may have to select
						// spells of some sort (known or prepared).
						if ( pcClass.hasKnownList() || pcClass.hasKnownSpells(aPC) )
						{
							Logging.debugPrint("NPCGenerator: known spells to select"); //$NON-NLS-1$
							for (int lvl = 0; lvl <= highestSpellLevel; ++lvl)
							{
								if (aPC.availableSpells(lvl, pcClass, Globals.getDefaultSpellBook(), true, true))
								{
									final int a = pcClass.getKnownForLevel(lvl, aPC);
									final int bonus = pcClass.getSpecialtyKnownForLevel(lvl, aPC);
									Logging.debugPrint("NPCGenerator: " + a + "known spells to select"); //$NON-NLS-1$ //$NON-NLS-2$
									
									final WeightedList<Spell> spellChoices = getKnownSpellWeights(pcClass, lvl);

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
								final int castTot = pcClass.getCastForLevel(lvl, "Prepared Spells", true, true, aPC);
								final int castNon = pcClass.getCastForLevel(lvl, "Prepared Spells", false, true, aPC);
								final int castSpec = castTot - castNon;
								Logging.debugPrint("NPCGenerator: " + castTot + "+" + castSpec + " prepared spells to select"); //$NON-NLS-1$ //$NON-NLS-2$
								if ( castSpec - bonusSpells[lvl] > 0 )
								{
									selectDomainSpell( aPC, pcClass, lvl );
									bonusSpells[lvl]++;
								}
								
								if (castTot > 0)
								{
									final WeightedList<Spell> spellChoices = getPreparedSpellWeights(pcClass, lvl);

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
			Globals.getBioSet().randomize(randBioString, aPC);
			
			final List<String> globalHairStyleList = SystemCollections.getUnmodifiableHairStyleList();
			aPC.setHairStyle(globalHairStyleList.get(Globals.getRandomInt(globalHairStyleList.size())));
			final List<String> speechList = SystemCollections.getUnmodifiableSpeechList();
			aPC.setSpeechTendency(speechList.get(Globals.getRandomInt(speechList.size())));
			final List<String> globalPhobiaList = SystemCollections.getUnmodifiablePhobiaList();
			aPC.setPhobias(globalPhobiaList.get(Globals.getRandomInt(globalPhobiaList.size())));
			final List<String> globalInterestsList = SystemCollections.getUnmodifiableInterestsList();
			aPC.setInterests(globalInterestsList.get(Globals.getRandomInt(globalInterestsList.size())));
			final List<String> globalPhraseList = SystemCollections.getUnmodifiablePhraseList();
			aPC.setCatchPhrase(globalPhraseList.get(Globals.getRandomInt(globalPhraseList.size())));
			final List<String> globalTraitList = SystemCollections.getUnmodifiableTraitList();
			aPC.setTrait1(globalTraitList.get(Globals.getRandomInt(globalTraitList.size())));
			aPC.setTrait2(globalTraitList.get(Globals.getRandomInt(globalTraitList.size())));

			final List<String> globalCityList = SystemCollections.getUnmodifiableCityList();
			aPC.setResidence(globalCityList.get(Globals.getRandomInt(globalCityList.size())));
			final List<String> globalLocationList = SystemCollections.getUnmodifiableLocationList();
			aPC.setLocation(globalLocationList.get(Globals.getRandomInt(globalLocationList.size())));
			final List<String> globalBirthplaceList = SystemCollections.getUnmodifiableBirthplaceList();
			aPC.setBirthplace(globalBirthplaceList.get(Globals.getRandomInt(globalBirthplaceList.size())));
			
			final Names nameGen = Names.getInstance();
			nameGen.init(aNameChoice, aPC);
			aPC.setName(nameGen.getRandomName());
		}
		catch (Exception e)
		{
			Logging.errorPrint("Problem generation NPC", e);
		}
		finally
		{
			SettingsHandler.setShowHPDialogAtLevelUp(tempShowHP);
			SettingsHandler.setSingleChoicePreference(tempChoicePref);
			ChooserFactory.setInterfaceClassname(oldChooser);
		}
	}
}
