/*
 * EquipmentList.java
 * Copyright 2003 (C) Jonas Karlsson <jujutsunerd@users.sourceforge.net>
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
 * Created on November 30, 2003, 15:24
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Equipment-related lists and methods extracted from Globals.java. Will
 * probably try to disentangle modifierlist into it's own class later.
 *
 * @author Jonas Karlsson <jujutsunerd@users.sourceforge.net>
 * @version $Revision$
 */
public class EquipmentList {

	private static final int MODIFIERLISTSIZE = 230;

	/** this is determined by preferences */
	private static boolean autoGeneration = false;
	private static List<EquipmentModifier> modifierList = new ArrayList<EquipmentModifier>(MODIFIERLISTSIZE);
	private static TreeMap<String, Equipment> equipmentNameMap = new TreeMap<String, Equipment>();
	private static TreeMap<String, Equipment> equipmentKeyMap = new TreeMap<String, Equipment>();

	/**
	 * Private to ensure utility object can't be instantiated.
	 */
	private EquipmentList() {
		// Empty Constructor
	}

	/**
	 * Empty the equipment list.
	 */
	public static void clearEquipmentMap() {
		equipmentNameMap.clear();
		equipmentKeyMap.clear();
	}

	/**
	 * Empty the modifier list.
	 */
	protected static void clearModifierList() {
		modifierList = new ArrayList<EquipmentModifier>(MODIFIERLISTSIZE);
	}

	private static boolean isAutoGeneration() {
		return autoGeneration;
	}



	/**
	 * Return the modifier list.
	 *
	 * @return the list
	 */
	public static List<EquipmentModifier> getModifierList() {
		return modifierList;
	}

	/**
	 * Set whether magic equipment auto generation should be on.
	 *
	 * @param auto
	 *          true if it should be on
	 */
	public static void setAutoGeneration(final boolean auto) {
		autoGeneration = auto;
	}

	/**
	 * @param equipmentMap
	 *          The equipmentMap to set.
	 */
	public static void setEquipmentMap(final TreeMap<String, Equipment> equipmentMap) {
		EquipmentList.equipmentNameMap = equipmentMap;
	}

	/**
	 * Return the equipment that has the passed-in name.
	 *
	 * @param baseName
	 *          the name to return an equipment for
	 * @param aPC
	 *          TODO
	 * @return the Equipment matching the name
	 */
	public static Equipment getEquipmentFromName(final String baseName, final PlayerCharacter aPC) {
		final List<String> modList = new ArrayList<String>();
		final List<String> namList = new ArrayList<String>();
		final List<String> sizList = new ArrayList<String>();
		Equipment eq;
		String aName = baseName;
		int i = aName.indexOf('(');

		// Remove all modifiers from item name and
		// split into "size" and "non-size" lists
		if (i >= 0) {
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i + 1), "/)", false);

			while (aTok.hasMoreTokens()) {
				final String cString = aTok.nextToken();
				int iSize;

				for (iSize = 0; iSize <= (SettingsHandler.getGame().getSizeAdjustmentListSize() - 1); ++iSize) {
					if (cString.equalsIgnoreCase(SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize).getAbbreviation())) {
						break;
					}
				}

				if (iSize <= (SettingsHandler.getGame().getSizeAdjustmentListSize() - 1)) {
					sizList.add(cString);
				} else {
					if ("Mighty Composite".equalsIgnoreCase(cString)) {
						modList.add("Mighty");
						modList.add("Composite");
					} else {
						modList.add(cString);
					}
				}
			}

			aName = aName.substring(0, i).trim();
		}

		// Separate the "non-size" descriptors into 2 Lists.
		// One containing those descriptors whose names match a
		// modifier name, and the other containing those descriptors
		// which are not possibly modifiers
		// (because they're not in the modifier list).
		//
		if (i >= 0) {
			for (i = modList.size() - 1; i >= 0; --i) {
				final String namePart = modList.get(i);

				if (getModifierNamed(namePart) == null) {
					namList.add(0, namePart); // add to the start as otherwise the list
																		// will be reversed
					modList.remove(i);
				}
			}
		}

		// Look for magic (or mighty) bonuses
		//
		int[] bonuses = null;
		int bonusCount = 0;
		i = aName.indexOf('+');

		if (i >= 0) {
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i), "/", false);
			bonusCount = aTok.countTokens();
			bonuses = new int[bonusCount];

			int idx = 0;

			while (aTok.hasMoreTokens()) {
				final String cString = aTok.nextToken();
				bonuses[idx++] = Delta.decode(cString).intValue();
			}

			aName = aName.substring(0, i).trim();

			//
			// Mighty bows suffered a (much-needed) renaming
			// (Long|Short)bow +n (Mighty/Composite) --> (Long|Short)bow (+n
			// Mighty/Composite)
			// (Long|Short)bow +x/+n (Mighty/Composite) --> (Long|Short)bow +x (+n
			// Mighty/Composite)
			//
			// Look through the modifier list for MIGHTY,
			// if found add the bonus to the start of the modifier's name
			//
			if (bonusCount > 0) {
				for (int idx1 = 0; idx1 < namList.size(); ++idx1) {
					String aString = namList.get(idx1);

					if ("Mighty".equalsIgnoreCase(aString)) {
						aString = Delta.toString(bonuses[bonusCount - 1]) + " " + aString;
						namList.set(idx1, aString);
						bonusCount -= 1;
					}
				}
			}
		}

		//
		// aName : name of item minus all descriptors held in () as well as any
		// bonuses
		// namList : list of all descriptors which cannot be modifiers
		// modList : list of all descriptors which *might* be modifiers
		// sizList : list of all size descriptors
		//
		String omitString = "";
		String bonusString = "";

		while (true) {
			final String eqName = aName + bonusString;
			eq = findEquipment(eqName, null, namList, sizList, omitString);

			if (eq != null) {
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, sizList, omitString);

			if (eq != null) {
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, null, omitString);

			if (eq != null) {
				break;
			}

			// If only 1 size then include it in name
			if (sizList.size() == 1) {
				eq = findEquipment(eqName, sizList, namList, null, omitString);

				if (eq == null) {
					eq = findEquipment(eqName, namList, sizList, null, omitString);
				}

				if (eq != null) {
					sizList.clear();

					break;
				}
			}

			// If we haven't found it yet,
			// try stripping Thrown from name
			if (baseName.indexOf("Thrown") >= 0) {
				if (omitString.length() == 0) {
					omitString = "Thrown";

					continue;
				}
			}

			// Still haven't found it?
			// Try adding bonus to end of name
			if ((bonusCount > 0) && (bonuses != null)) {
				if (bonusString.length() == 0) {
					omitString = "";
					bonusString = " " + Delta.toString(bonuses[0]);

					continue;
				}
			}

			break;
		}

		if (eq != null) {
			boolean bModified = false;
			boolean bError = false;
			eq = eq.clone();

			//
			// Now attempt to add all the modifiers.
			//
			for (Iterator<String> e = modList.iterator(); e.hasNext();) {
				final String namePart = e.next();
				final EquipmentModifier eqMod = getQualifiedModifierNamed(namePart, eq);

				if (eqMod != null) {
					eq.addEqModifier(eqMod, true, aPC);

					if (eqMod.getAssignToAll() && eq.isDouble()) {
						eq.addEqModifier(eqMod, false, aPC);
						bModified = true;
					}
				} else {
					Logging.errorPrint("Could not find a qualified modifier named: " + namePart + " for " + eq.getName() + ":"
							+ eq.typeList());
					bError = true;
				}
			}

			// Found what appeared to be the base item,
			// but one of the modifiers is not qualified
			// to be attached to the item
			//
			if (bError) { return null; }

			if (sizList.size() != 0) {
				eq.resizeItem(aPC, sizList.get(0));
				bModified = true;

				if (sizList.size() > 1) {
					Logging.errorPrint("Too many sizes in item name, used only 1st of: " + sizList);
				}
			}

			if (bModified) {
				eq.nameItemFromModifiers(aPC);

				if (!addEquipment(eq)) {
					eq = getEquipmentNamed(eq.getName());
				}
			}
		}

		return eq;
	}

	/**
	 * Find an Equipment object matching passed-in key
	 *
	 * @param aKey
	 *          the key
	 * @return the Equipment object matching the key
	 */
	public static Equipment getEquipmentKeyed(final String aKey) {
		return equipmentKeyMap.get(aKey);
	}

	/**
	 * Find an Equipment object matching aKey (exclude custom items)
	 *
	 * @param aKey
	 *          the key
	 * @return the Equipment object matching the key
	 */
	public static Equipment getEquipmentKeyedNoCustom(final String aKey) {
		final Equipment eq = getEquipmentKeyed(aKey);
		if (eq==null) {
			return null;
		}
		if (eq.isType(Constants.s_CUSTOM)) {
			return null;
		}
		return eq;
	}

	/**
	 * Return the equipment list.
	 *
	 * @return the equipment list
	 */
	public static Collection<Equipment> getEquipmentList() {
		return equipmentNameMap.values();
	}

	/**
	 * Get Equipment List Iterator
	 * @return Equipment List Iterator
	 */
	public static Iterator<Map.Entry<String, Equipment>> getEquipmentListIterator() {
		return equipmentKeyMap.entrySet().iterator();
	}

	/**
	 * Return an equipment object matching the passed-in name.
	 *
	 * @param name
	 *          the name to match
	 * @return the Equipment object matching the name
	 */
	public static Equipment getEquipmentNamed(final String name) {
		return equipmentNameMap.get(name);
	}

	/**
	 * Return an equipment object from the passed-in list matching the passed-in
	 * name.
	 *
	 * @param name
	 *          the name to match
	 * @param aList
	 *          the list to search in
	 * @return the Equipment object matching the name
	 */
	public static Equipment getEquipmentNamed(final String name, final List<Equipment> aList) {
		for ( Equipment eq : aList )
		{
			if (eq.getName().equalsIgnoreCase(name)) { return eq; }
		}

		return null;
	}

	/**
	 * Get an Equipment object from the list matching the passed-in type(s).
	 *
	 * @param eqIterator
	 *          the equipment list to search in
	 * @param desiredTypes
	 *          a '.' separated list of types to match
	 * @param excludedTypes
	 *          a '.' separated list of types to NOT match
	 * @return the matching Equipment
	 */
	public static List<Equipment> getEquipmentOfType(final Iterator<Map.Entry<String, Equipment>> eqIterator, final String desiredTypes, final String excludedTypes)
	{
		final List<String> desiredTypeList = CoreUtility.split(desiredTypes, '.');
		final List<String> excludedTypeList = CoreUtility.split(excludedTypes, '.');
		final List<Equipment> typeList = new ArrayList<Equipment>(100);

		if (desiredTypeList.size() != 0)
		{
			for ( ; eqIterator.hasNext(); )
			{
				final Equipment eq = eqIterator.next().getValue();
				boolean addIt = true;

				//
				// Must have all of the types in the desired list
				//
				for ( String type : desiredTypeList )
				{
					if (!eq.isType(type)) {
						addIt = false;

						break;
					}
				}

				if (addIt && (excludedTypeList.size() != 0)) {
					//
					// Can't have any of the types on the excluded list
					//
					for ( String type : excludedTypeList )
					{
						if (eq.isType(type)) {
							addIt = false;

							break;
						}
					}
				}

				if (addIt) {
					typeList.add(eq);
				}
			}
		}

		return typeList;
	}

	/**
	 * Get a list of equipment of a particular type
	 *
	 * @param desiredTypes
	 * @param excludedTypes
	 * @return list of equipment of a particular type
	 */
	public static List<Equipment> getEquipmentOfType(final String desiredTypes, final String excludedTypes) {
		return getEquipmentOfType(equipmentNameMap.entrySet().iterator(), desiredTypes, excludedTypes);
	}

	/**
	 * Return a modifier matching the passed-in key.
	 *
	 * @param aKey
	 *          the key to match
	 * @return the Equipment object
	 */
	public static EquipmentModifier getModifierKeyed(final String aKey) {
		return Globals.searchPObjectList(getModifierList(), aKey);
	}

	/**
	 * Add a piece of equipment to the equipment list.
	 *
	 * @param aEq
	 *          the equipment to add
	 * @return true if adding succeeded
	 */
	public static boolean addEquipment(final Equipment aEq) {
		if (getEquipmentKeyed(aEq.getKeyName()) != null)
		{
			return false;
		}


		//
		// Make sure all the equipment types are present in the sorted list
		//
		Equipment.getEquipmentTypes().addAll(aEq.typeList());

		// Keep a reference to the equipment by name and key
		equipmentNameMap.put(aEq.getName(), aEq);
		equipmentKeyMap.put(aEq.getKeyName(), aEq);

		return true;
	}

	/**
	 * Automatically add equipment types as requested by user.
	 *          TODO
	 */
	public static void autoGenerateEquipment() {
		setAutoGeneration(true);

		autogenerateRacialEquipment();

		autogenerateMasterWorkEquipment();

		autogenerateMagicEquipment();

		autogenerateExoticMaterialsEquipment();

		setAutoGeneration(false);
	}

	private static void autogenerateExoticMaterialsEquipment() {
		if (SettingsHandler.isAutogenExoticMaterial()) {
			final Set<Map.Entry<String, Equipment>> baseEquipSet = new HashSet<Map.Entry<String, Equipment>>(equipmentNameMap.entrySet());
			for (Iterator<Map.Entry<String, Equipment>> i = baseEquipSet.iterator(); i.hasNext(); ) {
				final Map.Entry<String, Equipment> entry = i.next();
				final Equipment eq = entry.getValue();

				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork()
						|| (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon())) {
					continue;
				}

				final EquipmentModifier eqDarkwood = getQualifiedModifierNamed("Darkwood", eq);
				final EquipmentModifier eqAdamantine = getQualifiedModifierNamed("Adamantine", eq);
				final EquipmentModifier eqMithral = getQualifiedModifierNamed("Mithral", eq);

				createItem(eq, eqDarkwood, null, null, null);
				createItem(eq, eqAdamantine, null, null, null);
				createItem(eq, eqMithral, null, null, null);
			}
		}
	}

	private static void autogenerateMagicEquipment() {
		if (SettingsHandler.isAutogenMagic()) {
			for (int iPlus = 1; iPlus <= 5; iPlus++) {
				final String aBonus = Delta.toString(iPlus);

				final Set<Map.Entry<String, Equipment>> baseEquipSet = new HashSet<Map.Entry<String, Equipment>>(equipmentNameMap.entrySet());
				for (Iterator<Map.Entry<String, Equipment>> i = baseEquipSet.iterator(); i.hasNext(); ) {
					final Map.Entry<String, Equipment> entry = i.next();
					Equipment eq = entry.getValue();

					// Only apply to non-magical
					// Armor, Shield and Weapon
					if (eq.isMagic() || eq.isMasterwork()
							|| (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon())) {
						continue;
					}

					// Items must be masterwork before
					// you can assign magic to them
					EquipmentModifier eqMod = getQualifiedModifierNamed("Masterwork", eq);

					if (eqMod == null) {
						Logging
						.debugPrint("Could not generate a Masterwork "
							+ eq.toString()
							+ " as the equipment modifier could not be found.");
						continue;
					}

					// Get list of choices
					final EquipmentChoice equipChoice = eqMod.buildEquipmentChoice(0, eq, false, false, 0);

					// Iterate over list, creating an item for each choice.
					final Iterator<String> equipIter = equipChoice.getChoiceIterator(true);
					for (; equipIter.hasNext();) {
						final String mwChoice = equipIter.next();
						eq = eq.clone();
						eq.addEqModifier(eqMod, true, null, mwChoice, equipChoice);

						if (eq.isWeapon() && eq.isDouble()) {
							eq.addEqModifier(eqMod, false, null, mwChoice, equipChoice);
						}

						eqMod = getQualifiedModifierNamed(aBonus, eq);

						if (eqMod == null) {
							Logging
								.debugPrint("Could not generate a "
									+ aBonus
									+ " "
									+ eq.toString()
									+ " as the equipment modifier could not be found.");
							continue;
						}
						createItem(eq, eqMod, null, null, null);
					}
				}
			}
		}
	}

	private static void autogenerateMasterWorkEquipment() {
		if (SettingsHandler.isAutogenMasterwork()) {
			final Set<Map.Entry<String, Equipment>> baseEquipSet = new HashSet<Map.Entry<String, Equipment>>(equipmentNameMap.entrySet());
			for (Iterator<Map.Entry<String, Equipment>> i = baseEquipSet.iterator(); i.hasNext(); ) {
				final Map.Entry<String, Equipment> entry = i.next();
				final Equipment eq = entry.getValue();

				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork()
						|| (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon())) {
					continue;
				}

				final EquipmentModifier eqMasterwork = getQualifiedModifierNamed("Masterwork", eq);

				// Get list of choices (extract code from EquipmentModifier.getChoice)
				final EquipmentChoice equipChoice = eqMasterwork.buildEquipmentChoice(0, eq, false, false, 0);

				// Iterate over list, creating an item for each choice.
				final Iterator<String> equipIter = equipChoice.getChoiceIterator(true);
				for (; equipIter.hasNext();) {
					final String choice = equipIter.next();
					createItem(eq, eqMasterwork, null, choice, equipChoice);
				}
			}
		}
	}

	private static void autogenerateRacialEquipment() {
		if (SettingsHandler.isAutogenRacial()) {

			//
			// Go through all loaded races and flag whether or not to make equipment
			// sized for them.  Karianna, changed the array length by 1 as Collosal
			// creatures weren't being catered for (and therefore an OutOfBounds exception
			// was being thrown) - Bug 937586
			//
			// TODO - This should not be hardcoded to 10
			final int[] gensizes = new int[10];

			for ( final Race race : Globals.getAllRaces() )
			{
				final int iSize = Globals.sizeInt(race.getSize());
				final int flag = 1;

				gensizes[iSize] |= flag;
			}

			int x = -1;

			final Set<Map.Entry<String, Equipment>> baseEquipSet = new HashSet<Map.Entry<String, Equipment>>(equipmentNameMap.entrySet());
			for (Iterator<Map.Entry<String, Equipment>> i = baseEquipSet.iterator(); i.hasNext(); ) {
				final Map.Entry<String, Equipment> entry = i.next();
				final Equipment eq = entry.getValue();

				//
				// Only apply to Armor, Shield and resizable items
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork()
						|| (!eq.isArmor() && !eq.isShield() && !eq.isType("RESIZABLE"))) {
					continue;
				}

				for (int j = 0; j <= (SettingsHandler.getGame().getSizeAdjustmentListSize() - 1); ++j) {
					if (x == -1) {
						final SizeAdjustment s = SettingsHandler.getGame().getSizeAdjustmentAtIndex(j);

						if (s.isDefaultSize()) {
							x = j;
						}
					}

					if (j == x) // skip over default size
					{
						continue;
					}

					if ((gensizes[j] & 0x01) != 0) {
						createItem(eq, j, null);
					}
				}
			}
		}
	}

	static EquipmentModifier getQualifiedModifierNamed(final String aName, final List<String> aType) {
		for (Iterator<EquipmentModifier> e = getModifierList().iterator(); e.hasNext();) {
			final EquipmentModifier aEqMod = e.next();

			if (aEqMod.getDisplayName().equals(aName)) {
				if (aEqMod.isType("All")) { return aEqMod; }

				for (Iterator<String> e2 = aType.iterator(); e2.hasNext();) {
					final String t = e2.next();

					if (aEqMod.isType(t)) { return aEqMod; }
				}
			}
		}

		return null;
	}

	private static EquipmentModifier getModifierNamed(final String aName) {
		for ( EquipmentModifier eqMod : getModifierList() )
		{
			if (eqMod.getDisplayName().equals(aName)) { return eqMod; }
		}

		return null;
	}

	private static EquipmentModifier getQualifiedModifierNamed(final String aName, final Equipment eq) {
		for ( EquipmentModifier eqMod : getModifierList() )
		{
			if (eqMod.getDisplayName().startsWith(aName)) {
				for (String t : eq.typeList() )
				{
					if (eqMod.isType(t)) {
						// Type matches, passes prereqs?
						if (eqMod.passesPreReqToGain(eq, null)) { return eqMod; }
					}
				}
			}
		}

		return null;
	}

	/**
	 * Appends name parts to the newName.
	 *
	 * @param nameList
	 * @param omitString
	 * @param newName
	 */
	private static void appendNameParts(final List<String> nameList, final String omitString, final StringBuffer newName) {
		for ( String namePart : nameList )
		{
			if ((omitString.length() != 0) && namePart.equals(omitString)) {
				continue;
			}

			if (newName.length() > 2) {
				newName.append('/');
			}

			newName.append(namePart);
		}
	}

	private static void createItem(final Equipment eq, final int iSize, final PlayerCharacter aPC) {
		createItem(eq, null, iSize, aPC, "", null);
	}

	private static void createItem(final Equipment eq, final EquipmentModifier eqMod, final PlayerCharacter aPC, final String choice,
			final EquipmentChoice equipChoice) {
		createItem(eq, eqMod, -1, aPC, choice, equipChoice);
	}

	private static void createItem(Equipment eq, final EquipmentModifier eqMod, final int iSize, final PlayerCharacter aPC,
			final String choice, final EquipmentChoice equipChoice) {
		if (eq == null) { return; }

		try {
			// Armor without an armor bonus is an exception
			//
			if (!eq.getModifiersAllowed()
					|| (eq.isArmor() && (eq.getACMod(aPC).intValue() == 0) && ((eqMod != null) && !eqMod.getDisplayName()
							.equalsIgnoreCase("MASTERWORK")))) { return; }

			eq = eq.clone();

			if (eq == null) {
				Logging.errorPrint("could not clone item");

				return;
			}

			if (eqMod != null) {
				eq.addEqModifier(eqMod, true, aPC, choice, equipChoice);

				if (eq.isWeapon() && eq.isDouble()) {
					eq.addEqModifier(eqMod, false, aPC, choice, equipChoice);
				}
			}

			if ((iSize >= 0) && (iSize <= (SettingsHandler.getGame().getSizeAdjustmentListSize() - 1))) {
				eq.resizeItem(aPC, SettingsHandler.getGame().getSizeAdjustmentAtIndex(iSize).getDisplayName());
			}

			//
			// Change the names, to protect the innocent
			//
			final String sName = eq.nameItemFromModifiers(aPC);
			final Equipment eqExists = getEquipmentKeyed(sName);

			if (eqExists != null) { return; }

			final String newType;

			if (isAutoGeneration()) {
				newType = "AUTO_GEN";
			} else {
				newType = Constants.s_CUSTOM;
			}

			if (!eq.isType(newType)) {
				eq.addMyType(newType);
			}

			//
			// Make sure all the equipment types are present in the sorted list
			//
			Equipment.getEquipmentTypes().addAll(eq.typeList());

			addEquipment(eq);
		} catch (NumberFormatException exception) {
			Logging.errorPrint("createItem: exception: " + eq.getName());
		}
	}

	private static Equipment findEquipment(final String aName, final List<String> preNameList, final List<String> postNameList,
			final List<String> sizList, final String omitString) {
		final StringBuffer newName = new StringBuffer(80);
		newName.append(" (");

		if (preNameList != null) {
			final List<String> nameList = preNameList;
			appendNameParts(nameList, omitString, newName);
		}

		if (sizList != null) {
			// Append 1st size if multiple sizes
			//
			if (sizList.size() > 1) {
				newName.append(sizList.get(0));
			}
		}

		if (postNameList != null) {
			appendNameParts(postNameList, omitString, newName);
		}

		if (newName.length() == 2) {
			newName.setLength(0);
		} else {
			newName.append(')');
		}

		final Equipment eq = getEquipmentKeyed(aName + newName);

		return eq;
	}

	/**
	 * @return size
	 */
	public static int size() {
		return equipmentKeyMap.size();
	}

	/**
	 * @param eq
	 */
	public static void remove(final Equipment eq) {
		if (eq == null)
		{
			return;
		}
		equipmentKeyMap.remove(eq.getKeyName());
		equipmentNameMap.remove(eq.getName());
	}
}
