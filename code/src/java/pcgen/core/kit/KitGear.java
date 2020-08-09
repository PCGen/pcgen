/*
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
 */
package pcgen.core.kit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import pcgen.base.formula.Formula;
import pcgen.base.util.NamedFormula;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.helper.EqModRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.EquipmentUtilities;
import pcgen.core.Globals;
import pcgen.core.Kit;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;
import pcgen.core.character.EquipSet;
import pcgen.output.channel.ChannelUtilities;

/**
 * {@code KitGear}.
 */
public final class KitGear extends BaseKit
{
	private Formula quantity;
	private Integer maxCost;
	private CDOMReference<Equipment> equip;
	private List<EqModRef> mods;
	private String theLocationStr = null;
	private Boolean sizeToPC;
	private CDOMSingleRef<SizeAdjustment> size;

	// These members store the state of an instance of this class.  They are
	// not cloned.
	private Formula actingQuantity;
	private Integer actingCost;
	private List<EqModRef> actingMods;
	private String actingLocation;
	private SizeAdjustment actingSize;

	private Equipment theEquipment = null;
	private int theQty = 0;
	private String theLocation = "";
	private BigDecimal theCost = BigDecimal.ZERO;

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
		final StringBuilder info = new StringBuilder(100);

		if (quantity != null)
		{
			String qtyStr = String.valueOf(quantity);
			if (!"1".equals(qtyStr))
			{
				info.append(quantity).append('x');
			}
		}

		info.append(equip == null ? "null" : equip.getLSTformat(false));

		if (mods != null)
		{
			info.append(" (");
			boolean needsSlash = false;
			for (EqModRef modRef : mods)
			{
				if (needsSlash)
				{
					info.append('/');
				}
				needsSlash = true;
				info.append(modRef.getRef().getLSTformat(false));
				for (String s : modRef.getChoices())
				{
					info.append(Constants.PIPE).append(s);
				}
			}
			info.append(')');
		}

		return info.toString();
	}

	private void processLookups(Kit aKit, PlayerCharacter aPC)
	{
		Collection<NamedFormula> lookups = getLookups();
		if (lookups == null)
		{
			return;
		}
		for (NamedFormula lookup : lookups)
		{
			KitTable kt = aKit.getTable(lookup.getName());
			KitGear gear = kt.getEntry(aPC, lookup.getFormula().resolve(aPC, "").intValue());
			gear.processLookups(aKit, aPC);
			overlayGear(gear);
		}
	}

	private void overlayGear(KitGear gear)
	{
		if (gear.quantity != null)
		{
			actingQuantity = gear.quantity;
		}
		if (gear.maxCost != null)
		{
			actingCost = gear.maxCost;
		}
		if (gear.mods != null)
		{
			actingMods.addAll(gear.mods);
		}
		if (gear.theLocationStr != null)
		{
			actingLocation = gear.theLocationStr;
		}
		if (gear.size != null)
		{
			actingSize = gear.size.get();
		}
	}

	@Override
	public boolean testApply(Kit aKit, PlayerCharacter aPC, List<String> warnings)
	{
		actingQuantity = quantity;
		actingCost = maxCost;
		actingMods = mods == null ? null : new ArrayList<>(mods);
		actingLocation = theLocationStr;
		if (size != null)
		{
			actingSize = size.get();
		}

		theEquipment = null;
		theQty = 0;
		theLocation = "";
		theCost = BigDecimal.ZERO;

		processLookups(aKit, aPC);

		int aBuyRate = aKit.getBuyRate(aPC);
		BigDecimal pcGold = new BigDecimal(ChannelUtilities
			.readControlledChannel(aPC.getCharID(), CControl.GOLDINPUT).toString());
		final BigDecimal fixedTotalCost = aKit.getTotalCost(aPC);
		if (fixedTotalCost != null)
		{
			// We are going to charge fr the kit once, rather than for every piece of gear
			aBuyRate = 0;
		}

		List<Equipment> eqList = new ArrayList<>(equip.getContainedObjects());
		if (actingCost != null)
		{
			final BigDecimal bdMaxCost = new BigDecimal(Integer.toString(actingCost));
			for (Iterator<Equipment> i = eqList.iterator(); i.hasNext();)
			{
				if (i.next().getCost(aPC).compareTo(bdMaxCost) > 0)
				{
					i.remove();
				}
			}
		}
		if (eqList.size() == 1)
		{
			theEquipment = eqList.get(0);
		}
		else
		{
			List<Equipment> selected = new ArrayList<>(1);
			selected = Globals.getChoiceFromList("Choose equipment", eqList, selected, 1, aPC);
			if (selected.size() == 1)
			{
				theEquipment = selected.get(0);
			}
		}

		//
		// TODO: Check to see if the user has selected an item that
		// requires modification (MOD:R)

		theEquipment = theEquipment.clone();

		//
		// Resize item for character--never resize weapons or ammo, unless it's a
		// natural (weapon)
		boolean tryResize = false;

		SizeAdjustment sizeToSet = aPC.getSizeAdjustment();

		if (actingSize == null)
		{
			if (theEquipment.isType("Natural") || (sizeToPC != null && sizeToPC)
				|| (!theEquipment.isWeapon() && !theEquipment.isAmmunition()))
			{
				tryResize = Globals.canResizeHaveEffect(theEquipment.typeList());
			}
		}
		else
		{
			if (sizeToPC != null && sizeToPC)
			{
				tryResize = Globals.canResizeHaveEffect(theEquipment.typeList());
			}
			else
			{
				sizeToSet = actingSize;
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
			theEquipment.resizeItem(aPC, theEquipment.getSizeAdjustment());
		}

		//
		// Find and add any equipment modifiers
		//
		if (actingMods != null)
		{
			for (EqModRef modref : actingMods)
			{
				/*
				 * Going to do this the long way for now to avoid ugly entanglements
				 */
				StringBuilder sb = new StringBuilder(50);
				EquipmentModifier eqMod = modref.getRef().get();
				sb.append(eqMod.getKeyName());
				for (String assoc : modref.getChoices())
				{
					sb.append(Constants.PIPE).append(eval(aPC, assoc));
				}
				theEquipment.addEqModifiers(sb.toString(), true);
			}
		}

		if (tryResize || (actingMods != null))
		{
			theEquipment.nameItemFromModifiers(aPC);
		}

		if (actingQuantity == null)
		{
			theQty = 1;
		}
		else
		{
			theQty = actingQuantity.resolve(aPC, "").intValue();
		}
		int origQty = theQty;
		final BigDecimal eqCost = theEquipment.getCost(aPC);
		if (aBuyRate != 0)
		{
            final BigDecimal bdBuyRate =
                    new BigDecimal(Integer.toString(aBuyRate)).multiply(new BigDecimal("0.01"));

            // Check to see if the PC can afford to buy this equipment. If
            // not, then decrement the quantity and try again.
            theCost = eqCost.multiply(new BigDecimal(Integer.toString(theQty))).multiply(bdBuyRate);

            while (theQty > 0)
            {
                if (theCost.compareTo(pcGold) <= 0) // PC has enough?
                {
                    break;
                }

                theCost = eqCost.multiply(new BigDecimal(Integer.toString(--theQty))).multiply(bdBuyRate);
            }

    		BigDecimal currentGold = new BigDecimal(ChannelUtilities
    			.readControlledChannel(aPC.getCharID(), CControl.GOLDINPUT).toString());
			ChannelUtilities.setControlledChannel(aPC.getCharID(),
				CControl.GOLDINPUT, currentGold.subtract(theCost));
		}

		boolean outOfFunds = false;
		if (theQty != origQty)
		{
			outOfFunds = true;
		}

		if (outOfFunds)
		{
			warnings.add("GEAR: Could not purchase " + (origQty - theQty) + " " + theEquipment.getName()
				+ ". Not enough funds.");
		}

		//
		// Can't buy none
		//
		if (theQty == 0)
		{
			return false;
		}

		Equipment testApplyEquipment = theEquipment.clone();
		// Temporarily add the equipment so we can see if we can equip it.
		testApplyEquipment.setQty(Float.valueOf(theQty));
		aPC.addEquipment(testApplyEquipment);
		Equipment theTarget = null;
		if (actingLocation != null)
		{
			theLocation = actingLocation;
			if (!theLocation.equalsIgnoreCase("DEFAULT")
				&& !theLocation.equalsIgnoreCase(Constants.EQUIP_LOCATION_CARRIED)
				&& !theLocation.equalsIgnoreCase(Constants.EQUIP_LOCATION_NOTCARRIED)
				&& !theLocation.equalsIgnoreCase(Constants.EQUIP_LOCATION_EQUIPPED))
			{
				theTarget = EquipmentUtilities.findEquipmentByBaseKey(aPC.getEquipmentMasterList(), theLocation);
			}
			else if (theLocation.equalsIgnoreCase("DEFAULT"))
			{
				theLocation = "";
			}

			EquipSet eSet = null;
			if (theTarget != null)
			{
				eSet = aPC.getEquipSetForItem(aPC.getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH), theTarget);
			}
			if (eSet == null)
			{
				eSet = aPC.getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH);
			}
			if (eSet == null)
			{
				warnings.add(
					"GEAR: Could not find location " + theLocation + " for gear " + testApplyEquipment.getName() + ".");
				return false;
			}
			else
			{
				EquipSet eqSet =
						aPC.addEquipToTarget(eSet, theTarget, theLocation, testApplyEquipment, -1.0f);
				if (eqSet == null)
				{
					warnings.add("GEAR: Could not equip " + testApplyEquipment.getName() + " to " + theLocation);
				}
			}
		}
		return true;
	}

	@Override
	public void apply(PlayerCharacter aPC)
	{
		final Equipment existing = aPC.getEquipmentNamed(theEquipment.getName());

		if (existing == null)
		{
			theEquipment.setQty(Float.valueOf(theQty));

			aPC.addEquipment(theEquipment);
			Globals.getContext().getReferenceContext().importObject(theEquipment);
		}
		else
		{
			existing.setQty(existing.qty() + theQty);
		}

		// If the target is null, try and grab it incase it is there now
		Equipment theTarget = null;
		EquipSet eSet;
		if (!theLocation.equalsIgnoreCase(Constants.EQUIP_LOCATION_CARRIED)
			&& !theLocation.equalsIgnoreCase(Constants.EQUIP_LOCATION_NOTCARRIED)
			&& !theLocation.equalsIgnoreCase(Constants.EQUIP_LOCATION_EQUIPPED))
		{
			theTarget = EquipmentUtilities.findEquipmentByBaseKey(aPC.getEquipmentMasterList(), theLocation);
			if (theTarget == null)
			{
				theLocation = Constants.EQUIP_LOCATION_CARRIED;
			}
		}
		if (theTarget == null)
		{
			eSet = aPC.getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH);
		}
		else
		{
			eSet = aPC.getEquipSetForItem(aPC.getEquipSetByIdPath(EquipSet.DEFAULT_SET_PATH), theTarget);
		}

		//
		// Equip the item to the default EquipSet.
		//
		aPC.addEquipToTarget(eSet, theTarget, theLocation, theEquipment, (float) theQty);

		BigDecimal currentGold = new BigDecimal(ChannelUtilities
			.readControlledChannel(aPC.getCharID(), CControl.GOLDINPUT).toString());
		ChannelUtilities.setControlledChannel(aPC.getCharID(),
			CControl.GOLDINPUT, currentGold.subtract(theCost));
	}

	@Override
	public String getObjectName()
	{
		return "Gear";
	}

	public void setQuantity(Formula formula)
	{
		quantity = formula;
	}

	public Formula getQuantity()
	{
		return quantity;
	}

	public void setMaxCost(Integer quan)
	{
		maxCost = quan;
	}

	public Integer getMaxCost()
	{
		return maxCost;
	}

	public void setEquipment(CDOMReference<Equipment> reference)
	{
		equip = reference;
	}

	public CDOMReference<Equipment> getEquipment()
	{
		return equip;
	}

	public void setSizeToPC(Boolean b)
	{
		sizeToPC = b;
	}

	public Boolean getSizeToPC()
	{
		return sizeToPC;
	}

	public void setSize(CDOMSingleRef<SizeAdjustment> sa)
	{
		size = sa;
	}

	public CDOMSingleRef<SizeAdjustment> getSize()
	{
		return size;
	}

	private List<NamedFormula> lookupList;

	public void loadLookup(String tableEntry, Formula f)
	{
		if (lookupList == null)
		{
			lookupList = new LinkedList<>();
		}
		lookupList.add(new NamedFormula(tableEntry, f));
	}

	public Collection<NamedFormula> getLookups()
	{
		return lookupList;
	}

	public void addModRef(EqModRef modRef)
	{
		if (mods == null)
		{
			mods = new LinkedList<>();
		}
		mods.add(modRef);
	}

	public boolean hasEqMods()
	{
		return mods != null && !mods.isEmpty();
	}

	public List<EqModRef> getEqMods()
	{
		return Collections.unmodifiableList(mods);
	}
}
