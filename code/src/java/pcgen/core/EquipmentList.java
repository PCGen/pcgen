/*
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
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.analysis.EquipmentChoiceDriver;
import pcgen.core.analysis.SizeUtilities;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Equipment-related lists and methods extracted from Globals.java. Will
 * probably try to disentangle modifierlist into it's own class later.
 */
public final class EquipmentList
{

	/** this is determined by preferences */
	private static boolean autoGeneration = false;

	/**
	 * Private to ensure utility object can't be instantiated.
	 */
	private EquipmentList()
	{
		// Empty Constructor
	}

	private static boolean isAutoGeneration()
	{
		return autoGeneration;
	}

	/**
	 * Set whether magic equipment auto generation should be on.
	 *
	 * @param auto
	 *          true if it should be on
	 */
	public static void setAutoGeneration(final boolean auto)
	{
		autoGeneration = auto;
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
	public static Equipment getEquipmentFromName(final String baseName, final PlayerCharacter aPC)
	{
		final List<String> modList = new ArrayList<>();
		final List<String> namList = new ArrayList<>();
		final List<String> sizList = new ArrayList<>();
		Equipment eq;
		String aName = baseName;
		int i = aName.indexOf('(');

		// Remove all modifiers from item name and
		// split into "size" and "non-size" lists
		if (i >= 0)
		{
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i + 1), "/)", false);

			while (aTok.hasMoreTokens())
			{
				final String cString = aTok.nextToken();

				SizeAdjustment sa = Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(SizeAdjustment.class, cString);

				if (sa != null)
				{
					sizList.add(cString);
				}
				else
				{
					if ("Mighty Composite".equalsIgnoreCase(cString))
					{
						modList.add("Mighty");
						modList.add("Composite");
					}
					else
					{
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
		if (i >= 0)
		{
			for (i = modList.size() - 1; i >= 0; --i)
			{
				final String namePart = modList.get(i);

				if (getModifierNamed(namePart) == null)
				{
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

		if (i >= 0)
		{
			final StringTokenizer aTok = new StringTokenizer(aName.substring(i), "/", false);
			bonusCount = aTok.countTokens();
			bonuses = new int[bonusCount];

			int idx = 0;

			while (aTok.hasMoreTokens())
			{
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
			if (bonusCount > 0)
			{
				for (int idx1 = 0; idx1 < namList.size(); ++idx1)
				{
					String aString = namList.get(idx1);

					if ("Mighty".equalsIgnoreCase(aString))
					{
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

		while (true)
		{
			final String eqName = aName + bonusString;
			eq = findEquipment(eqName, null, namList, sizList, omitString);

			if (eq != null)
			{
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, sizList, omitString);

			if (eq != null)
			{
				if (sizList.size() > 1) // was used in name, ignore as modifier
				{
					sizList.remove(0);
				}

				break;
			}

			eq = findEquipment(eqName, namList, null, null, omitString);

			if (eq != null)
			{
				break;
			}

			// If only 1 size then include it in name
			if (sizList.size() == 1)
			{
				eq = findEquipment(eqName, sizList, namList, null, omitString);

				if (eq == null)
				{
					eq = findEquipment(eqName, namList, sizList, null, omitString);
				}

				if (eq != null)
				{
					sizList.clear();

					break;
				}
			}

			// If we haven't found it yet,
			// try stripping Thrown from name
			if (baseName.contains("Thrown"))
			{
				if (omitString.isEmpty())
				{
					omitString = "Thrown";

					continue;
				}
			}

			// Still haven't found it?
			// Try adding bonus to end of name
			if ((bonusCount > 0) && (bonuses != null))
			{
				if (bonusString.isEmpty())
				{
					omitString = "";
					bonusString = " " + Delta.toString(bonuses[0]);

					continue;
				}
			}

			break;
		}

		if (eq != null)
		{
			boolean bModified = false;
			boolean bError = false;
			eq = eq.clone();

			//
			// Now attempt to add all the modifiers.
			//
			for (final String namePart : modList)
			{
				final EquipmentModifier eqMod = getQualifiedModifierNamed(namePart, eq);

				if (eqMod != null)
				{
					eq.addEqModifier(eqMod, true, aPC);

					if (eqMod.getSafe(ObjectKey.ASSIGN_TO_ALL) && eq.isDouble())
					{
						eq.addEqModifier(eqMod, false, aPC);
						bModified = true;
					}
				}
				else
				{
					Logging.errorPrint("Could not find a qualified modifier named: " + namePart + " for " + eq.getName()
						+ ":" + eq.typeList());
					bError = true;
				}
			}

			// Found what appeared to be the base item,
			// but one of the modifiers is not qualified
			// to be attached to the item
			//
			if (bError)
			{
				return null;
			}

			if (!sizList.isEmpty())
			{
				/*
				 * CONSIDER This size can be further optimized by changing sizList
				 */
				eq.resizeItem(aPC, Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(SizeAdjustment.class, sizList.get(0)));
				bModified = true;

				if (sizList.size() > 1)
				{
					Logging.errorPrint("Too many sizes in item name, used only 1st of: " + sizList);
				}
			}

			if (bModified)
			{
				eq.nameItemFromModifiers(aPC);
				Equipment equip = Globals.getContext().getReferenceContext()
					.silentlyGetConstructedCDOMObject(Equipment.class, eq.getKeyName());
				if (equip == null)
				{
					Globals.getContext().getReferenceContext().importObject(eq);
				}
				else
				{
					eq = equip;
				}
			}
		}

		return eq;
	}

	/**
	 * Get an Equipment object from the list matching the passed-in type(s).
	 *
	 * @param desiredTypes
	 *          a '.' separated list of types to match
	 * @param excludedTypes
	 *          a '.' separated list of types to NOT match
	 * @return the matching Equipment
	 */
	public static List<Equipment> getEquipmentOfType(final String desiredTypes, final String excludedTypes)
	{
		final List<String> desiredTypeList = CoreUtility.split(desiredTypes, '.');
		final List<String> excludedTypeList = CoreUtility.split(excludedTypes, '.');
		final List<Equipment> typeList = new ArrayList<>(100);

		if (!desiredTypeList.isEmpty())
		{
			for (Equipment eq : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class))
			{
				boolean addIt = true;

				//
				// Must have all of the types in the desired list
				//
				for (String type : desiredTypeList)
				{
					if (!eq.isType(type))
					{
						addIt = false;

						break;
					}
				}

				if (addIt && (!excludedTypeList.isEmpty()))
				{
					//
					// Can't have any of the types on the excluded list
					//
					for (String type : excludedTypeList)
					{
						if (eq.isType(type))
						{
							addIt = false;

							break;
						}
					}
				}

				if (addIt)
				{
					typeList.add(eq);
				}
			}
		}

		return typeList;
	}

	/**
	 * Automatically add equipment types as requested by user.
	 *          TODO
	 */
	public static void autoGenerateEquipment()
	{
		setAutoGeneration(true);

		autogenerateRacialEquipment();

		autogenerateMasterWorkEquipment();

		autogenerateMagicEquipment();

		autogenerateExoticMaterialsEquipment();

		setAutoGeneration(false);
	}

	private static void autogenerateExoticMaterialsEquipment()
	{
		if (SettingsHandler.isAutogenExoticMaterial())
		{
			for (Equipment eq : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class))
			{
				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork()
					|| (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
				{
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

	private static void autogenerateMagicEquipment()
	{
		if (SettingsHandler.isAutogenMagic())
		{
			for (int iPlus = 1; iPlus <= 5; iPlus++)
			{
				final String aBonus = Delta.toString(iPlus);

				for (Equipment eq : Globals.getContext().getReferenceContext()
					.getConstructedCDOMObjects(Equipment.class))
				{
					// Only apply to non-magical
					// Armor, Shield and Weapon
					if (eq.isMagic() || eq.isMasterwork()
						|| (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
					{
						continue;
					}

					// Items must be masterwork before
					// you can assign magic to them
					EquipmentModifier eqMod = getQualifiedModifierNamed("Masterwork", eq);

					if (eqMod == null)
					{
						Logging.debugPrint(
							"Could not generate a Masterwork " + eq + " as the equipment modifier could not be found.");
						continue;
					}

					// Get list of choices
					final EquipmentChoice equipChoice =
							EquipmentChoiceDriver.buildEquipmentChoice(0, eq, eqMod, false, false, 0, null);

					// Iterate over list, creating an item for each choice.
					final Iterator<Object> equipIter = equipChoice.getChoiceIterator(true);
					for (; equipIter.hasNext();)
					{
						final String mwChoice = String.valueOf(equipIter.next());
						eq = eq.clone();
						eq.addEqModifier(eqMod, true, null, mwChoice, equipChoice);

						if (eq.isWeapon() && eq.isDouble())
						{
							eq.addEqModifier(eqMod, false, null, mwChoice, equipChoice);
						}

						eqMod = getQualifiedModifierNamed(aBonus, eq);

						if (eqMod == null)
						{
							Logging.debugPrint("Could not generate a " + aBonus + " " + eq
								+ " as the equipment modifier could not be found.");
							continue;
						}
						createItem(eq, eqMod, null, null, null);
					}
				}
			}
		}
	}

	private static void autogenerateMasterWorkEquipment()
	{
		if (SettingsHandler.isAutogenMasterwork())
		{
			for (Equipment eq : Globals.getContext().getReferenceContext().getConstructedCDOMObjects(Equipment.class))
			{
				//
				// Only apply to non-magical Armor, Shield and Weapon
				//
				if (eq.isMagic() || eq.isUnarmed() || eq.isMasterwork()
					|| (!eq.isAmmunition() && !eq.isArmor() && !eq.isShield() && !eq.isWeapon()))
				{
					continue;
				}

				final EquipmentModifier eqMasterwork = getQualifiedModifierNamed("Masterwork", eq);
				if (eqMasterwork == null)
				{
					continue;
				}

				// Get list of choices (extract code from EquipmentModifier.getChoice)
				final EquipmentChoice equipChoice =
						EquipmentChoiceDriver.buildEquipmentChoice(0, eq, eqMasterwork, false, false, 0, null);

				// Iterate over list, creating an item for each choice.
				final Iterator<Object> equipIter = equipChoice.getChoiceIterator(true);
				for (; equipIter.hasNext();)
				{
					final String choice = String.valueOf(equipIter.next());
					createItem(eq, eqMasterwork, null, choice, equipChoice);
				}
			}
		}
	}

	private static void autogenerateRacialEquipment()
	{
		if (SettingsHandler.isAutogenRacial())
		{

			Set<Integer> gensizesid = new HashSet<>();
			//
			// Go through all loaded races and flag whether or not to make equipment
			// sized for them.  Karianna, changed the array length by 1 as Collosal
			// creatures weren't being catered for (and therefore an OutOfBounds exception
			// was being thrown) - Bug 937586
			//
			AbstractReferenceContext ref = Globals.getContext().getReferenceContext();
			for (final Race race : ref.getConstructedCDOMObjects(Race.class))
			{
				/*
				 * SIZE: in Race LST files enforces that the formula is fixed,
				 * so no isStatic() check needed here
				 */
				final int iSize = race.getSafe(FormulaKey.SIZE).resolveStatic().intValue();
				gensizesid.add(iSize);
			}

			SizeAdjustment defaultSize = SizeUtilities.getDefaultSizeAdjustment();
			Set<SizeAdjustment> gensizes = new HashSet<>();
			for (Integer i : gensizesid)
			{
				gensizes.add(ref.getSortedList(SizeAdjustment.class, IntegerKey.SIZEORDER).get(i));
			}
			// skip over default size
			gensizes.remove(defaultSize);

			PlayerCharacter dummyPc = new PlayerCharacter();
			for (Equipment eq : ref.getConstructedCDOMObjects(Equipment.class))
			{
				//
				// Only apply to Armor, Shield and resizable items
				//
				if (!Globals.canResizeHaveEffect(eq, null))
				{
					continue;
				}

				for (SizeAdjustment sa : gensizes)
				{
					createItem(eq, sa, dummyPc);
				}
			}
		}
	}

	private static EquipmentModifier getModifierNamed(final String aName)
	{
		for (EquipmentModifier eqMod : Globals.getContext().getReferenceContext()
			.getConstructedCDOMObjects(EquipmentModifier.class))
		{
			if (eqMod.getDisplayName().equals(aName))
			{
				return eqMod;
			}
		}

		return null;
	}

	private static EquipmentModifier getQualifiedModifierNamed(final String aName, final Equipment eq)
	{
		for (EquipmentModifier eqMod : Globals.getContext().getReferenceContext()
			.getConstructedCDOMObjects(EquipmentModifier.class))
		{
			if (eqMod.getDisplayName().startsWith(aName))
			{
				for (String t : eq.typeList())
				{
					if (eqMod.isType(t))
					{
						// Type matches, passes prereqs?
						if (PrereqHandler.passesAll(eqMod, eq, null))
						{
							return eqMod;
						}
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
	private static void appendNameParts(final List<String> nameList, final String omitString,
		final StringBuilder newName)
	{
		for (String namePart : nameList)
		{
			if ((!omitString.isEmpty()) && namePart.equals(omitString))
			{
				continue;
			}

			if (newName.length() > 2)
			{
				newName.append('/');
			}

			newName.append(namePart);
		}
	}

	private static void createItem(final Equipment eq, final SizeAdjustment sa, final PlayerCharacter aPC)
	{
		createItem(eq, null, sa, aPC, "", null);
	}

	private static void createItem(final Equipment eq, final EquipmentModifier eqMod, final PlayerCharacter aPC,
		final String choice, final EquipmentChoice equipChoice)
	{
		createItem(eq, eqMod, null, aPC, choice, equipChoice);
	}

	private static void createItem(Equipment eq, final EquipmentModifier eqMod, final SizeAdjustment sa,
		final PlayerCharacter aPC, final String choice, final EquipmentChoice equipChoice)
	{
		if (eq == null)
		{
			return;
		}

		try
		{
			// Armor without an armor bonus is an exception
			//
			if (!eq.getSafe(ObjectKey.MOD_CONTROL).getModifiersAllowed()
				|| (eq.isArmor() && (eq.getACMod(aPC).intValue() == 0)
					&& ((eqMod != null) && !eqMod.getDisplayName().equalsIgnoreCase("MASTERWORK"))))
			{
				return;
			}

			eq = eq.clone();

			if (eq == null)
			{
				Logging.errorPrint("could not clone item");

				return;
			}

			if (eqMod != null)
			{
				eq.addEqModifier(eqMod, true, aPC, choice, equipChoice);

				if (eq.isWeapon() && eq.isDouble())
				{
					eq.addEqModifier(eqMod, false, aPC, choice, equipChoice);
				}
			}

			if (sa != null)
			{
				eq.resizeItem(aPC, sa);
			}

			//
			// Change the names, to protect the innocent
			//
			final String sKeyName = eq.nameItemFromModifiers(aPC);
			final Equipment eqExists = Globals.getContext().getReferenceContext()
				.silentlyGetConstructedCDOMObject(Equipment.class, sKeyName);

			if (eqExists != null)
			{
				return;
			}

			final Type newType;

			if (isAutoGeneration())
			{
				newType = Type.AUTO_GEN;
			}
			else
			{
				newType = Type.CUSTOM;
			}

			if (!eq.isType(newType.toString()))
			{
				eq.addType(newType);
			}

			Globals.getContext().getReferenceContext().importObject(eq);
		}
		catch (NumberFormatException exception)
		{
			Logging.errorPrint("createItem: exception: " + eq.getName());
		}
	}

	private static Equipment findEquipment(final String aName, final List<String> preNameList,
		final List<String> postNameList, final List<String> sizList, final String omitString)
	{
		final StringBuilder newName = new StringBuilder(80);
		newName.append(" (");

		if (preNameList != null)
		{
			final List<String> nameList = preNameList;
			appendNameParts(nameList, omitString, newName);
		}

		if (sizList != null)
		{
			// Append 1st size if multiple sizes
			//
			if (sizList.size() > 1)
			{
				newName.append(sizList.get(0));
			}
		}

		if (postNameList != null)
		{
			appendNameParts(postNameList, omitString, newName);
		}

		if (newName.length() == 2)
		{
			newName.setLength(0);
		}
		else
		{
			newName.append(')');
		}

		final Equipment eq = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(Equipment.class, aName + newName);

		return eq;
	}

}
