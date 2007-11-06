/*
 * KitGear.java
 * Copyright 2001 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on September 23, 2002, 8:58 PM
 *
 * $Id$
 */
package pcgen.core.kit;

import pcgen.core.*;
import pcgen.core.character.EquipSet;
import pcgen.util.Logging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <code>KitGear</code>.
 *
 * @author Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
public final class KitGear extends BaseKit implements Serializable, Cloneable
{
	// Only change the UID when the serialized form of the class has also changed
	private static final long serialVersionUID = 1;

	private List<String> eqMods = null;
	private String name = "";
	private int maxCost = 0;
	private String qty = "1";
	private String size = null;
	private String theLocationStr = null;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private transient Equipment theEquipment = null;
	private transient int theQty = 0;
	private transient String theLocation = "";
	private transient Equipment theTarget = null;
	private transient BigDecimal theCost = BigDecimal.ZERO;

	/**
	 * Constructor
	 * @param gearName
	 */
	public KitGear(final String gearName)
	{
		name = gearName;
	}

	/**
	 * Get the equipmentt modifiers for this gear
	 * @return The equipmentt modifiers for this gear
	 */
	public List<String> getEqMods()
	{
		return eqMods;
	}

	/**
	 * Set the max cost
	 * @param argMaxCost
	 */
	public void setMaxCost(final String argMaxCost)
	{
		try
		{
			maxCost = Integer.parseInt(argMaxCost);
		}
		catch (NumberFormatException exc)
		{
			Logging.errorPrint("Invalid max cost \"" + argMaxCost
							   + "\" in KitGear.setMaxCost");
		}
	}

	/**
	 * Get the max cost
	 * @return max cost
	 */
	public int getMaxCost()
	{
		return maxCost;
	}

	/**
	 * Set the quantity
	 * @param argQty
	 */
	public void setQty(final String argQty)
	{
		qty = argQty;
	}

	/**
	 * Get the quantity
	 * @return quantity
	 */
	public String getQty()
	{
		return qty;
	}

	/**
	 * Add a equipment modifier
	 * @param argEqMod
	 */
	public void addEqMod(final String argEqMod)
	{
		if (eqMods == null)
		{
			eqMods = new ArrayList<String>();
		}

		eqMods.add(argEqMod);
	}

	/**
	 * Set the size
	 * @param aSize
	 */
	public void setSize(final String aSize)
	{
		size = aSize;
	}

	/**
	 * Get the size
	 * @return size
	 */
	public String getSize()
	{
		return size;
	}

	/**
	 * Set the location of the gear
	 * @param aLocation
	 */
	public void setLocation(final String aLocation)
	{
		theLocationStr = aLocation;
	}

	/**
	 * Get the location of the gear
	 * @return location of the gear
	 */
	public String getLocation()
	{
		return theLocationStr;
	}

	@Override
	public String toString()
	{
		int maxSize = 0;

		if (eqMods != null)
		{
			maxSize = eqMods.size();
		}

		final StringBuffer info = new StringBuffer(maxSize * 5);

		if (!"1".equals(qty))
		{
			info.append(qty).append('x');
		}

		info.append(name);

		if (maxSize > 0)
		{
			info.append(" (");

			for (int i = 0; i < maxSize; ++i)
			{
				if (i != 0)
				{
					info.append('/');
				}

				info.append( eqMods.get(i) );
			}

			info.append(')');
		}

		return info.toString();
	}

	private void processLookups(Kit aKit, PlayerCharacter aPC)
	{
		for ( String lookup : getLookups() )
		{
			final String colString = aKit.lookup(aPC, lookup);
			processLookup(aKit, aPC, colString);
		}
	}

	private void processLookups(Kit aKit, PlayerCharacter aPC, String a)
	{
		final String colString = aKit.lookup(aPC, a);
		processLookup(aKit, aPC, colString);
	}

	private void processLookup(Kit aKit, PlayerCharacter aPC,
							   final String lookupStr)
	{
		final StringTokenizer tok = new StringTokenizer(lookupStr, "[]");
		while (tok.hasMoreTokens())
		{
			final String colString = tok.nextToken();
			if (colString.startsWith("EQMOD:"))
			{
				addEqMod(colString.substring(6));
			}
			else if (colString.startsWith("QTY:"))
			{
				setQty(colString.substring(4));
			}
			else if (colString.startsWith("MAXCOST:"))
			{
				setMaxCost(colString.substring(8));
			}
			else if (colString.startsWith("SIZE:"))
			{
				setSize(colString.substring(5));
			}
			else if (colString.startsWith("LOCATION:"))
			{
				setLocation(colString.substring(9));
			}
			else if (colString.startsWith("LOOKUP:"))
			{
				processLookups(aKit, aPC, colString.substring(7));
			}
		}
	}

	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		theEquipment = null;
		theQty = 0;
		theLocation = "";
		theTarget = null;
		theCost = BigDecimal.ZERO;

		processLookups(aKit, aPC);

		int aBuyRate = aKit.getBuyRate(aPC);
		final BigDecimal pcGold = aPC.getGold();

		String eqName = name;
		if (name.startsWith("TYPE=") || name.startsWith("TYPE."))
		{
			final List<Equipment> eqList = EquipmentList.getEquipmentOfType(
				eqName.substring(5), "");
			//
			// Remove any that are too expensive
			//
			final int maximumCost = getMaxCost();

			if (maximumCost != 0)
			{
				final BigDecimal bdMaxCost = new BigDecimal(Integer.toString(
					maximumCost));

				for (Iterator<Equipment> i = eqList.iterator(); i.hasNext(); )
				{
					if ( i.next().getCost(aPC).compareTo(bdMaxCost) > 0 )
					{
						i.remove();
					}
				}
			}
			List<Equipment> selected = new ArrayList<Equipment>(1);
			Globals.getChoiceFromList("Choose equipment", eqList, selected, 1);
			if (selected.size() == 1)
			{
				theEquipment = selected.get(0);
			}

			//
			// TODO: Check to see if the user has selected an item that
			// requires modification (MOD:R)
		}
		else
		{
			theEquipment = EquipmentList.getEquipmentKeyed(eqName);
		}

		if (theEquipment == null)
		{
			warnings.add("GEAR: Non-existant gear \"" + eqName + "\"");

			return false;
		}

		theEquipment = theEquipment.clone();

		//
		// Resize item for character--never resize weapons or ammo, unless it's a
		// natural (weapon)
		boolean tryResize = false;

		String sizeToSet = SettingsHandler.getGame().getSizeAdjustmentAtIndex(
			aPC.sizeInt()).getAbbreviation();

		if (getSize() == null)
		{
			if (theEquipment.isType("Natural")
				|| (!theEquipment.isWeapon() && !theEquipment.isAmmunition()))
			{
				tryResize = Globals.canResizeHaveEffect(aPC, theEquipment, null);
			}
		}
		else
		{
			if ("PC".equals(getSize()))
			{
				tryResize = Globals.canResizeHaveEffect(aPC, theEquipment, null);
			}
			else
			{
				sizeToSet = getSize();
				tryResize = true;
			}
		}

		if (tryResize)
		{
			theEquipment.resizeItem(aPC, sizeToSet);
		}
		else
		{
			// We need setBase() called.  The only way to do that is to resize.
			// We will set the size to itself.
			theEquipment.resizeItem(aPC, theEquipment.getSize());
		}

		//
		// Find and add any equipment modifiers
		//
		final List<String> equipmentMods = getEqMods();
		if (equipmentMods != null)
		{
			for (int j = 0; j < equipmentMods.size(); ++j)
			{
				String eqModName = equipmentMods.get(j);
				eqModName = eval(aPC, eqModName);
				theEquipment.addEqModifiers(eqModName, true);
			}
		}

		if (tryResize || (equipmentMods != null))
		{
			theEquipment.nameItemFromModifiers(aPC);
		}

		theQty = aPC.getVariableValue(getQty(), "").intValue();
		int origQty = theQty;
		final BigDecimal eqCost = theEquipment.getCost(aPC);

		if (aBuyRate != 0)
		{
			final BigDecimal bdBuyRate = new BigDecimal(
				Integer.toString(aBuyRate)).multiply(new BigDecimal("0.01"));

			// Check to see if the PC can afford to buy this equipment. If
			// not, then decrement the quantity and try again.
			theCost = eqCost.multiply(new BigDecimal(Integer.toString(theQty)))
				.multiply(bdBuyRate);

			while (theQty > 0)
			{
				if (theCost.compareTo(pcGold) <= 0) // PC has enough?
				{
					break;
				}

				theCost = eqCost.multiply(
					new BigDecimal(Integer.toString(--theQty))).multiply(
						bdBuyRate);
			}

			aPC.setGold(aPC.getGold().subtract(theCost).toString());
		}

		boolean outOfFunds = false;
		if (theQty != origQty)
		{
			outOfFunds = true;
		}

		//
		// Can't buy none
		//
		if (theQty == 0)
		{
			return false;
		}

		if (outOfFunds)
		{
			warnings.add("GEAR: Could not purchase " + (origQty - theQty) + " "
						 + theEquipment.getName() + ". Not enough funds.");
		}

		// Temporarily add the equipment so we can see if we can equip it.
		theEquipment.setQty(new Float(theQty));
		aPC.addEquipment(theEquipment);
		if (getLocation() != null)
		{
			theLocation = getLocation();
			if (! (theLocation.equalsIgnoreCase("DEFAULT")
				   || theLocation.equalsIgnoreCase("Equipped")))
			{
				theTarget = aPC.getEquipmentNamed(theLocation);
			}
			else if (theLocation.equalsIgnoreCase("DEFAULT"))
			{
				theLocation = "";
			}

			EquipSet eqSet = aPC.addEquipToTarget(aPC.getEquipSetByIdPath("0.1"),
												  theTarget, theLocation,
												  theEquipment,
												  new Float( -1.0f));
			if (eqSet == null)
			{
				warnings.add("GEAR: Could not equip " + theEquipment.getName()
							 + " to " + theLocation);
			}
		}
		return true;
	}

	public void apply(PlayerCharacter aPC)
	{
		final Equipment existing = aPC.getEquipmentNamed(theEquipment.getName());

		if (existing == null)
		{
			theEquipment.setQty(new Float(theQty));

			aPC.addEquipment(theEquipment);
			EquipmentList.addEquipment(theEquipment);
		}
		else
		{
			existing.setQty(existing.qty() + theQty);
		}

		// If the target is null, try and grab it incase it is there now
		if (theTarget == null
			&& !(theLocation.equalsIgnoreCase("DEFAULT") || theLocation
				.equalsIgnoreCase("Equipped")))
		{
			theTarget = aPC.getEquipmentNamed(theLocation);
			//TODO (JD 7Nov07) Resized items get missed by the above call as their name has changed 
		}
		EquipSet eSet = null;
		if (theTarget != null)
		{
			eSet = aPC.getEquipSetForItem(aPC.getEquipSetByIdPath("0.1"), theTarget);
		}
		if (eSet == null)
		{
			eSet = aPC.getEquipSetByIdPath("0.1");
		}
	
		//
		// Equip the item to the default EquipSet.
		//
		aPC.addEquipToTarget(eSet, theTarget,
							 theLocation, theEquipment, new Float(theQty));

		aPC.setGold(aPC.getGold().subtract(theCost).toString());
	}

	@Override
	public KitGear clone()
	{
		return (KitGear) super.clone();
	}

	public String getObjectName()
	{
		return "Gear";
	}
}
