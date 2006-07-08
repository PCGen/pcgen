/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import pcgen.core.*;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author djones4
 *
 */
public class NaturalattacksLst implements GlobalLstToken {

	public String getTokenName() {
		return "NATURALATTACKS";
	}

	public boolean parse(PObject obj, String value, int anInt) {
		// first entry is primary, others are secondary
		// lets try the format:
		// NATURALATTACKS:primary weapon name,num attacks,damage|secondary1 weapon
		// name,num attacks,damage|secondary2.....
		// damage will be of the form XdY+Z or XdY-Z
		List<Equipment> naturalWeapons = parseNaturalAttacks(obj, value);
		for ( Equipment weapon : naturalWeapons )
		{
			obj.addNaturalWeapon(weapon, anInt);
		}
		return true;
	}

	/**
	 * NATURAL WEAPONS CODE <p/>first natural weapon is primary, the rest are
	 * secondary; NATURALATTACKS:primary weapon name,weapon type,num
	 * attacks,damage|secondary1 weapon name,weapon type,num
	 * attacks,damage|secondary2 format is exactly as it would be in an equipment
	 * lst file Type is of the format Weapon.Natural.Melee.Bludgeoning number of
	 * attacks is the number of attacks with that weapon at BAB (for primary), or
	 * BAB - 5 (for secondary)
	 * @param obj
	 * @param aString
	 * @return List
	 */
	private static List<Equipment> parseNaturalAttacks(PObject obj, String aString) {
		// Currently, this isn't going to work with monk attacks
		// - their unarmed stuff won't be affected.
		String aSize = "M";

		if (obj instanceof PCTemplate) {
			aSize = ((PCTemplate) obj).getTemplateSize();
		} else if (obj instanceof Race) {
			aSize = ((Race) obj).getSize();
		}

		if (aSize == null) {
			aSize = "M";
		}

		int count = 1;
		boolean onlyOne = false;

		final StringTokenizer attackTok = new StringTokenizer(aString, "|");

		// Make a preliminary guess at whether this is an "only" attack
		if (attackTok.countTokens() == 1) {
			onlyOne = true;
		}

		// This is wrong as we need to replace old natural weapons
		// with "better" ones
		List<Equipment> naturalWeapons = new ArrayList<Equipment>();

		while (attackTok.hasMoreTokens()) {
			StringTokenizer aTok = new StringTokenizer(attackTok.nextToken(), ",");
			Equipment anEquip = createNaturalWeapon(aTok, aSize);

			if (anEquip != null) {
				if (count == 1) {
					anEquip.setModifiedName("Natural/Primary");
				} else {
					anEquip.setModifiedName("Natural/Secondary");
				}

				if (onlyOne && anEquip.isOnlyNaturalWeapon()) {
					anEquip.setOnlyNaturalWeapon(true);
				} else {
					anEquip.setOnlyNaturalWeapon(false);
				}

				anEquip.setOutputIndex(0);
				anEquip.setOutputSubindex(count);
				naturalWeapons.add(anEquip);
			}

			count++;
		}
		return naturalWeapons;
	}

	/**
	 * Create the Natural weapon equipment item aTok = primary weapon name,weapon
	 * type,num attacks,damage for Example:
	 * Tentacle,Weapon.Natural.Melee.Slashing,*4,1d6
	 * @param aTok
	 * @param aSize
	 * @return natural weapon
	 */
	private static Equipment createNaturalWeapon(StringTokenizer aTok, String aSize) {
		final String attackName = aTok.nextToken();

		if (attackName.equalsIgnoreCase(Constants.s_NONE)) { return null; }

		Equipment anEquip = new Equipment();
		final String profType = aTok.nextToken();

		anEquip.setName(attackName);
		anEquip.setTypeInfo(profType);
		anEquip.setWeight("0");
		anEquip.setSize(aSize, true);

		String numAttacks = aTok.nextToken();
		boolean attacksProgress = true;

		if ((numAttacks.length() > 0) && (numAttacks.charAt(0) == '*')) {
			numAttacks = numAttacks.substring(1);
			attacksProgress = false;
		}

		int bonusAttacks = 0;

		try {
			bonusAttacks = Integer.parseInt(numAttacks) - 1;
		} catch (NumberFormatException exc) {
			Logging.errorPrint("Non-numeric value for number of attacks: '" + numAttacks + "'");
		}

		if (bonusAttacks > 0) {
			anEquip.addBonusList("WEAPON|ATTACKS|" + bonusAttacks);
			anEquip.setOnlyNaturalWeapon(false);
		} else {
			anEquip.setOnlyNaturalWeapon(true);
		}

		anEquip.setDamage(aTok.nextToken());
		anEquip.setCritRange("1");
		anEquip.setCritMult(2);
		anEquip.setProfName(attackName);

		// sage_sam 02 Dec 2002 for Bug #586332
		// allow hands to be required to equip natural weapons
		int handsRequired = 0;

		if (aTok.hasMoreTokens()) {
			final String hString = aTok.nextToken();

			try {
				handsRequired = Integer.parseInt(hString);
			} catch (NumberFormatException exc) {
				Logging.errorPrint("Non-numeric value for hands required: '" + hString + "'");
			}
		}

		anEquip.setSlots(handsRequired);

		//these values need to be locked.
		anEquip.setQty(new Float(1));
		anEquip.setNumberCarried(new Float(1));
		anEquip.setAttacksProgress(attacksProgress);

		// Check if the proficiency needs created
		WeaponProf prof = Globals.getWeaponProfKeyed(attackName);

		if (prof == null) {
			prof = new WeaponProf();
			prof.setTypeInfo(profType);
			prof.setName(attackName);
			prof.setKeyName(attackName);
			Globals.addWeaponProf(prof);
		}

		return anEquip;
	}
}

