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
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.EquipmentLocation;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SettingsHandler;
import pcgen.core.SizeAdjustment;
import pcgen.core.WeaponProf;
import pcgen.core.analysis.BonusCalc;
import pcgen.core.analysis.OutputNameFormatting;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.display.CharacterDisplay;
import pcgen.core.display.UnarmedDamageDisplay;
import pcgen.io.ExportHandler;
import pcgen.system.LanguageBundle;
import pcgen.util.Delta;
import pcgen.util.Logging;
import pcgen.util.enumeration.AttackType;

/**
 * Deal with the WEAPON Token
 */
public class WeaponToken extends Token
{
	/** Token Name */
	public static final String TOKENNAME = "WEAPON";
	/** PC Bonus = 0 */
	private static final int WPTYPEBONUS_PC = 0;
	/** Equipment Bonus = 1 */
	private static final int WPTYPEBONUS_EQ = 1;
	/** Feat Bonus = 2 */
	private static final int WPTYPEBONUS_FEAT = 2;
	/** Template Bonus = 3 */
	private static final int WPTYPEBONUS_TEMPLATE = 3;
	/** Damage Mode normal = 0 */
	private static final int DAMAGEMODE_NORMAL = 0;
	/** Damage Mode basic = 1 */
	private static final int DAMAGEMODE_BASIC = 1;
	/** Damage Mode offhand = 2 */
	private static final int DAMAGEMODE_OFFHAND = 2;
	/** Damage Mode twohands = 3 */
	private static final int DAMAGEMODE_TWOHANDS = 3;
	/** Damage Mode double = 4 */
	private static final int DAMAGEMODE_DOUBLE = 4;

	// This defines if I should return the values
	// based on weapon's location or not.
	// 1,2,3 and 4 overrides the actual location
	// of the weapon and calculates all data
	// with that setting
	/** total hit = 0 */
	private static final int HITMODE_TOTALHIT = 0;
	/** One weapon = 1 */
	private static final int HITMODE_BASEHIT = 1;
	/** Two weapons, this is primary, off-hand heavy = 2 */
	private static final int HITMODE_TWPHITH = 2;
	/** Two weapons, this is primary, off-hand light = 3 */
	private static final int HITMODE_TWPHITL = 3;
	/** Two weapons, this is off-hand (heavy) = 4 */
	private static final int HITMODE_TWOHIT = 4;
	/** Two weapons, this is off-hand (heavy) = 4 */
	private static final int HITMODE_TWFOHH = 4;
	/** Two weapons, this is off-hand (light) = 5 */
	private static final int HITMODE_TWFOHL = 5;
	/** One weapon, off-hand = 6 */
	private static final int HITMODE_OHHIT = 6;
	/** One weapon, both-hands = 7 */
	private static final int HITMODE_THHIT = 7;

	@Override
	public String getTokenName()
	{
		return TOKENNAME;
	}

	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		//Weapon Token
		aTok.nextToken();

		int merge = Constants.MERGE_ALL;
		int weapon;
		Equipment eq;

		// First check to see if there is a MERGE token
		String token = aTok.nextToken();
		switch (token)
		{
			case "MERGENONE":
				merge = Constants.MERGE_NONE;
				token = aTok.nextToken();
				break;
			case "MERGELOC":
				merge = Constants.MERGE_LOCATION;
				token = aTok.nextToken();
				break;
			case "MERGEALL":
				merge = Constants.MERGE_ALL;
				token = aTok.nextToken();
				break;
		}

		List<Equipment> weaponList = pc.getExpandedWeapons(merge);

		switch (token)
		{
			case "ALL":
				token = aTok.nextToken();
				break;
			case "EQUIPPED":
				// remove all weapons which are not equipped from list
				weaponList.removeIf(equipment -> !equipment.isEquipped());
				token = aTok.nextToken();
				break;
			case "NOT_EQUIPPED":
				// remove all weapons which are equipped from list
				weaponList.removeIf(Equipment::isEquipped);
				token = aTok.nextToken();
				break;
			case "CARRIED":
				// remove all weapons which are not carried from list
				weaponList.removeIf(equipment -> equipment.numberCarried().intValue() == 0);
				token = aTok.nextToken();
				break;
			case "NOT_CARRIED":
				// remove all weapons which are carried from list
				weaponList.removeIf(equipment -> equipment.numberCarried().intValue() > 0);
				token = aTok.nextToken();
				break;
		}

		weapon = getIntToken(token, 0);
		if (weapon < weaponList.size())
		{
			eq = weaponList.get(weapon);
			if (weapon == weaponList.size() - 1 && eh != null && eh.getExistsOnly())
			{
				eh.setNoMoreItems(true);
			}
			return getWeaponToken(pc, eq, aTok, tokenSource);
		}
		else if (eh != null && eh.getExistsOnly())
		{
			eh.setNoMoreItems(true);
			if (eh.getCheckBefore())
			{
				eh.setCanWrite(false);
			}
		}
		return "";
	}

	/**
	 * Get the Weapon Token output
	 * 
	 * @param pc The character being exported
	 * @param eq The weapon being exported
	 * @param aTok The exporttoken split by . and up to the weapon property 
	 * @param tokenSource The original source of the export token (for error reporting.)
	 * @return The output for the token for the weapon and character.
	 */
	protected String getWeaponToken(PlayerCharacter pc, Equipment eq, StringTokenizer aTok, String tokenSource)
	{
		String token = "";
		if (aTok.hasMoreTokens())
		{
			token = aTok.nextToken();
		}

		int range = -1;
		int content = -1;
		int ammo = -1;

		if (token.equals("RANGELIST"))
		{
			range = getIntToken(aTok, -1);

			if (aTok.hasMoreTokens())
			{
				token = aTok.nextToken();
			}
			else
			{
				token = "RANGELIST";
			}
		}

		if (token.equals("CONTENTS"))
		{
			if (aTok.hasMoreTokens())
			{
				content = getIntToken(aTok, -1);

				if (aTok.hasMoreTokens())
				{
					token = aTok.nextToken();
				}
				else
				{
					token = "CONTENTS";
				}
			}
			else
			{
				token = "CONTENTSCOUNT";
			}
		}

		if (token.equals("AMMUNITION"))
		{
			if (aTok.hasMoreTokens())
			{
				ammo = getIntToken(aTok, -1);

				if (aTok.hasMoreTokens())
				{
					token = aTok.nextToken();
				}
				else
				{
					token = "AMMUNITION";
				}
			}
			else
			{
				token = "AMMUNITIONCOUNT";
			}
		}

		switch (token)
		{
			case "NAME":
				boolean star = true;
				if (aTok.hasMoreTokens())
				{
					if ("NOSTAR".equals(aTok.nextToken()))
					{
						star = false;
					}
				}
				return getNameToken(eq, pc, star);
			case "OUTPUTNAME":
				return getOutputNameToken(eq, pc);
			case "LONGNAME":
				return getLongNameToken(eq);
			case "ATTACKS":
				return String.valueOf(getAttacksToken(pc, eq));
			case "AMMUNITIONCOUNT":
				return String.valueOf(getAmmunitionCountToken(pc, eq));
			case "AMMUNITION":
				return getAmmunitionToken(pc, eq, ammo);
			case "CONTENTSCOUNT":
				return String.valueOf(getContentsCountToken(eq));
			case "CONTENTS":
				return getContentsToken(eq, content);
			case "NUMATTACKS":
				return String.valueOf(getNumAttacksToken(pc, eq));
			case "HEFT":
				return getHeft(pc, eq);
			case "ISTYPE":
				if (aTok.hasMoreTokens())
				{
					return getIsTypeToken(eq, aTok.nextToken());
				}
				return "";
			case "CRIT":
				return getCritToken(pc, eq);
			case "MULT":
				return getMultToken(pc, eq);
			case "RANGELIST":
				return getRangeListToken(eq, range, pc);
			case "RANGE":
				boolean units = true;
				if (aTok.hasMoreTokens())
				{
					if ("NOUNITS".equals(aTok.nextToken()))

					{
						units = false;
					}
				}
				return getRangeToken(eq, pc, units);
			case "SIZEMOD":
				return Delta.toString(getSizeModToken(pc));
			case "TYPE":
				return getTypeToken(eq);
			case "HIT":
			case "TOTALHIT":
			{
				int attack = getIntToken(aTok, -1);
				return getTotalHitToken(pc, eq, range, content, ammo, attack);
			}
			case "BASEHIT":
			{
				int attack = getIntToken(aTok, -1);
				return getBaseHitToken(pc, eq, range, content, ammo, attack);
			}
			case "TWPHITH":
			{
				int attack = getIntToken(aTok, -1);
				return getTwpHitHToken(pc, eq, range, content, ammo, attack);
			}
			case "TWPHITL":
			{
				int attack = getIntToken(aTok, -1);
				return getTwpHitLToken(pc, eq, range, content, ammo, attack);
			}
			case "TWOHIT":
			{
				int attack = getIntToken(aTok, -1);
				return getTwoHitToken(pc, eq, range, content, ammo, attack);
			}
			case "OHHIT":
			{
				int attack = getIntToken(aTok, -1);
				return getOHHitToken(pc, eq, range, content, ammo, attack);
			}
			case "THHIT":
			{
				int attack = getIntToken(aTok, -1);
				return getTHHitToken(pc, eq, range, content, ammo, attack);
			}
			case "CATEGORY":
				return getCategoryToken(eq);
			case "HAND":
				return getHandToken(eq);
			case "MAGICDAMAGE":
				return Delta.toString(getMagicDamageToken(pc, eq));
			case "MAGICHIT":
				return Delta.toString(getMagicHitToken(pc, eq));
			case "MISC":
				return Delta.toString(getMiscToken(pc, eq));
			case "FEATDAMAGE":
				return Delta.toString(getFeatDamageToken(pc, eq));
			case "FEATHIT":
				return Delta.toString(getFeatHitToken(pc, eq));
			case "TEMPLATEDAMAGE":
				return Delta.toString(getTemplateDamageToken(pc, eq));
			case "TEMPLATEHIT":
				return Delta.toString(getTemplateHitToken(pc, eq));
			case "DAMAGE":
				return getDamageToken(pc, eq, range, content, ammo, false, false);
			case "BASEDAMAGE":
				return getDamageToken(pc, eq, range, content, ammo, false, true);
			case "BASICDAMAGE":
				return getBasicDamageToken(pc, eq, range, content, ammo, false);
			case "THDAMAGE":
				return getTHDamageToken(pc, eq, range, content, ammo, false);
			case "OHDAMAGE":
				return getOHDamageToken(pc, eq, range, content, ammo, false);
			case "DAMAGEBONUS":
			case "BONUSDAMAGE":
				return getDamageToken(pc, eq, range, content, ammo, true, false);
			case "BASEDAMAGEBONUS":
				return getDamageToken(pc, eq, range, content, ammo, true, true);
			case "THDAMAGEBONUS":
				return getTHDamageToken(pc, eq, range, content, ammo, true);
			case "OHDAMAGEBONUS":
				return getOHDamageToken(pc, eq, range, content, ammo, true);
			case "SIZE":
				return getSizeToken(eq);
			case "SPROP":
				return getSpropToken(pc, eq, content, ammo);
			case "REACH":
				return getReachToken(pc, eq);
			case "REACHUNIT":
				return Globals.getGameModeUnitSet().getDistanceUnit();
			case "WT":
				return getWTToken(pc, eq);
			case "RATEOFFIRE":
				FactKey<String> fk = FactKey.valueOf("RateOfFire");
				String str = eq.getResolved(fk);
				return (str == null) ? "" : str;
			case "ISLIGHT":
				return getIsLightToken(pc, eq);
			case "QUALITY":
				Map<String, String> qualityMap = eq.getMapFor(MapKey.QUALITY);
				if (qualityMap != null)
				{
					if (aTok.hasMoreTokens())
					{
						String next = aTok.nextToken();
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
						} catch (NumberFormatException e)
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
			case "CHARGES":
				String retString = "";
				int charges = eq.getRemainingCharges();
				if (charges >= 0)
				{
					retString = String.valueOf(charges);
				}
				return retString;

		}
		Logging.errorPrint("Invalid WEAPON token: " + tokenSource, new Throwable());
		return "";
	}

	/**
	 * Get the is light sub token
	 * @param pc
	 * @param eq
	 * @return is light sub token
	 */
	private static String getIsLightToken(PlayerCharacter pc, Equipment eq)
	{
		return eq.isWeaponLightForPC(pc) ? "TRUE" : "FALSE";
	}

	/**
	 * Get the name sub token
	 * @param eq
	 * @param pc
	 * @param star
	 * @return name sub token
	 */
	private static String getNameToken(Equipment eq, PlayerCharacter pc, boolean star)
	{
		StringBuilder sb = new StringBuilder();
		if (eq.isEquipped() && star)
		{
			sb.append('*');
		}
		sb.append(OutputNameFormatting.parseOutputName(eq, pc));
		sb.append(eq.getAppliedName());

		return sb.toString();
	}

	/**
	 * Get output name token
	 * @param eq
	 * @param pc
	 * @return out put name token
	 */
	private static String getOutputNameToken(Equipment eq, PlayerCharacter pc)
	{
		StringBuilder sb = new StringBuilder();
		if (eq.isEquipped())
		{
			sb.append('*');
		}
		sb.append(OutputNameFormatting.parseOutputName(eq, pc));
		sb.append(eq.getAppliedName());

		return sb.toString();
	}

	/**
	 * Get the long name sub token
	 * @param eq
	 * @return long name sub token
	 */
	private static String getLongNameToken(Equipment eq)
	{
		StringBuilder sb = new StringBuilder();
		if (eq.isEquipped())
		{
			sb.append('*');
		}
		sb.append(eq.longName());
		sb.append(eq.getAppliedName());

		return sb.toString();
	}

	/**
	 * Get Attacks sub token
	 * @param pc
	 * @param eq
	 * @return Attacks sub token
	 */
	private static int getAttacksToken(PlayerCharacter pc, Equipment eq)
	{
		return (int) eq.bonusTo(pc, "WEAPON", "ATTACKS", true);
	}

	/**
	 * Get ammunition count sub token
	 * @param pc
	 * @param eq
	 * @return ammunition count sub token
	 */
	private static int getAmmunitionCountToken(PlayerCharacter pc, Equipment eq)
	{
		int ammoCount = 0;
		String containerCapacity = eq.getContainerCapacityString().toUpperCase();

		for (Equipment equip : pc.getEquipmentListInOutputOrder())
		{
			for (String type : equip.typeList())
			{
				if (containerCapacity.contains(type))
				{
					++ammoCount;
					break;
				}
			}
		}

		return ammoCount;
	}

	/**
	 * Get the ammunition token
	 * @param pc
	 * @param eq
	 * @param ammo
	 * @return the ammunition token
	 */
	private static String getAmmunitionToken(PlayerCharacter pc, Equipment eq, int ammo)
	{
		Equipment ammoUser = getAmmoUser(pc, eq, ammo);

		if (ammoUser != null)
		{
			return ammoUser.getName();
		}
		return "";
	}

	/**
	 * Get the contents count sub token
	 * @param eq
	 * @return contents count sub token
	 */
	private static int getContentsCountToken(Equipment eq)
	{
		return eq.getContainedEquipmentCount();
	}

	/**
	 * Get Contents token
	 * @param eq
	 * @param content
	 * @return Get Contents token
	 */
	private static String getContentsToken(Equipment eq, int content)
	{
		if (content > -1)
		{
			if (content < eq.getContainedEquipmentCount())
			{
				return eq.getContainedEquipment(content).getName();
			}
		}
		return "";
	}

	/**
	 * Get the Heft token
	 * @param pc
	 * @param eq
	 * @return heft token
	 */
	private static String getHeft(PlayerCharacter pc, Equipment eq)
	{
		String retString;
		if (pc.sizeInt() > eq.sizeInt())
		{
			retString = "LIGHT";
		}
		else if (pc.sizeInt() == eq.sizeInt())
		{
			retString = "MEDIUM";
		}
		else
		{
			retString = "HEAVY";
		}
		return retString;
	}

	/**
	 * Get the is type token
	 * @param eq
	 * @param type
	 * @return is type token
	 */
	private static String getIsTypeToken(Equipment eq, String type)
	{
		return isTypeToken(eq, type) ? "TRUE" : "FALSE";
	}

	/**
	 * Get the istype token
	 * @param eq
	 * @param type
	 * @return is type token
	 */
	private static boolean isTypeToken(Equipment eq, String type)
	{
		return eq.isType(type);
	}

	/**
	 * Get the MULT token
	 * @param pc
	 * @param eq
	 * @return MULT token
	 */
	private static String getMultToken(PlayerCharacter pc, Equipment eq)
	{
		String critMultVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.CRITMULT);
		if (critMultVar != null)
		{
			return WeaponToken.getNewCritMultString(pc, eq, critMultVar);
		}
		String profName = getProfName(eq);
		StringBuilder sb = new StringBuilder();
		boolean isDouble = (eq.isDouble() && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS));
		int mult = (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "CRITMULTADD")
			+ getWeaponProfTypeBonuses(pc, eq, "CRITMULTADD", WPTYPEBONUS_PC);

		int critMult = eq.getCritMultiplier();
		if (critMult <= 0)
		{
			sb.append(mult);
		}
		else
		{
			sb.append(critMult + mult);
		}

		int altCrit = eq.getAltCritMultiplier();

		if (isDouble && (altCrit > 0))
		{
			sb.append('/').append(altCrit + mult);
		}
		return sb.toString();
	}

	public static String getNewCritMultString(PlayerCharacter pc, Equipment eq, String critMultVar)
	{
		CharID id = pc.getCharID();
		Object critMult1 = eq.getEquipmentHead(1).getLocalVariable(id, critMultVar);
		Object critMult2 = eq.getEquipmentHead(2).getLocalVariable(id, critMultVar);
		if (critMult1.equals(critMult2))
		{
			return critMult1.toString();
		}
		return String.valueOf(critMult1)
				+ '/'
				+ critMult2;
	}

	/**
	 * Retrieve the proficiency name for the provided item of equipment. That is
	 * the name of the weapon proficiency that is required to correctly use the 
	 * item. 
	 * @param eq The equipment item.
	 * @return The name of the proficiency, or empty string if no proficiency is defined.
	 */
	private static String getProfName(Equipment eq)
	{
		CDOMSingleRef<WeaponProf> ref = eq.get(ObjectKey.WEAPON_PROF);
		String profName;
		if (ref == null)
		{
			profName = "";
		}
		else
		{
			profName = ref.get().getKeyName();
		}
		return profName;
	}

	/**
	 * Get the range list token
	 * @param eq
	 * @param range
	 * @param aPC
	 * @return range list token
	 */
	private static String getRangeListToken(Equipment eq, int range, PlayerCharacter aPC)
	{
		List<String> rangeList = getRangeList(eq, true, aPC);

		if (range < rangeList.size())
		{
			return Globals.getGameModeUnitSet().displayDistanceInUnitSet(Integer.parseInt(rangeList.get(range)))
				+ Globals.getGameModeUnitSet().getDistanceUnit();
		}
		return "";
	}

	/**
	 * Get the range token
	 * @param eq
	 * @param pc
	 * @param units
	 * @return range token
	 */
	private static String getRangeToken(Equipment eq, PlayerCharacter pc, boolean units)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(Globals.getGameModeUnitSet().displayDistanceInUnitSet(EqToken.getRange(pc, eq)));

		if (units)
		{
			sb.append(Globals.getGameModeUnitSet().getDistanceUnit());
		}
		return sb.toString();
	}

	/**
	 * Get the size mod token
	 * @param pc
	 * @return the size mod token
	 */
	private static int getSizeModToken(PlayerCharacter pc)
	{
		return (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
	}

	/**
	 * Get the category token
	 * @param eq
	 * @return category token
	 */
	private static String getCategoryToken(Equipment eq)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(weaponCategories(eq));
		sb.append('-');

		if (eq.isNatural())
		{
			sb.append("Natural");
		}

		// If we're going to add another type then seperate with a ','
		// and set non standard to false
		if (appendSeperator(eq))
		{
			sb.append(',');
		}

		// Check if Both or Melee or Ranged
		if (eq.isType("Both"))
		{
			if (eq.isMelee())
			{
				sb.append("Both (Melee)");
			}
			else if (eq.isRanged())
			{
				sb.append("Both (Ranged)");
			}
		}
		else if (eq.isMelee())
		{
			sb.append("Melee");
		}
		else if (eq.isRanged())
		{
			sb.append("Ranged");
		}

		if (isNonStandard(eq))
		{
			sb.append("Non-Standard");
		}
		return sb.toString();
	}

	/**
	 * Get the type token
	 * @param eq
	 * @return type token
	 */
	private static String getTypeToken(Equipment eq)
	{
		String types = weaponTypes(eq, true);

		if (eq.isDouble())
		{
			types += ('/' + weaponTypes(eq, false));
		}

		return types;
	}

	/**
	 * Get hand token
	 * @param eq
	 * @return hand token
	 */
	private static String getHandToken(Equipment eq)
	{
		String location = eq.getLocation().getString();
		return location.replaceAll(".*\\(", "").replaceAll("\\(.*", "").replaceAll("\\).*", "");
	}

	/**
	 * Get Magic damage token
	 * @param pc
	 * @param eq
	 * @return Magic damage token
	 */
	private static int getMagicDamageToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return eq.getBonusToDamage(pc, true) + (int) BonusCalc.charBonusTo(eq, "WEAPONPROF=" + profName, "DAMAGE", pc)
			+ getWeaponProfTypeBonuses(pc, eq, "DAMAGE", WPTYPEBONUS_EQ);
	}

	/**
	 * Get the magic to hit token
	 * @param pc
	 * @param eq
	 * @return magic to hit token
	 */
	private static int getMagicHitToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return eq.getBonusToHit(pc, true) + (int) BonusCalc.charBonusTo(eq, "WEAPONPROF=" + profName, "TOHIT", pc)
			+ getWeaponProfTypeBonuses(pc, eq, "TOHIT", WPTYPEBONUS_EQ);
	}

	/**
	 * Get the misc token
	 * @param pc
	 * @param eq
	 * @return misc token
	 */
	private static int getMiscToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return ((int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "TOHIT")
			+ getWeaponProfTypeBonuses(pc, eq, "TOHIT", WPTYPEBONUS_PC))
			- (int) pc.getDisplay().getStatBonusTo("TOHIT", "TYPE.MELEE")
			- (int) pc.getSizeAdjustmentBonusTo("TOHIT", "TOHIT");
	}

	/**
	 * Get the feat damage token
	 * @param pc
	 * @param eq
	 * @return feat damage token
	 */
	private static int getFeatDamageToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return (int) pc.getFeatBonusTo("WEAPON", "DAMAGE") - (int) pc.getFeatBonusTo("WEAPON", "DAMAGE-SHORTRANGE")
			+ (int) pc.getFeatBonusTo("WEAPONPROF=" + profName, "DAMAGE")
			+ getWeaponProfTypeBonuses(pc, eq, "DAMAGE", WPTYPEBONUS_FEAT);
	}

	/**
	 * Get feat to hit token
	 * @param pc
	 * @param eq
	 * @return Get feat to hit token
	 */
	private static int getFeatHitToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return (int) pc.getFeatBonusTo("WEAPON", "TOHIT") + (int) pc.getFeatBonusTo("WEAPONPROF=" + profName, "TOHIT")
			+ getWeaponProfTypeBonuses(pc, eq, "TOHIT", WPTYPEBONUS_FEAT);
	}

	/**
	 * Get the template damage token
	 * @param pc
	 * @param eq
	 * @return template damage token
	 */
	private static int getTemplateDamageToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return (int) pc.getTemplateBonusTo("WEAPON", "DAMAGE")
			+ (int) pc.getTemplateBonusTo("WEAPONPROF=" + profName, "DAMAGE")
			+ getWeaponProfTypeBonuses(pc, eq, "DAMAGE", WPTYPEBONUS_TEMPLATE);
	}

	/**
	 * Get the template to hit token
	 * @param pc
	 * @param eq
	 * @return to hit token
	 */
	private static int getTemplateHitToken(PlayerCharacter pc, Equipment eq)
	{
		String profName = getProfName(eq);
		return (int) pc.getTemplateBonusTo("WEAPON", "TOHIT")
			+ (int) pc.getTemplateBonusTo("WEAPONPROF=" + profName, "TOHIT")
			+ getWeaponProfTypeBonuses(pc, eq, "TOHIT", WPTYPEBONUS_TEMPLATE);
	}

	/**
	 * Get the size token
	 * @param eq
	 * @return size token
	 */
	private static String getSizeToken(Equipment eq)
	{
		return eq.getSize();
	}

	/**
	 * Get SPROP token
	 * @param pc
	 * @param eq
	 * @param content
	 * @param ammo
	 * @return SPROP token
	 */
	private static String getSpropToken(PlayerCharacter pc, Equipment eq, int content, int ammo)
	{
		String sprop = eq.getSpecialProperties(pc);

		//Ammunition & Contents Modifier
		if (content > -1)
		{
			sprop = Constants.EMPTY_STRING;

			if ((content < eq.getContainedEquipmentCount())
				&& !Constants.EMPTY_STRING.equals(eq.getContainedEquipment(content).getSpecialProperties(pc)))
			{
				sprop = eq.getContainedEquipment(content).getSpecialProperties(pc);
			}
		}

		int ammoCount = 0;
		Equipment anEquip = null;
		if (ammo > -1)
		{
			final String containerCapacity = eq.getContainerCapacityString();

			for (Equipment equip : pc.getEquipmentListInOutputOrder())
			{
				sprop = Constants.EMPTY_STRING;

				for (String type : equip.typeList())
				{
					if (containerCapacity.contains(type))
					{
						++ammoCount;
						anEquip = equip;

						break;
					}
				}

				if (ammoCount == (ammo + 1))
				{
					break;
				}
			}
		}

		if ((anEquip != null) && (ammoCount > 0) && !Constants.EMPTY_STRING.equals(anEquip.getSpecialProperties(pc)))
		{
			sprop = anEquip.getSpecialProperties(pc);
		}

		if (sprop.startsWith(", "))
		{
			sprop = sprop.substring(2);
		}

		return sprop;
	}

	/**
	 * Get reach token
	 * Formula is as follows:
	 * 		REACH:(RACEREACH+(max(0,EQUIPREACH-5)))*EQUIPREACHMULT
	 * @param pc	the player
	 * @param eq	the equipment
	 * @return reach token
	 */
	private static String getReachToken(PlayerCharacter pc, Equipment eq)
	{
		String eqReach = pc.getControl("EQREACH");
		int sum;
		if (eqReach == null)
		{
			int dist = eq.getVariableValue(SettingsHandler.getGame().getWeaponReachFormula(), "", pc).intValue();
			String profName = getProfName(eq);
			int iAdd = (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "REACH")
				+ getWeaponProfTypeBonuses(pc, eq, "REACH", WPTYPEBONUS_PC);
			sum = dist + iAdd;
		}
		else
		{
			sum = ((Number) eq.getLocalVariable(pc.getCharID(), eqReach)).intValue();
		}
		return Globals.getGameModeUnitSet().displayDistanceInUnitSet(sum);
	}

	/**
	 * Get weight in set token
	 * @param pc
	 * @param eq
	 * @return  weight in set token
	 */
	private static String getWTToken(PlayerCharacter pc, Equipment eq)
	{
		return Globals.getGameModeUnitSet().displayWeightInUnitSet(eq.getWeight(pc).doubleValue());
	}

	/**
	 * Get the number of attacks token
	 * @param pc
	 * @param eq
	 * @return number of attacks token
	 */
	private static int getNumAttacksToken(PlayerCharacter pc, Equipment eq)
	{
		String melee = getMeleeAttackString(pc);
		String unarmed = getUnarmedAttackString(pc);
		String ranged = getRangedAttackString(pc);
		String weaponString = melee;

		if (eq.isRanged())
		{
			weaponString = ranged;
		}

		if (eq.isMonk())
		{
			if (unarmed.length() > melee.length())
			{
				weaponString = unarmed;
			}
			else if ((unarmed.length() == melee.length()) && !melee.equals(unarmed))
			{
				StringTokenizer mTok = new StringTokenizer(melee, "+/", false);
				StringTokenizer uTok = new StringTokenizer(unarmed, "+/", false);
				String msString = mTok.nextToken();
				String usString = uTok.nextToken();

				if (Integer.parseInt(usString) >= Integer.parseInt(msString))
				{
					weaponString = unarmed;
				}
			}
		}

		if (weaponString.contains("/"))
		{
			int i = weaponString.indexOf("/");
			boolean progress = eq.getSafe(ObjectKey.ATTACKS_PROGRESS);
			int bonusProgress = (int) eq.bonusTo(pc, "WEAPON", "ATTACKSPROGRESS", true);
			if (bonusProgress != 0)
			{
				progress = bonusProgress > 0;
			}
			if (!progress) // a natural weapon or other weapon with attack progression turned off
			{
				weaponString = weaponString.substring(0, i);
			}

		}
		StringTokenizer bTok = new StringTokenizer(weaponString, "/");
		int extra_attacks = (int) eq.bonusTo(pc, "WEAPON", "ATTACKS", true);
		return (bTok.countTokens() + extra_attacks);
	}

	/**
	 * Get critical token
	 * @param pc
	 * @param eq
	 * @return critical token
	 */
	private static String getCritToken(PlayerCharacter pc, Equipment eq)
	{
		StringBuilder sb = new StringBuilder();
		String critRangeVar = ControlUtilities.getControlToken(Globals.getContext(), CControl.CRITRANGE);
		if (critRangeVar != null)
		{
			EquipmentHead head = eq.getEquipmentHead(1);
			return getCritRangeHead(pc, head, critRangeVar).toString();
		}

		boolean isDouble = (eq.isDouble() && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS));
		int rawCritRange = eq.getRawCritRange(true);

		// see if the weapon has any crit range
		if (rawCritRange == 0)
		{
			// no crit range!
			return "none";
		}

		String profName = getProfName(eq);
		int dbl = (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "CRITRANGEDOUBLE")
			+ getWeaponProfTypeBonuses(pc, eq, "CRITRANGEDOUBLE", WPTYPEBONUS_PC);
		int iAdd = (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "CRITRANGEADD")
			+ getWeaponProfTypeBonuses(pc, eq, "CRITRANGEADD", WPTYPEBONUS_PC);
		int eqDbl = dbl + (int) eq.bonusTo(pc, "EQMWEAPON", "CRITRANGEDOUBLE", true);
		int critrange = eq.getRawCritRange(true) * (eqDbl + 1);
		critrange = 21 - (critrange + iAdd + (int) eq.bonusTo(pc, "EQMWEAPON", "CRITRANGEADD", true));
		sb.append(String.valueOf(critrange));
		if (critrange < 20)
		{
			sb.append("-20");
		}

		if (isDouble && (EqToken.getOldBonusedCritRange(pc, eq, false) > 0))
		{
			eqDbl = dbl + (int) eq.bonusTo(pc, "EQMWEAPON", "CRITRANGEDOUBLE", false);

			int altCritRange = eq.getRawCritRange(false) * (eqDbl + 1);
			altCritRange = 21 - (altCritRange + iAdd + (int) eq.bonusTo(pc, "EQMWEAPON", "CRITRANGEADD", false));

			if (altCritRange != critrange)
			{
				sb.append("/").append(altCritRange);

				if (altCritRange < 20)
				{
					sb.append("-20");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Get damage token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param bonusOnly
	 * @param base
	 * @return damage token
	 */
	private static String getDamageToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                     boolean bonusOnly, boolean base)
	{
		boolean isDouble = (eq.isDouble() && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS));
		boolean isDoubleSplit = (eq.isType("Head1") || eq.isType("Head2"));
		int damageMode;
		int hands = 1;

		if (eq.isNatural() && (eq.getLocation() == EquipmentLocation.EQUIPPED_SECONDARY))
		{
			damageMode = DAMAGEMODE_OFFHAND;
			hands = 0;
		}
		else if (eq.isUnarmed())
		{
			damageMode = DAMAGEMODE_BASIC;
		}
		else if (isDouble && !isDoubleSplit)
		{
			damageMode = DAMAGEMODE_DOUBLE;
			hands = 1;
		}
		else if ((isDoubleSplit) && (eq.isWeaponTwoHanded(pc)))
		{
			damageMode = DAMAGEMODE_TWOHANDS;
			hands = 2;
		}
		else if (pc.getDisplay().isSecondaryWeapon(eq))
		{
			damageMode = DAMAGEMODE_OFFHAND;
			hands = 0;
		}
		else if (pc.getDisplay().isPrimaryWeapon(eq))
		{
			if (eq.getLocation() == EquipmentLocation.EQUIPPED_BOTH)
			{
				damageMode = DAMAGEMODE_TWOHANDS;
				hands = 2;
			}
			else
			{
				damageMode = DAMAGEMODE_BASIC;
			}
		}
		else
		{
			// Not wielded, probably just carried
			if (eq.isWeaponTwoHanded(pc))
			{
				damageMode = DAMAGEMODE_TWOHANDS;
				hands = 2;
			}
			else
			{
				damageMode = DAMAGEMODE_BASIC;
			}
		}
		return getDamage(pc, eq, range, content, ammo, bonusOnly, hands, damageMode, base);
	}

	/**
	 * Get basic damage token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param bonusOnly
	 * @return basic damage token
	 */
	private static String getBasicDamageToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                          boolean bonusOnly)
	{
		int hands = 1;
		return getDamage(pc, eq, range, content, ammo, bonusOnly, hands, DAMAGEMODE_BASIC, false);
	}

	/**
	 * Get two handed damage token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param bonusOnly
	 * @return two handed damage token
	 */
	private static String getTHDamageToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                       boolean bonusOnly)
	{
		int hands = 2;
		return getDamage(pc, eq, range, content, ammo, bonusOnly, hands, DAMAGEMODE_TWOHANDS, false);
	}

	/**
	 * Get Off hand damage token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param bonusOnly
	 * @return Off hand damage token
	 */
	private static String getOHDamageToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                       boolean bonusOnly)
	{
		int hands = 0;
		return getDamage(pc, eq, range, content, ammo, bonusOnly, hands, DAMAGEMODE_OFFHAND, false);
	}

	/**
	 * Get total hit token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return total hit token
	 */
	private static String getTotalHitToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                       int attackNum)
	{
		CharacterDisplay display = pc.getDisplay();
		boolean isDouble = (eq.isDouble() && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS));
		boolean isDoubleSplit = (eq.isType("Head1") || eq.isType("Head2"));
		int hitMode;

		// First do unarmed.
		if (eq.isUnarmed())
		{
			hitMode = HITMODE_BASEHIT;
		}
		// next do Double weapons
		else if (isDouble && !isDoubleSplit)
		{
			hitMode = HITMODE_TWOHIT;
		}
		else if (!isDouble && isDoubleSplit)
		{
			hitMode = HITMODE_THHIT;
		}
		// eq is Primary
		else if (display.isPrimaryWeapon(eq) && display.hasSecondaryWeapons())
		{
			Equipment sEq = display.getSecondaryWeapons().iterator().next();

			if (sEq == null)
			{
				// Hmm, weird
				// default to off-hand light
				hitMode = HITMODE_TWPHITL;
			}
			else if (sEq.isWeaponLightForPC(pc))
			{
				// offhand light
				hitMode = HITMODE_TWPHITL;
			}
			else
			{
				// offhand heavy
				hitMode = HITMODE_TWPHITH;
			}
		}
		// eq is Secondary
		else if (display.isSecondaryWeapon(eq) && display.hasPrimaryWeapons())
		{
			if (eq.isWeaponLightForPC(pc))
			{
				// offhand light
				hitMode = HITMODE_TWFOHL;
			}
			else
			{
				// offhand heavy
				hitMode = HITMODE_TWFOHH;
			}
		}
		// Just a single off-hand weapon
		else if (display.isSecondaryWeapon(eq) && !display.hasPrimaryWeapons())
		{
			hitMode = HITMODE_OHHIT;
		}
		// Just a single primary weapon
		else if (display.isPrimaryWeapon(eq) && !display.hasSecondaryWeapons())
		{
			if (eq.getLocation() == EquipmentLocation.EQUIPPED_BOTH)
			{
				// both hands
				hitMode = HITMODE_THHIT;
			}
			else
			{
				// single hand
				hitMode = HITMODE_BASEHIT;
			}
		}
		else
		{
			// Not double or single
			// Not primary or Secondary
			// probably just carried
			if (eq.isWeaponTwoHanded(pc))
			{
				// Two Handed weapon
				hitMode = HITMODE_THHIT;
			}
			else
			{
				// one handed weapon
				hitMode = HITMODE_BASEHIT;
			}
		}

		return getToHit(pc, eq, range, content, ammo, hitMode, attackNum);
	}

	/**
	 * Get base hit token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return base hit token
	 */
	private static String getBaseHitToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                      int attackNum)
	{
		return getToHit(pc, eq, range, content, ammo, HITMODE_BASEHIT, attackNum);
	}

	/**
	 * Get two weapon heavy off hand token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return two weapon heavy off hand token
	 */
	private static String getTwpHitHToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                      int attackNum)
	{
		return getToHit(pc, eq, range, content, ammo, HITMODE_TWPHITH, attackNum);
	}

	/**
	 * Get two weapon light off hand token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return two weapon light off hand token
	 */
	private static String getTwpHitLToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                      int attackNum)
	{
		return getToHit(pc, eq, range, content, ammo, HITMODE_TWPHITL, attackNum);
	}

	/**
	 * Get two hit token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return two hit token
	 */
	private static String getTwoHitToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                     int attackNum)
	{
		return getToHit(pc, eq, range, content, ammo, HITMODE_TWOHIT, attackNum);
	}

	/**
	 * Get Off Hand Hit Token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return Off Hand Hit Toke
	 */
	private static String getOHHitToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                    int attackNum)
	{
		return getToHit(pc, eq, range, content, ammo, HITMODE_OHHIT, attackNum);
	}

	/**
	 * Get the TH Hit Token
	 * @param pc
	 * @param eq
	 * @param range
	 * @param content
	 * @param ammo
	 * @param attackNum
	 * @return the TH Hit Token
	 */
	private static String getTHHitToken(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
	                                    int attackNum)
	{
		return getToHit(pc, eq, range, content, ammo, HITMODE_THHIT, attackNum);
	}

	private static String getToHit(PlayerCharacter pc, Equipment eq, int range, int content, int ammo, int hitMode,
		int attackNum)
	{
		boolean isDouble = (eq.isDouble() && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS));
		boolean isDoubleSplit = (eq.isType("Head1") || eq.isType("Head2"));

		// If it's a two handed weapon, but is not
		// wielded as two handed, just punt now!
		if (eq.isMelee() && (eq.isWeaponTwoHanded(pc)))
		{
			if ((!isDouble && !isDoubleSplit && (hitMode != HITMODE_THHIT)) || (isDoubleSplit
				&& (hitMode == HITMODE_BASEHIT || hitMode == HITMODE_OHHIT || hitMode == HITMODE_TWPHITH)))
			{
				return LanguageBundle.getString("SettingsHandler.not.applicable");
			}
		}

		if (eq.isMelee() && eq.isWeaponOutsizedForPC(pc) && !eq.isNatural())
		{
			return LanguageBundle.getString("SettingsHandler.not.applicable");
		}

		int weaponBaseBonus = (int) eq.bonusTo(pc, "WEAPON", "WEAPONBAB", true);
		CDOMSingleRef<WeaponProf> ref = eq.get(ObjectKey.WEAPON_PROF);
		WeaponProf prof;
		String profKey;
		if (ref == null)
		{
			profKey = "";
			prof = null;
		}
		else
		{
			prof = ref.get();
			profKey = prof.getKeyName();
		}

		weaponBaseBonus += (int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "WEAPONBAB");
		weaponBaseBonus += getWeaponProfTypeBonuses(pc, eq, "WEAPONBAB", WPTYPEBONUS_PC);

		// The Melee, Ranged and Unarmed attack sequence
		String melee = getMeleeAttackString(pc, 0, weaponBaseBonus);
		String ranged = getRangedAttackString(pc, 0, weaponBaseBonus);
		String unarmed = getUnarmedAttackString(pc, 0, weaponBaseBonus);

		// Must leave this for 3.0 compatibility
		// 3.0 Monk uses special attack progression
		if (eq.isMonk())
		{
			if (unarmed.length() > melee.length())
			{
				melee = unarmed;
			}
			else if ((unarmed.length() == melee.length()) && !melee.equals(unarmed))
			{
				StringTokenizer mTok = new StringTokenizer(melee, "+/", false);
				StringTokenizer m1Tok = new StringTokenizer(melee, "+/", false);
				String msString = mTok.nextToken();
				String m1sString = m1Tok.nextToken();

				if (Integer.parseInt(m1sString) >= Integer.parseInt(msString))
				{
					melee = unarmed;
				}
			}
		}

		//
		// Now do all the calculations
		//
		int baseBonus = 0;

		int secondaryBonus = 0;
		int primaryBonus = 0;

		// Natural weapons are different
		if (eq.isNatural())
		{
			if (eq.getLocation() == EquipmentLocation.EQUIPPED_PRIMARY)
			{
				/* Primary Natural Weapons have no bonus or penalty
				 * associated with secondary weapons/attacks */
				baseBonus = 0;
			}
			else if (eq.getLocation() == EquipmentLocation.EQUIPPED_SECONDARY)
			{
				/* all secondary natural weapons attack at -5 */
				baseBonus = -5;

				/* Unless the creature has bonuses to improve
				 * secondary attacks, such as MultiAttack */
				baseBonus += pc.getTotalBonusTo("COMBAT", "TOHIT-SECONDARY");
			}
		}
		else
		{
			if ((hitMode == HITMODE_TOTALHIT && eq.isRanged()) || hitMode == HITMODE_BASEHIT
				|| hitMode == HITMODE_THHIT)
			{
				baseBonus = 0;
			}
			else if (hitMode == HITMODE_TWPHITH || hitMode == HITMODE_TWPHITL)
			{
				// TWF Primary hand
				baseBonus = -6;
			}
			else if (hitMode == HITMODE_OHHIT)
			{
				baseBonus = -4;
			}
			else
			{
				// TWF off-hand
				baseBonus = -10;
			}

			// TWF with off hand light gets a bonus
			if ((hitMode == HITMODE_TWPHITL) || (hitMode == HITMODE_TWFOHL))
			{
				baseBonus += pc.getOffHandLightBonus();
			}

			if ((hitMode == HITMODE_TWOHIT) && (isDouble || isDoubleSplit || eq.isWeaponLightForPC(pc)))
			{
				baseBonus += pc.getOffHandLightBonus();
			}

			if (hitMode == HITMODE_TWOHIT || hitMode == HITMODE_OHHIT || hitMode == HITMODE_TWFOHL)
			{
				secondaryBonus = (int) pc.getTotalBonusTo("COMBAT", "TOHIT-SECONDARY");

				if (eq.isRanged())
				{
					secondaryBonus -= (int) pc.getBonusDueToType("COMBAT", "TOHIT-SECONDARY", "NOTRANGED");
				}

				if (hitMode == HITMODE_OHHIT)
				{
					// If only using one weapon, Two-weapon Fighting Bonus does not apply
					// If you have TWF, you have both TOHIT-P and TOHIT-S, so remove TOHIT-P
					// TODO: Rework on this code and/or on the lst, because it "sounds" wrong
					// Felipe Diniz - 12/Feb/2003
					secondaryBonus -= (int) pc.getTotalBonusTo("COMBAT", "TOHIT-PRIMARY");
				}
			}

			if (((hitMode == HITMODE_TWPHITH) || (hitMode == HITMODE_TWPHITL)))
			{
				primaryBonus = (int) pc.getTotalBonusTo("COMBAT", "TOHIT-PRIMARY");

				if (eq.isRanged())
				{
					primaryBonus -= (int) pc.getBonusDueToType("COMBAT", "TOHIT-PRIMARY", "NOTRANGED");
				}
			}
		}

		/* If the character normally can't wield this weapon 1-handed,
		 but for some reason they can (e.g. Monkey Grip) then check
		 for TOHIT modifiers */

		if (eq.getLocation() == EquipmentLocation.EQUIPPED_PRIMARY
			|| eq.getLocation() == EquipmentLocation.EQUIPPED_SECONDARY
			|| eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS)
		{
			// TODO Fix this
			//			if (eq.isWeaponOneHanded(pc, wp, false) != eq.isWeaponOneHanded(pc, wp, true))
			//			{
			//				baseBonus += (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "TOHITOVERSIZE");
			//				baseBonus += getWeaponProfTypeBonuses(pc, eq, "TOHITOVERSIZE", WPTYPEBONUS_PC);
			//			}
		}

		if (hitMode == HITMODE_TWPHITH || hitMode == HITMODE_TWPHITL)
		{
			baseBonus += primaryBonus;
		}

		if (hitMode == HITMODE_TWOHIT || hitMode == HITMODE_OHHIT || hitMode == HITMODE_TWFOHL)
		{
			baseBonus += secondaryBonus;
		}

		/* An equipped buckler gives an additional -1 penalty to weapons used
		 off hand or a weapon used two handed. */
		/********************************************
		 * This is now all done via
		 * BONUS:COMBAT|TOHIT|-1|PREMULT:1,[PREEQUIPBOTH:1,TYPE=Melee],[PREEQUIPSECONDARY:1,TYPE=Melee] on the item(s)
		 * Byngl - Nov 20, 2005
		 if (eq.isMelee() &&
		 (hitMode == HITMODE_THHIT  ||
		 hitMode == HITMODE_TWOHIT ||
		 hitMode == HITMODE_OHHIT))
		 {
		 for (Iterator e = pc.getEquipmentOfType("buckler", 1).iterator(); e.hasNext();)
		 {
		 baseBonus--;
		 break;
		 }
		 }
		 */
		// Get BONUS:COMBAT|TOHIT.abc|x
		// Where abc: Ranged, Melee, Slashing, etc
		for (String type : eq.typeList())
		{
			// Finesseable is a special case that
			// is Handled elsewhere
			if (type.equalsIgnoreCase("Finesseable"))
			{
				continue;
			}
			//Prevents weapon from getting both melee & ranged bonuses
			if ((range > -1 && type.equalsIgnoreCase("MELEE")) || (range == -1 && eq.isMelee()
				&& (type.equalsIgnoreCase("THROWN") || type.equalsIgnoreCase("RANGED"))))
			{
				continue;
			}

			baseBonus += (int) pc.getTotalBonusTo("TOHIT", "TYPE." + type);
			baseBonus += (int) pc.getTotalBonusTo("COMBAT", "TOHIT." + type);
		}

		if (range == -1 && eq.isMelee() && eq.isFinessable(pc))
		{
			baseBonus += (int) pc.getTotalBonusTo("COMBAT", "TOHIT.Finesseable");
		}

		// 3.0 Syntax
		// This fixes Weapon Finesse for thrown weapons
		// BONUS:WEAPONPROF=abc|TOHIT|DEX|TYPE.NotRanged
		//
		// Dagger yields following:
		// WEAPONPROF=abc.TOHIT:NOTRANGED
		//
		if ((ref != null) && eq.isRanged())
		{
			baseBonus -= (int) pc.getBonusDueToType("WEAPONPROF=" + profKey, "TOHIT", "NOTRANGED");
			baseBonus -= getWeaponProfTypeBonuses(pc, eq, "TOHIT.NOTRANGED", WPTYPEBONUS_PC);
		}

		CharacterDisplay display = pc.getDisplay();
		if (!eq.isNatural() && ((ref == null) || !display.hasWeaponProf(prof)))
		{
			baseBonus += display.getNonProficiencyPenalty();
		}

		baseBonus += (int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "TOHIT");
		baseBonus += getWeaponProfTypeBonuses(pc, eq, "TOHIT", WPTYPEBONUS_PC);

		if (range > -1)
		{
			int rangeSize = getRangeList(eq, true, pc).size();
			int shortRange = SettingsHandler.getGame().getShortRangeDistance();

			/* range here is an index that represents a number of range
			 * increments, the actual distance is held in this range */

			if (range < rangeSize)
			{
				int thisRange = Integer.parseInt(getRangeList(eq, true, pc).get(range));

				// at short range, add SHORTRANGE bonus
				if (thisRange <= shortRange)
				{
					baseBonus += (int) pc.getTotalBonusTo("COMBAT", "TOHIT-SHORTRANGE");
					baseBonus += (int) pc.getTotalBonusTo("TOHIT", "SHORTRANGE");
					baseBonus += (int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "TOHIT-SHORTRANGE");
					baseBonus += getWeaponProfTypeBonuses(pc, eq, "TOHIT-SHORTRANGE", WPTYPEBONUS_PC);
					baseBonus += (int) eq.bonusTo(pc, "WEAPON", "TOHIT-SHORTRANGE", true);
				}
				// Long Range To-Hit Modifier
				int defaultRange = Integer.parseInt(EqToken.getRange(pc, eq).toString());
				int rangePenalty = SettingsHandler.getGame().getRangePenalty();
				rangePenalty += pc.getTotalBonusTo("COMBAT", "RANGEPENALTY");

				baseBonus += rangePenalty * (int) Math.max(Math.ceil(((float) thisRange / defaultRange)) - 1, 0);
			}
		}

		//Ammunition & Contents Modifier
		Equipment containedEq = null;
		if (content > -1)
		{
			if (content < eq.getContainedEquipmentCount())
			{
				containedEq = eq.getContainedEquipment(content);
				baseBonus += containedEq.getBonusToHit(pc, true);
			}
		}

		Equipment ammoUser = getAmmoUser(pc, eq, ammo);
		if (ammoUser != null)
		{
			baseBonus += ammoUser.getBonusToHit(pc, true);
		}

		// do NOT include the size bonus/penalty since
		// it is call in pc.getTotalBonusTo()
		// include players TOHIT bonuses
		baseBonus += (int) pc.getTotalBonusTo("TOHIT", "TOHIT");
		baseBonus += (int) pc.getTotalBonusTo("COMBAT", "TOHIT");

		// subtract Armor and Shield non-proficiency
		baseBonus += modFromArmorOnWeaponRolls(pc);

		// include bonuses from Item itself
		baseBonus += eq.getBonusToHit(pc, true);
		// If not using ammo stacking, correct for stacked enhancement bonus
		if (!Globals.checkRule(RuleConstants.AMMOSTACKSWITHWEAPON))
		{
			baseBonus += calcAmmoEqCorrection("WEAPON.TOHIT.ENHANCEMENT", eq, containedEq, ammoUser);
		}

		// BONUS:COMBAT|ATTACKS|#
		// represent extra attacks at BaB
		// such as from a weapon of 'Speed'
		int extra_attacks = (int) eq.bonusTo(pc, "WEAPON", "ATTACKS", true);

		// or possibly the "Haste" spell cast on PC
		extra_attacks += (int) pc.getTotalBonusTo("COMBAT", "ATTACKS");

		String babProgression;

		/* The range == -1 here deals with the case where the weapon is both
		 ranged and melee (e.g. a dagger).  The range == -1 indicates that
		 we want the melee progression */

		if (eq.isMelee() && range == -1)
		{
			babProgression = melee;
		}
		else if (eq.isRanged())
		{
			babProgression = ranged;
		}
		else
		{
			/* A weapon must either be ranged or melee.  If it's not, trap
			 it here */
			return "???";
		}

		StringTokenizer bTok = new StringTokenizer(babProgression, "/+");
		String attack = Delta.toString(Integer.parseInt(bTok.nextToken()));
		StringBuilder newAttack = new StringBuilder();

		for (int i = extra_attacks; i > 0; i--)
		{
			newAttack.append(attack).append('/');
		}

		boolean progress = eq.getSafe(ObjectKey.ATTACKS_PROGRESS);
		int bonusProgress = (int) eq.bonusTo(pc, "WEAPON", "ATTACKSPROGRESS", true);
		if (bonusProgress != 0)
		{
			progress = bonusProgress > 0;
		}
		if (progress)
		{

			/* For normal weapons, we need to append the original
			 * attack progression which was derived from the BAB to
			 * the end of the extra attacks */

			newAttack.append(babProgression);
		}
		else
		{

			/* This is for Natural weapons and any other weapon
			 * which has its attack progression turned off.  The
			 * attack progression should consist of the full number
			 * of attacks at the maximum tohit i.e. without
			 * appending the attacks from the normal attack
			 * progression */

			newAttack.append(attack);
		}

		StringTokenizer aTok = new StringTokenizer(newAttack.toString(), "/+");

		// When attackNum is > 0, the code is looking for a single attack
		// from the sequence.  This section of code down to
		// if (buildNewAttackSequence) builds a new aTok which contains
		// only the single attack we're looking for.

		int selectAttack = attackNum;
		String singleAttack = "";
		boolean buildNewAttackSequence = false;

		while (aTok.hasMoreTokens() && (selectAttack >= 0))
		{
			singleAttack = aTok.nextToken();
			selectAttack--;
			buildNewAttackSequence = true;
		}

		if (buildNewAttackSequence)
		{
			aTok = new StringTokenizer(singleAttack, "/+");
		}

		int secondariesToadd = 1 + (int) pc.getTotalBonusTo("COMBAT", "ATTACKS-SECONDARY");

		/*
		 * The data team wishes to keep the old syntax for secondary attacks.
		 * The docs have been updated to reflect this.  The new syntax (with
		 * the hyphen, see above) is not used in the repository.  This comment
		 * is here so that the old syntax will not be deprecated or removed in
		 * the future.
		 */
		secondariesToadd += (int) pc.getTotalBonusTo("COMBAT", "SECONDARYATTACKS");

		if (!display.hasPrimaryWeapons() && (hitMode == HITMODE_TOTALHIT))
		{
			secondariesToadd = 100;
		}

		// Whether to construct a string for secondary attacks.  This is only
		// needed for double weapons because single weapons in the off hand
		// are processed on their own as secondary weapons.  Additionally, We
		// should only construct a secondary attack string if we are not
		// looking for a single attack from the sequence (attackNum < 0)
		boolean doDouble = isDouble && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS) && attackNum < 0;

		// If the weapon is being considered as a secondary weapon, then we
		// shouldn't add the full attack progression as a secondary weapon only
		// gets one attack (plus any added by feats, etc. see extra attacks
		// above) i.e. we may need to break out of the loop while aTok has more
		// tokens
		boolean considerEarlyExit = !isDouble && (hitMode == HITMODE_TWOHIT || display.isSecondaryWeapon(eq));

		int toHit;
		int secondariesAdded = 0;

		StringBuilder primaryAttack = new StringBuilder(20);
		StringBuilder secondaryAttack = new StringBuilder(20);
		StringBuilder totalAttack = new StringBuilder();

		while (aTok.hasMoreTokens())
		{
			if (primaryAttack.length() > 0)
			{
				primaryAttack.append('/');
			}

			toHit = Integer.parseInt(aTok.nextToken()) + baseBonus;

			primaryAttack.append(Delta.toString(toHit));

			if (doDouble && secondariesAdded < secondariesToadd)
			{
				if (secondaryAttack.length() > 0)
				{
					secondaryAttack.append('/');
				}
				secondaryAttack.append(Delta.toString(toHit));
			}

			// Just in case we are looping forever
			if (++secondariesAdded > 100)
			{
				break;
			}

			if (considerEarlyExit && secondariesAdded >= secondariesToadd)
			{
				break;
			}
		}

		totalAttack.append(primaryAttack);

		if (secondaryAttack.length() != 0 && (hitMode == HITMODE_TOTALHIT || hitMode == HITMODE_TWOHIT))
		{
			totalAttack.append(";").append(secondaryAttack);
		}

		return totalAttack.toString();
	}

	private static int modFromArmorOnWeaponRolls(PlayerCharacter pc)
	{
		int bonus = 0;

		/*
		 * Equipped some armor that we're not proficient in? acCheck penalty to
		 * attack rolls
		 */
		for (Equipment eq : pc.getEquipmentOfType("Armor", 1))
		{
			if ((eq != null) && (!pc.isProficientWith(eq)))
			{
				bonus += EqToken.getAcCheckTokenInt(pc, eq);
			}
		}

		/*
		 * Equipped a shield that we're not proficient in? acCheck penalty to
		 * attack rolls
		 */
		for (Equipment eq : pc.getEquipmentOfType("Shield", 1))
		{
			if ((eq != null) && (!pc.isProficientWith(eq)))
			{
				bonus += EqToken.getAcCheckTokenInt(pc, eq);
			}
		}

		return bonus;
	}

	/**
	 * Calculate the correction required to cancel out all enhancement bonuses
	 * other than the highest one. This is because in some game modes, the
	 * enhancement bonuses for ammo does not stack with that of the weapon.
	 * Normally the key would be WEAPON.TOHIT.ENHANCEMENT or
	 * WEAPON.DAMAGE.ENHANCEMENT
	 *
	 * @param aKey The bonus to get the correction for.
	 * @param weapon The weapon that is holding the ammo or contents.
	 * @param contents The contents fo the weapon
	 * @param ammo The ammo being used in the weapon.
	 * @return The correction from the total enhancement bonus to the highest one.
	 */
	private static int calcAmmoEqCorrection(String aKey, Equipment weapon, Equipment contents, Equipment ammo)
	{
		float maxEnhancement = 0;
		float totalEnhancement;
		float bonusVal;
		String bonus;

		if (weapon == null)
		{
			return 0;
		}

		bonus = weapon.getBonusMap().get(aKey);
		if (bonus != null)
		{
			maxEnhancement = Float.parseFloat(bonus);
		}
		totalEnhancement = maxEnhancement;

		if (contents != null)
		{
			bonus = contents.getBonusMap().get(aKey);
			if (bonus != null)
			{
				bonusVal = Float.parseFloat(bonus);
				totalEnhancement += bonusVal;
				if (bonusVal > maxEnhancement)
				{
					maxEnhancement = bonusVal;
				}
			}
		}

		if (ammo != null)
		{
			bonus = ammo.getBonusMap().get(aKey);
			if (bonus != null)
			{
				bonusVal = Float.parseFloat(bonus);
				totalEnhancement += bonusVal;
				if (bonusVal > maxEnhancement)
				{
					maxEnhancement = bonusVal;
				}
			}
		}

		return (int) (maxEnhancement - totalEnhancement);
	}

	private static String getDamage(PlayerCharacter pc, Equipment eq, int range, int content, int ammo,
		boolean bonusOnly, int hands, int damageMode, boolean base)
	{
		boolean isDouble = (eq.isDouble() && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS));
		boolean isDoubleSplit = (eq.isType("Head1") || eq.isType("Head2"));

		if (eq.isMelee() && (eq.isWeaponTwoHanded(pc)))
		{
			if (!isDouble && !isDoubleSplit && (damageMode != DAMAGEMODE_NORMAL) && (damageMode != DAMAGEMODE_TWOHANDS)
				&& (damageMode != DAMAGEMODE_DOUBLE))
			{
				return LanguageBundle.getString("SettingsHandler.not.applicable");
			}
		}

		if (eq.isMelee() && eq.isWeaponOutsizedForPC(pc) && !eq.isNatural())
		{
			return LanguageBundle.getString("SettingsHandler.not.applicable");
		}

		if (eq.isWeaponLightForPC(pc) && (hands == 2))
		{
			// if wielding a 'Light' weapon two handed
			// treat as if wielding 1 handed for damage bonus
			hands = 1;
		}

		String profName = getProfName(eq);

		String damString = getEqDamage(pc, eq);
		int meleeDamageStatBonus = (int) pc.getDisplay().getStatBonusTo("COMBAT", "DAMAGE.MELEE");
		// TODO: remove this old syntax
		meleeDamageStatBonus += (int) pc.getDisplay().getStatBonusTo("DAMAGE", "TYPE.MELEE");
		meleeDamageStatBonus += (int) pc.getTotalBonusTo("WEAPONPROF=" + profName, "STATDAMAGE");
		double meleeDamageMult = pc.getTotalBonusTo("COMBAT", "DAMAGEMULT:" + hands);
		meleeDamageMult += pc.getTotalBonusTo("WEAPONPROF=" + profName, "DAMAGEMULT:" + hands);
		meleeDamageMult += BonusCalc.charBonusTo(eq, "WEAPON", "DAMAGEMULT:" + hands, pc);

		int bonus = 0;
		int weaponProfBonus = 0;
		int eqbonus = 0;
		int totalBonus = 0;

		damString = getMonkUnarmed(pc, eq, damString);

		if (!base)
		{
			int index;
			for (index = 0; index < damString.length(); ++index)
			{
				if ((damString.charAt(index) == '+') || (damString.charAt(index) == '-'))
				{
					totalBonus = Delta.decode(damString.substring(index));
					break;
				}
			}

			eqbonus = getEqBonus(pc, eq, content, ammo);
			bonus = getGeneralBonus(pc, eq, range, meleeDamageStatBonus, meleeDamageMult);
			weaponProfBonus = getWeaponProfBonus(pc, eq, range);

			totalBonus += (bonus + weaponProfBonus + eqbonus);
			damString = damString.substring(0, index);
		}

		StringBuilder sb = new StringBuilder();
		if (!"0d0".equalsIgnoreCase(damString))
		{
			if (!bonusOnly)
			{
				sb.append(damString);
			}

			if ((totalBonus != 0) || bonusOnly)
			{
				sb.append(Delta.toString(totalBonus));
			}
		}
		else
		{
			sb.append('0');
		}

		// Handle Double weapons
		if ((damageMode == DAMAGEMODE_DOUBLE) && (eq.getLocation() == EquipmentLocation.EQUIPPED_TWO_HANDS))
		{
			// This is the 'Off-Hand' portion of the double weapon
			hands = 0;
			meleeDamageMult = pc.getTotalBonusTo("COMBAT", "DAMAGEMULT:" + hands);
			meleeDamageMult += pc.getTotalBonusTo("WEAPONPROF=" + profName, "DAMAGEMULT:" + hands);
			meleeDamageMult += BonusCalc.charBonusTo(eq, "WEAPON", "DAMAGEMULT:" + hands, pc);
			totalBonus -= eqbonus;
			/*
			 * eq.getBonusToDamage(false) returns the eq bonus for
			 * the secondary head
			 */
			eqbonus = eq.getBonusToDamage(pc, false);
			if (!eq.getAltDamage(pc).isEmpty())
			{
				totalBonus = 0;
				damString = eq.getAltDamage(pc);
				if (damString.lastIndexOf('-') >= 0)
				{
					totalBonus = Integer.parseInt(damString.substring(damString.lastIndexOf('-')));
					damString = damString.substring(0, damString.lastIndexOf('-'));
				}
				else if (damString.lastIndexOf('+') >= 0)
				{
					totalBonus = Integer.parseInt(damString.substring(damString.lastIndexOf('+') + 1));
					damString = damString.substring(0, damString.lastIndexOf('+'));
				}
			}
			else
			{
				weaponProfBonus = 0;
				bonus = 0;
			}

			if (meleeDamageStatBonus > 0)
			{
				// getTotalBonusTo() includes the Stat Bonuses
				// so have to remove them before we can compute
				// the hands multiplier
				bonus -= meleeDamageStatBonus;

				// Off-hand, OneHanded and TwoHanded wield
				// have a Stat Bonus damage multiplier
				bonus += (meleeDamageMult * meleeDamageStatBonus);
			}

			totalBonus += bonus + weaponProfBonus + eqbonus;

			sb.append('/');
			if (!"0d0".equalsIgnoreCase(damString))
			{
				if (!bonusOnly)
				{
					sb.append(damString);
				}
				if (totalBonus != 0 || bonusOnly)
				{
					sb.append(Delta.toString(totalBonus));
				}
			}
			else
			{
				sb.append('0');
			}
		}
		return sb.toString();
	}

	private static int getGeneralBonus(PlayerCharacter pc, Equipment eq, int range, int meleeDamageStatBonus,
		double meleeDamageMult)
	{
		int bonus = 0;
		for (String type : eq.typeList())
		{
			//Makes sure that thrown weapons only get the right bonus at the right time
			if ((range > -1 && type.equalsIgnoreCase("MELEE"))
				|| (range == -1 && (type.equalsIgnoreCase("THROWN") || type.equalsIgnoreCase("RANGED"))))
			{
				continue;
			}
			bonus += (int) pc.getTotalBonusTo("COMBAT", "DAMAGE." + type);
			// TODO: remove this old syntax
			bonus += (int) pc.getTotalBonusTo("DAMAGE", "TYPE." + type);
		}
		bonus += (int) pc.getTotalBonusTo("WEAPONPROF=" + getProfName(eq), "STATDAMAGE");
		if (eq.isFinessable(pc) && !eq.isType("Finesseable"))
		{
			bonus += (int) pc.getTotalBonusTo("COMBAT", "DAMAGE.Finesseable");
		}
		if (eq.isMelee() && (meleeDamageStatBonus > 0))
		{
			// getTotalBonusTo() includes the Stat Bonuses
			// so have to remove them before we can compute
			// the hands multiplier
			bonus -= meleeDamageStatBonus;

			// Off-hand, OneHanded and TwoHanded wield
			// have a Stat Bonus damage multiplier
			bonus += (meleeDamageMult * meleeDamageStatBonus);
		}
		else if (eq.isThrown())
		{
			// Thrown weapons just get stat bonus
			// and its already been added in the
			// getTotalBonusTo(TYPE) above
		}
		// If at short range, add SHORTRANGE bonus
		if (range > -1)
		{
			int rangeSize = getRangeList(eq, true, pc).size();

			if ((range < rangeSize) && (Integer.parseInt(getRangeList(eq, true, pc).get(range)) <= SettingsHandler
				.getGame().getShortRangeDistance()))
			{
				bonus += (int) eq.bonusTo(pc, "WEAPON", "DAMAGE-SHORTRANGE", true);
				bonus += (int) pc.getTotalBonusTo("DAMAGE", "SHORTRANGE");
				bonus += (int) pc.getTotalBonusTo("COMBAT", "DAMAGE-SHORTRANGE");
			}
		}
		return bonus;
	}

	private static int getWeaponProfBonus(PlayerCharacter pc, Equipment eq, int range)
	{
		String profKey = getProfName(eq);
		int weaponProfBonus = (int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "DAMAGE")
			+ getWeaponProfTypeBonuses(pc, eq, "DAMAGE", WPTYPEBONUS_PC);

		if (eq.isRanged())
		{
			weaponProfBonus -= ((int) pc.getBonusDueToType("WEAPONPROF=" + profKey.toUpperCase(), "DAMAGE", "NOTRANGED")
				+ getWeaponProfTypeBonuses(pc, eq, "DAMAGE.NOTRANGED", WPTYPEBONUS_PC));
		}

		// If at short range, add SHORTRANGE bonus
		if (range > -1)
		{
			int rangeSize = getRangeList(eq, true, pc).size();

			if ((range < rangeSize) && (Integer.parseInt(getRangeList(eq, true, pc).get(range)) <= SettingsHandler
				.getGame().getShortRangeDistance()))
			{
				weaponProfBonus += ((int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "DAMAGE-SHORTRANGE")
					+ getWeaponProfTypeBonuses(pc, eq, "DAMAGE-SHORTRANGE", WPTYPEBONUS_PC));
			}
		}

		return weaponProfBonus;
	}

	private static int getEqBonus(PlayerCharacter pc, Equipment eq, int content, int ammo)
	{
		int eqbonus = eq.getBonusToDamage(pc, true);

		//Ammunition & Contents Modifier
		Equipment containedEq = null;
		if (content > -1)
		{
			if (content < eq.getContainedEquipmentCount())
			{
				containedEq = eq.getContainedEquipment(content);
				eqbonus += containedEq.getBonusToDamage(pc, true);
			}
		}

		Equipment ammoUser = getAmmoUser(pc, eq, ammo);

		if (ammoUser != null)
		{
			eqbonus += ammoUser.getBonusToDamage(pc, true);
		}

		// If not using ammo stacking, correct for stacked enhancement bonus
		if (!Globals.checkRule(RuleConstants.AMMOSTACKSWITHWEAPON))
		{
			eqbonus += calcAmmoEqCorrection("WEAPON.DAMAGE.ENHANCEMENT", eq, containedEq, ammoUser);
		}

		return eqbonus;
	}

	private static String getMonkUnarmed(PlayerCharacter pc, Equipment eq, String damString)
	{
		if (eq.isMonk() && eq.isUnarmed())
		{
			int eqSize = pc.getDisplay().getRace().getSafe(FormulaKey.SIZE).resolve(pc, "").intValue();
			int iMod = pc.sizeInt();

			/* This modifies damage (by size) from the default when the race is
			 * not the default size and the character is the default size for
			 * their race */
			boolean applySize = (eqSize == iMod);
			String uDamString = UnarmedDamageDisplay.getUnarmedDamageString(pc, false, applySize);

			StringTokenizer bTok = new StringTokenizer(damString, " d+-", false);
			bTok.nextToken();
			String b1String = bTok.nextToken();

			StringTokenizer cTok = new StringTokenizer(uDamString, " d+-", false);
			cTok.nextToken();
			String c1String = cTok.nextToken();

			if (Integer.parseInt(b1String) < Integer.parseInt(c1String))
			{
				damString = uDamString;
			}

			/*
			 * This modifies damage by size when the character is a different size
			 * than the race.  It also modifies it by applying any Bonuses to damage
			 * size.
			 */

			iMod += (int) pc.getTotalBonusTo("WEAPONPROF=Unarmed Strike", "DAMAGESIZE");
			iMod += (int) pc.getTotalBonusTo("COMBAT", "DAMAGESIZE");

			/* If not applying the race size modifier, then damString will
			 * represent the damage as if this Character were the default
			 * size.  Set eqSize to adjust from damage for the default size,
			 * not the race's actual size.
			 */
			if (!applySize)
			{
				final SizeAdjustment defAdj = SizeUtilities.getDefaultSizeAdjustment();
				if (defAdj != null)
				{
					eqSize = defAdj.get(IntegerKey.SIZEORDER);
				}
			}

			damString = Globals.adjustDamage(damString, iMod - eqSize);
		}
		return damString;
	}

	private static String getEqDamage(PlayerCharacter pc, Equipment eq)
	{
		String retString = eq.getDamage(pc);

		if (pc == null)
		{
			return retString;
		}

		String profKey = getProfName(eq);
		if (eq.isNatural())
		{
			int eqSize = pc.racialSizeInt();
			int iMod = pc.sizeInt();
			iMod += (int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "DAMAGESIZE");
			iMod += (int) pc.getTotalBonusTo("COMBAT", "DAMAGESIZE");
			retString = Globals.adjustDamage(retString, iMod - eqSize);
		}
		else
		{
			int eqSize = eq.sizeInt();
			int iMod = eqSize;
			iMod += (int) pc.getTotalBonusTo("WEAPONPROF=" + profKey, "DAMAGESIZE");
			iMod += (int) pc.getTotalBonusTo("COMBAT", "DAMAGESIZE");
			retString = Globals.adjustDamage(retString, iMod - eqSize);
		}

		return retString;
	}

	private static Equipment getAmmoUser(PlayerCharacter pc, Equipment eq, int ammo)
	{
		int ammoCount = 0;

		if (ammo < 0)
		{
			return null;
		}
		String containerCapacity = eq.getContainerCapacityString();

		for (Equipment equip : pc.getEquipmentListInOutputOrder())
		{
			for (String type : equip.typeList())
			{
				if (containerCapacity.contains(type))
				{
					++ammoCount;

					break;
				}
			}
			if (ammoCount == (ammo + 1))
			{
				return equip;
			}
		}
		return null;
	}

	private static int getWeaponProfTypeBonuses(PlayerCharacter pc, Equipment eq, String bonusType, int index)
	{
		int bonus = 0;
		boolean hasBoth = (eq.isRanged() && eq.isMelee());
		CDOMSingleRef<WeaponProf> ref = eq.get(ObjectKey.WEAPON_PROF);
		if (ref == null)
		{
			return 0;
		}
		WeaponProf wp = ref.get();

		StringTokenizer aTok = new StringTokenizer(wp.getType(), ".");

		while (aTok.hasMoreTokens())
		{
			String tString = aTok.nextToken();

			if (!hasBoth || !"RANGED".equalsIgnoreCase(tString))
			{
				switch (index)
				{
					case WPTYPEBONUS_PC:
						bonus += (int) pc.getTotalBonusTo("WEAPONPROF=TYPE." + tString, bonusType);
						break;

					case WPTYPEBONUS_EQ:
						bonus += (int) BonusCalc.charBonusTo(eq, "WEAPONPROF=TYPE." + tString, bonusType, pc);
						break;

					case WPTYPEBONUS_FEAT:
						bonus += (int) pc.getFeatBonusTo("WEAPONPROF=TYPE." + tString, bonusType);
						break;

					case WPTYPEBONUS_TEMPLATE:
						bonus += (int) pc.getTemplateBonusTo("WEAPONPROF=TYPE." + tString, bonusType);
						break;

					default:
						Logging.errorPrint(
							"In getWeaponProfTypeBonuses there is an unhandled case in a switch (the value is " + index
								+ '.');
						break;
				}
			}
		}

		return bonus;
	}

	private static String weaponCategories(Equipment eq)
	{
		StringBuilder wc = new StringBuilder(10);
		List<Type> categories = SettingsHandler.getGame().getWeaponCategories();

		for (Type type : categories)
		{
			if (eq.isType(type, true))
			{
				if (wc.length() != 0)
				{
					wc.append('/');
				}

				wc.append(type);
			}
		}

		if (wc.length() == 0)
		{
			wc.append("Non-Standard");
		}

		return wc.toString();
	}

	private static String weaponTypes(Equipment eq, boolean primary)
	{
		StringBuilder wt = new StringBuilder(10);
		StringTokenizer aTok = new StringTokenizer(SettingsHandler.getGame().getWeaponTypes(), "|", false);

		while (aTok.countTokens() >= 2)
		{
			String type = aTok.nextToken();
			String abbrev = aTok.nextToken();

			if (eq.isType(type, primary))
			{
				wt.append(abbrev);
			}
		}

		return wt.toString();
	}

	/**
	 * Get the ranged attack string for this {@code pc}
	 *
	 * @param pc The character that this ranged attack string is for
	 * @return   The ranged attack string affected only by BAB
	 */
	private static String getRangedAttackString(PlayerCharacter pc)
	{
		return pc.getAttackString(AttackType.RANGED, 0, 0);
	}

	/**
	 * Get the ranged attack string for this {@code pc}.  Use
	 * {@code bonus} to affect the size of attacks e.g.  +9/+4 with
	 * bonus 2 becomes +11/+6.  Use {@code BABbonus} to affect the
	 * size and number of attacks e.g.  +9/+4 with BABBonus 2 becomes
	 * +11/+6/+1.
	 *
	 * @param pc    The character that this ranged attack string is for
	 * @param bonus An increase to be applied to each number in the attack
	 *              string.
	 * @param BABBonus A bonus Which also affects the number of attacks in
	 *              the returned attackString.
	 * @return      The ranged attack string with number and size of attacks
	 *              affected by BAB and bonus
	 */
	private static String getRangedAttackString(PlayerCharacter pc, int bonus, int BABBonus)
	{
		return pc.getAttackString(AttackType.RANGED, bonus, BABBonus);
	}

	/**
	 * Get the melee attack string for this {@code pc}
	 *
	 * @param pc The character that this melee attack string is for
	 * @return   The melee attack string affected only by BAB
	 */
	private static String getMeleeAttackString(PlayerCharacter pc)
	{
		return pc.getAttackString(AttackType.MELEE, 0, 0);
	}

	/**
	 * Get the melee attack string for this {@code pc}.  Use
	 * {@code bonus} to affect the size of attacks e.g.  +9/+4 with
	 * bonus 2 becomes +11/+6.  Use {@code BABbonus} to affect the
	 * size and number of attacks e.g.  +9/+4 with BABBonus 2 becomes
	 * +11/+6/+1.
	 *
	 * @param pc    The character that this melee attack string is for
	 * @param bonus An increase to be applied to each number in the attack
	 *              string.
	 * @param BABBonus A bonus Which also affects the number of attacks in
	 *              the returned attackString.
	 * @return      The melee attack string with number and size of attacks
	 *              affected by BAB and bonus
	 */
	private static String getMeleeAttackString(PlayerCharacter pc, int bonus, int BABBonus)
	{
		return pc.getAttackString(AttackType.MELEE, bonus, BABBonus);
	}

	/**
	 * Get the unarmed attack string for this {@code pc}
	 *
	 * @param pc The character that this unarmed attack string is for
	 * @return   The unarmed attack string affected only by BAB
	 */
	private static String getUnarmedAttackString(PlayerCharacter pc)
	{
		return pc.getAttackString(AttackType.UNARMED, 0, 0);
	}

	/**
	 * Get the unarmed attack string for this {@code pc}.  Use
	 * {@code bonus} to affect the size of attacks e.g.  +9/+4 with
	 * bonus 2 becomes +11/+6.  Use {@code BABbonus} to affect the
	 * size and number of attacks e.g.  +9/+4 with BABBonus 2 becomes
	 * +11/+6/+1.
	 *
	 * @param pc    The character that this unarmed attack string is for
	 * @param bonus An increase to be applied to each number in the attack
	 *              string.
	 * @param BABBonus A bonus Which also affects the number of attacks in
	 *              the returned attackString.
	 * @return      The unarmed attack string with number and size of attacks
	 *              affected by BAB and bonus
	 */
	private static String getUnarmedAttackString(PlayerCharacter pc, int bonus, int BABBonus)
	{
		return pc.getAttackString(AttackType.UNARMED, bonus, BABBonus);
	}

	/**
	 * If the equipment has a type beyond natural then we need a
	 * seperator
	 * @param eq
	 * @return true if we need a sepearator
	 */
	private static boolean appendSeperator(Equipment eq)
	{
		return eq.isType("Natural") && (eq.isType("Both") || eq.isType("Melee") || eq.isType("Ranged"));
	}

	/**
	 * If none of the four types are true then it's non standard
	 * @param eq
	 * @return true if non standard
	 */
	private static boolean isNonStandard(Equipment eq)
	{
		return !(eq.isType("Natural") || eq.isType("Both") || eq.isType("Melee") || eq.isType("Ranged"));
	}

	/**
	 * Gets the range list of the Equipment object, adding the 30' range, if not present and required
	 *
	 * @param addShortRange boolean
	 * @param aPC
	 * @return The range list
	 */
	private static List<String> getRangeList(Equipment eq, boolean addShortRange, final PlayerCharacter aPC)
	{
		final List<String> aList = new ArrayList<>();
		final int baseRange = EqToken.getRange(aPC, eq);
		int aRange = baseRange;
		int maxIncrements = 0;

		if (eq.isRanged())
		{
			if (eq.isThrown())
			{
				maxIncrements = 5;
			}
			else
			{
				maxIncrements = 10;
			}
		}

		for (int numIncrements = 0; numIncrements < maxIncrements; ++numIncrements)
		{
			if (aRange == SettingsHandler.getGame().getShortRangeDistance())
			{
				addShortRange = false;
			}

			if ((aRange > SettingsHandler.getGame().getShortRangeDistance()) && addShortRange)
			{
				aList.add(Integer.toString(SettingsHandler.getGame().getShortRangeDistance()));
				addShortRange = false;
			}

			aList.add(Integer.toString(aRange));
			aRange += baseRange;
		}

		return aList;
	}

	public static String getNewCritRangeString(PlayerCharacter pc, Equipment eq, String critRangeVar)
	{
		StringBuilder sb = new StringBuilder();
		boolean needSlash = false;
		for (EquipmentHead head : eq.getEquipmentHeads())
		{
			if (needSlash)
			{
				sb.append('/');
			}
			sb.append(getCritRangeHead(pc, head, critRangeVar));
			needSlash = true;
		}
		return sb.toString();
	}

	static StringBuilder getCritRangeHead(PlayerCharacter pc, EquipmentHead head, String critRangeVar)
	{
		StringBuilder sb = new StringBuilder();
		Integer range = (Integer) head.getLocalVariable(pc.getCharID(), critRangeVar);
		sb.append(range);
		if (range < 20)
		{
			sb.append("-20");
		}
		return sb;
	}
}
