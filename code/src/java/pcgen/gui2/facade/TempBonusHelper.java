/*
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
 */
package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.BonusManager;
import pcgen.core.BonusManager.TempBonusInfo;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.bonus.EquipBonus;
import pcgen.core.display.BonusDisplay;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.prereq.PrereqHandler;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.InfoFactory;
import pcgen.facade.core.UIDelegate;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The Class {@code TempBonusHelper} splits out processing for temporary
 * bonuses from CnaracterFacadeImpl.
 *
 * 
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
	 * @param infoFactory An object to provide formatted information about an object. 
	 * @return The temporary equipment item, the character or null if the request 
	 * was cancelled.
	 */
	static Object getTempBonusTarget(CDOMObject originObj, PlayerCharacter theCharacter, UIDelegate delegate,
		InfoFactory infoFactory)
	{
		List<InfoFacade> possibleTargets = getListOfApplicableEquipment(originObj, theCharacter);
		boolean canApplyToPC = hasCharacterTempBonus(originObj);
		if (possibleTargets.isEmpty())
		{
			if (canApplyToPC)
			{
				return theCharacter;
			}

			delegate.showInfoMessage(
				Constants.APPLICATION_NAME, LanguageBundle.getString("in_itmNoSuitableEquip")); //$NON-NLS-1$
			return null;
		}

		// Get the user's choice of item
		String label = LanguageBundle.getString("im_itmSelectItem"); //$NON-NLS-1$
		if (canApplyToPC)
		{
			possibleTargets.add(new CharacterInfoFacade(theCharacter.getDisplay()));
		}
		final ArrayList<InfoFacade> selectedList = new ArrayList<>();
		GeneralChooserFacadeBase chooserFacade =
				new GeneralChooserFacadeBase(label, possibleTargets, new ArrayList<>(), 1, infoFactory)
				{
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
				aEq.setAppliedName(currAppName.substring(2, currAppName.length() - 1)
					+ ", " + originObj.getKeyName());
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
	public static List<InfoFacade> getListOfApplicableEquipment(CDOMObject originObj, PlayerCharacter theCharacter)
	{
		CharacterDisplay charDisplay = theCharacter.getDisplay();
		List<InfoFacade> possibleEquipment = new ArrayList<>();
		if (originObj == null)
		{
			return possibleEquipment;
		}
		boolean found = false;
		theCharacter.setCalcEquipmentList(theCharacter.getUseTempMods());
		for (Equipment aEq : charDisplay.getEquipmentSet())
		{
			found = false;

			for (EquipBonus eb : originObj.getSafeListFor(ListKey.BONUS_EQUIP))
			{
				String conditions = eb.conditions;
				boolean passesConditions = passesConditions(aEq, conditions);
				if (passesConditions && !found)
				{
					possibleEquipment.add(aEq);
					break;
				}
			}
		}
		return possibleEquipment;
	}

	private static boolean passesConditions(Equipment aEq, String conditions)
	{
		for (String andToken : conditions.split(","))
		{
			boolean passOr = false;
			for (String orToken : andToken.split("\\;"))
			{
				if (orToken.startsWith("["))
				{
					if (!aEq.isType(orToken.substring(1, orToken.length() - 1)))
					{
						passOr = true;
						break;
					}
				}
				else
				{
					if (aEq.isType(orToken))
					{
						passOr = true;
						break;
					}
				}
			}
			if (!passOr)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Apply the temporary bonus to the character's equipment. Generally
	 * equipment will be a 'temporary' equipment item created to show the item
	 * with the bonus.
	 * 
	 * @param aEq
	 *            The temporary equipment item to apply the bonus to.
	 * @param originObj
	 *            The rules object granting the bonus.
	 * @param theCharacter
	 *            The character the bonus is being applied to.
	 */
	static TempBonusFacadeImpl applyBonusToCharacterEquipment(Equipment aEq, CDOMObject originObj,
		PlayerCharacter theCharacter)
	{
		TempBonusFacadeImpl appliedBonus = null;
		String repeatValue = EMPTY_STRING;
		for (EquipBonus eb : originObj.getListFor(ListKey.BONUS_EQUIP))
		{
			BonusObj aBonus = eb.bonus;
			String oldValue = aBonus.toString();
			String newValue = oldValue;
			if (!originObj.getSafe(StringKey.TEMPVALUE).isEmpty())
			{
				BonusInfo bi = TempBonusHelper.getBonusChoice(oldValue, originObj, repeatValue, theCharacter);
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
				// if Target was this PC, then add
				// bonus to TempBonusMap
				theCharacter.setApplied(newB, PrereqHandler.passesAll(newB, aEq, theCharacter));
				aEq.addTempBonus(newB);
				TempBonusInfo tempBonusInfo = theCharacter.addTempBonus(newB, originObj, aEq);
				if (appliedBonus == null)
				{
					String bonusName = BonusDisplay.getBonusDisplayName(tempBonusInfo);
					appliedBonus = new TempBonusFacadeImpl(originObj, aEq, bonusName);
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

	/**
	 * Apply the temporary bonus to the character. The bonus is applied 
	 * directly to the character.  
	 *  
	 * @param originObj The rules object granting the bonus.
	 * @param theCharacter The character the bonus is being applied to.
	 */
	static TempBonusFacadeImpl applyBonusToCharacter(CDOMObject originObj, PlayerCharacter theCharacter)
	{
		TempBonusFacadeImpl appliedBonus = null;
		String repeatValue = EMPTY_STRING;
		for (BonusObj aBonus : getTempCharBonusesFor(originObj))
		{
			String oldValue = aBonus.toString();
			String newValue = oldValue;
			if (!originObj.getSafe(StringKey.TEMPVALUE).isEmpty())
			{
				BonusInfo bi = TempBonusHelper.getBonusChoice(oldValue, originObj, repeatValue, theCharacter);
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
				// if Target was this PC, then add
				// bonus to TempBonusMap
				theCharacter.setApplied(newB, newB.qualifies(theCharacter, null));
				TempBonusInfo tempBonusInfo = theCharacter.addTempBonus(newB, originObj, theCharacter);
				if (appliedBonus == null)
				{
					String bonusName = BonusDisplay.getBonusDisplayName(tempBonusInfo);
					appliedBonus = new TempBonusFacadeImpl(originObj, theCharacter, bonusName);
				}
			}
		}

		return appliedBonus;
	}

	private static List<BonusObj> getTempCharBonusesFor(CDOMObject originObj)
	{
		List<BonusObj> list = new ArrayList<>(5);
		list.addAll(originObj.getSafeListFor(ListKey.BONUS_ANYPC));
		list.addAll(originObj.getSafeListFor(ListKey.BONUS_PC));
		return list;
	}

	static void removeBonusFromCharacter(PlayerCharacter pc, Equipment aEq, CDOMObject aCreator)
	{

		for (Map.Entry<BonusObj, BonusManager.TempBonusInfo> me : pc.getTempBonusMap().entrySet())
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
	private static BonusInfo getBonusChoice(String oldValue, final CDOMObject source, String repeatValue,
		PlayerCharacter pc)
	{
		String value = oldValue;

		// If repeatValue is set, this is a multi BONUS and they all
		// should get the same value as the first choice
		if (!repeatValue.isEmpty())
		{
			// need to parse the aChoice string
			// and replace %CHOICE with choice
			if (value.contains("%CHOICE")) //$NON-NLS-1$
			{
				value = value.replaceAll(Pattern.quote("%CHOICE"), //$NON-NLS-1$ 
					repeatValue);
			}

			return new BonusInfo(value, repeatValue);
		}

		String aChoice = source.getSafe(StringKey.TEMPVALUE);
		StringTokenizer aTok = new StringTokenizer(aChoice, "|"); //$NON-NLS-1$

		String minString = aTok.nextToken().substring(4); //Take off MIN=
		String maxString = aTok.nextToken().substring(4); //Take off MAX=
		String titleString = aTok.nextToken().substring(6); // Take off TITLE=

		int min = pc.getVariableValue(minString, EMPTY_STRING).intValue();
		int max = pc.getVariableValue(maxString, EMPTY_STRING).intValue();

		if (max < min)
		{
			Logging.errorPrint("Temp Bonus Value had max < min: " + max + "<" + min);
			return null;
		}
		if (max <= 0)
		{
			Logging.errorPrint("Temp Bonus Value had max <= 0: " + max);
			return null;
		}

		List<Integer> numberList = new ArrayList<>();

		for (int i = min; i <= max; i++)
		{
			numberList.add(i);
		}

		// let them choose the number from a radio list
		List<Integer> selectedList = new ArrayList<>();
		selectedList = Globals.getChoiceFromList(titleString, numberList, selectedList, 1, false, true, pc);
		if (!selectedList.isEmpty())
		{
			final String aI = String.valueOf(selectedList.get(0));

			// need to parse the bonus.getValue()
			// string and replace %CHOICE
			if (oldValue.contains("%CHOICE")) //$NON-NLS-1$
			{
				value = oldValue.replaceAll(Pattern.quote("%CHOICE"), //$NON-NLS-1$
					aI);
			}

			return new BonusInfo(value, aI);
		}
		// they hit the cancel button
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
	 * The Class {@code CharacterInfoFacade} presents a character as an InfoFacade.
	 */
	static class CharacterInfoFacade implements InfoFacade
	{
		private final CharacterDisplay charDisplay;

		public CharacterInfoFacade(CharacterDisplay charDisplay)
		{
			this.charDisplay = charDisplay;

		}

		@Override
		public String getSource()
		{
			return EMPTY_STRING;
		}

		@Override
		public String getSourceForNodeDisplay()
		{
			return EMPTY_STRING;
		}

		@Override
		public String getKeyName()
		{
			return "PC"; //$NON-NLS-1$
		}

		@Override
		public boolean isNamePI()
		{
			return false;
		}

		@Override
		public String toString()
		{
			return LanguageBundle.getFormattedString("in_itmCharacterName", //$NON-NLS-1$
				charDisplay.getName());
		}

		@Override
		public String getType()
		{
			return "";
		}
	}

	////////////////////////////////////////////////
	//        Public Accessors and Mutators       //
	////////////////////////////////////////////////

	public static boolean hasAnyPCTempBonus(CDOMObject obj)
	{
		return obj.containsListFor(ListKey.BONUS_ANYPC);
	}

	public static boolean hasPCTempBonus(CDOMObject obj)
	{
		return obj.containsListFor(ListKey.BONUS_PC);
	}

	public static boolean hasNonPCTempBonus(CDOMObject obj)
	{
		return hasEquipmentTempBonus(obj) || hasAnyPCTempBonus(obj);
	}

	public static boolean hasCharacterTempBonus(CDOMObject obj)
	{
		return hasAnyPCTempBonus(obj) || hasPCTempBonus(obj);
	}

	public static boolean hasEquipmentTempBonus(CDOMObject obj)
	{
		return obj.containsListFor(ListKey.BONUS_EQUIP);
	}

	public static Set<String> getEquipmentApplyString(CDOMObject obj)
	{
		Set<String> set = new HashSet<>();
		//Should use hasEquipmentTempBonus first, so we do NOT do getSafeListFor
		for (EquipBonus bonus : obj.getListFor(ListKey.BONUS_EQUIP))
		{
			set.add(bonus.conditions);
		}
		return set;
	}

	static boolean hasTempBonus(CDOMObject obj)
	{
		return hasEquipmentTempBonus(obj) || hasAnyPCTempBonus(obj) || hasPCTempBonus(obj);
	}

}
