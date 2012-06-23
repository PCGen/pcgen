/*
 * CharacterLevelsFacadeImpl.java
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
 *
 * Created on 03/06/2010 12:09:38 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.base.util.NamedValue;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SkillCost;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Skill;
import pcgen.core.SkillComparator;
import pcgen.core.SkillUtilities;
import pcgen.core.analysis.SkillModifier;
import pcgen.core.analysis.SkillRankControl;
import pcgen.core.facade.CharacterLevelFacade;
import pcgen.core.facade.CharacterLevelsFacade;
import pcgen.core.facade.ClassFacade;
import pcgen.core.facade.SkillFacade;
import pcgen.core.facade.TodoFacade.CharacterTab;
import pcgen.core.facade.UIDelegate;
import pcgen.core.facade.util.AbstractListFacade;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.utils.CoreUtility;
import pcgen.gui.GuiConstants;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The Class <code>CharacterLevelsFacadeImpl</code> is an implementation of 
 * the CharacterLevelsFacade interface for the new user interface. It allows 
 * the user interface to work with the class levels of a character.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class CharacterLevelsFacadeImpl extends AbstractListFacade<CharacterLevelFacade> implements CharacterLevelsFacade
{
	private PlayerCharacter theCharacter;
	private UIDelegate delegate;

	private List<ClassFacade> classLevels;
	private List<CharacterLevelFacade> charLevels;
	private final TodoManager todoManager;
	
	/**
	 * Create a new CharacterLevelsFacadeImpl instance for a character.
	 * @param pc The character we are creating the instance for 
	 * @param delegate
	 */
	CharacterLevelsFacadeImpl(PlayerCharacter pc, UIDelegate delegate, TodoManager todoManager)
	{
		this.theCharacter = pc;
		this.delegate = delegate;
		this.todoManager = todoManager;
		initForCharacter();
	}

	/**
	 * Initialise the instance for the current character. 
	 */
	private void initForCharacter()
	{
		classLevels = new ArrayList<ClassFacade>();
		charLevels = new ArrayList<CharacterLevelFacade>();
		refreshClassList();
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
		List<PCClass> newClasses = theCharacter.getClassList();
		Collection<PCLevelInfo> levelInfo = theCharacter.getLevelInfo();

		Map<String, Integer> levelCount = new HashMap<String, Integer>();
		Map<String, PCClass> classMap = new HashMap<String, PCClass>();
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
				Logging.errorPrint("No PCClass found for '" + classKeyName +
						"' in character's class list: " + newClasses);
				return;
			}

			int clsLvlNum = levelCount.get(classKeyName);
			levelCount.put(classKeyName, clsLvlNum + 1);
			
			classLevels.add(currClass);
			
			CharacterLevelFacadeImpl levelFI = new CharacterLevelFacadeImpl(currClass, theCharacter, classLevels.size());
			addElement(levelFI);
			//PCClassLevel classLevel = currClass.getClassLevel(clsLvlNum);
		}
		updateSkillsTodo();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getClassTaken(int)
	 */
	@Override
	public ClassFacade getClassTaken(CharacterLevelFacade level)
	{
		if (level == null || !(level instanceof CharacterLevelFacadeImpl))
		{
			return null;
		}
		return classLevels.get(getLevelIndex(level));
	}

	private PCClassLevel getClassLevel(CharacterLevelFacade level)
	{
		if (level == null || !(level instanceof CharacterLevelFacadeImpl))
		{
			return null;
		}
		CharacterLevelFacadeImpl levelImpl = (CharacterLevelFacadeImpl) level;
		int lvlIdx = levelImpl.getCharacterLevel() - 1;

		final String classKeyName = theCharacter.getLevelInfoClassKeyName(lvlIdx);
		PCClass aClass = theCharacter.getClassKeyed(classKeyName);
		
		if (aClass != null)
		{
			final int clsLvl = theCharacter.getLevelInfoClassLevel(lvlIdx);
			PCClassLevel pcl = theCharacter.getActiveClassLevel(aClass, clsLvl-1);

			return pcl;
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getHPGained(int)
	 */
	@Override
	public int getHPGained(CharacterLevelFacade level)
	{
		int numHp = getHPRolled(level);

		numHp += (int) theCharacter.getStatBonusTo("HP", "BONUS");

		if (numHp < 1)
		{
			numHp = 1;
		}

		return Integer.valueOf(numHp);

	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getHPRolled(int)
	 */
	@Override
	public int getHPRolled(CharacterLevelFacade level)
	{
		PCClassLevel classLevel = getClassLevel(level);
		if (classLevel == null)
		{
			return 0;
		}

		return theCharacter.getHP(classLevel);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#setHPRolled(int, int)
	 */
	@Override
	public void setHPRolled(CharacterLevelFacade level, int hp)
	{
		PCClassLevel classLevel = getClassLevel(level);
		theCharacter.setHP(classLevel, Integer.valueOf(hp));
		fireHitPointEvent(this, getLevelIndex(level), false);
	}

	private PCLevelInfo getLevelInfo(CharacterLevelFacade level)
	{
		if (level == null
			|| !(level instanceof CharacterLevelFacadeImpl))
		{
			return null;
		}

		return theCharacter.getLevelInfo(getLevelIndex(level));
	}
	
	private int getLevelIndex(CharacterLevelFacade level)
	{
		CharacterLevelFacadeImpl levelImpl = (CharacterLevelFacadeImpl) level;
		return levelImpl.getCharacterLevel()-1;
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getGainedSkillPoints(int)
	 */
	@Override
	public int getGainedSkillPoints(CharacterLevelFacade level)
	{
		PCLevelInfo classLevel = getLevelInfo(level);
		return classLevel.getSkillPointsGained(theCharacter);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getMaxRanks(int, pcgen.cdom.enumeration.SkillCost)
	 */
	@Override
	public float getMaxRanks(CharacterLevelFacade level, SkillCost cost)
	{
		if (cost == null || level == null
				|| !(level instanceof CharacterLevelFacadeImpl))
		{
			return 0.0f;
		}
		if (cost.getCost() == 0)
		{
			return Float.NaN;
		}
		CharacterLevelFacadeImpl levelImpl = (CharacterLevelFacadeImpl) level;
		if (cost == SkillCost.CLASS)
		{
			return SkillUtilities.maxClassSkillForLevel(
				levelImpl.getCharacterLevel(), theCharacter).floatValue();
		}
		else if (cost == SkillCost.CROSS_CLASS)
		{
			return SkillUtilities.maxCrossClassSkillForLevel(
				levelImpl.getCharacterLevel(), theCharacter).floatValue();
		} 
		else if (cost == SkillCost.EXCLUSIVE)
		{
			// We can't test if the skill in question is valid for all classes 
			// So just assume it is for the time being. A check on the total 
			// levels for the skill itself will need to be made elsewhere 
			return SkillUtilities.maxClassSkillForLevel(
				levelImpl.getCharacterLevel(), theCharacter).floatValue();
		} 
		return Float.NaN;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getRankCost(int, pcgen.cdom.enumeration.SkillCost)
	 */
	@Override
	public int getRankCost(CharacterLevelFacade level, SkillCost cost)
	{
		return cost.getCost();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getSkillCost(int, pcgen.core.facade.SkillFacade)
	 */
	@Override
	public SkillCost getSkillCost(CharacterLevelFacade level, SkillFacade skill)
	{
		if (level != null && level instanceof CharacterLevelFacadeImpl)
		{
			final String classKeyName = theCharacter.getLevelInfoClassKeyName(getLevelIndex(level));
			PCClass aClass = theCharacter.getClassKeyed(classKeyName);
			if (skill instanceof Skill)
			{
				return theCharacter.getSkillCostForClass((Skill) skill, aClass);
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getSkillModifier(int, pcgen.core.facade.SkillFacade)
	 */
	@Override
	public int getSkillModifier(CharacterLevelFacade level, SkillFacade skill)
	{
		if (skill instanceof Skill)
		{
			return SkillModifier.modifier((Skill) skill, theCharacter);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getSkillRanks(int, pcgen.core.facade.SkillFacade)
	 */
	@Override
	public float getSkillRanks(CharacterLevelFacade level, SkillFacade skill)
	{
		// TODO Ranks aren't stored by level - have compromised by returning the total. Further discussion needed 
		if (skill instanceof Skill)
		{
			return SkillRankControl.getTotalRank(theCharacter, (Skill) skill);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getSkillTotal(int, pcgen.core.facade.SkillFacade)
	 */
	@Override
	public int getSkillTotal(CharacterLevelFacade level, SkillFacade skill)
	{
		// TODO Ranks aren't stored by level - have compromised by returning the total. Further discussion needed 
		if (skill instanceof Skill)
		{
			Float ranks =  SkillRankControl.getTotalRank(theCharacter, (Skill) skill);
			Integer mods = SkillModifier.modifier((Skill) skill, theCharacter);
			return Integer.valueOf(mods.intValue() + ranks.intValue());
		}
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getSpentSkillPoints(int)
	 */
	@Override
	public int getSpentSkillPoints(CharacterLevelFacade level)
	{
		PCLevelInfo classLevel = getLevelInfo(level);
		if (classLevel == null)
		{
			return 0;
		}
		return classLevel.getSkillPointsGained(theCharacter)
			- classLevel.getSkillPointsRemaining();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#getRemainingSkillPoints(int)
	 */
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

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#investSkillPoints(int, pcgen.core.facade.SkillFacade, int)
	 */
	@Override
	public boolean investSkillPoints(CharacterLevelFacade level, SkillFacade skill, int points)
	{
		if (points == 0 || level == null
				|| !(level instanceof CharacterLevelFacadeImpl))
		{
			Logging.errorPrint("Invalid request to investSkillPoints in "
				+ skill + ". Points: " + points + " level: " + level);
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

			if ((points < 0)
				&& (((skillPool - points) > classLevel
					.getSkillPointsGained(theCharacter))
				|| !classHasRanksIn(skill,
					((CharacterLevelFacadeImpl) level).getSelectedClass())))
			{
				level = findLevelWithSpentSkillPoints(points, skill);
				if (level == null)
				{
					delegate.showInfoMessage(Constants.APPLICATION_NAME,
						LanguageBundle.getFormattedString(
							"in_iskErr_message_05a", classLevel
								.getClassKeyName(), String.valueOf(classLevel
								.getClassLevel()), String.valueOf(classLevel
								.getSkillPointsGained(theCharacter))));
					return false;
				}

				classLevel = getLevelInfo(level);
				skillPool = classLevel.getSkillPointsRemaining();
			}
		}

		if ((points > 0) && (points > skillPool))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getFormattedString("in_iskErr_message_04a", String
					.valueOf(skillPool)));

			return false;
		}
		
		SkillCost sc = getSkillCost(level, skill);
		if (sc == null)
		{
			Logging.errorPrint("Failed to get skillcost for skill " + skill
				+ ". Could not process request to invest " + points
				+ " in the skill");
			return false;
		}

		if (sc.equals(SkillCost.EXCLUSIVE))
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, LanguageBundle
				.getString("in_iskErr_message_06"));

			return false;
		}

		final double cost = sc.getCost();
		double rank = points / cost;
		
		Skill aSkill = (Skill) skill;

		boolean hasSkill = theCharacter.hasSkill(aSkill);
		if (!hasSkill)
		{
			theCharacter.addSkill(aSkill);
			updateSkillsOutputOrder(aSkill);
		}
		
		final String classKeyName = theCharacter.getLevelInfoClassKeyName(getLevelIndex(level));
		PCClass aClass = theCharacter.getClassKeyed(classKeyName);
		String errMessage = SkillRankControl.modRanks(rank, aClass, false, theCharacter, aSkill);

		if ("".equals(errMessage)) //$NON-NLS-1$
		{
			classLevel.setSkillPointsRemaining(skillPool - points);
		}

		//
		// Remove the skill from the skill list if we've
		// just set the rank to zero and it is not untrained
		//
		if (CoreUtility.doublesEqual(SkillRankControl.getRank(theCharacter, aSkill).doubleValue(), 0.0)
			&& !aSkill.getSafe(ObjectKey.USE_UNTRAINED))
		{
			theCharacter.removeSkill(aSkill);
		}

		if (errMessage.length() > 0)
		{
			delegate.showInfoMessage(Constants.APPLICATION_NAME, errMessage);

			return false;
		}

		updateSkillsTodo();
		fireSkillPointEvent(this, getLevelIndex(level), false);
		fireSkillBonusEvent(this, getLevelIndex(level), false);
		return true;
	}

	/**
	 * Find a level which has a certain number of points spent.
	 * @param points The negative number of points spent required.
	 * @param skill 
	 * @return The level with spent points, or null if none match
	 */
	private CharacterLevelFacade findLevelWithSpentSkillPoints(int points, SkillFacade skill)
	{
		for (int i = charLevels.size()-1; i>= 0; i--)
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
	private boolean classHasRanksIn(SkillFacade skill, ClassFacade pcClass)
	{
		Collection<NamedValue> skillTakenClassList = theCharacter.getSafeAssocList(skill, AssociationListKey.SKILL_RANK);
		for (NamedValue namedValue : skillTakenClassList)
		{
			if (pcClass.getKeyName().equals(namedValue.name))
			{
				return true;
			}
		}
		return false;
	}

	private void updateSkillsTodo()
	{
		if (theCharacter.getSkillPoints() < 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SkillsTab, "Skills",
				"in_iskTodoTooMany", 1));
			todoManager.removeTodo("in_iskTodoRemain");
		}
		else if (theCharacter.getSkillPoints() > 0)
		{
			todoManager.addTodo(new TodoFacadeImpl(CharacterTab.SkillsTab, "Skills",
				"in_iskTodoRemain", 1));
			todoManager.removeTodo("in_iskTodoTooMany");
		}
		else
		{
			todoManager.removeTodo("in_iskTodoRemain");
			todoManager.removeTodo("in_iskTodoTooMany");
		}
	}

	private void updateSkillsOutputOrder(Skill aSkill)
	{
		// in order to get the selected table to sort properly
		// we need to sort the PC's skill list now that the
		// new skill has been added, this won't get called
		// when adding a rank to an existing skill
//		Collections.sort(theCharacter.getSkillList(),
//			new StringIgnoreCaseComparator());

		// Now re calc the output order
		if (theCharacter.getSkillsOutputOrder() != GuiConstants.INFOSKILLS_OUTPUT_BY_MANUAL)
		{
			resortSelected(theCharacter.getSkillsOutputOrder());
		}
		else
		{
			Integer outputIndex = theCharacter.getAssoc(aSkill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex == null || outputIndex == 0)
			{
				theCharacter.setAssoc(aSkill, AssociationKey.OUTPUT_INDEX, getHighestOutputIndex() + 1);
			}
		}
	}

	private void resortSelected(int sortSelection)
	{
		int sort = -1;
		boolean sortOrder = false;

		switch (sortSelection)
		{
			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_ASC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_NAME_DSC:
				sort = SkillComparator.RESORT_NAME;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_ASC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_ASCENDING;

				break;

			case GuiConstants.INFOSKILLS_OUTPUT_BY_TRAINED_DSC:
				sort = SkillComparator.RESORT_TRAINED;
				sortOrder = SkillComparator.RESORT_DESCENDING;

				break;

			default:

				// Manual sort, or unrecognised, so do no sorting.
				return;
		}

		resortSelected(sort, sortOrder);
	}

	private void resortSelected(int sort, boolean sortOrder)
	{
		if (theCharacter == null)
		{
			return;
		}
		SkillComparator comparator = new SkillComparator(theCharacter, sort, sortOrder);
		int nextOutputIndex = 1;
		List<Skill> skillList = new ArrayList<Skill>(theCharacter.getSkillSet());
		Collections.sort(skillList, comparator);

		for (Skill aSkill : skillList)
		{
			Integer outputIndex = theCharacter.getAssoc(aSkill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex == null || outputIndex >= 0)
			{
				theCharacter.setAssoc(aSkill, AssociationKey.OUTPUT_INDEX, nextOutputIndex++);
			}
		}
	}

	/**
	 * Retrieve the highest output index used in any of the
	 * character's skills.
	 * @return highest output index
	 */
	private int getHighestOutputIndex()
	{
		int maxOutputIndex = 0;
		final List<Skill> skillList = new ArrayList<Skill>(theCharacter.getSkillSet());
		for (Skill bSkill : skillList)
		{
			Integer outputIndex = theCharacter.getAssoc(bSkill, AssociationKey.OUTPUT_INDEX);
			if (outputIndex != null && outputIndex > maxOutputIndex)
			{
				maxOutputIndex = outputIndex;
			}
		}

		return maxOutputIndex;
	}
	

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#setGainedSkillPoints(int, int)
	 */
	@Override
	public void setGainedSkillPoints(CharacterLevelFacade level, int points)
	{
		int spentSkillPoints = getSpentSkillPoints(level);
		PCLevelInfo classLevel = getLevelInfo(level);
		classLevel.setSkillPointsGained(theCharacter, points);
		classLevel.setSkillPointsRemaining(points-spentSkillPoints);
		
		fireSkillPointEvent(this, getLevelIndex(level), false);
	}

	// ============== Level Management code =========================
	
	/**
	 * Register the addition of a new level to the character of the 
	 * specified class. It is expected that the backing PlayerCharacter object 
	 * will be updated by our caller.
	 * @param theClass The class the level is in.
	 */
	void addLevelOfClass(CharacterLevelFacadeImpl theClassLevel)
	{
		ClassFacade theClass = theClassLevel.getSelectedClass();
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
		classLevels.remove(classLevels.size()-1);
		removeElement(getSize()-1);
		updateSkillsTodo();
	}
	
	void classListRefreshRequired()
	{
		refreshClassList();
		fireClassChangedEvent(this, 0, true);
		fireSkillBonusEvent(this, 0, true);
	}
	
	// ============== Listener Management code =========================

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#addClasListener(pcgen.core.facade.CharacterLevelsFacade.ClassListener)
	 */
	@Override
	public void addClassListener(ClassListener listener)
	{
		listenerList.add(ClassListener.class, listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#addHitPointListener(pcgen.core.facade.CharacterLevelsFacade.HitPointListener)
	 */
	@Override
	public void addHitPointListener(HitPointListener listener)
	{
		listenerList.add(HitPointListener.class, listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#addSkillBonusListener(pcgen.core.facade.CharacterLevelsFacade.SkillBonusListener)
	 */
	@Override
	public void addSkillBonusListener(SkillBonusListener listener)
	{
		listenerList.add(SkillBonusListener.class, listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#addSkillPointListener(pcgen.core.facade.CharacterLevelsFacade.SkillPointListener)
	 */
	@Override
	public void addSkillPointListener(SkillPointListener listener)
	{
		listenerList.add(SkillPointListener.class, listener);
	}
	
	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#removeClassListener(pcgen.core.facade.CharacterLevelsFacade.ClassListener)
	 */
	@Override
	public void removeClassListener(ClassListener listener)
	{
		listenerList.remove(ClassListener.class, listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#removeHitPointListener(pcgen.core.facade.CharacterLevelsFacade.HitPointListener)
	 */
	@Override
	public void removeHitPointListener(HitPointListener listener)
	{
		listenerList.remove(HitPointListener.class, listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#removeSkillBonusListener(pcgen.core.facade.CharacterLevelsFacade.SkillBonusListener)
	 */
	@Override
	public void removeSkillBonusListener(SkillBonusListener listener)
	{
		listenerList.remove(SkillBonusListener.class, listener);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.CharacterLevelsFacade#removeSkillPointListener(pcgen.core.facade.CharacterLevelsFacade.SkillPointListener)
	 */
	@Override
	public void removeSkillPointListener(SkillPointListener listener)
	{
		listenerList.remove(SkillPointListener.class, listener);
	}

	protected void fireClassChangedEvent(Object source, int baseLevelIndex, boolean stacks)
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

	protected void fireHitPointEvent(Object source, int baseLevelIndex, boolean stacks)
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

	protected void fireSkillPointEvent(Object source, int baseLevelIndex, boolean stacks)
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

	protected void fireSkillBonusEvent(Object source, int baseLevelIndex, boolean stacks)
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

}
