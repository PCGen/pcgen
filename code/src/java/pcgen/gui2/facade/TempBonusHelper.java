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
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import pcgen.base.formula.Formula;
import pcgen.base.lang.UnreachableError;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.BonusManager;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
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
	static void applyBonusToCharacter(TempBonusFacadeImpl tempBonus,
		Equipment aEq, CDOMObject originObj, PlayerCharacter theCharacter) 
	{
		String repeatValue = "";
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
					if (bi != null)
					{
						newValue = bi.getBonusValue();
						repeatValue = bi.getRepeatValue();
					}
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
										"Prerequisites should be cloneable by PCGen design");
							}
						}
					}

					// if Target was this PC, then add
					// bonus to TempBonusMap
					if (aEq == null)
					{
						theCharacter.setApplied(newB, newB.qualifies(theCharacter, aEq ));
						theCharacter.addTempBonus(newB, originObj, theCharacter);
					}
					else
					{
						theCharacter.setApplied(newB, PrereqHandler.passesAll(newB.getPrerequisiteList(), aEq, theCharacter));
						aEq.addTempBonus(newB);
						theCharacter.addTempBonus(newB, originObj, aEq);
					}
				}
			}
		}
	}
	
	static void removeBonusFromCharacter(PlayerCharacter pc, Equipment aEq, CDOMObject aCreator)
	{

		for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> me : pc
				.getTempBonusMap().entrySet())
		{
			BonusObj aBonus = me.getKey();
			TempBonusInfo tbi = me.getValue();
			Object aC = tbi.source;
			Object aT = tbi.target;

			if ((aT instanceof Equipment) && (aEq != null))
			{
				if (aEq.equals(aT) && (aCreator == aC))
				{
					pc.removeTempBonus(aBonus);
					pc.removeTempBonusItemList((Equipment) aT);
					((Equipment) aT).removeTempBonus(aBonus);
					((Equipment) aT).setAppliedName("");
				}
			}
			else if ((aT instanceof PlayerCharacter) && (aEq == null))
			{
				if (aCreator == aC)
				{
					pc.removeTempBonus(aBonus);
				}
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
		StringTokenizer aTok = new StringTokenizer(aChoice, "|");

		String testNumber = aChoice;
		
		Formula numchoices = source.get(FormulaKey.NUMCHOICES);
		if (numchoices != null)
		{
			Logging.errorPrint("NUMCHOICES is not implemented "
					+ "for CHOOSE in Temporary Mods");
			Logging.errorPrint("  CHOOSE was: " + aChoice
					+ ", NUMCHOICES was: " + numchoices);
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
				min = pc.getVariableValue(minString, "").intValue();
			}
			else
			{
				min = pc.getVariableValue(minString, "").intValue();
			}

			if (maxString.startsWith("MAX=")) //$NON-NLS-1$
			{
				maxString = maxString.substring(4);
				max = pc.getVariableValue(maxString, "").intValue();
			}
			else
			{
				max = pc.getVariableValue(maxString, "").intValue();
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

}
