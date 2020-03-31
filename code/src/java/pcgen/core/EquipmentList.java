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
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.prereq.PrereqHandler;
import pcgen.core.utils.CoreUtility;
import pcgen.util.Delta;
import pcgen.util.Logging;

/**
 * Equipment-related lists and methods extracted from Globals.java. Will
 * probably try to disentangle modifierlist into it's own class later.
 */
public final class EquipmentList
{

	/**
	 * Private to ensure utility object can't be instantiated.
	 */
	private EquipmentList()
	{
		// Empty Constructor
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
				bonuses[idx++] = Delta.decode(cString);
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

	private static EquipmentModifier getModifierNamed(final String aName)
	{
		return Globals.getContext()
		              .getReferenceContext()
		              .getConstructedCDOMObjects(EquipmentModifier.class)
		              .stream()
		              .filter(eqMod -> eqMod.getDisplayName().equals(aName))
		              .findFirst()
		              .orElse(null);

	}

	private static EquipmentModifier getQualifiedModifierNamed(final String aName, final Equipment eq)
	{
		// Type matches, passes prereqs?
		return Globals.getContext()
		              .getReferenceContext()
		              .getConstructedCDOMObjects(EquipmentModifier.class)
		              .stream()
		              .filter(eqMod -> eqMod.getDisplayName().startsWith(aName))
		              .filter(eqMod -> eq.typeList()
		                                 .stream()
		                                 .filter(eqMod::isType)
		                                 .anyMatch(t -> PrereqHandler.passesAll(eqMod, eq, null)))
		              .findFirst()
		              .orElse(null);

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
				|| (eq.isArmor() && (eq.getACMod(aPC) == 0)
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

			final Type newType = Type.CUSTOM;

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
            appendNameParts(preNameList, omitString, newName);
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

        return Globals.getContext().getReferenceContext()
            .silentlyGetConstructedCDOMObject(Equipment.class, aName + newName);
	}

}
