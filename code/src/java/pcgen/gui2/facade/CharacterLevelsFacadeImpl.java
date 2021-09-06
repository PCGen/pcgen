/*
 * Copyright 2010 (C) James Dempsey
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.facet.BonusChangeFacet;
import pcgen.cdom.facet.BonusChangeFacet.BonusChangeEvent;
import pcgen.cdom.facet.BonusChangeFacet.BonusChangeListener;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.SkillFacet;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.SkillUtilities;
import pcgen.core.analysis.ChooseActivation;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.SkillDisplay;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.facade.core.CharacterLevelFacade;
import pcgen.facade.core.CharacterLevelsFacade;
import pcgen.facade.core.DataSetFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.AbstractListFacade;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.enumeration.Tab;

import org.jetbrains.annotations.Nullable;

/**
 * The Class {@code CharacterLevelsFacadeImpl} is an implementation of
 * the CharacterLevelsFacade interface for the new user interface. It allows 
 * the user interface to work with the class levels of a character.
 *
 * 
 */
public class CharacterLevelsFacadeImpl extends AbstractListFacade<CharacterLevelFacade>
		implements CharacterLevelsFacade, DataFacetChangeListener<CharID, Skill>, BonusChangeListener
{
	private PlayerCharacter theCharacter;
	private CharacterDisplay charDisplay;

	private final UIDelegate delegate;

	private List<PCClass> classLevels;
	private List<CharacterLevelFacade> charLevels;
	private final TodoManager todoManager;
	private CharID charID;
	private final DataSetFacade dataSetFacade;
	private final CharacterFacadeImpl characterFacadeImpl;

	/**
	 * Create a new CharacterLevelsFacadeImpl instance for a character.
	 * @param pc The character we are creating the instance for 
	 * @param delegate The user interface delegate that can do dialogs and choosers for us.
	 * @param todoManager The user tasks tracker.
	 * @param dataSetFacade The datasets that the character is using.
	 * @param characterFacadeImpl The facade managing the character.
	 */
	CharacterLevelsFacadeImpl(PlayerCharacter pc, UIDelegate delegate, TodoManager todoManager,
		DataSetFacade dataSetFacade, CharacterFacadeImpl characterFacadeImpl)
	{
		this.theCharacter = pc;
		this.characterFacadeImpl = characterFacadeImpl;
		this.charDisplay = pc.getDisplay();
		this.delegate = delegate;
		this.todoManager = todoManager;
		this.dataSetFacade = dataSetFacade;
		initForCharacter();
	}

	/**
	 * Tidy up character listeners when closing the character. 
	 */
	void closeCharacter()
	{
		SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		skillFacet.removeDataFacetChangeListener(this);
		BonusChangeFacet bcf = FacetLibrary.getFacet(BonusChangeFacet.class);
		for (Skill skillFacade : dataSetFacade.getSkills())
		{
			bcf.removeBonusChangeListener(this, "SKILLRANK", skillFacade.getKeyName().toUpperCase());
		}
		theCharacter = null;
		charDisplay = null;
		charID = null;
	}

	/**
	 * Initialise the instance for the current character. 
	 */
	private void initForCharacter()
	{
		classLevels = new ArrayList<>();
		charLevels = new ArrayList<>();
		refreshClassList();

		charID = theCharacter.getCharID();
		SkillFacet skillFacet = FacetLibrary.getFacet(SkillFacet.class);
		skillFacet.addDataFacetChangeListener(this);
		BonusChangeFacet bcf = FacetLibrary.getFacet(BonusChangeFacet.class);
		for (Skill skillFacade : dataSetFacade.getSkills())
		{
			bcf.addBonusChangeListener(this, "SKILLRANK", skillFacade.getKeyName().toUpperCase());
		}

	}

	@Override
	public CharacterLevelFacade getElementAt(int index)
	{
		if (index < 0 || index >= charLevels.size())
		{
			return null;
		}
		return charLevels.get(index);
	}

	@Override
	public int getSize()
	{
		return charLevels.size();
	}

	private void clearContents()
	{
		charLevels.clear();
		fireElementsChanged(this);
	}

	private void addElement(CharacterLevelFacadeImpl levelFI)
	{
		int index = charLevels.size();
		charLevels.add(levelFI);
		fireElementAdded(this, levelFI, index);
	}

	private void removeElement(int i)
	{
		fireElementRemoved(this, charLevels.remove(i), i);
	}

	/**
	 * Update the list of class levels from scratch to match the current
	 * state of the character. 
	 */
	private void refreshClassList()
	{
		List<PCClass> newClasses = charDisplay.getClassList();
		Collection<PCLevelInfo> levelInfo = charDisplay.getLevelInfo();

		Map<String, Integer> levelCount = new HashMap<>();
		Map<String, PCClass> classMap = new HashMap<>();
		for (PCClass pcClass : newClasses)
		{
			levelCount.put(pcClass.getKeyName(), 1);
			classMap.put(pcClass.getKeyName(), pcClass);
		}

		classLevels.clear();
		clearContents();
		for (PCLevelInfo lvlInfo : levelInfo)
		{
			final String classKeyName = lvlInfo.getClassKeyName();
			PCClass currClass = classMap.get(classKeyName);
			if (currClass == null)
			{
				Logging
					.errorPrint("No PCClass found for '" + classKeyName + "' in character's class list: " + newClasses);
				return;
			}

			int clsLvlNum = levelCount.get(classKeyName);
			levelCount.put(classKeyName, clsLvlNum + 1);

			classLevels.add(currClass);

			CharacterLevelFacadeImpl levelFI = new CharacterLevelFacadeImpl(currClass, classLevels.size());
			addElement(levelFI);
			//PCClassLevel classLevel = currClass.getClassLevel(clsLvlNum);
		}
		updateSkillsTodo();
	}

	@Override
	public PCClass getClassTaken(CharacterLevelFacade level)
	{
		if (level == null || !(level instanceof CharacterLevelFacadeImpl))
		{
			return null;
		}
		return classLevels.get(getLevelIndex(level));
	}

	private PCClassLevel getClassLevel(CharacterLevelFacade level)
	{
		if (level == null || !(level instanceof CharacterLevelFacadeImpl levelImpl))
		{
			return null;
		}
		int lvlIdx = levelImpl.getCharacterLevel() - 1;

		final String classKeyName = charDisplay.getLevelInfoClassKeyName(lvlIdx);
		PCClass aClass = theCharacter.getClassKeyed(classKeyName);

		if (aClass != null)
		{
			final int clsLvl = charDisplay.getLevelInfoClassLevel(lvlIdx);
			return charDisplay.getActiveClassLevel(aClass, clsLvl - 1);
		}

		return null;
	}

	@Override
	public int getHPGained(CharacterLevelFacade level)
	{
		int numHp = getHPRolled(level);

		numHp += (int) charDisplay.getStatBonusTo("HP", "BONUS");

		if (numHp < 1)
		{
			numHp = 1;
		}

		return numHp;

	}

	@Override
	public int getHPRolled(CharacterLevelFacade level)
	{
		PCClassLevel classLevel = getClassLevel(level);
		if (classLevel == null)
		{
			return 0;
		}

		return charDisplay.getHP(classLevel);
	}

	@Override
	public void setHPRolled(CharacterLevelFacade level, int hp)
	{
		PCClassLevel classLevel = getClassLevel(level);
		if (classLevel != null)
		{
			theCharacter.setHP(classLevel, hp);
			fireHitPointEvent(this, getLevelIndex(level), false);
		}
	}

	PCLevelInfo getLevelInfo(CharacterLevelFacade level)
	{
		if (level == null || !(level instanceof CharacterLevelFacadeImpl))
		{
			return null;
		}

		return charDisplay.getLevelInfo(getLevelIndex(level));
	}

	private int getLevelIndex(CharacterLevelFacade level)
	{
		CharacterLevelFacadeImpl levelImpl = (CharacterLevelFacadeImpl) level;
		if (levelImpl == null)
		{
			return 0;
		}
		return levelImpl.getCharacterLevel() - 1;
	}

	@Override
	public int getGainedSkillPoints(CharacterLevelFacade level)
	{
		PCLevelInfo classLevel = getLevelInfo(level);
		if (classLevel == null)
		{
			return 0;
		}
		return classLevel.getSkillPointsGained(theCharacter);
	}

	@Override
	public float getMaxRanks(CharacterLevelFacade level, SkillCost cost, boolean isClassForMaxRanks)
	{
		if (cost == null || level == null || !(level instanceof CharacterLevelFacadeImpl levelImpl))
		{
			return 0.0f;
		}
		if (cost.getCost() == 0)
		{
			return Float.NaN;
		}
		SkillCost costForMaxRanks = isClassForMaxRanks ? SkillCost.CLASS : cost;

		CharacterLevelFacadeImpl levelImpl = (CharacterLevelFacadeImpl) level;
		return switch (costForMaxRanks)
				{
					case CLASS -> SkillUtilities.maxClassSkillForLevel(levelImpl.getCharacterLevel(), theCharacter)
					                            .floatValue();
					case CROSS_CLASS -> SkillUtilities.maxCrossClassSkillForLevel(
							levelImpl.getCharacterLevel(),
							theCharacter
					).floatValue();
					case EXCLUSIVE ->
							// We can't test if the skill in question is valid for all classes
							// So just assume it is for the time being. A check on the total
							// levels for the skill itself will need to be made elsewhere
							SkillUtilities.maxClassSkillForLevel(levelImpl.getCharacterLevel(), theCharacter)
							              .floatValue();
				};
	}

	@Override
	public SkillCost getSkillCost(CharacterLevelFacade level, Skill skill)
	{
		if (level != null && level instanceof CharacterLevelFacadeImpl && charDisplay != null)
		{
			final String classKeyName = charDisplay.getLevelInfoClassKeyName(getLevelIndex(level));
			PCClass aClass = theCharacter.getClassKeyed(classKeyName);
			return theCharacter.getSkillCostForClass(skill, aClass);
		}

		return null;
	}

	@Override
	public boolean isClassSkillForMaxRanks(CharacterLevelFacade level, Skill skill)
	{
		for (int i = 0; i < charLevels.size(); i++)
		{
			CharacterLevelFacade testLevel = getElementAt(i);

			if (getSkillCost(testLevel, skill) == SkillCost.CLASS)
			{
				return true;
			}

			if (testLevel == level)
			{
				// Break as we have reached the level to be checked and it hasn't been class yet
				return false;
			}
		}
		return false;
	}

	@Override
	public float getSkillRanks(@Nullable CharacterLevelFacade level, @Nullable Skill skill)
	{
		// TODO Ranks aren't stored by level - have compromised by returning the total. Further discussion needed
		if (skill == null)
		{
			return 0;
		}
		return SkillRankControl.getTotalRank(theCharacter, skill);
	}

	@Override
	public SkillBreakdown getSkillBreakdown(CharacterLevelFacade level, Skill skill)
	{
		SkillBreakdown sb = new SkillBreakdown();
		// TODO Ranks aren't stored by level - have compromised by returning the total. Further discussion needed 
		sb.ranks = SkillRankControl.getTotalRank(theCharacter, skill);
		sb.modifier = SkillModifier.modifier(skill, theCharacter);
		sb.total = sb.modifier + (int) sb.ranks;
		return sb;
	}

	@Override
	public int getSpentSkillPoints(CharacterLevelFacade level)
	{
		PCLevelInfo classLevel = getLevelInfo(level);
		if (classLevel == null)
		{
			return 0;
		}
		return classLevel.getSkillPointsGained(theCharacter) - classLevel.getSkillPointsRemaining();
	}

	@Override
	public int getRemainingSkillPoints(CharacterLevelFacade level)
	{
		PCLevelInfo classLevel = getLevelInfo(level);
		if (classLevel == null)
		{
			return 0;
		}
		return classLevel.getSkillPointsRemaining();
	}

	@Override
	public boolean investSkillPoints(CharacterLevelFacade level, Skill skill, int points)
	{
		if (points == 0 || level == null || !(level instanceof CharacterLevelFacadeImpl))
		{
			Logging.errorPrint(
				"Invalid request to investSkillPoints in " + skill + ". Points: " + points + " level: " + level);
			return false;
		}

		PCLevelInfo classLevel = getLevelInfo(level);
		int skillPool;
		if (Globals.getGameModeHasPointPool())
		{
			skillPool = theCharacter.getSkillPoints();
		}
		else
		{
			skillPool = classLevel.getSkillPointsRemaining();

			if ((points < 0) && (((skillPool - points) > classLevel.getSkillPointsGained(theCharacter))
				|| !classHasRanksIn(skill, ((CharacterLevelFacadeImpl) level).getSelectedClass())))
			{
				level = findLevelWithSpentSkillPoints(points, skill);
				if (level == null)
				{
					delegate.showInfoMessage(Constants.APPLICATION_NAME,
						LanguageBundle.getFormattedString("in_iskErr_message_05", skill));
					return false;
				}

				classLevel = getLevelInfo(level);
				skillPool = classLevel.getSkillPointsRemaining();
			}
		}

		if ((points > 0) && (points > skillPool))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getFormattedString("in_iskErr_message_04a", String.valueOf(skillPool)));

			return false;
		}

		SkillCost sc = getSkillCost(level, skill);
		if (sc == null)
		{
			Logging.errorPrint("Failed to get skillcost for skill " + skill + ". Could not process request to invest "
				+ points + " in the skill");
			return false;
		}

		if (sc.equals(SkillCost.EXCLUSIVE))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, LanguageBundle.getString("in_iskErr_message_06"));

			return false;
		}

		final double cost = sc.getCost();
		double rank = points / cost;

		boolean hasSkill = charDisplay.hasSkill(skill);
		if (!hasSkill)
		{
			SkillDisplay.updateSkillsOutputOrder(theCharacter, skill);
		}

		final String classKeyName = charDisplay.getLevelInfoClassKeyName(getLevelIndex(level));
		PCClass aClass = theCharacter.getClassKeyed(classKeyName);
		String errMessage = SkillRankControl.modRanks(rank, aClass, false, theCharacter, skill);

		if ("".equals(errMessage)) //$NON-NLS-1$
		{
			classLevel.setSkillPointsRemaining(skillPool - points);
		}

		if (ChooseActivation.hasNewChooseToken(skill) && characterFacadeImpl != null)
		{
			characterFacadeImpl.postLevellingUpdates();
		}

		if (!errMessage.isEmpty())
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, errMessage);

			return false;
		}

		updateSkillsTodo();
		fireSkillPointEvent(this, getLevelIndex(level), false);
		fireSkillBonusEvent(this, getLevelIndex(level), false);
		return true;
	}

	@Override
	public CharacterLevelFacade findNextLevelForSkill(Skill skill, CharacterLevelFacade baseLevel, float newRank)
	{
		SkillCost skillCost = getSkillCost(baseLevel, skill);
		float maxRanks = getMaxRanks(baseLevel, skillCost, isClassSkillForMaxRanks(baseLevel, skill));

		float currRank = SkillRankControl.getTotalRank(theCharacter, skill);
		if (newRank < currRank)
		{
			// 1. Selected level (if class had purchased a rank and is not above max ranks)
			if (classHasRanksIn(skill, ((CharacterLevelFacadeImpl) baseLevel).getSelectedClass())
				&& !Float.isNaN(maxRanks) && maxRanks >= currRank && getSpentSkillPoints(baseLevel) > 0)
			{
				return baseLevel;
			}

			// 2. Scan from level 1 for first level of the same class as currently 
			// selected level in which the rank to be removed is below max ranks and 
			// is a class that has bought ranks in the class
			CharacterLevelFacade levelToRefundSkill =
					scanForLevelToRefundSkill(skill, currRank, getClassTaken(baseLevel));
			if (levelToRefundSkill != null)
			{
				return levelToRefundSkill;
			}

			// 3. Scan from level 1 for first level of any class in which the rank 
			// to be removed is below max ranks and is a class that has bought 
			// ranks in the class
			levelToRefundSkill = scanForLevelToRefundSkill(skill, currRank, null);
			return levelToRefundSkill;
		}

		// Check if current level ok
		if (!Float.isNaN(maxRanks) && maxRanks >= newRank && getRemainingSkillPoints(baseLevel) > 0)
		{
			return baseLevel;
		}

		// Check for class cost on this level or higher
		int baseLevelIndex = getLevelIndex(baseLevel);
		CharacterLevelFacade levelToBuySkill =
				scanForwardforLevelToBuySkill(skill, newRank, baseLevelIndex, SkillCost.CLASS);
		if (levelToBuySkill != null)
		{
			return levelToBuySkill;
		}
		// Check for class cost on any level
		levelToBuySkill = scanForwardforLevelToBuySkill(skill, newRank, 0, SkillCost.CLASS);
		if (levelToBuySkill != null)
		{
			return levelToBuySkill;
		}
		// Check for any cost on this level or higher
		levelToBuySkill = scanForwardforLevelToBuySkill(skill, newRank, baseLevelIndex, null);
		if (levelToBuySkill != null)
		{
			return levelToBuySkill;
		}
		// Check for any cost on any level
		levelToBuySkill = scanForwardforLevelToBuySkill(skill, newRank, 0, null);

		return levelToBuySkill;
	}

	private CharacterLevelFacade scanForwardforLevelToBuySkill(Skill aSkill, float testRank, int baseLevelIndex,
		SkillCost costToMatch)
	{
		for (int i = baseLevelIndex; i < charLevels.size(); i++)
		{
			CharacterLevelFacade testLevel = getElementAt(i);
			//Logging.errorPrint("Checking " + testLevel);
			if (getRemainingSkillPoints(testLevel) <= 0)
			{
				//Logging.errorPrint("Skipping level " + testLevel + " as it does not have points left.");
				continue;
			}
			SkillCost skillCost = getSkillCost(testLevel, aSkill);
			if (costToMatch != null && skillCost.getCost() != costToMatch.getCost())
			{
				//Logging.errorPrint("Skipping level " + testLevel + " as it is not the same cost as " + costToMatch);
				continue;
			}
			float maxRanks = getMaxRanks(testLevel, skillCost, isClassSkillForMaxRanks(testLevel, aSkill));
			if (!Float.isNaN(maxRanks) && maxRanks >= testRank)
			{
				//Logging.errorPrint("Selected level " + testLevel);
				return testLevel;
			}
			//Logging.errorPrint("Skipping level " + testLevel + " as skill is above max ranks");
		}
		return null;
	}

	private CharacterLevelFacade scanForLevelToRefundSkill(Skill aSkill, float testRank, PCClass classToMatch)
	{
		for (int i = 0; i < charLevels.size(); i++)
		{
			CharacterLevelFacade testLevel = getElementAt(i);
			//Logging.errorPrint("Checking " + testLevel);
			String lvlClassName = getLevelInfo(testLevel).getClassKeyName();
			if (classToMatch != null && !classToMatch.getKeyName().equals(lvlClassName))
			{
				//Logging.errorPrint("Skipping level " + testLevel + " as it is not the same class as " + classToMatch);
				continue;
			}
			if (!classHasRanksIn(aSkill, ((CharacterLevelFacadeImpl) testLevel).getSelectedClass()))
			{
				//Logging.errorPrint("Skipping level " + testLevel + " as it does not have ranks in " + aSkill);
				continue;
			}
			if (getSpentSkillPoints(testLevel) <= 0)
			{
				//Logging.errorPrint("Skipping level " + testLevel + " as it does not have spent points.");
				continue;
			}
			SkillCost skillCost = getSkillCost(testLevel, aSkill);
			float maxRanks = getMaxRanks(testLevel, skillCost, isClassSkillForMaxRanks(testLevel, aSkill));
			if (!Float.isNaN(maxRanks) && maxRanks >= testRank)
			{
				//Logging.errorPrint("Selected level " + testLevel);
				return testLevel;
			}
			//Logging.errorPrint("Skipping level " + testLevel + " as skill is above max ranks");
		}
		return null;
	}

	/**
	 * Find a level which has a certain number of points spent.
	 * @param points The negative number of points spent required.
	 * @param skill 
	 * @return The level with spent points, or null if none match
	 */
	private CharacterLevelFacade findLevelWithSpentSkillPoints(int points, Skill skill)
	{
		for (int i = charLevels.size() - 1; i >= 0; i--)
		{
			CharacterLevelFacadeImpl levelFacade = (CharacterLevelFacadeImpl) charLevels.get(i);
			PCLevelInfo levelInfo = getLevelInfo(levelFacade);
			if (levelInfo.getSkillPointsRemaining() - points <= levelInfo.getSkillPointsGained(theCharacter))
			{
				if (classHasRanksIn(skill, levelFacade.getSelectedClass()))
				{
					return levelFacade;
				}
			}
		}
		return null;
	}

	/**
	 * Identify if the class has ranks in the skill.
	 * @param skill The skill to be checked for.
	 * @param pcClass The class being checked.
	 * @return true if the character took ranks of the skill in the class.
	 */
	private boolean classHasRanksIn(Skill skill, PCClass pcClass)
	{
		Double rank = theCharacter.getSkillRankForClass(skill, pcClass);
		return (rank != null) && (rank > 0.0d);
	}

	void updateSkillsTodo()
	{
		int remainingPoints = calcRemainingSkillPoints();
		if (remainingPoints < 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SKILLS, "Skills", "in_iskTodoTooMany", 1));
			todoManager.removeTodo("in_iskTodoRemain");
		}
		else if (remainingPoints > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(Tab.SKILLS, "Skills", "in_iskTodoRemain", 1));
			todoManager.removeTodo("in_iskTodoTooMany");
		}
		else
		{
			todoManager.removeTodo("in_iskTodoRemain");
			todoManager.removeTodo("in_iskTodoTooMany");
		}
	}

	/**
	 * @return The total of each level's remaining skill points.
	 */
	private int calcRemainingSkillPoints()
	{
		return charLevels.stream()
		                 .mapToInt(this::getRemainingSkillPoints)
		                 .sum();
	}

	@Override
	public void setGainedSkillPoints(CharacterLevelFacade level, int points)
	{
		int spentSkillPoints = getSpentSkillPoints(level);
		PCLevelInfo classLevel = getLevelInfo(level);
		classLevel.setSkillPointsGained(theCharacter, points);
		classLevel.setSkillPointsRemaining(points - spentSkillPoints);

		fireSkillPointEvent(this, getLevelIndex(level), false);
	}

	// ============== Level Management code =========================

	/**
	 * Register the addition of a new level to the character of the 
	 * specified class. It is expected that the backing PlayerCharacter object 
	 * will be updated by our caller.
	 * @param theClassLevel The class the level is in.
	 */
	void addLevelOfClass(CharacterLevelFacadeImpl theClassLevel)
	{
		PCClass theClass = theClassLevel.getSelectedClass();
		classLevels.add(theClass);
		addElement(theClassLevel);
		updateSkillsTodo();
	}

	/**
	 * Remove the last level gained. It is expected that the backing 
	 * PlayerCharacter object will be updated by our caller.
	 */
	void removeLastLevel()
	{
		classLevels.remove(classLevels.size() - 1);
		removeElement(getSize() - 1);
		updateSkillsTodo();
	}

	void classListRefreshRequired()
	{
		refreshClassList();
		fireClassChangedEvent(this, 0, true);
		fireSkillBonusEvent(this, 0, true);
	}

	// ============== Listener Management code =========================

	@Override
	public void addClassListener(ClassListener listener)
	{
		listenerList.add(ClassListener.class, listener);
	}

	@Override
	public void addHitPointListener(HitPointListener listener)
	{
		listenerList.add(HitPointListener.class, listener);
	}

	@Override
	public void addSkillBonusListener(SkillBonusListener listener)
	{
		listenerList.add(SkillBonusListener.class, listener);
	}

	@Override
	public void addSkillPointListener(SkillPointListener listener)
	{
		listenerList.add(SkillPointListener.class, listener);
	}

	@Override
	public void removeHitPointListener(HitPointListener listener)
	{
		listenerList.remove(HitPointListener.class, listener);
	}

	@Override
	public void removeSkillBonusListener(SkillBonusListener listener)
	{
		listenerList.remove(SkillBonusListener.class, listener);
	}

	@Override
	public void removeSkillPointListener(SkillPointListener listener)
	{
		listenerList.remove(SkillPointListener.class, listener);
	}

	private void fireClassChangedEvent(Object source, int baseLevelIndex, boolean stacks)
	{
		Object[] listeners = listenerList.getListenerList();
		CharacterLevelEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == ClassListener.class)
			{
				if (e == null)
				{
					e = new CharacterLevelEvent(source, baseLevelIndex, stacks);
				}
				((ClassListener) listeners[i + 1]).classChanged(e);
			}
		}
	}

	private void fireHitPointEvent(Object source, int baseLevelIndex, boolean stacks)
	{
		Object[] listeners = listenerList.getListenerList();
		CharacterLevelEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == HitPointListener.class)
			{
				if (e == null)
				{
					e = new CharacterLevelEvent(source, baseLevelIndex, stacks);
				}
				((HitPointListener) listeners[i + 1]).hitPointsChanged(e);
			}
		}
	}

	private void fireSkillPointEvent(Object source, int baseLevelIndex, boolean stacks)
	{
		Object[] listeners = listenerList.getListenerList();
		CharacterLevelEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == SkillPointListener.class)
			{
				if (e == null)
				{
					e = new CharacterLevelEvent(source, baseLevelIndex, stacks);
				}
				((SkillPointListener) listeners[i + 1]).skillPointsChanged(e);
			}
		}
	}

	void fireSkillBonusEvent(Object source, int baseLevelIndex, boolean stacks)
	{
		Object[] listeners = listenerList.getListenerList();
		CharacterLevelEvent e = null;
		for (int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if (listeners[i] == SkillBonusListener.class)
			{
				if (e == null)
				{
					e = new CharacterLevelEvent(source, baseLevelIndex, stacks);
				}
				((SkillBonusListener) listeners[i + 1]).skillBonusChanged(e);
			}
		}
	}

	@Override
	public void dataAdded(DataFacetChangeEvent<CharID, Skill> dfce)
	{
		if (dfce.getCharID() != charID)
		{
			return;
		}
		Skill skill = dfce.getCDOMObject();
		if (theCharacter.getRank(skill) > 0)
		{
			fireSkillBonusEvent(this, 0, true);
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<CharID, Skill> dfce)
	{
		if (dfce.getCharID() != charID)
		{
			return;
		}
		//Skill skill = dfce.getCDOMObject();
		fireSkillBonusEvent(this, 0, true);
	}

	@Override
	public void bonusChange(BonusChangeEvent bce)
	{
		if (bce.getCharID() != charID || bce.getOldVal() == null)
		{
			return;
		}
		fireSkillBonusEvent(this, 0, true);
	}

}
