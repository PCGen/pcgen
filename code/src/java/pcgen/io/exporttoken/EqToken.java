/*
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package pcgen.io.exporttoken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.EquipmentUtilities;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.io.ExportHandler;
import pcgen.io.FileAccess;
import pcgen.util.BigDecimalHelper;

/**
 * Deals with EQ Token
 */
public class EqToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "EQ";
	private static String cachedString = null;
	private static List<Equipment> cachedList = null;
	private static int cachedSerial = 0;
	private static PlayerCharacter cachedPC = null;

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		// Starting EQ.%.NAME.MAGIC,befTrue,aftTrue,befFalse,aftFalse reading
		StringBuilder bFilter = new StringBuilder();
		String befTrue = "";
		String aftTrue = "";
		String befFalse = "";
		String aftFalse = "";
		StringTokenizer bTok = new StringTokenizer(tokenSource, "~");

		/* 
		 * If there are at least 3 tokens it means that we have a case where 
		 * we are using the ~<b>~</b> style syntax in order to display something 
		 * with HTML mark-up
		 */
		if (bTok.countTokens() >= 3)
		{
			bFilter = new StringBuilder(bTok.nextToken());
			befTrue = bTok.nextToken();
			aftTrue = bTok.nextToken();

			if (bTok.hasMoreTokens())
			{
				befFalse = bTok.nextToken();
				aftFalse = bTok.nextToken();
			}

			tokenSource = tokenSource.substring(0, bFilter.toString().lastIndexOf('.'));
		}

		bTok = new StringTokenizer(bFilter.toString(), ".");

		boolean if_detected = false;

		while (bTok.hasMoreTokens())
		{
			String bString = bTok.nextToken();

			if ("IF".equals(bString))
			{
				if_detected = true;
			}
			else
			{
				if (if_detected)
				{
					bFilter.append(".").append(bString);
				}
				else
				{
					bFilter = new StringBuilder(bString);
				}
			}
		}

		//
		// check to see if this was the same as the last list we were asked to export.
		//
		String comparatorString = tokenSource.split("[0-9]+")[0];
		List<Equipment> eqList = null;
		StringTokenizer aTok = null;
		int temp = -1;
		if (comparatorString.equals(cachedString) && pc == cachedPC && pc.getSerial() == cachedSerial)
		{
			//			cacheHit++;
			//			if (cacheHit%100==0) {
			//				System.out.println("cacheHit"+cacheHit + ", cacheMiss="+cacheMiss);
			//			}

			eqList = cachedList;
			tokenSource = tokenSource.substring(comparatorString.length());
			aTok = new StringTokenizer(tokenSource, ".", false);
			String token = aTok.nextToken();
			while (aTok.hasMoreTokens())
			{
				try
				{
					temp = Integer.parseInt(token);
				}
				catch (NumberFormatException exc)
				{
					// not an error!
				}

				if (temp >= 0)
				{
					break;
				}
				token = aTok.nextToken();
			}
		}
		else
		{
			// this is different to the last list we were asked to generate, so
			// get all of the required list entries
			aTok = new StringTokenizer(tokenSource, ".", false);
			aTok.nextToken();

			//Merge
			String token = aTok.nextToken();
			int merge = Constants.MERGE_ALL;
			if (token.contains("MERGE"))
			{
				merge = returnMergeType(token);
				token = aTok.nextToken();
			}

			// Get the list of equipment
			eqList = new ArrayList<>(pc.getEquipmentListInOutputOrder(merge));

			//Begin Not code...
			while (aTok.hasMoreTokens())
			{
				if ("NOT".equalsIgnoreCase(token))
				{
					eqList = listNotType(eqList, aTok.nextToken());
				}
				else if ("ADD".equalsIgnoreCase(token))
				{
					eqList = listAddType(pc, eqList, aTok.nextToken());
				}
				else if ("IS".equalsIgnoreCase(token))
				{
					eqList = listIsType(eqList, aTok.nextToken());
				}
				else
				{
					// In the end of the above, bString would
					// be valid token, that should go into temp.
					try
					{
						temp = Integer.parseInt(token);
					}
					catch (NumberFormatException exc)
					{
						// not an error!
					}
				}

				if (temp >= 0)
				{
					break;
				}
				token = aTok.nextToken();

			}

			cachedList = eqList;
			cachedString = comparatorString;
			cachedPC = pc;
			cachedSerial = pc.getSerial();
		}

		// Now that we have the list, get the token for the appropriate element

		String retString = "";
		if (aTok.hasMoreTokens())
		{
			String tempString = aTok.nextToken();

			if ((temp >= 0) && (temp < eqList.size()))
			{
				Equipment eq = eqList.get(temp);
				retString = FileAccess.filterString(getEqToken(pc, eq, tempString, aTok));

				// Starting EQ.%.NAME.MAGIC,befTrue,aftTrue,befFalse,aftFalse treatment
				if (bFilter != null && (bFilter.length() > 0))
				{
					aTok = new StringTokenizer(bFilter.toString(), ".");

					boolean result = false;
					boolean and_operation = false;

					while (aTok.hasMoreTokens())
					{
						String bString = aTok.nextToken();

						if ("AND".equals(bString))
						{
							and_operation = true;
						}
						else if ("OR".equals(bString))
						{
							and_operation = false;
						}
						else
						{
							if (and_operation)
							{
								result = (result && eq.isType(bString));
							}
							else
							{
								result = (result || eq.isType(bString));
							}
						}
					}

					if (result)
					{
						retString = befTrue + retString + aftTrue;
					}
					else
					{
						retString = befFalse + retString + aftFalse;
					}
				}
			}
		}
		return retString;
	}

	/**
	 * EqToken manages its own encoding to allow the data to be encoded but 
	 * export sheet markup to passed through as is. Tell ExportHandler that it
	 * should not re-encode the output from this token.  
	 */
	@Override
	public boolean isEncoded()
	{
		return false;
	}

	public static Integer getRange(final PlayerCharacter pc, Equipment eq)
	{
		String rangeVar = pc.getControl(CControl.EQRANGE);
		if (rangeVar != null)
		{
			//Need a special breakout based on PlayerCharacter reset of IntegerKey.RANGE
			if (eq.isType("Both") && eq.isType("Melee"))
			{
				return 0;
			}
			return ((Number) eq.getLocalVariable(pc.getCharID(), rangeVar)).intValue();
		}

		int range = eq.getSafe(IntegerKey.RANGE);

		if (range == 0)
		{
			final String aRange = eq.getWeaponInfo("RANGE", true);

			if (!aRange.isEmpty())
			{
				range = Integer.valueOf(aRange);
			}
		}

		int r = range + (int) eq.bonusTo(pc, "EQMWEAPON", "RANGEADD", true);
		final int i = (int) eq.bonusTo(pc, "EQMWEAPON", "RANGEMULT", true);
		double rangeMult = 1.0;

		if (i > 0)
		{
			rangeMult += (i - 1);
		}

		int postAdd = 0;

		if (pc != null)
		{
			if (eq.isThrown())
			{
				r += (int) pc.getTotalBonusTo("RANGEADD", "THROWN");
				postAdd = (int) pc.getTotalBonusTo("POSTRANGEADD", "THROWN");
				rangeMult += ((int) pc.getTotalBonusTo("RANGEMULT", "THROWN") / 100.0);
			}
			else if (eq.isProjectile())
			{
				r += (int) pc.getTotalBonusTo("RANGEADD", "PROJECTILE");
				postAdd = (int) pc.getTotalBonusTo("POSTRANGEADD", "PROJECTILE");
				rangeMult += ((int) pc.getTotalBonusTo("RANGEMULT", "PROJECTILE") / 100.0);
			}
		}

		r *= rangeMult;
		r += postAdd;

		return r;
	}

	/**
	 * Converts the critical multiplier into a dispalyable string, i.e.
	 * blank for zero, - for negative and puts an x before positive
	 * numbers e.g. x3
	 *
	 * @param mult The critical multiplier
	 * @return     The string to display
	 */
	public static String multAsString(final int mult)
	{
		if (mult == 0)
		{
			return "";
		}
		else if (mult < 0)
		{
			return "-";
		}

		return "x" + Integer.toString(mult);
	}

	/**
	 * Get the AC Check Token
	 * @param pc
	 * @param eq
	 * @return AC Check Token
	 */
	public static String getAcCheckToken(PlayerCharacter pc, Equipment eq)
	{
		return String.valueOf(getAcCheckTokenInt(pc, eq));
	}

	/**
	 * Get the AC Check Token as an int
	 * @param pc
	 * @param eq
	 * @return AC Check Token as an int
	 */
	public static int getAcCheckTokenInt(PlayerCharacter pc, Equipment eq)
	{
		String acCheckVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.EQACCHECK);
		if (acCheckVar == null)
		{
			return eq.preFormulaAcCheck(pc);
		}
		return ((Number) eq.getLocalVariable(pc.getCharID(), acCheckVar)).intValue();
	}

	/**
	 * Get the AC Mod Token
	 * @param pc
	 * @param eq
	 * @return AC Mod Token
	 */
	public static String getAcModToken(PlayerCharacter pc, Equipment eq)
	{
		return String.valueOf(getAcModTokenInt(pc, eq));
	}

	/**
	 * Get the AC Mod Token as an int
	 * @param pc
	 * @param eq
	 * @return AC Mod Token as an int
	 */
	public static int getAcModTokenInt(PlayerCharacter pc, Equipment eq)
	{
		return eq.getACMod(pc);
	}

	/**
	 * Get Alternative Critical Token
	 * @param eq
	 * @return Alternative Critical Token
	 */
	public static String getAltCritMultToken(Equipment eq)
	{
		return EqToken.multAsString(eq.getAltCritMultiplier());
	}

	/**
	 * Get Alternative Critical Range Token
	 * @param pc
	 * @param eq
	 * @return Alternative Critical Range Token
	 */
	public static String getAltCritRangeToken(PlayerCharacter pc, Equipment eq)
	{
		String critRangeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.CRITRANGE);
		if (critRangeVar == null)
		{
			int critRange = getOldBonusedCritRange(pc, eq, false);
			return critRange == 0 ? "" : Integer.toString(critRange);
		}
		else
		{
			EquipmentHead head = eq.getEquipmentHead(2);
			return WeaponToken.getCritRangeHead(pc, head, critRangeVar).toString();
		}
	}

	/**
	 * Get alternative damage token
	 * @param pc
	 * @param eq
	 * @return alternative damage token
	 */
	public static String getAltDamageToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getAltDamage(pc);
	}

	/**
	 * Get Attacks token
	 * @param pc
	 * @param eq
	 * @return Attacks token
	 */
	public static String getAttacksToken(PlayerCharacter pc, Equipment eq)
	{
		return String.valueOf(getAttacksTokenDouble(pc, eq));
	}

	/**
	 * Get Attacks token as a double
	 * @param pc
	 * @param eq
	 * @return Attacks token as a double
	 */
	public static double getAttacksTokenDouble(PlayerCharacter pc, Equipment eq)
	{
		return eq.bonusTo(pc, "COMBAT", "ATTACKS", true);
	}

	/**
	 * Get Carried token
	 * @param eq
	 * @return Carried token
	 */
	public static String getCarriedToken(Equipment eq)
	{
		return String.valueOf(getCarriedTokenFloat(eq));
	}

	/**
	 * Get Carried token as a float
	 * @param eq
	 * @return Carried token as a float
	 */
	public static float getCarriedTokenFloat(Equipment eq)
	{
		return eq.numberCarried();
	}

	/**
	 * Get Charges Token
	 * @param eq
	 * @return Charges Token
	 */
	public static String getChargesToken(Equipment eq)
	{
		String retString = "";
		int charges = getChargesTokenInt(eq);
		if (charges >= 0)
		{
			retString = String.valueOf(charges);
		}
		return retString;
	}

	/**
	 * Get Charges Token as int
	 * @param eq
	 * @return Charges Token as int
	 */
	public static int getChargesTokenInt(Equipment eq)
	{
		return eq.getRemainingCharges();
	}

	/**
	 * Get Charges Used Token
	 * @param eq
	 * @return Charges Used Token
	 */
	public static String getChargesUsedToken(Equipment eq)
	{
		String retString = "";
		int charges = getChargesUsedTokenInt(eq);
		if (charges >= 0)
		{
			retString = String.valueOf(charges);
		}
		return retString;
	}

	/**
	 * Get Charges Used Token as int
	 * @param eq
	 * @return Charges Used Token as int
	 */
	public static int getChargesUsedTokenInt(Equipment eq)
	{
		return eq.getUsedCharges();
	}

	/**
	 * Get Content Weight Token as double
	 * @param pc
	 * @param eq
	 * @return Content Weight Token as double
	 */
	public static double getContentWeightTokenDouble(PlayerCharacter pc, Equipment eq)
	{
		if (eq.getChildCount() == 0)
		{
			return 0.0;
		}
		return eq.getContainedWeight(pc, true).doubleValue();
	}

	/**
	 * Get Contents Token
	 * @param pc
	 * @param eq
	 * @param tokenizer
	 * @return Contents Token
	 */
	public static String getContentsToken(PlayerCharacter pc, Equipment eq, StringTokenizer tokenizer)
	{
		if (tokenizer.hasMoreTokens())
		{
			String bType = tokenizer.nextToken();
			String aSubTag = "NAME";

			if (tokenizer.hasMoreTokens())
			{
				aSubTag = tokenizer.nextToken();
			}

			try
			{
				int contentsIndex = Integer.parseInt(bType);
				return getEqToken(pc, eq.getContainedByIndex(contentsIndex), aSubTag, tokenizer);
			}
			catch (NumberFormatException e)
			{
				return eq.getContainerByType(bType, aSubTag);
			}
		}
		return getContentsToken(eq);
	}

	/**
	 * Get Contents Token as String
	 * @param eq
	 * @return Contents Token as String
	 */
	public static String getContentsToken(Equipment eq)
	{
		return eq.getContainerContentsString();
	}

	/**
	 * Get Contents Num Token
	 * @param eq
	 * @return Contents Num Token
	 */
	public static String getContentsNumToken(Equipment eq)
	{
		return String.valueOf(getContentsNumTokenInt(eq));
	}

	/**
	 * Get Contents Num Token as int
	 * @param eq
	 * @return Contents Num Token as int
	 */
	public static int getContentsNumTokenInt(Equipment eq)
	{
		return eq.getContents().size();
	}

	/**
	 * Get Cost token
	 * @param pc
	 * @param eq
	 * @return Cost token
	 */
	public static String getCostToken(PlayerCharacter pc, Equipment eq)
	{
		return BigDecimalHelper.trimZeros(eq.getCost(pc));
	}

	/**
	 * Get Critical Multiplier Token
	 * @param eq
	 * @return Critical Multiplier Token
	 */
	public static String getCritMultToken(Equipment eq)
	{
		return EqToken.multAsString(eq.getCritMultiplier());
	}

	/**
	 * Get Critical Range Token
	 * @param pc
	 * @param eq
	 * @return Critical Range Token
	 */
	public static String getCritRangeToken(PlayerCharacter pc, Equipment eq)
	{
		String critRangeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.CRITRANGE);
		if (critRangeVar == null)
		{
			int critRange = getOldBonusedCritRange(pc, eq, true);
			return critRange == 0 ? "" : Integer.toString(critRange);
		}
		else
		{
			EquipmentHead head = eq.getEquipmentHead(1);
			return WeaponToken.getCritRangeHead(pc, head, critRangeVar).toString();
		}
	}

	/**
	 * Get Damage Token
	 * @param pc
	 * @param eq
	 * @return Damage Token
	 */
	public static String getDamageToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getDamage(pc);
	}

	/**
	 * Get Description Token
	 * @param eq
	 * @return Description Token
	 */
	public static String getDescriptionToken(final PlayerCharacter aPC, Equipment eq)
	{
		return aPC.getDescription(eq);
	}

	/**
	 * Get eDR Token
	 * @param pc
	 * @param eq
	 * @return eDR Token
	 */
	public static String getEdrToken(PlayerCharacter pc, Equipment eq)
	{
		return String.valueOf(getEdrTokenInt(pc, eq));
	}

	/**
	 * Get eDR Token as int
	 * @param pc
	 * @param eq
	 * @return eDR Token as int
	 */
	public static int getEdrTokenInt(PlayerCharacter pc, Equipment eq)
	{
		String edrVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.EDR);
		if (edrVar == null)
		{
			return Math.max(0, eq.getSafe(IntegerKey.EDR) + (int) eq.bonusTo(pc, "EQMARMOR", "EDR", true));
		}
		return (Integer) eq.getLocalVariable(pc.getCharID(), edrVar);
	}

	/**
	 * Get Equipped Token
	 * @param eq
	 * @return Equipped Token
	 */
	public static String getEquippedToken(Equipment eq)
	{
		return getEquippedTokenBoolean(eq) ? "Y" : "N";
	}

	/**
	 * Get Equipped Token as boolean
	 * @param eq
	 * @return Equipped Token as boolean
	 */
	public static boolean getEquippedTokenBoolean(Equipment eq)
	{
		return eq.isEquipped();
	}

	/**
	 * Get Fumble Range Token
	 * @param eq
	 * @return Fumble Range Token
	 */
	public static String getFumbleRangeToken(PlayerCharacter pc, Equipment eq)
	{
		String frVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.FUMBLERANGE);
		if (frVar == null)
		{
			for (EquipmentModifier eqMod : eq.getEqModifierList(true))
			{
				String fr = eqMod.get(StringKey.FUMBLE_RANGE);
				if (fr != null)
				{
					return fr;
				}
			}

			for (EquipmentModifier eqMod : eq.getEqModifierList(false))
			{
				String fr = eqMod.get(StringKey.FUMBLE_RANGE);
				if (fr != null)
				{
					return fr;
				}
			}

			String fr = eq.get(StringKey.FUMBLE_RANGE);
			return fr == null ? "" : fr;
		}
		return (String) eq.getLocalVariable(pc.getCharID(), frVar);

	}

	/**
	 * Get Is Type Token
	 * @param eq
	 * @param type
	 * @return Is Type Token
	 */
	public static String getIsTypeToken(Equipment eq, String type)
	{
		return getIsTypeTokenBoolean(eq, type) ? "TRUE" : "FALSE";
	}

	/**
	 * Get Is Type Token as boolean
	 * @param eq
	 * @param type
	 * @return Is Type Token as boolean
	 */
	public static boolean getIsTypeTokenBoolean(Equipment eq, String type)
	{
		return eq.isType(type);
	}

	/**
	 * Get Location Token
	 * @param eq
	 * @return Location Token
	 */
	public static String getLocationToken(Equipment eq)
	{
		Equipment obj = eq.getParent();
		if (obj != null)
		{
			return OutputNameFormatting.getOutputName(obj);
		}
		return eq.getParentName();
	}

	/**
	 * Get Long Name Token
	 * @param eq
	 * @return Long Name Token
	 */
	public static String getLongNameToken(Equipment eq)
	{
		return eq.longName();
	}

	/**
	 * Get Max Charges Token
	 * @param eq
	 * @return Max Charges Token
	 */
	public static String getMaxChargesToken(Equipment eq)
	{
		String retString = "";
		int charges = getMaxChargesTokenInt(eq);
		if (charges >= 0)
		{
			retString = String.valueOf(charges);
		}
		return retString;
	}

	/**
	 * Get Max Charges Token as int
	 * @param eq
	 * @return Max Charges Token as int
	 */
	public static int getMaxChargesTokenInt(Equipment eq)
	{
		return eq.getMaxCharges();
	}

	/**
	 * Get Max DEX Token as int
	 * @param pc
	 * @param eq
	 * @return Max DEX Token as int
	 */
	public static int getMaxDexTokenInt(PlayerCharacter pc, Equipment eq)
	{
		String maxDexVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.EQMAXDEX);
		if (maxDexVar == null)
		{
			int mdex = eq.getSafe(IntegerKey.MAX_DEX) + (int) eq.bonusTo(pc, "EQMARMOR", "MAXDEX", true);
			return Math.min(Constants.MAX_MAXDEX, Math.max(0, mdex));
		}
		return ((Number) eq.getLocalVariable(pc.getCharID(), maxDexVar)).intValue();
	}

	/**
	 * Get Move Token
	 * @param eq
	 * @return Move Token
	 */
	public static String getMoveToken(Equipment eq)
	{
		return eq.moveString();
	}

	/**
	 * Get Name Token
	 * @param eq
	 * @param pc
	 * @return Name Token
	 */
	public static String getNameToken(Equipment eq, PlayerCharacter pc)
	{
		return OutputNameFormatting.parseOutputName(eq, pc);
	}

	/**
	 * Get Note Token
	 * @param eq
	 * @return Note Token
	 */
	public static String getNoteToken(Equipment eq)
	{
		return eq.getNote();
	}

	/**
	 * Get QTY Token
	 * @param eq
	 * @return QTY Token
	 */
	public static String getQtyToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(Double.toString(getQtyDoubleToken(eq)));
	}

	/**
	 * Get QTY Token as double
	 * @param eq
	 * @return QTY Token as double
	 */
	public static double getQtyDoubleToken(Equipment eq)
	{
		return eq.qty();
	}

	/**
	 * Get CHECKBOXES Token
	 * @param eq
	 * @return CHECKBOXES Token
	 */
	public static double getCheckboxesDoubleToken(Equipment eq)
	{
		return getQtyDoubleToken(eq) * eq.getSafe(IntegerKey.BASE_QUANTITY);
	}

	/**
	 * Get CHECKBOXES Token
	 * @param eq
	 * @return CHECKBOXES Token
	 */
	public static String getCheckboxesToken(Equipment eq)
	{
		return BigDecimalHelper.trimZeros(Double.toString(getCheckboxesDoubleToken(eq)));
	}

	/**
	 * Get Range Token
	 * @param eq
	 * @param pc
	 * @return Range Token
	 */
	public static String getRangeToken(Equipment eq, PlayerCharacter pc)
	{
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(getRange(pc, eq))
			+ Globals.getGameModeUnitSet().getDistanceUnit();
	}

	/**
	 * Get Size Token
	 * @param eq
	 * @return Size Token
	 */
	public static String getSizeToken(Equipment eq)
	{
		return eq.getSize();
	}

	/**
	 * Get SizeLong Token
	 * @param eq
	 * @return SizeLong Token
	 */
	public static String getSizeLongToken(Equipment eq)
	{
		return eq.getSizeAdjustment().getDisplayName();
	}

	/**
	 * Get Equipment Slot Token
	 * @param eq
	 * @return Equipment Slot Token
	 */
	public static String getSlotToken(Equipment eq)
	{
		return eq.getSlot();
	}

	/**
	 * Get Source Token
	 * @param eq
	 * @return Source Token
	 */
	public static String getSourceToken(Equipment eq)
	{
		return SourceFormat.getFormattedString(eq, Globals.getSourceDisplay(), true);
	}

	public static int getSpellFailureTokenInt(PlayerCharacter pc, Equipment eq)
	{
		String spellFailVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.EQSPELLFAILURE);
		if (spellFailVar == null)
		{
			return Math.max(0,
				eq.getSafe(IntegerKey.SPELL_FAILURE) + (int) eq.bonusTo(pc, "EQMARMOR", "SPELLFAILURE", true));
		}
		return ((Number) eq.getLocalVariable(pc.getCharID(), spellFailVar)).intValue();
	}

	/**
	 * Get Special Property Token
	 * @param pc
	 * @param eq
	 * @return Special Property Token
	 */
	public static String getSpropToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.getSpecialProperties(pc);
	}

	/**
	 * Get Total Weight Token as double
	 * @param pc
	 * @param eq
	 * @return Total Weight Token as double
	 */
	public static double getTotalWeightTokenDouble(PlayerCharacter pc, Equipment eq)
	{
		return getContentWeightTokenDouble(pc, eq) + getWtTokenDouble(pc, eq);
	}

	/**
	 * Get TotalWt Token as double
	 * @param pc
	 * @param eq
	 * @return TotalWt Token as double
	 */
	public static double getTotalWtTokenDouble(PlayerCharacter pc, Equipment eq)
	{
		return eq.qty() * eq.getWeightAsDouble(pc);
	}

	/**
	 * Get Type Token
	 * @param eq
	 * @return Type Token
	 */
	public static String getTypeToken(Equipment eq)
	{
		return eq.getType().toUpperCase();
	}

	/**
	 * Get Type Token
	 * @param eq
	 * @param num index
	 * @return Type Token
	 */
	public static String getTypeToken(Equipment eq, int num)
	{
		return eq.typeIndex(num).toUpperCase();
	}

	/**
	 * Get TotalWT Token as double
	 * @param pc
	 * @param eq
	 * @return TotalWT Token as double
	 */
	public static double getWtTokenDouble(PlayerCharacter pc, Equipment eq)
	{
		return eq.getWeightAsDouble(pc);
	}

	/**
	 * Remove a type from a EQ List
	 * @param eqList
	 * @param type
	 * @return List
	 */
	public static List<Equipment> listNotType(List<Equipment> eqList, String type)
	{
		return EquipmentUtilities.removeEqType(eqList, type);
	}

	/**
	 * Add a type from a EQ List
	 * @param pc
	 * @param eqList
	 * @param type
	 * @return List
	 */
	public static List<Equipment> listAddType(PlayerCharacter pc, List<Equipment> eqList, String type)
	{
		return pc.addEqType(eqList, type);
	}

	/**
	 * Remove all other types from a EQ List
	 * @param eqList
	 * @param type
	 * @return List
	 */
	public static List<Equipment> listIsType(List<Equipment> eqList, String type)
	{
		return EquipmentUtilities.removeNotEqType(eqList, type);
	}

	protected static String getEqToken(PlayerCharacter pc, Equipment eq, String token, StringTokenizer tokenizer)
	{
		String retString = "";

		if ("LONGNAME".equals(token))
		{
			retString = getLongNameToken(eq);
		}
		else if ("NAME".equals(token) || "OUTPUTNAME".equals(token))
		{
			retString = getNameToken(eq, pc);
		}
		else if ("NOTE".equals(token))
		{
			retString = getNoteToken(eq);
		}
		else if ("WT".equals(token) || "ITEMWEIGHT".equals(token))
		{
			retString = Globals.getGameModeUnitSet().displayWeightInUnitSet(getWtTokenDouble(pc, eq));
		}
		else if ("TOTALWT".equals(token))
		{
			retString = Globals.getGameModeUnitSet().displayWeightInUnitSet(getTotalWtTokenDouble(pc, eq));
		}
		else if ("TOTALWEIGHT".equals(token))
		{
			retString = Globals.getGameModeUnitSet().displayWeightInUnitSet(getTotalWeightTokenDouble(pc, eq));
		}
		else if ("ISTYPE".equals(token))
		{
			retString = getIsTypeToken(eq, tokenizer.nextToken());
		}
		else if ("CHECKBOXES".equals(token))
		{
			retString = getCheckboxesToken(eq);
		}
		else if ("CONTENTWEIGHT".equals(token))
		{
			retString = Globals.getGameModeUnitSet().displayWeightInUnitSet(getContentWeightTokenDouble(pc, eq));
		}
		else if ("COST".equals(token))
		{
			retString = getCostToken(pc, eq);
		}
		else if ("DESC".equals(token))
		{
			retString = getDescriptionToken(pc, eq);
		}
		else if ("FUMBLERANGE".equals(token))
		{
			retString = getFumbleRangeToken(pc, eq);
		}
		else if ("QTY".equals(token))
		{
			retString = getQtyToken(eq);
		}
		else if ("EQUIPPED".equals(token))
		{
			retString = getEquippedToken(eq);
		}
		else if ("CARRIED".equals(token))
		{
			retString = getCarriedToken(eq);
		}
		else if ("CONTENTSNUM".equals(token))
		{
			retString = getContentsNumToken(eq);
		}
		else if ("LOCATION".equals(token))
		{
			retString = getLocationToken(eq);
		}
		else if ("ACMOD".equals(token))
		{
			retString = getAcModToken(pc, eq);
		}
		else if ("MAXDEX".equals(token))
		{
			retString = Integer.toString(getMaxDexTokenInt(pc, eq));
		}
		else if ("ACCHECK".equals(token))
		{
			retString = getAcCheckToken(pc, eq);
		}
		else if ("EDR".equals(token))
		{
			retString = getEdrToken(pc, eq);
		}
		else if ("MOVE".equals(token))
		{
			retString = getMoveToken(eq);
		}
		else if ("TYPE".equals(token))
		{
			if (tokenizer.hasMoreTokens())
			{
				try
				{
					int num = Integer.parseInt(tokenizer.nextToken());
					return getTypeToken(eq, num);
				}
				catch (NumberFormatException e)
				{
					// TODO - This exception needs to be handled
				}
			}
			return getTypeToken(eq);
		}
		else if ("QUALITY".equals(token))
		{
			Map<String, String> qualityMap = eq.getMapFor(MapKey.QUALITY);
			if (qualityMap != null)
			{
				if (tokenizer.hasMoreTokens())
				{
					String next = tokenizer.nextToken();
					try
					{
						int idx = Integer.parseInt(next);
						for (String value : qualityMap.values())
						{
							idx--;
							if (idx == 0)
							{
								return value;
							}
						}
					}
					catch (NumberFormatException e)
					{
						String value = qualityMap.get(next);
						if (value != null)
						{
							return value;
						}
					}
					return "";
				}
				Set<String> qualities = new TreeSet<>();
				for (Map.Entry<String, String> me : qualityMap.entrySet())
				{
					qualities
						.add(me.getKey() + ": " + me.getValue());
				}
				return StringUtil.join(qualities, ", ");
			}
			return "";
		}
		else if ("SPELLFAILURE".equals(token))
		{
			retString = Integer.toString(EqToken.getSpellFailureTokenInt(pc, eq));
		}
		else if ("SIZE".equals(token))
		{
			retString = getSizeToken(eq);
		}
		else if ("SIZELONG".equals(token))
		{
			retString = getSizeLongToken(eq);
		}
		else if ("DAMAGE".equals(token))
		{
			retString = getDamageToken(pc, eq);
		}
		else if ("CRITRANGE".equals(token))
		{
			retString = getCritRangeToken(pc, eq);
		}
		else if ("CRITMULT".equals(token))
		{
			retString = getCritMultToken(eq);
		}
		else if ("ALTDAMAGE".equals(token))
		{
			retString = getAltDamageToken(pc, eq);
		}
		else if ("ALTCRITMULT".equals(token) || "ALTCRIT".equals(token))
		{
			retString = getAltCritMultToken(eq);
		}
		else if ("ALTCRITRANGE".equals(token))
		{
			retString = getAltCritRangeToken(pc, eq);
		}
		else if ("RANGE".equals(token))
		{
			retString = getRangeToken(eq, pc);
		}
		else if ("ATTACKS".equals(token))
		{
			retString = getAttacksToken(pc, eq);
		}
		else if ("PROF".equals(token))
		{
			retString = eq.consolidatedProfName();
		}
		else if ("SPROP".equals(token))
		{
			retString = getSpropToken(pc, eq);
		}
		else if ("CHARGES".equals(token))
		{
			retString = getChargesToken(eq);
		}
		else if ("CHARGESUSED".equals(token))
		{
			retString = getChargesUsedToken(eq);
		}
		else if ("MAXCHARGES".equals(token))
		{
			retString = getMaxChargesToken(eq);
		}
		else if ("CONTENTS".equals(token))
		{
			retString = getContentsToken(pc, eq, tokenizer);
		}
		else if ("SOURCE".equals(token))
		{
			retString = getSourceToken(eq);
		}
		else if ("SLOT".equals(token))
		{
			retString = getSlotToken(eq);
		}
		return retString;
	}

	protected static int returnMergeType(String type)
	{
		int merge = Constants.MERGE_ALL;

		if ("MERGENONE".equals(type))
		{
			merge = Constants.MERGE_NONE;
		}
		else if ("MERGELOC".equals(type))
		{
			merge = Constants.MERGE_LOCATION;
		}
		else if ("MERGEALL".equals(type))
		{
			merge = Constants.MERGE_ALL;
		}

		return merge;
	}

	public static int getOldBonusedCritRange(PlayerCharacter pc, Equipment e, boolean primary)
	{
		if (!primary && !e.isDouble())
		{
			return 0;
		}
		int raw = e.getRawCritRange(primary);
		int add = (int) e.bonusTo(pc, "EQMWEAPON", "CRITRANGEADD", primary);
		int dbl = 1 + (int) e.bonusTo(pc, "EQMWEAPON", "CRITRANGEDOUBLE", primary);
		return raw * dbl + add;

	}

}
