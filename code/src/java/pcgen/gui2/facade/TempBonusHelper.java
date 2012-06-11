/*
 * TempBonusHelper.java
 * Copyright James Dempsey, 2012
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
 * Created on 09/06/2012 12:41:49 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.BonusManager;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.facade.ChooserFacade.ChooserTreeViewType;
import pcgen.core.facade.InfoFacade;
import pcgen.core.facade.UIDelegate;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserRadio;

/**
 * The Class <code>TempBonusHelper</code> splits out processing for temporary 
 * bonuses from CnaracterFacadeImpl.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class TempBonusHelper
{

	/** An empty string. */
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	/**
	 * Get the target of the temporary bonus. This may present a chooser to the 
	 * user, if a choice of equipment is required. If the bonus is a character 
	 * bonus only, then no chooser will be presented and the return value will 
	 * be the character. If an equipment item is chosen, a new temporary item is 
	 * created from the chosen equipment item, i.e. a copy that has no weight or 
	 * cost.
	 *   
	 * @param originObj The rules object providing the bonus.
	 * @param theCharacter The target character.
	 * @param delegate The user interface delegate which will provide the chooser.
	 * @return The temporary equipment item, the character or null if the request 
	 * was cancelled.
	 */
	static Object getTempBonusTarget(
		CDOMObject originObj, PlayerCharacter theCharacter, UIDelegate delegate)
	{
		List<InfoFacade> possibleTargets =
				getListOfApplicableEquipment(originObj, theCharacter);
		boolean canApplyToPC = canApplyToPC(originObj, theCharacter);
		if (possibleTargets.isEmpty())
		{
			if (canApplyToPC)
			{
				return theCharacter;
			}
			
			delegate.showInfoMessage(Constants.APPLICATION_NAME, 
				LanguageBundle.getString("in_itmNoSuitableEquip")); //$NON-NLS-1$
			return null;
		}
		
		// Get the user's choice of item
		String label = LanguageBundle.getString("im_itmSelectItem"); //$NON-NLS-1$
		if (canApplyToPC)
		{
			possibleTargets.add(new CharacterInfoFacade(theCharacter));
		}
		final ArrayList<InfoFacade> selectedList = new ArrayList<InfoFacade>();
		GeneralChooserFacadeBase chooserFacade =
				new GeneralChooserFacadeBase(label, possibleTargets,
					new ArrayList<InfoFacade>(), 1)
				{
					/**
					 * {@inheritDoc}
					 */
					@Override
					public void commit()
					{
						for (InfoFacade item : getSelectedList())
						{
							selectedList.add(item);
						}
					}
				};
		chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
		delegate.showGeneralChooser(chooserFacade);
		
		if (!selectedList.isEmpty())
		{
			if (selectedList.get(0) instanceof CharacterInfoFacade)
			{
				return theCharacter;
			}
			
			// Create temporary item
			Equipment aEq = ((Equipment) selectedList.get(0)).clone();
			aEq.makeVirtual();
			String currAppName = aEq.getAppliedName();
			if (currAppName != null && currAppName.length() > 2)
			{
				if (theCharacter.hasTempApplied(originObj))
				{
					delegate.showInfoMessage(Constants.APPLICATION_NAME, 
						LanguageBundle.getString("in_itmAppBonButAlreadyApplied")); //$NON-NLS-1$
					return null;
				}
				// We need to remove the [] from the old name
				aEq.setAppliedName(currAppName.substring(2, currAppName
					.length() - 1)
					+ ", " + originObj.getKeyName()); //$NON-NLS-1$
			}
			else
			{
				aEq.setAppliedName(originObj.getKeyName());
			}
			return aEq;
		}
		return null;
	}

	/**
	 * Build a list of what equipment is possible to apply this bonus to.
	 * @param originObj The rules object providing the bonus.
	 * @param theCharacter The target character.
	 * @return The list of possible equipment.
	 */
	private static List<InfoFacade> getListOfApplicableEquipment(CDOMObject originObj,
		PlayerCharacter theCharacter)
	{
		List<InfoFacade> possibleEquipment = new ArrayList<InfoFacade>();
		boolean found = false;
		theCharacter.setCalcEquipmentList(theCharacter.getUseTempMods());
		for (Equipment aEq : theCharacter.getEquipmentSet())
		{
			found = false;

			for (BonusObj aBonus : originObj.getBonusList(aEq))
			{
				if (aBonus == null)
				{
					continue;
				}
				if (aBonus.isTempBonus())
				{
					boolean passesApply = true;
					for (Iterator<Prerequisite> iter =
							aBonus.getPrerequisiteList().iterator(); iter
						.hasNext()
						&& passesApply;)
					{
						Prerequisite element = iter.next();
						if (element.getKind() != null
							&& element.getKind().equalsIgnoreCase(
								Prerequisite.APPLY_KIND))
						{
							if (!PrereqHandler.passes(element, aEq, theCharacter))
							{
								passesApply = false;
							}
						}
					}
					if (passesApply && !found)
					{
						possibleEquipment.add(aEq);
						found = true;
					}
				}
			}
		}
		return possibleEquipment;
	}
	
	/**
	 * Identify if this temporary bonus can be applied to just a character.
	 * @param originObj The rules object providing the bonus.
	 * @param theCharacter The target character.
	 * @return true if an equipment choice is not required, false if equipment is needed.
	 */
	static boolean canApplyToPC(CDOMObject originObj, PlayerCharacter theCharacter)
	{
		boolean hasPCBonus = false;

		for (BonusObj aBonus : originObj.getBonusList(theCharacter))
		{
			if (aBonus == null)
			{
				continue;
			}

			if (aBonus.isTempBonus())
			{
				if ((aBonus
					.isTempBonusTarget(BonusObj.TempBonusTarget.ANYPC) || aBonus
					.isTempBonusTarget(BonusObj.TempBonusTarget.PC))
					&& !hasPCBonus)
				{
					hasPCBonus = true;
				}
			}
		}
		return hasPCBonus;
	}
	
	/**
	 * Apply the temporary bonus to the character. The bonus may be applied 
	 * directly to the character, or if a piece of equipment is nominated to
	 * that piece of equipment. Generally equipment will be a 'temporary'
	 * equipment item created to show the item with the bonus. 
	 *  
	 * @param tempBonus The bonus to be applied.
	 * @param aEq The temporary equipment item to apply the bonus to. 
	 * @param originObj The rules object granting the bonus.
	 * @param theCharacter The character the vonus is being applied to.
	 */
	static TempBonusFacadeImpl applyBonusToCharacter(TempBonusFacadeImpl tempBonus,
		Equipment aEq, CDOMObject originObj, PlayerCharacter theCharacter) 
	{
		TempBonusFacadeImpl appliedBonus = null;
		String repeatValue = EMPTY_STRING;
		for (BonusObj aBonus : tempBonus.getOriginObj().getBonusList(theCharacter))
		{
			if (aBonus.isTempBonus())
			{
				String oldValue = aBonus.toString();
				String newValue = oldValue;
				if (originObj.getSafe(StringKey.CHOICE_STRING)
					.length() > 0)
				{
					BonusInfo bi =
							TempBonusHelper.getBonusChoice(oldValue,
								originObj, repeatValue,
								theCharacter);
					if (bi == null)
					{
						return null;
					}
					newValue = bi.getBonusValue();
					repeatValue = bi.getRepeatValue();
				}
				BonusObj newB = Bonus.newBonus(Globals.getContext(), newValue);
				if (newB != null)
				{
					// We clear the prereqs and add the non-PREAPPLY prereqs from the old bonus
					// Why are we doing this? (for qualifies)
					newB.clearPrerequisiteList();
					for (Prerequisite prereq : aBonus.getPrerequisiteList())
					{
						if (prereq.getKind() == null
							|| !prereq.getKind().equalsIgnoreCase(
								Prerequisite.APPLY_KIND))
						{
							try
							{
								newB.addPrerequisite(prereq.clone());
							}
							catch (CloneNotSupportedException e)
							{
								throw new UnreachableError(
										"Prerequisites should be cloneable by PCGen design"); //$NON-NLS-1$
							}
						}
					}

					// if Target was this PC, then add
					// bonus to TempBonusMap
					TempBonusInfo tempBonusInfo;
					Object target;
					if (aEq == null)
					{
						theCharacter.setApplied(newB, newB.qualifies(theCharacter, aEq ));
						tempBonusInfo = theCharacter.addTempBonus(newB, originObj, theCharacter);
						target = theCharacter;
					}
					else
					{
						theCharacter.setApplied(newB, PrereqHandler.passesAll(
							newB.getPrerequisiteList(), aEq, theCharacter));
						aEq.addTempBonus(newB);
						tempBonusInfo = theCharacter.addTempBonus(newB, originObj, aEq);
						target = aEq;
					}

					if (appliedBonus == null)
					{
						String bonusName = new BonusManager(theCharacter).getBonusName(newB, tempBonusInfo);
						appliedBonus = new TempBonusFacadeImpl(tempBonus.getOriginObj(), target, bonusName);
					}
				}
			}
		}
		

		// if the Target is an Equipment item
		// then add it to the tempBonusItemList
		if (aEq != null)
		{
			theCharacter.addTempBonusItemList(aEq);
		}
		
		return appliedBonus;
	}
	
	static void removeBonusFromCharacter(PlayerCharacter pc, Equipment aEq, CDOMObject aCreator)
	{

		for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> me : pc
				.getTempBonusMap().entrySet())
		{
			BonusObj aBonus = me.getKey();
			TempBonusInfo tbi = me.getValue();
			Object aC = tbi.source;
			if (aCreator != aC)
			{
				continue;
			}
			Object aT = tbi.target;

			if ((aT instanceof Equipment) && (aEq != null))
			{
				if (aEq.equals(aT))
				{
					pc.removeTempBonus(aBonus);
					pc.removeTempBonusItemList((Equipment) aT);
					((Equipment) aT).removeTempBonus(aBonus);
					((Equipment) aT).setAppliedName(EMPTY_STRING);
				}
			}
			else if ((aT instanceof PlayerCharacter) && (aEq == null))
			{
				pc.removeTempBonus(aBonus);
			}
		}
	}
	
	/**
	 * Allows user to choose the value of a bonus.
	 * 
	 * @param oldValue The PCC text of the bonus.
	 * @param source The object providing the bonus.
	 * @param repeatValue The value of a previous bonus to be used for choices in this bonus.
	 * @param pc The character the bonus  is being applied to.
	 * @return The new values for the bonus.
	 */
	private static BonusInfo getBonusChoice(String oldValue, final CDOMObject source,
			String repeatValue, PlayerCharacter pc)
	{
		String value = oldValue;

		// If repeatValue is set, this is a multi BONUS and they all
		// should get the same value as the first choice
		if (repeatValue.length() > 0)
		{
			// need to parse the aChoice string
			// and replace %CHOICE with choice
			if (value.indexOf("%CHOICE") >= 0) //$NON-NLS-1$
			{
				value = value.replaceAll(
						Pattern.quote("%CHOICE"), //$NON-NLS-1$ 
						repeatValue);
			}

			return new BonusInfo(value, repeatValue);
		}

		String aChoice = source.getSafe(StringKey.CHOICE_STRING);
		StringTokenizer aTok = new StringTokenizer(aChoice, "|"); //$NON-NLS-1$

		String testNumber = aChoice;
		
		Formula numchoices = source.get(FormulaKey.NUMCHOICES);
		if (numchoices != null)
		{
			Logging.errorPrint("NUMCHOICES is not implemented " //$NON-NLS-1$
					+ "for CHOOSE in Temporary Mods"); //$NON-NLS-1$
			Logging.errorPrint("  CHOOSE was: " + aChoice //$NON-NLS-1$
					+ ", NUMCHOICES was: " + numchoices); //$NON-NLS-1$
		}
		if (testNumber.startsWith("NUMBER") && (aTok.countTokens() >= 3)) //$NON-NLS-1$
		{
			int min;
			int max;
			aTok.nextToken(); // throw away "NUMBER"

			String minString = aTok.nextToken();
			String maxString = aTok.nextToken();
			String titleString = LanguageBundle.getString("in_itmPickNumber"); //$NON-NLS-1$

			if (aTok.hasMoreTokens())
			{
				titleString = aTok.nextToken();

				if (titleString.startsWith("TITLE=")) //$NON-NLS-1$
				{
					// remove TITLE=
					titleString = titleString.substring(6);
				}
			}

			if (minString.startsWith("MIN=")) //$NON-NLS-1$
			{
				minString = minString.substring(4);
				min = pc.getVariableValue(minString, EMPTY_STRING).intValue();
			}
			else
			{
				min = pc.getVariableValue(minString, EMPTY_STRING).intValue();
			}

			if (maxString.startsWith("MAX=")) //$NON-NLS-1$
			{
				maxString = maxString.substring(4);
				max = pc.getVariableValue(maxString, EMPTY_STRING).intValue();
			}
			else
			{
				max = pc.getVariableValue(maxString, EMPTY_STRING).intValue();
			}

			if ((max > 0) || (min <= max))
			{
				List<String> numberList = new ArrayList<String>();

				for (int i = min; i <= max; i++)
				{
					numberList.add(Integer.toString(i));
				}

				// let them choose the number from a radio list
				ChooserRadio c = ChooserFactory.getRadioInstance();
				c.setAvailableList(numberList);
				c.setVisible(false);
				c.setTitle(LanguageBundle.getString("in_itmPickNumber")); //$NON-NLS-1$
				c.setMessageText(titleString);
				c.setVisible(true);

				ArrayList<String> selectedList = c.getSelectedList();
				if (selectedList.size() > 0)
				{
					final String aI = selectedList.get(0);

					// need to parse the bonus.getValue()
					// string and replace %CHOICE
					if (oldValue.indexOf("%CHOICE") >= 0) //$NON-NLS-1$
					{
						value =
							oldValue.replaceAll(Pattern.quote("%CHOICE"),  //$NON-NLS-1$
								                  aI);
					}

					return new BonusInfo(value, aI);
				}
				// they hit the cancel button
				return null;
			}
		}

		return null;
	}
	
	static class BonusInfo
	{

		private final String bonusValue;
		private final String repeatValue;
		
		public BonusInfo(String value, String repeat)
		{
			bonusValue = value;
			repeatValue = repeat;
		}

		public String getBonusValue()
		{
			return bonusValue;
		}

		public String getRepeatValue()
		{
			return repeatValue;
		}
		
	}

	/**
	 * The Class <code>CharacterInfoFacade</code> presents a character as an InfoFacade.
	 */
	static class CharacterInfoFacade implements InfoFacade
	{
		private final PlayerCharacter theCharacter;

		public CharacterInfoFacade(PlayerCharacter theCharacter)
		{
			this.theCharacter = theCharacter;
			
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSource()
		{
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getSourceForNodeDisplay()
		{
			return EMPTY_STRING;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getKeyName()
		{
			return "PC"; //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isNamePI()
		{
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString()
		{
			return LanguageBundle.getFormattedString("in_itmCharacterName", //$NON-NLS-1$
				theCharacter.getName());
		}
	}
}
